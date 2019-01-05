package bwapi;

import sun.misc.Unsafe;
import sun.nio.ch.DirectBuffer;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;

class MemoryAccesses {
    private static final Charset charSet = StandardCharsets.ISO_8859_1;
    private static final CharsetEncoder enc = charSet.newEncoder();

    private static String asString(final byte[] buf, final int maxLen) {
        int len = 0;
        while (len < maxLen && buf[len] != 0) {
            ++len;
        }
        return new String(buf, 0, len, charSet);
    }
    private static void setString(final ByteBuffer buffer, final int offset, final int maxLen, final String string) {
        if (string.length() + 1 >= maxLen) {
            throw new StringIndexOutOfBoundsException();
        }
        buffer.position(offset);
        enc.encode(CharBuffer.wrap(string), buffer, true);
        buffer.put((byte) 0);
    }

    interface MemoryAccess {

        byte getByte(int offset);

        void putByte(int offset, byte value);

        short getShort(int offset);

        void putShort(int offset, short value);

        int getInt(int offset);

        void putInt(int offset, final int value);

        double getDouble(int offset);

        void putDouble(int offset, final double value);

        String getString(int offset, final int maxLen);

        void putString(int offset, final int maxLen, final String string);
    }

    /**
     * Wrapper around ByteBuffer that makes use of sun.misc.Unsafe
     */
    static class UnsafeAccess implements MemoryAccess {

        private final ByteBuffer buffer;
        private final long address;
        private final Unsafe unsafe;

        UnsafeAccess(final ByteBuffer byteBuffer) throws NoSuchFieldException, IllegalAccessException {
            final Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);

            unsafe = (Unsafe) theUnsafe.get(null);
            buffer = byteBuffer;
            address = ((DirectBuffer) buffer).address();
        }

        public byte getByte(int offset) {
            return unsafe.getByte(address + offset);
        }

        public void putByte(int offset, byte value) {
            unsafe.putByte(address + offset, value);
        }

        public short getShort(int offset) {
            return unsafe.getShort(address + offset);
        }

        public void putShort(int offset, short value) {
            unsafe.putShort(address + offset, value);
        }

        public int getInt(int offset) {
            return unsafe.getInt(address + offset);
        }

        public void putInt(int offset, int value) {
            unsafe.putInt(address + offset, value);
        }

        public double getDouble(int offset) {
            return unsafe.getDouble(address + offset);
        }

        public void putDouble(int offset, double value) {
            unsafe.putDouble(address + offset, value);
        }

        public String getString(int offset, int maxLen) {
            final byte[] buf = new byte[maxLen];
            unsafe.copyMemory(null, address + offset, buf, Unsafe.ARRAY_BYTE_BASE_OFFSET, maxLen);
            return asString(buf, maxLen);
        }

        public void putString(int offset, int maxLen, String string) {
            setString(buffer, offset, maxLen, string);
        }
    }

    static class DirectBufferAccess implements MemoryAccess {
        private final ByteBuffer buffer;

        DirectBufferAccess(final ByteBuffer buffer) {
            this.buffer = buffer;
        }
        public byte getByte(int offset) {
            return buffer.get(offset);
        }

        public void putByte(int offset, byte value) {
            buffer.put(offset, value);
        }

        public short getShort(int offset) {
            return buffer.getShort(offset);
        }

        public void putShort(int offset, short value) {
            buffer.putShort(offset, value);
        }

        public int getInt(int offset) {
            return buffer.getInt(offset);
        }

        @Override
        public void putInt(int offset, int value) {
            buffer.putInt(offset, value);
        }

        @Override
        public double getDouble(int offset) {
            return buffer.getDouble(offset);
        }

        @Override
        public void putDouble(int offset, double value) {
            buffer.putDouble(offset, value);
        }

        @Override
        public String getString(int offset, int maxLen) {
            final byte[] buf = new byte[maxLen];
            buffer.position(offset);
            buffer.get(buf, 0, maxLen);
            return null;
        }

        @Override
        public void putString(int offset, int maxLen, String string) {
            setString(buffer, offset, maxLen, string);
        }
    }
}
