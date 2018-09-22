package bwapi.types;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static bwapi.types.Race.*;
import static bwapi.types.UnitType.*;

public enum UpgradeType {
    Terran_Infantry_Armor(0),
    Terran_Vehicle_Plating(1),
    Terran_Ship_Plating(2),
    Zerg_Carapace(3),
    Zerg_Flyer_Carapace(4),
    Protoss_Ground_Armor(5),
    Protoss_Air_Armor(6),
    Terran_Infantry_Weapons(7),
    Terran_Vehicle_Weapons(8),
    Terran_Ship_Weapons(9),
    Zerg_Melee_Attacks(10),
    Zerg_Missile_Attacks(11),
    Zerg_Flyer_Attacks(12),
    Protoss_Ground_Weapons(13),
    Protoss_Air_Weapons(14),
    Protoss_Plasma_Shields(15),
    U_238_Shells(16),

    Ion_Thrusters(17),
    Titan_Reactor(19),
    Ocular_Implants(20),
    Moebius_Reactor(21),
    Apollo_Reactor(22),
    Colossus_Reactor(23),
    Ventral_Sacs(24),
    Antennae(25),
    Pneumatized_Carapace(26),
    Metabolic_Boost(27),
    Adrenal_Glands(28),
    Muscular_Augments(29),
    Grooved_Spines(30),
    Gamete_Meiosis(31),
    Metasynaptic_Node(32),
    Singularity_Charge(33),
    Leg_Enhancements(34),
    Scarab_Damage(35),
    Reaver_Capacity(36),
    Gravitic_Drive(37),
    Sensor_Array(38),
    Gravitic_Boosters(39),
    Khaydarin_Amulet(40),
    Apial_Sensors(41),
    Gravitic_Thrusters(42),
    Carrier_Capacity(43),
    Khaydarin_Core(44),

    Argus_Jewel(47),

    Argus_Talisman(49),

    Caduceus_Reactor(51),
    Chitinous_Plating(52),
    Anabolic_Synthesis(53),
    Charon_Boosters(54),

    Upgrade_60(60),
    None(61),
    Unknown(62);

    public static UpgradeType[] upgradeTypes = new UpgradeType[62+1];
    static {
        Arrays.stream(UpgradeType.values()).forEach(v -> upgradeTypes[v.id] = v);
    }

    public final int id;

    UpgradeType(int id) {
        this.id = id;
    }

    public Race getRace() {
        return upgradeRaces[id];
    }

    public int mineralPrice() {
        return mineralPrice(1);
    }

    public int mineralPrice(int level) {
        return defaultOreCostBase[id] + Math.max(0, level-1) * mineralPriceFactor();
    }

    public int mineralPriceFactor() {
        return defaultOreCostFactor[id];
    }

    public int gasPrice() {
        return mineralPrice();
    }

    public int gasPrice(int level) {
        return mineralPrice(level);
    }

    public int gasPriceFactor() {
        return mineralPriceFactor();
    }

    public int upgradeTime() {
        return upgradeTime(1);
    }

    public int upgradeTime(int level) {
        return defaultTimeCostBase[id] + Math.max(0, level-1) * upgradeTimeFactor();
    }

    public int upgradeTimeFactor() {
        return defaultTimeCostFactor[id];
    }

    public UnitType whatUpgrades() {
        return whatUpgrades[id];
    }

    public Set<UnitType> whatUses() {
        return Arrays.stream(upgradeWhatUses[id]).collect(Collectors.toSet());
    }

    public int maxRepeats() {
        return defaultMaxRepeats[id];
    }

    public UnitType whatsRequired() {
        return whatsRequired(1);
    }

    public UnitType whatsRequired(int level) {
        return (level >= 1 && level <= 3) ? requirements[level-1][id] : UnitType.None;
    }

