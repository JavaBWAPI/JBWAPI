package bwapi;

import org.junit.Test;

import java.lang.management.BufferPoolMXBean;
import java.lang.management.ManagementFactory;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

/**
 * Guards the order of startup work: attempt the BWAPI connection before creating
 * performance metrics or allocating the large buffers used for asynchronous frames.
 * Measures direct-buffer allocation at a mocked connection boundary, so the test
 * needs neither a running server nor a machine-dependent elapsed-time threshold.
 */
public class BWClientStartupTest {
    // Stops startGame at the mocked connection boundary, before a game loop can run.
    private static class StopAfterConnectionAttempt extends RuntimeException {
    }

    @Test
    public void attemptsConnectionBeforeAllocatingFrameBuffersOrMetrics() {
        BWClient bwClient = new BWClient(new DefaultBWListener());
        Client client = mock(Client.class);
        bwClient.setClient(client);
        BWClientConfiguration configuration = new BWClientConfiguration.Builder()
                .withAsync(true)
                .withAsyncFrameBufferCapacity(3)
                .build();
        BufferPoolMXBean directBuffers = ManagementFactory.getPlatformMXBeans(BufferPoolMXBean.class)
                .stream()
                .filter(pool -> "direct".equals(pool.getName()))
                .findFirst()
                .get();
        long bytesBeforeStart = directBuffers.getMemoryUsed();
        doAnswer(invocation -> {
            assertTrue("Connection attempts must precede allocation of even one game-state buffer",
                    directBuffers.getMemoryUsed() - bytesBeforeStart < ClientData.GameData.SIZE);
            assertNull(bwClient.getPerformanceMetrics());
            throw new StopAfterConnectionAttempt();
        }).when(client).reconnect();

        assertThrows(StopAfterConnectionAttempt.class, () -> bwClient.startGame(configuration));
    }
}
