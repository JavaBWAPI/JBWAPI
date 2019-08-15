package bwapi;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static bwapi.Order.*;


/**
 * Namespace containing tech types.
 * @see TechType
 */
public enum TechType {
    Stim_Packs(0),
    Lockdown(1),
    EMP_Shockwave(2),
    Spider_Mines(3),
    Scanner_Sweep(4),
    Tank_Siege_Mode(5),
    Defensive_Matrix(6),
    Irradiate(7),
    Yamato_Gun(8),
    Cloaking_Field(9),
    Personnel_Cloaking(10),
    Burrowing(11),
    Infestation(12),
    Spawn_Broodlings(13),
    Dark_Swarm(14),
    Plague(15),
    Consume(16),
    Ensnare(17),
    Parasite(18),
    Psionic_Storm(19),
    Hallucination(20),
    Recall(21),
    Stasis_Field(22),
    Archon_Warp(23),
    Restoration(24),
    Disruption_Web(25),
    Unused_26(26),
    Mind_Control(27),
    Dark_Archon_Meld(28),
    Feedback(29),
    Optical_Flare(30),
    Maelstrom(31),
    Lurker_Aspect(32),
    Unused_33(33),
    Healing(34),

    None(44),
    Nuclear_Strike(45),
    Unknown(46);

