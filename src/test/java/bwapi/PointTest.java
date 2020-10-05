package bwapi;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class PointTest {
	
	@Test
	public void pointEqualsTest() {
		assertEquals(new TilePosition(1, 1), new TilePosition(1, 1));

		assertNotEquals(new TilePosition(1, 1), new TilePosition(1, 2));
		assertNotEquals(new TilePosition(1, 1), new TilePosition(2, 1));
		assertNotEquals(new TilePosition(1, 1), new TilePosition(2, 2));

		assertNotEquals(new TilePosition(1, 1), new Position(1, 1));

		assertNotEquals(new TilePosition(1, 1), null);
	}

	@Test
	public void pointDistanceAccesibleTest() {
		TilePosition tp = TilePosition.Origin;

		assertEquals(0, tp.getApproxDistance(tp));
		assertEquals(0, tp.getDistance(tp), 0.001);
		assertEquals(0 , tp.getLength(), 0.001);
	}

	@Test
	public void alternativeConstructorTest() {
		TilePosition tp = new TilePosition(23, 7);

		assertEquals(tp, new TilePosition(new Position(tp)));
		assertEquals(tp, new TilePosition(new WalkPosition(tp)));
		assertEquals(tp, new TilePosition(tp));

		Position p = new Position(97, 43);
		assertEquals(p.toTilePosition(), new TilePosition(p)); // P -> TP loses precision
		assertEquals(p.toWalkPosition(), new WalkPosition(p));
		assertEquals(p, new Position(p));

		WalkPosition wp = new WalkPosition(41, 22);
		assertEquals(wp, new WalkPosition(new Position(wp)));
		assertEquals(wp.toTilePosition(), new TilePosition(wp));
		assertEquals(wp, new WalkPosition(wp));
	}

	@Test
	public void isValidChecks() {
		Game game = new Game();
		game.botClientData().setBuffer(GameBuilder.binToBufferUnchecked(GameBuilder.DEFAULT_BUFFER_PATH));
		game.botClientData().gameData().setMapWidth(256);
		game.botClientData().gameData().setMapHeight(256);
		game.init();

		assertEquals(256, game.mapHeight());
		assertEquals(256, game.mapWidth());
		assertEquals(32 * 256, game.mapPixelHeight());
		assertEquals(32 * 256, game.mapPixelWidth());
		assertTrue(new Position(0,0).isValid(game));
		assertTrue(new Position(1, 1).isValid(game));

		assertFalse(new Position(-1, -1).isValid(game));
		assertFalse(new Position(1, -1).isValid(game));

		assertTrue(new TilePosition(0, 0).isValid(game));
		assertFalse(new TilePosition(256, 256).isValid(game));
	}

	@Test
	public void randomPointTests() {
		Random r = new Random();
		int bound = 100_000;

		for (int i=0; i < 100; i++) {
			TilePosition tp1 = new TilePosition(r.nextInt(bound), r.nextInt(bound));
			TilePosition tp2 = new TilePosition(tp1);
			assertEquals(tp1, tp2);

			Position p1 = new Position(tp1);
			Position p2 = tp1.toPosition();
			assertEquals(p1, p2);
			assertEquals(tp1.x * 32, p1.x);
			assertEquals(tp1.y * 32 , p2.y);

			WalkPosition wp1 = new WalkPosition(tp1);
			WalkPosition wp2 = tp1.toWalkPosition();
			assertEquals(wp1, wp2);
			assertEquals(tp1.x * 4, wp1.x);
			assertEquals(tp1.y * 4 , wp2.y);

		}

		for (int i=0; i < 100; i++) {
			WalkPosition wp1 = new WalkPosition(r.nextInt(bound), r.nextInt(bound));
			WalkPosition wp2 = new WalkPosition(wp1);
			assertEquals(wp1, wp2);

			Position p1 = new Position(wp1);
			Position p2 = wp1.toPosition();
			assertEquals(p1, p2);
			assertEquals(wp1.x * 8, p1.x);
			assertEquals(wp1.y * 8 , p2.y);

			TilePosition tp1 = new TilePosition(wp1);
			TilePosition tp2 = wp1.toTilePosition();
			assertEquals(tp1, tp2);
			assertEquals(wp1.x / 4, tp1.x);
			assertEquals(wp1.y / 4 , tp2.y);
		}

		for (int i=0; i < 100; i++) {
			Position p1 = new Position(r.nextInt(bound), r.nextInt(bound));
			Position p2 = new Position(p1);
			assertEquals(p1, p2);

			WalkPosition wp1 = new WalkPosition(p1);
			WalkPosition wp2 = p1.toWalkPosition();
			assertEquals(wp1, wp2);
			assertEquals(p1.x / 8, wp1.x);
			assertEquals(p1.y / 8 , wp2.y);

			TilePosition tp1 = new TilePosition(p1);
			TilePosition tp2 = p1.toTilePosition();
			assertEquals(tp1, tp2);
			assertEquals(p1.x / 32, tp1.x);
			assertEquals(p1.y / 32 , tp2.y);
		}
	}
}
