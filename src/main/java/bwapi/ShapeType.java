package bwapi;

enum ShapeType implements WithId {
    None(0),
    Text(1),
    Box(2),
    Triangle(3),
    Circle(4),
    Ellipse(5),
    Dot(6),
    Line(7);

    public final int value;

    ShapeType(final int value) {
      this.value = value;
    }

    @Override
    public int getId() {
      return value;
    }

    static ShapeType withId(int id) {
        if (id < 0) return null;
        ShapeType shapeType = IdMapper.shapeTypeForId[id];
        if (shapeType == null) {
            throw new IllegalArgumentException("No ShapeType with id " + id);
        }
        return shapeType;
    }

    private static class IdMapper {

        static final ShapeType[] shapeTypeForId = IdMapperHelper.toIdTypeArray(ShapeType.class);
    }
}
