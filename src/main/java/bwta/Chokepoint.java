package bwta;

import bwapi.Position;
import bwem.ChokePoint;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;


public class Chokepoint {
    private final ChokePoint chokePoint;
    private final Pair<Position, Position> sides;
    private final Position center;
    private final double width;

    Chokepoint(final ChokePoint chokePoint) {
        this.chokePoint = chokePoint;
        this.sides = new ImmutablePair<>(chokePoint.getGeometry().get(1).toPosition(), chokePoint.getGeometry().get(2).toPosition());
        this.center = chokePoint.getGeometry().get(0).toPosition();
        this.width = sides.getLeft().getDistance(sides.getRight());
    }

    public Pair<Region, Region> getRegions() {
        return new ImmutablePair<>(BWTA.regionMap.get(chokePoint.getAreas().getLeft()), BWTA.regionMap.get(chokePoint.getAreas().getRight()));
    }

    public Pair<Position, Position> getSides() {
        return sides;
    }

    public Position getCenter() {
        return center;
    }

    public double getWidth() {
        return width;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Chokepoint)) {
            return false;
        }
        return this.chokePoint.equals(((Chokepoint) o).chokePoint);
    }

    @Override
    public int hashCode() {
        return chokePoint.hashCode();
    }
}
