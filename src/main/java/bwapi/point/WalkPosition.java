package bwapi.point;

public class WalkPosition extends Point{

    public static final int SIZE_IN_PIXELS = 8;

    public WalkPosition(int x, int y) {
        super(x, y, SIZE_IN_PIXELS);
    }

    public Position toPosition() {
        return new Position(x*SIZE_IN_PIXELS, y*SIZE_IN_PIXELS);
    }

    public TilePosition toTilePosition() {
        return new TilePosition(x / TILE_WALK_FACTOR, y / TILE_WALK_FACTOR);
    }

    public static  WalkPosition Invalid = new WalkPosition(32000 / SIZE_IN_PIXELS, 32000 / SIZE_IN_PIXELS);
    public static  WalkPosition None = new WalkPosition(32000 / SIZE_IN_PIXELS, 32032 / SIZE_IN_PIXELS);
    public static  WalkPosition Unknown = new WalkPosition(32000 / SIZE_IN_PIXELS, 32064 / SIZE_IN_PIXELS);
    public static  WalkPosition Origin = new WalkPosition(0,0);
}