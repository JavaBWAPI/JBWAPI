package bwapi;

import bwem.BWEM;
import bwem.BWMap;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.InflaterOutputStream;

import static org.junit.Assert.assertEquals;

public class BWEMTester {
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

        public boolean equals(Object object) {
            BWEMMap bwemMap = (BWEMMap) object;
            return areas == bwemMap.areas && bases == bwemMap.bases && chokes == bwemMap.chokes;
        }
    }

    static Map<String, BWEMMap> mapData = new HashMap<>();
    static {
        mapData.put("(2)Benzene.scx", new BWEMMap(20, 12, 29));
    }


    Game initGame(String mapName) throws IOException {
        String location = "src/test/resources/2019-12-21_" + mapName + "_frame0_buffer.bin";

        // load bytebuffer
        byte[] compressedBytes = Files.readAllBytes(Paths.get(location));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        InflaterOutputStream zin = new InflaterOutputStream(out);
        zin.write(compressedBytes);
        zin.flush();
        zin.close();
        byte[] bytes = out.toByteArray();
        ByteBuffer buffer = ByteBuffer.allocateDirect(bytes.length);
        buffer.put(bytes);

        Client client = new Client(buffer);
        Game game = new Game(client);
        game.init();
        return game;
    }

    @Test
    public void checkSSCAITMaps() throws IOException {
        for (String mapName : mapData.keySet()) {
            Game game = initGame(mapName);
            BWEM bwem = new BWEM(game);
            bwem.initialize();
            assertEquals(new BWEMMap(bwem.getMap()), mapData.get(mapName));
        }
    }
}
