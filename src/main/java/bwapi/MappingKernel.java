package bwapi;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.win32.W32APIOptions;

interface MappingKernel extends Kernel32 {
    MappingKernel INSTANCE = (MappingKernel) Native.loadLibrary(MappingKernel.class, W32APIOptions.DEFAULT_OPTIONS);
    HANDLE OpenFileMapping(int desiredAccess, boolean inherit, String name);
}
