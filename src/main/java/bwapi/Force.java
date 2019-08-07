package bwapi;

import bwapi.ClientData.ForceData;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Force implements Comparable<Force>{
    private final Game game;

    private final int id;
    private final String name;

    Force(final ForceData forceData, int id, final Game game) {
        this.game = game;
        this.id = id;
        this.name = forceData.getName();
    }

    public int getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<Player> getPlayers() {
        return game.getPlayers().stream()
                .filter(p -> equals(p.getForce()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Force force = (Force) o;
        return id == force.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public int compareTo(final Force other) {
        return id - other.id;
    }
}
