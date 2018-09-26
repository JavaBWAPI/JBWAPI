package game;
import bwapi.BWClient;
import bwapi.DefaultBWListener;
import bwapi.Game;
import bwapi.Region;


class RegionTest extends DefaultBWListener {
    final BWClient bwClient;

    Game game;

    RegionTest() {
        bwClient = new BWClient(this);
        bwClient.startGame();
    }

    public void onStart() {
        game = bwClient.getGame();

        for (final Region region : game.getAllRegions()) {
            System.out.println(region.getID() + ": {(" + region.getBoundsLeft() + ", " + region.getBoundsTop() + "), (" + region.getBoundsRight() + ", " + region.getBoundsBottom() + ")}, neighbours: " + region.getNeighbors().size());
        }
    }

    public static void main(String[] args) {
        new RegionTest();
    }
}
