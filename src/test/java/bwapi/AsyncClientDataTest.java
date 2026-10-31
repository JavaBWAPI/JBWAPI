package bwapi;

import org.junit.Test;

import static org.junit.Assert.*;

public class AsyncClientDataTest {

    @Test
    public void frameBufferAllocatesReducedMemory() {
        // Full GameData buffer is ~33 MB
        assertEquals(33017048, ClientData.GameData.SIZE);

        // Packed dynamic FrameBuffer is ~4.8 MB
        assertEquals(4805308, FrameBuffer.BUFFER_SIZE);

        assertTrue("FrameBuffer size must be significantly smaller than full GameData size",
                FrameBuffer.BUFFER_SIZE < ClientData.GameData.SIZE / 6);
    }

    @Test
    public void staticDataReadsFromStaticBuffer() {
        WrappedBuffer mainBuffer = new WrappedBuffer(ClientData.GameData.SIZE);
        WrappedBuffer dynamicBuffer = new WrappedBuffer(FrameBuffer.BUFFER_SIZE, true);

        // Populate static data in the main buffer
        ClientData mainData = new SyncClientData();
        mainData.setBuffer(mainBuffer);

        mainData.gameData().setGetGroundHeight(10, 20, 2);
        mainData.gameData().setIsWalkable(15, 25, true);
        mainData.gameData().setIsBuildable(12, 22, true);
        mainData.gameData().setMapTileRegionId(5, 7, (short) 42);
        mainData.gameData().setRegionCount(3);
        mainData.gameData().getRegions(0).setId(101);
        mainData.gameData().getRegions(0).setIslandID(999);
        mainData.gameData().getRegions(0).setCenter_x(320);
        mainData.gameData().getRegions(0).setCenter_y(240);

        // AsyncClientData reads static data from staticBuffer and dynamic data from dynamicBuffer
        AsyncClientData asyncData = new AsyncClientData(mainBuffer);
        asyncData.setBuffer(dynamicBuffer);

        assertEquals(2, asyncData.gameData().getGroundHeight(10, 20));
        assertTrue(asyncData.gameData().isWalkable(15, 25));
        assertTrue(asyncData.gameData().isBuildable(12, 22));
        assertEquals((short) 42, asyncData.gameData().getMapTileRegionId(5, 7));
        assertEquals(3, asyncData.gameData().getRegionCount());

        ClientData.RegionData region = asyncData.gameData().getRegions(0);
        assertEquals(101, region.getId());
        assertEquals(999, region.islandID());
        assertEquals(320, region.getCenter_x());
        assertEquals(240, region.getCenter_y());
    }

    @Test
    public void dynamicDataCopiesAndReadsCorrectlyAcrossRanges() {
        WrappedBuffer mainBuffer = new WrappedBuffer(ClientData.GameData.SIZE);
        WrappedBuffer packedBuffer = new WrappedBuffer(FrameBuffer.BUFFER_SIZE, true);

        // Setup mock source data in mainBuffer
        ClientData mainData = new SyncClientData();
        mainData.setBuffer(mainBuffer);

        // Range 1 (units, frameCount)
        mainData.gameData().setFrameCount(1234);
        mainData.gameData().getUnits(0).setHitPoints(40);
        mainData.gameData().getUnits(0).setPositionX(640);
        mainData.gameData().getUnits(0).setPositionY(480);

        // Range 2 (dynamic tiles)
        mainData.gameData().setIsVisible(5, 10, true);
        mainData.gameData().setIsExplored(5, 10, true);
        mainData.gameData().setHasCreep(5, 10, true);
        mainData.gameData().setIsOccupied(5, 10, true);

        // Range 3 (events, game state)
        mainData.gameData().setIsInGame(true);
        mainData.gameData().setStartLocationCount(2);
        mainData.gameData().getStartLocations(0).setX(16);
        mainData.gameData().getStartLocations(0).setY(32);
        mainData.gameData().setEventCount(1);
        mainData.gameData().getEvents(0).setType(EventType.MatchFrame);
        mainData.gameData().setEventStringCount(1);
        mainData.gameData().setEventStrings(0, "TestMessage");

        // Range 4 (commands)
        mainData.gameData().setCommandCount(5);
        mainData.gameData().setUnitCommandCount(10);

        // Perform packed frame buffer copy
        BWClientConfiguration config = new BWClientConfiguration.Builder().build();
        FrameBuffer frameBuffer = new FrameBuffer(config);
        frameBuffer.copyBuffer(mainBuffer, packedBuffer);

        // Wire AsyncClientData
        AsyncClientData asyncData = new AsyncClientData(mainBuffer);
        asyncData.setBuffer(packedBuffer);

        // Verify Range 1
        assertEquals(1234, asyncData.gameData().getFrameCount());
        assertEquals(40, asyncData.gameData().getUnits(0).getHitPoints());
        assertEquals(640, asyncData.gameData().getUnits(0).getPositionX());
        assertEquals(480, asyncData.gameData().getUnits(0).getPositionY());

        // Verify Range 2
        assertTrue(asyncData.gameData().isVisible(5, 10));
        assertTrue(asyncData.gameData().isExplored(5, 10));
        assertTrue(asyncData.gameData().getHasCreep(5, 10));
        assertTrue(asyncData.gameData().isOccupied(5, 10));
        assertFalse(asyncData.gameData().isVisible(6, 10));

        // Verify Range 3
        assertTrue(asyncData.gameData().isInGame());
        assertEquals(2, asyncData.gameData().getStartLocationCount());
        assertEquals(16, asyncData.gameData().getStartLocations(0).getX());
        assertEquals(32, asyncData.gameData().getStartLocations(0).getY());
        assertEquals(1, asyncData.gameData().getEventCount());
        assertEquals(EventType.MatchFrame, asyncData.gameData().getEvents(0).getType());
        assertEquals("TestMessage", asyncData.gameData().getEventStrings(0));

        // Verify Range 4
        assertEquals(5, asyncData.gameData().getCommandCount());
        assertEquals(10, asyncData.gameData().getUnitCommandCount());
    }

    @Test
    public void asyncClientDataFallsBackCorrectlyWithFullBuffer() {
        // Tests asyncUnsafe mode where bot is placed directly on the full 33MB liveData buffer
        WrappedBuffer fullBuffer = new WrappedBuffer(ClientData.GameData.SIZE);
        assertFalse(fullBuffer.isPacked());

        ClientData syncData = new SyncClientData();
        syncData.setBuffer(fullBuffer);
        syncData.gameData().setIsVisible(10, 20, true);
        syncData.gameData().setIsInGame(true);

        AsyncClientData asyncData = new AsyncClientData(fullBuffer);
        asyncData.setBuffer(fullBuffer); // Unpacked full buffer

        // Should read with zero delta offset and match syncData
        assertTrue(asyncData.gameData().isVisible(10, 20));
        assertTrue(asyncData.gameData().isInGame());
    }
}
