package bwapi;

import JavaBWAPIBackend.Client.GameData.ForceData;

import java.util.Set;
import java.util.stream.Collectors;

public class Force {
    private final ForceData forceData;
    private final Game game;

    Force(final ForceData forceData, final Game game) {
        this.forceData = forceData;
        this.game = game;
    }

    public int getID() {
        return forceData.id();
    }

    public String getName() {
        return forceData.name();
    }

    public Set<Player> getPlayers() {
        return game.getPlayers().stream()
                .filter(p -> equals(p.getForce()))
                .collect(Collectors.toSet());
    }

    public boolean equals(Object that){
        if(!(that instanceof Force)) return false;
        return getID() == ((Force)that).getID();
    }

    public int hashCode(){
        return getID();
    }
}
