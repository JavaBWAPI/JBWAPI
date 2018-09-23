package bwapi;

import JavaBWAPIBackend.Client.GameData.UnitData;
import bwapi.point.Position;
import bwapi.point.TilePosition;
import bwapi.types.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static bwapi.types.Order.*;
import static bwapi.types.Race.Terran;
import static bwapi.types.UnitType.*;

public class Unit {
    private final UnitData unitData;
    private final Game game;

    private final UnitType initialType;
    private final int initialResources;
    private final int initialHitPoints;
    private final Position initialPosition;

    //TODO
    private final Set<Unit> connectedUnits = new HashSet<>();

    Unit(final UnitData unitData, final Game game) {
        this.unitData = unitData;
        this.game = game;

        initialType = getType();
        initialResources = getResources();
        initialHitPoints = getHitPoints();
        initialPosition = getPosition();
    }

    public int getID() {
        return unitData.id();
    }


    public boolean exists() {
        return unitData.exists();
    }

    public int getReplayID() {
        return unitData.replayID();
    }

    public Player getPlayer() {
        return game.getPlayer(unitData.player());
    }

    public UnitType getType() {
        return UnitType.unitTypes[unitData.type()];
    }

    public Position getPosition() {
        return new Position(unitData.positionX(), unitData.positionY());
    }

    public TilePosition getTilePosition() {
        return getPosition().toTilePosition();
    }

    public double getAngle() {
        return unitData.angle();
    }

    public double getVelocityX() {
        return unitData.velocityX();
    }

    public double getVelocityY() {
        return unitData.velocityY();
    }

    public Region getRegion() {
        return game.getRegionAt(getPosition());
    }

    public int getLeft() {
        return unitData.positionX() - getType().dimensionLeft();
    }

    public int getTop() {
        return unitData.positionY() - getType().dimensionUp();
    }

    public int getRight() {
        return unitData.positionX() - getType().dimensionRight();
    }

    public int getBottom() {
        return unitData.positionY() - getType().dimensionDown();
    }

    public int getHitPoints() {
        return unitData.hitPoints();
    }

    public int getShields() {
        return unitData.shields();
    }

    public int getEnergy() {
        return unitData.energy();
    }

    public int getResources() {
        return unitData.resources();
    }

    public int getResourceGroup() {
        return unitData.resourceGroup();
    }

    public int getDistance(final Position target) {
        // If this unit does not exist or target is invalid
        if (!exists() || target == null) {
            return Integer.MAX_VALUE;
        }
        /////// Compute distance

        // retrieve left/top/right/bottom values for calculations
        int left = target.x - 1;
        int top = target.y - 1;
        int right = target.x + 1;
        int bottom = target.y + 1;

        // compute x distance
        int xDist = getLeft() - right;
        if (xDist < 0) {
            xDist = left - getRight();
            if (xDist < 0) {
                xDist = 0;
            }
        }

        // compute y distance
        int yDist = getTop() - bottom;
        if (yDist < 0) {
            yDist = top - getBottom();
            if (yDist < 0) {
                yDist = 0;
            }
        }

        // compute actual distance
        return Position.Origin.getApproxDistance(new Position(xDist, yDist));
    }

    public int getDistance(final Unit target) {
        // If this unit does not exist or target is invalid
        if (!exists() || target == null || !target.exists()) {
            return Integer.MAX_VALUE;
        }

        // If target is the same as the source
        if (this == target) {
            return 0;
        }

        /////// Compute distance

        // retrieve left/top/right/bottom values for calculations
        int left = target.getLeft() - 1;
        int top = target.getTop() - 1;
        int right = target.getRight() + 1;
        int bottom = target.getBottom() + 1;

        // compute x distance
        int xDist = getLeft() - right;
        if (xDist < 0) {
            xDist = left -getRight();
            if (xDist < 0) {
                xDist = 0;
            }
        }

        // compute y distance
        int yDist = getTop() - bottom;
        if (yDist < 0) {
            yDist = top - getBottom();
            if (yDist < 0) {
                yDist = 0;
            }
        }

        // compute actual distance
        return Position.Origin.getApproxDistance(new Position(xDist, yDist));
    }

    public boolean hasPath(final Position target) {
        // Return true if this unit is an air unit
        return isFlying() ||
                game.hasPath(getPosition(), target) ||
                game.hasPath(new Position(getLeft(), getTop()), target) ||
                game.hasPath(new Position(getRight(), getTop()), target) ||
                game.hasPath(new Position(getLeft(), getBottom()), target) ||
                game.hasPath(new Position(getRight(), getBottom()), target);
    }

    public boolean hasPath(final Unit target) {
        return hasPath(target.getPosition());
    }

//    //TODO
//    public int getLastCommandFrame();

//    public UnitCommand getLastCommand();

    public Player getLastAttackingPlayer() {
        return game.getPlayer(unitData.lastAttackerPlayer());
    }

    public UnitType getInitialType() {
        return initialType;
    }

    public Position getInitialPosition() {
        return initialPosition;
    }

    public TilePosition getInitialTilePosition() {
        return initialPosition.toTilePosition();
    }

    public int getInitialHitPoints() {
        return initialHitPoints;
    }

    public int getInitialResources() {
        return initialResources;
    }

    public int getKillCount() {
        return unitData.killCount();
    }

