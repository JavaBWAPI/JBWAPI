package bwapi;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 5, time = 2)
@Fork(1)
@State(Scope.Thread)
public class PerformanceBenchmark {

    private static final String MAP_NAME = "(4)Andromeda.scx";

    Game game;
    ClientData.GameData gameData;

    List<Unit> allUnits;
    List<Unit> friendlyCombatUnits;
    List<Unit> enemyCombatUnits;
    List<Unit> mobileUnits;
    ClientData.UnitData[] mobileUnitData;
    int[] baseMobileX;
    int[] baseMobileY;

    Position[] targetPositions;
    Position[] rectTopLefts;
    Position[] rectBottomRights;
    TilePosition[] buildCandidateTiles;
    Unit builderWorker;

    int currentFrame;

    @Setup(Level.Trial)
    public void setup() throws IOException {
        game = GameBuilder.createGame(MAP_NAME);
        gameData = game.botClientData().gameData();

        // 1. Show all initial map units (minerals, geysers, starting depot + 4 workers)
        // On (4)Andromeda.scx this is ~190 static/initial units clustered at IDs 0..initialCount-1
        int initialCount = gameData.getInitialUnitCount();
        for (int id = 0; id < initialCount; id++) {
            Unit u = game.getUnit(id);
            if (u != null) {
                game.unitShow(id);
            }
        }

        SplittableRandom rng = new SplittableRandom(42L);
        int selfId = game.self().getID();
        int enemyId = game.enemy().getID();

        TilePosition selfStart = game.self().getStartLocation();
        int startPixelX = selfStart.x * 32;
        int startPixelY = selfStart.y * 32;

        int nextId = initialCount;

        // 2. Add 50 stationary buildings (25 friendly, 25 enemy) contiguous after initial map resources
        UnitType[] buildingTypes = {
                UnitType.Terran_Supply_Depot,
                UnitType.Terran_Barracks,
                UnitType.Terran_Factory,
                UnitType.Terran_Missile_Turret,
                UnitType.Terran_Bunker
        };
        for (int i = 0; i < 50; i++) {
            int id = nextId++;
            UnitType type = buildingTypes[i % buildingTypes.length];
            int owner = (i < 25) ? selfId : enemyId;
            int px = clampPixelX(startPixelX + rng.nextInt(-600, 600));
            int py = clampPixelY(startPixelY + rng.nextInt(-600, 600));
            populateUnitData(id, owner, type, px, py);
            game.unitShow(id);
        }

        // 3. Add 120 mobile combat/worker units (60 friendly, 60 enemy) at the tail of the unit table
        UnitType[] mobileTypes = {
                UnitType.Terran_Marine,
                UnitType.Terran_Medic,
                UnitType.Terran_Siege_Tank_Tank_Mode,
                UnitType.Terran_Vulture,
                UnitType.Terran_Goliath,
                UnitType.Terran_Science_Vessel
        };
        friendlyCombatUnits = new ArrayList<>(60);
        enemyCombatUnits = new ArrayList<>(60);
        mobileUnits = new ArrayList<>(120);
        mobileUnitData = new ClientData.UnitData[120];
        baseMobileX = new int[120];
        baseMobileY = new int[120];

        for (int i = 0; i < 120; i++) {
            int id = nextId++;
            UnitType type = mobileTypes[i % mobileTypes.length];
            boolean isFriendly = (i < 60);
            int owner = isFriendly ? selfId : enemyId;
            int px = clampPixelX(startPixelX + rng.nextInt(-1000, 1000));
            int py = clampPixelY(startPixelY + rng.nextInt(-1000, 1000));

            populateUnitData(id, owner, type, px, py);
            game.unitShow(id);

            Unit u = game.getUnit(id);
            mobileUnits.add(u);
            mobileUnitData[i] = gameData.getUnits(id);
            baseMobileX[i] = px;
            baseMobileY[i] = py;

            if (isFriendly) {
                friendlyCombatUnits.add(u);
            } else {
                enemyCombatUnits.add(u);
            }
        }

        currentFrame = 1;
        gameData.setFrameCount(currentFrame);
        game.onFrame(currentFrame);
        allUnits = game.getAllUnits();

        // Promote long-lived Game and Unit instances to Old Gen, matching a running match
        System.gc();

        builderWorker = game.self().getUnits().stream()
                .filter(u -> u.getType().isWorker())
                .findFirst()
                .orElse(friendlyCombatUnits.get(0));

        // 4. Target positions for getDistance(Position)
        targetPositions = new Position[20];
        for (int i = 0; i < targetPositions.length; i++) {
            targetPositions[i] = new Position(
                    clampPixelX(startPixelX + rng.nextInt(-1200, 1200)),
                    clampPixelY(startPixelY + rng.nextInt(-1200, 1200))
            );
        }

        // 5. Search rectangles for getUnitsInRectangle (32 rectangles of ~10x10 tiles around active areas)
        rectTopLefts = new Position[32];
        rectBottomRights = new Position[32];
        for (int i = 0; i < 32; i++) {
            int cx = clampPixelX(startPixelX + rng.nextInt(-900, 900));
            int cy = clampPixelY(startPixelY + rng.nextInt(-900, 900));
            rectTopLefts[i] = new Position(Math.max(0, cx - 160), Math.max(0, cy - 160));
            rectBottomRights[i] = new Position(Math.min(game.mapPixelWidth() - 1, cx + 160), Math.min(game.mapPixelHeight() - 1, cy + 160));
        }

        // 6. Collectible buildable tiles near start location so canBuildHere passes the static map buildability check
        // and actually executes the unit-in-rectangle and resource checks.
        List<TilePosition> buildableCandidates = new ArrayList<>(32);
        for (int dy = -20; dy <= 20 && buildableCandidates.size() < 32; dy++) {
            for (int dx = -20; dx <= 20 && buildableCandidates.size() < 32; dx++) {
                int tx = selfStart.x + dx;
                int ty = selfStart.y + dy;
                if (areTilesBuildable(tx, ty, 4, 3)) {
                    buildableCandidates.add(new TilePosition(tx, ty));
                }
            }
        }
        buildCandidateTiles = buildableCandidates.toArray(new TilePosition[0]);
    }

