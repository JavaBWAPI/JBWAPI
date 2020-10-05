package bwapi;

import com.sun.jna.Platform;
import sun.nio.ch.DirectBuffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Circular buffer of game states.
 */
class FrameBuffer {
    private static final int BUFFER_SIZE = ClientData.GameData.SIZE;

    private WrappedBuffer liveData;
    private PerformanceMetrics performanceMetrics;
    private BWClientConfiguration configuration;
    private int capacity;
    private int stepGame = 0;
    private int stepBot = 0;
    private ArrayList<WrappedBuffer> dataBuffer = new ArrayList<>();

    private final Lock lockWrite = new ReentrantLock();
    final Lock lockSize = new ReentrantLock();
    final Condition conditionSize = lockSize.newCondition();

    FrameBuffer(BWClientConfiguration configuration) {
        this.capacity = configuration.getAsyncFrameBufferCapacity();
        this.configuration = configuration;
        while(dataBuffer.size() < capacity) {
            dataBuffer.add(new WrappedBuffer(BUFFER_SIZE));
        }
    }

    /**
     * Resets for a new game
     */
    void initialize(WrappedBuffer liveData, PerformanceMetrics performanceMetrics) {
        this.liveData = liveData;
        this.performanceMetrics = performanceMetrics;
        stepGame = 0;
        stepBot = 0;
    }

    /**
     * @return The number of frames currently buffered ahead of the bot's current frame
     */
    synchronized int framesBuffered() {
        return stepGame - stepBot;
    }

    /**
     * @return Number of frames currently stored in the buffer
     */
    int size() {
        lockSize.lock();
        try {
            return framesBuffered();
        }  finally {
            lockSize.unlock();
        }
    }

    /**
     * @return Whether the frame buffer is empty and has no frames available for the bot to consume.
     */
    boolean empty() {
        return size() <= 0;
    }

    /**
     * @return Whether the frame buffer is full and can not buffer any additional frames.
     * When the frame buffer is full, JBWAPI must wait for the bot to complete a frame before returning control to StarCraft.
     */
    boolean full() {
        lockSize.lock();
        try {
            return framesBuffered() >= capacity;
        } finally {
            lockSize.unlock();
        }
    }

    private int indexGame() {
        return stepGame % capacity;
    }

    private int indexBot() {
        return stepBot % capacity;
    }

    /**
     * Copy dataBuffer from shared memory into the head of the frame buffer.
     */
    void enqueueFrame() {
        lockWrite.lock();
        try {
            lockSize.lock();
            try {
                while (full()) {
                    configuration.log("Main: Waiting for frame buffer capacity");
                    performanceMetrics.getIntentionallyBlocking().startTiming();
                    conditionSize.awaitUninterruptibly();
                }
                performanceMetrics.getIntentionallyBlocking().stopTiming();
            } finally { lockSize.unlock(); };

            // For the first frame of the game, populate all buffers completely
            // This is to ensure all buffers have access to immutable data like regions/walkability/buildability
            // Afterwards, we want to shorten this process by only copying important and mutable data
            if (stepGame == 0) {
                for (WrappedBuffer frameBuffer : dataBuffer) {
                    copyBuffer(liveData, frameBuffer, true);
                }
            } else {
                performanceMetrics.getCopyingToBuffer().time(() -> {
                    WrappedBuffer dataTarget = dataBuffer.get(indexGame());
                    copyBuffer(liveData, dataTarget, false);
                });
            }

            lockSize.lock();
            try {
                performanceMetrics.getFrameBufferSize().record(framesBuffered());
                ++stepGame;
                conditionSize.signalAll();
            } finally { lockSize.unlock(); }
        } finally { lockWrite.unlock(); }
    }

    /**
     * Peeks the front-most value in the buffer.
     */
    WrappedBuffer peek() {
        lockSize.lock();
        try {
            while(empty()) conditionSize.awaitUninterruptibly();
            return dataBuffer.get(indexBot());
        } finally { lockSize.unlock(); }
    }

    /**
     * Removes the front-most frame in the buffer.
     */
    void dequeue() {
        lockSize.lock();
        try {
            while(empty()) conditionSize.awaitUninterruptibly();
            ++stepBot;
            conditionSize.signalAll();
        } finally { lockSize.unlock(); }
    }

    /**
     *
     * @param source Address to copy from
     * @param destination Address to copy to
     * @param size Number of bytes to copy
     * @return True if the copy succeeded
     */
    private boolean tryMemcpyBuffer(WrappedBuffer source, WrappedBuffer destination, long offset, int size) {
        long addressSource = source.getAddress() + offset;
        long addressDestination = destination.getAddress() + offset;
        try {
            if (Platform.isWindows()) {
                if (Platform.is64Bit()) {
                    MSVCRT.INSTANCE.memcpy(addressDestination, addressSource, size);
                    return true;
                } else {
                    MSVCRT.INSTANCE.memcpy((int) addressDestination, (int) addressSource, size);
                    return true;
                }
            }
        }
        catch(Exception ignored) {}
        return false;
    }

    void copyBuffer(WrappedBuffer source, WrappedBuffer destination, boolean copyEverything) {
        /*
        The speed at which we copy data into the frame buffer is a major cost of JBWAPI's asynchronous operation.
        Copy times observed in the wild for the complete buffer usually range from 2.6ms - 19ms
        but are prone to large amounts of variance.

        The normal Java way to execute this copy is via ByteBuffer.put(), which has reasonably good performance characteristics.
        Experiments in 64-bit JRE have shown that using a native memcpy achieves a 35% speedup.
        Experiments in 32-bit JRE show no difference in performance.

        So, speculatively, we attempt to do a native memcpy.
        */

        if (copyEverything) {
            if (tryMemcpyBuffer(source, destination, 0, FrameBuffer.BUFFER_SIZE)) {
                return;
            }
        } else {
            // After the buffer has been filled the first time,
            // we can omit copying blocks of data which are unused or which don't change after game start.
            // These blocks account for *most* of the 33MB shared memory,
            // so omitting them drastically reduces the copy duration
            final int STATICTILES_START = 3447004; // getGroundHeight, isWalkable, isBuildable
            final int STATICTILES_END = 4823260;
            final int REGION_START = 5085404; // getMapTileRegionId, ..., getRegions
            final int REGION_END = 10586480;
            final int STRINGSSHAPES_START = 10962632; // getStringCount, ... getShapes
            final int STRINGSHAPES_END = 32242636;
            final int UNITFINDER_START = 32962644;
            if (
                tryMemcpyBuffer(source, destination, 0, STATICTILES_START)
                && tryMemcpyBuffer(source, destination, STATICTILES_END, REGION_START - STATICTILES_END)
                && tryMemcpyBuffer(source, destination, REGION_END, STRINGSSHAPES_START - REGION_END)
                && tryMemcpyBuffer(source, destination, STRINGSHAPES_END, UNITFINDER_START - STRINGSHAPES_END)) {
                return;
            }
        }

        // There's no specific case where we expect to fail above,
        // but this is a safe fallback regardless,
        // and serves to document the known-good (and cross-platform, for BWAPI 5) way to executing the copy.

        // TODO: This part was written for ByteBuffer.
        // Adapt it for WrappedBuffer
        /*
        source.rewind();
        destination.rewind();
        destination.put(liveData);
        */
    }
}