    public int getAcidSporeCount() {
        return unitData.acidSporeCount();
    }

    public int getInterceptorCount() {
        return unitData.interceptorCount();
    }

    public int getScarabCount() {
        return unitData.scarabCount();
    }

    public int getSpiderMineCount() {
        return unitData.spiderMineCount();
    }

    public int getGroundWeaponCooldown() {
        return unitData.groundWeaponCooldown();
    }

    public int getAirWeaponCooldown() {
        return unitData.airWeaponCooldown();
    }

    public int getSpellCooldown() {
        return unitData.spellCooldown();
    }

    public int getDefenseMatrixPoints() {
        return unitData.defenseMatrixPoints();
    }

    public int getDefenseMatrixTimer() {
        return unitData.defenseMatrixTimer();
    }

    public int getEnsnareTimer() {
        return unitData.ensnareTimer();
    }

    public int getIrradiateTimer() {
        return unitData.irradiateTimer();
    }

    public int getLockdownTimer() {
        return unitData.lockdownTimer();
    }

    public int getMaelstromTimer() {
        return unitData.maelstromTimer();
    }

    public int getOrderTimer() {
        return unitData.orderTimer();
    }

    public int getPlagueTimer() {
        return unitData.plagueTimer();
    }

    public int getRemoveTimer() {
        return unitData.removeTimer();
    }

    public int getStasisTimer() {
        return unitData.stasisTimer();
    }

    public int getStimTimer() {
        return unitData.stimTimer();
    }

    public UnitType getBuildType() {
        return UnitType.unitTypes[unitData.buildType()];
    }

    public List<UnitType> getTrainingQueue() {
        return IntStream.range(0, unitData.trainingQueueCount())
                .mapToObj(i -> UnitType.unitTypes[unitData.trainingQueue(i)])
                .collect(Collectors.toList());
    }

    public TechType getTech() {
        return TechType.techTypes[unitData.tech()];
    }

    public UpgradeType getUpgrade() {
        return UpgradeType.upgradeTypes[unitData.upgrade()];
    }

    public int getRemainingBuildTime() {
        return unitData.remainingBuildTime();
    }

    public int getRemainingTrainTime() {
        return unitData.remainingTrainTime();
    }

    public int getRemainingResearchTime() {
        return unitData.remainingResearchTime();
    }

    public int getRemainingUpgradeTime() {
        return unitData.remainingUpgradeTime();
    }

    public Unit getBuildUnit() {
        return game.getUnit(unitData.buildUnit());
    }

    public Unit getTarget() {
        return game.getUnit(unitData.target());
    }

    public Position getTargetPosition() {
        return new Position(unitData.orderTargetPositionX(), unitData.orderTargetPositionY());
    }

    public Order getOrder() {
        return Order.orders[unitData.order()];
    }

    public Order getSecondaryOrder() {
        return Order.orders[unitData.secondaryOrder()];
    }

    public Unit getOrderTarget() {
        return game.getUnit(unitData.orderTarget());
    }

    public Position getOrderTargetPosition() {
        return new Position(unitData.orderTargetPositionX(), unitData.orderTargetPositionY());
    }

    public Position getRallyPosition() {
        return new Position(unitData.rallyPositionX(), unitData.rallyPositionY());
    }

    public Unit getRallyUnit() {
        return game.getUnit(unitData.rallyUnit());
    }

    public Unit getAddon() {
        return game.getUnit(unitData.addon());
    }

    public Unit getNydusExit() {
        return game.getUnit(unitData.nydusExit());
    }

    public Unit getPowerUp() {
        return game.getUnit(unitData.powerUp());
    }

    public Unit getTransport() {
        return game.getUnit(unitData.transport());
    }

    //TODO
    public List<Unit> getLoadedUnits() {
        return null;
    }

    public int getSpaceRemaining() {
        int space = getType().spaceProvided();

        // Decrease the space for each loaded unit
        for (final Unit u : getLoadedUnits()) {
            space -= u.getType().spaceRequired();
        }
        return Math.max(space, 0);
    }

    public Unit getCarrier() {
        return game.getUnit(unitData.carrier());
    }

    public Set<Unit> getInterceptors() {
        if (getType() != Protoss_Carrier && getType() != Hero_Gantrithor) {
            return new HashSet<>();
        }
        return connectedUnits;
    }

    public Unit getHatchery() {
        return game.getUnit(unitData.hatchery());
    }

    public Set<Unit> getLarva() {
        if (!getType().producesLarva()) {
            return new HashSet<>();
        }
        return connectedUnits;
    }

    //TODO
    //public Set<Unit> getUnitsInRadius(final int radius)

    //TODO
    //public List<Unit> getUnitsInWeaponRange(final WeaponType weapon);

    public boolean hasNuke() {
        return unitData.hasNuke();
    }

    public boolean isAccelerating() {
        return unitData.isAccelerating();
    }

    public boolean isAttacking() {
        return unitData.isAttacking();
    }

    public boolean isAttackFrame() {
        return unitData.isAttackFrame();
    }

    public boolean isBeingConstructed() {
        if (isMorphing()) {
            return true;
        }
        if (isCompleted()) {
            return false;
        }
        if (getType().getRace() != Terran ) {
            return true;
        }
        return getBuildUnit() != null;
    }

