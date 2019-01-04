package bwapi;

import sun.misc.Unsafe;
import sun.nio.ch.DirectBuffer;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Wrapper around ByteBuffer that makes use of sun.misc.Unsafe if available.
 * If not available it will fall back on using the ByteBuffer itself.
 */
class WrappedBuffer {
    private static final CharsetEncoder enc = Charset.forName("ISO-8859-1").newEncoder();

    private final ByteBuffer buffer;
    private final long address;
    private final Unsafe unsafe;
    private final boolean useUnsafe;

    WrappedBuffer(final ByteBuffer byteBuffer) {
        final Optional<Unsafe> optionalUnsafe = getTheUnsafe();

        buffer = byteBuffer;

        useUnsafe = optionalUnsafe.isPresent();
        address = useUnsafe ? ((DirectBuffer) buffer).address() : 0;
        unsafe = useUnsafe ? optionalUnsafe.get() : null;
    }

    private static Optional<Unsafe> getTheUnsafe() {
        try {
            final Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            return Optional.of((Unsafe) theUnsafe.get(null));
        }
        catch (final Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public byte getByte(final int offset) {
        if (useUnsafe) {
            return unsafe.getByte(address + offset);
        }
        else {
            return buffer.get(offset);
        }
    }

    public void putByte(final int offset, final byte value) {
        if (useUnsafe) {
            unsafe.putByte(address + offset, value);
        }
        else {
            buffer.put(offset, value);
        }
    }

    public short getShort(final int offset) {
        if (useUnsafe) {
            return unsafe.getShort(address + offset);
        }
        else {
            return buffer.getShort(offset);
        }
    }

    public void putShort(final int offset, final short value) {
        if (useUnsafe) {
            unsafe.putShort(address + offset, value);
        }
        else {
            buffer.putShort(offset, value);
        }
    }

    public int getInt(final int offset) {
        if (useUnsafe) {
            return unsafe.getInt(address + offset);
        }
        else {
            return buffer.getInt(offset);
        }
    }

    public void putInt(final int offset, final int value) {
        if (useUnsafe) {
            unsafe.putInt(address + offset, value);
        }
        else {
            buffer.putInt(offset, value);
        }
    }

    public double getDouble(final int offset) {
        if (useUnsafe) {
            return unsafe.getDouble(address + offset);
        }
        else {
            return buffer.getDouble(offset);
        }
    }

    public void putDouble(final int offset, final double value) {
        if (useUnsafe) {
            unsafe.putDouble(address + offset, value);
        }
        else {
            buffer.putDouble(offset, value);
        }
    }

    public String getString(final int offset, final int maxLen) {
        final byte[] buf = new byte[maxLen];
        buffer.position(offset);
        buffer.get(buf, 0, maxLen);
        buffer.position(0);
        int len = 0;
        while (len < maxLen && buf[len] != 0) {
            ++len;
        }
        return new String(buf, 0, len, StandardCharsets.ISO_8859_1);
    }

    public void putString(final int offset, final int maxLen, final String string) {
        final int len = string.length() + 1;
        if (len >= maxLen) {
            throw new StringIndexOutOfBoundsException();
        }
        buffer.position(offset);
        enc.encode(CharBuffer.wrap(string), buffer, true);
        buffer.put((byte) 0);
        buffer.rewind();
    }
}
