import bwapi.*;
import static bwapi.Color.*;
import static bwapi.UnitType.*;

public class BuildingPlacer extends DefaultBWListener {

    final BWClient bwClient;

    Game game;

    BuildingPlacer() {
        bwClient = new BWClient(this);
        bwClient.startGame();
    }
    @Override
    public void onStart() {
        game = bwClient.getGame();
        System.out.println("seed:" + game.getRandomSeed());
        System.out.println("StartPosition: " + game.self().getStartLocation());
    }

    @Override
    public void onFrame() {
        TilePosition start = game.self().getStartLocation();
        game.drawBoxMap(start.toPosition(), start.add(new TilePosition(1, 1)).toPosition(), Red);
        TilePosition tp = game.getBuildLocation(Protoss_Pylon, start );
        System.out.println("chosen: " + tp);
        game.drawBoxMap(tp.toPosition(), tp.add(new TilePosition(1, 1)).toPosition(), Purple);
    }

    public static void main(String[] args) {
        new BuildingPlacer();
    }
}
