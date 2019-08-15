package bwapi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class TypeTester {
    private TypeTester() {
        // Utility class
    }

    static void ensureSimpleGettersReturnNonNullAndDontFail(Class<? extends Enum<?>> typeEnumClass) throws InvocationTargetException, IllegalAccessException {
        List<Method> simpleGetters = Arrays.stream(typeEnumClass.getMethods())
                .filter(it -> it.getParameterCount() == 0 && it.getReturnType() != Void.TYPE)
                .collect(Collectors.toList());
        for (Enum<?> type : typeEnumClass.getEnumConstants()) {
            for (Method getter : simpleGetters) {
                // WHEN
                Object result = getter.invoke(type);

                // THEN
                assertThat(result).describedAs("When calling " + getter.getName()).isNotNull();
            }
        }
    }
}
