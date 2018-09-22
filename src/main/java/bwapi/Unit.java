package bwapi;

import bwapi.point.Position;
import bwapi.point.TilePosition;
import bwapi.types.*;

import java.util.List;

public class Unit {
    private final int id;
    private final Game game;

    Unit(final int id, final Game game) {
        this.id = id;
        this.game = game;
    }

    public int getID() {
        return id;
    }

    /*

    public boolean exists();

    public int getReplayID();

    */
    public Player getPlayer() {
        return null;
    }
    /*
    public UnitType getType();

    public Position getPosition();

    public TilePosition getTilePosition();

    public double getAngle();

    public double getVelocityX();

    public double getVelocityY();

    public Region getRegion();

    public int getLeft();

    public int getTop();

    public int getRight();

    public int getBottom();

    public int getHitPoints();

    public int getShields();

    public int getEnergy();

    public int getResources();

    public int getResourceGroup();

    public int getDistance(Position target);

    public int getDistance(Unit target);

    public boolean hasPath(Position target);

    public boolean hasPath(Unit target);

    public int getLastCommandFrame();

    public UnitCommand getLastCommand();

    public Player getLastAttackingPlayer();

    public UnitType getInitialType();

    public Position getInitialPosition();

    public TilePosition getInitialTilePosition();

    public int getInitialHitPoints();

    public int getInitialResources();

    public int getKillCount();

    public int getAcidSporeCount();

    public int getInterceptorCount();

    public int getScarabCount();

    public int getSpiderMineCount();

    public int getGroundWeaponCooldown();

    public int getAirWeaponCooldown();

    public int getSpellCooldown();

    public int getDefenseMatrixPoints();

    public int getDefenseMatrixTimer();

    public int getEnsnareTimer();

    public int getIrradiateTimer();

    public int getLockdownTimer();

    public int getMaelstromTimer();

    public int getOrderTimer();

    public int getPlagueTimer();

    public int getRemoveTimer();

    public int getStasisTimer();

    public int getStimTimer();

    public UnitType getBuildType();

    public List<UnitType> getTrainingQueue();

    public TechType getTech();

    public UpgradeType getUpgrade();

    public int getRemainingBuildTime();

    public int getRemainingTrainTime();

    public int getRemainingResearchTime();

    public int getRemainingUpgradeTime();

    public Unit getBuildUnit();

    public Unit getTarget();

    public Position getTargetPosition();

    public Order getOrder();

    public Order getSecondaryOrder();

    public Unit getOrderTarget();

    public Position getOrderTargetPosition();

    public Position getRallyPosition();

    public Unit getRallyUnit();

    public Unit getAddon();

    public Unit getNydusExit();

    public Unit getPowerUp();

    public Unit getTransport();

    public List<Unit> getLoadedUnits();

    public int getSpaceRemaining();

    public Unit getCarrier();

    public List<Unit> getInterceptors();

    public Unit getHatchery();

    public List<Unit> getLarva();

    public List<Unit> getUnitsInRadius(int radius);

    public List<Unit> getUnitsInWeaponRange(WeaponType weapon);

    public boolean hasNuke();

    public boolean isAccelerating();

    public boolean isAttacking();

    public boolean isAttackFrame();

    public boolean isBeingConstructed();

    public boolean isBeingGathered();

    public boolean isBeingHealed();

    public boolean isBlind();

    public boolean isBraking();

    public boolean isBurrowed();

    public boolean isCarryingGas();

    public boolean isCarryingMinerals();

    public boolean isCloaked();

    public boolean isCompleted();

    public boolean isConstructing();

    public boolean isDefenseMatrixed();

    public boolean isDetected();

    public boolean isEnsnared();

    public boolean isFlying();

    public boolean isFollowing();

    public boolean isGatheringGas();

    public boolean isGatheringMinerals();

    public boolean isHallucination();

    public boolean isHoldingPosition();

    public boolean isIdle();

    public boolean isInterruptible();

    public boolean isInvincible();

    public boolean isInWeaponRange(Unit target);

    public boolean isIrradiated();

    public boolean isLifted();

    public boolean isLoaded();

    public boolean isLockedDown();

    public boolean isMaelstrommed();

    public boolean isMorphing();

    public boolean isMoving();

    public boolean isParasited();

    public boolean isPatrolling();

    public boolean isPlagued();

    public boolean isRepairing();

    public boolean isResearching();

    public boolean isSelected();

    public boolean isSieged();

    public boolean isStartingAttack();

    public boolean isStasised();

    public boolean isStimmed();

    public boolean isStuck();

    public boolean isTraining();

    public boolean isUnderAttack();

    public boolean isUnderDarkSwarm();

    public boolean isUnderDisruptionWeb();

    public boolean isUnderStorm();

    public boolean isPowered();

    public boolean isUpgrading();

    public boolean isVisible();

    public boolean isVisible(Player player);

    public boolean isTargetable();

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
