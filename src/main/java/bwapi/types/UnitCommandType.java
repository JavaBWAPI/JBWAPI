package bwapi.types;

public enum UnitCommandType {
    Attack_Move(0),
    Attack_Unit(1),
    Build(2),
    Build_Addon(3),
    Train(4),
    Morph(5),
    Research(6),
    Upgrade(7),
    Set_Rally_Position(8),
    Set_Rally_Unit(9),
    Move(10),
    Patrol(11),
    Hold_Position(12),
    Stop(13),
    Follow(14),
    Gather(15),
    Return_Cargo(16),
    Repair(17),
    Burrow(18),
    Unburrow(19),
    Cloak(20),
    Decloak(21),
    Siege(22),
    Unsiege(23),
    Lift(24),
    Land(25),
    Load(26),
    Unload(27),
    Unload_All(28),
    Unload_All_Position(29),
    Right_Click_Position(30),
    Right_Click_Unit(31),
    Halt_Construction(32),
    Cancel_Construction(33),
    Cancel_Addon(34),
    Cancel_Train(35),
    Cancel_Train_Slot(36),
    Cancel_Morph(37),
    Cancel_Research(38),
    Cancel_Upgrade(39),
    Use_Tech(40),
    Use_Tech_Position(41),
    Use_Tech_Unit(42),
    Place_COP(43),
    None(44),
    Unknown(45);

    private int id;

    UnitCommandType(int value) {
        this.id = id;
    }
}
