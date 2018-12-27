package bwapi;

public enum CommandType implements WithId {
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

    public final int value;

    CommandType(final int value) {
        this.value = value;
    }

    @Override
    public int getId() {
        return value;
    }

    static CommandType withId(int id) {
        if (id < 0) return null;
        CommandType commandType = IdMapper.commandTypeForId[id];
        if (commandType == null) {
            throw new IllegalArgumentException("No CommandType with id " + id);
        }
        return commandType;
    }

    private static class IdMapper {

        static final CommandType[] commandTypeForId = IdMapperHelper.toIdTypeArray(CommandType.class);
    }
}
