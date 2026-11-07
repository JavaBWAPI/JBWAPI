package bwapi;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BulletTest {

    @Test
    public void isVisibleShouldFallbackToAnyPlayerWhenSelfIsNullAndCheckValidPlayerId() {
        Game game = mock(Game.class);
        when(game.self()).thenReturn(null);

        ClientData.BulletData bulletData = mock(ClientData.BulletData.class);
        Bullet bullet = new Bullet(bulletData, 0, game);

        assertFalse(bullet.isVisible());

        when(bulletData.isVisible(2)).thenReturn(true);
        assertTrue(bullet.isVisible());
        assertTrue(bullet.isVisible(null));

        ClientData.PlayerData neutralData = mock(ClientData.PlayerData.class);
        when(neutralData.getName()).thenReturn("Neutral");
        Player neutral = new Player(neutralData, 11, game);
        when(bulletData.isVisible(11)).thenReturn(true);
        assertFalse(bullet.isVisible(neutral));
    }
}
