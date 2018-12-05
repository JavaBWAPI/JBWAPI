package bwta;

import bwapi.Position;
import bwem.ChokePoint;
import bwapi.Pair;
import bwapi.Pair;


public class Chokepoint {
    private  ChokePoint chokePoint;
    private  Pair<Position, Position> sides;
    private Position center;
    private double width;

    Chokepoint(final ChokePoint chokePoint) {
        try {
            this.chokePoint = chokePoint;
            this.sides = new Pair<>(chokePoint.getGeometry().get(1).toPosition(), chokePoint.getGeometry().get(2).toPosition());
            this.center = chokePoint.getGeometry().get(0).toPosition();
            this.width = sides.getLeft().getDistance(sides.getRight());
        }
        catch (Exception e) {
            System.out.println(width);
        }
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
}
