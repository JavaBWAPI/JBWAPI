package game;

import bwapi.BWClient;
import bwapi.DefaultBWListener;
import bwapi.Game;

class GameTest extends DefaultBWListener {
    final BWClient bwClient;

    Game game;

    GameTest() {
        bwClient = new BWClient(this);
        bwClient.startGame();
    }

    public void onStart() {
        game = bwClient.getGame();
        assert game != null;
        assert game.isLatComEnabled();
        game.setLatCom(false);
        assert !game.isLatComEnabled();
    }

    public static void main(String[] args) {
        new GameTest();
    }
}
