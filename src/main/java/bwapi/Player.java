package bwapi;

import bwapi.ClientData.PlayerData;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static bwapi.UnitType.*;
import static bwapi.UpgradeType.*;
import static bwapi.WeaponType.*;

/**
 * The Player represents a unique controller in the game. Each player in
 * a match will have his or her own player instance. There is also a neutral player which owns
 * all the neutral units (such as mineral patches and vespene geysers).
 *
 * @see PlayerType
 * @see Race
 */
public class Player implements Comparable<Player> {
    private final PlayerData playerData;
    private final Game game;
    private final int id;
    private final String name;
    private final Race race;
    private final PlayerType playerType;
    private final Force force;
    private final TilePosition startLocation;

    Player(final PlayerData playerData, final int id, final Game game) {
        this.playerData = playerData;
        this.game = game;
        this.id = id;
        this.name = playerData.getName();
        this.race = Race.idToEnum[playerData.getRace()];
        this.playerType = PlayerType.idToEnum[playerData.getType()];
        this.force = game.getForce(playerData.getForce());
        this.startLocation = new TilePosition(playerData.getStartLocationX(), playerData.getStartLocationY());
    }

    /**
     * Retrieves a unique ID that represents the player.
     *
     * @return An integer representing the ID of the player.
     */
    public int getID() {
        return id;
    }

