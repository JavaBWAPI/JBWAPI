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
	public void pointDistanceAccessibleTest() {
		TilePosition tp = TilePosition.Origin;

		assertEquals(0, tp.getApproxDistance(tp));
		assertEquals(0, tp.getDistance(tp), 0.001);
		assertEquals(0 , tp.getLength(), 0.001);
	}
}
