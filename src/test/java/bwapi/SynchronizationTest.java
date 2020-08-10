package bwapi;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import net.bytebuddy.implementation.bytecode.Addition;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.Mockito;

public class SynchronizationTest {

    class Environment {
        BWClientConfiguration configuration = new BWClientConfiguration();
        BWEventListener listener = mock(BWEventListener.class);
        Client client = mock(Client.class);

        Game game;
        public Environment() {
            try {
                game = GameBuilder.createGame();
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
            Mockito.doAnswer(answer -> { stepFrame(); return null; }).when(client).update();
        }

        public void stepFrame() {
            game.clientData().gameData().setFrameCount(game.clientData().gameData().getFrameCount() + 1);
        }
    }

    @Test
    public void synchronizedRuns() {
        Environment environment = new Environment();
        environment.configuration.async = false;
    }

}
