import bwapi.*;
import bwapi.types.UnitType;

import java.util.Comparator;
import java.util.PriorityQueue;

class PlayTestListener extends DefaultBWListener {
    final BWClient bwClient;

    Game game;

    PlayTestListener() {
        bwClient = new BWClient(this);
        bwClient.startGame();
    }

    public void onStart() {
        game = bwClient.getGame();

        game.setLocalSpeed(20);
    }

    public void onFrame() {
        final Player self = game.self();
        // find the depot
        final Unit depot = self.getUnits().stream()
                .filter(u -> u.getType().isResourceDepot())
                .findFirst().get();

        // train workers
        if (depot.isIdle()) {
            depot.train(self.getRace().getWorker());
        }


        //find accessible minerals
        final PriorityQueue<Unit> minerals = new PriorityQueue<>(Comparator.comparingInt(a -> a.getDistance(depot)));

        minerals.addAll(game.getMinerals());

        self.getUnits().stream()
                .filter(u -> u.getType().isWorker() && u.isIdle())
                .forEach(u -> u.gather(minerals.poll()));
    }

    public static void main(String[] args) {
        new PlayTestListener();
    }
}