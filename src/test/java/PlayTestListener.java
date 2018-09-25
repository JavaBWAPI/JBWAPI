import bwapi.BWClient;
import bwapi.DefaultBWListener;
import bwapi.Game;

public class PlayTestListener extends DefaultBWListener {
    final BWClient bwClient;

    Game game;

    PlayTestListener() {
        bwClient = new BWClient(this);
        bwClient.startGame();
    }

    public void onStart() {
        game = bwClient.getGame();
        System.out.println(game.getAllUnits().size());
        System.out.println(game.getStaticMinerals().size());
    }

    public void onFrame() {
        System.out.println(game.getAllUnits().size());
        System.out.println(game.getMinerals().size());
    }

    public static void main(String[] args) {
        new PlayTestListener();
    }
}