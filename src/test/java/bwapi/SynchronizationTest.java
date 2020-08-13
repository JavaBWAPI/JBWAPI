package bwapi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

public class SynchronizationTest {

    private void sleepUnchecked(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch(InterruptedException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Test
    public void sync_IfException_ThrowException() throws InterruptedException {
        SynchronizationEnvironment environment = new SynchronizationEnvironment();
        environment.configuration.async = false;
        environment.onFrame(0, () -> { throw new RuntimeException("Simulated bot exception"); });
        assertThrows(RuntimeException.class, environment::runGame);
    }

    @Test
    public void async_IfException_ThrowException() throws InterruptedException {
        // An exception in the bot thread must be re-thrown by the main thread.
        SynchronizationEnvironment environment = new SynchronizationEnvironment();
        environment.configuration.async = true;
        environment.configuration.asyncFrameBufferSize = 3;
        environment.onFrame(0, () -> { throw new RuntimeException("Simulated bot exception"); });
        assertThrows(RuntimeException.class, environment::runGame);
    }

    @Test
    public void sync_IfDelay_ThenNoBuffer() throws InterruptedException {
        SynchronizationEnvironment environment = new SynchronizationEnvironment();
        environment.configuration.async = false;
        environment.configuration.asyncFrameDurationMs = 1;
        environment.configuration.asyncFrameBufferSize = 3;
        IntStream.range(0, 5).forEach(frame -> {
            environment.onFrame(frame, () -> {
                sleepUnchecked(5);
                assertEquals(0, environment.bwClient.framesBehind());
                assertEquals(frame, environment.bwClient.getGame().getFrameCount());
                assertEquals(frame, environment.liveGameData().getFrameCount());
            });
        });

        environment.runGame();
    }

    @Test
    public void async_IfDelay_ThenBuffer() throws InterruptedException {
        SynchronizationEnvironment environment = new SynchronizationEnvironment();
        environment.configuration.async = true;
        environment.configuration.asyncFrameDurationMs = 1;
        environment.configuration.asyncFrameBufferSize = 3;
        environment.onFrame(0, () -> {
            sleepUnchecked(5);
            assertEquals(2, environment.bwClient.framesBehind());
            assertEquals(0, environment.bwClient.getGame().getFrameCount());
            assertEquals(2, environment.liveGameData().getFrameCount());
        });
        environment.runGame();
    }
}
