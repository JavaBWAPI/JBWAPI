package bwapi;

public enum TextSize {
    Small(0),
    Default(1),
    Large(2),
    Huge(3);

    final int id;

    TextSize(final int id) {
        this.id = id;
    }
}