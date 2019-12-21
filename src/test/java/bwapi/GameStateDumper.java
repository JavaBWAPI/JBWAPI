package bwapi;

import bwem.Area;
import bwem.BWEM;
import bwem.BWMap;
import bwem.Base;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;
import java.util.zip.DeflaterOutputStream;

import static java.util.Calendar.*;

public class GameStateDumper extends DefaultBWListener{
    static BWClient client;

    Game game;
    Calendar cal;
    String name;

    @Override
    public void onStart() {
        game = client.getGame();
        cal = Calendar.getInstance(TimeZone.getDefault());
        name = "src/test/resources/" + cal.get(YEAR) + "-" + (cal.get(MONTH) + 1) + "-" +  cal.get(DATE) + "_" + game.mapFileName();

//        try {
//            dumpBuffer(name + "_frame" + game.getFrameCount());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        BWEM bwem = new BWEM(game);
        bwem.initialize();
        BWMap map = bwem.getMap();

//        List<TilePosition> areas = map.getAreas().stream().map(Area::getTopLeft).sorted(Point::compareTo).collect(Collectors.toList());
//        List<TilePosition> bases = map.getBases().stream().map(Base::getLocation).sorted(Point::compareTo).collect(Collectors.toList());
//        List<TilePosition> chokepoints = map.getChokePoints().stream().map(c -> c.getCenter().toTilePosition()).sorted(Point::compareTo).collect(Collectors.toList());
        System.out.println("new BWEMMap(" + map.getAreas().size() + ", " + map.getBases().size() + ", " + map.getChokePoints().size() + ");");

    }

    // settings
    private void dumpBuffer(String name) throws IOException {
        ByteBuffer buf = game.getClient().clientData().buffer.getBuffer();
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
