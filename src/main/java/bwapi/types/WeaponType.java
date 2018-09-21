package bwapi.types;


import static bwapi.types.DamageType.*;
import static bwapi.types.ExplosionType.*;
import static bwapi.types.UnitType.*;
import static bwapi.types.UpgradeType.*;

public enum WeaponType {
    Gauss_Rifle(0),
    Gauss_Rifle_Jim_Raynor(1),
    C_10_Canister_Rifle(2),
    C_10_Canister_Rifle_Sarah_Kerrigan(3),
    Fragmentation_Grenade(4),
    Fragmentation_Grenade_Jim_Raynor(5),
    Spider_Mines(6),
    Twin_Autocannons(7),
    Hellfire_Missile_Pack(8),
    Twin_Autocannons_Alan_Schezar(9),
    Hellfire_Missile_Pack_Alan_Schezar(10),
    Arclite_Cannon(11),
    Arclite_Cannon_Edmund_Duke(12),
    Fusion_Cutter(13),

    Gemini_Missiles(15),
    Burst_Lasers(16),
    Gemini_Missiles_Tom_Kazansky(17),
    Burst_Lasers_Tom_Kazansky(18),
    ATS_Laser_Battery(19),
    ATA_Laser_Battery(20),
    ATS_Laser_Battery_Hero(21),
    ATA_Laser_Battery_Hero(22),
    ATS_Laser_Battery_Hyperion(23),
    ATA_Laser_Battery_Hyperion(24),
    Flame_Thrower(25),
    Flame_Thrower_Gui_Montag(26),
    Arclite_Shock_Cannon(27),
    Arclite_Shock_Cannon_Edmund_Duke(28),
    Longbolt_Missile(29),
    Yamato_Gun(30),
    Nuclear_Strike(31),
    Lockdown(32),
    EMP_Shockwave(33),
    Irradiate(34),
    Claws(35),
    Claws_Devouring_One(36),
    Claws_Infested_Kerrigan(37),
    Needle_Spines(38),
    Needle_Spines_Hunter_Killer(39),
    Kaiser_Blades(40),
    Kaiser_Blades_Torrasque(41),
    Toxic_Spores(42),
    Spines(43),

    Acid_Spore(46),
    Acid_Spore_Kukulza(47),
    Glave_Wurm(48),
    Glave_Wurm_Kukulza(49),

    Seeker_Spores(52),
    Subterranean_Tentacle(53),
    Suicide_Infested_Terran(54),
    Suicide_Scourge(55),
    Parasite(56),
    Spawn_Broodlings(57),
    Ensnare(58),
    Dark_Swarm(59),
    Plague(60),
    Consume(61),
    Particle_Beam(62),

    Psi_Blades(64),
    Psi_Blades_Fenix(65),
    Phase_Disruptor(66),
    Phase_Disruptor_Fenix(67),

    Psi_Assault(69),
    Psionic_Shockwave(70),
    Psionic_Shockwave_TZ_Archon(71),

    Dual_Photon_Blasters(73),
    Anti_Matter_Missiles(74),
    Dual_Photon_Blasters_Mojo(75),
    Anti_Matter_Missiles_Mojo(76),
    Phase_Disruptor_Cannon(77),
    Phase_Disruptor_Cannon_Danimoth(78),
    Pulse_Cannon(79),
    STS_Photon_Cannon(80),
    STA_Photon_Cannon(81),
    Scarab(82),
    Stasis_Field(83),
    Psionic_Storm(84),
    Warp_Blades_Zeratul(85),
    Warp_Blades_Hero(86),

    Platform_Laser_Battery(92),
    Independant_Laser_Battery(93),

    Twin_Autocannons_Floor_Trap(96),
    Hellfire_Missile_Pack_Wall_Trap(97),
    Flame_Thrower_Wall_Trap(98),
    Hellfire_Missile_Pack_Floor_Trap(99),

    Neutron_Flare(100),
    Disruption_Web(101),
    Restoration(102),
    Halo_Rockets(103),
    Corrosive_Acid(104),
    Mind_Control(105),
    Feedback(106),
    Optical_Flare(107),
    Maelstrom(108),
    Subterranean_Spines(109),