    // DEFAULTS
    private static int defaultOreCostBase[] = {   // same as default gas cost base
            100, 100, 150, 150, 150, 100, 150, 100, 100, 100, 100, 100, 100, 100, 100, 200, 150, 100, 200, 150, 100, 150, 200, 150, 200, 150, 150, 100, 200,
            150, 150, 150, 150, 150, 150, 200, 200, 200, 150, 150, 150, 100, 200, 100, 150, 0, 0, 100, 100, 150, 150, 150, 150, 200, 100, 0, 0, 0, 0, 0, 0, 0, 0
    };
    private static int defaultOreCostFactor[] =  {   // same as default gas cost factor
            75, 75, 75, 75, 75, 75, 75, 75, 75, 50, 50, 50, 75, 50, 75, 100, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };
    private static  int defaultTimeCostBase[] = {
            4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 1500, 1500, 0, 2500,
            2500, 2500, 2500, 2500, 2400, 2000, 2000, 1500, 1500, 1500, 1500, 2500, 2500, 2500, 2000, 2500, 2500, 2500, 2000,
            2000, 2500, 2500, 2500, 1500, 2500, 0, 0, 2500, 2500, 2500, 2500, 2500, 2000, 2000, 2000, 0, 0, 0, 0, 0, 0, 0, 0
    };
    private static  int defaultTimeCostFactor[] = {
            480, 480, 480, 480, 480, 480, 480, 480, 480, 480, 480, 480, 480, 480, 480, 480, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };
    private static int defaultMaxRepeats[] = {
            3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 0, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0
    };

    private static UnitType whatUpgrades[] = {
            Terran_Engineering_Bay, Terran_Armory, Terran_Armory, Zerg_Evolution_Chamber, Zerg_Spire, Protoss_Forge, Protoss_Cybernetics_Core, Terran_Engineering_Bay,
            Terran_Armory, Terran_Armory, Zerg_Evolution_Chamber, Zerg_Evolution_Chamber, Zerg_Spire, Protoss_Forge, Protoss_Cybernetics_Core, Protoss_Forge, Terran_Academy,
            Terran_Machine_Shop, UnitType.None, Terran_Science_Facility, Terran_Covert_Ops, Terran_Covert_Ops, Terran_Control_Tower, Terran_Physics_Lab, Zerg_Lair, Zerg_Lair, Zerg_Lair,
            Zerg_Spawning_Pool, Zerg_Spawning_Pool, Zerg_Hydralisk_Den, Zerg_Hydralisk_Den, Zerg_Queens_Nest, Zerg_Defiler_Mound, Protoss_Cybernetics_Core, Protoss_Citadel_of_Adun,
            Protoss_Robotics_Support_Bay, Protoss_Robotics_Support_Bay, Protoss_Robotics_Support_Bay, Protoss_Observatory, Protoss_Observatory, Protoss_Templar_Archives,
            Protoss_Fleet_Beacon, Protoss_Fleet_Beacon, Protoss_Fleet_Beacon, Protoss_Arbiter_Tribunal, UnitType.None, UnitType.None, Protoss_Fleet_Beacon, UnitType.None, Protoss_Templar_Archives,
            UnitType.None, Terran_Academy, Zerg_Ultralisk_Cavern, Zerg_Ultralisk_Cavern, Terran_Machine_Shop, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None
    };

    private static UnitType requirements[][] = {
        // Level 1
        {
            UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None,
            UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, Zerg_Hive, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None,
            UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, Terran_Armory, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None
        },
        // Level 2
        {
            Terran_Science_Facility, Terran_Science_Facility, Terran_Science_Facility, Zerg_Lair, Zerg_Lair, Protoss_Templar_Archives, Protoss_Fleet_Beacon,
            Terran_Science_Facility, Terran_Science_Facility, Terran_Science_Facility, Zerg_Lair, Zerg_Lair, Zerg_Lair, Protoss_Templar_Archives,
            Protoss_Fleet_Beacon, Protoss_Cybernetics_Core, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None,
            UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None,
            UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None
        },
        // Level 3
        {
            Terran_Science_Facility, Terran_Science_Facility, Terran_Science_Facility, Zerg_Hive, Zerg_Hive, Protoss_Templar_Archives, Protoss_Fleet_Beacon,
            Terran_Science_Facility, Terran_Science_Facility, Terran_Science_Facility, Zerg_Hive, Zerg_Hive, Zerg_Hive, Protoss_Templar_Archives,
            Protoss_Fleet_Beacon, Protoss_Cybernetics_Core, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None,
            UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None,
            UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None
        },
    };

