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

package bwem.map;

import bwapi.Game;
import bwapi.TilePosition;
import bwapi.WalkPosition;
import bwem.CheckMode;
import bwem.tile.MiniTile;
import bwem.tile.Tile;

public interface TerrainDataInitializer {
  Tile getTile_(TilePosition tilePosition, CheckMode checkMode);

  Tile getTile_(TilePosition tilePosition);

  MiniTile getMiniTile_(WalkPosition walkPosition, CheckMode checkMode);

  MiniTile getMiniTile_(WalkPosition walkPosition);

  void markUnwalkableMiniTiles(final Game game);

  void markBuildableTilesAndGroundHeight(final Game game);

  void decideSeasOrLakes(int lakeMaxMiniTiles, int lakeMaxWidthInMiniTiles);
}
