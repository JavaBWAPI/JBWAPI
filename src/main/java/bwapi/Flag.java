package bwapi;

public enum Flag {
    CompleteMapInformation(0),
    UserInput(1);

    public final int value;

    Flag(final int value) {
        this.value = value;
    }
}
