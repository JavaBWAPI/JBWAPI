package bwapi;


import bwapi.ClientData.UnitData;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static bwapi.Order.*;
import static bwapi.Race.Terran;
import static bwapi.Race.Zerg;
import static bwapi.UnitType.*;

/**
 * The {@link Unit} class is used to get information about individual units as well as issue
 * orders to units. Each unit in the game has a unique {@link Unit} object, and {@link Unit} objects
 * are not deleted until the end of the match (so you don't need to worry about unit pointers
 * becoming invalid).
 * <p>
 * Every Unit in the game is either accessible or inaccessible. To determine if an AI can access
 * a particular unit, BWAPI checks to see if {@link Flag#CompleteMapInformation} is enabled. So there
 * are two cases to consider - either the flag is enabled, or it is disabled:
 * <p>
 * If {@link Flag#CompleteMapInformation} is disabled, then a unit is accessible if and only if it is visible.
 * <p>
 * Some properties of visible enemy units will not be made available to the AI (such as the
 * contents of visible enemy dropships). If a unit is not visible, {@link Unit#exists} will return false,
 * regardless of whether or not the unit exists. This is because absolutely no state information on
 * invisible enemy units is made available to the AI. To determine if an enemy unit has been destroyed, the
 * AI must watch for {@link BWEventListener#onUnitDestroy} messages from BWAPI, which is only called for visible units
 * which get destroyed.
 * <p>
 * If {@link Flag#CompleteMapInformation} is enabled, then all units that exist in the game are accessible, and
 * {@link Unit#exists} is accurate for all units. Similarly {@link BWEventListener#onUnitDestroy} messages are generated for all
 * units that get destroyed, not just visible ones.
 * <p>
 * If a Unit is not accessible, then only the getInitial__ functions will be available to the AI.
 * However for units that were owned by the player, {@link #getPlayer} and {@link #getType} will continue to work for units
 * that have been destroyed.
 */
public class Unit implements Comparable<Unit> {
    private static final Set<Order> gatheringGasOrders = EnumSet.of(
            Harvest1, Harvest2, MoveToGas, WaitForGas, HarvestGas, ReturnGas, ResetCollision);
    private static final Set<Order> gatheringMineralOrders = EnumSet.of(
            Harvest1, Harvest2, MoveToMinerals, WaitForMinerals, MiningMinerals, ReturnMinerals, ResetCollision);
    private final UnitData unitData;
    private final Game game;
    // static
    private final UnitType initialType;
    private final int initialResources;
    private final int initialHitPoints;
    private final Position initialPosition;
    private final TilePosition initialTilePosition;
    private final int id;
    // variable
    private Position position;
    private int lastPositionUpdate = -1;
    private int lastCommandFrame;
    private UnitCommand lastCommand;

    // Don't make non-latcom users pay for latcom in memory usage
    private UnitSelf self = null;
    UnitSelf self() {
        if (self == null) {
            self = new UnitSelf();
        }
        return self;
    }


    Unit(final UnitData unitData, int id, final Game game) {
        this.unitData = unitData;
        this.game = game;

        updatePosition(0);

        initialType = getType();
        initialResources = getResources();
        initialHitPoints = getHitPoints();
        initialPosition = getPosition();
        initialTilePosition = getTilePosition();

        this.id = id;
    }

    private static boolean reallyGatheringGas(final Unit targ, final Player player) {
        return targ != null && targ.exists() && targ.isCompleted() && targ.getPlayer() == player &&
                targ.getType() != Resource_Vespene_Geyser && (targ.getType().isRefinery() || targ.getType().isResourceDepot());
    }

    private static boolean reallyGatheringMinerals(final Unit targ, final Player player) {
        return targ != null && targ.exists() && (targ.getType().isMineralField() ||
                (targ.isCompleted() && targ.getPlayer() == player && targ.getType().isResourceDepot()));
    }

    /**
     * Retrieves a unique identifier for this unit.
     *
     * @return An integer containing the unit's identifier.
     * @see #getReplayID
     */
    public int getID() {
        return id;
    }

    /**
     * Checks if the Unit exists in the view of the BWAPI player.
     * <p>
     * This is used primarily to check if BWAPI has access to a specific unit, or if the
     * unit is alive. This function is more general and would be synonymous to an isAlive
     * function if such a function were necessary.
     *
     * @return true If the unit exists on the map and is visible according to BWAPI, false If the unit is not accessible or the unit is dead.
     * <p>
     * In the event that this function returns false, there are two cases to consider:
     * 1. You own the unit. This means the unit is dead.
     * 2. Another player owns the unit. This could either mean that you don't have access
     * to the unit or that the unit has died. You can specifically identify dead units
     * by polling onUnitDestroy.
     * @see #isVisible
     * @see #isCompleted
     */
    public boolean exists() {
        return unitData.getExists();
    }

    /**
     * Retrieves the unit identifier for this unit as seen in replay data.
     * <p>
     * This is only available if {@link Flag#CompleteMapInformation} is enabled.
     *
     * @return An integer containing the replay unit identifier.
     * @see #getID
     */
    public int getReplayID() {
        return unitData.getReplayID();
    }

    /**
     * Retrieves the player that owns this unit.
     *
     * @return The owning Player object. Returns {@link Game#neutral()} If the unit is a neutral unit or inaccessible.
     */
    public Player getPlayer() {
        return game.getPlayer(unitData.getPlayer());
    }

    /**
     * Retrieves the unit's type.
     *
     * @return A {@link UnitType} objects representing the unit's type. Returns {@link UnitType#Unknown} if this unit is inaccessible or cannot be determined.
     * @see #getInitialType
     */
    public UnitType getType() {
        if (game.isLatComEnabled() && self().type.valid(game.getFrameCount())) {
            return self().type.get();
        }
        return UnitType.idToEnum[unitData.getType()];
    }

    /**
     * Retrieves the unit's position from the upper left corner of the map in pixels.
     * The position returned is roughly the center if the unit.
     * <p>
     * The unit bounds are defined as this value plus/minus the values of
     * {@link UnitType#dimensionLeft}, {@link UnitType#dimensionUp}, {@link UnitType#dimensionRight},
     * and {@link UnitType#dimensionDown}, which is conveniently expressed in {@link Unit#getLeft},
     * {@link Unit#getTop}, {@link Unit#getRight}, and {@link Unit#getBottom} respectively.
     *
     * @return {@link Position} object representing the unit's current position. Returns {@link Position#Unknown} if this unit is inaccessible.
     * @see #getTilePosition
     * @see #getInitialPosition
     * @see #getLeft
     * @see #getTop
     */
    public Position getPosition() {
        return position;
    }

    public int getX() {
        return getPosition().x;
    }

    public int getY() {
        return getPosition().y;
    }

    /**
     * Retrieves the unit's build position from the upper left corner of the map in
     * tiles.
     * <p>
     * This tile position is the tile that is at the top left corner of the structure.
     *
     * @return {@link TilePosition} object representing the unit's current tile position. Returns {@link TilePosition#Unknown} if this unit is inaccessible.
     * @see #getPosition
     * @see #getInitialTilePosition
     */
    public TilePosition getTilePosition() {
        final Position p = getPosition();
        final UnitType ut = getType();
        return new Position(Math.abs(p.x - ut.tileWidth() * 32 / 2), Math.abs(p.y - ut.tileHeight() * 32 / 2))
                .toTilePosition();
    }

    /**
     * Retrieves the unit's facing direction in radians.
     * <p>
     * A value of 0.0 means the unit is facing east.
     *
     * @return A double with the angle measure in radians.
     */
    public double getAngle() {
        return unitData.getAngle();
    }

    /**
     * Retrieves the x component of the unit's velocity, measured in pixels per frame.
     *
     * @return A double that represents the velocity's x component.
     * @see #getVelocityY
     */
    public double getVelocityX() {
        return unitData.getVelocityX();
    }

    /**
     * Retrieves the y component of the unit's velocity, measured in pixels per frame.
     *
     * @return A double that represents the velocity's y component.
     * @see #getVelocityX
     */
    public double getVelocityY() {
        return unitData.getVelocityY();
    }

    /**
     * Retrieves the {@link Region} that the center of the unit is in.
     *
     * @return The {@link Region} object that contains this unit. Returns null if the unit is inaccessible.
     */
    public Region getRegion() {
        return game.getRegionAt(getPosition());
    }

    /**
     * Retrieves the X coordinate of the unit's left boundary, measured in pixels from
     * the left side of the map.
     *
     * @return An integer representing the position of the left side of the unit.
     * @see #getTop
     * @see #getRight
     * @see #getBottom
     */
    public int getLeft() {
        return getX() - getType().dimensionLeft();
    }

    /**
     * Retrieves the Y coordinate of the unit's top boundary, measured in pixels from
     * the top of the map.
     *
     * @return An integer representing the position of the top side of the unit.
     * @see #getLeft
     * @see #getRight
     * @see #getBottom
     */
    public int getTop() {
        return getY() - getType().dimensionUp();
    }

    /**
     * Retrieves the X coordinate of the unit's right boundary, measured in pixels from
     * the left side of the map.
     *
     * @return An integer representing the position of the right side of the unit.
     * @see #getLeft
     * @see #getTop
     * @see #getBottom
     */
    public int getRight() {
        return getX() + getType().dimensionRight();
    }

    /**
     * Retrieves the Y coordinate of the unit's bottom boundary, measured in pixels from
     * the top of the map.
     *
     * @return An integer representing the position of the bottom side of the unit.
     * @see #getLeft
     * @see #getTop
     * @see #getRight
     */
    public int getBottom() {
        return getY() + getType().dimensionDown();
    }

    /**
     * Retrieves the unit's current Hit Points (HP) as seen in the game.
     *
     * @return An integer representing the amount of hit points a unit currently has.
     * <p>
     * In Starcraft, a unit usually dies when its HP reaches 0. It is possible however, to
     * have abnormal HP values in the Use Map Settings game type and as the result of a hack over
     * Battle.net. Such values include units that have 0 HP (can't be killed conventionally)
     * or even negative HP (death in one hit).
     * @see UnitType#maxHitPoints
     * @see #getShields
     * @see #getInitialHitPoints
     */
    public int getHitPoints() {
        int hitpoints = unitData.getHitPoints();
        if (game.isLatComEnabled() && self().hitPoints.valid(game.getFrameCount())) {
            return hitpoints + self().hitPoints.get();
        }
        return hitpoints;
    }

    /**
     * Retrieves the unit's current Shield Points (Shields) as seen in the game.
     *
     * @return An integer representing the amount of shield points a unit currently has.
     * @see UnitType#maxShields
     * @see #getHitPoints
     */
    public int getShields() {
        return unitData.getShields();
    }

    /**
     * Retrieves the unit's current Energy Points (Energy) as seen in the game.
     *
     * @return An integer representing the amount of energy points a unit currently has.
     * <p>
     * Energy is required in order for units to use abilities.
     * @see UnitType#maxEnergy
     */
    public int getEnergy() {
        int energy = unitData.getEnergy();
        if (game.isLatComEnabled() && self().energy.valid(game.getFrameCount())) {
            return energy + self().energy.get();
        }
        return energy;
    }

    /**
     * Retrieves the resource amount from a resource container, such as a Mineral Field
     * and Vespene Geyser. If the unit is inaccessible, then the last known resource
     * amount is returned.
     *
     * @return An integer representing the last known amount of resources remaining in this
     * resource.
     * @see #getInitialResources
     */
    public int getResources() {
        return unitData.getResources();
    }

    /**
     * Retrieves a grouping index from a resource container. Other resource
     * containers of the same value are considered part of one expansion location (group of
     * resources that are close together).
     * <p>
     * This grouping method is explicitly determined by Starcraft itself and is used only
     * by the internal AI.
     *
     * @return An integer with an identifier between 0 and 250 that determine which resources
     * are grouped together to form an expansion.
     */
    public int getResourceGroup() {
        return unitData.getResourceGroup();
    }

