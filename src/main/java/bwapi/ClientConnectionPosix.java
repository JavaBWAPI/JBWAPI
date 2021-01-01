package bwapi;

import com.sun.jna.LastErrorException;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.linux.Fcntl;
import com.sun.jna.platform.linux.LibC;
import com.sun.jna.platform.linux.LibRT;
import org.newsclub.net.unix.AFUNIXSocket;
import org.newsclub.net.unix.AFUNIXSocketAddress;

import java.io.File;
import java.io.IOException;

import static com.sun.jna.platform.linux.Mman.*;

class ClientConnectionPosix implements ClientConnection {
    interface LibCExt extends LibC {
        LibCExt INSTANCE = Native.load(LibCExt.class);

        Pointer mmap(Pointer addr, int length, int prot, int flags, int fd, int offset) throws LastErrorException;
    }

    private AFUNIXSocket syncSocket = null;

    @Override
    public void disconnect() {
        if (syncSocket != null) {
            try {
                syncSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            syncSocket = null;
        }
    }

    @Override
    public WrappedBuffer getGameTable() {
        int fd = LibRT.INSTANCE.shm_open("/bwapi_shared_memory_game_list", Fcntl.O_RDWR, 0);
        if (fd < 0) throw new IllegalStateException("SHM not found");
        Pointer gameTableView = LibCExt.INSTANCE.mmap(Pointer.NULL, GameTable.SIZE, PROT_READ | PROT_WRITE, MAP_SHARED, fd, 0);
        return new WrappedBuffer(gameTableView, GameTable.SIZE);
    }

    @Override
    public WrappedBuffer getSharedMemory(int serverProcID) {
        String sharedMemoryName = "/bwapi_shared_memory_" + serverProcID;
        try {
            Pointer mapFileView = LibCExt.INSTANCE.mmap(Pointer.NULL, ClientData.GameData.SIZE, PROT_READ | PROT_WRITE, MAP_SHARED, LibRT.INSTANCE.shm_open(sharedMemoryName, Fcntl.O_RDWR, 0),
                    0);
            return new WrappedBuffer(mapFileView, ClientData.GameData.SIZE);
        } catch (Exception e) {
            throw new SharedMemoryConnectionException(sharedMemoryName, e);
        }
    }

    @Override
    public void connectSharedLock(int serverProcID) {
        final String communicationSocket = "/tmp/bwapi_socket_" + serverProcID;
        try {
            syncSocket = AFUNIXSocket.newInstance();
            syncSocket.connect(new AFUNIXSocketAddress(new File(communicationSocket)));
        } catch (IOException e) {
            syncSocket = null;
            throw new SharedLockConnectionException("Unable to open communications socket: " + communicationSocket, e);
        }
    }

    @Override
    public void waitForServerReady() {
        // NOOP
    }

    @Override
    public void waitForServerData() throws IOException {
        while ((syncSocket.getInputStream().read() != 2)) ;
    }

    @Override
    public void submitClientData() throws IOException {
        syncSocket.getOutputStream().write(1);
    }
}
