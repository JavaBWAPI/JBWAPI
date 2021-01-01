package bwapi;

import java.io.IOException;

interface ClientConnection {
    void disconnect();

    WrappedBuffer getGameTable();

    WrappedBuffer getSharedMemory(int serverProcID);

    void connectSharedLock(int serverProcID) throws IOException;

    void waitForServerReady() throws IOException;

    void waitForServerData() throws IOException;

    void submitClientData() throws IOException;
}

class SharedMemoryConnectionException extends RuntimeException {
    public SharedMemoryConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}

class SharedLockConnectionException extends RuntimeException {
    public SharedLockConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}