package bwapi;

import org.junit.Test;

import static org.junit.Assert.*;

public class ClientDataAsyncTest {

    @Test
    public void retainedViewsSurviveLiveAndPackedHandoffs() {
        WrappedBuffer live = new WrappedBuffer(ClientData.GameData.SIZE);
        PackedWrappedBuffer packed = new PackedWrappedBuffer(ClientData.GameData.SIZE);
        ClientData source = new ClientData();
        source.setBuffer(live);
        source.gameData().setEventCount(1);
        source.gameData().getEvents(0).setType(EventType.MatchFrame);
        source.gameData().getStartLocations(0).setX(123);
        new FrameBuffer(new BWClientConfiguration.Builder().withAsyncFrameBufferCapacity(1).build())
                .copyBuffer(live, packed);

        ClientDataAsync data = new ClientDataAsync(live);
        data.setBuffer(live);
        ClientData.Event event = data.gameData().getEvents(0);
        ClientData.Position position = data.gameData().getStartLocations(0);
        data.setBuffer(packed);
        assertEquals(EventType.MatchFrame, event.getType());
        assertEquals(123, position.getX());

        event.setType(EventType.MatchEnd);
        position.setX(789);
        ClientData.Event packedEvent = data.gameData().getEvents(0);
        data.setBuffer(live);
        assertEquals(EventType.MatchFrame, packedEvent.getType());
        assertEquals(123, position.getX());
        data.setBuffer(packed);
        assertEquals(EventType.MatchEnd, event.getType());
        assertEquals(789, position.getX());
    }

    @Test
    public void handoffDuringReadKeepsTheSelectedBuffersLayout() {
        class SwitchingBuffer extends WrappedBuffer {
            Runnable handoff;

            SwitchingBuffer() {
                super(ClientData.GameData.SIZE);
            }

            @Override
            byte getByte(int offset) {
                if (handoff != null) handoff.run();
                return super.getByte(offset);
            }
        }

        SwitchingBuffer live = new SwitchingBuffer();
        PackedWrappedBuffer packed = new PackedWrappedBuffer();
        ClientData source = new ClientData();
        source.setBuffer(live);
        source.gameData().setIsVisible(0, 0, true);
        new FrameBuffer(new BWClientConfiguration.Builder().withAsyncFrameBufferCapacity(1).build())
                .copyBuffer(live, packed);

        ClientDataAsync data = new ClientDataAsync(live);
        data.setBuffer(live);
        live.handoff = () -> data.setBuffer(packed);
        assertTrue(data.gameData().isVisible(0, 0));
        assertSame(packed, data.buffer);

        live.handoff = null;
        assertTrue(data.gameData().isVisible(0, 0));
        data.setBuffer(live);
        assertTrue(data.gameData().isVisible(0, 0));
        assertSame(live, data.buffer);
    }

    @Test
    public void frameBufferAllocatesReducedMemory() {
        // Full GameData buffer is ~33 MB
        assertEquals(33017048, ClientData.GameData.SIZE);
        // Packed dynamic FrameBuffer size is ~4.08 MB
        assertEquals(4085300, FrameBuffer.BUFFER_SIZE);
        assertTrue(FrameBuffer.BUFFER_SIZE < 5 * 1024 * 1024);
    }

