package bwapi;

import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;

import static com.sun.javafx.fxml.expression.Expression.greaterThan;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.hamcrest.MatcherAssert.*;

public class SynchronizationEnvironment {
    BWClientConfiguration configuration;
    BWClient bwClient;
    private BWEventListener listener;
    private Client client;
    private int onEndFrame;
    private Map<Integer, Runnable> onFrames;

    SynchronizationEnvironment() {
        configuration = new BWClientConfiguration();
        listener = mock(BWEventListener.class);
        client = mock(Client.class);
        bwClient = new BWClient(listener);
        bwClient.setClient(client);
        onEndFrame = -1;
        onFrames = new HashMap<>();

        when(client.mapFile()).thenReturn(GameBuilder.binToBufferUnchecked(GameBuilder.DEFAULT_BUFFER_PATH));
        when(client.clientData()).thenReturn(new ClientData());
        client.clientData().setBuffer(client.mapFile());
        client.clientData().gameData().setFrameCount(-1);
        client.clientData().gameData().setIsInGame(false);

        when(client.isConnected()).thenReturn(true);
        doAnswer(answer -> {
            clientUpdate();
            return null;
        }).when(client).update();
        doAnswer(answer -> {
            System.out.println("onStart()");
            return null;
        }).when(listener).onStart();
        doAnswer(answer -> {
            System.out.println("onEnd()");
            return null;
        }).when(listener).onEnd(anyBoolean());
        doAnswer(answer -> {
            System.out.println("onFrame()");
            if (onFrames.containsKey(liveFrame())) {
                onFrames.get(liveFrame()).run();
            }
            return null;
        }).when(listener).onFrame();
    }

    ClientData.GameData liveGameData() {
        return client.clientData().gameData();
    }

    void onFrame(Integer frame, Runnable runnable) {
        onFrames.put(frame, runnable);
        onEndFrame = Math.max(onEndFrame, frame + 1);
    }

    void runGame() {
        if (configuration.async) {
            final long MEGABYTE = 1024 * 1024;
            long memoryFree = Runtime.getRuntime().freeMemory() / MEGABYTE;
            long memoryRequired = configuration.asyncFrameBufferSize * ClientData.GameData.SIZE / MEGABYTE;
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
        return client.clientData().gameData().getFrameCount();
    }

    private void clientUpdate() {
        client.clientData().gameData().setFrameCount(liveFrame() + 1);
        System.out.println("clientUpdate() to liveFrame #" + liveFrame());
        if (liveFrame() == 0) {
            client.clientData().gameData().setIsInGame(true);
            client.clientData().gameData().setEventCount(2);
            client.clientData().gameData().getEvents(0).setType(EventType.MatchStart);
            client.clientData().gameData().getEvents(1).setType(EventType.MatchFrame);
        } else if (liveFrame() < onEndFrame) {
            client.clientData().gameData().setIsInGame(true);
            client.clientData().gameData().setEventCount(1);
            client.clientData().gameData().getEvents(0).setType(EventType.MatchFrame);
        } else if (liveFrame() == onEndFrame) {
            client.clientData().gameData().setIsInGame(true);
            client.clientData().gameData().getEvents(0).setType(EventType.MatchEnd);
        } else {
            client.clientData().gameData().setIsInGame(false);
            client.clientData().gameData().setEventCount(0);
        }
    }
}