    Warp_Blades(111),
    C_10_Canister_Rifle_Samir_Duran(112),
    C_10_Canister_Rifle_Infested_Duran(113),
    Dual_Photon_Blasters_Artanis(114),
    Anti_Matter_Missiles_Artanis(115),
    C_10_Canister_Rifle_Alexei_Stukov(116),

    None(130),
    Unknown(131);

    private int id;

    WeaponType(int id) {
        this.id = id;
    }

    public TechType getTech() {
        return attachedTech[id];
    }

    public UnitType whatUses() {
        return whatUses[id];
    }

    public int damageAmount() {
        return defaultWpnDamageAmt[id];
    }

    public int damageBonus() {
        return defaultWpnDamageBonus[id];
    }

    public int damageCooldown() {
        return wpnDamageCooldowns[id];
    }

    public int damageFactor() {
        return wpnDamageFactor[id];
    }

    public UpgradeType upgradeType() {
        return upgrade[id];
    }

    public DamageType damageType() {
        return damageType[id];
    }

    public ExplosionType explosionType() {
        return explosionType[id];
    }

    public int minRange() {
        return wpnMinRange[id];
    }

    public int maxRange() {
        return wpnMaxRange[id];
    }

    public int innerSplashRadius() {
        return wpnSplashRangeInner[id];
    }

    public int medianSplashRadius() {
        return wpnSplashRangeMid[id];
    }

    public int outerSplashRadius() {
        return wpnSplashRangeOuter[id];
    }

    public boolean targetsAir() {
        return (wpnFlags[id] & TARG_AIR) != 0;
    }

    public boolean targetsGround() {
        return (wpnFlags[id] & TARG_GROUND) != 0;
    }

    public boolean targetsMechanical() {
        return (wpnFlags[id] & TARG_MECH) != 0;
    }

    public boolean targetsOrganic() {
        return (wpnFlags[id] & TARG_ORGANIC) != 0;
    }

    public boolean targetsNonBuilding() {
        return (wpnFlags[id] & TARG_NOBUILD) != 0;
    }

    public boolean targetsNonRobotic() {
        return (wpnFlags[id] & TARG_NOROBOT) != 0;
    }

    public boolean targetsTerrain() {
        return (wpnFlags[id] & TARG_TERRAIN) != 0;
    }

    public boolean targetsOrgOrMech() {
        return (wpnFlags[id] & TARG_ORGMECH) != 0;
    }

    public boolean targetsOwn() {
        return (wpnFlags[id] & TARG_OWN) != 0;
    }

    private static int defaultWpnDamageAmt[] = {
            6, 18, 10, 30, 20, 30, 125, 12, 10, 24, 20, 30, 70, 5, 0, 20, 8, 40, 16, 25, 25, 50, 50, 30, 30, 8, 16, 70,
            150, 20, 260, 600, 0, 0, 250, 5, 10, 50, 10, 20, 20, 50, 4, 5, 0, 30, 20, 40, 9, 18, 5, 10, 15, 40, 500, 110,
            0, 0, 0, 0, 300, 0, 5, 0, 8, 20, 20, 45, 5, 20, 30, 60, 4, 8, 14, 20, 28, 10, 20, 6, 20, 20, 100, 0, 14, 100,
            45, 7, 7, 7, 7, 7, 7, 7, 4, 30, 10, 10, 8, 10, 5, 0, 20, 6, 25, 8, 8, 8, 0, 20, 6, 40, 25, 25, 20, 28, 30, 6,
            6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 0, 0
    };
    private static int defaultWpnDamageBonus[] = {
            1, 1, 1, 1, 2, 2, 0, 1, 2, 1, 1, 3, 3, 1, 0, 2, 1, 2, 1, 3, 3, 3, 3, 3, 3, 1, 1, 5, 5, 0, 0, 0, 0, 0, 0,
            1, 1, 1, 1, 1, 3, 3, 1, 0, 0, 1, 2, 2, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 2, 2, 1, 1,
            3, 3, 0, 1, 1, 1, 1, 1, 1, 1, 0, 0, 25, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 1, 2,
            1, 1, 1, 1, 2, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0
    };

