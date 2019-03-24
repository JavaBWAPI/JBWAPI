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

package bwem.tile;

import bwem.util.Markable;
import bwem.util.StaticMarkable;
import bwem.area.typedef.AreaId;
import bwem.typedef.Altitude;
import bwem.unit.Neutral;

public class TileImpl implements Tile {
    private static final StaticMarkable staticMarkable = new StaticMarkable();
    private final Markable markable;

    private Neutral neutral;
    private Altitude lowestAltitude;
    private AreaId areaId;
    private int internalData;
    private Tile.GroundHeight groundHeight;
    private boolean isBuildable;
    private boolean isDoodad;

    TileImpl() {
        this.markable = new Markable(TileImpl.staticMarkable);
        this.neutral = null;
        this.lowestAltitude = Altitude.ZERO;
        this.areaId = AreaId.ZERO;
        this.internalData = 0;
        this.groundHeight = GroundHeight.LOW_GROUND;
        this.isBuildable = false;
        this.isDoodad = false;
    }

    public static StaticMarkable getStaticMarkable() {
        return TileImpl.staticMarkable;
    }

    public Markable getMarkable() {
        return this.markable;
    }

    @Override
    public boolean isBuildable() {
        return this.isBuildable;
    }

    @Override
    public AreaId getAreaId() {
        return this.areaId;
    }

    public void setAreaId(final AreaId areaId) {
        if (!(areaId.intValue() == -1 || getAreaId().intValue() == 0 && areaId.intValue() != 0)) {
            throw new IllegalStateException();
        }
        this.areaId = areaId;
    }

    @Override
    public Altitude getLowestAltitude() {
        return this.lowestAltitude;
    }

    public void setLowestAltitude(final Altitude lowestAltitude) {
        if (!(lowestAltitude.intValue() >= 0)) {
            throw new IllegalArgumentException();
        }
        this.lowestAltitude = lowestAltitude;
    }

    @Override
    public boolean isWalkable() {
        return (getAreaId().intValue() != 0);
    }

    @Override
    public boolean isTerrain() {
        return isWalkable();
    }

    @Override
    public GroundHeight getGroundHeight() {
        return this.groundHeight;
    }

    public void setGroundHeight(final int groundHeight) {
        //        { bwem_assert((0 <= h) && (h <= 2)); bits.groundHeight = h; }
        //        if (!((0 <= h) && (h <= 2))) {
        //            throw new IllegalArgumentException();
        //        }
        this.groundHeight = GroundHeight.parseGroundHeight(groundHeight);
    }

    @Override
    public boolean isDoodad() {
        return this.isDoodad;
    }

    @Override
    public Neutral getNeutral() {
        return this.neutral;
    }

    @Override
    public int getStackedNeutralCount() {
        int stackSize = 0;
        for (Neutral stackedNeutral = getNeutral();
             stackedNeutral != null;
             stackedNeutral = stackedNeutral.getNextStacked()) {
            ++stackSize;
        }
        return stackSize;
    }

    public void setBuildable() {
        this.isBuildable = true;
    }

    public void setDoodad() {
        this.isDoodad = true;
    }

    public void addNeutral(final Neutral neutral) {
        if (!(getNeutral() == null && neutral != null)) {
            throw new IllegalStateException();
        }
        this.neutral = neutral;
    }

    public void resetAreaId() {
        this.areaId = AreaId.ZERO;
    }

    public void removeNeutral(final Neutral neutral) {
        if (!getNeutral().equals(neutral)) {
            throw new IllegalStateException();
        }
        this.neutral = null;
    }

    public int getInternalData() {
        return this.internalData;
    }

    public void setInternalData(int internalData) {
        this.internalData = internalData;
    }
}
