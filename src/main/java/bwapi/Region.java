package bwapi;


import java.util.HashSet;
import java.util.Set;

public class Region {
    private final Client.GameData.RegionData regionData;
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

    Region(final Client.GameData.RegionData regionData, final Game game) {
        this.regionData = regionData;
        this.game = game;
        this.id = regionData.id();
        this.regionGroupID = regionData.islandID();
        this.center = new Position(regionData.centerX(), regionData.centerY());
        this.higherGround = regionData.isHigherGround();
        this.defensePriority = regionData.priority();
        this.accessible = regionData.isAccessible();
        this.boundsLeft = regionData.leftMost();
        this.boundsTop = regionData.topMost();
        this.boundsRight = regionData.rightMost();
        this.boundsBottom = regionData.bottomMost();
    }

    void updateNeighbours() {
        int accessibleBestDist = Integer.MAX_VALUE;
        int inaccessibleBestDist = Integer.MAX_VALUE;

        for (int i = 0; i < regionData.neighborCount(); i++) {
            final Region region = game.getRegion(regionData.neighbor(i));
            neighbours.add(region);
            int d = getDistance(region);
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
                (u -> equals(u.getRegion())));
    }

    public boolean equals(Object that) {
        if (!(that instanceof Region)) {
            return false;
        }
        return getID() == ((Region) that).getID();
    }

    public int hashCode() {
        return getID();
    }
}
