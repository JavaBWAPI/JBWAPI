package bwta;

import bwapi.Position;
import bwem.Area;

import java.util.ArrayList;
import java.util.List;

public class Polygon {
    private final Area area;
    private final List<Position> boundaryVertices;
    private final double areaTotal;
    private final double perimeterTotal;
    private final Position center;

    Polygon(final Area area) {
        this.area = area;
        this.boundaryVertices = area.getBoundaryVertices();

        // calculate surface
        // https://en.wikipedia.org/wiki/Shoelace_formula#Statement
        double surface = 0;
        for (int i=0; i < boundaryVertices.size(); i++) {
            final Position p0 = boundaryVertices.get(i);
            final Position p1 = boundaryVertices.get((i+1) % boundaryVertices.size());
            surface += p0.x * p1.y - p1.x * p0.y;
        }
        areaTotal = Math.abs(surface) / 2;

        // calculate perimeter
        double perimeter = 0;
        for (int i=0; i < boundaryVertices.size(); i++) {
            perimeter += boundaryVertices.get(i).getDistance(boundaryVertices.get((i + 1) % boundaryVertices.size()));
        }
        perimeterTotal = perimeter;

        // calculate center of gravity
        // https://stackoverflow.com/questions/5271583/center-of-gravity-of-a-polygon
        if (areaTotal == 0) {
            center = null;
        }
        else {
            int Cx = 0;
            int Cy = 0;
            for (int i=0; i < boundaryVertices.size(); i++) {
                final Position p0 = boundaryVertices.get(i);
                final Position p1 = boundaryVertices.get((i+1) % boundaryVertices.size());
                final int div = p0.x * p1.y - p1.x * p0.y;
                Cx += (p0.x + p1.x) * div;
                Cy += (p0.y + p1.y) * div;
            }
            Cx /= 6 * areaTotal;
            Cy /= 6 * areaTotal;
            center = new Position(Cx, Cy);
        }
    }

    public double getArea() {
        return areaTotal;
    }

    public double getPerimeter() {
        return perimeterTotal;
    }

    public Position getCenter() {
        return center;
    }

    public boolean isInside(final Position position) {
        // approximately
        return BWTA.getRegion(position).area.equals(area);
    }

    public List<Position> getPoints() {
        return new ArrayList<>(boundaryVertices);
    }
}
