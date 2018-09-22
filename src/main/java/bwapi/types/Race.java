package bwapi.types;


import static bwapi.types.UnitType.*;

public enum Race{
    Zerg(0),
    Terran(1),
    Protoss(2),
    Other(3),
    Unused(4),
    Select(5),
    Random(6),
    None(7),
    Unknown(8);

    public static Race[] races = {
            Zerg,  Terran, Protoss, Other, Unused, Select, Random, None, Unknown
    };

    public final int id;

    Race(int id){
        this.id = id;
    }

    public String toString() {
        return null;
    }

    public UnitType getWorker() {
        return workerTypes[id];
    }

    //@since 4.2.0
    public UnitType getResourceDepot() {
        return baseTypes[id];
    }

    public UnitType getCenter() {
        return getResourceDepot();
    }

    public UnitType getRefinery() {
        return refineryTypes[id];
    }

    public UnitType getTransport() {
        return transportTypes[id];
    }

    public UnitType getSupplyProvider() {
        return supplyTypes[id];
    }

    // DATA

    static UnitType workerTypes[] = {
            Zerg_Drone, Terran_SCV, Protoss_Probe,
            UnitType.None, UnitType.None, UnitType.None, // unused
            UnitType.Unknown, UnitType.None, UnitType.Unknown // random, none, unk
    };

    static UnitType baseTypes[] = {
            Zerg_Hatchery, Terran_Command_Center, Protoss_Nexus,
            UnitType.None, UnitType.None, UnitType.None, // unused
            UnitType.Unknown, UnitType.None, UnitType.Unknown // random, none, unk
    };

    static UnitType refineryTypes[] = {
        Zerg_Extractor, Terran_Refinery, Protoss_Assimilator,
            UnitType.None, UnitType.None, UnitType.None, // unused
            UnitType.Unknown, UnitType.None, UnitType.Unknown // random, none, unk
    };

    static UnitType transportTypes[] = {
            Zerg_Overlord, Terran_Dropship, Protoss_Shuttle,
            UnitType.None, UnitType.None, UnitType.None, // unused
            UnitType.Unknown, UnitType.None, UnitType.Unknown // random, none, unk
    };

    static UnitType supplyTypes[] = {
            Zerg_Overlord, Terran_Supply_Depot, Protoss_Pylon,
            UnitType.None, UnitType.None, UnitType.None, // unused
            UnitType.Unknown, UnitType.None, UnitType.Unknown // random, none, unk
    };
}
