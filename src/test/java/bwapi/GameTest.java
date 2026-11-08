package bwapi;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.FromDataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

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

    @Test
    public void hasPowerOnTile() throws IOException {
        Game game = GameBuilder.createGame();
        Unit pylon = mock(Unit.class);
        given(pylon.getType()).willReturn(UnitType.Protoss_Pylon);
        given(pylon.getPlayer()).willReturn(game.self());
        given(pylon.exists()).willReturn(true);
        given(pylon.isCompleted()).willReturn(true);
        given(pylon.getPosition()).willReturn(new Position(1024, 512));
        game.setAllUnits(Collections.singletonList(pylon));

        // tile (31, 15) is pixel (992, 480), right next to the pylon
        assertTrue(game.hasPower(31, 15));
    }

    @Test
    public void getUnitsOnTileShouldUseUnitBoundingBoxAndValidateTile() {
        Game game = spy(new Game());
        doReturn(128 * 32).when(game).mapPixelWidth();
        doReturn(128 * 32).when(game).mapPixelHeight();

        // Command Center at top-left tile (10, 10) spans pixels [320..447, 320..415] (tiles 10..13, 10..12)
        Unit commandCenter = mock(Unit.class);
        when(commandCenter.getTilePosition()).thenReturn(new TilePosition(10, 10));
        when(commandCenter.getLeft()).thenReturn(320);
        when(commandCenter.getTop()).thenReturn(320);
        when(commandCenter.getRight()).thenReturn(447);
        when(commandCenter.getBottom()).thenReturn(415);

        game.setAllUnits(Collections.singletonList(commandCenter));

        // Tile (11, 11) is covered by the Command Center even though its top-left tile is (10, 10)
        List<Unit> onTile = game.getUnitsOnTile(11, 11);
        assertEquals(Collections.singletonList(commandCenter), onTile);

        // Invalid tile should return empty list
        assertTrue(game.getUnitsOnTile(-1, -1).isEmpty());
    }

    @Test
    public void getUnitsInRadiusShouldUseUnitEdgeDistance() {
        Game game = new Game();

        // Unit centered at (100, 100) with bounding box [80..120, 80..120]
        Unit unit = mock(Unit.class);
        Position unitCenter = new Position(100, 100);
        Position queryCenter = new Position(150, 100);
        when(unit.getPosition()).thenReturn(unitCenter);
        when(unit.getLeft()).thenReturn(80);
        when(unit.getRight()).thenReturn(120);
        when(unit.getTop()).thenReturn(80);
        when(unit.getBottom()).thenReturn(120);
        when(unit.exists()).thenReturn(true);
        when(unit.getDistance(queryCenter)).thenCallRealMethod();

        game.setAllUnits(Collections.singletonList(unit));

        // Center-to-center distance is 50 (> 35), but edge-to-point distance is 30 (<= 35)
        List<Unit> result = game.getUnitsInRadius(queryCenter, 35);
        assertEquals(Collections.singletonList(unit), result);
    }

    @Test
    public void setMapWithEmptyStringShouldReturnFalseInsteadOfThrowing() {
        Game game = new Game();
        assertFalse(game.setMap(""));
    }

    @Test
    public void onFrameShouldRefreshPlayersAndExcludeDefeatedOrLeftPlayers() {
        Game game = spy(new Game());
        Player self = mock(Player.class);
        Player ally = mock(Player.class);
        Player enemy = mock(Player.class);
        Player defeatedEnemy = mock(Player.class);
        Player leftAlly = mock(Player.class);
        Player observer = mock(Player.class);

        when(self.isAlly(ally)).thenReturn(true);
        when(self.isAlly(leftAlly)).thenReturn(true);
        when(self.isEnemy(enemy)).thenReturn(true);
        when(self.isEnemy(defeatedEnemy)).thenReturn(true);

        when(defeatedEnemy.isDefeated()).thenReturn(true);
        when(leftAlly.leftGame()).thenReturn(true);
        when(observer.isObserver()).thenReturn(true);

        doReturn(self).when(game).self();
        doReturn(Arrays.asList(self, ally, enemy, defeatedEnemy, leftAlly, observer)).when(game).getPlayers();
        game.setAllUnits(Collections.emptyList());

        game.onFrame(0);

        assertEquals(Collections.singletonList(ally), game.allies());
        assertEquals(Collections.singletonList(enemy), game.enemies());
        assertEquals(Collections.singletonList(observer), game.observers());

        when(enemy.isDefeated()).thenReturn(true);
        game.onFrame(1);

        assertTrue(game.enemies().isEmpty());
    }

    @Test
    public void unitListCachesShouldBeLazyAndCachedPerFrame() throws IOException {
        Game game = GameBuilder.createGame();
        Player self = game.self();
        Player neutral = game.neutral();

        Unit mineral = mock(Unit.class);
        when(mineral.getType()).thenReturn(UnitType.Resource_Mineral_Field);
        when(mineral.getPlayer()).thenReturn(neutral);

        Unit geyser = mock(Unit.class);
        when(geyser.getType()).thenReturn(UnitType.Resource_Vespene_Geyser);
        when(geyser.getPlayer()).thenReturn(neutral);

        Unit pylon = mock(Unit.class);
        when(pylon.getType()).thenReturn(UnitType.Protoss_Pylon);
        when(pylon.getPlayer()).thenReturn(self);

        game.setAllUnits(Arrays.asList(mineral, geyser, pylon));

        assertEquals(Collections.singletonList(mineral), game.getMinerals());
        assertSame(game.getMinerals(), game.getMinerals());
        assertEquals(Collections.singletonList(geyser), game.getGeysers());
        assertSame(game.getGeysers(), game.getGeysers());
        assertEquals(Arrays.asList(mineral, geyser), game.getNeutralUnits());
        assertSame(game.getNeutralUnits(), game.getNeutralUnits());
        assertEquals(Collections.singletonList(pylon), self.getUnits());
        assertSame(self.getUnits(), self.getUnits());
        assertEquals(Arrays.asList(mineral, geyser), neutral.getUnits());
    }
}