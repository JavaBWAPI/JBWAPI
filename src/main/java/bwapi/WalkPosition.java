package bwapi;

public class WalkPosition extends Point {

    public static final int SIZE_IN_PIXELS = 8;
    public static final WalkPosition Invalid = new WalkPosition(32000 / SIZE_IN_PIXELS, 32000 / SIZE_IN_PIXELS);
    public static final WalkPosition None = new WalkPosition(32000 / SIZE_IN_PIXELS, 32032 / SIZE_IN_PIXELS);
    public static final WalkPosition Unknown = new WalkPosition(32000 / SIZE_IN_PIXELS, 32064 / SIZE_IN_PIXELS);
    public static final WalkPosition Origin = new WalkPosition(0, 0);

    public WalkPosition(final int x, final int y) {
        super(x, y, SIZE_IN_PIXELS);
    }

    public Position toPosition() {
        return new Position(x * SIZE_IN_PIXELS, y * SIZE_IN_PIXELS);
    }

    public TilePosition toTilePosition() {
        return new TilePosition(x / TILE_WALK_FACTOR, y / TILE_WALK_FACTOR);
    }

    public WalkPosition subtract(final WalkPosition other) {
        return new WalkPosition(x - other.x, y - other.y);
    }

    public WalkPosition add(final WalkPosition other) {
        return new WalkPosition(x + other.x, y + other.y);
    }

    public WalkPosition divide(final int divisor) {
        return new WalkPosition(x / divisor, y / divisor);
    }

    public WalkPosition multiply(final int multiplier) {
        return new WalkPosition(x * multiplier, y * multiplier);
    }
}