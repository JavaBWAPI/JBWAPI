package bwapi;

import bwapi.ClientData.Command;
import bwapi.ClientData.GameData;
import bwapi.ClientData.Shape;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static bwapi.CommandType.*;
import static bwapi.Point.TILE_WALK_FACTOR;
import static bwapi.Race.Zerg;
import static bwapi.UnitType.*;

public class Game {
    private static final int[][] damageRatio = {
            // Ind, Sml, Med, Lrg, Non, Unk
            {0, 0, 0, 0, 0, 0}, // Independent
            {0, 128, 192, 256, 0, 0}, // Explosive
            {0, 256, 128, 64, 0, 0}, // Concussive
            {0, 256, 256, 256, 0, 0}, // Normal
            {0, 256, 256, 256, 0, 0}, // Ignore_Armor
            {0, 0, 0, 0, 0, 0}, // None
            {0, 0, 0, 0, 0, 0}  // Unknown
    };
    private static final boolean[][] bPsiFieldMask = {
            {false, false, false, false, false, true, true, true, true, true, true, false, false, false, false, false},
            {false, false, true, true, true, true, true, true, true, true, true, true, true, true, false, false},
            {false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false},
            {true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true},
            {true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true},
            {true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true},
            {true, true, true, true, true, true, true, true, true, true, true, true, true, true, true, true},
            {false, true, true, true, true, true, true, true, true, true, true, true, true, true, true, false},
            {false, false, true, true, true, true, true, true, true, true, true, true, true, true, false, false},
            {false, false, false, false, false, true, true, true, true, true, true, false, false, false, false, false}
    };

    private static final int REGION_DATA_SIZE = 5000;

    private final Set<Integer> visibleUnits = new HashSet<>();
    private List<Unit> allUnits;
    private final Client client;
    private final GameData gameData;

    private List<Unit> staticMinerals;
    private List<Unit> staticGeysers;
    private List<Unit> staticNeutralUnits;

    // CONSTANT
    private Player[] players;
    private Region[] regions;
    private Force[] forces;
    private Bullet[] bullets;
    private List<Force> forceSet;
    private List<Player> playerSet;
    private List<Region> regionSet;

    private List<Player> allies;
    private List<Player> enemies;
    private List<Player> observers;

    // CHANGING
    private Unit[] units;

    //CACHED
    private int randomSeed;
    private int revision;
    private boolean debug;
    private Player self;
    private Player enemy;
    private Player neutral;
    private boolean replay;
    private boolean multiplayer;
    private boolean battleNet;
    private List<TilePosition> startLocations;
    private int mapWidth;
    private int mapHeight;
    private int mapPixelWidth;
    private int mapPixelHeight;
    private String mapFileName;
    private String mapPathName;
    private String mapName;
    private String mapHash;
    private boolean[][] buildable;
    private boolean[][] walkable;
    private int[][] groundHeight;
    private short[][] mapTileRegionID;
    private short[] mapSplitTilesMiniTileMask;
    private short[] mapSplitTilesRegion1;
    private short[] mapSplitTilesRegion2;
    // USER DEFINED
    private TextSize textSize = TextSize.Default;

    public Game(Client client) {
        this.client = client;
        this.gameData = client.data();
    }

    private static boolean hasPower(final int x, final int y, final UnitType unitType, final List<Unit> pylons) {
        if (unitType.id >= 0 && unitType.id < UnitType.None.id && (!unitType.requiresPsi() || !unitType.isBuilding())) {
            return true;
        }

        // Loop through all pylons for the current player
        for (final Unit i : pylons) {
            if (!i.exists() || !i.isCompleted()) {
                continue;
            }
            final Position p = i.getPosition();
            if (Math.abs(p.x - x) >= 256) {
                continue;
            }
            if (Math.abs(p.y - y) >= 160) {
                continue;
            }

            if (bPsiFieldMask[(y - p.y + 160) / 32][(x - p.x + 256) / 32]) {
                return true;
            }
        }
        return false;
    }

