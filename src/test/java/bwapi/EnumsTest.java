package bwapi;

import org.junit.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class EnumsTest {
    static String CLASSES_LOCATION = "src/main/java/bwapi";
    static String ARRAY_VALUE = "idToEnum";
    static String ID_VALUE = "id";


    static List<Class<?>> getBWAPIEnums() {
        return Arrays.stream(Objects.requireNonNull(new File(CLASSES_LOCATION).listFiles()))
                .filter(File::isFile)
                .map(f -> {
                    try {
                        return Class.forName("bwapi." + f.getName().replace(".java", ""));
                    } catch (final ClassNotFoundException exc) {
                        throw new RuntimeException(exc);
                    }
                })
                .filter(Class::isEnum)
                .flatMap(e -> Stream.concat(Stream.of(e), Arrays.stream(e.getDeclaredClasses()).filter(Class::isEnum)))
                .collect(Collectors.toList());
    }
    /*
     * Check, via reflection, that all Enums that have an "idToEnum" array to be of correct lenght
     */
    @Test
    public void checkAllidToEnumsArrayLenghts() throws Exception {
        for (Class<?> cls : getBWAPIEnums()) {
            if (Arrays.stream(cls.getDeclaredFields()).anyMatch(f -> f.getName().equals(ARRAY_VALUE))) {
                Field field = cls.getDeclaredField(ARRAY_VALUE);
                assertFalse(field.isAccessible());
                field.setAccessible(true);

                int arrayLength = ((Object[])field.get(null)).length;
                int maxEnumVal = Integer.MIN_VALUE;
                for (Object obj : (Object[]) cls.getDeclaredMethod("values").invoke(null)) {
                    Field f = obj.getClass().getDeclaredField(ID_VALUE);
                    assertFalse(f.isAccessible());
                    f.setAccessible(true);
                    int val = f.getInt(obj);
                    if (val > maxEnumVal) {
                        maxEnumVal = val;
                    }
                }
                assertEquals(arrayLength, maxEnumVal + 1);
            }
        }
    }

    @Test
    public void ensureSimpleGettersReturnNonNullAndDontFail() throws InvocationTargetException, IllegalAccessException {
        for (Class<?> cls : getBWAPIEnums()) {
            List<Method> simpleGetters = Arrays.stream(cls.getMethods())
                    .filter(it -> it.getParameterCount() == 0 && it.getReturnType() != Void.TYPE)
                    .collect(Collectors.toList());
            for (Object type : cls.getEnumConstants()) {
                for (Method getter : simpleGetters) {
                    // WHEN
                    Object result = getter.invoke(type);
                    // THEN
                    assertThat(result).describedAs("When calling " + getter.getName()).isNotNull();
                }
            }
        }
    }

    @Test
    public void ensureEnumsExposePublicId() throws NoSuchFieldException {
        for (Class<?> cls : getBWAPIEnums()) {
            Field idField = cls.getField(ID_VALUE);
            assertThat(Modifier.isPublic(idField.getModifiers()))
                    .describedAs("ID public for class "+ cls)
                    .isTrue();
        }
    }
}
