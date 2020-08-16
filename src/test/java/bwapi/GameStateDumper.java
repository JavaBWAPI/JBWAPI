package bwapi;

import bwem.BWEM;
import bwem.BWMap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.zip.DeflaterOutputStream;

public class GameStateDumper extends DefaultBWListener{
    static BWClient client;

    Game game;
    String name;

    @Override
    public void onStart() {
        game = client.getGame();
        name = "src/test/resources/" + game.mapFileName();

        try {
            dumpBuffer(name + "_frame" + game.getFrameCount());
        } catch (IOException e) {
            e.printStackTrace();
        }
        BWEM bwem = new BWEM(game);
        bwem.initialize();
        BWMap map = bwem.getMap();
        System.out.println("mapData.put(\"" + game.mapFileName() + "\", new BWEMMap("
                + map.getAreas().size() + ", "
                + map.getBases().size() + ", "
                + map.getChokePoints().size() + "));");
    }

    private void dumpBuffer(String name) throws IOException {
        ByteBuffer buf = client.getClient().mapFile();
        buf.rewind();
        byte[] bytearr = new byte[buf.remaining()];
        buf.get(bytearr);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        DeflaterOutputStream zout = new DeflaterOutputStream(out);
        zout.write(bytearr);
        zout.flush();
        zout.close();
        byte[] compressed = out.toByteArray();
        File file = new File(name +"_buffer.bin");
        FileOutputStream fos = new FileOutputStream(file, false);
        fos.write(compressed);
        fos.close();
    }

    public static void main(String[] args) {
        client = new BWClient(new GameStateDumper());
        client.startGame();
    }
}
