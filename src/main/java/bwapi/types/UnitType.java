package bwapi.types;

import bwapi.point.TilePosition;

import java.util.Map;
import java.util.Map.Entry;
import java.util.List;

public enum UnitType {
    Terran_Marine(0),
    Terran_Ghost(1),
    Terran_Vulture(2),
    Terran_Goliath(3),
    Terran_Goliath_Turret(4),
    Terran_Siege_Tank_Tank_Mode(5),
    Terran_Siege_Tank_Tank_Mode_Turret(6),
    Terran_SCV(7),
    Terran_Wraith(8),
    Terran_Science_Vessel(9),
    Hero_Gui_Montag(10),
    Terran_Dropship(11),
    Terran_Battlecruiser(12),
    Terran_Vulture_Spider_Mine(13),
    Terran_Nuclear_Missile(14),
    Terran_Civilian(15),
    Hero_Sarah_Kerrigan(16),
    Hero_Alan_Schezar(17),
    Hero_Alan_Schezar_Turret(18),
    Hero_Jim_Raynor_Vulture(19),
    Hero_Jim_Raynor_Marine(20),
    Hero_Tom_Kazansky(21),
    Hero_Magellan(22),
    Hero_Edmund_Duke_Tank_Mode(23),
    Hero_Edmund_Duke_Tank_Mode_Turret(24),
    Hero_Edmund_Duke_Siege_Mode(25),
    Hero_Edmund_Duke_Siege_Mode_Turret(26),
    Hero_Arcturus_Mengsk(27),
    Hero_Hyperion(28),
    Hero_Norad_II(29),
    Terran_Siege_Tank_Siege_Mode(30),
    Terran_Siege_Tank_Siege_Mode_Turret(31),
    Terran_Firebat(32),
    Spell_Scanner_Sweep(33),
    Terran_Medic(34),
    Zerg_Larva(35),
    Zerg_Egg(36),
    Zerg_Zergling(37),
    Zerg_Hydralisk(38),
    Zerg_Ultralisk(39),
    Zerg_Broodling(40),
    Zerg_Drone(41),
    Zerg_Overlord(42),
    Zerg_Mutalisk(43),
    Zerg_Guardian(44),
    Zerg_Queen(45),
    Zerg_Defiler(46),
    Zerg_Scourge(47),
    Hero_Torrasque(48),
    Hero_Matriarch(49),
    Zerg_Infested_Terran(50),
    Hero_Infested_Kerrigan(51),
    Hero_Unclean_One(52),
    Hero_Hunter_Killer(53),
    Hero_Devouring_One(54),
    Hero_Kukulza_Mutalisk(55),
    Hero_Kukulza_Guardian(56),
    Hero_Yggdrasill(57),
    Terran_Valkyrie(58),
    Zerg_Cocoon(59),
    Protoss_Corsair(60),
    Protoss_Dark_Templar(61),
    Zerg_Devourer(62),
    Protoss_Dark_Archon(63),
    Protoss_Probe(64),
    Protoss_Zealot(65),
    Protoss_Dragoon(66),
    Protoss_High_Templar(67),
    Protoss_Archon(68),
    Protoss_Shuttle(69),
    Protoss_Scout(70),
    Protoss_Arbiter(71),
    Protoss_Carrier(72),
    Protoss_Interceptor(73),
    Hero_Dark_Templar(74),
    Hero_Zeratul(75),
    Hero_Tassadar_Zeratul_Archon(76),
    Hero_Fenix_Zealot(77),
    Hero_Fenix_Dragoon(78),
    Hero_Tassadar(79),
    Hero_Mojo(80),
    Hero_Warbringer(81),
    Hero_Gantrithor(82),
    Protoss_Reaver(83),
    Protoss_Observer(84),
    Protoss_Scarab(85),
    Hero_Danimoth(86),
    Hero_Aldaris(87),
    Hero_Artanis(88),
    Critter_Rhynadon(89),
    Critter_Bengalaas(90),
    Special_Cargo_Ship(91),
    Special_Mercenary_Gunship(92),
    Critter_Scantid(93),
    Critter_Kakaru(94),
    Critter_Ragnasaur(95),
    Critter_Ursadon(96),
    Zerg_Lurker_Egg(97),
    Hero_Raszagal(98),
    Hero_Samir_Duran(99),
    Hero_Alexei_Stukov(100),
    Special_Map_Revealer(101),
    Hero_Gerard_DuGalle(102),
    Zerg_Lurker(103),
    Hero_Infested_Duran(104),
    Spell_Disruption_Web(105),
    Terran_Command_Center(106),
    Terran_Comsat_Station(107),
    Terran_Nuclear_Silo(108),
    Terran_Supply_Depot(109),
    Terran_Refinery(110),
    Terran_Barracks(111),
    Terran_Academy(112),
    Terran_Factory(113),
    Terran_Starport(114),
    Terran_Control_Tower(115),
    Terran_Science_Facility(116),
    Terran_Covert_Ops(117),
    Terran_Physics_Lab(118),
    Unused_Terran1(119),
    Terran_Machine_Shop(120),
    Unused_Terran2(121),
    Terran_Engineering_Bay(122),
    Terran_Armory(123),
    Terran_Missile_Turret(124),
    Terran_Bunker(125),
    Special_Crashed_Norad_II(126),
    Special_Ion_Cannon(127),
    Powerup_Uraj_Crystal(128),
    Powerup_Khalis_Crystal(129),
    Zerg_Infested_Command_Center(130),
    Zerg_Hatchery(131),
    Zerg_Lair(132),
    Zerg_Hive(133),
    Zerg_Nydus_Canal(134),
    Zerg_Hydralisk_Den(135),
    Zerg_Defiler_Mound(136),
    Zerg_Greater_Spire(137),
    Zerg_Queens_Nest(138),
    Zerg_Evolution_Chamber(139),
    Zerg_Ultralisk_Cavern(140),
    Zerg_Spire(141),
    Zerg_Spawning_Pool(142),
    Zerg_Creep_Colony(143),
    Zerg_Spore_Colony(144),
    Unused_Zerg1(145),
    Zerg_Sunken_Colony(146),
    Special_Overmind_With_Shell(147),
    Special_Overmind(148),
    Zerg_Extractor(149),
    Special_Mature_Chrysalis(150),
    Special_Cerebrate(151),
    Special_Cerebrate_Daggoth(152),
    Unused_Zerg2(153),
    Protoss_Nexus(154),
    Protoss_Robotics_Facility(155),
    Protoss_Pylon(156),
    Protoss_Assimilator(157),
    Unused_Protoss1(158),
    Protoss_Observatory(159),
    Protoss_Gateway(160),
    Unused_Protoss2(161),
    Protoss_Photon_Cannon(162),
    Protoss_Citadel_of_Adun(163),
    Protoss_Cybernetics_Core(164),
    Protoss_Templar_Archives(165),
    Protoss_Forge(166),
    Protoss_Stargate(167),
    Special_Stasis_Cell_Prison(168),
    Protoss_Fleet_Beacon(169),
    Protoss_Arbiter_Tribunal(170),
    Protoss_Robotics_Support_Bay(171),
    Protoss_Shield_Battery(172),
    Special_Khaydarin_Crystal_Form(173),
    Special_Protoss_Temple(174),
    Special_XelNaga_Temple(175),
    Resource_Mineral_Field(176),
    Resource_Mineral_Field_Type_2(177),
    Resource_Mineral_Field_Type_3(178),
    Unused_Cave(179),
    Unused_Cave_In(180),
    Unused_Cantina(181),
    Unused_Mining_Platform(182),
    Unused_Independant_Command_Center(183),
    Special_Independant_Starport(184),
    Unused_Independant_Jump_Gate(185),
    Unused_Ruins(186),
    Unused_Khaydarin_Crystal_Formation(187),
    Resource_Vespene_Geyser(188),
    Special_Warp_Gate(189),
    Special_Psi_Disrupter(190),
    Unused_Zerg_Marker(191),
    Unused_Terran_Marker(192),
    Unused_Protoss_Marker(193),
    Special_Zerg_Beacon(194),
    Special_Terran_Beacon(195),
    Special_Protoss_Beacon(196),
    Special_Zerg_Flag_Beacon(197),
    Special_Terran_Flag_Beacon(198),
    Special_Protoss_Flag_Beacon(199),
    Special_Power_Generator(200),
    Special_Overmind_Cocoon(201),
    Spell_Dark_Swarm(202),
    Special_Floor_Missile_Trap(203),
    Special_Floor_Hatch(204),
    Special_Upper_Level_Door(205),
    Special_Right_Upper_Level_Door(206),
    Special_Pit_Door(207),
    Special_Right_Pit_Door(208),
    Special_Floor_Gun_Trap(209),
    Special_Wall_Missile_Trap(210),
    Special_Wall_Flame_Trap(211),
    Special_Right_Wall_Missile_Trap(212),
    Special_Right_Wall_Flame_Trap(213),
    Special_Start_Location(214),
    Powerup_Flag(215),
    Powerup_Young_Chrysalis(216),
    Powerup_Psi_Emitter(217),
    Powerup_Data_Disk(218),
    Powerup_Khaydarin_Crystal(219),
    Powerup_Mineral_Cluster_Type_1(220),
    Powerup_Mineral_Cluster_Type_2(221),
    Powerup_Protoss_Gas_Orb_Type_1(222),
    Powerup_Protoss_Gas_Orb_Type_2(223),
    Powerup_Zerg_Gas_Sac_Type_1(224),
    Powerup_Zerg_Gas_Sac_Type_2(225),
    Powerup_Terran_Gas_Tank_Type_1(226),
    Powerup_Terran_Gas_Tank_Type_2(227),
    None(228),
    AllUnits(229),
    Men(230),
    Buildings(231),
    Factories(232),
    Unknown(233);

