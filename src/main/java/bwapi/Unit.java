package bwapi;


import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static bwapi.Order.*;
import static bwapi.Race.Terran;
import static bwapi.Race.Zerg;
import static bwapi.UnitType.*;

public class Unit {
    private static Set<Order> gatheringGasOrders = new HashSet<>(Arrays.asList(
            Harvest1, Harvest2, MoveToGas, WaitForGas, HarvestGas, ReturnGas, ResetCollision));
    private static Set<Order> gatheringMineralOrders = new HashSet<>(Arrays.asList(
            Harvest1, Harvest2, MoveToMinerals, WaitForMinerals, MiningMinerals, ReturnMinerals, ResetCollision));
    private final Client.GameData.UnitData unitData;
    private final Game game;
    // static
    private final UnitType initialType;
    private final int initialResources;
    private final int initialHitPoints;
    private final Position initialPosition;
    private final TilePosition initialTilePosition;
    private final int id;
    private final int replayID;
    // variable
    private Player player;
    private UnitType unitType;
    private Position position;
    private int lastPositionUpdate = -1;
    private int lastTypeUpdate = -1;
    private int lastPlayerUpdate = -1;
    private int lastCommandFrame = 0;
    private UnitCommand lastCommand;


    Unit(final Client.GameData.UnitData unitData, final Game game) {
        this.unitData = unitData;
        this.game = game;

        updateType(0);
        updatePlayer(0);
        updatePosition(0);

        initialType = getType();
        initialResources = getResources();
        initialHitPoints = getHitPoints();
        initialPosition = getPosition();
        initialTilePosition = getTilePosition();

        id = unitData.id();
        replayID = unitData.replayID();
    }

    private static boolean reallyGatheringGas(final Unit targ, final Player player) {
        return targ != null && targ.exists() && targ.isCompleted() && targ.getPlayer() == player &&
                targ.getType() != Resource_Vespene_Geyser && (targ.getType().isRefinery() || targ.getType().isResourceDepot());
    }

    private static boolean reallyGatheringMinerals(final Unit targ, final Player player) {
        return targ != null && targ.exists() && (targ.getType().isMineralField() ||
                (targ.isCompleted() && targ.getPlayer() == player && targ.getType().isResourceDepot()));
    }

    public int getID() {
        return id;
    }

    public boolean exists() {
        return unitData.exists();
    }

    public int getReplayID() {
        return replayID;
    }

    public Player getPlayer() {
        return player;
    }

    public UnitType getType() {
        return unitType;
    }

    public Position getPosition() {
        return position;
    }

