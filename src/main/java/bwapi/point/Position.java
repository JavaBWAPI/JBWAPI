package bwapi.point;

import bwapi.utils.BWMath;

import java.util.HashMap;
import java.util.Map;

public class Position extends Point{

    public static final int SIZE_IN_PIXELS = 1;

    public Position(int x, int y) {
        super(x, y, SIZE_IN_PIXELS);
    }

    public int getApproxDistance(final Position position) {
        return BWMath.getApproxDistance(x, y, position.x, position.y);
    }

    public TilePosition toTilePosition() {
        return new TilePosition(x / TilePosition.SIZE_IN_PIXELS, y / TilePosition.SIZE_IN_PIXELS);
    }

    public WalkPosition toWalkPosition() {
        return new WalkPosition(x / WalkPosition.SIZE_IN_PIXELS, y / WalkPosition.SIZE_IN_PIXELS);
    }

    public static Position Invalid = new Position(32000 / SIZE_IN_PIXELS, 32000 / SIZE_IN_PIXELS);
    public static Position None = new Position(32000 / SIZE_IN_PIXELS, 32032 / SIZE_IN_PIXELS);
    public static Position Unknown = new Position(32000 / SIZE_IN_PIXELS, 32064 / SIZE_IN_PIXELS);
    public static Position Origin = new Position(0, 0);
}