    public boolean isBeingGathered() {
        return unitData.isBeingGathered();
    }

    public boolean isBeingHealed() {
        return getType().getRace() == Terran && isCompleted() && getHitPoints() > unitData.lastHitPoints();
    }

    public boolean isBlind() {
        return unitData.isBlind();
    }

    public boolean isBraking() {
        return unitData.isBraking();
    }

    public boolean isBurrowed() {
        return unitData.isBurrowed();
    }

    public boolean isCarryingGas() {
        return unitData.carryResourceType() == 1;
    }

    public boolean isCarryingMinerals() {
        return unitData.carryResourceType() == 2;
    }

    public boolean isCloaked() {
        return unitData.isCloaked();
    }

    public boolean isCompleted() {
        return unitData.isCompleted();
    }

    public boolean isConstructing() {
        return unitData.isConstructing();
    }

    public boolean isDefenseMatrixed() {
        return getDefenseMatrixTimer() != 0;
    }

    public boolean isDetected() {
        return unitData.isDetected();
    }

    public boolean isEnsnared() {
        return getEnsnareTimer() != 0;
    }

    public boolean isFlying() {
        return getType().isFlyer() || isLifted();
    }

    public boolean isFollowing() {
        return getOrder() == Order.Follow;
    }

    private static Set<Order> gatheringGasOrders = new HashSet<>(Arrays.asList(
            Harvest1, Harvest2, MoveToGas, WaitForGas, HarvestGas, ReturnGas, ResetCollision));

    private static boolean reallyGatheringGas(final Unit targ, final Player player) {
        return targ != null && targ.exists() && targ.isCompleted() && targ.getPlayer() == player &&
                targ.getType() != Resource_Vespene_Geyser && (targ.getType().isRefinery() || targ.getType().isResourceDepot());
    }

    public boolean isGatheringGas() {
        if (!unitData.isGathering()) {
            return false;
        }
        final Order order = getOrder();
        if (!gatheringGasOrders.contains(order)) {
            return false;
        }
        if (order == ResetCollision) {
            return unitData.carryResourceType() == 1;
        }
        //return true if BWOrder is WaitForGas, HarvestGas, or ReturnGas
        if (order == WaitForGas || order == HarvestGas || order == ReturnGas) {
            return true;
        }
        //if BWOrder is MoveToGas, Harvest1, or Harvest2 we need to do some additional checks to make sure the unit is really gathering
        return reallyGatheringGas(getTarget(), getPlayer()) || reallyGatheringGas(getOrderTarget(), getPlayer());
    }

    private static boolean reallyGatheringMinerals(final Unit targ, final Player player) {
        return targ != null && targ.exists() && (targ.getType().isMineralField() ||
                (targ.isCompleted() && targ.getPlayer() == player && targ.getType().isResourceDepot()));
    }

    private static Set<Order> gatheringMineralOrders = new HashSet<>(Arrays.asList(
            Harvest1, Harvest2, MoveToMinerals, WaitForMinerals, MiningMinerals, ReturnMinerals, ResetCollision));

    public boolean isGatheringMinerals() {
        if (!unitData.isGathering()) {
            return false;
        }
        final Order order = getOrder();
        if (!gatheringMineralOrders.contains(order)) {
            return false;
        }
        if (order == ResetCollision) {
            return unitData.carryResourceType() == 2;
        }
        //return true if BWOrder is WaitForMinerals, MiningMinerals, or ReturnMinerals
        if (order == WaitForMinerals || order == MiningMinerals || order == ReturnMinerals) {
            return true;
        }
        //if BWOrder is MoveToMinerals, Harvest1, or Harvest2 we need to do some additional checks to make sure the unit is really gathering
        return reallyGatheringMinerals(getTarget(), getPlayer()) || reallyGatheringMinerals(getOrderTarget(), getPlayer());
    }

    public boolean isHallucination() {
        return unitData.isHallucination();
    }

    public boolean isHoldingPosition() {
        return getOrder() == HoldPosition;
    }

    public boolean isIdle() {
        return unitData.isIdle();
    }

    public boolean isInterruptible() {
        return unitData.isInterruptible();
    }

    public boolean isInvincible() {
        return unitData.isInvincible();
    }

    public boolean isInWeaponRange(final Unit target) {
        // Preliminary checks
        if ( !exists() || target == null || !target.exists() || this == target) {
            return false;
        }

        // Store the types as locals
        final UnitType thisType = getType();
        final UnitType targType = target.getType();

        // Obtain the weapon type
        final WeaponType wpn = target.isFlying() ? thisType.airWeapon() : thisType.groundWeapon();

        // Return if there is no weapon type
        if (wpn == WeaponType.None || wpn == WeaponType.Unknown) {
            return false;
        }

        // Retrieve the min and max weapon ranges
        int minRange = wpn.minRange();
        int maxRange = getPlayer().weaponMaxRange(wpn);

        // Check if the distance to the unit is within the weapon range
        int distance = getDistance(target);
        return (minRange != 0 ? minRange < distance : true) && distance <= maxRange;
    }

    public boolean isIrradiated() {
        return getIrradiateTimer() != 0;
    }

    public boolean isLifted() {
        return unitData.isLifted();
    }

    public boolean isLoaded() {
        return getTransport() != null;
    }