    private boolean areTilesBuildable(int tx, int ty, int w, int h) {
        for (int x = tx; x < tx + w; x++) {
            for (int y = ty; y < ty + h; y++) {
                if (!game.isBuildable(x, y, true)) {
                    return false;
                }
            }
        }
        return true;
    }

    private int clampPixelX(int x) {
        return Math.max(64, Math.min(game.mapPixelWidth() - 64, x));
    }

    private int clampPixelY(int y) {
        return Math.max(64, Math.min(game.mapPixelHeight() - 64, y));
    }

    private void populateUnitData(int id, int playerId, UnitType type, int x, int y) {
        ClientData.UnitData ud = gameData.getUnits(id);
        ud.setId(id);
        ud.setPlayer(playerId);
        ud.setType(type.id);
        ud.setPositionX(x);
        ud.setPositionY(y);
        ud.setHitPoints(type.maxHitPoints());
        ud.setExists(true);
        ud.setIsCompleted(true);
        ud.setIsLifted(false);
        ud.setTransport(-1);
        ud.setCarrier(-1);
        ud.setHatchery(-1);
        ud.setAddon(-1);
    }

    /**
     * Benchmarks 3,600 Unit.getDistance(Unit) calls (60 friendly x 60 enemy units),
     * typical of target selection / threat evaluation in a combat frame.
     */
    @Benchmark
    public int unitGetDistanceToUnit() {
        int sum = 0;
        List<Unit> friendly = this.friendlyCombatUnits;
        List<Unit> enemy = this.enemyCombatUnits;
        for (int i = 0, fSize = friendly.size(); i < fSize; i++) {
            Unit f = friendly.get(i);
            for (int j = 0, eSize = enemy.size(); j < eSize; j++) {
                sum += f.getDistance(enemy.get(j));
            }
        }
        return sum;
    }

    /**
     * Benchmarks 2,400 Unit.getDistance(Position) calls (120 mobile units x 20 positions).
     */
    @Benchmark
    public int unitGetDistanceToPosition() {
        int sum = 0;
        List<Unit> units = this.mobileUnits;
        Position[] positions = this.targetPositions;
        for (int i = 0, uSize = units.size(); i < uSize; i++) {
            Unit u = units.get(i);
            for (int j = 0; j < positions.length; j++) {
                sum += u.getDistance(positions[j]);
            }
        }
        return sum;
    }

    /**
     * Benchmarks Unit.updatePosition(frame) across all ~360 units in the game,
     * where the first ~240 units (resources + buildings) are stationary and the
     * last 120 units (army/workers) change position every frame.
     */
    @Benchmark
    public int unitUpdatePositionPerFrame() {
        int frame = ++currentFrame;
        int offset = frame & 7;
        ClientData.UnitData[] uds = this.mobileUnitData;
        for (int i = 0; i < uds.length; i++) {
            uds[i].setPositionX(baseMobileX[i] + offset);
            uds[i].setPositionY(baseMobileY[i] + offset);
        }

        int sumX = 0;
        for (Unit u : allUnits) {
            u.updatePosition(frame);
            sumX += u.getPosition().x;
        }
        return sumX;
    }