    public TilePosition getTilePosition() {
        final Position p = getPosition();
        final UnitType ut = getType();
        return new Position(Math.abs(p.x - ut.tileWidth() * 32 / 2), Math.abs(p.y - ut.tileHeight() * 32 / 2))
                .toTilePosition();
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

    public int getLastCommandFrame() {
        return lastCommandFrame;
    }

    public UnitCommand getLastCommand() {
        return lastCommand;
    }

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
        return initialTilePosition;
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

    public Set<Unit> getLoadedUnits() {
        if (getType().spaceProvided() < 1) {
            return new HashSet<>();
        }
        return game.getAllUnits().stream()
                .filter(u -> equals(u.getTransport()))
                .collect(Collectors.toSet());
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
        return game.getAllUnits().stream()
                .filter(u -> equals(u.getCarrier()))
                .collect(Collectors.toSet());
    }

    public Unit getHatchery() {
        return game.getUnit(unitData.hatchery());
    }

    public Set<Unit> getLarva() {
        if (!getType().producesLarva()) {
            return new HashSet<>();
        }
        return game.getAllUnits().stream()
                .filter(u -> equals(u.getHatchery()))
                .collect(Collectors.toSet());
    }

    public Set<Unit> getUnitsInRadius(final int radius) {
        if (!exists()) {
            return new HashSet<>();
        }
        return game.getUnitsInRectangle(
                getLeft() - radius,
                getTop() - radius,
                getRight() + radius,
                getBottom() + radius,
                (u -> getDistance(u) <= radius));
    }

    public Set<Unit> getUnitsInWeaponRange(final WeaponType weapon) {
        // Return if this unit does not exist
        if (!exists()) {
            return new HashSet<>();
        }

        int max = getPlayer().weaponMaxRange(weapon);

        return game.getUnitsInRectangle(
                getLeft() - max,
                getTop() - max,
                getRight() + max,
                getBottom() + max,
                (u -> {
                    // Unit check and unit status
                    if (u == this || u.isInvincible()) {
                        return false;
                    }

                    // Weapon distance check
                    final int dist = getDistance(u);
                    if ((weapon.minRange() != 0 && dist < weapon.minRange()) || dist > max) {
                        return false;
                    }

                    // Weapon behavioural checks
                    final UnitType ut = u.getType();
                    return (!weapon.targetsOwn() || u.getPlayer().equals(getPlayer())) &&
                            (weapon.targetsAir() || u.isFlying()) &&
                            (weapon.targetsGround() || !u.isFlying()) &&
                            (!weapon.targetsMechanical() || !ut.isMechanical()) &&
                            (!weapon.targetsOrganic() || !ut.isOrganic()) &&
                            (!weapon.targetsNonBuilding() || ut.isBuilding()) &&
                            (!weapon.targetsNonRobotic() || ut.isRobotic()) &&
                            (!weapon.targetsOrgOrMech() || (!ut.isOrganic() && !ut.isMechanical()));
                }));
    }

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
        if (getType().getRace() != Terran) {
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

    public boolean isCarrying() {
        return isCarryingGas() || isCarryingMinerals();
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
        if (!exists() || target == null || !target.exists() || this == target) {
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
    }

    ;

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
        return t == Terran_Siege_Tank_Siege_Mode || t == Hero_Edmund_Duke_Siege_Mode;
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

    public boolean isTargetable() {
        if (!exists()) {
            return false;
        }
        final UnitType ut = getType();
        if (!isCompleted() &&
                !ut.isBuilding() &&
                !isMorphing() &&
                ut != Protoss_Archon &&
                ut != Protoss_Dark_Archon) {
            return false;

        }
        return ut != Spell_Scanner_Sweep &&
                ut != Spell_Dark_Swarm &&
                ut != Spell_Disruption_Web &&
                ut != Special_Map_Revealer;
    }


    public boolean issueCommand(final UnitCommand command) {
        if (!canIssueCommand(command)) {
            return false;
        }
        command.unit = this;

        // If using train or morph on a hatchery, automatically switch selection to larva
        // (assuming canIssueCommand ensures that there is a larva)
        if ((command.type == UnitCommandType.Train || command.type == UnitCommandType.Morph) &&
                getType().producesLarva() && command.getUnitType().whatBuilds().getKey() == UnitType.Zerg_Larva) {
            for (final Unit larva : getLarva()) {
                if (!larva.isConstructing() && larva.isCompleted() && larva.canCommand()) {
                    command.unit = larva;
                    break;
                }
            }
            if (command.unit == this) {
                return false;
            }
        }

        game.addUnitCommand(
                command.getType().id,
                command.getUnit().getID(),
                command.getTarget() != null ? command.getTarget().getID() : -1,
                command.x,
                command.y,
                command.extra
        );
        lastCommandFrame = game.getFrameCount();
        lastCommand = command;
        return true;
    }


    public boolean attack(final Position target) {
        return issueCommand(UnitCommand.attack(this, target));
    }

    public boolean attack(final Unit target) {
        return issueCommand(UnitCommand.attack(this, target));
    }

    public boolean attack(final Position target, final boolean shiftQueueCommand) {
        return issueCommand(UnitCommand.attack(this, target, shiftQueueCommand));
    }

    public boolean attack(final Unit target, final boolean shiftQueueCommand) {
        return issueCommand(UnitCommand.attack(this, target, shiftQueueCommand));
    }

    public boolean build(final UnitType type) {
        return issueCommand(UnitCommand.train(this, type));
    }

    public boolean build(final UnitType type, final TilePosition target) {
        return issueCommand(UnitCommand.build(this, target, type));
    }

    public boolean buildAddon(final UnitType type) {
        return issueCommand(UnitCommand.buildAddon(this, type));
    }

    public boolean train(final UnitType type) {
        return issueCommand(UnitCommand.train(this, type));
    }

    public boolean morph(final UnitType type) {
        return issueCommand(UnitCommand.morph(this, type));
    }

    public boolean research(final TechType tech) {
        return issueCommand(UnitCommand.research(this, tech));
    }

    public boolean upgrade(final UpgradeType upgrade) {
        return issueCommand(UnitCommand.upgrade(this, upgrade));
    }

    public boolean setRallyPoint(final Position target) {
        return issueCommand(UnitCommand.setRallyPoint(this, target));
    }

    public boolean setRallyPoint(final Unit target) {
        return issueCommand(UnitCommand.setRallyPoint(this, target));
    }

    public boolean move(final Position target) {
        return issueCommand(UnitCommand.move(this, target));
    }

    public boolean move(final Position target, final boolean shiftQueueCommand) {
        return issueCommand(UnitCommand.move(this, target, shiftQueueCommand));
    }

    public boolean patrol(final Position target) {
        return issueCommand(UnitCommand.patrol(this, target));
    }

    public boolean patrol(final Position target, final boolean shiftQueueCommand) {
        return issueCommand(UnitCommand.patrol(this, target, shiftQueueCommand));
    }

    public boolean holdPosition() {
        return issueCommand(UnitCommand.holdPosition(this));
    }

    public boolean holdPosition(final boolean shiftQueueCommand) {
        return issueCommand(UnitCommand.holdPosition(this, shiftQueueCommand));
    }

    public boolean stop() {
        return issueCommand(UnitCommand.stop(this));
    }

    public boolean stop(final boolean shiftQueueCommand) {
        return issueCommand(UnitCommand.stop(this, shiftQueueCommand));
    }

    public boolean follow(final Unit target) {
        return issueCommand(UnitCommand.follow(this, target));
    }

    public boolean follow(final Unit target, final boolean shiftQueueCommand) {
        return issueCommand(UnitCommand.follow(this, target, shiftQueueCommand));
    }

    public boolean gather(final Unit target) {
        return issueCommand(UnitCommand.gather(this, target));
    }

    public boolean gather(final Unit target, final boolean shiftQueueCommand) {
        return issueCommand(UnitCommand.gather(this, target, shiftQueueCommand));
    }

    public boolean returnCargo() {
        return issueCommand(UnitCommand.returnCargo(this));
    }

    public boolean returnCargo(final boolean shiftQueueCommand) {
        return issueCommand(UnitCommand.returnCargo(this, shiftQueueCommand));
    }

    public boolean repair(final Unit target) {
        return issueCommand(UnitCommand.repair(this, target));
    }

    public boolean repair(final Unit target, final boolean shiftQueueCommand) {
        return issueCommand(UnitCommand.repair(this, target, shiftQueueCommand));
    }

    public boolean burrow() {
        return issueCommand(UnitCommand.burrow(this));
    }

    public boolean unburrow() {
        return issueCommand(UnitCommand.unburrow(this));
    }

    public boolean cloak() {
        return issueCommand(UnitCommand.cloak(this));
    }

    public boolean decloak() {
        return issueCommand(UnitCommand.decloak(this));
    }

    public boolean siege() {
        return issueCommand(UnitCommand.siege(this));
    }

    public boolean unsiege() {
        return issueCommand(UnitCommand.unsiege(this));
    }

    public boolean lift() {
        return issueCommand(UnitCommand.lift(this));
    }

    public boolean land(final TilePosition target) {
        return issueCommand(UnitCommand.land(this, target));
    }

    public boolean load(final Unit target) {
        return issueCommand(UnitCommand.load(this, target));
    }

    public boolean load(final Unit target, final boolean shiftQueueCommand) {
        return issueCommand(UnitCommand.load(this, target, shiftQueueCommand));
    }

    public boolean unload(final Unit target) {
        return issueCommand(UnitCommand.unload(this, target));
    }

    public boolean unloadAll() {
        return issueCommand(UnitCommand.unloadAll(this));
    }

    public boolean unloadAll(final boolean shiftQueueCommand) {
        return issueCommand(UnitCommand.unloadAll(this, shiftQueueCommand));
    }

    public boolean unloadAll(final Position target) {
        return issueCommand(UnitCommand.unloadAll(this, target));
    }

    public boolean unloadAll(final Position target, final boolean shiftQueueCommand) {
        return issueCommand(UnitCommand.unloadAll(this, target, shiftQueueCommand));
    }

    public boolean rightClick(final Position target) {
        return issueCommand(UnitCommand.rightClick(this, target));
    }

    public boolean rightClick(final Unit target) {
        return issueCommand(UnitCommand.rightClick(this, target));
    }

    public boolean rightClick(final Position target, final boolean shiftQueueCommand) {
        return issueCommand(UnitCommand.rightClick(this, target, shiftQueueCommand));
    }

    public boolean rightClick(final Unit target, final boolean shiftQueueCommand) {
        return issueCommand(UnitCommand.rightClick(this, target, shiftQueueCommand));
    }

    public boolean haltConstruction() {
        return issueCommand(UnitCommand.haltConstruction(this));
    }

    public boolean cancelConstruction() {
        return issueCommand(UnitCommand.cancelConstruction(this));
    }

    public boolean cancelAddon() {
        return issueCommand(UnitCommand.cancelAddon(this));
    }

    public boolean cancelTrain() {
        return issueCommand(UnitCommand.cancelTrain(this));
    }

    public boolean cancelTrain(final int slot) {
        return issueCommand(UnitCommand.cancelTrain(this, slot));
    }

    public boolean cancelMorph() {
        return issueCommand(UnitCommand.cancelMorph(this));
    }

    public boolean cancelResearch() {
        return issueCommand(UnitCommand.cancelResearch(this));
    }

    public boolean cancelUpgrade() {
        return issueCommand(UnitCommand.cancelUpgrade(this));
    }

    public boolean useTech(final TechType tech) {
        return issueCommand(UnitCommand.useTech(this, tech));
    }

    public boolean useTech(final TechType tech, final Position target) {
        return issueCommand(UnitCommand.useTech(this, tech, target));
    }

    public boolean useTech(final TechType tech, final Unit target) {
        return issueCommand(UnitCommand.useTech(this, tech, target));
    }

    public boolean placeCOP(final TilePosition target) {
        return issueCommand(UnitCommand.placeCOP(this, target));
    }

    public boolean canIssueCommand(UnitCommand command, boolean checkCanUseTechPositionOnPositions, boolean checkCanUseTechUnitOnUnits, boolean checkCanBuildUnitType, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
        return canIssueCommand(command, checkCanUseTechPositionOnPositions, checkCanUseTechUnitOnUnits, checkCanBuildUnitType, checkCanTargetUnit, checkCanIssueCommandType, true);
    }

    public boolean canIssueCommand(UnitCommand command, boolean checkCanUseTechPositionOnPositions, boolean checkCanUseTechUnitOnUnits, boolean checkCanBuildUnitType, boolean checkCanTargetUnit) {
        return canIssueCommand(command, checkCanUseTechPositionOnPositions, checkCanUseTechUnitOnUnits, checkCanBuildUnitType, checkCanTargetUnit, true);
    }

    public boolean canIssueCommand(UnitCommand command, boolean checkCanUseTechPositionOnPositions, boolean checkCanUseTechUnitOnUnits, boolean checkCanBuildUnitType) {
        return canIssueCommand(command, checkCanUseTechPositionOnPositions, checkCanUseTechUnitOnUnits, checkCanBuildUnitType, true);
    }

    public boolean canIssueCommand(UnitCommand command, boolean checkCanUseTechPositionOnPositions, boolean checkCanUseTechUnitOnUnits) {
        return canIssueCommand(command, checkCanUseTechPositionOnPositions, checkCanUseTechUnitOnUnits, true);
    }

    public boolean canIssueCommand(UnitCommand command, boolean checkCanUseTechPositionOnPositions) {
        return canIssueCommand(command, checkCanUseTechPositionOnPositions, true);
    }

    public boolean canIssueCommand(UnitCommand command) {
        return canIssueCommand(command, true);
    }

    public boolean canIssueCommand(UnitCommand command, boolean checkCanUseTechPositionOnPositions, boolean checkCanUseTechUnitOnUnits, boolean checkCanBuildUnitType, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        final UnitCommandType ct = command.type;
        if (checkCanIssueCommandType && !canIssueCommandType(ct, false)) {
            return false;
        }

        switch (ct) {
            case Attack_Move:
                return true;
            case Attack_Unit:
                return canAttackUnit(command.target, checkCanTargetUnit, false, false);
            case Build:
                return canBuild(command.getUnitType(), new TilePosition(command.x, command.y), checkCanBuildUnitType, false, false);
            case Build_Addon:
                return canBuildAddon(command.getUnitType(), false, false);
            case Train:
                return canTrain(command.getUnitType(), false, false);
            case Morph:
                return canMorph(command.getUnitType(), false, false);
            case Research:
                return game.canResearch(command.getTechType(), this, false);
            case Upgrade:
                return game.canUpgrade(command.getUpgradeType(), this, false);
            case Set_Rally_Position:
                return true;
            case Set_Rally_Unit:
                return canSetRallyUnit(command.target, checkCanTargetUnit, false, false);
            case Move:
                return true;
            case Patrol:
                return true;
            case Hold_Position:
                return true;
            case Stop:
                return true;
            case Follow:
                return canFollow(command.target, checkCanTargetUnit, false, false);
            case Gather:
                return canGather(command.target, checkCanTargetUnit, false, false);
            case Return_Cargo:
                return true;
            case Repair:
                return canRepair(command.target, checkCanTargetUnit, false, false);
            case Burrow:
                return true;
            case Unburrow:
                return true;
            case Cloak:
                return true;
            case Decloak:
                return true;
            case Siege:
                return true;
            case Unsiege:
                return true;
            case Lift:
                return true;
            case Land:
                return canLand(new TilePosition(command.x, command.y), false, false);
            case Load:
                return canLoad(command.target, checkCanTargetUnit, false, false);
            case Unload:
                return canUnload(command.target, checkCanTargetUnit, false, false, false);
            case Unload_All:
                return true;
            case Unload_All_Position:
                return canUnloadAllPosition(command.getTargetPosition(), false, false);
            case Right_Click_Position:
                return true;
            case Right_Click_Unit:
                return canRightClickUnit(command.target, checkCanTargetUnit, false, false);
            case Halt_Construction:
                return true;
            case Cancel_Construction:
                return true;
            case Cancel_Addon:
                return true;
            case Cancel_Train:
                return true;
            case Cancel_Train_Slot:
                return canCancelTrainSlot(command.extra, false, false);
            case Cancel_Morph:
                return true;
            case Cancel_Research:
                return true;
            case Cancel_Upgrade:
                return true;
            case Use_Tech:
                return canUseTechWithoutTarget(TechType.techTypes[command.extra], false, false);
            case Use_Tech_Unit:
                return canUseTechUnit(TechType.techTypes[command.extra], command.target, checkCanTargetUnit, checkCanUseTechUnitOnUnits, false, false);
            case Use_Tech_Position:
                return canUseTechPosition(TechType.techTypes[command.extra], command.getTargetPosition(), checkCanUseTechPositionOnPositions, false, false);
            case Place_COP:
                return canPlaceCOP(new TilePosition(command.x, command.y), false, false);
        }
        return true;
    }


    public boolean canIssueCommandGrouped(UnitCommand command, boolean checkCanUseTechPositionOnPositions, boolean checkCanUseTechUnitOnUnits, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped) {
        return canIssueCommandGrouped(command, checkCanUseTechPositionOnPositions, checkCanUseTechUnitOnUnits, checkCanTargetUnit, checkCanIssueCommandType, checkCommandibilityGrouped, true);
    }

    public boolean canIssueCommandGrouped(UnitCommand command, boolean checkCanUseTechPositionOnPositions, boolean checkCanUseTechUnitOnUnits, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
        return canIssueCommandGrouped(command, checkCanUseTechPositionOnPositions, checkCanUseTechUnitOnUnits, checkCanTargetUnit, checkCanIssueCommandType, true);

    }

    public boolean canIssueCommandGrouped(UnitCommand command, boolean checkCanUseTechPositionOnPositions, boolean checkCanUseTechUnitOnUnits, boolean checkCanTargetUnit) {
        return canIssueCommandGrouped(command, checkCanUseTechPositionOnPositions, checkCanUseTechUnitOnUnits, checkCanTargetUnit, true);

    }

    public boolean canIssueCommandGrouped(UnitCommand command, boolean checkCanUseTechPositionOnPositions, boolean checkCanUseTechUnitOnUnits) {
        return canIssueCommandGrouped(command, checkCanUseTechPositionOnPositions, checkCanUseTechUnitOnUnits, true);
    }

    public boolean canIssueCommandGrouped(UnitCommand command, boolean checkCanUseTechPositionOnPositions) {
        return canIssueCommandGrouped(command, checkCanUseTechPositionOnPositions, true);
    }

    public boolean canIssueCommandGrouped(UnitCommand command) {
        return canIssueCommandGrouped(command, true);
    }

    public boolean canIssueCommandGrouped(UnitCommand command, boolean checkCanUseTechPositionOnPositions, boolean checkCanUseTechUnitOnUnits, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (checkCommandibilityGrouped && !canCommandGrouped(false)) {
            return false;
        }

        final UnitCommandType ct = command.type;
        if (checkCanIssueCommandType && !canIssueCommandTypeGrouped(ct, false, false)) {
            return false;
        }

        switch (ct) {
            case Attack_Move:
                return true;
            case Attack_Unit:
                return canAttackUnitGrouped(command.target, checkCanTargetUnit, false, false, false);
            case Build:
                return false;
            case Build_Addon:
                return false;
            case Train:
                return canTrain(command.getUnitType(), false, false);
            case Morph:
                return canMorph(command.getUnitType(), false, false);
            case Research:
                return false;
            case Upgrade:
                return false;
            case Set_Rally_Position:
                return false;
            case Set_Rally_Unit:
                return false;
            case Move:
                return true;
            case Patrol:
                return true;
            case Hold_Position:
                return true;
            case Stop:
                return true;
            case Follow:
                return canFollow(command.target, checkCanTargetUnit, false, false);
            case Gather:
                return canGather(command.target, checkCanTargetUnit, false, false);
            case Return_Cargo:
                return true;
            case Repair:
                return canRepair(command.target, checkCanTargetUnit, false, false);
            case Burrow:
                return true;
            case Unburrow:
                return true;
            case Cloak:
                return true;
            case Decloak:
                return true;
            case Siege:
                return true;
            case Unsiege:
                return true;
            case Lift:
                return false;
            case Land:
                return false;
            case Load:
                return canLoad(command.target, checkCanTargetUnit, false, false);
            case Unload:
                return false;
            case Unload_All:
                return false;
            case Unload_All_Position:
                return canUnloadAllPosition(command.getTargetPosition(), false, false);
            case Right_Click_Position:
                return true;
            case Right_Click_Unit:
                return canRightClickUnitGrouped(command.target, checkCanTargetUnit, false, false, false);
            case Halt_Construction:
                return true;
            case Cancel_Construction:
                return false;
            case Cancel_Addon:
                return false;
            case Cancel_Train:
                return false;
            case Cancel_Train_Slot:
                return false;
            case Cancel_Morph:
                return true;
            case Cancel_Research:
                return false;
            case Cancel_Upgrade:
                return false;
            case Use_Tech:
                return canUseTechWithoutTarget(TechType.techTypes[command.extra], false, false);
            case Use_Tech_Unit:
                return canUseTechUnit(TechType.techTypes[command.extra], command.target, checkCanTargetUnit, checkCanUseTechUnitOnUnits, false, false);
            case Use_Tech_Position:
                return canUseTechPosition(TechType.techTypes[command.extra], command.getTargetPosition(), checkCanUseTechPositionOnPositions, false, false);
            case Place_COP:
                return false;
        }
        return true;
    }


    public boolean canCommand() {
        if (!exists() || !getPlayer().equals(game.self())) {
            return false;
        }

        // Global can be ordered check
        if (isLockedDown() || isMaelstrommed() || isStasised() ||
                !isPowered() || getOrder() == Order.ZergBirth || isLoaded()) {
            if (!getType().producesLarva()) {
                return false;
            } else {
                for (Unit larva : getLarva()) {
                    if (larva.canCommand()) {
                        return true;
                    }
                }
                return false;
            }
        }

        final UnitType uType = getType();
        if (uType == Protoss_Interceptor ||
                uType == Terran_Vulture_Spider_Mine ||
                uType == Spell_Scanner_Sweep ||
                uType == Special_Map_Revealer) {
            return false;
        }

        if (isCompleted() &&
                (uType == Protoss_Pylon ||
                        uType == Terran_Supply_Depot ||
                        uType.isResourceContainer() ||
                        uType == Protoss_Shield_Battery ||
                        uType == Terran_Nuclear_Missile ||
                        uType.isPowerup() ||
                        (uType.isSpecialBuilding() && !uType.isFlagBeacon()))) {
            return false;
        }
        return isCompleted() || uType.isBuilding() || isMorphing();
    }

    public boolean canCommandGrouped() {
        return canCommandGrouped(true);
    }

    public boolean canCommandGrouped(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        return !getType().isBuilding() && !getType().isCritter();
    }

    public boolean canIssueCommandType(UnitCommandType ct) {
        return canIssueCommandType(ct, true);
    }

    public boolean canIssueCommandType(UnitCommandType ct, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }
        switch (ct) {
            case Attack_Move:
                return canAttackMove(false);
            case Attack_Unit:
                return canAttackUnit(false);
            case Build:
                return canBuild(false);
            case Build_Addon:
                return canBuildAddon(false);
            case Train:
                return canTrain(false);
            case Morph:
                return canMorph(false);
            case Research:
                return canResearch(false);
            case Upgrade:
                return canUpgrade(false);
            case Set_Rally_Position:
                return canSetRallyPosition(false);
            case Set_Rally_Unit:
                return canSetRallyUnit(false);
            case Move:
                return canMove(false);
            case Patrol:
                return canPatrol(false);
            case Hold_Position:
                return canHoldPosition(false);
            case Stop:
                return canStop(false);
            case Follow:
                return canFollow(false);
            case Gather:
                return canGather(false);
            case Return_Cargo:
                return canReturnCargo(false);
            case Repair:
                return canRepair(false);
            case Burrow:
                return canBurrow(false);
            case Unburrow:
                return canUnburrow(false);
            case Cloak:
                return canCloak(false);
            case Decloak:
                return canDecloak(false);
            case Siege:
                return canSiege(false);
            case Unsiege:
                return canUnsiege(false);
            case Lift:
                return canLift(false);
            case Land:
                return canLand(false);
            case Load:
                return canLoad(false);
            case Unload:
                return canUnload(false);
            case Unload_All:
                return canUnloadAll(false);
            case Unload_All_Position:
                return canUnloadAllPosition(false);
            case Right_Click_Position:
                return canRightClickPosition(false);
            case Right_Click_Unit:
                return canRightClickUnit(false);
            case Halt_Construction:
                return canHaltConstruction(false);
            case Cancel_Construction:
                return canCancelConstruction(false);
            case Cancel_Addon:
                return canCancelAddon(false);
            case Cancel_Train:
                return canCancelTrain(false);
            case Cancel_Train_Slot:
                return canCancelTrainSlot(false);
            case Cancel_Morph:
                return canCancelMorph(false);
            case Cancel_Research:
                return canCancelResearch(false);
            case Cancel_Upgrade:
                return canCancelUpgrade(false);
            case Use_Tech:
            case Use_Tech_Unit:
            case Use_Tech_Position:
                return canUseTechWithOrWithoutTarget(false);
            case Place_COP:
                return canPlaceCOP(false);
        }

        return true;
    }

    public boolean canIssueCommandTypeGrouped(UnitCommandType ct, boolean checkCommandibilityGrouped) {
        return canIssueCommandTypeGrouped(ct, checkCommandibilityGrouped, true);
    }

    public boolean canIssueCommandTypeGrouped(UnitCommandType ct) {
        return canIssueCommandTypeGrouped(ct, true);
    }

    public boolean canIssueCommandTypeGrouped(UnitCommandType ct, boolean checkCommandibilityGrouped, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (checkCommandibilityGrouped && !canCommandGrouped(false)) {
            return false;
        }

        switch (ct) {
            case Attack_Move:
                return canAttackMoveGrouped(false, false);
            case Attack_Unit:
                return canAttackUnitGrouped(false, false);
            case Build:
                return false;
            case Build_Addon:
                return false;
            case Train:
                return canTrain(false);
            case Morph:
                return canMorph(false);
            case Research:
                return false;
            case Upgrade:
                return false;
            case Set_Rally_Position:
                return false;
            case Set_Rally_Unit:
                return false;
            case Move:
                return canMoveGrouped(false, false);
            case Patrol:
                return canPatrolGrouped(false, false);
            case Hold_Position:
                return canHoldPosition(false);
            case Stop:
                return canStop(false);
            case Follow:
                return canFollow(false);
            case Gather:
                return canGather(false);
            case Return_Cargo:
                return canReturnCargo(false);
            case Repair:
                return canRepair(false);
            case Burrow:
                return canBurrow(false);
            case Unburrow:
                return canUnburrow(false);
            case Cloak:
                return canCloak(false);
            case Decloak:
                return canDecloak(false);
            case Siege:
                return canSiege(false);
            case Unsiege:
                return canUnsiege(false);
            case Lift:
                return false;
            case Land:
                return false;
            case Load:
                return canLoad(false);
            case Unload:
                return false;
            case Unload_All:
                return false;
            case Unload_All_Position:
                return canUnloadAllPosition(false);
            case Right_Click_Position:
                return canRightClickPositionGrouped(false, false);
            case Right_Click_Unit:
                return canRightClickUnitGrouped(false, false);
            case Halt_Construction:
                return canHaltConstruction(false);
            case Cancel_Construction:
                return false;
            case Cancel_Addon:
                return false;
            case Cancel_Train:
                return false;
            case Cancel_Train_Slot:
                return false;
            case Cancel_Morph:
                return canCancelMorph(false);
            case Cancel_Research:
                return false;
            case Cancel_Upgrade:
                return false;
            case Use_Tech:
            case Use_Tech_Unit:
            case Use_Tech_Position:
                return canUseTechWithOrWithoutTarget(false);
            case Place_COP:
                return false;
        }
        return true;
    }

    public boolean canTargetUnit(Unit targetUnit) {
        if (targetUnit == null || !targetUnit.exists()) {
            return false;
        }
        final UnitType targetType = targetUnit.getType();
        if (!targetUnit.isCompleted() && !targetType.isBuilding() && !targetUnit.isMorphing() &&
                targetType != Protoss_Archon && targetType != Protoss_Dark_Archon) {
            return false;
        }
        return targetType != Spell_Scanner_Sweep &&
                targetType != Spell_Dark_Swarm &&
                targetType != Spell_Disruption_Web &&
                targetType != Special_Map_Revealer;
    }

    public boolean canTargetUnit(Unit targetUnit, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }
        return canTargetUnit(targetUnit);
    }

    public boolean canAttack() {
        return canAttack(true);
    }

    public boolean canAttack(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        return canAttackMove(false) || canAttackUnit(false);
    }

    public boolean canAttack(Position target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
        return canAttack(target, checkCanTargetUnit, checkCanIssueCommandType, true);
    }

    public boolean canAttack(Unit target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
        return canAttack(target, checkCanTargetUnit, checkCanIssueCommandType, true);
    }

    public boolean canAttack(Position target, boolean checkCanTargetUnit) {
        return canAttack(target, checkCanTargetUnit, true);
    }

    public boolean canAttack(Unit target, boolean checkCanTargetUnit) {
        return canAttack(target, checkCanTargetUnit, true);
    }

    public boolean canAttack(Position target) {
        return canAttack(target, true);
    }

    public boolean canAttack(Unit target) {
        return canAttack(target, true);
    }

    public boolean canAttack(Position target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        return canAttackMove(false);
    }

    public boolean canAttack(Unit target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (target == null) {
            return false;
        }
        return canAttackUnit(target, checkCanTargetUnit, checkCanIssueCommandType, false);
    }

    public boolean canAttackGrouped(boolean checkCommandibilityGrouped) {
        return canAttackGrouped(checkCommandibilityGrouped, true);

    }

    public boolean canAttackGrouped() {
        return canAttackGrouped(true);
    }

    public boolean canAttackGrouped(boolean checkCommandibilityGrouped, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (checkCommandibilityGrouped && !canCommandGrouped(false)) {
            return false;
        }

        return canAttackMoveGrouped(false, false) || canAttackUnitGrouped(false, false);
    }

    public boolean canAttackGrouped(Position target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped) {
        return canAttackGrouped(target, checkCanTargetUnit, checkCanIssueCommandType, checkCommandibilityGrouped, true);
    }

    public boolean canAttackGrouped(Unit target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped) {
        return canAttackGrouped(target, checkCanTargetUnit, checkCanIssueCommandType, checkCommandibilityGrouped, true);
    }

    public boolean canAttackGrouped(Position target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
        return canAttackGrouped(target, checkCanTargetUnit, checkCanIssueCommandType, true);
    }

    public boolean canAttackGrouped(Unit target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
        return canAttackGrouped(target, checkCanTargetUnit, checkCanIssueCommandType, true);
    }

    public boolean canAttackGrouped(Position target, boolean checkCanTargetUnit) {
        return canAttackGrouped(target, checkCanTargetUnit, true);
    }

    public boolean canAttackGrouped(Unit target, boolean checkCanTargetUnit) {
        return canAttackGrouped(target, checkCanTargetUnit, true);
    }

    public boolean canAttackGrouped(Position target) {
        return canAttackGrouped(target, true);
    }

    public boolean canAttackGrouped(Unit target) {
        return canAttackGrouped(target, true);
    }

    public boolean canAttackGrouped(Position target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (checkCommandibilityGrouped && !canCommandGrouped(false)) {
            return false;
        }
        return canAttackMoveGrouped(false, false) || canAttackUnitGrouped(false, false);
    }

    public boolean canAttackGrouped(Unit target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (checkCommandibilityGrouped && !canCommandGrouped(false)) {
            return false;
        }

        if (target == null) {
            return false;
        }
        return canAttackUnitGrouped(target, checkCanTargetUnit, checkCanIssueCommandType, false, false);
    }

    public boolean canAttackMove() {
        return canAttackMove(true);
    }

    public boolean canAttackMove(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if ((getType() != Terran_Medic && !canAttackUnit(false)) || !canMove(false))
            return false;

        return true;
    }

    public boolean canAttackMoveGrouped(boolean checkCommandibilityGrouped) {
        return canAttackMoveGrouped(checkCommandibilityGrouped, true);
    }

    public boolean canAttackMoveGrouped() {
        return canAttackMoveGrouped(true);
    }

    public boolean canAttackMoveGrouped(boolean checkCommandibilityGrouped, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (checkCommandibilityGrouped && !canCommandGrouped(false)) {
            return false;
        }
        final UnitType ut = getType();
        return ut.canMove() || ut == Terran_Siege_Tank_Siege_Mode || ut == Zerg_Cocoon || ut == Zerg_Lurker_Egg;
    }

    public boolean canAttackUnit() {
        return canAttackUnit(true);
    }

    public boolean canAttackUnit(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        final UnitType ut = getType();
        if (!ut.isBuilding() && !isInterruptible()) {
            return false;

        }
        if (ut.groundWeapon() == WeaponType.None && ut.airWeapon() == WeaponType.None) {
            if (ut == Protoss_Carrier || ut == Hero_Gantrithor) {
                if (getInterceptorCount() <= 0) {
                    return false;
                }
            } else if (ut == Protoss_Reaver || ut == Hero_Warbringer) {
                if (getScarabCount() <= 0) {
                    return false;
                }
            } else
                return false;
        }
        if (ut == Zerg_Lurker) {
            if (!isBurrowed()) {
                return false;
            }
        } else if (isBurrowed()) {
            return false;
        }
        if (!isCompleted()) {
            return false;
        }
        return getOrder() != Order.ConstructingBuilding;
    }

    public boolean canAttackUnit(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
        return canAttackUnit(targetUnit, checkCanTargetUnit, checkCanIssueCommandType, true);
    }

    public boolean canAttackUnit(Unit targetUnit, boolean checkCanTargetUnit) {
        return canAttackUnit(targetUnit, checkCanTargetUnit, true);
    }

    public boolean canAttackUnit(Unit targetUnit) {
        return canAttackUnit(targetUnit, true);
    }

    public boolean canAttackUnit(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (checkCanIssueCommandType && !canAttackUnit(false)) {
            return false;
        }

        if (checkCanTargetUnit && !canTargetUnit(targetUnit, false)) {
            return false;
        }
        if (targetUnit.isInvincible()) {
            return false;
        }

        final UnitType type = getType();
        final boolean targetInAir = targetUnit.isFlying();
        WeaponType weapon = targetInAir ? type.airWeapon() : type.groundWeapon();

        if (weapon == WeaponType.None) {
            switch (type) {
                case Protoss_Carrier:
                case Hero_Gantrithor:
                    break;
                case Protoss_Reaver:
                case Hero_Warbringer:
                    if (targetInAir) {
                        return false;
                    }
                    break;
                default:
                    return false;
            }
        }

        if (!type.canMove() && !isInWeaponRange(targetUnit))
            return false;

        if (type == Zerg_Lurker && !isInWeaponRange(targetUnit))
            return false;

        return !equals(targetUnit);
    }

    public boolean canAttackUnitGrouped(boolean checkCommandibilityGrouped) {
        return canAttackUnitGrouped(checkCommandibilityGrouped, true);
    }

    public boolean canAttackUnitGrouped() {
        return canAttackUnitGrouped(true);
    }

    public boolean canAttackUnitGrouped(boolean checkCommandibilityGrouped, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (checkCommandibilityGrouped && !canCommandGrouped(false)) {
            return false;
        }
        if (!isInterruptible()) {
            return false;
        }
        final UnitType ut = getType();
        if (!ut.canMove() && ut != Terran_Siege_Tank_Siege_Mode) {
            return false;
        }
        if (!isCompleted()) {
            return false;
        }
        if (getType() == Zerg_Lurker) {
            if (!isBurrowed()) {
                return false;
            }
        } else if (isBurrowed()) {
            return false;
        }
        return getOrder() != ConstructingBuilding;
    }

    public boolean canAttackUnitGrouped(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped) {
        return canAttackUnitGrouped(targetUnit, checkCanTargetUnit, checkCanIssueCommandType, checkCommandibilityGrouped, true);
    }

    public boolean canAttackUnitGrouped(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
        return canAttackUnitGrouped(targetUnit, checkCanTargetUnit, checkCanIssueCommandType, true);
    }

    public boolean canAttackUnitGrouped(Unit targetUnit, boolean checkCanTargetUnit) {
        return canAttackUnitGrouped(targetUnit, checkCanTargetUnit, true);
    }

    public boolean canAttackUnitGrouped(Unit targetUnit) {
        return canAttackUnitGrouped(targetUnit, true);
    }

    public boolean canAttackUnitGrouped(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandTypeGrouped, boolean checkCommandibilityGrouped, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (checkCommandibilityGrouped && !canCommandGrouped(false)) {
            return false;
        }

        if (checkCanIssueCommandTypeGrouped && !canAttackUnitGrouped(false, false)) {
            return false;
        }

        if (checkCanTargetUnit && !canTargetUnit(targetUnit, false)) {
            return false;
        }

        if (isInvincible()) {
            return false;
        }

        final UnitType ut = getType();
        if (ut == Zerg_Lurker && !isInWeaponRange(targetUnit)) {
            return false;
        }

        if (ut == Zerg_Queen &&
                (targetUnit.getType() != Terran_Command_Center ||
                        targetUnit.getHitPoints() >= 750 || targetUnit.getHitPoints() <= 0)) {
            return false;
        }

        return !equals(targetUnit);
    }

    public boolean canBuild() {
        return canBuild(true);
    }

    public boolean canBuild(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        final UnitType ut = getType();
        if (!ut.isBuilding() && !isInterruptible()) {
            return false;
        }
        if (isConstructing() ||
                !isCompleted() ||
                (ut.isBuilding() && !isIdle())) {
            return false;
        }
        return !isHallucination();
    }

    public boolean canBuild(UnitType uType, boolean checkCanIssueCommandType) {
        return canBuild(uType, checkCanIssueCommandType, true);
    }

    public boolean canBuild(UnitType uType) {
        return canBuild(uType, true);
    }

    public boolean canBuild(UnitType uType, boolean checkCanIssueCommandType, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (checkCanIssueCommandType && !canBuild(false)) {
            return false;
        }
        if (!game.canMake(uType, this)) {
            return false;
        }

        if (!uType.isBuilding()) {
            return false;
        }
        return getAddon() == null;
    }

    public boolean canBuild(UnitType uType, TilePosition tilePos, boolean checkTargetUnitType, boolean checkCanIssueCommandType) {
        return canBuild(uType, tilePos, checkTargetUnitType, checkCanIssueCommandType, true);
    }

    public boolean canBuild(UnitType uType, TilePosition tilePos, boolean checkTargetUnitType) {
        return canBuild(uType, tilePos, checkTargetUnitType, true);
    }

    public boolean canBuild(UnitType uType, TilePosition tilePos) {
        return canBuild(uType, tilePos, true);
    }

    public boolean canBuild(UnitType uType, TilePosition tilePos, boolean checkTargetUnitType, boolean checkCanIssueCommandType, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (checkCanIssueCommandType && !canBuild(false)) {
            return false;
        }

        if (checkTargetUnitType && !canBuild(uType, false, false)) {
            return false;
        }

        if (!tilePos.isValid(game)) {
            return false;
        }

        return game.canBuildHere(tilePos, uType, this, true);
    }

    public boolean canBuildAddon() {
        return canBuildAddon(true);
    }

    public boolean canBuildAddon(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (isConstructing() || !isCompleted() || isLifted() || (getType().isBuilding() && !isIdle())) {
            return false;
        }
        if (getAddon() != null) {
            return false;
        }
        return getType().canBuildAddon();
    }

    public boolean canBuildAddon(UnitType uType, boolean checkCanIssueCommandType) {
        return canBuildAddon(uType, checkCanIssueCommandType, true);
    }

    public boolean canBuildAddon(UnitType uType) {
        return canBuildAddon(uType, true);
    }

    public boolean canBuildAddon(UnitType uType, boolean checkCanIssueCommandType, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }
        if (checkCanIssueCommandType && !canBuildAddon(uType, false)) {
            return false;
        }
        if (!game.canMake(uType, this)) {
            return false;
        }
        if (!uType.isAddon()) {
            return false;
        }
        return game.canBuildHere(getTilePosition(), uType, this);
    }

