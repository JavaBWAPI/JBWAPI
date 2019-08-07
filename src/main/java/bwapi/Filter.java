package bwapi;

public class Filter {
    public static final UnitFilter IsTransPort = (u -> u.getType().spaceProvided() > 0 && u.getType() != UnitType.Terran_Bunker);
    public static final UnitFilter CanProduce = (u) -> u.getType().canProduce();
    public static final UnitFilter CanAttack = (u) -> u.getType().canAttack();
    public static final UnitFilter CanMove = (u) -> u.getType().canMove();
    public static final UnitFilter IsFlyer = (u) -> u.getType().isFlyer();
    public static final UnitFilter IsFlying = (u) -> u.isFlying();
    public static final UnitFilter RegeneratesHP = (u) -> u.getType().regeneratesHP();
    public static final UnitFilter IsSpellcaster = (u) -> u.getType().isSpellcaster();
    public static final UnitFilter HasPermanentCloak = (u) -> u.getType().hasPermanentCloak();
    public static final UnitFilter IsOrganic = (u) -> u.getType().isOrganic();
    public static final UnitFilter IsMechanical = (u) -> u.getType().isMechanical();
    public static final UnitFilter IsRobotic = (u) -> u.getType().isRobotic();
    public static final UnitFilter IsDetector = (u) -> u.getType().isDetector();
    public static final UnitFilter IsResourceContainer = (u) -> u.getType().isResourceContainer();
    public static final UnitFilter IsResourceDepot = (u) -> u.getType().isResourceDepot();
    public static final UnitFilter IsRefinery = (u) -> u.getType().isRefinery();
    public static final UnitFilter IsWorker = (u) -> u.getType().isWorker();
    public static final UnitFilter RequiresPsi = (u) -> u.getType().requiresPsi();
    public static final UnitFilter RequiresCreep = (u) -> u.getType().requiresCreep();
    public static final UnitFilter IsBurrowable = (u) -> u.getType().isBurrowable();
    public static final UnitFilter IsCloakable = (u) -> u.getType().isCloakable();
    public static final UnitFilter IsBuilding = (u) -> u.getType().isBuilding();
    public static final UnitFilter IsAddon = (u) -> u.getType().isAddon();
    public static final UnitFilter IsFlyingBuilding = (u) -> u.getType().isFlyingBuilding();
    public static final UnitFilter IsNeutral = (u) -> u.getType().isNeutral();
    public static final UnitFilter IsHero = (u) -> u.getType().isHero();
    public static final UnitFilter IsPowerup = (u) -> u.getType().isPowerup();
    public static final UnitFilter IsBeacon = (u) -> u.getType().isBeacon();
    public static final UnitFilter IsFlagBeacon = (u) -> u.getType().isFlagBeacon();
    public static final UnitFilter IsSpecialBuilding = (u) -> u.getType().isSpecialBuilding();
    public static final UnitFilter IsSpell = (u) -> u.getType().isSpell();
    public static final UnitFilter ProducesLarva = (u) -> u.getType().producesLarva();
    public static final UnitFilter IsMineralField = (u) -> u.getType().isMineralField();
    public static final UnitFilter IsCritter = (u) -> u.getType().isCritter();
    public static final UnitFilter CanBuildAddon = (u) -> u.getType().canBuildAddon();

    public interface Condition<T> {
        boolean apply(T t);
    }

    // static UnitFilter example = HP((x) -> x > 40);
    public static UnitFilter HP(Condition<Integer> c) {
        return u -> c.apply(u.getHitPoints());
    }

    public static UnitFilter MaxHP(Condition<Integer> c) {
        return u -> c.apply(u.getType().maxHitPoints());
    }

    public static UnitFilter HP_Percent(Condition<Integer> c) {
        return u -> c.apply((u.getType().maxHitPoints() != 0) ? ((u.getHitPoints() * 100) / u.getType().maxHitPoints()) : 0);
    }

    public static UnitFilter Shields(Condition<Integer> c) {
        return u -> c.apply(u.getShields());
    }

