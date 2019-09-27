package bwapi;


public class Position extends Point<Position> {

    public static final int SIZE_IN_PIXELS = 1;
    public static final Position Invalid = new Position(32000 / SIZE_IN_PIXELS, 32000 / SIZE_IN_PIXELS);
    public static final Position None = new Position(32000 / SIZE_IN_PIXELS, 32032 / SIZE_IN_PIXELS);
    public static final Position Unknown = new Position(32000 / SIZE_IN_PIXELS, 32064 / SIZE_IN_PIXELS);
    public static final Position Origin = new Position(0, 0);

    public Position(final int x, final int y) {
        super(x, y, SIZE_IN_PIXELS);
    }

    Position(ClientData.Position position) {
        this(position.getX(), position.getY());
    }

    public TilePosition toTilePosition() {
        return new TilePosition(x / TilePosition.SIZE_IN_PIXELS, y / TilePosition.SIZE_IN_PIXELS);
    }

    public WalkPosition toWalkPosition() {
        return new WalkPosition(x / WalkPosition.SIZE_IN_PIXELS, y / WalkPosition.SIZE_IN_PIXELS);
    }

    public Position subtract(final Position other) {
        return new Position(x - other.x, y - other.y);
    }

    public Position add(final Position other) {
        return new Position(x + other.x, y + other.y);
    }

    public Position divide(final int divisor) {
        return new Position(x / divisor, y / divisor);
    }

    public Position multiply(final int multiplier) {
        return new Position(x * multiplier, y * multiplier);
    }
}