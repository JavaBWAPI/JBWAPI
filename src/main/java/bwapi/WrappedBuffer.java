package bwapi;

import sun.misc.Unsafe;
import sun.nio.ch.DirectBuffer;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Wrapper around ByteBuffer that makes use of sun.misc.Unsafe if available.
 */
class WrappedBuffer {
    private static final Charset charSet = StandardCharsets.ISO_8859_1;
    private static final CharsetEncoder enc = charSet.newEncoder();

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
        }
        catch (final Exception e) {
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
        final byte[] buf = new byte[maxLen];

        unsafe.copyMemory(null, address + offset, buf, Unsafe.ARRAY_BYTE_BASE_OFFSET, maxLen);

        int len = 0;
        while (len < maxLen && buf[len] != 0) {
            ++len;
        }
        return new String(buf, 0, len, charSet);
    }

    void putString(final int offset, final int maxLen, final String string) {
        if (string.length() + 1 >= maxLen) {
            throw new StringIndexOutOfBoundsException();
        }
        buffer.position(offset);
        enc.encode(CharBuffer.wrap(string), buffer, true);
        buffer.put((byte) 0);
    }
}
