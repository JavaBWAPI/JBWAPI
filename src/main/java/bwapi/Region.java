package bwapi;


import bwapi.ClientData.RegionData;
import java.util.HashSet;
import java.util.Set;

public class Region {
    private final RegionData regionData;
    private final Game game;

    private final Set<Region> neighbours = new HashSet<>();
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

    Region(final ClientData.RegionData regionData, final Game game) {
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

    public Set<Region> getNeighbors() {
        return new HashSet<>(neighbours);
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

    public Set<Unit> getUnits() {
        return game.getUnitsInRectangle(getBoundsLeft(), getBoundsTop(), getBoundsRight(), getBoundsBottom(),
                u -> equals(u.getRegion()));
    }

    public boolean equals(final Object that) {
        if (!(that instanceof Region)) {
            return false;
        }
        return getID() == ((Region) that).getID();
    }

    public int hashCode() {
        return getID();
    }
}
