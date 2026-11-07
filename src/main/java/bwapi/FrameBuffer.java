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
    static final int BUFFER_SIZE = PackedWrappedBuffer.BUFFER_SIZE;

    static final int INITIAL_UNIT_COUNT_OFFSET = 69648;
    static final int UNITS_START = 69656;
    static final int MAX_UNITS = 10000;
    static final int UNITS_END = UNITS_START + MAX_UNITS * ClientData.UnitData.SIZE;

    static final int EVENT_COUNT_OFFSET = 10586620;
    static final int EVENTS_START = 10586624;
    static final int MAX_EVENTS = 10000;
    static final int EVENTS_HEADER_SIZE = EVENTS_START - PackedWrappedBuffer.REGION_END;

    static final int EVENT_STRING_COUNT_OFFSET = 10706624;
    static final int EVENT_STRING_SIZE = 256;
    static final int MAX_EVENT_STRINGS = 1000;

    private static final Unsafe unsafe = UnsafeTools.getUnsafe();

    private WrappedBuffer liveData;
    private PerformanceMetrics performanceMetrics;
    private final BWClientConfiguration configuration;
    private final int capacity;
    private int stepGame = 0;
    private int stepBot = 0;
    private int maxUnitCount = 0;
    private final ArrayList<WrappedBuffer> dataBuffer = new ArrayList<>();

    private final Lock lockWrite = new ReentrantLock();
    final Lock lockSize = new ReentrantLock();
    final Condition conditionSize = lockSize.newCondition();

    FrameBuffer(BWClientConfiguration configuration) {
        this.capacity = configuration.getAsyncFrameBufferCapacity();
        this.configuration = configuration;
        while(dataBuffer.size() < capacity) {
            dataBuffer.add(new PackedWrappedBuffer());
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
        maxUnitCount = 0;
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

    /**
     * The speed at which we copy data into the frame buffer is a major cost of JBWAPI's asynchronous operation.
     * Copy times observed in the wild for the complete 33 MB shared memory buffer used to range from 2.6ms - 19ms
     * and are prone to large amounts of variance.
     *
     * To minimize both memory footprint and copy time, we:
     * 1. Omit static map and region data (groundHeight, isWalkable, isBuildable, regions), which are read directly
     *    from the main shared memory buffer via AsyncClientData.
     * 2. Omit client-to-server output buffers (strings, shapes, commands, unitCommands), which are flushed directly
     *    to live shared memory by SideEffectQueue at the end of the frame.
     * 3. Pack the remaining 3 dynamic ranges into a 4.08 MB PackedWrappedBuffer (using pre-shifted slice views so
     *    static BWAPI field offsets work without runtime branching).
     * 4. Within those ranges, only copy the currently active prefixes of units[0..maxUnitCount),
     *    events[0..eventCount), and eventStrings[0..eventStringCount), skipping their unused tails.
     *    (Further sub-slicing the ~349 KB fixed base, e.g. mapWidth slicing on Slice 2's 4 tile tables or skipping
     *    unused players/unitArray/nukeDots, only saves ~5-40 us and isn't worth splitting 5 contiguous copies into 10.)
     *
     * Approximate bytes copied per frame (Fastest game speed ~= 23.8 frames/sec, ~1,428 frames/minute):
     * - Fixed base (header, forces, players, bullets, dynamic tile flags): ~349 KB (0.35 MB)
     * - Game start (Frame 0, ~300 initial units):                          ~450 KB (0.45 MB)
     * - Mid-game   (~8-10 min / ~7,500 frames, ~1,000 cumulative units):   ~685 KB (0.69 MB)
     * - Late-game  (~20+ min / ~20,000 frames, ~2,500 cumulative units):   ~1.19 MB (max cap at 10,000 units: 4.08 MB)
     *
     * Estimated per-frame copy times across hardware and CPU core counts:
     * +-----------------------------------------------+---------------+---------------+---------------+---------------+
     * | Environment / Hardware & Core Count           | Start (450KB) | Mid (685KB)   | Late (1.19MB) | Max (4.08MB)  |
     * +-----------------------------------------------+---------------+---------------+---------------+---------------+
     * | Weak / Virtualized (1-vCPU Docker / shared VM | 0.12 - 0.35ms | 0.18 - 0.50ms | 0.30 - 0.85ms | 1.00 - 2.80ms |
     * | ~3-5 GB/s; Wine, Client & Bot share 1 core)   |               |               |               |               |
     * +-----------------------------------------------+---------------+---------------+---------------+---------------+
     * | Normal Desktop / Tourney VM (2-4 cores, DDR4, | 0.04 - 0.08ms | 0.06 - 0.12ms | 0.10 - 0.20ms | 0.30 - 0.60ms |
     * | ~12 GB/s; Copy runs in parallel with Bot)     |               |               |               |               |
     * +-----------------------------------------------+---------------+---------------+---------------+---------------+
     * | Fast Modern CPU (4+ cores, DDR5, ~30+ GB/s;   | 0.01 - 0.03ms | 0.02 - 0.05ms | 0.04 - 0.08ms | 0.12 - 0.25ms |
     * | fits in L2/L3, finishes before Bot wakes)     |               |               |               |               |
     * +-----------------------------------------------+---------------+---------------+---------------+---------------+
     */
    void copyBuffer(WrappedBuffer source, WrappedBuffer destination) {
        long srcAddr = source.getAddress();
        long dstAddr = destination.getAddress();

        int eventCount = clamp(source.getInt(EVENT_COUNT_OFFSET), 0, MAX_EVENTS);
        int eventStringCount = clamp(source.getInt(EVENT_STRING_COUNT_OFFSET), 0, MAX_EVENT_STRINGS);
        int activeUnits = updateMaxUnitCount(source, eventCount);

        // Slice 1a (dynamic size): header, forces, players, initialUnitCount, and active units [0 .. activeUnits)
        copySlice(srcAddr, dstAddr, 0, 0, UNITS_START + activeUnits * ClientData.UnitData.SIZE);
        // Slice 1b (static size): unitArray, bullets, nukeDots, frameCount, and other post-unit Slice 1 fields
        copySlice(srcAddr, dstAddr, UNITS_END, 0, PackedWrappedBuffer.STATICTILES_START - UNITS_END);
        // Slice 2 (static size): tile visibility, exploration, creep, and occupation flags
        copySlice(srcAddr, dstAddr, PackedWrappedBuffer.STATICTILES_END, PackedWrappedBuffer.DELTA_RANGE2, PackedWrappedBuffer.DYNAMIC_SLICE2_SIZE);
        // Slice 3a (dynamic size): startLocations, game state flags, selectedUnits, self/enemy/neutral, eventCount, and active events [0 .. eventCount)
        copySlice(srcAddr, dstAddr, PackedWrappedBuffer.REGION_END, PackedWrappedBuffer.DELTA_RANGE3, EVENTS_HEADER_SIZE + eventCount * ClientData.Event.SIZE);
        // Slice 3b (dynamic size): eventStringCount and active eventStrings [0 .. eventStringCount)
        copySlice(srcAddr, dstAddr, EVENT_STRING_COUNT_OFFSET, PackedWrappedBuffer.DELTA_RANGE3, 4 + eventStringCount * EVENT_STRING_SIZE);
    }

    int getMaxUnitCount() {
        return maxUnitCount;
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    private int updateMaxUnitCount(WrappedBuffer source, int eventCount) {
        int initialUnits = clamp(source.getInt(INITIAL_UNIT_COUNT_OFFSET), 0, MAX_UNITS);
        if (initialUnits > maxUnitCount) {
            maxUnitCount = initialUnits;
        }

        for (int i = 0; i < eventCount; i++) {
            int eventOffset = EVENTS_START + i * ClientData.Event.SIZE;
            if (source.getInt(eventOffset) == EventType.UnitDiscover.id) {
                int unitId = source.getInt(eventOffset + 4);
                if (unitId >= 0 && unitId < MAX_UNITS && unitId + 1 > maxUnitCount) {
                    maxUnitCount = unitId + 1;
                }
            }
        }
        return maxUnitCount;
    }

    private static void copySlice(long srcAddr, long dstAddr, int srcOffset, int delta, int length) {
        unsafe.copyMemory(srcAddr + srcOffset, dstAddr + srcOffset - delta, length);
    }
}
