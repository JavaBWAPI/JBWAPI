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

/**
 * The {@link Game} class is implemented by BWAPI and is the primary means of obtaining all
 * game state information from Starcraft Broodwar. Game state information includes all units,
 * resources, players, forces, bullets, terrain, fog of war, regions, etc.
 */
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

    private Text.Size textSize = Text.Size.Default;
    private boolean latcom = true;


    Game(Client client) {
        this.client = client;
        this.gameData = client.gameData();
    }

    Client getClient() {
        return client;
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
            bullets[id] = new Bullet(gameData.getBullets(id), id, this);
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

        randomSeed = gameData.getRandomSeed();

        revision = gameData.getRevision();
        debug = gameData.isDebug();
        replay = gameData.isReplay();
        neutral = players[gameData.getNeutral()];
        self = isReplay() ? null : players[gameData.getSelf()];
        enemy = isReplay() ? null : players[gameData.getEnemy()];
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

        if (isReplay()) {
            enemies = Collections.emptyList();
            allies = Collections.emptyList();
            observers = Collections.emptyList();
        }
        else {
            enemies = playerSet.stream().filter(p -> !p.equals(self()) && self().isEnemy(p))
                    .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
            allies = playerSet.stream().filter(p -> !p.equals(self()) && self().isAlly(p))
                    .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));

            observers = playerSet.stream().filter(p -> !p.equals(self()) && p.isObserver())
                    .collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
        }
        setLatCom(true);
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

    /**
     * Retrieves the set of all teams/forces. Forces are commonly seen in @UMS
     * game types and some others such as @TvB and the team versions of game types.
     *
     * @return List<Force> containing all forces in the game.
     */
    public List<Force> getForces() {
        return forceSet;
    }

    /**
     * Retrieves the set of all players in the match. This includes the neutral
     * player, which owns all the resources and critters by default.
     *
     * @return List<Player> containing all players in the game.
     */
    public List<Player> getPlayers() {
        return playerSet;
    }

    /**
     * Retrieves the set of all accessible units.
     * If {@link Flag#CompleteMapInformation} is enabled, then the set also includes units that are not
     * visible to the player.
     * <p>
     * Units that are inside refineries are not included in this set.
     *
     * @return List<Unit> containing all known units in the game.
     */
    public List<Unit> getAllUnits() {
        return allUnits;
    }

    /**
     * Retrieves the set of all accessible @minerals in the game.
     *
     * @return List<Unit> containing @minerals
     */
    public List<Unit> getMinerals() {
        return getAllUnits().stream()
                .filter(u -> u.getType().isMineralField())
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the set of all accessible @geysers in the game.
     *
     * @return List<Unit> containing @geysers
     */
    public List<Unit> getGeysers() {
        return getAllUnits().stream()
                .filter(u -> u.getType() == Resource_Vespene_Geyser)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the set of all accessible neutral units in the game. This
     * includes @minerals, @geysers, and @critters.
     *
     * @return List<Unit> containing all neutral units.
     */
    public List<Unit> getNeutralUnits() {
        return getAllUnits().stream()
                .filter(u -> u.getPlayer().equals(neutral()))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the set of all @minerals that were available at the beginning of the
     * game.
     * <p>
     * This set includes resources that have been mined out or are inaccessible.
     *
     * @return List<Unit> containing static @minerals
     */
    public List<Unit> getStaticMinerals() {
        return staticMinerals;
    }

    /**
     * Retrieves the set of all @geysers that were available at the beginning of the
     * game.
     * <p>
     * This set includes resources that are inaccessible.
     *
     * @return List<Unit> containing static @geysers
     */
    public List<Unit> getStaticGeysers() {
        return staticGeysers;
    }

    /**
     * Retrieves the set of all units owned by the neutral player (resources, critters,
     * etc.) that were available at the beginning of the game.
     * <p>
     * This set includes units that are inaccessible.
     *
     * @return List<Unit> containing static neutral units
     */
    public List<Unit> getStaticNeutralUnits() {
        return staticNeutralUnits;
    }

    /**
     * Retrieves the set of all accessible bullets.
     *
     * @return List<Bullet> containing all accessible {@link Bullet} objects.
     */
    public List<Bullet> getBullets() {
        return Arrays.stream(bullets)
                .filter(Bullet::exists)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the set of all accessible @Nuke dots.
     * <p>
     * Nuke dots are the red dots painted by a @Ghost when using the nuclear strike ability.
     *
     * @return Set of Positions giving the coordinates of nuke locations.
     */
    public List<Position> getNukeDots() {
        return IntStream.range(0, gameData.getNukeDotCount())
                .mapToObj(id -> new Position(gameData.getNukeDots(id)))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the {@link Force} object associated with a given identifier.
     *
     * @param forceID The identifier for the Force object.
     * @return {@link Force} object mapped to the given forceID. Returns null if the given identifier is invalid.
     */
    public Force getForce(final int forceID) {
        if (forceID < 0 || forceID >= forces.length) {
            return null;
        }
        return forces[forceID];
    }

    /**
     * Retrieves the {@link Player} object associated with a given identifier.
     *
     * @param playerID The identifier for the {@link Player} object.
     * @return {@link Player} object mapped to the given playerID. null if the given identifier is invalid.
     */
    public Player getPlayer(final int playerID) {
        if (playerID < 0 || playerID >= players.length) {
            return null;
        }
        return players[playerID];
    }

    /**
     * Retrieves the {@link Unit} object associated with a given identifier.
     *
     * @param unitID The identifier for the {@link Unit} object.
     * @return {@link Unit} object mapped to the given unitID. null if the given identifier is invalid.
     */
    public Unit getUnit(final int unitID) {
        if (unitID < 0 || unitID >= units.length) {
            return null;
        }
        return units[unitID];
    }

    /**
     * Retrieves the {@link Region} object associated with a given identifier.
     *
     * @param regionID The identifier for the {@link Region} object.
     * @return {@link Region} object mapped to the given regionID. Returns null if the given ID is invalid.
     */
    public Region getRegion(final int regionID) {
        if (regionID < 0 || regionID >= regions.length) {
            return null;
        }
        return regions[regionID];
    }

    /**
     * Retrieves the {@link GameType} of the current game.
     *
     * @return {@link GameType} indicating the rules of the match.
     * @see GameType
     */
    public GameType getGameType() {
        return GameType.idToEnum[gameData.getGameType()];
    }

    /**
     * Retrieves the current latency setting that the game is set to. {@link Latency}
     * indicates the delay between issuing a command and having it processed.
     *
     * @return The {@link Latency} setting of the game, which is of Latency.
     * @see Latency
     */
    public Latency getLatency() {
        return Latency.idToEnum[gameData.getLatency()];
    }

    /**
     * Retrieves the number of logical frames since the beginning of the match.
     * If the game is paused, then getFrameCount will not increase.
     *
     * @return Number of logical frames that have elapsed since the game started as an integer.
     */
    public int getFrameCount() {
        return gameData.getFrameCount();
    }

    /**
     * Retrieves the maximum number of logical frames that have been recorded in a
     * replay. If the game is not a replay, then the value returned is undefined.
     *
     * @return The number of logical frames that the replay contains.
     */
    public int getReplayFrameCount() {
        return gameData.getReplayFrameCount();
    }

    /**
     * Retrieves the logical frame rate of the game in frames per second (FPS).
     *
     * @return Logical frames per second that the game is currently running at as an integer.
     * @see #getAverageFPS
     */
    public int getFPS() {
        return gameData.getFps();
    }

    /**
     * Retrieves the average logical frame rate of the game in frames per second (FPS).
     *
     * @return Average logical frames per second that the game is currently running at as a
     * double.
     * @see #getFPS
     */
    public double getAverageFPS() {
        return gameData.getAverageFPS();
    }

    /**
     * Retrieves the position of the user's mouse on the screen, in {@link Position} coordinates.
     *
     * @return {@link Position} indicating the location of the mouse. Returns {@link Position#Unknown} if {@link Flag#UserInput} is disabled.
     */
    public Position getMousePosition() {
        return new Position(gameData.getMouseX(), gameData.getMouseY());
    }

    /**
     * Retrieves the state of the given mouse button.
     *
     * @param button A {@link MouseButton} enum member indicating which button on the mouse to check.
     * @return A boolean indicating the state of the given button. true if the button was pressed
     * and false if it was not. Returns false always if {@link Flag#UserInput} is disabled.
     * @see MouseButton
     */
    public boolean getMouseState(final MouseButton button) {
        return gameData.getMouseState(button.id);
    }

    /**
     * Retrieves the state of the given keyboard key.
     *
     * @param key A {@link Key} enum member indicating which key on the keyboard to check.
     * @return A boolean indicating the state of the given key. true if the key was pressed
     * and false if it was not. Returns false always if {@link Flag#UserInput} is disabled.
     * @see Key
     */
    public boolean getKeyState(final Key key) {
        return gameData.getKeyState(key.id);
    }

    /**
     * Retrieves the top left position of the viewport from the top left corner of the
     * map, in pixels.
     *
     * @return {@link Position} containing the coordinates of the top left corner of the game's viewport. Returns {@link Position#Unknown} always if {@link Flag#UserInput} is disabled.
     * @see #setScreenPosition
     */
    public Position getScreenPosition() {
        return new Position(gameData.getScreenX(), gameData.getScreenY());
    }

    public void setScreenPosition(final Position p) {
        setScreenPosition(p.x, p.y);
    }

    /**
     * Moves the top left corner of the viewport to the provided position relative to
     * the map's origin (top left (0,0)).
     *
     * @param x The x coordinate to move the screen to, in pixels.
     * @param y The y coordinate to move the screen to, in pixels.
     * @see #getScreenPosition
     */
    public void setScreenPosition(final int x, final int y) {
        addCommand(SetScreenPosition, x, y);
    }

    /**
     * Pings the minimap at the given position. Minimap pings are visible to
     * allied players.
     *
     * @param x The x coordinate to ping at, in pixels, from the map's origin (left).
     * @param y The y coordinate to ping at, in pixels, from the map's origin (top).
     */
    public void pingMinimap(final int x, final int y) {
        addCommand(PingMinimap, x, y);
    }

    public void pingMinimap(final Position p) {
        pingMinimap(p.x, p.y);
    }

    /**
     * Checks if the state of the given flag is enabled or not.
     * <p>
     * Flags may only be enabled at the start of the match during the {@link BWEventListener#onStart}
     * callback.
     *
     * @param flag The {@link Flag} entry describing the flag's effects on BWAPI.
     * @return true if the given flag is enabled, false if the flag is disabled.
     * @see Flag
     */
    public boolean isFlagEnabled(final Flag flag) {
        return gameData.getFlags(flag.id);
    }

    /**
     * Enables the state of a given flag.
     * <p>
     * Flags may only be enabled at the start of the match during the {@link BWEventListener#onStart}
     * callback.
     *
     * @param flag The {@link Flag} entry describing the flag's effects on BWAPI.
     * @see Flag
     */
    public void enableFlag(final Flag flag) {
        addCommand(EnableFlag, flag.id, 1);
    }

    public List<Unit> getUnitsOnTile(final TilePosition tile) {
        return getUnitsOnTile(tile.x, tile.y);
    }

    public List<Unit> getUnitsOnTile(final int tileX, final int tileY) {
        return getUnitsOnTile(tileX, tileY, u -> true);
    }

    /**
     * Retrieves the set of accessible units that are on a given build tile.
     *
     * @param tileX The X position, in tiles.
     * @param tileY The Y position, in tiles.
     * @param pred  A function predicate that indicates which units are included in the returned set.
     * @return A List<Unit> object consisting of all the units that have any part of them on the
     * given build tile.
     */
    public List<Unit> getUnitsOnTile(final int tileX, final int tileY, final UnitFilter pred) {
        return getAllUnits().stream().filter(u -> {
            final TilePosition tp = u.getTilePosition();
            return tp.x == tileX && tp.y == tileY && pred.test(u);
        }).collect(Collectors.toList());
    }

    public List<Unit> getUnitsInRectangle(final int left, final int top, final int right, final int bottom) {
        return getUnitsInRectangle(left, top, right, bottom, u -> true);
    }

    /**
     * Retrieves the set of accessible units that are in a given rectangle.
     *
     * @param left   The X coordinate of the left position of the bounding box, in pixels.
     * @param top    The Y coordinate of the top position of the bounding box, in pixels.
     * @param right  The X coordinate of the right position of the bounding box, in pixels.
     * @param bottom The Y coordinate of the bottom position of the bounding box, in pixels.
     * @param pred   A function predicate that indicates which units are included in the returned set.
     * @return A List<Unit> object consisting of all the units that have any part of them within the
     * given rectangle bounds.
     */
    public List<Unit> getUnitsInRectangle(final int left, final int top, final int right, final int bottom, final UnitFilter pred) {
        return getAllUnits().stream()
                .filter(u -> left <= u.getRight() && top <= u.getBottom() && right >= u.getLeft() && bottom >= u.getTop() && pred.test(u))
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

    /**
     * Retrieves the set of accessible units that are within a given radius of a
     * position.
     *
     * @param x      The x coordinate of the center, in pixels.
     * @param y      The y coordinate of the center, in pixels.
     * @param radius The radius from the center, in pixels, to include units.
     * @param pred   A function predicate that indicates which units are included in the returned set.
     * @return A List<Unit> object consisting of all the units that have any part of them within the
     * given radius from the center position.
     */
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

    /**
     * Retrieves the closest unit to center that matches the criteria of the callback
     * pred within an optional rectangle.
     *
     * @param center The position to start searching for the closest unit.
     * @param pred   The {@link UnitFilter} predicate to determine which units should be included. This includes all units by default.
     * @param left   The left position of the rectangle. This value is 0 by default.
     * @param top    The top position of the rectangle. This value is 0 by default.
     * @param right  The right position of the rectangle. This value includes the entire map width by default.
     * @param bottom The bottom position of the rectangle. This value includes the entire map height by default.
     * @see UnitFilter
     */
    public Unit getClosestUnitInRectangle(final Position center, final int left, final int top, final int right, final int bottom, final UnitFilter pred) {
        return getUnitsInRectangle(left, top, right, bottom, pred).stream()
                .min(Comparator.comparingInt(u -> u.getDistance(center)))
                .orElse(null);
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

    /**
     * Retrieves the closest unit to center that matches the criteria of the callback
     * pred within an optional radius.
     *
     * @param center The position to start searching for the closest unit.
     * @param pred   The UnitFilter predicate to determine which units should be included. This includes all units by default.
     * @param radius The radius to search in. If omitted, the entire map will be searched.
     * @return The desired unit that is closest to center. Returns null If a suitable unit was not found.
     * @see UnitFilter
     */
    public Unit getClosestUnit(final Position center, final int radius, final UnitFilter pred) {
        return getUnitsInRadius(center, radius, pred).stream()
                .min(Comparator.comparingInt(u -> u.getDistance(center)))
                .orElse(null);
    }

    /**
     * Retrieves the width of the map in build tile units.
     *
     * @return Width of the map in tiles.
     */
    public int mapWidth() {
        return mapWidth;
    }

    /**
     * Retrieves the height of the map in build tile units.
     *
     * @return Height of the map in tiles.
     */
    public int mapHeight() {
        return mapHeight;
    }

    int mapPixelWidth() {
        return mapPixelWidth;
    }

    int mapPixelHeight() {
        return mapPixelHeight;
    }

    /**
     * Retrieves the file name of the currently loaded map.
     *
     * @return Map file name as String object.
     * @see #mapPathName
     * @see #mapName
     */
    public String mapFileName() {
        return mapFileName;
    }

    /**
     * Retrieves the full path name of the currently loaded map.
     *
     * @return Map file name as String object.
     * @see #mapFileName
     * @see #mapName
     */
    public String mapPathName() {
        return mapPathName;
    }

    /**
     * Retrieves the title of the currently loaded map.
     *
     * @return Map title as String object.
     * @see #mapFileName
     * @see #mapPathName
     */
    public String mapName() {
        return mapName;
    }

    /**
     * Calculates the SHA-1 hash of the currently loaded map file.
     *
     * @return String object containing SHA-1 hash.
     * <p>
     * Campaign maps will return a hash of their internal map chunk components(.chk), while
     * standard maps will return a hash of their entire map archive (.scm,.scx).
     */
    public String mapHash() {
        return mapHash;
    }

    /**
     * Checks if the given mini-tile position is walkable.
     * <p>
     * This function only checks if the static terrain is walkable. Its current occupied
     * state is excluded from this check. To see if the space is currently occupied or not, then
     * see {@link #getUnitsInRectangle}.
     *
     * @param walkX The x coordinate of the mini-tile, in mini-tile units (8 pixels).
     * @param walkY The y coordinate of the mini-tile, in mini-tile units (8 pixels).
     * @return true if the mini-tile is walkable and false if it is impassable for ground units.
     */
    public boolean isWalkable(final int walkX, final int walkY) {
        return isWalkable(new WalkPosition(walkX, walkY));
    }

    public boolean isWalkable(final WalkPosition position) {
        if (!position.isValid(this)) {
            return false;
        }
        return walkable[position.x][position.y];
    }

    /**
     * Returns the ground height at the given tile position.
     *
     * @param tileX X position to query, in tiles
     * @param tileY Y position to query, in tiles
     * @return The tile height as an integer. Possible values are:
     * - 0: Low ground
     * - 1: Low ground doodad
     * - 2: High ground
     * - 3: High ground doodad
     * - 4: Very high ground
     * - 5: Very high ground doodad
     * .
     */
    public int getGroundHeight(final int tileX, final int tileY) {
        return getGroundHeight(new TilePosition(tileX, tileY));
    }

    public int getGroundHeight(final TilePosition position) {
        if (!position.isValid(this)) {
            return 0;
        }
        return groundHeight[position.x][position.y];
    }

    public boolean isBuildable(final int tileX, final int tileY) {
        return isBuildable(tileX, tileY, false);
    }

    /**
     * Checks if a given tile position is buildable. This means that, if all
     * other requirements are met, a structure can be placed on this tile. This function uses
     * static map data.
     *
     * @param tileX            The x value of the tile to check.
     * @param tileY            The y value of the tile to check.
     * @param includeBuildings If this is true, then this function will also check if any visible structures are occupying the space. If this value is false, then it only checks the static map data for tile buildability. This value is false by default.
     * @return boolean identifying if the given tile position is buildable (true) or not (false).
     * If includeBuildings was provided, then it will return false if a structure is currently
     * occupying the tile.
     */
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

    /**
     * Checks if a given tile position is visible to the current player.
     *
     * @param tileX The x value of the tile to check.
     * @param tileY The y value of the tile to check.
     * @return boolean identifying the visibility of the tile. If the given tile is visible, then
     * the value is true. If the given tile is concealed by the fog of war, then this value will
     * be false.
     */
    public boolean isVisible(final int tileX, final int tileY) {
        return isVisible(new TilePosition(tileX, tileY));
    }

    public boolean isVisible(final TilePosition position) {
        if (!position.isValid(this)) {
            return false;
        }
        return gameData.isVisible(position.x, position.y);
    }

    /**
     * Checks if a given tile position has been explored by the player. An
     * explored tile position indicates that the player has seen the location at some point in the
     * match, partially revealing the fog of war for the remainder of the match.
     *
     * @param tileX The x tile coordinate to check.
     * @param tileY The y tile coordinate to check.
     * @return true if the player has explored the given tile position (partially revealed fog), false if the tile position was never explored (completely black fog).
     * @see #isVisible
     */
    public boolean isExplored(final int tileX, final int tileY) {
        return isExplored(new TilePosition(tileX, tileY));
    }

    public boolean isExplored(final TilePosition position) {
        if (!position.isValid(this)) {
            return false;
        }
        return gameData.isExplored(position.x, position.y);
    }

    /**
     * Checks if the given tile position has @Zerg creep on it.
     *
     * @param tileX The x tile coordinate to check.
     * @param tileY The y tile coordinate to check.
     * @return true if the given tile has creep on it, false if the given tile does not have creep, or if it is concealed by the fog of war.
     */
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

    /**
     * Checks if the given pixel position is powered by an owned @Protoss_Pylon for an
     * optional unit type.
     *
     * @param x        The x pixel coordinate to check.
     * @param y        The y pixel coordinate to check.
     * @param unitType Checks if the given {@link UnitType} requires power or not. If ommitted, then it will assume that the position requires power for any unit type.
     * @return true if the type at the given position will have power, false if the type at the given position will be unpowered.
     */
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

    /**
     * Checks if the given tile position if powered by an owned @Protoss_Pylon for an
     * optional unit type.
     *
     * @param tileX    The x tile coordinate to check.
     * @param tileY    The y tile coordinate to check.
     * @param unitType Checks if the given UnitType will be powered if placed at the given tile position. If omitted, then only the immediate tile position is checked for power, and the function will assume that the location requires power for any unit type.
     * @return true if the type at the given tile position will receive power, false if the type will be unpowered at the given tile position.
     */
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

    /**
     * Checks if the given unit type can be built at the given build tile position.
     * This function checks for creep, power, and resource distance requirements in addition to
     * the tiles' buildability and possible units obstructing the build location.
     * <p>
     * If the type is an addon and a builer is provided, then the location of the addon will
     * be placed 4 tiles to the right and 1 tile down from the given position. If the builder
     * is not given, then the check for the addon will be conducted at position.
     * <p>
     * If type is UnitType.Special_Start_Location, then the area for a resource depot
     * (@Command_Center, @Hatchery, @Nexus) is checked as normal, but any potential obstructions
     * (existing structures, creep, units, etc.) are ignored.
     *
     * @param position      Indicates the tile position that the top left corner of the structure is intended to go.
     * @param type          The UnitType to check for.
     * @param builder       The intended unit that will build the structure. If specified, then this function will also check if there is a path to the build site and exclude the builder from the set of units that may be blocking the build site.
     * @param checkExplored If this parameter is true, it will also check if the target position has been explored by the current player. This value is false by default, ignoring the explored state of the build site.
     * @return true indicating that the structure can be placed at the given tile position, and
     * false if something may be obstructing the build location.
     */
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

    /**
     * Checks all the requirements in order to make a given unit type for the current
     * player. These include resources, supply, technology tree, availability, and
     * required units.
     *
     * @param type    The {@link UnitType} to check.
     * @param builder The Unit that will be used to build/train the provided unit type. If this value is null or excluded, then the builder will be excluded in the check.
     * @return true indicating that the type can be made. If builder is provided, then it is
     * only true if builder can make the type. Otherwise it will return false, indicating
     * that the unit type can not be made.
     */
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

    /**
     * Checks all the requirements in order to research a given technology type for the
     * current player. These include resources, technology tree, availability, and
     * required units.
     *
     * @param type                     The {@link TechType} to check.
     * @param unit                     The {@link Unit} that will be used to research the provided technology type. If this value is null or excluded, then the unit will be excluded in the check.
     * @param checkCanIssueCommandType TODO fill this in
     * @return true indicating that the type can be researched. If unit is provided, then it is
     * only true if unit can research the type. Otherwise it will return false, indicating
     * that the technology can not be researched.
     */
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

    /**
     * Checks all the requirements in order to upgrade a given upgrade type for the
     * current player. These include resources, technology tree, availability, and
     * required units.
     *
     * @param type                     The {@link UpgradeType} to check.
     * @param unit                     The {@link Unit} that will be used to upgrade the provided upgrade type. If this value is null or excluded, then the unit will be excluded in the check.
     * @param checkCanIssueCommandType TODO fill this in
     * @return true indicating that the type can be upgraded. If unit is provided, then it is
     * only true if unit can upgrade the type. Otherwise it will return false, indicating
     * that the upgrade can not be upgraded.
     */
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

    /**
     * Retrieves the set of all starting locations for the current map. A
     * starting location is essentially a candidate for a player's spawn point.
     *
     * @return A List<TilePosition> containing all the {@link TilePosition} objects that indicate a start
     * location.
     * @see Player#getStartLocation
     */
    public List<TilePosition> getStartLocations() {
        return startLocations;
    }

    /**
     * Prints text to the screen as a notification. This function allows text
     * formatting using {@link Text#formatText}.
     * <p>
     * That text printed through this function is not seen by other players or in replays.
     *
     * @param string String to print.
     */
    public void printf(final String string) {
        addCommand(Printf, client.addString(string), 0);
    }

    /**
     * Sends a text message to all other players in the game.
     * <p>
     * In a single player game this function can be used to execute cheat codes.
     *
     * @param string String to send.
     * @see #sendTextEx
     */
    public void sendText(final String string) {
        addCommand(SendText, client.addString(string), 0);
    }

    /**
     * An extended version of {@link #sendText} which allows messages to be forwarded to
     * allies.
     *
     * @param toAllies If this parameter is set to true, then the message is only sent to allied players, otherwise it will be sent to all players.
     * @param string   String to send.
     * @see #sendText
     */
    public void sendTextEx(final boolean toAllies, final String string) {
        addCommand(SendText, client.addString(string), toAllies ? 1 : 0);
    }

    /**
     * Checks if the current client is inside a game.
     *
     * @return true if the client is in a game, and false if it is not.
     */
    public boolean isInGame() {
        return gameData.isInGame();
    }

    /**
     * Checks if the current client is inside a multiplayer game.
     *
     * @return true if the client is in a multiplayer game, and false if it is a single player
     * game, a replay, or some other state.
     */
    public boolean isMultiplayer() {
        return multiplayer;
    }

    /**
     * Checks if the client is in a game that was created through the Battle.net
     * multiplayer gaming service.
     *
     * @return true if the client is in a multiplayer Battle.net game and false if it is not.
     */
    public boolean isBattleNet() {
        return battleNet;
    }

    /**
     * Checks if the current game is paused. While paused, {@link BWEventListener#onFrame}
     * will still be called.
     *
     * @return true if the game is paused and false otherwise
     * @see #pauseGame
     * @see #resumeGame
     */
    public boolean isPaused() {
        return gameData.isPaused();
    }

    /**
     * Checks if the client is watching a replay.
     *
     * @return true if the client is watching a replay and false otherwise
     */
    public boolean isReplay() {
        return replay;
    }

    /**
     * Pauses the game. While paused, {@link BWEventListener#onFrame} will still be called.
     *
     * @see #resumeGame
     */
    public void pauseGame() {
        addCommand(PauseGame, 0, 0);
    }

    /**
     * Resumes the game from a paused state.
     *
     * @see #pauseGame
     */
    public void resumeGame() {
        addCommand(ResumeGame, 0, 0);
    }

    /**
     * Leaves the current game by surrendering and enters the post-game statistics/score
     * screen.
     */
    public void leaveGame() {
        addCommand(LeaveGame, 0, 0);
    }

    /**
     * Restarts the match. Works the same as if the match was restarted from
     * the in-game menu (F10). This option is only available in single player games.
     */
    public void restartGame() {
        addCommand(RestartGame, 0, 0);
    }

    /**
     * Sets the number of milliseconds Broodwar spends in each frame. The
     * default values are as follows:
     * - Fastest: 42ms/frame
     * - Faster: 48ms/frame
     * - Fast: 56ms/frame
     * - Normal: 67ms/frame
     * - Slow: 83ms/frame
     * - Slower: 111ms/frame
     * - Slowest: 167ms/frame
     * <p>
     * Specifying a value of 0 will not guarantee that logical frames are executed as fast
     * as possible. If that is the intention, use this in combination with #setFrameSkip.
     * <p>
     * Changing this value will cause the execution of @UMS scenario triggers to glitch.
     * This will only happen in campaign maps and custom scenarios (non-melee).
     *
     * @param speed The time spent per frame, in milliseconds. A value of 0 indicates that frames are executed immediately with no delay. Negative values will restore the default value as listed above.
     * @see #setFrameSkip
     * @see #getFPS
     */
    public void setLocalSpeed(final int speed) {
        addCommand(SetLocalSpeed, speed, 0);
    }

    /**
     * Issues a given command to a set of units. This function automatically
     * splits the set into groups of 12 and issues the same command to each of them. If a unit
     * is not capable of executing the command, then it is simply ignored.
     *
     * @param units   A List<Unit> containing all the units to issue the command for.
     * @param command A {@link UnitCommand} object containing relevant information about the command to be issued. The {@link Unit} object associated with the command will be ignored.
     * @return true if any one of the units in the List<Unit> were capable of executing the
     * command, and false if none of the units were capable of executing the command.
     */
    public boolean issueCommand(final Collection<Unit> units, final UnitCommand command) {
        return units.stream()
                .map(u -> u.issueCommand(command))
                .reduce(false, (a, b) -> a | b);
    }

    /**
     * Retrieves the set of units that are currently selected by the user outside of
     * BWAPI. This function requires that{@link Flag#UserInput} be enabled.
     *
     * @return A List<Unit> containing the user's selected units. If {@link Flag#UserInput} is disabled,
     * then this set is always empty.
     * @see #enableFlag
     */
    public List<Unit> getSelectedUnits() {
        if (!isFlagEnabled(Flag.UserInput)) {
            return Collections.emptyList();
        }
        return IntStream.range(0, gameData.getSelectedUnitCount())
                .mapToObj(i -> units[gameData.getSelectedUnits(i)])
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the player object that BWAPI is controlling.
     *
     * @return Player object representing the current player. null if the current game is a replay.
     */
    public Player self() {
        return self;
    }

    /**
     * Retrieves the {@link Player} interface that represents the enemy player. If
     * there is more than one enemy, and that enemy is destroyed, then this function will still
     * retrieve the same, defeated enemy. If you wish to handle multiple opponents, see the
     * {@link Game#enemies} function.
     *
     * @return Player interface representing an enemy player. Returns null if there is no enemy or the current game is a replay.
     * @see #enemies
     */
    public Player enemy() {
        return enemy;
    }

    /**
     * Retrieves the {@link Player} object representing the neutral player.
     * The neutral player owns all the resources and critters on the map by default.
     *
     * @return {@link Player} indicating the neutral player.
     */
    public Player neutral() {
        return neutral;
    }

    /**
     * Retrieves a set of all the current player's remaining allies.
     *
     * @return List<Player> containing all allied players.
     */
    public List<Player> allies() {
        return allies;
    }

    /**
     * Retrieves a set of all the current player's remaining enemies.
     *
     * @return List<Player> containing all enemy players.
     */
    public List<Player> enemies() {
        return enemies;
    }

    /**
     * Retrieves a set of all players currently observing the game. An observer
     * is defined typically in a @UMS game type as not having any impact on the game. This means
     * an observer cannot start with any units, and cannot have any active trigger actions that
     * create units for it.
     *
     * @return List<Player> containing all currently active observer players
     */
    public List<Player> observers() {
        return observers;
    }

    public void drawText(final CoordinateType ctype, final int x, final int y, final String string) {
        final int stringId = client.addString(string);
        addShape(ShapeType.Text, ctype, x, y, 0, 0, stringId, textSize.id, 0, false);
    }

    public void drawTextMap(final int x, final int y, final String string) {
        drawText(CoordinateType.Map, x, y, string);
    }

    public void drawTextMap(final Position p, final String string) {
        drawTextMap(p.x, p.y, string);
    }

    public void drawTextMouse(final int x, final int y, final String string) {
        drawText(CoordinateType.Mouse, x, y, string);
    }

    public void drawTextMouse(final Position p, final String string) {
        drawTextMouse(p.x, p.y, string);

    }

    public void drawTextScreen(final int x, final int y, final String string) {
        drawText(CoordinateType.Screen, x, y, string);
    }

    public void drawTextScreen(final Position p, final String string) {
        drawTextScreen(p.x, p.y, string);
    }

    public void drawBox(final CoordinateType ctype, final int left, final int top, final int right, final int bottom, final Color color) {
        drawBox(ctype, left, top, right, bottom, color, false);
    }

    /**
     * Draws a rectangle on the screen with the given color.
     *
     * @param ctype   The coordinate type. Indicates the relative position to draw the shape.
     * @param left    The x coordinate, in pixels, relative to ctype, of the left edge of the rectangle.
     * @param top     The y coordinate, in pixels, relative to ctype, of the top edge of the rectangle.
     * @param right   The x coordinate, in pixels, relative to ctype, of the right edge of the rectangle.
     * @param bottom  The y coordinate, in pixels, relative to ctype, of the bottom edge of the rectangle.
     * @param color   The color of the rectangle.
     * @param isSolid If true, then the shape will be filled and drawn as a solid, otherwise it will be drawn as an outline. If omitted, this value will default to false.
     */
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

    /**
     * Draws a triangle on the screen with the given color.
     *
     * @param ctype   The coordinate type. Indicates the relative position to draw the shape.
     * @param ax      The x coordinate, in pixels, relative to ctype, of the first point.
     * @param ay      The y coordinate, in pixels, relative to ctype, of the first point.
     * @param bx      The x coordinate, in pixels, relative to ctype, of the second point.
     * @param by      The y coordinate, in pixels, relative to ctype, of the second point.
     * @param cx      The x coordinate, in pixels, relative to ctype, of the third point.
     * @param cy      The y coordinate, in pixels, relative to ctype, of the third point.
     * @param color   The color of the triangle.
     * @param isSolid If true, then the shape will be filled and drawn as a solid, otherwise it will be drawn as an outline. If omitted, this value will default to false.
     */
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

    /**
     * Draws a circle on the screen with the given color.
     *
     * @param ctype   The coordinate type. Indicates the relative position to draw the shape.
     * @param x       The x coordinate, in pixels, relative to ctype.
     * @param y       The y coordinate, in pixels, relative to ctype.
     * @param radius  The radius of the circle, in pixels.
     * @param color   The color of the circle.
     * @param isSolid If true, then the shape will be filled and drawn as a solid, otherwise it will be drawn as an outline. If omitted, this value will default to false.
     */
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

    /**
     * Draws an ellipse on the screen with the given color.
     *
     * @param ctype   The coordinate type. Indicates the relative position to draw the shape.
     * @param x       The x coordinate, in pixels, relative to ctype.
     * @param y       The y coordinate, in pixels, relative to ctype.
     * @param xrad    The x radius of the ellipse, in pixels.
     * @param yrad    The y radius of the ellipse, in pixels.
     * @param color   The color of the ellipse.
     * @param isSolid If true, then the shape will be filled and drawn as a solid, otherwise it will be drawn as an outline. If omitted, this value will default to false.
     */
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

    /**
     * Draws a dot on the map or screen with a given color.
     *
     * @param ctype The coordinate type. Indicates the relative position to draw the shape.
     * @param x     The x coordinate, in pixels, relative to ctype.
     * @param y     The y coordinate, in pixels, relative to ctype.
     * @param color The color of the dot.
     */
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

    /**
     * Draws a line on the map or screen with a given color.
     *
     * @param ctype The coordinate type. Indicates the relative position to draw the shape.
     * @param x1    The starting x coordinate, in pixels, relative to ctype.
     * @param y1    The starting y coordinate, in pixels, relative to ctype.
     * @param x2    The ending x coordinate, in pixels, relative to ctype.
     * @param y2    The ending y coordinate, in pixels, relative to ctype.
     * @param color The color of the line.
     */
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

    /**
     * Retrieves the maximum delay, in number of frames, between a command being issued
     * and the command being executed by Broodwar.
     * <p>
     * In Broodwar, latency is used to keep the game synchronized between players without
     * introducing lag.
     *
     * @return Difference in frames between commands being sent and executed.
     * @see #getLatencyTime
     * @see #getRemainingLatencyFrames
     */
    public int getLatencyFrames() {
        return gameData.getLatencyFrames();
    }

    /**
     * Retrieves the maximum delay, in milliseconds, between a command being issued and
     * the command being executed by Broodwar.
     *
     * @return Difference in milliseconds between commands being sent and executed.
     * @see #getLatencyFrames
     * @see #getRemainingLatencyTime
     */
    public int getLatencyTime() {
        return gameData.getLatencyTime();
    }

    /**
     * Retrieves the number of frames it will take before a command sent in the current
     * frame will be executed by the game.
     *
     * @return Number of frames until a command is executed if it were sent in the current
     * frame.
     * @see #getRemainingLatencyTime
     * @see #getLatencyFrames
     */
    public int getRemainingLatencyFrames() {
        return gameData.getRemainingLatencyFrames();
    }

    /**
     * Retrieves the number of milliseconds it will take before a command sent in the
     * current frame will be executed by Broodwar.
     *
     * @return Amount of time, in milliseconds, until a command is executed if it were sent in
     * the current frame.
     * @see #getRemainingLatencyFrames
     * @see #getLatencyTime
     */
    public int getRemainingLatencyTime() {
        return gameData.getRemainingLatencyTime();
    }

    /**
     * Retrieves the current revision of BWAPI.
     *
     * @return The revision number of the current BWAPI interface.
     */
    public int getRevision() {
        return revision;
    }

    /**
     * Retrieves the debug state of the BWAPI build.
     *
     * @return true if the BWAPI module is a DEBUG build, and false if it is a RELEASE build.
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * Checks the state of latency compensation.
     *
     * @return true if latency compensation is enabled, false if it is disabled.
     * @see #setLatCom
     */
    public boolean isLatComEnabled() {
        return latcom;
    }

    /**
     * Changes the state of latency compensation. Latency compensation
     * modifies the state of BWAPI's representation of units to reflect the implications of
     * issuing a command immediately after the command was performed, instead of waiting
     * consecutive frames for the results. Latency compensation is enabled by default.
     *
     * @param isEnabled Set whether the latency compensation feature will be enabled (true) or disabled (false).
     * @see #isLatComEnabled
     */
    public void setLatCom(final boolean isEnabled) {
        //update shared memory
        gameData.setHasLatCom(isEnabled);
        //update internal memory
        latcom = isEnabled;
        //update server
        addCommand(SetLatCom, isEnabled ? 1 : 0, 0);
    }

    /**
     * Retrieves the Starcraft instance number recorded by BWAPI to identify which
     * Starcraft instance an AI module belongs to. The very first instance should
     * return 0.
     *
     * @return An integer value representing the instance number.
     */
    public int getInstanceNumber() {
        return gameData.getInstanceID();
    }

    public int getAPM() {
        return getAPM(false);
    }

    /**
     * Retrieves the Actions Per Minute (APM) that the bot is producing.
     *
     * @param includeSelects If true, the return value will include selections as individual commands, otherwise it will exclude selections. This value is false by default.
     * @return The number of actions that the bot has executed per minute, on average.
     */
    public int getAPM(final boolean includeSelects) {
        return includeSelects ? gameData.getBotAPM_selects() : gameData.getBotAPM_noselects();
    }

    /**
     * Sets the number of graphical frames for every logical frame. This
     * allows the game to run more logical frames per graphical frame, increasing the speed at
     * which the game runs.
     *
     * @param frameSkip Number of graphical frames per logical frame. If this value is 0 or less, then it will default to 1.
     * @see #setLocalSpeed
     */
    public void setFrameSkip(int frameSkip) {
        addCommand(SetFrameSkip, Math.max(frameSkip, 1), 0);
    }

    /**
     * Sets the alliance state of the current player with the target player.</summary>
     *
     * @param player        The target player to set alliance with.
     * @param allied        If true, the current player will ally the target player. If false, the current player
     *                      will make the target player an enemy. This value is true by default.
     * @param alliedVictory Sets the state of "allied victory". If true, the game will end in a victory if all
     *                      allied players have eliminated their opponents. Otherwise, the game will only end if
     *                      no other players are remaining in the game. This value is true by default.
     */
    public boolean setAlliance(Player player, boolean allied, boolean alliedVictory) {
        if (self() == null || isReplay() || player == null || player.equals(self())) {
            return false;
        }

        addCommand(CommandType.SetAllies, player.getID(), allied ? (alliedVictory ? 2 : 1) : 0);
        return true;
    }

    public boolean setAlliance(Player player, boolean allied) {
        return setAlliance(player, allied, true);
    }

    public boolean setAlliance(Player player) {
        return setAlliance(player, true);
    }

    /**
     * In a game, this function sets the vision of the current BWAPI player with the
     * target player.
     * <p>
     * In a replay, this function toggles the visibility of the target player.
     *
     * @param player  The target player to toggle vision.
     * @param enabled The vision state. If true, and in a game, the current player will enable shared vision
     *                with the target player, otherwise it will unshare vision. If in a replay, the vision
     *                of the target player will be shown, otherwise the target player will be hidden. This
     *                value is true by default.
     */
    public boolean setVision(Player player, boolean enabled) {
        if (player == null) {
            return false;
        }

        if (!isReplay() && (self() == null || player.equals(self()))) {
            return false;
        }

        addCommand(CommandType.SetVision, player.getID(), enabled ? 1 : 0);
        return true;
    }

    /**
     * Checks if the GUI is enabled.
     * <p>
     * The GUI includes all drawing functions of BWAPI, as well as screen updates from Broodwar.
     *
     * @return true if the GUI is enabled, and everything is visible, false if the GUI is disabled and drawing
     * functions are rejected
     * @see #setGUI
     */
    boolean isGUIEnabled() {
        return gameData.getHasGUI();
    }

    /**
     * Sets the rendering state of the Starcraft GUI.
     * <p>
     * This typically gives Starcraft a very low graphical frame rate and disables all drawing functionality in BWAPI.
     *
     * @param enabled A boolean value that determines the state of the GUI. Passing false to this function
     *                will disable the GUI, and true will enable it.
     * @see #isGUIEnabled
     */
    public void setGUI(boolean enabled) {
        gameData.setHasGUI(enabled);
        //queue up command for server so it also applies the change
        addCommand(CommandType.SetGui, enabled ? 1 : 0, 0);
    }

    /**
     * Retrieves the amount of time (in milliseconds) that has elapsed when running the last AI
     * module callback.
     * <p>
     * This is used by tournament modules to penalize AI modules that use too
     * much processing time.
     *
     * @return Time in milliseconds spent in last AI module call. Returns 0 When called from an AI module.
     */
    public int getLastEventTime() {
        return 0;
    }

    /**
     * Changes the map to the one specified.
     * <p>
     * Once restarted, the game will load the map that was provided.
     * Changes do not take effect unless the game is restarted.
     *
     * @param mapFileName A string containing the path and file name to the desired map.
     * @return Returns true if the function succeeded and has changed the map. Returns false if the function failed,
     * does not have permission from the tournament module, failed to find the map specified, or received an invalid
     * parameter.
     */
    public boolean setMap(final String mapFileName) {
        if (mapFileName == null || mapFileName.length() >= 260 || mapFileName.charAt(0) == 0) {
            return false;
        }

        addCommand(CommandType.SetMap, client.addString(mapFileName), 0);
        return true;
    }

    /**
     * Sets the state of the fog of war when watching a replay.
     *
     * @param reveal The state of the reveal all flag. If false, all fog of war will be enabled. If true,
     *               then the fog of war will be revealed. It is true by default.
     */
    public boolean setRevealAll(boolean reveal) {
        if (!isReplay()) {
            return false;
        }
        addCommand(CommandType.SetRevealAll, reveal ? 1 : 0, 0);
        return true;
    }

    public boolean setRevealAll() {
        return setRevealAll(true);
    }


    /**
     * Checks if there is a path from source to destination. This only checks
     * if the source position is connected to the destination position. This function does not
     * check if all units can actually travel from source to destination. Because of this
     * limitation, it has an O(1) complexity, and cases where this limitation hinders gameplay is
     * uncommon at best.
     * <p>
     * If making queries on a unit, it's better to call {@link Unit#hasPath}, since it is
     * a more lenient version of this function that accounts for some edge cases.
     *
     * @param source      The source position.
     * @param destination The destination position.
     * @return true if there is a path between the two positions, and false if there is not.
     * @see Unit#hasPath
     */
    public boolean hasPath(final Position source, final Position destination) {
        if (source == null || destination == null) {
            return false;
        }
        if (source.isValid(this) && destination.isValid(this)) {
            final Region rgnA = getRegionAt(source);
            final Region rgnB = getRegionAt(destination);
            return rgnA != null && rgnB != null && rgnA.getRegionGroupID() == rgnB.getRegionGroupID();
        }
        return false;
    }

    public void setTextSize() {
        setTextSize(Text.Size.Default);
    }

    /**
     * Sets the size of the text for all calls to {@link #drawText} following this one.
     *
     * @param size The size of the text. This value is one of Text#Size. If this value is omitted, then a default value of {@link Text.Size#Default} is used.
     * @see Text.Size
     */
    public void setTextSize(final Text.Size size) {
        textSize = size;
    }

    /**
     * Retrieves current amount of time in seconds that the game has elapsed.
     *
     * @return Time, in seconds, that the game has elapsed as an integer.
     */
    public int elapsedTime() {
        return gameData.getElapsedTime();
    }

    /**
     * Sets the command optimization level. Command optimization is a feature
     * in BWAPI that tries to reduce the APM of the bot by grouping or eliminating unnecessary
     * game actions. For example, suppose the bot told 24 @Zerglings to @Burrow. At command
     * optimization level 0, BWAPI is designed to select each Zergling to burrow individually,
     * which costs 48 actions. With command optimization level 1, it can perform the same
     * behaviour using only 4 actions. The command optimizer also reduces the amount of bytes used
     * for each action if it can express the same action using a different command. For example,
     * Right_Click uses less bytes than Move.
     *
     * @param level An integer representation of the aggressiveness for which commands are optimized. A lower level means less optimization, and a higher level means more optimization.
     *              <p>
     *              The values for level are as follows:
     *              - 0: No optimization.
     *              - 1: Some optimization.
     *              - Is not detected as a hack.
     *              - Does not alter behaviour.
     *              - Units performing the following actions are grouped and ordered 12 at a time:
     *              - Attack_Unit
     *              - Morph (@Larva only)
     *              - Hold_Position
     *              - Stop
     *              - Follow
     *              - Gather
     *              - Return_Cargo
     *              - Repair
     *              - Burrow
     *              - Unburrow
     *              - Cloak
     *              - Decloak
     *              - Siege
     *              - Unsiege
     *              - Right_Click_Unit
     *              - Halt_Construction
     *              - Cancel_Train (@Carrier and @Reaver only)
     *              - Cancel_Train_Slot (@Carrier and @Reaver only)
     *              - Cancel_Morph (for non-buildings only)
     *              - Use_Tech
     *              - Use_Tech_Unit
     *              .
     *              - The following order transformations are applied to allow better grouping:
     *              - Attack_Unit becomes Right_Click_Unit if the target is an enemy
     *              - Move becomes Right_Click_Position
     *              - Gather becomes Right_Click_Unit if the target contains resources
     *              - Set_Rally_Position becomes Right_Click_Position for buildings
     *              - Set_Rally_Unit becomes Right_Click_Unit for buildings
     *              - Use_Tech_Unit with Infestation becomes Right_Click_Unit if the target is valid
     *              .
     *              .
     *              - 2: More optimization by grouping structures.
     *              - Includes the optimizations made by all previous levels.
     *              - May be detected as a hack by some replay utilities.
     *              - Does not alter behaviour.
     *              - Units performing the following actions are grouped and ordered 12 at a time:
     *              - Attack_Unit (@Turrets, @Photon_Cannons, @Sunkens, @Spores)
     *              - Train
     *              - Morph
     *              - Set_Rally_Unit
     *              - Lift
     *              - Cancel_Construction
     *              - Cancel_Addon
     *              - Cancel_Train
     *              - Cancel_Train_Slot
     *              - Cancel_Morph
     *              - Cancel_Research
     *              - Cancel_Upgrade
     *              .
     *              .
     *              - 3: Extensive optimization
     *              - Includes the optimizations made by all previous levels.
     *              - Units may behave or move differently than expected.
     *              - Units performing the following actions are grouped and ordered 12 at a time:
     *              - Attack_Move
     *              - Set_Rally_Position
     *              - Move
     *              - Patrol
     *              - Unload_All
     *              - Unload_All_Position
     *              - Right_Click_Position
     *              - Use_Tech_Position
     *              .
     *              .
     *              - 4: Aggressive optimization
     *              - Includes the optimizations made by all previous levels.
     *              - Positions used in commands will be rounded to multiples of 32.
     *              - @High_Templar and @Dark_Templar that merge into @Archons will be grouped and may
     *              choose a different target to merge with. It will not merge with a target that
     *              wasn't included.
     *              .
     *              .
     */
    public void setCommandOptimizationLevel(final int level) {
        addCommand(SetCommandOptimizerLevel, level, 0);
    }

    /**
     * Returns the remaining countdown time. The countdown timer is used in @CTF and @UMS game types.
     *
     * @return Integer containing the time (in game seconds) on the countdown timer.
     */
    public int countdownTimer() {
        return gameData.getCountdownTimer();
    }

    /**
     * Retrieves the set of all regions on the map.
     *
     * @return List<Region> containing all map regions.
     */
    public List<Region> getAllRegions() {
        return regionSet;
    }

    /**
     * Retrieves the region at a given position.
     *
     * @param x The x coordinate, in pixels.
     * @param y The y coordinate, in pixels.
     * @return the Region interface at the given position. Returns null if the provided position is not valid (i.e. not within the map bounds).
     * @see #getAllRegions
     * @see #getRegion
     */
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

    /**
     * Retrieves a basic build position just as the default Computer AI would.
     * This allows users to find simple build locations without relying on external libraries.
     *
     * @param type            A valid UnitType representing the unit type to accomodate space for.
     * @param desiredPosition A valid TilePosition containing the desired placement position.
     * @param maxRange        The maximum distance (in tiles) to build from desiredPosition.
     * @param creep           A special boolean value that changes the behaviour of @Creep_Colony placement.
     * @return A TilePosition containing the location that the structure should be constructed at. Returns {@link TilePosition#Invalid} If a build location could not be found within maxRange.
     */
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

    /**
     * Calculates the damage received for a given player. It can be understood
     * as the damage from fromType to toType. Does not include shields in calculation.
     * Includes upgrades if players are provided.
     *
     * @param fromType   The unit type that will be dealing the damage.
     * @param toType     The unit type that will be receiving the damage.
     * @param fromPlayer The player owner of the given type that will be dealing the damage. If omitted, then no player will be used to calculate the upgrades for fromType.
     * @param toPlayer   The player owner of the type that will be receiving the damage. If omitted, then this parameter will default to {@link #self}.
     * @return The amount of damage that fromType would deal to toType.
     * @see #getDamageTo
     */
    public int getDamageFrom(final UnitType fromType, final UnitType toType, final Player fromPlayer, final Player toPlayer) {
        return getDamageFromImpl(fromType, toType, fromPlayer, toPlayer == null ? self() : toPlayer);
    }

    public int getDamageTo(final UnitType toType, final UnitType fromType, final Player toPlayer) {
        return getDamageTo(toType, fromType, toPlayer, null);
    }

    public int getDamageTo(final UnitType toType, final UnitType fromType) {
        return getDamageTo(toType, fromType, null);
    }

    /**
     * Calculates the damage dealt for a given player. It can be understood as
     * the damage to toType from fromType. Does not include shields in calculation.
     * Includes upgrades if players are provided.
     * <p>
     * This function is nearly the same as {@link #getDamageFrom}. The only difference is that
     * the last parameter is intended to default to {@link #self}.
     *
     * @param toType     The unit type that will be receiving the damage.
     * @param fromType   The unit type that will be dealing the damage.
     * @param toPlayer   The player owner of the type that will be receiving the damage. If omitted, then no player will be used to calculate the upgrades for toType.
     * @param fromPlayer The player owner of the given type that will be dealing the damage. If omitted, then this parameter will default to {@link #self}).
     * @return The amount of damage that fromType would deal to toType.
     * @see #getDamageFrom
     */
    public int getDamageTo(final UnitType toType, final UnitType fromType, final Player toPlayer, final Player fromPlayer) {
        return getDamageFromImpl(fromType, toType, fromPlayer == null ? self() : fromPlayer, toPlayer);
    }

    /**
     * Retrieves the initial random seed that was used in this game's creation.
     * This is used to identify the seed that started this game, in case an error occurred, so
     * that developers can deterministically reproduce the error. Works in both games and replays.
     *
     * @return This game's random seed.
     * @since 4.2.0
     */
    public int getRandomSeed() {
        return randomSeed;
    }
}
