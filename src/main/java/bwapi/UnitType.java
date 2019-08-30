package bwapi;

import java.util.*;

import static bwapi.TechType.*;

/**
 * The {@link UnitType} is used to get information about a particular type of unit, such as its cost,
 * build time, weapon, hit points, abilities, etc.
 *
 * @see Unit#getType
 */
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

    static final UnitType[] idToEnum = new UnitType[233 + 1];

    static {
        Arrays.stream(UnitType.values()).forEach(v -> idToEnum[v.id] = v);
    }

    final int id;

    UnitType(final int id) {
        this.id = id;
    }

    /**
     * Retrieves the maximum unit width from the set of all units. Used
     * internally to search through unit positions efficiently.
     *
     * @return The maximum width of all unit types, in pixels.
     */
    public static int maxUnitWidth() {
        return Arrays.stream(UnitType.values()).max(Comparator.comparingInt(UnitType::width)).get().width();
    }

    /**
     * Retrieves the maximum unit height from the set of all units. Used
     * internally to search through unit positions efficiently.
     *
     * @return The maximum height of all unit types, in pixels.
     */
    public static int maxUnitHeight() {
        return Arrays.stream(UnitType.values()).max(Comparator.comparingInt(UnitType::height)).get().height();
    }

    /**
     * Retrieves the {@link Race} that the unit type belongs to.
     *
     * @return {@link Race} indicating the race that owns this unit type.
     * Returns {@link Race#None} indicating that the unit type does not belong to any particular race (a
     * critter for example).
     */
    public Race getRace() {
        return UnitTypeContainer.unitRace[id];
    }

    /**
     * Obtains the source unit type that is used to build or train this unit type, as well as the
     * amount of them that are required.
     *
     * @return std#pair in which the first value is the {@link UnitType} that builds this unit type, and
     * the second value is the number of those types that are required (this value is 2 for @Archons, and 1 for all other types).
     * Returns pair({@link UnitType#None},0) If this unit type cannot be made by the player.
     */
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

    /**
     * Retrieves the immediate technology tree requirements to make this unit type.
     *
     * @return Map containing a UnitType to number mapping of UnitTypes required.
     */
    public Map<UnitType, Integer> requiredUnits() {
        return UnitTypeContainer.reqUnitsMap.get(id);
    }

    /**
     * Identifies the required {@link TechType} in order to create certain units.
     *
     * The only unit that requires a technology is the @Lurker, which needs @Lurker_Aspect.
     *
     * @return {@link TechType} indicating the technology that must be researched in order to create this
     * unit type.
     * Returns {@link TechType#None} If creating this unit type does not require a technology to be
     * researched.
     */
    public TechType requiredTech() {
        return this == Zerg_Lurker || this == Zerg_Lurker_Egg ? Lurker_Aspect : TechType.None;
    }

    /**
     * Retrieves the cloaking technology associated with certain units.
     *
     * @return {@link TechType} referring to the cloaking technology that this unit type uses as an
     * ability.
     * Returns {@link TechType#None} If this unit type does not have an active cloak ability.
     */
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

    /**
     * Retrieves the set of abilities that this unit can use, provided it is available to you in
     * the game.
     *
     * @return List of TechTypes containing ability information.
     */
    public List<TechType> abilities() {
        return Collections.unmodifiableList(Arrays.asList(UnitTypeContainer.unitTechs[id]));
    }

    /**
     * Retrieves the set of upgrades that this unit can use to enhance its fighting ability.
     *
     * @return List of UpgradeTypes containing upgrade types that will impact this unit type.
     */
    public List<UpgradeType> upgrades() {
        return Collections.unmodifiableList(Arrays.asList(UnitTypeContainer.upgrades[id]));
    }

    /**
     * Retrieves the upgrade type used to increase the armor of this unit type. For each upgrade,
     * this unit type gains +1 additional armor.
     *
     * @return {@link UpgradeType} indicating the upgrade that increases this unit type's armor amount.
     */
    public UpgradeType armorUpgrade() {
        return UnitTypeContainer.armorUpgrade[id];
    }

    /**
     * Retrieves the default maximum amount of hit points that this unit type can have.
     *
     * This value may not necessarily match the value seen in the @UMS game type.
     *
     * @return Integer indicating the maximum amount of hit points for this unit type.
     */
    public int maxHitPoints() {
        return UnitTypeContainer.defaultMaxHP[id];
    }

    /**
     * Retrieves the default maximum amount of shield points that this unit type can have.
     *
     * This value may not necessarily match the value seen in the @UMS game type.
     *
     * @return Integer indicating the maximum amount of shield points for this unit type.
     * Returns 0 if this unit type does not have shields.
     */
    public int maxShields() {
        return UnitTypeContainer.defaultMaxSP[id];
    }

    /**
     * Retrieves the maximum amount of energy this unit type can have by default.
     *
     * @return Integer indicating the maximum amount of energy for this unit type.
     * Retunrs 0 ff this unit does not gain energy for abilities.
     */
    public int maxEnergy() {
        return isSpellcaster() ? isHero() ? 250 : 200 : 0;
    }

    /**
     * Retrieves the default amount of armor that the unit type starts with, excluding upgrades.
     *
     * This value may not necessarily match the value seen in the @UMS game type.
     *
     * @return The amount of armor the unit type has.
     */
    public int armor() {
        return UnitTypeContainer.defaultArmorAmount[id];
    }

    /**
     * Retrieves the default mineral price of purchasing the unit.
     *
     * This value may not necessarily match the value seen in the @UMS game type.
     *
     * @return Mineral cost of the unit.
     */
    public int mineralPrice() {
        return UnitTypeContainer.defaultOreCost[id];
    }

    /**
     * Retrieves the default vespene gas price of purchasing the unit.
     *
     * This value may not necessarily match the value seen in the @UMS game type.
     *
     * @return Vespene gas cost of the unit.
     */
    public int gasPrice() {
        return UnitTypeContainer.defaultGasCost[id];
    }

    /**
     * Retrieves the default time, in frames, needed to train, morph, or build the unit.
     *
     * This value may not necessarily match the value seen in the @UMS game type.
     *
     * @return Number of frames needed in order to build the unit.
     * @see Unit#getRemainingBuildTime
     */
    public int buildTime() {
        return UnitTypeContainer.defaultTimeCost[id];
    }

    /**
     * Retrieves the amount of supply that this unit type will use when created. It will use the
     * supply pool that is appropriate for its Race.
     *
     * In Starcraft programming, the managed supply values are double than what they appear
     * in the game. The reason for this is because @Zerglings use 0.5 visible supply.
     *
     * @return Integer containing the supply required to build this unit.
     * @see #supplyProvided
     * @see Player#supplyTotal
     * @see Player#supplyUsed
     */
    public int supplyRequired() {
        return UnitTypeContainer.unitSupplyRequired[id];
    }

    /**
     * Retrieves the amount of supply that this unit type produces for its appropriate Race's
     * supply pool.
     *
     * In Starcraft programming, the managed supply values are double than what they appear
     * in the game. The reason for this is because @Zerglings use 0.5 visible supply.
     *
     * @see #supplyRequired
     * @see Player#supplyTotal
     * @see Player#supplyUsed
     */
    public int supplyProvided() {
        return UnitTypeContainer.unitSupplyProvided[id];
    }

    /**
     * Retrieves the amount of space required by this unit type to fit inside a @Bunker or @Transport.
     *
     * @return Amount of space required by this unit type for transport.
     * Returns 255 If this unit type can not be transported.
     * @see #spaceProvided
     */
    public int spaceRequired() {
        return UnitTypeContainer.unitSpaceRequired[id];
    }

    /**
     * Retrieves the amount of space provided by this @Bunker or @Transport for unit
     * transportation.
     *
     * @return The number of slots provided by this unit type.
     * @see #spaceRequired
     */
    public int spaceProvided() {
        return UnitTypeContainer.unitSpaceProvided[id];
    }

    /**
     * Retrieves the amount of score points awarded for constructing this unit type. This value is
     * used for calculating scores in the post-game score screen.
     *
     * @return Number of points awarded for constructing this unit type.
     * @see #destroyScore
     */
    public int buildScore() {
        return UnitTypeContainer.unitBuildScore[id];
    }

    /**
     * Retrieves the amount of score points awarded for killing this unit type. This value is
     * used for calculating scores in the post-game score screen.
     *
     * @return Number of points awarded for killing this unit type.
     * @see #buildScore
     */
    public int destroyScore() {
        return UnitTypeContainer.unitDestroyScore[id];
    }

    /**
     * Retrieves the UnitSizeType of this unit, which is used in calculations along with weapon
     * damage types to determine the amount of damage that will be dealt to this type.
     *
     * @return {@link UnitSizeType} indicating the conceptual size of the unit type.
     * @see WeaponType#damageType()
     */
    public UnitSizeType size() {
        return UnitTypeContainer.unitSize[id];
    }

    /**
     * Retrieves the width of this unit type, in tiles. Used for determining the tile size of
     * structures.
     *
     * @return Width of this unit type, in tiles.
     */
    public int tileWidth() {
        return UnitTypeContainer.unitDimensions[id][UnitTypeContainer.UnitDimensions.tileWidth];
    }

    /**
     * Retrieves the height of this unit type, in tiles. Used for determining the tile size of
     * structures.
     *
     * @return Height of this unit type, in tiles.
     */
    public int tileHeight() {
        return UnitTypeContainer.unitDimensions[id][UnitTypeContainer.UnitDimensions.tileHeight];
    }

    /**
     * Retrieves the tile size of this unit type. Used for determining the tile size of
     * structures.
     *
     * @return {@link TilePosition} containing the width (x) and height (y) of the unit type, in tiles.
     */
    public TilePosition tileSize() {
        return new TilePosition(tileWidth(), tileHeight());
    }

    /**
     * Retrieves the distance from the center of the unit type to its left edge.
     *
     * @return Distance to this unit type's left edge from its center, in pixels.
     */
    public int dimensionLeft() {
        return UnitTypeContainer.unitDimensions[id][UnitTypeContainer.UnitDimensions.left];
    }

    /**
     * Retrieves the distance from the center of the unit type to its top edge.
     *
     * @return Distance to this unit type's top edge from its center, in pixels.
     */
    public int dimensionUp() {
        return UnitTypeContainer.unitDimensions[id][UnitTypeContainer.UnitDimensions.up];
    }

    /**
     * Retrieves the distance from the center of the unit type to its right edge.
     *
     * @return Distance to this unit type's right edge from its center, in pixels.
     */
    public int dimensionRight() {
        return UnitTypeContainer.unitDimensions[id][UnitTypeContainer.UnitDimensions.right];
    }

    /**
     * Retrieves the distance from the center of the unit type to its bottom edge.
     *
     * @return Distance to this unit type's bottom edge from its center, in pixels.
     */
    public int dimensionDown() {
        return UnitTypeContainer.unitDimensions[id][UnitTypeContainer.UnitDimensions.down];
    }

    /**
     * A macro for retrieving the width of the unit type, which is calculated using
     * dimensionLeft + dimensionRight + 1.
     *
     * @return Width of the unit, in pixels.
     */
    public int width() {
        return dimensionLeft() + 1 + dimensionRight();
    }

    /**
     * A macro for retrieving the height of the unit type, which is calculated using
     * dimensionUp + dimensionDown + 1.
     *
     * @return Height of the unit, in pixels.
     */
    public int height() {
        return dimensionUp() + 1 + dimensionDown();
    }

    /**
     * Retrieves the range at which this unit type will start targeting enemy units.
     *
     * @return Distance at which this unit type begins to seek out enemy units, in pixels.
     */
    public int seekRange() {
        return UnitTypeContainer.seekRangeTiles[id] * 32;
    }

    /**
     * Retrieves the sight range of this unit type.
     *
     * @return Sight range of this unit type, measured in pixels.
     */
    public int sightRange() {
        return UnitTypeContainer.sightRangeTiles[id] * 32;
    }

    /**
     * Retrieves this unit type's weapon type used when attacking targets on the ground.
     *
     * @return {@link WeaponType} used as this unit type's ground weapon.
     * @see #maxGroundHits
     * @see #airWeapon
     */
    public WeaponType groundWeapon() {
        return UnitTypeContainer.groundWeapon[id];
    }

    /**
     * Retrieves the maximum number of hits this unit can deal to a ground target using its
     * ground weapon. This value is multiplied by the ground weapon's damage to calculate the
     * unit type's damage potential.
     *
     * @return Maximum number of hits given to ground targets.
     * @see #groundWeapon
     * @see #maxAirHits
     */
    public int maxGroundHits() {
        return UnitTypeContainer.groundWeaponHits[id];
    }

    /**
     * Retrieves this unit type's weapon type used when attacking targets in the air.
     *
     * @return WeaponType used as this unit type's air weapon.
     * @see #maxAirHits
     * @see #groundWeapon
     */
    public WeaponType airWeapon() {
        return UnitTypeContainer.airWeapon[id];
    }

    /**
     * Retrieves the maximum number of hits this unit can deal to a flying target using its
     * air weapon. This value is multiplied by the air weapon's damage to calculate the
     * unit type's damage potential.
     *
     * @return Maximum number of hits given to air targets.
     * @see #airWeapon
     * @see #maxGroundHits
     */
    public int maxAirHits() {
        return UnitTypeContainer.airWeaponHits[id];
    }

    /**
     * Retrieves this unit type's top movement speed with no upgrades.
     *
     * That some units have inconsistent movement and this value is sometimes an
     * approximation.
     *
     * @return The approximate top speed, in pixels per frame, as a double. For liftable @Terran
     * structures, this function returns their movement speed while lifted.
     */
    public double topSpeed() {
        return UnitTypeContainer.unitTopSpeeds[id];
    }

    /**
     * Retrieves the unit's acceleration amount.
     *
     * @return How fast the unit can accelerate to its top speed.
     */
    public int acceleration() {
        return UnitTypeContainer.unitAcceleration[id];
    }

    /**
     * Retrieves the unit's halting distance. This determines how fast a unit
     * can stop moving.
     *
     * @return A halting distance value.
     */
    public int haltDistance() {
        return UnitTypeContainer.unitHaltDistance[id];
    }

    /**
     * Retrieves a unit's turning radius. This determines how fast a unit can
     * turn.
     *
     * @return A turn radius value.
     */
    public int turnRadius() {
        return UnitTypeContainer.unitTurnRadius[id];
    }

    /**
     * Determines if a unit can train other units. For example,
     * UnitType.Terran_Barracks.canProduce() will return true, while
     * UnitType.Terran_Marine.canProduce() will return false. This is also true for two
     * non-structures: @Carrier (can produce interceptors) and @Reaver (can produce scarabs).
     *
     * @return true if this unit type can have a production queue, and false otherwise.
     */
    public boolean canProduce() {
        return (UnitTypeContainer.unitFlags[id] & UnitTypeContainer.ProducesUnits) != 0;
    }

    /**
     * Checks if this unit is capable of attacking.
     *
     * This function returns false for units that can only inflict damage via special
     * abilities, such as the @High_Templar.
     *
     * @return true if this unit type is capable of damaging other units with a standard attack,
     * and false otherwise.
     */
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

    /**
     * Checks if this unit type is capable of movement.
     *
     * Buildings will return false, including @Terran liftable buildings which are capable
     * of moving when lifted.
     *
     * @return true if this unit can use a movement command, and false if they cannot move.
     */
    public boolean canMove() {
        return (UnitTypeContainer.unitFlags[id] & UnitTypeContainer.AutoAttackAndMove) != 0;
    }

    /**
     * Checks if this unit type is a flying unit. Flying units ignore ground pathing and
     * collisions.
     *
     * @return true if this unit type is in the air by default, and false otherwise.
     */
    public boolean isFlyer() {
        return (UnitTypeContainer.unitFlags[id] & UnitTypeContainer.Flyer) != 0;
    }

    /**
     * Checks if this unit type can regenerate hit points. This generally applies to @Zerg units.
     *
     * @return true if this unit type regenerates its hit points, and false otherwise.
     */
    public boolean regeneratesHP() {
        return (UnitTypeContainer.unitFlags[id] & UnitTypeContainer.RegeneratesHP) != 0;
    }

    /**
     * Checks if this unit type has the capacity to store energy and use it for special abilities.
     *
     * @return true if this unit type generates energy, and false if it does not have an energy
     * pool.
     */
    public boolean isSpellcaster() {
        return (UnitTypeContainer.unitFlags[id] & UnitTypeContainer.Spellcaster) != 0;
    }

    /**
     * Checks if this unit type is permanently cloaked. This means the unit type is always
     * cloaked and requires a detector in order to see it.
     *
     * @return true if this unit type is permanently cloaked, and false otherwise.
     */
    public boolean hasPermanentCloak() {
        return (UnitTypeContainer.unitFlags[id] & UnitTypeContainer.PermanentCloak) != 0;
    }

    /**
     * Checks if this unit type is invincible by default. Invincible units
     * cannot take damage.
     *
     * @return true if this unit type is invincible, and false if it is vulnerable to attacks.
     */
    public boolean isInvincible() {
        return (UnitTypeContainer.unitFlags[id] & UnitTypeContainer.Invincible) != 0;
    }

    /**
     * Checks if this unit is an organic unit. The organic property is required for some abilities
     * such as @Heal.
     *
     * @return true if this unit type has the organic property, and false otherwise.
     */
    public boolean isOrganic() {
        return (UnitTypeContainer.unitFlags[id] & UnitTypeContainer.OrganicUnit) != 0;
    }

    /**
     * Checks if this unit is mechanical. The mechanical property is required for some actions
     * such as @Repair.
     *
     * @return true if this unit type has the mechanical property, and false otherwise.
     */
    public boolean isMechanical() {
        return (UnitTypeContainer.unitFlags[id] & UnitTypeContainer.Mechanical) != 0;
    }

    /**
     * Checks if this unit is robotic. The robotic property is applied
     * to robotic units such as the @Probe which prevents them from taking damage from @Irradiate.
     *
     * @return true if this unit type has the robotic property, and false otherwise.
     */
    public boolean isRobotic() {
        return (UnitTypeContainer.unitFlags[id] & UnitTypeContainer.RoboticUnit) != 0;
    }

    /**
     * Checks if this unit type is capable of detecting units that are cloaked or burrowed.
     *
     * @return true if this unit type is a detector by default, false if it does not have this
     * property
     */
    public boolean isDetector() {
        return (UnitTypeContainer.unitFlags[id] & UnitTypeContainer.Detector) != 0;
    }

    /**
     * Checks if this unit type is capable of storing resources such as @minerals. Resources
     * are harvested from resource containers.
     *
     * @return true if this unit type may contain resources that can be harvested, false
     * otherwise.
     */
    public boolean isResourceContainer() {
        return (UnitTypeContainer.unitFlags[id] & UnitTypeContainer.ResourceContainer) != 0;
    }

    /**
     * Checks if this unit type is a resource depot. Resource depots must be placed a certain
     * distance from resources. Resource depots are typically the main building for any
     * particular race. Workers will return resources to the nearest resource depot.
     *
     * Example:
     * @return true if the unit type is a resource depot, false if it is not.
     */
    public boolean isResourceDepot() {
        return (UnitTypeContainer.unitFlags[id] & UnitTypeContainer.ResourceDepot) != 0;
    }

    /**
     * Checks if this unit type is a refinery. A refinery is a structure that is placed on top of
     * a @geyser . Refinery types are @refinery , @extractor , and @assimilator.
     *
     * @return true if this unit type is a refinery, and false if it is not.
     */
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

    /**
     * Checks if this unit type is a worker unit. Worker units can harvest resources and build
     * structures. Worker unit types include the @SCV , @probe, and @drone.
     *
     * @return true if this unit type is a worker, and false if it is not.
     */
    public boolean isWorker() {
        return (UnitTypeContainer.unitFlags[id] & UnitTypeContainer.Worker) != 0;
    }

    /**
     * Checks if this structure is powered by a psi field. Structures powered
     * by psi can only be placed near a @Pylon. If the @Pylon is destroyed, then this unit will
     * lose power.
     *
     * @return true if this unit type can only be placed in a psi field, false otherwise.
     */
    public boolean requiresPsi() {
        return (UnitTypeContainer.unitFlags[id] & UnitTypeContainer.RequiresPsi) != 0;
    }

    /**
     * Checks if this structure must be placed on @Zerg creep.
     *
     * @return true if this unit type requires creep, false otherwise.
     */
    public boolean requiresCreep() {
        return (UnitTypeContainer.unitFlags[id] & UnitTypeContainer.CreepBuilding) != 0;
    }

    /**
     * Checks if this unit type spawns two units when being hatched from an @Egg.
     * This is only applicable to @Zerglings and @Scourges.
     *
     * @return true if morphing this unit type will spawn two of them, and false if only one
     * is spawned.
     */
    public boolean isTwoUnitsInOneEgg() {
        return (UnitTypeContainer.unitFlags[id] & UnitTypeContainer.TwoUnitsIn1Egg) != 0;
    }

    /**
     * Checks if this unit type has the capability to use the @Burrow technology when it
     * is researched.
     *
     * The @Lurker can burrow even without researching the ability.
     * @see TechType#Burrowing
     * @return true if this unit can use the @Burrow ability, and false otherwise.
     */
    public boolean isBurrowable() {
        return (UnitTypeContainer.unitFlags[id] & UnitTypeContainer.Burrowable) != 0;
    }

    /**
     * Checks if this unit type has the capability to use a cloaking ability when it
     * is researched. This applies only to @Wraiths and @Ghosts, and does not include
     * units which are permanently cloaked.
     *
     * @return true if this unit has a cloaking ability, false otherwise.
     * @see #hasPermanentCloak
     * @see TechType#Cloaking_Field
     * @see TechType#Personnel_Cloaking
     */
    public boolean isCloakable() {
        return (UnitTypeContainer.unitFlags[id] & UnitTypeContainer.Cloakable) != 0;
    }

    /**
     * Checks if this unit is a structure. This includes @Mineral_Fields and @Vespene_Geysers.
     *
     * @return true if this unit is a building, and false otherwise.
     */
    public boolean isBuilding() {
        return (UnitTypeContainer.unitFlags[id] & UnitTypeContainer.Building) != 0;
    }

    /**
     * Checks if this unit is an add-on. Add-ons are attachments used by some @Terran structures such as the @Comsat_Station.
     *
     * @return true if this unit is an add-on, and false otherwise.
     */
    public boolean isAddon() {
        return (UnitTypeContainer.unitFlags[id] & UnitTypeContainer.Addon) != 0;
    }

    /**
     * Checks if this structure has the capability to use the lift-off command.
     *
     * @return true if this unit type is a flyable building, false otherwise.
     */
    public boolean isFlyingBuilding() {
        return (UnitTypeContainer.unitFlags[id] & UnitTypeContainer.FlyingBuilding) != 0;
    }

    /**
     * Checks if this unit type is a neutral type, such as critters and resources.
     *
     * @return true if this unit is intended to be neutral, and false otherwise.
     */
    public boolean isNeutral() {
        return getRace() == Race.None &&
                (isCritter() || isResourceContainer() || isSpell());
    }

    /**
     * Checks if this unit type is a hero. Heroes are types that the player
     * cannot obtain normally, and are identified by the white border around their icon when
     * selected with a group.
     *
     * There are two non-hero units included in this set, the @Civilian and @Dark_Templar_Hero.
     *
     * @return true if this unit type is a hero type, and false otherwise.
     */
    public boolean isHero() {
        return ((UnitTypeContainer.unitFlags[id] & UnitTypeContainer.Hero) != 0) ||
                this == Hero_Dark_Templar ||
                this == Terran_Civilian;
    }

    /**
     * Checks if this unit type is a powerup. Powerups can be picked up and
     * carried by workers. They are usually only seen in campaign maps and @Capture_the_flag.
     *
     * @return true if this unit type is a powerup type, and false otherwise.
     */
    public boolean isPowerup() {
        return this == Powerup_Uraj_Crystal ||
                this == Powerup_Khalis_Crystal ||
                (this.id >= Powerup_Flag.id && this.id < None.id);
    }

    /**
     * Checks if this unit type is a beacon. Each race has exactly one beacon
     * each. They are {@link UnitType#Special_Zerg_Beacon}, {@link UnitType#Special_Terran_Beacon}, and
     * {@link UnitType#Special_Protoss_Beacon}.
     *
     * @see #isFlagBeacon
     * @return true if this unit type is one of the three race beacons, and false otherwise.
     */
    public boolean isBeacon() {
        return this == Special_Zerg_Beacon ||
                this == Special_Terran_Beacon ||
                this == Special_Protoss_Beacon;
    }

    /**
     * Checks if this unit type is a flag beacon. Each race has exactly one
     * flag beacon each. They are {@link UnitType#Special_Zerg_Flag_Beacon},
     * {@link UnitType#Special_Terran_Flag_Beacon}, and {@link UnitType#Special_Protoss_Flag_Beacon}.
     * Flag beacons spawn a @Flag after some ARBITRARY I FORGOT AMOUNT OF FRAMES.
     *
     * @see #isBeacon
     * @return true if this unit type is one of the three race flag beacons, and false otherwise.
     */
    public boolean isFlagBeacon() {
        return this == Special_Zerg_Flag_Beacon ||
                this == Special_Terran_Flag_Beacon ||
                this == Special_Protoss_Flag_Beacon;
    }

    /**
     * Checks if this structure is special and cannot be obtained normally within the
     * game.
     *
     * @return true if this structure is a special building, and false otherwise.
     */
    public boolean isSpecialBuilding() {
        return isBuilding() &&
                whatBuilds().getValue() == 0 &&
                this != Zerg_Infested_Command_Center;
    }

    /**
     * Identifies if this unit type is used to complement some @abilities.
     * These include {@link UnitType#Spell_Dark_Swarm}, {@link UnitType#Spell_Disruption_Web}, and
     * {@link UnitType#Spell_Scanner_Sweep}, which correspond to {@link TechType#Dark_Swarm},
     * {@link TechType#Disruption_Web}, and {@link TechType#Scanner_Sweep} respectively.
     *
     * @return true if this unit type is used for an ability, and false otherwise.
     */
    public boolean isSpell() {
        return this == Spell_Dark_Swarm ||
                this == Spell_Disruption_Web ||
                this == Spell_Scanner_Sweep;
    }

    /**
     * Checks if this structure type produces creep. That is, the unit type
     * spreads creep over a wide area so that @Zerg structures can be placed on it.
     *
     * @return true if this unit type spreads creep.
     *
     * @since 4.1.2
     */
    public boolean producesCreep() {
        return producesLarva() ||
                this == Zerg_Creep_Colony ||
                this == Zerg_Spore_Colony ||
                this == Zerg_Sunken_Colony;
    }

    /**
     * Checks if this unit type produces larva. This is essentially used to
     * check if the unit type is a @Hatchery, @Lair, or @Hive.
     *
     * @return true if this unit type produces larva.
     */
    public boolean producesLarva() {
        return this == Zerg_Hatchery ||
                this == Zerg_Lair ||
                this == Zerg_Hive;
    }

    /**
     * Checks if this unit type is a mineral field and contains a resource amount.
     * This indicates that the unit type is either {@link UnitType#Resource_Mineral_Field},
     * {@link UnitType#Resource_Mineral_Field_Type_2}, or {@link UnitType#Resource_Mineral_Field_Type_3}.
     *
     * @return true if this unit type is a mineral field resource.
     */
    public boolean isMineralField() {
        return this == Resource_Mineral_Field ||
                this == Resource_Mineral_Field_Type_2 ||
                this == Resource_Mineral_Field_Type_3;
    }

    /**
     * Checks if this unit type is a neutral critter.
     *
     * @return true if this unit type is a critter, and false otherwise.
     *
     */
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

    /**
     * Checks if this unit type is capable of constructing an add-on. An add-on is an extension
     * or attachment for <em>Terran</em> structures, specifically the <em>Command_Center</em>, <em>Factory</em>,
     * <em>Starport</em>, and <em>Science_Facility</em>.
     *
     * @return true if this unit type can construct an add-on, and false if it can not.
     * @see #isAddon
     */
    public boolean canBuildAddon() {
        return this == Terran_Command_Center ||
                this == Terran_Factory ||
                this == Terran_Starport ||
                this == Terran_Science_Facility;
    }

    /**
     * Retrieves the set of units that this unit type is capable of creating.
     * This includes training, constructing, warping, and morphing.
     *
     * Some maps have special parameters that disable construction of units that are otherwise
     * normally available. Use {@link Player#isUnitAvailable} to determine if a unit type is
     * actually available in the current game for a specific player.
     *
     * @return List of UnitTypes containing the units it can build.
     * @see Player#isUnitAvailable
     *
     * @since 4.1.2
     */
    public List<UnitType> buildsWhat() {
        return Collections.unmodifiableList(Arrays.asList(UnitTypeContainer.buildsWhat[id]));
    }

    /**
     * Retrieves the set of technologies that this unit type is capable of researching.
     *
     * Some maps have special parameters that disable certain technologies. Use
     * {@link Player#isResearchAvailable} to determine if a technology is actually available in the
     * current game for a specific player.
     *
     * @return List of TechTypes containing the technology types that can be researched.
     * @see Player#isResearchAvailable
     *
     * @since 4.1.2
     */
    public List<TechType> researchesWhat() {
        return Collections.unmodifiableList(Arrays.asList(UnitTypeContainer.researchesWhat[id]));
    }

    /**
     * Retrieves the set of upgrades that this unit type is capable of upgrading.
     *
     * Some maps have special upgrade limitations. Use {@link Player#getMaxUpgradeLevel}
     * to check if an upgrade is available.
     *
     * @return List of UpgradeTypes containing the upgrade types that can be upgraded.
     * @see Player#getMaxUpgradeLevel
     *
     * @since 4.1.2
     */
    public List<UpgradeType> upgradesWhat() {
        return Collections.unmodifiableList(Arrays.asList(UnitTypeContainer.upgradesWhat[id]));
    }

    /**
     * Checks if the current type is equal to the provided type, or a successor of the
     * provided type. For example, a Hive is a successor of a Hatchery, since it can
     * still research the @Burrow technology.
     *
     * @param type The unit type to check.
     *
     * @see TechType#whatResearches()
     * @see UpgradeType#whatUpgrades()
     * @since 4.2.0
     */
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
