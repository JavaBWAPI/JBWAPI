package bwapi;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PlayerTest {

    @Test
    public void neutralIsNotEnemy() {
        Player neutral = mock(Player.class);
        Player self = mock(Player.class);

        when(neutral.isNeutral()).thenReturn(true);
        when(self.isEnemy(neutral)).thenCallRealMethod();

        assertFalse(self.isEnemy(neutral));
    }

    @Test
    public void getTextColorShouldReturnDarkGreenForColorGreen() {
        Player player = mock(Player.class);
        when(player.getColor()).thenReturn(Color.Green); // Color(117)
        when(player.getTextColor()).thenCallRealMethod();

        assertEquals(Text.DarkGreen, player.getTextColor());
    }

    @Test
    public void isEnemyAndIsNeutralShouldDelegateToPlayerData() {
        Game game = mock(Game.class);
        ClientData.PlayerData selfData = mock(ClientData.PlayerData.class);
        ClientData.PlayerData otherData = mock(ClientData.PlayerData.class);
        when(selfData.getName()).thenReturn("Self");
        when(otherData.getName()).thenReturn("Other");
        when(selfData.isParticipating()).thenReturn(true);
        when(otherData.isParticipating()).thenReturn(true);
        // Neither ally nor enemy in PlayerData:
        when(selfData.isAlly(1)).thenReturn(false);
        when(selfData.isEnemy(1)).thenReturn(false);

        Player self = new Player(selfData, 0, game);
        Player other = new Player(otherData, 1, game);

        assertFalse(self.isEnemy(other));

        when(selfData.isEnemy(1)).thenReturn(true);
        assertTrue(self.isEnemy(other));

        when(otherData.isNeutral()).thenReturn(true);
        assertTrue(other.isNeutral());
    }

    @Test
    public void supplyTotalAndSupplyUsedShouldHandleRaceNoneAndInvalidRace() {
        Game game = mock(Game.class);
        ClientData.PlayerData playerData = mock(ClientData.PlayerData.class);
        when(playerData.getName()).thenReturn("Self");
        when(playerData.getRace()).thenReturn(Race.Terran.id);
        when(playerData.getSupplyTotal(Race.Terran.id)).thenReturn(40);
        when(playerData.getSupplyUsed(Race.Terran.id)).thenReturn(24);

        Player player = new Player(playerData, 0, game);

        assertEquals(40, player.supplyTotal(Race.None));
        assertEquals(24, player.supplyUsed(Race.None));
        assertEquals(0, player.supplyTotal(Race.Unknown));
        assertEquals(0, player.supplyUsed(Race.Unknown));
    }
}
