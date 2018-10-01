package marinehell;

import java.util.*;

import bwapi.*;
import bwapi.point.Position;
import bwapi.point.TilePosition;
import bwapi.types.UnitType;
import bwem.BWEM;
import bwem.Base;
import bwem.ChokePoint;

public class MarineHell extends DefaultBWListener {

    private BWClient mirror = new BWClient(this);

    private Game game;

    private Player self;

    private int frameskip = 0;
    private int cyclesForSearching = 0;
    private int maxCyclesForSearching = 0;
    private int searchingScv = 0;
    private int searchingTimeout = 0;
    private boolean dontBuild = false;
    private int timeout = 0;
    private Unit bunkerBuilder;
    private Unit searcher;

    private BWEM BWTA;

    private String debugText = "";

    private enum Strategy {
        WaitFor50, AttackAtAllCost
    }

    private Strategy selectedStrategy = Strategy.WaitFor50;

    private Set<Position> enemyBuildingMemory = new HashSet<>();

    private void run() {
        mirror.startGame();
    }

    @Override
    public void onStart() {
        frameskip = 0;
        cyclesForSearching = 0;
        maxCyclesForSearching = 0;
        int enemies = 0;
        searchingScv = 0;
        searchingTimeout = 0;
        dontBuild = false;
        timeout = 0;
        bunkerBuilder = null;
        searcher = null;

        game = mirror.getGame();
        self = game.self();
        game.setLocalSpeed(0);

        BWTA = new BWEM(game);
        BWTA.initialize();
    }

