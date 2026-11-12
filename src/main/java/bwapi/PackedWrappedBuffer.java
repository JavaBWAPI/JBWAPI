package bwapi;

/**
 * Packed off-heap buffer for asynchronous frame slots that stores only dynamic game data slices
 * and exposes pre-shifted slice views so static BWAPI offsets work without runtime branching.
 */
class PackedWrappedBuffer extends WrappedBuffer {
    static final int STATICTILES_START = 3447004;
    static final int STATICTILES_END = 4823260;
    static final int REGION_START = 5085404;
    static final int REGION_END = 10586480;
    static final int STRINGSSHAPES_START = 10962632;

    static final int DYNAMIC_SLICE1_SIZE = STATICTILES_START;
    static final int DYNAMIC_SLICE2_SIZE = REGION_START - STATICTILES_END;
    static final int DYNAMIC_SLICE3_SIZE = STRINGSSHAPES_START - REGION_END;

    static final int DEST_SLICE1_OFFSET = 0;
    static final int DEST_SLICE2_OFFSET = DYNAMIC_SLICE1_SIZE;
    static final int DEST_SLICE3_OFFSET = DEST_SLICE2_OFFSET + DYNAMIC_SLICE2_SIZE;

    static final int BUFFER_SIZE = DEST_SLICE3_OFFSET + DYNAMIC_SLICE3_SIZE;

    static final int DELTA_RANGE2 = STATICTILES_END - STATICTILES_START;
    static final int DELTA_RANGE3 = DELTA_RANGE2 + (REGION_END - REGION_START);

    final WrappedBuffer slice2;
    final WrappedBuffer slice3;

    PackedWrappedBuffer() {
        this(BUFFER_SIZE);
    }

    PackedWrappedBuffer(final int size) {
        super(size);
        long addr = getAddress();
        this.slice2 = new WrappedBuffer(getBuffer(), addr - DELTA_RANGE2);
        this.slice3 = new WrappedBuffer(getBuffer(), addr - DELTA_RANGE3);
    }

    PackedWrappedBuffer(final WrappedBuffer unpacked) {
        super(null, 0L);
        this.slice2 = unpacked;
        this.slice3 = unpacked;
    }
}