    private int value;

    public int getValue(){
        return value;
    }

    UnitType(int value) {
        this.value = value;
    }

    public Race getRace() {
        return null;
    }

    public Entry<UnitType, Integer> whatBuilds() {
       return null;
    }

    public Map<UnitType, Integer> requiredUnits() {
        return null;
    }

    public TechType requiredTech() {
        return null;
    }

    public TechType cloakingTech() {
        return null;
    }

    public List<TechType> abilities() {
        return null;
    }

    public List<UpgradeType> upgrades() {
        return null;
    }

    public UpgradeType armorUpgrade() {
        return null;
    }

    public int maxHitPoints() {
        return -1;
    }

    public int maxShields() {
        return -1;
    }

    public int maxEnergy() {
        return -1;
    }

    public int armor() {
        return -1;
    }

    public int mineralPrice() {
        return -1;
    }

    public int gasPrice() {
        return -1;
    }

    public int buildTime() {
        return -1;
    }

    public int supplyRequired() {
        return -1;
    }

    public int supplyProvided() {
        return -1;
    }

    public int spaceRequired() {
        return -1;
    }

    public int spaceProvided() {
        return -1;
    }

    public int buildScore() {
        return -1;
    }

    public int destroyScore() {
        return -1;
    }

    public UnitSizeType size() {
        return null;
    }

