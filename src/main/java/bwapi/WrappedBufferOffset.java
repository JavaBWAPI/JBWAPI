package bwapi;

/**
 * Like WrappedBuffer but with offsets.
 *
 * <p> </p>For now this hardcodes a few things (size, offsets).
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

    byte getByte(final int offset) {
        int newOffset = DynamicData.offset(offset);
        if (newOffset == -1) {
            return sourceBuffer.getByte(offset);
        } else {
            return UNSAFE.getByte(address + newOffset);
        }
    }

    void putByte(final int offset, final byte value) {
        int newOffset = DynamicData.offset(offset);
        if (newOffset == -1) {
            sourceBuffer.putByte(offset, value);
        } else {
            UNSAFE.putByte(address + newOffset, value);
        }
    }

    short getShort(final int offset) {
        int newOffset = DynamicData.offset(offset);
        if (newOffset == -1) {
            return sourceBuffer.getShort(offset);
        } else {
            return UNSAFE.getShort(address + newOffset);
        }
    }

    void putShort(final int offset, final short value) {
        int newOffset = DynamicData.offset(offset);
        if (newOffset == -1) {
            sourceBuffer.putShort(offset, value);
        } else {
            UNSAFE.putShort(address + newOffset, value);
        }
    }

    int getInt(final int offset) {
        int newOffset = DynamicData.offset(offset);
        if (newOffset == -1) {
            return sourceBuffer.getInt(offset);
        } else {
            return UNSAFE.getInt(address + newOffset);
        }
    }

    void putInt(final int offset, final int value) {
        int newOffset = DynamicData.offset(offset);
        if (newOffset == -1) {
            sourceBuffer.putInt(offset, value);
        } else {
            UNSAFE.putInt(address + newOffset, value);
        }
    }

    double getDouble(final int offset) {
        int newOffset = DynamicData.offset(offset);
        if (newOffset == -1) {
            return sourceBuffer.getDouble(offset);
        } else {
            return UNSAFE.getDouble(address + newOffset);
        }
    }

    void putDouble(final int offset, final double value) {
        int newOffset = DynamicData.offset(offset);
        if (newOffset == -1) {
            sourceBuffer.putDouble(offset, value);
        } else {
            UNSAFE.putDouble(address + newOffset, value);
        }
    }

    String getString(final int offset, final int maxLen) {
        int newOffset = DynamicData.offset(offset);
        if (newOffset == -1) {
            return sourceBuffer.getString(offset, maxLen);
        }
        else {
            final char[] buf = new char[maxLen];
            long pos = newOffset + address;
            for (int i = 0; i < maxLen; i++) {
                byte b = UNSAFE.getByte(pos);
                if (b == 0) break;
                buf[i] = (char) (b & 0xff);
                pos++;
            }
            return new String(buf, 0, (int) (pos - newOffset - address));
        }
    }

    void putString(final int offset, final int maxLen, final String string) {
        int newOffset = DynamicData.offset(offset);
        if (newOffset == -1) {
            sourceBuffer.putString(offset, maxLen, string);
        }
        else {
            long pos = newOffset + address;
            for (int i = 0; i < Math.min(string.length(), maxLen - 1); i++) {
                UNSAFE.putByte(pos, (byte) string.charAt(i));
                pos++;
            }
            UNSAFE.putByte(pos, (byte) 0);
        }
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
        static final int SIZE = OFFSET_3 + (UNITFINDER_START - STRINGSHAPES_END);

        static int offset(int offset) {
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
