package bwapi;

import sun.misc.Unsafe;

import java.util.ArrayList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Circular buffer of game states.
 */
class FrameBuffer {
    static final int STATICTILES_START = 3447004; // getGroundHeight, isWalkable, isBuildable
    static final int STATICTILES_END = 4823260;
    static final int REGION_START = 5085404; // getMapTileRegionId, ..., getRegions
    static final int REGION_END = 10586480;
    static final int STRINGSSHAPES_START = 10962632; // getStringCount, ... getShapes
    static final int STRINGSHAPES_END = 32242636;
    static final int UNITFINDER_START = 32962644;

    static final int DYNAMIC_SLICE1_SIZE = STATICTILES_START; // 3447004
    static final int DYNAMIC_SLICE2_SIZE = REGION_START - STATICTILES_END; // 262144
    static final int DYNAMIC_SLICE3_SIZE = STRINGSSHAPES_START - REGION_END; // 376152
    static final int DYNAMIC_SLICE4_SIZE = UNITFINDER_START - STRINGSHAPES_END; // 720008

    static final int DEST_SLICE1_OFFSET = 0;
    static final int DEST_SLICE2_OFFSET = DYNAMIC_SLICE1_SIZE; // 3447004
    static final int DEST_SLICE3_OFFSET = DEST_SLICE2_OFFSET + DYNAMIC_SLICE2_SIZE; // 3709148
    static final int DEST_SLICE4_OFFSET = DEST_SLICE3_OFFSET + DYNAMIC_SLICE3_SIZE; // 4085300

    static final int BUFFER_SIZE = DEST_SLICE4_OFFSET + DYNAMIC_SLICE4_SIZE; // 4805308
    private static final Unsafe unsafe = UnsafeTools.getUnsafe();

    private WrappedBuffer liveData;
    private PerformanceMetrics performanceMetrics;
    private final BWClientConfiguration configuration;
    private final int capacity;
    private int stepGame = 0;
    private int stepBot = 0;
    private final ArrayList<WrappedBuffer> dataBuffer = new ArrayList<>();

    private final Lock lockWrite = new ReentrantLock();
    final Lock lockSize = new ReentrantLock();
    final Condition conditionSize = lockSize.newCondition();

    FrameBuffer(BWClientConfiguration configuration) {
        this.capacity = configuration.getAsyncFrameBufferCapacity();
        this.configuration = configuration;
        while(dataBuffer.size() < capacity) {
            dataBuffer.add(new WrappedBuffer(BUFFER_SIZE, true));
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

            // Copy dynamic game information into the current frame buffer slot.
            // Static information (e.g. walkability/buildability/regions) is accessed
            // directly from the main shared memory buffer by AsyncClientData.
            if (stepGame == 0) {
                copyBuffer(liveData, dataBuffer.get(indexGame()));
            } else {
                performanceMetrics.getCopyingToBuffer().time(() -> {
                    WrappedBuffer dataTarget = dataBuffer.get(indexGame());
                    copyBuffer(liveData, dataTarget);
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

    void copyBuffer(WrappedBuffer source, WrappedBuffer destination) {
        unsafe.copyMemory(source.getAddress() + 0, destination.getAddress() + DEST_SLICE1_OFFSET, DYNAMIC_SLICE1_SIZE);
        unsafe.copyMemory(source.getAddress() + STATICTILES_END, destination.getAddress() + DEST_SLICE2_OFFSET, DYNAMIC_SLICE2_SIZE);
        unsafe.copyMemory(source.getAddress() + REGION_END, destination.getAddress() + DEST_SLICE3_OFFSET, DYNAMIC_SLICE3_SIZE);
        unsafe.copyMemory(source.getAddress() + STRINGSHAPES_END, destination.getAddress() + DEST_SLICE4_OFFSET, DYNAMIC_SLICE4_SIZE);
    }
}
