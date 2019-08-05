package bwapi;

import bwapi.ClientData.BulletData;

import java.util.Objects;

public class Bullet {
    private final BulletData bulletData;
    private final int id;
    private final Game game;

    Bullet(final BulletData bulletData, final int id, final Game game) {
        this.bulletData = bulletData;
        this.id = id;
        this.game = game;
    }

    public int getID() {
        return id;
    }

    public boolean exists() {
        return bulletData.getExists();
    }

    public Player getPlayer() {
        return game.getPlayer(bulletData.getPlayer());
    }

    public BulletType getType() {
        return BulletType.idToEnum[bulletData.getType()];
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bullet bullet = (Bullet) o;
        return id == bullet.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