    public boolean isLockedDown() {
        return getLockdownTimer() != 0;
    }

    public boolean isMaelstrommed() {
        return getMaelstromTimer() != 0;
    }

    public boolean isMorphing() {
        return unitData.isMorphing();
    }

    public boolean isMoving() {
        return unitData.isMoving();
    };

    public boolean isParasited() {
        return unitData.isParasited();
    }

    public boolean isPatrolling() {
        return getOrder() == Patrol;
    }

    public boolean isPlagued() {
        return getPlagueTimer() != 0;
    }

    public boolean isRepairing() {
        return getOrder() == Repair;
    }

    public boolean isResearching() {
        return getOrder() == ResearchTech;
    }

    public boolean isSelected() {
        return unitData.isSelected();
    }

    public boolean isSieged() {
        final UnitType t = getType();
        return  t == Terran_Siege_Tank_Siege_Mode || t == Hero_Edmund_Duke_Siege_Mode;
    }

    public boolean isStartingAttack() {
        return unitData.isStartingAttack();
    }

    public boolean isStasised() {
        return getStasisTimer() != 0;
    }

    public boolean isStimmed() {
        return getStimTimer() != 0;
    }

    public boolean isStuck() {
        return unitData.isStuck();
    }

    public boolean isTraining() {
        return unitData.isTraining();
    }

    public boolean isUnderAttack() {
        return unitData.recentlyAttacked();
    }

    public boolean isUnderDarkSwarm() {
        return unitData.isUnderDarkSwarm();
    }

    public boolean isUnderDisruptionWeb() {
        return unitData.isUnderDWeb();
    }

    public boolean isUnderStorm() {
        return unitData.isUnderStorm();
    }

    public boolean isPowered() {
        return unitData.isPowered();
    }

    public boolean isUpgrading() {
        return getOrder() == Upgrade;
    }

    public boolean isVisible() {
        return isVisible(game.self());
    }

    public boolean isVisible(final Player player) {
        return unitData.isVisible(player.getID());
    }

    //TODO
    //public boolean isTargetable();

    /*
    public boolean issueCommand(UnitCommand command);

    public boolean attack(Position target);

    public boolean attack(Unit target);

    public boolean attack(Position target, boolean shiftQueueCommand);

    public boolean attack(Unit target, boolean shiftQueueCommand);

    public boolean build(UnitType type);

    public boolean build(UnitType type, TilePosition target);

    public boolean buildAddon(UnitType type);

    public boolean train();

    public boolean train(UnitType type);

    public boolean morph(UnitType type);

    public boolean research(TechType tech);

    public boolean upgrade(UpgradeType upgrade);

    public boolean setRallyPoint(Position target);

    public boolean setRallyPoint(Unit target);

    public boolean move(Position target);

    public boolean move(Position target, boolean shiftQueueCommand);

    public boolean patrol(Position target);

    public boolean patrol(Position target, boolean shiftQueueCommand);

    public boolean holdPosition();

    public boolean holdPosition(boolean shiftQueueCommand);

    public boolean stop();

    public boolean stop(boolean shiftQueueCommand);

    public boolean follow(Unit target);

    public boolean follow(Unit target, boolean shiftQueueCommand);

    public boolean gather(Unit target);

    public boolean gather(Unit target, boolean shiftQueueCommand);

    public boolean repair(Unit target);

    public boolean repair(Unit target, boolean shiftQueueCommand);

    public boolean burrow();

    public boolean unburrow();

    public boolean cloak();

    public boolean decloak();

    public boolean siege();

    public boolean unsiege();

    public boolean lift();

    public boolean land(TilePosition target);

    public boolean load(Unit target);

    public boolean load(Unit target, boolean shiftQueueCommand);

    public boolean unload(Unit target);

    public boolean unloadAll();

    public boolean unloadAll(boolean shiftQueueCommand);

    public boolean unloadAll(Position target);

    public boolean unloadAll(Position target, boolean shiftQueueCommand);

    public boolean rightClick(Position target);

    public boolean rightClick(Unit target);

    public boolean rightClick(Position target, boolean shiftQueueCommand);

    public boolean rightClick(Unit target, boolean shiftQueueCommand);

    public boolean haltConstruction();

    public boolean cancelConstruction();

    public boolean cancelAddon();

    public boolean cancelTrain();

    public boolean cancelTrain(int slot);

    public boolean cancelMorph();

    public boolean cancelResearch();

    public boolean cancelUpgrade();

    public boolean useTech(TechType tech);

    public boolean useTech(TechType tech, Position target);

    public boolean useTech(TechType tech, Unit target);

    public boolean placeCOP(TilePosition target);

    */