    /**
     * Retrieves the name of the player.
     *
     * @return A String object containing the player's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the set of all units that the player owns. This also includes
     * incomplete units.
     *
     * @return Reference to a List<Unit> containing the units.
     * <p>
     * This does not include units that are loaded into transports, @Bunkers, @Refineries, @Assimilators, or @Extractors.
     */
    public List<Unit> getUnits() {
        return game.getAllUnits().stream()
                .filter(u -> equals(u.getPlayer()))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the race of the player. This allows you to change strategies
     * against different races, or generalize some commands for yourself.
     *
     * @return The Race that the player is using.
     * Returns {@link Race#Unknown} if the player chose {@link Race#Random} when the game started and they
     * have not been seen.
     */
    public Race getRace() {
        return race;
    }

    /**
     * Retrieves the player's controller type. This allows you to distinguish
     * betweeen computer and human players.
     *
     * @return The {@link PlayerType} that identifies who is controlling a player.
     * <p>
     * Other players using BWAPI will be treated as a human player and return {@link PlayerType#Player}.
     */
    public PlayerType getType() {
        return playerType;
    }

    /**
     * Retrieves the player's force. A force is the team that the player is
     * playing on.
     *
     * @return The {@link Force} object that the player is part of.
     */
    public Force getForce() {
        return force;
    }

    /**
     * Checks if this player is allied to the specified player.
     *
     * @param player The player to check alliance with.
     *               Returns true if this player is allied with player, false if this player is not allied with player.
     *               <p>
     *               This function will also return false if this player is neutral or an observer, or
     *               if player is neutral or an observer.
     * @see #isEnemy
     */
    public boolean isAlly(final Player player) {
        if (player == null || isNeutral() || player.isNeutral() || isObserver() || player.isObserver()) {
            return false;
        }
        return playerData.isAlly(player.getID());
    }

    /**
     * Checks if this player is unallied to the specified player.
     *
     * @param player The player to check alliance with.
     * @return true if this player is allied with player, false if this player is not allied with player .
     * <p>
     * This function will also return false if this player is neutral or an observer, or if
     * player is neutral or an observer.
     * @see #isAlly
     */
    public boolean isEnemy(final Player player) {
        if (player == null || isNeutral() || player.isNeutral() || isObserver() || player.isObserver()) {
            return false;
        }
        return !playerData.isAlly(player.getID());
    }

    /**
     * Checks if this player is the neutral player.
     *
     * @return true if this player is the neutral player, false if this player is any other player.
     */
    public boolean isNeutral() {
        return equals(game.neutral());
    }

    /**
     * Retrieve's the player's starting location.
     *
     * @return A {@link TilePosition} containing the position of the start location.
     * Returns {@link TilePosition#None} if the player does not have a start location.
     * Returns {@link TilePosition#Unknown} if an error occured while trying to retrieve the start
     * location.
     * @see Game#getStartLocations
     */
    public TilePosition getStartLocation() {
        return startLocation;
    }

    /**
     * Checks if the player has achieved victory.
     *
     * @return true if this player has achieved victory, otherwise false
     */
    public boolean isVictorious() {
        return playerData.isVictorious();
    }

    /**
     * Checks if the player has been defeated.
     *
     * @return true if the player is defeated, otherwise false
     */
    public boolean isDefeated() {
        return playerData.isDefeated();
    }

    /**
     * Checks if the player has left the game.
     *
     * @return true if the player has left the game, otherwise false
     */
    public boolean leftGame() {
        return playerData.getLeftGame();
    }

    /**
     * Retrieves the current amount of minerals/ore that this player has.
     * <p>
     * This function will return 0 if the player is inaccessible.
     *
     * @return Amount of minerals that the player currently has for spending.
     */
    public int minerals() {
        return playerData.getMinerals();
    }

    /**
     * Retrieves the current amount of vespene gas that this player has.
     * <p>
     * This function will return 0 if the player is inaccessible.
     *
     * @return Amount of gas that the player currently has for spending.
     */
    public int gas() {
        return playerData.getGas();
    }

    /**
     * Retrieves the cumulative amount of minerals/ore that this player has gathered
     * since the beginning of the game, including the amount that the player starts the game
     * with (if any).
     * <p>
     * This function will return 0 if the player is inaccessible.
     *
     * @return Cumulative amount of minerals that the player has gathered.
     */
    public int gatheredMinerals() {
        return playerData.getGatheredMinerals();
    }

    /**
     * Retrieves the cumulative amount of vespene gas that this player has gathered since
     * the beginning of the game, including the amount that the player starts the game with (if
     * any).
     * <p>
     * This function will return 0 if the player is inaccessible.
     *
     * @return Cumulative amount of gas that the player has gathered.
     */
    public int gatheredGas() {
        return playerData.getGatheredGas();
    }

    /**
     * Retrieves the cumulative amount of minerals/ore that this player has spent on
     * repairing units since the beginning of the game. This function only applies to @Terran players.
     * <p>
     * This function will return 0 if the player is inaccessible.
     *
     * @return Cumulative amount of minerals that the player has spent repairing.
     */
    public int repairedMinerals() {
        return playerData.getRepairedMinerals();
    }

    /**
     * Retrieves the cumulative amount of vespene gas that this player has spent on
     * repairing units since the beginning of the game. This function only applies to @Terran players.
     * <p>
     * This function will return 0 if the player is inaccessible.
     *
     * @return Cumulative amount of gas that the player has spent repairing.
     */
    public int repairedGas() {
        return playerData.getRepairedGas();
    }

    /**
     * Retrieves the cumulative amount of minerals/ore that this player has gained from
     * refunding (cancelling) units and structures.
     * <p>
     * This function will return 0 if the player is inaccessible.
     *
     * @return Cumulative amount of minerals that the player has received from refunds.
     */
    public int refundedMinerals() {
        return playerData.getRefundedMinerals();
    }

    /**
     * Retrieves the cumulative amount of vespene gas that this player has gained from
     * refunding (cancelling) units and structures.
     * <p>
     * This function will return 0 if the player is inaccessible.
     *
     * @return Cumulative amount of gas that the player has received from refunds.
     */
    public int refundedGas() {
        return playerData.getRefundedGas();
    }

    /**
     * Retrieves the cumulative amount of minerals/ore that this player has spent,
     * excluding repairs.
     * <p>
     * This function will return 0 if the player is inaccessible.
     *
     * @return Cumulative amount of minerals that the player has spent.
     */
    public int spentMinerals() {
        return gatheredMinerals() + refundedMinerals() - minerals() - repairedMinerals();
    }

    /**
     * Retrieves the cumulative amount of vespene gas that this player has spent,
     * excluding repairs.
     * <p>
     * This function will return 0 if the player is inaccessible.
     *
     * @return Cumulative amount of gas that the player has spent.
     */
    public int spentGas() {
        return gatheredGas() + refundedGas() - gas() - repairedGas();
    }

    public int supplyTotal() {
        return supplyTotal(getRace());
    }

    /**
     * Retrieves the total amount of supply the player has available for unit control.
     * <p>
     * In Starcraft programming, the managed supply values are double than what they appear
     * in the game. The reason for this is because @Zerglings use 0.5 visible supply.
     * <p>
     * In Starcraft, the supply for each race is separate. Having a @Pylon and an @Overlord
     * will not give you 32 supply. It will instead give you 16 @Protoss supply and 16 @Zerg
     * supply.
     *
     * @param race The race to query the total supply for. If this is omitted, then the player's current race will be used.
     * @return The total supply available for this player and the given race.
     * @see #supplyUsed
     */
    public int supplyTotal(final Race race) {
        return playerData.getSupplyTotal(race.id);
    }

    public int supplyUsed() {
        return supplyUsed(getRace());
    }

    /**
     * Retrieves the current amount of supply that the player is using for unit control.
     *
     * @param race The race to query the used supply for. If this is omitted, then the player's current race will be used.
     * @return The supply that is in use for this player and the given race.
     * @see #supplyTotal
     */
    public int supplyUsed(final Race race) {
        return playerData.getSupplyUsed(race.id);
    }

    public int allUnitCount() {
        return allUnitCount(UnitType.AllUnits);
    }

    /**
     * Retrieves the total number of units that the player has. If the
     * information about the player is limited, then this function will only return the number
     * of visible units.
     * <p>
     * While in-progress @Protoss and @Terran units will be counted, in-progress @Zerg units
     * (i.e. inside of an egg) do not.
     *
     * @param unit The unit type to query. UnitType macros are accepted. If this parameter is omitted, then it will use UnitType.AllUnits by default.
     * @return The total number of units of the given type that the player owns.
     * @see #visibleUnitCount
     * @see #completedUnitCount
     * @see #incompleteUnitCount
     */
    public int allUnitCount(final UnitType unit) {
        return playerData.getAllUnitCount(unit.id);
    }

    public int visibleUnitCount() {
        return visibleUnitCount(UnitType.AllUnits);
    }

    /**
     * Retrieves the total number of strictly visible units that the player has, even if
     * information on the player is unrestricted.
     *
     * @param unit The unit type to query. UnitType macros are accepted. If this parameter is omitted, then it will use UnitType.AllUnits by default.
     * @return The total number of units of the given type that the player owns, and is visible
     * to the BWAPI player.
     * @see #allUnitCount
     * @see #completedUnitCount
     * @see #incompleteUnitCount
     */
    public int visibleUnitCount(final UnitType unit) {
        return playerData.getVisibleUnitCount(unit.id);
    }

    public int completedUnitCount() {
        return completedUnitCount(UnitType.AllUnits);
    }

    /**
     * Retrieves the number of completed units that the player has. If the
     * information about the player is limited, then this function will only return the number of
     * visible completed units.
     *
     * @param unit The unit type to query. UnitType macros are accepted. If this parameter is omitted, then it will use UnitType.AllUnits by default.
     * @return The number of completed units of the given type that the player owns.
     * @see #allUnitCount
     * @see #visibleUnitCount
     * @see #incompleteUnitCount
     */
    public int completedUnitCount(final UnitType unit) {
        return playerData.getCompletedUnitCount(unit.id);
    }

    public int incompleteUnitCount() {
        return allUnitCount() - completedUnitCount();
    }

    /**
     * Retrieves the number of incomplete units that the player has. If the
     * information about the player is limited, then this function will only return the number of
     * visible incomplete units.
     * <p>
     * This function is a macro for allUnitCount() - completedUnitCount().
     * <p>
     * Incomplete @Zerg units inside of eggs are not counted.
     *
     * @param unit The unit type to query. UnitType macros are accepted. If this parameter is omitted, then it will use UnitType.AllUnits by default.
     * @return The number of incomplete units of the given type that the player owns.
     * @see #allUnitCount
     * @see #visibleUnitCount
     * @see #completedUnitCount
     */
    public int incompleteUnitCount(final UnitType unit) {
        return allUnitCount(unit) - completedUnitCount(unit);
    }

    public int deadUnitCount() {
        return deadUnitCount(UnitType.AllUnits);
    }

    /**
     * Retrieves the number units that have died for this player.
     *
     * @param unit The unit type to query. {@link UnitType} macros are accepted. If this parameter is omitted, then it will use {@link UnitType#AllUnits} by default.
     * @return The total number of units that have died throughout the game.
     */
    public int deadUnitCount(final UnitType unit) {
        return playerData.getDeadUnitCount(unit.id);
    }

    public int killedUnitCount() {
        return killedUnitCount(UnitType.AllUnits);
    }

    /**
     * Retrieves the number units that the player has killed.
     *
     * @param unit The unit type to query. UnitType macros are accepted. If this parameter is omitted, then it will use {@link UnitType#AllUnits} by default.
     * @return The total number of units that the player has killed throughout the game.
     */
    public int killedUnitCount(final UnitType unit) {
        return playerData.getKilledUnitCount(unit.id);
    }

    /**
     * Retrieves the current upgrade level that the player has attained for a given
     * upgrade type.
     *
     * @param upgrade The UpgradeType to query.
     * @return The number of levels that the upgrade has been upgraded for this player.
     * @see Unit#upgrade
     * @see #getMaxUpgradeLevel
     */
    public int getUpgradeLevel(final UpgradeType upgrade) {
        return playerData.getUpgradeLevel(upgrade.id);
    }

    /**
     * Checks if the player has already researched a given technology.
     *
     * @param tech The {@link TechType} to query.
     * @return true if the player has obtained the given tech, or false if they have not
     * @see #isResearching
     * @see Unit#research
     * @see #isResearchAvailable
     */
    public boolean hasResearched(final TechType tech) {
        return playerData.getHasResearched(tech.id);
    }

    /**
     * Checks if the player is researching a given technology type.
     *
     * @param tech The {@link TechType} to query.
     * @return true if the player is currently researching the tech, or false otherwise
     * @see Unit#research
     * @see #hasResearched
     */
    public boolean isResearching(final TechType tech) {
        return playerData.isResearching(tech.id);
    }

    /**
     * Checks if the player is upgrading a given upgrade type.
     *
     * @param upgrade The upgrade type to query.
     * @return true if the player is currently upgrading the given upgrade, false otherwise
     * @see Unit#upgrade
     */
    public boolean isUpgrading(final UpgradeType upgrade) {
        return playerData.isUpgrading(upgrade.id);
    }

    /**
     * Retrieves the color value of the current player.
     *
     * @return {@link Color} object that represents the color of the current player.
     */
    public Color getColor() {
        return new Color(playerData.getColor());
    }

    /**
     * Retrieves the control code character that changes the color of text messages to
     * represent this player.
     *
     * @return character code to use for text in Broodwar.
     */
    public TextColor getTextColor() {
        switch (playerData.getColor()) {
            case 111: // red
                return TextColor.BrightRed;
            case 165: // blue
                return TextColor.Blue;
            case 159: // teal
                return TextColor.Teal;
            case 164: // purp
                return TextColor.Purple;
            case 156: // orange with fix from @n00byEdge
                return TextColor.Orange;
            case 19:  // brown
                return TextColor.Brown;
            case 84:  // white
                return TextColor.PlayerWhite;
            case 135: // yellow
                return TextColor.PlayerYellow;
            case 185: // green p9
                return TextColor.DarkGreen;
            case 136: // p10
                return TextColor.LightYellow;
            case 134: // p11
                return TextColor.Tan;
            case 51:  // p12
                return TextColor.GreyBlue;
            default:
                return TextColor.Default;
        }
    }

    /**
     * Retrieves the maximum amount of energy that a unit type will have, taking the
     * player's energy upgrades into consideration.
     *
     * @param unit The {@link UnitType} to retrieve the maximum energy for.
     * @return Maximum amount of energy that the given unit type can have.
     */
    public int maxEnergy(final UnitType unit) {
        int energy = unit.maxEnergy();
        if (unit == Protoss_Arbiter && getUpgradeLevel(Khaydarin_Core) > 0 ||
                unit == Protoss_Corsair && getUpgradeLevel(Argus_Jewel) > 0 ||
                unit == Protoss_Dark_Archon && getUpgradeLevel(Argus_Talisman) > 0 ||
                unit == Protoss_High_Templar && getUpgradeLevel(Khaydarin_Amulet) > 0 ||
                unit == Terran_Ghost && getUpgradeLevel(Moebius_Reactor) > 0 ||
                unit == Terran_Battlecruiser && getUpgradeLevel(Colossus_Reactor) > 0 ||
                unit == Terran_Science_Vessel && getUpgradeLevel(Titan_Reactor) > 0 ||
                unit == Terran_Wraith && getUpgradeLevel(Apollo_Reactor) > 0 ||
                unit == Terran_Medic && getUpgradeLevel(Caduceus_Reactor) > 0 ||
                unit == Zerg_Defiler && getUpgradeLevel(Metasynaptic_Node) > 0 ||
                unit == Zerg_Queen && getUpgradeLevel(Gamete_Meiosis) > 0) {
            energy += 50;
        }
        return energy;
    }

    /**
     * Retrieves the top speed of a unit type, taking the player's speed upgrades into
     * consideration.
     *
     * @param unit The {@link UnitType} to retrieve the top speed for.
     * @return Top speed of the provided unit type for this player.
     */
    public double topSpeed(final UnitType unit) {
        double speed = unit.topSpeed();
        if (unit == Terran_Vulture && getUpgradeLevel(Ion_Thrusters) > 0 ||
                unit == Zerg_Overlord && getUpgradeLevel(Pneumatized_Carapace) > 0 ||
                unit == Zerg_Zergling && getUpgradeLevel(Metabolic_Boost) > 0 ||
                unit == Zerg_Hydralisk && getUpgradeLevel(Muscular_Augments) > 0 ||
                unit == Protoss_Zealot && getUpgradeLevel(Leg_Enhancements) > 0 ||
                unit == Protoss_Shuttle && getUpgradeLevel(Gravitic_Drive) > 0 ||
                unit == Protoss_Observer && getUpgradeLevel(Gravitic_Boosters) > 0 ||
                unit == Protoss_Scout && getUpgradeLevel(Gravitic_Thrusters) > 0 ||
                unit == Zerg_Ultralisk && getUpgradeLevel(Anabolic_Synthesis) > 0) {
            if (unit == Protoss_Scout) {
                speed += 427 / 256.0;
            } else {
                speed = speed * 1.5;
            }
            if (speed < 853 / 256.0) {
                speed = 853 / 256.0;
            }
            //acceleration *= 2;
            //turnRadius *= 2;
        }
        return speed;
    }

    /**
     * Retrieves the maximum weapon range of a weapon type, taking the player's weapon
     * upgrades into consideration.
     *
     * @param weapon The {@link WeaponType} to retrieve the maximum range for.
     * @return Maximum range of the given weapon type for units owned by this player.
     */
    public int weaponMaxRange(final WeaponType weapon) {
        int range = weapon.maxRange();
        if (weapon == Gauss_Rifle && getUpgradeLevel(U_238_Shells) > 0 ||
                weapon == Needle_Spines && getUpgradeLevel(Grooved_Spines) > 0) {
            range += 1 * 32;
        } else if (weapon == Phase_Disruptor && getUpgradeLevel(Singularity_Charge) > 0) {
            range += 2 * 32;
        } else if (weapon == Hellfire_Missile_Pack && getUpgradeLevel(Charon_Boosters) > 0) {
            range += 3 * 32;
        }
        return range;
    }

    /**
     * Retrieves the sight range of a unit type, taking the player's sight range
     * upgrades into consideration.
     *
     * @param unit The {@link UnitType} to retrieve the sight range for.
     * @return Sight range of the provided unit type for this player.
     */
    public int sightRange(final UnitType unit) {
        int range = unit.sightRange();
        if (unit == Terran_Ghost && getUpgradeLevel(Ocular_Implants) > 0 ||
                unit == Zerg_Overlord && getUpgradeLevel(Antennae) > 0 ||
                unit == Protoss_Observer && getUpgradeLevel(Sensor_Array) > 0 ||
                unit == Protoss_Scout && getUpgradeLevel(Apial_Sensors) > 0) {
            range = 11 * 32;
        }
        return range;
    }

    /**
     * Retrieves the weapon cooldown of a unit type, taking the player's attack speed
     * upgrades into consideration.
     *
     * @param unit The {@link UnitType} to retrieve the damage cooldown for.
     * @return Weapon cooldown of the provided unit type for this player.
     */
    public int weaponDamageCooldown(final UnitType unit) {
        int cooldown = unit.groundWeapon().damageCooldown();
        if (unit == Zerg_Zergling && getUpgradeLevel(Adrenal_Glands) > 0) {
            // Divide cooldown by 2
            cooldown /= 2;
            // Prevent cooldown from going out of bounds
            cooldown = Math.min(Math.max(cooldown, 5), 250);
        }
        return cooldown;
    }

    /**
     * Calculates the armor that a given unit type will have, including upgrades.
     *
     * @param unit The unit type to calculate armor for, using the current player's upgrades.
     * @return The amount of armor that the unit will have with the player's upgrades.
     */
    public int armor(final UnitType unit) {
        int armor = unit.armor();
        armor += getUpgradeLevel(unit.armorUpgrade());
        if ((unit == Zerg_Ultralisk && getUpgradeLevel(Chitinous_Plating) > 0) || unit == Hero_Torrasque) {
            armor += 2;
        }
        return armor;
    }

    /**
     * Calculates the damage that a given weapon type can deal, including upgrades.
     *
     * @param wpn The weapon type to calculate for.
     * @return The amount of damage that the weapon deals with this player's upgrades.
     */
    public int damage(final WeaponType wpn) {
        int dmg = wpn.damageAmount();
        dmg += getUpgradeLevel(wpn.upgradeType()) * wpn.damageBonus();
        dmg *= wpn.damageFactor();
        return dmg;
    }

    /**
     * Retrieves the total unit score, as seen in the end-game score screen.
     *
     * @return The player's unit score.
     */
    public int getUnitScore() {
        return playerData.getTotalUnitScore();
    }

    /**
     * Retrieves the total kill score, as seen in the end-game score screen.
     *
     * @return The player's kill score.
     */
    public int getKillScore() {
        return playerData.getTotalKillScore();
    }

    /**
     * Retrieves the total building score, as seen in the end-game score screen.
     *
     * @return The player's building score.
     */
    public int getBuildingScore() {
        return playerData.getTotalBuildingScore();
    }

    /**
     * Retrieves the total razing score, as seen in the end-game score screen.
     *
     * @return The player's razing score.
     */
    public int getRazingScore() {
        return playerData.getTotalRazingScore();
    }

    /**
     * Retrieves the player's custom score. This score is used in @UMS game
     * types.
     *
     * @return The player's custom score.
     */
    public int getCustomScore() {
        return playerData.getCustomScore();
    }

    /**
     * Checks if the player is an observer player, typically in a @UMS observer
     * game. An observer player does not participate in the game.
     *
     * @return true if the player is observing, or false if the player is capable of playing in
     * the game.
     */
    public boolean isObserver() {
        return !playerData.isParticipating();
    }

    /**
     * Retrieves the maximum upgrades available specific to the player. This
     * value is only different from UpgradeType#maxRepeats in @UMS games.
     *
     * @param upgrade The {@link UpgradeType} to retrieve the maximum upgrade level for.
     * @return Maximum upgrade level of the given upgrade type.
     */
    public int getMaxUpgradeLevel(final UpgradeType upgrade) {
        return playerData.getMaxUpgradeLevel(upgrade.id);
    }

    /**
     * Checks if a technology can be researched by the player. Certain
     * technologies may be disabled in @UMS game types.
     *
     * @param tech The {@link TechType} to query.
     * @return true if the tech type is available to the player for research.
     */
    public boolean isResearchAvailable(final TechType tech) {
        return playerData.isResearchAvailable(tech.id);
    }

    /**
     * Checks if a unit type can be created by the player. Certain unit types
     * may be disabled in @UMS game types.
     *
     * @param unit The {@link UnitType} to check.
     * @return true if the unit type is available to the player.
     */
    public boolean isUnitAvailable(final UnitType unit) {
        return playerData.isUnitAvailable(unit.id);
    }

    public boolean hasUnitTypeRequirement(final UnitType unit) {
        return hasUnitTypeRequirement(unit, 1);
    }

    /**
     * Verifies that this player satisfies a unit type requirement.
     * This verifies complex type requirements involving morphable @Zerg structures. For example,
     * if something requires a @Spire, but the player has (or is in the process of morphing) a @Greater_Spire,
     * this function will identify the requirement. It is simply a convenience function
     * that performs all of the requirement checks.
     *
     * @param unit   The UnitType to check.
     * @param amount The amount of units that are required.
     * @return true if the unit type requirements are met, and false otherwise.
     * @since 4.1.2
     */
    public boolean hasUnitTypeRequirement(final UnitType unit, final int amount) {
        if (unit == UnitType.None) {
            return true;
        }

        switch (unit) {
            case Zerg_Hatchery:
                return completedUnitCount(Zerg_Hatchery) + allUnitCount(Zerg_Lair) + allUnitCount(Zerg_Hive) >= amount;
            case Zerg_Lair:
                return completedUnitCount(Zerg_Lair) + allUnitCount(Zerg_Hive) >= amount;
            case Zerg_Spire:
                return completedUnitCount(Zerg_Spire) + allUnitCount(Zerg_Greater_Spire) >= amount;
            default:
                return completedUnitCount(unit) >= amount;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return id == player.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(final Player other) {
        return id - other.id;
    }
}
