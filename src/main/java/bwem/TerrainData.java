// Original work Copyright (c) 2015, 2017, Igor Dimitrijevic
// Modified work Copyright (c) 2017-2018 OpenBW Team

//////////////////////////////////////////////////////////////////////////
//
// This file is part of the BWEM Library.
// BWEM is free software, licensed under the MIT/X11 License.
// A copy of the license is provided with the library in the LICENSE file.
// Copyright (c) 2015, 2017, Igor Dimitrijevic
//
//////////////////////////////////////////////////////////////////////////

package bwem;

import bwapi.TilePosition;
import bwapi.WalkPosition;
import bwem.util.CheckMode;

public abstract class TerrainData {
    private final MapData mapData;
    private final TileData tileData;

    TerrainData(final MapData mapData, final TileData tileData) {
        this.mapData = mapData;
        this.tileData = tileData;
    }

    public MapData getMapData() {
        return this.mapData;
    }

    public TileData getTileData() {
        return this.tileData;
    }

    public Tile getTile(final TilePosition tilePosition, final CheckMode checkMode) {
        if (!((checkMode == CheckMode.NO_CHECK) || getMapData().isValid(tilePosition))) {
            throw new IllegalArgumentException();
        }
        return getTileData()
                .getTiles()
                .get(getMapData().getTileSize().getX() * tilePosition.getY() + tilePosition.getX());
    }

    public Tile getTile(final TilePosition tilePosition) {
        return getTile(tilePosition, CheckMode.CHECK);
    }

    public MiniTile getMiniTile(final WalkPosition walkPosition, final CheckMode checkMode) {
        if (!((checkMode == CheckMode.NO_CHECK) || getMapData().isValid(walkPosition))) {
            throw new IllegalArgumentException();
        }
        return getTileData()
                .getMiniTiles()
                .get(getMapData().getWalkSize().getX() * walkPosition.getY() + walkPosition.getX());
    }

    public MiniTile getMiniTile(final WalkPosition walkPosition) {
        return getMiniTile(walkPosition, CheckMode.CHECK);
    }

    boolean isSeaWithNonSeaNeighbors(final WalkPosition walkPosition) {
        if (!getMiniTile(walkPosition).isSea()) {
            return false;
        }

        final WalkPosition[] deltas = {
                new WalkPosition(0, -1),
                new WalkPosition(-1, 0),
                new WalkPosition(1, 0),
                new WalkPosition(0, 1)
        };
        for (final WalkPosition delta : deltas) {
            final WalkPosition walkPositionDelta = walkPosition.add(delta);
            if (getMapData().isValid(walkPositionDelta) && !getMiniTile(walkPositionDelta,
                CheckMode.NO_CHECK).isSea()) {
                return true;
            }
        }

        return false;
    }
}
