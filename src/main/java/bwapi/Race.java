package bwapi;


import java.util.Arrays;

import static bwapi.UnitType.*;

/**
 * The {@link Race} enum is used to get information about a particular race.
 *
 * For example, the default worker and supply provider {@link UnitType}.
 *
 * As you should already know, Starcraft has three races: @Terran , @Protoss , and @Zerg .
 * @see {@link UnitType#getRace}, {@link Player#getRace}
 */
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

    /**
     * Retrieves the default worker type for this {@link Race}.
     *
     * In Starcraft, workers are the units that are used to construct structures.
     *
     * @return {@link UnitType} of the worker that this race uses.
     */
    public UnitType getWorker() {
        return workerTypes[id];
    }

    /**
     * Retrieves the default resource depot {@link UnitType} that workers of this race can
     * construct and return resources to.
     *
     * In Starcraft, the center is the very first structure of the Race's technology
     * tree. Also known as its base of operations or resource depot.
     *
     * @return {@link UnitType} of the center that this race uses.
     *
     * @since 4.2.0
     */
    public UnitType getResourceDepot() {
        return baseTypes[id];
    }

    /**
     * Deprecated. Use getResourceDepot instead.
     * @deprecated As of 4.2.0 due to naming inconsistency. Use #getResourceDepot instead.
     * See https://github.com/bwapi/bwapi/issues/621 for more information.
     */
    @Deprecated
    public UnitType getCenter() {
        return getResourceDepot();
    }

    /**
     * Retrieves the default structure UnitType for this Race that is used to harvest gas from @Geysers.
     *
     * In Starcraft, you must first construct a structure over a @Geyser in order to
     * begin harvesting Vespene Gas.
     *
     * @return {@link UnitType} of the structure used to harvest gas.
     */
    public UnitType getRefinery() {
        return refineryTypes[id];
    }

    /**
     * Retrieves the default transport {@link UnitType} for this race that is used to transport ground
     * units across the map.
     *
     * In Starcraft, transports will allow you to carry ground units over unpassable
     * terrain.
     *
     * @return {@link UnitType} for transportation.
     */
    public UnitType getTransport() {
        return transportTypes[id];
    }

    /**
     * Retrieves the default supply provider {@link UnitType} for this race that is used to  construct
     * units.
     *
     * In Starcraft, training, morphing, or warping in units requires that the player
     * has sufficient supply available for their Race.
     *
     * @return {@link UnitType} that provides the player with supply.
     */
    public UnitType getSupplyProvider() {
        return supplyTypes[id];
    }
}