    /// IMPLEMENTATION
    private static final int[] defaultOreCost =         // Same as default gas cost
            {100, 200, 200, 100, 0, 150, 0, 200, 100, 150, 100, 100, 0, 100, 0, 200, 100, 100, 0, 200, 150, 150, 150, 0,
                    100, 200, 0, 200, 0, 100, 100, 100, 200, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private static final int[] defaultTimeCost =
            {1200, 1500, 1800, 1200, 0, 1200, 0, 1200, 1800, 1500, 1200, 1200, 0, 1200, 0, 1500, 1500, 1200, 0, 1800,
                    1200, 1800, 1500, 0, 1200, 1200, 0, 1800, 0, 1800, 1800, 1500, 1800, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0};
    private static final int[] defaultEnergyCost =
            {0, 100, 100, 0, 50, 0, 100, 75, 150, 25, 25, 0, 0, 150, 100, 150, 0, 75, 75, 75, 100, 150, 100, 0, 50, 125,
                    0, 150, 0, 50, 75, 100, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    private static final UnitType[] whatResearches = {
            UnitType.Terran_Academy, UnitType.Terran_Covert_Ops, UnitType.Terran_Science_Facility, UnitType.Terran_Machine_Shop,
            UnitType.None, UnitType.Terran_Machine_Shop, UnitType.None, UnitType.Terran_Science_Facility, UnitType.Terran_Physics_Lab,
            UnitType.Terran_Control_Tower, UnitType.Terran_Covert_Ops, UnitType.Zerg_Hatchery, UnitType.None, UnitType.Zerg_Queens_Nest,
            UnitType.None, UnitType.Zerg_Defiler_Mound, UnitType.Zerg_Defiler_Mound, UnitType.Zerg_Queens_Nest, UnitType.None,
            UnitType.Protoss_Templar_Archives, UnitType.Protoss_Templar_Archives, UnitType.Protoss_Arbiter_Tribunal,
            UnitType.Protoss_Arbiter_Tribunal, UnitType.None, UnitType.Terran_Academy, UnitType.Protoss_Fleet_Beacon, UnitType.None,
            UnitType.Protoss_Templar_Archives, UnitType.None, UnitType.None, UnitType.Terran_Academy, UnitType.Protoss_Templar_Archives,
            UnitType.Zerg_Hydralisk_Den, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None,
            UnitType.None, UnitType.None, UnitType.Unknown
    };
    private static final Race[] techRaces = {
            Race.Terran, Race.Terran, Race.Terran, Race.Terran, Race.Terran, Race.Terran, Race.Terran, Race.Terran, Race.Terran, Race.Terran, Race.Terran,
            Race.Zerg, Race.Zerg, Race.Zerg, Race.Zerg, Race.Zerg, Race.Zerg, Race.Zerg, Race.Zerg,
            Race.Protoss, Race.Protoss, Race.Protoss, Race.Protoss, Race.Protoss,
            Race.Terran, Race.Protoss, Race.None, Race.Protoss, Race.Protoss, Race.Protoss, Race.Terran, Race.Protoss, Race.Zerg, Race.None, Race.Terran,
            Race.None, Race.None, Race.None, Race.None, Race.None, Race.None, Race.None, Race.None, Race.None, Race.None, Race.Terran, Race.Unknown
    };
    private static final WeaponType[] techWeapons = {
            WeaponType.None, WeaponType.Lockdown, WeaponType.EMP_Shockwave, WeaponType.Spider_Mines, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.Irradiate, WeaponType.Yamato_Gun,
            WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.Spawn_Broodlings, WeaponType.Dark_Swarm, WeaponType.Plague, WeaponType.Consume, WeaponType.Ensnare, WeaponType.Parasite,
            WeaponType.Psionic_Storm, WeaponType.None, WeaponType.None, WeaponType.Stasis_Field, WeaponType.None, WeaponType.Restoration, WeaponType.Disruption_Web, WeaponType.None, WeaponType.Mind_Control,
            WeaponType.None, WeaponType.Feedback, WeaponType.Optical_Flare, WeaponType.Maelstrom, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None,
            WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.Nuclear_Strike, WeaponType.Unknown
    };
    private static final int TARG_UNIT = 1;
    private static final int TARG_POS = 2;
    private static final int TARG_BOTH = 3;
    private static final int[] techTypeFlags = {
            0, TARG_UNIT, TARG_BOTH, TARG_POS, TARG_BOTH, 0, TARG_UNIT, TARG_UNIT, TARG_UNIT, 0, 0, 0,
            TARG_UNIT, TARG_UNIT, TARG_BOTH, TARG_BOTH, TARG_UNIT, TARG_BOTH, TARG_UNIT, TARG_BOTH, TARG_UNIT,
            TARG_BOTH, TARG_BOTH, TARG_UNIT, TARG_UNIT, TARG_BOTH, 0, TARG_UNIT, TARG_UNIT, TARG_UNIT, TARG_UNIT,
            TARG_BOTH, 0, 0, TARG_BOTH, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, TARG_BOTH, 0
    };
    private static final Order[] techOrders = {
            Order.None, CastLockdown, CastEMPShockwave, PlaceMine, CastScannerSweep, Order.None, CastDefensiveMatrix,
            CastIrradiate, FireYamatoGun, Order.None, Order.None, Order.None, CastInfestation, CastSpawnBroodlings,
            CastDarkSwarm, CastPlague, CastConsume, CastEnsnare, CastParasite, CastPsionicStorm,
            CastHallucination, CastRecall, CastStasisField, Order.None, CastRestoration, CastDisruptionWeb,
            Order.None, CastMindControl, Order.None, CastFeedback, CastOpticalFlare, CastMaelstrom, Order.None, Order.None, MedicHeal,
            Order.None, Order.None, Order.None, Order.None, Order.None, Order.None, Order.None, Order.None, Order.None, Order.None, NukePaint, Order.Unknown
    };
    private static final UnitType[][] techWhatUses = {
            // Stimpacks
            {UnitType.Terran_Marine, UnitType.Terran_Firebat, UnitType.Hero_Jim_Raynor_Marine, UnitType.Hero_Gui_Montag},
            // Lockdown
            {UnitType.Terran_Ghost, UnitType.Hero_Alexei_Stukov, UnitType.Hero_Infested_Duran, UnitType.Hero_Samir_Duran, UnitType.Hero_Sarah_Kerrigan},
            // EMP
            {UnitType.Terran_Science_Vessel, UnitType.Hero_Magellan},
            // Spider Mine
            {UnitType.Terran_Vulture, UnitType.Hero_Jim_Raynor_Vulture},
            // Scanner Sweep
            {UnitType.Terran_Comsat_Station},
            // Siege Mode
            {UnitType.Terran_Siege_Tank_Tank_Mode, UnitType.Terran_Siege_Tank_Siege_Mode, UnitType.Hero_Edmund_Duke_Tank_Mode, UnitType.Hero_Edmund_Duke_Siege_Mode},
            // Defensive Matrix
            {UnitType.Terran_Science_Vessel, UnitType.Hero_Magellan},
            // Irradiate
            {UnitType.Terran_Science_Vessel, UnitType.Hero_Magellan},
            // Yamato Cannon
            {UnitType.Terran_Battlecruiser, UnitType.Hero_Gerard_DuGalle, UnitType.Hero_Hyperion, UnitType.Hero_Norad_II},
            // Cloaking Field
            {UnitType.Terran_Wraith, UnitType.Hero_Tom_Kazansky},
            // Personnel Cloaking
            {UnitType.Terran_Ghost, UnitType.Hero_Alexei_Stukov, UnitType.Hero_Infested_Duran, UnitType.Hero_Samir_Duran, UnitType.Hero_Sarah_Kerrigan, UnitType.Hero_Infested_Kerrigan},
            // Burrow
            {UnitType.Zerg_Zergling, UnitType.Zerg_Hydralisk, UnitType.Zerg_Drone, UnitType.Zerg_Defiler, UnitType.Zerg_Infested_Terran, UnitType.Hero_Unclean_One, UnitType.Hero_Hunter_Killer, UnitType.Hero_Devouring_One, UnitType.Zerg_Lurker},
            // Infestation
            {UnitType.Zerg_Queen, UnitType.Hero_Matriarch},
            // Spawn Broodlings
            {UnitType.Zerg_Queen, UnitType.Hero_Matriarch},
            // Dark Swarm
            {UnitType.Zerg_Defiler, UnitType.Hero_Unclean_One},
            // Plague
            {UnitType.Zerg_Defiler, UnitType.Hero_Unclean_One},
            // Consume
            {UnitType.Zerg_Defiler, UnitType.Hero_Unclean_One, UnitType.Hero_Infested_Kerrigan, UnitType.Hero_Infested_Duran},
            // Ensnare
            {UnitType.Zerg_Queen, UnitType.Hero_Matriarch, UnitType.Hero_Infested_Kerrigan},
            // Parasite
            {UnitType.Zerg_Queen, UnitType.Hero_Matriarch},
            // Psi Storm
            {UnitType.Protoss_High_Templar, UnitType.Hero_Tassadar, UnitType.Hero_Infested_Kerrigan},
            // Hallucination
            {UnitType.Protoss_High_Templar, UnitType.Hero_Tassadar},
            // Recall
            {UnitType.Protoss_Arbiter, UnitType.Hero_Danimoth},
            // Stasis Field
            {UnitType.Protoss_Arbiter, UnitType.Hero_Danimoth},
            // Archon Warp
            {UnitType.Protoss_High_Templar},
            // Restoration
            {UnitType.Terran_Medic},
            // Disruption Web
            {UnitType.Protoss_Corsair, UnitType.Hero_Raszagal},
            // Unused
            {},
            // Mind Control
            {UnitType.Protoss_Dark_Archon},
            // Dark Archon Meld
            {UnitType.Protoss_Dark_Templar},
            // Feedback
            {UnitType.Protoss_Dark_Archon},
            // Optical Flare
            {UnitType.Terran_Medic},
            // Maelstrom
            {UnitType.Protoss_Dark_Archon},
            // Lurker Aspect
            {UnitType.Zerg_Hydralisk},
            // Unused
            {},
            // Healing
            {UnitType.Terran_Medic},
            // Unused
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {},
            // Extra (Nuke)
            {UnitType.Terran_Ghost},
            {}
    };

    static final TechType[] idToEnum = new TechType[46 + 1];

    static {
        Arrays.stream(TechType.values()).forEach(v -> idToEnum[v.id] = v);
    }

    final int id;

    TechType(final int id) {
        this.id = id;
    }

    /**
     * Retrieves the race that is required to research or use the TechType.
     *
     * @note There is an exception where @Infested_Kerrigan can use @Psi_Storm. This does not
     * apply to the behavior of this function.
     *
     * @return Race object indicating which race is designed to use this technology type.
     */
    public Race getRace() {
        return techRaces[id];
    }

    /**
     * Retrieves the mineral cost of researching this technology.
     *
     * @return Amount of minerals needed in order to research this technology.
     */
    public int mineralPrice() {
        return defaultOreCost[id];
    }

    /**
     * Retrieves the vespene gas cost of researching this technology.
     *
     * @return Amount of vespene gas needed in order to research this technology.
     */
    public int gasPrice() {
        return mineralPrice();
    }

    /**
     * Retrieves the number of frames needed to research the tech type.
     *
     * @return The time, in frames, it will take for the research to complete.
     * @see Unit#getRemainingResearchTime
     */
    public int researchTime() {
        return defaultTimeCost[id];
    }

    /**
     * Retrieves the amount of energy needed to use this TechType as an ability.
     *
     * @return Energy cost of the ability.
     * @see Unit#getEnergy
     */
    public int energyCost() {
        return defaultEnergyCost[id];
    }

    /**
     * Retrieves the UnitType that can research this technology.
     *
     * @return UnitType that is able to research the technology in the game.
     * @retval UnitType.None If the technology/ability is either provided for free or never
     * available.
     */
    public UnitType whatResearches() {
        return whatResearches[id];
    }

    /**
     * Retrieves the Weapon that is attached to this tech type.
     * A technology's WeaponType is used to indicate the range and behaviour of the ability
     * when used by a Unit.
     *
     * @return WeaponType containing information about the ability's behavior.
     * @retval WeaponType.None If there is no corresponding WeaponType.
     */
    public WeaponType getWeapon() {
        return techWeapons[id];
    }

    /**
     * Checks if this ability can be used on other units.
     *
     * @return true if the ability can be used on other units, and false if it can not.
     */
    public boolean targetsUnit() {
        return (techTypeFlags[id] & TARG_UNIT) != 0;
    }

    /**
     * Checks if this ability can be used on the terrain (ground).
     *
     * @return true if the ability can be used on the terrain.
     */
    public boolean targetsPosition() {
        return (techTypeFlags[id] & TARG_POS) != 0;
    }

    /**
     * Retrieves the set of all UnitTypes that are capable of using this ability.
     *
     * @return Set of UnitTypes that can use this ability when researched.
     */
    public List<UnitType> whatUses() {
        return Collections.unmodifiableList(Arrays.asList(techWhatUses[id]));
    }

    /**
     * Retrieves the Order that a Unit uses when using this ability.
     *
     * @return Order representing the action a Unit uses to perform this ability
     */
    public Order getOrder() {
        return techOrders[id];
    }

    /**
     * Retrieves the UnitType required to research this technology.
     * The required unit type must be a completed unit owned by the player researching the
     * technology.
     *
     * @return UnitType that is needed to research this tech type.
     * @retval UnitType.None if no unit is required to research this tech type.
     *
     * @see Player#completedUnitCount
     *
     * @since 4.1.2
     */
    public UnitType requiredUnit() {
        return this == Lurker_Aspect ? UnitType.Zerg_Lair : UnitType.None;
    }
}
