package bwapi;

import java.util.function.Predicate;

public interface UnitFilter extends Predicate<Unit> {
    UnitFilter IsTransPort = u -> u.getType().spaceProvided() > 0 && u.getType() != UnitType.Terran_Bunker;
    UnitFilter CanProduce = u -> u.getType().canProduce();
    UnitFilter CanAttack = u -> u.getType().canAttack();
    UnitFilter CanMove = u -> u.getType().canMove();
    UnitFilter IsFlyer = u -> u.getType().isFlyer();
    UnitFilter IsFlying = Unit::isFlying;
    UnitFilter RegeneratesHP = u -> u.getType().regeneratesHP();
    UnitFilter IsSpellcaster = u -> u.getType().isSpellcaster();
    UnitFilter HasPermanentCloak = u -> u.getType().hasPermanentCloak();
    UnitFilter IsOrganic = u -> u.getType().isOrganic();
    UnitFilter IsMechanical = u -> u.getType().isMechanical();
    UnitFilter IsRobotic = u -> u.getType().isRobotic();
    UnitFilter IsDetector = u -> u.getType().isDetector();
    UnitFilter IsResourceContainer = u -> u.getType().isResourceContainer();
    UnitFilter IsResourceDepot = u -> u.getType().isResourceDepot();
    UnitFilter IsRefinery = u -> u.getType().isRefinery();
    UnitFilter IsWorker = u -> u.getType().isWorker();
    UnitFilter RequiresPsi = u -> u.getType().requiresPsi();
    UnitFilter RequiresCreep = u -> u.getType().requiresCreep();
    UnitFilter IsBurrowable = u -> u.getType().isBurrowable();
    UnitFilter IsCloakable = u -> u.getType().isCloakable();
    UnitFilter IsBuilding = u -> u.getType().isBuilding();
    UnitFilter IsAddon = u -> u.getType().isAddon();
    UnitFilter IsFlyingBuilding = u -> u.getType().isFlyingBuilding();
    UnitFilter IsNeutral = u -> u.getType().isNeutral();
    UnitFilter IsHero = u -> u.getType().isHero();
    UnitFilter IsPowerup = u -> u.getType().isPowerup();
    UnitFilter IsBeacon = u -> u.getType().isBeacon();
    UnitFilter IsFlagBeacon = u -> u.getType().isFlagBeacon();
    UnitFilter IsSpecialBuilding = u -> u.getType().isSpecialBuilding();
    UnitFilter IsSpell = u -> u.getType().isSpell();
    UnitFilter ProducesLarva = u -> u.getType().producesLarva();
    UnitFilter IsMineralField = u -> u.getType().isMineralField();
    UnitFilter IsCritter = u -> u.getType().isCritter();
    UnitFilter CanBuildAddon = u -> u.getType().canBuildAddon();

    // static UnitFilter example = HP(x -> x > 40);
    static UnitFilter HP(Predicate<Integer> c) {
        return u -> c.test(u.getHitPoints());
    }

    static UnitFilter MaxHP(Predicate<Integer> c) {
        return u -> c.test(u.getType().maxHitPoints());
    }

    static UnitFilter HP_Percent(Predicate<Integer> c) {
        return u -> c.test((u.getType().maxHitPoints() != 0) ? ((u.getHitPoints() * 100) / u.getType().maxHitPoints()) : 0);
    }

    static UnitFilter Shields(Predicate<Integer> c) {
        return u -> c.test(u.getShields());
    }

    static UnitFilter MaxShields(Predicate<Integer> c) {
        return u -> c.test(u.getType().maxShields());
    }

    static UnitFilter Shields_Percent(Predicate<Integer> c) {
        return u -> c.test((u.getType().maxShields() != 0) ? ((u.getShields() * 100) / u.getType().maxShields()) : 0);
    }

    static UnitFilter Energy(Predicate<Integer> c) {
        return u -> c.test(u.getEnergy());
    }

    static UnitFilter MaxEnergy(Predicate<Integer> c) {
        return u -> c.test(u.getPlayer().maxEnergy(u.getType()));
    }

    static UnitFilter Energy_Percent(Predicate<Integer> c) {
        return u -> c.test((u.getPlayer().maxEnergy(u.getType()) != 0) ? ((u.getEnergy() * 100) / u.getPlayer().maxEnergy(u.getType())) : 0);
    }

    static UnitFilter Armor(Predicate<Integer> c) {
        return u -> c.test(u.getPlayer().armor(u.getType()));
    }

    static UnitFilter ArmorUpgrade(Predicate<UpgradeType> c) {
        return u -> c.test(u.getType().armorUpgrade());
    }

    static UnitFilter MineralPrice(Predicate<Integer> c) {
        return u -> c.test(u.getType().mineralPrice());
    }

