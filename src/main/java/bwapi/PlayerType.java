package bwapi;

import java.util.Arrays;

/**
 * Represents the type of controller for the player slot (i.e. human, computer).
 */
public enum PlayerType {
    None(0),
    Computer(1),
    Player(2),
    RescuePassive(3),
    RescueActive(4),
    EitherPreferComputer(5),
    EitherPreferHuman(6),
    Neutral(7),
    Closed(8),
    Observer(9),
    PlayerLeft(10),
    ComputerLeft(11),
    Unknown(12);

    static final PlayerType[] idToEnum = new PlayerType[12 + 1];

    static {
        Arrays.stream(PlayerType.values()).forEach(v -> idToEnum[v.id] = v);
    }

    final int id;

    PlayerType(final int id) {
        this.id = id;
    }

    /**
     * Identifies whether or not this type is used for the pre-game lobby.
     * A type such as PlayerType.ComputerLeft would only appear in-game when a computer
     * player is defeated.
     *
     * @return true if this type can appear in the pre-game lobby, false otherwise.
     */
    public boolean isLobbyType() {
        return this == EitherPreferComputer || this == EitherPreferHuman || isRescueNeutralType();
    }

    /**
     * Identifies whether or not this type is used in-game. A type such as
     * PlayerType.Closed would not be a valid in-game type.
     *
     * @return true if the type can appear in-game, false otherwise.
     * @see isLobbyType
     */
    public boolean isGameType() {
        return this == Player || this == Computer || isRescueNeutralType();
    }

    private boolean isRescueNeutralType() {
        return this == RescuePassive || this == RescueActive || this == Neutral;
    }
}
