package bwapi;
import java.nio.ByteBuffer;
final class ClientData {
  private final ByteBuffer sharedMemory;
  ClientData(ByteBuffer sharedMemory) {
    this.sharedMemory = sharedMemory;
  }
  class UnitCommand {
    static final int SIZE = 24;
    private int myOffset;
    public UnitCommand(int myOffset) {
      this.myOffset = myOffset;
    }
    int getTid() {
      int offset = myOffset + 0;
      return sharedMemory.getInt(offset);
    }
    void setTid(int value) {
      sharedMemory.putInt(myOffset + 0, value);
    }
    int getUnitIndex() {
      int offset = myOffset + 4;
      return sharedMemory.getInt(offset);
    }
    void setUnitIndex(int value) {
      sharedMemory.putInt(myOffset + 4, value);
    }
    int getTargetIndex() {
      int offset = myOffset + 8;
      return sharedMemory.getInt(offset);
    }
    void setTargetIndex(int value) {
      sharedMemory.putInt(myOffset + 8, value);
    }
    int getX() {
      int offset = myOffset + 12;
      return sharedMemory.getInt(offset);
    }
    void setX(int value) {
      sharedMemory.putInt(myOffset + 12, value);
    }
    int getY() {
      int offset = myOffset + 16;
      return sharedMemory.getInt(offset);
    }
    void setY(int value) {
      sharedMemory.putInt(myOffset + 16, value);
    }
    int getExtra() {
      int offset = myOffset + 20;
      return sharedMemory.getInt(offset);
    }
    void setExtra(int value) {
      sharedMemory.putInt(myOffset + 20, value);
    }
  }
  class GameData {
    static final int SIZE = 33017048;
    private int myOffset;
    public GameData(int myOffset) {
      this.myOffset = myOffset;
    }
    int getClient_version() {
      int offset = myOffset + 0;
      return sharedMemory.getInt(offset);
    }
    void setClient_version(int value) {
      sharedMemory.putInt(myOffset + 0, value);
    }
    int getRevision() {
      int offset = myOffset + 4;
      return sharedMemory.getInt(offset);
    }
    void setRevision(int value) {
      sharedMemory.putInt(myOffset + 4, value);
    }
    boolean isDebug() {
      int offset = myOffset + 8;
      return sharedMemory.get(offset) != 0;
    }
    void setIsDebug(boolean value) {      sharedMemory.put(myOffset + 8, (byte) (value ? 1 : 0));
    }
    int getInstanceID() {
      int offset = myOffset + 12;
      return sharedMemory.getInt(offset);
    }
    void setInstanceID(int value) {
      sharedMemory.putInt(myOffset + 12, value);
    }
    int getBotAPM_noselects() {
      int offset = myOffset + 16;
      return sharedMemory.getInt(offset);
    }
    void setBotAPM_noselects(int value) {
      sharedMemory.putInt(myOffset + 16, value);
    }
    int getBotAPM_selects() {
      int offset = myOffset + 20;
      return sharedMemory.getInt(offset);
    }
    void setBotAPM_selects(int value) {
      sharedMemory.putInt(myOffset + 20, value);
    }
    int getForceCount() {
      int offset = myOffset + 24;
      return sharedMemory.getInt(offset);
    }
    void setForceCount(int value) {
      sharedMemory.putInt(myOffset + 24, value);
    }
    ForceData getForces(int i) {
      int offset = myOffset + 28 + 32 * 1 * i;
      return new ForceData(offset);
    }
    int getPlayerCount() {
      int offset = myOffset + 188;
      return sharedMemory.getInt(offset);
    }
    void setPlayerCount(int value) {
      sharedMemory.putInt(myOffset + 188, value);
    }
    PlayerData getPlayers(int i) {
      int offset = myOffset + 192 + 5788 * 1 * i;
      return new PlayerData(offset);
    }
    int getInitialUnitCount() {
      int offset = myOffset + 69648;
      return sharedMemory.getInt(offset);
    }
    void setInitialUnitCount(int value) {
      sharedMemory.putInt(myOffset + 69648, value);
    }
    UnitData getUnits(int i) {
      int offset = myOffset + 69656 + 336 * 1 * i;
      return new UnitData(offset);
    }
    int getUnitArray(int i) {
      int offset = myOffset + 3429656 + 4 * 1 * i;
      return sharedMemory.getInt(offset);
    }
    void setUnitArray(int i, int value) {
      sharedMemory.putInt(myOffset + 3429656 + 4 * 1 * i, value);
    }
    BulletData getBullets(int i) {
      int offset = myOffset + 3436456 + 80 * 1 * i;
      return new BulletData(offset);
    }
    int getNukeDotCount() {
      int offset = myOffset + 3444456;
      return sharedMemory.getInt(offset);
    }
    void setNukeDotCount(int value) {
      sharedMemory.putInt(myOffset + 3444456, value);
    }
    Position getNukeDots(int i) {
      int offset = myOffset + 3444460 + 8 * 1 * i;
      return new Position(offset);
    }
    int getGameType() {
      int offset = myOffset + 3446060;
      return sharedMemory.getInt(offset);
    }
    void setGameType(int value) {
      sharedMemory.putInt(myOffset + 3446060, value);
    }
    int getLatency() {
      int offset = myOffset + 3446064;
      return sharedMemory.getInt(offset);
    }
    void setLatency(int value) {
      sharedMemory.putInt(myOffset + 3446064, value);
    }
    int getLatencyFrames() {
      int offset = myOffset + 3446068;
      return sharedMemory.getInt(offset);
    }
    void setLatencyFrames(int value) {
      sharedMemory.putInt(myOffset + 3446068, value);
    }
    int getLatencyTime() {
      int offset = myOffset + 3446072;
      return sharedMemory.getInt(offset);
    }
    void setLatencyTime(int value) {
      sharedMemory.putInt(myOffset + 3446072, value);
    }
    int getRemainingLatencyFrames() {
      int offset = myOffset + 3446076;
      return sharedMemory.getInt(offset);
    }
    void setRemainingLatencyFrames(int value) {
      sharedMemory.putInt(myOffset + 3446076, value);
    }
    int getRemainingLatencyTime() {
      int offset = myOffset + 3446080;
      return sharedMemory.getInt(offset);
    }
    void setRemainingLatencyTime(int value) {
      sharedMemory.putInt(myOffset + 3446080, value);
    }
    boolean getHasLatCom() {
      int offset = myOffset + 3446084;
      return sharedMemory.get(offset) != 0;
    }
    void setHasLatCom(boolean value) {      sharedMemory.put(myOffset + 3446084, (byte) (value ? 1 : 0));
    }
    boolean getHasGUI() {
      int offset = myOffset + 3446085;
      return sharedMemory.get(offset) != 0;
    }
    void setHasGUI(boolean value) {      sharedMemory.put(myOffset + 3446085, (byte) (value ? 1 : 0));
    }
    int getReplayFrameCount() {
      int offset = myOffset + 3446088;
      return sharedMemory.getInt(offset);
    }
    void setReplayFrameCount(int value) {
      sharedMemory.putInt(myOffset + 3446088, value);
    }
    int getRandomSeed() {
      int offset = myOffset + 3446092;
      return sharedMemory.getInt(offset);
    }
    void setRandomSeed(int value) {
      sharedMemory.putInt(myOffset + 3446092, value);
    }
    int getFrameCount() {
      int offset = myOffset + 3446096;
      return sharedMemory.getInt(offset);
    }
    void setFrameCount(int value) {
      sharedMemory.putInt(myOffset + 3446096, value);
    }
    int getElapsedTime() {
      int offset = myOffset + 3446100;
      return sharedMemory.getInt(offset);
    }
    void setElapsedTime(int value) {
      sharedMemory.putInt(myOffset + 3446100, value);
    }
    int getCountdownTimer() {
      int offset = myOffset + 3446104;
      return sharedMemory.getInt(offset);
    }
    void setCountdownTimer(int value) {
      sharedMemory.putInt(myOffset + 3446104, value);
    }
    int getFps() {
      int offset = myOffset + 3446108;
      return sharedMemory.getInt(offset);
    }
    void setFps(int value) {
      sharedMemory.putInt(myOffset + 3446108, value);
    }
    double getAverageFPS() {
      int offset = myOffset + 3446112;
      return sharedMemory.getDouble(offset);
    }
    void setAverageFPS(double value) {      sharedMemory.putDouble(myOffset + 3446112, value);
    }
    int getMouseX() {
      int offset = myOffset + 3446120;
      return sharedMemory.getInt(offset);
    }
    void setMouseX(int value) {
      sharedMemory.putInt(myOffset + 3446120, value);
    }
    int getMouseY() {
      int offset = myOffset + 3446124;
      return sharedMemory.getInt(offset);
    }
    void setMouseY(int value) {
      sharedMemory.putInt(myOffset + 3446124, value);
    }
    boolean getMouseState(int i) {
      int offset = myOffset + 3446128 + 1 * 1 * i;
      return sharedMemory.get(offset) != 0;
    }
    void setMouseState(int i, boolean value) {      sharedMemory.put(myOffset + 3446128 + 1 * 1 * i, (byte) (value ? 1 : 0));
    }
    boolean getKeyState(int i) {
      int offset = myOffset + 3446131 + 1 * 1 * i;
      return sharedMemory.get(offset) != 0;
    }
    void setKeyState(int i, boolean value) {      sharedMemory.put(myOffset + 3446131 + 1 * 1 * i, (byte) (value ? 1 : 0));
    }
    int getScreenX() {
      int offset = myOffset + 3446388;
      return sharedMemory.getInt(offset);
    }
    void setScreenX(int value) {
      sharedMemory.putInt(myOffset + 3446388, value);
    }
    int getScreenY() {
      int offset = myOffset + 3446392;
      return sharedMemory.getInt(offset);
    }
    void setScreenY(int value) {
      sharedMemory.putInt(myOffset + 3446392, value);
    }
    boolean getFlags(int i) {
      int offset = myOffset + 3446396 + 1 * 1 * i;
      return sharedMemory.get(offset) != 0;
    }
    void setFlags(int i, boolean value) {      sharedMemory.put(myOffset + 3446396 + 1 * 1 * i, (byte) (value ? 1 : 0));
    }
    int getMapWidth() {
      int offset = myOffset + 3446400;
      return sharedMemory.getInt(offset);
    }
    void setMapWidth(int value) {
      sharedMemory.putInt(myOffset + 3446400, value);
    }
    int getMapHeight() {
      int offset = myOffset + 3446404;
      return sharedMemory.getInt(offset);
    }
    void setMapHeight(int value) {
      sharedMemory.putInt(myOffset + 3446404, value);
    }
    String getMapFileName() {
      int offset = myOffset + 3446408;
      return Buffers.toString(sharedMemory, offset, 1);
    }
    void setMapFileName(String value) {
      Buffers.fromString(sharedMemory, myOffset + 3446408, 261, value);
    }
    String getMapPathName() {
      int offset = myOffset + 3446669;
      return Buffers.toString(sharedMemory, offset, 1);
    }
    void setMapPathName(String value) {
      Buffers.fromString(sharedMemory, myOffset + 3446669, 261, value);
    }
    String getMapName() {
      int offset = myOffset + 3446930;
      return Buffers.toString(sharedMemory, offset, 1);
    }
    void setMapName(String value) {
      Buffers.fromString(sharedMemory, myOffset + 3446930, 33, value);
    }
    String getMapHash() {
      int offset = myOffset + 3446963;
      return Buffers.toString(sharedMemory, offset, 1);
    }
    void setMapHash(String value) {
      Buffers.fromString(sharedMemory, myOffset + 3446963, 41, value);
    }
    int getGroundHeight(int i, int j) {
      int offset = myOffset + 3447004 + 4 * 1 * j + 4 * 256 * i;
      return sharedMemory.getInt(offset);
    }
    void setGetGroundHeight(int i, int j, int value) {
      sharedMemory.putInt(myOffset + 3447004 + 4 * 1 * j + 4 * 256 * i, value);
    }
    boolean isWalkable(int i, int j) {
      int offset = myOffset + 3709148 + 1 * 1 * j + 1 * 1024 * i;
      return sharedMemory.get(offset) != 0;
    }
    void setIsWalkable(int i, int j, boolean value) {      sharedMemory.put(myOffset + 3709148 + 1 * 1 * j + 1 * 1024 * i, (byte) (value ? 1 : 0));
    }
    boolean isBuildable(int i, int j) {
      int offset = myOffset + 4757724 + 1 * 1 * j + 1 * 256 * i;
      return sharedMemory.get(offset) != 0;
    }
    void setIsBuildable(int i, int j, boolean value) {      sharedMemory.put(myOffset + 4757724 + 1 * 1 * j + 1 * 256 * i, (byte) (value ? 1 : 0));
    }
    boolean isVisible(int i, int j) {
      int offset = myOffset + 4823260 + 1 * 1 * j + 1 * 256 * i;
      return sharedMemory.get(offset) != 0;
    }
    void setIsVisible(int i, int j, boolean value) {      sharedMemory.put(myOffset + 4823260 + 1 * 1 * j + 1 * 256 * i, (byte) (value ? 1 : 0));
    }
    boolean isExplored(int i, int j) {
      int offset = myOffset + 4888796 + 1 * 1 * j + 1 * 256 * i;
      return sharedMemory.get(offset) != 0;
    }
    void setIsExplored(int i, int j, boolean value) {      sharedMemory.put(myOffset + 4888796 + 1 * 1 * j + 1 * 256 * i, (byte) (value ? 1 : 0));
    }
    boolean getHasCreep(int i, int j) {
      int offset = myOffset + 4954332 + 1 * 1 * j + 1 * 256 * i;
      return sharedMemory.get(offset) != 0;
    }
    void setHasCreep(int i, int j, boolean value) {      sharedMemory.put(myOffset + 4954332 + 1 * 1 * j + 1 * 256 * i, (byte) (value ? 1 : 0));
    }
    boolean isOccupied(int i, int j) {
      int offset = myOffset + 5019868 + 1 * 1 * j + 1 * 256 * i;
      return sharedMemory.get(offset) != 0;
    }
    void setIsOccupied(int i, int j, boolean value) {      sharedMemory.put(myOffset + 5019868 + 1 * 1 * j + 1 * 256 * i, (byte) (value ? 1 : 0));
    }
    short getMapTileRegionId(int i, int j) {
      int offset = myOffset + 5085404 + 2 * 1 * j + 2 * 256 * i;
      return sharedMemory.getShort(offset);
    }
    void setMapTileRegionId(int i, int j, short value) {      sharedMemory.putShort(myOffset + 5085404 + 2 * 1 * j + 2 * 256 * i, value);
    }
    short getMapSplitTilesMiniTileMask(int i) {
      int offset = myOffset + 5216476 + 2 * 1 * i;
      return sharedMemory.getShort(offset);
    }
    void setMapSplitTilesMiniTileMask(int i, short value) {      sharedMemory.putShort(myOffset + 5216476 + 2 * 1 * i, value);
    }
    short getMapSplitTilesRegion1(int i) {
      int offset = myOffset + 5226476 + 2 * 1 * i;
      return sharedMemory.getShort(offset);
    }
    void setMapSplitTilesRegion1(int i, short value) {      sharedMemory.putShort(myOffset + 5226476 + 2 * 1 * i, value);
    }
    short getMapSplitTilesRegion2(int i) {
      int offset = myOffset + 5236476 + 2 * 1 * i;
      return sharedMemory.getShort(offset);
    }
    void setMapSplitTilesRegion2(int i, short value) {      sharedMemory.putShort(myOffset + 5236476 + 2 * 1 * i, value);
    }
    int getRegionCount() {
      int offset = myOffset + 5246476;
      return sharedMemory.getInt(offset);
    }
    void setRegionCount(int value) {
      sharedMemory.putInt(myOffset + 5246476, value);
    }
    RegionData getRegions(int i) {
      int offset = myOffset + 5246480 + 1068 * 1 * i;
      return new RegionData(offset);
    }
    int getStartLocationCount() {
      int offset = myOffset + 10586480;
      return sharedMemory.getInt(offset);
    }
    void setStartLocationCount(int value) {
      sharedMemory.putInt(myOffset + 10586480, value);
    }
    Position getStartLocations(int i) {
      int offset = myOffset + 10586484 + 8 * 1 * i;
      return new Position(offset);
    }
    boolean isInGame() {
      int offset = myOffset + 10586548;
      return sharedMemory.get(offset) != 0;
    }
    void setIsInGame(boolean value) {      sharedMemory.put(myOffset + 10586548, (byte) (value ? 1 : 0));
    }
    boolean isMultiplayer() {
      int offset = myOffset + 10586549;
      return sharedMemory.get(offset) != 0;
    }
    void setIsMultiplayer(boolean value) {      sharedMemory.put(myOffset + 10586549, (byte) (value ? 1 : 0));
    }
    boolean isBattleNet() {
      int offset = myOffset + 10586550;
      return sharedMemory.get(offset) != 0;
    }
    void setIsBattleNet(boolean value) {      sharedMemory.put(myOffset + 10586550, (byte) (value ? 1 : 0));
    }
    boolean isPaused() {
      int offset = myOffset + 10586551;
      return sharedMemory.get(offset) != 0;
    }
    void setIsPaused(boolean value) {      sharedMemory.put(myOffset + 10586551, (byte) (value ? 1 : 0));
    }
    boolean isReplay() {
      int offset = myOffset + 10586552;
      return sharedMemory.get(offset) != 0;
    }
    void setIsReplay(boolean value) {      sharedMemory.put(myOffset + 10586552, (byte) (value ? 1 : 0));
    }
    int getSelectedUnitCount() {
      int offset = myOffset + 10586556;
      return sharedMemory.getInt(offset);
    }
    void setSelectedUnitCount(int value) {
      sharedMemory.putInt(myOffset + 10586556, value);
    }
    int getSelectedUnits(int i) {
      int offset = myOffset + 10586560 + 4 * 1 * i;
      return sharedMemory.getInt(offset);
    }
    void setSelectedUnits(int i, int value) {
      sharedMemory.putInt(myOffset + 10586560 + 4 * 1 * i, value);
    }
    int getSelf() {
      int offset = myOffset + 10586608;
      return sharedMemory.getInt(offset);
    }
    void setSelf(int value) {
      sharedMemory.putInt(myOffset + 10586608, value);
    }
    int getEnemy() {
      int offset = myOffset + 10586612;
      return sharedMemory.getInt(offset);
    }
    void setEnemy(int value) {
      sharedMemory.putInt(myOffset + 10586612, value);
    }
    int getNeutral() {
      int offset = myOffset + 10586616;
      return sharedMemory.getInt(offset);
    }
    void setNeutral(int value) {
      sharedMemory.putInt(myOffset + 10586616, value);
    }
    int getEventCount() {
      int offset = myOffset + 10586620;
      return sharedMemory.getInt(offset);
    }
    void setEventCount(int value) {
      sharedMemory.putInt(myOffset + 10586620, value);
    }
    Event getEvents(int i) {
      int offset = myOffset + 10586624 + 12 * 1 * i;
      return new Event(offset);
    }
    int getEventStringCount() {
      int offset = myOffset + 10706624;
      return sharedMemory.getInt(offset);
    }
    void setEventStringCount(int value) {
      sharedMemory.putInt(myOffset + 10706624, value);
    }
    String getEventStrings(int i) {
      int offset = myOffset + 10706628 + 1 * 256 * i;
      return Buffers.toString(sharedMemory, offset, 2);
    }
    void setEventStrings(int i, String value) {
      Buffers.fromString(sharedMemory, myOffset + 10706628 + 1 * 256 * i, 256, value);
    }
    int getStringCount() {
      int offset = myOffset + 10962628;
      return sharedMemory.getInt(offset);
    }
    void setStringCount(int value) {
      sharedMemory.putInt(myOffset + 10962628, value);
    }
    String getStrings(int i) {
      int offset = myOffset + 10962632 + 1 * 1024 * i;
      return Buffers.toString(sharedMemory, offset, 2);
    }
    void setStrings(int i, String value) {
      Buffers.fromString(sharedMemory, myOffset + 10962632 + 1 * 1024 * i, 1024, value);
    }
    int getShapeCount() {
      int offset = myOffset + 31442632;
      return sharedMemory.getInt(offset);
    }
    void setShapeCount(int value) {
      sharedMemory.putInt(myOffset + 31442632, value);
    }
    Shape getShapes(int i) {
      int offset = myOffset + 31442636 + 40 * 1 * i;
      return new Shape(offset);
    }
    int getCommandCount() {
      int offset = myOffset + 32242636;
      return sharedMemory.getInt(offset);
    }
    void setCommandCount(int value) {
      sharedMemory.putInt(myOffset + 32242636, value);
    }
    Command getCommands(int i) {
      int offset = myOffset + 32242640 + 12 * 1 * i;
      return new Command(offset);
    }
    int getUnitCommandCount() {
      int offset = myOffset + 32482640;
      return sharedMemory.getInt(offset);
    }
    void setUnitCommandCount(int value) {
      sharedMemory.putInt(myOffset + 32482640, value);
    }
    UnitCommand getUnitCommands(int i) {
      int offset = myOffset + 32482644 + 24 * 1 * i;
      return new UnitCommand(offset);
    }
    int getUnitSearchSize() {
      int offset = myOffset + 32962644;
      return sharedMemory.getInt(offset);
    }
    void setUnitSearchSize(int value) {
      sharedMemory.putInt(myOffset + 32962644, value);
    }
    unitFinder getXUnitSearch(int i) {
      int offset = myOffset + 32962648 + 8 * 1 * i;
      return new unitFinder(offset);
    }
    unitFinder getYUnitSearch(int i) {
      int offset = myOffset + 32989848 + 8 * 1 * i;
      return new unitFinder(offset);
    }
  }
  class Shape {
    static final int SIZE = 40;
    private int myOffset;
    public Shape(int myOffset) {
      this.myOffset = myOffset;
    }
    ShapeType getType() {
      int offset = myOffset + 0;
      return ShapeType.withId(sharedMemory.getInt(offset));
    }
    void setType(ShapeType value) {
      sharedMemory.putInt(myOffset + 0, value.getId());
    }
    CoordinateType getCtype() {
      int offset = myOffset + 4;
      return CoordinateType.withId(sharedMemory.getInt(offset));
    }
    void setCtype(CoordinateType value) {
      sharedMemory.putInt(myOffset + 4, value.getId());
    }
    int getX1() {
      int offset = myOffset + 8;
      return sharedMemory.getInt(offset);
    }
    void setX1(int value) {
      sharedMemory.putInt(myOffset + 8, value);
    }
    int getY1() {
      int offset = myOffset + 12;
      return sharedMemory.getInt(offset);
    }
    void setY1(int value) {
      sharedMemory.putInt(myOffset + 12, value);
    }
    int getX2() {
      int offset = myOffset + 16;
      return sharedMemory.getInt(offset);
    }
    void setX2(int value) {
      sharedMemory.putInt(myOffset + 16, value);
    }
    int getY2() {
      int offset = myOffset + 20;
      return sharedMemory.getInt(offset);
    }
    void setY2(int value) {
      sharedMemory.putInt(myOffset + 20, value);
    }
    int getExtra1() {
      int offset = myOffset + 24;
      return sharedMemory.getInt(offset);
    }
    void setExtra1(int value) {
      sharedMemory.putInt(myOffset + 24, value);
    }
    int getExtra2() {
      int offset = myOffset + 28;
      return sharedMemory.getInt(offset);
    }
    void setExtra2(int value) {
      sharedMemory.putInt(myOffset + 28, value);
    }
    int getColor() {
      int offset = myOffset + 32;
      return sharedMemory.getInt(offset);
    }
    void setColor(int value) {
      sharedMemory.putInt(myOffset + 32, value);
    }
    boolean isSolid() {
      int offset = myOffset + 36;
      return sharedMemory.get(offset) != 0;
    }
    void setIsSolid(boolean value) {      sharedMemory.put(myOffset + 36, (byte) (value ? 1 : 0));
    }
  }
  class Command {
    static final int SIZE = 12;
    private int myOffset;
    public Command(int myOffset) {
      this.myOffset = myOffset;
    }
    CommandType getType() {
      int offset = myOffset + 0;
      return CommandType.withId(sharedMemory.getInt(offset));
    }
    void setType(CommandType value) {
      sharedMemory.putInt(myOffset + 0, value.getId());
    }
    int getValue1() {
      int offset = myOffset + 4;
      return sharedMemory.getInt(offset);
    }
    void setValue1(int value) {
      sharedMemory.putInt(myOffset + 4, value);
    }
    int getValue2() {
      int offset = myOffset + 8;
      return sharedMemory.getInt(offset);
    }
    void setValue2(int value) {
      sharedMemory.putInt(myOffset + 8, value);
    }
  }
  class Position {
    static final int SIZE = 8;
    private int myOffset;
    public Position(int myOffset) {
      this.myOffset = myOffset;
    }
    int getX() {
      int offset = myOffset + 0;
      return sharedMemory.getInt(offset);
    }
    void setX(int value) {
      sharedMemory.putInt(myOffset + 0, value);
    }
    int getY() {
      int offset = myOffset + 4;
      return sharedMemory.getInt(offset);
    }
    void setY(int value) {
      sharedMemory.putInt(myOffset + 4, value);
    }
  }
  class Event {
    static final int SIZE = 12;
    private int myOffset;
    public Event(int myOffset) {
      this.myOffset = myOffset;
    }
    EventType getType() {
      int offset = myOffset + 0;
      return EventType.withId(sharedMemory.getInt(offset));
    }
    void setType(EventType value) {
      sharedMemory.putInt(myOffset + 0, value.getId());
    }
    int getV1() {
      int offset = myOffset + 4;
      return sharedMemory.getInt(offset);
    }
    void setV1(int value) {
      sharedMemory.putInt(myOffset + 4, value);
    }
    int getV2() {
      int offset = myOffset + 8;
      return sharedMemory.getInt(offset);
    }
    void setV2(int value) {
      sharedMemory.putInt(myOffset + 8, value);
    }
  }
  class RegionData {
    static final int SIZE = 1068;
    private int myOffset;
    public RegionData(int myOffset) {
      this.myOffset = myOffset;
    }
    int getId() {
      int offset = myOffset + 0;
      return sharedMemory.getInt(offset);
    }
    void setId(int value) {
      sharedMemory.putInt(myOffset + 0, value);
    }
    int islandID() {
      int offset = myOffset + 4;
      return sharedMemory.getInt(offset);
    }
    void setIslandID(int value) {
      sharedMemory.putInt(myOffset + 4, value);
    }
    int getCenter_x() {
      int offset = myOffset + 8;
      return sharedMemory.getInt(offset);
    }
    void setCenter_x(int value) {
      sharedMemory.putInt(myOffset + 8, value);
    }
    int getCenter_y() {
      int offset = myOffset + 12;
      return sharedMemory.getInt(offset);
    }
    void setCenter_y(int value) {
      sharedMemory.putInt(myOffset + 12, value);
    }
    int getPriority() {
      int offset = myOffset + 16;
      return sharedMemory.getInt(offset);
    }
    void setPriority(int value) {
      sharedMemory.putInt(myOffset + 16, value);
    }
    int getLeftMost() {
      int offset = myOffset + 20;
      return sharedMemory.getInt(offset);
    }
    void setLeftMost(int value) {
      sharedMemory.putInt(myOffset + 20, value);
    }
    int getRightMost() {
      int offset = myOffset + 24;
      return sharedMemory.getInt(offset);
    }
    void setRightMost(int value) {
      sharedMemory.putInt(myOffset + 24, value);
    }
    int getTopMost() {
      int offset = myOffset + 28;
      return sharedMemory.getInt(offset);
    }
    void setTopMost(int value) {
      sharedMemory.putInt(myOffset + 28, value);
    }
    int getBottomMost() {
      int offset = myOffset + 32;
      return sharedMemory.getInt(offset);
    }
    void setBottomMost(int value) {
      sharedMemory.putInt(myOffset + 32, value);
    }
    int getNeighborCount() {
      int offset = myOffset + 36;
      return sharedMemory.getInt(offset);
    }
    void setNeighborCount(int value) {
      sharedMemory.putInt(myOffset + 36, value);
    }
    int getNeighbors(int i) {
      int offset = myOffset + 40 + 4 * 1 * i;
      return sharedMemory.getInt(offset);
    }
    void setNeighbors(int i, int value) {
      sharedMemory.putInt(myOffset + 40 + 4 * 1 * i, value);
    }
    boolean isAccessible() {
      int offset = myOffset + 1064;
      return sharedMemory.get(offset) != 0;
    }
    void setIsAccessible(boolean value) {      sharedMemory.put(myOffset + 1064, (byte) (value ? 1 : 0));
    }
    boolean isHigherGround() {
      int offset = myOffset + 1065;
      return sharedMemory.get(offset) != 0;
    }
    void setIsHigherGround(boolean value) {      sharedMemory.put(myOffset + 1065, (byte) (value ? 1 : 0));
    }
  }
  class ForceData {
    static final int SIZE = 32;
    private int myOffset;
    public ForceData(int myOffset) {
      this.myOffset = myOffset;
    }
    String getName() {
      int offset = myOffset + 0;
      return Buffers.toString(sharedMemory, offset, 1);
    }
    void setName(String value) {
      Buffers.fromString(sharedMemory, myOffset + 0, 32, value);
    }
  }
  class PlayerData {
    static final int SIZE = 5788;
    private int myOffset;
    public PlayerData(int myOffset) {
      this.myOffset = myOffset;
    }
    String getName() {
      int offset = myOffset + 0;
      return Buffers.toString(sharedMemory, offset, 1);
    }
    void setName(String value) {
      Buffers.fromString(sharedMemory, myOffset + 0, 25, value);
    }
    int getRace() {
      int offset = myOffset + 28;
      return sharedMemory.getInt(offset);
    }
    void setRace(int value) {
      sharedMemory.putInt(myOffset + 28, value);
    }
    int getType() {
      int offset = myOffset + 32;
      return sharedMemory.getInt(offset);
    }
    void setType(int value) {
      sharedMemory.putInt(myOffset + 32, value);
    }
    int getForce() {
      int offset = myOffset + 36;
      return sharedMemory.getInt(offset);
    }
    void setForce(int value) {
      sharedMemory.putInt(myOffset + 36, value);
    }
    boolean isAlly(int i) {
      int offset = myOffset + 40 + 1 * 1 * i;
      return sharedMemory.get(offset) != 0;
    }
    void setIsAlly(int i, boolean value) {      sharedMemory.put(myOffset + 40 + 1 * 1 * i, (byte) (value ? 1 : 0));
    }
    boolean isEnemy(int i) {
      int offset = myOffset + 52 + 1 * 1 * i;
      return sharedMemory.get(offset) != 0;
    }
    void setIsEnemy(int i, boolean value) {      sharedMemory.put(myOffset + 52 + 1 * 1 * i, (byte) (value ? 1 : 0));
    }
    boolean isNeutral() {
      int offset = myOffset + 64;
      return sharedMemory.get(offset) != 0;
    }
    void setIsNeutral(boolean value) {      sharedMemory.put(myOffset + 64, (byte) (value ? 1 : 0));
    }
    int getStartLocationX() {
      int offset = myOffset + 68;
      return sharedMemory.getInt(offset);
    }
    void setStartLocationX(int value) {
      sharedMemory.putInt(myOffset + 68, value);
    }
    int getStartLocationY() {
      int offset = myOffset + 72;
      return sharedMemory.getInt(offset);
    }
    void setStartLocationY(int value) {
      sharedMemory.putInt(myOffset + 72, value);
    }
    boolean isVictorious() {
      int offset = myOffset + 76;
      return sharedMemory.get(offset) != 0;
    }
    void setIsVictorious(boolean value) {      sharedMemory.put(myOffset + 76, (byte) (value ? 1 : 0));
    }
    boolean isDefeated() {
      int offset = myOffset + 77;
      return sharedMemory.get(offset) != 0;
    }
    void setIsDefeated(boolean value) {      sharedMemory.put(myOffset + 77, (byte) (value ? 1 : 0));
    }
    boolean getLeftGame() {
      int offset = myOffset + 78;
      return sharedMemory.get(offset) != 0;
    }
    void setLeftGame(boolean value) {      sharedMemory.put(myOffset + 78, (byte) (value ? 1 : 0));
    }
    boolean isParticipating() {
      int offset = myOffset + 79;
      return sharedMemory.get(offset) != 0;
    }
    void setIsParticipating(boolean value) {      sharedMemory.put(myOffset + 79, (byte) (value ? 1 : 0));
    }
    int getMinerals() {
      int offset = myOffset + 80;
      return sharedMemory.getInt(offset);
    }
    void setMinerals(int value) {
      sharedMemory.putInt(myOffset + 80, value);
    }
    int getGas() {
      int offset = myOffset + 84;
      return sharedMemory.getInt(offset);
    }
    void setGas(int value) {
      sharedMemory.putInt(myOffset + 84, value);
    }
    int getGatheredMinerals() {
      int offset = myOffset + 88;
      return sharedMemory.getInt(offset);
    }
    void setGatheredMinerals(int value) {
      sharedMemory.putInt(myOffset + 88, value);
    }
    int getGatheredGas() {
      int offset = myOffset + 92;
      return sharedMemory.getInt(offset);
    }
    void setGatheredGas(int value) {
      sharedMemory.putInt(myOffset + 92, value);
    }
    int getRepairedMinerals() {
      int offset = myOffset + 96;
      return sharedMemory.getInt(offset);
    }
    void setRepairedMinerals(int value) {
      sharedMemory.putInt(myOffset + 96, value);
    }
    int getRepairedGas() {
      int offset = myOffset + 100;
      return sharedMemory.getInt(offset);
    }
    void setRepairedGas(int value) {
      sharedMemory.putInt(myOffset + 100, value);
    }
    int getRefundedMinerals() {
      int offset = myOffset + 104;
      return sharedMemory.getInt(offset);
    }
    void setRefundedMinerals(int value) {
      sharedMemory.putInt(myOffset + 104, value);
    }
    int getRefundedGas() {
      int offset = myOffset + 108;
      return sharedMemory.getInt(offset);
    }
    void setRefundedGas(int value) {
      sharedMemory.putInt(myOffset + 108, value);
    }
    int getSupplyTotal(int i) {
      int offset = myOffset + 112 + 4 * 1 * i;
      return sharedMemory.getInt(offset);
    }
    void setSupplyTotal(int i, int value) {
      sharedMemory.putInt(myOffset + 112 + 4 * 1 * i, value);
    }
    int getSupplyUsed(int i) {
      int offset = myOffset + 124 + 4 * 1 * i;
      return sharedMemory.getInt(offset);
    }
    void setSupplyUsed(int i, int value) {
      sharedMemory.putInt(myOffset + 124 + 4 * 1 * i, value);
    }
    int getAllUnitCount(int i) {
      int offset = myOffset + 136 + 4 * 1 * i;
      return sharedMemory.getInt(offset);
    }
    void setAllUnitCount(int i, int value) {
      sharedMemory.putInt(myOffset + 136 + 4 * 1 * i, value);
    }
    int getVisibleUnitCount(int i) {
      int offset = myOffset + 1072 + 4 * 1 * i;
      return sharedMemory.getInt(offset);
    }
    void setVisibleUnitCount(int i, int value) {
      sharedMemory.putInt(myOffset + 1072 + 4 * 1 * i, value);
    }
    int getCompletedUnitCount(int i) {
      int offset = myOffset + 2008 + 4 * 1 * i;
      return sharedMemory.getInt(offset);
    }
    void setCompletedUnitCount(int i, int value) {
      sharedMemory.putInt(myOffset + 2008 + 4 * 1 * i, value);
    }
    int getDeadUnitCount(int i) {
      int offset = myOffset + 2944 + 4 * 1 * i;
      return sharedMemory.getInt(offset);
    }
    void setDeadUnitCount(int i, int value) {
      sharedMemory.putInt(myOffset + 2944 + 4 * 1 * i, value);
    }
    int getKilledUnitCount(int i) {
      int offset = myOffset + 3880 + 4 * 1 * i;
      return sharedMemory.getInt(offset);
    }
    void setKilledUnitCount(int i, int value) {
      sharedMemory.putInt(myOffset + 3880 + 4 * 1 * i, value);
    }
    int getUpgradeLevel(int i) {
      int offset = myOffset + 4816 + 4 * 1 * i;
      return sharedMemory.getInt(offset);
    }
    void setUpgradeLevel(int i, int value) {
      sharedMemory.putInt(myOffset + 4816 + 4 * 1 * i, value);
    }
    boolean getHasResearched(int i) {
      int offset = myOffset + 5068 + 1 * 1 * i;
      return sharedMemory.get(offset) != 0;
    }
    void setHasResearched(int i, boolean value) {      sharedMemory.put(myOffset + 5068 + 1 * 1 * i, (byte) (value ? 1 : 0));
    }
    boolean isResearching(int i) {
      int offset = myOffset + 5115 + 1 * 1 * i;
      return sharedMemory.get(offset) != 0;
    }
    void setIsResearching(int i, boolean value) {      sharedMemory.put(myOffset + 5115 + 1 * 1 * i, (byte) (value ? 1 : 0));
    }
    boolean isUpgrading(int i) {
      int offset = myOffset + 5162 + 1 * 1 * i;
      return sharedMemory.get(offset) != 0;
    }
    void setIsUpgrading(int i, boolean value) {      sharedMemory.put(myOffset + 5162 + 1 * 1 * i, (byte) (value ? 1 : 0));
    }
    int getColor() {
      int offset = myOffset + 5228;
      return sharedMemory.getInt(offset);
    }
    void setColor(int value) {
      sharedMemory.putInt(myOffset + 5228, value);
    }
    int getTotalUnitScore() {
      int offset = myOffset + 5232;
      return sharedMemory.getInt(offset);
    }
    void setTotalUnitScore(int value) {
      sharedMemory.putInt(myOffset + 5232, value);
    }
    int getTotalKillScore() {
      int offset = myOffset + 5236;
      return sharedMemory.getInt(offset);
    }
    void setTotalKillScore(int value) {
      sharedMemory.putInt(myOffset + 5236, value);
    }
    int getTotalBuildingScore() {
      int offset = myOffset + 5240;
      return sharedMemory.getInt(offset);
    }
    void setTotalBuildingScore(int value) {
      sharedMemory.putInt(myOffset + 5240, value);
    }
    int getTotalRazingScore() {
      int offset = myOffset + 5244;
      return sharedMemory.getInt(offset);
    }
    void setTotalRazingScore(int value) {
      sharedMemory.putInt(myOffset + 5244, value);
    }
    int getCustomScore() {
      int offset = myOffset + 5248;
      return sharedMemory.getInt(offset);
    }
    void setCustomScore(int value) {
      sharedMemory.putInt(myOffset + 5248, value);
    }
    int getMaxUpgradeLevel(int i) {
      int offset = myOffset + 5252 + 4 * 1 * i;
      return sharedMemory.getInt(offset);
    }
    void setMaxUpgradeLevel(int i, int value) {
      sharedMemory.putInt(myOffset + 5252 + 4 * 1 * i, value);
    }
    boolean isResearchAvailable(int i) {
      int offset = myOffset + 5504 + 1 * 1 * i;
      return sharedMemory.get(offset) != 0;
    }
    void setIsResearchAvailable(int i, boolean value) {      sharedMemory.put(myOffset + 5504 + 1 * 1 * i, (byte) (value ? 1 : 0));
    }
    boolean isUnitAvailable(int i) {
      int offset = myOffset + 5551 + 1 * 1 * i;
      return sharedMemory.get(offset) != 0;
    }
    void setIsUnitAvailable(int i, boolean value) {      sharedMemory.put(myOffset + 5551 + 1 * 1 * i, (byte) (value ? 1 : 0));
    }
  }
  class BulletData {
    static final int SIZE = 80;
    private int myOffset;
    public BulletData(int myOffset) {
      this.myOffset = myOffset;
    }
    int getId() {
      int offset = myOffset + 0;
      return sharedMemory.getInt(offset);
    }
    void setId(int value) {
      sharedMemory.putInt(myOffset + 0, value);
    }
    int getPlayer() {
      int offset = myOffset + 4;
      return sharedMemory.getInt(offset);
    }
    void setPlayer(int value) {
      sharedMemory.putInt(myOffset + 4, value);
    }
    int getType() {
      int offset = myOffset + 8;
      return sharedMemory.getInt(offset);
    }
    void setType(int value) {
      sharedMemory.putInt(myOffset + 8, value);
    }
    int getSource() {
      int offset = myOffset + 12;
      return sharedMemory.getInt(offset);
    }
    void setSource(int value) {
      sharedMemory.putInt(myOffset + 12, value);
    }
    int getPositionX() {
      int offset = myOffset + 16;
      return sharedMemory.getInt(offset);
    }
    void setPositionX(int value) {
      sharedMemory.putInt(myOffset + 16, value);
    }
    int getPositionY() {
      int offset = myOffset + 20;
      return sharedMemory.getInt(offset);
    }
    void setPositionY(int value) {
      sharedMemory.putInt(myOffset + 20, value);
    }
    double getAngle() {
      int offset = myOffset + 24;
      return sharedMemory.getDouble(offset);
    }
    void setAngle(double value) {      sharedMemory.putDouble(myOffset + 24, value);
    }
    double getVelocityX() {
      int offset = myOffset + 32;
      return sharedMemory.getDouble(offset);
    }
    void setVelocityX(double value) {      sharedMemory.putDouble(myOffset + 32, value);
    }
    double getVelocityY() {
      int offset = myOffset + 40;
      return sharedMemory.getDouble(offset);
    }
    void setVelocityY(double value) {      sharedMemory.putDouble(myOffset + 40, value);
    }
    int getTarget() {
      int offset = myOffset + 48;
      return sharedMemory.getInt(offset);
    }
    void setTarget(int value) {
      sharedMemory.putInt(myOffset + 48, value);
    }
    int getTargetPositionX() {
      int offset = myOffset + 52;
      return sharedMemory.getInt(offset);
    }
    void setTargetPositionX(int value) {
      sharedMemory.putInt(myOffset + 52, value);
    }
    int getTargetPositionY() {
      int offset = myOffset + 56;
      return sharedMemory.getInt(offset);
    }
    void setTargetPositionY(int value) {
      sharedMemory.putInt(myOffset + 56, value);
    }
    int getRemoveTimer() {
      int offset = myOffset + 60;
      return sharedMemory.getInt(offset);
    }
    void setRemoveTimer(int value) {
      sharedMemory.putInt(myOffset + 60, value);
    }
    boolean getExists() {
      int offset = myOffset + 64;
      return sharedMemory.get(offset) != 0;
    }
    void setExists(boolean value) {      sharedMemory.put(myOffset + 64, (byte) (value ? 1 : 0));
    }
    boolean isVisible(int i) {
      int offset = myOffset + 65 + 1 * 1 * i;
      return sharedMemory.get(offset) != 0;
    }
    void setIsVisible(int i, boolean value) {      sharedMemory.put(myOffset + 65 + 1 * 1 * i, (byte) (value ? 1 : 0));
    }
  }
  class unitFinder {
    static final int SIZE = 8;
    private int myOffset;
    public unitFinder(int myOffset) {
      this.myOffset = myOffset;
    }
    int getUnitIndex() {
      int offset = myOffset + 0;
      return sharedMemory.getInt(offset);
    }
    void setUnitIndex(int value) {
      sharedMemory.putInt(myOffset + 0, value);
    }
    int getSearchValue() {
      int offset = myOffset + 4;
      return sharedMemory.getInt(offset);
    }
    void setSearchValue(int value) {
      sharedMemory.putInt(myOffset + 4, value);
    }
  }
  class UnitData {
    static final int SIZE = 336;
    private int myOffset;
    public UnitData(int myOffset) {
      this.myOffset = myOffset;
    }
    int getClearanceLevel() {
      int offset = myOffset + 0;
      return sharedMemory.getInt(offset);
    }
    void setClearanceLevel(int value) {
      sharedMemory.putInt(myOffset + 0, value);
    }
    int getId() {
      int offset = myOffset + 4;
      return sharedMemory.getInt(offset);
    }
    void setId(int value) {
      sharedMemory.putInt(myOffset + 4, value);
    }
    int getPlayer() {
      int offset = myOffset + 8;
      return sharedMemory.getInt(offset);
    }
    void setPlayer(int value) {
      sharedMemory.putInt(myOffset + 8, value);
    }
    int getType() {
      int offset = myOffset + 12;
      return sharedMemory.getInt(offset);
    }
    void setType(int value) {
      sharedMemory.putInt(myOffset + 12, value);
    }
    int getPositionX() {
      int offset = myOffset + 16;
      return sharedMemory.getInt(offset);
    }
    void setPositionX(int value) {
      sharedMemory.putInt(myOffset + 16, value);
    }
    int getPositionY() {
      int offset = myOffset + 20;
      return sharedMemory.getInt(offset);
    }
    void setPositionY(int value) {
      sharedMemory.putInt(myOffset + 20, value);
    }
    double getAngle() {
      int offset = myOffset + 24;
      return sharedMemory.getDouble(offset);
    }
    void setAngle(double value) {      sharedMemory.putDouble(myOffset + 24, value);
    }
    double getVelocityX() {
      int offset = myOffset + 32;
      return sharedMemory.getDouble(offset);
    }
    void setVelocityX(double value) {      sharedMemory.putDouble(myOffset + 32, value);
    }
    double getVelocityY() {
      int offset = myOffset + 40;
      return sharedMemory.getDouble(offset);
    }
    void setVelocityY(double value) {      sharedMemory.putDouble(myOffset + 40, value);
    }
    int getHitPoints() {
      int offset = myOffset + 48;
      return sharedMemory.getInt(offset);
    }
    void setHitPoints(int value) {
      sharedMemory.putInt(myOffset + 48, value);
    }
    int getLastHitPoints() {
      int offset = myOffset + 52;
      return sharedMemory.getInt(offset);
    }
    void setLastHitPoints(int value) {
      sharedMemory.putInt(myOffset + 52, value);
    }
    int getShields() {
      int offset = myOffset + 56;
      return sharedMemory.getInt(offset);
    }
    void setShields(int value) {
      sharedMemory.putInt(myOffset + 56, value);
    }
    int getEnergy() {
      int offset = myOffset + 60;
      return sharedMemory.getInt(offset);
    }
    void setEnergy(int value) {
      sharedMemory.putInt(myOffset + 60, value);
    }
    int getResources() {
      int offset = myOffset + 64;
      return sharedMemory.getInt(offset);
    }
    void setResources(int value) {
      sharedMemory.putInt(myOffset + 64, value);
    }
    int getResourceGroup() {
      int offset = myOffset + 68;
      return sharedMemory.getInt(offset);
    }
    void setResourceGroup(int value) {
      sharedMemory.putInt(myOffset + 68, value);
    }
    int getKillCount() {
      int offset = myOffset + 72;
      return sharedMemory.getInt(offset);
    }
    void setKillCount(int value) {
      sharedMemory.putInt(myOffset + 72, value);
    }
    int getAcidSporeCount() {
      int offset = myOffset + 76;
      return sharedMemory.getInt(offset);
    }
    void setAcidSporeCount(int value) {
      sharedMemory.putInt(myOffset + 76, value);
    }
    int getScarabCount() {
      int offset = myOffset + 80;
      return sharedMemory.getInt(offset);
    }
    void setScarabCount(int value) {
      sharedMemory.putInt(myOffset + 80, value);
    }
    int getInterceptorCount() {
      int offset = myOffset + 84;
      return sharedMemory.getInt(offset);
    }
    void setInterceptorCount(int value) {
      sharedMemory.putInt(myOffset + 84, value);
    }
    int getSpiderMineCount() {
      int offset = myOffset + 88;
      return sharedMemory.getInt(offset);
    }
    void setSpiderMineCount(int value) {
      sharedMemory.putInt(myOffset + 88, value);
    }
    int getGroundWeaponCooldown() {
      int offset = myOffset + 92;
      return sharedMemory.getInt(offset);
    }
    void setGroundWeaponCooldown(int value) {
      sharedMemory.putInt(myOffset + 92, value);
    }
    int getAirWeaponCooldown() {
      int offset = myOffset + 96;
      return sharedMemory.getInt(offset);
    }
    void setAirWeaponCooldown(int value) {
      sharedMemory.putInt(myOffset + 96, value);
    }
    int getSpellCooldown() {
      int offset = myOffset + 100;
      return sharedMemory.getInt(offset);
    }
    void setSpellCooldown(int value) {
      sharedMemory.putInt(myOffset + 100, value);
    }
    int getDefenseMatrixPoints() {
      int offset = myOffset + 104;
      return sharedMemory.getInt(offset);
    }
    void setDefenseMatrixPoints(int value) {
      sharedMemory.putInt(myOffset + 104, value);
    }
    int getDefenseMatrixTimer() {
      int offset = myOffset + 108;
      return sharedMemory.getInt(offset);
    }
    void setDefenseMatrixTimer(int value) {
      sharedMemory.putInt(myOffset + 108, value);
    }
    int getEnsnareTimer() {
      int offset = myOffset + 112;
      return sharedMemory.getInt(offset);
    }
    void setEnsnareTimer(int value) {
      sharedMemory.putInt(myOffset + 112, value);
    }
    int getIrradiateTimer() {
      int offset = myOffset + 116;
      return sharedMemory.getInt(offset);
    }
    void setIrradiateTimer(int value) {
      sharedMemory.putInt(myOffset + 116, value);
    }
    int getLockdownTimer() {
      int offset = myOffset + 120;
      return sharedMemory.getInt(offset);
    }
    void setLockdownTimer(int value) {
      sharedMemory.putInt(myOffset + 120, value);
    }
    int getMaelstromTimer() {
      int offset = myOffset + 124;
      return sharedMemory.getInt(offset);
    }
    void setMaelstromTimer(int value) {
      sharedMemory.putInt(myOffset + 124, value);
    }
    int getOrderTimer() {
      int offset = myOffset + 128;
      return sharedMemory.getInt(offset);
    }
    void setOrderTimer(int value) {
      sharedMemory.putInt(myOffset + 128, value);
    }
    int getPlagueTimer() {
      int offset = myOffset + 132;
      return sharedMemory.getInt(offset);
    }
    void setPlagueTimer(int value) {
      sharedMemory.putInt(myOffset + 132, value);
    }
    int getRemoveTimer() {
      int offset = myOffset + 136;
      return sharedMemory.getInt(offset);
    }
    void setRemoveTimer(int value) {
      sharedMemory.putInt(myOffset + 136, value);
    }
    int getStasisTimer() {
      int offset = myOffset + 140;
      return sharedMemory.getInt(offset);
    }
    void setStasisTimer(int value) {
      sharedMemory.putInt(myOffset + 140, value);
    }
    int getStimTimer() {
      int offset = myOffset + 144;
      return sharedMemory.getInt(offset);
    }
    void setStimTimer(int value) {
      sharedMemory.putInt(myOffset + 144, value);
    }
    int getBuildType() {
      int offset = myOffset + 148;
      return sharedMemory.getInt(offset);
    }
    void setBuildType(int value) {
      sharedMemory.putInt(myOffset + 148, value);
    }
    int getTrainingQueueCount() {
      int offset = myOffset + 152;
      return sharedMemory.getInt(offset);
    }
    void setTrainingQueueCount(int value) {
      sharedMemory.putInt(myOffset + 152, value);
    }
    int getTrainingQueue(int i) {
      int offset = myOffset + 156 + 4 * 1 * i;
      return sharedMemory.getInt(offset);
    }
    void setTrainingQueue(int i, int value) {
      sharedMemory.putInt(myOffset + 156 + 4 * 1 * i, value);
    }
    int getTech() {
      int offset = myOffset + 176;
      return sharedMemory.getInt(offset);
    }
    void setTech(int value) {
      sharedMemory.putInt(myOffset + 176, value);
    }
    int getUpgrade() {
      int offset = myOffset + 180;
      return sharedMemory.getInt(offset);
    }
    void setUpgrade(int value) {
      sharedMemory.putInt(myOffset + 180, value);
    }
    int getRemainingBuildTime() {
      int offset = myOffset + 184;
      return sharedMemory.getInt(offset);
    }
    void setRemainingBuildTime(int value) {
      sharedMemory.putInt(myOffset + 184, value);
    }
    int getRemainingTrainTime() {
      int offset = myOffset + 188;
      return sharedMemory.getInt(offset);
    }
    void setRemainingTrainTime(int value) {
      sharedMemory.putInt(myOffset + 188, value);
    }
    int getRemainingResearchTime() {
      int offset = myOffset + 192;
      return sharedMemory.getInt(offset);
    }
    void setRemainingResearchTime(int value) {
      sharedMemory.putInt(myOffset + 192, value);
    }
    int getRemainingUpgradeTime() {
      int offset = myOffset + 196;
      return sharedMemory.getInt(offset);
    }
    void setRemainingUpgradeTime(int value) {
      sharedMemory.putInt(myOffset + 196, value);
    }
    int getBuildUnit() {
      int offset = myOffset + 200;
      return sharedMemory.getInt(offset);
    }
    void setBuildUnit(int value) {
      sharedMemory.putInt(myOffset + 200, value);
    }
    int getTarget() {
      int offset = myOffset + 204;
      return sharedMemory.getInt(offset);
    }
    void setTarget(int value) {
      sharedMemory.putInt(myOffset + 204, value);
    }
    int getTargetPositionX() {
      int offset = myOffset + 208;
      return sharedMemory.getInt(offset);
    }
    void setTargetPositionX(int value) {
      sharedMemory.putInt(myOffset + 208, value);
    }
    int getTargetPositionY() {
      int offset = myOffset + 212;
      return sharedMemory.getInt(offset);
    }
    void setTargetPositionY(int value) {
      sharedMemory.putInt(myOffset + 212, value);
    }
    int getOrder() {
      int offset = myOffset + 216;
      return sharedMemory.getInt(offset);
    }
    void setOrder(int value) {
      sharedMemory.putInt(myOffset + 216, value);
    }
    int getOrderTarget() {
      int offset = myOffset + 220;
      return sharedMemory.getInt(offset);
    }
    void setOrderTarget(int value) {
      sharedMemory.putInt(myOffset + 220, value);
    }
    int getOrderTargetPositionX() {
      int offset = myOffset + 224;
      return sharedMemory.getInt(offset);
    }
    void setOrderTargetPositionX(int value) {
      sharedMemory.putInt(myOffset + 224, value);
    }
    int getOrderTargetPositionY() {
      int offset = myOffset + 228;
      return sharedMemory.getInt(offset);
    }
    void setOrderTargetPositionY(int value) {
      sharedMemory.putInt(myOffset + 228, value);
    }
    int getSecondaryOrder() {
      int offset = myOffset + 232;
      return sharedMemory.getInt(offset);
    }
    void setSecondaryOrder(int value) {
      sharedMemory.putInt(myOffset + 232, value);
    }
    int getRallyPositionX() {
      int offset = myOffset + 236;
      return sharedMemory.getInt(offset);
    }
    void setRallyPositionX(int value) {
      sharedMemory.putInt(myOffset + 236, value);
    }
    int getRallyPositionY() {
      int offset = myOffset + 240;
      return sharedMemory.getInt(offset);
    }
    void setRallyPositionY(int value) {
      sharedMemory.putInt(myOffset + 240, value);
    }
    int getRallyUnit() {
      int offset = myOffset + 244;
      return sharedMemory.getInt(offset);
    }
    void setRallyUnit(int value) {
      sharedMemory.putInt(myOffset + 244, value);
    }
    int getAddon() {
      int offset = myOffset + 248;
      return sharedMemory.getInt(offset);
    }
    void setAddon(int value) {
      sharedMemory.putInt(myOffset + 248, value);
    }
    int getNydusExit() {
      int offset = myOffset + 252;
      return sharedMemory.getInt(offset);
    }
    void setNydusExit(int value) {
      sharedMemory.putInt(myOffset + 252, value);
    }
    int getPowerUp() {
      int offset = myOffset + 256;
      return sharedMemory.getInt(offset);
    }
    void setPowerUp(int value) {
      sharedMemory.putInt(myOffset + 256, value);
    }
    int getTransport() {
      int offset = myOffset + 260;
      return sharedMemory.getInt(offset);
    }
    void setTransport(int value) {
      sharedMemory.putInt(myOffset + 260, value);
    }
    int getCarrier() {
      int offset = myOffset + 264;
      return sharedMemory.getInt(offset);
    }
    void setCarrier(int value) {
      sharedMemory.putInt(myOffset + 264, value);
    }
    int getHatchery() {
      int offset = myOffset + 268;
      return sharedMemory.getInt(offset);
    }
    void setHatchery(int value) {
      sharedMemory.putInt(myOffset + 268, value);
    }
    boolean getExists() {
      int offset = myOffset + 272;
      return sharedMemory.get(offset) != 0;
    }
    void setExists(boolean value) {      sharedMemory.put(myOffset + 272, (byte) (value ? 1 : 0));
    }
    boolean getHasNuke() {
      int offset = myOffset + 273;
      return sharedMemory.get(offset) != 0;
    }
    void setHasNuke(boolean value) {      sharedMemory.put(myOffset + 273, (byte) (value ? 1 : 0));
    }
    boolean isAccelerating() {
      int offset = myOffset + 274;
      return sharedMemory.get(offset) != 0;
    }
    void setIsAccelerating(boolean value) {      sharedMemory.put(myOffset + 274, (byte) (value ? 1 : 0));
    }
    boolean isAttacking() {
      int offset = myOffset + 275;
      return sharedMemory.get(offset) != 0;
    }
    void setIsAttacking(boolean value) {      sharedMemory.put(myOffset + 275, (byte) (value ? 1 : 0));
    }
    boolean isAttackFrame() {
      int offset = myOffset + 276;
      return sharedMemory.get(offset) != 0;
    }
    void setIsAttackFrame(boolean value) {      sharedMemory.put(myOffset + 276, (byte) (value ? 1 : 0));
    }
    boolean isBeingGathered() {
      int offset = myOffset + 277;
      return sharedMemory.get(offset) != 0;
    }
    void setIsBeingGathered(boolean value) {      sharedMemory.put(myOffset + 277, (byte) (value ? 1 : 0));
    }
    boolean isBlind() {
      int offset = myOffset + 278;
      return sharedMemory.get(offset) != 0;
    }
    void setIsBlind(boolean value) {      sharedMemory.put(myOffset + 278, (byte) (value ? 1 : 0));
    }
    boolean isBraking() {
      int offset = myOffset + 279;
      return sharedMemory.get(offset) != 0;
    }
    void setIsBraking(boolean value) {      sharedMemory.put(myOffset + 279, (byte) (value ? 1 : 0));
    }
    boolean isBurrowed() {
      int offset = myOffset + 280;
      return sharedMemory.get(offset) != 0;
    }
    void setIsBurrowed(boolean value) {      sharedMemory.put(myOffset + 280, (byte) (value ? 1 : 0));
    }
    int getCarryResourceType() {
      int offset = myOffset + 284;
      return sharedMemory.getInt(offset);
    }
    void setCarryResourceType(int value) {
      sharedMemory.putInt(myOffset + 284, value);
    }
    boolean isCloaked() {
      int offset = myOffset + 288;
      return sharedMemory.get(offset) != 0;
    }
    void setIsCloaked(boolean value) {      sharedMemory.put(myOffset + 288, (byte) (value ? 1 : 0));
    }
    boolean isCompleted() {
      int offset = myOffset + 289;
      return sharedMemory.get(offset) != 0;
    }
    void setIsCompleted(boolean value) {      sharedMemory.put(myOffset + 289, (byte) (value ? 1 : 0));
    }
    boolean isConstructing() {
      int offset = myOffset + 290;
      return sharedMemory.get(offset) != 0;
    }
    void setIsConstructing(boolean value) {      sharedMemory.put(myOffset + 290, (byte) (value ? 1 : 0));
    }
    boolean isDetected() {
      int offset = myOffset + 291;
      return sharedMemory.get(offset) != 0;
    }
    void setIsDetected(boolean value) {      sharedMemory.put(myOffset + 291, (byte) (value ? 1 : 0));
    }
    boolean isGathering() {
      int offset = myOffset + 292;
      return sharedMemory.get(offset) != 0;
    }
    void setIsGathering(boolean value) {      sharedMemory.put(myOffset + 292, (byte) (value ? 1 : 0));
    }
    boolean isHallucination() {
      int offset = myOffset + 293;
      return sharedMemory.get(offset) != 0;
    }
    void setIsHallucination(boolean value) {      sharedMemory.put(myOffset + 293, (byte) (value ? 1 : 0));
    }
    boolean isIdle() {
      int offset = myOffset + 294;
      return sharedMemory.get(offset) != 0;
    }
    void setIsIdle(boolean value) {      sharedMemory.put(myOffset + 294, (byte) (value ? 1 : 0));
    }
    boolean isInterruptible() {
      int offset = myOffset + 295;
      return sharedMemory.get(offset) != 0;
    }
    void setIsInterruptible(boolean value) {      sharedMemory.put(myOffset + 295, (byte) (value ? 1 : 0));
    }
    boolean isInvincible() {
      int offset = myOffset + 296;
      return sharedMemory.get(offset) != 0;
    }
    void setIsInvincible(boolean value) {      sharedMemory.put(myOffset + 296, (byte) (value ? 1 : 0));
    }
    boolean isLifted() {
      int offset = myOffset + 297;
      return sharedMemory.get(offset) != 0;
    }
    void setIsLifted(boolean value) {      sharedMemory.put(myOffset + 297, (byte) (value ? 1 : 0));
    }
    boolean isMorphing() {
      int offset = myOffset + 298;
      return sharedMemory.get(offset) != 0;
    }
    void setIsMorphing(boolean value) {      sharedMemory.put(myOffset + 298, (byte) (value ? 1 : 0));
    }
    boolean isMoving() {
      int offset = myOffset + 299;
      return sharedMemory.get(offset) != 0;
    }
    void setIsMoving(boolean value) {      sharedMemory.put(myOffset + 299, (byte) (value ? 1 : 0));
    }
    boolean isParasited() {
      int offset = myOffset + 300;
      return sharedMemory.get(offset) != 0;
    }
    void setIsParasited(boolean value) {      sharedMemory.put(myOffset + 300, (byte) (value ? 1 : 0));
    }
    boolean isSelected() {
      int offset = myOffset + 301;
      return sharedMemory.get(offset) != 0;
    }
    void setIsSelected(boolean value) {      sharedMemory.put(myOffset + 301, (byte) (value ? 1 : 0));
    }
    boolean isStartingAttack() {
      int offset = myOffset + 302;
      return sharedMemory.get(offset) != 0;
    }
    void setIsStartingAttack(boolean value) {      sharedMemory.put(myOffset + 302, (byte) (value ? 1 : 0));
    }
    boolean isStuck() {
      int offset = myOffset + 303;
      return sharedMemory.get(offset) != 0;
    }
    void setIsStuck(boolean value) {      sharedMemory.put(myOffset + 303, (byte) (value ? 1 : 0));
    }
    boolean isTraining() {
      int offset = myOffset + 304;
      return sharedMemory.get(offset) != 0;
    }
    void setIsTraining(boolean value) {      sharedMemory.put(myOffset + 304, (byte) (value ? 1 : 0));
    }
    boolean isUnderStorm() {
      int offset = myOffset + 305;
      return sharedMemory.get(offset) != 0;
    }
    void setIsUnderStorm(boolean value) {      sharedMemory.put(myOffset + 305, (byte) (value ? 1 : 0));
    }
    boolean isUnderDarkSwarm() {
      int offset = myOffset + 306;
      return sharedMemory.get(offset) != 0;
    }
    void setIsUnderDarkSwarm(boolean value) {      sharedMemory.put(myOffset + 306, (byte) (value ? 1 : 0));
    }
    boolean isUnderDWeb() {
      int offset = myOffset + 307;
      return sharedMemory.get(offset) != 0;
    }
    void setIsUnderDWeb(boolean value) {      sharedMemory.put(myOffset + 307, (byte) (value ? 1 : 0));
    }
    boolean isPowered() {
      int offset = myOffset + 308;
      return sharedMemory.get(offset) != 0;
    }
    void setIsPowered(boolean value) {      sharedMemory.put(myOffset + 308, (byte) (value ? 1 : 0));
    }
    boolean isVisible(int i) {
      int offset = myOffset + 309 + 1 * 1 * i;
      return sharedMemory.get(offset) != 0;
    }
    void setIsVisible(int i, boolean value) {      sharedMemory.put(myOffset + 309 + 1 * 1 * i, (byte) (value ? 1 : 0));
    }
    int getButtonset() {
      int offset = myOffset + 320;
      return sharedMemory.getInt(offset);
    }
    void setButtonset(int value) {
      sharedMemory.putInt(myOffset + 320, value);
    }
    int getLastAttackerPlayer() {
      int offset = myOffset + 324;
      return sharedMemory.getInt(offset);
    }
    void setLastAttackerPlayer(int value) {
      sharedMemory.putInt(myOffset + 324, value);
    }
    boolean getRecentlyAttacked() {
      int offset = myOffset + 328;
      return sharedMemory.get(offset) != 0;
    }
    void setRecentlyAttacked(boolean value) {      sharedMemory.put(myOffset + 328, (byte) (value ? 1 : 0));
    }
    int getReplayID() {
      int offset = myOffset + 332;
      return sharedMemory.getInt(offset);
    }
    void setReplayID(int value) {
      sharedMemory.putInt(myOffset + 332, value);
    }
  }
}

