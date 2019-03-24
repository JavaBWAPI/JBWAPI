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

import bwapi.Pair;
import bwapi.WalkPosition;
import bwem.area.Area;
import bwem.map.Map;
import bwem.tile.MiniTile;
import bwem.tile.MiniTileImpl;
import bwem.typedef.CPPath;
import bwem.unit.Neutral;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChokePointImpl implements ChokePoint {
    private final Graph graph;
    private final boolean isPseudo;
    private final int index;
    private final Pair<Area, Area> areas;
    private final WalkPosition[] nodes;
    private final List<Pair<WalkPosition, WalkPosition>> nodesInArea;
    private final List<WalkPosition> geometry;
    private boolean isBlocked;
    private Neutral blockingNeutral;
    private ChokePoint pathBackTrace = null;

    ChokePointImpl(
        final Graph graph,
        final int index,
        final Area area1,
        final Area area2,
        final List<WalkPosition> geometry,
        final Neutral blockingNeutral) {
        if (geometry.isEmpty()) {
            throw new IllegalArgumentException();
        }

        this.graph = graph;
        this.index = index;
        this.areas = new Pair<>(area1, area2);
        this.geometry = geometry;

        // Ensures that in the case where several neutrals are stacked, blockingNeutral points to the
        // bottom one:
        this.blockingNeutral =
                blockingNeutral != null
                        ? getMap().getData().getTile(blockingNeutral.getTopLeft()).getNeutral()
                        : blockingNeutral;

        this.isBlocked = blockingNeutral != null;
        this.isPseudo = this.isBlocked;

        this.nodes = new WalkPosition[Node.NODE_COUNT.ordinal()];
        this.nodes[Node.END1.ordinal()] = geometry.get(0);
        this.nodes[Node.END2.ordinal()] = geometry.get(geometry.size() - 1);

        this.nodesInArea = new ArrayList<>(Node.NODE_COUNT.ordinal());
        for (int i = 0; i < Node.NODE_COUNT.ordinal(); ++i) {
            this.nodesInArea.add(new Pair<>(new WalkPosition(0, 0), new WalkPosition(0, 0)));
        }

        int i = geometry.size() / 2;
        while ((i > 0)
                && (getMap().getData().getMiniTile(geometry.get(i - 1)).getAltitude().intValue()
                > getMap().getData().getMiniTile(geometry.get(i)).getAltitude().intValue())) {
            --i;
        }
        while ((i < geometry.size() - 1)
                && (getMap().getData().getMiniTile(geometry.get(i + 1)).getAltitude().intValue()
                > getMap().getData().getMiniTile(geometry.get(i)).getAltitude().intValue())) {
            ++i;
        }
        this.nodes[Node.MIDDLE.ordinal()] = geometry.get(i);

        Map map = getMap();
        for (int n = 0; n < Node.NODE_COUNT.ordinal(); ++n) {
            for (final Area area : new Area[]{area1, area2}) {
                final WalkPosition nodeInArea =
                    getGraph()
                        .getMap()
                        .breadthFirstSearch(
                            this.nodes[n],
                            // findCond
                            (MiniTile miniTile, WalkPosition w) -> (
                                miniTile.getAreaId().equals(area.getId())
                                    && map.getData()
                                    .getTile(w.toTilePosition(), CheckMode.NO_CHECK)
                                    .getNeutral() == null),
                            // visitCond
                            (MiniTile miniTile, WalkPosition w) -> (
                                miniTile.getAreaId().equals(area.getId())
                                    || (isBlocked()
                                    && (((MiniTileImpl) miniTile).isBlocked()
                                    || map.getData()
                                    .getTile(w.toTilePosition(), CheckMode.NO_CHECK)
                                    .getNeutral() != null))));

                /*
                 * Note: In the original C++ code, "nodeInArea" is a reference to a "WalkPosition" in
                 * "nodesInArea" which changes! Change that object here (after the call to
                 * "breadthFirstSearch")...
                 */
                final WalkPosition left = nodesInArea.get(n).getLeft();
                final WalkPosition right = nodesInArea.get(n).getRight();
                final Pair<WalkPosition, WalkPosition> replacementPair =
                        new Pair<>(left, right);
                if (area.equals(this.areas.getLeft())) {
                    replacementPair.setLeft(nodeInArea);
                } else {
                    replacementPair.setRight(nodeInArea);
                }
                this.nodesInArea.set(n, replacementPair);
            }
        }
    }

    ChokePointImpl(
        final Graph graph,
        final int index,
        final Area area1,
        final Area area2,
        final List<WalkPosition> geometry) {
        this(graph, index, area1, area2, geometry, null);
    }

    private Map getMap() {
        return this.graph.getMap();
    }

    private Graph getGraph() {
        return this.graph;
    }

    @Override
    public boolean isPseudo() {
        return this.isPseudo;
    }

    @Override
    public Pair<Area, Area> getAreas() {
        return this.areas;
    }

    @Override
    public WalkPosition getCenter() {
        return getNodePosition(Node.MIDDLE);
    }

    @Override
    public WalkPosition getNodePosition(final Node node) {
        if (!(node.ordinal() < Node.NODE_COUNT.ordinal())) {
            throw new IllegalArgumentException();
        }
        return this.nodes[node.ordinal()];
    }

    @Override
    public WalkPosition getNodePositionInArea(final Node node, final Area area) {
        if (!(area.equals(this.areas.getLeft()) || area.equals(this.areas.getRight()))) {
            throw new IllegalArgumentException();
        }
        return area.equals(areas.getLeft())
                ? this.nodesInArea.get(node.ordinal()).getLeft()
                : this.nodesInArea.get(node.ordinal()).getRight();
    }

    @Override
    public List<WalkPosition> getGeometry() {
        return this.geometry;
    }

    @Override
    public boolean isBlocked() {
        return this.isBlocked;
    }

    @Override
    public Neutral getBlockingNeutral() {
        return this.blockingNeutral;
    }

    @Override
    public int distanceFrom(final ChokePoint chokePoint) {
        return getGraph().distance(this, chokePoint);
    }

    @Override
    public boolean accessibleFrom(final ChokePoint chokePoint) {
        return (distanceFrom(chokePoint) >= 0);
    }

    @Override
    public CPPath getPathTo(final ChokePoint cp) {
        return getGraph().getPath(this, cp);
    }

    public void onBlockingNeutralDestroyed(final Neutral pBlocking) {
        if (!(pBlocking != null && pBlocking.isBlocking())) {
            throw new IllegalStateException();
        }

        if (pBlocking.equals(this.blockingNeutral)) {
            // Ensures that in the case where several neutrals are stacked, blockingNeutral points to the
            // bottom one:
            this.blockingNeutral =
                    getMap().getData().getTile(this.blockingNeutral.getTopLeft()).getNeutral();

            if (this.blockingNeutral == null && getGraph().getMap().automaticPathUpdate()) {
                this.isBlocked = false;
            }
        }
    }

    int getIndex() {
        return this.index;
    }

    ChokePoint getPathBackTrace() {
        return this.pathBackTrace;
    }

    void setPathBackTrace(final ChokePoint pathBackTrace) {
        this.pathBackTrace = pathBackTrace;
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) {
            return true;
        } else if (!(object instanceof ChokePointImpl)) {
            return false;
        } else {
            final ChokePointImpl that = (ChokePointImpl) object;
            final boolean lel = this.areas.getLeft().equals(that.areas.getLeft());
            final boolean ler = this.areas.getLeft().equals(that.areas.getRight());
            final boolean rer = this.areas.getRight().equals(that.areas.getRight());
            final boolean rel = this.areas.getRight().equals(that.areas.getLeft());
            return lel && rer
                    || ler && rel; /* true if area pairs are an exact match or if one pair is reversed. */
        }
    }

    @Override
    public int hashCode() {
        int idLeft = areas.getLeft().getId().intValue();
        int idRight = areas.getRight().getId().intValue();
        if (idLeft > idRight) {
            final int idLeftTmp = idLeft;
            idLeft = idRight;
            idRight = idLeftTmp;
        }
        return Objects.hash(idLeft, idRight);
    }
}
