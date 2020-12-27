package bwapi;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Mocks BWAPI and a bot listener, for synchronization tests.
 */
class SynchronizationEnvironment {
    BWClientConfiguration configuration;
    BWClient bwClient;
    private Client client;
    private int onEndFrame;
    private long bwapiDelayMs;
    private Map<Integer, Runnable> onFrames;

    SynchronizationEnvironment() {
        BWEventListener listener = mock(BWEventListener.class);

        configuration = new BWClientConfiguration();
        client = mock(Client.class);
        bwClient = new BWClient(listener);
        bwClient.setClient(client);
        onEndFrame = -1;
        bwapiDelayMs = 0;
        onFrames = new HashMap<>();

        WrappedBuffer newGameState = GameBuilder.binToBufferUnchecked(GameBuilder.DEFAULT_BUFFER_PATH);
        when(client.mapFile()).thenReturn(newGameState);
        when(client.liveClientData()).thenReturn(new ClientData());
        client.liveClientData().setBuffer(client.mapFile());
        client.liveClientData().gameData().setFrameCount(-1);
        client.liveClientData().gameData().setIsInGame(false);

        when(client.isConnected()).thenReturn(true);
        doAnswer(answer -> {
            clientUpdate();
            return null;
        }).when(client).sendFrameReceiveFrame();
        doAnswer(answer -> {
            configuration.log("Test: onStart()");
            return null;
        }).when(listener).onStart();
        doAnswer(answer -> {
            configuration.log("Test: onEnd()");
            return null;
        }).when(listener).onEnd(anyBoolean());
        doAnswer(answer -> {
            configuration.log("Test: onFrame() start");
            int botFrame = bwClient.getGame().getFrameCount();
            if (onFrames.containsKey(botFrame)) {
                onFrames.get(botFrame).run();
            }
            configuration.log("Test: onFrame() end");
            return null;
        }).when(listener).onFrame();
    }

    ClientData.GameData liveGameData() {
        return client.liveClientData().gameData();
    }

    PerformanceMetrics metrics() {
        return bwClient.getPerformanceMetrics();
    }

    void onFrame(Integer frame, Runnable runnable) {
        onFrames.put(frame, runnable);
    }

    void setBwapiDelayMs(long milliseconds) {
        bwapiDelayMs = milliseconds;
    }

    void runGame() {
        runGame(10);
    }

    void runGame(int onEndFrame) {
        this.onEndFrame = onEndFrame;
        if (configuration.getAsync()) {
            final long MEGABYTE = 1024 * 1024;
            long memoryFree = Runtime.getRuntime().freeMemory() / MEGABYTE;
            long memoryRequired = configuration.getAsyncFrameBufferCapacity() * ClientData.GameData.SIZE / MEGABYTE;
            assertTrue(
                    "Unit test needs to be run with sufficient memory to allocate frame buffer. Has "
                            + memoryFree
                            + "mb of "
                            + memoryRequired
                            + "mb required.\n"
                            + "Current JVM arguments: "
                            + ManagementFactory.getRuntimeMXBean().getInputArguments(),
                    memoryFree > memoryRequired);
        }
        bwClient.startGame(configuration);
    }

    private int liveFrame() {
        return client.liveClientData().gameData().getFrameCount();
    }

    private void clientUpdate() throws InterruptedException{
        Thread.sleep(bwapiDelayMs);
        client.liveClientData().gameData().setFrameCount(liveFrame() + 1);
        configuration.log("Test: clientUpdate() to liveFrame #" + liveFrame());
        if (liveFrame() == 0) {
            client.liveClientData().gameData().setIsInGame(true);
            client.liveClientData().gameData().setEventCount(2);
            client.liveClientData().gameData().getEvents(0).setType(EventType.MatchStart);
            client.liveClientData().gameData().getEvents(1).setType(EventType.MatchFrame);
        } else if (liveFrame() < onEndFrame) {
            client.liveClientData().gameData().setIsInGame(true);
            client.liveClientData().gameData().setEventCount(1);
            client.liveClientData().gameData().getEvents(0).setType(EventType.MatchFrame);
        } else if (liveFrame() == onEndFrame) {
            client.liveClientData().gameData().setIsInGame(true);
            client.liveClientData().gameData().getEvents(0).setType(EventType.MatchEnd);
        } else {
            client.liveClientData().gameData().setIsInGame(false);
            client.liveClientData().gameData().setEventCount(0);
        }
    }
}