    public static UnitFilter MaxShields(Condition<Integer> c) {
        return u -> c.apply(u.getType().maxShields());
    }

    public static UnitFilter Shields_Percent(Condition<Integer> c) {
        return u -> c.apply((u.getType().maxShields() != 0) ? ((u.getShields() * 100) / u.getType().maxShields()) : 0);
    }

    public static UnitFilter Energy(Condition<Integer> c) {
        return u -> c.apply(u.getEnergy());
    }

    public static UnitFilter MaxEnergy(Condition<Integer> c) {
        return u -> c.apply(u.getPlayer().maxEnergy(u.getType()));
    }

    public static UnitFilter Energy_Percent(Condition<Integer> c) {
        return u -> c.apply((u.getPlayer().maxEnergy(u.getType()) != 0) ? ((u.getEnergy() * 100) / u.getPlayer().maxEnergy(u.getType())) : 0);
    }

    public static UnitFilter Armor(Condition<Integer> c) {
        return u -> c.apply(u.getPlayer().armor(u.getType()));
    }

    public static UnitFilter ArmorUpgrade(Condition<UpgradeType> c) {
        return u -> c.apply(u.getType().armorUpgrade());
    }

    public static UnitFilter MineralPrice(Condition<Integer> c) {
        return u -> c.apply(u.getType().mineralPrice());
    }

    public static UnitFilter GasPrice(Condition<Integer> c) {
        return u -> c.apply(u.getType().gasPrice());
    }

    public static UnitFilter BuildTime(Condition<Integer> c) {
        return u -> c.apply(u.getType().buildTime());
    }

    public static UnitFilter SupplyRequired(Condition<Integer> c) {
        return u -> c.apply(u.getType().supplyRequired());
    }

    public static UnitFilter SupplyProvided(Condition<Integer> c) {
        return u -> c.apply(u.getType().supplyProvided());
    }

    public static UnitFilter SpaceRequired(Condition<Integer> c) {
        return u -> c.apply(u.getType().spaceRequired());
    }

    public static UnitFilter SpaceRemaining(Condition<Integer> c) {
        return u -> c.apply(u.getSpaceRemaining());
    }

    public static UnitFilter SpaceProvided(Condition<Integer> c) {
        return u -> c.apply(u.getType().spaceProvided());
    }

    public static UnitFilter BuildScore(Condition<Integer> c) {
        return u -> c.apply(u.getType().buildScore());
    }

    public static UnitFilter DestroyScore(Condition<Integer> c) {
        return u -> c.apply(u.getType().destroyScore());
    }

    public static UnitFilter TopSpeed(Condition<Double> c) {
        return u -> c.apply(u.getPlayer().topSpeed(u.getType()));
    }

    public static UnitFilter SightRange(Condition<Integer> c) {
        return u -> c.apply(u.getPlayer().sightRange(u.getType()));
    }

    public static UnitFilter MaxWeaponCooldown(Condition<Integer> c) {
        return u -> c.apply(u.getPlayer().weaponDamageCooldown(u.getType()));
    }

    public static UnitFilter SizeType(Condition<UnitSizeType> c) {
        return u -> c.apply(u.getType().size());
    }

    public static UnitFilter GroundWeapon(Condition<WeaponType> c) {
        return u -> c.apply(u.getType().groundWeapon());
    }

    public static UnitFilter AirWeapon(Condition<WeaponType> c) {
        return u -> c.apply(u.getType().airWeapon());
    }

    public static UnitFilter GetType(Condition<UnitType> c) {
        return u -> c.apply(u.getType());
    }

    public static UnitFilter GetRace(Condition<Race> c) {
        return u -> c.apply(u.getType().getRace());
    }

    public static UnitFilter GetPlayer(Condition<Player> c) {
        return u -> c.apply(u.getPlayer());
    }

    public static UnitFilter Resources(Condition<Integer> c) {
        return u -> c.apply(u.getResources());
    }