    static UnitFilter GasPrice(Predicate<Integer> c) {
        return u -> c.test(u.getType().gasPrice());
    }

    static UnitFilter BuildTime(Predicate<Integer> c) {
        return u -> c.test(u.getType().buildTime());
    }

    static UnitFilter SupplyRequired(Predicate<Integer> c) {
        return u -> c.test(u.getType().supplyRequired());
    }

    static UnitFilter SupplyProvided(Predicate<Integer> c) {
        return u -> c.test(u.getType().supplyProvided());
    }

    static UnitFilter SpaceRequired(Predicate<Integer> c) {
        return u -> c.test(u.getType().spaceRequired());
    }

    static UnitFilter SpaceRemaining(Predicate<Integer> c) {
        return u -> c.test(u.getSpaceRemaining());
    }

    static UnitFilter SpaceProvided(Predicate<Integer> c) {
        return u -> c.test(u.getType().spaceProvided());
    }

    static UnitFilter BuildScore(Predicate<Integer> c) {
        return u -> c.test(u.getType().buildScore());
    }

    static UnitFilter DestroyScore(Predicate<Integer> c) {
        return u -> c.test(u.getType().destroyScore());
    }

    static UnitFilter TopSpeed(Predicate<Double> c) {
        return u -> c.test(u.getPlayer().topSpeed(u.getType()));
    }

    static UnitFilter SightRange(Predicate<Integer> c) {
        return u -> c.test(u.getPlayer().sightRange(u.getType()));
    }

    static UnitFilter MaxWeaponCooldown(Predicate<Integer> c) {
        return u -> c.test(u.getPlayer().weaponDamageCooldown(u.getType()));
    }

    static UnitFilter SizeType(Predicate<UnitSizeType> c) {
        return u -> c.test(u.getType().size());
    }

    static UnitFilter GroundWeapon(Predicate<WeaponType> c) {
        return u -> c.test(u.getType().groundWeapon());
    }

    static UnitFilter AirWeapon(Predicate<WeaponType> c) {
        return u -> c.test(u.getType().airWeapon());
    }

    static UnitFilter GetType(Predicate<UnitType> c) {
        return u -> c.test(u.getType());
    }

    static UnitFilter GetRace(Predicate<Race> c) {
        return u -> c.test(u.getType().getRace());
    }

    static UnitFilter GetPlayer(Predicate<Player> c) {
        return u -> c.test(u.getPlayer());
    }

    static UnitFilter Resources(Predicate<Integer> c) {
        return u -> c.test(u.getResources());
    }

    static UnitFilter ResourceGroup(Predicate<Integer> c) {
        return u -> c.test(u.getResourceGroup());
    }

    static UnitFilter AcidSporeCount(Predicate<Integer> c) {
        return u -> c.test(u.getAcidSporeCount());
    }

    static UnitFilter InterceptorCount(Predicate<Integer> c) {
        return u -> c.test(u.getInterceptorCount());
    }

    static UnitFilter ScarabCount(Predicate<Integer> c) {
        return u -> c.test(u.getScarabCount());
    }

    static UnitFilter SpiderMineCount(Predicate<Integer> c) {
        return u -> c.test(u.getSpiderMineCount());
    }

    static UnitFilter WeaponCooldown(Predicate<Integer> c) {
        return u -> c.test(u.getGroundWeaponCooldown());
    }

    static UnitFilter SpellCooldown(Predicate<Integer> c) {
        return u -> c.test(u.getSpellCooldown());
    }

    static UnitFilter DefenseMatrixPoints(Predicate<Integer> c) {
        return u -> c.test(u.getDefenseMatrixPoints());
    }

    static UnitFilter DefenseMatrixTime(Predicate<Integer> c) {
        return u -> c.test(u.getDefenseMatrixTimer());
    }

    static UnitFilter EnsnareTime(Predicate<Integer> c) {
        return u -> c.test(u.getEnsnareTimer());
    }

    static UnitFilter IrradiateTime(Predicate<Integer> c) {
        return u -> c.test(u.getIrradiateTimer());
    }

    static UnitFilter LockdownTime(Predicate<Integer> c) {
        return u -> c.test(u.getLockdownTimer());
    }

    static UnitFilter MaelstromTime(Predicate<Integer> c) {
        return u -> c.test(u.getMaelstromTimer());
    }

    static UnitFilter OrderTime(Predicate<Integer> c) {
        return u -> c.test(u.getOrderTimer());
    }

    static UnitFilter PlagueTimer(Predicate<Integer> c) {
        return u -> c.test(u.getPlagueTimer());
    }

    static UnitFilter RemoveTime(Predicate<Integer> c) {
        return u -> c.test(u.getRemoveTimer());
    }

    static UnitFilter StasisTime(Predicate<Integer> c) {
        return u -> c.test(u.getStasisTimer());
    }