    private static int wpnDamageCooldowns[] ={
            15, 15, 22, 22, 30, 22, 22, 22, 22, 22, 22, 37, 37, 15, 15, 22, 30, 22, 30, 30, 30, 30, 30, 22, 22, 22, 22,
            75, 75, 15, 15, 1, 1, 1, 75, 8, 8, 15, 15, 15, 15, 15, 15, 22, 22, 22, 30, 30, 30, 30, 22, 22, 15, 32, 1, 1,
            1, 1, 1, 1, 1, 1, 22, 22, 22, 22, 30, 22, 30, 22, 20, 20, 22, 30, 22, 30, 22, 45, 45, 1, 22, 22, 1, 1, 45, 22,
            30, 22, 22, 22, 22, 22, 22, 22, 9, 22, 22, 22, 22, 22, 8, 22, 22, 64, 100, 22, 22, 22, 1, 37, 15, 30, 22, 22,
            30, 22, 22, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 15, 0, 0
    };
    private static int wpnDamageFactor[] = {
            1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
            2, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 2, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1,
            1, 1, 1, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0
    };
    private static int wpnMinRange[] = {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 64, 64, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };
    private static int wpnMaxRange[] = {
            128, 160, 224, 192, 160, 160, 10, 192, 160, 160, 160, 224, 224, 10, 10, 160, 160, 160, 160, 192, 192, 192, 192,
            192, 192, 32, 32, 384, 384, 224, 320, 3, 256, 256, 288, 15, 15, 15, 128, 160, 25, 25, 2, 32, 128, 64, 256, 256,
            96, 96, 128, 128, 224, 224, 3, 3, 384, 288, 288, 288, 288, 16, 32, 10, 15, 15, 128, 128, 96, 96, 64, 64, 32,
            128, 128, 128, 128, 160, 160, 128, 224, 224, 128, 288, 288, 15, 15, 128, 128, 32, 128, 224, 224, 128, 160, 192,
            160, 160, 64, 160, 160, 288, 192, 192, 192, 256, 320, 288, 320, 192, 128, 15, 192, 192, 128, 128, 192, 128, 128,
            128, 128, 128, 128, 128, 128, 128, 128, 128, 128, 128, 0, 0
    };
    private static int wpnSplashRangeInner[] = {
            0, 0, 0, 0, 0, 0, 50, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 15, 15, 10, 10, 0, 0, 128, 0, 64, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 10, 10, 0, 0, 20, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 3,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20, 0, 48, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 15, 0, 5, 0, 0, 5, 0, 0, 0, 0, 0,
             20, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };
    private static int wpnSplashRangeMid[] = {
            0, 0, 0, 0, 0, 0, 75, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20, 20, 25, 25, 0, 0, 192, 0, 64, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20, 20, 0, 0, 40, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 15, 15, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 40, 0, 48, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 20, 0, 50, 0, 0, 50, 0, 0, 0, 0, 0, 20,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };
    private static int wpnSplashRangeOuter[] = {
            0, 0, 0, 0, 0, 0, 100, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 25, 25, 40, 40, 0, 0, 256, 0, 64, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 30, 30, 0, 0, 60, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 30, 30, 0, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 60, 0, 48, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 25, 0, 100, 0, 0, 100, 0, 0, 0, 0, 0, 20, 0, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };

    private static int TARG_AIR =     0x01;
    private static int TARG_GROUND =  0x02;
    private static int TARG_MECH =    0x04;
    private static int TARG_ORGANIC = 0x08;
    private static int TARG_NOBUILD = 0x10;
    private static int TARG_NOROBOT = 0x20;
    private static int TARG_TERRAIN = 0x40;
    private static int TARG_ORGMECH = 0x80;
    private static int TARG_OWN =     0x100;

