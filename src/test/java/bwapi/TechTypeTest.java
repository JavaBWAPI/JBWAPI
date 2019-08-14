package bwapi;

import org.junit.Test;
import java.lang.reflect.*;
import java.util.*;

import static org.junit.Assert.assertEquals;


/**
 * Reads all arrays
 */
public class TechTypeTest {

    @Test
    public void testArraySizes() throws IllegalAccessException {

        int expectedSize = 1 + Arrays.stream(TechType.values()).max(Comparator.comparingInt(a -> a.id)).get().id;

        for (Field f : TechType.class.getDeclaredFields()) {
            if (f.getType().isArray() && !f.getName().startsWith("$") && !f.getName().startsWith("_")) {
                f.setAccessible(true);
                int size = Array.getLength(f.get(0));
                assertEquals(expectedSize, size);
            }
        }
    }

    @Test
    public void ensureSimpleGettersReturnNonNullAndDontFail() throws InvocationTargetException, IllegalAccessException {
        TypeTester.ensureSimpleGettersReturnNonNullAndDontFail(TechType.class);
    }

}
