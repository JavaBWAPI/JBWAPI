package bwapi;

public enum EventType implements WithId {
  MatchStart(0),
  MatchEnd(1),
  MatchFrame(2),
  MenuFrame(3),
  SendText(4),
  ReceiveText(5),
  PlayerLeft(6),
  NukeDetect(7),
  UnitDiscover(8),
  UnitEvade(9),
  UnitShow(10),
  UnitHide(11),
  UnitCreate(12),
  UnitDestroy(13),
  UnitMorph(14),
  UnitRenegade(15),
  SaveGame(16),
  UnitComplete(17),
  //TriggerAction,
  None(18);

  private final int value;

  EventType(int value) {

    this.value = value;
  }

  @Override
  public int getId() {
    return value;
  }

  static EventType withId(int id) {
    if (id < 0) {
      return null;
    }
    EventType eventType = IdMapper.eventTypes[id];
    if (eventType == null) {
      throw new IllegalArgumentException("No EventType with id " + id);
    }
    return eventType;
  }

  private static class IdMapper {

    static final EventType[] eventTypes = IdMapperHelper.toIdTypeArray(EventType.class);
  }

}
