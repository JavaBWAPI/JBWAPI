package bwapi;

import bwapi.ClientData.BulletData;

import java.util.Objects;

/**
 * An object representing a bullet or missile spawned from an attack.
 * <p>
 * The Bullet interface allows you to detect bullets, missiles, and other types
 * of non-melee attacks or special abilities that would normally be visible through
 * human eyes (A lurker spike or a Queen's flying parasite), allowing quicker reaction
 * to unavoidable consequences.
 * <p>
 * For example, ordering medics to restore units that are about to receive a lockdown
 * to compensate for latency and minimize its effects. You can't know entirely which unit
 * will be receiving a lockdown unless you can detect the lockdown missile using the
 * {@link Bullet} class.
 * <p>
 * {@link Bullet} objects are re-used after they are destroyed, however their ID is updated when it
 * represents a new Bullet.
 * <p>
 * If {@link Flag#CompleteMapInformation} is disabled, then a {@link Bullet} is accessible if and only if
 * it is visible. Otherwise if {@link Flag#CompleteMapInformation} is timersEnabled, then all Bullets
 * in the game are accessible.
 *
 * @see Game#getBullets
 * @see Bullet#exists
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
     * Retrieves a unique identifier for the current {@link Bullet}.
     *
     * @return An integer value containing the identifier.
     */
    public int getID() {
        return id;
    }

    /**
     * Checks if the {@link Bullet} exists in the view of the BWAPI player.
     * <p>
     * If {@link Flag#CompleteMapInformation} is disabled, and a {@link Bullet} is not visible, then the
     * return value will be false regardless of the Bullet's true existence. This is because
     * absolutely no state information on invisible enemy bullets is made available to the AI.
     * <p>
     * If {@link Flag#CompleteMapInformation} is timersEnabled, then this function is accurate for all
     * {@link Bullet} information.
     *
     * @return true if the bullet exists or is visible, false if the bullet was destroyed or has gone out of scope.
     * @see #isVisible
     * @see Unit#exists
     */
    public boolean exists() {
        return bulletData.getExists();
    }

    /**
     * Retrieves the {@link Player} interface that owns the Bullet.
     *
     * @return The owning {@link Player} object. Returns null if the {@link Player} object for this {@link Bullet} is inaccessible.
     */
    public Player getPlayer() {
        return game.getPlayer(bulletData.getPlayer());
    }

    /**
     * Retrieves the type of this {@link Bullet}.
     *
     * @return A {@link BulletType} representing the Bullet's type. Returns {@link BulletType#Unknown} if the {@link Bullet} is inaccessible.
     */
    public BulletType getType() {
        return BulletType.idToEnum[bulletData.getType()];
    }

    /**
     * Retrieves the {@link Unit} that the {@link Bullet} spawned from.
     *
     * @return The owning {@link Unit} object. Returns null if the source can not be identified or is inaccessible.
     * @see #getTarget
     */
    public Unit getSource() {
        return game.getUnit(bulletData.getSource());
    }

    /**
     * Retrieves the Bullet's current position.
     *
     * @return A {@link Position} containing the Bullet's current coordinates. Returns {@link Position#Unknown} if the {@link Bullet} is inaccessible.
     * @see #getTargetPosition
     */
    public Position getPosition() {
        return new Position(bulletData.getPositionX(), bulletData.getPositionY());
    }

    /**
     * Retrieve's the direction the {@link Bullet} is facing. If the angle is 0, then
     * the {@link Bullet} is facing right.
     *
     * @return A double representing the direction the {@link Bullet} is facing. Returns 0.0 if the bullet is inaccessible.
     */
    public double getAngle() {
        return bulletData.getAngle();
    }

    /**
     * Retrieves the X component of the Bullet's velocity, measured in pixels per frame.
     *
     * @return A double representing the number of pixels moved on the X axis per frame. Returns 0.0 if the {@link Bullet} is inaccessible.
     * @see #getVelocityY
     * @see #getAngle
     */
    public double getVelocityX() {
        return bulletData.getVelocityX();
    }

    /**
     * Retrieves the Y component of the Bullet's velocity, measured in pixels per frame.
     *
     * @return A double representing the number of pixels moved on the Y axis per frame. Returns 0.0 if the {@link Bullet} is inaccessible.
     * @see #getVelocityX
     * @see #getAngle
     */
    public double getVelocityY() {
        return bulletData.getVelocityY();
    }

    /**
     * Retrieves the Unit interface that the {@link Bullet} is heading to.
     *
     * @return The target Unit object, if one exists. Returns null if the Bullet's target {@link Unit} is inaccessible, the {@link Bullet} is targetting the ground, or if the {@link Bullet} itself is inaccessible.
     * @see #getTargetPosition
     * @see #getSource
     */
    public Unit getTarget() {
        return game.getUnit(bulletData.getTarget());
    }

    /**
     * Retrieves the target position that the {@link Bullet} is heading to.
     *
     * @return A {@link Position} indicating where the {@link Bullet} is headed. Returns {@link Position#Unknown} if the bullet is inaccessible.
     * @see #getTarget
     * @see #getPosition
     */
    public Position getTargetPosition() {
        return new Position(bulletData.getTargetPositionX(), bulletData.getTargetPositionY());
    }

    /**
     * Retrieves the timer that indicates the Bullet's life span.
     * <p>
     * Bullets are not permanent objects, so they will often have a limited life span.
     * This life span is measured in frames. Normally a Bullet will reach its target
     * before being removed.
     *
     * @return An integer representing the remaining number of frames until the {@link Bullet} self-destructs. Returns 0 if the {@link Bullet} is inaccessible.
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
     * @param player If this parameter is specified, then the Bullet's visibility to the given player is checked. If this parameter is omitted, then a default value of null is used, which will check if the BWAPI player has vision of the {@link Bullet}.
     * @return true if the {@link Bullet} is visible to the specified player, false if the {@link Bullet} is not visible to the specified player.
     */
    public boolean isVisible(final Player player) {
        if (player == null) {
            return isVisible();
        }
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
