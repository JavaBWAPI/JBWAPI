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

import bwapi.Game;
import bwem.map.Map;
import bwem.map.MapInitializer;
import bwem.map.MapInitializerImpl;

public final class BWEM {
  private final Map map;

  public BWEM(final Game game) {
    this.map = new MapInitializerImpl(game);
  }

  /** Returns the root internal data container. */
  public Map getMap() {
    return this.map;
  }


  /**
   * Initializes and pre-computes all of the internal data.
   *
   */
  public void initialize() {
    if (!(this.map instanceof MapInitializer)) {
      throw new IllegalStateException("BWEM was not instantiated properly.");
    }
    ((MapInitializer) this.map).initialize();
    this.map.assignStartingLocationsToSuitableBases();
  }
}