    public static UnitFilter ResourceGroup(Condition<Integer> c) {
        return u -> c.apply(u.getResourceGroup());
    }

    public static UnitFilter AcidSporeCount(Condition<Integer> c) {
        return u -> c.apply(u.getAcidSporeCount());
    }

    public static UnitFilter InterceptorCount(Condition<Integer> c) {
        return u -> c.apply(u.getInterceptorCount());
    }

    public static UnitFilter ScarabCount(Condition<Integer> c) {
        return u -> c.apply(u.getScarabCount());
    }

    public static UnitFilter SpiderMineCount(Condition<Integer> c) {
        return u -> c.apply(u.getSpiderMineCount());
    }

    public static UnitFilter WeaponCooldown(Condition<Integer> c) {
        return u -> c.apply(u.getGroundWeaponCooldown());
    }

    public static UnitFilter SpellCooldown(Condition<Integer> c) {
        return u -> c.apply(u.getSpellCooldown());
    }

    public static UnitFilter DefenseMatrixPoints(Condition<Integer> c) {
        return u -> c.apply(u.getDefenseMatrixPoints());
    }

    public static UnitFilter DefenseMatrixTime(Condition<Integer> c) {
        return u -> c.apply(u.getDefenseMatrixTimer());
    }

    public static UnitFilter EnsnareTime(Condition<Integer> c) {
        return u -> c.apply(u.getEnsnareTimer());
    }

    public static UnitFilter IrradiateTime(Condition<Integer> c) {
        return u -> c.apply(u.getIrradiateTimer());
    }

    public static UnitFilter LockdownTime(Condition<Integer> c) {
        return u -> c.apply(u.getLockdownTimer());
    }

    public static UnitFilter MaelstromTime(Condition<Integer> c) {
        return u -> c.apply(u.getMaelstromTimer());
    }

    public static UnitFilter OrderTime(Condition<Integer> c) {
        return u -> c.apply(u.getOrderTimer());
    }

    public static UnitFilter PlagueTimer(Condition<Integer> c) {
        return u -> c.apply(u.getPlagueTimer());
    }

    public static UnitFilter RemoveTime(Condition<Integer> c) {
        return u -> c.apply(u.getRemoveTimer());
    }

    public static UnitFilter StasisTime(Condition<Integer> c) {
        return u -> c.apply(u.getStasisTimer());
    }

    public static UnitFilter StimTime(Condition<Integer> c) {
        return u -> c.apply(u.getStimTimer());
    }

    public static UnitFilter BuildType(Condition<UnitType> c) {
        return u -> c.apply(u.getBuildType());
    }

    public static UnitFilter RemainingBuildTime(Condition<Integer> c) {
        return u -> c.apply(u.getRemainingBuildTime());
    }

    public static UnitFilter RemainingTrainTime(Condition<Integer> c) {
        return u -> c.apply(u.getRemainingTrainTime());
    }

    public static UnitFilter Target(Condition<Unit> c) {
        return u -> c.apply(u.getTarget());
    }

    public static UnitFilter CurrentOrder(Condition<Order> c) {
        return u -> c.apply(u.getOrder());
    }

    public static UnitFilter SecondaryOrder(Condition<Order> c) {
        return u -> c.apply(u.getSecondaryOrder());
    }

    public static UnitFilter OrderTarget(Condition<Unit> c) {
        return u -> c.apply(u.getOrderTarget());
    }

    public static UnitFilter GetLeft(Condition<Integer> c) {
        return u -> c.apply(u.getLeft());
    }

    public static UnitFilter GetTop(Condition<Integer> c) {
        return u -> c.apply(u.getTop());
    }

    public static UnitFilter GetRight(Condition<Integer> c) {
        return u -> c.apply(u.getRight());
    }

    public static UnitFilter GetBottom(Condition<Integer> c) {
        return u -> c.apply(u.getBottom());
    }

