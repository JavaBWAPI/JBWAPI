package bwapi;

import com.sun.jna.Pointer;

final class ClientData {
    private WrappedBuffer buffer;
    private GameData gameData;
    ClientData() {
        gameData = new ClientData.GameData(0);
    }
    GameData gameData() {
        return gameData;
    }
    void setBuffer(WrappedBuffer buffer) {
        this.buffer = buffer;
    }
    void setPointer(Pointer pointer) {
        setBuffer(new WrappedBuffer(pointer, GameData.SIZE));
    }
    class UnitCommand {
        static final int SIZE = 24;
        private int myOffset;
        UnitCommand(int myOffset) {
          this.myOffset = myOffset;
        }
        int getTid() {
            int offset = myOffset + 0;
            return buffer.getInt(offset);
        }
        void setTid(int value) {
            buffer.putInt(myOffset + 0, value);
        }
        int getUnitIndex() {
            int offset = myOffset + 4;
            return buffer.getInt(offset);
        }
        void setUnitIndex(int value) {
            buffer.putInt(myOffset + 4, value);
        }
        int getTargetIndex() {
            int offset = myOffset + 8;
            return buffer.getInt(offset);
        }
        void setTargetIndex(int value) {
            buffer.putInt(myOffset + 8, value);
        }
        int getX() {
            int offset = myOffset + 12;
            return buffer.getInt(offset);
        }
        void setX(int value) {
            buffer.putInt(myOffset + 12, value);
        }
        int getY() {
            int offset = myOffset + 16;
            return buffer.getInt(offset);
        }
        void setY(int value) {
            buffer.putInt(myOffset + 16, value);
        }
        int getExtra() {
            int offset = myOffset + 20;
            return buffer.getInt(offset);
        }
        void setExtra(int value) {
            buffer.putInt(myOffset + 20, value);
        }
    }
    class GameData {
        static final int SIZE = 33017048;
        private int myOffset;
        GameData(int myOffset) {
          this.myOffset = myOffset;
        }
        int getClient_version() {
            int offset = myOffset + 0;
            return buffer.getInt(offset);
        }
        void setClient_version(int value) {
            buffer.putInt(myOffset + 0, value);
        }
        int getRevision() {
            int offset = myOffset + 4;
            return buffer.getInt(offset);
        }
        void setRevision(int value) {
            buffer.putInt(myOffset + 4, value);
        }
        boolean isDebug() {
            int offset = myOffset + 8;
            return buffer.getByte(offset) != 0;
        }
        void setIsDebug(boolean value) {
            buffer.putByte(myOffset + 8, (byte) (value ? 1 : 0));
        }
        int getInstanceID() {
            int offset = myOffset + 12;
            return buffer.getInt(offset);
        }
        void setInstanceID(int value) {
            buffer.putInt(myOffset + 12, value);
        }
        int getBotAPM_noselects() {
            int offset = myOffset + 16;
            return buffer.getInt(offset);
        }
        void setBotAPM_noselects(int value) {
            buffer.putInt(myOffset + 16, value);
        }
        int getBotAPM_selects() {
            int offset = myOffset + 20;
            return buffer.getInt(offset);
        }
        void setBotAPM_selects(int value) {
            buffer.putInt(myOffset + 20, value);
        }
        int getForceCount() {
            int offset = myOffset + 24;
            return buffer.getInt(offset);
        }
        void setForceCount(int value) {
            buffer.putInt(myOffset + 24, value);
        }
        ForceData getForces(int i) {
            int offset = myOffset + 28 + 32 * 1 * i;
            return new ForceData(offset);
        }
        int getPlayerCount() {
            int offset = myOffset + 188;
            return buffer.getInt(offset);
        }
        void setPlayerCount(int value) {
            buffer.putInt(myOffset + 188, value);
        }
        PlayerData getPlayers(int i) {
            int offset = myOffset + 192 + 5788 * 1 * i;
            return new PlayerData(offset);
        }
        int getInitialUnitCount() {
            int offset = myOffset + 69648;
            return buffer.getInt(offset);
        }
        void setInitialUnitCount(int value) {
            buffer.putInt(myOffset + 69648, value);
        }
        UnitData getUnits(int i) {
            int offset = myOffset + 69656 + 336 * 1 * i;
            return new UnitData(offset);
        }
        int getUnitArray(int i) {
            int offset = myOffset + 3429656 + 4 * 1 * i;
            return buffer.getInt(offset);
        }
        void setUnitArray(int i, int value) {
            buffer.putInt(myOffset + 3429656 + 4 * 1 * i, value);
        }
        BulletData getBullets(int i) {
            int offset = myOffset + 3436456 + 80 * 1 * i;
            return new BulletData(offset);
        }
        int getNukeDotCount() {
            int offset = myOffset + 3444456;
            return buffer.getInt(offset);
        }
        void setNukeDotCount(int value) {
            buffer.putInt(myOffset + 3444456, value);
        }
        Position getNukeDots(int i) {
            int offset = myOffset + 3444460 + 8 * 1 * i;
            return new Position(offset);
        }
        int getGameType() {
            int offset = myOffset + 3446060;
            return buffer.getInt(offset);
        }
        void setGameType(int value) {
            buffer.putInt(myOffset + 3446060, value);
        }
        int getLatency() {
            int offset = myOffset + 3446064;
            return buffer.getInt(offset);
        }
        void setLatency(int value) {
            buffer.putInt(myOffset + 3446064, value);
        }
        int getLatencyFrames() {
            int offset = myOffset + 3446068;
            return buffer.getInt(offset);
        }
        void setLatencyFrames(int value) {
            buffer.putInt(myOffset + 3446068, value);
        }
        int getLatencyTime() {
            int offset = myOffset + 3446072;
            return buffer.getInt(offset);
        }
        void setLatencyTime(int value) {
            buffer.putInt(myOffset + 3446072, value);
        }
        int getRemainingLatencyFrames() {
            int offset = myOffset + 3446076;
            return buffer.getInt(offset);
        }
        void setRemainingLatencyFrames(int value) {
            buffer.putInt(myOffset + 3446076, value);
        }
        int getRemainingLatencyTime() {
            int offset = myOffset + 3446080;
            return buffer.getInt(offset);
        }
        void setRemainingLatencyTime(int value) {
            buffer.putInt(myOffset + 3446080, value);
        }
        boolean getHasLatCom() {
            int offset = myOffset + 3446084;
            return buffer.getByte(offset) != 0;
        }
        void setHasLatCom(boolean value) {
            buffer.putByte(myOffset + 3446084, (byte) (value ? 1 : 0));
        }
        boolean getHasGUI() {
            int offset = myOffset + 3446085;
            return buffer.getByte(offset) != 0;
        }
        void setHasGUI(boolean value) {
            buffer.putByte(myOffset + 3446085, (byte) (value ? 1 : 0));
        }
        int getReplayFrameCount() {
            int offset = myOffset + 3446088;
            return buffer.getInt(offset);
        }
        void setReplayFrameCount(int value) {
            buffer.putInt(myOffset + 3446088, value);
        }
        int getRandomSeed() {
            int offset = myOffset + 3446092;
            return buffer.getInt(offset);
        }
        void setRandomSeed(int value) {
            buffer.putInt(myOffset + 3446092, value);
        }
        int getFrameCount() {
            int offset = myOffset + 3446096;
            return buffer.getInt(offset);
        }
        void setFrameCount(int value) {
            buffer.putInt(myOffset + 3446096, value);
        }
        int getElapsedTime() {
            int offset = myOffset + 3446100;
            return buffer.getInt(offset);
        }
        void setElapsedTime(int value) {
            buffer.putInt(myOffset + 3446100, value);
        }
        int getCountdownTimer() {
            int offset = myOffset + 3446104;
            return buffer.getInt(offset);
        }
        void setCountdownTimer(int value) {
            buffer.putInt(myOffset + 3446104, value);
        }
        int getFps() {
            int offset = myOffset + 3446108;
            return buffer.getInt(offset);
        }
        void setFps(int value) {
            buffer.putInt(myOffset + 3446108, value);
        }
        double getAverageFPS() {
            int offset = myOffset + 3446112;
            return buffer.getDouble(offset);
        }
        void setAverageFPS(double value) {
            buffer.putDouble(myOffset + 3446112, value);
        }
        int getMouseX() {
            int offset = myOffset + 3446120;
            return buffer.getInt(offset);
        }
        void setMouseX(int value) {
            buffer.putInt(myOffset + 3446120, value);
        }
        int getMouseY() {
            int offset = myOffset + 3446124;
            return buffer.getInt(offset);
        }
        void setMouseY(int value) {
            buffer.putInt(myOffset + 3446124, value);
        }
        boolean getMouseState(int i) {
            int offset = myOffset + 3446128 + 1 * 1 * i;
            return buffer.getByte(offset) != 0;
        }
        void setMouseState(int i, boolean value) {
            buffer.putByte(myOffset + 3446128 + 1 * 1 * i, (byte) (value ? 1 : 0));
        }
        boolean getKeyState(int i) {
            int offset = myOffset + 3446131 + 1 * 1 * i;
            return buffer.getByte(offset) != 0;
        }
        void setKeyState(int i, boolean value) {
            buffer.putByte(myOffset + 3446131 + 1 * 1 * i, (byte) (value ? 1 : 0));
        }
        int getScreenX() {
            int offset = myOffset + 3446388;
            return buffer.getInt(offset);
        }
        void setScreenX(int value) {
            buffer.putInt(myOffset + 3446388, value);
        }
        int getScreenY() {
            int offset = myOffset + 3446392;
            return buffer.getInt(offset);
        }
        void setScreenY(int value) {
            buffer.putInt(myOffset + 3446392, value);
        }
        boolean getFlags(int i) {
            int offset = myOffset + 3446396 + 1 * 1 * i;
            return buffer.getByte(offset) != 0;
        }
        void setFlags(int i, boolean value) {
            buffer.putByte(myOffset + 3446396 + 1 * 1 * i, (byte) (value ? 1 : 0));
        }
        int getMapWidth() {
            int offset = myOffset + 3446400;
            return buffer.getInt(offset);
        }
        void setMapWidth(int value) {
            buffer.putInt(myOffset + 3446400, value);
        }
        int getMapHeight() {
            int offset = myOffset + 3446404;
            return buffer.getInt(offset);
        }
        void setMapHeight(int value) {
            buffer.putInt(myOffset + 3446404, value);
        }
        String getMapFileName() {
            int offset = myOffset + 3446408;
            return buffer.getString(offset, 261);
        }
        void setMapFileName(String value) {
            buffer.putString(myOffset + 3446408, 261, value);
        }
        String getMapPathName() {
            int offset = myOffset + 3446669;
            return buffer.getString(offset, 261);
        }
        void setMapPathName(String value) {
            buffer.putString(myOffset + 3446669, 261, value);
        }
        String getMapName() {
            int offset = myOffset + 3446930;
            return buffer.getString(offset, 33);
        }
        void setMapName(String value) {
            buffer.putString(myOffset + 3446930, 33, value);
        }
        String getMapHash() {
            int offset = myOffset + 3446963;
            return buffer.getString(offset, 41);
        }
        void setMapHash(String value) {
            buffer.putString(myOffset + 3446963, 41, value);
        }
        int getGroundHeight(int i, int j) {
            int offset = myOffset + 3447004 + 4 * 1 * j + 4 * 256 * i;
            return buffer.getInt(offset);
        }
        void setGetGroundHeight(int i, int j, int value) {
            buffer.putInt(myOffset + 3447004 + 4 * 1 * j + 4 * 256 * i, value);
        }
        boolean isWalkable(int i, int j) {
            int offset = myOffset + 3709148 + 1 * 1 * j + 1 * 1024 * i;
            return buffer.getByte(offset) != 0;
        }
        void setIsWalkable(int i, int j, boolean value) {
            buffer.putByte(myOffset + 3709148 + 1 * 1 * j + 1 * 1024 * i, (byte) (value ? 1 : 0));
        }
        boolean isBuildable(int i, int j) {
            int offset = myOffset + 4757724 + 1 * 1 * j + 1 * 256 * i;
            return buffer.getByte(offset) != 0;
        }
        void setIsBuildable(int i, int j, boolean value) {
            buffer.putByte(myOffset + 4757724 + 1 * 1 * j + 1 * 256 * i, (byte) (value ? 1 : 0));
        }
        boolean isVisible(int i, int j) {
            int offset = myOffset + 4823260 + 1 * 1 * j + 1 * 256 * i;
            return buffer.getByte(offset) != 0;
        }
        void setIsVisible(int i, int j, boolean value) {
            buffer.putByte(myOffset + 4823260 + 1 * 1 * j + 1 * 256 * i, (byte) (value ? 1 : 0));
        }
        boolean isExplored(int i, int j) {
            int offset = myOffset + 4888796 + 1 * 1 * j + 1 * 256 * i;
            return buffer.getByte(offset) != 0;
        }
        void setIsExplored(int i, int j, boolean value) {
            buffer.putByte(myOffset + 4888796 + 1 * 1 * j + 1 * 256 * i, (byte) (value ? 1 : 0));
        }
        boolean getHasCreep(int i, int j) {
            int offset = myOffset + 4954332 + 1 * 1 * j + 1 * 256 * i;
            return buffer.getByte(offset) != 0;
        }
        void setHasCreep(int i, int j, boolean value) {
            buffer.putByte(myOffset + 4954332 + 1 * 1 * j + 1 * 256 * i, (byte) (value ? 1 : 0));
        }
        boolean isOccupied(int i, int j) {
            int offset = myOffset + 5019868 + 1 * 1 * j + 1 * 256 * i;
            return buffer.getByte(offset) != 0;
        }
        void setIsOccupied(int i, int j, boolean value) {
            buffer.putByte(myOffset + 5019868 + 1 * 1 * j + 1 * 256 * i, (byte) (value ? 1 : 0));
        }
        short getMapTileRegionId(int i, int j) {
            int offset = myOffset + 5085404 + 2 * 1 * j + 2 * 256 * i;
            return buffer.getShort(offset);
        }
        void setMapTileRegionId(int i, int j, short value) {
            buffer.putShort(myOffset + 5085404 + 2 * 1 * j + 2 * 256 * i, value);
        }
        short getMapSplitTilesMiniTileMask(int i) {
            int offset = myOffset + 5216476 + 2 * 1 * i;
            return buffer.getShort(offset);
        }
        void setMapSplitTilesMiniTileMask(int i, short value) {
            buffer.putShort(myOffset + 5216476 + 2 * 1 * i, value);
        }
        short getMapSplitTilesRegion1(int i) {
            int offset = myOffset + 5226476 + 2 * 1 * i;
            return buffer.getShort(offset);
        }
        void setMapSplitTilesRegion1(int i, short value) {
            buffer.putShort(myOffset + 5226476 + 2 * 1 * i, value);
        }
        short getMapSplitTilesRegion2(int i) {
            int offset = myOffset + 5236476 + 2 * 1 * i;
            return buffer.getShort(offset);
        }
        void setMapSplitTilesRegion2(int i, short value) {
            buffer.putShort(myOffset + 5236476 + 2 * 1 * i, value);
        }
        int getRegionCount() {
            int offset = myOffset + 5246476;
            return buffer.getInt(offset);
        }
        void setRegionCount(int value) {
            buffer.putInt(myOffset + 5246476, value);
        }
        RegionData getRegions(int i) {
            int offset = myOffset + 5246480 + 1068 * 1 * i;
            return new RegionData(offset);
        }
        int getStartLocationCount() {
            int offset = myOffset + 10586480;
            return buffer.getInt(offset);
        }
        void setStartLocationCount(int value) {
            buffer.putInt(myOffset + 10586480, value);
        }
        Position getStartLocations(int i) {
            int offset = myOffset + 10586484 + 8 * 1 * i;
            return new Position(offset);
        }
        boolean isInGame() {
            int offset = myOffset + 10586548;
            return buffer.getByte(offset) != 0;
        }
        void setIsInGame(boolean value) {
            buffer.putByte(myOffset + 10586548, (byte) (value ? 1 : 0));
        }
        boolean isMultiplayer() {
            int offset = myOffset + 10586549;
            return buffer.getByte(offset) != 0;
        }
        void setIsMultiplayer(boolean value) {
            buffer.putByte(myOffset + 10586549, (byte) (value ? 1 : 0));
        }
        boolean isBattleNet() {
            int offset = myOffset + 10586550;
            return buffer.getByte(offset) != 0;
        }
        void setIsBattleNet(boolean value) {
            buffer.putByte(myOffset + 10586550, (byte) (value ? 1 : 0));
        }
        boolean isPaused() {
            int offset = myOffset + 10586551;
            return buffer.getByte(offset) != 0;
        }
        void setIsPaused(boolean value) {
            buffer.putByte(myOffset + 10586551, (byte) (value ? 1 : 0));
        }
        boolean isReplay() {
            int offset = myOffset + 10586552;
            return buffer.getByte(offset) != 0;
        }
        void setIsReplay(boolean value) {
            buffer.putByte(myOffset + 10586552, (byte) (value ? 1 : 0));
        }
        int getSelectedUnitCount() {
            int offset = myOffset + 10586556;
            return buffer.getInt(offset);
        }
        void setSelectedUnitCount(int value) {
            buffer.putInt(myOffset + 10586556, value);
        }
        int getSelectedUnits(int i) {
            int offset = myOffset + 10586560 + 4 * 1 * i;
            return buffer.getInt(offset);
        }
        void setSelectedUnits(int i, int value) {
            buffer.putInt(myOffset + 10586560 + 4 * 1 * i, value);
        }
        int getSelf() {
            int offset = myOffset + 10586608;
            return buffer.getInt(offset);
        }
        void setSelf(int value) {
            buffer.putInt(myOffset + 10586608, value);
        }
        int getEnemy() {
            int offset = myOffset + 10586612;
            return buffer.getInt(offset);
        }
        void setEnemy(int value) {
            buffer.putInt(myOffset + 10586612, value);
        }
        int getNeutral() {
            int offset = myOffset + 10586616;
            return buffer.getInt(offset);
        }
        void setNeutral(int value) {
            buffer.putInt(myOffset + 10586616, value);
        }
        int getEventCount() {
            int offset = myOffset + 10586620;
            return buffer.getInt(offset);
        }
        void setEventCount(int value) {
            buffer.putInt(myOffset + 10586620, value);
        }
        Event getEvents(int i) {
            int offset = myOffset + 10586624 + 12 * 1 * i;
            return new Event(offset);
        }
        int getEventStringCount() {
            int offset = myOffset + 10706624;
            return buffer.getInt(offset);
        }
        void setEventStringCount(int value) {
            buffer.putInt(myOffset + 10706624, value);
        }
        String getEventStrings(int i) {
            int offset = myOffset + 10706628 + 1 * 256 * i;
            return buffer.getString(offset, 256);
        }
        void setEventStrings(int i, String value) {
            buffer.putString(myOffset + 10706628 + 1 * 256 * i, 256, value);
        }
        int getStringCount() {
            int offset = myOffset + 10962628;
            return buffer.getInt(offset);
        }
        void setStringCount(int value) {
            buffer.putInt(myOffset + 10962628, value);
        }
        String getStrings(int i) {
            int offset = myOffset + 10962632 + 1 * 1024 * i;
            return buffer.getString(offset, 1024);
        }
        void setStrings(int i, String value) {
            buffer.putString(myOffset + 10962632 + 1 * 1024 * i, 1024, value);
        }
        int getShapeCount() {
            int offset = myOffset + 31442632;
            return buffer.getInt(offset);
        }
        void setShapeCount(int value) {
            buffer.putInt(myOffset + 31442632, value);
        }
        Shape getShapes(int i) {
            int offset = myOffset + 31442636 + 40 * 1 * i;
            return new Shape(offset);
        }
        int getCommandCount() {
            int offset = myOffset + 32242636;
            return buffer.getInt(offset);
        }
        void setCommandCount(int value) {
            buffer.putInt(myOffset + 32242636, value);
        }
        Command getCommands(int i) {
            int offset = myOffset + 32242640 + 12 * 1 * i;
            return new Command(offset);
        }
        int getUnitCommandCount() {
            int offset = myOffset + 32482640;
            return buffer.getInt(offset);
        }
        void setUnitCommandCount(int value) {
            buffer.putInt(myOffset + 32482640, value);
        }
        UnitCommand getUnitCommands(int i) {
            int offset = myOffset + 32482644 + 24 * 1 * i;
            return new UnitCommand(offset);
        }
        int getUnitSearchSize() {
            int offset = myOffset + 32962644;
            return buffer.getInt(offset);
        }
        void setUnitSearchSize(int value) {
            buffer.putInt(myOffset + 32962644, value);
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
        Shape(int myOffset) {
          this.myOffset = myOffset;
        }
        ShapeType getType() {
            int offset = myOffset + 0;
            return ShapeType.idToEnum[buffer.getInt(offset)];
        }
        void setType(ShapeType value) {
            buffer.putInt(myOffset + 0, value.id);
        }
        CoordinateType getCtype() {
            int offset = myOffset + 4;
            return CoordinateType.idToEnum[buffer.getInt(offset)];
        }
        void setCtype(CoordinateType value) {
            buffer.putInt(myOffset + 4, value.id);
        }
        int getX1() {
            int offset = myOffset + 8;
            return buffer.getInt(offset);
        }
        void setX1(int value) {
            buffer.putInt(myOffset + 8, value);
        }
        int getY1() {
            int offset = myOffset + 12;
            return buffer.getInt(offset);
        }
        void setY1(int value) {
            buffer.putInt(myOffset + 12, value);
        }
        int getX2() {
            int offset = myOffset + 16;
            return buffer.getInt(offset);
        }
        void setX2(int value) {
            buffer.putInt(myOffset + 16, value);
        }
        int getY2() {
            int offset = myOffset + 20;
            return buffer.getInt(offset);
        }
        void setY2(int value) {
            buffer.putInt(myOffset + 20, value);
        }
        int getExtra1() {
            int offset = myOffset + 24;
            return buffer.getInt(offset);
        }
        void setExtra1(int value) {
            buffer.putInt(myOffset + 24, value);
        }
        int getExtra2() {
            int offset = myOffset + 28;
            return buffer.getInt(offset);
        }
        void setExtra2(int value) {
            buffer.putInt(myOffset + 28, value);
        }
        int getColor() {
            int offset = myOffset + 32;
            return buffer.getInt(offset);
        }
        void setColor(int value) {
            buffer.putInt(myOffset + 32, value);
        }
        boolean isSolid() {
            int offset = myOffset + 36;
            return buffer.getByte(offset) != 0;
        }
        void setIsSolid(boolean value) {
            buffer.putByte(myOffset + 36, (byte) (value ? 1 : 0));
        }
    }
    class Command {
        static final int SIZE = 12;
        private int myOffset;
        Command(int myOffset) {
          this.myOffset = myOffset;
        }
        CommandType getType() {
            int offset = myOffset + 0;
            return CommandType.idToEnum[buffer.getInt(offset)];
        }
        void setType(CommandType value) {
            buffer.putInt(myOffset + 0, value.id);
        }
        int getValue1() {
            int offset = myOffset + 4;
            return buffer.getInt(offset);
        }
        void setValue1(int value) {
            buffer.putInt(myOffset + 4, value);
        }
        int getValue2() {
            int offset = myOffset + 8;
            return buffer.getInt(offset);
        }
        void setValue2(int value) {
            buffer.putInt(myOffset + 8, value);
        }
    }
    class Position {
        static final int SIZE = 8;
        private int myOffset;
        Position(int myOffset) {
          this.myOffset = myOffset;
        }
        int getX() {
            int offset = myOffset + 0;
            return buffer.getInt(offset);
        }
        void setX(int value) {
            buffer.putInt(myOffset + 0, value);
        }
        int getY() {
            int offset = myOffset + 4;
            return buffer.getInt(offset);
        }
        void setY(int value) {
            buffer.putInt(myOffset + 4, value);
        }
    }
    class Event {
        static final int SIZE = 12;
        private int myOffset;
        Event(int myOffset) {
          this.myOffset = myOffset;
        }
        EventType getType() {
            int offset = myOffset + 0;
            return EventType.idToEnum[buffer.getInt(offset)];
        }
        void setType(EventType value) {
            buffer.putInt(myOffset + 0, value.id);
        }
        int getV1() {
            int offset = myOffset + 4;
            return buffer.getInt(offset);
        }
        void setV1(int value) {
            buffer.putInt(myOffset + 4, value);
        }
        int getV2() {
            int offset = myOffset + 8;
            return buffer.getInt(offset);
        }
        void setV2(int value) {
            buffer.putInt(myOffset + 8, value);
        }
    }
    class RegionData {
        static final int SIZE = 1068;
        private int myOffset;
        RegionData(int myOffset) {
          this.myOffset = myOffset;
        }
        int getId() {
            int offset = myOffset + 0;
            return buffer.getInt(offset);
        }
        void setId(int value) {
            buffer.putInt(myOffset + 0, value);
        }
        int islandID() {
            int offset = myOffset + 4;
            return buffer.getInt(offset);
        }
        void setIslandID(int value) {
            buffer.putInt(myOffset + 4, value);
        }
        int getCenter_x() {
            int offset = myOffset + 8;
            return buffer.getInt(offset);
        }
        void setCenter_x(int value) {
            buffer.putInt(myOffset + 8, value);
        }
        int getCenter_y() {
            int offset = myOffset + 12;
            return buffer.getInt(offset);
        }
        void setCenter_y(int value) {
            buffer.putInt(myOffset + 12, value);
        }
        int getPriority() {
            int offset = myOffset + 16;
            return buffer.getInt(offset);
        }
        void setPriority(int value) {
            buffer.putInt(myOffset + 16, value);
        }
        int getLeftMost() {
            int offset = myOffset + 20;
            return buffer.getInt(offset);
        }
        void setLeftMost(int value) {
            buffer.putInt(myOffset + 20, value);
        }
        int getRightMost() {
            int offset = myOffset + 24;
            return buffer.getInt(offset);
        }
        void setRightMost(int value) {
            buffer.putInt(myOffset + 24, value);
        }
        int getTopMost() {
            int offset = myOffset + 28;
            return buffer.getInt(offset);
        }
        void setTopMost(int value) {
            buffer.putInt(myOffset + 28, value);
        }
        int getBottomMost() {
            int offset = myOffset + 32;
            return buffer.getInt(offset);
        }
        void setBottomMost(int value) {
            buffer.putInt(myOffset + 32, value);
        }
        int getNeighborCount() {
            int offset = myOffset + 36;
            return buffer.getInt(offset);
        }
        void setNeighborCount(int value) {
            buffer.putInt(myOffset + 36, value);
        }
        int getNeighbors(int i) {
            int offset = myOffset + 40 + 4 * 1 * i;
            return buffer.getInt(offset);
        }
        void setNeighbors(int i, int value) {
            buffer.putInt(myOffset + 40 + 4 * 1 * i, value);
        }
        boolean isAccessible() {
            int offset = myOffset + 1064;
            return buffer.getByte(offset) != 0;
        }
        void setIsAccessible(boolean value) {
            buffer.putByte(myOffset + 1064, (byte) (value ? 1 : 0));
        }
        boolean isHigherGround() {
            int offset = myOffset + 1065;
            return buffer.getByte(offset) != 0;
        }
        void setIsHigherGround(boolean value) {
            buffer.putByte(myOffset + 1065, (byte) (value ? 1 : 0));
        }
    }
    class ForceData {
        static final int SIZE = 32;
        private int myOffset;
        ForceData(int myOffset) {
          this.myOffset = myOffset;
        }
        String getName() {
            int offset = myOffset + 0;
            return buffer.getString(offset, 32);
        }
        void setName(String value) {
            buffer.putString(myOffset + 0, 32, value);
        }
    }
    class PlayerData {
        static final int SIZE = 5788;
        private int myOffset;
        PlayerData(int myOffset) {
          this.myOffset = myOffset;
        }
        String getName() {
            int offset = myOffset + 0;
            return buffer.getString(offset, 25);
        }
        void setName(String value) {
            buffer.putString(myOffset + 0, 25, value);
        }
        int getRace() {
            int offset = myOffset + 28;
            return buffer.getInt(offset);
        }
        void setRace(int value) {
            buffer.putInt(myOffset + 28, value);
        }
        int getType() {
            int offset = myOffset + 32;
            return buffer.getInt(offset);
        }
        void setType(int value) {
            buffer.putInt(myOffset + 32, value);
        }
        int getForce() {
            int offset = myOffset + 36;
            return buffer.getInt(offset);
        }
        void setForce(int value) {
            buffer.putInt(myOffset + 36, value);
        }
        boolean isAlly(int i) {
            int offset = myOffset + 40 + 1 * 1 * i;
            return buffer.getByte(offset) != 0;
        }
        void setIsAlly(int i, boolean value) {
            buffer.putByte(myOffset + 40 + 1 * 1 * i, (byte) (value ? 1 : 0));
        }
        boolean isEnemy(int i) {
            int offset = myOffset + 52 + 1 * 1 * i;
            return buffer.getByte(offset) != 0;
        }
        void setIsEnemy(int i, boolean value) {
            buffer.putByte(myOffset + 52 + 1 * 1 * i, (byte) (value ? 1 : 0));
        }
        boolean isNeutral() {
            int offset = myOffset + 64;
            return buffer.getByte(offset) != 0;
        }
        void setIsNeutral(boolean value) {
            buffer.putByte(myOffset + 64, (byte) (value ? 1 : 0));
        }
        int getStartLocationX() {
            int offset = myOffset + 68;
            return buffer.getInt(offset);
        }
        void setStartLocationX(int value) {
            buffer.putInt(myOffset + 68, value);
        }
        int getStartLocationY() {
            int offset = myOffset + 72;
            return buffer.getInt(offset);
        }
        void setStartLocationY(int value) {
            buffer.putInt(myOffset + 72, value);
        }
        boolean isVictorious() {
            int offset = myOffset + 76;
            return buffer.getByte(offset) != 0;
        }
        void setIsVictorious(boolean value) {
            buffer.putByte(myOffset + 76, (byte) (value ? 1 : 0));
        }
        boolean isDefeated() {
            int offset = myOffset + 77;
            return buffer.getByte(offset) != 0;
        }
        void setIsDefeated(boolean value) {
            buffer.putByte(myOffset + 77, (byte) (value ? 1 : 0));
        }
        boolean getLeftGame() {
            int offset = myOffset + 78;
            return buffer.getByte(offset) != 0;
        }
        void setLeftGame(boolean value) {
            buffer.putByte(myOffset + 78, (byte) (value ? 1 : 0));
        }
        boolean isParticipating() {
            int offset = myOffset + 79;
            return buffer.getByte(offset) != 0;
        }
        void setIsParticipating(boolean value) {
            buffer.putByte(myOffset + 79, (byte) (value ? 1 : 0));
        }
        int getMinerals() {
            int offset = myOffset + 80;
            return buffer.getInt(offset);
        }
        void setMinerals(int value) {
            buffer.putInt(myOffset + 80, value);
        }
        int getGas() {
            int offset = myOffset + 84;
            return buffer.getInt(offset);
        }
        void setGas(int value) {
            buffer.putInt(myOffset + 84, value);
        }
        int getGatheredMinerals() {
            int offset = myOffset + 88;
            return buffer.getInt(offset);
        }
        void setGatheredMinerals(int value) {
            buffer.putInt(myOffset + 88, value);
        }
        int getGatheredGas() {
            int offset = myOffset + 92;
            return buffer.getInt(offset);
        }
        void setGatheredGas(int value) {
            buffer.putInt(myOffset + 92, value);
        }
        int getRepairedMinerals() {
            int offset = myOffset + 96;
            return buffer.getInt(offset);
        }
        void setRepairedMinerals(int value) {
            buffer.putInt(myOffset + 96, value);
        }
        int getRepairedGas() {
            int offset = myOffset + 100;
            return buffer.getInt(offset);
        }
        void setRepairedGas(int value) {
            buffer.putInt(myOffset + 100, value);
        }
        int getRefundedMinerals() {
            int offset = myOffset + 104;
            return buffer.getInt(offset);
        }
        void setRefundedMinerals(int value) {
            buffer.putInt(myOffset + 104, value);
        }
        int getRefundedGas() {
            int offset = myOffset + 108;
            return buffer.getInt(offset);
        }
        void setRefundedGas(int value) {
            buffer.putInt(myOffset + 108, value);
        }
        int getSupplyTotal(int i) {
            int offset = myOffset + 112 + 4 * 1 * i;
            return buffer.getInt(offset);
        }
        void setSupplyTotal(int i, int value) {
            buffer.putInt(myOffset + 112 + 4 * 1 * i, value);
        }
        int getSupplyUsed(int i) {
            int offset = myOffset + 124 + 4 * 1 * i;
            return buffer.getInt(offset);
        }
        void setSupplyUsed(int i, int value) {
            buffer.putInt(myOffset + 124 + 4 * 1 * i, value);
        }
        int getAllUnitCount(int i) {
            int offset = myOffset + 136 + 4 * 1 * i;
            return buffer.getInt(offset);
        }
        void setAllUnitCount(int i, int value) {
            buffer.putInt(myOffset + 136 + 4 * 1 * i, value);
        }
        int getVisibleUnitCount(int i) {
            int offset = myOffset + 1072 + 4 * 1 * i;
            return buffer.getInt(offset);
        }
        void setVisibleUnitCount(int i, int value) {
            buffer.putInt(myOffset + 1072 + 4 * 1 * i, value);
        }
        int getCompletedUnitCount(int i) {
            int offset = myOffset + 2008 + 4 * 1 * i;
            return buffer.getInt(offset);
        }
        void setCompletedUnitCount(int i, int value) {
            buffer.putInt(myOffset + 2008 + 4 * 1 * i, value);
        }
        int getDeadUnitCount(int i) {
            int offset = myOffset + 2944 + 4 * 1 * i;
            return buffer.getInt(offset);
        }
        void setDeadUnitCount(int i, int value) {
            buffer.putInt(myOffset + 2944 + 4 * 1 * i, value);
        }
        int getKilledUnitCount(int i) {
            int offset = myOffset + 3880 + 4 * 1 * i;
            return buffer.getInt(offset);
        }
        void setKilledUnitCount(int i, int value) {
            buffer.putInt(myOffset + 3880 + 4 * 1 * i, value);
        }
        int getUpgradeLevel(int i) {
            int offset = myOffset + 4816 + 4 * 1 * i;
            return buffer.getInt(offset);
        }
        void setUpgradeLevel(int i, int value) {
            buffer.putInt(myOffset + 4816 + 4 * 1 * i, value);
        }
        boolean getHasResearched(int i) {
            int offset = myOffset + 5068 + 1 * 1 * i;
            return buffer.getByte(offset) != 0;
        }
        void setHasResearched(int i, boolean value) {
            buffer.putByte(myOffset + 5068 + 1 * 1 * i, (byte) (value ? 1 : 0));
        }
        boolean isResearching(int i) {
            int offset = myOffset + 5115 + 1 * 1 * i;
            return buffer.getByte(offset) != 0;
        }
        void setIsResearching(int i, boolean value) {
            buffer.putByte(myOffset + 5115 + 1 * 1 * i, (byte) (value ? 1 : 0));
        }
        boolean isUpgrading(int i) {
            int offset = myOffset + 5162 + 1 * 1 * i;
            return buffer.getByte(offset) != 0;
        }
        void setIsUpgrading(int i, boolean value) {
            buffer.putByte(myOffset + 5162 + 1 * 1 * i, (byte) (value ? 1 : 0));
        }
        int getColor() {
            int offset = myOffset + 5228;
            return buffer.getInt(offset);
        }
        void setColor(int value) {
            buffer.putInt(myOffset + 5228, value);
        }
        int getTotalUnitScore() {
            int offset = myOffset + 5232;
            return buffer.getInt(offset);
        }
        void setTotalUnitScore(int value) {
            buffer.putInt(myOffset + 5232, value);
        }
        int getTotalKillScore() {
            int offset = myOffset + 5236;
            return buffer.getInt(offset);
        }
        void setTotalKillScore(int value) {
            buffer.putInt(myOffset + 5236, value);
        }
        int getTotalBuildingScore() {
            int offset = myOffset + 5240;
            return buffer.getInt(offset);
        }
        void setTotalBuildingScore(int value) {
            buffer.putInt(myOffset + 5240, value);
        }
        int getTotalRazingScore() {
            int offset = myOffset + 5244;
            return buffer.getInt(offset);
        }
        void setTotalRazingScore(int value) {
            buffer.putInt(myOffset + 5244, value);
        }
        int getCustomScore() {
            int offset = myOffset + 5248;
            return buffer.getInt(offset);
        }
        void setCustomScore(int value) {
            buffer.putInt(myOffset + 5248, value);
        }
        int getMaxUpgradeLevel(int i) {
            int offset = myOffset + 5252 + 4 * 1 * i;
            return buffer.getInt(offset);
        }
        void setMaxUpgradeLevel(int i, int value) {
            buffer.putInt(myOffset + 5252 + 4 * 1 * i, value);
        }
        boolean isResearchAvailable(int i) {
            int offset = myOffset + 5504 + 1 * 1 * i;
            return buffer.getByte(offset) != 0;
        }
        void setIsResearchAvailable(int i, boolean value) {
            buffer.putByte(myOffset + 5504 + 1 * 1 * i, (byte) (value ? 1 : 0));
        }
        boolean isUnitAvailable(int i) {
            int offset = myOffset + 5551 + 1 * 1 * i;
            return buffer.getByte(offset) != 0;
        }
        void setIsUnitAvailable(int i, boolean value) {
            buffer.putByte(myOffset + 5551 + 1 * 1 * i, (byte) (value ? 1 : 0));
        }
    }
    class BulletData {
        static final int SIZE = 80;
        private int myOffset;
        BulletData(int myOffset) {
          this.myOffset = myOffset;
        }
        int getId() {
            int offset = myOffset + 0;
            return buffer.getInt(offset);
        }
        void setId(int value) {
            buffer.putInt(myOffset + 0, value);
        }
        int getPlayer() {
            int offset = myOffset + 4;
            return buffer.getInt(offset);
        }
        void setPlayer(int value) {
            buffer.putInt(myOffset + 4, value);
        }
        int getType() {
            int offset = myOffset + 8;
            return buffer.getInt(offset);
        }
        void setType(int value) {
            buffer.putInt(myOffset + 8, value);
        }
        int getSource() {
            int offset = myOffset + 12;
            return buffer.getInt(offset);
        }
        void setSource(int value) {
            buffer.putInt(myOffset + 12, value);
        }
        int getPositionX() {
            int offset = myOffset + 16;
            return buffer.getInt(offset);
        }
        void setPositionX(int value) {
            buffer.putInt(myOffset + 16, value);
        }
        int getPositionY() {
            int offset = myOffset + 20;
            return buffer.getInt(offset);
        }
        void setPositionY(int value) {
            buffer.putInt(myOffset + 20, value);
        }
        double getAngle() {
            int offset = myOffset + 24;
            return buffer.getDouble(offset);
        }
        void setAngle(double value) {
            buffer.putDouble(myOffset + 24, value);
        }
        double getVelocityX() {
            int offset = myOffset + 32;
            return buffer.getDouble(offset);
        }
        void setVelocityX(double value) {
            buffer.putDouble(myOffset + 32, value);
        }
        double getVelocityY() {
            int offset = myOffset + 40;
            return buffer.getDouble(offset);
        }
        void setVelocityY(double value) {
            buffer.putDouble(myOffset + 40, value);
        }
        int getTarget() {
            int offset = myOffset + 48;
            return buffer.getInt(offset);
        }
        void setTarget(int value) {
            buffer.putInt(myOffset + 48, value);
        }
        int getTargetPositionX() {
            int offset = myOffset + 52;
            return buffer.getInt(offset);
        }
        void setTargetPositionX(int value) {
            buffer.putInt(myOffset + 52, value);
        }
        int getTargetPositionY() {
            int offset = myOffset + 56;
            return buffer.getInt(offset);
        }
        void setTargetPositionY(int value) {
            buffer.putInt(myOffset + 56, value);
        }
        int getRemoveTimer() {
            int offset = myOffset + 60;
            return buffer.getInt(offset);
        }
        void setRemoveTimer(int value) {
            buffer.putInt(myOffset + 60, value);
        }
        boolean getExists() {
            int offset = myOffset + 64;
            return buffer.getByte(offset) != 0;
        }
        void setExists(boolean value) {
            buffer.putByte(myOffset + 64, (byte) (value ? 1 : 0));
        }
        boolean isVisible(int i) {
            int offset = myOffset + 65 + 1 * 1 * i;
            return buffer.getByte(offset) != 0;
        }
        void setIsVisible(int i, boolean value) {
            buffer.putByte(myOffset + 65 + 1 * 1 * i, (byte) (value ? 1 : 0));
        }
    }
    class unitFinder {
        static final int SIZE = 8;
        private int myOffset;
        unitFinder(int myOffset) {
          this.myOffset = myOffset;
        }
        int getUnitIndex() {
            int offset = myOffset + 0;
            return buffer.getInt(offset);
        }
        void setUnitIndex(int value) {
            buffer.putInt(myOffset + 0, value);
        }
        int getSearchValue() {
            int offset = myOffset + 4;
            return buffer.getInt(offset);
        }
        void setSearchValue(int value) {
            buffer.putInt(myOffset + 4, value);
        }
    }
    class UnitData {
        static final int SIZE = 336;
        private int myOffset;
        UnitData(int myOffset) {
          this.myOffset = myOffset;
        }
        int getClearanceLevel() {
            int offset = myOffset + 0;
            return buffer.getInt(offset);
        }
        void setClearanceLevel(int value) {
            buffer.putInt(myOffset + 0, value);
        }
        int getId() {
            int offset = myOffset + 4;
            return buffer.getInt(offset);
        }
        void setId(int value) {
            buffer.putInt(myOffset + 4, value);
        }
        int getPlayer() {
            int offset = myOffset + 8;
            return buffer.getInt(offset);
        }
        void setPlayer(int value) {
            buffer.putInt(myOffset + 8, value);
        }
        int getType() {
            int offset = myOffset + 12;
            return buffer.getInt(offset);
        }
        void setType(int value) {
            buffer.putInt(myOffset + 12, value);
        }
        int getPositionX() {
            int offset = myOffset + 16;
            return buffer.getInt(offset);
        }
        void setPositionX(int value) {
            buffer.putInt(myOffset + 16, value);
        }
        int getPositionY() {
            int offset = myOffset + 20;
            return buffer.getInt(offset);
        }
        void setPositionY(int value) {
            buffer.putInt(myOffset + 20, value);
        }
        double getAngle() {
            int offset = myOffset + 24;
            return buffer.getDouble(offset);
        }
        void setAngle(double value) {
            buffer.putDouble(myOffset + 24, value);
        }
        double getVelocityX() {
            int offset = myOffset + 32;
            return buffer.getDouble(offset);
        }
        void setVelocityX(double value) {
            buffer.putDouble(myOffset + 32, value);
        }
        double getVelocityY() {
            int offset = myOffset + 40;
            return buffer.getDouble(offset);
        }
        void setVelocityY(double value) {
            buffer.putDouble(myOffset + 40, value);
        }
        int getHitPoints() {
            int offset = myOffset + 48;
            return buffer.getInt(offset);
        }
        void setHitPoints(int value) {
            buffer.putInt(myOffset + 48, value);
        }
        int getLastHitPoints() {
            int offset = myOffset + 52;
            return buffer.getInt(offset);
        }
        void setLastHitPoints(int value) {
            buffer.putInt(myOffset + 52, value);
        }
        int getShields() {
            int offset = myOffset + 56;
            return buffer.getInt(offset);
        }
        void setShields(int value) {
            buffer.putInt(myOffset + 56, value);
        }
        int getEnergy() {
            int offset = myOffset + 60;
            return buffer.getInt(offset);
        }
        void setEnergy(int value) {
            buffer.putInt(myOffset + 60, value);
        }
        int getResources() {
            int offset = myOffset + 64;
            return buffer.getInt(offset);
        }
        void setResources(int value) {
            buffer.putInt(myOffset + 64, value);
        }
        int getResourceGroup() {
            int offset = myOffset + 68;
            return buffer.getInt(offset);
        }
        void setResourceGroup(int value) {
            buffer.putInt(myOffset + 68, value);
        }
        int getKillCount() {
            int offset = myOffset + 72;
            return buffer.getInt(offset);
        }
        void setKillCount(int value) {
            buffer.putInt(myOffset + 72, value);
        }
        int getAcidSporeCount() {
            int offset = myOffset + 76;
            return buffer.getInt(offset);
        }
        void setAcidSporeCount(int value) {
            buffer.putInt(myOffset + 76, value);
        }
        int getScarabCount() {
            int offset = myOffset + 80;
            return buffer.getInt(offset);
        }
        void setScarabCount(int value) {
            buffer.putInt(myOffset + 80, value);
        }
        int getInterceptorCount() {
            int offset = myOffset + 84;
            return buffer.getInt(offset);
        }
        void setInterceptorCount(int value) {
            buffer.putInt(myOffset + 84, value);
        }
        int getSpiderMineCount() {
            int offset = myOffset + 88;
            return buffer.getInt(offset);
        }
        void setSpiderMineCount(int value) {
            buffer.putInt(myOffset + 88, value);
        }
        int getGroundWeaponCooldown() {
            int offset = myOffset + 92;
            return buffer.getInt(offset);
        }
        void setGroundWeaponCooldown(int value) {
            buffer.putInt(myOffset + 92, value);
        }
        int getAirWeaponCooldown() {
            int offset = myOffset + 96;
            return buffer.getInt(offset);
        }
        void setAirWeaponCooldown(int value) {
            buffer.putInt(myOffset + 96, value);
        }
        int getSpellCooldown() {
            int offset = myOffset + 100;
            return buffer.getInt(offset);
        }
        void setSpellCooldown(int value) {
            buffer.putInt(myOffset + 100, value);
        }
        int getDefenseMatrixPoints() {
            int offset = myOffset + 104;
            return buffer.getInt(offset);
        }
        void setDefenseMatrixPoints(int value) {
            buffer.putInt(myOffset + 104, value);
        }
        int getDefenseMatrixTimer() {
            int offset = myOffset + 108;
            return buffer.getInt(offset);
        }
        void setDefenseMatrixTimer(int value) {
            buffer.putInt(myOffset + 108, value);
        }
        int getEnsnareTimer() {
            int offset = myOffset + 112;
            return buffer.getInt(offset);
        }
        void setEnsnareTimer(int value) {
            buffer.putInt(myOffset + 112, value);
        }
        int getIrradiateTimer() {
            int offset = myOffset + 116;
            return buffer.getInt(offset);
        }
        void setIrradiateTimer(int value) {
            buffer.putInt(myOffset + 116, value);
        }
        int getLockdownTimer() {
            int offset = myOffset + 120;
            return buffer.getInt(offset);
        }
        void setLockdownTimer(int value) {
            buffer.putInt(myOffset + 120, value);
        }
        int getMaelstromTimer() {
            int offset = myOffset + 124;
            return buffer.getInt(offset);
        }
        void setMaelstromTimer(int value) {
            buffer.putInt(myOffset + 124, value);
        }
        int getOrderTimer() {
            int offset = myOffset + 128;
            return buffer.getInt(offset);
        }
        void setOrderTimer(int value) {
            buffer.putInt(myOffset + 128, value);
        }
        int getPlagueTimer() {
            int offset = myOffset + 132;
            return buffer.getInt(offset);
        }
        void setPlagueTimer(int value) {
            buffer.putInt(myOffset + 132, value);
        }
        int getRemoveTimer() {
            int offset = myOffset + 136;
            return buffer.getInt(offset);
        }
        void setRemoveTimer(int value) {
            buffer.putInt(myOffset + 136, value);
        }
        int getStasisTimer() {
            int offset = myOffset + 140;
            return buffer.getInt(offset);
        }
        void setStasisTimer(int value) {
            buffer.putInt(myOffset + 140, value);
        }
        int getStimTimer() {
            int offset = myOffset + 144;
            return buffer.getInt(offset);
        }
        void setStimTimer(int value) {
            buffer.putInt(myOffset + 144, value);
        }
        int getBuildType() {
            int offset = myOffset + 148;
            return buffer.getInt(offset);
        }
        void setBuildType(int value) {
            buffer.putInt(myOffset + 148, value);
        }
        int getTrainingQueueCount() {
            int offset = myOffset + 152;
            return buffer.getInt(offset);
        }
        void setTrainingQueueCount(int value) {
            buffer.putInt(myOffset + 152, value);
        }
        int getTrainingQueue(int i) {
            int offset = myOffset + 156 + 4 * 1 * i;
            return buffer.getInt(offset);
        }
        void setTrainingQueue(int i, int value) {
            buffer.putInt(myOffset + 156 + 4 * 1 * i, value);
        }
        int getTech() {
            int offset = myOffset + 176;
            return buffer.getInt(offset);
        }
        void setTech(int value) {
            buffer.putInt(myOffset + 176, value);
        }
        int getUpgrade() {
            int offset = myOffset + 180;
            return buffer.getInt(offset);
        }
        void setUpgrade(int value) {
            buffer.putInt(myOffset + 180, value);
        }
        int getRemainingBuildTime() {
            int offset = myOffset + 184;
            return buffer.getInt(offset);
        }
        void setRemainingBuildTime(int value) {
            buffer.putInt(myOffset + 184, value);
        }
        int getRemainingTrainTime() {
            int offset = myOffset + 188;
            return buffer.getInt(offset);
        }
        void setRemainingTrainTime(int value) {
            buffer.putInt(myOffset + 188, value);
        }
        int getRemainingResearchTime() {
            int offset = myOffset + 192;
            return buffer.getInt(offset);
        }
        void setRemainingResearchTime(int value) {
            buffer.putInt(myOffset + 192, value);
        }
        int getRemainingUpgradeTime() {
            int offset = myOffset + 196;
            return buffer.getInt(offset);
        }
        void setRemainingUpgradeTime(int value) {
            buffer.putInt(myOffset + 196, value);
        }
        int getBuildUnit() {
            int offset = myOffset + 200;
            return buffer.getInt(offset);
        }
        void setBuildUnit(int value) {
            buffer.putInt(myOffset + 200, value);
        }
        int getTarget() {
            int offset = myOffset + 204;
            return buffer.getInt(offset);
        }
        void setTarget(int value) {
            buffer.putInt(myOffset + 204, value);
        }
        int getTargetPositionX() {
            int offset = myOffset + 208;
            return buffer.getInt(offset);
        }
        void setTargetPositionX(int value) {
            buffer.putInt(myOffset + 208, value);
        }
        int getTargetPositionY() {
            int offset = myOffset + 212;
            return buffer.getInt(offset);
        }
        void setTargetPositionY(int value) {
            buffer.putInt(myOffset + 212, value);
        }
        int getOrder() {
            int offset = myOffset + 216;
            return buffer.getInt(offset);
        }
        void setOrder(int value) {
            buffer.putInt(myOffset + 216, value);
        }
        int getOrderTarget() {
            int offset = myOffset + 220;
            return buffer.getInt(offset);
        }
        void setOrderTarget(int value) {
            buffer.putInt(myOffset + 220, value);
        }
        int getOrderTargetPositionX() {
            int offset = myOffset + 224;
            return buffer.getInt(offset);
        }
        void setOrderTargetPositionX(int value) {
            buffer.putInt(myOffset + 224, value);
        }
        int getOrderTargetPositionY() {
            int offset = myOffset + 228;
            return buffer.getInt(offset);
        }
        void setOrderTargetPositionY(int value) {
            buffer.putInt(myOffset + 228, value);
        }
        int getSecondaryOrder() {
            int offset = myOffset + 232;
            return buffer.getInt(offset);
        }
        void setSecondaryOrder(int value) {
            buffer.putInt(myOffset + 232, value);
        }
        int getRallyPositionX() {
            int offset = myOffset + 236;
            return buffer.getInt(offset);
        }
        void setRallyPositionX(int value) {
            buffer.putInt(myOffset + 236, value);
        }
        int getRallyPositionY() {
            int offset = myOffset + 240;
            return buffer.getInt(offset);
        }
        void setRallyPositionY(int value) {
            buffer.putInt(myOffset + 240, value);
        }
        int getRallyUnit() {
            int offset = myOffset + 244;
            return buffer.getInt(offset);
        }
        void setRallyUnit(int value) {
            buffer.putInt(myOffset + 244, value);
        }
        int getAddon() {
            int offset = myOffset + 248;
            return buffer.getInt(offset);
        }
        void setAddon(int value) {
            buffer.putInt(myOffset + 248, value);
        }
        int getNydusExit() {
            int offset = myOffset + 252;
            return buffer.getInt(offset);
        }
        void setNydusExit(int value) {
            buffer.putInt(myOffset + 252, value);
        }
        int getPowerUp() {
            int offset = myOffset + 256;
            return buffer.getInt(offset);
        }
        void setPowerUp(int value) {
            buffer.putInt(myOffset + 256, value);
        }
        int getTransport() {
            int offset = myOffset + 260;
            return buffer.getInt(offset);
        }
        void setTransport(int value) {
            buffer.putInt(myOffset + 260, value);
        }
        int getCarrier() {
            int offset = myOffset + 264;
            return buffer.getInt(offset);
        }
        void setCarrier(int value) {
            buffer.putInt(myOffset + 264, value);
        }
        int getHatchery() {
            int offset = myOffset + 268;
            return buffer.getInt(offset);
        }
        void setHatchery(int value) {
            buffer.putInt(myOffset + 268, value);
        }
        boolean getExists() {
            int offset = myOffset + 272;
            return buffer.getByte(offset) != 0;
        }
        void setExists(boolean value) {
            buffer.putByte(myOffset + 272, (byte) (value ? 1 : 0));
        }
        boolean getHasNuke() {
            int offset = myOffset + 273;
            return buffer.getByte(offset) != 0;
        }
        void setHasNuke(boolean value) {
            buffer.putByte(myOffset + 273, (byte) (value ? 1 : 0));
        }
        boolean isAccelerating() {
            int offset = myOffset + 274;
            return buffer.getByte(offset) != 0;
        }
        void setIsAccelerating(boolean value) {
            buffer.putByte(myOffset + 274, (byte) (value ? 1 : 0));
        }
        boolean isAttacking() {
            int offset = myOffset + 275;
            return buffer.getByte(offset) != 0;
        }
        void setIsAttacking(boolean value) {
            buffer.putByte(myOffset + 275, (byte) (value ? 1 : 0));
        }
        boolean isAttackFrame() {
            int offset = myOffset + 276;
            return buffer.getByte(offset) != 0;
        }
        void setIsAttackFrame(boolean value) {
            buffer.putByte(myOffset + 276, (byte) (value ? 1 : 0));
        }
        boolean isBeingGathered() {
            int offset = myOffset + 277;
            return buffer.getByte(offset) != 0;
        }
        void setIsBeingGathered(boolean value) {
            buffer.putByte(myOffset + 277, (byte) (value ? 1 : 0));
        }
        boolean isBlind() {
            int offset = myOffset + 278;
            return buffer.getByte(offset) != 0;
        }
        void setIsBlind(boolean value) {
            buffer.putByte(myOffset + 278, (byte) (value ? 1 : 0));
        }
        boolean isBraking() {
            int offset = myOffset + 279;
            return buffer.getByte(offset) != 0;
        }
        void setIsBraking(boolean value) {
            buffer.putByte(myOffset + 279, (byte) (value ? 1 : 0));
        }
        boolean isBurrowed() {
            int offset = myOffset + 280;
            return buffer.getByte(offset) != 0;
        }
        void setIsBurrowed(boolean value) {
            buffer.putByte(myOffset + 280, (byte) (value ? 1 : 0));
        }
        int getCarryResourceType() {
            int offset = myOffset + 284;
            return buffer.getInt(offset);
        }
        void setCarryResourceType(int value) {
            buffer.putInt(myOffset + 284, value);
        }
        boolean isCloaked() {
            int offset = myOffset + 288;
            return buffer.getByte(offset) != 0;
        }
        void setIsCloaked(boolean value) {
            buffer.putByte(myOffset + 288, (byte) (value ? 1 : 0));
        }
        boolean isCompleted() {
            int offset = myOffset + 289;
            return buffer.getByte(offset) != 0;
        }
        void setIsCompleted(boolean value) {
            buffer.putByte(myOffset + 289, (byte) (value ? 1 : 0));
        }
        boolean isConstructing() {
            int offset = myOffset + 290;
            return buffer.getByte(offset) != 0;
        }
        void setIsConstructing(boolean value) {
            buffer.putByte(myOffset + 290, (byte) (value ? 1 : 0));
        }
        boolean isDetected() {
            int offset = myOffset + 291;
            return buffer.getByte(offset) != 0;
        }
        void setIsDetected(boolean value) {
            buffer.putByte(myOffset + 291, (byte) (value ? 1 : 0));
        }
        boolean isGathering() {
            int offset = myOffset + 292;
            return buffer.getByte(offset) != 0;
        }
        void setIsGathering(boolean value) {
            buffer.putByte(myOffset + 292, (byte) (value ? 1 : 0));
        }
        boolean isHallucination() {
            int offset = myOffset + 293;
            return buffer.getByte(offset) != 0;
        }
        void setIsHallucination(boolean value) {
            buffer.putByte(myOffset + 293, (byte) (value ? 1 : 0));
        }
        boolean isIdle() {
            int offset = myOffset + 294;
            return buffer.getByte(offset) != 0;
        }
        void setIsIdle(boolean value) {
            buffer.putByte(myOffset + 294, (byte) (value ? 1 : 0));
        }
        boolean isInterruptible() {
            int offset = myOffset + 295;
            return buffer.getByte(offset) != 0;
        }
        void setIsInterruptible(boolean value) {
            buffer.putByte(myOffset + 295, (byte) (value ? 1 : 0));
        }
        boolean isInvincible() {
            int offset = myOffset + 296;
            return buffer.getByte(offset) != 0;
        }
        void setIsInvincible(boolean value) {
            buffer.putByte(myOffset + 296, (byte) (value ? 1 : 0));
        }
        boolean isLifted() {
            int offset = myOffset + 297;
            return buffer.getByte(offset) != 0;
        }
        void setIsLifted(boolean value) {
            buffer.putByte(myOffset + 297, (byte) (value ? 1 : 0));
        }
        boolean isMorphing() {
            int offset = myOffset + 298;
            return buffer.getByte(offset) != 0;
        }
        void setIsMorphing(boolean value) {
            buffer.putByte(myOffset + 298, (byte) (value ? 1 : 0));
        }
        boolean isMoving() {
            int offset = myOffset + 299;
            return buffer.getByte(offset) != 0;
        }
        void setIsMoving(boolean value) {
            buffer.putByte(myOffset + 299, (byte) (value ? 1 : 0));
        }
        boolean isParasited() {
            int offset = myOffset + 300;
            return buffer.getByte(offset) != 0;
        }
        void setIsParasited(boolean value) {
            buffer.putByte(myOffset + 300, (byte) (value ? 1 : 0));
        }
        boolean isSelected() {
            int offset = myOffset + 301;
            return buffer.getByte(offset) != 0;
        }
        void setIsSelected(boolean value) {
            buffer.putByte(myOffset + 301, (byte) (value ? 1 : 0));
        }
        boolean isStartingAttack() {
            int offset = myOffset + 302;
            return buffer.getByte(offset) != 0;
        }
        void setIsStartingAttack(boolean value) {
            buffer.putByte(myOffset + 302, (byte) (value ? 1 : 0));
        }
        boolean isStuck() {
            int offset = myOffset + 303;
            return buffer.getByte(offset) != 0;
        }
        void setIsStuck(boolean value) {
            buffer.putByte(myOffset + 303, (byte) (value ? 1 : 0));
        }
        boolean isTraining() {
            int offset = myOffset + 304;
            return buffer.getByte(offset) != 0;
        }
        void setIsTraining(boolean value) {
            buffer.putByte(myOffset + 304, (byte) (value ? 1 : 0));
        }
        boolean isUnderStorm() {
            int offset = myOffset + 305;
            return buffer.getByte(offset) != 0;
        }
        void setIsUnderStorm(boolean value) {
            buffer.putByte(myOffset + 305, (byte) (value ? 1 : 0));
        }
        boolean isUnderDarkSwarm() {
            int offset = myOffset + 306;
            return buffer.getByte(offset) != 0;
        }
        void setIsUnderDarkSwarm(boolean value) {
            buffer.putByte(myOffset + 306, (byte) (value ? 1 : 0));
        }
        boolean isUnderDWeb() {
            int offset = myOffset + 307;
            return buffer.getByte(offset) != 0;
        }
        void setIsUnderDWeb(boolean value) {
            buffer.putByte(myOffset + 307, (byte) (value ? 1 : 0));
        }
        boolean isPowered() {
            int offset = myOffset + 308;
            return buffer.getByte(offset) != 0;
        }
        void setIsPowered(boolean value) {
            buffer.putByte(myOffset + 308, (byte) (value ? 1 : 0));
        }
        boolean isVisible(int i) {
            int offset = myOffset + 309 + 1 * 1 * i;
            return buffer.getByte(offset) != 0;
        }
        void setIsVisible(int i, boolean value) {
            buffer.putByte(myOffset + 309 + 1 * 1 * i, (byte) (value ? 1 : 0));
        }
        int getButtonset() {
            int offset = myOffset + 320;
            return buffer.getInt(offset);
        }
        void setButtonset(int value) {
            buffer.putInt(myOffset + 320, value);
        }
        int getLastAttackerPlayer() {
            int offset = myOffset + 324;
            return buffer.getInt(offset);
        }
        void setLastAttackerPlayer(int value) {
            buffer.putInt(myOffset + 324, value);
        }
        boolean getRecentlyAttacked() {
            int offset = myOffset + 328;
            return buffer.getByte(offset) != 0;
        }
        void setRecentlyAttacked(boolean value) {
            buffer.putByte(myOffset + 328, (byte) (value ? 1 : 0));
        }
        int getReplayID() {
            int offset = myOffset + 332;
            return buffer.getInt(offset);
        }
        void setReplayID(int value) {
            buffer.putInt(myOffset + 332, value);
        }
    }
}

