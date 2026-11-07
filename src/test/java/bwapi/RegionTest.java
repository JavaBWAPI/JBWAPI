package bwapi;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RegionTest {

    @Test
    public void updateNeighboursShouldDetermineAccessibilityByComparingRegionGroupId() {
        Game game = mock(Game.class);

        ClientData.RegionData originData = mock(ClientData.RegionData.class);
        when(originData.getId()).thenReturn(0);
        when(originData.islandID()).thenReturn(10);
        when(originData.getCenter_x()).thenReturn(100);
        when(originData.getCenter_y()).thenReturn(100);
        when(originData.isAccessible()).thenReturn(true);
        when(originData.getNeighborCount()).thenReturn(2);
        when(originData.getNeighbors(0)).thenReturn(1);
        when(originData.getNeighbors(1)).thenReturn(2);

        // Neighbor 1 is closer and walkable (isAccessible == true), but on a different island/group
        ClientData.RegionData diffGroupData = mock(ClientData.RegionData.class);
        when(diffGroupData.getId()).thenReturn(1);
        when(diffGroupData.islandID()).thenReturn(20);
        when(diffGroupData.getCenter_x()).thenReturn(120);
        when(diffGroupData.getCenter_y()).thenReturn(100);
        when(diffGroupData.isAccessible()).thenReturn(true);

        // Neighbor 2 is farther, but in the same region group
        ClientData.RegionData sameGroupData = mock(ClientData.RegionData.class);
        when(sameGroupData.getId()).thenReturn(2);
        when(sameGroupData.islandID()).thenReturn(10);
        when(sameGroupData.getCenter_x()).thenReturn(200);
        when(sameGroupData.getCenter_y()).thenReturn(100);
        when(sameGroupData.isAccessible()).thenReturn(true);

        Region origin = new Region(originData, game);
        Region diffGroupNeighbor = new Region(diffGroupData, game);
        Region sameGroupNeighbor = new Region(sameGroupData, game);

        when(game.getRegion(1)).thenReturn(diffGroupNeighbor);
        when(game.getRegion(2)).thenReturn(sameGroupNeighbor);

        origin.updateNeighbours();

        assertEquals(sameGroupNeighbor, origin.getClosestAccessibleRegion());
        assertEquals(diffGroupNeighbor, origin.getClosestInaccessibleRegion());
    }
}
