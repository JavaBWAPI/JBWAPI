package bwta;

import bwapi.Position;
import bwapi.TilePosition;
import bwapi.Unit;
import bwem.Base;

import java.util.Set;
import java.util.stream.Collectors;

public class BaseLocation {
    private final Base base;
    private final Position position;
    private final TilePosition tilePosition;
    private final int minerals;
    private final int gas;
    private final Set<Unit> mineralSet;
    private final Set<Unit> geyserSet;
    private final boolean island;
    private final boolean mineralOnly;
    private final boolean startLocation;


    BaseLocation(final Base base) {
        this.base = base;
        this.position = base.getCenter();
        this.tilePosition = base.getLocation();
        this.minerals = 1;
        this.gas = 1;
        this.mineralSet = base.getMinerals().stream().map(m -> m.getUnit()).collect(Collectors.toSet());
        this.geyserSet =  base.getGeysers().stream().map(g -> g.getUnit()).collect(Collectors.toSet());
        this.island = base.getArea().getAccessibleNeighbors().isEmpty();
        this.mineralOnly = !mineralSet.isEmpty() && geyserSet.isEmpty();
        this.startLocation = base.isStartingLocation();
    }

    public Position getPosition() {
        return position;
    }

    public TilePosition getTilePosition() {
        return tilePosition;
    }

    public Region getRegion() {
        return BWTA.regionMap.get(base.getArea());
    }

    public int minerals() {
        return minerals;
    }

    public int gas() {
        return gas;
    }

    public Set<Unit> getMinerals() {
        return mineralSet;
    }

    public Set<Unit> getGeysers() {
        return geyserSet;
    }

    public double getGroundDistance(final BaseLocation other) {
        return BWTA.getGroundDistance(tilePosition, other.tilePosition);
    }

    public double getAirDistance(final BaseLocation other) {
        return position.getDistance(other.position);
    }

    public boolean isIsland() {
        return island;
    }

    public boolean isMineralOnly() {
        return mineralOnly;
    }

    public boolean isStartLocation() {
        return startLocation;
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof BaseLocation)) {
            return false;
        }
        return base.equals(((BaseLocation) o).base);
    }

    @Override
    public int hashCode() {
        return base.hashCode();
    }
}
