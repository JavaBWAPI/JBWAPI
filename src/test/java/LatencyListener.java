import bwapi.*;

import java.util.Random;

public class LatencyListener extends DefaultBWListener {
    BWClient client;
    Random random;
    Game game;
    Player self;
    Unit[] minerals;
    Unit[] workers;
    Unit depot;

    public void onStart() {
        game = client.getGame();
        self = game.self();
        random = new Random();

        game.setLatCom(true);
        System.out.println("latcom: " + game.isLatComEnabled());
        System.out.println("latcom frames: " + game.getLatencyFrames());

        minerals = game.getMinerals().stream().filter(Unit::isVisible).toArray(Unit[]::new);
        workers = game.getAllUnits().stream().filter(u -> u.getPlayer().equals(self) && u.getType().isWorker()).toArray(Unit[]::new);
        depot = game.getAllUnits().stream().filter(u -> u.getPlayer().equals(self) && u.getType().isResourceDepot()).findFirst().get();

        for (Unit worker: workers) {
            worker.gather(minerals[random.nextInt(minerals.length)]);
        }
        System.out.println("mineralsAmout: " + self.minerals());
        depot.train(self.getRace().getWorker());
    }

    public void onFrame() {
        if (game.getFrameCount() > 2) System.exit(0);
        System.out.println("FRAME: " + game.getFrameCount());
        for (Unit worker: workers) {
            System.out.println("Worker " + worker.getID());
            System.out.println("idle: " + worker.isIdle());
            System.out.println("order: " + worker.getOrder());
            System.out.println("orderTargetId: " + worker.getOrderTarget());
        }
        System.out.println("Depot: " + depot.getID());
        System.out.println("idle: " + depot.isIdle());
        System.out.println("order: " + depot.getOrder());
        System.out.println("orderTargetId: " + depot.getOrderTarget());
        System.out.println("minerals: " + self.minerals());

    }

    public static void main(String[] args) {
        LatencyListener bot = new LatencyListener();
        bot.client = new BWClient(bot);
        bot.client.startGame();
    }
}