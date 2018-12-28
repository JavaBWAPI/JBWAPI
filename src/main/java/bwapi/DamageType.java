package bwapi;

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
