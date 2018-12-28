package bwta;

import bwapi.Position;
import bwapi.WalkPosition;
import bwem.ChokePoint;
import bwapi.Pair;

import java.util.List;


public class Chokepoint {
    private ChokePoint chokePoint;
    private Pair<Position, Position> sides;
    private Position center;
    private double width;

    Chokepoint(final ChokePoint chokePoint) {
        this.chokePoint = chokePoint;
        this.sides = calculateSides(chokePoint.getGeometry());
        this.center = sides.getFirst().add(sides.getSecond()).divide(2);
        this.width = sides.getLeft().getDistance(sides.getRight());
    }

    public Pair<Region, Region> getRegions() {
        return new Pair<>(BWTA.regionMap.get(chokePoint.getAreas().getLeft()), BWTA.regionMap.get(chokePoint.getAreas().getRight()));
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

    private static Pair<Position, Position> calculateSides(final List<WalkPosition> wp) {
        WalkPosition p1 = wp.get(0);
        WalkPosition p2 = wp.get(0);
        double d_max = -1;

        for (int i=0; i < wp.size(); i++) {
            for (int j=i+1; j < wp.size(); j++) {
                double d = wp.get(i).getDistance(wp.get(j));
                if (d > d_max) {
                    d_max = d;
                    p1 = wp.get(i);
                    p2 = wp.get(j);
                }
            }
        }
        return new Pair<>(p1.toPosition(), p2.toPosition());
    }
}
