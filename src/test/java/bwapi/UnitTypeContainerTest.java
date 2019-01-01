package bwapi;

import org.junit.Test;

import static org.junit.Assert.assertEquals;


public class UnitTypeContainerTest {

    @Test
    public void checkContainerSizes() {
        final int expected = UnitType.values().length;
        assertEquals(UnitTypeContainer.defaultArmorAmount.length, expected);
        assertEquals(UnitTypeContainer.defaultOreCost.length, expected);
        assertEquals(UnitTypeContainer.defaultGasCost.length, expected);
        assertEquals(UnitTypeContainer.defaultTimeCost.length, expected);
        assertEquals(UnitTypeContainer.unitSupplyProvided.length, expected);
        assertEquals(UnitTypeContainer.unitSupplyRequired.length, expected);
        assertEquals(UnitTypeContainer.unitSpaceRequired.length, expected);
        assertEquals(UnitTypeContainer.unitSpaceProvided.length, expected);
        assertEquals(UnitTypeContainer.unitBuildScore.length, expected);
        assertEquals(UnitTypeContainer.unitDestroyScore.length, expected);
    }
}
