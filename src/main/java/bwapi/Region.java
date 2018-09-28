package bwapi;

import bwapi.point.Position;

import JavaBWAPIBackend.Client.GameData.RegionData;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

public class Region {
    private final RegionData regionData;
    private final Game game;

    private final Set<Region> neighbours = new HashSet<>();
    private Region closestAccessibleRegion;
    private Region closestInaccessibleRegion;

    Region(final RegionData regionData, final Game game) {
        this.regionData = regionData;
        this.game = game;
    }

    void updateNeighbours() {
        int accessibleBestDist = Integer.MAX_VALUE;
        int inaccessibleBestDist = Integer.MAX_VALUE;

        for (int i=0; i < regionData.neighborCount(); i++) {
            final Region region = game.getRegion(regionData.neighbor(i));
            neighbours.add(region);
            int d = getDistance(region);
            if (region.isAccessible()) {
                if (d < accessibleBestDist) {
                    closestAccessibleRegion = region;
                    accessibleBestDist = d;
                }
            }
            else if (d < inaccessibleBestDist) {
                closestInaccessibleRegion = region;
                inaccessibleBestDist = d;
            }
        }
    }

    public int getID() {
        return regionData.id();
    }

    public int getRegionGroupID() {
        return regionData.islandID();
    }

    public Position getCenter() {
        return new Position(regionData.centerX(), regionData.centerY());
    }

    public boolean isHigherGround() {
        return regionData.isHigherGround();
    }

    public int getDefensePriority() {
        return regionData.priority();
    }

    public boolean isAccessible() {
        return regionData.isAccessible();
    }

    public Set<Region> getNeighbors() {
        return new HashSet<>(neighbours);
    }

    public int getBoundsLeft() {
        return regionData.leftMost();
    }

    public int getBoundsTop() {
        return regionData.topMost();
    }

    public int getBoundsRight() {
        return regionData.rightMost();
    }

    public int getBoundsBottom() {
        return regionData.bottomMost();
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

    public boolean equals(Object that){
        if(!(that instanceof Region)){
            return false;
        }
        return getID() == ((Region)that).getID();
    }

    public int hashCode(){
        return getID();
    }
}