    public static final UnitFilter Exists = (u) -> u.exists();
    public static final UnitFilter IsAttacking = (u) -> u.isAttacking();
    public static final UnitFilter IsBeingConstructed = (u) -> u.isBeingConstructed();
    public static final UnitFilter IsBeingGathered = (u) -> u.isBeingGathered();
    public static final UnitFilter IsBeingHealed = (u) -> u.isBeingHealed();
    public static final UnitFilter IsBlind = (u) -> u.isBlind();
    public static final UnitFilter IsBraking = (u) -> u.isBraking();
    public static final UnitFilter IsBurrowed = (u) -> u.isBurrowed();
    public static final UnitFilter IsCarryingGas = (u) -> u.isCarryingGas();
    public static final UnitFilter IsCarryingMinerals = (u) -> u.isCarryingMinerals();
    public static final UnitFilter IsCarryingSomething = (u) -> u.isCarryingMinerals() || u.isCarryingGas();
    public static final UnitFilter IsCloaked = (u) -> u.isCloaked();
    public static final UnitFilter IsCompleted = (u) -> u.isCompleted();
    public static final UnitFilter IsConstructing = (u) -> u.isConstructing();
    public static final UnitFilter IsDefenseMatrixed = (u) -> u.isDefenseMatrixed();
    public static final UnitFilter IsDetected = (u) -> u.isDetected();
    public static final UnitFilter IsEnsnared = (u) -> u.isEnsnared();
    public static final UnitFilter IsFollowing = (u) -> u.isFollowing();
    public static final UnitFilter IsGatheringGas = (u) -> u.isGatheringGas();
    public static final UnitFilter IsGatheringMinerals = (u) -> u.isGatheringMinerals();
    public static final UnitFilter IsHallucination = (u) -> u.isHallucination();
    public static final UnitFilter IsHoldingPosition = (u) -> u.isHoldingPosition();
    public static final UnitFilter IsIdle = (u) -> u.isIdle();
    public static final UnitFilter IsInterruptible = (u) -> u.isInterruptible();
    public static final UnitFilter IsInvincible = (u) -> u.isInvincible();
    public static final UnitFilter IsIrradiated = (u) -> u.isIrradiated();
    public static final UnitFilter IsLifted = (u) -> u.isLifted();
    public static final UnitFilter IsLoaded = (u) -> u.isLoaded();
    public static final UnitFilter IsLockedDown = (u) -> u.isLockedDown();
    public static final UnitFilter IsMaelstrommed = (u) -> u.isMaelstrommed();
    public static final UnitFilter IsMorphing = (u) -> u.isMorphing();
    public static final UnitFilter IsMoving = (u) -> u.isMoving();
    public static final UnitFilter IsParasited = (u) -> u.isParasited();
    public static final UnitFilter IsPatrolling = (u) -> u.isPatrolling();
    public static final UnitFilter IsPlagued = (u) -> u.isPlagued();
    public static final UnitFilter IsRepairing = (u) -> u.isRepairing();
    public static final UnitFilter IsResearching = (u) -> u.isResearching();
    public static final UnitFilter IsSieged = (u) -> u.isSieged();
    public static final UnitFilter IsStartingAttack = (u) -> u.isStartingAttack();
    public static final UnitFilter IsStasised = (u) -> u.isStasised();
    public static final UnitFilter IsStimmed = (u) -> u.isStimmed();
    public static final UnitFilter IsStuck = (u) -> u.isStuck();
    public static final UnitFilter IsTraining = (u) -> u.isTraining();
    public static final UnitFilter IsUnderAttack = (u) -> u.isUnderAttack();
    public static final UnitFilter IsUnderDarkSwarm = (u) -> u.isUnderDarkSwarm();
    public static final UnitFilter IsUnderDisruptionWeb = (u) -> u.isUnderDisruptionWeb();
    public static final UnitFilter IsUnderStorm = (u) -> u.isUnderStorm();
    public static final UnitFilter IsPowered = (u) -> u.isPowered();
    public static final UnitFilter IsVisible = (u) -> u.isVisible();
}
