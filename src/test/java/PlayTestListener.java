import bwapi.BWClient;
import bwapi.DefaultBWListener;
import bwapi.Game;
import bwapi.Player;
import bwapi.types.UnitType;

public class PlayTestListener extends DefaultBWListener {
    final BWClient bwClient;

    Game game;

    PlayTestListener() {
        bwClient = new BWClient(this);
        bwClient.startGame();
    }

    public void onStart() {
        game = bwClient.getGame();
        final Player self = game.self();
        self.getUnits().stream().filter(u -> u.getType().isResourceDepot()).forEach(u -> u.train(self.getRace().getWorker()));
        System.out.println(game.getAllUnits().size());

    }

    public void onFrame() {
        System.out.println(game.getAllUnits().size());
        System.out.println(game.getMinerals().size());
    }

    public static void main(String[] args) {
        new PlayTestListener();
    }
}