    public boolean canTrain() {
        return canTrain(true);
    }

    public boolean canTrain(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }
        final UnitType ut = getType();
        if (ut.producesLarva()) {
            if (!isConstructing() && isCompleted()) {
                return true;
            }
            for (Unit larva : getLarva()) {
                if (!larva.isConstructing() && larva.isCompleted() && larva.canCommand()) {
                    return true;
                }
            }
            return false;
        }

        if (isConstructing() || !isCompleted() || isLifted()) {
            return false;
        }
        if (!ut.canProduce() &&
                ut != Terran_Nuclear_Silo &&
                ut != Zerg_Hydralisk &&
                ut != Zerg_Mutalisk &&
                ut != Zerg_Creep_Colony &&
                ut != Zerg_Spire &&
                ut != Zerg_Larva) {
            return false;
        }
        return !isHallucination();
    }

    public boolean canTrain(UnitType uType, boolean checkCanIssueCommandType) {
        return canTrain(uType, checkCanIssueCommandType, true);
    }

    public boolean canTrain(UnitType uType) {
        return canTrain(uType, true);
    }

    public boolean canTrain(UnitType uType, boolean checkCanIssueCommandType, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (checkCanIssueCommandType && !canTrain(false)) {
            return false;
        }

        Unit thisUnit = this;
        if (getType().producesLarva()) {
            if (uType.whatBuilds().getKey() == Zerg_Larva) {
                boolean foundCommandableLarva = false;
                for (Unit larva : getLarva()) {
                    if (larva.canTrain(true)) {
                        foundCommandableLarva = true;
                        thisUnit = larva;
                        break;
                    }
                }
                if (!foundCommandableLarva) {
                    return false;
                }
            } else if (isConstructing() || !isCompleted()) {
                return false;
            }
        }

        if (!game.canMake(uType, thisUnit)) {
            return false;
        }

        if (uType.isAddon() || (uType.isBuilding() && !thisUnit.getType().isBuilding())) {
            return false;
        }
        return uType != Zerg_Larva && uType != Zerg_Egg && uType != Zerg_Cocoon;
    }

    public boolean canMorph() {
        return canMorph(true);
    }

    public boolean canMorph(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        final UnitType ut = getType();
        if (ut.producesLarva()) {
            if (!isConstructing() && isCompleted() && (!ut.isBuilding() || isIdle())) {
                return true;
            }
            for (Unit larva : getLarva()) {
                if (!larva.isConstructing() && larva.isCompleted() && larva.canCommand()) {
                    return true;
                }
            }
            return false;
        }

        if (isConstructing() || !isCompleted() || (ut.isBuilding() && !isIdle())) {
            return false;
        }

        if (ut != Zerg_Hydralisk &&
                ut != Zerg_Mutalisk &&
                ut != Zerg_Creep_Colony &&
                ut != Zerg_Spire &&
                ut != Zerg_Hatchery &&
                ut != Zerg_Lair &&
                ut != Zerg_Hive &&
                ut != Zerg_Larva) {
            return false;
        }
        return !isHallucination();
    }

    public boolean canMorph(UnitType uType, boolean checkCanIssueCommandType) {
        return canMorph(uType, checkCanIssueCommandType, true);
    }

    public boolean canMorph(UnitType uType) {
        return canMorph(uType, true);
    }

    public boolean canMorph(UnitType uType, boolean checkCanIssueCommandType, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (checkCanIssueCommandType && !canMorph(false))
            return false;

        Unit thisUnit = this;
        if (getType().producesLarva()) {
            if (uType.whatBuilds().getKey() == Zerg_Larva) {
                boolean foundCommandableLarva = false;
                for (Unit larva : getLarva()) {
                    if (larva.canMorph(true)) {
                        foundCommandableLarva = true;
                        thisUnit = larva;
                        break;
                    }
                }
                if (!foundCommandableLarva) {
                    return false;
                }
            } else if (isConstructing() || !isCompleted() || (getType().isBuilding() && !isIdle())) {
                return false;
            }
        }

        if (!game.canMake(uType, thisUnit)) {
            return false;
        }
        return uType != Zerg_Larva && uType != Zerg_Egg && uType != Zerg_Cocoon;
    }

    public boolean canResearch() {
        return canResearch(true);
    }

    public boolean canResearch(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        return !isLifted() && isIdle() && isCompleted();
    }

    public boolean canResearch(TechType type) {
        return canResearch(type, true);
    }

    public boolean canResearch(TechType type, boolean checkCanIssueCommandType) {
        final Player self = game.self();
        if (!getPlayer().equals(self)) {
            return false;
        }

        if (!getType().isSuccessorOf(type.whatResearches())) {
            return false;
        }

        if (checkCanIssueCommandType && (isLifted() || !isIdle() || !isCompleted())) {
            return false;
        }

        if (self.isResearching(type)) {
            return false;
        }

        if (self.hasResearched(type)) {
            return false;
        }

        if (!self.isResearchAvailable(type)) {
            return false;
        }

        if (self.minerals() < type.mineralPrice()) {
            return false;
        }

        if (self.gas() < type.gasPrice()) {
            return false;
        }

        return self.hasUnitTypeRequirement(type.requiredUnit());
    }

    public boolean canUpgrade() {
        return canUpgrade(true);
    }

    public boolean canUpgrade(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        return !isLifted() && isIdle() && isCompleted();
    }

    public boolean canUpgrade(UpgradeType type) {
        return canUpgrade(type, true);
    }

    public boolean canUpgrade(UpgradeType type, boolean checkCanIssueCommandType) {
        final Player self = game.self();

        if (!getPlayer().equals(self)) {
            return false;
        }

        if (!getType().isSuccessorOf(type.whatUpgrades())) {
            return false;
        }

        if (checkCanIssueCommandType && (isLifted() || !isIdle() || !isCompleted()))
            return false;

        int nextLvl = self.getUpgradeLevel(type) + 1;

        if (!self.hasUnitTypeRequirement(type.whatUpgrades())) {
            return false;
        }

        if (!self.hasUnitTypeRequirement(type.whatsRequired(nextLvl))) {
            return false;
        }

        if (self.isUpgrading(type)) {
            return false;
        }

        if (self.getUpgradeLevel(type) >= self.getMaxUpgradeLevel(type)) {
            return false;
        }

        if (self.minerals() < type.mineralPrice(nextLvl)) {
            return false;
        }

        return self.gas() >= type.gasPrice(nextLvl);
    }

    public boolean canSetRallyPoint() {
        return canSetRallyPoint(true);
    }

    public boolean canSetRallyPoint(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        return canSetRallyPosition(false) || canSetRallyUnit(false);
    }

    public boolean canSetRallyPoint(Position target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
        return canSetRallyPoint(target, checkCanTargetUnit, checkCanIssueCommandType, true);
    }

    public boolean canSetRallyPoint(Unit target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
        return canSetRallyPoint(target, checkCanTargetUnit, checkCanIssueCommandType, true);
    }

    public boolean canSetRallyPoint(Position target, boolean checkCanTargetUnit) {
        return canSetRallyPoint(target, checkCanTargetUnit, true);
    }

    public boolean canSetRallyPoint(Unit target, boolean checkCanTargetUnit) {
        return canSetRallyPoint(target, checkCanTargetUnit, true);
    }

    public boolean canSetRallyPoint(Position target) {
        return canSetRallyPoint(target, true);
    }

    public boolean canSetRallyPoint(Unit target) {
        return canSetRallyPoint(target, true);
    }

    public boolean canSetRallyPoint(Position target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        return canSetRallyPosition(false);
    }

    public boolean canSetRallyPoint(Unit target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (target == null) {
            return false;
        }
        return canSetRallyUnit(target, checkCanTargetUnit, checkCanIssueCommandType, false);
    }

    public boolean canSetRallyPosition() {
        return canSetRallyPosition(true);
    }

    public boolean canSetRallyPosition(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (!getType().canProduce() || !getType().isBuilding()) {
            return false;
        }
        return !isLifted();
    }

    public boolean canSetRallyUnit() {
        return canSetRallyUnit(true);
    }

    public boolean canSetRallyUnit(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (!getType().canProduce() || !getType().isBuilding()) {
            return false;
        }
        return !isLifted();
    }

    public boolean canSetRallyUnit(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
        return canSetRallyUnit(targetUnit, checkCanTargetUnit, checkCanIssueCommandType, true);
    }

    public boolean canSetRallyUnit(Unit targetUnit, boolean checkCanTargetUnit) {
        return canSetRallyUnit(targetUnit, checkCanTargetUnit, true);
    }

    public boolean canSetRallyUnit(Unit targetUnit) {
        return canSetRallyUnit(targetUnit, true);
    }

    public boolean canSetRallyUnit(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (checkCanIssueCommandType && !canSetRallyUnit(false)) {
            return false;
        }

        return !checkCanTargetUnit || canTargetUnit(targetUnit, false);
    }

    public boolean canMove() {
        return canMove(true);
    }

    public boolean canMove(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }
        final UnitType ut = getType();
        if (!ut.isBuilding()) {
            if (!isInterruptible()) {
                return false;
            }
            if (!getType().canMove()) {
                return false;
            }
            if (isBurrowed()) {
                return false;
            }
            if (getOrder() == ConstructingBuilding) {
                return false;
            }
            if (ut == Zerg_Larva) {
                return false;
            }
        } else {
            if (!ut.isFlyingBuilding()) {
                return false;
            }
            if (!isLifted()) {
                return false;
            }
        }
        return isCompleted();
    }

    public boolean canMoveGrouped(boolean checkCommandibilityGrouped) {
        return canMoveGrouped(checkCommandibilityGrouped, true);
    }

    public boolean canMoveGrouped() {
        return canMoveGrouped(true);
    }

    public boolean canMoveGrouped(boolean checkCommandibilityGrouped, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (checkCommandibilityGrouped && !canCommandGrouped(false)) {
            return false;
        }

        if (!getType().canMove()) {
            return false;
        }
        return isCompleted() || isMorphing();
    }

    public boolean canPatrol() {
        return canPatrol(true);
    }

    public boolean canPatrol(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }
        return canMove(false);
    }

    public boolean canPatrolGrouped(boolean checkCommandibilityGrouped) {
        return canPatrolGrouped(checkCommandibilityGrouped, true);
    }

    public boolean canPatrolGrouped() {
        return canPatrolGrouped(true);
    }

    public boolean canPatrolGrouped(boolean checkCommandibilityGrouped, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (checkCommandibilityGrouped && !canCommandGrouped(false)) {
            return false;
        }

        return canMoveGrouped(false, false);
    }

    public boolean canFollow() {
        return canFollow(true);
    }

    public boolean canFollow(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        return canMove(false);
    }

    public boolean canFollow(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
        return canFollow(targetUnit, checkCanTargetUnit, checkCanIssueCommandType, true);
    }

    public boolean canFollow(Unit targetUnit, boolean checkCanTargetUnit) {
        return canFollow(targetUnit, checkCanTargetUnit, true);
    }

    public boolean canFollow(Unit targetUnit) {
        return canFollow(targetUnit, true);
    }

    public boolean canFollow(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (checkCanIssueCommandType && !canFollow(false)) {
            return false;
        }

        if (checkCanTargetUnit && !canTargetUnit(targetUnit, false)) {
            return false;
        }

        return targetUnit != this;
    }

    public boolean canGather() {
        return canGather(true);
    }

    public boolean canGather(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }
        final UnitType ut = getType();

        if (!ut.isBuilding() && !isInterruptible()) {
            return false;
        }
        if (!ut.isWorker()) {
            return false;
        }
        if (getPowerUp() != null) {
            return false;
        }
        if (isHallucination()) {
            return false;
        }
        if (isBurrowed()) {
            return false;
        }
        if (!isCompleted()) {
            return false;
        }
        return getOrder() != ConstructingBuilding;
    }

    public boolean canGather(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
        return canGather(targetUnit, checkCanTargetUnit, checkCanIssueCommandType, true);
    }

    public boolean canGather(Unit targetUnit, boolean checkCanTargetUnit) {
        return canGather(targetUnit, checkCanTargetUnit, true);
    }

    public boolean canGather(Unit targetUnit) {
        return canGather(targetUnit, true);
    }

    public boolean canGather(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (checkCanIssueCommandType && !canGather(false)) {
            return false;
        }

        if (checkCanTargetUnit && !canTargetUnit(targetUnit, false)) {
            return false;
        }

        final UnitType uType = targetUnit.getType();
        if (!uType.isResourceContainer() || uType == Resource_Vespene_Geyser) {
            return false;
        }

        if (!isCompleted()) {
            return false;
        }

        if (!hasPath(getPosition())) {
            return false;
        }

        return !uType.isRefinery() || getPlayer().equals(game.self());

    }

    public boolean canReturnCargo() {
        return canReturnCargo(true);
    }

    public boolean canReturnCargo(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }
        final UnitType ut = getType();

        if (!ut.isBuilding() && !isInterruptible()) {
            return false;
        }
        if (!ut.isWorker()) {
            return false;
        }
        if (!isCarryingGas() && !isCarryingMinerals()) {
            return false;
        }
        if (isBurrowed()) {
            return false;
        }
        return getOrder() != ConstructingBuilding;
    }

    public boolean canHoldPosition() {
        return canHoldPosition(true);
    }

    public boolean canHoldPosition(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        final UnitType ut = getType();

        if (!ut.isBuilding()) {
            if (!ut.canMove()) {
                return false;
            }
            if (isBurrowed() && ut != Zerg_Lurker) {
                return false;
            }
            if (getOrder() == ConstructingBuilding) {
                return false;
            }
            if (ut == Zerg_Larva) {
                return false;
            }
        } else {
            if (!ut.isFlyingBuilding()) {
                return false;
            }
            if (!isLifted()) {
                return false;
            }
        }

        return isCompleted();
    }

    public boolean canStop() {
        return canStop(true);
    }

    public boolean canStop(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        final UnitType ut = getType();

        if (!isCompleted()) {
            return false;
        }
        if (isBurrowed() && ut != Zerg_Lurker) {
            return false;
        }
        return !ut.isBuilding() || isLifted() ||
                ut == Protoss_Photon_Cannon ||
                ut == Zerg_Sunken_Colony ||
                ut == Zerg_Spore_Colony ||
                ut == Terran_Missile_Turret;
    }

    public boolean canRepair() {
        return canRepair(true);
    }

    public boolean canRepair(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (!isInterruptible()) {
            return false;
        }
        if (getType() != Terran_SCV) {
            return false;
        }
        if (!isCompleted()) {
            return false;
        }
        if (isHallucination()) {
            return false;
        }
        return getOrder() != ConstructingBuilding;
    }

    public boolean canRepair(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
        return canRepair(targetUnit, checkCanTargetUnit, checkCanIssueCommandType, true);
    }

    public boolean canRepair(Unit targetUnit, boolean checkCanTargetUnit) {
        return canRepair(targetUnit, checkCanTargetUnit, true);
    }

    public boolean canRepair(Unit targetUnit) {
        return canRepair(targetUnit, true);
    }

    public boolean canRepair(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (checkCanIssueCommandType && !canRepair(false))
            return false;

        if (checkCanTargetUnit && !canTargetUnit(targetUnit, false))
            return false;

        UnitType targType = targetUnit.getType();
        if (targType.getRace() != Terran || !targType.isMechanical()) {
            return false;
        }
        if (targetUnit.getHitPoints() == targType.maxHitPoints()) {
            return false;
        }
        if (!targetUnit.isCompleted()) {
            return false;
        }
        return targetUnit != this;
    }

    public boolean canBurrow() {
        return canBurrow(true);
    }

    public boolean canBurrow(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        return canUseTechWithoutTarget(TechType.Burrowing, true, false);
    }

    public boolean canUnburrow() {
        return canUnburrow(true);
    }

    public boolean canUnburrow(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (!getType().isBurrowable()) {
            return false;
        }
        return isBurrowed() && getOrder() != Unburrowing;
    }

    public boolean canCloak() {
        return canCloak(true);
    }

    public boolean canCloak(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        return canUseTechWithoutTarget(getType().cloakingTech(), true, false);
    }

    public boolean canDecloak() {
        return canDecloak(true);
    }

    public boolean canDecloak(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (getType().cloakingTech() == TechType.None) {
            return false;
        }
        return getSecondaryOrder() == Cloak;
    }

    public boolean canSiege() {
        return canSiege(true);
    }

    public boolean canSiege(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        return canUseTechWithoutTarget(TechType.Tank_Siege_Mode, true, false);
    }

    public boolean canUnsiege() {
        return canUnsiege(true);
    }

    public boolean canUnsiege(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (!isSieged()) {
            return false;
        }
        final Order order = getOrder();
        if (order == Sieging || order == Unsieging) {
            return false;
        }
        return !isHallucination();
    }

    public boolean canLift() {
        return canLift(true);
    }

    public boolean canLift(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (!getType().isFlyingBuilding()) {
            return false;
        }
        if (isLifted()) {
            return false;
        }
        if (!isCompleted()) {
            return false;
        }
        return isIdle();
    }

    public boolean canLand() {
        return canLand(true);
    }

    public boolean canLand(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (!getType().isFlyingBuilding()) {
            return false;
        }
        return isLifted();
    }

    public boolean canLand(TilePosition target, boolean checkCanIssueCommandType) {
        return canLand(target, checkCanIssueCommandType, true);
    }

    public boolean canLand(TilePosition target) {
        return canLand(target, true);
    }

    public boolean canLand(TilePosition target, boolean checkCanIssueCommandType, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (checkCanIssueCommandType && !canLand(checkCommandibility)) {
            return false;
        }

        return game.canBuildHere(target, getType(), null, true);
    }

    public boolean canLoad() {
        return canLoad(true);
    }

    public boolean canLoad(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        final UnitType ut = getType();

        if (!ut.isBuilding() && !isInterruptible()) {
            return false;
        }
        if (!isCompleted()) {
            return false;
        }
        if (ut == Zerg_Overlord && game.self().getUpgradeLevel(UpgradeType.Ventral_Sacs) == 0) {
            return false;
        }
        if (isBurrowed()) {
            return false;
        }
        if (getOrder() == ConstructingBuilding) {
            return false;
        }
        return ut != Zerg_Larva;
    }

    public boolean canLoad(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
        return canLoad(targetUnit, checkCanTargetUnit, checkCanIssueCommandType, true);
    }

    public boolean canLoad(Unit targetUnit, boolean checkCanTargetUnit) {
        return canLoad(targetUnit, checkCanTargetUnit, true);
    }

    public boolean canLoad(Unit targetUnit) {
        return canLoad(targetUnit, true);
    }

    public boolean canLoad(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (checkCanIssueCommandType && !canLoad(false)) {
            return false;
        }

        if (checkCanTargetUnit && !canTargetUnit(targetUnit, false)) {
            return false;
        }

        final Player self = game.self();
        //target must also be owned by self
        if (!targetUnit.getPlayer().equals(self)) {
            return false;
        }
        if (targetUnit.isLoaded() || !targetUnit.isCompleted()) {
            return false;
        }

        // verify upgrade for Zerg Overlord
        if (targetUnit.getType() == Zerg_Overlord && self.getUpgradeLevel(UpgradeType.Ventral_Sacs) == 0) {
            return false;
        }

        int thisUnitSpaceProvided = getType().spaceProvided();
        int targetSpaceProvided = targetUnit.getType().spaceProvided();
        if (thisUnitSpaceProvided <= 0 && targetSpaceProvided <= 0) {
            return false;
        }

        final Unit unitToBeLoaded = thisUnitSpaceProvided > 0 ? targetUnit : this;
        final UnitType unitToBeLoadedType = unitToBeLoaded.getType();
        if (!unitToBeLoadedType.canMove() || unitToBeLoadedType.isFlyer() || unitToBeLoadedType.spaceRequired() > 8) {
            return false;
        }
        if (!unitToBeLoaded.isCompleted()) {
            return false;
        }
        if (unitToBeLoaded.isBurrowed()) {
            return false;
        }

        final Unit unitThatLoads = thisUnitSpaceProvided > 0 ? this : targetUnit;
        final UnitType unitThatLoadsType = unitThatLoads.getType();
        if (unitThatLoads.isHallucination()) {
            return false;
        }

        if (unitThatLoadsType == Terran_Bunker) {
            if (!unitThatLoadsType.isOrganic() || unitThatLoadsType.getRace() != Terran) {
                return false;
            }
            if (!unitToBeLoaded.hasPath(unitThatLoads.getPosition())) {
                return false;
            }
        }

        int freeSpace = thisUnitSpaceProvided > 0 ? thisUnitSpaceProvided : targetSpaceProvided;
        for (Unit u : unitThatLoads.getLoadedUnits()) {
            final int requiredSpace = u.getType().spaceRequired();
            if (requiredSpace > 0 && requiredSpace < 8) {
                freeSpace -= requiredSpace;
            }
        }
        return unitToBeLoadedType.spaceRequired() <= freeSpace;
    }

    public boolean canUnloadWithOrWithoutTarget() {
        return canUnloadWithOrWithoutTarget(true);
    }

    public boolean canUnloadWithOrWithoutTarget(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        final UnitType ut = getType();
        if (!ut.isBuilding() && !isInterruptible()) {
            return false;
        }

        if (getLoadedUnits().size() == 0) {
            return false;
        }

        // Check overlord tech
        if (ut == Zerg_Overlord && game.self().getUpgradeLevel(UpgradeType.Ventral_Sacs) == 0) {
            return false;
        }

        return ut.spaceProvided() > 0;
    }

    public boolean canUnloadAtPosition(Position targDropPos, boolean checkCanIssueCommandType) {
        return canUnloadAtPosition(targDropPos, checkCanIssueCommandType, true);
    }

    public boolean canUnloadAtPosition(Position targDropPos) {
        return canUnloadAtPosition(targDropPos, true);
    }

    public boolean canUnloadAtPosition(Position targDropPos, boolean checkCanIssueCommandType, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (checkCanIssueCommandType && !canUnloadWithOrWithoutTarget(false)) {
            return false;
        }

        if (getType() != Terran_Bunker) {
            if (!new WalkPosition(targDropPos.x / 8, targDropPos.y / 8).isValid(game)) {
                return false;
            } else if (!game.isWalkable(targDropPos.x / 8, targDropPos.y / 8)) {
                return false;
            }
        }

        return true;
    }

    public boolean canUnload() {
        return canUnload(true);
    }

    public boolean canUnload(boolean checkCommandibility) {
        return canUnloadAtPosition(getPosition(), true, checkCommandibility);
    }

    public boolean canUnload(Unit targetUnit, boolean checkCanTargetUnit, boolean checkPosition, boolean checkCanIssueCommandType) {
        return canUnload(targetUnit, checkCanTargetUnit, checkPosition, checkCanIssueCommandType, true);
    }

    public boolean canUnload(Unit targetUnit, boolean checkCanTargetUnit, boolean checkPosition) {
        return canUnload(targetUnit, checkCanTargetUnit, checkPosition, true);
    }

    public boolean canUnload(Unit targetUnit, boolean checkCanTargetUnit) {
        return canUnload(targetUnit, checkCanTargetUnit, true);
    }

    public boolean canUnload(Unit targetUnit) {
        return canUnload(targetUnit, true);
    }

    public boolean canUnload(Unit targetUnit, boolean checkCanTargetUnit, boolean checkPosition, boolean checkCanIssueCommandType, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (checkCanIssueCommandType && !canUnloadWithOrWithoutTarget(false)) {
            return false;
        }

        if (checkPosition && !canUnloadAtPosition(getPosition(), false, false)) {
            return false;
        }

        if (checkCanTargetUnit && !canTargetUnit(targetUnit, false)) {
            return false;
        }

        if (!targetUnit.isLoaded()) {
            return false;
        }

        return equals(targetUnit.getTransport());
    }

    public boolean canUnloadAll() {
        return canUnloadAll(true);
    }

    public boolean canUnloadAll(boolean checkCommandibility) {
        return canUnloadAtPosition(getPosition(), true, checkCommandibility);
    }

    public boolean canUnloadAllPosition() {
        return canUnloadAllPosition(true);
    }

    public boolean canUnloadAllPosition(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (!canUnloadWithOrWithoutTarget(false)) {
            return false;
        }

        return getType() != Terran_Bunker;
    }

    public boolean canUnloadAllPosition(Position targDropPos, boolean checkCanIssueCommandType) {
        return canUnloadAllPosition(targDropPos, checkCanIssueCommandType, true);
    }

    public boolean canUnloadAllPosition(Position targDropPos) {
        return canUnloadAllPosition(targDropPos, true);
    }

    public boolean canUnloadAllPosition(Position targDropPos, boolean checkCanIssueCommandType, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (checkCanIssueCommandType && !canUnloadAllPosition(false)) {
            return false;
        }

        return canUnloadAtPosition(targDropPos, false, false);
    }

    public boolean canRightClick() {
        return canRightClick(true);
    }

    public boolean canRightClick(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }
        return canRightClickPosition(false) || canRightClickUnit(false);
    }

    public boolean canRightClick(Position target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
        return canRightClick(target, checkCanTargetUnit, checkCanIssueCommandType, true);
    }

    public boolean canRightClick(Unit target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
        return canRightClick(target, checkCanTargetUnit, checkCanIssueCommandType, true);
    }

    public boolean canRightClick(Position target, boolean checkCanTargetUnit) {
        return canRightClick(target, checkCanTargetUnit, true);
    }

    public boolean canRightClick(Unit target, boolean checkCanTargetUnit) {
        return canRightClick(target, checkCanTargetUnit, true);
    }

    public boolean canRightClick(Position target) {
        return canRightClick(target, true);
    }

    public boolean canRightClick(Unit target) {
        return canRightClick(target, true);
    }

    public boolean canRightClick(Position target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        return canRightClickPosition(false);
    }

    public boolean canRightClick(Unit target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (target == null) {
            return false;
        }
        return canRightClickUnit(target, checkCanTargetUnit, checkCanIssueCommandType, false);
    }

    public boolean canRightClickGrouped(boolean checkCommandibilityGrouped) {
        return canRightClickGrouped(checkCommandibilityGrouped, true);
    }

    public boolean canRightClickGrouped() {
        return canRightClickGrouped(true);
    }

    public boolean canRightClickGrouped(boolean checkCommandibilityGrouped, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (checkCommandibilityGrouped && !canCommandGrouped(false))
            return false;

        return canRightClickPositionGrouped(false, false) || canRightClickUnitGrouped(false, false);
    }

    public boolean canRightClickGrouped(Position target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped) {
        return canRightClickGrouped(target, checkCanTargetUnit, checkCanIssueCommandType, checkCommandibilityGrouped, true);
    }

    public boolean canRightClickGrouped(Unit target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped) {
        return canRightClickGrouped(target, checkCanTargetUnit, checkCanIssueCommandType, checkCommandibilityGrouped, true);
    }

    public boolean canRightClickGrouped(Position target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
        return canRightClickGrouped(target, checkCanTargetUnit, checkCanIssueCommandType, true);
    }

    public boolean canRightClickGrouped(Unit target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
        return canRightClickGrouped(target, checkCanTargetUnit, checkCanIssueCommandType, true);
    }

    public boolean canRightClickGrouped(Position target, boolean checkCanTargetUnit) {
        return canRightClickGrouped(target, checkCanTargetUnit, true);
    }

    public boolean canRightClickGrouped(Unit target, boolean checkCanTargetUnit) {
        return canRightClickGrouped(target, checkCanTargetUnit, true);
    }

    public boolean canRightClickGrouped(Position target) {
        return canRightClickGrouped(target, true);
    }

    public boolean canRightClickGrouped(Unit target) {
        return canRightClickGrouped(target, true);
    }

    public boolean canRightClickGrouped(Position target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (checkCommandibilityGrouped && !canCommandGrouped(false)) {
            return false;
        }

        return canRightClickPositionGrouped(false, false);
    }

    public boolean canRightClickGrouped(Unit target, boolean checkCanTargetUnit, boolean checkCanIssueCommandTypeGrouped, boolean checkCommandibilityGrouped, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (checkCommandibilityGrouped && !canCommandGrouped(false)) {
            return false;
        }
        if (target == null) {
            return false;
        }
        return canRightClickUnitGrouped(target, checkCanTargetUnit, checkCanIssueCommandTypeGrouped, false, false);
    }

    public boolean canRightClickPosition() {
        return canRightClickPosition(true);
    }

    public boolean canRightClickPosition(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (!getType().isBuilding() && !isInterruptible()) {
            return false;
        }
        return canMove(false) || canSetRallyPosition(false);
    }

    public boolean canRightClickPositionGrouped(boolean checkCommandibilityGrouped) {
        return canRightClickPositionGrouped(checkCommandibilityGrouped, true);
    }

    public boolean canRightClickPositionGrouped() {
        return canRightClickPositionGrouped(true);
    }

    public boolean canRightClickPositionGrouped(boolean checkCommandibilityGrouped, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }
        if (checkCommandibilityGrouped && !canCommandGrouped(false)) {
            return false;
        }

        if (!isInterruptible()) {
            return false;
        }
        return canMoveGrouped(false, false);
    }

    public boolean canRightClickUnit() {
        return canRightClickUnit(true);
    }

    public boolean canRightClickUnit(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (!getType().isBuilding() && !isInterruptible()) {
            return false;
        }
        return canFollow(false) ||
                canAttackUnit(false) ||
                canLoad(false) ||
                canSetRallyUnit(false);
    }

    public boolean canRightClickUnit(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
        return canRightClickUnit(targetUnit, checkCanTargetUnit, checkCanIssueCommandType, true);
    }

    public boolean canRightClickUnit(Unit targetUnit, boolean checkCanTargetUnit) {
        return canRightClickUnit(targetUnit, checkCanTargetUnit, true);
    }

    public boolean canRightClickUnit(Unit targetUnit) {
        return canRightClickUnit(targetUnit, true);
    }

    public boolean canRightClickUnit(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (checkCanIssueCommandType && !canRightClickUnit(false)) {
            return false;
        }

        if (checkCanTargetUnit && !canTargetUnit(targetUnit, false)) {
            return false;
        }

        if (!targetUnit.getPlayer().isNeutral() && getPlayer().isEnemy(targetUnit.getPlayer()) &&
                !canAttackUnit(targetUnit, false, true, false)) {
            return false;
        }

        return canFollow(targetUnit, false, true, false) ||
                canLoad(targetUnit, false, true, false) ||
                canSetRallyUnit(targetUnit, false, true, false);
    }

    public boolean canRightClickUnitGrouped(boolean checkCommandibilityGrouped) {
        return canRightClickUnitGrouped(checkCommandibilityGrouped, true);
    }

    public boolean canRightClickUnitGrouped() {
        return canRightClickUnitGrouped(true);
    }

    public boolean canRightClickUnitGrouped(boolean checkCommandibilityGrouped, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (checkCommandibilityGrouped && !canCommandGrouped(false)) {
            return false;
        }

        if (!isInterruptible()) {
            return false;
        }
        return canFollow(false) ||
                canAttackUnitGrouped(false, false) ||
                canLoad(false);
    }

    public boolean canRightClickUnitGrouped(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped) {
        return canRightClickUnitGrouped(targetUnit, checkCanTargetUnit, checkCanIssueCommandType, checkCommandibilityGrouped, true);
    }

    public boolean canRightClickUnitGrouped(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType) {
        return canRightClickUnitGrouped(targetUnit, checkCanTargetUnit, checkCanIssueCommandType, true);
    }

    public boolean canRightClickUnitGrouped(Unit targetUnit, boolean checkCanTargetUnit) {
        return canRightClickUnitGrouped(targetUnit, checkCanTargetUnit, true);
    }

    public boolean canRightClickUnitGrouped(Unit targetUnit) {
        return canRightClickUnitGrouped(targetUnit, true);
    }

    public boolean canRightClickUnitGrouped(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (checkCommandibilityGrouped && !canCommandGrouped(false)) {
            return false;
        }
        if (checkCanIssueCommandType && !canRightClickUnitGrouped(false, false)) {
            return false;
        }
        if (checkCanTargetUnit && !canTargetUnit(targetUnit, false)) {
            return false;
        }

        if (!targetUnit.getPlayer().isNeutral() && getPlayer().isEnemy(targetUnit.getPlayer()) &&
                !canAttackUnitGrouped(targetUnit, false, true, false, false)) {
            return false;
        }

        return canFollow(targetUnit, false, true, false) ||
                canLoad(targetUnit, false, true, false);
    }

    public boolean canHaltConstruction() {
        return canHaltConstruction(true);
    }

    public boolean canHaltConstruction(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        return getOrder() == ConstructingBuilding;
    }
    //------------------------------------------- CAN CANCEL CONSTRUCTION ------------------------------------

    public boolean canCancelConstruction() {
        return canCancelConstruction(true);
    }

    public boolean canCancelConstruction(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (!getType().isBuilding()) {
            return false;
        }

        return !isCompleted() && (getType() != Zerg_Nydus_Canal || getNydusExit() == null);
    }

    public boolean canCancelAddon() {
        return canCancelAddon(true);
    }

    public boolean canCancelAddon(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }
        final Unit addon = getAddon();
        return addon != null && !addon.isCompleted();
    }

    public boolean canCancelTrain() {
        return canCancelTrain(true);
    }

    public boolean canCancelTrain(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        return isTraining();
    }

    public boolean canCancelTrainSlot() {
        return canCancelTrainSlot(true);
    }

    public boolean canCancelTrainSlot(boolean checkCommandibility) {
        return canCancelTrain(checkCommandibility);
    }

    public boolean canCancelTrainSlot(int slot, boolean checkCanIssueCommandType) {
        return canCancelTrainSlot(slot, checkCanIssueCommandType, true);
    }

    public boolean canCancelTrainSlot(int slot) {
        return canCancelTrainSlot(slot, true);
    }

    public boolean canCancelTrainSlot(int slot, boolean checkCanIssueCommandType, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (checkCanIssueCommandType && !canCancelTrainSlot(false))
            return false;

        return isTraining() && slot >= 0 && (getTrainingQueue().size() > slot);
    }

    public boolean canCancelMorph() {
        return canCancelMorph(true);
    }

    public boolean canCancelMorph(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (!isMorphing() || (!isCompleted() && getType() == Zerg_Nydus_Canal && getNydusExit() != null)) {
            return false;
        }
        return !isHallucination();
    }

    public boolean canCancelResearch() {
        return canCancelResearch(true);
    }

    public boolean canCancelResearch(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        return getOrder() == ResearchTech;
    }

    public boolean canCancelUpgrade() {
        return canCancelUpgrade(true);
    }

    public boolean canCancelUpgrade(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        return getOrder() == Upgrade;
    }

    public boolean canUseTechWithOrWithoutTarget() {
        return canUseTechWithOrWithoutTarget(true);
    }

    public boolean canUseTechWithOrWithoutTarget(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (!getType().isBuilding() && !isInterruptible()) {
            return false;
        }
        if (!isCompleted()) {
            return false;
        }
        return !isHallucination();
    }

    public boolean canUseTechWithOrWithoutTarget(TechType tech, boolean checkCanIssueCommandType) {
        return canUseTechWithOrWithoutTarget(tech, checkCanIssueCommandType, true);
    }

    public boolean canUseTechWithOrWithoutTarget(TechType tech) {
        return canUseTechWithOrWithoutTarget(tech, true);
    }

    public boolean canUseTechWithOrWithoutTarget(TechType tech, boolean checkCanIssueCommandType, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (checkCanIssueCommandType && !canUseTechWithOrWithoutTarget(false)) {
            return false;
        }

        final UnitType ut = getType();
        // researched check
        if (!ut.isHero() && !game.self().hasResearched(tech) && ut != Zerg_Lurker) {
            return false;
        }

        // energy check
        if (getEnergy() < tech.energyCost()) {
            return false;
        }
        // unit check
        if (tech != TechType.Burrowing && !tech.whatsUses().contains(ut)) {
            return false;
        }

        final Order order = getOrder();
        switch (tech) {
            case Spider_Mines:
                return getSpiderMineCount() > 0;
            case Tank_Siege_Mode:
                return !isSieged() && order != Order.Sieging && order != Order.Unsieging;
            case Cloaking_Field:
            case Personnel_Cloaking:
                return getSecondaryOrder() != Cloak;
            case Burrowing:
                return ut.isBurrowable() && !isBurrowed() && order != Burrowing && order != Unburrowing;
            case None:
                return false;
            case Nuclear_Strike:
                return getPlayer().completedUnitCount(Terran_Nuclear_Missile) != 0;
            case Unknown:
                return false;
        }

        return true;
    }

    public boolean canUseTech(TechType tech, Position target, boolean checkCanTargetUnit, boolean checkTargetsType, boolean checkCanIssueCommandType) {
        return canUseTech(tech, target, checkCanTargetUnit, checkTargetsType, checkCanIssueCommandType, true);
    }

    public boolean canUseTech(TechType tech, Unit target, boolean checkCanTargetUnit, boolean checkTargetsType, boolean checkCanIssueCommandType) {
        return canUseTech(tech, target, checkCanTargetUnit, checkTargetsType, checkCanIssueCommandType, true);
    }

    public boolean canUseTech(TechType tech, Position target, boolean checkCanTargetUnit, boolean checkTargetsType) {
        return canUseTech(tech, target, checkCanTargetUnit, checkTargetsType, true);
    }

    public boolean canUseTech(TechType tech, Unit target, boolean checkCanTargetUnit, boolean checkTargetsType) {
        return canUseTech(tech, target, checkCanTargetUnit, checkTargetsType, true);
    }

    public boolean canUseTech(TechType tech, Position target, boolean checkCanTargetUnit) {
        return canUseTech(tech, target, checkCanTargetUnit, true);
    }

    public boolean canUseTech(TechType tech, Unit target, boolean checkCanTargetUnit) {
        return canUseTech(tech, target, checkCanTargetUnit, true);
    }

    public boolean canUseTech(TechType tech, Position target) {
        return canUseTech(tech, target, true);
    }

    public boolean canUseTech(TechType tech, Unit target) {
        return canUseTech(tech, target, true);
    }

