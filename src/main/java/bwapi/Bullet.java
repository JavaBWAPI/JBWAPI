package bwapi;

public class Bullet {
    private final Client.GameData.BulletData bulletData;
    private final Game game;

    Bullet(final Client.GameData.BulletData bulletData, final Game game) {
        this.bulletData = bulletData;
        this.game = game;
    }

    public int getID() {
        return bulletData.id();
    }

    public boolean exists() {
        return bulletData.exists();
    }

    public Player getPlayer() {
        return game.getPlayer(bulletData.player());
    }

    public BulletType getType() {
        return BulletType.bulletTypes[bulletData.type()];
    }

    public Unit getSource() {
        return game.getUnit(bulletData.source());
    }

    public Position getPosition() {
        return new Position(bulletData.positionX(), bulletData.positionY());
    }

    public double getAngle() {
        return bulletData.angle();
    }

    public double getVelocityX() {
        return bulletData.velocityX();
    }

    public double getVelocityY() {
        return bulletData.velocityY();
    }

    public Unit getTarget() {
        return game.getUnit(bulletData.target());
    }

    public Position getTargetPosition() {
        return new Position(bulletData.targetPositionX(), bulletData.targetPositionY());
    }

    public int getRemoveTimer() {
        return bulletData.removeTimer();
    }

    public boolean isVisible() {
        return isVisible(game.self());
    }

    public boolean isVisible(final Player player) {
        return bulletData.isVisible(player.getID());
    }

    public boolean equals(Object that) {
        if (!(that instanceof Bullet)) return false;
        return getID() == ((Bullet) that).getID();
    }

    public int hashCode() {
        return getID();
    }

}
