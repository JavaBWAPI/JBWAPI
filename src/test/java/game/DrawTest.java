package game;

import bwapi.BWClient;
import bwapi.DefaultBWListener;
import bwapi.Game;
import bwapi.values.Color;

class DrawTest extends DefaultBWListener {
    final BWClient bwClient;

    Game game;

    DrawTest() {
        bwClient = new BWClient(this);
        bwClient.startGame();
    }

    public void onStart() {
        game = bwClient.getGame();
    }

    @Override
    public void onFrame() {
        Color color = Color.Purple;
        
        game.drawCircleScreen(200, 200, 10, color);
        game.drawTextScreen(50, 50, "frame: " + game.getFrameCount());
    }

    public static void main(String[] args) {
        new DrawTest();
    }
}
