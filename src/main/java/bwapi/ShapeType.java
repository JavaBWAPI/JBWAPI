package bwapi;

import java.util.Arrays;

/**
 * Used in {@link UnitCommand}
 */
enum ShapeType {
    None(0),
    Text(1),
    Box(2),
    Triangle(3),
    Circle(4),
    Ellipse(5),
    Dot(6),
    Line(7);

    static final ShapeType[] idToEnum = new ShapeType[8];

    static {
        Arrays.stream(ShapeType.values()).forEach(v -> idToEnum[v.id] = v);
    }

    final int id;

    ShapeType(final int id) {
        this.id = id;
    }
}
