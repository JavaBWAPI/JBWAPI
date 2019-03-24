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

import java.util.ArrayList;
import java.util.List;

class TileData {
    private final List<Tile> tiles;
    private final List<MiniTile> miniTiles;

    TileData(final int tileCount, final int miniTileCount) {
        this.tiles = new ArrayList<>(tileCount);
        for (int i = 0; i < tileCount; ++i) {
            this.tiles.add(new Tile());
        }

        this.miniTiles = new ArrayList<>(miniTileCount);
        for (int i = 0; i < miniTileCount; ++i) {
            this.miniTiles.add(new MiniTile());
        }
    }

    List<Tile> getTiles() {
        return this.tiles;
    }

    List<MiniTile> getMiniTiles() {
        return this.miniTiles;
    }
}