    @Test
    public void clientDataAsyncReadsStaticDataFromStaticBuffer() {
        WrappedBuffer mainBuffer = new WrappedBuffer(ClientData.GameData.SIZE);
        PackedWrappedBuffer dynamicBuffer = new PackedWrappedBuffer();

        // Populate static data in the main buffer
        ClientData mainData = new ClientData();
        mainData.setBuffer(mainBuffer);

        mainData.gameData().setGetGroundHeight(10, 20, 2);
        mainData.gameData().setIsWalkable(100, 200, true);
        mainData.gameData().setIsBuildable(15, 25, true);
        mainData.gameData().setMapTileRegionId(5, 6, (short) 42);
        mainData.gameData().setMapSplitTilesMiniTileMask(3, (short) 0xAAAA);
        mainData.gameData().setMapSplitTilesRegion1(3, (short) 11);
        mainData.gameData().setMapSplitTilesRegion2(3, (short) 22);
        mainData.gameData().setRegionCount(7);
        mainData.gameData().getRegions(2).setId(99);
        mainData.gameData().getRegions(2).setCenter_x(320);
        mainData.gameData().getRegions(2).setCenter_y(240);
        mainData.gameData().getRegions(2).setIsAccessible(true);
        mainData.gameData().getRegions(2).setIsHigherGround(true);

        // Configure ClientDataAsync with mainBuffer as static and dynamicBuffer as active
        ClientDataAsync asyncData = new ClientDataAsync(mainBuffer);
        asyncData.setBuffer(dynamicBuffer);

        assertEquals(2, asyncData.gameData().getGroundHeight(10, 20));
        assertTrue(asyncData.gameData().isWalkable(100, 200));
        assertFalse(asyncData.gameData().isWalkable(100, 201));
        assertTrue(asyncData.gameData().isBuildable(15, 25));
        assertEquals(42, asyncData.gameData().getMapTileRegionId(5, 6));
        assertEquals((short) 0xAAAA, asyncData.gameData().getMapSplitTilesMiniTileMask(3));
        assertEquals(11, asyncData.gameData().getMapSplitTilesRegion1(3));
        assertEquals(22, asyncData.gameData().getMapSplitTilesRegion2(3));
        assertEquals(7, asyncData.gameData().getRegionCount());
        assertEquals(99, asyncData.gameData().getRegions(2).getId());
        assertEquals(320, asyncData.gameData().getRegions(2).getCenter_x());
        assertEquals(240, asyncData.gameData().getRegions(2).getCenter_y());
        assertTrue(asyncData.gameData().getRegions(2).isAccessible());
        assertTrue(asyncData.gameData().getRegions(2).isHigherGround());
    }

    @Test
    public void clientDataAsyncReadsDynamicDataWithPackedOffsets() {
        WrappedBuffer mainBuffer = new WrappedBuffer(ClientData.GameData.SIZE);
        PackedWrappedBuffer packedBuffer = new PackedWrappedBuffer();

        // Setup mock source data in mainBuffer
        ClientData mainData = new ClientData();
        mainData.setBuffer(mainBuffer);

        // Range 1 (units, frameCount)
        mainData.gameData().setFrameCount(1234);
        mainData.gameData().getUnits(0).setHitPoints(40);
        mainData.gameData().getUnits(0).setPositionX(640);
        mainData.gameData().getUnits(0).setPositionY(480);

        // Range 2 (dynamic tiles: visible, explored, creep, occupied)
        mainData.gameData().setIsVisible(30, 40, true);
        mainData.gameData().setIsExplored(31, 41, true);
        mainData.gameData().setHasCreep(32, 42, true);
        mainData.gameData().setIsOccupied(33, 43, true);

        // Range 3 (dynamic game state, events, eventStrings)
        mainData.gameData().setStartLocationCount(4);
        mainData.gameData().getStartLocations(1).setX(128);
        mainData.gameData().getStartLocations(1).setY(256);
        mainData.gameData().setIsInGame(true);
        mainData.gameData().setSelf(0);
        mainData.gameData().setEnemy(1);
        mainData.gameData().setEventCount(2);
        mainData.gameData().getEvents(0).setType(EventType.MatchStart);
        mainData.gameData().getEvents(1).setType(EventType.UnitDiscover);
        mainData.gameData().getEvents(1).setV1(5);
        mainData.gameData().setEventStringCount(1);
        mainData.gameData().setEventStrings(0, "Hello BWAPI");

        // Use FrameBuffer.copyBuffer to pack dynamic data from mainBuffer into packedBuffer
        BWClientConfiguration config = new BWClientConfiguration.Builder().withAsyncFrameBufferCapacity(1).build();
        FrameBuffer frameBuffer = new FrameBuffer(config);
        frameBuffer.copyBuffer(mainBuffer, packedBuffer);

        // Verify ClientDataAsync reads packed dynamic data accurately
        ClientDataAsync asyncData = new ClientDataAsync(mainBuffer);
        asyncData.setBuffer(packedBuffer);

        // Range 1
        assertEquals(1234, asyncData.gameData().getFrameCount());
        assertEquals(40, asyncData.gameData().getUnits(0).getHitPoints());
        assertEquals(640, asyncData.gameData().getUnits(0).getPositionX());
        assertEquals(480, asyncData.gameData().getUnits(0).getPositionY());

        // Range 2
        assertTrue(asyncData.gameData().isVisible(30, 40));
        assertFalse(asyncData.gameData().isVisible(30, 41));
        assertTrue(asyncData.gameData().isExplored(31, 41));
        assertTrue(asyncData.gameData().getHasCreep(32, 42));
        assertTrue(asyncData.gameData().isOccupied(33, 43));

        // Range 3
        assertEquals(4, asyncData.gameData().getStartLocationCount());
        assertEquals(128, asyncData.gameData().getStartLocations(1).getX());
        assertEquals(256, asyncData.gameData().getStartLocations(1).getY());
        assertTrue(asyncData.gameData().isInGame());
        assertEquals(0, asyncData.gameData().getSelf());
        assertEquals(1, asyncData.gameData().getEnemy());
        assertEquals(2, asyncData.gameData().getEventCount());
        assertEquals(EventType.MatchStart, asyncData.gameData().getEvents(0).getType());
        assertEquals(EventType.UnitDiscover, asyncData.gameData().getEvents(1).getType());
        assertEquals(5, asyncData.gameData().getEvents(1).getV1());
        assertEquals(1, asyncData.gameData().getEventStringCount());
        assertEquals("Hello BWAPI", asyncData.gameData().getEventStrings(0));
    }