    static UnitFilter StimTime(Predicate<Integer> c) {
        return u -> c.test(u.getStimTimer());
    }

    static UnitFilter BuildType(Predicate<UnitType> c) {
        return u -> c.test(u.getBuildType());
    }

    static UnitFilter RemainingBuildTime(Predicate<Integer> c) {
        return u -> c.test(u.getRemainingBuildTime());
    }

    static UnitFilter RemainingTrainTime(Predicate<Integer> c) {
        return u -> c.test(u.getRemainingTrainTime());
    }

    static UnitFilter Target(Predicate<Unit> c) {
        return u -> c.test(u.getTarget());
    }

    static UnitFilter CurrentOrder(Predicate<Order> c) {
        return u -> c.test(u.getOrder());
    }

    static UnitFilter SecondaryOrder(Predicate<Order> c) {
        return u -> c.test(u.getSecondaryOrder());
    }

    static UnitFilter OrderTarget(Predicate<Unit> c) {
        return u -> c.test(u.getOrderTarget());
    }

    static UnitFilter GetLeft(Predicate<Integer> c) {
        return u -> c.test(u.getLeft());
    }

    static UnitFilter GetTop(Predicate<Integer> c) {
        return u -> c.test(u.getTop());
    }

    static UnitFilter GetRight(Predicate<Integer> c) {
        return u -> c.test(u.getRight());
    }

    static UnitFilter GetBottom(Predicate<Integer> c) {
        return u -> c.test(u.getBottom());
    }

    UnitFilter Exists = Unit::exists;
    UnitFilter IsAttacking = Unit::isAttacking;
    UnitFilter IsBeingConstructed = Unit::isBeingConstructed;
    UnitFilter IsBeingGathered = Unit::isBeingGathered;
    UnitFilter IsBeingHealed = Unit::isBeingHealed;
    UnitFilter IsBlind = Unit::isBlind;
    UnitFilter IsBraking = Unit::isBraking;
    UnitFilter IsBurrowed = Unit::isBurrowed;
    UnitFilter IsCarryingGas = Unit::isCarryingGas;
    UnitFilter IsCarryingMinerals = Unit::isCarryingMinerals;
    UnitFilter IsCarryingSomething = u -> u.isCarryingMinerals() || u.isCarryingGas();
    UnitFilter IsCloaked = Unit::isCloaked;
    UnitFilter IsCompleted = Unit::isCompleted;
    UnitFilter IsConstructing = Unit::isConstructing;
    UnitFilter IsDefenseMatrixed = Unit::isDefenseMatrixed;
    UnitFilter IsDetected = Unit::isDetected;
    UnitFilter IsEnsnared = Unit::isEnsnared;
    UnitFilter IsFollowing = Unit::isFollowing;
    UnitFilter IsGatheringGas = Unit::isGatheringGas;
    UnitFilter IsGatheringMinerals = Unit::isGatheringMinerals;
    UnitFilter IsHallucination = Unit::isHallucination;
    UnitFilter IsHoldingPosition = Unit::isHoldingPosition;
    UnitFilter IsIdle = Unit::isIdle;
    UnitFilter IsInterruptible = Unit::isInterruptible;
    UnitFilter IsInvincible = Unit::isInvincible;
    UnitFilter IsIrradiated = Unit::isIrradiated;
    UnitFilter IsLifted = Unit::isLifted;
    UnitFilter IsLoaded = Unit::isLoaded;
    UnitFilter IsLockedDown = Unit::isLockedDown;
    UnitFilter IsMaelstrommed = Unit::isMaelstrommed;
    UnitFilter IsMorphing = Unit::isMorphing;
    UnitFilter IsMoving = Unit::isMoving;
    UnitFilter IsParasited = Unit::isParasited;
    UnitFilter IsPatrolling = Unit::isPatrolling;
    UnitFilter IsPlagued = Unit::isPlagued;
    UnitFilter IsRepairing = Unit::isRepairing;
    UnitFilter IsResearching = Unit::isResearching;
    UnitFilter IsSieged = Unit::isSieged;
    UnitFilter IsStartingAttack = Unit::isStartingAttack;
    UnitFilter IsStasised = Unit::isStasised;
    UnitFilter IsStimmed = Unit::isStimmed;
    UnitFilter IsStuck = Unit::isStuck;
    UnitFilter IsTraining = Unit::isTraining;
    UnitFilter IsUnderAttack = Unit::isUnderAttack;
    UnitFilter IsUnderDarkSwarm = Unit::isUnderDarkSwarm;
    UnitFilter IsUnderDisruptionWeb = Unit::isUnderDisruptionWeb;
    UnitFilter IsUnderStorm = Unit::isUnderStorm;
    UnitFilter IsPowered = Unit::isPowered;
    UnitFilter IsVisible = Unit::isVisible;
}

