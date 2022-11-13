package bwapi;

import bwem.BWEM;
import bwem.BWMap;
import org.junit.Test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class BWEMTest {
    static class BWEMMap{
        int areas;
        int bases;
        int chokes;

        BWEMMap(BWMap bwMap) {
            this(bwMap.getAreas().size(), bwMap.getBases().size(), bwMap.getChokePoints().size());
        }

        BWEMMap(int areas, int bases, int chokes) {
            this.areas = areas;
            this.bases = bases;
            this.chokes = chokes;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            BWEMMap bwemMap = (BWEMMap) o;

            if (areas != bwemMap.areas) return false;
            if (bases != bwemMap.bases) return false;
            return chokes == bwemMap.chokes;
        }

        @Override
        public int hashCode() {
            int result = areas;
            result = 31 * result + bases;
            result = 31 * result + chokes;
            return result;
        }
    }

    static Map<String, BWEMMap> mapData = new HashMap<>();
    static {
        mapData.put("(2)Benzene.scx", new BWEMMap(20, 12, 29));
        mapData.put("(2)Destination.scx", new BWEMMap(16, 12, 25));
        mapData.put("(2)Heartbreak Ridge.scx", new BWEMMap(23, 11, 32));
        mapData.put("(3)Neo Moon Glaive.scx", new BWEMMap(28, 12, 35));
        mapData.put("(3)Tau Cross.scx", new BWEMMap(21, 12, 21));
        mapData.put("(4)Andromeda.scx", new BWEMMap(30, 17, 33));
        mapData.put("(4)Circuit Breaker.scx", new BWEMMap(31, 16, 40));
        mapData.put("(4)Electric Circuit.scx", new BWEMMap(37, 16, 52));
        mapData.put("(4)Empire of the Sun.scm", new BWEMMap(33, 14, 40));
        mapData.put("(4)Fighting Spirit.scx", new BWEMMap(25, 13, 29));
        mapData.put("(4)Icarus.scm", new BWEMMap(24, 12, 23));
        mapData.put("(4)Jade.scx", new BWEMMap(26, 12, 33));
        mapData.put("(4)La Mancha1.1.scx", new BWEMMap(22, 12, 26));
        mapData.put("(4)Python.scx", new BWEMMap(13, 14, 10));
        mapData.put("(4)Roadrunner.scx", new BWEMMap(26, 12, 35));
    }

    @Test
    public void checkSSCAITMaps() throws IOException {
        for (String mapName : mapData.keySet()) {
            Game game = GameBuilder.createGame(mapName);
            BWEM bwem = new BWEM(game);
            bwem.initialize();
            assertEquals(new BWEMMap(bwem.getMap()), mapData.get(mapName));
        }
    }
}
