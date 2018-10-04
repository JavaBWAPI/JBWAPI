package bwapi;

import java.util.Arrays;

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

    public static PlayerType[] playerTypes = new PlayerType[12 + 1];

    static {
        Arrays.stream(PlayerType.values()).forEach(v -> playerTypes[v.id] = v);
    }

    private int id;

    PlayerType(int id) {
        this.id = id;
    }

    public boolean isLobbyType() {
        return this == EitherPreferComputer || this == EitherPreferHuman || isRescueNeutralType();
    }

    public boolean isGameType() {
        return this == Player || this == Computer || isRescueNeutralType();
    }

    private boolean isRescueNeutralType() {
        return this == RescuePassive || this == RescueActive || this == Neutral;
    }
}
