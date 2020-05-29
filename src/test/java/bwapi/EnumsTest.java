package bwapi;

import org.junit.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class EnumsTest {
    static String CLASSES_LOCATION = "src/main/java/bwapi";
    static String ARRAY_VALUE = "idToEnum";
    static String ID_VALUE = "id";

    /*
     * Check, via reflection, that all Enums that have an "idToEnum" array to be of correct lenght
     */
    @Test
    public void checkAllEnums() throws Exception {
        for (File file : Objects.requireNonNull(new File(CLASSES_LOCATION).listFiles())) {
            if (file.isFile()) {
                Class<?> cls = Class.forName("bwapi." + file.getName().replace(".java", ""));
                if (cls.isEnum() && Arrays.stream(cls.getDeclaredFields()).anyMatch(f -> f.getName().equals(ARRAY_VALUE))) {
                    Field field = cls.getDeclaredField(ARRAY_VALUE);
                    assertFalse(field.isAccessible());
                    field.setAccessible(true);

                    int arrayLength = ((Object[])field.get(null)).length;
                    int maxEnumVal = Arrays.stream((Object[]) cls.getDeclaredMethod("values").invoke(null))
                            .mapToInt(obj -> {
                                try {
                                    Field f = obj.getClass().getDeclaredField(ID_VALUE);
                                    assertFalse(f.isAccessible());
                                    f.setAccessible(true);
                                    return f.getInt(obj);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    return -1;
                                }
                            })
                            .max()
                            .orElse(-1);
                    assertEquals(arrayLength, maxEnumVal + 1);
                }
            }
        }
    }
}
