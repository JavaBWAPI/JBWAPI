package bwapi;

import java.util.*;
import java.util.function.Function;

class ConnectedUnitCache {
    private int lastUpdate = -1;
    private final Map<Unit, List<Unit>> connectedUnits = new HashMap<>();
    private final Function<Unit, Unit> condition;
    private final Game game;

    ConnectedUnitCache(final Game game, final Function<Unit, Unit> condition) {
        this.game = game;
        this.condition = condition;
    }

    /**
     * Lazily update connectedUnits. Only users of the calls pay for it, and only
     * pay once per frame.
     * Avoids previous O(n^2) implementation which would be costly for
     * lategame carrier fights
     */
    List<Unit> getConnected(final Unit unit) {
        final int frame = game.getFrameCount();
        if (lastUpdate < frame) {
            connectedUnits.values().forEach(List::clear);
            for (final Unit u : game.getAllUnits()) {
                final Unit owner = condition.apply(u);
                if (owner != null) {
                    if (!connectedUnits.containsKey(owner)) {
                        connectedUnits.put(owner, new ArrayList<>());
                    }
                    connectedUnits.get(owner).add(u);
                }
            }
            lastUpdate = frame;
        }
        if (!connectedUnits.containsKey(unit)) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(connectedUnits.get(unit));
    }
    void reset() {
        lastUpdate = -1;
        connectedUnits.clear();
    }
}