    private static UnitType _Infantry_Armor[] = { Terran_Marine, Terran_Ghost, Terran_SCV, Hero_Gui_Montag, Terran_Civilian, Hero_Sarah_Kerrigan,
            Hero_Jim_Raynor_Marine, Terran_Firebat, Terran_Medic, Hero_Samir_Duran, Hero_Alexei_Stukov };
    private static UnitType _Vehicle_Plating[] = { Terran_Vulture, Terran_Goliath, Terran_Siege_Tank_Tank_Mode, Hero_Alan_Schezar, Hero_Jim_Raynor_Vulture,
            Hero_Edmund_Duke_Tank_Mode, Hero_Edmund_Duke_Siege_Mode, Terran_Siege_Tank_Siege_Mode };
    private static UnitType _Ship_Plating[] = { Terran_Wraith, Terran_Science_Vessel, Terran_Dropship, Terran_Battlecruiser, Hero_Tom_Kazansky, Hero_Magellan,
            Hero_Arcturus_Mengsk, Hero_Hyperion, Hero_Norad_II, Terran_Valkyrie, Hero_Gerard_DuGalle };
    private static UnitType _Carapace[] = { Zerg_Larva, Zerg_Egg, Zerg_Zergling, Zerg_Hydralisk, Zerg_Ultralisk, Zerg_Broodling, Zerg_Drone, Zerg_Defiler,
            Hero_Torrasque, Zerg_Infested_Terran, Hero_Infested_Kerrigan, Hero_Unclean_One, Hero_Hunter_Killer, Hero_Devouring_One,
            Zerg_Cocoon, Zerg_Lurker_Egg, Zerg_Lurker, Hero_Infested_Duran };
    private static UnitType _Flyer_Carapace[] = { Zerg_Overlord, Zerg_Mutalisk, Zerg_Guardian, Zerg_Queen, Zerg_Scourge, Hero_Matriarch, Hero_Kukulza_Mutalisk,
            Hero_Kukulza_Guardian, Hero_Yggdrasill, Zerg_Devourer };
    private static UnitType _Protoss_Armor[] = { Protoss_Dark_Templar, Protoss_Dark_Archon, Protoss_Probe, Protoss_Zealot, Protoss_Dragoon, Protoss_High_Templar,
            Protoss_Archon, Hero_Dark_Templar, Hero_Zeratul, Hero_Tassadar_Zeratul_Archon, Hero_Fenix_Zealot, Hero_Fenix_Dragoon,
            Hero_Tassadar, Hero_Warbringer, Protoss_Reaver, Hero_Aldaris };
    private static UnitType _Protoss_Plating[] = { Protoss_Corsair, Protoss_Shuttle, Protoss_Scout, Protoss_Arbiter, Protoss_Carrier, Protoss_Interceptor, Hero_Mojo,
            Hero_Gantrithor, Protoss_Observer, Hero_Danimoth, Hero_Artanis, Hero_Raszagal };
    private static UnitType _Infantry_Weapons[] = { Terran_Marine, Hero_Jim_Raynor_Marine, Terran_Ghost, Hero_Sarah_Kerrigan, Terran_Firebat, Hero_Gui_Montag,
            Special_Wall_Flame_Trap, Special_Right_Wall_Flame_Trap, Hero_Samir_Duran, Hero_Alexei_Stukov, Hero_Infested_Duran };
    private static UnitType _Vehicle_Weapons[] = { Terran_Vulture, Hero_Jim_Raynor_Vulture, Terran_Goliath, Hero_Alan_Schezar, Terran_Siege_Tank_Tank_Mode,
            Terran_Siege_Tank_Siege_Mode, Hero_Edmund_Duke_Tank_Mode, Hero_Edmund_Duke_Siege_Mode, Special_Floor_Missile_Trap,
            Special_Floor_Gun_Trap, Special_Wall_Missile_Trap, Special_Right_Wall_Missile_Trap };
    private static UnitType _Ship_Weapons[] = { Terran_Wraith, Hero_Tom_Kazansky, Terran_Battlecruiser, Hero_Hyperion, Hero_Norad_II, Hero_Arcturus_Mengsk,
            Hero_Gerard_DuGalle, Terran_Valkyrie };
    private static UnitType _Zerg_MeleeAtk[] = { Zerg_Zergling, Hero_Devouring_One, Hero_Infested_Kerrigan, Zerg_Ultralisk, Hero_Torrasque, Zerg_Broodling };
    private static UnitType _Zerg_RangeAtk[] = { Zerg_Hydralisk, Hero_Hunter_Killer, Zerg_Lurker };
    private static UnitType _Zerg_FlyerAtk[] = { Zerg_Mutalisk, Hero_Kukulza_Mutalisk, Hero_Kukulza_Guardian, Zerg_Guardian, Zerg_Devourer };
    private static UnitType _Protoss_GrndWpn[] = { Protoss_Zealot, Hero_Fenix_Zealot, Protoss_Dragoon, Hero_Fenix_Dragoon, Hero_Tassadar, Hero_Aldaris, Protoss_Archon,
            Hero_Tassadar_Zeratul_Archon, Hero_Dark_Templar, Hero_Zeratul, Protoss_Dark_Templar };
    private static UnitType _Protoss_AirWpn[] = { Protoss_Scout, Hero_Mojo, Protoss_Arbiter, Hero_Danimoth, Protoss_Interceptor, Protoss_Carrier, Protoss_Corsair, Hero_Artanis };
    private static UnitType _Shields[] = { Protoss_Corsair, Protoss_Dark_Templar, Protoss_Dark_Archon, Protoss_Probe, Protoss_Zealot, Protoss_Dragoon, Protoss_High_Templar,
            Protoss_Archon, Protoss_Shuttle, Protoss_Scout, Protoss_Arbiter, Protoss_Carrier, Protoss_Interceptor, Hero_Dark_Templar,
            Hero_Zeratul, Hero_Tassadar_Zeratul_Archon, Hero_Fenix_Zealot, Hero_Fenix_Dragoon, Hero_Tassadar, Hero_Mojo, Hero_Warbringer,
            Hero_Gantrithor, Protoss_Reaver, Protoss_Observer, Hero_Danimoth, Hero_Aldaris, Hero_Artanis, Hero_Raszagal };
    private static UnitType Shells[] = { Terran_Marine };
    private static UnitType _Ion_Thrusters[] = { Terran_Vulture };
    private static UnitType _Titan_Reactor[] = { Terran_Science_Vessel };
    private static UnitType _Ghost_Upgrades[] = { Terran_Ghost };
    private static UnitType _Apollo_Reactor[] = { Terran_Wraith };
    private static UnitType _Colossus_Reactor[] = { Terran_Battlecruiser };
    private static UnitType _Overlord_Upgrades[] = { Zerg_Overlord };
    private static UnitType _Zergling_Upgrades[] = { Zerg_Zergling };
    private static UnitType _Hydralisk_Upgrades[] = { Zerg_Hydralisk };
    private static UnitType _Gamete_Meiosis[] = { Zerg_Queen };
    private static UnitType _Metasynaptic_Node[] = { Zerg_Defiler };
    private static UnitType _Singularity_Charge[] = { Protoss_Dragoon };
    private static UnitType _Leg_Enhancements[] = { Protoss_Zealot };
    private static UnitType _Reaver_Upgrades[] = { Protoss_Reaver };
    private static UnitType _Gravitic_Drive[] = { Protoss_Shuttle };
    private static UnitType _Observer_Upgrades[] = { Protoss_Observer };
    private static UnitType _Khaydarin_Amulet[] = { Protoss_High_Templar };
    private static UnitType _Scout_Upgrades[] = { Protoss_Scout };
    private static UnitType _Carrier_Capacity[] = { Protoss_Carrier };
    private static UnitType _Khaydarin_Core[] = { Protoss_Arbiter };
    private static UnitType _Argus_Jewel[] = { Protoss_Corsair };
    private static UnitType _Argus_Talisman[] = { Protoss_Dark_Archon };
    private static UnitType _Caduceus_Reactor[] = { Terran_Medic };
    private static UnitType _Ultralisk_Upgrades[] = { Zerg_Ultralisk };
    private static UnitType _Charon_Boosters[] = { Terran_Goliath };

