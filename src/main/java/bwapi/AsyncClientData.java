package bwapi;

/**
 * Asynchronous client data that routes static information to the main/static shared buffer
 * and dynamic information to the packed dynamic buffer.
 */
class AsyncClientData extends ClientData {
    static final int STATICTILES_START = 3447004;
    static final int STATICTILES_END = 4823260;
    static final int REGION_START = 5085404;
    static final int REGION_END = 10586480;
    static final int STRINGSSHAPES_START = 10962632;
    static final int STRINGSHAPES_END = 32242636;
    static final int UNITFINDER_START = 32962644;

    static final int DELTA_RANGE2 = STATICTILES_END - STATICTILES_START; // 1376256
    static final int DELTA_RANGE3 = DELTA_RANGE2 + (REGION_END - REGION_START); // 6877332
    static final int DELTA_RANGE4 = DELTA_RANGE3 + (STRINGSHAPES_END - STRINGSSHAPES_START); // 28157336

    private WrappedBuffer staticBuffer;

    AsyncClientData() {
        super();
    }

    AsyncClientData(WrappedBuffer staticBuffer) {
        super();
        this.staticBuffer = staticBuffer;
    }

    void setStaticBuffer(WrappedBuffer staticBuffer) {
        this.staticBuffer = staticBuffer;
    }

    @Override
    WrappedBuffer getStaticBuffer() {
        return staticBuffer != null ? staticBuffer : buffer;
    }

    @Override
    GameData createGameData(int offset) {
        return new AsyncGameData(offset);
    }

    class AsyncGameData extends GameData {
        AsyncGameData(int myOffset) {
            super(myOffset);
        }

        private int deltaRange2() {
            return buffer != null && buffer.isPacked() ? DELTA_RANGE2 : 0;
        }

        private int deltaRange3() {
            return buffer != null && buffer.isPacked() ? DELTA_RANGE3 : 0;
        }

        private int deltaRange4() {
            return buffer != null && buffer.isPacked() ? DELTA_RANGE4 : 0;
        }

        // Static tiles -> route to staticBuffer
        @Override
        int getGroundHeight(int i, int j) {
            int offset = 3447004 + 4 * 1 * j + 4 * 256 * i;
            return getStaticBuffer().getInt(offset);
        }

        @Override
        void setGetGroundHeight(int i, int j, int value) {
            getStaticBuffer().putInt(3447004 + 4 * 1 * j + 4 * 256 * i, value);
        }

        @Override
        boolean isWalkable(int i, int j) {
            int offset = 3709148 + 1 * 1 * j + 1 * 1024 * i;
            return getStaticBuffer().getByte(offset) != 0;
        }

        @Override
        void setIsWalkable(int i, int j, boolean value) {
            getStaticBuffer().putByte(3709148 + 1 * 1 * j + 1 * 1024 * i, (byte) (value ? 1 : 0));
        }

        @Override
        boolean isBuildable(int i, int j) {
            int offset = 4757724 + 1 * 1 * j + 1 * 256 * i;
            return getStaticBuffer().getByte(offset) != 0;
        }

        @Override
        void setIsBuildable(int i, int j, boolean value) {
            getStaticBuffer().putByte(4757724 + 1 * 1 * j + 1 * 256 * i, (byte) (value ? 1 : 0));
        }

        // Static regions -> route to staticBuffer
        @Override
        short getMapTileRegionId(int i, int j) {
            int offset = 5085404 + 2 * 1 * j + 2 * 256 * i;
            return getStaticBuffer().getShort(offset);
        }

        @Override
        void setMapTileRegionId(int i, int j, short value) {
            getStaticBuffer().putShort(5085404 + 2 * 1 * j + 2 * 256 * i, value);
        }

        @Override
        short getMapSplitTilesMiniTileMask(int i) {
            int offset = 5216476 + 2 * 1 * i;
            return getStaticBuffer().getShort(offset);
        }

        @Override
        void setMapSplitTilesMiniTileMask(int i, short value) {
            getStaticBuffer().putShort(5216476 + 2 * 1 * i, value);
        }

