package bwapi;


import java.util.Arrays;

import static bwapi.UnitType.*;

public enum Race {
    Zerg(0),
    Terran(1),
    Protoss(2),
    Other(3),
    Unused(4),
    Select(5),
    Random(6),
    None(7),
    Unknown(8);

    static final UnitType[] workerTypes = {
            Zerg_Drone, Terran_SCV, Protoss_Probe,
            UnitType.None, UnitType.None, UnitType.None, // unused
            UnitType.Unknown, UnitType.None, UnitType.Unknown // random, none, unk
    };
    static final UnitType[] baseTypes = {
            Zerg_Hatchery, Terran_Command_Center, Protoss_Nexus,
            UnitType.None, UnitType.None, UnitType.None, // unused
            UnitType.Unknown, UnitType.None, UnitType.Unknown // random, none, unk
    };
    static final UnitType[] refineryTypes = {
            Zerg_Extractor, Terran_Refinery, Protoss_Assimilator,
            UnitType.None, UnitType.None, UnitType.None, // unused
            UnitType.Unknown, UnitType.None, UnitType.Unknown // random, none, unk
    };
    static final UnitType[] transportTypes = {
            Zerg_Overlord, Terran_Dropship, Protoss_Shuttle,
            UnitType.None, UnitType.None, UnitType.None, // unused
            UnitType.Unknown, UnitType.None, UnitType.Unknown // random, none, unk
    };
    static final UnitType[] supplyTypes = {
            Zerg_Overlord, Terran_Supply_Depot, Protoss_Pylon,
            UnitType.None, UnitType.None, UnitType.None, // unused
            UnitType.Unknown, UnitType.None, UnitType.Unknown // random, none, unk
    };

    static final Race[] idToEnum = new Race[8 + 1];

    static {
        Arrays.stream(Race.values()).forEach(v -> idToEnum[v.id] = v);
    }

    final int id;

    Race(final int id) {
        this.id = id;
    }

    public UnitType getWorker() {
        return workerTypes[id];
    }

    // DATA

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
}