    private static UnitType _Upgrade60[] = { Terran_Vulture_Spider_Mine, Critter_Ursadon, Critter_Scantid, Critter_Rhynadon, Critter_Ragnasaur, Critter_Kakaru, Critter_Bengalaas,
            Special_Cargo_Ship, Special_Mercenary_Gunship, Terran_SCV, Protoss_Probe, Zerg_Drone, Zerg_Infested_Terran, Zerg_Scourge };

    private static UnitType upgradeWhatUses[][] = {
        _Infantry_Armor, _Vehicle_Plating, _Ship_Plating, _Carapace, _Flyer_Carapace, _Protoss_Armor, _Protoss_Plating,
                _Infantry_Weapons, _Vehicle_Weapons, _Ship_Weapons, _Zerg_MeleeAtk, _Zerg_RangeAtk, _Zerg_FlyerAtk, _Protoss_GrndWpn,
                _Protoss_AirWpn, _Shields, Shells, _Ion_Thrusters, {}, _Titan_Reactor, _Ghost_Upgrades, _Ghost_Upgrades,
                _Apollo_Reactor, _Colossus_Reactor, _Overlord_Upgrades, _Overlord_Upgrades, _Overlord_Upgrades, _Zergling_Upgrades,
                _Zergling_Upgrades, _Hydralisk_Upgrades, _Hydralisk_Upgrades, _Gamete_Meiosis, _Metasynaptic_Node, _Singularity_Charge,
                _Leg_Enhancements, _Reaver_Upgrades, _Reaver_Upgrades, _Gravitic_Drive, _Observer_Upgrades, _Observer_Upgrades,
                _Khaydarin_Amulet, _Scout_Upgrades, _Scout_Upgrades, _Carrier_Capacity, _Khaydarin_Core, {}, {},
                _Argus_Jewel, {}, _Argus_Talisman, {}, _Caduceus_Reactor, _Ultralisk_Upgrades, _Ultralisk_Upgrades,
                _Charon_Boosters, {}, {}, {}, {}, {}, _Upgrade60, {}, {}
    };

    private static Race upgradeRaces[] = {
        Terran, Terran, Terran, Zerg, Zerg, Protoss, Protoss, Terran, Terran, Terran, Zerg, Zerg, Zerg, Protoss, Protoss, Protoss, Terran, Terran, Terran, Terran, Terran,
                Terran, Terran, Terran, Zerg, Zerg, Zerg, Zerg, Zerg, Zerg, Zerg, Zerg, Zerg, Protoss, Protoss, Protoss, Protoss, Protoss, Protoss, Protoss, Protoss, Protoss,
                Protoss, Protoss, Protoss, Race.None, Race.None, Protoss, Race.None, Protoss, Race.None, Terran, Zerg, Zerg, Terran, Race.None, Race.None, Race.None, Race.None, Race.None, Race.None, Race.None, Race.Unknown
    };
}
