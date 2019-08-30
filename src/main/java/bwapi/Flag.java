package bwapi;

/**
 * Contains flag enumerations for BWAPI.
 * @see Game#enableFlag
 * @see Game#isFlagEnabled
 */
public enum Flag {
    /**
     * Enable to get information about all units on the map, not just the visible units.
     */
    CompleteMapInformation(0),
    /**
     * Enable to get information from the user (what units are selected, chat messages the user enters, etc)
     */
    UserInput(1);

    final int id;

    Flag(final int id) {
        this.id = id;
    }
}
