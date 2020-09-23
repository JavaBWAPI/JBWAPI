package bwapi;

import com.sun.jna.Library;
import com.sun.jna.Native;

/**
 * JNI interface for accessing native MSVC code.
 */
interface MSVCRT extends Library {
    MSVCRT INSTANCE = Native.load("msvcrt.dll", MSVCRT.class);

    /**
     * 32-bit implementation of memcpy.
     * @param dest A 32-bit address of a memory block (likely from a ByteBuffer) to copy to
     * @param src  A 32-bit address of a memory block (likely from a ByteBuffer) to copy from
     * @param count The number of bytes to copy
     */
    long memcpy(int dest, int src, int count);

    /**
     * 64-bit implementation of memcpy.
     * @param dest A 64-bit address of a memory block (likely from a ByteBuffer) to copy to
     * @param src  A 64-bit address of a memory block (likely from a ByteBuffer) to copy from
     * @param count The number of bytes to copy
     */
    long memcpy(long dest, long src, int count);
}