    private static int wpnFlags[] = {
            TARG_AIR | TARG_GROUND,
            TARG_AIR | TARG_GROUND,
            TARG_AIR | TARG_GROUND,
            TARG_AIR | TARG_GROUND,
            TARG_GROUND,
            TARG_GROUND,
            TARG_GROUND | TARG_NOBUILD,
            TARG_GROUND,
            TARG_AIR,
            TARG_GROUND,
            TARG_AIR,
            TARG_GROUND,
            TARG_GROUND,
            TARG_GROUND,
            TARG_GROUND,
            TARG_AIR,
            TARG_GROUND,
            TARG_AIR,
            TARG_GROUND,
            TARG_GROUND,
            TARG_AIR,
            TARG_GROUND,
            TARG_AIR,
            TARG_GROUND,
            TARG_AIR,
            TARG_GROUND,
            TARG_GROUND,
            TARG_GROUND,
            TARG_GROUND,
            TARG_AIR,
            TARG_AIR | TARG_GROUND,
            TARG_AIR | TARG_GROUND,
            TARG_AIR | TARG_GROUND | TARG_MECH | TARG_NOBUILD,
            TARG_AIR | TARG_GROUND | TARG_TERRAIN,
            TARG_AIR | TARG_GROUND | TARG_TERRAIN,
            TARG_GROUND,
            TARG_GROUND,
            TARG_GROUND,
            TARG_AIR | TARG_GROUND,
            TARG_AIR | TARG_GROUND,
            TARG_GROUND,
            TARG_GROUND,
            TARG_GROUND,
            TARG_GROUND,
            TARG_GROUND,
            TARG_AIR | TARG_GROUND,
            TARG_GROUND,
            TARG_GROUND,
            TARG_AIR | TARG_GROUND,
            TARG_AIR | TARG_GROUND,
            TARG_GROUND,
            TARG_GROUND,
            TARG_AIR,
            TARG_GROUND,
            TARG_GROUND,
            TARG_AIR,
            TARG_AIR | TARG_GROUND | TARG_NOBUILD,
            TARG_GROUND | TARG_NOBUILD | TARG_NOROBOT | TARG_ORGMECH,
            TARG_AIR | TARG_GROUND | TARG_TERRAIN,
            TARG_AIR | TARG_GROUND,
            TARG_AIR | TARG_GROUND | TARG_TERRAIN,
            TARG_AIR | TARG_GROUND | TARG_ORGANIC | TARG_NOBUILD | TARG_OWN,
            TARG_GROUND,
            TARG_GROUND,
            TARG_GROUND,
            TARG_GROUND,
            TARG_AIR | TARG_GROUND,
            TARG_AIR | TARG_GROUND,
            TARG_GROUND,
            TARG_GROUND,
            TARG_AIR | TARG_GROUND,
            TARG_AIR | TARG_GROUND,
            TARG_AIR,
            TARG_GROUND,
            TARG_AIR,
            TARG_GROUND,
            TARG_AIR,
            TARG_AIR | TARG_GROUND,
            TARG_AIR | TARG_GROUND,
            TARG_AIR | TARG_GROUND,
            TARG_GROUND,
            TARG_AIR,
            TARG_GROUND,
            TARG_AIR | TARG_GROUND | TARG_TERRAIN,
            TARG_AIR | TARG_GROUND | TARG_NOBUILD | TARG_TERRAIN,
            TARG_GROUND,
            TARG_GROUND,
            TARG_AIR,
            TARG_AIR,
            TARG_AIR,
            TARG_GROUND,
            TARG_AIR,
            TARG_AIR,
            TARG_AIR,
            TARG_AIR,
            TARG_GROUND,
            TARG_GROUND,
            TARG_GROUND,
            TARG_GROUND,
            TARG_GROUND,
            TARG_AIR,
            TARG_GROUND,
            TARG_AIR | TARG_GROUND | TARG_TERRAIN,
            TARG_AIR,
            TARG_AIR,
            TARG_AIR | TARG_GROUND | TARG_TERRAIN,
            TARG_AIR | TARG_GROUND | TARG_TERRAIN,
            TARG_GROUND,
            TARG_AIR | TARG_GROUND | TARG_TERRAIN,
            TARG_GROUND,
            TARG_AIR | TARG_GROUND,
            TARG_GROUND,
            TARG_AIR | TARG_GROUND,
            TARG_AIR | TARG_GROUND,
            TARG_GROUND,
            TARG_AIR,
            TARG_AIR | TARG_GROUND,
            TARG_AIR | TARG_GROUND,
            TARG_AIR | TARG_GROUND,
            TARG_AIR | TARG_GROUND,
            TARG_AIR | TARG_GROUND,
            TARG_AIR | TARG_GROUND,
            TARG_AIR | TARG_GROUND,
            TARG_AIR | TARG_GROUND,
            TARG_AIR | TARG_GROUND,
            TARG_AIR | TARG_GROUND,
            TARG_AIR | TARG_GROUND,
            TARG_AIR | TARG_GROUND,
            TARG_AIR | TARG_GROUND,
            TARG_AIR | TARG_GROUND,
            0, 0
    };