        @Override
        short getMapSplitTilesRegion1(int i) {
            int offset = 5226476 + 2 * 1 * i;
            return getStaticBuffer().getShort(offset);
        }

        @Override
        void setMapSplitTilesRegion1(int i, short value) {
            getStaticBuffer().putShort(5226476 + 2 * 1 * i, value);
        }

        @Override
        short getMapSplitTilesRegion2(int i) {
            int offset = 5236476 + 2 * 1 * i;
            return getStaticBuffer().getShort(offset);
        }

        @Override
        void setMapSplitTilesRegion2(int i, short value) {
            getStaticBuffer().putShort(5236476 + 2 * 1 * i, value);
        }

        @Override
        int getRegionCount() {
            int offset = 5246476;
            return getStaticBuffer().getInt(offset);
        }

        @Override
        void setRegionCount(int value) {
            getStaticBuffer().putInt(5246476, value);
        }

        @Override
        RegionData getRegions(int i) {
            int offset = 5246480 + 1068 * 1 * i;
            return new RegionData(offset);
        }

        // Dynamic Range 2 -> subtract deltaRange2()
        @Override
        boolean isVisible(int i, int j) {
            int offset = 4823260 - deltaRange2() + 1 * 1 * j + 1 * 256 * i;
            return buffer.getByte(offset) != 0;
        }

        @Override
        void setIsVisible(int i, int j, boolean value) {
            buffer.putByte(4823260 - deltaRange2() + 1 * 1 * j + 1 * 256 * i, (byte) (value ? 1 : 0));
        }

        @Override
        boolean isExplored(int i, int j) {
            int offset = 4888796 - deltaRange2() + 1 * 1 * j + 1 * 256 * i;
            return buffer.getByte(offset) != 0;
        }

        @Override
        void setIsExplored(int i, int j, boolean value) {
            buffer.putByte(4888796 - deltaRange2() + 1 * 1 * j + 1 * 256 * i, (byte) (value ? 1 : 0));
        }

        @Override
        boolean getHasCreep(int i, int j) {
            int offset = 4954332 - deltaRange2() + 1 * 1 * j + 1 * 256 * i;
            return buffer.getByte(offset) != 0;
        }

        @Override
        void setHasCreep(int i, int j, boolean value) {
            buffer.putByte(4954332 - deltaRange2() + 1 * 1 * j + 1 * 256 * i, (byte) (value ? 1 : 0));
        }

        @Override
        boolean isOccupied(int i, int j) {
            int offset = 5019868 - deltaRange2() + 1 * 1 * j + 1 * 256 * i;
            return buffer.getByte(offset) != 0;
        }

        @Override
        void setIsOccupied(int i, int j, boolean value) {
            buffer.putByte(5019868 - deltaRange2() + 1 * 1 * j + 1 * 256 * i, (byte) (value ? 1 : 0));
        }

        // Dynamic Range 3 -> subtract deltaRange3()
        @Override
        int getStartLocationCount() {
            int offset = 10586480 - deltaRange3();
            return buffer.getInt(offset);
        }

        @Override
        void setStartLocationCount(int value) {
            buffer.putInt(10586480 - deltaRange3(), value);
        }

        @Override
        Position getStartLocations(int i) {
            int offset = 10586484 - deltaRange3() + 8 * 1 * i;
            return new Position(offset);
        }

        @Override
        boolean isInGame() {
            int offset = 10586548 - deltaRange3();
            return buffer.getByte(offset) != 0;
        }

        @Override
        void setIsInGame(boolean value) {
            buffer.putByte(10586548 - deltaRange3(), (byte) (value ? 1 : 0));
        }

        @Override
        boolean isMultiplayer() {
            int offset = 10586549 - deltaRange3();
            return buffer.getByte(offset) != 0;
        }

        @Override
        void setIsMultiplayer(boolean value) {
            buffer.putByte(10586549 - deltaRange3(), (byte) (value ? 1 : 0));
        }

        @Override
        boolean isBattleNet() {
            int offset = 10586550 - deltaRange3();
            return buffer.getByte(offset) != 0;
        }

        @Override
        void setIsBattleNet(boolean value) {
            buffer.putByte(10586550 - deltaRange3(), (byte) (value ? 1 : 0));
        }

