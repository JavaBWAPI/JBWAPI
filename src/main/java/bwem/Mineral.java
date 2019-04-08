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

import bwapi.Unit;

/**
 * Minerals Correspond to the units in BWAPI::getStaticNeutralUnits() for which
 * getType().isMineralField().
 */
public final class Mineral extends Resource {
    Mineral(final Unit unit, final BWMap map) {
        super(unit, map);

        //        bwem_assert(Type().isMineralField());
        if (!unit.getType().isMineralField()) {
            throw new IllegalArgumentException(
                    "Unit is not a MineralPatch: " + unit.getClass().getName());
        }
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof Mineral)) {
            return false;
        } else {
            final Mineral that = (Mineral) object;
            return (this.getUnit().getID() == that.getUnit().getID());
        }
    }

    @Override
    public int hashCode() {
        return getUnit().hashCode();
    }
}
