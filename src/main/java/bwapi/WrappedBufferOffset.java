package bwapi;

/**
 * Like WrappedBuffer but to only copy the dynamic parts of the source WrappedBuffer.
 *
 * <p> For now this hardcodes a few things (size, offsets).
 * TODO: the `put` operations might not need the check (always dynamic?)
 * TODO: this class might not even be required if the Game class caches all initial static calls.
 */
class WrappedBufferOffset extends WrappedBuffer {
    private WrappedBuffer sourceBuffer;

    WrappedBufferOffset() {
        super(DynamicData.SIZE);
    }

    void setSourceBuffer(WrappedBuffer sourceBuffer) {
        if (sourceBuffer instanceof WrappedBufferOffset) {
            throw new RuntimeException("Only Non-Offset buffer can be sourceBuffer.");
        }
        this.sourceBuffer = sourceBuffer;
    }

    void initialize() {
        // Copy the dynamic values from the underlying sourceBuffer.
        for (MemRegion region : DynamicData.MEM_REGIONS) {
            copyBuffer(region.start, region.offset, region.end - region.start);
        }
    }

    long applyOffset(int offset) {
        // Only 4 region checks.
        for (MemRegion r : DynamicData.MEM_REGIONS) {
            if (offset >= r.start && offset < r.end) {
                return address + r.offset + (offset - r.start);
            }
        }
        return sourceBuffer.address + offset;
    }

    byte getByte(final int offset) {
        return UNSAFE.getByte(applyOffset(offset));
    }

    void putByte(final int offset, final byte value) {
        UNSAFE.putByte(applyOffset(offset), value);
    }

    short getShort(final int offset) {
        return UNSAFE.getShort(applyOffset(offset));
    }

    void putShort(final int offset, final short value) {
        UNSAFE.putShort(applyOffset(offset), value);
    }

    int getInt(final int offset) {
        return UNSAFE.getInt(applyOffset(offset));
    }

    void putInt(final int offset, final int value) {
        UNSAFE.putInt(applyOffset(offset), value);
    }

    double getDouble(final int offset) {
        return UNSAFE.getDouble(applyOffset(offset));
    }

    void putDouble(final int offset, final double value) {
        UNSAFE.putDouble(applyOffset(offset), value);
    }


    String getString(final int offset, final int maxLen) {
        final char[] buf = new char[maxLen];
        long start = applyOffset(offset);
        long pos = start;
        for (int i = 0; i < maxLen; i++) {
            byte b = UNSAFE.getByte(pos);
            if (b == 0) break;
            buf[i] = (char) (b & 0xff);
            pos++;
        }
        return new String(buf, 0, (int) (pos - start));

    }

    void putString(final int offset, final int maxLen, final String string) {
        long pos = applyOffset(offset);
        for (int i = 0; i < Math.min(string.length(), maxLen - 1); i++) {
            UNSAFE.putByte(pos, (byte) string.charAt(i));
            pos++;
        }
        UNSAFE.putByte(pos, (byte) 0);
    }

    private void copyBuffer(long sourceOffset, long destinationOffset, int size) {
        long addressSource = sourceBuffer.getAddress() + sourceOffset;
        long addressDestination = this.getAddress() + destinationOffset;
        UNSAFE.copyMemory(addressSource, addressDestination, size);
    }


    // These blocks account for *most* of the 33MB shared memory,
    // so omitting them drastically reduces the copy duration
    static final class DynamicData {
        // TODO: infer these from clientdata instead of hardcoding.

        static final int OFFSET_0 = 0;
        static final int STATICTILES_START = 3447004; // getGroundHeight, isWalkable, isBuildable
        static final int OFFSET_1 = STATICTILES_START;

        static final int STATICTILES_END = 4823260;
        static final int REGION_START = 5085404; // getMapTileRegionId, ..., getRegions
        static final int OFFSET_2 = OFFSET_1 + (REGION_START - STATICTILES_END);

        static final int REGION_END = 10586480;
        static final int STRINGSSHAPES_START = 10962632; // getStringCount, ... getShapes
        static final int OFFSET_3 = OFFSET_2 + (STRINGSSHAPES_START - REGION_END);

        static final int STRINGSHAPES_END = 32242636;
        static final int UNITFINDER_START = 32962644;

        // ~5MB
        static final int SIZE = OFFSET_3 + (UNITFINDER_START - STRINGSHAPES_END);

        static int offset(int offset) {
            // Only 4 region checks.
            for (MemRegion r : MEM_REGIONS) {
                if (offset >= r.start && offset < r.end) {
                    return r.offset + (offset - r.start);
                }
            }
            return -1;
        }

        static final MemRegion[] MEM_REGIONS = new MemRegion[]{
                new MemRegion(0, STATICTILES_START, OFFSET_0),
                new MemRegion(STATICTILES_END, REGION_START,  OFFSET_1),
                new MemRegion(REGION_END, STRINGSSHAPES_START, OFFSET_2),
                new MemRegion(STRINGSHAPES_END, UNITFINDER_START, OFFSET_3)
        };
    }

    private static class MemRegion {
        final int start;
        final int end;
        final int offset;

        MemRegion(int start, int end, int offset) {
            this.start = start;
            this.end = end;
            this.offset = offset;
        }
    }
}
