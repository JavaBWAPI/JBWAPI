package bwapi;

import org.junit.Test;
import sun.misc.Unsafe;

import java.nio.ByteBuffer;

import static org.junit.Assert.*;

public class UnsafeToolsTest {

    @Test
    public void testGettingUnsafe() {
        assertNotNull(UnsafeTools.getUnsafe());
    }

    @Test
    public void testDirectByteBuffer() {
        byte[] bytes = {112, 117, 114, 112, 108, 101, 119, 97, 118, 101};
        final ByteBuffer buffer = ByteBuffer.allocateDirect(bytes.length);
        buffer.put(bytes);

        long address = UnsafeTools.getAddress(buffer);
        assertNotEquals(0, address);

        Unsafe unsafe = UnsafeTools.getUnsafe(); //to obtain memory values via the address
        for (int i=0; i < bytes.length; i++) {
            assertEquals(bytes[i], unsafe.getByte(address + i));
        }
    }
}
