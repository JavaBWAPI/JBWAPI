package bwapi.types;

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

    private int value;

    public int getValue(){
        return value;
    }

    TechType(int value) {
        this.value = value;
    }

    public Race getRace() {
        return null;
    }

    public int mineralPrice() {
        return -1;
    }

    public int gasPrice() {
        return -1;
    }

    public int researchTime() {
        return -1;
    }

    public int energyCost() {
        return -1;
    }

    public UnitType whatResearches() {
        return null;
    }

    public WeaponType getWeapon() {
        return null;
    }

    public boolean targetsUnit() {
        return false;
    }

    public boolean targetsPosition() {
        return false;
    }

    public Order getOrder() {
        return null;
    }

    public UnitType requiredUnit() {
        return null;
    }

    /// IMPLEMENTATION
    private static int defaultOreCost[] =         // Same as default gas cost
    { 100, 200, 200, 100, 0, 150, 0, 200, 100, 150, 100, 100, 0, 100, 0, 200, 100, 100, 0, 200, 150, 150, 150, 0, 100, 200, 0, 200, 0, 100, 100, 100, 200 };
    private static int defaultTimeCost[] =
    { 1200, 1500, 1800, 1200, 0, 1200, 0, 1200, 1800, 1500, 1200, 1200, 0, 1200, 0, 1500, 1500, 1200, 0, 1800, 1200, 1800, 1500, 0, 1200, 1200, 0, 1800, 0, 1800, 1800, 1500, 1800 };
    private static int defaultEnergyCost[] =
    { 0, 100, 100, 0, 50, 0, 100, 75, 150, 25, 25, 0, 0, 150, 100, 150, 0, 75, 75, 75, 100, 150, 100, 0, 50, 125, 0, 150, 0, 50, 75, 100, 0, 0, 1 };

//    static const int whatResearches[] =
//    { Terran_Academy, Terran_Covert_Ops, Terran_Science_Facility, Terran_Machine_Shop,
//            None, Terran_Machine_Shop, None, Terran_Science_Facility, Terran_Physics_Lab,
//            Terran_Control_Tower, Terran_Covert_Ops, Zerg_Hatchery, None, Zerg_Queens_Nest,
//            None, Zerg_Defiler_Mound, Zerg_Defiler_Mound, Zerg_Queens_Nest, None,
//            Protoss_Templar_Archives, Protoss_Templar_Archives, Protoss_Arbiter_Tribunal,
//            Protoss_Arbiter_Tribunal, None, Terran_Academy, Protoss_Fleet_Beacon, None,
//            Protoss_Templar_Archives, None, None, Terran_Academy, Protoss_Templar_Archives,
//            Zerg_Hydralisk_Den, None, None, None, None, None, None, None, None, None, None, None,
//            None, None, Unknown
//    };
}
