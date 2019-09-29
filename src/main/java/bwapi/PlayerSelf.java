package bwapi;

import java.util.stream.IntStream;

class PlayerSelf {
    IntegerCache minerals = new IntegerCache();
    IntegerCache gas = new IntegerCache();
    IntegerCache[] supplyUsed = new IntegerCache[3];

    BooleanCache[] isResearching = new BooleanCache[TechType.idToEnum.length];
    BooleanCache[] isUpgrading = new BooleanCache[UpgradeType.idToEnum.length];

    PlayerSelf() {
        IntStream.range(0, supplyUsed.length).forEach(i -> supplyUsed[i] = new IntegerCache());
        IntStream.range(0, isResearching.length).forEach(i -> isResearching[i] = new BooleanCache());
        IntStream.range(0, isUpgrading.length).forEach(i -> isUpgrading[i] = new BooleanCache());
    }
}
