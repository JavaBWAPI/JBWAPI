package game;

import bwapi.*;
import bwem.Area;
import bwem.BWEM;
import bwem.BWMap;

import java.util.List;

public class AreaBoundariesTest extends DefaultBWListener {
    BWClient bwClient;
    Game game;
    BWMap bwMap;

    public void onStart() {
        game = bwClient.getGame();
        game.sendText("black sheep wall");

        BWEM bwem = new BWEM(game);
        bwem.initialize();
        bwem.calculateAreaBoundaries();
        bwMap = bwem.getMap();
    }

    @Override
    public void onFrame() {
        for (Area a : bwMap.getAreas()) {
            final List<Position> p = a.getBoundaryVertices();
            for (int i=1; i < p.size(); i++) {
                game.drawLineMap(p.get(i-1), p.get(i), Color.Green);
            }
            p.forEach(pp -> game.drawDotMap(pp, Color.Red));
        }
    }

    public static void main(String[] args) {
        AreaBoundariesTest abt = new AreaBoundariesTest();
        abt.bwClient = new BWClient(abt);
        abt.bwClient.startGame();
    }
}
