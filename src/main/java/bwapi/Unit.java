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

    // static
    private final UnitType initialType;
    private final int initialResources;
    private final int initialHitPoints;
    private final Position initialPosition;

    // variable
    private int lastCommandFrame = 0;
    private UnitCommand lastCommand;

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
        if ( !exists() ) {
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
                    if ( u == this || u.isInvincible() ) {
                        return false;
                    }

                    // Weapon distance check
                    final int dist = getDistance(u);
                    if ( (weapon.minRange() != 0 && dist < weapon.minRange()) || dist > max ) {
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
        if ( !canIssueCommand(command) ) {
            return false;
        }
        command.unit = this;

        // If using train or morph on a hatchery, automatically switch selection to larva
        // (assuming canIssueCommand ensures that there is a larva)
        if ( (command.type == UnitCommandType.Train || command.type == UnitCommandType.Morph) &&
                getType().producesLarva() && command.getUnitType().whatBuilds().getKey() == UnitType.Zerg_Larva) {
            for (final Unit larva : getLarva()) {
                if ( !larva.isConstructing() && larva.isCompleted() && larva.canCommand() )  {
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
        return issueCommand(UnitCommand.attack(this, target,shiftQueueCommand));
    }

    public boolean build(final UnitType type){
        return issueCommand(UnitCommand.train(this, type));
    }

    public boolean build(final UnitType type, final TilePosition target){
        return issueCommand(UnitCommand.build(this, target, type));
    }

    public boolean buildAddon(final UnitType type){
        return issueCommand(UnitCommand.buildAddon(this, type));
    }

    public boolean train(final UnitType type){
        return issueCommand(UnitCommand.train(this, type));
    }

    public boolean morph(final UnitType type){
        return issueCommand(UnitCommand.morph(this, type));
    }

    public boolean research(final TechType tech){
        return issueCommand(UnitCommand.research(this, tech));
    }

    public boolean upgrade(final UpgradeType upgrade){
        return issueCommand(UnitCommand.upgrade(this, upgrade));
    }

    public boolean setRallyPoint(final Position target){
        return issueCommand(UnitCommand.setRallyPoint(this, target));
    }

    public boolean setRallyPoint(final Unit target){
        return issueCommand(UnitCommand.setRallyPoint(this, target));
    }

    public boolean move(final Position target){
        return issueCommand(UnitCommand.move(this, target));
    }

    public boolean move(final Position target, final boolean shiftQueueCommand){
        return issueCommand(UnitCommand.move(this, target, shiftQueueCommand));
    }

    public boolean patrol(final Position target){
        return issueCommand(UnitCommand.patrol(this, target));
    }

    public boolean patrol(final Position target, final boolean shiftQueueCommand){
        return issueCommand(UnitCommand.patrol(this, target, shiftQueueCommand));
    }

    public boolean holdPosition(){
        return issueCommand(UnitCommand.holdPosition(this));
    }

    public boolean holdPosition(final boolean shiftQueueCommand){
        return issueCommand(UnitCommand.holdPosition(this, shiftQueueCommand));
    }

    public boolean stop(){
        return issueCommand(UnitCommand.stop(this));
    }

    public boolean stop(final boolean shiftQueueCommand){
        return issueCommand(UnitCommand.stop(this, shiftQueueCommand));
    }

    public boolean follow(final Unit target){
        return issueCommand(UnitCommand.follow(this, target));
    }
    public boolean follow(final Unit target, final boolean shiftQueueCommand){
        return issueCommand(UnitCommand.follow(this, target, shiftQueueCommand));
    }

    public boolean gather(final Unit target){
        return issueCommand(UnitCommand.gather(this, target));
    }

    public boolean gather(final Unit target, final boolean shiftQueueCommand){
        return issueCommand(UnitCommand.gather(this, target, shiftQueueCommand));
    }

    public boolean repair(final Unit target){
        return issueCommand(UnitCommand.repair(this, target));
    }

    public boolean repair(final Unit target, final boolean shiftQueueCommand){
        return issueCommand(UnitCommand.repair(this, target, shiftQueueCommand));
    }

    public boolean burrow(){
        return issueCommand(UnitCommand.burrow(this));
    }

    public boolean unburrow(){
        return issueCommand(UnitCommand.unburrow(this));
    }

    public boolean cloak(){
        return issueCommand(UnitCommand.cloak(this));
    }

    public boolean decloak(){
        return issueCommand(UnitCommand.decloak(this));
    }

    public boolean siege(){
        return issueCommand(UnitCommand.siege(this));
    }

    public boolean unsiege(){
        return issueCommand(UnitCommand.unsiege(this));
    }

    public boolean lift(){
        return issueCommand(UnitCommand.lift(this));
    }

    public boolean land(final TilePosition target){
        return issueCommand(UnitCommand.land(this, target));
    }

    public boolean load(final Unit target){
        return issueCommand(UnitCommand.load(this, target));
    }

    public boolean load(final Unit target, final boolean shiftQueueCommand){
        return issueCommand(UnitCommand.load(this, target, shiftQueueCommand));
    }

    public boolean unload(final Unit target){
        return issueCommand(UnitCommand.unload(this, target));
    }

    public boolean unloadAll(){
        return issueCommand(UnitCommand.unloadAll(this));
    }

    public boolean unloadAll(final boolean shiftQueueCommand){
        return issueCommand(UnitCommand.unloadAll(this, shiftQueueCommand));
    }

    public boolean unloadAll(final Position target){
        return issueCommand(UnitCommand.unloadAll(this, target));
    }

    public boolean unloadAll(final Position target, final boolean shiftQueueCommand){
        return issueCommand(UnitCommand.unloadAll(this, target, shiftQueueCommand));
    }

    public boolean rightClick(final Position target){
        return issueCommand(UnitCommand.rightClick(this, target));
    }

    public boolean rightClick(final Unit target){
        return issueCommand(UnitCommand.rightClick(this, target));
    }

    public boolean rightClick(final Position target, final boolean shiftQueueCommand){
        return issueCommand(UnitCommand.rightClick(this, target, shiftQueueCommand));
    }

    public boolean rightClick(final Unit target, final boolean shiftQueueCommand){
        return issueCommand(UnitCommand.rightClick(this, target, shiftQueueCommand));
    }

    public boolean haltConstruction(){
        return issueCommand(UnitCommand.haltConstruction(this));
    }

    public boolean cancelConstruction(){
        return issueCommand(UnitCommand.cancelConstruction(this));
    }

    public boolean cancelAddon(){
        return issueCommand(UnitCommand.cancelAddon(this));
    }

    public boolean cancelTrain(){
        return issueCommand(UnitCommand.cancelTrain(this));
    }

    public boolean cancelTrain(final int slot){
        return issueCommand(UnitCommand.cancelTrain(this, slot));
    }

    public boolean cancelMorph(){
        return issueCommand(UnitCommand.cancelMorph(this));
    }

    public boolean cancelResearch(){
        return issueCommand(UnitCommand.cancelResearch(this));
    }

    public boolean cancelUpgrade(){
        return issueCommand(UnitCommand.cancelUpgrade(this));
    }

    public boolean useTech(final TechType tech){
        return issueCommand(UnitCommand.useTech(this, tech));
    }

    public boolean useTech(final TechType tech, final Position target){
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
        return canIssueCommand(command, checkCanUseTechPositionOnPositions, checkCanUseTechUnitOnUnits, checkCanBuildUnitType);
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
        if ( checkCanIssueCommandType && !canIssueCommandType(ct, false)) {
            return false;
        }

        switch (ct)  {
            case Attack_Move: return true;
            case Attack_Unit: return canAttackUnit(command.target, checkCanTargetUnit, false, false);
            case Build: return canBuild(command.getUnitType(), new TilePosition(command.x, command.y), checkCanBuildUnitType, false, false);
            case Build_Addon: return canBuildAddon( command.getUnitType(), false, false);
            case Train: return canTrain( command.getUnitType(), false, false);
            case Morph: return canMorph( command.getUnitType(), false, false);
            case Research: return game.canResearch(command.getTechType(), this,  false);
            case Upgrade: return game.canUpgrade(command.getUpgradeType(),  this,false);
            case Set_Rally_Position: return true;
            case Set_Rally_Unit: return canSetRallyUnit(command.target, checkCanTargetUnit, false, false);
            case Move: return true;
            case Patrol: return true;
            case Hold_Position:  return true;
            case Stop: return true;
            case Follow: return canFollow(command.target, checkCanTargetUnit, false, false);
            case Gather: return canGather(command.target, checkCanTargetUnit, false, false);
            case Return_Cargo: return true;
            case Repair:  return canRepair(command.target, checkCanTargetUnit, false, false);
            case Burrow: return true;
            case Unburrow: return true;
            case Cloak: return true;
            case Decloak: return true;
            case Siege: return true;
            case Unsiege: return true;
            case Lift: return true;
            case Land: return canLand(new TilePosition(command.x, command.y), false, false);
            case Load: return canLoad(command.target, checkCanTargetUnit, false, false);
            case Unload:  return canUnload( command.target, checkCanTargetUnit, false, false, false);
            case Unload_All: return true;
            case Unload_All_Position:  return canUnloadAllPosition( command.getTargetPosition(), false, false);
            case Right_Click_Position:  return true;
            case Right_Click_Unit:  return canRightClickUnit( command.target, checkCanTargetUnit, false, false);
            case Halt_Construction: return true;
            case Cancel_Construction: return true;
            case Cancel_Addon: return true;
            case Cancel_Train:  return true;
            case Cancel_Train_Slot: return canCancelTrainSlot( command.extra, false, false);
            case Cancel_Morph: return true;
            case Cancel_Research: return true;
            case Cancel_Upgrade: return true;
            case Use_Tech: return canUseTechWithoutTarget( TechType.techTypes[command.extra], false, false);
            case Use_Tech_Unit: return canUseTechUnit(TechType.techTypes[command.extra], command.target, checkCanTargetUnit, checkCanUseTechUnitOnUnits, false, false);
            case Use_Tech_Position: return canUseTechPosition(TechType.techTypes[command.extra], command.getTargetPosition(), checkCanUseTechPositionOnPositions, false, false);
            case Place_COP: return canPlaceCOP(new TilePosition(command.x, command.y), false, false);
        }
        return true;
    }


    public boolean canIssueCommandGrouped(UnitCommand command, boolean checkCanUseTechPositionOnPositions, boolean checkCanUseTechUnitOnUnits, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped){
        return canIssueCommandGrouped(command, checkCanUseTechPositionOnPositions, checkCanUseTechUnitOnUnits, checkCanTargetUnit, checkCanIssueCommandType, checkCommandibilityGrouped, true);
    }

    public boolean canIssueCommandGrouped(UnitCommand command, boolean checkCanUseTechPositionOnPositions, boolean checkCanUseTechUnitOnUnits, boolean checkCanTargetUnit, boolean checkCanIssueCommandType){
        return canIssueCommandGrouped(command, checkCanUseTechPositionOnPositions, checkCanUseTechUnitOnUnits, checkCanTargetUnit, checkCanIssueCommandType, true);

    }

    public boolean canIssueCommandGrouped(UnitCommand command, boolean checkCanUseTechPositionOnPositions, boolean checkCanUseTechUnitOnUnits, boolean checkCanTargetUnit){
        return canIssueCommandGrouped(command, checkCanUseTechPositionOnPositions, checkCanUseTechUnitOnUnits, checkCanTargetUnit, true);

    }

    public boolean canIssueCommandGrouped(UnitCommand command, boolean checkCanUseTechPositionOnPositions, boolean checkCanUseTechUnitOnUnits){
        return canIssueCommandGrouped(command, checkCanUseTechPositionOnPositions, checkCanUseTechUnitOnUnits, true);
    }

    public boolean canIssueCommandGrouped(UnitCommand command, boolean checkCanUseTechPositionOnPositions){
        return canIssueCommandGrouped(command, checkCanUseTechPositionOnPositions, true);
    }

    public boolean canIssueCommandGrouped(UnitCommand command) {
        return canIssueCommandGrouped(command, true);
    }

    public boolean canIssueCommandGrouped(UnitCommand command, boolean checkCanUseTechPositionOnPositions, boolean checkCanUseTechUnitOnUnits, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped, boolean checkCommandibility){
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if ( checkCommandibilityGrouped && !canCommandGrouped(false) ) {
            return false;
        }

        final UnitCommandType ct = command.type;
        if ( checkCanIssueCommandType && !canIssueCommandTypeGrouped(ct, false, false) ) {
            return false;
        }

        switch (ct)  {
            case Attack_Move: return true;
            case Attack_Unit:  return canAttackUnitGrouped(command.target, checkCanTargetUnit, false, false, false);
            case Build: return false;
            case Build_Addon:  return false;
            case Train: return canTrain(command.getUnitType(), false, false);
            case Morph:  return canMorph(command.getUnitType(), false, false);
            case Research: return false;
            case Upgrade: return false;
            case Set_Rally_Position: return false;
            case Set_Rally_Unit: return false;
            case Move: return true;
            case Patrol: return true;
            case Hold_Position: return true;
            case Stop: return true;
            case Follow: return canFollow(command.target, checkCanTargetUnit, false, false);
            case Gather: return canGather(command.target, checkCanTargetUnit, false, false);
            case Return_Cargo: return true;
            case Repair: return canRepair(command.target, checkCanTargetUnit, false, false);
            case Burrow: return true;
            case Unburrow: return true;
            case Cloak: return true;
            case Decloak: return true;
            case Siege: return true;
            case Unsiege: return true;
            case Lift: return false;
            case Land: return false;
            case Load: return canLoad(command.target, checkCanTargetUnit, false, false);
            case Unload: return false;
            case Unload_All: return false;
            case Unload_All_Position: return canUnloadAllPosition(command.getTargetPosition(), false, false);
            case Right_Click_Position: return true;
            case Right_Click_Unit: return canRightClickUnitGrouped(command.target, checkCanTargetUnit, false, false, false);
            case Halt_Construction: return true;
            case Cancel_Construction: return false;
            case Cancel_Addon: return false;
            case Cancel_Train: return false;
            case Cancel_Train_Slot: return false;
            case Cancel_Morph: return true;
            case Cancel_Research: return false;
            case Cancel_Upgrade: return false;
            case Use_Tech: return canUseTechWithoutTarget(TechType.techTypes[command.extra], false, false);
            case Use_Tech_Unit: return canUseTechUnit(TechType.techTypes[command.extra], command.target, checkCanTargetUnit, checkCanUseTechUnitOnUnits, false, false);
            case Use_Tech_Position: return canUseTechPosition(TechType.techTypes[command.extra], command.getTargetPosition(), checkCanUseTechPositionOnPositions, false, false);
            case Place_COP: return false;
        }
        return true;
    }


    public boolean canCommand() {
        if (!exists() || !getPlayer().equals(game.self())) {
            return false;
        }

        // Global can be ordered check
        if ( isLockedDown() || isMaelstrommed() || isStasised() ||
                !isPowered() || getOrder() == Order.ZergBirth || isLoaded() ) {
            if ( !getType().producesLarva() ) {
                return false;
            }
            else {
                for (Unit larva : getLarva())  {
                    if (larva.canCommand() ) {
                        return true;
                    }
                }
                return false;
            }
        }

        final UnitType uType = getType();
        if ( uType == Protoss_Interceptor ||
                uType == Terran_Vulture_Spider_Mine ||
                uType == Spell_Scanner_Sweep ||
                uType == Special_Map_Revealer ) {
            return false;
        }

        if (isCompleted() &&
                ( uType == Protoss_Pylon ||
                        uType == Terran_Supply_Depot ||
                        uType.isResourceContainer() ||
                        uType == Protoss_Shield_Battery ||
                        uType == Terran_Nuclear_Missile ||
                        uType.isPowerup() ||
                        ( uType.isSpecialBuilding() && !uType.isFlagBeacon()))) {
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
            case Attack_Move: return canAttackMove(false);
            case Attack_Unit: return canAttackUnit(false);
            case Build: return canBuild(false);
            case Build_Addon: return canBuildAddon(false);
            case Train: return canTrain(false);
            case Morph: return canMorph(false);
            case Research: return canResearch(false);
            case Upgrade: return canUpgrade(false);
            case Set_Rally_Position: return canSetRallyPosition(false);
            case Set_Rally_Unit: return canSetRallyUnit(false);
            case Move: return canMove(false);
            case Patrol: return canPatrol(false);
            case Hold_Position: return canHoldPosition(false);
            case Stop: return canStop(false);
            case Follow: return canFollow(false);
            case Gather: return canGather(false);
            case Return_Cargo: return canReturnCargo(false);
            case Repair: return canRepair(false);
            case Burrow: return canBurrow(false);
            case Unburrow: return canUnburrow(false);
            case Cloak: return canCloak(false);
            case Decloak: return canDecloak(false);
            case Siege: return canSiege(false);
            case Unsiege: return canUnsiege(false);
            case Lift: return canLift(false);
            case Land: return canLand(false);
            case Load: return canLoad(false);
            case Unload: return canUnload(false);
            case Unload_All: return canUnloadAll(false);
            case Unload_All_Position: return canUnloadAllPosition(false);
            case Right_Click_Position: return canRightClickPosition(false);
            case Right_Click_Unit: return canRightClickUnit(false);
            case Halt_Construction: return canHaltConstruction(false);
            case Cancel_Construction: return canCancelConstruction(false);
            case Cancel_Addon: return canCancelAddon(false);
            case Cancel_Train: return canCancelTrain(false);
            case Cancel_Train_Slot: return canCancelTrainSlot(false);
            case Cancel_Morph: return canCancelMorph(false);
            case Cancel_Research: return canCancelResearch(false);
            case Cancel_Upgrade: return canCancelUpgrade(false);
            case Use_Tech:
            case Use_Tech_Unit:
            case Use_Tech_Position: return canUseTechWithOrWithoutTarget(false);
            case Place_COP: return canPlaceCOP(false);
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
            case Attack_Move: return canAttackMoveGrouped(false, false);
            case Attack_Unit: return canAttackUnitGrouped(false, false);
            case Build: return false;
            case Build_Addon: return false;
            case Train: return canTrain(false);
            case Morph: return canMorph(false);
            case Research: return false;
            case Upgrade: return false;
            case Set_Rally_Position: return false;
            case Set_Rally_Unit: return false;
            case Move: return canMoveGrouped(false, false);
            case Patrol: return canPatrolGrouped(false, false);
            case Hold_Position: return canHoldPosition(false);
            case Stop: return canStop(false);
            case Follow: return canFollow(false);
            case Gather: return canGather(false);
            case Return_Cargo: return canReturnCargo(false);
            case Repair: return canRepair(false);
            case Burrow: return canBurrow(false);
            case Unburrow: return canUnburrow(false);
            case Cloak: return canCloak(false);
            case Decloak: return canDecloak(false);
            case Siege: return canSiege(false);
            case Unsiege: return canUnsiege(false);
            case Lift: return false;
            case Land: return false;
            case Load: return canLoad(false);
            case Unload: return false;
            case Unload_All: return false;
            case Unload_All_Position: return canUnloadAllPosition(false);
            case Right_Click_Position: return canRightClickPositionGrouped(false, false);
            case Right_Click_Unit: return canRightClickUnitGrouped(false, false);
            case Halt_Construction: return canHaltConstruction(false);
            case Cancel_Construction: return false;
            case Cancel_Addon: return false;
            case Cancel_Train: return false;
            case Cancel_Train_Slot: return false;
            case Cancel_Morph: return canCancelMorph(false);
            case Cancel_Research: return false;
            case Cancel_Upgrade: return false;
            case Use_Tech:
            case Use_Tech_Unit:
            case Use_Tech_Position: return canUseTechWithOrWithoutTarget(false);
            case Place_COP: return false;
        }
        return true;
    }

    public boolean canTargetUnit(Unit targetUnit) {
        if ( targetUnit == null || !targetUnit.exists() ) {
            return false;
        }
        final UnitType targetType = targetUnit.getType();
        if (!targetUnit.isCompleted() && !targetType.isBuilding() && !targetUnit.isMorphing() &&
                targetType != Protoss_Archon && targetType != Protoss_Dark_Archon ) {
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

        if ( target == null ) {
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

        if ( checkCommandibilityGrouped && !canCommandGrouped(false) ) {
            return false;
        }

        return canAttackMoveGrouped(false, false) || canAttackUnitGrouped(false, false);
    }

    public boolean canAttackGrouped(Position target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped){
        return canAttackGrouped(target, checkCanTargetUnit, checkCanIssueCommandType, checkCommandibilityGrouped, true);
    }

    public boolean canAttackGrouped(Unit target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped){
        return canAttackGrouped(target, checkCanTargetUnit, checkCanIssueCommandType, checkCommandibilityGrouped, true);
    }

    public boolean canAttackGrouped(Position target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType){
        return canAttackGrouped(target, checkCanTargetUnit, checkCanIssueCommandType, true);
    }

    public boolean canAttackGrouped(Unit target, boolean checkCanTargetUnit, boolean checkCanIssueCommandType){
        return canAttackGrouped(target, checkCanTargetUnit, checkCanIssueCommandType, true);
    }

    public boolean canAttackGrouped(Position target, boolean checkCanTargetUnit){
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

        if ( checkCommandibilityGrouped && !canCommandGrouped(false) ) {
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

        if ( target == null ) {
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

        if ( (getType() != Terran_Medic && !canAttackUnit( false) ) || !canMove(false) )
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

        if ( checkCommandibilityGrouped && !canCommandGrouped(false)) {
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
        if ( !ut.isBuilding() && !isInterruptible() ) {
            return false;

        }
        if ( ut.groundWeapon() == WeaponType.None && ut.airWeapon() == WeaponType.None ) {
            if ( ut == Protoss_Carrier || ut == Hero_Gantrithor ) {
                if ( getInterceptorCount() <= 0 ) {
                    return false;
                }
            }
            else if ( ut == Protoss_Reaver || ut == Hero_Warbringer ) {
                if ( getScarabCount() <= 0 ) {
                    return false;
                }
            }
            else
                return false;
        }
        if (ut == Zerg_Lurker ) {
            if ( !isBurrowed() ) {
                return false;
            }
        }
        else if (isBurrowed() ) {
            return false;
        }
        if ( !isCompleted() ) {
            return false;
        }
        return getOrder() != Order.ConstructingBuilding;
    }

    public boolean canAttackUnit(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType){
        return canAttackUnit(targetUnit, checkCanTargetUnit, checkCanIssueCommandType, true);
    }

    public boolean canAttackUnit(Unit targetUnit, boolean checkCanTargetUnit){
        return canAttackUnit(targetUnit, checkCanTargetUnit, true);
    }

    public boolean canAttackUnit(Unit targetUnit) {
        return canAttackUnit(targetUnit, true);
    }

    public boolean canAttackUnit(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if ( checkCanIssueCommandType && !canAttackUnit( false) ) {
            return false;
        }

        if ( checkCanTargetUnit && !canTargetUnit( targetUnit, false) ) {
            return false;
        }
        if ( targetUnit.isInvincible() ) {
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

        if ( !type.canMove() && !isInWeaponRange(targetUnit) )
            return false;

        if ( type == Zerg_Lurker && !isInWeaponRange(targetUnit) )
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
        if ( checkCommandibility && !canCommand() ) {
            return false;
        }

        if ( checkCommandibilityGrouped && !canCommandGrouped(false) ) {
            return false;
        }
        if ( !isInterruptible() ) {
            return false;
        }
        final UnitType ut = getType();
        if ( !ut.canMove() &&  ut != Terran_Siege_Tank_Siege_Mode ) {
            return false;
        }
        if ( !isCompleted() ) {
             return false;
        }
        if ( getType() == Zerg_Lurker ) {
            if ( !isBurrowed() ) {
                return false;
            }
        }
        else if ( isBurrowed() ) {
            return false;
        }
        return getOrder() != ConstructingBuilding;
    }

    public boolean canAttackUnitGrouped(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibilityGrouped){
        return canAttackUnitGrouped(targetUnit, checkCanTargetUnit, checkCanIssueCommandType, checkCommandibilityGrouped, true);
    }

    public boolean canAttackUnitGrouped(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType){
        return canAttackUnitGrouped(targetUnit, checkCanTargetUnit, checkCanIssueCommandType, true);
    }

    public boolean canAttackUnitGrouped(Unit targetUnit, boolean checkCanTargetUnit){
        return canAttackUnitGrouped(targetUnit, checkCanTargetUnit, true);
    }

    public boolean canAttackUnitGrouped(Unit targetUnit) {
        return canAttackUnitGrouped(targetUnit, true);
    }

    public boolean canAttackUnitGrouped(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandTypeGrouped, boolean checkCommandibilityGrouped, boolean checkCommandibility) {
        if ( checkCommandibility && !canCommand() ) {
            return false;
        }

        if ( checkCommandibilityGrouped && !canCommandGrouped(false) ) {
            return false;
        }

        if ( checkCanIssueCommandTypeGrouped && !canAttackUnitGrouped(false, false) ) {
            return false;
        }

        if ( checkCanTargetUnit && !canTargetUnit(targetUnit, false) ) {
            return false;
        }

        if ( isInvincible() ) {
            return false;
        }

        final UnitType ut = getType();
        if ( ut == Zerg_Lurker && !isInWeaponRange(targetUnit) ) {
            return false;
        }

        if ( ut == Zerg_Queen &&
                ( targetUnit.getType() != Terran_Command_Center ||
                        targetUnit.getHitPoints() >= 750 || targetUnit.getHitPoints() <= 0 ) ) {
            return false;
        }

        return !equals(targetUnit);
    }

    public boolean canBuild() {
        return canBuild(true);
    }

    public boolean canBuild(boolean checkCommandibility) {
        if ( checkCommandibility  && !canCommand() ) {
            return false;
        }

        final UnitType ut = getType();
        if ( !ut.isBuilding() && !isInterruptible() ) {
            return false;
        }
        if ( isConstructing() ||
                !isCompleted()   ||
                        (ut.isBuilding() && !isIdle()) ) {
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
        if ( checkCommandibility && !canCommand() ) {
            return false;
        }

        if ( checkCanIssueCommandType && !canBuild(false) ) {
            return false;
        }
        if ( !game.canMake(uType, this) ) {
            return false;
        }

        if ( !uType.isBuilding() ) {
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
        if ( checkCommandibility  && !canCommand() ) {
            return false;
        }

        if ( checkCanIssueCommandType && !canBuild(false) ){
            return false;
        }

        if ( checkTargetUnitType && !canBuild(uType, false, false) ){
            return false;
        }

        if ( !tilePos.isValid(game)){
            return false;
        }

        return game.canBuildHere(tilePos, uType, this, true);
    }

    public boolean canBuildAddon() {
        return canBuildAddon(true);
    }

    public boolean canBuildAddon(boolean checkCommandibility) {
        if ( checkCommandibility && !canCommand() ) {
            return false;
        }

        if (isConstructing() || !isCompleted() || isLifted() || (getType().isBuilding() && !isIdle()) ) {
            return false;
        }
        if ( getAddon() != null ) {
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
        if ( checkCommandibility && !canCommand() ) {
            return false;
        }
        if ( checkCanIssueCommandType && !canBuildAddon(this, false) ) {
            return false;
        }
        if ( !game.canMake(uType, this) ) {
            return false;
        }
        if ( !uType.isAddon() ) {
            return false;
        }
        return game.canBuildHere(getTilePosition(), uType, this);
    }

    public boolean canTrain() {
        return canTrain(true);
    }

    public boolean canTrain(boolean checkCommandibility) {
        if ( checkCommandibility && !canCommand() ) {
            return false;
        }
        final UnitType ut = getType();
        if ( ut.producesLarva() ) {
            if ( !isConstructing() && isCompleted() ) {
                return true;
            }
            for (Unit larva : getLarva()) {
                if ( !larva.isConstructing() && larva.isCompleted() && larva.canCommand() ) {
                    return true;
                }
            }
            return false;
        }

        if ( isConstructing() || !isCompleted() || isLifted() ) {
            return false;
        }
        if ( !ut.canProduce() &&
                ut != Terran_Nuclear_Silo &&
                ut != Zerg_Hydralisk &&
                ut != Zerg_Mutalisk &&
                ut != Zerg_Creep_Colony &&
                ut != Zerg_Spire &&
                ut != Zerg_Larva ) {
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
        if ( checkCommandibility && !canCommand() ) {
            return false;
        }

        if ( checkCanIssueCommandType && !canTrain(false) ){
            return false;
        }

        Unit thisUnit = this;
        if (getType().producesLarva() ) {
            if ( uType.whatBuilds().getKey() == Zerg_Larva ) {
                boolean foundCommandableLarva = false;
                for (Unit larva : getLarva()) {
                    if ( larva.canTrain(true) ) {
                        foundCommandableLarva = true;
                        thisUnit = larva;
                        break;
                    }
                }
                if (!foundCommandableLarva){
                    return false;
                }
            }
            else if (isConstructing() || !isCompleted() ){
                return false;
            }
        }

        if ( !game.canMake(uType, thisUnit) ){
            return false;
        }

        if ( uType.isAddon() || ( uType.isBuilding() && !thisUnit.getType().isBuilding() ) ){
            return false;
        }
        return uType != Zerg_Larva && uType != Zerg_Egg && uType != Zerg_Cocoon;
    }

    public boolean canMorph() {
        return canMorph(true);
    }

    public boolean canMorph(boolean checkCommandibility) {
        if ( checkCommandibility && !canCommand() ) {
            return false;
        }

        final UnitType ut = getType();
        if (ut.producesLarva() ) {
            if ( !isConstructing() && isCompleted() && ( !ut.isBuilding() || isIdle() ) ) {
                return true;
            }
            for (Unit larva : getLarva()) {
                if ( !larva.isConstructing() && larva.isCompleted() && larva.canCommand() ) {
                    return true;
                }
            }
            return false;
        }

        if ( isConstructing() || !isCompleted()   || (ut.isBuilding() && !isIdle()) ) {
            return false;
        }

        if ( ut != Zerg_Hydralisk &&
                ut != Zerg_Mutalisk &&
                ut != Zerg_Creep_Colony &&
                ut != Zerg_Spire &&
                ut != Zerg_Hatchery &&
                ut != Zerg_Lair &&
                ut != Zerg_Hive &&
                ut != Zerg_Larva ) {
            return false;
        }
        return !isHallucination();
    }

    public boolean canMorph(UnitType uType, boolean checkCanIssueCommandType);

    public boolean canMorph(UnitType uType) {

    }

    public boolean canMorph(UnitType uType, boolean checkCanIssueCommandType, boolean checkCommandibility) {
        if ( checkCommandibility && !canCommand() ) {
            return false;
        }

        if ( checkCanIssueCommandType && !canMorph( false) )
            return false;

        Unit thisUnit = this;
        if ( getType().producesLarva()) {
            if ( uType.whatBuilds().getKey() == Zerg_Larva ) {
                boolean foundCommandableLarva = false;
                for (Unit larva : getLarva()) {
                    if ( larva.canMorph(true) ) {
                        foundCommandableLarva = true;
                        thisUnit = larva;
                        break;
                    }
                }
                if (!foundCommandableLarva) {
                    return false;
                }
            }
            else if ( isConstructing() || !isCompleted() || (getType().isBuilding() && !isIdle()) ) {
                return false;
            }
        }

        if ( !game.canMake(uType, thisUnit) ) {
            return false;
        }
        return uType != Zerg_Larva && uType != Zerg_Egg && uType != Zerg_Cocoon;
    }

    //TODO
    // https://github.com/bwapi/bwapi/blob/456ad612abc84da4103162ba0bf8ec4f053a4b1d/bwapi/Shared/Templates.h#L1033
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
