package bwapi;

/**
 * Namespace containing unit size types.
 *
 * @see UnitSizeType
 *
 * [View on Starcraft Campendium (Official Website)](http://classic.battle.net/scc/gs/damage.shtml)<br>
 */
public enum UnitSizeType {
    Independent(0),
    Small(1),
    Medium(2),
    Large(3),
    None(4),
    Unknown(5);

    final int id;

    UnitSizeType(final int id) {
        this.id = id;
    }
}
