package bwapi;

import com.sun.jna.Library;
import com.sun.jna.Native;

interface MSVCRT extends Library {
    MSVCRT INSTANCE = Native.load("msvcrt.dll", MSVCRT.class);

    long memcpy(long dest, long src, int count);
}