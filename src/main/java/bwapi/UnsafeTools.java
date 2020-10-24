package bwapi;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.nio.Buffer;

class UnsafeTools {

    private static Object getOrCrash(final Class<?> className, final Object object, final String fieldName) {
        try { // get
            final Field field = className.getDeclaredField(fieldName);
            final boolean accessible = field.isAccessible();
            if (!accessible) {
                field.setAccessible(true);
            }
            final Object result = field.get(object);
            if (!accessible) {
                field.setAccessible(false);
            }
            return result;

        } catch (final Exception e) { // or crash...
            e.printStackTrace();
            System.exit(-1);
            return null;
        }
    }

    static Unsafe getUnsafe() {
        return (Unsafe) getOrCrash(Unsafe.class, null, "theUnsafe");
    }

    /**
     * Alternative to `((DirectBuffer) buffer).address())`
     * (ab)using reflection
     */
    static long getAddress(final Buffer buffer) {
        return (long) getOrCrash(Buffer.class, buffer, "address");
    }
}
