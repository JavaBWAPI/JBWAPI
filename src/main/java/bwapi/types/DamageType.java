package bwapi.types;

public enum DamageType {
    Independent(0),
    Explosive(1),
    Concussive(2),
    Normal(3),
    Ignore_Armor(4),
    None(5),
    Unknown(6);

    private int value;

    public int getValue(){
        return value;
    }

    DamageType(int value){
        this.value = value;
    }
}
