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

import java.io.OutputStream;

/**
 * BWEM Broodwar Map analysis library by Igor Dimitrijevic.
 * Ported to Java by the OpenBW Team
 *
 * By default BWEM throws when an invalid state is encountered.
 * But if you know what you are doing, you can skip these throws by setting
 * {@link #setFailOnError} to `false`.
 * These errors will then be outputted to `System.err`, but this can also be changed
 * with {@link #setFailOutputStream} (if you set it to `null` the errors will be completely ignored).
 */
public final class BWEM {
    private final BWMapInitializer map;
    private final Asserter asserter;

    public BWEM(final Game game) {
        this.asserter = new Asserter();
        this.map = new BWMapInitializer(game, asserter);
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
        this.map.initialize();
        this.map.assignStartingLocationsToSuitableBases();
    }

    public void setFailOnError(boolean failOnError) {
        asserter.setFailOnError(failOnError);
    }

    public void setFailOutputStream(OutputStream outputStream) {
        asserter.setFailOutputStream(outputStream);
    }

    public void calculateAreaBoundaries() {
        if (!this.map.isInitialized()) {
            throw new IllegalStateException("BWEM needs to be initialized first.");
        }
        this.map.getAreas().forEach(a -> ((AreaInitializer) a).calcBoundaryVertices());
    }
}
