package bwapi;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TextTest {

    @Test
    public void textFormattingTest() {
        assertEquals("\u0006Red", Text.formatText("Red", Text.Red));
        assertEquals("\u0006Red and \u000EBlue then \u0002Default", Game.formatString("%cRed and %cBlue then %cDefault", Text.Red, Text.Blue, Text.Default));
    }
}
