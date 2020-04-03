package bwapi;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;


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
}
