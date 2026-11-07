package bwapi;

import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

public class CommandTempTest {

    @Test
    public void upgradeShouldSubtractGasInsteadOfAdding() {
        Game game = spy(new Game());
        doReturn(10).when(game).getFrameCount();

        ClientData.PlayerData playerData = mock(ClientData.PlayerData.class);
        when(playerData.getName()).thenReturn("Self");
        when(playerData.getMinerals()).thenReturn(500);
        when(playerData.getGas()).thenReturn(500);
        Player player = new Player(playerData, 0, game);

        Unit unit = spy(new Unit(mock(ClientData.UnitData.class), 0, game));
        doReturn(true).when(unit).exists();
        doReturn(player).when(unit).getPlayer();

        UpgradeType upgrade = UpgradeType.Terran_Infantry_Weapons;
        CommandTemp cmd = new CommandTemp(UnitCommand.upgrade(unit, upgrade), game);
        cmd.execute(false);

        assertEquals(500 - upgrade.mineralPrice(1), player.minerals());
        assertEquals(500 - upgrade.gasPrice(1), player.gas());
    }

    @Test
    public void cancelTrainSlotShouldShiftQueueWithoutIndexOutOfBounds() {
        Game game = spy(new Game());
        doReturn(10).when(game).getFrameCount();

        ClientData.PlayerData playerData = mock(ClientData.PlayerData.class);
        when(playerData.getName()).thenReturn("Self");
        Player player = new Player(playerData, 0, game);

        ClientData.UnitData unitData = mock(ClientData.UnitData.class);
        when(unitData.getExists()).thenReturn(true);
        when(unitData.getTrainingQueueCount()).thenReturn(2);
        when(unitData.getTrainingQueue(0)).thenReturn(UnitType.Terran_Marine.id);
        when(unitData.getTrainingQueue(1)).thenReturn(UnitType.Terran_Medic.id);
        when(unitData.getTrainingQueue(2)).thenReturn(UnitType.None.id);
        when(unitData.getTrainingQueue(3)).thenReturn(UnitType.None.id);
        when(unitData.getTrainingQueue(4)).thenReturn(UnitType.None.id);

        Unit unit = spy(new Unit(unitData, 0, game));
        doReturn(player).when(unit).getPlayer();

        CommandTemp cmd = new CommandTemp(UnitCommand.cancelTrain(unit, 1), game);
        cmd.execute(false);

        assertEquals(Collections.singletonList(UnitType.Terran_Marine), unit.getTrainingQueue());
    }
}
