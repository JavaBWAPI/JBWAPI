package bwta;

import bwapi.Position;

import bwem.Area;
import java.util.List;
import java.util.stream.Collectors;

public class Region {
    private final Area area;
    private final Position center;

    Region(final Area area) {
        this.area = area;
        this.center = area.getWalkPositionWithHighestAltitude().toPosition();
    }

    public Position getCenter() {
        return center;
    }

    public List<Chokepoint> getChokepoints() {
        return area.getChokePoints().stream()
                .map(c -> BWTA.chokeMap.get(c))
                .collect(Collectors.toList());
    }

    public List<BaseLocation> getBaseLocations() {
        return area.getBases().stream()
                .map(b -> BWTA.baseMap.get(b))
                .collect(Collectors.toList());
    }

    public boolean isReachable(final Region region) {
        return area.isAccessibleFrom(region.area);
    }

    public List<Region> getReachableRegions() {
        return area.getAccessibleNeighbors().stream()
                .map(a -> BWTA.regionMap.get(a))
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof Region)) {
            return false;
        }
        return area.equals(((Region) o).area);
    }

    @Override
    public int hashCode() {
        return area.hashCode();
    }
}
