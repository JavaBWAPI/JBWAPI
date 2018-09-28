package bwapi.values;

public enum TextSize {
    Small(0),
    Default(1),
    Large(2),
    Huge(3);

    public final int value;


    TextSize(final int value){
        this.value = value;
    }
}