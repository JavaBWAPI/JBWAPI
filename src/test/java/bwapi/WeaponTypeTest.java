package bwapi;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

public class WeaponTypeTest {
    @Test
    public void ensureSimpleGettersReturnNonNullAndDontFail() throws InvocationTargetException, IllegalAccessException {
        TypeTester.ensureSimpleGettersReturnNonNullAndDontFail(WeaponType.class);
    }
}