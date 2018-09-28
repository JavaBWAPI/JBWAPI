package game;

import bwapi.BWClient;
import bwapi.DefaultBWListener;
import bwapi.Game;

class DrawTest extends DefaultBWListener {
    final BWClient bwClient;

    Game game;

    DrawTest() {
        bwClient = new BWClient(this);
        bwClient.startGame();
    }

    public void onStart() {
        game = bwClient.getGame();

        //TODO
    }

    public static void main(String[] args) {
        new DrawTest();
    }
}