    public int tileWidth() {
        return -1;
    }

    public int tileHeight() {
        return -1;
    }

    public TilePosition tileSize() {
        return null;
    }

    public int dimensionLeft() {
        return -1;
    }

    public int dimensionUp() {
        return -1;
    }

    public int dimensionRight() {
        return -1;
    }

    public int dimensionDown() {
        return -1;
    }

    public int width() {
        return -1;
    }

    public int height() {
        return -1;
    }

    public int seekRange() {
        return -1;
    }

    public int sightRange() {
        return -1;
    }

    public WeaponType groundWeapon() {
        return null;
    }

    public int maxGroundHits() {
        return -1;
    }

    public WeaponType airWeapon() {
        return null;
    }

    public int maxAirHits() {
        return -1;
    }

    public double topSpeed() {
        return -1;
    }

    public int acceleration() {
        return -1;
    }

    public int haltDistance() {
        return -1;
    }

    public int turnRadius() {
        return -1;
    }

    public boolean canProduce() {
        return false;
    }

    public boolean canAttack() {
        return false;
    }

    public boolean canMove() {
        return false;
    }

    public boolean isFlyer() {
        return false;
    }

    public boolean regeneratesHP() {
        return false;
    }

    public boolean isSpellcaster() {
        return false;
    }

    public boolean hasPermanentCloak() {
        return false;
    }

    public boolean isInvincible() {
        return false;
    }

    public boolean isOrganic() {
        return false;
    }

    public boolean isMechanical() {
        return false;
    }

    public boolean isRobotic() {
        return false;
    }

    public boolean isDetector() {
        return false;
    }

    public boolean isResourceContainer() {
        return false;
    }

    public boolean isResourceDepot() {
        return false;
    }

    public boolean isRefinery() {
        return false;
    }

    public boolean isWorker() {
        return false;
    }

    public boolean requiresPsi() {
        return false;
    }

    public boolean requiresCreep() {
        return false;
    }

    public boolean isTwoUnitsInOneEgg() {
        return false;
    }

    public boolean isBurrowable() {
        return false;
    }

    public boolean isCloakable() {
        return false;
    }

    public boolean isBuilding() {
        return false;
    }

    public boolean isAddon() {
        return false;
    }

    public boolean isFlyingBuilding() {
        return false;
    }

    public boolean isNeutral() {
        return false;
    }

    public boolean isHero() {
        return false;
    }

    public boolean isPowerup() {
        return false;
    }

    public boolean isBeacon() {
        return false;
    }

    public boolean isFlagBeacon() {
        return false;
    }

    public boolean isSpecialBuilding() {
        return false;
    }

    public boolean isSpell() {
        return false;
    }

    public boolean producesCreep() {
        return false;
    }

    public boolean producesLarva() {
        return false;
    }

    public boolean isMineralField() {
        return false;
    }

    public boolean isCritter() {
        return false;
    }

    public boolean canBuildAddon() {
        return false;
    }

    public List<TechType> researchesWhat() {
        return null;
    }

    public List<UpgradeType> upgradesWhat() {
        return null;
    }
}
