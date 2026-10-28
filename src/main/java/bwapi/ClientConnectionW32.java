package bwapi;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Default Windows BWAPI pipe connection with shared memory.
 */
class ClientConnectionW32 implements ClientConnection {
    private static final int READ_WRITE = 0x1 | 0x2 | 0x4;

    /**
     * Opens the BWAPI server's game table and game-state shared-memory mappings.
     * Binding only these two kernel32 functions avoids initializing JNA's full
     * Kernel32 proxy before the client can discover a server.
     */
    interface MappingKernel extends StdCallLibrary {
        MappingKernel INSTANCE = Native.load("kernel32", MappingKernel.class, W32APIOptions.DEFAULT_OPTIONS);

        HANDLE OpenFileMapping(int desiredAccess, boolean inherit, String name);

        Pointer MapViewOfFile(HANDLE mapping, int desiredAccess, int offsetHigh, int offsetLow, int bytesToMap);
    }


    private RandomAccessFile pipeObjectHandle = null;

    @Override
    public void disconnect() {
        if (pipeObjectHandle != null) {
            try {
                pipeObjectHandle.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            pipeObjectHandle = null;
        }
    }

    @Override
    public WrappedBuffer getGameTable() {
        final Pointer gameTableView = MappingKernel.INSTANCE.MapViewOfFile(MappingKernel.INSTANCE
                        .OpenFileMapping(READ_WRITE, false, "Local\\bwapi_shared_memory_game_list"), READ_WRITE,
                0, 0, GameTable.SIZE);
        return new WrappedBuffer(gameTableView, GameTable.SIZE);
    }

    @Override
    public WrappedBuffer getSharedMemory(int serverProcID) {
        String sharedMemoryName = "Local\\bwapi_shared_memory_" + serverProcID;
        try {
            final Pointer mapFileView = MappingKernel.INSTANCE.MapViewOfFile(MappingKernel.INSTANCE
                            .OpenFileMapping(READ_WRITE, false, sharedMemoryName), READ_WRITE,
                    0, 0, ClientData.GameData.SIZE);
            return new WrappedBuffer(mapFileView, ClientData.GameData.SIZE);
        } catch (Exception e) {
            throw new SharedMemoryConnectionException(sharedMemoryName, e);
        }
    }

    @Override
    public void connectSharedLock(int serverProcID) {
        final String communicationPipe = "\\\\.\\pipe\\bwapi_pipe_" + serverProcID;
        try {
            pipeObjectHandle = new RandomAccessFile(communicationPipe, "rw");
        } catch (FileNotFoundException e) {
            throw new SharedLockConnectionException("Unable to open communications pipe: " + communicationPipe, e);
        }
    }

    @Override
    public void waitForServerData() throws IOException {
        while (pipeObjectHandle.readByte() != 2) ;
    }

    @Override
    public void submitClientData() throws IOException {
        pipeObjectHandle.writeByte(1);
    }
}
