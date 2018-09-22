package bwapi;

import bwapi.point.Position;

import JavaBWAPIBackend.Client.GameData.RegionData;

import java.util.List;

public class Region {
    private final RegionData regionData;
    private final Game game;

    Region(final RegionData regionData, final Game game) {
        this.regionData = regionData;
        this.game = game;
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

    //TODO
    public List<Region> getNeighbors() {
        return null;
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

    //TODO
    public Region getClosestAccessibleRegion() {
        return null;
    }

    //TODO
    public Region getClosestInaccessibleRegion() {
        return null;
    }

    //TODO
    public int getDistance(Region other) {
        return -1;
    }

    //TODO
    public List<Unit> getUnits() {
        return null;
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
