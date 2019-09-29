package bwapi;

import java.util.stream.IntStream;

class UnitSelf {
    OrderCache order = new OrderCache();
    IntegerCache targetPositionX = new IntegerCache();
    IntegerCache targetPositionY = new IntegerCache();
    IntegerCache orderTargetPositionX = new IntegerCache();
    IntegerCache orderTargetPositionY = new IntegerCache();
    IntegerCache target = new IntegerCache();
    BooleanCache isConstructing = new BooleanCache();
    BooleanCache isIdle = new BooleanCache();
    UnitTypeCache buildType = new UnitTypeCache();
    OrderCache secondaryOrder = new OrderCache();
    IntegerCache remainingBuildTime = new IntegerCache();
    IntegerCache buildUnit = new IntegerCache();
    UnitTypeCache type = new UnitTypeCache();
    BooleanCache isMorphing = new BooleanCache();
    BooleanCache isCompleted = new BooleanCache();
    IntegerCache remainingResearchTime = new IntegerCache();
    TechTypeCache tech = new TechTypeCache();
    BooleanCache isTraining = new BooleanCache();
    IntegerCache remainingTrainTime = new IntegerCache();
    UpgradeTypeCache upgrade = new UpgradeTypeCache();
    IntegerCache remainingUpgradeTime = new IntegerCache();
    IntegerCache energy = new IntegerCache();
    BooleanCache isMoving = new BooleanCache();
    BooleanCache isGathering = new BooleanCache();
    IntegerCache rallyPositionX = new IntegerCache();
    IntegerCache rallyPositionY = new IntegerCache();
    IntegerCache rallyUnit = new IntegerCache();
    IntegerCache stimTimer = new IntegerCache();
    IntegerCache orderTarget = new IntegerCache();

    UnitTypeCache[] trainingQueue = new UnitTypeCache[5];

    IntegerCache hitPoints = new IntegerCache();
    IntegerCache trainingQueueCount = new IntegerCache();


    UnitSelf() {
        IntStream.range(0, 5).forEach(i -> trainingQueue[i] = new UnitTypeCache());
    }
}
