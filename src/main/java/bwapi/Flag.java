package bwapi;

public enum Flag {
    CompleteMapInformation(0),
    UserInput(1);

    final int id;

    Flag(final int id) {
        this.id = id;
    }
}