    private static UpgradeType upgrade[] = {
            Terran_Infantry_Weapons, Terran_Infantry_Weapons, Terran_Infantry_Weapons, Terran_Infantry_Weapons, Terran_Vehicle_Weapons,
            Terran_Vehicle_Weapons, Upgrade_60, Terran_Vehicle_Weapons, Terran_Vehicle_Weapons, Terran_Vehicle_Weapons, Terran_Vehicle_Weapons,
            Terran_Vehicle_Weapons, Terran_Vehicle_Weapons, Upgrade_60, Upgrade_60, Terran_Ship_Weapons, Terran_Ship_Weapons, Terran_Ship_Weapons,
            Terran_Ship_Weapons, Terran_Ship_Weapons, Terran_Ship_Weapons, Terran_Ship_Weapons, Terran_Ship_Weapons, Terran_Ship_Weapons,
            Terran_Ship_Weapons, Terran_Infantry_Weapons, Terran_Infantry_Weapons, Terran_Vehicle_Weapons, Terran_Vehicle_Weapons, Upgrade_60,
            Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Zerg_Melee_Attacks, Zerg_Melee_Attacks, Zerg_Melee_Attacks,
            Zerg_Missile_Attacks, Zerg_Missile_Attacks, Zerg_Melee_Attacks, Zerg_Melee_Attacks, Zerg_Melee_Attacks, Upgrade_60, Upgrade_60,
            Zerg_Flyer_Attacks, Zerg_Flyer_Attacks, Zerg_Flyer_Attacks, Zerg_Flyer_Attacks, Zerg_Flyer_Attacks, Zerg_Missile_Attacks,
            Zerg_Missile_Attacks, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60,
            Upgrade_60, Upgrade_60, Upgrade_60, Protoss_Ground_Weapons, Protoss_Ground_Weapons, Protoss_Ground_Weapons, Protoss_Ground_Weapons,
            Protoss_Ground_Weapons, Protoss_Ground_Weapons, Protoss_Ground_Weapons, Protoss_Ground_Weapons, Protoss_Air_Weapons, Protoss_Air_Weapons,
            Protoss_Air_Weapons, Protoss_Air_Weapons, Protoss_Air_Weapons, Protoss_Air_Weapons, Protoss_Air_Weapons, Protoss_Air_Weapons, Upgrade_60,
            Upgrade_60, Scarab_Damage, Upgrade_60, Upgrade_60, Protoss_Ground_Weapons, Protoss_Ground_Weapons, Upgrade_60, Upgrade_60, Upgrade_60,
            Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Terran_Vehicle_Weapons, Terran_Vehicle_Weapons,
            Terran_Infantry_Weapons, Terran_Vehicle_Weapons, Protoss_Air_Weapons, Upgrade_60, Upgrade_60, Terran_Ship_Weapons, Zerg_Flyer_Attacks,
            Upgrade_60, Upgrade_60, Upgrade_60, Upgrade_60, Zerg_Missile_Attacks, Terran_Infantry_Weapons, Protoss_Ground_Weapons,
            Terran_Infantry_Weapons, Terran_Infantry_Weapons, Protoss_Air_Weapons, Protoss_Air_Weapons, Terran_Infantry_Weapons,
            Terran_Infantry_Weapons, Terran_Infantry_Weapons, Terran_Infantry_Weapons, Terran_Infantry_Weapons, Terran_Infantry_Weapons,
            Terran_Infantry_Weapons, Terran_Infantry_Weapons, Terran_Infantry_Weapons, Terran_Infantry_Weapons, Terran_Infantry_Weapons,
            Terran_Infantry_Weapons, Terran_Infantry_Weapons, Terran_Infantry_Weapons, UpgradeType.None, UpgradeType.Unknown
    };