    /**
     * Benchmarks 32 Game.getUnitsInRectangle queries over the populated map.
     */
    @Benchmark
    public void getUnitsInRectangle(Blackhole bh) {
        Position[] tls = this.rectTopLefts;
        Position[] brs = this.rectBottomRights;
        for (int i = 0; i < tls.length; i++) {
            List<Unit> units = game.getUnitsInRectangle(tls[i], brs[i], u -> !u.isFlying() && !u.isLoaded());
            bh.consume(units);
        }
    }

    /**
     * Benchmarks Game.canBuildHere across 32 buildable candidate tiles for a Barracks, Supply Depot, and Command Center.
     */
    @Benchmark
    public int canBuildHere() {
        int buildableCount = 0;
        TilePosition[] tiles = this.buildCandidateTiles;
        Unit builder = this.builderWorker;
        for (int i = 0; i < tiles.length; i++) {
            TilePosition tile = tiles[i];
            if (game.canBuildHere(tile, UnitType.Terran_Barracks, builder, false)) {
                buildableCount++;
            }
            if (game.canBuildHere(tile, UnitType.Terran_Supply_Depot, builder, false)) {
                buildableCount++;
            }
            if (game.canBuildHere(tile, UnitType.Terran_Command_Center, builder, false)) {
                buildableCount++;
            }
        }
        return buildableCount;
    }

    /**
     * Separate state for worst-case late-game visibility churn:
     * - 700 active visible units distributed across IDs 0..3000 (simulating a 25-minute macro game
     *   where ~2,300 units have died and new units have IDs > 127 outside Integer.valueOf cache,
     *   and peak unit count previously reached ~1,200).
     * - 40 units at the fog-of-war boundary toggling unitHide / unitShow on a frame, followed by onFrame(frame).
     */
    @State(Scope.Thread)
    public static class LateGameVisibilityState {
        Game game;
        ClientData.GameData gameData;
        int[] fogBoundaryIds;
        int frame;

        @Setup(Level.Trial)
        public void setup() throws IOException {
            game = GameBuilder.createGame(MAP_NAME);
            gameData = game.botClientData().gameData();

            int selfId = game.self().getID();
            int enemyId = game.enemy().getID();

            // Simulate peak of 1,200 units earlier in the game so the collection has experienced realistic growth
            for (int id = 0; id < 1200; id++) {
                ClientData.UnitData ud = gameData.getUnits(id);
                ud.setId(id);
                ud.setPlayer(id % 2 == 0 ? selfId : enemyId);
                ud.setType(UnitType.Terran_Marine.id);
                ud.setPositionX(1000);
                ud.setPositionY(1000);
                ud.setHitPoints(40);
                ud.setExists(true);
                ud.setIsCompleted(true);
                game.unitShow(id);
            }
            for (int id = 0; id < 1200; id++) {
                game.unitHide(id);
            }

            // Now populate 700 currently visible units scattered across IDs 0..2800 (step 4)
            fogBoundaryIds = new int[40];
            int count = 0;
            for (int id = 0; count < 700; id += 4) {
                ClientData.UnitData ud = gameData.getUnits(id);
                ud.setId(id);
                ud.setPlayer(count < 350 ? selfId : enemyId);
                ud.setType(UnitType.Terran_Marine.id);
                ud.setPositionX(1024 + (count & 63) * 8);
                ud.setPositionY(1024 + (count >> 6) * 8);
                ud.setHitPoints(40);
                ud.setExists(true);
                ud.setIsCompleted(true);
                game.unitShow(id);
                if (count >= 660) {
                    fogBoundaryIds[count - 660] = id;
                }
                count++;
            }

            frame = 1;
            gameData.setFrameCount(frame);
            game.onFrame(frame);
            System.gc();
        }
    }

    /**
     * Benchmarks worst-case late-game visibility churn + onFrame rebuild of allUnits
     * (40 fog-of-war unitHide/unitShow events with IDs > 2600 + onFrame over 700 visible units).
     */
    @Benchmark
    public int visibleUnitsWorstCaseOnFrame(LateGameVisibilityState s) {
        Game g = s.game;
        int[] boundary = s.fogBoundaryIds;
        for (int i = 0; i < boundary.length; i++) {
            int id = boundary[i];
            g.unitHide(id);
            g.unitShow(id);
        }
        int f = ++s.frame;
        g.onFrame(f);
        return g.getAllUnits().size();
    }
}
