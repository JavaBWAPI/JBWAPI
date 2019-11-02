package bwapi;

/**
 * An enumeration of mouse button inputs.
 *
 * @see Game#getMouseState
 */
public enum MouseButton {
    M_LEFT(0),
    M_RIGHT(1),
    M_MIDDLE(2),
    M_MAX(3);

    final int id;

    MouseButton(final int id) {
        this.id = id;
    }

}
