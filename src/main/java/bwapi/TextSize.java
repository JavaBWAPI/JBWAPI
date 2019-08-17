package bwapi;

/**
 * Enumeration of available text sizes.
 */
public enum TextSize {
    /**
     * The smallest text size in the game.
     */
    Small(0),
    /**
     * The standard text size, used for most things in the game such as chat messages.
     */
    Default(1),
    /**
     * A larger text size. This size is used for the in-game countdown timer seen in @CTF and @UMS game types.
     */
    Large(2),
    /**
     * The largest text size in the game.
     */
    Huge(3);

    final int id;

    TextSize(final int id) {
        this.id = id;
    }
}