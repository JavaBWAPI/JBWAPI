package bwapi;

import bwapi.ClientData.ForceData;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The Force class is used to get information about each force in a match.
 * Normally this is considered a team.
 *
 * @note It is not called a team because players on the same force do not necessarily need
 * to be allied at the beginning of a match.
 *
 */
public class Force implements Comparable<Force>{
    private final Game game;

    private final int id;
    private final String name;

    Force(final ForceData forceData, int id, final Game game) {
        this.game = game;
        this.id = id;
        this.name = forceData.getName();
    }

    /**
     * Retrieves the unique ID that represents this Force.
     *
     * @return  An integer containing the ID for the Force.
     */
    public int getID() {
        return id;
    }

    /**
     * Retrieves the name of the Force.
     *
     * @return  A String object containing the name of the force.
     *
     *
     * @note Don't forget to use String when passing this parameter to
     * Game#sendText and other variadic functions.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the set of players that belong to this Force.
     *
     * @return  A List<Player> object containing the players that are part of this Force.
     *
     */
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
