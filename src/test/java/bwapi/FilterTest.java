package bwapi;

import org.junit.*;
import java.util.*;

import static bwapi.UnitFilter.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FilterTest {

    List<Unit> units;

    @Before
    public void setup() {
        Unit u = mock(Unit.class);
        when(u.getHitPoints()).thenReturn(20);
        when(u.getType()).thenReturn(UnitType.Resource_Mineral_Field);

        units = Arrays.asList(u);
    }

    @Test
    public void testBasicFilters() {
        assertTrue(units.stream().anyMatch(IsMineralField));
        assertTrue(units.stream().noneMatch(IsRefinery));
    }

    @Test
    public void testVariableFilters() {
        assertTrue(units.stream().anyMatch(GetType(t -> t == UnitType.Resource_Mineral_Field)));
        assertTrue(units.stream().anyMatch(HP(x -> x > 10)));
        assertTrue(units.stream().noneMatch(HP(x -> x == 10)));
    }

    @Test
    public void testFilterCombinations() {
        assertTrue(units.stream().anyMatch(IsMineralField.or(IsRefinery)));
        assertTrue(units.stream().noneMatch(IsMineralField.and(IsRefinery)));
        assertTrue(units.stream().noneMatch(IsMineralField.negate()));
    }

    @Test
    public void isTransportShouldMatchDropshipAndExcludeBunker() {
        Unit dropship = mock(Unit.class);
        when(dropship.getType()).thenReturn(UnitType.Terran_Dropship);

        Unit bunker = mock(Unit.class);
        when(bunker.getType()).thenReturn(UnitType.Terran_Bunker);

        Unit marine = mock(Unit.class);
        when(marine.getType()).thenReturn(UnitType.Terran_Marine);

        assertTrue(IsTransport.test(dropship));
        assertFalse(IsTransport.test(bunker));
        assertFalse(IsTransport.test(marine));
    }
}
