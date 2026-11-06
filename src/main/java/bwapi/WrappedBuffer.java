package bwapi;

import com.sun.jna.Pointer;
import sun.misc.Unsafe;

import java.nio.ByteBuffer;

/**
 * Wrapper around offheap memory that uses sun.misc.Unsafe for fast access.
 */
class WrappedBuffer {
    private final ByteBuffer buffer;
    private final long address;
    private final boolean packed;

    private static final Unsafe unsafe = UnsafeTools.getUnsafe();

    WrappedBuffer(final int size) {
        this(size, false);
    }

    WrappedBuffer(final int size, final boolean packed) {
        buffer = ByteBuffer.allocateDirect(size);
        address = UnsafeTools.getAddress(buffer);
        this.packed = packed;
    }

    WrappedBuffer(final Pointer pointer, final int size) {
        this.buffer = pointer.getByteBuffer(0, size);
        this.address = Pointer.nativeValue(pointer);
        this.packed = false;
    }

    boolean isPacked() {
        return packed;
    }

    private long addressAt(int offset) {
        // Translate on the selected buffer: unsafe async can switch layouts during a read.
        if (packed && offset >= AsyncClientData.STATICTILES_END) {
            if (offset >= AsyncClientData.STATICTILES_END && offset < AsyncClientData.REGION_START) {
                offset -= AsyncClientData.DELTA_RANGE2;
            } else if (offset >= AsyncClientData.REGION_END && offset < AsyncClientData.STRINGSSHAPES_START) {
                offset -= AsyncClientData.DELTA_RANGE3;
            } else if (offset >= AsyncClientData.STRINGSHAPES_END && offset < AsyncClientData.UNITFINDER_START) {
                offset -= AsyncClientData.DELTA_RANGE4;
            }
        }
        return address + offset;
    }

    byte getByte(final int offset) {
        return unsafe.getByte(addressAt(offset));
    }

    void putByte(final int offset, final byte value) {
        unsafe.putByte(addressAt(offset), value);
    }

    short getShort(final int offset) {
        return unsafe.getShort(addressAt(offset));
    }

    void putShort(final int offset, final short value) {
        unsafe.putShort(addressAt(offset), value);
    }

    int getInt(final int offset) {
        return unsafe.getInt(addressAt(offset));
    }

    void putInt(final int offset, final int value) {
        unsafe.putInt(addressAt(offset), value);
    }

    double getDouble(final int offset) {
        return unsafe.getDouble(addressAt(offset));
    }

    void putDouble(final int offset, final double value) {
        unsafe.putDouble(addressAt(offset), value);
    }

    String getString(final int offset, final int maxLen) {
        final char[] buf = new char[maxLen];
        final long start = addressAt(offset);
        long pos = start;
        for (int i = 0; i < maxLen; i++) {
            byte b = unsafe.getByte(pos);
            if (b == 0) break;
            buf[i] = (char) (b & 0xff);
            pos++;
        }
        return new String(buf, 0, (int) (pos - start));
    }

    void putString(final int offset, final int maxLen, final String string) {
        long pos = addressAt(offset);
        for (int i = 0; i < Math.min(string.length(), maxLen - 1); i++) {
            unsafe.putByte(pos, (byte) string.charAt(i));
            pos++;
        }
        unsafe.putByte(pos, (byte) 0);
    }


    ByteBuffer getBuffer() {
        return buffer;
    }

    long getAddress() {
        return address;
    }
}
