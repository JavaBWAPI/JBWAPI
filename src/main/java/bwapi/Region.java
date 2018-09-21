package bwapi;

import bwapi.point.Position;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class Region {
    private final int id;

    Region(int id) {
        this.id = id;
    }

    public int getID() {
        return id;
    }

    public int getRegionGroupID() {
        return -1;
    }

    public Position getCenter() {
        return null;
    }

    public boolean isHigherGround() {
        return false;
    }

    public int getDefensePriority() {
        return -1;
    }

    public boolean isAccessible() {
        return false;
    }

    public List<Region> getNeighbors() {
        return null;
    }

    public int getBoundsLeft() {
        return -1;
    }

    public int getBoundsTop() {
        return -1;
    }

    public int getBoundsRight() {
        return -1;
    }

    public int getBoundsBottom() {
        return -1;
    }

    public Region getClosestAccessibleRegion() {
        return null;
    }

    public Region getClosestInaccessibleRegion() {
        return null;
    }

    public int getDistance(Region other) {
        return -1;
    }

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