    /*
    public boolean canIssueCommand(UnitCommand command, boolean checkCanUseTechPositionOnPositions, boolean checkCanUseTechUnitOnUnits, boolean checkCanBuildUnitType, boolean checkCanTargetUnit, boolean checkCanIssueCommandType);

    public boolean canIssueCommand(UnitCommand command, boolean checkCanUseTechPositionOnPositions, boolean checkCanUseTechUnitOnUnits, boolean checkCanBuildUnitType, boolean checkCanTargetUnit);

    public boolean canIssueCommand(UnitCommand command, boolean checkCanUseTechPositionOnPositions, boolean checkCanUseTechUnitOnUnits, boolean checkCanBuildUnitType);

    public boolean canIssueCommand(UnitCommand command, boolean checkCanUseTechPositionOnPositions, boolean checkCanUseTechUnitOnUnits);

    public boolean canIssueCommand(UnitCommand command, boolean checkCanUseTechPositionOnPositions);

    public boolean canIssueCommand(UnitCommand command);

    public boolean canIssueCommand(UnitCommand command, boolean checkCanUseTechPositionOnPositions, boolean checkCanUseTechUnitOnUnits, boolean checkCanBuildUnitType, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility);

    public boolean canIssueCommandGrouped(UnitCommand command, boolean checkCanUseTechPositionOnPositions, boolean checkCanUseTechUnitOnUnits, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped);

    public boolean canIssueCommandGrouped(UnitCommand command, boolean checkCanUseTechPositionOnPositions, boolean checkCanUseTechUnitOnUnits, boolean checkCanTargetUnit, boolean checkCanIssueCommandType);

    public boolean canIssueCommandGrouped(UnitCommand command, boolean checkCanUseTechPositionOnPositions, boolean checkCanUseTechUnitOnUnits, boolean checkCanTargetUnit);

    public boolean canIssueCommandGrouped(UnitCommand command, boolean checkCanUseTechPositionOnPositions, boolean checkCanUseTechUnitOnUnits);

    public boolean canIssueCommandGrouped(UnitCommand command, boolean checkCanUseTechPositionOnPositions);

    public boolean canIssueCommandGrouped(UnitCommand command);

    public boolean canIssueCommandGrouped(UnitCommand command, boolean checkCanUseTechPositionOnPositions, boolean checkCanUseTechUnitOnUnits, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped, boolean checkCommandibility);

    public boolean canCommand();

    public boolean canCommandGrouped();

    public boolean canCommandGrouped(boolean checkCommandibility);

    public boolean canIssueCommandType(UnitCommandType ct);

    public boolean canIssueCommandType(UnitCommandType ct, boolean checkCommandibility);

    public boolean canIssueCommandTypeGrouped(UnitCommandType ct, boolean checkCommandibilityGrouped);

    public boolean canIssueCommandTypeGrouped(UnitCommandType ct);

    public boolean canIssueCommandTypeGrouped(UnitCommandType ct, boolean checkCommandibilityGrouped, boolean checkCommandibility);

    public boolean canTargetUnit(Unit targetUnit);

    public boolean canTargetUnit(Unit targetUnit, boolean checkCommandibility);

    public boolean canAttack();

    public boolean canAttack(boolean checkCommandibility);

    public boolean canAttack(Position target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType);

    public boolean canAttack(Unit target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType);

    public boolean canAttack(Position target, boolean checkCanTargetUnit);

    public boolean canAttack(Unit target, boolean checkCanTargetUnit);


    public boolean canAttack(Position target);

    public boolean canAttack(Unit target);

    public boolean canAttack(Position target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility);

    public boolean canAttack(Unit target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility);

    public boolean canAttackGrouped(boolean checkCommandibilityGrouped);

    public boolean canAttackGrouped();

    public boolean canAttackGrouped(boolean checkCommandibilityGrouped, boolean checkCommandibility);

    public boolean canAttackGrouped(Position target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped);

    public boolean canAttackGrouped(Unit target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped);

    public boolean canAttackGrouped(Position target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType);

    public boolean canAttackGrouped(Unit target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType);


    public boolean canAttackGrouped(Position target, boolean checkCanTargetUnit);

    public boolean canAttackGrouped(Unit target, boolean checkCanTargetUnit);

    public boolean canAttackGrouped(Position target);

    public boolean canAttackGrouped(Unit target);

    public boolean canAttackGrouped(Position target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped, boolean checkCommandibility);

    public boolean canAttackGrouped(Unit target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped, boolean checkCommandibility);

    public boolean canAttackMove();

    public boolean canAttackMove(boolean checkCommandibility);

    public boolean canAttackMoveGrouped(boolean checkCommandibilityGrouped);

    public boolean canAttackMoveGrouped();

    public boolean canAttackMoveGrouped(boolean checkCommandibilityGrouped, boolean checkCommandibility);

    public boolean canAttackUnit();

    public boolean canAttackUnit(boolean checkCommandibility);

    public boolean canAttackUnit(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType);

    public boolean canAttackUnit(Unit targetUnit, boolean checkCanTargetUnit);

    public boolean canAttackUnit(Unit targetUnit);

    public boolean canAttackUnit(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility);

    public boolean canAttackUnitGrouped(boolean checkCommandibilityGrouped);

    public boolean canAttackUnitGrouped();

    public boolean canAttackUnitGrouped(boolean checkCommandibilityGrouped, boolean checkCommandibility);

    public boolean canAttackUnitGrouped(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped);

    public boolean canAttackUnitGrouped(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType);

    public boolean canAttackUnitGrouped(Unit targetUnit, boolean checkCanTargetUnit);

    public boolean canAttackUnitGrouped(Unit targetUnit);

    public boolean canAttackUnitGrouped(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped, boolean checkCommandibility);

    public boolean canBuild();

    public boolean canBuild(boolean checkCommandibility);

    public boolean canBuild(UnitType uType, boolean checkCanIssueCommandType);

    public boolean canBuild(UnitType uType);

    public boolean canBuild(UnitType uType, boolean checkCanIssueCommandType, boolean checkCommandibility);

    public boolean canBuild(UnitType uType, TilePosition tilePos, boolean checkTargetUnitType, boolean checkCanIssueCommandType);

    public boolean canBuild(UnitType uType, TilePosition tilePos, boolean checkTargetUnitType);

    public boolean canBuild(UnitType uType, TilePosition tilePos);

    public boolean canBuild(UnitType uType, TilePosition tilePos, boolean checkTargetUnitType, boolean checkCanIssueCommandType, boolean checkCommandibility);

    public boolean canBuildAddon();

    public boolean canBuildAddon(boolean checkCommandibility);

    public boolean canBuildAddon(UnitType uType, boolean checkCanIssueCommandType);

    public boolean canBuildAddon(UnitType uType);

    public boolean canBuildAddon(UnitType uType, boolean checkCanIssueCommandType, boolean checkCommandibility);

    public boolean canTrain();

    public boolean canTrain(boolean checkCommandibility);

    public boolean canTrain(UnitType uType, boolean checkCanIssueCommandType);

    public boolean canTrain(UnitType uType);

    public boolean canTrain(UnitType uType, boolean checkCanIssueCommandType, boolean checkCommandibility);

    public boolean canMorph();

    public boolean canMorph(boolean checkCommandibility);

    public boolean canMorph(UnitType uType, boolean checkCanIssueCommandType);

    public boolean canMorph(UnitType uType);

    public boolean canMorph(UnitType uType, boolean checkCanIssueCommandType, boolean checkCommandibility);

    public boolean canResearch();

    public boolean canResearch(boolean checkCommandibility);

    public boolean canResearch(TechType type);

    public boolean canResearch(TechType type, boolean checkCanIssueCommandType);

    public boolean canUpgrade();

    public boolean canUpgrade(boolean checkCommandibility);

    public boolean canUpgrade(UpgradeType type);

    public boolean canUpgrade(UpgradeType type, boolean checkCanIssueCommandType);

    public boolean canSetRallyPoint();

    public boolean canSetRallyPoint(boolean checkCommandibility);

    public boolean canSetRallyPoint(Position target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType);

    public boolean canSetRallyPoint(Unit target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType);

    public boolean canSetRallyPoint(Position target, boolean checkCanTargetUnit);

    public boolean canSetRallyPoint(Unit target, boolean checkCanTargetUnit);

    public boolean canSetRallyPoint(Position target);

    public boolean canSetRallyPoint(Unit target);

    public boolean canSetRallyPoint(Position target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility);

    public boolean canSetRallyPoint(Unit target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility);

    public boolean canSetRallyPosition();

    public boolean canSetRallyPosition(boolean checkCommandibility);

    public boolean canSetRallyUnit();

    public boolean canSetRallyUnit(boolean checkCommandibility);

    public boolean canSetRallyUnit(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType);

    public boolean canSetRallyUnit(Unit targetUnit, boolean checkCanTargetUnit);

    public boolean canSetRallyUnit(Unit targetUnit);

    public boolean canSetRallyUnit(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility);

    public boolean canMove();

    public boolean canMove(boolean checkCommandibility);

    public boolean canMoveGrouped(boolean checkCommandibilityGrouped);

    public boolean canMoveGrouped();

    public boolean canMoveGrouped(boolean checkCommandibilityGrouped, boolean checkCommandibility);

    public boolean canPatrol();

    public boolean canPatrol(boolean checkCommandibility);

    public boolean canPatrolGrouped(boolean checkCommandibilityGrouped);

    public boolean canPatrolGrouped();

    public boolean canPatrolGrouped(boolean checkCommandibilityGrouped, boolean checkCommandibility);

    public boolean canFollow();

    public boolean canFollow(boolean checkCommandibility);

    public boolean canFollow(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType);

    public boolean canFollow(Unit targetUnit, boolean checkCanTargetUnit);

    public boolean canFollow(Unit targetUnit);

    public boolean canFollow(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility);

    public boolean canGather();

    public boolean canGather(boolean checkCommandibility);

    public boolean canGather(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType);

    public boolean canGather(Unit targetUnit, boolean checkCanTargetUnit);

    public boolean canGather(Unit targetUnit);

    public boolean canGather(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility);

    public boolean canReturnCargo();

    public boolean canReturnCargo(boolean checkCommandibility);

    public boolean canHoldPosition();

    public boolean canHoldPosition(boolean checkCommandibility);

    public boolean canStop();

    public boolean canStop(boolean checkCommandibility);

    public boolean canRepair();

    public boolean canRepair(boolean checkCommandibility);

    public boolean canRepair(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType);

    public boolean canRepair(Unit targetUnit, boolean checkCanTargetUnit);

    public boolean canRepair(Unit targetUnit);

    public boolean canRepair(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility);

    public boolean canBurrow();

    public boolean canBurrow(boolean checkCommandibility);

    public boolean canUnburrow();

    public boolean canUnburrow(boolean checkCommandibility);

    public boolean canCloak();

    public boolean canCloak(boolean checkCommandibility);

    public boolean canDecloak();

    public boolean canDecloak(boolean checkCommandibility);

    public boolean canSiege();

    public boolean canSiege(boolean checkCommandibility);

    public boolean canUnsiege();

    public boolean canUnsiege(boolean checkCommandibility);

    public boolean canLift();

    public boolean canLift(boolean checkCommandibility);

    public boolean canLand();

    public boolean canLand(boolean checkCommandibility);

    public boolean canLand(TilePosition target, boolean checkCanIssueCommandType);

    public boolean canLand(TilePosition target);

    public boolean canLand(TilePosition target, boolean checkCanIssueCommandType, boolean checkCommandibility);

    public boolean canLoad();

    public boolean canLoad(boolean checkCommandibility);

    public boolean canLoad(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType);

    public boolean canLoad(Unit targetUnit, boolean checkCanTargetUnit);

    public boolean canLoad(Unit targetUnit);

    public boolean canLoad(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility);

    public boolean canUnloadWithOrWithoutTarget();

    public boolean canUnloadWithOrWithoutTarget(boolean checkCommandibility);

    public boolean canUnloadAtPosition(Position targDropPos, boolean checkCanIssueCommandType);

    public boolean canUnloadAtPosition(Position targDropPos);

    public boolean canUnloadAtPosition(Position targDropPos, boolean checkCanIssueCommandType, boolean checkCommandibility);

    public boolean canUnload();

    public boolean canUnload(boolean checkCommandibility);

    public boolean canUnload(Unit targetUnit, boolean checkCanTargetUnit, boolean checkPosition, boolean checkCanIssueCommandType);

    public boolean canUnload(Unit targetUnit, boolean checkCanTargetUnit, boolean checkPosition);

    public boolean canUnload(Unit targetUnit, boolean checkCanTargetUnit);

    public boolean canUnload(Unit targetUnit);

    public boolean canUnload(Unit targetUnit, boolean checkCanTargetUnit, boolean checkPosition, boolean checkCanIssueCommandType, boolean checkCommandibility);

    public boolean canUnloadAll();

    public boolean canUnloadAll(boolean checkCommandibility);

    public boolean canUnloadAllPosition();

    public boolean canUnloadAllPosition(boolean checkCommandibility);

    public boolean canUnloadAllPosition(Position targDropPos, boolean checkCanIssueCommandType);

    public boolean canUnloadAllPosition(Position targDropPos);

    public boolean canUnloadAllPosition(Position targDropPos, boolean checkCanIssueCommandType, boolean checkCommandibility);

    public boolean canRightClick();

    public boolean canRightClick(boolean checkCommandibility);

    public boolean canRightClick(Position target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType);

    public boolean canRightClick(Unit target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType);

    public boolean canRightClick(Position target, boolean checkCanTargetUnit);

    public boolean canRightClick(Unit target, boolean checkCanTargetUnit);


    public boolean canRightClick(Position target);

    public boolean canRightClick(Unit target);

    public boolean canRightClick(Position target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility);

    public boolean canRightClick(Unit target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility);

    public boolean canRightClickGrouped(boolean checkCommandibilityGrouped);

    public boolean canRightClickGrouped();

    public boolean canRightClickGrouped(boolean checkCommandibilityGrouped, boolean checkCommandibility);

    public boolean canRightClickGrouped(Position target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped);

    public boolean canRightClickGrouped(Unit target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped);

    public boolean canRightClickGrouped(Position target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType);

    public boolean canRightClickGrouped(Unit target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType);

    public boolean canRightClickGrouped(Position target, boolean checkCanTargetUnit);

    public boolean canRightClickGrouped(Unit target, boolean checkCanTargetUnit);

    public boolean canRightClickGrouped(Position target);

    public boolean canRightClickGrouped(Unit target);

    public boolean canRightClickGrouped(Position target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped, boolean checkCommandibility);

    public boolean canRightClickGrouped(Unit target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped, boolean checkCommandibility);

    public boolean canRightClickPosition();

    public boolean canRightClickPosition(boolean checkCommandibility);

    public boolean canRightClickPositionGrouped(boolean checkCommandibilityGrouped);

    public boolean canRightClickPositionGrouped();

    public boolean canRightClickPositionGrouped(boolean checkCommandibilityGrouped, boolean checkCommandibility);

    public boolean canRightClickUnit();

    public boolean canRightClickUnit(boolean checkCommandibility);

    public boolean canRightClickUnit(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType);

    public boolean canRightClickUnit(Unit targetUnit, boolean checkCanTargetUnit);

    public boolean canRightClickUnit(Unit targetUnit);

    public boolean canRightClickUnit(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility);

    public boolean canRightClickUnitGrouped(boolean checkCommandibilityGrouped);

    public boolean canRightClickUnitGrouped();

    public boolean canRightClickUnitGrouped(boolean checkCommandibilityGrouped, boolean checkCommandibility);

    public boolean canRightClickUnitGrouped(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped);

    public boolean canRightClickUnitGrouped(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType);

    public boolean canRightClickUnitGrouped(Unit targetUnit, boolean checkCanTargetUnit);

    public boolean canRightClickUnitGrouped(Unit targetUnit);

    public boolean canRightClickUnitGrouped(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped, boolean checkCommandibility);

    public boolean canHaltConstruction();

    public boolean canHaltConstruction(boolean checkCommandibility);

    public boolean canCancelConstruction();

    public boolean canCancelConstruction(boolean checkCommandibility);

    public boolean canCancelAddon();

    public boolean canCancelAddon(boolean checkCommandibility);

    public boolean canCancelTrain();

    public boolean canCancelTrain(boolean checkCommandibility);

    public boolean canCancelTrainSlot();

    public boolean canCancelTrainSlot(boolean checkCommandibility);

    public boolean canCancelTrainSlot(int slot, boolean checkCanIssueCommandType);

    public boolean canCancelTrainSlot(int slot);

    public boolean canCancelTrainSlot(int slot, boolean checkCanIssueCommandType, boolean checkCommandibility);

    public boolean canCancelMorph();

    public boolean canCancelMorph(boolean checkCommandibility);

    public boolean canCancelResearch();

    public boolean canCancelResearch(boolean checkCommandibility);

    public boolean canCancelUpgrade();

    public boolean canCancelUpgrade(boolean checkCommandibility);

    public boolean canUseTechWithOrWithoutTarget();

    public boolean canUseTechWithOrWithoutTarget(boolean checkCommandibility);

    public boolean canUseTechWithOrWithoutTarget(TechType tech, boolean checkCanIssueCommandType);

    public boolean canUseTechWithOrWithoutTarget(TechType tech);

    public boolean canUseTechWithOrWithoutTarget(TechType tech, boolean checkCanIssueCommandType, boolean checkCommandibility);

    public boolean canUseTech(TechType tech, Position target, boolean checkCanTargetUnit, boolean checkTargetsType, boolean checkCanIssueCommandType);

    public boolean canUseTech(TechType tech, Unit target, boolean checkCanTargetUnit, boolean checkTargetsType, boolean checkCanIssueCommandType);

    public boolean canUseTech(TechType tech, Position target, boolean checkCanTargetUnit, boolean checkTargetsType);

    public boolean canUseTech(TechType tech, Unit target, boolean checkCanTargetUnit, boolean checkTargetsType);

    public boolean canUseTech(TechType tech, Position target, boolean checkCanTargetUnit);

    public boolean canUseTech(TechType tech, Unit target, boolean checkCanTargetUnit);

    public boolean canUseTech(TechType tech, Position target);

    public boolean canUseTech(TechType tech, Unit target);

    public boolean canUseTech(TechType tech);

    public boolean canUseTech(TechType tech, Position target, boolean checkCanTargetUnit, boolean checkTargetsType, boolean checkCanIssueCommandType, boolean checkCommandibility);

    public boolean canUseTech(TechType tech, Unit target, boolean checkCanTargetUnit, boolean checkTargetsType, boolean checkCanIssueCommandType, boolean checkCommandibility);

    public boolean canUseTechWithoutTarget(TechType tech, boolean checkCanIssueCommandType);

    public boolean canUseTechWithoutTarget(TechType tech);

    public boolean canUseTechWithoutTarget(TechType tech, boolean checkCanIssueCommandType, boolean checkCommandibility);

    public boolean canUseTechUnit(TechType tech, boolean checkCanIssueCommandType);

    public boolean canUseTechUnit(TechType tech);

    public boolean canUseTechUnit(TechType tech, boolean checkCanIssueCommandType, boolean checkCommandibility);

    public boolean canUseTechUnit(TechType tech, Unit targetUnit, boolean checkCanTargetUnit, boolean checkTargetsUnits, boolean checkCanIssueCommandType);

    public boolean canUseTechUnit(TechType tech, Unit targetUnit, boolean checkCanTargetUnit, boolean checkTargetsUnits);

    public boolean canUseTechUnit(TechType tech, Unit targetUnit, boolean checkCanTargetUnit);

    public boolean canUseTechUnit(TechType tech, Unit targetUnit);

    public boolean canUseTechUnit(TechType tech, Unit targetUnit, boolean checkCanTargetUnit, boolean checkTargetsUnits, boolean checkCanIssueCommandType, boolean checkCommandibility);

    public boolean canUseTechPosition(TechType tech, boolean checkCanIssueCommandType);

    public boolean canUseTechPosition(TechType tech);

    public boolean canUseTechPosition(TechType tech, boolean checkCanIssueCommandType, boolean checkCommandibility);

    public boolean canUseTechPosition(TechType tech, Position target, boolean checkTargetsPositions, boolean checkCanIssueCommandType);

    public boolean canUseTechPosition(TechType tech, Position target, boolean checkTargetsPositions);

    public boolean canUseTechPosition(TechType tech, Position target);

    public boolean canUseTechPosition(TechType tech, Position target, boolean checkTargetsPositions, boolean checkCanIssueCommandType, boolean checkCommandibility);

    public boolean canPlaceCOP();

    public boolean canPlaceCOP(boolean checkCommandibility);

    public boolean canPlaceCOP(TilePosition target, boolean checkCanIssueCommandType);

    public boolean canPlaceCOP(TilePosition target);

    public boolean canPlaceCOP(TilePosition target, boolean checkCanIssueCommandType, boolean checkCommandibility);

    */

    public boolean equals(Object that){
        if(!(that instanceof Unit)){
            return false;
        }
        return getID() == ((Unit)that).getID();
    }

    public int hashCode(){
        return getID();
    }

}
