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
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.win32.W32APIOptions;

import java.io.RandomAccessFile;

class Client {
    interface MappingKernel extends Kernel32 {
        MappingKernel INSTANCE = Native.load(MappingKernel.class, W32APIOptions.DEFAULT_OPTIONS);

        HANDLE OpenFileMapping(int desiredAccess, boolean inherit, String name);
    }

    public interface EventHandler {
        void operation(ClientData.Event event);
    }

    private static final int READ_WRITE = 0x1 | 0x2 | 0x4;

    private static final int SUPPORTED_BWAPI_VERSION = 10003;
    static final int MAX_COUNT = 19999;

    private ClientData clientData;
    private ClientData.GameData gameData;
    private boolean connected = false;
    private RandomAccessFile pipeObjectHandle = null;
    private WrappedBuffer mapFileHandle = null;
    private WrappedBuffer gameTableFileHandle = null;

    private boolean debugConnection = false;

    Client(boolean debugConnection) {
        this.debugConnection = debugConnection;
    }

    /**
     * For test purposes only
     */
    Client(final WrappedBuffer buffer) {
        clientData = new ClientData(buffer);
        gameData = clientData.new GameData(0);
    }

    ClientData clientData() {
        return clientData;
    }

    GameData gameData() {
        return gameData;
    }

    boolean isConnected() {
        return connected;
    }

    void reconnect() {
        while (!connect()) {
            sleep(1000);
        }
    }

    void disconnect() {
        if (debugConnection) {
            System.err.print("Disconnect called by: ");
            System.err.println(Thread.currentThread().getStackTrace()[2]);
        }
        if (!connected) {
            return;
        }

        if (pipeObjectHandle != null) {
            try {
                pipeObjectHandle.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            pipeObjectHandle = null;
        }

        mapFileHandle = null;
        gameTableFileHandle = null;
        gameData = null;
        connected = false;
    }

    boolean connect() {
        if (connected) {
            System.err.println("Already connected");
            return true;
        }

        int serverProcID = -1;
        int gameTableIndex = -1;

        try {
            final Pointer gameTableView = Kernel32.INSTANCE.MapViewOfFile(MappingKernel.INSTANCE
                            .OpenFileMapping(READ_WRITE, false, "Local\\bwapi_shared_memory_game_list"), READ_WRITE,
                    0, 0, GameTable.SIZE);
            gameTableFileHandle = new WrappedBuffer(gameTableView, GameTable.SIZE);
        } catch (Exception e) {
            System.err.println("Game table mapping not found.");
            return false;
        }

        GameTable gameTable;
        try {
            gameTable = new GameTable(gameTableFileHandle);
        } catch (Exception e) {
            System.err.println("Unable to map Game table.");
            if (debugConnection) {
                e.printStackTrace();
            }
            return false;
        }

        int latest = 0;
        for (int i = 0; i < GameTable.MAX_GAME_INSTANCES; i++) {
            GameInstance gameInstance = gameTable.gameInstances[i];
            System.out.println(i + " | " + gameInstance.serverProcessID + " | " + (gameInstance.isConnected ? 1 : 0) + " | " + gameInstance.lastKeepAliveTime);
            if (gameInstance.serverProcessID != 0 && !gameInstance.isConnected) {
                if (gameTableIndex == -1 || latest == 0 || gameInstance.lastKeepAliveTime < latest) {
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
            pipeObjectHandle = new RandomAccessFile(communicationPipe, "rw");
        } catch (Exception e) {
            System.err.println("Unable to open communications pipe: " + communicationPipe);
            if (debugConnection) {
                e.printStackTrace();
            }
            gameTableFileHandle = null;
            return false;
        }
        System.out.println("Connected");

        try {
            final Pointer mapFileView = Kernel32.INSTANCE.MapViewOfFile(MappingKernel.INSTANCE
                            .OpenFileMapping(READ_WRITE, false, sharedMemoryName), READ_WRITE,
                    0, 0, GameData.SIZE);
            mapFileHandle = new WrappedBuffer(mapFileView, GameData.SIZE);
        } catch (Exception e) {
            System.err.println("Unable to open shared memory mapping: " + sharedMemoryName);
            if (debugConnection) {
                e.printStackTrace();
            }
            pipeObjectHandle = null;
            gameTableFileHandle = null;
            return false;
        }
        try {
            clientData = new ClientData(mapFileHandle);
            gameData = clientData.new GameData(0);
        } catch (Exception e) {
            System.err.println("Unable to map game data.");
            if (debugConnection) {
                e.printStackTrace();
            }
            return false;
        }

        if (SUPPORTED_BWAPI_VERSION != gameData.getClient_version()) {
            System.err.println("Error: Client and Server are not compatible!");
            System.err.println("Client version: " + SUPPORTED_BWAPI_VERSION);
            System.err.println("Server version: " + gameData.getClient_version());
            disconnect();
            sleep(2000);
            return false;
        }
        byte code = 1;
        while (code != 2) {
            try {
                code = pipeObjectHandle.readByte();
            } catch (Exception e) {
                System.err.println("Unable to read pipe object.");
                if (debugConnection) {
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

    void update(final EventHandler handler) {
        byte code = 1;
        try {
            pipeObjectHandle.writeByte(code);
        } catch (Exception e) {
            System.err.println("failed, disconnecting");
            if (debugConnection) {
                e.printStackTrace();
            }
            disconnect();
            return;
        }
        while (code != 2) {
            try {
                code = pipeObjectHandle.readByte();
            } catch (Exception e) {
                System.err.println("failed, disconnecting");
                if (debugConnection) {
                    e.printStackTrace();
                }
                disconnect();
                return;
            }
        }
        for (int i = 0; i < gameData.getEventCount(); i++) {
            handler.operation(gameData.getEvents(i));
        }
    }

    String eventString(final int s) {
        return gameData.getEventStrings(s);
    }

    int addString(final String string) {
        int stringCount = gameData.getStringCount();
        if (stringCount >= MAX_COUNT) {
            throw new IllegalStateException("Too many strings!");
        }

        gameData.setStringCount(stringCount + 1);
        gameData.setStrings(stringCount, string);
        return stringCount;
    }

    Shape addShape() {
        int shapeCount = gameData.getShapeCount();
        if (shapeCount >= MAX_COUNT) {
            throw new IllegalStateException("Too many shapes!");
        }
        gameData.setShapeCount(shapeCount + 1);
        return gameData.getShapes(shapeCount);
    }

    Command addCommand() {
        final int commandCount = gameData.getCommandCount();
        if (commandCount >= MAX_COUNT) {
            throw new IllegalStateException("Too many commands!");
        }
        gameData.setCommandCount(commandCount + 1);
        return gameData.getCommands(commandCount);
    }

    ClientData.UnitCommand addUnitCommand() {
        int unitCommandCount = gameData.getUnitCommandCount();
        if (unitCommandCount >= MAX_COUNT) {
            throw new IllegalStateException("Too many unit commands!");
        }
        gameData.setUnitCommandCount(unitCommandCount + 1);
        return gameData.getUnitCommands(unitCommandCount);
    }

    private void sleep(final int millis) {
        try {
            Thread.sleep(millis);
        } catch (Exception ignored) {
        }
    }
}
