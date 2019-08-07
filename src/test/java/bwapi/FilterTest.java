package bwapi;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FilterTest {

    @Test
    public void someFilterTests() {
        Unit u = mock(Unit.class);
        when(u.getHitPoints()).thenReturn(20);
        when(u.getType()).thenReturn(UnitType.Resource_Mineral_Field);

        List<Unit> units = Arrays.asList(u);


        UnitFilter validUT = Filter.GetType(t -> t.equals(UnitType.Resource_Mineral_Field));

        assertTrue(units.stream().anyMatch(Filter.IsMineralField::filter));
        assertTrue(units.stream().noneMatch(Filter.IsRefinery::filter));
        assertTrue(units.stream().anyMatch(validUT::filter));

        UnitFilter validHP = Filter.HP(x -> x > 10);
        UnitFilter invalidHP = Filter.HP(x -> x == 10);

        assertTrue(units.stream().anyMatch(validHP::filter));
        assertTrue(units.stream().noneMatch(invalidHP::filter));
    }
}
