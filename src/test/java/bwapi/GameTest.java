package bwapi;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@RunWith(Theories.class)
public class GameTest {

    private final Game sut = new Game();
    private Unit dummy;

    @DataPoints("overlapping")
    public static final Pair<?, ?>[] overlapping = {
            new Pair<>(new Position(15, 35), new Position(20, 40)),
            new Pair<>(new Position(15, 32), new Position(25, 38)),
            new Pair<>(new Position(15, 28), new Position(25, 42)),
            new Pair<>(new Position(5, 35), new Position(15, 40)),
            new Pair<>(new Position(0, 32), new Position(15, 38)),
            new Pair<>(new Position(0, 28), new Position(15, 42)),
            new Pair<>(new Position(12, 25), new Position(22, 35)),
            new Pair<>(new Position(15, 38), new Position(28, 42)),
            new Pair<>(new Position(5, 20), new Position(25, 45))
    };

    @DataPoints("non-overlapping")
    public static final Pair<?, ?>[] nonOverlapping = {
            new Pair<>(new Position(0, 0), new Position(200, 20)),
            new Pair<>(new Position(50, 0), new Position(55, 200)),
            new Pair<>(new Position(0, 0), new Position(5, 200)),
            new Pair<>(new Position(0, 45), new Position(20, 50))
    };

    @Before
    public void setup() {
        dummy = mock(Unit.class);
        given(dummy.getLeft()).willReturn(10);
        given(dummy.getRight()).willReturn(20);
        given(dummy.getTop()).willReturn(30);
        given(dummy.getBottom()).willReturn(40);
    }

    @Theory
    public void shouldFindOverlappingUnits(
            @FromDataPoints("overlapping") Pair<Position, Position> rect) {
        // GIVEN
        sut.setAllUnits(Collections.singletonList(dummy));

        // WHEN
        List<Unit> unitsInRectangle = sut
                .getUnitsInRectangle(rect.getLeft(), rect.getRight(), unused -> true);

        // THEN
        assertThat(unitsInRectangle).contains(dummy);
    }

    @Theory
    public void shouldNotFindNonOverlappingUnits(
            @FromDataPoints("non-overlapping") Pair<Position, Position> rect) {
        // GIVEN
        sut.setAllUnits(Collections.singletonList(dummy));

        // WHEN
        List<Unit> unitsInRectangle = sut
                .getUnitsInRectangle(rect.getLeft(), rect.getRight(), unused -> true);

        // THEN
        assertThat(unitsInRectangle).doesNotContain(dummy);
    }

    @Test
    public void ifReplaySelfAndEnemyShouldBeNull() throws IOException {
        WrappedBuffer buffer = GameBuilder.binToBuffer(GameBuilder.DEFAULT_BUFFER_PATH);

        // modify the buffer to fake a replay
        ClientData clientData = new ClientData();
        clientData.setBuffer(buffer);
        clientData.gameData().setIsReplay(true);

        Game game = new Game();
        game.botClientData().setBuffer(buffer);
        game.init();

        assertTrue(game.isReplay());
        assertNull(game.self());
        assertNull(game.enemy());
        assertTrue(game.enemies().isEmpty());
        assertTrue(game.allies().isEmpty());
    }
}