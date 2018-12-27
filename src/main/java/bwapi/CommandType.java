package bwapi;

import java.util.Arrays;

public enum CommandType {
    None(0),
    SetScreenPosition(1),
    PingMinimap(2),
    EnableFlag(3),
    Printf(4),
    SendText(5),
    PauseGame(6),
    ResumeGame(7),
    LeaveGame(8),
    RestartGame(9),
    SetLocalSpeed(10),
    SetLatCom(11),
    SetGui(12),
    SetFrameSkip(13),
    SetMap(14),
    SetAllies(15),
    SetVision(16),
    SetCommandOptimizerLevel(17),
    SetRevealAll(18);

    static final CommandType[] idToEnum = new CommandType[19];

    static {
        Arrays.stream(CommandType.values()).forEach(v -> idToEnum[v.id] = v);
    }

    public final int id;

    CommandType(final int id) {
        this.id = id;
    }
}
