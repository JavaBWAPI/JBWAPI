package bwapi;

import com.sun.jna.Pointer;
import sun.misc.Unsafe;

import java.nio.ByteBuffer;

/**
 * Wrapper around offheap memory that uses sun.misc.Unsafe for fast access.
 */
class WrappedBuffer {
    protected static final Unsafe UNSAFE = UnsafeTools.getUnsafe();

    protected final ByteBuffer buffer;
    protected final long address;

    WrappedBuffer(final int size) {
        buffer = ByteBuffer.allocateDirect(size);
        address = UnsafeTools.getAddress(buffer);
    }

    WrappedBuffer(final Pointer pointer, final int size) {
        this.buffer = pointer.getByteBuffer(0, size);
        this.address = Pointer.nativeValue(pointer);
    }

    byte getByte(final int offset) {
        return UNSAFE.getByte(address + offset);
    }

    void putByte(final int offset, final byte value) {
        UNSAFE.putByte(address + offset, value);
    }

    short getShort(final int offset) {
        return UNSAFE.getShort(address + offset);
    }

    void putShort(final int offset, final short value) {
        UNSAFE.putShort(address + offset, value);
    }

    int getInt(final int offset) {
        return UNSAFE.getInt(address + offset);
    }

    void putInt(final int offset, final int value) {
        UNSAFE.putInt(address + offset, value);
    }

    double getDouble(final int offset) {
        return UNSAFE.getDouble(address + offset);
    }

    void putDouble(final int offset, final double value) {
        UNSAFE.putDouble(address + offset, value);
    }

    String getString(final int offset, final int maxLen) {
        final char[] buf = new char[maxLen];
        long pos = offset + address;
        for (int i = 0; i < maxLen; i++) {
            byte b = UNSAFE.getByte(pos);
            if (b == 0) break;
            buf[i] = (char) (b & 0xff);
            pos++;
        }
        return new String(buf, 0, (int) (pos - offset - address));
    }

    void putString(final int offset, final int maxLen, final String string) {
        long pos = offset + address;
        for (int i = 0; i < Math.min(string.length(), maxLen - 1); i++) {
            UNSAFE.putByte(pos, (byte) string.charAt(i));
            pos++;
        }
        UNSAFE.putByte(pos, (byte) 0);
    }

    ByteBuffer getBuffer() {
        return buffer;
    }

    long getAddress() {
        return address;
    }
}
