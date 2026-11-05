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
    static final int BUFFER_SIZE = AsyncClientData.BUFFER_SIZE;
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
        unsafe.copyMemory(source.getAddress() + 0, destination.getAddress() + AsyncClientData.DEST_SLICE1_OFFSET, AsyncClientData.DYNAMIC_SLICE1_SIZE);
        unsafe.copyMemory(source.getAddress() + AsyncClientData.STATICTILES_END, destination.getAddress() + AsyncClientData.DEST_SLICE2_OFFSET, AsyncClientData.DYNAMIC_SLICE2_SIZE);
        unsafe.copyMemory(source.getAddress() + AsyncClientData.REGION_END, destination.getAddress() + AsyncClientData.DEST_SLICE3_OFFSET, AsyncClientData.DYNAMIC_SLICE3_SIZE);
        unsafe.copyMemory(source.getAddress() + AsyncClientData.STRINGSHAPES_END, destination.getAddress() + AsyncClientData.DEST_SLICE4_OFFSET, AsyncClientData.DYNAMIC_SLICE4_SIZE);
    }
}
