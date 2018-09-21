package bwapi;

import JavaBWAPIBackend.Client.GameData.ForceData;

import java.util.List;

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

    public List<Player> getPlayers() {
        return null;
    }

    public boolean equals(Object that){
        if(!(that instanceof Force)) return false;
        return getID() == ((Force)that).getID();
    }

    public int hashCode(){
        return getID();
    }
}
