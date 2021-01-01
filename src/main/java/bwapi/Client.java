/*
MIT License

Copyright (c) 2018 Hannes Bredberg
Modified work Copyright (c) 2018 Jasper

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package bwapi;

import bwapi.ClientData.Command;
import bwapi.ClientData.GameData;
import bwapi.ClientData.Shape;

class Client {
    private static final int SUPPORTED_BWAPI_VERSION = 10003;

    private ClientData clientData;
    private ClientData.GameData gameData;
    private BWClient bwClient;
    private boolean connected = false;
    private WrappedBuffer mapShm = null;
    private WrappedBuffer gameTableShm = null;
    private final ClientConnection clientConnector;

    Client(BWClient bwClient) {
        this.bwClient = bwClient;
        boolean windowsOs = System.getProperty("os.name").toLowerCase().contains("win");
        clientConnector = windowsOs ? new ClientConnectionW32() : new ClientConnectionPosix();
    }

    /**
     * For test purposes only
     */
    Client(final WrappedBuffer buffer) {
        clientData = new ClientData();
        clientData.setBuffer(buffer);
        clientConnector = null;
    }

    ClientData liveClientData() {
        return clientData;
    }

    WrappedBuffer mapFile() {
        return mapFileHandle;
    }

    boolean isConnected() {
        return connected;
    }

    void reconnect() {
        while (!connect()) {
            sleep(1000);
        }
    }

    private void disconnect() {
        if (bwClient.getConfiguration().getDebugConnection()) {
            System.err.print("Disconnect called by: ");
            System.err.println(Thread.currentThread().getStackTrace()[2]);
        }
        if (!connected) {
            return;
        }
        clientConnector.disconnect();
        mapShm = null;
        gameTableShm = null;
        clientData = null;
        connected = false;
    }

    boolean connect() {
        if (connected) {
            System.err.println("Already connected");
            return true;
        }

        int serverProcID = -1;
        int gameTableIndex = -1;

        // Expose the BWAPI list of games from shared memory via a ByteBuffer
        try {
            gameTableShm = clientConnector.getGameTable();
        }
        catch (Exception e) {
            System.err.println("Game table mapping not found.");
            return false;
        }

        GameTable gameTable;
        try {
            gameTable = new GameTable(this.gameTableShm);
        } catch (Exception e) {
            System.err.println("Unable to map Game table.");
            if (bwClient.getConfiguration().getDebugConnection()) {
                e.printStackTrace();
            }
            return false;
        }

        int oldest = Integer.MAX_VALUE;
        for (int i = 0; i < GameTable.MAX_GAME_INSTANCES; i++) {
            GameInstance gameInstance = gameTable.gameInstances[i];
            System.out.println(i + " | " + gameInstance.serverProcessID + " | " + (gameInstance.isConnected ? 1 : 0) + " | " + gameInstance.lastKeepAliveTime);
            if (gameInstance.serverProcessID != 0 && !gameInstance.isConnected) {
                if (gameTableIndex == -1 || gameInstance.lastKeepAliveTime < oldest) {
                    oldest = gameInstance.lastKeepAliveTime;
                    gameTableIndex = i;
                }
            }
        }

        if (gameTableIndex != -1) {
            serverProcID = gameTable.gameInstances[gameTableIndex].serverProcessID;
        }

        if (serverProcID == -1) {
            System.err.println("No server proc ID");
            return false;
        }

        try {
            mapShm = clientConnector.getSharedMemory(serverProcID);
        }
        catch (Exception e) {
            System.err.println("Unable to open shared memory mapping: " + e.getMessage());
            if (bwClient.getConfiguration().getDebugConnection()) {
                e.printStackTrace();
            }
            this.gameTableShm = null;
            return false;
        }
        try {
            clientData = new ClientData(mapShm);
            gameData = clientData.new GameData(0);
        } catch (Exception e) {
            System.err.println("Unable to map game data.");
            if (bwClient.getConfiguration().getDebugConnection()) {
                e.printStackTrace();
            }
            return false;
        }


        try {
            clientConnector.connectSharedLock(serverProcID);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            if (bwClient.getConfiguration().getDebugConnection()) {
                e.printStackTrace();
            }
            this.gameTableShm = null;
            return false;
        }
        System.out.println("Connected");

        if (SUPPORTED_BWAPI_VERSION != clientData.gameData().getClient_version()) {
            System.err.println("Error: Client and Server are not compatible!");
            System.err.println("Client version: " + SUPPORTED_BWAPI_VERSION);
            System.err.println("Server version: " + clientData.gameData().getClient_version());
            disconnect();
            sleep(2000);
            return false;
        }

        try {
            clientConnector.waitForServerReady();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            if (bwClient.getConfiguration().getDebugConnection()) {
                e.printStackTrace();
            }
            disconnect();
            return false;
        }

        System.out.println("Connection successful");
        connected = true;
        return true;
    }

    void sendFrameReceiveFrame() {
        final PerformanceMetrics metrics = bwClient.getPerformanceMetrics();

        // Tell BWAPI that we are done with the current frame
        metrics.getFrameDurationReceiveToSend().stopTiming();
        if (bwClient.doTime()) {
            metrics.getCommunicationSendToReceive().startTiming();
            metrics.getCommunicationSendToSent().startTiming();
        }
        try {
            clientConnector.waitForServerData();
        }
        catch (Exception e) {
            System.err.println("failed, disconnecting");
            if (bwClient.getConfiguration().getDebugConnection()) {
                e.printStackTrace();
            }
            disconnect();
            return;
        }
        metrics.getCommunicationSendToSent().stopTiming();
        metrics.getFrameDurationReceiveToSent().stopTiming();
        if (bwClient.doTime()) {
            final int eventCount = clientData.gameData().getEventCount();
            metrics.getNumberOfEvents().record(eventCount);
            metrics.getNumberOfEventsTimesDurationReceiveToSent().record(eventCount * metrics.getFrameDurationReceiveToSent().getRunningTotal().getLast());
        }

        // Listen for BWAPI to indicate that a new frame is ready
        if (bwClient.doTime()) {
            metrics.getCommunicationListenToReceive().startTiming();
        }
        try {
            clientConnector.submitClientData();
        } catch (Exception e) {
            System.err.println("failed, disconnecting");
            if (debugConnection) {
                e.printStackTrace();
            }
            disconnect();
        }
        }

        metrics.getCommunicationListenToReceive().stopTiming();
        metrics.getCommunicationSendToReceive().stopTiming();

        if (bwClient.doTime()) {
            metrics.getFrameDurationReceiveToSend().startTiming();
            metrics.getFrameDurationReceiveToSent().startTiming();
        }
        metrics.getFrameDurationReceiveToReceive().stopTiming();
        if (bwClient.doTime()) {
            metrics.getFrameDurationReceiveToReceive().startTiming();
        }
    }

    private void sleep(final int millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception ignored) {
            // Not relevant
        }
    }
}
