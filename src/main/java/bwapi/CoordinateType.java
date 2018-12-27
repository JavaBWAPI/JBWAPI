package bwapi;

public enum CoordinateType implements WithId {
    None(0),
    Screen(1),
    Map(2),
    Mouse(3);

    public final int value;

    CoordinateType(final int value) {
        this.value = value;
    }

    @Override
    public int getId() {
        return value;
    }

    static CoordinateType withId(int id) {
        if (id < 0) return null;
        CoordinateType coordinateType = IdMapper.coordinateTypes[id];
        if (coordinateType == null) {
            throw new IllegalArgumentException("No CoordinateType with id " + id);
        }
        return coordinateType;
    }

    private static class IdMapper {

        static final CoordinateType[] coordinateTypes = IdMapperHelper.toIdTypeArray(CoordinateType.class);
    }

}