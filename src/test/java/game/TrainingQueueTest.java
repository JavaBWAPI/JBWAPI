package game;

import bwapi.*;

import java.util.List;
import java.util.stream.Collectors;

class TrainingQueueTest extends DefaultBWListener {
    final BWClient bwClient;
    Game game;
    int mineralsReq = 250;
    int workers = 1;

    TrainingQueueTest() {
        bwClient = new BWClient(this);
        bwClient.startGame();
    }

    public void onStart() {
        game = bwClient.getGame();
        game.enableFlag(Flag.UserInput);
        game.setLocalSpeed(0);
        //game.setLatCom(false);
    }

    @Override
    public void onFrame() {
        List<Unit> minerals = game.getMinerals().stream().filter(Unit::isVisible).collect(Collectors.toList());
        if (game.self().minerals() >= 230) {
            game.setLocalSpeed(20);
        }
        for (Unit unit : game.self().getUnits()) {
            UnitType type = unit.getType();

            if (type.isWorker() && unit.isIdle()) {
                unit.gather(minerals.get(unit.getID() % minerals.size()));
            }
            else if (type.isResourceDepot() && game.self().minerals() >= mineralsReq) {
                System.out.println("######## FRAME " + game.getFrameCount() + " ########");
                unit.train(game.self().getRace().getWorker());
                System.out.println("training worker: " + workers);
                mineralsReq -= 50;
                workers += 1;
                System.out.println("trainingQueue: " + unit.getTrainingQueue());
            }
        }
    }

    public static void main(String[] args) {
        new TrainingQueueTest();
    }
}