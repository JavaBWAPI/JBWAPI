package bwapi;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.win32.W32APIOptions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UncheckedIOException;

class ClientConnectionW32 implements ClientConnection {
    private static final int READ_WRITE = 0x1 | 0x2 | 0x4;

    interface MappingKernel extends Kernel32 {
        MappingKernel INSTANCE = Native.load(MappingKernel.class, W32APIOptions.DEFAULT_OPTIONS);

        HANDLE OpenFileMapping(int desiredAccess, boolean inherit, String name);
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
        final Pointer gameTableView = Kernel32.INSTANCE.MapViewOfFile(MappingKernel.INSTANCE
                        .OpenFileMapping(READ_WRITE, false, "Local\\bwapi_shared_memory_game_list"), READ_WRITE,
                0, 0, GameTable.SIZE);
        return new WrappedBuffer(gameTableView, GameTable.SIZE);
    }

    @Override
    public WrappedBuffer getSharedMemory(int serverProcID) {
        String sharedMemoryName = "Local\\bwapi_shared_memory_" + serverProcID;
        try {
            final Pointer mapFileView = Kernel32.INSTANCE.MapViewOfFile(MappingKernel.INSTANCE
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
    public void waitForServerReady() throws IOException {
        byte code = 1;
        while (code != 2) {
            try {
                code = pipeObjectHandle.readByte();
            } catch (IOException e) {
                throw new UncheckedIOException("Unable to read pipe object.", e);
            }
        }
    }

    @Override
    public void waitForServerData() throws IOException {
        byte code = 1;
        pipeObjectHandle.writeByte(code);
        while (code != 2) {
            code = pipeObjectHandle.readByte();
        }
    }

    @Override
    public void submitClientData() throws IOException {
        // Noop
    }
}
