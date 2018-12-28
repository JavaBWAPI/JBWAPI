package bwapi;

import java.util.*;
import java.util.stream.Collectors;

import static bwapi.TechType.*;

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

    static UnitType[] idToEnum = new UnitType[233 + 1];

    static {
        Arrays.stream(UnitType.values()).forEach(v -> idToEnum[v.id] = v);
    }

    final int id;

    UnitType(final int id) {
        this.id = id;
    }

    public static int maxUnitWidth() {
        return Arrays.stream(UnitType.values()).max(Comparator.comparingInt(UnitType::width)).get().width();
    }

    public static int maxUnitHeight() {
        return Arrays.stream(UnitType.values()).max(Comparator.comparingInt(UnitType::height)).get().height();
    }

    public Race getRace() {
        return UnitTypeContainer.unitRace[id];
    }

    public Pair<UnitType, Integer> whatBuilds() {
        // Retrieve the type
        final UnitType type = UnitTypeContainer.whatBuilds[id];
        int count = 1;
        // Set count to 0 if there is no whatBuilds and 2 if it's an archon
        if (type == UnitType.None) {
            count = 0;
        } else if (this == UnitType.Protoss_Archon || this == UnitType.Protoss_Dark_Archon) {
            count = 2;
        }
        // Return the desired pair
        return new Pair<>(type, count);
    }

    public Map<UnitType, Integer> requiredUnits() {
        return UnitTypeContainer.reqUnitsMap.get(id);
    }

    public TechType requiredTech() {
        return this == Zerg_Lurker || this == Zerg_Lurker_Egg ? Lurker_Aspect : TechType.None;
    }

    public TechType cloakingTech() {
        switch (this) {
            case Terran_Ghost:
            case Hero_Alexei_Stukov:
            case Hero_Infested_Duran:
            case Hero_Infested_Kerrigan:
            case Hero_Sarah_Kerrigan:
            case Hero_Samir_Duran:
                return Personnel_Cloaking;
            case Terran_Wraith:
            case Hero_Tom_Kazansky:
                return Cloaking_Field;
            default:
                return TechType.None;
        }
    }

    public List<TechType> abilities() {
        return Arrays.asList(UnitTypeContainer.unitTechs[id]);
    }

    public List<UpgradeType> upgrades() {
        return Arrays.asList(UnitTypeContainer.upgrades[id]);
    }

    public UpgradeType armorUpgrade() {
        return UnitTypeContainer.armorUpgrade[id];
    }

    public int maxHitPoints() {
        return UnitTypeContainer.defaultMaxHP[id];
    }

    public int maxShields() {
        return UnitTypeContainer.defaultMaxSP[id];
    }

    public int maxEnergy() {
        return isSpellcaster() ? isHero() ? 250 : 200 : 0;
    }

    public int armor() {
        return UnitTypeContainer.defaultArmorAmount[id];
    }

    public int mineralPrice() {
        return UnitTypeContainer.defaultOreCost[id];
    }

    public int gasPrice() {
        return UnitTypeContainer.defaultGasCost[id];
    }

    public int buildTime() {
        return UnitTypeContainer.defaultTimeCost[id];
    }

    public int supplyRequired() {
        return UnitTypeContainer.unitSupplyRequired[id];
    }

    public int supplyProvided() {
        return UnitTypeContainer.unitSupplyProvided[id];
    }

    public int spaceRequired() {
        return UnitTypeContainer.unitSpaceRequired[id];
    }

    public int spaceProvided() {
        return UnitTypeContainer.unitSpaceProvided[id];
    }

    public int buildScore() {
        return UnitTypeContainer.unitBuildScore[id];
    }

    public int destroyScore() {
        return UnitTypeContainer.unitDestroyScore[id];
    }

    public UnitSizeType size() {
        return UnitTypeContainer.unitSize[id];
    }

    public int tileWidth() {
        return UnitTypeContainer.unitDimensions[id][UnitTypeContainer.UnitDimensions.tileWidth];
    }

    public int tileHeight() {
        return UnitTypeContainer.unitDimensions[id][UnitTypeContainer.UnitDimensions.tileHeight];
    }

    public TilePosition tileSize() {
        return new TilePosition(tileWidth(), tileHeight());
    }

    public int dimensionLeft() {
        return UnitTypeContainer.unitDimensions[id][UnitTypeContainer.UnitDimensions.left];
    }

    public int dimensionUp() {
        return UnitTypeContainer.unitDimensions[id][UnitTypeContainer.UnitDimensions.up];
    }

    public int dimensionRight() {
        return UnitTypeContainer.unitDimensions[id][UnitTypeContainer.UnitDimensions.right];
    }

    public int dimensionDown() {
        return UnitTypeContainer.unitDimensions[id][UnitTypeContainer.UnitDimensions.down];
    }

    public int width() {
        return dimensionLeft() + 1 + dimensionRight();
    }

    public int height() {
        return dimensionUp() + 1 + dimensionDown();
    }

    public int seekRange() {
        return UnitTypeContainer.seekRangeTiles[id] * 32;
    }

    public int sightRange() {
        return UnitTypeContainer.sightRangeTiles[id] * 32;
    }

    public WeaponType groundWeapon() {
        return UnitTypeContainer.groundWeapon[id];
    }

    public int maxGroundHits() {
        return UnitTypeContainer.groundWeaponHits[id];
    }

    public WeaponType airWeapon() {
        return UnitTypeContainer.airWeapon[id];
    }

    public int maxAirHits() {
        return UnitTypeContainer.airWeaponHits[id];
    }

    public double topSpeed() {
        return UnitTypeContainer.unitTopSpeeds[id];
    }

    public int acceleration() {
        return UnitTypeContainer.unitAcceleration[id];
    }

    public int haltDistance() {
        return UnitTypeContainer.unitHaltDistance[id];
    }

    public int turnRadius() {
        return UnitTypeContainer.unitTurnRadius[id];
    }

    public boolean canProduce() {
        return (UnitTypeContainer.unitFlags[id] & UnitTypeContainer.ProducesUnits) != 0;
    }

    public boolean canAttack() {
        switch (this) {
            case Protoss_Carrier:
            case Hero_Gantrithor:
            case Protoss_Reaver:
            case Hero_Warbringer:
            case Terran_Nuclear_Missile:
                return true;
            case Special_Independant_Starport:
                return false;
            default:
                return airWeapon() != WeaponType.None || groundWeapon() != WeaponType.None;
        }
    }

    public boolean canMove() {
        return (UnitTypeContainer.unitFlags[id] & UnitTypeContainer.AutoAttackAndMove) != 0;
    }

    public boolean isFlyer() {
        return (UnitTypeContainer.unitFlags[id] & UnitTypeContainer.Flyer) != 0;
    }

    public boolean regeneratesHP() {
        return (UnitTypeContainer.unitFlags[id] & UnitTypeContainer.RegeneratesHP) != 0;
    }

    public boolean isSpellcaster() {
        return (UnitTypeContainer.unitFlags[id] & UnitTypeContainer.Spellcaster) != 0;
    }

    public boolean hasPermanentCloak() {
        return (UnitTypeContainer.unitFlags[id] & UnitTypeContainer.PermanentCloak) != 0;
    }

    public boolean isInvincible() {
        return (UnitTypeContainer.unitFlags[id] & UnitTypeContainer.Invincible) != 0;
    }

    public boolean isOrganic() {
        return (UnitTypeContainer.unitFlags[id] & UnitTypeContainer.OrganicUnit) != 0;
    }

    public boolean isMechanical() {
        return (UnitTypeContainer.unitFlags[id] & UnitTypeContainer.Mechanical) != 0;
    }

    public boolean isRobotic() {
        return (UnitTypeContainer.unitFlags[id] & UnitTypeContainer.RoboticUnit) != 0;
    }

    public boolean isDetector() {
        return (UnitTypeContainer.unitFlags[id] & UnitTypeContainer.Detector) != 0;
    }

    public boolean isResourceContainer() {
        return (UnitTypeContainer.unitFlags[id] & UnitTypeContainer.ResourceContainer) != 0;
    }

    public boolean isResourceDepot() {
        return (UnitTypeContainer.unitFlags[id] & UnitTypeContainer.ResourceDepot) != 0;
    }

    public boolean isRefinery() {
        switch (this) {
            case Terran_Refinery:
            case Zerg_Extractor:
            case Protoss_Assimilator:
                return true;
            default:
                return false;
        }
    }

    public boolean isWorker() {
        return (UnitTypeContainer.unitFlags[id] & UnitTypeContainer.Worker) != 0;
    }

    public boolean requiresPsi() {
        return (UnitTypeContainer.unitFlags[id] & UnitTypeContainer.RequiresPsi) != 0;
    }

    public boolean requiresCreep() {
        return (UnitTypeContainer.unitFlags[id] & UnitTypeContainer.CreepBuilding) != 0;
    }

    public boolean isTwoUnitsInOneEgg() {
        return (UnitTypeContainer.unitFlags[id] & UnitTypeContainer.TwoUnitsIn1Egg) != 0;
    }

    public boolean isBurrowable() {
        return (UnitTypeContainer.unitFlags[id] & UnitTypeContainer.Burrowable) != 0;
    }

    public boolean isCloakable() {
        return (UnitTypeContainer.unitFlags[id] & UnitTypeContainer.Cloakable) != 0;
    }

    public boolean isBuilding() {
        return (UnitTypeContainer.unitFlags[id] & UnitTypeContainer.Building) != 0;
    }

    public boolean isAddon() {
        return (UnitTypeContainer.unitFlags[id] & UnitTypeContainer.Addon) != 0;
    }

    public boolean isFlyingBuilding() {
        return (UnitTypeContainer.unitFlags[id] & UnitTypeContainer.FlyingBuilding) != 0;
    }

    public boolean isNeutral() {
        return getRace() == Race.None &&
                (isCritter() || isResourceContainer() || isSpell());
    }

    public boolean isHero() {
        return ((UnitTypeContainer.unitFlags[id] & UnitTypeContainer.Hero) != 0) ||
                this == Hero_Dark_Templar ||
                this == Terran_Civilian;
    }

    public boolean isPowerup() {
        return this == Powerup_Uraj_Crystal ||
                this == Powerup_Khalis_Crystal ||
                (this.id >= Powerup_Flag.id && this.id < None.id);
    }

    public boolean isBeacon() {
        return this == Special_Zerg_Beacon ||
                this == Special_Terran_Beacon ||
                this == Special_Protoss_Beacon;
    }

    public boolean isFlagBeacon() {
        return this == Special_Zerg_Flag_Beacon ||
                this == Special_Terran_Flag_Beacon ||
                this == Special_Protoss_Flag_Beacon;
    }

    public boolean isSpecialBuilding() {
        return isBuilding() &&
                whatBuilds().getValue() == 0 &&
                this != Zerg_Infested_Command_Center;
    }

    public boolean isSpell() {
        return this == Spell_Dark_Swarm ||
                this == Spell_Disruption_Web ||
                this == Spell_Scanner_Sweep;
    }

    public boolean producesCreep() {
        return producesLarva() ||
                this == Zerg_Creep_Colony ||
                this == Zerg_Spore_Colony ||
                this == Zerg_Sunken_Colony;
    }

    public boolean producesLarva() {
        return this == Zerg_Hatchery ||
                this == Zerg_Lair ||
                this == Zerg_Hive;
    }

    public boolean isMineralField() {
        return this == Resource_Mineral_Field ||
                this == Resource_Mineral_Field_Type_2 ||
                this == Resource_Mineral_Field_Type_3;
    }

    public boolean isCritter() {
        switch (this) {
            case Critter_Bengalaas:
            case Critter_Kakaru:
            case Critter_Ragnasaur:
            case Critter_Rhynadon:
            case Critter_Scantid:
            case Critter_Ursadon:
                return true;
            default:
                return false;
        }
    }

    public boolean canBuildAddon() {
        return this == Terran_Command_Center ||
                this == Terran_Factory ||
                this == Terran_Starport ||
                this == Terran_Science_Facility;
    }

    public List<UnitType> buildsWhat() {
        return Arrays.asList(UnitTypeContainer.buildsWhat[id]);
    }

    public List<TechType> researchesWhat() {
        return Arrays.asList(UnitTypeContainer.researchesWhat[id]);
    }

    public List<UpgradeType> upgradesWhat() {
        return Arrays.asList(UnitTypeContainer.upgradesWhat[id]);
    }

    public boolean isSuccessorOf(final UnitType type) {
        if (this == type) {
            return true;
        }
        switch (type) {
            case Zerg_Hatchery:
                return this == Zerg_Lair || this == Zerg_Hive;
            case Zerg_Lair:
                return this == Zerg_Hive;
            case Zerg_Spire:
                return this == Zerg_Greater_Spire;
            default:
                return false;
        }
    }
}
