package bwapi.types;

import java.util.*;
import java.util.stream.Collectors;

import static bwapi.types.Order.*;
import static bwapi.types.Race.*;
import static bwapi.types.UnitType.*;


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

    public static TechType[] techTypes = new TechType[46+1];
    static {
        Arrays.stream(TechType.values()).forEach(v -> techTypes[v.id] = v);
    }

    public final int id;

    TechType(int id) {
        this.id = id;
    }

    public Race getRace() {
        return techRaces[id];
    }

    public int mineralPrice() {
        return defaultOreCost[id];
    }

    public int gasPrice() {
        return mineralPrice();
    }

    public int researchTime() {
        return defaultTimeCost[id];
    }

    public int energyCost() {
        return defaultEnergyCost[id];
    }

    public UnitType whatResearches() {
        return whatResearches[id];
    }

    public WeaponType getWeapon() {
        return techWeapons[id];
    }

    public boolean targetsUnit() {
        return (techTypeFlags[id] & TARG_UNIT) != 0;
    }

    public boolean targetsPosition() {
        return (techTypeFlags[id] & TARG_POS) != 0;
    }

    public Set<UnitType> whatsUses() {
        return Arrays.stream(techWhatUses[id]).collect(Collectors.toSet());
    }

    public Order getOrder() {
        return techOrders[id];
    }

    public UnitType requiredUnit() {
        return this == Lurker_Aspect ? Zerg_Lair : UnitType.None;
    }

    /// IMPLEMENTATION
    private static int defaultOreCost[] =         // Same as default gas cost
    { 100, 200, 200, 100, 0, 150, 0, 200, 100, 150, 100, 100, 0, 100, 0, 200, 100, 100, 0, 200, 150, 150, 150, 0, 100, 200, 0, 200, 0, 100, 100, 100, 200 };
    private static int defaultTimeCost[] =
    { 1200, 1500, 1800, 1200, 0, 1200, 0, 1200, 1800, 1500, 1200, 1200, 0, 1200, 0, 1500, 1500, 1200, 0, 1800, 1200, 1800, 1500, 0, 1200, 1200, 0, 1800, 0, 1800, 1800, 1500, 1800 };
    private static int defaultEnergyCost[] =
    { 0, 100, 100, 0, 50, 0, 100, 75, 150, 25, 25, 0, 0, 150, 100, 150, 0, 75, 75, 75, 100, 150, 100, 0, 50, 125, 0, 150, 0, 50, 75, 100, 0, 0, 1 };

    private static UnitType whatResearches[] = {
            Terran_Academy, Terran_Covert_Ops, Terran_Science_Facility, Terran_Machine_Shop,
            UnitType.None, Terran_Machine_Shop, UnitType.None, Terran_Science_Facility, Terran_Physics_Lab,
            Terran_Control_Tower, Terran_Covert_Ops, Zerg_Hatchery, UnitType.None, Zerg_Queens_Nest,
            UnitType.None, Zerg_Defiler_Mound, Zerg_Defiler_Mound, Zerg_Queens_Nest, UnitType.None,
            Protoss_Templar_Archives, Protoss_Templar_Archives, Protoss_Arbiter_Tribunal,
            Protoss_Arbiter_Tribunal, UnitType.None, Terran_Academy, Protoss_Fleet_Beacon, UnitType.None,
            Protoss_Templar_Archives, UnitType.None, UnitType.None, Terran_Academy, Protoss_Templar_Archives,
            Zerg_Hydralisk_Den, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None,
            UnitType.None, UnitType.None, UnitType.Unknown
    };

    private static Race techRaces[] = {
            Terran, Terran, Terran, Terran, Terran, Terran, Terran, Terran, Terran, Terran, Terran,
            Zerg, Zerg, Zerg, Zerg, Zerg, Zerg, Zerg, Zerg,
            Protoss, Protoss, Protoss, Protoss, Protoss,
            Terran, Protoss, Race.None, Protoss, Protoss, Protoss, Terran, Protoss, Zerg, Race.None, Terran,
            Race.None, Race.None, Race.None, Race.None, Race.None, Race.None, Race.None, Race.None, Race.None, Race.None, Terran, Race.Unknown
    };

    private static WeaponType techWeapons[] = {
            WeaponType.None, WeaponType.Lockdown, WeaponType.EMP_Shockwave, WeaponType.Spider_Mines, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.Irradiate, WeaponType.Yamato_Gun,
            WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.Spawn_Broodlings, WeaponType.Dark_Swarm, WeaponType.Plague, WeaponType.Consume, WeaponType.Ensnare, WeaponType.Parasite,
            WeaponType.Psionic_Storm, WeaponType.None, WeaponType.None, WeaponType.Stasis_Field, WeaponType.None, WeaponType.Restoration, WeaponType.Disruption_Web, WeaponType.None, WeaponType.Mind_Control,
            WeaponType.None, WeaponType.Feedback, WeaponType.Optical_Flare, WeaponType.Maelstrom, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None,
            WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.None, WeaponType.Nuclear_Strike, WeaponType.Unknown
    };

    private static int TARG_UNIT = 1;
    private static int TARG_POS =  2;
    private static int TARG_BOTH = 3;

    private static int techTypeFlags[] = {
            0, TARG_UNIT, TARG_BOTH, TARG_POS, TARG_BOTH, 0, TARG_UNIT, TARG_UNIT, TARG_UNIT, 0, 0, 0,
            TARG_UNIT, TARG_UNIT, TARG_BOTH, TARG_BOTH, TARG_UNIT, TARG_BOTH, TARG_UNIT, TARG_BOTH, TARG_UNIT,
            TARG_BOTH, TARG_BOTH, TARG_UNIT, TARG_UNIT, TARG_BOTH, 0, TARG_UNIT, TARG_UNIT, TARG_UNIT, TARG_UNIT,
            TARG_BOTH, 0, 0, TARG_BOTH, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, TARG_BOTH
    };

    private static Order techOrders[] = {
            Order.None, CastLockdown, CastEMPShockwave, PlaceMine, CastScannerSweep, Order.None, CastDefensiveMatrix,
            CastIrradiate, FireYamatoGun, Order.None, Order.None, Order.None, CastInfestation, CastSpawnBroodlings,
            CastDarkSwarm, CastPlague, CastConsume, CastEnsnare, CastParasite, CastPsionicStorm,
            CastHallucination, CastRecall, CastStasisField, Order.None, CastRestoration, CastDisruptionWeb,
            Order.None, CastMindControl, Order.None, CastFeedback, CastOpticalFlare, CastMaelstrom, Order.None, Order.None, MedicHeal,
            Order.None, Order.None, Order.None, Order.None, Order.None, Order.None, Order.None, Order.None, Order.None, Order.None, NukePaint, Order.Unknown
    };

    private static UnitType techWhatUses[][] = {
        // Stimpacks
        {Terran_Marine, Terran_Firebat, Hero_Jim_Raynor_Marine, Hero_Gui_Montag},
        // Lockdown
        { Terran_Ghost, Hero_Alexei_Stukov, Hero_Infested_Duran, Hero_Samir_Duran, Hero_Sarah_Kerrigan },
        // EMP
        { Terran_Science_Vessel, Hero_Magellan },
        // Spider Mine
        { Terran_Vulture, Hero_Jim_Raynor_Vulture },
        // Scanner Sweep
        { Terran_Comsat_Station },
        // Siege Mode
        { Terran_Siege_Tank_Tank_Mode, Terran_Siege_Tank_Siege_Mode, Hero_Edmund_Duke_Tank_Mode, Hero_Edmund_Duke_Siege_Mode },
        // Defensive Matrix
        { Terran_Science_Vessel, Hero_Magellan },
        // Irradiate
        { Terran_Science_Vessel, Hero_Magellan },
        // Yamato Cannon
        { Terran_Battlecruiser, Hero_Gerard_DuGalle, Hero_Hyperion, Hero_Norad_II },
        // Cloaking Field
        { Terran_Wraith, Hero_Tom_Kazansky },
        // Personnel Cloaking
        { Terran_Ghost, Hero_Alexei_Stukov, Hero_Infested_Duran, Hero_Samir_Duran, Hero_Sarah_Kerrigan, Hero_Infested_Kerrigan },
        // Burrow
        { Zerg_Zergling, Zerg_Hydralisk, Zerg_Drone, Zerg_Defiler, Zerg_Infested_Terran, Hero_Unclean_One, Hero_Hunter_Killer, Hero_Devouring_One, Zerg_Lurker },
        // Infestation
        { Zerg_Queen, Hero_Matriarch },
        // Spawn Broodlings
        { Zerg_Queen, Hero_Matriarch },
        // Dark Swarm
        { Zerg_Defiler, Hero_Unclean_One },
        // Plague
        { Zerg_Defiler, Hero_Unclean_One },
        // Consume
        { Zerg_Defiler, Hero_Unclean_One, Hero_Infested_Kerrigan, Hero_Infested_Duran },
        // Ensnare
        { Zerg_Queen, Hero_Matriarch, Hero_Infested_Kerrigan },
        // Parasite
        { Zerg_Queen, Hero_Matriarch },
        // Psi Storm
        { Protoss_High_Templar, Hero_Tassadar, Hero_Infested_Kerrigan },
        // Hallucination
        { Protoss_High_Templar, Hero_Tassadar },
        // Recall
        { Protoss_Arbiter, Hero_Danimoth },
        // Stasis Field
        { Protoss_Arbiter, Hero_Danimoth },
        // Archon Warp
        { Protoss_High_Templar },
        // Restoration
        { Terran_Medic },
        // Disruption Web
        { Protoss_Corsair, Hero_Raszagal },
        // Unused
        {},
        // Mind Control
        { Protoss_Dark_Archon },
        // Dark Archon Meld
        { Protoss_Dark_Templar },
        // Feedback
        { Protoss_Dark_Archon },
        // Optical Flare
        { Terran_Medic },
        // Maelstrom
        { Protoss_Dark_Archon },
        // Lurker Aspect
        { Zerg_Hydralisk },
        // Unused
        {},
        // Healing
        { Terran_Medic },
        // Unused
        {}, {}, {}, {}, {}, {}, {}, {}, {}, {},
        // Extra (Nuke)
        { Terran_Ghost },
        {}
    };
}
