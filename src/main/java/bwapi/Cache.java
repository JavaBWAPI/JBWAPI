package bwapi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

class Cache<T> {
    private int frame = -1;
    private T obj;

    void set(T obj, int frame) {
        this.frame = frame;
        this.obj = obj;
    }

    boolean valid(int currentFrame) {
        return frame == currentFrame;
    }

    T get() {
        return obj;
    }
}

class IntegerCache extends Cache<Integer> {

    void setOrAdd(int obj, int frame) {
        if (valid(frame)) {
            set(get() + obj, frame);
        }
        else {
            set(obj, frame);
        }
    }
}

class BooleanCache extends Cache<Boolean>{}
class OrderCache extends Cache<Order>{}
class UnitTypeCache extends Cache<UnitType>{}
class UpgradeTypeCache extends Cache<UpgradeType>{}
class TechTypeCache extends Cache<TechType>{}

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
                    connectedUnits.computeIfAbsent(owner, k -> new ArrayList<>()).add(u);
                }
            }
            lastUpdate = frame;
        }
        List<Unit> connected = connectedUnits.get(unit);
        return connected == null ?
                Collections.emptyList() :
                Collections.unmodifiableList(connected);
    }

    void reset() {
        lastUpdate = -1;
        connectedUnits.clear();
    }
}

/**
 * Lazily filters and caches an unmodifiable List<Unit> at most once per frame.
 * Only callers of the corresponding getter pay for the filtering, and at most once per frame.
 */
class UnitListCache {
    private final Game game;
    private final Supplier<List<Unit>> source;
    private final Predicate<Unit> filter;
    private int lastFrame = -1;
    private List<Unit> cached = Collections.emptyList();

    UnitListCache(final Game game, final Predicate<Unit> filter) {
        this(game, game::getAllUnits, filter);
    }

    UnitListCache(final Game game, final Supplier<List<Unit>> source, final Predicate<Unit> filter) {
        this.game = game;
        this.source = source;
        this.filter = filter;
    }

    List<Unit> get() {
        final int frame = game.getFrameCount();
        if (lastFrame != frame) {
            cached = source.get().stream()
                    .filter(filter)
                    .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
            lastFrame = frame;
        }
        return cached;
    }

    void reset() {
        lastFrame = -1;
        cached = Collections.emptyList();
    }
}