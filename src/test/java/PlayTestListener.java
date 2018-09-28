import bwapi.*;
import bwapi.point.Position;
import bwapi.point.TilePosition;
import bwapi.types.Race;
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
        final Race race = self.getRace();

        // find the depot
        final Unit depot = self.getUnits().stream()
                .filter(u -> u.getType().isResourceDepot())
                .findFirst().get();

        // train workers
        if (depot.isIdle()) {
            depot.train(race.getWorker());
        }

        // find accessible minerals
        final PriorityQueue<Unit> minerals = new PriorityQueue<>(Comparator.comparingInt(a -> a.getDistance(depot)));

        Position avgMineralPos = Position.Origin;
        int count = 0;
        for (final Unit mineral : game.getMinerals()) {
            if (mineral.isVisible()) {
                avgMineralPos = avgMineralPos.add(mineral.getInitialPosition());
                count += 1;
                minerals.add(mineral);
            }
        }
        avgMineralPos = avgMineralPos.divide(count);

        // make workers gather minerals if idle
        self.getUnits().stream()
                .filter(u -> u.getType().isWorker() && u.isIdle())
                .forEach(u -> u.gather(minerals.poll()));


        // create extra supply
        if (self.supplyTotal() - self.supplyUsed() <= 2) {
            final UnitType supplyProvider = race.getSupplyProvider();
            if (self.minerals() >= supplyProvider.mineralPrice()) {
                final TilePosition depotTP = depot.getTilePosition();
                final TilePosition avgMinTP = avgMineralPos.toTilePosition();
                final UnitType builderType = supplyProvider.whatBuilds().getKey();
                self.getUnits().stream()
                        .filter(u -> u.getType().equals(builderType) && !u.isCarrying())
                        .findFirst().ifPresent(u -> {
                            if (u.getType().isWorker()) {
                                u.build(supplyProvider, depotTP.add(depotTP.subtract(avgMinTP)));
                            }
                            else {
                                u.train(supplyProvider);
                            }
                });
            }
        }

    }

    public static void main(String[] args) {
        new PlayTestListener();
    }
}