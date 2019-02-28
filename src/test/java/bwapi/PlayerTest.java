package bwapi;

import org.junit.Test;

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
}
