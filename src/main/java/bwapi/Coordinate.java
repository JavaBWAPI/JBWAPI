package bwapi;

public enum Coordinate {
    None(0),
    Screen(1),
    Map(2),
    Mouse(3);

    public final int value;

    Coordinate(final int value){
        this.value = value;
    }
}