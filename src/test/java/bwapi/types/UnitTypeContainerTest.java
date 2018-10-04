package bwapi.types;



import org.junit.Assert;
import org.junit.Test;

import static bwapi.types.UnitTypeContainer.*;
import static org.junit.Assert.assertEquals;

public class UnitTypeContainerTest {
    @Test
    public void checkContainerSizes() {
        final int expected = UnitType.values().length;
        assertEquals(defaultArmorAmount.length, expected);
        Assert.assertEquals(defaultOreCost.length, expected);
        Assert.assertEquals(UnitTypeContainer.defaultGasCost.length, expected);
        Assert.assertEquals(UnitTypeContainer.defaultTimeCost.length, expected);
        Assert.assertEquals(UnitTypeContainer.unitSupplyProvided.length, expected);
        Assert.assertEquals(UnitTypeContainer.unitSupplyRequired.length, expected);
        Assert.assertEquals(UnitTypeContainer.unitSpaceRequired.length, expected);
        Assert.assertEquals(UnitTypeContainer.unitSpaceProvided.length, expected);
        Assert.assertEquals(UnitTypeContainer.unitBuildScore.length, expected);
        Assert.assertEquals(UnitTypeContainer.unitDestroyScore.length, expected);
    }
}
