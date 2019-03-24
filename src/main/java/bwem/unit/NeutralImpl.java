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

package bwem.unit;

import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwapi.WalkPosition;
import bwem.area.Area;
import bwem.map.Map;
import bwem.map.MapInitializer;
import bwem.tile.Tile;
import bwem.tile.TileImpl;

import java.util.ArrayList;
import java.util.List;

public abstract class NeutralImpl implements Neutral {
    private final Unit bwapiUnit;
    private final Position pos;
    private final TilePosition topLeft;
    private final TilePosition tileSize;
    private final Map map;
    private Neutral nextStacked = null;
    private List<WalkPosition> blockedAreas = new ArrayList<>();

    NeutralImpl(final Unit unit, final Map map) {
        this.bwapiUnit = unit;
        this.map = map;
        this.pos = unit.getInitialPosition();
        this.topLeft = unit.getInitialTilePosition();
        this.tileSize = unit.getType().tileSize();

        putOnTiles();
    }

    public void simulateCPPObjectDestructor() {
        removeFromTiles();

        if (isBlocking()) {
            MapInitializer map = (MapInitializer) getMap();
            map.onBlockingNeutralDestroyed(this);
        }
    }

    @Override
    public Unit getUnit() {
        return this.bwapiUnit;
    }

    @Override
    public Position getCenter() {
        return this.pos;
    }

    @Override
    public TilePosition getTopLeft() {
        return this.topLeft;
    }

    @Override
    public TilePosition getBottomRight() {
        return this.topLeft.add(this.tileSize).subtract(new TilePosition(1, 1));
    }

    @Override
    public TilePosition getSize() {
        return this.tileSize;
    }

    @Override
    public boolean isBlocking() {
        return !this.blockedAreas.isEmpty();
    }

    public void setBlocking(final List<WalkPosition> blockedAreas) {
        if (!(this.blockedAreas.isEmpty() && !blockedAreas.isEmpty())) {
            throw new IllegalStateException();
        }
        this.blockedAreas = blockedAreas;
    }

    @Override
    public List<Area> getBlockedAreas() {
        final List<Area> blockedAreas = new ArrayList<>();
        for (final WalkPosition w : this.blockedAreas) {
            if (getMap().getArea(w) != null) {
                blockedAreas.add(getMap().getArea(w));
            }
        }
        return blockedAreas;
    }

    @Override
    public Neutral getNextStacked() {
        return this.nextStacked;
    }

    // Returns the last Neutral stacked over this Neutral, if ever.
    public Neutral getLastStacked() {
        Neutral topNeutral = this;
        while (topNeutral.getNextStacked() != null) {
            topNeutral = topNeutral.getNextStacked();
        }
        return topNeutral;
    }

    private boolean isSameUnitTypeAs(Neutral neutral) {
        return this.getUnit().getClass().getName().equals(neutral.getUnit().getClass().getName());
    }

    private void putOnTiles() {
        if (!(getNextStacked() == null)) {
            throw new IllegalStateException();
        }

        for (int dy = 0; dy < getSize().getY(); ++dy)
            for (int dx = 0; dx < getSize().getX(); ++dx) {
                final Tile deltaTile = getMap().getData()
                                .getTile(getTopLeft().add(new TilePosition(dx, dy)));
                if (deltaTile.getNeutral() == null) {
                    ((TileImpl) deltaTile).addNeutral(this);
                } else {
                    final Neutral topNeutral = deltaTile.getNeutral().getLastStacked();
                    if (this.equals(deltaTile.getNeutral())) {
                        throw new IllegalStateException();
                    } else if (this.equals(topNeutral)) {
                        throw new IllegalStateException();
                    } else if (topNeutral.getClass().getName().equals(Geyser.class.getName())) {
                        throw new IllegalStateException();
                    } else if (!((NeutralImpl) topNeutral).isSameUnitTypeAs(this)) {
                        //                    bwem_assert_plus(pTop->Type() == Type(), "stacked neutrals have
                        throw new IllegalStateException(
                                "Stacked Neutral objects have different types: top="
                                        + topNeutral.getClass().getName()
                                        + ", this="
                                        + this.getClass().getName());
                    } else if (!(topNeutral.getTopLeft().equals(getTopLeft()))) {
                        //                    bwem_assert_plus(pTop->topLeft() == topLeft(), "stacked neutrals
                        throw new IllegalStateException(
                                "Stacked Neutral objects not aligned: top="
                                        + topNeutral.toString()
                                        + ", this="
                                        + getTopLeft().toString());
                    } else if (!(dx == 0 && dy == 0)) {
                        throw new IllegalStateException();
                    } else {
                        ((NeutralImpl) topNeutral).nextStacked = this;
                        return;
                    }
                }
            }
    }

    /**
     * Warning: Do not use this function outside of BWEM's internals. This method is designed to be
     * called from the "~Neutral" destructor in C++.
     */
    private void removeFromTiles() {
        for (int dy = 0; dy < getSize().getY(); ++dy)
            for (int dx = 0; dx < getSize().getX(); ++dx) {
                final Tile tile = getMap().getData()
                        .getTile(getTopLeft().add(new TilePosition(dx, dy)));
                if (tile.getNeutral() == null) {
                    throw new IllegalStateException();
                }

                if (tile.getNeutral().equals(this)) {
                    ((TileImpl) tile).removeNeutral(this);
                    if (this.nextStacked != null) {
                        ((TileImpl) tile).addNeutral(this.nextStacked);
                    }
                } else {
                    Neutral prevStacked = tile.getNeutral();
                    while (prevStacked != null && !this.equals(prevStacked.getNextStacked())) {
                        prevStacked = prevStacked.getNextStacked();
                    }
                    if (!(dx == 0 && dy == 0)) {
                        throw new IllegalStateException();
                    }
                    if (prevStacked != null) {
                        if (!((NeutralImpl) prevStacked).isSameUnitTypeAs(this) || !(prevStacked
                            .getTopLeft().equals(getTopLeft()))) {
                            throw new IllegalStateException();
                        }
                        ((NeutralImpl) prevStacked).nextStacked = nextStacked;
                    }
                    this.nextStacked = null;
                    return;
                }
            }

        this.nextStacked = null;
    }

    private Map getMap() {
        return this.map;
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof Neutral)) {
            return false;
        } else {
            final Neutral that = (Neutral) object;
            return (getUnit().getID() == that.getUnit().getID());
        }
    }

    @Override
    public int hashCode() {
        return getUnit().hashCode();
    }
}
