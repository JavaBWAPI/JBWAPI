package bwapi.point;

public class TilePosition extends Point{

    public static final int SIZE_IN_PIXELS = 32;

    public TilePosition(int x, int y) {
        super(x, y, SIZE_IN_PIXELS);
    }

    public Position toPosition(){
        return new Position(x * SIZE_IN_PIXELS, y * SIZE_IN_PIXELS);
    }

    public WalkPosition toWalkPosition() {
        return new WalkPosition(x * TILE_WALK_FACTOR, y * TILE_WALK_FACTOR);
    }

    public static TilePosition Invalid = new TilePosition(32000 / SIZE_IN_PIXELS, 32000 / SIZE_IN_PIXELS);
    public static TilePosition None = new TilePosition(32000 / SIZE_IN_PIXELS, 32032 / SIZE_IN_PIXELS);
    public static TilePosition Unknown = new TilePosition(32000 / SIZE_IN_PIXELS, 32064 / SIZE_IN_PIXELS);
    public static TilePosition Origin = new TilePosition(0, 0);
}