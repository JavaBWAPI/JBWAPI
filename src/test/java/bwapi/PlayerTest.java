package bwapi;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
}
