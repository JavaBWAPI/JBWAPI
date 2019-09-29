package bwapi;

/**
 * Latency Compensation:
 * Only need to implement LatCom for current frame, the server updates the next frame already if latcom is enabled.
 * Use Caches for all internal state that might be affected by latcom, and add the (current) frame, to let Player & Unit
 * check if they need to use the cached/latcom version of the value or the from server (or a combination of both)
 *
 * Inspiration:
 * https://github.com/bwapi/bwapi/blob/e4a29d73e6021037901da57ceb06e37248760240/bwapi/include/BWAPI/Client/CommandTemp.h
 */
class CommandTemp {
    enum EventType {
        Order,
        Resource,
        Finish
    }

    UnitCommand command;
    EventType eventType = EventType.Resource;
    Player player = null;
    Game game;

    CommandTemp(final UnitCommand command, Game game) {
        this.command = command;
        this.game = game;
    }

    int getUnitID(Unit unit) {
        if (unit == null) {
            return -1;
        }
        return unit.getID();
    }

    void execute() {
        switch(command.type) {
            case Halt_Construction:
                eventType = EventType.Order;
            default:
                execute(game.getRemainingLatencyFrames() == 0);
                break;
        }
    }

    void execute(boolean isCurrentFrame) {
        // Immediately return if latency compensation is disabled or if the command was queued
        if (!game.isLatComEnabled() || command.isQueued()) return;
        Unit unit = command.unit;
        Unit target = command.target;
        int frame = game.getFrameCount();

        if (isCurrentFrame) {
            switch (command.type) { // Commands which do things during the current frame
                case Morph:       // Morph, Build_Addon and Train orders may reserve resources or supply that
                case Build_Addon: // SC does not take until the next frame to protect bots from overspending.
                case Train:
                    if(eventType == EventType.Resource)
                        break;
                    return;
                default:
                    return;
            }
        }

        // Get the player (usually the unit's owner)
        if (player == null) {
            player = unit != null ? unit.getPlayer() : game.self();
        }

        // Existence test
        if (unit == null || !unit.exists()) {
            return;
        }

        // Move test
        switch (command.type) {
            case Follow:
            case Hold_Position:
            case Move:
            case Patrol:
            case Right_Click_Position:
            case Attack_Move:
                if (!unit.getType().canMove())
                    return;
                break;
            default:
                break;
        }

        switch(command.type) {
            // RLF
            case Attack_Move:
                unit.self().order.set(Order.AttackMove, frame);
                unit.self().targetPositionX.set(command.x, frame);
                unit.self().targetPositionY.set(command.y, frame);
                unit.self().orderTargetPositionX.set(command.x, frame);
                unit.self().orderTargetPositionY.set(command.y, frame);
                break;

            // RLF
            case Attack_Unit:
                if (target == null || !target.exists() || !unit.getType().canAttack())
                    return;
                unit.self().order.set(Order.AttackUnit, frame);
                unit.self().target.set(getUnitID(target), frame);
                break;

            // RLF
            case Build:
                unit.self().order.set(Order.PlaceBuilding, frame);
                unit.self().isConstructing.set(true, frame);
                unit.self().isIdle.set(false, frame);
                unit.self().buildType.set(UnitType.idToEnum[command.extra], frame);
                break;

            // For building addons, SC takes minerals on RLF + 1.
            // Latcom will do as with building.building morph and reserve these resources.
            // RLF: Resource event
            // RLF + 1: Order event
            case Build_Addon: {
                UnitType addonType = UnitType.idToEnum[command.extra];
                switch (eventType) {
                    case Resource:
                        player.self().minerals.setOrAdd(-addonType.mineralPrice(), frame);
                        player.self().gas.setOrAdd(-addonType.gasPrice(), frame);

                        if (!isCurrentFrame) { // We will pretend the building is busy building, this doesn't
                            unit.self().isIdle.set(false, frame);
                            unit.self().order.set(Order.PlaceAddon, frame);
                        }
                        break;

                    case Order:
                        unit.self().isConstructing.set(true, frame);
                        unit.self().order.set(Order.Nothing, frame);
                        unit.self().secondaryOrder.set(Order.BuildAddon, frame);
                        unit.self().buildType.set(UnitType.idToEnum[command.extra], frame);
                        break;
                }
            }
            break;

            // RLF
            case Burrow:
                unit.self().order.set(Order.Burrowing, frame);
                break;

            // RLF: Resource event
            // RLF + 1: Order event
            case Cancel_Addon:
                switch(eventType) {
                    case Resource: {
                        UnitType addonType = unit.getBuildType();
                        player.self().minerals.setOrAdd((int) (addonType.mineralPrice() * 0.75), frame);
                        player.self().gas.setOrAdd((int) (addonType.gasPrice() * 0.75), frame);
                        unit.self().buildType.set(UnitType.None, frame);
                    }
                    break;
                    case Order:
                        unit.self().remainingBuildTime.set(0, frame);
                        unit.self().isConstructing.set(false, frame);
                        unit.self().order.set(Order.Nothing, frame);
                        unit.self().isIdle.set(true, frame);
                        unit.self().buildUnit.set(-1, frame);
                        break;
                }

                break;

            // RLF: Resource event
            // RLF + 1: Order event
            // RLF + 2: Finish event
            case Cancel_Construction: {
                if (unit.getType().getRace() == Race.Terran) {
                    Unit builder = unit.getBuildUnit();
                    if (builder != null && builder.exists()) {
                        switch (eventType) {
                            case Resource:
                                builder.self().buildType.set(UnitType.None, frame);
                                break;
                            case Order:
                                builder.self().isConstructing.set(false, frame);
                                builder.self().order.set(Order.ResetCollision, frame);
                                break;
                            case Finish:
                                builder.self().order.set(Order.PlayerGuard, frame);
                                break;
                        }
                    }
                }

                if (eventType == EventType.Resource) {
                    unit.self().buildUnit.set(-1, frame);
                    player.self().minerals.setOrAdd((int) (unit.getType().mineralPrice() * 0.75), frame);
                    player.self().gas.setOrAdd((int) (unit.getType().gasPrice() * 0.75), frame);
                    unit.self().remainingBuildTime.set(0, frame);
                }

                if (unit.getType().getRace() == Race.Zerg) {
                    switch (eventType) {
                        case Resource:
                            unit.self().type.set(unit.getType().whatBuilds().getFirst(), frame);
                            unit.self().buildType.set(UnitType.None, frame);
                            unit.self().isMorphing.set(false, frame);
                            unit.self().order.set(Order.ResetCollision, frame);
                            unit.self().isConstructing.set(false, frame);

                            player.self().supplyUsed[unit.getType().getRace().id]
                                    .setOrAdd(unit.getType().supplyRequired(), frame);
                            break;

                        case Order:
                            unit.self().order.set(Order.PlayerGuard, frame);
                            unit.self().isIdle.set(true, frame);
                            break;
                    }
                }

                break;
            }


            // RLF: Resource event
            // RLF + 1: Order event (only for builing . building morphs)
            // RLF + 13: Finish event (only for unit . unit morphs)
            // RLF + 15: Finish event (only for building . building morphs)
            case Cancel_Morph:
                switch(eventType) {
                    case Resource: {
                        UnitType builtType = unit.getBuildType();
                        UnitType newType = builtType.whatBuilds().getFirst();

                        if (newType.isBuilding()) {
                            player.self().minerals.setOrAdd((int) (builtType.mineralPrice() * 0.75), frame);
                            player.self().gas.setOrAdd((int) (builtType.gasPrice() * 0.75), frame);
                        }
                        else {
                            player.self().minerals.setOrAdd(builtType.mineralPrice(), frame);
                            player.self().gas.setOrAdd(builtType.gasPrice(), frame);
                        }

                        if (newType.isBuilding() && newType.producesCreep()) {
                            unit.self().order.set(Order.InitCreepGrowth, frame);
                        }

                        if (unit.getType() != UnitType.Zerg_Egg) { // Issue #781
                            // https://github.com/bwapi/bwapi/issues/781
                            unit.self().type.set(newType, frame);
                        }

                        unit.self().buildType.set(UnitType.None, frame);
                        unit.self().isConstructing.set(false, frame);
                        unit.self().isMorphing.set(false, frame);
                        unit.self().isCompleted.set(true, frame);
                        unit.self().remainingBuildTime.set(0, frame);
                    }

                    break;

                    case Order:
                        if (unit.getType().isBuilding()) {// This event would hopefully not have been created
                                                         // if this wasn't true (see event note above)
                            unit.self().isIdle.set(true, frame);
                            unit.self().order.set(Order.Nothing, frame);
                            if(unit.getType() == UnitType.Zerg_Hatchery || unit.getType() == UnitType.Zerg_Lair) {
                             // Type should have updated during last event to the cancelled type
                                unit.self().secondaryOrder.set(Order.SpreadCreep, frame);
                            }
                        }
                        else {
                            player.self().supplyUsed[unit.getType().getRace().id]
                                    .setOrAdd(
                                    - (unit.getType().supplyRequired() * (1 + (unit.getType().isTwoUnitsInOneEgg() ? 1 : 0))),
                                    frame);

                            player.self().supplyUsed[unit.getType().getRace().id].setOrAdd( // Could these races be different? Probably not.
                                    // Should we handle it?            Definetely.
                                    unit.getType().whatBuilds().getFirst().supplyRequired() * unit.getType().whatBuilds().getSecond(),
                                    frame);
                            // Note: unit.getType().whatBuilds().second is always 1 but we
                            // might as well handle the general case, in case Blizzard
                            // all of a sudden allows you to cancel archon morphs
                        }

                        break;

                    case Finish:
                        if(unit.getType() == UnitType.Zerg_Hatchery || unit.getType() == UnitType.Zerg_Lair) {
                            unit.self().secondaryOrder.set(Order.SpawningLarva, frame);
                        }
                        else if(!unit.getType().isBuilding()) {
                            unit.self().order.set(Order.PlayerGuard, frame);
                            unit.self().isCompleted.set(true, frame);
                            unit.self().isConstructing.set(false, frame);
                            unit.self().isIdle.set(true, frame);
                            unit.self().isMorphing .set(false, frame);
                        }
                        break;
                }

                break;

            // RLF: Resource event
            // RLF + 1: Order update
            case Cancel_Research: {
                switch(eventType) {
                    case Resource: {
                        TechType techType =  unit.getTech();
                        player.self().minerals.setOrAdd(techType.mineralPrice(), frame);
                        player.self().gas.setOrAdd(techType.gasPrice(), frame);
                        unit.self().remainingResearchTime.set(0, frame);
                        unit.self().tech.set(TechType.None, frame);
                    }
                    break;

                    case Order:
                        unit.self().order.set(Order.Nothing, frame);
                        unit.self().isIdle .set(true, frame);
                        break;
                }
            }

            break;

            // RLF: Resource event
            // RLF + 1: Order event
            // RLF + 3: Finish event
            case Cancel_Train_Slot:
                if (command.extra != 0) {
                    if (eventType == EventType.Resource) {
                        UnitType unitType = unit.getTrainingQueue().get(command.extra);
                        player.self().minerals.setOrAdd(unitType.mineralPrice(), frame);
                        player.self().gas.setOrAdd(unitType.gasPrice(), frame);

                        // Shift training queue back one slot after the cancelled unit
                        for (int i = command.extra; i < 4; ++i) {
                            unit.self().trainingQueue[i].set(unit.getTrainingQueue().get(i + 1), frame);
                        }

                        unit.self().trainingQueueCount.setOrAdd(- 1, frame);
                    }
                    break;
                }

                // If we're cancelling slot 0, we fall through to Cancel_Train.
                // RLF: Resource event
                // RLF + 1: Order event
                // RLF + 3: Finish event
            case Cancel_Train: {
                switch(eventType) {
                    case Resource: {
                        UnitType unitType = unit.getTrainingQueue().get(unit.getTrainingQueueCount() - 1);
                        player.self().minerals.setOrAdd(unitType.mineralPrice(), frame);
                        player.self().gas.setOrAdd(unitType.gasPrice(), frame);

                        unit.self().buildUnit.set(-1, frame);

                        if (unit.getTrainingQueueCount() == 1) {
                            unit.self().isIdle.set(false, frame);
                            unit.self().isTraining.set(false, frame);
                        }
                        break;
                    }

                    case Order: {
                        unit.self().trainingQueueCount.setOrAdd(- 1, frame);
                        UnitType unitType = unit.getTrainingQueue().get(unit.getTrainingQueueCount());
                        player.self().supplyUsed[unitType.getRace().id]
                                .setOrAdd(-unitType.supplyRequired(), frame);

                        if (unit.getTrainingQueueCount() == 0) {
                            unit.self().buildType.set(UnitType.None, frame);
                        }
                        else {
                            UnitType ut = unit.getTrainingQueue().get(unit.getTrainingQueueCount() - 1);
                            // Actual time decreases, but we'll let it be the buildTime until latency catches up.
                            unit.self().remainingTrainTime.set(ut.buildTime(), frame);
                            unit.self().buildType.set(ut, frame);
                        }
                    }

                    break;

                    case Finish:
                        if (unit.getBuildType() == UnitType.None) {
                            unit.self().order.set(Order.Nothing, frame);
                        }
                        break;
                }
                break;
            }

            // RLF: Resource event
            // RLF + 1: Order event
            case Cancel_Upgrade:
                switch(eventType) {
                    case Resource: {
                        UpgradeType upgradeType = unit.getUpgrade();
                        int nextLevel     = unit.getPlayer().getUpgradeLevel(upgradeType) + 1;

                        player.self().minerals.setOrAdd(upgradeType.mineralPrice(nextLevel), frame);
                        player.self().gas.setOrAdd(upgradeType.gasPrice(nextLevel), frame);

                        unit.self().upgrade.set(UpgradeType.None, frame);
                        unit.self().remainingUpgradeTime.set(0, frame);
                    }
                    break;

                    case Order:
                        unit.self().order.set(Order.Nothing, frame);
                        unit.self().isIdle.set(true, frame);
                        break;
                }

                break;

            // RLF
            case Cloak:
                unit.self().order.set(Order.Cloak, frame);
                unit.self().energy.set(-unit.getType().cloakingTech().energyCost(), frame);
                break;

            // RLF
            case Decloak:
                unit.self().order.set(Order.Decloak, frame);
                break;

            // RLF
            case Follow:
                unit.self().order.set(Order.Follow, frame);
                unit.self().target.set(getUnitID(target), frame);
                unit.self().isIdle.set(false, frame);
                unit.self().isMoving.set(true, frame);
                break;

            // RLF
            case Gather:
                unit.self().target.set(getUnitID(target), frame);
                unit.self().isIdle.set(false, frame);
                unit.self().isMoving.set(true, frame);
                unit.self().isGathering.set(true, frame);

                // @TODO: Fully time and test this order
                if (target.getType().isMineralField())
                    unit.self().order.set(Order.MoveToMinerals, frame);
                else if (target.getType().isRefinery())
                    unit.self().order.set(Order.MoveToGas, frame);

                break;

            // RLF: Order event
            // RLF + 1: Finish event
            case Halt_Construction:
                switch(eventType) {
                    case Order:
                        Unit building = unit.getBuildUnit();
                        if (building != null) {
                        building.self().buildUnit.set(-1, frame);
                    }
                    unit.self().buildUnit.set(-1, frame);
                    unit.self().order.set(Order.ResetCollision, frame);
                    unit.self().isConstructing.set(false, frame);
                    unit.self().buildType.set(UnitType.None, frame);
                    break;

                    case Finish:
                        unit.self().order.set(Order.PlayerGuard, frame);
                        unit.self().isIdle.set(true, frame);
                        break;
                }

                break;

            // RLF
            case Hold_Position:
                unit.self().isMoving.set(false, frame);
                unit.self().isIdle.set(false, frame);
                unit.self().order.set(Order.HoldPosition, frame);
                break;

            // RLF
            case Land:
                unit.self().order.set(Order.BuildingLand, frame);
                unit.self().isIdle.set(false, frame);
                break;

            // RLF
            case Lift:
                unit.self().order.set(Order.BuildingLiftOff, frame);
                unit.self().isIdle.set(false, frame);
                break;

            // RLF
            case Load:
                if (unit.getType() == UnitType.Terran_Bunker) {
                    unit.self().order.set(Order.PickupBunker, frame);
                    unit.self().target.set(getUnitID(target), frame);
                }
                else if (unit.getType().spaceProvided() != 0) {
                    unit.self().order.set(Order.PickupTransport, frame);
                    unit.self().target.set(getUnitID(target), frame);
                }
                else if (target.getType().spaceProvided() != 0) {
                    unit.self().order.set(Order.EnterTransport, frame);
                    unit.self().target.set(getUnitID(target), frame);
                }
                unit.self().isIdle.set(false, frame);

                break;

            // For morph, SC takes minerals on RLF + 1 if morphing building.building.
            // Latcom will do as with addons and reserve these resources.
            // RLF: Resource event
            // RLF + 1: Order event
            case Morph: {
                UnitType morphType = UnitType.idToEnum[command.extra];

                switch (eventType) {
                    case Resource:
                        if(!isCurrentFrame) {
                            unit.self().isCompleted.set(false, frame);
                            unit.self().isIdle.set(false, frame);
                            unit.self().isConstructing.set(true, frame);
                            unit.self().isMorphing.set(true, frame);
                            unit.self().buildType.set(morphType, frame);
                        }

                        if (unit.getType().isBuilding()) {
                            if (!isCurrentFrame) { // Actions that don't happen when we're reserving resources
                                unit.self().order.set(Order.ZergBuildingMorph, frame);
                                unit.self().type.set(morphType, frame);
                            }
                            player.self().minerals.setOrAdd(-morphType.mineralPrice(), frame);
                            player.self().gas.setOrAdd(-morphType.gasPrice(), frame);
                        }
                        else {
                            player.self().supplyUsed[morphType.getRace().id]
                                    .setOrAdd(morphType.supplyRequired() *
                                    (1 + (morphType.isTwoUnitsInOneEgg() ? 1 : 0)) - unit.getType().supplyRequired(),
                                    frame);

                            if(!isCurrentFrame) {
                                unit.self().order.set(Order.ZergUnitMorph, frame);

                                player.self().minerals.setOrAdd(-morphType.mineralPrice(), frame);
                                player.self().gas.setOrAdd(-morphType.gasPrice(), frame);

                                switch(morphType) {
                                    case Zerg_Lurker_Egg:
                                        unit.self().type.set(UnitType.Zerg_Lurker_Egg, frame);
                                        break;

                                    case Zerg_Devourer:
                                    case Zerg_Guardian:
                                        unit.self().type.set(UnitType.Zerg_Cocoon, frame);
                                        break;

                                    default:
                                        unit.self().type.set(UnitType.Zerg_Egg, frame);
                                        break;
                                }
                                unit.self().trainingQueue[unit.getTrainingQueueCount()].set(morphType, frame);
                                unit.self().trainingQueueCount.setOrAdd( +1, frame);

                            }
                        }
                        break;
                    case Order:
                        if (unit.getType().isBuilding()) {
                            unit.self().order.set(Order.IncompleteBuilding, frame);
                        }
                        break;
                }
            }

            break;

            // RLF
            case Move:
                unit.self().order.set(Order.Move, frame);
                unit.self().targetPositionX.set(command.x, frame);
                unit.self().targetPositionY.set(command.y, frame);
                unit.self().orderTargetPositionX.set(command.x, frame);
                unit.self().orderTargetPositionY.set(command.y, frame);
                unit.self().isMoving.set(true, frame);
                unit.self().isIdle.set(false, frame);
                break;

            // RLF
            case Patrol:
                unit.self().order.set(Order.Patrol, frame);
                unit.self().isIdle.set(false, frame);
                unit.self().isMoving.set(true, frame);
                unit.self().targetPositionX.set(command.x, frame);
                unit.self().targetPositionY.set(command.y, frame);
                unit.self().orderTargetPositionX.set(command.x, frame);
                unit.self().orderTargetPositionY.set(command.y, frame);
                break;

            // RLF
            case Repair:
                if (unit.getType() != UnitType.Terran_SCV) {
                    return;
                }
                unit.self().order.set(Order.Repair, frame);
                unit.self().target.set(getUnitID(target), frame);
                unit.self().isIdle.set(false, frame);
                break;

            // RLF
            case Research: {
                TechType techType = TechType.idToEnum[command.extra];
                unit.self().order.set(Order.ResearchTech, frame);
                unit.self().tech.set(techType, frame);
                unit.self().isIdle.set(false, frame);
                unit.self().remainingResearchTime.set(techType.researchTime(), frame);

                player.self().minerals.setOrAdd(-techType.mineralPrice(), frame);
                player.self().gas.setOrAdd(-techType.gasPrice(), frame);
                player.self().isResearching[techType.id].set(true, frame);
            }
            break;

            // RLF
            case Return_Cargo:
                if (!unit.isCarrying()) {
                    return;
                }

                unit.self().order.set(unit.isCarryingGas() ? Order.ReturnGas : Order.ReturnMinerals, frame);
                unit.self().isGathering.set(true, frame);
                unit.self().isIdle.set(false, frame);

                break;

            // RLF
            case Right_Click_Position:
                unit.self().order.set(Order.Move, frame);
                unit.self().targetPositionX.set(command.x, frame);
                unit.self().targetPositionY.set(command.y, frame);
                unit.self().orderTargetPositionX.set(command.x, frame);
                unit.self().orderTargetPositionY.set(command.y, frame);
                unit.self().isMoving.set(true, frame);
                unit.self().isIdle.set(false, frame);
                break;

            // RLF
            case Right_Click_Unit:
                unit.self().target.set(getUnitID(target), frame);
                unit.self().isIdle.set(false, frame);
                unit.self().isMoving.set(true, frame);

                if (unit.getType().isWorker() && target.getType().isMineralField()) {
                    unit.self().isGathering.set(true, frame);
                    unit.self().order.set(Order.MoveToMinerals, frame);
                }
                else if (unit.getType().isWorker() && target.getType().isRefinery()) {
                    unit.self().isGathering.set(true, frame);
                    unit.self().order.set(Order.MoveToGas, frame);
                }
                else if (unit.getType().isWorker() && target.getType().getRace() == Race.Terran &&
                                target.getType().whatBuilds().getFirst() == unit.getType() && !target.isCompleted()) {
                    unit.self().order.set(Order.ConstructingBuilding, frame);
                    unit.self().buildUnit.set(getUnitID(target), frame);
                    target.self().buildUnit.set(getUnitID(unit), frame);
                    unit.self().isConstructing.set(true, frame);
                    target.self().isConstructing.set(true, frame);
                }
                else if (unit.getType().canAttack() && target.getPlayer() != unit.getPlayer() && !target.getType().isNeutral()) {
                    unit.self().order.set(Order.AttackUnit, frame);
                }
                else if(unit.getType().canMove()) {
                    unit.self().order.set(Order.Follow, frame);
                }

                break;

            // RLF
            case Set_Rally_Position:
                if (!unit.getType().canProduce()) {
                    return;
                }

                unit.self().order.set(Order.RallyPointTile, frame);
                unit.self().rallyPositionX.set(command.x, frame);
                unit.self().rallyPositionY.set(command.y, frame);
                unit.self().rallyUnit.set(-1, frame);

                break;

            // RLF
            case Set_Rally_Unit:
                if (!unit.getType().canProduce()) {
                    return;
                }
                if (target == null || !target.exists()) {
                    return;
                }

                unit.self().order.set(Order.RallyPointUnit, frame);
                unit.self().rallyUnit.set(getUnitID(target), frame);

                break;

            // RLF
            case Siege:
                unit.self().order.set(Order.Sieging, frame);
                break;

            // RLF
            case Stop:
                unit.self().order.set(Order.Stop, frame);
                unit.self().isIdle.set(true, frame);
                break;

            // With train, the game does not take the supply until RLF + 1.
            // We just pretend that it happens on RLF.
            case Train: {
                UnitType unitType = UnitType.idToEnum[command.extra];

                if (!isCurrentFrame) {
                    // Happens on RLF, we don't want to duplicate this.
                    player.self().minerals.setOrAdd(-unitType.mineralPrice(), frame);
                    player.self().gas.setOrAdd(-unitType.gasPrice(), frame);
                }

                // Happens on RLF + 1, we want to pretend this happens on RLF.
                unit.self().trainingQueue[unit.getTrainingQueueCount()].set(unitType, frame);
                unit.self().trainingQueueCount.setOrAdd(+1, frame);
                player.self().supplyUsed[unitType.getRace().id]
                        .setOrAdd(unitType.supplyRequired(), frame);

                // Happens on RLF or RLF + 1, doesn't matter if we do twice
                unit.self().isTraining.set(true, frame);
                unit.self().isIdle.set(false, frame);
                unit.self().remainingTrainTime.set(unitType.buildTime(), frame);

                if (unitType == UnitType.Terran_Nuclear_Missile) {
                    unit.self().secondaryOrder.set(Order.Train, frame);
                }
            }

            break;

            // RLF
            case Unburrow:
                unit.self().order.set(Order.Unburrowing, frame);
                break;

            // RLF
            case Unload:
                unit.self().order.set(Order.Unload, frame);
                unit.self().target.set(getUnitID(target), frame);
                break;

            // RLF
            case Unload_All:
                if (unit.getType() == UnitType.Terran_Bunker) {
                    unit.self().order.set(Order.Unload, frame);
                }
                else {
                    unit.self().order .set(Order.MoveUnload, frame);
                    unit.self().targetPositionX.set(command.x, frame);
                    unit.self().targetPositionY.set(command.y, frame);
                    unit.self().orderTargetPositionX.set(command.x, frame);
                    unit.self().orderTargetPositionY.set(command.y, frame);
                }

                break;

            // RLF
            case Unload_All_Position:
                unit.self().order.set(Order.MoveUnload, frame);
                unit.self().targetPositionX.set(command.x, frame);
                unit.self().targetPositionY.set(command.y, frame);
                unit.self().orderTargetPositionX.set(command.x, frame);
                unit.self().orderTargetPositionY.set(command.y, frame);
                break;

            // RLF
            case Unsiege:
                unit.self().order.set(Order.Unsieging, frame);
                break;

            // RLF
            case Upgrade: {
                UpgradeType upgradeType = UpgradeType.idToEnum[command.extra];

                unit.self().order.set(Order.Upgrade, frame);
                unit.self().upgrade.set(upgradeType, frame);
                unit.self().isIdle.set(false, frame);

                int level                  = unit.getPlayer().getUpgradeLevel(upgradeType);
                unit.self().remainingUpgradeTime.set(upgradeType.upgradeTime(level + 1), frame);

                player.self().minerals.setOrAdd(-upgradeType.mineralPrice(level + 1), frame);
                player.self().gas.setOrAdd(upgradeType.gasPrice(level + 1), frame);

                player.self().isUpgrading[upgradeType.id].set( true, frame);
            }
            break;

            // RLF
            case Use_Tech:
                if (TechType.idToEnum[command.extra] == TechType.Stim_Packs && unit.getHitPoints() > 10) {
                    unit.self().hitPoints.setOrAdd(-10, frame);
                    unit.self().stimTimer.set(17, frame);
                }
                break;

            // RLF
            case Use_Tech_Position: {
                TechType techType = TechType.idToEnum[command.extra];

                if (!techType.targetsPosition()) {
                    return;
                }

                unit.self().order.set(techType.getOrder(), frame);
                unit.self().targetPositionX.set(command.x, frame);
                unit.self().targetPositionY.set(command.y, frame);
                unit.self().orderTargetPositionX.set(command.x, frame);
                unit.self().orderTargetPositionY.set(command.y, frame);
            }

            break;

            // RLF
            case Use_Tech_Unit: {
                TechType techType = TechType.idToEnum[command.extra];

                if (!techType.targetsUnit()) {
                    return;
                }

                unit.self().order.set(techType.getOrder(), frame);
                unit.self().orderTarget.set(getUnitID(target), frame);

                Position targetPosition    = target.getPosition();

                unit.self().targetPositionX.set(targetPosition.x, frame);
                unit.self().targetPositionY.set(targetPosition.y, frame);
                unit.self().orderTargetPositionX.set(targetPosition.x, frame);
                unit.self().orderTargetPositionY.set(targetPosition.y, frame);

                break;
            }
        }
    }
}
