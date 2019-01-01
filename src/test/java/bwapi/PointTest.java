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
}
