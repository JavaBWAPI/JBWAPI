package bwapi;



import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UnitTypeContainerTest {
    @Test
    public void checkContainerSizes() {
        final int expected = UnitType.values().length;
        Assert.assertEquals(UnitTypeContainer.defaultArmorAmount.length, expected);
        Assert.assertEquals(UnitTypeContainer.defaultOreCost.length, expected);
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
