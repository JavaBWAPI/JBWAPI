package bwapi;

/**
 * Contains flag enumerations for BWAPI.
 * @see {@link Game#enableFlag}, {@link Game#isFlagEnabled}
 */
public enum Flag {
    CompleteMapInformation(0),
    UserInput(1);

    final int id;

    Flag(final int id) {
        this.id = id;
    }
}
