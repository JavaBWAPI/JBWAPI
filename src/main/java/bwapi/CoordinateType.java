package bwapi;

import java.util.Arrays;

/**
 * The coordinate type enumeration for relative drawing positions.
 */
public enum CoordinateType {
    /**
     * A default value for uninitialized coordinate types.
     */
    None(0),
    /**
     * {@link Position#Origin} (0,0) corresponds to the top left corner of the <b>screen</b>
     */
    Screen(1),
    /**
     * {@link Position#Origin} (0,0) corresponds to the top left corner of the <b>map</b>
     */
    Map(2),
    /**
     * {@link Position#Origin} (0,0) corresponds to the top left corner of the <b>mouse cursor</b>
     */
    Mouse(3);

    static final CoordinateType[] idToEnum = new CoordinateType[4];

    static {
        Arrays.stream(CoordinateType.values()).forEach(v -> idToEnum[v.id] = v);
    }

    final int id;

    CoordinateType(final int id) {
        this.id = id;
    }
}
