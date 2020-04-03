package bwapi;

public class TilePosition extends Point<TilePosition> {

    public static final int SIZE_IN_PIXELS = 32;
    public static final TilePosition Invalid = new TilePosition(32000 / SIZE_IN_PIXELS, 32000 / SIZE_IN_PIXELS);
    public static final TilePosition None = new TilePosition(32000 / SIZE_IN_PIXELS, 32032 / SIZE_IN_PIXELS);
    public static final TilePosition Unknown = new TilePosition(32000 / SIZE_IN_PIXELS, 32064 / SIZE_IN_PIXELS);
    public static final TilePosition Origin = new TilePosition(0, 0);

    public TilePosition(final int x, final int y) {
        super(x, y, SIZE_IN_PIXELS);
    }

    public TilePosition(final Position p) {
        super(p.x / SIZE_IN_PIXELS, p.y / SIZE_IN_PIXELS, SIZE_IN_PIXELS);
    }

    public TilePosition(final WalkPosition wp) {
        super(wp.x / WalkPosition.TILE_WALK_FACTOR, wp.y / WalkPosition.TILE_WALK_FACTOR, SIZE_IN_PIXELS);
    }

    public TilePosition(final TilePosition tp) {
        super(tp.x, tp.y, SIZE_IN_PIXELS);
    }

    TilePosition(ClientData.Position position) {
        this(position.getX(), position.getY());
    }

    public Position toPosition() {
        return new Position(x * SIZE_IN_PIXELS, y * SIZE_IN_PIXELS);
    }

    public WalkPosition toWalkPosition() {
        return new WalkPosition(x * TILE_WALK_FACTOR, y * TILE_WALK_FACTOR);
    }

    public TilePosition subtract(final TilePosition other) {
        return new TilePosition(x - other.x, y - other.y);
    }

    public TilePosition add(final TilePosition other) {
        return new TilePosition(x + other.x, y + other.y);
    }

    public TilePosition divide(final int divisor) {
        return new TilePosition(x / divisor, y / divisor);
    }

    public TilePosition multiply(final int multiplier) {
        return new TilePosition(x * multiplier, y * multiplier);
    }
}