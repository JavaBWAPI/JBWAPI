package bwapi;

import bwapi.ClientData.BulletData;

public class Bullet {
    private final BulletData bulletData;
    private final Game game;

    Bullet(final ClientData.BulletData bulletData, final Game game) {
        this.bulletData = bulletData;
        this.game = game;
    }

    public int getID() {
        return bulletData.getId();
    }

    public boolean exists() {
        return bulletData.getExists();
    }

    public Player getPlayer() {
        return game.getPlayer(bulletData.getPlayer());
    }

    public BulletType getType() {
        return BulletType.bulletTypes[bulletData.getType()];
    }

    public Unit getSource() {
        return game.getUnit(bulletData.getSource());
    }

    public Position getPosition() {
        return new Position(bulletData.getPositionX(), bulletData.getPositionY());
    }

    public double getAngle() {
        return bulletData.getAngle();
    }

    public double getVelocityX() {
        return bulletData.getVelocityX();
    }

    public double getVelocityY() {
        return bulletData.getVelocityY();
    }

    public Unit getTarget() {
        return game.getUnit(bulletData.getTarget());
    }

    public Position getTargetPosition() {
        return new Position(bulletData.getTargetPositionX(), bulletData.getTargetPositionY());
    }

    public int getRemoveTimer() {
        return bulletData.getRemoveTimer();
    }

    public boolean isVisible() {
        return isVisible(game.self());
    }

    public boolean isVisible(final Player player) {
        return bulletData.isVisible(player.getID());
    }

    public boolean equals(final Object that) {
        if (!(that instanceof Bullet)) {
            return false;
        }
        return getID() == ((Bullet) that).getID();
    }

    public int hashCode() {
        return getID();
    }

}
