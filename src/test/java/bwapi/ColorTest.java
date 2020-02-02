package bwapi;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ColorTest {

    @Test
    public void checkBlackColor() {
        Color c = Color.Black;
        assertEquals(0, c.red());
        assertEquals(0, c.green());
        assertEquals(0, c.blue());

        assertEquals(new Color(0, 0,0 ), c);
    }
}
