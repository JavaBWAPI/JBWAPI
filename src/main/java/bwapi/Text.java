package bwapi;

/**
 * Enum containing text formatting codes.
 * <p>
 * Such codes are used in calls to {@link Game#drawText}, {@link Game#printf}
 */
public enum Text {
    /**
     * Uses the previous color that was specified before the current one.
     */
    Previous(1),
    /**
     * Uses the default blueish color. This color is used in standard game messages.
     */
    Default(2),
    /**
     * A solid yellow. This yellow is used in notifications and is also the default color when printing text to Broodwar.
     */
    Yellow(3),
    /**
     * A bright white. This is used for timers.
     */
    White(4),
    /**
     * A dark grey. This color code will override all color formatting that follows.
     */
    Grey(5),
    /**
     * A deep red. This color code is used for error messages.
     */
    Red(6),
    /**
     * A solid green. This color is used for sent messages and resource counters.
     */
    Green(7),
    /**
     * A type of red. This color is used to color the name of the red player.
     */
    BrightRed(8),
    /**
     * This code hides all text and formatting that follows.
     */
    Invisible(11),
    /**
     * A deep blue. This color is used to color the name of the blue player.
     */
    Blue(14),
    /**
     * A teal color. This color is used to color the name of the teal player.
     */
    Teal(15),
    /**
     * A deep purple. This color is used to color the name of the purple player.
     */
    Purple(16),
    /**
     * A solid orange. This color is used to color the name of the orange player.
     */
    Orange(17),
    /**
     * An alignment directive that aligns the text to the right side of the screen.
     */
    Align_Right(18),
    /**
     * An alignment directive that aligns the text to the center of the screen.
     */
    Align_Center(19),
    /**
     * This code hides all text and formatting that follows.
     */
    Invisible2(20),
    /**
     * A dark brown. This color is used to color the name of the brown player.
     */
    Brown(21),
    /**
     * A dirty white. This color is used to color the name of the white player.
     */
    PlayerWhite(22),
    /**
     * A deep yellow. This color is used to color the name of the yellow player.
     */
    PlayerYellow(23),
    /**
     * A dark green. This color is used to color the name of the green player.
     */
    DarkGreen(24),
    /**
     * A bright yellow.
     */
    LightYellow(25),
    /**
     * A cyan color. Similar to Default.
     */
    Cyan(26),
    /**
     * A tan color.
     */
    Tan(27),
    /**
     * A dark blueish color.
     */
    GreyBlue(28),
    /**
     * A type of Green.
     */
    GreyGreen(29),
    /**
     * A different type of Cyan.
     */
    GreyCyan(30),
    /**
     * A bright blue color.
     */
    Turquoise(31);

    final byte id;

    Text(final int id) {
        this.id = (byte) id;
    }

    /**
     * Format text with a textcolor to display on broodwar
     */
    public static String formatText(final String text, final Text format) {
        final byte[] data = text.getBytes();
        final int len = text.length();
        final byte[] formatted = new byte[len + 1];
        formatted[0] = format.id;
        System.arraycopy(data, 0, formatted, 1, len);
        return new String(formatted);
    }

    /**
     * Checks if the given character is a color-changing control code.
     *
     * @return true if c is a regular color, not {@link #Previous}, {@link #Invisible}, {@link #Invisible2}, {@link #Align_Right} or {@link #Align_Center}
     * @since 4.2.0
     */
    boolean isColor() {
        final int c = this.id;
        return (2 <= c && c <= 8) || (14 <= c && c <= 17) || (21 <= c && c <= 31);
    }

    /**
     * Enumeration of available text sizes.
     */
    public enum Size {
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

        Size(final int id) {
            this.id = id;
        }
    }
}
