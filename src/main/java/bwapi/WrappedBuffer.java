package bwapi;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

/**
 * Wrapper around offheap memory that uses sun.misc.Unsafe to for fast access.
 */
class WrappedBuffer {
    private final ByteBuffer buffer;
    private final long address;

    private static Unsafe unsafe;
    static {
        try {
            final Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            unsafe = (Unsafe) theUnsafe.get(null);

        } catch (final Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    WrappedBuffer(final int size) {
        this(new Memory(size), size);
    }

    WrappedBuffer(final Pointer pointer, final int size) {
        this.buffer = pointer.getByteBuffer(0, size);
        this.address = Pointer.nativeValue(pointer);
    }

    byte getByte(final int offset) {
        return unsafe.getByte(address + offset);
    }

    void putByte(final int offset, final byte value) {
        unsafe.putByte(address + offset, value);
    }

    short getShort(final int offset) {
        return unsafe.getShort(address + offset);
    }

    void putShort(final int offset, final short value) {
        unsafe.putShort(address + offset, value);
    }

    int getInt(final int offset) {
        return unsafe.getInt(address + offset);
    }

    void putInt(final int offset, final int value) {
        unsafe.putInt(address + offset, value);
    }

    double getDouble(final int offset) {
        return unsafe.getDouble(address + offset);
    }

    void putDouble(final int offset, final double value) {
        unsafe.putDouble(address + offset, value);
    }

    String getString(final int offset, final int maxLen) {
        final char[] buf = new char[maxLen];
        long pos = offset + address;
        for (int i = 0; i < maxLen; i++) {
            byte b = unsafe.getByte(pos);
            if (b == 0) break;
            buf[i] = (char) (b & 0xff);
            pos++;
        }
        return new String(buf, 0, (int) (pos - offset - address));
    }

    void putString(final int offset, final int maxLen, final String string) {
        long pos = offset + address;
        for (int i = 0; i < Math.min(string.length(), maxLen - 1); i++) {
            unsafe.putByte(pos, (byte) string.charAt(i));
            pos++;
        }
        unsafe.putByte(pos, (byte) 0);
    }

    ByteBuffer getBuffer() {
        return buffer;
    }
}