    @Override
    public void onFrame() {
        // game.setTextSize(10);
        game.drawTextScreen(10, 10, "Playing as " + self.getName() + " - " + self.getRace());
        game.drawTextScreen(10, 20, "Units: " + self.getUnits().size() + "; Enemies: " + enemyBuildingMemory.size());
        game.drawTextScreen(10, 30,
                "Cycles for buildings: " + cyclesForSearching + "; Max cycles: " + maxCyclesForSearching);
        game.drawTextScreen(10, 40, "Elapsed time: " + game.elapsedTime() + "; Strategy: " + selectedStrategy);
        game.drawTextScreen(10, 50, debugText);
        game.drawTextScreen(10, 60, "supply: " + self.supplyTotal() + " used: " + self.supplyUsed());


        if (maxCyclesForSearching > 300000) {
            dontBuild = true;
        }

        game.setLocalSpeed(0);

        if (maxCyclesForSearching < cyclesForSearching) {
            maxCyclesForSearching = cyclesForSearching;
        }
        cyclesForSearching = 0;

        List<Unit> workers = new ArrayList<>();
        List<Unit> barracks = new ArrayList<>();
        Unit commandCenter = null;
        List<Unit> marines = new ArrayList<>();
        List<Base> baseLocations = new ArrayList<>();
        List<Base> allLocations = new ArrayList<>();
        Unit bunker = null;
        Position workerAttacked = null;


        if (bunkerBuilder != null && !bunkerBuilder.exists()) {
            bunkerBuilder = null;
        }

        if (searcher != null && !searcher.exists()) {
            searcher = null;
        }

        if (searcher != null) {
            game.drawTextMap(searcher.getPosition(), "Mr. Searcher");
        }

        // iterate through my units
        for (Unit myUnit : self.getUnits()) {
            // units.append(myUnit.getType()).append("
            // ").append(myUnit.getTilePosition()).append("\n");

            if (myUnit.getType().isWorker()) {
                workers.add(myUnit);
            }

            // if there's enough minerals, train an SCV
            if (myUnit.getType() == UnitType.Terran_Command_Center) {
                commandCenter = myUnit;
            }

            if (myUnit.getType() == UnitType.Terran_Barracks && !myUnit.isBeingConstructed()) {
                barracks.add(myUnit);
            }

            if (myUnit.getType() == UnitType.Terran_Marine) {
                marines.add(myUnit);
            }

            if (myUnit.getType() == UnitType.Terran_Bunker && !myUnit.isBeingConstructed()) {
                bunker = myUnit;
            }

            if (myUnit.isUnderAttack() && myUnit.canAttack()) {
                game.setLocalSpeed(1);
                myUnit.attack(myUnit.getPosition());
            }

        }

        for (Unit myUnit : workers) {
            // if it's a worker and it's idle, send it to the closest mineral
            // patch
            if (myUnit.getType().isWorker() && myUnit.isIdle()) {
                boolean skip = false;
                if (bunker == null && myUnit.equals(bunkerBuilder) && !barracks.isEmpty()) {
                    skip = true;
                }

                Unit closestMineral = null;

                // find the closest mineral
                for (Unit neutralUnit : game.neutral().getUnits()) {
                    if (neutralUnit.getType().isMineralField()) {
                        if (closestMineral == null
                                || myUnit.getDistance(neutralUnit) < myUnit.getDistance(closestMineral)) {
                            closestMineral = neutralUnit;
                        }
                    }
                }

                // if a mineral patch was found, send the worker to gather it
                if (closestMineral != null) {
                    if (!skip) {
                        myUnit.gather(closestMineral, false);
                    }
                }
            }

            if (myUnit.isUnderAttack() && myUnit.canAttack()) {
                game.setLocalSpeed(1);
                myUnit.attack(myUnit.getPosition());
            }

            if (myUnit.isUnderAttack() && myUnit.isGatheringMinerals()){
                workerAttacked = myUnit.getPosition();
            }
        }

        if (bunkerBuilder == null && workers.size() > 10) {
            bunkerBuilder = workers.get(10);
        }

        if (bunker == null && barracks.size() >= 1 && workers.size() > 10 && !dontBuild) {
            game.setLocalSpeed(20);

            if (timeout < 200) {
                game.drawTextMap(bunkerBuilder.getPosition(), "Moving to create bunker " + timeout + "/400");
                bunkerBuilder.move(getClosestChokePoint(BWTA, bunkerBuilder.getPosition()).getCenter().toPosition());
                timeout++;
            } else {
                game.drawTextMap(bunkerBuilder.getPosition(), "Buiding bunker");
                TilePosition buildTile = getBuildTile(bunkerBuilder, UnitType.Terran_Barracks,
                        bunkerBuilder.getTilePosition());
                if (buildTile != null) {
                    bunkerBuilder.build(UnitType.Terran_Bunker, buildTile);
                }
            }
        } else if (workers.size() > 10) {
            game.setLocalSpeed(10);
            game.drawTextMap(workers.get(10).getPosition(), "He will build bunker");
        }

        if (bunker != null && bunkerBuilder != null && !bunkerBuilder.isRepairing()) {
            game.drawTextMap(bunkerBuilder.getPosition(), "Reparing bunker");
            bunkerBuilder.repair(bunker);
        }

        if (commandCenter.getTrainingQueue().isEmpty() && workers.size() < 20 && self.minerals() >= 50) {
            commandCenter.build(UnitType.Terran_SCV);
        }

        frameskip++;
        if (frameskip == 20) {
            frameskip = 0;
        }

        if (frameskip != 0) {
            return;
        }

        searchingTimeout++;

        int i = 1;
        for (Unit worker : workers) {
            if (worker.isGatheringMinerals() && !dontBuild) {
                if (self.minerals() >= 150 * i && barracks.size() < 6) {
                    TilePosition buildTile = getBuildTile(worker, UnitType.Terran_Barracks, self.getStartLocation());
                    if (buildTile != null) {
                        worker.build(UnitType.Terran_Barracks, buildTile);
                    }
                }

                if (self.minerals() >= i * 100 && self.supplyUsed() + (self.supplyUsed() / 3) >= self.supplyTotal()
                        && self.supplyTotal() < 400) {
                    TilePosition buildTile = getBuildTile(worker, UnitType.Terran_Supply_Depot,
                            self.getStartLocation());
                    // and, if found, send the worker to build it (and leave
                    // others
                    // alone - break;)
                    if (buildTile != null) {
                        worker.build(UnitType.Terran_Supply_Depot, buildTile);
                    }
                }
            }

            i++;
        }

        for (Unit barrack : barracks) {
            if (barrack.getTrainingQueue().isEmpty()) {
                barrack.build(UnitType.Terran_Marine);
            }
        }

        for (Base b : BWTA.getMap().getBases()) {
            // If this is a possible start location,
            if (b.isStartingLocation()) {
                baseLocations.add(b);
            }

            allLocations.add(b);
        }

        int k = 0;
        for (Unit marine : marines) {
            if (!marine.isAttacking() && !marine.isMoving()) {
                if (marines.size() > 50 || selectedStrategy == Strategy.AttackAtAllCost) {
                    if (marines.size() > 40) {
                        selectedStrategy = Strategy.AttackAtAllCost;
                    } else {
                        selectedStrategy = Strategy.WaitFor50;
                    }
                    if (enemyBuildingMemory.isEmpty()) {
                        marine.attack(allLocations.get(k % allLocations.size()).getCenter());
                    } else {
                        for (Position p : enemyBuildingMemory) {
                            marine.attack(p);
                        }
                    }

                    if (marines.size() > 70) {
                        if (k < allLocations.size()) {
                            marine.attack(allLocations.get(k).getCenter());
                        }
                    }
                } else {
                    Position newPos;

                    if (bunker != null) {
                        Iterator<ChokePoint> cpppath = BWTA.getMap().getPath(bunker.getPosition(), game.self().getStartLocation().toPosition()).iterator();
                        List<TilePosition> path = new ArrayList<>();
                        while (cpppath.hasNext()) {
                            path.add(cpppath.next().getCenter().toTilePosition());
                        }
                        if (path.size() > 1) {
                            newPos = path.get(1).toPosition();
                        } else {
                            newPos = getClosestChokePoint(BWTA, marine.getPosition()).getCenter().toPosition();
                        }
                    } else {
                        newPos = getClosestChokePoint(BWTA, marine.getPosition()).getCenter().toPosition();
                    }

                    marine.attack(newPos);
                }
            }
            k++;

            if (bunker != null && bunker.getLoadedUnits().size() < 4 && k < 5) {
                marine.load(bunker);
            }

            if (workerAttacked != null){
                marine.attack(workerAttacked);
            }
        }

        if (workers.size() > 7 && searcher == null) {
            searcher = workers.get(7);
        }

        if (searcher != null && searcher.isGatheringMinerals() && searchingScv < baseLocations.size()
                && searchingTimeout % 10 == 0) {
            searcher.move(baseLocations.get(searchingScv).getCenter());
            searchingScv++;
        }
        int workersSize = workers.size();
        debugText = "Size: " + workersSize + "; isGathering" + workers.get(workersSize > 7 ? 7 : workersSize - 1).isGatheringMinerals() + "; location: "
                + baseLocations.size() + "; num: " + searchingScv;

        for (Unit u : game.enemy().getUnits()) {
            // if this unit is in fact a building
            if (u.getType().isBuilding()) {
                enemyBuildingMemory.add(u.getPosition());
            }
        }

        // loop over all the positions that we remember
        for (Position p : enemyBuildingMemory) {
            // compute the TilePosition corresponding to our remembered Position
            // p
            TilePosition tileCorrespondingToP = new TilePosition(p.getX() / 32, p.getY() / 32);

            // if that tile is currently visible to us...
            if (game.isVisible(tileCorrespondingToP)) {

                // loop over all the visible enemy buildings and find out if at
                // least
                // one of them is still at that remembered position
                boolean buildingStillThere = false;
                for (Unit u : game.enemy().getUnits()) {
                    if ((u.getType().isBuilding()) && (u.getPosition() == p)) {
                        buildingStillThere = true;
                        break;
                    }
                }

                // if there is no more any building, remove that position from
                // our memory
                if (!buildingStillThere) {
                    enemyBuildingMemory.remove(p);
                    break;
                }
            }
        }

        // draw my units on screen
        // game.drawTextScreen(10, 25, units.toString());
    }

