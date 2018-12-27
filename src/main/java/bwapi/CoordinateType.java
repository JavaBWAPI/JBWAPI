package bwapi;

import java.util.Arrays;

public enum CoordinateType {
    None(0),
    Screen(1),
    Map(2),
    Mouse(3);

    static final CoordinateType[] idToEnum = new CoordinateType[4];

    static {
        Arrays.stream(CoordinateType.values()).forEach(v -> idToEnum[v.id] = v);
    }

    public final int id;

    CoordinateType(final int id) {
        this.id = id;
    }
}