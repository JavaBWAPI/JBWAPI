package bwapi;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

    // DEFAULTS
    private static int[] defaultOreCostBase = {   // same as default gas cost base
            100, 100, 150, 150, 150, 100, 150, 100, 100, 100, 100, 100, 100, 100, 100, 200, 150, 100, 200, 150, 100, 150, 200, 150, 200, 150, 150, 100, 200,
            150, 150, 150, 150, 150, 150, 200, 200, 200, 150, 150, 150, 100, 200, 100, 150, 0, 0, 100, 100, 150, 150, 150, 150, 200, 100, 0, 0, 0, 0, 0, 0, 0, 0
    };
    private static int[] defaultOreCostFactor = {   // same as default gas cost factor
            75, 75, 75, 75, 75, 75, 75, 75, 75, 50, 50, 50, 75, 50, 75, 100, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };
    private static int[] defaultTimeCostBase = {
            4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 4000, 1500, 1500, 0, 2500,
            2500, 2500, 2500, 2500, 2400, 2000, 2000, 1500, 1500, 1500, 1500, 2500, 2500, 2500, 2000, 2500, 2500, 2500, 2000,
            2000, 2500, 2500, 2500, 1500, 2500, 0, 0, 2500, 2500, 2500, 2500, 2500, 2000, 2000, 2000, 0, 0, 0, 0, 0, 0, 0, 0
    };
    private static int[] defaultTimeCostFactor = {
            480, 480, 480, 480, 480, 480, 480, 480, 480, 480, 480, 480, 480, 480, 480, 480, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };
    private static int[] defaultMaxRepeats = {
            3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 0, 1, 0, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0
    };
    private static UnitType[] whatUpgrades = {
            UnitType.Terran_Engineering_Bay, UnitType.Terran_Armory, UnitType.Terran_Armory, UnitType.Zerg_Evolution_Chamber, UnitType.Zerg_Spire, UnitType.Protoss_Forge, UnitType.Protoss_Cybernetics_Core, UnitType.Terran_Engineering_Bay,
            UnitType.Terran_Armory, UnitType.Terran_Armory, UnitType.Zerg_Evolution_Chamber, UnitType.Zerg_Evolution_Chamber, UnitType.Zerg_Spire, UnitType.Protoss_Forge, UnitType.Protoss_Cybernetics_Core, UnitType.Protoss_Forge, UnitType.Terran_Academy,
            UnitType.Terran_Machine_Shop, UnitType.None, UnitType.Terran_Science_Facility, UnitType.Terran_Covert_Ops, UnitType.Terran_Covert_Ops, UnitType.Terran_Control_Tower, UnitType.Terran_Physics_Lab, UnitType.Zerg_Lair, UnitType.Zerg_Lair, UnitType.Zerg_Lair,
            UnitType.Zerg_Spawning_Pool, UnitType.Zerg_Spawning_Pool, UnitType.Zerg_Hydralisk_Den, UnitType.Zerg_Hydralisk_Den, UnitType.Zerg_Queens_Nest, UnitType.Zerg_Defiler_Mound, UnitType.Protoss_Cybernetics_Core, UnitType.Protoss_Citadel_of_Adun,
            UnitType.Protoss_Robotics_Support_Bay, UnitType.Protoss_Robotics_Support_Bay, UnitType.Protoss_Robotics_Support_Bay, UnitType.Protoss_Observatory, UnitType.Protoss_Observatory, UnitType.Protoss_Templar_Archives,
            UnitType.Protoss_Fleet_Beacon, UnitType.Protoss_Fleet_Beacon, UnitType.Protoss_Fleet_Beacon, UnitType.Protoss_Arbiter_Tribunal, UnitType.None, UnitType.None, UnitType.Protoss_Fleet_Beacon, UnitType.None, UnitType.Protoss_Templar_Archives,
            UnitType.None, UnitType.Terran_Academy, UnitType.Zerg_Ultralisk_Cavern, UnitType.Zerg_Ultralisk_Cavern, UnitType.Terran_Machine_Shop, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None
    };
    private static UnitType[][] requirements = {
            // Level 1
            {
                    UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None,
                    UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.Zerg_Hive, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None,
                    UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.Terran_Armory, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None
            },
            // Level 2
            {
                    UnitType.Terran_Science_Facility, UnitType.Terran_Science_Facility, UnitType.Terran_Science_Facility, UnitType.Zerg_Lair, UnitType.Zerg_Lair, UnitType.Protoss_Templar_Archives, UnitType.Protoss_Fleet_Beacon,
                    UnitType.Terran_Science_Facility, UnitType.Terran_Science_Facility, UnitType.Terran_Science_Facility, UnitType.Zerg_Lair, UnitType.Zerg_Lair, UnitType.Zerg_Lair, UnitType.Protoss_Templar_Archives,
                    UnitType.Protoss_Fleet_Beacon, UnitType.Protoss_Cybernetics_Core, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None,
                    UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None,
                    UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None
            },
            // Level 3
            {
                    UnitType.Terran_Science_Facility, UnitType.Terran_Science_Facility, UnitType.Terran_Science_Facility, UnitType.Zerg_Hive, UnitType.Zerg_Hive, UnitType.Protoss_Templar_Archives, UnitType.Protoss_Fleet_Beacon,
                    UnitType.Terran_Science_Facility, UnitType.Terran_Science_Facility, UnitType.Terran_Science_Facility, UnitType.Zerg_Hive, UnitType.Zerg_Hive, UnitType.Zerg_Hive, UnitType.Protoss_Templar_Archives,
                    UnitType.Protoss_Fleet_Beacon, UnitType.Protoss_Cybernetics_Core, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None,
                    UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None,
                    UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None
            },
    };
    private static UnitType[] _Infantry_Armor = {UnitType.Terran_Marine, UnitType.Terran_Ghost, UnitType.Terran_SCV, UnitType.Hero_Gui_Montag, UnitType.Terran_Civilian, UnitType.Hero_Sarah_Kerrigan,
            UnitType.Hero_Jim_Raynor_Marine, UnitType.Terran_Firebat, UnitType.Terran_Medic, UnitType.Hero_Samir_Duran, UnitType.Hero_Alexei_Stukov};
    private static UnitType[] _Vehicle_Plating = {UnitType.Terran_Vulture, UnitType.Terran_Goliath, UnitType.Terran_Siege_Tank_Tank_Mode, UnitType.Hero_Alan_Schezar, UnitType.Hero_Jim_Raynor_Vulture,
            UnitType.Hero_Edmund_Duke_Tank_Mode, UnitType.Hero_Edmund_Duke_Siege_Mode, UnitType.Terran_Siege_Tank_Siege_Mode};
    private static UnitType[] _Ship_Plating = {UnitType.Terran_Wraith, UnitType.Terran_Science_Vessel, UnitType.Terran_Dropship, UnitType.Terran_Battlecruiser, UnitType.Hero_Tom_Kazansky, UnitType.Hero_Magellan,
            UnitType.Hero_Arcturus_Mengsk, UnitType.Hero_Hyperion, UnitType.Hero_Norad_II, UnitType.Terran_Valkyrie, UnitType.Hero_Gerard_DuGalle};
    private static UnitType[] _Carapace = {UnitType.Zerg_Larva, UnitType.Zerg_Egg, UnitType.Zerg_Zergling, UnitType.Zerg_Hydralisk, UnitType.Zerg_Ultralisk, UnitType.Zerg_Broodling, UnitType.Zerg_Drone, UnitType.Zerg_Defiler,
            UnitType.Hero_Torrasque, UnitType.Zerg_Infested_Terran, UnitType.Hero_Infested_Kerrigan, UnitType.Hero_Unclean_One, UnitType.Hero_Hunter_Killer, UnitType.Hero_Devouring_One,
            UnitType.Zerg_Cocoon, UnitType.Zerg_Lurker_Egg, UnitType.Zerg_Lurker, UnitType.Hero_Infested_Duran};
    private static UnitType[] _Flyer_Carapace = {UnitType.Zerg_Overlord, UnitType.Zerg_Mutalisk, UnitType.Zerg_Guardian, UnitType.Zerg_Queen, UnitType.Zerg_Scourge, UnitType.Hero_Matriarch, UnitType.Hero_Kukulza_Mutalisk,
            UnitType.Hero_Kukulza_Guardian, UnitType.Hero_Yggdrasill, UnitType.Zerg_Devourer};
    private static UnitType[] _Protoss_Armor = {UnitType.Protoss_Dark_Templar, UnitType.Protoss_Dark_Archon, UnitType.Protoss_Probe, UnitType.Protoss_Zealot, UnitType.Protoss_Dragoon, UnitType.Protoss_High_Templar,
            UnitType.Protoss_Archon, UnitType.Hero_Dark_Templar, UnitType.Hero_Zeratul, UnitType.Hero_Tassadar_Zeratul_Archon, UnitType.Hero_Fenix_Zealot, UnitType.Hero_Fenix_Dragoon,
            UnitType.Hero_Tassadar, UnitType.Hero_Warbringer, UnitType.Protoss_Reaver, UnitType.Hero_Aldaris};
    private static UnitType[] _Protoss_Plating = {UnitType.Protoss_Corsair, UnitType.Protoss_Shuttle, UnitType.Protoss_Scout, UnitType.Protoss_Arbiter, UnitType.Protoss_Carrier, UnitType.Protoss_Interceptor, UnitType.Hero_Mojo,
            UnitType.Hero_Gantrithor, UnitType.Protoss_Observer, UnitType.Hero_Danimoth, UnitType.Hero_Artanis, UnitType.Hero_Raszagal};
    private static UnitType[] _Infantry_Weapons = {UnitType.Terran_Marine, UnitType.Hero_Jim_Raynor_Marine, UnitType.Terran_Ghost, UnitType.Hero_Sarah_Kerrigan, UnitType.Terran_Firebat, UnitType.Hero_Gui_Montag,
            UnitType.Special_Wall_Flame_Trap, UnitType.Special_Right_Wall_Flame_Trap, UnitType.Hero_Samir_Duran, UnitType.Hero_Alexei_Stukov, UnitType.Hero_Infested_Duran};
    private static UnitType[] _Vehicle_Weapons = {UnitType.Terran_Vulture, UnitType.Hero_Jim_Raynor_Vulture, UnitType.Terran_Goliath, UnitType.Hero_Alan_Schezar, UnitType.Terran_Siege_Tank_Tank_Mode,
            UnitType.Terran_Siege_Tank_Siege_Mode, UnitType.Hero_Edmund_Duke_Tank_Mode, UnitType.Hero_Edmund_Duke_Siege_Mode, UnitType.Special_Floor_Missile_Trap,
            UnitType.Special_Floor_Gun_Trap, UnitType.Special_Wall_Missile_Trap, UnitType.Special_Right_Wall_Missile_Trap};
    private static UnitType[] _Ship_Weapons = {UnitType.Terran_Wraith, UnitType.Hero_Tom_Kazansky, UnitType.Terran_Battlecruiser, UnitType.Hero_Hyperion, UnitType.Hero_Norad_II, UnitType.Hero_Arcturus_Mengsk,
            UnitType.Hero_Gerard_DuGalle, UnitType.Terran_Valkyrie};
    private static UnitType[] _Zerg_MeleeAtk = {UnitType.Zerg_Zergling, UnitType.Hero_Devouring_One, UnitType.Hero_Infested_Kerrigan, UnitType.Zerg_Ultralisk, UnitType.Hero_Torrasque, UnitType.Zerg_Broodling};
    private static UnitType[] _Zerg_RangeAtk = {UnitType.Zerg_Hydralisk, UnitType.Hero_Hunter_Killer, UnitType.Zerg_Lurker};
    private static UnitType[] _Zerg_FlyerAtk = {UnitType.Zerg_Mutalisk, UnitType.Hero_Kukulza_Mutalisk, UnitType.Hero_Kukulza_Guardian, UnitType.Zerg_Guardian, UnitType.Zerg_Devourer};
    private static UnitType[] _Protoss_GrndWpn = {UnitType.Protoss_Zealot, UnitType.Hero_Fenix_Zealot, UnitType.Protoss_Dragoon, UnitType.Hero_Fenix_Dragoon, UnitType.Hero_Tassadar, UnitType.Hero_Aldaris, UnitType.Protoss_Archon,
            UnitType.Hero_Tassadar_Zeratul_Archon, UnitType.Hero_Dark_Templar, UnitType.Hero_Zeratul, UnitType.Protoss_Dark_Templar};
    private static UnitType[] _Protoss_AirWpn = {UnitType.Protoss_Scout, UnitType.Hero_Mojo, UnitType.Protoss_Arbiter, UnitType.Hero_Danimoth, UnitType.Protoss_Interceptor, UnitType.Protoss_Carrier, UnitType.Protoss_Corsair, UnitType.Hero_Artanis};
    private static UnitType[] _Shields = {UnitType.Protoss_Corsair, UnitType.Protoss_Dark_Templar, UnitType.Protoss_Dark_Archon, UnitType.Protoss_Probe, UnitType.Protoss_Zealot, UnitType.Protoss_Dragoon, UnitType.Protoss_High_Templar,
            UnitType.Protoss_Archon, UnitType.Protoss_Shuttle, UnitType.Protoss_Scout, UnitType.Protoss_Arbiter, UnitType.Protoss_Carrier, UnitType.Protoss_Interceptor, UnitType.Hero_Dark_Templar,
            UnitType.Hero_Zeratul, UnitType.Hero_Tassadar_Zeratul_Archon, UnitType.Hero_Fenix_Zealot, UnitType.Hero_Fenix_Dragoon, UnitType.Hero_Tassadar, UnitType.Hero_Mojo, UnitType.Hero_Warbringer,
            UnitType.Hero_Gantrithor, UnitType.Protoss_Reaver, UnitType.Protoss_Observer, UnitType.Hero_Danimoth, UnitType.Hero_Aldaris, UnitType.Hero_Artanis, UnitType.Hero_Raszagal};
    private static UnitType[] Shells = {UnitType.Terran_Marine};
    private static UnitType[] _Ion_Thrusters = {UnitType.Terran_Vulture};
    private static UnitType[] _Titan_Reactor = {UnitType.Terran_Science_Vessel};
    private static UnitType[] _Ghost_Upgrades = {UnitType.Terran_Ghost};
    private static UnitType[] _Apollo_Reactor = {UnitType.Terran_Wraith};
    private static UnitType[] _Colossus_Reactor = {UnitType.Terran_Battlecruiser};
    private static UnitType[] _Overlord_Upgrades = {UnitType.Zerg_Overlord};
    private static UnitType[] _Zergling_Upgrades = {UnitType.Zerg_Zergling};
    private static UnitType[] _Hydralisk_Upgrades = {UnitType.Zerg_Hydralisk};
    private static UnitType[] _Gamete_Meiosis = {UnitType.Zerg_Queen};
    private static UnitType[] _Metasynaptic_Node = {UnitType.Zerg_Defiler};
    private static UnitType[] _Singularity_Charge = {UnitType.Protoss_Dragoon};
    private static UnitType[] _Leg_Enhancements = {UnitType.Protoss_Zealot};
    private static UnitType[] _Reaver_Upgrades = {UnitType.Protoss_Reaver};
    private static UnitType[] _Gravitic_Drive = {UnitType.Protoss_Shuttle};
    private static UnitType[] _Observer_Upgrades = {UnitType.Protoss_Observer};
    private static UnitType[] _Khaydarin_Amulet = {UnitType.Protoss_High_Templar};
    private static UnitType[] _Scout_Upgrades = {UnitType.Protoss_Scout};
    private static UnitType[] _Carrier_Capacity = {UnitType.Protoss_Carrier};
    private static UnitType[] _Khaydarin_Core = {UnitType.Protoss_Arbiter};
    private static UnitType[] _Argus_Jewel = {UnitType.Protoss_Corsair};
    private static UnitType[] _Argus_Talisman = {UnitType.Protoss_Dark_Archon};
    private static UnitType[] _Caduceus_Reactor = {UnitType.Terran_Medic};
    private static UnitType[] _Ultralisk_Upgrades = {UnitType.Zerg_Ultralisk};
    private static UnitType[] _Charon_Boosters = {UnitType.Terran_Goliath};
    private static UnitType[] _Upgrade60 = {UnitType.Terran_Vulture_Spider_Mine, UnitType.Critter_Ursadon, UnitType.Critter_Scantid, UnitType.Critter_Rhynadon, UnitType.Critter_Ragnasaur, UnitType.Critter_Kakaru, UnitType.Critter_Bengalaas,
            UnitType.Special_Cargo_Ship, UnitType.Special_Mercenary_Gunship, UnitType.Terran_SCV, UnitType.Protoss_Probe, UnitType.Zerg_Drone, UnitType.Zerg_Infested_Terran, UnitType.Zerg_Scourge};
    private static UnitType[][] upgradeWhatUses = {
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
    private static Race[] upgradeRaces = {
            Race.Terran, Race.Terran, Race.Terran, Race.Zerg, Race.Zerg, Race.Protoss, Race.Protoss, Race.Terran, Race.Terran, Race.Terran, Race.Zerg, Race.Zerg, Race.Zerg, Race.Protoss, Race.Protoss, Race.Protoss, Race.Terran, Race.Terran, Race.Terran, Race.Terran, Race.Terran,
            Race.Terran, Race.Terran, Race.Terran, Race.Zerg, Race.Zerg, Race.Zerg, Race.Zerg, Race.Zerg, Race.Zerg, Race.Zerg, Race.Zerg, Race.Zerg, Race.Protoss, Race.Protoss, Race.Protoss, Race.Protoss, Race.Protoss, Race.Protoss, Race.Protoss, Race.Protoss, Race.Protoss,
            Race.Protoss, Race.Protoss, Race.Protoss, Race.None, Race.None, Race.Protoss, Race.None, Race.Protoss, Race.None, Race.Terran, Race.Zerg, Race.Zerg, Race.Terran, Race.None, Race.None, Race.None, Race.None, Race.None, Race.None, Race.None, Race.Unknown
    };

    public static UpgradeType[] idToEnum = new UpgradeType[62 + 1];

    static {
        Arrays.stream(UpgradeType.values()).forEach(v -> idToEnum[v.id] = v);
    }

    final int id;

    UpgradeType(final int id) {
        this.id = id;
    }

    public Race getRace() {
        return upgradeRaces[id];
    }

    public int mineralPrice() {
        return mineralPrice(1);
    }

    public int mineralPrice(final int level) {
        return defaultOreCostBase[id] + Math.max(0, level - 1) * mineralPriceFactor();
    }

    public int mineralPriceFactor() {
        return defaultOreCostFactor[id];
    }

    public int gasPrice() {
        return mineralPrice();
    }

    public int gasPrice(final int level) {
        return mineralPrice(level);
    }

    public int gasPriceFactor() {
        return mineralPriceFactor();
    }

    public int upgradeTime() {
        return upgradeTime(1);
    }

    public int upgradeTime(final int level) {
        return defaultTimeCostBase[id] + Math.max(0, level - 1) * upgradeTimeFactor();
    }

    public int upgradeTimeFactor() {
        return defaultTimeCostFactor[id];
    }

    public UnitType whatUpgrades() {
        return whatUpgrades[id];
    }

    public List<UnitType> whatUses() {
        return Collections.unmodifiableList(Arrays.asList(upgradeWhatUses[id]));
    }

    public int maxRepeats() {
        return defaultMaxRepeats[id];
    }

    public UnitType whatsRequired() {
        return whatsRequired(1);
    }

    public UnitType whatsRequired(final int level) {
        return level >= 1 && level <= 3 ? requirements[level - 1][id] : UnitType.None;
    }
}
