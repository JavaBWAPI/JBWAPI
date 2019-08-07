package bwapi;

import bwapi.ClientData.BulletData;

import java.util.Objects;

/**
 * An object representing a bullet or missile spawned from an attack.
 *
 * The Bullet interface allows you to detect bullets, missiles, and other types
 * of non-melee attacks or special abilities that would normally be visible through
 * human eyes (A lurker spike or a Queen's flying parasite), allowing quicker reaction
 * to unavoidable consequences.
 *
 * For example, ordering medics to restore units that are about to receive a lockdown
 * to compensate for latency and minimize its effects. You can't know entirely which unit
 * will be receiving a lockdown unless you can detect the lockdown missile using the
 * Bullet class.
 *
 * Bullet objects are re-used after they are destroyed, however their ID is updated when it
 * represents a new Bullet.
 *
 * If Flag#CompleteMapInformation is disabled, then a Bullet is accessible if and only if
 * it is visible. Otherwise if Flag#CompleteMapInformation is enabled, then all Bullets
 * in the game are accessible.
 * @see Game#getBullets, Bullet#exists
 */
public class Bullet implements Comparable<Bullet> {
    private final BulletData bulletData;
    private final int id;
    private final Game game;

    Bullet(final BulletData bulletData, final int id, final Game game) {
        this.bulletData = bulletData;
        this.id = id;
        this.game = game;
    }

    /**
     * Retrieves a unique identifier for the current Bullet.
     *
     * @return  An integer value containing the identifier.
     */
    public int getID() {
        return id;
    }

    /**
     * Checks if the Bullet exists in the view of the BWAPI player.
     *
     * @retval true If the bullet exists or is visible.
     * @retval false If the bullet was destroyed or has gone out of scope.
     *
     * If Flag#CompleteMapInformation is disabled, and a Bullet is not visible, then the
     * return value will be false regardless of the Bullet's true existence. This is because
     * absolutely no state information on invisible enemy bullets is made available to the AI.
     *
     * If Flag#CompleteMapInformation is enabled, then this function is accurate for all
     * Bullet information.
     * @see isVisible, Unit#exists
     */
    public boolean exists() {
        return bulletData.getExists();
    }

    /**
     * Retrieves the Player interface that owns the Bullet.
     *
     * @retval null If the Player object for this Bullet is inaccessible.
     *
     * @return  The owning Player object.
     */
    public Player getPlayer() {
        return game.getPlayer(bulletData.getPlayer());
    }

    /**
     * Retrieves the type of this Bullet.
     *
     * @retval BulletType.Unknown if the Bullet is inaccessible.
     *
     * @return  A BulletType representing the Bullet's type.
     */
    public BulletType getType() {
        return BulletType.idToEnum[bulletData.getType()];
    }

    /**
     * Retrieves the Unit interface that the Bullet spawned from.
     *
     * @retval null If the source can not be identified or is inaccessible.
     *
     * @return  The owning Unit object.
     * @see getTarget
     */
    public Unit getSource() {
        return game.getUnit(bulletData.getSource());
    }

    /**
     * Retrieves the Bullet's current position.
     *
     * @retval Position.Unknown If the Bullet is inaccessible.
     *
     * @return  A Position containing the Bullet's current coordinates.
     * @see getTargetPosition
     */
    public Position getPosition() {
        return new Position(bulletData.getPositionX(), bulletData.getPositionY());
    }

    /**
     * Retrieve's the direction the Bullet is facing. If the angle is 0, then
     * the Bullet is facing right.
     *
     * @retval 0.0 If the bullet is inaccessible.
     *
     * @return  A double representing the direction the Bullet is facing.
     */
    public double getAngle() {
        return bulletData.getAngle();
    }

    /**
     * Retrieves the X component of the Bullet's velocity, measured in pixels per frame.
     *
     * @retval 0.0 if the Bullet is inaccessible.
     *
     * @return  A double representing the number of pixels moved on the X axis per frame.
     *
     * @see getVelocityY, getAngle
     */
    public double getVelocityX() {
        return bulletData.getVelocityX();
    }

    /**
     * Retrieves the Y component of the Bullet's velocity, measured in pixels per frame.
     *
     * @retval 0.0 if the Bullet is inaccessible.
     *
     * @return  A double representing the number of pixels moved on the Y axis per frame.
     *
     * @see getVelocityX, getAngle
     */
    public double getVelocityY() {
        return bulletData.getVelocityY();
    }

    /**
     * Retrieves the Unit interface that the Bullet is heading to.
     *
     * @retval null If the Bullet's target Unit is inaccessible, the Bullet is targetting the
     * ground, or if the Bullet itself is inaccessible.
     *
     * @return  The target Unit object, if one exists.
     * @see getTargetPosition, getSource
     */
    public Unit getTarget() {
        return game.getUnit(bulletData.getTarget());
    }

    /**
     * Retrieves the target position that the Bullet is heading to.
     *
     * @retval Position.Unknown If the bullet is inaccessible.
     *
     * @return  A Position indicating where the Bullet is headed.
     * @see getTarget, getPosition
     */
    public Position getTargetPosition() {
        return new Position(bulletData.getTargetPositionX(), bulletData.getTargetPositionY());
    }

    /**
     * Retrieves the timer that indicates the Bullet's life span.
     *
     * Bullets are not permanent objects, so they will often have a limited life span.
     * This life span is measured in frames. Normally a Bullet will reach its target
     * before being removed.
     *
     * @retval 0 If the Bullet is inaccessible.
     *
     * @return  An integer representing the remaining number of frames until the Bullet self-destructs.
     */
    public int getRemoveTimer() {
        return bulletData.getRemoveTimer();
    }

    public boolean isVisible() {
        return isVisible(game.self());
    }

    /**
     * Retrieves the visibility state of the Bullet.
     *
     * @param player If this parameter is specified, then the Bullet's visibility to the given player is checked. If this parameter is omitted, then a default value of null is used, which will check if the BWAPI player has vision of the Bullet.
     *
     * @note If \c player is null and game.self() is also null, then the visibility of
     * the Bullet is determined by checking if at least one other player has vision of the
     * Bullet.
     *
     * @retval true If the Bullet is visible to the specified player.
     * @retval false If the Bullet is not visible to the specified player.
     */
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

    @Override
    public int compareTo(final Bullet other) {
        return id - other.id;
    }
}