    public static void main(String[] args) {
        new MarineHell().run();
    }

    // Returns a suitable TilePosition to build a given building type near
    // specified TilePosition aroundTile, or null if not found. (builder
    // parameter is our worker)
    public TilePosition getBuildTile(Unit builder, UnitType buildingType, TilePosition aroundTile) {
        TilePosition ret = null;
        int maxDist = 3;
        int stopDist = 40;

        // Refinery, Assimilator, Extractor
        if (buildingType.isRefinery()) {
            for (Unit n : game.neutral().getUnits()) {
                cyclesForSearching++;
                if ((n.getType() == UnitType.Resource_Vespene_Geyser)
                        && (Math.abs(n.getTilePosition().getX() - aroundTile.getX()) < stopDist)
                        && (Math.abs(n.getTilePosition().getY() - aroundTile.getY()) < stopDist))
                    return n.getTilePosition();
            }
        }

        while ((maxDist < stopDist) && (ret == null)) {
            for (int i = aroundTile.getX() - maxDist; i <= aroundTile.getX() + maxDist; i++) {
                for (int j = aroundTile.getY() - maxDist; j <= aroundTile.getY() + maxDist; j++) {
                    if (game.canBuildHere(new TilePosition(i, j), buildingType, builder, false)) {
                        // units that are blocking the tile
                        boolean unitsInWay = false;
                        for (Unit u : game.getAllUnits()) {
                            cyclesForSearching++;
                            if (u.getID() == builder.getID())
                                continue;
                            if ((Math.abs(u.getTilePosition().getX() - i) < 4)
                                    && (Math.abs(u.getTilePosition().getY() - j) < 4))
                                unitsInWay = true;
                        }
                        if (!unitsInWay) {
                            cyclesForSearching++;
                            return new TilePosition(i, j);
                        }
                        // creep for Zerg
                        if (buildingType.requiresCreep()) {
                            boolean creepMissing = false;
                            for (int k = i; k <= i + buildingType.tileWidth(); k++) {
                                for (int l = j; l <= j + buildingType.tileHeight(); l++) {
                                    cyclesForSearching++;
                                    if (!game.hasCreep(k, l))
                                        creepMissing = true;
                                    break;
                                }
                            }
                            if (creepMissing)
                                continue;
                        }
                    }
                }
            }
            maxDist += 2;
        }

        if (ret == null)
            game.printf("Unable to find suitable build position for " + buildingType.toString());
        return ret;
    }

    // Jabbo
    public static ChokePoint getClosestChokePoint(BWEM BWTA, Position pos) {
        ChokePoint closestBase = null;
        double dist = Double.MAX_VALUE;
        for (ChokePoint base : BWTA.getMap().getChokePoints()) {
            double cDist = pos.getApproxDistance(base.getCenter().toPosition());
            if (closestBase == null || cDist < dist) {
                closestBase = base;
                dist = cDist;
            }
        }
        return closestBase;
    }
}