    private static DamageType damageType[] = {
            DamageType.Normal, DamageType.Normal, Concussive, Concussive, Concussive, Concussive, Explosive, DamageType.Normal, Explosive, DamageType.Normal, Explosive, Explosive,
            Explosive, DamageType.Normal, DamageType.Normal, Explosive, DamageType.Normal, Explosive, DamageType.Normal, DamageType.Normal, DamageType.Normal, DamageType.Normal, DamageType.Normal, DamageType.Normal, DamageType.Normal, Concussive,
            Concussive, Explosive, Explosive, Explosive, Explosive, Explosive, Concussive, Concussive, Ignore_Armor, DamageType.Normal, DamageType.Normal, DamageType.Normal,
            Explosive, Explosive, DamageType.Normal, DamageType.Normal, DamageType.Normal, DamageType.Normal, DamageType.Normal, Explosive, DamageType.Normal, DamageType.Normal, DamageType.Normal, DamageType.Normal, Concussive, Concussive,
            DamageType.Normal, Explosive, Explosive, DamageType.Normal, Independent, Independent, Independent, Independent, Independent, Independent, DamageType.Normal, DamageType.Normal,
            DamageType.Normal, DamageType.Normal, Explosive, Explosive, DamageType.Normal, DamageType.Normal, DamageType.Normal, DamageType.Normal, Explosive, DamageType.Normal, Explosive, DamageType.Normal, Explosive, Explosive,
            Explosive, DamageType.Normal, DamageType.Normal, DamageType.Normal, DamageType.Normal, Independent, Ignore_Armor, DamageType.Normal, DamageType.Normal, Explosive, DamageType.Normal, Explosive, Explosive,
            DamageType.Normal, DamageType.Normal, DamageType.Normal, DamageType.Normal, DamageType.Normal, DamageType.Normal, Explosive, Concussive, Explosive, Explosive, Ignore_Armor, Ignore_Armor, Explosive,
            Explosive, DamageType.Normal, Ignore_Armor, Independent, Independent, DamageType.Normal, DamageType.Normal, DamageType.Normal, Concussive, Concussive, DamageType.Normal, Explosive,
            Concussive, DamageType.Normal, DamageType.Normal, DamageType.Normal, DamageType.Normal, DamageType.Normal, DamageType.Normal, DamageType.Normal, DamageType.Normal, DamageType.Normal, DamageType.Normal, DamageType.Normal, DamageType.Normal, DamageType.Normal, DamageType.None, DamageType.Unknown
    };
    private static ExplosionType explosionType[] = {
            ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, Radial_Splash, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.None,
            ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, Enemy_Splash, Enemy_Splash, Radial_Splash,
            Radial_Splash, ExplosionType.Normal, ExplosionType.Yamato_Gun, Nuclear_Missile, ExplosionType.Lockdown, ExplosionType.EMP_Shockwave, ExplosionType.Irradiate, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal,
            ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, Radial_Splash, Radial_Splash, ExplosionType.Normal, ExplosionType.Normal,
            Radial_Splash, ExplosionType.Normal, ExplosionType.Parasite, Broodlings, ExplosionType.Ensnare, ExplosionType.Dark_Swarm, ExplosionType.Plague, ExplosionType.Consume, ExplosionType.Normal, ExplosionType.None, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal,
            ExplosionType.Normal, ExplosionType.Normal, Enemy_Splash, Enemy_Splash, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal,
            Enemy_Splash, ExplosionType.Stasis_Field, Radial_Splash, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal,
            ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, Enemy_Splash, ExplosionType.Normal, Air_Splash, ExplosionType.Disruption_Web, ExplosionType.Restoration, Air_Splash, ExplosionType.Corrosive_Acid, ExplosionType.Mind_Control,
            ExplosionType.Feedback, ExplosionType.Optical_Flare, ExplosionType.Maelstrom, Enemy_Splash, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal,
            ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.Normal, ExplosionType.None, ExplosionType.Unknown
    };
    
