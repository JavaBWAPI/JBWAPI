package bwapi;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;

public class PlayerTypeTest {
    @Test
    public void ensureSimpleGettersReturnNonNullAndDontFail() throws InvocationTargetException, IllegalAccessException {
        TypeTester.ensureSimpleGettersReturnNonNullAndDontFail(WeaponType.class);
    }
}