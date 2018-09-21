package bwapi.types;

public enum UnitSizeType {

    Independent(0),
    Small(1),
    Medium(2),
    Large(3),
    None(4),
    Unknown(5);

    private int value;

    public int getValue(){
        return value;
    }

    UnitSizeType(int value) {
        this.value = value;
    }
}