    @Test
    public void clientDataAsyncWorksWithFullUnpackedBuffer() {
        // Tests asyncUnsafe mode where bot is placed directly on the full 33MB liveData buffer
        WrappedBuffer fullBuffer = new WrappedBuffer(ClientData.GameData.SIZE);

        ClientData syncData = new ClientData();
        syncData.setBuffer(fullBuffer);
        syncData.gameData().setIsVisible(10, 20, true);
        syncData.gameData().setIsInGame(true);

        ClientDataAsync asyncData = new ClientDataAsync(fullBuffer);
        asyncData.setBuffer(fullBuffer); // Unpacked full buffer

        assertTrue(asyncData.gameData().isVisible(10, 20));
        assertTrue(asyncData.gameData().isInGame());
    }

    @Test
    public void copyBufferTracksMaxUnitsAndOnlyCopiesActiveUnitsAndEvents() {
        WrappedBuffer mainBuffer = new WrappedBuffer(ClientData.GameData.SIZE);
        PackedWrappedBuffer packedBuffer = new PackedWrappedBuffer();

        ClientData mainData = new ClientData();
        mainData.setBuffer(mainBuffer);

        // Set initialUnitCount = 3 and populate unit #2 and unit #50 (beyond initialUnitCount)
        mainData.gameData().setInitialUnitCount(3);
        mainData.gameData().getUnits(2).setHitPoints(100);
        mainData.gameData().getUnits(50).setHitPoints(250);

        // Populate event #0 (active) and event #5 (beyond eventCount = 1)
        mainData.gameData().setEventCount(1);
        mainData.gameData().getEvents(0).setType(EventType.MatchFrame);
        mainData.gameData().getEvents(5).setType(EventType.MatchEnd);

        FrameBuffer frameBuffer = new FrameBuffer(
                new BWClientConfiguration.Builder().withAsyncFrameBufferCapacity(1).build());
        frameBuffer.copyBuffer(mainBuffer, packedBuffer);

        ClientDataAsync asyncData = new ClientDataAsync(mainBuffer);
        asyncData.setBuffer(packedBuffer);

        assertEquals(3, frameBuffer.getMaxUnitCount());
        assertEquals(100, asyncData.gameData().getUnits(2).getHitPoints());
        // Unit #50 and Event #5 were beyond active counts, so they were skipped
        assertEquals(0, asyncData.gameData().getUnits(50).getHitPoints());
        assertEquals(EventType.MatchFrame, asyncData.gameData().getEvents(0).getType());
        assertEquals(EventType.MatchStart, asyncData.gameData().getEvents(5).getType());

        // Now fire UnitDiscover for unit #50 on the next frame
        mainData.gameData().setEventCount(1);
        mainData.gameData().getEvents(0).setType(EventType.UnitDiscover);
        mainData.gameData().getEvents(0).setV1(50);
        frameBuffer.copyBuffer(mainBuffer, packedBuffer);

        assertEquals(51, frameBuffer.getMaxUnitCount());
        assertEquals(250, asyncData.gameData().getUnits(50).getHitPoints());
    }
}
