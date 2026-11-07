package bwapi;

import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BuildingPlacerTest {

    @Test
    public void reserveExistingAddonPlacementShouldUseHalfMaxRangeOffset() {
        Game game = mock(Game.class);
        Player self = mock(Player.class);
        when(game.self()).thenReturn(self);

        TilePosition desired = new TilePosition(40, 40);
        // start should be desired - (64, 64)/2 = (8, 8).
        // Factory at (40, 40) has its 2x2 addon at world tiles (44, 41)..(45, 42),
        // which corresponds to reserve coordinates (36, 33)..(37, 34).
        Unit factory = mock(Unit.class);
        when(factory.exists()).thenReturn(true);
        when(factory.getType()).thenReturn(UnitType.Terran_Factory);
        when(factory.getTilePosition()).thenReturn(new TilePosition(40, 40));
        when(self.getUnits()).thenReturn(Collections.singletonList(factory));

        BuildingPlacer.PlacementReserve reserve = new BuildingPlacer.PlacementReserve(64);
        reserve.setRange(0, 0, 64, 64, (byte) 1);

        BuildingPlacer.ReserveExistingAddonPlacement(reserve, desired, game);

        assertEquals(0, reserve.getValue(36, 33));
        assertEquals(0, reserve.getValue(37, 34));
    }

    @Test
    public void reserveDefaultShouldUsePaddingTwoForFactory() {
        Game game = mock(Game.class);
        Player self = mock(Player.class);
        when(game.self()).thenReturn(self);

        TilePosition desired = new TilePosition(40, 40);
        Unit factory = mock(Unit.class);
        when(factory.exists()).thenReturn(true);
        when(factory.getType()).thenReturn(UnitType.Terran_Factory);
        when(factory.getTilePosition()).thenReturn(new TilePosition(40, 40));
        when(factory.getPosition()).thenReturn(new TilePosition(40, 40).toPosition());
        when(self.getUnits()).thenReturn(Collections.singletonList(factory));

        BuildingPlacer.PlacementReserve reserve = new BuildingPlacer.PlacementReserve(64);
        reserve.setRange(0, 0, 64, 64, (byte) 1);

        BuildingPlacer.ReserveDefault(reserve, UnitType.Terran_Supply_Depot, desired, game);

        // For a 4x3 Factory at (40, 40) (reserve 32, 32) and a 3x2 Supply Depot:
        // padding=1 reserves x in [25, 35), y in [27, 35)
        // padding=2 reserves x in [24, 36), y in [26, 36)
        assertEquals(0, reserve.getValue(24, 26));
        assertEquals(0, reserve.getValue(35, 35));
    }
}
