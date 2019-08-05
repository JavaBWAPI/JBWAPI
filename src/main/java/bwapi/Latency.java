package bwapi;

import java.util.Arrays;

public enum Latency {

    SinglePlayer(2),
    LanLow(5),
    LanMedium(7),
    LanHigh(9),
    BattlenetLow(14),
    BattlenetMedium(19),
    BattlenetHigh(24);

    static final Latency[] idToEnum = new Latency[24 + 1];

    static {
        Arrays.stream(Latency.values()).forEach(v -> idToEnum[v.id] = v);
    }

    final int id;

    Latency(final int id) {
        this.id = id;
    }
}
