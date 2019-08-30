package bwapi;

/**
 * Damage types are used in Broodwar to determine the amount of damage that will be
 * done to a unit.
 *
 * This corresponds with UnitSizeType to determine the damage done to
 * a unit.
 *
 * @see WeaponType
 * @see DamageType
 * @see UnitSizeType
 *
 * [View on Liquipedia](http://wiki.teamliquid.net/starcraft/Damage_Type)<br>
 * [View on Starcraft Campendium (Official Website)](http://classic.battle.net/scc/gs/damage.shtml)<br>
 * [View on Starcraft Wikia](http://starcraft.wikia.com/wiki/Damage_types)<br>
 */
public enum DamageType {
    Independent(0),
    Explosive(1),
    Concussive(2),
    Normal(3),
    Ignore_Armor(4),
    None(5),
    Unknown(6);

    final int id;

    DamageType(final int id) {
        this.id = id;
    }
}
