package bwapi;

import java.util.stream.IntStream;

class UnitSelf {
    final OrderCache order = new OrderCache();
    final IntegerCache targetPositionX = new IntegerCache();
    final IntegerCache targetPositionY = new IntegerCache();
    final IntegerCache orderTargetPositionX = new IntegerCache();
    final IntegerCache orderTargetPositionY = new IntegerCache();
    final IntegerCache target = new IntegerCache();
    final BooleanCache isConstructing = new BooleanCache();
    final BooleanCache isIdle = new BooleanCache();
    final UnitTypeCache buildType = new UnitTypeCache();
    final OrderCache secondaryOrder = new OrderCache();
    final IntegerCache remainingBuildTime = new IntegerCache();
    final IntegerCache buildUnit = new IntegerCache();
    final UnitTypeCache type = new UnitTypeCache();
    final BooleanCache isMorphing = new BooleanCache();
    final BooleanCache isCompleted = new BooleanCache();
    final IntegerCache remainingResearchTime = new IntegerCache();
    final TechTypeCache tech = new TechTypeCache();
    final BooleanCache isTraining = new BooleanCache();
    final IntegerCache remainingTrainTime = new IntegerCache();
    final UpgradeTypeCache upgrade = new UpgradeTypeCache();
    final IntegerCache remainingUpgradeTime = new IntegerCache();
    final BooleanCache isMoving = new BooleanCache();
    final BooleanCache isGathering = new BooleanCache();
    final IntegerCache rallyPositionX = new IntegerCache();
    final IntegerCache rallyPositionY = new IntegerCache();
    final IntegerCache rallyUnit = new IntegerCache();
    final IntegerCache stimTimer = new IntegerCache();
    final IntegerCache orderTarget = new IntegerCache();

    final UnitTypeCache[] trainingQueue = new UnitTypeCache[5];

    final IntegerCache hitPoints = new IntegerCache();
    final IntegerCache trainingQueueCount = new IntegerCache();
    final IntegerCache energy = new IntegerCache();


    UnitSelf() {
        IntStream.range(0, 5).forEach(i -> trainingQueue[i] = new UnitTypeCache());
    }
}
