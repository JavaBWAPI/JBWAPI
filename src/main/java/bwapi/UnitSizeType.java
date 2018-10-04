package bwapi;

public enum UnitSizeType {

    Independent(0),
    Small(1),
    Medium(2),
    Large(3),
    None(4),
    Unknown(5);

    public final int id;

    UnitSizeType(final int id) {
        this.id = id;
    }
}
