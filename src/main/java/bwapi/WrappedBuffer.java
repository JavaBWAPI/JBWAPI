package bwapi;

import sun.misc.Unsafe;
import sun.nio.ch.DirectBuffer;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

/**
 * Wrapper around ByteBuffer that makes use of sun.misc.Unsafe if available.
 */
class WrappedBuffer {
    private final ByteBuffer buffer;
    private final long address;
    private final Unsafe unsafe;

    WrappedBuffer(final ByteBuffer byteBuffer) {
        unsafe = getTheUnsafe();
        buffer = byteBuffer;
        address = ((DirectBuffer) buffer).address();
    }

    private static Unsafe getTheUnsafe() {
        try {
            final Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            return (Unsafe) theUnsafe.get(null);
        } catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
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
        char[] buf = new char[maxLen];
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
        if (string.length() + 1 >= maxLen) {
            throw new StringIndexOutOfBoundsException();
        }
        long pos = offset + address;
        for (int i = 0; i < string.length(); i++) {
            unsafe.putByte(pos, (byte) string.charAt(i));
            pos++;
        }
        unsafe.putByte(pos, (byte) 0);
    }
}