        @Override
        boolean isPaused() {
            int offset = 10586551 - deltaRange3();
            return buffer.getByte(offset) != 0;
        }

        @Override
        void setIsPaused(boolean value) {
            buffer.putByte(10586551 - deltaRange3(), (byte) (value ? 1 : 0));
        }

        @Override
        boolean isReplay() {
            int offset = 10586552 - deltaRange3();
            return buffer.getByte(offset) != 0;
        }

        @Override
        void setIsReplay(boolean value) {
            buffer.putByte(10586552 - deltaRange3(), (byte) (value ? 1 : 0));
        }

        @Override
        int getSelectedUnitCount() {
            int offset = 10586556 - deltaRange3();
            return buffer.getInt(offset);
        }

        @Override
        void setSelectedUnitCount(int value) {
            buffer.putInt(10586556 - deltaRange3(), value);
        }

        @Override
        int getSelectedUnits(int i) {
            int offset = 10586560 - deltaRange3() + 4 * 1 * i;
            return buffer.getInt(offset);
        }

        @Override
        void setSelectedUnits(int i, int value) {
            buffer.putInt(10586560 - deltaRange3() + 4 * 1 * i, value);
        }

        @Override
        int getSelf() {
            int offset = 10586608 - deltaRange3();
            return buffer.getInt(offset);
        }

        @Override
        void setSelf(int value) {
            buffer.putInt(10586608 - deltaRange3(), value);
        }

        @Override
        int getEnemy() {
            int offset = 10586612 - deltaRange3();
            return buffer.getInt(offset);
        }

        @Override
        void setEnemy(int value) {
            buffer.putInt(10586612 - deltaRange3(), value);
        }

        @Override
        int getNeutral() {
            int offset = 10586616 - deltaRange3();
            return buffer.getInt(offset);
        }

        @Override
        void setNeutral(int value) {
            buffer.putInt(10586616 - deltaRange3(), value);
        }

        @Override
        int getEventCount() {
            int offset = 10586620 - deltaRange3();
            return buffer.getInt(offset);
        }

        @Override
        void setEventCount(int value) {
            buffer.putInt(10586620 - deltaRange3(), value);
        }

        @Override
        Event getEvents(int i) {
            int offset = 10586624 - deltaRange3() + 12 * 1 * i;
            return new Event(offset);
        }

        @Override
        int getEventStringCount() {
            int offset = 10706624 - deltaRange3();
            return buffer.getInt(offset);
        }

        @Override
        void setEventStringCount(int value) {
            buffer.putInt(10706624 - deltaRange3(), value);
        }

        @Override
        String getEventStrings(int i) {
            int offset = 10706628 - deltaRange3() + 1 * 256 * i;
            return buffer.getString(offset, 256);
        }

        @Override
        void setEventStrings(int i, String value) {
            buffer.putString(10706628 - deltaRange3() + 1 * 256 * i, 256, value);
        }

        @Override
        int getStringCount() {
            int offset = 10962628 - deltaRange3();
            return buffer.getInt(offset);
        }

        @Override
        void setStringCount(int value) {
            buffer.putInt(10962628 - deltaRange3(), value);
        }

        // Dynamic Range 4 -> subtract deltaRange4()
        @Override
        int getCommandCount() {
            int offset = 32242636 - deltaRange4();
            return buffer.getInt(offset);
        }

        @Override
        void setCommandCount(int value) {
            buffer.putInt(32242636 - deltaRange4(), value);
        }

        @Override
        Command getCommands(int i) {
            int offset = 32242640 - deltaRange4() + 12 * 1 * i;
            return new Command(offset);
        }

        @Override
        int getUnitCommandCount() {
            int offset = 32482640 - deltaRange4();
            return buffer.getInt(offset);
        }

        @Override
        void setUnitCommandCount(int value) {
            buffer.putInt(32482640 - deltaRange4(), value);
        }

        @Override
        UnitCommand getUnitCommands(int i) {
            int offset = 32482644 - deltaRange4() + 24 * 1 * i;
            return new UnitCommand(offset);
        }
    }
}
