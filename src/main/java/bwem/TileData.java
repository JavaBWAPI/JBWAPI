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

class TileData {

    private final Tile[] tiles;
    private final MiniTile[] miniTiles;

    TileData(final int tileCount, final int miniTileCount) {
        tiles = new Tile[tileCount];
        for (int i = 0; i < tileCount; ++i) {
            tiles[i] = new Tile();
        }

        miniTiles = new MiniTile[miniTileCount];
        for (int i = 0; i < miniTileCount; ++i) {
            miniTiles[i] = new MiniTile();
        }
    }

    Tile getTile(int index) {
        return tiles[index];
    }

    MiniTile getMiniTile(int index) {
        return miniTiles[index];
    }
}
