package bwapi;


/**
 * The Error object is generally used to determine why certain functions in BWAPI have failed.
 * For example, you may not have enough resources to construct a unit.
 *
 * @see Game#getLastError, Game#setLastError
 */
public enum Error {
    Unit_Does_Not_Exist(0),
    Unit_Not_Visible(1),
    Unit_Not_Owned(2),
    Unit_Busy(3),
    Incompatible_UnitType(4),
    Incompatible_TechType(5),
    Incompatible_State(6),
    Already_Researched(7),
    Fully_Upgraded(8),
    Currently_Researching(9),
    Currently_Upgrading(10),
    Insufficient_Minerals(11),
    Insufficient_Gas(12),
    Insufficient_Supply(13),
    Insufficient_Energy(14),
    Insufficient_Tech(15),
    Insufficient_Ammo(16),
    Insufficient_Space(17),
    Invalid_Tile_Position(18),
    Unbuildable_Location(19),
    Unreachable_Location(20),
    Out_Of_Range(21),
    Unable_To_Hit(22),
    Access_Denied(23),
    File_Not_Found(24),
    Invalid_Parameter(25),
    None(26),
    Unknown(27);

    public final int id;

    Error(final int id) {
        this.id = id;
    }
}
