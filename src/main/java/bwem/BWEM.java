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

public final class BWEM {
    private final BWMap map;

    public BWEM(final Game game) {
        this.map = new BWMapInitializer(game);
    }

    /**
     * Returns the root internal data container.
     */
    public BWMap getMap() {
        return this.map;
    }


    /**
     * Initializes and pre-computes all of the internal data.
     */
    public void initialize() {
        if (!(this.map instanceof BWMapInitializer)) {
            throw new IllegalStateException("BWEM was not instantiated properly.");
        }
        ((BWMapInitializer) this.map).initialize();
        this.map.assignStartingLocationsToSuitableBases();
    }
}
