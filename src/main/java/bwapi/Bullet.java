package bwapi;

import bwapi.point.Position;
import bwapi.types.BulletType;

public class Bullet {
    private final int id;

    Bullet(int id) {
        this.id = id;
    }

    public int getID() {
        return  id;
    }

    public boolean exists() {
        return false;
    }

    public Player getPlayer() {
        return null;
    }

    public BulletType getType() {
        return null;
    }

    public Unit getSource() {
        return null;
    }

    public Position getPosition() {
        return null;
    }

    public double getAngle() {
        return -1;
    }

    public double getVelocityX() {
        return -1;
    }

    public double getVelocityY() {
        return -1;
    }

    public Unit getTarget() {
        return null;
    }

    public Position getTargetPosition() {
        return null;
    }

    public int getRemoveTimer() {
        return -1;
    }

    public boolean isVisible() {
        return false;
    }

    public boolean isVisible(Player player) {
        return false;
    }


    public boolean equals(Object that){
        if(!(that instanceof Bullet)) return false;
        return getID() == ((Bullet)that).getID();
    }

    public int hashCode(){
        return getID();
    }

}