    /*
    Call this method in EventHander::OnMatchStart
    */
    void init() {
        visibleUnits.clear();

        final int forceCount = gameData.getForceCount();
        forces = new Force[forceCount];
        for (int id = 0; id < forceCount; id++) {
            forces[id] = new Force(gameData.getForces(id), id, this);
        }

        forceSet = Collections.unmodifiableList(Arrays.asList(forces));

        final int playerCount = gameData.getPlayerCount();
        players = new Player[playerCount];
        for (int id = 0; id < playerCount; id++) {
            players[id] = new Player(gameData.getPlayers(id), id, this);
        }

        playerSet = Collections.unmodifiableList(Arrays.asList(players));

        final int bulletCount = 100;
        bullets = new Bullet[bulletCount];
        for (int id = 0; id < bulletCount; id++) {
            bullets[id] = new Bullet(gameData.getBullets(id), id,this);
        }

        final int regionCount = gameData.getRegionCount();
        regions = new Region[regionCount];
        for (int id = 0; id < regionCount; id++) {
            regions[id] = new Region(gameData.getRegions(id), this);
        }

        for (final Region region : regions) {
            region.updateNeighbours();
        }

        regionSet = Collections.unmodifiableList(Arrays.asList(regions));

        units = new Unit[10000];

        final List<Unit> staticMinerals = new ArrayList<>();
        final List<Unit> staticGeysers = new ArrayList<>();
        final List<Unit> staticNeutralUnits = new ArrayList<>();
        final List<Unit> allUnits = new ArrayList<>();
        for (int id = 0; id < gameData.getInitialUnitCount(); id++) {
            final Unit unit = new Unit(gameData.getUnits(id), id, this);
            //skip ghost units
            if (unit.getInitialType() == UnitType.Terran_Marine && unit.getInitialHitPoints() == 0) {
                continue;
            }
            this.units[id] = unit;
            allUnits.add(unit);

            if (unit.getType().isMineralField()) {
                staticMinerals.add(unit);
            }
            if (unit.getType() == Resource_Vespene_Geyser) {
                staticGeysers.add(unit);
            }
            if (unit.getPlayer().equals(neutral())) {
                staticNeutralUnits.add(unit);
            }
        }

        this.staticMinerals = Collections.unmodifiableList(staticMinerals);
        this.staticGeysers = Collections.unmodifiableList(staticGeysers);
        this.staticNeutralUnits = Collections.unmodifiableList(staticNeutralUnits);
        this.allUnits = Collections.unmodifiableList(allUnits);



        randomSeed = gameData.getRandomSeed();

        revision = gameData.getRevision();
        debug = gameData.isDebug();
        self = players[gameData.getSelf()];
        enemy = players[gameData.getEnemy()];
        neutral = players[gameData.getNeutral()];
        replay = gameData.isReplay();
        multiplayer = gameData.isMultiplayer();
        battleNet = gameData.isBattleNet();
        startLocations = IntStream.range(0, gameData.getStartLocationCount())
                .mapToObj(i -> new TilePosition(gameData.getStartLocations(i)))
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
        mapWidth = gameData.getMapWidth();
        mapHeight = gameData.getMapHeight();
        mapFileName = gameData.getMapFileName();
        mapPathName = gameData.getMapPathName();
        mapName = gameData.getMapName();
        mapHash = gameData.getMapHash();

        buildable = new boolean[mapWidth][mapHeight];
        groundHeight = new int[mapWidth][mapHeight];
        mapTileRegionID = new short[mapWidth][mapHeight];
        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                buildable[x][y] = gameData.isBuildable(x, y);
                groundHeight[x][y] = gameData.getGroundHeight(x, y);
                mapTileRegionID[x][y] = gameData.getMapTileRegionId(x, y);
            }
        }
        walkable = new boolean[mapWidth * TILE_WALK_FACTOR][mapHeight * TILE_WALK_FACTOR];
        for (int i = 0; i < mapWidth * TILE_WALK_FACTOR; i++) {
            for (int j = 0; j < mapHeight * TILE_WALK_FACTOR; j++) {
                walkable[i][j] = gameData.isWalkable(i, j);
            }
        }

        mapSplitTilesMiniTileMask = new short[REGION_DATA_SIZE];
        mapSplitTilesRegion1 = new short[REGION_DATA_SIZE];
        mapSplitTilesRegion2 = new short[REGION_DATA_SIZE];
        for (int i = 0; i < REGION_DATA_SIZE; i++) {
            mapSplitTilesMiniTileMask[i] = gameData.getMapSplitTilesMiniTileMask(i);
            mapSplitTilesRegion1[i] = gameData.getMapSplitTilesRegion1(i);
            mapSplitTilesRegion2[i] = gameData.getMapSplitTilesRegion2(i);
        }

        mapPixelWidth = mapWidth * TilePosition.SIZE_IN_PIXELS;
        mapPixelHeight = mapHeight * TilePosition.SIZE_IN_PIXELS;


        enemies = playerSet.stream().filter(p -> !p.equals(self) && self.isEnemy(p))
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
        allies = playerSet.stream().filter(p -> !p.equals(self) && self.isAlly(p))
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));

        observers = playerSet.stream().filter(p -> !p.equals(self) && p.isObserver())
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
    }

    void unitCreate(final int id) {
        if (id > units.length) {
            //rescale unit array if needed
            final Unit[] largerUnitsArray = new Unit[2 * units.length];
            System.arraycopy(units, 0, largerUnitsArray, 0, units.length);
            units = largerUnitsArray;
        }

        if (units[id] == null) {
            final Unit u = new Unit(gameData.getUnits(id), id, this);
            units[id] = u;
        }
    }

    void unitShow(final int id) {
        unitCreate(id);
        visibleUnits.add(id);
    }

    void unitHide(final int id) {
        visibleUnits.remove(id);
    }

    void onFrame(final int frame) {
        if (frame > 0) {
            allUnits = visibleUnits.stream()
                .map(i -> units[i])
                .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
        }
        getAllUnits().forEach(u -> u.updatePosition(frame));
    }

    void addUnitCommand(final int type, final int unit, final int target, final int x, final int y, final int extra) {
        ClientData.UnitCommand unitCommand = client.addUnitCommand();
        unitCommand.setTid(type);
        unitCommand.setUnitIndex(unit);
        unitCommand.setTargetIndex(target);
        unitCommand.setX(x);
        unitCommand.setY(y);
        unitCommand.setExtra(extra);
    }

    void addCommand(final CommandType type, final int value1, final int value2) {
        Command command = client.addCommand();
        command.setType(type);
        command.setValue1(value1);
        command.setValue2(value2);
    }

    void addShape(final ShapeType type, final CoordinateType coordType, final int x1, final int y1, final int x2, final int y2, final int extra1, final int extra2, final int color, final boolean isSolid) {
        Shape shape = client.addShape();
        shape.setType(type);
        shape.setCtype(coordType);
        shape.setX1(x1);
        shape.setY1(y1);
        shape.setX2(x2);
        shape.setY2(y2);
        shape.setExtra1(extra1);
        shape.setExtra2(extra2);
        shape.setColor(color);
        shape.setIsSolid(isSolid);
    }

    public List<Force> getForces() {
        return forceSet;
    }

    public List<Player> getPlayers() {
        return playerSet;
    }

    public List<Unit> getAllUnits() {
        return allUnits;
    }

    public List<Unit> getMinerals() {
        return getAllUnits().stream()
                .filter(u -> u.getType().isMineralField())
                .collect(Collectors.toList());
    }

    public List<Unit> getGeysers() {
        return getAllUnits().stream()
                .filter(u -> u.getType() == Resource_Vespene_Geyser)
                .collect(Collectors.toList());
    }

    public List<Unit> getNeutralUnits() {
        return getAllUnits().stream()
                .filter(u -> u.getPlayer().equals(neutral()))
                .collect(Collectors.toList());
    }

    public List<Unit> getStaticMinerals() {
        return staticMinerals;
    }

    public List<Unit> getStaticGeysers() {
        return staticGeysers;
    }

    public List<Unit> getStaticNeutralUnits() {
        return staticNeutralUnits;
    }

    public List<Bullet> getBullets() {
        return Arrays.stream(bullets)
                .filter(Bullet::exists)
                .collect(Collectors.toList());
    }

    public List<Position> getNukeDots() {
        return IntStream.range(0, gameData.getNukeDotCount())
                .mapToObj(id -> new Position(gameData.getNukeDots(id)))
                .collect(Collectors.toList());
    }

    public Force getForce(final int forceID) {
        return forces[forceID];
    }

    public Player getPlayer(final int playerID) {
        return players[playerID];
    }

    public Unit getUnit(final int unitID) {
        if (unitID < 0 || unitID >= units.length) {
            return null;
        }
        return units[unitID];
    }

    public Region getRegion(final int regionID) {
        return regions[regionID];
    }

    public GameType getGameType() {
        return GameType.idToEnum[gameData.getGameType()];
    }

    public Latency getLatency() {
        return Latency.idToEnum[gameData.getLatency()];
    }

    public int getFrameCount() {
        return gameData.getFrameCount();
    }

    public int getReplayFrameCount() {
        return gameData.getReplayFrameCount();
    }

    public int getFPS() {
        return gameData.getFps();
    }

    public double getAverageFPS() {
        return gameData.getAverageFPS();
    }

    public Position getMousePosition() {
        return new Position(gameData.getMouseX(), gameData.getMouseY());
    }

    public boolean getMouseState(final MouseButton button) {
        return gameData.getMouseState(button.id);
    }

    public boolean getKeyState(final Key key) {
        return gameData.getKeyState(key.id);
    }

    public Position getScreenPosition() {
        return new Position(gameData.getScreenX(), gameData.getScreenY());
    }

    public void setScreenPosition(final Position p) {
        setScreenPosition(p.x, p.y);
    }

    public void setScreenPosition(final int x, final int y) {
        addCommand(SetScreenPosition, x, y);
    }

    public void pingMinimap(final int x, final int y) {
        addCommand(PingMinimap, x, y);
    }

    public void pingMinimap(final Position p) {
        pingMinimap(p.x, p.y);
    }

    public boolean isFlagEnabled(final Flag flag) {
        return gameData.getFlags(flag.id);
    }

    public void enableFlag(final Flag flag) {
        addCommand(EnableFlag, flag.id, 1);
    }

    public List<Unit> getUnitsOnTile(final int tileX, final int tileY) {
        return getAllUnits().stream().filter(u -> {
            final TilePosition tp = u.getTilePosition();
            return tp.x == tileX && tp.y == tileY;
        }).collect(Collectors.toList());
    }

    public List<Unit> getUnitsOnTile(final TilePosition tile) {
        return getUnitsOnTile(tile.x, tile.y);
    }

    public List<Unit> getUnitsInRectangle(final int left, final int top, final int right, final int bottom) {
        return getUnitsInRectangle(left, top, right, bottom, u -> true);
    }

    public List<Unit> getUnitsInRectangle(final int left, final int top, final int right, final int bottom, final UnitFilter pred) {
        return getAllUnits().stream().filter(u ->
            left <= u.getRight() && top <= u.getBottom() && right >= u.getLeft() && bottom >= u.getTop() && pred.test(u))
            .collect(Collectors.toList());
    }

    public List<Unit> getUnitsInRectangle(final Position leftTop, final Position rightBottom) {
        return getUnitsInRectangle(leftTop.x, leftTop.y, rightBottom.x, rightBottom.y, u -> true);
    }

    public List<Unit> getUnitsInRectangle(final Position leftTop, final Position rightBottom, final UnitFilter pred) {
        return getUnitsInRectangle(leftTop.x, leftTop.y, rightBottom.x, rightBottom.y, pred);
    }

    public List<Unit> getUnitsInRadius(final int x, final int y, final int radius) {
        return getUnitsInRadius(x, y, radius, u -> true);
    }

    public List<Unit> getUnitsInRadius(final int x, final int y, final int radius, final UnitFilter pred) {
        return getUnitsInRadius(new Position(x, y), radius, pred);
    }

    public List<Unit> getUnitsInRadius(final Position center, final int radius) {
        return getUnitsInRadius(center, radius, u -> true);
    }

    public List<Unit> getUnitsInRadius(final Position center, final int radius, final UnitFilter pred) {
        return getAllUnits().stream()
                .filter(u -> center.getApproxDistance(u.getPosition()) <= radius && pred.test(u))
                .collect(Collectors.toList());
    }

    public Unit getClosestUnitInRectangle(final Position center, final int left, final int top, final int right, final int bottom) {
        return getClosestUnitInRectangle(center, left, top, right, bottom, u -> true);
    }

    public Unit getClosestUnitInRectangle(final Position center, final int left, final int top, final int right, final int bottom, final UnitFilter pred) {
        return getUnitsInRectangle(left, top, right, bottom, pred).stream()
                .min(Comparator.comparingInt(u -> u.getDistance(center))).orElse(null);
    }

    public Unit getClosestUnit(final Position center) {
        return getClosestUnit(center, 999999);
    }

    public Unit getClosestUnit(final Position center, final UnitFilter pred) {
        return getClosestUnit(center, 999999, pred);
    }

    public Unit getClosestUnit(final Position center, final int radius) {
        return getClosestUnit(center, radius, u -> true);
    }

    public Unit getClosestUnit(final Position center, final int radius, final UnitFilter pred) {
        return getUnitsInRadius(center, radius, pred).stream()
                .min(Comparator.comparingInt(u -> u.getDistance(center))).orElse(null);
    }

    public int mapWidth() {
        return mapWidth;
    }

    public int mapHeight() {
        return mapHeight;
    }

    int mapPixelWidth() {
        return mapPixelWidth;
    }

    int mapPixelHeight() {
        return mapPixelHeight;
    }

    public String mapFileName() {
        return mapFileName;
    }

    public String mapPathName() {
        return mapPathName;
    }

    public String mapName() {
        return mapName;
    }

    public String mapHash() {
        return mapHash;
    }

    public boolean isWalkable(final int walkX, final int walkY) {
        return isWalkable(new WalkPosition(walkX, walkY));
    }

    public boolean isWalkable(final WalkPosition position) {
        if (!position.isValid(this)) {
            return false;
        }
        return walkable[position.x][position.y];
    }

    public int getGroundHeight(final int tileX, final int tileY) {
        return getGroundHeight(new TilePosition(tileX, tileY));
    }

    public int getGroundHeight(final TilePosition position) {
        if (!position.isValid(this)) {
            return -1;
        }
        return groundHeight[position.x][position.y];
    }

    public boolean isBuildable(final int tileX, final int tileY) {
        return isBuildable(tileX, tileY, false);
    }

    public boolean isBuildable(final int tileX, final int tileY, final boolean includeBuildings) {
        return isBuildable(new TilePosition(tileX, tileY), includeBuildings);
    }

    public boolean isBuildable(final TilePosition position) {
        return isBuildable(position, false);
    }

    public boolean isBuildable(final TilePosition position, final boolean includeBuildings) {
        if (!position.isValid(this)) {
            return false;
        }
        return buildable[position.x][position.y] && (!includeBuildings || !gameData.isOccupied(position.x, position.y));
    }

    public boolean isVisible(final int tileX, final int tileY) {
        return isVisible(new TilePosition(tileX, tileY));
    }

    public boolean isVisible(final TilePosition position) {
        if (!position.isValid(this)) {
            return false;
        }
        return gameData.isVisible(position.x, position.y);
    }

    public boolean isExplored(final int tileX, final int tileY) {
        return isExplored(new TilePosition(tileX, tileY));
    }

    public boolean isExplored(final TilePosition position) {
        if (!position.isValid(this)) {
            return false;
        }
        return gameData.isExplored(position.x, position.y);
    }

    public boolean hasCreep(final int tileX, final int tileY) {
        return hasCreep(new TilePosition(tileX, tileY));
    }

    public boolean hasCreep(final TilePosition position) {
        if (!position.isValid(this)) {
            return false;
        }
        return gameData.getHasCreep(position.x, position.y);
    }

    public boolean hasPowerPrecise(final int x, final int y) {
        return hasPowerPrecise(new Position(x, y));
    }

    public boolean hasPowerPrecise(final int x, final int y, final UnitType unitType) {
        return hasPowerPrecise(new Position(x, y), unitType);
    }

    public boolean hasPowerPrecise(final Position position) {
        return hasPowerPrecise(position, UnitType.None);
    }

    public boolean hasPowerPrecise(final Position position, final UnitType unitType) {
        if (!position.isValid(this)) {
            return false;
        }
        return hasPower(position.x, position.y, unitType, self().getUnits().stream().filter(u -> u.getType() == Protoss_Pylon).collect(Collectors.toList()));
    }

    public boolean hasPower(final int tileX, final int tileY) {
        return hasPower(new TilePosition(tileX, tileY));
    }

    public boolean hasPower(final int tileX, final int tileY, final UnitType unitType) {
        return hasPower(new TilePosition(tileX, tileY), unitType);
    }

    public boolean hasPower(final TilePosition position) {
        return hasPower(position.x, position.y, UnitType.None);
    }

    public boolean hasPower(final TilePosition position, final UnitType unitType) {
        if (unitType.id >= 0 && unitType.id < UnitType.None.id) {
            return hasPowerPrecise(position.x * 32 + unitType.tileWidth() * 16, position.y * 32 + unitType.tileHeight() * 16, unitType);
        }
        return hasPowerPrecise(position.x * 32, position.y * 32, UnitType.None);
    }

    public boolean hasPower(final int tileX, final int tileY, final int tileWidth, final int tileHeight) {
        return hasPower(tileX, tileY, tileWidth, tileHeight, UnitType.Unknown);
    }

    public boolean hasPower(final int tileX, final int tileY, final int tileWidth, final int tileHeight, final UnitType unitType) {
        return hasPowerPrecise(tileX * 32 + tileWidth * 16, tileY * 32 + tileHeight * 16, unitType);
    }

    public boolean hasPower(final TilePosition position, final int tileWidth, final int tileHeight) {
        return hasPower(position.x, position.y, tileWidth, tileHeight);
    }

    public boolean hasPower(final TilePosition position, final int tileWidth, final int tileHeight, final UnitType unitType) {
        return hasPower(position.x, position.y, tileWidth, tileHeight, unitType);
    }

    public boolean canBuildHere(final TilePosition position, final UnitType type, final Unit builder) {
        return canBuildHere(position, type, builder, false);
    }

    public boolean canBuildHere(final TilePosition position, final UnitType type) {
        return canBuildHere(position, type, null);
    }

    public boolean canBuildHere(final TilePosition position, final UnitType type, final Unit builder, final boolean checkExplored) {
        // lt = left top, rb = right bottom
        final TilePosition lt = builder != null && type.isAddon() ?
                position.add(new TilePosition(4, 1)) : // addon build offset
                position;
        final TilePosition rb = lt.add(type.tileSize());

        // Map limit check
        if (!lt.isValid(this) || !(rb.toPosition().subtract(new Position(1, 1)).isValid(this))) {
            return false;
        }

        //if the getUnit is a refinery, we just need to check the set of geysers to see if the position
        //matches one of them (and the type is still vespene geyser)
        if (type.isRefinery()) {
            for (final Unit g : getGeysers()) {
                if (g.getTilePosition().equals(lt)) {
                    return !g.isVisible() || g.getType() == Resource_Vespene_Geyser;
                }
            }
            return false;
        }

        // Tile buildability check
        for (int x = lt.x; x < rb.x; ++x) {
            for (int y = lt.y; y < rb.y; ++y) {
                // Check if tile is buildable/unoccupied and explored.
                if (!isBuildable(x, y) || (checkExplored && !isExplored(x, y))) {
                    return false;
                }
            }
        }

        // Check if builder is capable of reaching the building site
        if (builder != null) {
            if (!builder.getType().isBuilding()) {
                if (!builder.hasPath(lt.toPosition().add(type.tileSize().toPosition().divide(2)))) {
                    return false;
                }
            } else if (!builder.getType().isFlyingBuilding() && type != Zerg_Nydus_Canal && !type.isFlagBeacon()) {
                return false;
            }
        }

        // Ground getUnit dimension check
        if (type != Special_Start_Location) {
            final Position targPos = lt.toPosition().add(type.tileSize().toPosition().divide(2));
            final List<Unit> unitsInRect = getUnitsInRectangle(
                targPos.subtract(new Position(type.dimensionLeft(), type.dimensionUp())),
                targPos.add(new Position(type.dimensionRight(), type.dimensionDown())),
                    u -> !u.isFlying() && !u.isLoaded() && (builder != u || type == Zerg_Nydus_Canal));

            for (final Unit u : unitsInRect) {
                // Addons can be placed over units that can move, pushing them out of the way
                if (!(type.isAddon() && u.getType().canMove())) {
                    return false;
                }
            }

            // Creep Check
            // Note: Zerg structures that don't require creep can still be placed on creep
            final boolean needsCreep = type.requiresCreep();
            if (type.getRace() != Zerg || needsCreep) {
                for (int x = lt.x; x < rb.x; ++x) {
                    for (int y = lt.y; y < rb.y; ++y) {
                        if (needsCreep != hasCreep(x, y)) {
                            return false;
                        }
                    }
                }
            }

            // Power Check
            if (type.requiresPsi() && !hasPower(lt, type)) {
                return false;
            }

        } //don't ignore units

        // Resource Check (CC, Nex, Hatch)
        if (type.isResourceDepot()) {
            for (final Unit m : getStaticMinerals()) {
                final TilePosition tp = m.getInitialTilePosition();
                if ((isVisible(tp) || isVisible(tp.x + 1, tp.y)) && !m.exists()) {
                    continue; // tile position is visible, but mineral is not => mineral does not exist
                }
                if (tp.x > lt.x - 5 &&
                        tp.y > lt.y - 4 &&
                        tp.x < lt.x + 7 &&
                        tp.y < lt.y + 6) {
                    return false;
                }
            }
            for (final Unit g : getStaticGeysers()) {
                final TilePosition tp = g.getInitialTilePosition();
                if (tp.x > lt.x - 7 &&
                        tp.y > lt.y - 5 &&
                        tp.x < lt.x + 7 &&
                        tp.y < lt.y + 6) {
                    return false;
                }
            }
        }

        // A building can build an addon at a different location (i.e. automatically lifts (if not already lifted)
        // then lands at the new location before building the addon), so we need to do similar checks for the
        // location that the building will be when it builds the addon.
        if (builder != null && !builder.getType().isAddon() && type.isAddon()) {
            return canBuildHere(lt.subtract(new TilePosition(4, 1)), builder.getType(), builder, checkExplored);
        }

        //if the build site passes all these tests, return true.
        return true;
    }

    public boolean canMake(final UnitType type) {
        return canMake(type, null);
    }

    public boolean canMake(final UnitType type, final Unit builder) {
        final Player pSelf = self();
        // Error checking
        if (pSelf == null) {
            return false;
        }

        // Check if the unit type is available (UMS game)
        if (!pSelf.isUnitAvailable(type)) {
            return false;
        }

        // Get the required UnitType
        final UnitType requiredType = type.whatBuilds().getKey();

        // do checks if a builder is provided
        if (builder != null) {
            // Check if the owner of the unit is you
            if (!pSelf.equals(builder.getPlayer())) {
                return false;
            }

            final UnitType builderType = builder.getType();
            if (type == Zerg_Nydus_Canal && builderType == Zerg_Nydus_Canal) {
                if (!builder.isCompleted()) {
                    return false;
                }
                return builder.getNydusExit() == null;
            }

            // Check if this unit can actually build the unit type
            if (requiredType == Zerg_Larva && builderType.producesLarva()) {
                if (builder.getLarva().size() == 0) {
                    return false;
                }
            } else if (!builderType.equals(requiredType)) {
                return false;
            }

            // Carrier/Reaver space checking
            int max_amt;
            switch (builderType) {
                case Protoss_Carrier:
                case Hero_Gantrithor:
                    // Get max interceptors
                    max_amt = 4;
                    if (pSelf.getUpgradeLevel(UpgradeType.Carrier_Capacity) > 0 || builderType == Hero_Gantrithor) {
                        max_amt += 4;
                    }

                    // Check if there is room
                    if (builder.getInterceptorCount() + builder.getTrainingQueue().size() >= max_amt) {
                        return false;
                    }
                    break;
                case Protoss_Reaver:
                case Hero_Warbringer:
                    // Get max scarabs
                    max_amt = 5;
                    if (pSelf.getUpgradeLevel(UpgradeType.Reaver_Capacity) > 0 || builderType == Hero_Warbringer) {
                        max_amt += 5;
                    }

                    // check if there is room
                    if (builder.getScarabCount() + builder.getTrainingQueue().size() >= max_amt) {
                        return false;
                    }
                    break;
            }
        } // if builder != nullptr

        // Check if player has enough minerals
        if (pSelf.minerals() < type.mineralPrice()) {
            return false;
        }

        // Check if player has enough gas
        if (pSelf.gas() < type.gasPrice()) {
            return false;
        }

        // Check if player has enough supplies
        final Race typeRace = type.getRace();
        final int supplyRequired = type.supplyRequired() * (type.isTwoUnitsInOneEgg() ? 2 : 1);
        if (supplyRequired > 0 && pSelf.supplyTotal(typeRace) < pSelf.supplyUsed(typeRace) + supplyRequired - (requiredType.getRace() == typeRace ? requiredType.supplyRequired() : 0)) {
            return false;
        }

        UnitType addon = UnitType.None;
        final Map<UnitType, Integer> reqUnits = type.requiredUnits();
        for (final UnitType ut : type.requiredUnits().keySet()) {
            if (ut.isAddon()) {
                addon = ut;
            }

            if (!pSelf.hasUnitTypeRequirement(ut, reqUnits.get(ut))) {
                return false;
            }
        }

        if (type.requiredTech() != TechType.None && !pSelf.hasResearched(type.requiredTech())) {
            return false;
        }

        return builder == null ||
                addon == UnitType.None ||
                addon.whatBuilds().getKey() != type.whatBuilds().getKey() ||
                (builder.getAddon() != null && builder.getAddon().getType() == addon);
    }

    public boolean canResearch(final TechType type, final Unit unit) {
        return canResearch(type, unit, true);
    }

    public boolean canResearch(final TechType type) {
        return canResearch(type, null);
    }

    public boolean canResearch(final TechType type, final Unit unit, final boolean checkCanIssueCommandType) {
        final Player self = self();
        // Error checking
        if (self == null) {
            return false;
        }

        if (unit != null) {
            if (!unit.getPlayer().equals(self)) {
                return false;
            }

            if (!unit.getType().isSuccessorOf(type.whatResearches())) {
                return false;
            }

            if (checkCanIssueCommandType && (unit.isLifted() || !unit.isIdle() || !unit.isCompleted())) {
                return false;
            }
        }
        if (self.isResearching(type)) {
            return false;
        }

        if (self.hasResearched(type)) {
            return false;
        }

        if (!self.isResearchAvailable(type)) {
            return false;
        }

        if (self.minerals() < type.mineralPrice()) {
            return false;
        }

        if (self.gas() < type.gasPrice()) {
            return false;
        }

        return self.hasUnitTypeRequirement(type.requiredUnit());

    }

    public boolean canUpgrade(final UpgradeType type, final Unit unit) {
        return canUpgrade(type, unit, true);
    }

    public boolean canUpgrade(final UpgradeType type) {
        return canUpgrade(type, null);
    }

    public boolean canUpgrade(final UpgradeType type, final Unit unit, final boolean checkCanIssueCommandType) {
        final Player self = self();
        if (self == null) {
            return false;
        }

        if (unit != null) {
            if (!unit.getPlayer().equals(self)) {
                return false;
            }

            if (!unit.getType().isSuccessorOf(type.whatUpgrades())) {
                return false;
            }

            if (checkCanIssueCommandType && (unit.isLifted() || !unit.isIdle() || !unit.isCompleted())) {
                return false;
            }
        }

        if (!self.hasUnitTypeRequirement(type.whatUpgrades())) {
            return false;
        }

        final int nextLvl = self.getUpgradeLevel(type) + 1;

        if (!self.hasUnitTypeRequirement(type.whatsRequired(nextLvl))) {
            return false;
        }

        if (self.isUpgrading(type)) {
            return false;
        }

        if (self.getUpgradeLevel(type) >= self.getMaxUpgradeLevel(type)) {
            return false;
        }

        if (self.minerals() < type.mineralPrice(nextLvl)) {
            return false;
        }

        return self.gas() >= type.gasPrice(nextLvl);
    }

    public List<TilePosition> getStartLocations() {
        return startLocations;
    }

    public void printf(final String cstr_format) {
        addCommand(Printf, client.addString(cstr_format), 0);
    }

    public void sendText(final String cstr_format) {
        addCommand(SendText, client.addString(cstr_format), 0);
    }

    public void sendTextEx(final boolean toAllies, final String cstr_format) {
        addCommand(SendText, client.addString(cstr_format), toAllies ? 1 : 0);
    }

    public boolean isInGame() {
        return gameData.isInGame();
    }

    public boolean isMultiplayer() {
        return multiplayer;
    }

    public boolean isBattleNet() {
        return battleNet;
    }

    public boolean isPaused() {
        return gameData.isPaused();
    }

    public boolean isReplay() {
        return replay;
    }

    public void pauseGame() {
        addCommand(PauseGame, 0, 0);
    }

    public void resumeGame() {
        addCommand(ResumeGame, 0, 0);
    }

    public void leaveGame() {
        addCommand(LeaveGame, 0, 0);
    }

    public void restartGame() {
        addCommand(RestartGame, 0, 0);
    }

    public void setLocalSpeed(final int speed) {
        addCommand(SetLocalSpeed, speed, 0);
    }

    public boolean issueCommand(final Collection<Unit> units, final UnitCommand command) {
        return !units.stream()
                .map(u -> u.issueCommand(command))
                .collect(Collectors.toList())
                .contains(false);
    }

    public List<Unit> getSelectedUnits() {
        return IntStream.range(0, gameData.getSelectedUnitCount())
                .mapToObj(i -> units[gameData.getSelectedUnits(i)])
                .collect(Collectors.toList());
    }

    public Player self() {
        return self;
    }

    public Player enemy() {
        return enemy;
    }

    public Player neutral() {
        return neutral;
    }

    public List<Player> allies() {
        return allies;
    }

    public List<Player> enemies() {
        return enemies;
    }

    public List<Player> observers() {
        return observers;
    }

    public void drawText(final CoordinateType ctype, final int x, final int y, final String cstr_format) {
        final int stringId = client.addString(cstr_format);
        addShape(ShapeType.Text, ctype, x, y, 0, 0, stringId, textSize.id, 0, false);
    }

    public void drawTextMap(final int x, final int y, final String cstr_format) {
        drawText(CoordinateType.Map, x, y, cstr_format);
    }

    public void drawTextMap(final Position p, final String cstr_format) {
        drawTextMap(p.x, p.y, cstr_format);
    }

    public void drawTextMouse(final int x, final int y, final String cstr_format) {
        drawText(CoordinateType.Mouse, x, y, cstr_format);
    }

    public void drawTextMouse(final Position p, final String cstr_format) {
        drawTextMouse(p.x, p.y, cstr_format);

    }

    public void drawTextScreen(final int x, final int y, final String cstr_format) {
        drawText(CoordinateType.Screen, x, y, cstr_format);
    }

    public void drawTextScreen(final Position p, final String cstr_format) {
        drawTextScreen(p.x, p.y, cstr_format);
    }

    public void drawBox(final CoordinateType ctype, final int left, final int top, final int right, final int bottom, final Color color) {
        drawBox(ctype, left, top, right, bottom, color, false);
    }

    public void drawBox(final CoordinateType ctype, final int left, final int top, final int right, final int bottom, final Color color, final boolean isSolid) {
        addShape(ShapeType.Box, ctype, left, top, right, bottom, 0, 0, color.id, isSolid);
    }

    public void drawBoxMap(int left, int top, int right, int bottom, Color color) {
        drawBox(CoordinateType.Map, left, top, right, bottom, color);
    }

    public void drawBoxMap(int left, int top, int right, int bottom, Color color, boolean isSolid) {
        drawBox(CoordinateType.Map, left, top, right, bottom, color, isSolid);
    }

    public void drawBoxMap(Position leftTop, Position rightBottom, Color color) {
        drawBox(CoordinateType.Map, leftTop.x, leftTop.y, rightBottom.x, rightBottom.y, color);
    }

    public void drawBoxMap(Position leftTop, Position rightBottom, Color color, boolean isSolid) {
        drawBox(CoordinateType.Map, leftTop.x, leftTop.y, rightBottom.x, rightBottom.y, color, isSolid);
    }

    public void drawBoxMouse(int left, int top, int right, int bottom, Color color) {
        drawBox(CoordinateType.Mouse, left, top, right, bottom, color);
    }

    public void drawBoxMouse(int left, int top, int right, int bottom, Color color, boolean isSolid) {
        drawBox(CoordinateType.Mouse, left, top, right, bottom, color, isSolid);
    }

    public void drawBoxMouse(Position leftTop, Position rightBottom, Color color) {
        drawBox(CoordinateType.Mouse, leftTop.x, leftTop.y, rightBottom.x, rightBottom.y, color);
    }

    public void drawBoxMouse(Position leftTop, Position rightBottom, Color color, boolean isSolid) {
        drawBox(CoordinateType.Mouse, leftTop.x, leftTop.y, rightBottom.x, rightBottom.y, color, isSolid);
    }

    public void drawBoxScreen(int left, int top, int right, int bottom, Color color) {
        drawBox(CoordinateType.Screen, left, top, right, bottom, color);
    }

    public void drawBoxScreen(int left, int top, int right, int bottom, Color color, boolean isSolid) {
        drawBox(CoordinateType.Screen, left, top, right, bottom, color, isSolid);
    }

    public void drawBoxScreen(Position leftTop, Position rightBottom, Color color) {
        drawBox(CoordinateType.Screen, leftTop.x, leftTop.y, rightBottom.x, rightBottom.y, color);
    }

    public void drawBoxScreen(Position leftTop, Position rightBottom, Color color, boolean isSolid) {
        drawBox(CoordinateType.Screen, leftTop.x, leftTop.y, rightBottom.x, rightBottom.y, color, isSolid);
    }

    public void drawTriangle(CoordinateType ctype, int ax, int ay, int bx, int by, int cx, int cy, Color color) {
        drawTriangle(ctype, ax, ay, bx, by, cx, cy, color, false);
    }

    public void drawTriangle(CoordinateType ctype, int ax, int ay, int bx, int by, int cx, int cy, Color color, boolean isSolid) {
        addShape(ShapeType.Triangle, ctype, ax, ay, bx, by, cx, cy, color.id, isSolid);
    }

    public void drawTriangleMap(int ax, int ay, int bx, int by, int cx, int cy, Color color) {
        drawTriangle(CoordinateType.Map, ax, ay, bx, by, cx, cy, color);
    }

    public void drawTriangleMap(int ax, int ay, int bx, int by, int cx, int cy, Color color, boolean isSolid) {
        drawTriangle(CoordinateType.Map, ax, ay, bx, by, cx, cy, color, isSolid);
    }

    public void drawTriangleMap(Position a, Position b, Position c, Color color) {
        drawTriangle(CoordinateType.Map, a.x, a.y, b.x, b.y, c.x, c.y, color);
    }

    public void drawTriangleMap(Position a, Position b, Position c, Color color, boolean isSolid) {
        drawTriangle(CoordinateType.Map, a.x, a.y, b.x, b.y, c.x, c.y, color, isSolid);
    }

    public void drawTriangleMouse(int ax, int ay, int bx, int by, int cx, int cy, Color color) {
        drawTriangle(CoordinateType.Mouse, ax, ay, bx, by, cx, cy, color);
    }

    public void drawTriangleMouse(int ax, int ay, int bx, int by, int cx, int cy, Color color, boolean isSolid) {
        drawTriangle(CoordinateType.Mouse, ax, ay, bx, by, cx, cy, color, isSolid);
    }

    public void drawTriangleMouse(Position a, Position b, Position c, Color color) {
        drawTriangle(CoordinateType.Mouse, a.x, a.y, b.x, b.y, c.x, c.y, color);
    }

    public void drawTriangleMouse(Position a, Position b, Position c, Color color, boolean isSolid) {
        drawTriangle(CoordinateType.Mouse, a.x, a.y, b.x, b.y, c.x, c.y, color, isSolid);

    }

    public void drawTriangleScreen(int ax, int ay, int bx, int by, int cx, int cy, Color color) {
        drawTriangle(CoordinateType.Screen, ax, ay, bx, by, cx, cy, color);
    }

    public void drawTriangleScreen(int ax, int ay, int bx, int by, int cx, int cy, Color color, boolean isSolid) {
        drawTriangle(CoordinateType.Screen, ax, ay, bx, by, cx, cy, color, isSolid);
    }

    public void drawTriangleScreen(Position a, Position b, Position c, Color color) {
        drawTriangle(CoordinateType.Screen, a.x, a.y, b.x, b.y, c.x, c.y, color);
    }

    public void drawTriangleScreen(Position a, Position b, Position c, Color color, boolean isSolid) {
        drawTriangle(CoordinateType.Screen, a.x, a.y, b.x, b.y, c.x, c.y, color, isSolid);
    }

    public void drawCircle(CoordinateType ctype, int x, int y, int radius, Color color) {
        drawCircle(ctype, x, y, radius, color, false);
    }

    public void drawCircle(CoordinateType ctype, int x, int y, int radius, Color color, boolean isSolid) {
        addShape(ShapeType.Circle, ctype, x, y, 0, 0, radius, 0, color.id, isSolid);
    }

    public void drawCircleMap(int x, int y, int radius, Color color) {
        drawCircle(CoordinateType.Map, x, y, radius, color);
    }

    public void drawCircleMap(int x, int y, int radius, Color color, boolean isSolid) {
        drawCircle(CoordinateType.Map, x, y, radius, color, isSolid);
    }

    public void drawCircleMap(Position p, int radius, Color color) {
        drawCircle(CoordinateType.Map, p.x, p.y, radius, color);
    }

    public void drawCircleMap(Position p, int radius, Color color, boolean isSolid) {
        drawCircle(CoordinateType.Map, p.x, p.y, radius, color, isSolid);
    }

    public void drawCircleMouse(int x, int y, int radius, Color color) {
        drawCircle(CoordinateType.Mouse, x, y, radius, color);
    }

    public void drawCircleMouse(int x, int y, int radius, Color color, boolean isSolid) {
        drawCircle(CoordinateType.Mouse, x, y, radius, color, isSolid);
    }

    public void drawCircleMouse(Position p, int radius, Color color) {
        drawCircle(CoordinateType.Mouse, p.x, p.y, radius, color);
    }

    public void drawCircleMouse(Position p, int radius, Color color, boolean isSolid) {
        drawCircle(CoordinateType.Mouse, p.x, p.y, radius, color, isSolid);
    }

    public void drawCircleScreen(int x, int y, int radius, Color color) {
        drawCircle(CoordinateType.Screen, x, y, radius, color);
    }

    public void drawCircleScreen(int x, int y, int radius, Color color, boolean isSolid) {
        drawCircle(CoordinateType.Screen, x, y, radius, color, isSolid);
    }

    public void drawCircleScreen(Position p, int radius, Color color) {
        drawCircle(CoordinateType.Screen, p.x, p.y, radius, color);
    }

    public void drawCircleScreen(Position p, int radius, Color color, boolean isSolid) {
        drawCircle(CoordinateType.Screen, p.x, p.y, radius, color, isSolid);
    }

    public void drawEllipse(CoordinateType ctype, int x, int y, int xrad, int yrad, Color color) {
        drawEllipse(ctype, x, y, xrad, yrad, color, false);
    }

    public void drawEllipse(CoordinateType ctype, int x, int y, int xrad, int yrad, Color color, boolean isSolid) {
        addShape(ShapeType.Ellipse, ctype, x, y, 0, 0, xrad, yrad, color.id, isSolid);
    }

    public void drawEllipseMap(int x, int y, int xrad, int yrad, Color color) {
        drawEllipse(CoordinateType.Map, x, y, xrad, yrad, color);
    }

    public void drawEllipseMap(int x, int y, int xrad, int yrad, Color color, boolean isSolid) {
        drawEllipse(CoordinateType.Map, x, y, xrad, yrad, color, isSolid);
    }

    public void drawEllipseMap(Position p, int xrad, int yrad, Color color) {
        drawEllipse(CoordinateType.Map, p.x, p.y, xrad, yrad, color);
    }

    public void drawEllipseMap(Position p, int xrad, int yrad, Color color, boolean isSolid) {
        drawEllipse(CoordinateType.Map, p.x, p.y, xrad, yrad, color, isSolid);
    }

    public void drawEllipseMouse(int x, int y, int xrad, int yrad, Color color) {
        drawEllipse(CoordinateType.Mouse, x, y, xrad, yrad, color);
    }

    public void drawEllipseMouse(int x, int y, int xrad, int yrad, Color color, boolean isSolid) {
        drawEllipse(CoordinateType.Mouse, x, y, xrad, yrad, color, isSolid);
    }

    public void drawEllipseMouse(Position p, int xrad, int yrad, Color color) {
        drawEllipse(CoordinateType.Mouse, p.x, p.y, xrad, yrad, color);
    }

    public void drawEllipseMouse(Position p, int xrad, int yrad, Color color, boolean isSolid) {
        drawEllipse(CoordinateType.Mouse, p.x, p.y, xrad, yrad, color, isSolid);
    }

    public void drawEllipseScreen(int x, int y, int xrad, int yrad, Color color) {
        drawEllipse(CoordinateType.Screen, x, y, xrad, yrad, color);
    }

    public void drawEllipseScreen(int x, int y, int xrad, int yrad, Color color, boolean isSolid) {
        drawEllipse(CoordinateType.Mouse, x, y, xrad, yrad, color, isSolid);
    }

    public void drawEllipseScreen(Position p, int xrad, int yrad, Color color) {
        drawEllipse(CoordinateType.Mouse, p.x, p.y, xrad, yrad, color);
    }

    public void drawEllipseScreen(Position p, int xrad, int yrad, Color color, boolean isSolid) {
        drawEllipse(CoordinateType.Mouse, p.x, p.y, xrad, yrad, color, isSolid);
    }

    public void drawDot(CoordinateType ctype, int x, int y, Color color) {
        addShape(ShapeType.Dot, ctype, x, y, 0, 0, 0, 0, color.id, false);
    }

    public void drawDotMap(int x, int y, Color color) {
        drawDot(CoordinateType.Map, x, y, color);
    }

    public void drawDotMap(Position p, Color color) {
        drawDot(CoordinateType.Map, p.x, p.y, color);
    }

    public void drawDotMouse(int x, int y, Color color) {
        drawDot(CoordinateType.Mouse, x, y, color);
    }

    public void drawDotMouse(Position p, Color color) {
        drawDot(CoordinateType.Mouse, p.x, p.y, color);
    }

    public void drawDotScreen(int x, int y, Color color) {
        drawDot(CoordinateType.Screen, x, y, color);
    }

    public void drawDotScreen(Position p, Color color) {
        drawDot(CoordinateType.Screen, p.x, p.y, color);
    }

    public void drawLine(CoordinateType ctype, int x1, int y1, int x2, int y2, Color color) {
        addShape(ShapeType.Line, ctype, x1, y1, x2, y2, 0, 0, color.id, false);
    }

    public void drawLineMap(int x1, int y1, int x2, int y2, Color color) {
        drawLine(CoordinateType.Map, x1, y1, x2, y2, color);
    }

    public void drawLineMap(Position a, Position b, Color color) {
        drawLine(CoordinateType.Map, a.x, a.y, b.x, b.y, color);
    }

    public void drawLineMouse(int x1, int y1, int x2, int y2, Color color) {
        drawLine(CoordinateType.Mouse, x1, y1, x2, y2, color);

    }

    public void drawLineMouse(Position a, Position b, Color color) {
        drawLine(CoordinateType.Mouse, a.x, a.y, b.x, b.y, color);

    }

    public void drawLineScreen(int x1, int y1, int x2, int y2, Color color) {
        drawLine(CoordinateType.Screen, x1, y1, x2, y2, color);
    }

    public void drawLineScreen(Position a, Position b, Color color) {
        drawLine(CoordinateType.Screen, a.x, a.y, b.x, b.y, color);
    }

    public int getLatencyFrames() {
        return gameData.getLatencyFrames();
    }

    public int getLatencyTime() {
        return gameData.getLatencyTime();
    }

    public int getRemainingLatencyFrames() {
        return gameData.getRemainingLatencyFrames();
    }

    public int getRemainingLatencyTime() {
        return gameData.getRemainingLatencyTime();
    }

    public int getRevision() {
        return revision;
    }

    public boolean isDebug() {
        return debug;
    }

    public boolean isLatComEnabled() {
        return gameData.getHasLatCom();
    }

    public void setLatCom(final boolean isEnabled) {
        addCommand(SetLatCom, isEnabled ? 1 : 0, 0);
    }

    public int getInstanceNumber() {
        return gameData.getInstanceID();
    }

    public int getAPM() {
        return getAPM(false);
    }

    public int getAPM(final boolean includeSelects) {
        return includeSelects ? gameData.getBotAPM_selects() : gameData.getBotAPM_noselects();
    }

    public void setFrameSkip(int frameSkip) {
        addCommand(SetFrameSkip, frameSkip, 0);
    }

    // If you need these please implement (see addCommand and make a PR to the github repo)
    // public boolean setAlliance(Player player, boolean allied);
    // public boolean setAlliance(Player player);
    // public boolean setAlliance(Player player, boolean allied, boolean alliedVictory);
    // public boolean setVision(Player player, boolean enabled);
    // public void setGUI(bool enabled);
    // public int getLastEventTime();
    // public boolean setMap(final String cstr_mapFileName);
    // public boolean setRevealAll();
    // public boolean setRevealAll(boolean reveal);

    public boolean hasPath(final Position source, final Position destination) {
        if (source.isValid(this) && destination.isValid(this)) {
            final Region rgnA = getRegionAt(source);
            final Region rgnB = getRegionAt(destination);
            return rgnA != null && rgnB != null && rgnA.getRegionGroupID() == rgnB.getRegionGroupID();
        }
        return false;
    }

    public void setTextSize() {
        textSize = TextSize.Default;
    }

    public void setTextSize(final TextSize size) {
        textSize = size;
    }

    public int elapsedTime() {
        return gameData.getElapsedTime();
    }

    public void setCommandOptimizationLevel(final int level) {
        addCommand(SetCommandOptimizerLevel, level, 0);
    }

    public int countdownTimer() {
        return gameData.getCountdownTimer();
    }

    public List<Region> getAllRegions() {
        return regionSet;
    }

    public Region getRegionAt(final int x, final int y) {
        return getRegionAt(new Position(x, y));
    }

    public Region getRegionAt(final Position position) {
        if (!position.isValid(this)) {
            return null;
        }
        final short idx = mapTileRegionID[position.x / 32][position.y / 32];
        if ((idx & 0x2000) != 0) {
            final int index = idx & 0x1FFF;

            if (index >= REGION_DATA_SIZE) {
                return null;
            }
            final int minitileShift = ((position.x & 0x1F) / 8) + ((position.y & 0x1F) / 8) * 4;

            if (((mapSplitTilesMiniTileMask[index] >> minitileShift) & 1) != 0) {
                return getRegion(mapSplitTilesRegion2[index]);
            } else {
                return getRegion(mapSplitTilesRegion1[index]);
            }
        }
        return getRegion(idx);
    }

    public TilePosition getBuildLocation(final UnitType type, final TilePosition desiredPosition, final int maxRange) {
        return getBuildLocation(type, desiredPosition, maxRange, false);
    }

    public TilePosition getBuildLocation(final UnitType type, final TilePosition desiredPosition) {
        return getBuildLocation(type, desiredPosition, 64);
    }

    public TilePosition getBuildLocation(final UnitType type, TilePosition desiredPosition, final int maxRange, final boolean creep) {
        return BuildingPlacer.getBuildLocation(type, desiredPosition, maxRange, creep, this);
    }

    private int getDamageFromImpl(UnitType fromType, UnitType toType, Player fromPlayer, Player toPlayer) {
        // Retrieve appropriate weapon
        final WeaponType wpn = toType.isFlyer() ? fromType.airWeapon() : fromType.groundWeapon();
        if (wpn == WeaponType.None || wpn == WeaponType.Unknown) {
            return 0;
        }

        // Get initial weapon damage
        int dmg = fromPlayer != null ? fromPlayer.damage(wpn) : wpn.damageAmount() * wpn.damageFactor();

        // If we need to calculate using armor
        if (wpn.damageType() != DamageType.Ignore_Armor && toPlayer != null) {
            dmg -= Math.min(dmg, toPlayer.armor(toType));
        }

        return dmg * damageRatio[wpn.damageType().id][toType.size().id] / 256;
    }


    public int getDamageFrom(final UnitType fromType, final UnitType toType, final Player fromPlayer) {
        return getDamageFrom(fromType, toType, fromPlayer, null);
    }

    public int getDamageFrom(final UnitType fromType, final UnitType toType) {
        return getDamageFrom(fromType, toType, null);
    }

    public int getDamageFrom(final UnitType fromType, final UnitType toType, final Player fromPlayer, final Player toPlayer) {
        return getDamageFromImpl(fromType, toType, fromPlayer, toPlayer == null ? self() : toPlayer);
    }

    public int getDamageTo(final UnitType toType, final UnitType fromType, final Player toPlayer) {
        return getDamageTo(toType, fromType, toPlayer, null);
    }

    public int getDamageTo(final UnitType toType, final UnitType fromType) {
        return getDamageTo(toType, fromType, null);
    }

    public int getDamageTo(final UnitType toType, final UnitType fromType, final Player toPlayer, final Player fromPlayer) {
        return getDamageFromImpl(fromType, toType, fromPlayer == null ? self() : fromPlayer, toPlayer);
    }

    //Since 4.2.0
    public int getRandomSeed() {
        return randomSeed;
    }
}
