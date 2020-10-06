package bwapi;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

class UnsafeTools {
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

    static Unsafe getUnsafe() {
        return unsafe;
    }
}
