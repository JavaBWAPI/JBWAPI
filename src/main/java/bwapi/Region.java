package bwapi;


import bwapi.ClientData.RegionData;
import java.util.*;

public class Region implements Comparable<Region> {
    private final RegionData regionData;
    private final Game game;

    private final int id;
    private final int regionGroupID;
    private final Position center;
    private final boolean higherGround;
    private final int defensePriority;
    private final boolean accessible;
    private final int boundsLeft;
    private final int boundsTop;
    private final int boundsRight;
    private final int boundsBottom;
    private Region closestAccessibleRegion;
    private Region closestInaccessibleRegion;

    private List<Region> neighbours;

    Region(final RegionData regionData, final Game game) {
        this.regionData = regionData;
        this.game = game;
        this.id = regionData.getId();
        this.regionGroupID = regionData.islandID();
        this.center = new Position(regionData.getCenter_x(), regionData.getCenter_y());
        this.higherGround = regionData.isHigherGround();
        this.defensePriority = regionData.getPriority();
        this.accessible = regionData.isAccessible();
        this.boundsLeft = regionData.getLeftMost();
        this.boundsTop = regionData.getTopMost();
        this.boundsRight = regionData.getRightMost();
        this.boundsBottom = regionData.getBottomMost();
    }

    void updateNeighbours() {
        int accessibleBestDist = Integer.MAX_VALUE;
        int inaccessibleBestDist = Integer.MAX_VALUE;

        final List<Region> neighbours = new ArrayList<>();
        for (int i = 0; i < regionData.getNeighborCount(); i++) {
            final Region region = game.getRegion(regionData.getNeighbors(i));
            neighbours.add(region);
            final int d = getDistance(region);
            if (region.isAccessible()) {
                if (d < accessibleBestDist) {
                    closestAccessibleRegion = region;
                    accessibleBestDist = d;
                }
            } else if (d < inaccessibleBestDist) {
                closestInaccessibleRegion = region;
                inaccessibleBestDist = d;
            }
        }
        this.neighbours = Collections.unmodifiableList(neighbours);
    }

    public int getID() {
        return id;
    }

    public int getRegionGroupID() {
        return regionGroupID;
    }

    public Position getCenter() {
        return center;
    }

    public boolean isHigherGround() {
        return higherGround;
    }

    public int getDefensePriority() {
        return defensePriority;
    }

    public boolean isAccessible() {
        return accessible;
    }

    public List<Region> getNeighbors() {
        return neighbours;
    }

    public int getBoundsLeft() {
        return boundsLeft;
    }

    public int getBoundsTop() {
        return boundsTop;
    }

    public int getBoundsRight() {
        return boundsRight;
    }

    public int getBoundsBottom() {
        return boundsBottom;
    }

    public Region getClosestAccessibleRegion() {
        return closestAccessibleRegion;
    }

    public Region getClosestInaccessibleRegion() {
        return closestInaccessibleRegion;
    }

    public int getDistance(final Region other) {
        return getCenter().getApproxDistance(other.getCenter());
    }

    public List<Unit> getUnits() {
        return game.getUnitsInRectangle(getBoundsLeft(), getBoundsTop(), getBoundsRight(), getBoundsBottom(),
                u -> equals(u.getRegion()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Region region = (Region) o;
        return id == region.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(final Region other) {
        return id - other.id;
    }
}