    /**
     * Retrieves the distance between this unit and a target position.
     * <p>
     * Distance is calculated from the edge of this unit, using Starcraft's own distance
     * algorithm. Ignores collisions.
     *
     * @param target A {@link Position} to calculate the distance to.
     * @return An integer representation of the number of pixels between this unit and the
     * target.
     */
    public int getDistance(final Position target) {
        // If this unit does not exist or target is invalid
        if (!exists() || target == null) {
            return Integer.MAX_VALUE;
        }
        /////// Compute distance

        // compute x distance
        int xDist = getLeft() - target.x;
        if (xDist < 0) {
            xDist = target.x - (getRight() + 1);
            if (xDist < 0) {
                xDist = 0;
            }
        }

        // compute y distance
        int yDist = getTop() - target.y;
        if (yDist < 0) {
            yDist = target.y - (getBottom() + 1);
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
        final int left = target.getLeft() - 1;
        final int top = target.getTop() - 1;
        final int right = target.getRight() + 1;
        final int bottom = target.getBottom() + 1;

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

    /**
     * Using data provided by Starcraft, checks if there is a path available from this
     * unit to the given target.
     * <p>
     * This function only takes into account the terrain data, and does not include
     * buildings when determining if a path is available. However, the complexity of this
     * function is constant ( O(1) ), and no extensive calculations are necessary.
     * <p>
     * If the current unit is an air unit, then this function will always return true.
     * <p>
     * If the unit somehow gets stuck in unwalkable terrain, then this function may still
     * return true if one of the unit's corners is on walkable terrain (i.e. if the unit is expected
     * to return to the walkable terrain).
     *
     * @param target A {@link Position} or a {@link Unit} that is used to determine if this unit has a path to the target.
     * @return true If there is a path between this unit and the target position, otherwise it will return false.
     * @see Game#hasPath
     */
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

    /**
     * Retrieves the frame number that sent the last successful command.
     * <p>
     * This value is comparable to {@link Game#getFrameCount}.
     *
     * @return The frame number that sent the last successfully processed command to BWAPI.
     * @see Game#getFrameCount
     * @see #getLastCommand
     */
    public int getLastCommandFrame() {
        return lastCommandFrame;
    }

    /**
     * Retrieves the last successful command that was sent to BWAPI.
     *
     * @return A {@link UnitCommand} object containing information about the command that was processed.
     * @see #getLastCommandFrame
     */
    public UnitCommand getLastCommand() {
        return lastCommand;
    }

    /**
     * Retrieves the {@link Player} that last attacked this unit.
     *
     * @return Player object representing the player that last attacked this unit. Returns null if this unit was not attacked.
     */
    public Player getLastAttackingPlayer() {
        return game.getPlayer(unitData.getLastAttackerPlayer());
    }

    /**
     * Retrieves the initial type of the unit. This is the type that the unit
     * starts as in the beginning of the game. This is used to access the types of static neutral
     * units such as mineral fields when they are not visible.
     *
     * @return {@link UnitType} of this unit as it was when it was created.
     * Returns {@link UnitType#Unknown} if this unit was not a static neutral unit in the beginning of the game.
     */
    public UnitType getInitialType() {
        return initialType;
    }

    /**
     * Retrieves the initial position of this unit. This is the position that
     * the unit starts at in the beginning of the game. This is used to access the positions of
     * static neutral units such as mineral fields when they are not visible.
     *
     * @return {@link Position} indicating the unit's initial position when it was created.
     * Returns {@link Position#Unknown} if this unit was not a static neutral unit in the beginning of
     * the game.
     */
    public Position getInitialPosition() {
        return initialPosition;
    }

    /**
     * Retrieves the initial build tile position of this unit. This is the tile
     * position that the unit starts at in the beginning of the game. This is used to access the
     * tile positions of static neutral units such as mineral fields when they are not visible.
     * The build tile position corresponds to the upper left corner of the unit.
     *
     * @return {@link TilePosition} indicating the unit's initial tile position when it was created.
     * Returns {@link TilePosition#Unknown} if this unit was not a static neutral unit in the beginning of
     * the game.
     */
    public TilePosition getInitialTilePosition() {
        return initialTilePosition;
    }

    /**
     * Retrieves the amount of hit points that this unit started off with at the
     * beginning of the game. The unit must be neutral.
     *
     * @return Number of hit points that this unit started with.
     * Returns 0 if this unit was not a neutral unit at the beginning of the game.
     * <p>
     * It is possible for the unit's initial hit points to differ from the maximum hit
     * points.
     * @see Game#getStaticNeutralUnits
     */
    public int getInitialHitPoints() {
        return initialHitPoints;
    }

    /**
     * Retrieves the amount of resources contained in the unit at the beginning of the
     * game. The unit must be a neutral resource container.
     *
     * @return Amount of resources that this unit started with.
     * Returns 0 if this unit was not a neutral unit at the beginning of the game, or if this
     * unit does not contain resources. It is possible that the unit simply contains 0 resources.
     * @see Game#getStaticNeutralUnits
     */
    public int getInitialResources() {
        return initialResources;
    }

    /**
     * Retrieves the number of units that this unit has killed in total.
     * <p>
     * The maximum amount of recorded kills per unit is 255.
     *
     * @return integer indicating this unit's kill count.
     */
    public int getKillCount() {
        return unitData.getKillCount();
    }

    /**
     * Retrieves the number of acid spores that this unit is inflicted with.
     *
     * @return Number of acid spores on this unit.
     */
    public int getAcidSporeCount() {
        return unitData.getAcidSporeCount();
    }

    /**
     * Retrieves the number of interceptors that this unit manages. This
     * function is only for the @Carrier and its hero.
     * <p>
     * This number may differ from the number of units returned from #getInterceptors. This
     * occurs for cases in which you can see the number of enemy interceptors in the Carrier HUD,
     * but don't actually have access to the individual interceptors.
     *
     * @return Number of interceptors in this unit.
     * @see #getInterceptors
     */
    public int getInterceptorCount() {
        return unitData.getInterceptorCount();
    }

    /**
     * Retrieves the number of scarabs that this unit has for use. This
     * function is only for the @Reaver.
     *
     * @return Number of scarabs this unit has ready.
     */
    public int getScarabCount() {
        return unitData.getScarabCount();
    }

    /**
     * Retrieves the amount of @mines this unit has available. This function
     * is only for the @Vulture.
     *
     * @return Number of spider mines available for placement.
     */
    public int getSpiderMineCount() {
        return unitData.getSpiderMineCount();
    }

    /**
     * Retrieves the unit's ground weapon cooldown. This value decreases every
     * frame, until it reaches 0. When the value is 0, this indicates that the unit is capable of
     * using its ground weapon, otherwise it must wait until it reaches 0.
     * <p>
     * This value will vary, because Starcraft adds an additional random value between
     * (-1) and (+2) to the unit's weapon cooldown.
     *
     * @return Number of frames needed for the unit's ground weapon to become available again.
     */
    public int getGroundWeaponCooldown() {
        return unitData.getGroundWeaponCooldown();
    }

    /**
     * Retrieves the unit's air weapon cooldown. This value decreases every
     * frame, until it reaches 0. When the value is 0, this indicates that the unit is capable of
     * using its air weapon, otherwise it must wait until it reaches 0.
     * <p>
     * This value will vary, because Starcraft adds an additional random value between
     * (-1) and (+2) to the unit's weapon cooldown.
     *
     * @return Number of frames needed for the unit's air weapon to become available again.
     */
    public int getAirWeaponCooldown() {
        return unitData.getAirWeaponCooldown();
    }

    /**
     * Retrieves the unit's ability cooldown. This value decreases every frame,
     * until it reaches 0. When the value is 0, this indicates that the unit is capable of using
     * one of its special abilities, otherwise it must wait until it reaches 0.
     * <p>
     * This value will vary, because Starcraft adds an additional random value between
     * (-1) and (+2) to the unit's ability cooldown.
     *
     * @return Number of frames needed for the unit's abilities to become available again.
     */
    public int getSpellCooldown() {
        return unitData.getSpellCooldown();
    }

    /**
     * Retrieves the amount of hit points remaining on the @matrix created by a @Science_Vessel.
     * The @matrix ability starts with 250 hit points when it is used.
     *
     * @return Number of hit points remaining on this unit's @matrix.
     * @see #getDefenseMatrixTimer
     * @see #isDefenseMatrixed
     */
    public int getDefenseMatrixPoints() {
        return unitData.getDefenseMatrixPoints();
    }

    /**
     * Retrieves the time, in frames, that the @matrix will remain active on the current
     * unit.
     *
     * @return Number of frames remaining until the effect is removed.
     * @see #getDefenseMatrixPoints
     * @see #isDefenseMatrixed
     */
    public int getDefenseMatrixTimer() {
        return unitData.getDefenseMatrixTimer();
    }

    /**
     * Retrieves the time, in frames, that @ensnare will remain active on the current
     * unit.
     *
     * @return Number of frames remaining until the effect is removed.
     * @see #isEnsnared
     */
    public int getEnsnareTimer() {
        return unitData.getEnsnareTimer();
    }

    /**
     * Retrieves the time, in frames, that @irradiate will remain active on the current
     * unit.
     *
     * @return Number of frames remaining until the effect is removed.
     * @see #isIrradiated
     */
    public int getIrradiateTimer() {
        return unitData.getIrradiateTimer();
    }

    /**
     * Retrieves the time, in frames, that @lockdown will remain active on the current
     * unit.
     *
     * @return Number of frames remaining until the effect is removed.
     * @see #isLockedDown()
     */
    public int getLockdownTimer() {
        return unitData.getLockdownTimer();
    }

    /**
     * Retrieves the time, in frames, that @maelstrom will remain active on the current
     * unit.
     *
     * @return Number of frames remaining until the effect is removed.
     * @see #isMaelstrommed
     */
    public int getMaelstromTimer() {
        return unitData.getMaelstromTimer();
    }

    /**
     * Retrieves an internal timer used for the primary order. Its use is
     * specific to the order type that is currently assigned to the unit.
     *
     * @return A value used as a timer for the primary order.
     * @see #getOrder
     */
    public int getOrderTimer() {
        return unitData.getOrderTimer();
    }

    /**
     * Retrieves the time, in frames, that @plague will remain active on the current
     * unit.
     *
     * @return Number of frames remaining until the effect is removed.
     * @see #isPlagued
     */
    public int getPlagueTimer() {
        return unitData.getPlagueTimer();
    }

    /**
     * Retrieves the time, in frames, until this temporary unit is destroyed or
     * removed. This is used to determine the remaining time for the following units
     * that were created by abilities:
     * - @hallucination
     * - @broodling
     * - @swarm
     * - @dweb
     * - @scanner
     * .
     * Once this value reaches 0, the unit is destroyed.
     */
    public int getRemoveTimer() {
        return unitData.getRemoveTimer();
    }

    /**
     * Retrieves the time, in frames, that @stasis will remain active on the current
     * unit.
     *
     * @return Number of frames remaining until the effect is removed.
     * @see #isPlagued
     */
    public int getStasisTimer() {
        return unitData.getStasisTimer();
    }

    /**
     * Retrieves the time, in frames, that @stim will remain active on the current
     * unit.
     *
     * @return Number of frames remaining until the effect is removed.
     * @see #isPlagued
     */
    public int getStimTimer() {
        if (game.isLatComEnabled() && self().stimTimer.valid(game.getFrameCount())) {
            return self().stimTimer.get();
        }
        return unitData.getStimTimer();
    }

    /**
     * Retrieves the building type that a @worker is about to construct. If
     * the unit is morphing or is an incomplete structure, then this returns the {@link UnitType} that it
     * will become when it has completed morphing/constructing.
     *
     * @return {@link UnitType} indicating the type that a @worker is about to construct, or an
     * incomplete unit will be when completed.
     */
    public UnitType getBuildType() {
        if (game.isLatComEnabled() && self().buildType.valid(game.getFrameCount())) {
            return self().buildType.get();
        }
        return UnitType.idToEnum[unitData.getBuildType()];
    }

    /**
     * Retrieves the list of units queued up to be trained.
     *
     * @return a List<UnitType> containing all the types that are in this factory's training
     * queue, from oldest to most recent.
     * @see #train
     * @see #cancelTrain
     * @see #isTraining
     */
    public List<UnitType> getTrainingQueue() {
        return IntStream.range(0, getTrainingQueueCount())
                .mapToObj(i -> game.isLatComEnabled() && self().trainingQueue[i].valid(game.getFrameCount()) ?
                        self().trainingQueue[i].get() :
                        UnitType.idToEnum[unitData.getTrainingQueue(i)])
                .collect(Collectors.toList());
    }

    int getTrainingQueueCount() {
        int count = unitData.getTrainingQueueCount();
        if (game.isLatComEnabled() && self().trainingQueueCount.valid(game.getFrameCount())) {
            return count + self().trainingQueueCount.get();
        }
        return count;
    }

    /**
     * Retrieves the technology that this unit is currently researching.
     *
     * @return {@link TechType} indicating the technology being researched by this unit.
     * Returns {@link TechType#None} if this unit is not researching anything.
     * @see #research
     * @see #cancelResearch
     * @see #isResearching
     * @see #getRemainingResearchTime
     */
    public TechType getTech() {
        if (game.isLatComEnabled() && self().tech.valid(game.getFrameCount())) {
            return self().tech.get();
        }
        return TechType.idToEnum[unitData.getTech()];
    }

    /**
     * Retrieves the upgrade that this unit is currently upgrading.
     *
     * @return {@link UpgradeType} indicating the upgrade in progress by this unit.
     * Returns {@link UpgradeType#None} if this unit is not upgrading anything.
     * @see #upgrade
     * @see #cancelUpgrade
     * @see #isUpgrading
     * @see #getRemainingUpgradeTime
     */
    public UpgradeType getUpgrade() {
        if (game.isLatComEnabled() && self().upgrade.valid(game.getFrameCount())) {
            return self().upgrade.get();
        }
        return UpgradeType.idToEnum[unitData.getUpgrade()];
    }

    /**
     * Retrieves the remaining build time for a unit or structure that is being trained
     * or constructed.
     *
     * @return Number of frames remaining until the unit's completion.
     */
    public int getRemainingBuildTime() {
        if (game.isLatComEnabled() && self().remainingBuildTime.valid(game.getFrameCount())) {
            return self().remainingBuildTime.get();
        }
        return unitData.getRemainingBuildTime();
    }

    /**
     * Retrieves the remaining time, in frames, of the unit that is currently being
     * trained.
     * <p>
     * If the unit is a @Hatchery, @Lair, or @Hive, this retrieves the amount of time until
     * the next larva spawns.
     *
     * @return Number of frames remaining until the current training unit becomes completed, or
     * the number of frames remaining until the next larva spawns.
     * Returns 0 if the unit is not training or has three larvae.
     * <p>
     * + @see #train
     * @see #getTrainingQueue
     */
    public int getRemainingTrainTime() {
        if (game.isLatComEnabled() && self().remainingTrainTime.valid(game.getFrameCount())) {
            return self().remainingTrainTime.get();
        }
        return unitData.getRemainingTrainTime();
    }

    /**
     * Retrieves the amount of time until the unit is done researching its currently
     * assigned {@link TechType}.
     *
     * @return The remaining research time, in frames, for the current technology being
     * researched by this unit.
     * Returns 0 if the unit is not researching anything.
     * @see #research
     * @see #cancelResearch
     * @see #isResearching
     * @see #getTech
     */
    public int getRemainingResearchTime() {
        if (game.isLatComEnabled() && self().remainingResearchTime.valid(game.getFrameCount())) {
            return self().remainingResearchTime.get();
        }
        return unitData.getRemainingResearchTime();
    }

    /**
     * Retrieves the amount of time until the unit is done upgrading its current upgrade.
     *
     * @return The remaining upgrade time, in frames, for the current upgrade.
     * Returns 0 if the unit is not upgrading anything.
     * @see #upgrade
     * @see #cancelUpgrade
     * @see #isUpgrading
     * @see #getUpgrade
     */
    public int getRemainingUpgradeTime() {
        if (game.isLatComEnabled() && self().remainingUpgradeTime.valid(game.getFrameCount())) {
            return self().remainingUpgradeTime.get();
        }
        return unitData.getRemainingUpgradeTime();
    }

    /**
     * Retrieves the unit currently being trained, or the corresponding paired unit for @SCVs
     * and @Terran structures, depending on the context.
     * For example, if this unit is a @Factory under construction, this function will return the @SCV
     * that is constructing it. If this unit is a @SCV, then it will return the structure it
     * is currently constructing. If this unit is a @Nexus, and it is training a @Probe, then the
     * probe will be returned.
     * <p>
     * BUG: This will return an incorrect unit when called on @Reavers.
     *
     * @return Paired build unit that is either constructing this unit, structure being constructed by
     * this unit, or the unit that is being trained by this structure.
     * Returns null if there is no unit constructing this one, or this unit is not constructing
     * another unit.
     */
    public Unit getBuildUnit() {
        if (game.isLatComEnabled() && self().buildType.valid(game.getFrameCount())) {
            return game.getUnit(self().buildUnit.get());
        }
        return game.getUnit(unitData.getBuildUnit());
    }

    /**
     * Generally returns the appropriate target unit after issuing an order that accepts
     * a target unit (i.e. attack, repair, gather, etc.). To get a target that has been
     * acquired automatically without issuing an order, use {@link #getOrderTarget}.
     *
     * @return Unit that is currently being targeted by this unit.
     * @see #getOrderTarget
     */
    public Unit getTarget() {
        if (game.isLatComEnabled() && self().target.valid(game.getFrameCount())) {
            return game.getUnit(self().target.get());
        }
        return game.getUnit(unitData.getTarget());
    }

    /**
     * Retrieves the target position the unit is moving to, provided a valid path to the
     * target position exists.
     *
     * @return Target position of a movement action.
     */
    public Position getTargetPosition() {
        if (game.isLatComEnabled() && self().targetPositionX.valid(game.getFrameCount())) {
            return new Position(self().targetPositionX.get(), self().targetPositionY.get());
        }
        return new Position(unitData.getTargetPositionX(), unitData.getTargetPositionY());
    }

    /**
     * Retrieves the primary Order that the unit is assigned. Primary orders
     * are distinct actions such as {@link Order#AttackUnit} and {@link Order#PlayerGuard}.
     *
     * @return The primary {@link Order} that the unit is executing.
     */
    public Order getOrder() {
        if (game.isLatComEnabled() && self().order.valid(game.getFrameCount())) {
            return self().order.get();
        }
        return Order.idToEnum[unitData.getOrder()];
    }

    /**
     * Retrieves the secondary Order that the unit is assigned. Secondary
     * orders are step in the background as a sub-order. An example would be {@link Order#TrainFighter},
     * because a @Carrier can move and train fighters at the same time.
     *
     * @return The secondary {@link Order} that the unit is executing.
     */
    public Order getSecondaryOrder() {
        if (game.isLatComEnabled() && self().secondaryOrder.valid(game.getFrameCount())) {
            return self().secondaryOrder.get();
        }
        return Order.idToEnum[unitData.getSecondaryOrder()];
    }

    /**
     * Retrieves the unit's primary order target. This is usually set when the
     * low level unit AI acquires a new target automatically. For example if an enemy @Probe
     * comes in range of your @Marine, the @Marine will start attacking it, and getOrderTarget
     * will be set in this case, but not getTarget.
     *
     * @return The {@link Unit} that this unit is currently targetting.
     * @see #getTarget
     * @see #getOrder
     */
    public Unit getOrderTarget() {
        if (game.isLatComEnabled() && self().orderTarget.valid(game.getFrameCount())) {
            return game.getUnit(self().orderTarget.get());
        }
        return game.getUnit(unitData.getOrderTarget());
    }

    /**
     * Retrieves the target position for the unit's order. For example, when
     * {@link Order#Move} is assigned, {@link #getTargetPosition} returns the end of the unit's path, but this
     * returns the location that the unit is trying to move to.
     *
     * @return {@link Position} that this unit is currently targetting.
     * @see #getTargetPosition
     * @see #getOrder
     */
    public Position getOrderTargetPosition() {
        if (game.isLatComEnabled() && self().orderTargetPositionX.valid(game.getFrameCount())) {
            return new Position(self().orderTargetPositionX.get(), self().orderTargetPositionY.get());
        }
        return new Position(unitData.getOrderTargetPositionX(), unitData.getOrderTargetPositionY());
    }

    /**
     * Retrieves the position the structure is rallying units to once they are
     * completed.
     *
     * @return {@link Position} that a completed unit coming from this structure will travel to.
     * Returns {@link Position#None} If this building does not produce units.
     * <p>
     * If {@link #getRallyUnit} is valid, then this value is ignored.
     * @see #setRallyPoint
     * @see #getRallyUnit
     */
    public Position getRallyPosition() {
        if (game.isLatComEnabled() && self().rallyPositionX.valid(game.getFrameCount())) {
            return new Position(self().rallyPositionX.get(), self().rallyPositionY.get());
        }
        return new Position(unitData.getRallyPositionX(), unitData.getRallyPositionY());
    }

    /**
     * Retrieves the unit the structure is rallying units to once they are completed.
     * Units will then follow the targetted unit.
     *
     * @return {@link Unit} that a completed unit coming from this structure will travel to.
     * Returns null if the structure is not rallied to a unit or it does not produce units.
     * <p>
     * A rallied unit takes precedence over a rallied position. That is if the return value
     * is valid(non-null), then getRallyPosition is ignored.
     * @see #setRallyPoint
     * @see #getRallyPosition
     */
    public Unit getRallyUnit() {
        if (game.isLatComEnabled() && self().rallyUnit.valid(game.getFrameCount())) {
            return game.getUnit(self().rallyUnit.get());
        }
        return game.getUnit(unitData.getRallyUnit());
    }

    /**
     * Retrieves the add-on that is attached to this unit.
     *
     * @return Unit interface that represents the add-on that is attached to this unit.
     * Returns null if this unit does not have an add-on.
     */
    public Unit getAddon() {
        return game.getUnit(unitData.getAddon());
    }

    /**
     * Retrieves the @Nydus_Canal that is attached to this one. Every @Nydus_Canal
     * can place a "Nydus Exit" which, when connected, can be travelled through by @Zerg units.
     *
     * @return {@link Unit} object representing the @Nydus_Canal connected to this one.
     * Returns null if the unit is not a @Nydus_Canal, is not owned, or has not placed a Nydus
     * Exit.
     */
    public Unit getNydusExit() {
        return game.getUnit(unitData.getNydusExit());
    }

    /**
     * Retrieves the power-up that the worker unit is holding. Power-ups are
     * special units such as the @Flag in the @CTF game type, which can be picked up by worker
     * units.
     * <p>
     * If your bot is strictly melee/1v1, then this method is not necessary.
     *
     * @return The {@link Unit} object that represents the power-up.
     * Returns null if the unit is not carrying anything.
     */
    public Unit getPowerUp() {
        return game.getUnit(unitData.getPowerUp());
    }

    /**
     * Retrieves the @Transport or @Bunker unit that has this unit loaded inside of it.
     *
     * @return Unit object representing the @Transport containing this unit.
     * Returns null if this unit is not in a @Transport.
     */
    public Unit getTransport() {
        return game.getUnit(unitData.getTransport());
    }

    /**
     * Retrieves the set of units that are contained within this @Bunker or @Transport.
     *
     * @return A List<Unit> object containing all of the units that are loaded inside of the
     * current unit.
     */
    public List<Unit> getLoadedUnits() {
        if (getType().spaceProvided() < 1) {
            return Collections.emptyList();
        }
        return game.getAllUnits().stream()
                .filter(u -> equals(u.getTransport()))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the remaining unit-space available for @Bunkers and @Transports.
     *
     * @return The number of spots available to transport a unit.
     * @see #getLoadedUnits
     */
    public int getSpaceRemaining() {
        int space = getType().spaceProvided();

        // Decrease the space for each loaded unit
        for (final Unit u : getLoadedUnits()) {
            space -= u.getType().spaceRequired();
        }
        return Math.max(space, 0);
    }

    /**
     * Retrieves the parent @Carrier that owns this @Interceptor.
     *
     * @return The parent @Carrier unit that has ownership of this one.
     * Returns null if the current unit is not an @Interceptor.
     */
    public Unit getCarrier() {
        return game.getUnit(unitData.getCarrier());
    }

    /**
     * Retrieves the set of @Interceptors controlled by this unit. This is
     * intended for @Carriers and its hero.
     *
     * @return List<Unit> containing @Interceptor units owned by this carrier.
     * @see #getInterceptorCount
     */
    public List<Unit> getInterceptors() {
        if (getType() != Protoss_Carrier && getType() != Hero_Gantrithor) {
            return Collections.emptyList();
        }
        return game.getAllUnits().stream()
                .filter(u -> equals(u.getCarrier()))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the parent @Hatchery, @Lair, or @Hive that owns this particular unit.
     * This is intended for @Larvae.
     *
     * @return Hatchery unit that has ownership of this larva.
     * Returns null if the current unit is not a @Larva or has no parent.
     * @see #getLarva
     */
    public Unit getHatchery() {
        return game.getUnit(unitData.getHatchery());
    }

    /**
     * Retrieves the set of @Larvae that were spawned by this unit.
     * Only @Hatcheries, @Lairs, and @Hives are capable of spawning @Larvae. This is like clicking the
     * "Select Larva" button and getting the selection of @Larvae.
     *
     * @return List<Unit> containing @Larva units owned by this unit. The set will be empty if
     * there are none.
     * @see #getHatchery
     */
    public List<Unit> getLarva() {
        if (!getType().producesLarva()) {
            return Collections.emptyList();
        }
        return game.getAllUnits().stream()
                .filter(u -> equals(u.getHatchery()))
                .collect(Collectors.toList());
    }

    public List<Unit> getUnitsInRadius(final int radius) {
        return getUnitsInRadius(radius, u -> true);
    }

    /**
     * Retrieves the set of all units in a given radius of the current unit.
     * <p>
     * Takes into account this unit's dimensions. Can optionally specify a filter that is composed
     * using BWAPI Filter semantics to include only specific units (such as only ground units, etc.)
     *
     * @param radius The radius, in pixels, to search for units.
     * @param pred   The composed function predicate to include only specific (desired) units in the set. Defaults to null, which means no filter.
     * @return A List<Unit> containing the set of units that match the given criteria.
     * @see Game#getClosestUnit
     * @see #getUnitsInWeaponRange
     * @see Game#getUnitsInRadius
     * @see Game#getUnitsInRectangle
     */
    public List<Unit> getUnitsInRadius(final int radius, final UnitFilter pred) {
        if (!exists()) {
            return Collections.emptyList();
        }
        return game.getUnitsInRectangle(
                getLeft() - radius,
                getTop() - radius,
                getRight() + radius,
                getBottom() + radius,
                u -> getDistance(u) <= radius && pred.test(u));
    }

    public List<Unit> getUnitsInWeaponRange(final WeaponType weapon) {
        return getUnitsInWeaponRange(weapon, u -> true);
    }

    /**
     * Obtains the set of units within weapon range of this unit.
     *
     * @param weapon The weapon type to use as a filter for distance and units that can be hit by it.
     * @param pred   A predicate used as an additional filter. If omitted, no additional filter is used.
     * @see #getUnitsInRadius
     * @see Game#getClosestUnit
     * @see Game#getUnitsInRadius
     * @see Game#getUnitsInRectangle
     */
    public List<Unit> getUnitsInWeaponRange(final WeaponType weapon, final UnitFilter pred) {
        // Return if this unit does not exist
        if (!exists()) {
            return Collections.emptyList();
        }

        final int max = getPlayer().weaponMaxRange(weapon);

        return game.getUnitsInRectangle(
                getLeft() - max,
                getTop() - max,
                getRight() + max,
                getBottom() + max,
                u -> {
                    if (!pred.test(u)) {
                        return false;
                    }
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
                });
    }

    /**
     * Checks if the current unit is housing a @Nuke. This is only available
     * for @Silos.
     *
     * @return true if this unit has a @Nuke ready, and false if there is no @Nuke.
     */
    public boolean hasNuke() {
        return unitData.getHasNuke();
    }

    /**
     * Checks if the current unit is accelerating.
     *
     * @return true if this unit is accelerating, and false otherwise
     */
    public boolean isAccelerating() {
        return unitData.isAccelerating();
    }

    /**
     * Checks if this unit is currently attacking something.
     *
     * @return true if this unit is attacking another unit, and false if it is not.
     */
    public boolean isAttacking() {
        return unitData.isAttacking();
    }

    /**
     * Checks if this unit is currently playing an attack animation. Issuing
     * commands while this returns true may interrupt the unit's next attack sequence.
     *
     * @return true if this unit is currently running an attack frame, and false if interrupting
     * the unit is feasible.
     * <p>
     * This function is only available to some unit types, specifically those that play
     * special animations when they attack.
     */
    public boolean isAttackFrame() {
        return unitData.isAttackFrame();
    }

    /**
     * Checks if the current unit is being constructed. This is mostly
     * applicable to Terran structures which require an SCV to be constructing a structure.
     *
     * @return true if this is either a Protoss structure, Zerg structure, or Terran structure
     * being constructed by an attached SCV.
     * false if this is either completed, not a structure, or has no SCV constructing it
     * @see #build
     * @see #cancelConstruction
     * @see #haltConstruction
     * @see #isConstructing
     */
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

    /**
     * Checks this @Mineral_Field or @Refinery is currently being gathered from.
     *
     * @return true if this unit is a resource container and being harvested by a worker, and
     * false otherwise
     */
    public boolean isBeingGathered() {
        return unitData.isBeingGathered();
    }

    /**
     * Checks if this unit is currently being healed by a @Medic or repaired by a @SCV.
     *
     * @return true if this unit is being healed, and false otherwise.
     */
    public boolean isBeingHealed() {
        return getType().getRace() == Terran && isCompleted() && getHitPoints() > unitData.getLastHitPoints();
    }

    /**
     * Checks if this unit is currently blinded by a @Medic 's @Optical_Flare ability.
     * Blinded units have reduced sight range and cannot detect other units.
     *
     * @return true if this unit is blind, and false otherwise
     */
    public boolean isBlind() {
        return unitData.isBlind();
    }

    /**
     * Checks if the current unit is slowing down to come to a stop.
     *
     * @return true if this unit is breaking, false if it has stopped or is still moving at full
     * speed.
     */
    public boolean isBraking() {
        return unitData.isBraking();
    }

    /**
     * Checks if the current unit is burrowed, either using the @Burrow ability, or is
     * an armed @Spider_Mine.
     *
     * @return true if this unit is burrowed, and false otherwise
     * @see #burrow
     * @see #unburrow
     */
    public boolean isBurrowed() {
        return unitData.isBurrowed();
    }

    public boolean isCarrying() {
        return isCarryingGas() || isCarryingMinerals();
    }

    /**
     * Checks if this worker unit is carrying some vespene gas.
     *
     * @return true if this is a worker unit carrying vespene gas, and false if it is either
     * not a worker, or not carrying gas.
     * @see #returnCargo
     * @see #isGatheringGas
     * @see #isCarryingMinerals
     */
    public boolean isCarryingGas() {
        return unitData.getCarryResourceType() == 1;
    }

    /**
     * Checks if this worker unit is carrying some minerals.
     *
     * @return true if this is a worker unit carrying minerals, and false if it is either
     * not a worker, or not carrying minerals.
     * @see #returnCargo
     * @see #isGatheringMinerals
     * @see #isCarryingMinerals
     */
    public boolean isCarryingMinerals() {
        return unitData.getCarryResourceType() == 2;
    }

    /**
     * Checks if this unit is currently @cloaked.
     *
     * @return true if this unit is cloaked, and false if it is visible.
     * @see #cloak
     * @see #decloak
     */
    public boolean isCloaked() {
        return unitData.isCloaked();
    }

    /**
     * Checks if this unit has finished being constructed, trained, morphed, or warped
     * in, and can now receive orders.
     *
     * @return true if this unit is completed, and false if it is under construction or inaccessible.
     */
    public boolean isCompleted() {
        if (game.isLatComEnabled() && self().isCompleted.valid(game.getFrameCount())) {
            return self().isCompleted.get();
        }
        return unitData.isCompleted();
    }

    /**
     * Checks if a unit is either constructing something or moving to construct something.
     *
     * @return true when a unit has been issued an order to build a structure and is moving to
     * the build location, or is currently constructing something.
     * @see #isBeingConstructed
     * @see #build
     * @see #cancelConstruction
     * @see #haltConstruction
     */
    public boolean isConstructing() {
        if (game.isLatComEnabled() && self().isConstructing.valid(game.getFrameCount())) {
            return self().isConstructing.get();
        }
        return unitData.isConstructing();
    }

    /**
     * Checks if this unit has the @matrix effect.
     *
     * @return true if the @matrix ability was used on this unit, and false otherwise.
     */
    public boolean isDefenseMatrixed() {
        return getDefenseMatrixTimer() != 0;
    }

    /**
     * Checks if this unit is visible or revealed by a detector unit. If this
     * is false and #isVisible is true, then the unit is only partially visible and requires a
     * detector in order to be targetted.
     *
     * @return true if this unit is detected, and false if it needs a detector unit nearby in
     * order to see it.
     */
    public boolean isDetected() {
        return unitData.isDetected();
    }

    /**
     * Checks if the @Queen ability @Ensnare has been used on this unit.
     *
     * @return true if the unit is ensnared, and false if it is not
     */
    public boolean isEnsnared() {
        return getEnsnareTimer() != 0;
    }

    /**
     * This macro function checks if this unit is in the air. That is, the unit is
     * either a flyer or a flying building.
     *
     * @return true if this unit is in the air, and false if it is on the ground
     * @see UnitType#isFlyer
     * @see Unit#isLifted
     */
    public boolean isFlying() {
        return getType().isFlyer() || isLifted();
    }

    /**
     * Checks if this unit is following another unit. When a unit is following
     * another unit, it simply moves where the other unit does, and does not attack enemies when
     * it is following.
     *
     * @return true if this unit is following another unit, and false if it is not
     * @see #follow
     * @see #getTarget
     */
    public boolean isFollowing() {
        return getOrder() == Order.Follow;
    }


    boolean isGathering() {
        if (game.isLatComEnabled() && self().isGathering.valid(game.getFrameCount())) {
            return self().isGathering.get();
        }
        return unitData.isGathering();
    }
    /**
     * Checks if this unit is currently gathering gas. That is, the unit is
     * either moving to a refinery, waiting to enter a refinery, harvesting from the refinery, or
     * returning gas to a resource depot.
     *
     * @return true if this unit is harvesting gas, and false if it is not
     * @see #isCarryingGas
     */
    public boolean isGatheringGas() {
        if (!isGathering()) {
            return false;
        }
        final Order order = getOrder();
        if (!gatheringGasOrders.contains(order)) {
            return false;
        }
        if (order == ResetCollision) {
            return unitData.getCarryResourceType() == 1;
        }
        //return true if BWOrder is WaitForGas, HarvestGas, or ReturnGas
        if (order == WaitForGas || order == HarvestGas || order == ReturnGas) {
            return true;
        }
        //if BWOrder is MoveToGas, Harvest1, or Harvest2 we need to do some additional checks to make sure the unit is really gathering
        return reallyGatheringGas(getTarget(), getPlayer()) || reallyGatheringGas(getOrderTarget(), getPlayer());
    }

    /**
     * Checks if this unit is currently harvesting minerals. That is, the unit
     * is either moving to a @mineral_field, waiting to mine, mining minerals, or returning
     * minerals to a resource depot.
     *
     * @return true if this unit is gathering minerals, and false if it is not
     * @see #isCarryingMinerals
     */
    public boolean isGatheringMinerals() {
        if (!isGathering()) {
            return false;
        }
        final Order order = getOrder();
        if (!gatheringMineralOrders.contains(order)) {
            return false;
        }
        if (order == ResetCollision) {
            return unitData.getCarryResourceType() == 2;
        }
        //return true if BWOrder is WaitForMinerals, MiningMinerals, or ReturnMinerals
        if (order == WaitForMinerals || order == MiningMinerals || order == ReturnMinerals) {
            return true;
        }
        //if BWOrder is MoveToMinerals, Harvest1, or Harvest2 we need to do some additional checks to make sure the unit is really gathering
        return reallyGatheringMinerals(getTarget(), getPlayer()) || reallyGatheringMinerals(getOrderTarget(), getPlayer());
    }

    /**
     * Checks if this unit is a hallucination. Hallucinations are created by
     * the @High_Templar using the @Hallucination ability. Enemy hallucinations are unknown if
     * {@link Flag#CompleteMapInformation} is disabled. Hallucinations have a time limit until they are
     * destroyed (see {@link Unit#getRemoveTimer}).
     *
     * @return true if the unit is a hallucination and false otherwise.
     * @see #getRemoveTimer
     */
    public boolean isHallucination() {
        return unitData.isHallucination();
    }

    /**
     * Checks if the unit is currently holding position. A unit that is holding
     * position will attack other units, but will not chase after them.
     *
     * @return true if this unit is holding position, and false if it is not.
     * @see #holdPosition
     */
    public boolean isHoldingPosition() {
        return getOrder() == HoldPosition;
    }

    /**
     * Checks if this unit is running an idle order. This function is
     * particularly useful when checking for units that aren't doing any tasks that you assigned.
     * <p>
     * A unit is considered idle if it is <b>not</b> doing any of the following:
     * - Training
     * - Constructing
     * - Morphing
     * - Researching
     * - Upgrading
     * <p>
     * In <b>addition</b> to running one of the following orders:
     * - Order.PlayerGuard: Player unit idle.
     * - Order.Guard: Generic unit idle.
     * - Order.Stop
     * - Order.PickupIdle
     * - Order.Nothing: Structure/generic idle.
     * - Order.Medic: Medic idle.
     * - Order.Carrier: Carrier idle.
     * - Order.Reaver: Reaver idle.
     * - Order.Critter: Critter idle.
     * - Order.Neutral: Neutral unit idle.
     * - Order.TowerGuard: Turret structure idle.
     * - Order.Burrowed: Burrowed unit idle.
     * - Order.NukeTrain
     * - Order.Larva: Larva idle.
     *
     * @return true if this unit is idle, and false if this unit is performing any action, such
     * as moving or attacking
     * @see Unit#stop
     */
    public boolean isIdle() {
        if (game.isLatComEnabled() && self().isIdle.valid(game.getFrameCount())) {
            return self().isIdle.get();
        }
        return unitData.isIdle();
    }

    /**
     * Checks if the unit can be interrupted.
     *
     * @return true if this unit can be interrupted, or false if this unit is uninterruptable
     */
    public boolean isInterruptible() {
        return unitData.isInterruptible();
    }

    /**
     * Checks the invincibility state for this unit.
     *
     * @return true if this unit is currently invulnerable, and false if it is vulnerable
     */
    public boolean isInvincible() {
        return unitData.isInvincible();
    }

    /**
     * Checks if the target unit can immediately be attacked by this unit in the current
     * frame.
     *
     * @param target The target unit to use in this check.
     * @return true if target is within weapon range of this unit's appropriate weapon, and
     * false otherwise.
     * Returns false if target is invalid, inaccessible, too close, too far, or this unit does
     * not have a weapon that can attack target.
     */
    public boolean isInWeaponRange(final Unit target) {
        // Preliminary checks
        if (!exists() || target == null || !target.exists() || this == target) {
            return false;
        }

        // Store the types as locals
        final UnitType thisType = getType();

        // Obtain the weapon type
        final WeaponType wpn = target.isFlying() ? thisType.airWeapon() : thisType.groundWeapon();

        // Return if there is no weapon type
        if (wpn == WeaponType.None || wpn == WeaponType.Unknown) {
            return false;
        }

        // Retrieve the min and max weapon ranges
        final int minRange = wpn.minRange();
        final int maxRange = getPlayer().weaponMaxRange(wpn);

        // Check if the distance to the unit is within the weapon range
        final int distance = getDistance(target);
        return (minRange == 0 || minRange < distance) && distance <= maxRange;
    }

    /**
     * Checks if this unit is irradiated by a @Science_Vessel 's @Irradiate ability.
     *
     * @return true if this unit is irradiated, and false otherwise
     * @see #getIrradiateTimer
     */
    public boolean isIrradiated() {
        return getIrradiateTimer() != 0;
    }

    /**
     * Checks if this unit is a @Terran building and lifted off the ground.
     * This function generally implies getType().isBuilding() and isCompleted() both
     * return true.
     *
     * @return true if this unit is a @Terran structure lifted off the ground.
     * @see #isFlying
     */
    public boolean isLifted() {
        return unitData.isLifted();
    }

    /**
     * Checks if this unit is currently loaded into another unit such as a @Transport.
     *
     * @return true if this unit is loaded in another one, and false otherwise
     * @see #load
     * @see #unload
     * @see #unloadAll
     */
    public boolean isLoaded() {
        return getTransport() != null;
    }

    /**
     * Checks if this unit is currently @locked by a @Ghost.
     *
     * @return true if this unit is locked down, and false otherwise
     * @see #getLockdownTimer
     */
    public boolean isLockedDown() {
        return getLockdownTimer() != 0;
    }

    /**
     * Checks if this unit has been @Maelstrommed by a @Dark_Archon.
     *
     * @return true if this unit is maelstrommed, and false otherwise
     * @see #getMaelstromTimer
     */
    public boolean isMaelstrommed() {
        return getMaelstromTimer() != 0;
    }

    /**
     * Finds out if the current unit is morphing or not. @Zerg units and
     * structures often have the ability to #morph into different types of units. This function
     * allows you to identify when this process is occurring.
     *
     * @return true if the unit is currently morphing, false if the unit is not morphing
     * @see #morph
     * @see #cancelMorph
     * @see #getBuildType
     * @see #getRemainingBuildTime
     */
    public boolean isMorphing() {
        if (game.isLatComEnabled() && self().isMorphing.valid(game.getFrameCount())) {
            return self().isMorphing.get();
        }
        return unitData.isMorphing();
    }

    /**
     * Checks if this unit is currently moving.
     *
     * @return true if this unit is moving, and false if it is not
     * @see #stop
     */
    public boolean isMoving() {
        if (game.isLatComEnabled() && self().isMoving.valid(game.getFrameCount())) {
            return self().isMoving.get();
        }
        return unitData.isMoving();
    }

    /**
     * Checks if this unit has been parasited by some other player.
     *
     * @return true if this unit is inflicted with @parasite, and false if it is clean
     */
    public boolean isParasited() {
        return unitData.isParasited();
    }

    /**
     * Checks if this unit is patrolling between two positions.
     *
     * @return true if this unit is patrolling and false if it is not
     * @see #patrol
     */
    public boolean isPatrolling() {
        return getOrder() == Patrol;
    }

    /**
     * Checks if this unit has been been @plagued by a @defiler.
     *
     * @return true if this unit is inflicted with @plague and is taking damage, and false if it
     * is clean
     * @see #getPlagueTimer
     */
    public boolean isPlagued() {
        return getPlagueTimer() != 0;
    }

    /**
     * Checks if this unit is repairing or moving to @repair another unit.
     * This is only applicable to @SCVs.
     *
     * @return true if this unit is currently repairing or moving to @repair another unit, and
     * false if it is not
     */
    public boolean isRepairing() {
        return getOrder() == Repair;
    }

    /**
     * Checks if this unit is a structure that is currently researching a technology.
     * See TechTypes for a complete list of technologies in Broodwar.
     *
     * @return true if this structure is researching a technology, false otherwise
     * @see #research
     * @see #cancelResearch
     * @see #getTech
     * @see #getRemainingResearchTime
     */
    public boolean isResearching() {
        return getOrder() == ResearchTech;
    }

    /**
     * Checks if this unit has been selected in the user interface. This
     * function is only available if the flag Flag#UserInput is enabled.
     *
     * @return true if this unit is currently selected, and false if this unit is not selected
     * @see Game#getSelectedUnits
     */
    public boolean isSelected() {
        return unitData.isSelected();
    }

    /**
     * Checks if this unit is currently @sieged. This is only applicable to @Siege_Tanks.
     *
     * @return true if the unit is in siege mode, and false if it is either not in siege mode or
     * not a @Siege_Tank
     * @see #siege
     * @see #unsiege
     */
    public boolean isSieged() {
        final UnitType t = getType();
        return t == Terran_Siege_Tank_Siege_Mode || t == Hero_Edmund_Duke_Siege_Mode;
    }

    /**
     * Checks if the unit is starting to attack.
     *
     * @return true if this unit is starting an attack.
     * @see #attack
     * @see #getGroundWeaponCooldown
     * @see #getAirWeaponCooldown
     */
    public boolean isStartingAttack() {
        return unitData.isStartingAttack();
    }

    /**
     * Checks if this unit is inflicted with @Stasis by an @Arbiter.
     *
     * @return true if this unit is locked in a @Stasis and is unable to move, and false if it
     * is free.
     * <p>
     * This function does not necessarily imply that the unit is invincible, since there
     * is a feature in the @UMS game type that allows stasised units to be vulnerable.
     * @see #getStasisTimer
     */
    public boolean isStasised() {
        return getStasisTimer() != 0;
    }

    /**
     * Checks if this unit is currently under the influence of a @Stim_Pack.
     *
     * @return true if this unit has used a stim pack, false otherwise
     * @see #getStimTimer
     */
    public boolean isStimmed() {
        return getStimTimer() != 0;
    }

    /**
     * Checks if this unit is currently trying to resolve a collision by randomly moving
     * around.
     *
     * @return true if this unit is currently stuck and trying to resolve a collision, and false
     * if this unit is free
     */
    public boolean isStuck() {
        return unitData.isStuck();
    }

    /**
     * Checks if this unit is training a new unit. For example, a @Barracks
     * training a @Marine.
     * <p>
     * It is possible for a unit to remain in the training queue with no progress. In that
     * case, this function will return false because of supply or unit count limitations.
     *
     * @return true if this unit is currently training another unit, and false otherwise.
     * @see #train
     * @see #getTrainingQueue
     * @see #cancelTrain
     * @see #getRemainingTrainTime
     */
    public boolean isTraining() {
        if (game.isLatComEnabled() && self().isTraining.valid(game.getFrameCount())) {
            return self().isTraining.get();
        }
        return unitData.isTraining();
    }

    /**
     * Checks if the current unit is being attacked. Has a small delay before
     * this returns false
     * again when the unit is no longer being attacked.
     *
     * @return true if this unit has been attacked within the past few frames, and false
     * if it has not
     */
    public boolean isUnderAttack() {
        return unitData.getRecentlyAttacked();
    }

    /**
     * Checks if this unit is under the cover of a @Dark_Swarm.
     *
     * @return true if this unit is protected by a @Dark_Swarm, and false if it is not
     */
    public boolean isUnderDarkSwarm() {
        return unitData.isUnderDarkSwarm();
    }

    /**
     * Checks if this unit is currently being affected by a @Disruption_Web.
     *
     * @return true if this unit is under the effects of @Disruption_Web.
     */
    public boolean isUnderDisruptionWeb() {
        return unitData.isUnderDWeb();
    }

    /**
     * Checks if this unit is currently taking damage from a @Psi_Storm.
     *
     * @return true if this unit is losing hit points from a @Psi_Storm, and false otherwise.
     */
    public boolean isUnderStorm() {
        return unitData.isUnderStorm();
    }

    /**
     * Checks if this unit has power. Most structures are powered by default,
     * but @Protoss structures require a @Pylon to be powered and functional.
     *
     * @return true if this unit has power or is inaccessible, and false if this unit is
     * unpowered.
     * @since 4.0.1 Beta (previously isUnpowered)
     */
    public boolean isPowered() {
        return unitData.isPowered();
    }

    /**
     * Checks if this unit is a structure that is currently upgrading an upgrade.
     * See UpgradeTypes for a full list of upgrades in Broodwar.
     *
     * @return true if this structure is upgrading, false otherwise
     * @see #upgrade
     * @see #cancelUpgrade
     * @see #getUpgrade
     * @see #getRemainingUpgradeTime
     */
    public boolean isUpgrading() {
        return getOrder() == Upgrade;
    }

    public boolean isVisible() {
        return isVisible(game.self());
    }

    /**
     * Checks if this unit is visible.
     *
     * @param player The player to check visibility for. If this parameter is omitted, then the BWAPI player obtained from {@link Game#self()} will be used.
     * @return true if this unit is visible to the specified player, and false if it is not.
     * <p>
     * If the {@link Flag#CompleteMapInformation} flag is enabled, existing units hidden by the
     * fog of war will be accessible, but isVisible will still return false.
     * @see #exists
     */
    public boolean isVisible(final Player player) {
        return unitData.isVisible(player.getID());
    }

    /**
     * Performs some cheap checks to attempt to quickly detect whether the unit is
     * unable to be targetted as the target unit of an unspecified command.
     *
     * @return true if BWAPI was unable to determine whether the unit can be a target, false if an error occurred and the unit can not be a target.
     * @see Unit#canTargetUnit
     */
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


    /**
     * This function issues a command to the unit(s), however it is used for interfacing
     * only, and is recommended to use one of the more specific command functions when writing an
     * AI.
     *
     * @param command A {@link UnitCommand} containing command parameters such as the type, position, target, etc.
     * @return true if the command was passed to Broodwar, and false if BWAPI determined that
     * the command would fail.
     * <p>
     * There is a small chance for a command to fail after it has been passed to Broodwar.
     * @see UnitCommandType
     * @see Unit#canIssueCommand
     */
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

        if (game.isLatComEnabled()) {
            new CommandTemp(command, game).execute();
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

    /**
     * Orders the unit(s) to attack move to the specified position.
     *
     * @param target            A {@link Position} to designate as the target. The unit will perform an Attack Move command.
     * @param shiftQueueCommand If this value is true, then the order will be queued instead of immediately executed. If this value is omitted, then the order will be executed immediately by default.
     * @return true if the command was passed to Broodwar, and false if BWAPI determined that
     * the command would fail.
     * <p>
     * There is a small chance for a command to fail after it has been passed to Broodwar.
     * <p>
     * A @Medic will use Heal Move instead of attack.
     * @see Unit#canAttack
     */
    public boolean attack(final Position target, final boolean shiftQueueCommand) {
        return issueCommand(UnitCommand.attack(this, target, shiftQueueCommand));
    }

    public boolean attack(final Unit target, final boolean shiftQueueCommand) {
        return issueCommand(UnitCommand.attack(this, target, shiftQueueCommand));
    }

    public boolean build(final UnitType type) {
        return issueCommand(UnitCommand.train(this, type));
    }

    /**
     * Orders the worker unit(s) to construct a structure at a target position.
     *
     * @param type   The {@link UnitType} to build.
     * @param target A {@link TilePosition} to specify the build location, specifically the upper-left corner of the location. If the target is not specified, then the function call will be redirected to the train command.
     * @return true if the command was passed to Broodwar, and false if BWAPI determined that
     * the command would fail.
     * <p>
     * There is a small chance for a command to fail after it has been passed to Broodwar.
     * <p>
     * You must have sufficient resources and meet the necessary requirements in order to
     * build a structure.
     * @see Unit#train
     * @see Unit#cancelConstruction
     * @see Unit#canBuild
     */
    public boolean build(final UnitType type, final TilePosition target) {
        return issueCommand(UnitCommand.build(this, target, type));
    }

    /**
     * Orders the @Terran structure(s) to construct an add-on.
     *
     * @param type The add-on {@link UnitType} to construct.
     * @return true if the command was passed to Broodwar, and false if BWAPI determined that
     * the command would fail.
     * <p>
     * There is a small chance for a command to fail after it has been passed to Broodwar.
     * <p>
     * You must have sufficient resources and meet the necessary requirements in order to
     * build a structure.
     * @see Unit#build
     * @see Unit#cancelAddon
     * @see Unit#canBuildAddon
     */
    public boolean buildAddon(final UnitType type) {
        return issueCommand(UnitCommand.buildAddon(this, type));
    }

    /**
     * Orders the unit(s) to add a UnitType to its training queue, or morphs into the
     * {@link UnitType} if it is @Zerg.
     *
     * @param type The {@link UnitType} to train.
     * @return true if the command was passed to Broodwar, and false if BWAPI determined that
     * the command would fail.
     * <p>
     * There is a small chance for a command to fail after it has been passed to Broodwar.
     * <p>
     * You must have sufficient resources, supply, and meet the necessary requirements in
     * order to train a unit.
     * <p>
     * This command is also used for training @Interceptors and @Scarabs.
     * If you call this using a @Hatchery, @Lair, or @Hive, then it will automatically
     * pass the command to one of its @Larvae.
     * @see Unit#build
     * @see Unit#morph
     * @see Unit#cancelTrain
     * @see Unit#isTraining
     * @see Unit#canTrain
     */
    public boolean train(final UnitType type) {
        return issueCommand(UnitCommand.train(this, type));
    }

    /**
     * Orders the unit(s) to morph into a different {@link UnitType}.
     *
     * @param type The {@link UnitType} to morph into.
     * @return true if the command was passed to Broodwar, and false if BWAPI determined that
     * the command would fail.
     * <p>
     * There is a small chance for a command to fail after it has been passed to Broodwar.
     * @see Unit#build
     * @see Unit#morph
     * @see Unit#canMorph
     */
    public boolean morph(final UnitType type) {
        return issueCommand(UnitCommand.morph(this, type));
    }

    /**
     * Orders the unit to research the given tech type.
     *
     * @param tech The {@link TechType} to research.
     * @return true if the command was passed to Broodwar, and false if BWAPI determined that
     * the command would fail.
     * <p>
     * There is a small chance for a command to fail after it has been passed to Broodwar.
     * @see #cancelResearch
     * @see #isResearching
     * @see #getRemainingResearchTime
     * @see #getTech
     * @see #canResearch
     */
    public boolean research(final TechType tech) {
        return issueCommand(UnitCommand.research(this, tech));
    }

    /**
     * Orders the unit to upgrade the given upgrade type.
     *
     * @param upgrade The {@link UpgradeType} to upgrade.
     * @return true if the command was passed to Broodwar, and false if BWAPI determined that
     * the command would fail.
     * <p>
     * There is a small chance for a command to fail after it has been passed to Broodwar.
     * @see #cancelUpgrade
     * @see #isUpgrading
     * @see #getRemainingUpgradeTime
     * @see #getUpgrade
     * @see #canUpgrade
     */
    public boolean upgrade(final UpgradeType upgrade) {
        return issueCommand(UnitCommand.upgrade(this, upgrade));
    }

    /**
     * Orders the unit to set its rally position.
     *
     * @param target The target position that this structure will rally completed units to.
     * @return true if the command was passed to Broodwar, and false if BWAPI determined that
     * the command would fail.
     * <p>
     * There is a small chance for a command to fail after it has been passed to Broodwar.
     * @see #getRallyPosition
     * @see #getRallyUnit
     * @see #canSetRallyPoint
     * @see #canSetRallyPosition
     * @see #canSetRallyUnit
     */
    public boolean setRallyPoint(final Position target) {
        return issueCommand(UnitCommand.setRallyPoint(this, target));
    }

    public boolean setRallyPoint(final Unit target) {
        return issueCommand(UnitCommand.setRallyPoint(this, target));
    }

    public boolean move(final Position target) {
        return issueCommand(UnitCommand.move(this, target));
    }

    /**
     * Orders the unit to move from its current position to the specified position.
     *
     * @param target            The target position to move to.
     * @param shiftQueueCommand If this value is true, then the order will be queued instead of immediately executed. If this value is omitted, then the order will be executed immediately by default.
     * @return true if the command was passed to Broodwar, and false if BWAPI determined that
     * the command would fail.
     * There is a small chance for a command to fail after it has been passed to Broodwar.
     * @see #isMoving
     * @see #canMove
     */
    public boolean move(final Position target, final boolean shiftQueueCommand) {
        return issueCommand(UnitCommand.move(this, target, shiftQueueCommand));
    }

    public boolean patrol(final Position target) {
        return issueCommand(UnitCommand.patrol(this, target));
    }

    /**
     * Orders the unit to patrol between its current position and the specified position.
     * While patrolling, units will attack and chase enemy units that they encounter, and then
     * return to its patrol route. @Medics will automatically heal units and then return to their
     * patrol route.
     *
     * @param target            The position to patrol to.
     * @param shiftQueueCommand If this value is true, then the order will be queued instead of immediately executed. If this value is omitted, then the order will be executed immediately by default.
     * @return true if the command was passed to Broodwar, and false if BWAPI determined that
     * the command would fail.
     * <p>
     * There is a small chance for a command to fail after it has been passed to Broodwar.
     * @see #isPatrolling
     * @see #canPatrol
     */
    public boolean patrol(final Position target, final boolean shiftQueueCommand) {
        return issueCommand(UnitCommand.patrol(this, target, shiftQueueCommand));
    }

    public boolean holdPosition() {
        return issueCommand(UnitCommand.holdPosition(this));
    }

    /**
     * Orders the unit to hold its position.
     *
     * @param shiftQueueCommand If this value is true, then the order will be queued instead of immediately executed. If this value is omitted, then the order will be executed immediately by default.
     * @return true if the command was passed to Broodwar, and false if BWAPI determined that
     * the command would fail.
     * <p>
     * There is a small chance for a command to fail after it has been passed to Broodwar.
     * @see #canHoldPosition
     * @see #isHoldingPosition
     */
    public boolean holdPosition(final boolean shiftQueueCommand) {
        return issueCommand(UnitCommand.holdPosition(this, shiftQueueCommand));
    }

    public boolean stop() {
        return issueCommand(UnitCommand.stop(this));
    }

    /**
     * Orders the unit to stop.
     *
     * @param shiftQueueCommand If this value is true, then the order will be queued instead of immediately executed. If this value is omitted, then the order will be executed immediately by default.
     * @return true if the command was passed to Broodwar, and false if BWAPI determined that
     * the command would fail.
     * <p>
     * There is a small chance for a command to fail after it has been passed to Broodwar.
     * @see #canStop
     * @see #isIdle
     */
    public boolean stop(final boolean shiftQueueCommand) {
        return issueCommand(UnitCommand.stop(this, shiftQueueCommand));
    }

    public boolean follow(final Unit target) {
        return issueCommand(UnitCommand.follow(this, target));
    }

    /**
     * Orders the unit to follow the specified unit. Units that are following
     * other units will not perform any other actions such as attacking. They will ignore attackers.
     *
     * @param target            The target unit to start following.
     * @param shiftQueueCommand If this value is true, then the order will be queued instead of immediately executed. If this value is omitted, then the order will be executed immediately by default.
     * @return true if the command was passed to Broodwar, and false if BWAPI determined that
     * the command would fail.
     * <p>
     * There is a small chance for a command to fail after it has been passed to Broodwar.
     * @see #isFollowing
     * @see #canFollow
     * @see #getOrderTarget
     */
    public boolean follow(final Unit target, final boolean shiftQueueCommand) {
        return issueCommand(UnitCommand.follow(this, target, shiftQueueCommand));
    }

    public boolean gather(final Unit target) {
        return issueCommand(UnitCommand.gather(this, target));
    }

    /**
     * Orders the unit to gather the specified unit (must be mineral or refinery type).
     *
     * @param target            The target unit to gather from.
     * @param shiftQueueCommand If this value is true, then the order will be queued instead of immediately executed. If this value is omitted, then the order will be executed immediately by default.
     * @return true if the command was passed to Broodwar, and false if BWAPI determined that
     * the command would fail.
     * <p>
     * There is a small chance for a command to fail after it has been passed to Broodwar.
     * @see #isGatheringGas
     * @see #isGatheringMinerals
     * @see #canGather
     */
    public boolean gather(final Unit target, final boolean shiftQueueCommand) {
        return issueCommand(UnitCommand.gather(this, target, shiftQueueCommand));
    }

    public boolean returnCargo() {
        return issueCommand(UnitCommand.returnCargo(this));
    }

    /**
     * Orders the unit to return its cargo to a nearby resource depot such as a Command
     * Center. Only workers that are carrying minerals or gas can be ordered to return
     * cargo.
     *
     * @param shiftQueueCommand If this value is true, then the order will be queued instead of immediately executed. If this value is omitted, then the order will be executed immediately by default.
     * @return true if the command was passed to Broodwar, and false if BWAPI determined that
     * the command would fail.
     * <p>
     * There is a small chance for a command to fail after it has been passed to Broodwar.
     * @see #isCarryingGas
     * @see #isCarryingMinerals
     * @see #canReturnCargo
     */
    public boolean returnCargo(final boolean shiftQueueCommand) {
        return issueCommand(UnitCommand.returnCargo(this, shiftQueueCommand));
    }

    public boolean repair(final Unit target) {
        return issueCommand(UnitCommand.repair(this, target));
    }

    /**
     * Orders the unit to repair the specified unit. Only Terran SCVs can be
     * ordered to repair, and the target must be a mechanical @Terran unit or building.
     *
     * @param target            The unit to repair.
     * @param shiftQueueCommand If this value is true, then the order will be queued instead of immediately executed. If this value is omitted, then the order will be executed immediately by default.
     * @return true if the command was passed to Broodwar, and false if BWAPI determined that
     * the command would fail.
     * <p>
     * There is a small chance for a command to fail after it has been passed to Broodwar.
     * @see #isRepairing
     * @see #canRepair
     */
    public boolean repair(final Unit target, final boolean shiftQueueCommand) {
        return issueCommand(UnitCommand.repair(this, target, shiftQueueCommand));
    }

    /**
     * Orders the unit to burrow. Either the unit must be a @Lurker, or the
     * unit must be a @Zerg ground unit that is capable of @Burrowing, and @Burrow technology
     * must be researched.
     *
     * @return true if the command was passed to Broodwar, and false if BWAPI determined that
     * the command would fail.
     * There is a small chance for a command to fail after it has been passed to Broodwar.
     * @see #unburrow
     * @see #isBurrowed
     * @see #canBurrow
     */
    public boolean burrow() {
        return issueCommand(UnitCommand.burrow(this));
    }

    /**
     * Orders a burrowed unit to unburrow.
     *
     * @return true if the command was passed to Broodwar, and false if BWAPI determined that
     * the command would fail.
     * <p>
     * There is a small chance for a command to fail after it has been passed to Broodwar.
     * @see #burrow
     * @see #isBurrowed
     * @see #canUnburrow
     */
    public boolean unburrow() {
        return issueCommand(UnitCommand.unburrow(this));
    }

    /**
     * Orders the unit to cloak.
     *
     * @return true if the command was passed to Broodwar, and false if BWAPI determined that
     * the command would fail.
     * <p>
     * There is a small chance for a command to fail after it has been passed to Broodwar.
     * @see #decloak
     * @see #isCloaked
     * @see #canCloak
     */
    public boolean cloak() {
        return issueCommand(UnitCommand.cloak(this));
    }

    /**
     * Orders a cloaked unit to decloak.
     *
     * @return true if the command was passed to Broodwar, and false if BWAPI determined that
     * the command would fail.
     * <p>
     * There is a small chance for a command to fail after it has been passed to Broodwar.
     * @see #cloak
     * @see #isCloaked
     * @see #canDecloak
     */
    public boolean decloak() {
        return issueCommand(UnitCommand.decloak(this));
    }

    /**
     * Orders the unit to siege. Only works for @Siege_Tanks.
     *
     * @return true if the command was passed to Broodwar, and false if BWAPI determined that
     * the command would fail.
     * <p>
     * There is a small chance for a command to fail after it has been passed to Broodwar.
     * @see #unsiege
     * @see #isSieged
     * @see #canSiege
     */
    public boolean siege() {
        return issueCommand(UnitCommand.siege(this));
    }

    /**
     * Orders the unit to unsiege. Only works for sieged @Siege_Tanks.
     *
     * @return true if the command was passed to Broodwar, and false if BWAPI determined that
     * the command would fail.
     * <p>
     * There is a small chance for a command to fail after it has been passed to Broodwar.
     * @see #siege
     * @see #isSieged
     * @see #canUnsiege
     */
    public boolean unsiege() {
        return issueCommand(UnitCommand.unsiege(this));
    }

    /**
     * Orders the unit to lift. Only works for liftable @Terran structures.
     *
     * @return true if the command was passed to Broodwar, and false if BWAPI determined that
     * the command would fail.
     * <p>
     * There is a small chance for a command to fail after it has been passed to Broodwar.
     * @see #land
     * @see #isLifted
     * @see #canLift
     */
    public boolean lift() {
        return issueCommand(UnitCommand.lift(this));
    }

    /**
     * Orders the unit to land. Only works for @Terran structures that are
     * currently lifted.
     *
     * @param target The tile position to land this structure at.
     * @return true if the command was passed to Broodwar, and false if BWAPI determined that
     * the command would fail.
     * <p>
     * There is a small chance for a command to fail after it has been passed to Broodwar.
     * @see #lift
     * @see #isLifted
     * @see #canLand
     */
    public boolean land(final TilePosition target) {
        return issueCommand(UnitCommand.land(this, target));
    }

    public boolean load(final Unit target) {
        return issueCommand(UnitCommand.load(this, target));
    }

    /**
     * Orders the unit to load the target unit. Only works if this unit is a @Transport or @Bunker type.
     *
     * @param target            The target unit to load into this @Transport or @Bunker.
     * @param shiftQueueCommand If this value is true, then the order will be queued instead of immediately executed. If this value is omitted, then the order will be executed immediately by default.
     * @return true if the command was passed to Broodwar, and false if BWAPI determined that
     * the command would fail.
     * <p>
     * There is a small chance for a command to fail after it has been passed to Broodwar.
     * @see #unload
     * @see #unloadAll
     * @see #getLoadedUnits
     * @see #isLoaded
     */
    public boolean load(final Unit target, final boolean shiftQueueCommand) {
        return issueCommand(UnitCommand.load(this, target, shiftQueueCommand));
    }

    /**
     * Orders the unit to unload the target unit. Only works for @Transports
     * and @Bunkers.
     *
     * @param target Unloads the target unit from this @Transport or @Bunker.
     * @return true if the command was passed to Broodwar, and false if BWAPI determined that
     * the command would fail.
     * <p>
     * There is a small chance for a command to fail after it has been passed to Broodwar.
     * @see #load
     * @see #unloadAll
     * @see #getLoadedUnits
     * @see #isLoaded
     * @see #canUnload
     * @see #canUnloadAtPosition
     */
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

    /**
     * Orders the unit to unload all loaded units at the unit's current position.
     * Only works for @Transports and @Bunkers.
     *
     * @param shiftQueueCommand If this value is true, then the order will be queued instead of immediately executed. If this value is omitted, then the order will be executed immediately by default.
     * @return true if the command was passed to Broodwar, and false if BWAPI determined that
     * the command would fail.
     * <p>
     * There is a small chance for a command to fail after it has been passed to Broodwar.
     * @see #load
     * @see #unload
     * @see #getLoadedUnits
     * @see #isLoaded
     * @see #canUnloadAll
     * @see #canUnloadAtPosition
     */
    public boolean unloadAll(final Position target, final boolean shiftQueueCommand) {
        return issueCommand(UnitCommand.unloadAll(this, target, shiftQueueCommand));
    }

    public boolean rightClick(final Position target) {
        return issueCommand(UnitCommand.rightClick(this, target));
    }

    public boolean rightClick(final Unit target) {
        return issueCommand(UnitCommand.rightClick(this, target));
    }

    /**
     * Performs a right click action as it would work in StarCraft.
     *
     * @param target            The target position to right click.
     * @param shiftQueueCommand If this value is true, then the order will be queued instead of immediately executed. If this value is omitted, then the order will be executed immediately by default.
     * @return true if the command was passed to Broodwar, and false if BWAPI determined that
     * the command would fail.
     * <p>
     * There is a small chance for a command to fail after it has been passed to Broodwar.
     * @see #canRightClick
     * @see #canRightClickPosition
     * @see #canRightClickUnit
     */
    public boolean rightClick(final Position target, final boolean shiftQueueCommand) {
        return issueCommand(UnitCommand.rightClick(this, target, shiftQueueCommand));
    }

    public boolean rightClick(final Unit target, final boolean shiftQueueCommand) {
        return issueCommand(UnitCommand.rightClick(this, target, shiftQueueCommand));
    }

    /**
     * Orders a @SCV to stop constructing a structure. This leaves the
     * structure in an incomplete state until it is either cancelled, razed, or completed by
     * another @SCV.
     *
     * @return true if the command was passed to Broodwar, and false if BWAPI determined that
     * the command would fail.
     * <p>
     * There is a small chance for a command to fail after it has been passed to Broodwar.
     * @see #isConstructing
     * @see #canHaltConstruction
     */
    public boolean haltConstruction() {
        return issueCommand(UnitCommand.haltConstruction(this));
    }

    /**
     * Orders this unit to cancel and refund itself from begin constructed.
     *
     * @return true if the command was passed to Broodwar, and false if BWAPI determined that
     * the command would fail.
     * <p>
     * There is a small chance for a command to fail after it has been passed to Broodwar.
     * @see #isBeingConstructed
     * @see #build
     * @see #canCancelConstruction
     */
    public boolean cancelConstruction() {
        return issueCommand(UnitCommand.cancelConstruction(this));
    }

    /**
     * Orders this unit to cancel and refund an add-on that is being constructed.
     *
     * @return true if the command was passed to Broodwar, and false if BWAPI determined that
     * the command would fail.
     * <p>
     * There is a small chance for a command to fail after it has been passed to Broodwar.
     * @see #canCancelAddon
     * @see #buildAddon
     */
    public boolean cancelAddon() {
        return issueCommand(UnitCommand.cancelAddon(this));
    }

    public boolean cancelTrain() {
        return issueCommand(UnitCommand.cancelTrain(this));
    }

    /**
     * Orders the unit to remove the specified unit from its training queue.
     *
     * @param slot Identifies the slot that will be cancelled. If the specified value is at least 0, then the unit in the corresponding slot from the list provided by {@link #getTrainingQueue} will be cancelled. If the value is either omitted or -2, then the last slot is cancelled.
     *             <p>
     *             The value of slot is passed directly to Broodwar. Other negative values have no
     *             effect.
     * @see #train
     * @see #cancelTrain
     * @see #isTraining
     * @see #getTrainingQueue
     * @see #canCancelTrain
     * @see #canCancelTrainSlot
     */
    public boolean cancelTrain(final int slot) {
        return issueCommand(UnitCommand.cancelTrain(this, slot));
    }

    /**
     * Orders this unit to cancel and refund a unit that is morphing.
     *
     * @return true if the command was passed to Broodwar, and false if BWAPI determined that
     * the command would fail.
     * <p>
     * There is a small chance for a command to fail after it has been passed to Broodwar.
     * @see #morph
     * @see #isMorphing
     * @see #canCancelMorph
     */
    public boolean cancelMorph() {
        return issueCommand(UnitCommand.cancelMorph(this));
    }

    /**
     * Orders this unit to cancel and refund a research that is in progress.
     *
     * @return true if the command was passed to Broodwar, and false if BWAPI determined that
     * the command would fail.
     * <p>
     * There is a small chance for a command to fail after it has been passed to Broodwar.
     * @see #research
     * @see #isResearching
     * @see #getTech
     * @see #canCancelResearch
     */
    public boolean cancelResearch() {
        return issueCommand(UnitCommand.cancelResearch(this));
    }

    /**
     * Orders this unit to cancel and refund an upgrade that is in progress.
     *
     * @return true if the command was passed to Broodwar, and false if BWAPI determined that
     * the command would fail.
     * <p>
     * There is a small chance for a command to fail after it has been passed to Broodwar.
     * @see #upgrade
     * @see #isUpgrading
     * @see #getUpgrade
     * @see #canCancelUpgrade
     */
    public boolean cancelUpgrade() {
        return issueCommand(UnitCommand.cancelUpgrade(this));
    }

    public boolean useTech(final TechType tech) {
        return issueCommand(UnitCommand.useTech(this, tech));
    }

    /**
     * Orders the unit to use a technology.
     *
     * @param tech   The technology type to use.
     * @param target If specified, indicates the target location to use the tech on.
     * @return true if the command was passed to Broodwar, and false if BWAPI determined that
     * the command would fail.
     * @see #canUseTechWithOrWithoutTarget
     * @see #canUseTech
     * @see #canUseTechWithoutTarget
     * @see #canUseTechUnit
     * @see #canUseTechPosition
     * @see TechType
     */
    public boolean useTech(final TechType tech, final Position target) {
        return issueCommand(UnitCommand.useTech(this, tech, target));
    }

    public boolean useTech(final TechType tech, final Unit target) {
        return issueCommand(UnitCommand.useTech(this, tech, target));
    }

    /**
     * Moves a @Flag_Beacon to a different location. This is only used for @CTF
     * or @UMS game types.
     *
     * @param target The target tile position to place the @Flag_Beacon.
     * @return true if the command was passed to Broodwar, and false if BWAPI determined that
     * the command would fail.
     * <p>
     * There is a small chance for a command to fail after it has been passed to Broodwar.
     * <p>
     * This command is only available for the first 10 minutes of the game, as in Broodwar.
     * @see #canPlaceCOP
     */
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

    /**
     * Checks whether the unit is able to execute the given command. If you
     * are calling this function repeatedly (e.g. to generate a collection of valid commands),
     * you can avoid repeating the same kinds of checks by specifying false for some of the
     * optional boolean arguments. Make sure that the state hasn't changed since the check was
     * done though (eg a new frame/event, or a command issued). Also see the more specific functions.
     *
     * @param command                            A {@link UnitCommand} to check.
     * @param checkCanUseTechPositionOnPositions Only used if the command type is {@link UnitCommandType#Use_Tech_Position}. A boolean for whether to perform cheap checks for whether the unit is unable to target any positions using the command's {@link TechType} (i.e. regardless of what the other command parameters are). You can set this to false if you know this check has already just been performed.
     * @param checkCanUseTechUnitOnUnits         Only used if the command type is {@link UnitCommandType#Use_Tech_Unit}. A boolean for whether to perform cheap checks for whether the unit is unable to target any units using the command's {@link TechType} (i.e. regardless of what the other command parameters are). You can set this to false if you know this check has already just been performed.
     * @param checkCanBuildUnitType              Only used if the command type is {@link UnitCommandType#Build}. A boolean for whether to perform cheap checks for whether the unit is unable to build the specified {@link UnitType} (i.e. regardless of what the other command parameters are). You can set this to false if you know this check has already just been performed.
     * @param checkCanTargetUnit                 Only used for command types that can target a unit. A boolean for whether to perform {@link Unit#canTargetUnit} as a check. You can set this to false if you know this check has already just been performed.
     * @param checkCanIssueCommandType           A boolean for whether to perform {@link Unit#canIssueCommandType} as a check. You can set this to false if you know this check has already just been performed.
     * @param checkCommandibility                A boolean for whether to perform {@link Unit#canCommand} as a check. You can set this to false if you know this check has already just been performed.
     * @return true if BWAPI determined that the command is valid, false if an error occurred and the command is invalid.
     * @see UnitCommandType
     * @see Unit#canCommand
     * @see Unit#canIssueCommandType
     * @see Unit#canTargetUnit
     */
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
                return canUseTechWithoutTarget(TechType.idToEnum[command.extra], false, false);
            case Use_Tech_Unit:
                return canUseTechUnit(TechType.idToEnum[command.extra], command.target, checkCanTargetUnit, checkCanUseTechUnitOnUnits, false, false);
            case Use_Tech_Position:
                return canUseTechPosition(TechType.idToEnum[command.extra], command.getTargetPosition(), checkCanUseTechPositionOnPositions, false, false);
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

    /**
     * Checks whether the unit is able to execute the given command as part of a List<Unit>
     * (even if none of the units in the List<Unit> are able to execute the command individually).
     * The reason this function exists is because some commands are valid for an individual unit
     * but not for those individuals as a group (e.g. buildings, critters) and some commands are
     * only valid for a unit if it is commanded as part of a unit group, e.g.:
     * 1. attackMove/attackUnit for a List<Unit>, some of which can't attack, e.g. @High_Templar.
     * This is supported simply for consistency with BW's behaviour - you
     * could issue move command(s) individually instead.
     * 2. attackMove/move/patrol/rightClickPosition for air unit(s) + e.g. @Larva, as part of
     * the air stacking technique. This is supported simply for consistency with BW's
     * behaviour - you could issue move/patrol/rightClickPosition command(s) for them
     * individually instead.
     * <p>
     * BWAPI allows the following special cases to command a unit individually (rather than
     * only allowing it to be commanded as part of a List<Unit>). These commands are not available
     * to a user in BW when commanding units individually, but BWAPI allows them for convenience:
     * - attackMove for @Medic, which is equivalent to Heal Move.
     * - holdPosition for burrowed @Lurker, for ambushes.
     * - stop for @Larva, to move it to a different side of the @Hatchery / @Lair / @Hive (e.g.
     * so that @Drones morphed later morph nearer to minerals/gas).
     *
     * @see UnitCommandType
     * @see Unit#canIssueCommand
     * @see Unit#canCommandGrouped
     * @see Unit#canIssueCommandTypeGrouped
     * @see Unit#canTargetUnit
     */
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
                return canUseTechWithoutTarget(TechType.idToEnum[command.extra], false, false);
            case Use_Tech_Unit:
                return canUseTechUnit(TechType.idToEnum[command.extra], command.target, checkCanTargetUnit, checkCanUseTechUnitOnUnits, false, false);
            case Use_Tech_Position:
                return canUseTechPosition(TechType.idToEnum[command.extra], command.getTargetPosition(), checkCanUseTechPositionOnPositions, false, false);
            case Place_COP:
                return false;
        }
        return true;
    }


    /**
     * Performs some cheap checks to attempt to quickly detect whether the unit is unable to
     * execute any commands (eg the unit is stasised).
     *
     * @return true if BWAPI was unable to determine whether the unit can be commanded, false if an error occurred and the unit can not be commanded.
     * @see Unit#canIssueCommand
     */
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
                for (final Unit larva : getLarva()) {
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

    /**
     * Performs some cheap checks to attempt to quickly detect whether the unit is unable to
     * execute any commands as part of a List<Unit> (eg buildings, critters).
     *
     * @return true if BWAPI was unable to determine whether the unit can be commanded grouped, false if an error occurred and the unit can not be commanded grouped.
     * @see Unit#canIssueCommandGrouped
     * @see Unit#canIssueCommand
     */
    public boolean canCommandGrouped(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        return !getType().isBuilding() && !getType().isCritter();
    }

    public boolean canIssueCommandType(UnitCommandType ct) {
        return canIssueCommandType(ct, true);
    }

    /**
     * Performs some cheap checks to attempt to quickly detect whether the unit is unable to
     * execute the given command type (i.e. regardless of what other possible command parameters
     * could be).
     *
     * @param ct                  A {@link UnitCommandType}.
     * @param checkCommandibility A boolean for whether to perform {@link Unit#canCommand} as a check. You can set this to false if you know this check has already just been performed.
     * @return true if BWAPI was unable to determine whether the command type is invalid, false if an error occurred and the command type is invalid.
     * @see UnitCommandType
     * @see Unit#canIssueCommand
     */
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

    /**
     * Performs some cheap checks to attempt to quickly detect whether the unit is unable to
     * execute the given command type (i.e. regardless of what other possible command parameters
     * could be) as part of a List<Unit>.
     *
     * @param ct                         A {@link UnitCommandType}.
     * @param checkCommandibilityGrouped A boolean for whether to perform {@link Unit#canCommandGrouped} as a check. You can set this to false if you know this check has already just been performed.
     * @param checkCommandibility        A boolean for whether to perform {@link Unit#canCommand} as a check. You can set this to false if you know this check has already just been performed.
     * @return true if BWAPI was unable to determine whether the command type is invalid, false if an error occurred and the command type is invalid.
     * @see UnitCommandType
     * @see Unit#canIssueCommandGrouped
     */
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

    /**
     * Performs some cheap checks to attempt to quickly detect whether the unit is unable to
     * use the given unit as the target unit of an unspecified command.
     *
     * @param targetUnit          A target unit for an unspecified command.
     * @param checkCommandibility A boolean for whether to perform {@link Unit#canCommand} as a check. You can set this to false if you know this check has already just been performed.
     * @return true if BWAPI was unable to determine whether the unit can target the given unit, false if an error occurred and the unit can not target the given unit.
     * @see Unit#canIssueCommand
     * @see Unit#isTargetable
     */
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

    /**
     * Cheap checks for whether the unit is able to execute an attack command to attack-move or attack a unit.
     *
     * @see Unit#canIssueCommand
     * @see Unit#attack
     * @see Unit#canAttackMove
     * @see Unit#canAttackUnit
     */
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

    /**
     * Cheap checks for whether the unit is able to execute an attack command to attack-move or attack a unit,
     * as part of a List<Unit>.
     *
     * @see Unit#canIssueCommandGrouped
     * @see Unit#canAttack
     */
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

    /**
     * Checks whether the unit is able to execute an attack command to attack-move.
     *
     * @see Unit#canIssueCommand
     * @see Unit#attack
     */
    public boolean canAttackMove(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        return (getType() == Terran_Medic || canAttackUnit(false)) && canMove(false);
    }

    public boolean canAttackMoveGrouped(boolean checkCommandibilityGrouped) {
        return canAttackMoveGrouped(checkCommandibilityGrouped, true);
    }

    public boolean canAttackMoveGrouped() {
        return canAttackMoveGrouped(true);
    }

    /**
     * Checks whether the unit is able to execute an attack command to attack-move, as part of a
     * List<Unit>.
     *
     * @see Unit#canIssueCommandGrouped
     * @see Unit#canAttackMove
     */
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
            } else {
                return false;
            }
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

    /**
     * Cheap checks for whether the unit is able to execute an attack command to attack a unit.
     *
     * @see Unit#canIssueCommand
     * @see Unit#attack
     */
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
        final WeaponType weapon = targetInAir ? type.airWeapon() : type.groundWeapon();

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

        if (!type.canMove() && !isInWeaponRange(targetUnit)) {
            return false;
        }

        if (type == Zerg_Lurker && !isInWeaponRange(targetUnit)) {
            return false;
        }

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

    /**
     * Cheap checks for whether the unit is able to execute an attack command to attack a unit,
     * as part of a List<Unit>.
     *
     * @see Unit#canIssueCommandGrouped
     * @see Unit#canAttackUnit
     */
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

    /**
     * Cheap checks for whether the unit is able to execute a build command.
     *
     * @see Unit#canIssueCommand
     * @see Unit#build
     */
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

    /**
     * Cheap checks for whether the unit is able to execute a buildAddon command.
     *
     * @see Unit#canIssueCommand
     * @see Unit#buildAddon
     */
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
            for (final Unit larva : getLarva()) {
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

    /**
     * Cheap checks for whether the unit is able to execute a train command.
     *
     * @see Unit#canIssueCommand
     * @see Unit#train
     */
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
                for (final Unit larva : getLarva()) {
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
            for (final Unit larva : getLarva()) {
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

    /**
     * Cheap checks for whether the unit is able to execute a morph command.
     *
     * @see Unit#canIssueCommand
     * @see Unit#morph
     */
    public boolean canMorph(UnitType uType, boolean checkCanIssueCommandType, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (checkCanIssueCommandType && !canMorph(false)) {
            return false;
        }

        Unit thisUnit = this;
        if (getType().producesLarva()) {
            if (uType.whatBuilds().getKey() == Zerg_Larva) {
                boolean foundCommandableLarva = false;
                for (final Unit larva : getLarva()) {
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

    /**
     * Cheap checks for whether the unit is able to execute a research command.
     *
     * @see Unit#canIssueCommand
     * @see Unit#research
     */
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

    /**
     * Cheap checks for whether the unit is able to execute an upgrade command.
     *
     * @see Unit#canIssueCommand
     * @see Unit#upgrade
     */
    public boolean canUpgrade(UpgradeType type, boolean checkCanIssueCommandType) {
        final Player self = game.self();

        if (!getPlayer().equals(self)) {
            return false;
        }

        if (!getType().isSuccessorOf(type.whatUpgrades())) {
            return false;
        }

        if (checkCanIssueCommandType && (isLifted() || !isIdle() || !isCompleted())) {
            return false;
        }

        if (!self.hasUnitTypeRequirement(type.whatUpgrades())) {
            return false;
        }

        final int nextLvl = self.getUpgradeLevel(type) + 1;

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

    /**
     * Cheap checks for whether the unit is able to execute a setRallyPoint command to a
     * position or unit.
     *
     * @see Unit#canIssueCommand
     * @see Unit#setRallyPoint
     * @see Unit#canSetRallyPosition
     * @see Unit#canSetRallyUnit
     */
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

    /**
     * Checks whether the unit is able to execute a setRallyPoint command to a position.
     *
     * @see Unit#canIssueCommand
     * @see Unit#setRallyPoint
     */
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

    /**
     * Cheap checks for whether the unit is able to execute a setRallyPoint command to a unit.
     *
     * @see Unit#canIssueCommand
     * @see Unit#setRallyPoint
     */
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

    /**
     * Checks whether the unit is able to execute a move command.
     *
     * @see Unit#canIssueCommand
     * @see Unit#move
     */
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

    /**
     * Checks whether the unit is able to execute a move command, as part of a List<Unit>.
     *
     * @see Unit#canIssueCommandGrouped
     * @see Unit#canMove
     */
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

    /**
     * Checks whether the unit is able to execute a patrol command.
     *
     * @see Unit#canIssueCommand
     * @see Unit#patrol
     */
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

    /**
     * Checks whether the unit is able to execute a patrol command, as part of a List<Unit>.
     *
     * @see Unit#canIssueCommandGrouped
     * @see Unit#canPatrol
     */
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

    /**
     * Cheap checks for whether the unit is able to execute a follow command.
     *
     * @see Unit#canIssueCommand
     * @see Unit#follow
     */
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

    /**
     * Cheap checks for whether the unit is able to execute a gather command.
     *
     * @see Unit#canIssueCommand
     * @see Unit#gather
     */
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

    /**
     * Checks whether the unit is able to execute a returnCargo command.
     *
     * @see Unit#canIssueCommand
     * @see Unit#returnCargo
     */
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

    /**
     * Checks whether the unit is able to execute a holdPosition command.
     *
     * @see Unit#canIssueCommand
     * @see Unit#holdPosition
     */
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

    /**
     * Checks whether the unit is able to execute a stop command.
     *
     * @see Unit#canIssueCommand
     * @see Unit#stop
     */
    public boolean canStop(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (!isCompleted()) {
            return false;
        }

        final UnitType ut = getType();

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

    /**
     * Cheap checks for whether the unit is able to execute a repair command.
     *
     * @see Unit#canIssueCommand
     * @see Unit#repair
     */
    public boolean canRepair(Unit targetUnit, boolean checkCanTargetUnit, boolean checkCanIssueCommandType, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (checkCanIssueCommandType && !canRepair(false)) {
            return false;
        }

        if (checkCanTargetUnit && !canTargetUnit(targetUnit, false)) {
            return false;
        }

        final UnitType targType = targetUnit.getType();
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

    /**
     * Checks whether the unit is able to execute a burrow command.
     *
     * @see Unit#canIssueCommand
     * @see Unit#burrow
     */
    public boolean canBurrow(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        return canUseTechWithoutTarget(TechType.Burrowing, true, false);
    }

    public boolean canUnburrow() {
        return canUnburrow(true);
    }

    /**
     * Checks whether the unit is able to execute an unburrow command.
     *
     * @see Unit#canIssueCommand
     * @see Unit#unburrow
     */
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

    /**
     * Checks whether the unit is able to execute a cloak command.
     *
     * @see Unit#canIssueCommand
     * @see Unit#cloak
     */
    public boolean canCloak(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        return canUseTechWithoutTarget(getType().cloakingTech(), true, false);
    }

    public boolean canDecloak() {
        return canDecloak(true);
    }

    /**
     * Checks whether the unit is able to execute a decloak command.
     *
     * @see Unit#canIssueCommand
     * @see Unit#decloak
     */
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

    /**
     * Checks whether the unit is able to execute a siege command.
     *
     * @see Unit#canIssueCommand
     * @see Unit#siege
     */
    public boolean canSiege(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        return canUseTechWithoutTarget(TechType.Tank_Siege_Mode, true, false);
    }

    public boolean canUnsiege() {
        return canUnsiege(true);
    }

    /**
     * Checks whether the unit is able to execute an unsiege command.
     *
     * @see Unit#canIssueCommand
     * @see Unit#unsiege
     */
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

    /**
     * Checks whether the unit is able to execute a lift command.
     *
     * @see Unit#canIssueCommand
     * @see Unit#lift
     */
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

    /**
     * Cheap checks for whether the unit is able to execute a land command.
     *
     * @see Unit#canIssueCommand
     * @see Unit#land
     */
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

    /**
     * Cheap checks for whether the unit is able to execute a load command.
     *
     * @see Unit#canIssueCommand
     * @see Unit#load
     */
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

        final int thisUnitSpaceProvided = getType().spaceProvided();
        final int targetSpaceProvided = targetUnit.getType().spaceProvided();
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

        if (unitThatLoads.isHallucination()) {
            return false;
        }
        final UnitType unitThatLoadsType = unitThatLoads.getType();

        if (unitThatLoadsType == Terran_Bunker) {
            if (!unitToBeLoadedType.isOrganic() || unitToBeLoadedType.getRace() != Terran) {
                return false;
            }
            if (!unitToBeLoaded.hasPath(unitThatLoads.getPosition())) {
                return false;
            }
        }

        int freeSpace = thisUnitSpaceProvided > 0 ? thisUnitSpaceProvided : targetSpaceProvided;
        for (final Unit u : unitThatLoads.getLoadedUnits()) {
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

    /**
     * Cheap checks for whether the unit is able to execute an unload command or unloadAll at
     * current position command or unloadAll at a different position command.
     *
     * @see Unit#canIssueCommand
     * @see Unit#unload
     * @see Unit#unloadAll
     */
    public boolean canUnloadWithOrWithoutTarget(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        final UnitType ut = getType();
        if (!ut.isBuilding() && !isInterruptible()) {
            return false;
        }

        if (getLoadedUnits().isEmpty()) {
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

    /**
     * Cheap checks for whether the unit is able to execute an unload command or unloadAll at
     * current position command or unloadAll at a different position command, for a given
     * position.
     *
     * @see Unit#canIssueCommand
     * @see Unit#unload
     * @see Unit#unloadAll
     */
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
            } else return game.isWalkable(targDropPos.x / 8, targDropPos.y / 8);
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

    /**
     * Cheap checks for whether the unit is able to execute an unload command.
     *
     * @see Unit#canIssueCommand
     * @see Unit#unload
     */
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

    /**
     * Checks whether the unit is able to execute an unloadAll command for the current position.
     *
     * @see Unit#canIssueCommand
     * @see Unit#unloadAll
     */
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

    /**
     * Cheap checks for whether the unit is able to execute an unloadAll command for a different
     * position.
     *
     * @see Unit#canIssueCommand
     * @see Unit#unloadAll
     */
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

    /**
     * Cheap checks for whether the unit is able to execute a rightClick command to a position
     * or unit.
     *
     * @see Unit#canIssueCommand
     * @see Unit#rightClick
     * @see Unit#canRightClickPosition
     * @see Unit#canRightClickUnit
     */
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

        if (checkCommandibilityGrouped && !canCommandGrouped(false)) {
            return false;
        }

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

    /**
     * Cheap checks for whether the unit is able to execute a rightClick command to a position
     * or unit, as part of a List<Unit>.
     *
     * @see Unit#canIssueCommandGrouped
     * @see Unit#canRightClickUnit
     */
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

    /**
     * Checks whether the unit is able to execute a rightClick command for a position.
     *
     * @see Unit#canIssueCommand
     * @see Unit#rightClick
     */
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

    /**
     * Checks whether the unit is able to execute a rightClick command for a position, as part of
     * a List<Unit>.
     *
     * @see Unit#canIssueCommandGrouped
     * @see Unit#canRightClick
     */
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

    /**
     * Cheap checks for whether the unit is able to execute a rightClick command to a unit.
     *
     * @see Unit#canIssueCommand
     * @see Unit#rightClick
     */
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

    /**
     * Cheap checks for whether the unit is able to execute a rightClick command to a unit, as
     * part of a List<Unit>.
     *
     * @see Unit#canIssueCommandGrouped
     * @see Unit#canRightClickUnit
     */
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

    /**
     * Checks whether the unit is able to execute a haltConstruction command.
     *
     * @see Unit#canIssueCommand
     * @see Unit#haltConstruction
     */
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

    /**
     * Checks whether the unit is able to execute a cancelConstruction command.
     *
     * @see Unit#canIssueCommand
     * @see Unit#cancelConstruction
     */
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

    /**
     * Checks whether the unit is able to execute a cancelAddon command.
     *
     * @see Unit#canIssueCommand
     * @see Unit#cancelAddon
     */
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

    /**
     * Checks whether the unit is able to execute a cancelTrain command for any slot.
     *
     * @see Unit#canIssueCommand
     * @see Unit#cancelTrain
     */
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

    /**
     * Cheap checks for whether the unit is able to execute a cancelTrain command for an
     * unspecified slot.
     *
     * @see Unit#canIssueCommand
     * @see Unit#cancelTrain
     */
    public boolean canCancelTrainSlot(int slot, boolean checkCanIssueCommandType, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (checkCanIssueCommandType && !canCancelTrainSlot(false)) {
            return false;
        }

        return isTraining() && slot >= 0 && getTrainingQueue().size() > slot;
    }

    public boolean canCancelMorph() {
        return canCancelMorph(true);
    }

    /**
     * Checks whether the unit is able to execute a cancelMorph command.
     *
     * @see Unit#canIssueCommand
     * @see Unit#cancelMorph
     */
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

    /**
     * Checks whether the unit is able to execute a cancelResearch command.
     *
     * @see Unit#canIssueCommand
     * @see Unit#cancelResearch
     */
    public boolean canCancelResearch(boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        return getOrder() == ResearchTech;
    }

    public boolean canCancelUpgrade() {
        return canCancelUpgrade(true);
    }

    /**
     * Checks whether the unit is able to execute a cancelUpgrade command.
     *
     * @see Unit#canIssueCommand
     * @see Unit#cancelUpgrade
     */
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

    /**
     * Cheap checks for whether the unit is able to execute a useTech command without a target or
     * or a useTech command with a target position or a useTech command with a target unit.
     *
     * @see Unit#canIssueCommand
     * @see Unit#useTech
     */
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
        if (tech != TechType.Burrowing && !tech.whatUses().contains(ut)) {
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

    public boolean canUseTech(TechType tech) {
        return canUseTech(tech, (Unit) null);
    }

    /**
     * Checks whether the unit is able to execute a useTech command for a specified position or
     * unit (only specify null if the TechType does not target another position/unit).
     *
     * @see Unit#canIssueCommand
     * @see Unit#useTech
     * @see Unit#canUseTechWithoutTarget
     * @see Unit#canUseTechUnit
     * @see Unit#canUseTechPosition
     */
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

    /**
     * Checks whether the unit is able to execute a useTech command without a target.
     *
     * @see Unit#canIssueCommand
     * @see Unit#useTech
     */
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

    /**
     * Cheap checks for whether the unit is able to execute a useTech command with an unspecified
     * target unit.
     *
     * @see Unit#canIssueCommand
     * @see Unit#useTech
     */
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

    /**
     * Checks whether the unit is able to execute a useTech command with an unspecified target
     * position.
     *
     * @see Unit#canIssueCommand
     * @see Unit#useTech
     */
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

        return unitData.getButtonset() != 228 && getOrder() == CTFCOPInit;
    }

    public boolean canPlaceCOP(TilePosition target, boolean checkCanIssueCommandType) {
        return canPlaceCOP(target, checkCanIssueCommandType, true);
    }

    public boolean canPlaceCOP(TilePosition target) {
        return canPlaceCOP(target, true);
    }

    /**
     * Cheap checks for whether the unit is able to execute a placeCOP command.
     *
     * @see Unit#canIssueCommand
     * @see Unit#placeCOP
     */
    public boolean canPlaceCOP(TilePosition target, boolean checkCanIssueCommandType, boolean checkCommandibility) {
        if (checkCommandibility && !canCommand()) {
            return false;
        }

        if (checkCanIssueCommandType && !canPlaceCOP(checkCommandibility)) {
            return false;
        }

        return game.canBuildHere(target, getType(), this, true);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Unit unit = (Unit) o;
        return id == unit.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public int compareTo(final Unit other) {
        return id - other.id;
    }

    void updatePosition(final int frame) {
        if (frame > lastPositionUpdate) {
            lastPositionUpdate = frame;
            position = new Position(unitData.getPositionX(), unitData.getPositionY());
        }
    }
}