    private static UnitType whatUses[] = {
            Terran_Marine, Hero_Jim_Raynor_Marine, Terran_Ghost, Hero_Sarah_Kerrigan, Terran_Vulture, Hero_Jim_Raynor_Vulture,
            Terran_Vulture_Spider_Mine, Terran_Goliath, Terran_Goliath, Hero_Alan_Schezar, Hero_Alan_Schezar, Terran_Siege_Tank_Tank_Mode,
            Hero_Edmund_Duke_Tank_Mode, Terran_SCV, Terran_SCV, Terran_Wraith, Terran_Wraith, Hero_Tom_Kazansky, Hero_Tom_Kazansky,
            Terran_Battlecruiser, Terran_Battlecruiser, Hero_Norad_II, Hero_Norad_II, Hero_Hyperion, Hero_Hyperion, Terran_Firebat,
            Hero_Gui_Montag, Terran_Siege_Tank_Siege_Mode, Hero_Edmund_Duke_Siege_Mode, Terran_Missile_Turret, Terran_Battlecruiser,
            Terran_Ghost, Terran_Ghost, Terran_Science_Vessel, Terran_Science_Vessel, Zerg_Zergling, Hero_Devouring_One, Hero_Infested_Kerrigan,
            Zerg_Hydralisk, Hero_Hunter_Killer, Zerg_Ultralisk, Hero_Torrasque, Zerg_Broodling, Zerg_Drone, Zerg_Drone, UnitType.None, Zerg_Guardian,
            Hero_Kukulza_Guardian, Zerg_Mutalisk, Hero_Kukulza_Mutalisk, UnitType.None, UnitType.None, Zerg_Spore_Colony, Zerg_Sunken_Colony, Zerg_Infested_Terran,
            Zerg_Scourge, Zerg_Queen, Zerg_Queen, Zerg_Queen, Zerg_Defiler, Zerg_Defiler, Zerg_Defiler, Protoss_Probe, Protoss_Probe,
            Protoss_Zealot, Hero_Fenix_Zealot, Protoss_Dragoon, Hero_Fenix_Dragoon, UnitType.None, Hero_Tassadar, Protoss_Archon, Hero_Tassadar_Zeratul_Archon,
            UnitType.None, Protoss_Scout, Protoss_Scout, Hero_Mojo, Hero_Mojo, Protoss_Arbiter, Hero_Danimoth, Protoss_Interceptor, Protoss_Photon_Cannon,
            Protoss_Photon_Cannon, Protoss_Scarab, Protoss_Arbiter, Protoss_High_Templar, Hero_Zeratul, Hero_Dark_Templar, UnitType.None, UnitType.None, UnitType.None, UnitType.None,
            UnitType.None, UnitType.None, Special_Independant_Starport, UnitType.None, UnitType.None, Special_Floor_Gun_Trap, Special_Wall_Missile_Trap, Special_Wall_Flame_Trap,
            Special_Floor_Missile_Trap, Protoss_Corsair, Protoss_Corsair, Terran_Medic, Terran_Valkyrie, Zerg_Devourer, Protoss_Dark_Archon,
            Protoss_Dark_Archon, Terran_Medic, Protoss_Dark_Archon, Zerg_Lurker, UnitType.None, Protoss_Dark_Templar, Hero_Samir_Duran, Hero_Infested_Duran,
            Hero_Artanis, Hero_Artanis, Hero_Alexei_Stukov, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None, UnitType.None,
            UnitType.None, UnitType.Unknown
    };
    
    private static TechType attachedTech[] = {
            // Terran
            TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.Spider_Mines, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None,
            TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.Yamato_Gun, TechType.Nuclear_Strike, TechType.Lockdown, TechType.EMP_Shockwave, TechType.Irradiate,
            // Zerg
            TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None,  TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None,
            TechType.None, TechType.Parasite, TechType.Spawn_Broodlings, TechType.Ensnare, TechType.Dark_Swarm, TechType.Plague, TechType.Consume,
            // Protoss
            TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None,
            TechType.None, TechType.Stasis_Field, TechType.Psionic_Storm, TechType.None, TechType.None,
            // Other
            TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None,
            // Expansion
            TechType.None, TechType.Disruption_Web, TechType.Restoration, TechType.None, TechType.None, TechType.Mind_Control, TechType.Feedback, TechType.Optical_Flare, TechType.Maelstrom, TechType.None, TechType.None, TechType.None, TechType.None,
            TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None, TechType.None
    };
}
