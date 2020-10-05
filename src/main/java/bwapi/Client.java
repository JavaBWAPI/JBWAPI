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

import bwapi.ClientData.GameData;
import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.win32.W32APIOptions;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

class Client {
    interface MappingKernel extends Kernel32 {
        MappingKernel INSTANCE = Native.load(MappingKernel.class, W32APIOptions.DEFAULT_OPTIONS);

        HANDLE OpenFileMapping(int desiredAccess, boolean inherit, String name);
    }

    private static final int READ_WRITE = 0x1 | 0x2 | 0x4;
    private static final int SUPPORTED_BWAPI_VERSION = 10003;

    private ClientData clientData;
    private BWClient bwClient;
    private boolean connected = false;
    private RandomAccessFile pipeObjectHandle = null;
    private ByteBuffer gameTableFileHandle = null;
    private ByteBuffer mapFileHandle = null;

    Client(BWClient bwClient) {
        this.bwClient = bwClient;
    }

    /**
     * For test purposes only
     */
    Client(ByteBuffer buffer) {
        clientData = new ClientData();
        clientData.setBuffer(buffer);
    }

    ClientData liveClientData() {
        return clientData;
    }

    ByteBuffer mapFile() {
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

        if (pipeObjectHandle != null ) {
            try {
                pipeObjectHandle.close();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            pipeObjectHandle = null;
        }

        gameTableFileHandle = null;
        mapFileHandle = null;
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
            gameTableFileHandle = Kernel32.INSTANCE.MapViewOfFile(
                    MappingKernel.INSTANCE.OpenFileMapping(READ_WRITE, false, "Local\\bwapi_shared_memory_game_list"), READ_WRITE, 0, 0, GameTable.SIZE)
                    .getByteBuffer(0, GameTable.SIZE);
            gameTableFileHandle.order(ByteOrder.LITTLE_ENDIAN);
        }
        catch (Exception e) {
            System.err.println("Game table mapping not found.");
            return false;
        }

        GameTable gameTable;
        try {
            gameTable = new GameTable(gameTableFileHandle);
        }
        catch (Exception e) {
            System.err.println("Unable to map Game table.");
            if (bwClient.getConfiguration().getDebugConnection()) {
                e.printStackTrace();
            }
            return false;
        }

        int latest = 0;
        for(int i = 0; i < GameTable.MAX_GAME_INSTANCES; i++) {
            GameInstance gameInstance = gameTable.gameInstances[i];
            System.out.println(i + " | " + gameInstance.serverProcessID + " | " + (gameInstance.isConnected ? 1 : 0) + " | " + gameInstance.lastKeepAliveTime);
            if (gameInstance.serverProcessID != 0 && !gameInstance.isConnected) {
                if ( gameTableIndex == -1 || latest == 0 || gameInstance.lastKeepAliveTime < latest ) {
                    latest = gameInstance.lastKeepAliveTime;
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

        final String sharedMemoryName = "Local\\bwapi_shared_memory_" + serverProcID;
        final String communicationPipe = "\\\\.\\pipe\\bwapi_pipe_" + serverProcID;
        try {
            pipeObjectHandle  = new RandomAccessFile(communicationPipe, "rw");
        }
        catch (Exception e) {
            System.err.println("Unable to open communications pipe: " + communicationPipe);
            if (bwClient.getConfiguration().getDebugConnection()) {
                e.printStackTrace();
            }
            gameTableFileHandle = null;
            return false;
        }
        System.out.println("Connected");

        // Expose the raw game data from shared memory via a ByteBuffer
        try {
            mapFileHandle = Kernel32.INSTANCE.MapViewOfFile(MappingKernel.INSTANCE
                            .OpenFileMapping(READ_WRITE, false, sharedMemoryName), READ_WRITE,
                    0, 0, GameData.SIZE).getByteBuffer(0, GameData.SIZE);
        }
        catch (Exception e) {
            System.err.println("Unable to open shared memory mapping: " + sharedMemoryName);
            if (bwClient.getConfiguration().getDebugConnection()) {
                e.printStackTrace();
            }
            pipeObjectHandle = null;
            gameTableFileHandle = null;
            return false;
        }
        try {
            clientData = new ClientData();
            clientData.setBuffer(mapFileHandle);
        }
        catch (Exception e) {
            System.err.println("Unable to map game data.");
            if (bwClient.getConfiguration().getDebugConnection()) {
                e.printStackTrace();
            }
            return false;
        }

        if (SUPPORTED_BWAPI_VERSION != clientData.gameData().getClient_version()) {
            System.err.println("Error: Client and Server are not compatible!");
            System.err.println("Client version: " + SUPPORTED_BWAPI_VERSION);
            System.err.println("Server version: " + clientData.gameData().getClient_version());
            disconnect();
            sleep(2000);
            return false;
        }
        byte code = 1;
        while (code != 2) {
            try {
                code = pipeObjectHandle.readByte();
            }
            catch (Exception e) {
                System.err.println("Unable to read pipe object.");
                if (bwClient.getConfiguration().getDebugConnection()) {
                    e.printStackTrace();
                }
                disconnect();
                return false;
            }
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
            // 1 is the "frame done" signal to BWAPI
            pipeObjectHandle.writeByte(1);
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
        metrics.getFrameDurationReceiveToSentGTC().stopTiming();
        if (bwClient.doTime()) {
            final int eventCount = clientData.gameData().getEventCount();
            metrics.getNumberOfEvents().record(eventCount);
            metrics.getNumberOfEventsTimesDurationReceiveToSent().record(eventCount * metrics.getFrameDurationReceiveToSent().getRunningTotal().getLast());
            metrics.getNumberOfEventsTimesDurationReceiveToSentGTC().record(eventCount * metrics.getFrameDurationReceiveToSentGTC().getRunningTotal().getLast());
        }

        // Listen for BWAPI to indicate that a new frame is ready
        if (bwClient.doTime()) {
            metrics.getCommunicationListenToReceive().startTiming();
        }
        boolean frameReady = false;
        while (!frameReady) {
            try {
                // 2 is the "frame ready" signal from BWAPI
                frameReady = pipeObjectHandle.readByte() == 2;
            } catch (Exception e) {
                System.err.println("failed, disconnecting");
                if (bwClient.getConfiguration().getDebugConnection()) {
                    e.printStackTrace();
                }
                disconnect();
                break;
            }
        }
        metrics.getCommunicationListenToReceive().stopTiming();
        metrics.getCommunicationSendToReceive().stopTiming();

        if (bwClient.doTime()) {
            metrics.getFrameDurationReceiveToSend().startTiming();
            metrics.getFrameDurationReceiveToSent().startTiming();
            metrics.getFrameDurationReceiveToSentGTC().startTiming();
        }
        metrics.getFrameDurationReceiveToReceive().stopTiming();
        metrics.getFrameDurationReceiveToReceiveGTC().stopTiming();
        if (bwClient.doTime()) {
            metrics.getFrameDurationReceiveToReceive().startTiming();
            metrics.getFrameDurationReceiveToReceiveGTC().startTiming();
        }
    }

    private void sleep(final int millis) {
        try {
            Thread.sleep(millis);
        }
        catch (Exception ignored) {
        }
    }
}
