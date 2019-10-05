import bwapi.*;
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
            System.err.println(e);
            System.out.println(
                    Arrays
                            .stream(e.getStackTrace())
                            .map(StackTraceElement::toString)
                            .collect(Collectors.joining("\n"))
            );
        }

//        if (MapKnowledge.knowledge.containsKey(mapHash)) {
//            MapData expected = MapKnowledge.knowledge.get(mapHash);
//            if ( ! expected.equals(mapData)) {
//                //TODO Write to file here
//                System.out.println("Expected: " + expected + ", but got: " + mapData);
//            }
//            else {
//                // Success, (probably) nothing broke.
//            }

        System.out.println("New map [" + mapHash + "]: " + mapData);
        game.leaveGame();
    }

    public static void main(String[] args) {
        client = new BWClient(new MapTester());
        client.startGame();
    }

    class MapData {
        private final String name;
        private final int startLocationsAmount;
        private final int areaAmount;
        private final int basesAmount;
        private final int chokeAmount;

        MapData(String name, int startLocationsAmount, int areaAmount, int basesAmount, int chokeAmount) {
            this.name = name;
            this.startLocationsAmount = startLocationsAmount;
            this.areaAmount = areaAmount;
            this.basesAmount = basesAmount;
            this.chokeAmount = chokeAmount;
        }

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MapData mapData = (MapData) o;
            return startLocationsAmount == mapData.startLocationsAmount &&
                    areaAmount == mapData.areaAmount &&
                    basesAmount == mapData.basesAmount &&
                    chokeAmount == mapData.chokeAmount &&
                    name.equals(mapData.name);
        }

        public String toString() {
            return "MapData{" +
                    "name='" + name + '\'' +
                    ", startLocationsAmount=" + startLocationsAmount +
                    ", areaAmount=" + areaAmount +
                    ", basesAmount=" + basesAmount +
                    ", chokeAmount=" + chokeAmount +
                    '}';
        }
    }
}