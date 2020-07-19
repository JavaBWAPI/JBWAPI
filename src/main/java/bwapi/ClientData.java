package bwapi;
import java.nio.ByteBuffer;
final class ClientData {
    WrappedBuffer buffer;
    private int bufferOffset = 0;
    public void setBuffer(ByteBuffer buffer) {
        this.buffer = new WrappedBuffer(buffer);
    }
    public void setBufferOffset(int offset) {
        this.bufferOffset = offset;
    }
    class UnitCommand {
        static final int SIZE = 24;
        private int myOffset;
        public UnitCommand(int myOffset) {
          this.myOffset = myOffset;
        }
        int getTid() {
            int offset = bufferOffset + myOffset + 0;
            return buffer.getInt(offset);
        }
        void setTid(int value) {
            buffer.putInt(bufferOffset + myOffset + 0, value);
        }
        int getUnitIndex() {
            int offset = bufferOffset + myOffset + 4;
            return buffer.getInt(offset);
        }
        void setUnitIndex(int value) {
            buffer.putInt(bufferOffset + myOffset + 4, value);
        }
        int getTargetIndex() {
            int offset = bufferOffset + myOffset + 8;
            return buffer.getInt(offset);
        }
        void setTargetIndex(int value) {
            buffer.putInt(bufferOffset + myOffset + 8, value);
        }
        int getX() {
            int offset = bufferOffset + myOffset + 12;
            return buffer.getInt(offset);
        }
        void setX(int value) {
            buffer.putInt(bufferOffset + myOffset + 12, value);
        }
        int getY() {
            int offset = bufferOffset + myOffset + 16;
            return buffer.getInt(offset);
        }
        void setY(int value) {
            buffer.putInt(bufferOffset + myOffset + 16, value);
        }
        int getExtra() {
            int offset = bufferOffset + myOffset + 20;
            return buffer.getInt(offset);
        }
        void setExtra(int value) {
            buffer.putInt(bufferOffset + myOffset + 20, value);
        }
    }
    class GameData {
        static final int SIZE = 33017048;
        private int myOffset;
        public GameData(int myOffset) {
          this.myOffset = myOffset;
        }
        int getClient_version() {
            int offset = bufferOffset + myOffset + 0;
            return buffer.getInt(offset);
        }
        void setClient_version(int value) {
            buffer.putInt(bufferOffset + myOffset + 0, value);
        }
        int getRevision() {
            int offset = bufferOffset + myOffset + 4;
            return buffer.getInt(offset);
        }
        void setRevision(int value) {
            buffer.putInt(bufferOffset + myOffset + 4, value);
        }
        boolean isDebug() {
            int offset = bufferOffset + myOffset + 8;
            return buffer.getByte(offset) != 0;
        }
        void setIsDebug(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 8, (byte) (value ? 1 : 0));
        }
        int getInstanceID() {
            int offset = bufferOffset + myOffset + 12;
            return buffer.getInt(offset);
        }
        void setInstanceID(int value) {
            buffer.putInt(bufferOffset + myOffset + 12, value);
        }
        int getBotAPM_noselects() {
            int offset = bufferOffset + myOffset + 16;
            return buffer.getInt(offset);
        }
        void setBotAPM_noselects(int value) {
            buffer.putInt(bufferOffset + myOffset + 16, value);
        }
        int getBotAPM_selects() {
            int offset = bufferOffset + myOffset + 20;
            return buffer.getInt(offset);
        }
        void setBotAPM_selects(int value) {
            buffer.putInt(bufferOffset + myOffset + 20, value);
        }
        int getForceCount() {
            int offset = bufferOffset + myOffset + 24;
            return buffer.getInt(offset);
        }
        void setForceCount(int value) {
            buffer.putInt(bufferOffset + myOffset + 24, value);
        }
        ForceData getForces(int i) {
            int offset = bufferOffset + myOffset + 28 + 32 * 1 * i;
            return new ForceData(offset);
        }
        int getPlayerCount() {
            int offset = bufferOffset + myOffset + 188;
            return buffer.getInt(offset);
        }
        void setPlayerCount(int value) {
            buffer.putInt(bufferOffset + myOffset + 188, value);
        }
        PlayerData getPlayers(int i) {
            int offset = bufferOffset + myOffset + 192 + 5788 * 1 * i;
            return new PlayerData(offset);
        }
        int getInitialUnitCount() {
            int offset = bufferOffset + myOffset + 69648;
            return buffer.getInt(offset);
        }
        void setInitialUnitCount(int value) {
            buffer.putInt(bufferOffset + myOffset + 69648, value);
        }
        UnitData getUnits(int i) {
            int offset = bufferOffset + myOffset + 69656 + 336 * 1 * i;
            return new UnitData(offset);
        }
        int getUnitArray(int i) {
            int offset = bufferOffset + myOffset + 3429656 + 4 * 1 * i;
            return buffer.getInt(offset);
        }
        void setUnitArray(int i, int value) {
            buffer.putInt(bufferOffset + myOffset + 3429656 + 4 * 1 * i, value);
        }
        BulletData getBullets(int i) {
            int offset = bufferOffset + myOffset + 3436456 + 80 * 1 * i;
            return new BulletData(offset);
        }
        int getNukeDotCount() {
            int offset = bufferOffset + myOffset + 3444456;
            return buffer.getInt(offset);
        }
        void setNukeDotCount(int value) {
            buffer.putInt(bufferOffset + myOffset + 3444456, value);
        }
        Position getNukeDots(int i) {
            int offset = bufferOffset + myOffset + 3444460 + 8 * 1 * i;
            return new Position(offset);
        }
        int getGameType() {
            int offset = bufferOffset + myOffset + 3446060;
            return buffer.getInt(offset);
        }
        void setGameType(int value) {
            buffer.putInt(bufferOffset + myOffset + 3446060, value);
        }
        int getLatency() {
            int offset = bufferOffset + myOffset + 3446064;
            return buffer.getInt(offset);
        }
        void setLatency(int value) {
            buffer.putInt(bufferOffset + myOffset + 3446064, value);
        }
        int getLatencyFrames() {
            int offset = bufferOffset + myOffset + 3446068;
            return buffer.getInt(offset);
        }
        void setLatencyFrames(int value) {
            buffer.putInt(bufferOffset + myOffset + 3446068, value);
        }
        int getLatencyTime() {
            int offset = bufferOffset + myOffset + 3446072;
            return buffer.getInt(offset);
        }
        void setLatencyTime(int value) {
            buffer.putInt(bufferOffset + myOffset + 3446072, value);
        }
        int getRemainingLatencyFrames() {
            int offset = bufferOffset + myOffset + 3446076;
            return buffer.getInt(offset);
        }
        void setRemainingLatencyFrames(int value) {
            buffer.putInt(bufferOffset + myOffset + 3446076, value);
        }
        int getRemainingLatencyTime() {
            int offset = bufferOffset + myOffset + 3446080;
            return buffer.getInt(offset);
        }
        void setRemainingLatencyTime(int value) {
            buffer.putInt(bufferOffset + myOffset + 3446080, value);
        }
        boolean getHasLatCom() {
            int offset = bufferOffset + myOffset + 3446084;
            return buffer.getByte(offset) != 0;
        }
        void setHasLatCom(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 3446084, (byte) (value ? 1 : 0));
        }
        boolean getHasGUI() {
            int offset = bufferOffset + myOffset + 3446085;
            return buffer.getByte(offset) != 0;
        }
        void setHasGUI(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 3446085, (byte) (value ? 1 : 0));
        }
        int getReplayFrameCount() {
            int offset = bufferOffset + myOffset + 3446088;
            return buffer.getInt(offset);
        }
        void setReplayFrameCount(int value) {
            buffer.putInt(bufferOffset + myOffset + 3446088, value);
        }
        int getRandomSeed() {
            int offset = bufferOffset + myOffset + 3446092;
            return buffer.getInt(offset);
        }
        void setRandomSeed(int value) {
            buffer.putInt(bufferOffset + myOffset + 3446092, value);
        }
        int getFrameCount() {
            int offset = bufferOffset + myOffset + 3446096;
            return buffer.getInt(offset);
        }
        void setFrameCount(int value) {
            buffer.putInt(bufferOffset + myOffset + 3446096, value);
        }
        int getElapsedTime() {
            int offset = bufferOffset + myOffset + 3446100;
            return buffer.getInt(offset);
        }
        void setElapsedTime(int value) {
            buffer.putInt(bufferOffset + myOffset + 3446100, value);
        }
        int getCountdownTimer() {
            int offset = bufferOffset + myOffset + 3446104;
            return buffer.getInt(offset);
        }
        void setCountdownTimer(int value) {
            buffer.putInt(bufferOffset + myOffset + 3446104, value);
        }
        int getFps() {
            int offset = bufferOffset + myOffset + 3446108;
            return buffer.getInt(offset);
        }
        void setFps(int value) {
            buffer.putInt(bufferOffset + myOffset + 3446108, value);
        }
        double getAverageFPS() {
            int offset = bufferOffset + myOffset + 3446112;
            return buffer.getDouble(offset);
        }
        void setAverageFPS(double value) {
            buffer.putDouble(bufferOffset + myOffset + 3446112, value);
        }
        int getMouseX() {
            int offset = bufferOffset + myOffset + 3446120;
            return buffer.getInt(offset);
        }
        void setMouseX(int value) {
            buffer.putInt(bufferOffset + myOffset + 3446120, value);
        }
        int getMouseY() {
            int offset = bufferOffset + myOffset + 3446124;
            return buffer.getInt(offset);
        }
        void setMouseY(int value) {
            buffer.putInt(bufferOffset + myOffset + 3446124, value);
        }
        boolean getMouseState(int i) {
            int offset = bufferOffset + myOffset + 3446128 + 1 * 1 * i;
            return buffer.getByte(offset) != 0;
        }
        void setMouseState(int i, boolean value) {
            buffer.putByte(bufferOffset + myOffset + 3446128 + 1 * 1 * i, (byte) (value ? 1 : 0));
        }
        boolean getKeyState(int i) {
            int offset = bufferOffset + myOffset + 3446131 + 1 * 1 * i;
            return buffer.getByte(offset) != 0;
        }
        void setKeyState(int i, boolean value) {
            buffer.putByte(bufferOffset + myOffset + 3446131 + 1 * 1 * i, (byte) (value ? 1 : 0));
        }
        int getScreenX() {
            int offset = bufferOffset + myOffset + 3446388;
            return buffer.getInt(offset);
        }
        void setScreenX(int value) {
            buffer.putInt(bufferOffset + myOffset + 3446388, value);
        }
        int getScreenY() {
            int offset = bufferOffset + myOffset + 3446392;
            return buffer.getInt(offset);
        }
        void setScreenY(int value) {
            buffer.putInt(bufferOffset + myOffset + 3446392, value);
        }
        boolean getFlags(int i) {
            int offset = bufferOffset + myOffset + 3446396 + 1 * 1 * i;
            return buffer.getByte(offset) != 0;
        }
        void setFlags(int i, boolean value) {
            buffer.putByte(bufferOffset + myOffset + 3446396 + 1 * 1 * i, (byte) (value ? 1 : 0));
        }
        int getMapWidth() {
            int offset = bufferOffset + myOffset + 3446400;
            return buffer.getInt(offset);
        }
        void setMapWidth(int value) {
            buffer.putInt(bufferOffset + myOffset + 3446400, value);
        }
        int getMapHeight() {
            int offset = bufferOffset + myOffset + 3446404;
            return buffer.getInt(offset);
        }
        void setMapHeight(int value) {
            buffer.putInt(bufferOffset + myOffset + 3446404, value);
        }
        String getMapFileName() {
            int offset = bufferOffset + myOffset + 3446408;
            return buffer.getString(offset, 261);
        }
        void setMapFileName(String value) {
            buffer.putString(bufferOffset + myOffset + 3446408, 261, value);
        }
        String getMapPathName() {
            int offset = bufferOffset + myOffset + 3446669;
            return buffer.getString(offset, 261);
        }
        void setMapPathName(String value) {
            buffer.putString(bufferOffset + myOffset + 3446669, 261, value);
        }
        String getMapName() {
            int offset = bufferOffset + myOffset + 3446930;
            return buffer.getString(offset, 33);
        }
        void setMapName(String value) {
            buffer.putString(bufferOffset + myOffset + 3446930, 33, value);
        }
        String getMapHash() {
            int offset = bufferOffset + myOffset + 3446963;
            return buffer.getString(offset, 41);
        }
        void setMapHash(String value) {
            buffer.putString(bufferOffset + myOffset + 3446963, 41, value);
        }
        int getGroundHeight(int i, int j) {
            int offset = bufferOffset + myOffset + 3447004 + 4 * 1 * j + 4 * 256 * i;
            return buffer.getInt(offset);
        }
        void setGetGroundHeight(int i, int j, int value) {
            buffer.putInt(bufferOffset + myOffset + 3447004 + 4 * 1 * j + 4 * 256 * i, value);
        }
        boolean isWalkable(int i, int j) {
            int offset = bufferOffset + myOffset + 3709148 + 1 * 1 * j + 1 * 1024 * i;
            return buffer.getByte(offset) != 0;
        }
        void setIsWalkable(int i, int j, boolean value) {
            buffer.putByte(bufferOffset + myOffset + 3709148 + 1 * 1 * j + 1 * 1024 * i, (byte) (value ? 1 : 0));
        }
        boolean isBuildable(int i, int j) {
            int offset = bufferOffset + myOffset + 4757724 + 1 * 1 * j + 1 * 256 * i;
            return buffer.getByte(offset) != 0;
        }
        void setIsBuildable(int i, int j, boolean value) {
            buffer.putByte(bufferOffset + myOffset + 4757724 + 1 * 1 * j + 1 * 256 * i, (byte) (value ? 1 : 0));
        }
        boolean isVisible(int i, int j) {
            int offset = bufferOffset + myOffset + 4823260 + 1 * 1 * j + 1 * 256 * i;
            return buffer.getByte(offset) != 0;
        }
        void setIsVisible(int i, int j, boolean value) {
            buffer.putByte(bufferOffset + myOffset + 4823260 + 1 * 1 * j + 1 * 256 * i, (byte) (value ? 1 : 0));
        }
        boolean isExplored(int i, int j) {
            int offset = bufferOffset + myOffset + 4888796 + 1 * 1 * j + 1 * 256 * i;
            return buffer.getByte(offset) != 0;
        }
        void setIsExplored(int i, int j, boolean value) {
            buffer.putByte(bufferOffset + myOffset + 4888796 + 1 * 1 * j + 1 * 256 * i, (byte) (value ? 1 : 0));
        }
        boolean getHasCreep(int i, int j) {
            int offset = bufferOffset + myOffset + 4954332 + 1 * 1 * j + 1 * 256 * i;
            return buffer.getByte(offset) != 0;
        }
        void setHasCreep(int i, int j, boolean value) {
            buffer.putByte(bufferOffset + myOffset + 4954332 + 1 * 1 * j + 1 * 256 * i, (byte) (value ? 1 : 0));
        }
        boolean isOccupied(int i, int j) {
            int offset = bufferOffset + myOffset + 5019868 + 1 * 1 * j + 1 * 256 * i;
            return buffer.getByte(offset) != 0;
        }
        void setIsOccupied(int i, int j, boolean value) {
            buffer.putByte(bufferOffset + myOffset + 5019868 + 1 * 1 * j + 1 * 256 * i, (byte) (value ? 1 : 0));
        }
        short getMapTileRegionId(int i, int j) {
            int offset = bufferOffset + myOffset + 5085404 + 2 * 1 * j + 2 * 256 * i;
            return buffer.getShort(offset);
        }
        void setMapTileRegionId(int i, int j, short value) {
            buffer.putShort(bufferOffset + myOffset + 5085404 + 2 * 1 * j + 2 * 256 * i, value);
        }
        short getMapSplitTilesMiniTileMask(int i) {
            int offset = bufferOffset + myOffset + 5216476 + 2 * 1 * i;
            return buffer.getShort(offset);
        }
        void setMapSplitTilesMiniTileMask(int i, short value) {
            buffer.putShort(bufferOffset + myOffset + 5216476 + 2 * 1 * i, value);
        }
        short getMapSplitTilesRegion1(int i) {
            int offset = bufferOffset + myOffset + 5226476 + 2 * 1 * i;
            return buffer.getShort(offset);
        }
        void setMapSplitTilesRegion1(int i, short value) {
            buffer.putShort(bufferOffset + myOffset + 5226476 + 2 * 1 * i, value);
        }
        short getMapSplitTilesRegion2(int i) {
            int offset = bufferOffset + myOffset + 5236476 + 2 * 1 * i;
            return buffer.getShort(offset);
        }
        void setMapSplitTilesRegion2(int i, short value) {
            buffer.putShort(bufferOffset + myOffset + 5236476 + 2 * 1 * i, value);
        }
        int getRegionCount() {
            int offset = bufferOffset + myOffset + 5246476;
            return buffer.getInt(offset);
        }
        void setRegionCount(int value) {
            buffer.putInt(bufferOffset + myOffset + 5246476, value);
        }
        RegionData getRegions(int i) {
            int offset = bufferOffset + myOffset + 5246480 + 1068 * 1 * i;
            return new RegionData(offset);
        }
        int getStartLocationCount() {
            int offset = bufferOffset + myOffset + 10586480;
            return buffer.getInt(offset);
        }
        void setStartLocationCount(int value) {
            buffer.putInt(bufferOffset + myOffset + 10586480, value);
        }
        Position getStartLocations(int i) {
            int offset = bufferOffset + myOffset + 10586484 + 8 * 1 * i;
            return new Position(offset);
        }
        boolean isInGame() {
            int offset = bufferOffset + myOffset + 10586548;
            return buffer.getByte(offset) != 0;
        }
        void setIsInGame(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 10586548, (byte) (value ? 1 : 0));
        }
        boolean isMultiplayer() {
            int offset = bufferOffset + myOffset + 10586549;
            return buffer.getByte(offset) != 0;
        }
        void setIsMultiplayer(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 10586549, (byte) (value ? 1 : 0));
        }
        boolean isBattleNet() {
            int offset = bufferOffset + myOffset + 10586550;
            return buffer.getByte(offset) != 0;
        }
        void setIsBattleNet(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 10586550, (byte) (value ? 1 : 0));
        }
        boolean isPaused() {
            int offset = bufferOffset + myOffset + 10586551;
            return buffer.getByte(offset) != 0;
        }
        void setIsPaused(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 10586551, (byte) (value ? 1 : 0));
        }
        boolean isReplay() {
            int offset = bufferOffset + myOffset + 10586552;
            return buffer.getByte(offset) != 0;
        }
        void setIsReplay(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 10586552, (byte) (value ? 1 : 0));
        }
        int getSelectedUnitCount() {
            int offset = bufferOffset + myOffset + 10586556;
            return buffer.getInt(offset);
        }
        void setSelectedUnitCount(int value) {
            buffer.putInt(bufferOffset + myOffset + 10586556, value);
        }
        int getSelectedUnits(int i) {
            int offset = bufferOffset + myOffset + 10586560 + 4 * 1 * i;
            return buffer.getInt(offset);
        }
        void setSelectedUnits(int i, int value) {
            buffer.putInt(bufferOffset + myOffset + 10586560 + 4 * 1 * i, value);
        }
        int getSelf() {
            int offset = bufferOffset + myOffset + 10586608;
            return buffer.getInt(offset);
        }
        void setSelf(int value) {
            buffer.putInt(bufferOffset + myOffset + 10586608, value);
        }
        int getEnemy() {
            int offset = bufferOffset + myOffset + 10586612;
            return buffer.getInt(offset);
        }
        void setEnemy(int value) {
            buffer.putInt(bufferOffset + myOffset + 10586612, value);
        }
        int getNeutral() {
            int offset = bufferOffset + myOffset + 10586616;
            return buffer.getInt(offset);
        }
        void setNeutral(int value) {
            buffer.putInt(bufferOffset + myOffset + 10586616, value);
        }
        int getEventCount() {
            int offset = bufferOffset + myOffset + 10586620;
            return buffer.getInt(offset);
        }
        void setEventCount(int value) {
            buffer.putInt(bufferOffset + myOffset + 10586620, value);
        }
        Event getEvents(int i) {
            int offset = bufferOffset + myOffset + 10586624 + 12 * 1 * i;
            return new Event(offset);
        }
        int getEventStringCount() {
            int offset = bufferOffset + myOffset + 10706624;
            return buffer.getInt(offset);
        }
        void setEventStringCount(int value) {
            buffer.putInt(bufferOffset + myOffset + 10706624, value);
        }
        String getEventStrings(int i) {
            int offset = bufferOffset + myOffset + 10706628 + 1 * 256 * i;
            return buffer.getString(offset, 256);
        }
        void setEventStrings(int i, String value) {
            buffer.putString(bufferOffset + myOffset + 10706628 + 1 * 256 * i, 256, value);
        }
        int getStringCount() {
            int offset = bufferOffset + myOffset + 10962628;
            return buffer.getInt(offset);
        }
        void setStringCount(int value) {
            buffer.putInt(bufferOffset + myOffset + 10962628, value);
        }
        String getStrings(int i) {
            int offset = bufferOffset + myOffset + 10962632 + 1 * 1024 * i;
            return buffer.getString(offset, 1024);
        }
        void setStrings(int i, String value) {
            buffer.putString(bufferOffset + myOffset + 10962632 + 1 * 1024 * i, 1024, value);
        }
        int getShapeCount() {
            int offset = bufferOffset + myOffset + 31442632;
            return buffer.getInt(offset);
        }
        void setShapeCount(int value) {
            buffer.putInt(bufferOffset + myOffset + 31442632, value);
        }
        Shape getShapes(int i) {
            int offset = bufferOffset + myOffset + 31442636 + 40 * 1 * i;
            return new Shape(offset);
        }
        int getCommandCount() {
            int offset = bufferOffset + myOffset + 32242636;
            return buffer.getInt(offset);
        }
        void setCommandCount(int value) {
            buffer.putInt(bufferOffset + myOffset + 32242636, value);
        }
        Command getCommands(int i) {
            int offset = bufferOffset + myOffset + 32242640 + 12 * 1 * i;
            return new Command(offset);
        }
        int getUnitCommandCount() {
            int offset = bufferOffset + myOffset + 32482640;
            return buffer.getInt(offset);
        }
        void setUnitCommandCount(int value) {
            buffer.putInt(bufferOffset + myOffset + 32482640, value);
        }
        UnitCommand getUnitCommands(int i) {
            int offset = bufferOffset + myOffset + 32482644 + 24 * 1 * i;
            return new UnitCommand(offset);
        }
        int getUnitSearchSize() {
            int offset = bufferOffset + myOffset + 32962644;
            return buffer.getInt(offset);
        }
        void setUnitSearchSize(int value) {
            buffer.putInt(bufferOffset + myOffset + 32962644, value);
        }
        unitFinder getXUnitSearch(int i) {
            int offset = bufferOffset + myOffset + 32962648 + 8 * 1 * i;
            return new unitFinder(offset);
        }
        unitFinder getYUnitSearch(int i) {
            int offset = bufferOffset + myOffset + 32989848 + 8 * 1 * i;
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
            int offset = bufferOffset + myOffset + 0;
            return ShapeType.idToEnum[buffer.getInt(offset)];
        }
        void setType(ShapeType value) {
            buffer.putInt(bufferOffset + myOffset + 0, value.id);
        }
        CoordinateType getCtype() {
            int offset = bufferOffset + myOffset + 4;
            return CoordinateType.idToEnum[buffer.getInt(offset)];
        }
        void setCtype(CoordinateType value) {
            buffer.putInt(bufferOffset + myOffset + 4, value.id);
        }
        int getX1() {
            int offset = bufferOffset + myOffset + 8;
            return buffer.getInt(offset);
        }
        void setX1(int value) {
            buffer.putInt(bufferOffset + myOffset + 8, value);
        }
        int getY1() {
            int offset = bufferOffset + myOffset + 12;
            return buffer.getInt(offset);
        }
        void setY1(int value) {
            buffer.putInt(bufferOffset + myOffset + 12, value);
        }
        int getX2() {
            int offset = bufferOffset + myOffset + 16;
            return buffer.getInt(offset);
        }
        void setX2(int value) {
            buffer.putInt(bufferOffset + myOffset + 16, value);
        }
        int getY2() {
            int offset = bufferOffset + myOffset + 20;
            return buffer.getInt(offset);
        }
        void setY2(int value) {
            buffer.putInt(bufferOffset + myOffset + 20, value);
        }
        int getExtra1() {
            int offset = bufferOffset + myOffset + 24;
            return buffer.getInt(offset);
        }
        void setExtra1(int value) {
            buffer.putInt(bufferOffset + myOffset + 24, value);
        }
        int getExtra2() {
            int offset = bufferOffset + myOffset + 28;
            return buffer.getInt(offset);
        }
        void setExtra2(int value) {
            buffer.putInt(bufferOffset + myOffset + 28, value);
        }
        int getColor() {
            int offset = bufferOffset + myOffset + 32;
            return buffer.getInt(offset);
        }
        void setColor(int value) {
            buffer.putInt(bufferOffset + myOffset + 32, value);
        }
        boolean isSolid() {
            int offset = bufferOffset + myOffset + 36;
            return buffer.getByte(offset) != 0;
        }
        void setIsSolid(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 36, (byte) (value ? 1 : 0));
        }
    }
    class Command {
        static final int SIZE = 12;
        private int myOffset;
        public Command(int myOffset) {
          this.myOffset = myOffset;
        }
        CommandType getType() {
            int offset = bufferOffset + myOffset + 0;
            return CommandType.idToEnum[buffer.getInt(offset)];
        }
        void setType(CommandType value) {
            buffer.putInt(bufferOffset + myOffset + 0, value.id);
        }
        int getValue1() {
            int offset = bufferOffset + myOffset + 4;
            return buffer.getInt(offset);
        }
        void setValue1(int value) {
            buffer.putInt(bufferOffset + myOffset + 4, value);
        }
        int getValue2() {
            int offset = bufferOffset + myOffset + 8;
            return buffer.getInt(offset);
        }
        void setValue2(int value) {
            buffer.putInt(bufferOffset + myOffset + 8, value);
        }
    }
    class Position {
        static final int SIZE = 8;
        private int myOffset;
        public Position(int myOffset) {
          this.myOffset = myOffset;
        }
        int getX() {
            int offset = bufferOffset + myOffset + 0;
            return buffer.getInt(offset);
        }
        void setX(int value) {
            buffer.putInt(bufferOffset + myOffset + 0, value);
        }
        int getY() {
            int offset = bufferOffset + myOffset + 4;
            return buffer.getInt(offset);
        }
        void setY(int value) {
            buffer.putInt(bufferOffset + myOffset + 4, value);
        }
    }
    class Event {
        static final int SIZE = 12;
        private int myOffset;
        public Event(int myOffset) {
          this.myOffset = myOffset;
        }
        EventType getType() {
            int offset = bufferOffset + myOffset + 0;
            return EventType.idToEnum[buffer.getInt(offset)];
        }
        void setType(EventType value) {
            buffer.putInt(bufferOffset + myOffset + 0, value.id);
        }
        int getV1() {
            int offset = bufferOffset + myOffset + 4;
            return buffer.getInt(offset);
        }
        void setV1(int value) {
            buffer.putInt(bufferOffset + myOffset + 4, value);
        }
        int getV2() {
            int offset = bufferOffset + myOffset + 8;
            return buffer.getInt(offset);
        }
        void setV2(int value) {
            buffer.putInt(bufferOffset + myOffset + 8, value);
        }
    }
    class RegionData {
        static final int SIZE = 1068;
        private int myOffset;
        public RegionData(int myOffset) {
          this.myOffset = myOffset;
        }
        int getId() {
            int offset = bufferOffset + myOffset + 0;
            return buffer.getInt(offset);
        }
        void setId(int value) {
            buffer.putInt(bufferOffset + myOffset + 0, value);
        }
        int islandID() {
            int offset = bufferOffset + myOffset + 4;
            return buffer.getInt(offset);
        }
        void setIslandID(int value) {
            buffer.putInt(bufferOffset + myOffset + 4, value);
        }
        int getCenter_x() {
            int offset = bufferOffset + myOffset + 8;
            return buffer.getInt(offset);
        }
        void setCenter_x(int value) {
            buffer.putInt(bufferOffset + myOffset + 8, value);
        }
        int getCenter_y() {
            int offset = bufferOffset + myOffset + 12;
            return buffer.getInt(offset);
        }
        void setCenter_y(int value) {
            buffer.putInt(bufferOffset + myOffset + 12, value);
        }
        int getPriority() {
            int offset = bufferOffset + myOffset + 16;
            return buffer.getInt(offset);
        }
        void setPriority(int value) {
            buffer.putInt(bufferOffset + myOffset + 16, value);
        }
        int getLeftMost() {
            int offset = bufferOffset + myOffset + 20;
            return buffer.getInt(offset);
        }
        void setLeftMost(int value) {
            buffer.putInt(bufferOffset + myOffset + 20, value);
        }
        int getRightMost() {
            int offset = bufferOffset + myOffset + 24;
            return buffer.getInt(offset);
        }
        void setRightMost(int value) {
            buffer.putInt(bufferOffset + myOffset + 24, value);
        }
        int getTopMost() {
            int offset = bufferOffset + myOffset + 28;
            return buffer.getInt(offset);
        }
        void setTopMost(int value) {
            buffer.putInt(bufferOffset + myOffset + 28, value);
        }
        int getBottomMost() {
            int offset = bufferOffset + myOffset + 32;
            return buffer.getInt(offset);
        }
        void setBottomMost(int value) {
            buffer.putInt(bufferOffset + myOffset + 32, value);
        }
        int getNeighborCount() {
            int offset = bufferOffset + myOffset + 36;
            return buffer.getInt(offset);
        }
        void setNeighborCount(int value) {
            buffer.putInt(bufferOffset + myOffset + 36, value);
        }
        int getNeighbors(int i) {
            int offset = bufferOffset + myOffset + 40 + 4 * 1 * i;
            return buffer.getInt(offset);
        }
        void setNeighbors(int i, int value) {
            buffer.putInt(bufferOffset + myOffset + 40 + 4 * 1 * i, value);
        }
        boolean isAccessible() {
            int offset = bufferOffset + myOffset + 1064;
            return buffer.getByte(offset) != 0;
        }
        void setIsAccessible(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 1064, (byte) (value ? 1 : 0));
        }
        boolean isHigherGround() {
            int offset = bufferOffset + myOffset + 1065;
            return buffer.getByte(offset) != 0;
        }
        void setIsHigherGround(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 1065, (byte) (value ? 1 : 0));
        }
    }
    class ForceData {
        static final int SIZE = 32;
        private int myOffset;
        public ForceData(int myOffset) {
          this.myOffset = myOffset;
        }
        String getName() {
            int offset = bufferOffset + myOffset + 0;
            return buffer.getString(offset, 32);
        }
        void setName(String value) {
            buffer.putString(bufferOffset + myOffset + 0, 32, value);
        }
    }
    class PlayerData {
        static final int SIZE = 5788;
        private int myOffset;
        public PlayerData(int myOffset) {
          this.myOffset = myOffset;
        }
        String getName() {
            int offset = bufferOffset + myOffset + 0;
            return buffer.getString(offset, 25);
        }
        void setName(String value) {
            buffer.putString(bufferOffset + myOffset + 0, 25, value);
        }
        int getRace() {
            int offset = bufferOffset + myOffset + 28;
            return buffer.getInt(offset);
        }
        void setRace(int value) {
            buffer.putInt(bufferOffset + myOffset + 28, value);
        }
        int getType() {
            int offset = bufferOffset + myOffset + 32;
            return buffer.getInt(offset);
        }
        void setType(int value) {
            buffer.putInt(bufferOffset + myOffset + 32, value);
        }
        int getForce() {
            int offset = bufferOffset + myOffset + 36;
            return buffer.getInt(offset);
        }
        void setForce(int value) {
            buffer.putInt(bufferOffset + myOffset + 36, value);
        }
        boolean isAlly(int i) {
            int offset = bufferOffset + myOffset + 40 + 1 * 1 * i;
            return buffer.getByte(offset) != 0;
        }
        void setIsAlly(int i, boolean value) {
            buffer.putByte(bufferOffset + myOffset + 40 + 1 * 1 * i, (byte) (value ? 1 : 0));
        }
        boolean isEnemy(int i) {
            int offset = bufferOffset + myOffset + 52 + 1 * 1 * i;
            return buffer.getByte(offset) != 0;
        }
        void setIsEnemy(int i, boolean value) {
            buffer.putByte(bufferOffset + myOffset + 52 + 1 * 1 * i, (byte) (value ? 1 : 0));
        }
        boolean isNeutral() {
            int offset = bufferOffset + myOffset + 64;
            return buffer.getByte(offset) != 0;
        }
        void setIsNeutral(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 64, (byte) (value ? 1 : 0));
        }
        int getStartLocationX() {
            int offset = bufferOffset + myOffset + 68;
            return buffer.getInt(offset);
        }
        void setStartLocationX(int value) {
            buffer.putInt(bufferOffset + myOffset + 68, value);
        }
        int getStartLocationY() {
            int offset = bufferOffset + myOffset + 72;
            return buffer.getInt(offset);
        }
        void setStartLocationY(int value) {
            buffer.putInt(bufferOffset + myOffset + 72, value);
        }
        boolean isVictorious() {
            int offset = bufferOffset + myOffset + 76;
            return buffer.getByte(offset) != 0;
        }
        void setIsVictorious(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 76, (byte) (value ? 1 : 0));
        }
        boolean isDefeated() {
            int offset = bufferOffset + myOffset + 77;
            return buffer.getByte(offset) != 0;
        }
        void setIsDefeated(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 77, (byte) (value ? 1 : 0));
        }
        boolean getLeftGame() {
            int offset = bufferOffset + myOffset + 78;
            return buffer.getByte(offset) != 0;
        }
        void setLeftGame(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 78, (byte) (value ? 1 : 0));
        }
        boolean isParticipating() {
            int offset = bufferOffset + myOffset + 79;
            return buffer.getByte(offset) != 0;
        }
        void setIsParticipating(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 79, (byte) (value ? 1 : 0));
        }
        int getMinerals() {
            int offset = bufferOffset + myOffset + 80;
            return buffer.getInt(offset);
        }
        void setMinerals(int value) {
            buffer.putInt(bufferOffset + myOffset + 80, value);
        }
        int getGas() {
            int offset = bufferOffset + myOffset + 84;
            return buffer.getInt(offset);
        }
        void setGas(int value) {
            buffer.putInt(bufferOffset + myOffset + 84, value);
        }
        int getGatheredMinerals() {
            int offset = bufferOffset + myOffset + 88;
            return buffer.getInt(offset);
        }
        void setGatheredMinerals(int value) {
            buffer.putInt(bufferOffset + myOffset + 88, value);
        }
        int getGatheredGas() {
            int offset = bufferOffset + myOffset + 92;
            return buffer.getInt(offset);
        }
        void setGatheredGas(int value) {
            buffer.putInt(bufferOffset + myOffset + 92, value);
        }
        int getRepairedMinerals() {
            int offset = bufferOffset + myOffset + 96;
            return buffer.getInt(offset);
        }
        void setRepairedMinerals(int value) {
            buffer.putInt(bufferOffset + myOffset + 96, value);
        }
        int getRepairedGas() {
            int offset = bufferOffset + myOffset + 100;
            return buffer.getInt(offset);
        }
        void setRepairedGas(int value) {
            buffer.putInt(bufferOffset + myOffset + 100, value);
        }
        int getRefundedMinerals() {
            int offset = bufferOffset + myOffset + 104;
            return buffer.getInt(offset);
        }
        void setRefundedMinerals(int value) {
            buffer.putInt(bufferOffset + myOffset + 104, value);
        }
        int getRefundedGas() {
            int offset = bufferOffset + myOffset + 108;
            return buffer.getInt(offset);
        }
        void setRefundedGas(int value) {
            buffer.putInt(bufferOffset + myOffset + 108, value);
        }
        int getSupplyTotal(int i) {
            int offset = bufferOffset + myOffset + 112 + 4 * 1 * i;
            return buffer.getInt(offset);
        }
        void setSupplyTotal(int i, int value) {
            buffer.putInt(bufferOffset + myOffset + 112 + 4 * 1 * i, value);
        }
        int getSupplyUsed(int i) {
            int offset = bufferOffset + myOffset + 124 + 4 * 1 * i;
            return buffer.getInt(offset);
        }
        void setSupplyUsed(int i, int value) {
            buffer.putInt(bufferOffset + myOffset + 124 + 4 * 1 * i, value);
        }
        int getAllUnitCount(int i) {
            int offset = bufferOffset + myOffset + 136 + 4 * 1 * i;
            return buffer.getInt(offset);
        }
        void setAllUnitCount(int i, int value) {
            buffer.putInt(bufferOffset + myOffset + 136 + 4 * 1 * i, value);
        }
        int getVisibleUnitCount(int i) {
            int offset = bufferOffset + myOffset + 1072 + 4 * 1 * i;
            return buffer.getInt(offset);
        }
        void setVisibleUnitCount(int i, int value) {
            buffer.putInt(bufferOffset + myOffset + 1072 + 4 * 1 * i, value);
        }
        int getCompletedUnitCount(int i) {
            int offset = bufferOffset + myOffset + 2008 + 4 * 1 * i;
            return buffer.getInt(offset);
        }
        void setCompletedUnitCount(int i, int value) {
            buffer.putInt(bufferOffset + myOffset + 2008 + 4 * 1 * i, value);
        }
        int getDeadUnitCount(int i) {
            int offset = bufferOffset + myOffset + 2944 + 4 * 1 * i;
            return buffer.getInt(offset);
        }
        void setDeadUnitCount(int i, int value) {
            buffer.putInt(bufferOffset + myOffset + 2944 + 4 * 1 * i, value);
        }
        int getKilledUnitCount(int i) {
            int offset = bufferOffset + myOffset + 3880 + 4 * 1 * i;
            return buffer.getInt(offset);
        }
        void setKilledUnitCount(int i, int value) {
            buffer.putInt(bufferOffset + myOffset + 3880 + 4 * 1 * i, value);
        }
        int getUpgradeLevel(int i) {
            int offset = bufferOffset + myOffset + 4816 + 4 * 1 * i;
            return buffer.getInt(offset);
        }
        void setUpgradeLevel(int i, int value) {
            buffer.putInt(bufferOffset + myOffset + 4816 + 4 * 1 * i, value);
        }
        boolean getHasResearched(int i) {
            int offset = bufferOffset + myOffset + 5068 + 1 * 1 * i;
            return buffer.getByte(offset) != 0;
        }
        void setHasResearched(int i, boolean value) {
            buffer.putByte(bufferOffset + myOffset + 5068 + 1 * 1 * i, (byte) (value ? 1 : 0));
        }
        boolean isResearching(int i) {
            int offset = bufferOffset + myOffset + 5115 + 1 * 1 * i;
            return buffer.getByte(offset) != 0;
        }
        void setIsResearching(int i, boolean value) {
            buffer.putByte(bufferOffset + myOffset + 5115 + 1 * 1 * i, (byte) (value ? 1 : 0));
        }
        boolean isUpgrading(int i) {
            int offset = bufferOffset + myOffset + 5162 + 1 * 1 * i;
            return buffer.getByte(offset) != 0;
        }
        void setIsUpgrading(int i, boolean value) {
            buffer.putByte(bufferOffset + myOffset + 5162 + 1 * 1 * i, (byte) (value ? 1 : 0));
        }
        int getColor() {
            int offset = bufferOffset + myOffset + 5228;
            return buffer.getInt(offset);
        }
        void setColor(int value) {
            buffer.putInt(bufferOffset + myOffset + 5228, value);
        }
        int getTotalUnitScore() {
            int offset = bufferOffset + myOffset + 5232;
            return buffer.getInt(offset);
        }
        void setTotalUnitScore(int value) {
            buffer.putInt(bufferOffset + myOffset + 5232, value);
        }
        int getTotalKillScore() {
            int offset = bufferOffset + myOffset + 5236;
            return buffer.getInt(offset);
        }
        void setTotalKillScore(int value) {
            buffer.putInt(bufferOffset + myOffset + 5236, value);
        }
        int getTotalBuildingScore() {
            int offset = bufferOffset + myOffset + 5240;
            return buffer.getInt(offset);
        }
        void setTotalBuildingScore(int value) {
            buffer.putInt(bufferOffset + myOffset + 5240, value);
        }
        int getTotalRazingScore() {
            int offset = bufferOffset + myOffset + 5244;
            return buffer.getInt(offset);
        }
        void setTotalRazingScore(int value) {
            buffer.putInt(bufferOffset + myOffset + 5244, value);
        }
        int getCustomScore() {
            int offset = bufferOffset + myOffset + 5248;
            return buffer.getInt(offset);
        }
        void setCustomScore(int value) {
            buffer.putInt(bufferOffset + myOffset + 5248, value);
        }
        int getMaxUpgradeLevel(int i) {
            int offset = bufferOffset + myOffset + 5252 + 4 * 1 * i;
            return buffer.getInt(offset);
        }
        void setMaxUpgradeLevel(int i, int value) {
            buffer.putInt(bufferOffset + myOffset + 5252 + 4 * 1 * i, value);
        }
        boolean isResearchAvailable(int i) {
            int offset = bufferOffset + myOffset + 5504 + 1 * 1 * i;
            return buffer.getByte(offset) != 0;
        }
        void setIsResearchAvailable(int i, boolean value) {
            buffer.putByte(bufferOffset + myOffset + 5504 + 1 * 1 * i, (byte) (value ? 1 : 0));
        }
        boolean isUnitAvailable(int i) {
            int offset = bufferOffset + myOffset + 5551 + 1 * 1 * i;
            return buffer.getByte(offset) != 0;
        }
        void setIsUnitAvailable(int i, boolean value) {
            buffer.putByte(bufferOffset + myOffset + 5551 + 1 * 1 * i, (byte) (value ? 1 : 0));
        }
    }
    class BulletData {
        static final int SIZE = 80;
        private int myOffset;
        public BulletData(int myOffset) {
          this.myOffset = myOffset;
        }
        int getId() {
            int offset = bufferOffset + myOffset + 0;
            return buffer.getInt(offset);
        }
        void setId(int value) {
            buffer.putInt(bufferOffset + myOffset + 0, value);
        }
        int getPlayer() {
            int offset = bufferOffset + myOffset + 4;
            return buffer.getInt(offset);
        }
        void setPlayer(int value) {
            buffer.putInt(bufferOffset + myOffset + 4, value);
        }
        int getType() {
            int offset = bufferOffset + myOffset + 8;
            return buffer.getInt(offset);
        }
        void setType(int value) {
            buffer.putInt(bufferOffset + myOffset + 8, value);
        }
        int getSource() {
            int offset = bufferOffset + myOffset + 12;
            return buffer.getInt(offset);
        }
        void setSource(int value) {
            buffer.putInt(bufferOffset + myOffset + 12, value);
        }
        int getPositionX() {
            int offset = bufferOffset + myOffset + 16;
            return buffer.getInt(offset);
        }
        void setPositionX(int value) {
            buffer.putInt(bufferOffset + myOffset + 16, value);
        }
        int getPositionY() {
            int offset = bufferOffset + myOffset + 20;
            return buffer.getInt(offset);
        }
        void setPositionY(int value) {
            buffer.putInt(bufferOffset + myOffset + 20, value);
        }
        double getAngle() {
            int offset = bufferOffset + myOffset + 24;
            return buffer.getDouble(offset);
        }
        void setAngle(double value) {
            buffer.putDouble(bufferOffset + myOffset + 24, value);
        }
        double getVelocityX() {
            int offset = bufferOffset + myOffset + 32;
            return buffer.getDouble(offset);
        }
        void setVelocityX(double value) {
            buffer.putDouble(bufferOffset + myOffset + 32, value);
        }
        double getVelocityY() {
            int offset = bufferOffset + myOffset + 40;
            return buffer.getDouble(offset);
        }
        void setVelocityY(double value) {
            buffer.putDouble(bufferOffset + myOffset + 40, value);
        }
        int getTarget() {
            int offset = bufferOffset + myOffset + 48;
            return buffer.getInt(offset);
        }
        void setTarget(int value) {
            buffer.putInt(bufferOffset + myOffset + 48, value);
        }
        int getTargetPositionX() {
            int offset = bufferOffset + myOffset + 52;
            return buffer.getInt(offset);
        }
        void setTargetPositionX(int value) {
            buffer.putInt(bufferOffset + myOffset + 52, value);
        }
        int getTargetPositionY() {
            int offset = bufferOffset + myOffset + 56;
            return buffer.getInt(offset);
        }
        void setTargetPositionY(int value) {
            buffer.putInt(bufferOffset + myOffset + 56, value);
        }
        int getRemoveTimer() {
            int offset = bufferOffset + myOffset + 60;
            return buffer.getInt(offset);
        }
        void setRemoveTimer(int value) {
            buffer.putInt(bufferOffset + myOffset + 60, value);
        }
        boolean getExists() {
            int offset = bufferOffset + myOffset + 64;
            return buffer.getByte(offset) != 0;
        }
        void setExists(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 64, (byte) (value ? 1 : 0));
        }
        boolean isVisible(int i) {
            int offset = bufferOffset + myOffset + 65 + 1 * 1 * i;
            return buffer.getByte(offset) != 0;
        }
        void setIsVisible(int i, boolean value) {
            buffer.putByte(bufferOffset + myOffset + 65 + 1 * 1 * i, (byte) (value ? 1 : 0));
        }
    }
    class unitFinder {
        static final int SIZE = 8;
        private int myOffset;
        public unitFinder(int myOffset) {
          this.myOffset = myOffset;
        }
        int getUnitIndex() {
            int offset = bufferOffset + myOffset + 0;
            return buffer.getInt(offset);
        }
        void setUnitIndex(int value) {
            buffer.putInt(bufferOffset + myOffset + 0, value);
        }
        int getSearchValue() {
            int offset = bufferOffset + myOffset + 4;
            return buffer.getInt(offset);
        }
        void setSearchValue(int value) {
            buffer.putInt(bufferOffset + myOffset + 4, value);
        }
    }
    class UnitData {
        static final int SIZE = 336;
        private int myOffset;
        public UnitData(int myOffset) {
          this.myOffset = myOffset;
        }
        int getClearanceLevel() {
            int offset = bufferOffset + myOffset + 0;
            return buffer.getInt(offset);
        }
        void setClearanceLevel(int value) {
            buffer.putInt(bufferOffset + myOffset + 0, value);
        }
        int getId() {
            int offset = bufferOffset + myOffset + 4;
            return buffer.getInt(offset);
        }
        void setId(int value) {
            buffer.putInt(bufferOffset + myOffset + 4, value);
        }
        int getPlayer() {
            int offset = bufferOffset + myOffset + 8;
            return buffer.getInt(offset);
        }
        void setPlayer(int value) {
            buffer.putInt(bufferOffset + myOffset + 8, value);
        }
        int getType() {
            int offset = bufferOffset + myOffset + 12;
            return buffer.getInt(offset);
        }
        void setType(int value) {
            buffer.putInt(bufferOffset + myOffset + 12, value);
        }
        int getPositionX() {
            int offset = bufferOffset + myOffset + 16;
            return buffer.getInt(offset);
        }
        void setPositionX(int value) {
            buffer.putInt(bufferOffset + myOffset + 16, value);
        }
        int getPositionY() {
            int offset = bufferOffset + myOffset + 20;
            return buffer.getInt(offset);
        }
        void setPositionY(int value) {
            buffer.putInt(bufferOffset + myOffset + 20, value);
        }
        double getAngle() {
            int offset = bufferOffset + myOffset + 24;
            return buffer.getDouble(offset);
        }
        void setAngle(double value) {
            buffer.putDouble(bufferOffset + myOffset + 24, value);
        }
        double getVelocityX() {
            int offset = bufferOffset + myOffset + 32;
            return buffer.getDouble(offset);
        }
        void setVelocityX(double value) {
            buffer.putDouble(bufferOffset + myOffset + 32, value);
        }
        double getVelocityY() {
            int offset = bufferOffset + myOffset + 40;
            return buffer.getDouble(offset);
        }
        void setVelocityY(double value) {
            buffer.putDouble(bufferOffset + myOffset + 40, value);
        }
        int getHitPoints() {
            int offset = bufferOffset + myOffset + 48;
            return buffer.getInt(offset);
        }
        void setHitPoints(int value) {
            buffer.putInt(bufferOffset + myOffset + 48, value);
        }
        int getLastHitPoints() {
            int offset = bufferOffset + myOffset + 52;
            return buffer.getInt(offset);
        }
        void setLastHitPoints(int value) {
            buffer.putInt(bufferOffset + myOffset + 52, value);
        }
        int getShields() {
            int offset = bufferOffset + myOffset + 56;
            return buffer.getInt(offset);
        }
        void setShields(int value) {
            buffer.putInt(bufferOffset + myOffset + 56, value);
        }
        int getEnergy() {
            int offset = bufferOffset + myOffset + 60;
            return buffer.getInt(offset);
        }
        void setEnergy(int value) {
            buffer.putInt(bufferOffset + myOffset + 60, value);
        }
        int getResources() {
            int offset = bufferOffset + myOffset + 64;
            return buffer.getInt(offset);
        }
        void setResources(int value) {
            buffer.putInt(bufferOffset + myOffset + 64, value);
        }
        int getResourceGroup() {
            int offset = bufferOffset + myOffset + 68;
            return buffer.getInt(offset);
        }
        void setResourceGroup(int value) {
            buffer.putInt(bufferOffset + myOffset + 68, value);
        }
        int getKillCount() {
            int offset = bufferOffset + myOffset + 72;
            return buffer.getInt(offset);
        }
        void setKillCount(int value) {
            buffer.putInt(bufferOffset + myOffset + 72, value);
        }
        int getAcidSporeCount() {
            int offset = bufferOffset + myOffset + 76;
            return buffer.getInt(offset);
        }
        void setAcidSporeCount(int value) {
            buffer.putInt(bufferOffset + myOffset + 76, value);
        }
        int getScarabCount() {
            int offset = bufferOffset + myOffset + 80;
            return buffer.getInt(offset);
        }
        void setScarabCount(int value) {
            buffer.putInt(bufferOffset + myOffset + 80, value);
        }
        int getInterceptorCount() {
            int offset = bufferOffset + myOffset + 84;
            return buffer.getInt(offset);
        }
        void setInterceptorCount(int value) {
            buffer.putInt(bufferOffset + myOffset + 84, value);
        }
        int getSpiderMineCount() {
            int offset = bufferOffset + myOffset + 88;
            return buffer.getInt(offset);
        }
        void setSpiderMineCount(int value) {
            buffer.putInt(bufferOffset + myOffset + 88, value);
        }
        int getGroundWeaponCooldown() {
            int offset = bufferOffset + myOffset + 92;
            return buffer.getInt(offset);
        }
        void setGroundWeaponCooldown(int value) {
            buffer.putInt(bufferOffset + myOffset + 92, value);
        }
        int getAirWeaponCooldown() {
            int offset = bufferOffset + myOffset + 96;
            return buffer.getInt(offset);
        }
        void setAirWeaponCooldown(int value) {
            buffer.putInt(bufferOffset + myOffset + 96, value);
        }
        int getSpellCooldown() {
            int offset = bufferOffset + myOffset + 100;
            return buffer.getInt(offset);
        }
        void setSpellCooldown(int value) {
            buffer.putInt(bufferOffset + myOffset + 100, value);
        }
        int getDefenseMatrixPoints() {
            int offset = bufferOffset + myOffset + 104;
            return buffer.getInt(offset);
        }
        void setDefenseMatrixPoints(int value) {
            buffer.putInt(bufferOffset + myOffset + 104, value);
        }
        int getDefenseMatrixTimer() {
            int offset = bufferOffset + myOffset + 108;
            return buffer.getInt(offset);
        }
        void setDefenseMatrixTimer(int value) {
            buffer.putInt(bufferOffset + myOffset + 108, value);
        }
        int getEnsnareTimer() {
            int offset = bufferOffset + myOffset + 112;
            return buffer.getInt(offset);
        }
        void setEnsnareTimer(int value) {
            buffer.putInt(bufferOffset + myOffset + 112, value);
        }
        int getIrradiateTimer() {
            int offset = bufferOffset + myOffset + 116;
            return buffer.getInt(offset);
        }
        void setIrradiateTimer(int value) {
            buffer.putInt(bufferOffset + myOffset + 116, value);
        }
        int getLockdownTimer() {
            int offset = bufferOffset + myOffset + 120;
            return buffer.getInt(offset);
        }
        void setLockdownTimer(int value) {
            buffer.putInt(bufferOffset + myOffset + 120, value);
        }
        int getMaelstromTimer() {
            int offset = bufferOffset + myOffset + 124;
            return buffer.getInt(offset);
        }
        void setMaelstromTimer(int value) {
            buffer.putInt(bufferOffset + myOffset + 124, value);
        }
        int getOrderTimer() {
            int offset = bufferOffset + myOffset + 128;
            return buffer.getInt(offset);
        }
        void setOrderTimer(int value) {
            buffer.putInt(bufferOffset + myOffset + 128, value);
        }
        int getPlagueTimer() {
            int offset = bufferOffset + myOffset + 132;
            return buffer.getInt(offset);
        }
        void setPlagueTimer(int value) {
            buffer.putInt(bufferOffset + myOffset + 132, value);
        }
        int getRemoveTimer() {
            int offset = bufferOffset + myOffset + 136;
            return buffer.getInt(offset);
        }
        void setRemoveTimer(int value) {
            buffer.putInt(bufferOffset + myOffset + 136, value);
        }
        int getStasisTimer() {
            int offset = bufferOffset + myOffset + 140;
            return buffer.getInt(offset);
        }
        void setStasisTimer(int value) {
            buffer.putInt(bufferOffset + myOffset + 140, value);
        }
        int getStimTimer() {
            int offset = bufferOffset + myOffset + 144;
            return buffer.getInt(offset);
        }
        void setStimTimer(int value) {
            buffer.putInt(bufferOffset + myOffset + 144, value);
        }
        int getBuildType() {
            int offset = bufferOffset + myOffset + 148;
            return buffer.getInt(offset);
        }
        void setBuildType(int value) {
            buffer.putInt(bufferOffset + myOffset + 148, value);
        }
        int getTrainingQueueCount() {
            int offset = bufferOffset + myOffset + 152;
            return buffer.getInt(offset);
        }
        void setTrainingQueueCount(int value) {
            buffer.putInt(bufferOffset + myOffset + 152, value);
        }
        int getTrainingQueue(int i) {
            int offset = bufferOffset + myOffset + 156 + 4 * 1 * i;
            return buffer.getInt(offset);
        }
        void setTrainingQueue(int i, int value) {
            buffer.putInt(bufferOffset + myOffset + 156 + 4 * 1 * i, value);
        }
        int getTech() {
            int offset = bufferOffset + myOffset + 176;
            return buffer.getInt(offset);
        }
        void setTech(int value) {
            buffer.putInt(bufferOffset + myOffset + 176, value);
        }
        int getUpgrade() {
            int offset = bufferOffset + myOffset + 180;
            return buffer.getInt(offset);
        }
        void setUpgrade(int value) {
            buffer.putInt(bufferOffset + myOffset + 180, value);
        }
        int getRemainingBuildTime() {
            int offset = bufferOffset + myOffset + 184;
            return buffer.getInt(offset);
        }
        void setRemainingBuildTime(int value) {
            buffer.putInt(bufferOffset + myOffset + 184, value);
        }
        int getRemainingTrainTime() {
            int offset = bufferOffset + myOffset + 188;
            return buffer.getInt(offset);
        }
        void setRemainingTrainTime(int value) {
            buffer.putInt(bufferOffset + myOffset + 188, value);
        }
        int getRemainingResearchTime() {
            int offset = bufferOffset + myOffset + 192;
            return buffer.getInt(offset);
        }
        void setRemainingResearchTime(int value) {
            buffer.putInt(bufferOffset + myOffset + 192, value);
        }
        int getRemainingUpgradeTime() {
            int offset = bufferOffset + myOffset + 196;
            return buffer.getInt(offset);
        }
        void setRemainingUpgradeTime(int value) {
            buffer.putInt(bufferOffset + myOffset + 196, value);
        }
        int getBuildUnit() {
            int offset = bufferOffset + myOffset + 200;
            return buffer.getInt(offset);
        }
        void setBuildUnit(int value) {
            buffer.putInt(bufferOffset + myOffset + 200, value);
        }
        int getTarget() {
            int offset = bufferOffset + myOffset + 204;
            return buffer.getInt(offset);
        }
        void setTarget(int value) {
            buffer.putInt(bufferOffset + myOffset + 204, value);
        }
        int getTargetPositionX() {
            int offset = bufferOffset + myOffset + 208;
            return buffer.getInt(offset);
        }
        void setTargetPositionX(int value) {
            buffer.putInt(bufferOffset + myOffset + 208, value);
        }
        int getTargetPositionY() {
            int offset = bufferOffset + myOffset + 212;
            return buffer.getInt(offset);
        }
        void setTargetPositionY(int value) {
            buffer.putInt(bufferOffset + myOffset + 212, value);
        }
        int getOrder() {
            int offset = bufferOffset + myOffset + 216;
            return buffer.getInt(offset);
        }
        void setOrder(int value) {
            buffer.putInt(bufferOffset + myOffset + 216, value);
        }
        int getOrderTarget() {
            int offset = bufferOffset + myOffset + 220;
            return buffer.getInt(offset);
        }
        void setOrderTarget(int value) {
            buffer.putInt(bufferOffset + myOffset + 220, value);
        }
        int getOrderTargetPositionX() {
            int offset = bufferOffset + myOffset + 224;
            return buffer.getInt(offset);
        }
        void setOrderTargetPositionX(int value) {
            buffer.putInt(bufferOffset + myOffset + 224, value);
        }
        int getOrderTargetPositionY() {
            int offset = bufferOffset + myOffset + 228;
            return buffer.getInt(offset);
        }
        void setOrderTargetPositionY(int value) {
            buffer.putInt(bufferOffset + myOffset + 228, value);
        }
        int getSecondaryOrder() {
            int offset = bufferOffset + myOffset + 232;
            return buffer.getInt(offset);
        }
        void setSecondaryOrder(int value) {
            buffer.putInt(bufferOffset + myOffset + 232, value);
        }
        int getRallyPositionX() {
            int offset = bufferOffset + myOffset + 236;
            return buffer.getInt(offset);
        }
        void setRallyPositionX(int value) {
            buffer.putInt(bufferOffset + myOffset + 236, value);
        }
        int getRallyPositionY() {
            int offset = bufferOffset + myOffset + 240;
            return buffer.getInt(offset);
        }
        void setRallyPositionY(int value) {
            buffer.putInt(bufferOffset + myOffset + 240, value);
        }
        int getRallyUnit() {
            int offset = bufferOffset + myOffset + 244;
            return buffer.getInt(offset);
        }
        void setRallyUnit(int value) {
            buffer.putInt(bufferOffset + myOffset + 244, value);
        }
        int getAddon() {
            int offset = bufferOffset + myOffset + 248;
            return buffer.getInt(offset);
        }
        void setAddon(int value) {
            buffer.putInt(bufferOffset + myOffset + 248, value);
        }
        int getNydusExit() {
            int offset = bufferOffset + myOffset + 252;
            return buffer.getInt(offset);
        }
        void setNydusExit(int value) {
            buffer.putInt(bufferOffset + myOffset + 252, value);
        }
        int getPowerUp() {
            int offset = bufferOffset + myOffset + 256;
            return buffer.getInt(offset);
        }
        void setPowerUp(int value) {
            buffer.putInt(bufferOffset + myOffset + 256, value);
        }
        int getTransport() {
            int offset = bufferOffset + myOffset + 260;
            return buffer.getInt(offset);
        }
        void setTransport(int value) {
            buffer.putInt(bufferOffset + myOffset + 260, value);
        }
        int getCarrier() {
            int offset = bufferOffset + myOffset + 264;
            return buffer.getInt(offset);
        }
        void setCarrier(int value) {
            buffer.putInt(bufferOffset + myOffset + 264, value);
        }
        int getHatchery() {
            int offset = bufferOffset + myOffset + 268;
            return buffer.getInt(offset);
        }
        void setHatchery(int value) {
            buffer.putInt(bufferOffset + myOffset + 268, value);
        }
        boolean getExists() {
            int offset = bufferOffset + myOffset + 272;
            return buffer.getByte(offset) != 0;
        }
        void setExists(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 272, (byte) (value ? 1 : 0));
        }
        boolean getHasNuke() {
            int offset = bufferOffset + myOffset + 273;
            return buffer.getByte(offset) != 0;
        }
        void setHasNuke(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 273, (byte) (value ? 1 : 0));
        }
        boolean isAccelerating() {
            int offset = bufferOffset + myOffset + 274;
            return buffer.getByte(offset) != 0;
        }
        void setIsAccelerating(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 274, (byte) (value ? 1 : 0));
        }
        boolean isAttacking() {
            int offset = bufferOffset + myOffset + 275;
            return buffer.getByte(offset) != 0;
        }
        void setIsAttacking(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 275, (byte) (value ? 1 : 0));
        }
        boolean isAttackFrame() {
            int offset = bufferOffset + myOffset + 276;
            return buffer.getByte(offset) != 0;
        }
        void setIsAttackFrame(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 276, (byte) (value ? 1 : 0));
        }
        boolean isBeingGathered() {
            int offset = bufferOffset + myOffset + 277;
            return buffer.getByte(offset) != 0;
        }
        void setIsBeingGathered(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 277, (byte) (value ? 1 : 0));
        }
        boolean isBlind() {
            int offset = bufferOffset + myOffset + 278;
            return buffer.getByte(offset) != 0;
        }
        void setIsBlind(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 278, (byte) (value ? 1 : 0));
        }
        boolean isBraking() {
            int offset = bufferOffset + myOffset + 279;
            return buffer.getByte(offset) != 0;
        }
        void setIsBraking(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 279, (byte) (value ? 1 : 0));
        }
        boolean isBurrowed() {
            int offset = bufferOffset + myOffset + 280;
            return buffer.getByte(offset) != 0;
        }
        void setIsBurrowed(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 280, (byte) (value ? 1 : 0));
        }
        int getCarryResourceType() {
            int offset = bufferOffset + myOffset + 284;
            return buffer.getInt(offset);
        }
        void setCarryResourceType(int value) {
            buffer.putInt(bufferOffset + myOffset + 284, value);
        }
        boolean isCloaked() {
            int offset = bufferOffset + myOffset + 288;
            return buffer.getByte(offset) != 0;
        }
        void setIsCloaked(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 288, (byte) (value ? 1 : 0));
        }
        boolean isCompleted() {
            int offset = bufferOffset + myOffset + 289;
            return buffer.getByte(offset) != 0;
        }
        void setIsCompleted(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 289, (byte) (value ? 1 : 0));
        }
        boolean isConstructing() {
            int offset = bufferOffset + myOffset + 290;
            return buffer.getByte(offset) != 0;
        }
        void setIsConstructing(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 290, (byte) (value ? 1 : 0));
        }
        boolean isDetected() {
            int offset = bufferOffset + myOffset + 291;
            return buffer.getByte(offset) != 0;
        }
        void setIsDetected(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 291, (byte) (value ? 1 : 0));
        }
        boolean isGathering() {
            int offset = bufferOffset + myOffset + 292;
            return buffer.getByte(offset) != 0;
        }
        void setIsGathering(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 292, (byte) (value ? 1 : 0));
        }
        boolean isHallucination() {
            int offset = bufferOffset + myOffset + 293;
            return buffer.getByte(offset) != 0;
        }
        void setIsHallucination(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 293, (byte) (value ? 1 : 0));
        }
        boolean isIdle() {
            int offset = bufferOffset + myOffset + 294;
            return buffer.getByte(offset) != 0;
        }
        void setIsIdle(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 294, (byte) (value ? 1 : 0));
        }
        boolean isInterruptible() {
            int offset = bufferOffset + myOffset + 295;
            return buffer.getByte(offset) != 0;
        }
        void setIsInterruptible(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 295, (byte) (value ? 1 : 0));
        }
        boolean isInvincible() {
            int offset = bufferOffset + myOffset + 296;
            return buffer.getByte(offset) != 0;
        }
        void setIsInvincible(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 296, (byte) (value ? 1 : 0));
        }
        boolean isLifted() {
            int offset = bufferOffset + myOffset + 297;
            return buffer.getByte(offset) != 0;
        }
        void setIsLifted(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 297, (byte) (value ? 1 : 0));
        }
        boolean isMorphing() {
            int offset = bufferOffset + myOffset + 298;
            return buffer.getByte(offset) != 0;
        }
        void setIsMorphing(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 298, (byte) (value ? 1 : 0));
        }
        boolean isMoving() {
            int offset = bufferOffset + myOffset + 299;
            return buffer.getByte(offset) != 0;
        }
        void setIsMoving(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 299, (byte) (value ? 1 : 0));
        }
        boolean isParasited() {
            int offset = bufferOffset + myOffset + 300;
            return buffer.getByte(offset) != 0;
        }
        void setIsParasited(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 300, (byte) (value ? 1 : 0));
        }
        boolean isSelected() {
            int offset = bufferOffset + myOffset + 301;
            return buffer.getByte(offset) != 0;
        }
        void setIsSelected(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 301, (byte) (value ? 1 : 0));
        }
        boolean isStartingAttack() {
            int offset = bufferOffset + myOffset + 302;
            return buffer.getByte(offset) != 0;
        }
        void setIsStartingAttack(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 302, (byte) (value ? 1 : 0));
        }
        boolean isStuck() {
            int offset = bufferOffset + myOffset + 303;
            return buffer.getByte(offset) != 0;
        }
        void setIsStuck(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 303, (byte) (value ? 1 : 0));
        }
        boolean isTraining() {
            int offset = bufferOffset + myOffset + 304;
            return buffer.getByte(offset) != 0;
        }
        void setIsTraining(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 304, (byte) (value ? 1 : 0));
        }
        boolean isUnderStorm() {
            int offset = bufferOffset + myOffset + 305;
            return buffer.getByte(offset) != 0;
        }
        void setIsUnderStorm(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 305, (byte) (value ? 1 : 0));
        }
        boolean isUnderDarkSwarm() {
            int offset = bufferOffset + myOffset + 306;
            return buffer.getByte(offset) != 0;
        }
        void setIsUnderDarkSwarm(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 306, (byte) (value ? 1 : 0));
        }
        boolean isUnderDWeb() {
            int offset = bufferOffset + myOffset + 307;
            return buffer.getByte(offset) != 0;
        }
        void setIsUnderDWeb(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 307, (byte) (value ? 1 : 0));
        }
        boolean isPowered() {
            int offset = bufferOffset + myOffset + 308;
            return buffer.getByte(offset) != 0;
        }
        void setIsPowered(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 308, (byte) (value ? 1 : 0));
        }
        boolean isVisible(int i) {
            int offset = bufferOffset + myOffset + 309 + 1 * 1 * i;
            return buffer.getByte(offset) != 0;
        }
        void setIsVisible(int i, boolean value) {
            buffer.putByte(bufferOffset + myOffset + 309 + 1 * 1 * i, (byte) (value ? 1 : 0));
        }
        int getButtonset() {
            int offset = bufferOffset + myOffset + 320;
            return buffer.getInt(offset);
        }
        void setButtonset(int value) {
            buffer.putInt(bufferOffset + myOffset + 320, value);
        }
        int getLastAttackerPlayer() {
            int offset = bufferOffset + myOffset + 324;
            return buffer.getInt(offset);
        }
        void setLastAttackerPlayer(int value) {
            buffer.putInt(bufferOffset + myOffset + 324, value);
        }
        boolean getRecentlyAttacked() {
            int offset = bufferOffset + myOffset + 328;
            return buffer.getByte(offset) != 0;
        }
        void setRecentlyAttacked(boolean value) {
            buffer.putByte(bufferOffset + myOffset + 328, (byte) (value ? 1 : 0));
        }
        int getReplayID() {
            int offset = bufferOffset + myOffset + 332;
            return buffer.getInt(offset);
        }
        void setReplayID(int value) {
            buffer.putInt(bufferOffset + myOffset + 332, value);
        }
    }
}

