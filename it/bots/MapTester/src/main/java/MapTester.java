import bwapi.BWClient;
import bwapi.DefaultBWListener;
import bwapi.Game;
import bwem.BWEM;

import java.util.Arrays;
import java.util.stream.Collectors;


class MapTester extends DefaultBWListener {
    private static BWClient client;

    public void onStart() {
        Game game = MapTester.client.getGame();

        final String mapHash = game.mapHash();

        MapData mapData = null;
        try {
            BWEM bwem = new BWEM(game);
            bwem.initialize();
            bwem.getMap().assignStartingLocationsToSuitableBases();
            mapData = new MapData(
                    game.mapName(),
                    game.getStartLocations().size(),
                    bwem.getMap().getAreas().size(),
                    bwem.getMap().getBases().size(),
                    bwem.getMap().getChokePoints().size()
            );
        }
        catch (Exception e) {
            //TODO Write to file here
            System.out.println(e);
            System.out.println(
                    Arrays
                    .stream(e.getStackTrace())
                    .map(StackTraceElement::toString)
                    .collect(Collectors.joining("\n"))
            );
        }

        if (MapKnowledge.knowledge.containsKey(mapHash)) {
            MapData expected = MapKnowledge.knowledge.get(mapHash);
            if ( ! expected.equals(mapData)) {
                //TODO Write to file here
                System.out.println("Expected: " + expected + ", but got: " + mapData);
            }
            else {
                // Success, (probably) nothing broke.
            }
        }
        else {
            //TODO Write to file here
            System.out.println("New map [" + mapHash + "]: " + mapData);
        }

        game.leaveGame();
    }

    public static void main(String[] args) {
        client = new BWClient(new MapTester());
        client.startGame();
    }
}