//    public boolean canUseTech(TechType tech) {
//        return canUseTech(tech, TechType.None);
//    }

    public boolean canUseTech(TechType tech, Position target, boolean checkCanTargetUnit, boolean checkTargetsType, boolean checkCanIssueCommandType, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        return canUseTechPosition(tech, target, checkTargetsType, checkCanIssueCommandType, false);
    }

    public boolean canUseTech(TechType tech, Unit target, boolean checkCanTargetUnit, boolean checkTargetsType, boolean checkCanIssueCommandType, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }
        if (target == null) {
            return canUseTechWithoutTarget(tech, checkCanIssueCommandType, false);
        }
        return canUseTechUnit(tech, target, checkCanTargetUnit, checkTargetsType, checkCanIssueCommandType, false);
    }

    public boolean canUseTechWithoutTarget(TechType tech, boolean checkCanIssueCommandType) {
        return canUseTechWithoutTarget(tech, checkCanIssueCommandType, true);
    }

    public boolean canUseTechWithoutTarget(TechType tech) {
        return canUseTechWithoutTarget(tech, true);
    }

    public boolean canUseTechWithoutTarget(TechType tech, boolean checkCanIssueCommandType, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (checkCanIssueCommandType && !canUseTechWithOrWithoutTarget(false)) {
            return false;
        }

        if (!canUseTechWithOrWithoutTarget(tech, false, false)) {
            return false;
        }
        return !tech.targetsUnit() && !tech.targetsPosition() && tech != TechType.None && tech != TechType.Unknown && tech != TechType.Lurker_Aspect;
    }

    public boolean canUseTechUnit(TechType tech, boolean checkCanIssueCommandType) {
        return canUseTechUnit(tech, checkCanIssueCommandType, true);
    }

    public boolean canUseTechUnit(TechType tech) {
        return canUseTechUnit(tech, true);
    }

    public boolean canUseTechUnit(TechType tech, boolean checkCanIssueCommandType, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (checkCanIssueCommandType && !canUseTechWithOrWithoutTarget(false)) {
            return false;
        }

        if (!canUseTechWithOrWithoutTarget(tech, false, false)) {
            return false;
        }
        return tech.targetsUnit();
    }

    public boolean canUseTechUnit(TechType tech, Unit targetUnit, boolean checkCanTargetUnit, boolean checkTargetsUnits, boolean checkCanIssueCommandType) {
        return canUseTech(tech, targetUnit, checkCanTargetUnit, checkTargetsUnits, checkCanIssueCommandType, true);
    }

    public boolean canUseTechUnit(TechType tech, Unit targetUnit, boolean checkCanTargetUnit, boolean checkTargetsUnits) {
        return canUseTech(tech, targetUnit, checkCanTargetUnit, checkTargetsUnits, true);
    }

    public boolean canUseTechUnit(TechType tech, Unit targetUnit, boolean checkCanTargetUnit) {
        return canUseTech(tech, targetUnit, checkCanTargetUnit, true);
    }

    public boolean canUseTechUnit(TechType tech, Unit targetUnit) {
        return canUseTech(tech, targetUnit, true);
    }

    public boolean canUseTechUnit(TechType tech, Unit targetUnit, boolean checkCanTargetUnit, boolean checkTargetsUnits, boolean checkCanIssueCommandType, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (checkCanIssueCommandType && !canUseTechWithOrWithoutTarget(false)) {
            return false;
        }

        if (checkTargetsUnits && !canUseTechUnit(tech, false, false)) {
            return false;
        }

        if (checkCanTargetUnit && !canTargetUnit(targetUnit, false)) {
            return false;
        }

        final UnitType targetType = targetUnit.getType();

        switch (tech) {
            case Archon_Warp:
                if (targetType != Protoss_High_Templar) {
                    return false;
                }
                if (!getPlayer().equals(targetUnit.getPlayer())) {
                    return false;
                }
                break;

            case Dark_Archon_Meld:
                if (targetType != Protoss_Dark_Templar) {
                    return false;
                }
                if (!getPlayer().equals(targetUnit.getPlayer())) {
                    return false;
                }
                break;

            case Consume:
                if (!getPlayer().equals(targetUnit.getPlayer())) {
                    return false;
                }
                if (targetType.getRace() != Zerg || targetType == Zerg_Larva) {
                    return false;
                }
                break;

            case Spawn_Broodlings:
                if ((!targetType.isOrganic() && !targetType.isMechanical()) ||
                        targetType.isRobotic() ||
                        targetType.isFlyer()) {
                    return false;
                }
                break;

            case Lockdown:
                if (!targetType.isMechanical()) {
                    return false;
                }
                break;

            case Healing:
                if (targetUnit.getHitPoints() == targetType.maxHitPoints()) {
                    return false;
                }
                if (!targetType.isOrganic() || targetType.isFlyer()) {
                    return false;
                }
                if (!targetUnit.getPlayer().isNeutral() && getPlayer().isEnemy(getPlayer())) {
                    return false;
                }
                break;

            case Mind_Control:
                if (getPlayer().equals(targetUnit.getPlayer())) {
                    return false;
                }
                if (targetType == Protoss_Interceptor ||
                        targetType == Terran_Vulture_Spider_Mine ||
                        targetType == Zerg_Lurker_Egg ||
                        targetType == Zerg_Cocoon ||
                        targetType == Zerg_Larva ||
                        targetType == Zerg_Egg) {
                    return false;
                }
                break;

            case Feedback:
                if (!targetType.isSpellcaster()) {
                    return false;
                }
                break;

            case Infestation:
                if (targetType != Terran_Command_Center ||
                        targetUnit.getHitPoints() >= 750 || targetUnit.getHitPoints() <= 0) {
                    return false;
                }
                break;
        }

        switch (tech) {
            case Archon_Warp:
            case Dark_Archon_Meld:
                if (!hasPath(targetUnit.getPosition())) {
                    return false;
                }
                if (targetUnit.isHallucination()) {
                    return false;
                }
                if (targetUnit.isMaelstrommed()) {
                    return false;
                }
                // Fall through (don't break).
            case Parasite:
            case Irradiate:
            case Optical_Flare:
            case Spawn_Broodlings:
            case Lockdown:
            case Defensive_Matrix:
            case Hallucination:
            case Healing:
            case Restoration:
            case Mind_Control:
            case Consume:
            case Feedback:
            case Yamato_Gun:
                if (targetUnit.isStasised()) {
                    return false;
                }
                break;
        }

        switch (tech) {
            case Yamato_Gun:
                if (targetUnit.isInvincible()) {
                    return false;
                }
                break;

            case Parasite:
            case Irradiate:
            case Optical_Flare:
            case Spawn_Broodlings:
            case Lockdown:
            case Defensive_Matrix:
            case Hallucination:
            case Healing:
            case Restoration:
            case Mind_Control:
                if (targetUnit.isInvincible()) {
                    return false;
                }
                // Fall through (don't break).
            case Consume:
            case Feedback:
                if (targetType.isBuilding()) {
                    return false;
                }
                break;
        }

        return targetUnit != this;
    }

    public boolean canUseTechPosition(TechType tech, boolean checkCanIssueCommandType) {
        return canUseTechPosition(tech, checkCanIssueCommandType, true);
    }

    public boolean canUseTechPosition(TechType tech) {
        return canUseTechPosition(tech, true);
    }

    public boolean canUseTechPosition(TechType tech, boolean checkCanIssueCommandType, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (checkCanIssueCommandType && !canUseTechWithOrWithoutTarget(false)) {
            return false;
        }

        if (!canUseTechWithOrWithoutTarget(tech, false, false)) {
            return false;
        }
        return tech.targetsPosition();
    }

    public boolean canUseTechPosition(TechType tech, Position target, boolean checkTargetsPositions, boolean checkCanIssueCommandType) {
        return canUseTechPosition(tech, target, checkTargetsPositions, checkCanIssueCommandType, true);
    }

    public boolean canUseTechPosition(TechType tech, Position target, boolean checkTargetsPositions) {
        return canUseTechPosition(tech, target, checkTargetsPositions, true);
    }

    public boolean canUseTechPosition(TechType tech, Position target) {
        return canUseTechPosition(tech, target, true);
    }

    public boolean canUseTechPosition(TechType tech, Position target, boolean checkTargetsPositions, boolean checkCanIssueCommandType, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (checkCanIssueCommandType && !canUseTechWithOrWithoutTarget(false)) {
            return false;
        }

        if (checkTargetsPositions && !canUseTechPosition(tech, false, false)) {
            return false;
        }

        return tech != TechType.Spider_Mines || hasPath(target);
    }

    public boolean canPlaceCOP() {
        return canPlaceCOP(true);
    }

    public boolean canPlaceCOP(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (!getType().isFlagBeacon()) {
            return false;
        }

        return unitData.buttonset() != 228 && getOrder() == CTFCOPInit;
    }

    public boolean canPlaceCOP(TilePosition target, boolean checkCanIssueCommandType) {
        return canPlaceCOP(target, checkCanIssueCommandType, true);
    }

    public boolean canPlaceCOP(TilePosition target) {
        return canPlaceCOP(target, true);
    }

    public boolean canPlaceCOP(TilePosition target, boolean checkCanIssueCommandType, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (checkCanIssueCommandType && !canPlaceCOP(checkCommandibility))
            return false;

        return game.canBuildHere(target, getType(), this, true);
    }


    public boolean equals(Object that) {
        if (!(that instanceof Unit)) {
            return false;
        }
        return getID() == ((Unit) that).getID();
    }

    public int hashCode() {
        return getID();
    }

    void updateType(final int frame) {
        if (frame > lastTypeUpdate) {
            lastTypeUpdate = frame;
            position = new Position(unitData.positionX(), unitData.positionY());
        }
        unitType = UnitType.unitTypes[unitData.type()];
    }

    void updatePlayer(final int frame) {
        if (frame > lastPlayerUpdate) {
            lastPlayerUpdate = frame;
            player = game.getPlayer(unitData.player());
        }
    }

    void updatePosition(final int frame) {
        if (frame > lastPositionUpdate) {
            lastPositionUpdate = frame;
            position = new Position(unitData.positionX(), unitData.positionY());
        }
    }
}
