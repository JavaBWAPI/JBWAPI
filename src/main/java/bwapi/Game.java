package bwapi;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static bwapi.CommandType.*;
import static bwapi.Race.Zerg;
import static bwapi.UnitType.*;

public class Game {
    private static final int damageRatio[][] = {
            // Ind, Sml, Med, Lrg, Non, Unk
            {0, 0, 0, 0, 0, 0}, // Independent
            {0, 128, 192, 256, 0, 0}, // Explosive
            {0, 256, 128, 64, 0, 0}, // Concussive
            {0, 256, 256, 256, 0, 0}, // Normal
            {0, 256, 256, 256, 0, 0}, // Ignore_Armor
            {0, 0, 0, 0, 0, 0}, // None
            {0, 0, 0, 0, 0, 0}  // Unknown
    };
    private static boolean bPsiFieldMask[][] = {
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
    private final Set<Unit> staticMinerals = new HashSet<>();
    private final Set<Unit> staticGeysers = new HashSet<>();
    private final Set<Unit> staticNeutralUnits = new HashSet<>();
    private final Set<Integer> visibleUnits = new HashSet<>();
    private Client.GameData gameData;
    // CONSTANT
    private Player[] players;
    private Region[] regions;
    private Force[] forces;
    private Bullet[] bullets;
    private Set<Force> forceSet;
    private Set<Player> playerSet;
    private Set<Region> regionSet;
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

    public Game(final Client.GameData gameData) {
        this.gameData = gameData;
    }

    private static boolean hasPower(int x, int y, final UnitType unitType, final Set<Unit> pylons) {
        if (unitType.id >= 0 && unitType.id < UnitType.None.id && (!unitType.requiresPsi() || !unitType.isBuilding())) {
            return true;
        }

        // Loop through all pylons for the current player
        for (Unit i : pylons) {
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
        staticMinerals.clear();
        staticGeysers.clear();
        staticNeutralUnits.clear();
        visibleUnits.clear();

        final int forceCount = gameData.getForceCount();
        forces = new Force[forceCount];
        for (int id = 0; id < forceCount; id++) {
            forces[id] = new Force(gameData.getForce(id), this);
        }

        forceSet = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(forces)));

        final int playerCount = gameData.getPlayerCount();
        players = new Player[playerCount];
        for (int id = 0; id < playerCount; id++) {
            players[id] = new Player(gameData.getPlayer(id), this);
        }

        playerSet = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(players)));

        final int bulletCount = gameData.bulletCount();
        bullets = new Bullet[bulletCount];
        for (int id = 0; id < bulletCount; id++) {
            bullets[id] = new Bullet(gameData.getBullet(id), this);
        }

        final int regionCount = gameData.regionCount();
        regions = new Region[regionCount];
        for (int id = 0; id < regionCount; id++) {
            regions[id] = new Region(gameData.getRegion(id), this);
        }

        for (final Region region : regions) {
            region.updateNeighbours();
        }

        regionSet = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(regions)));

        units = new Unit[1000];
        for (int id = 0; id < gameData.getInitialUnitCount(); id++) {
            final Unit unit = new Unit(gameData.getUnit(id), this);

            units[id] = unit;

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

        randomSeed = gameData.randomSeed();
        revision = gameData.getRevision();
        debug = gameData.isDebug();
        self = players[gameData.self()];
        enemy = players[gameData.enemy()];
        neutral = players[gameData.neutral()];
        replay = gameData.isReplay();
        multiplayer = gameData.isMultiplayer();
        battleNet = gameData.isBattleNet();
        startLocations = IntStream.range(0, gameData.startLocationCount())
                .mapToObj(i -> new TilePosition(gameData.startLocationX(i), gameData.startLocationY(i)))
                .collect(Collectors.toList());
        mapWidth = gameData.mapWidth();
        mapHeight = gameData.mapHeight();
        mapFileName = gameData.mapFileName();
        mapPathName = gameData.mapPathName();
        mapName = gameData.mapName();
        mapHash = gameData.mapHash();

        buildable = new boolean[mapWidth][mapHeight];
        groundHeight = new int[mapWidth][mapHeight];
        mapTileRegionID = new short[mapWidth][mapHeight];
        for (int x = 0; x < mapWidth; x++) {
            for (int y = 0; y < mapHeight; y++) {
                buildable[x][y] = gameData.buildable(x, y);
                groundHeight[x][y] = gameData.groundHeight(x, y);
                mapTileRegionID[x][y] = gameData.mapTileRegionID(x, y);
            }
        }
        walkable = new boolean[mapWidth * 4][mapHeight * 4];
        for (int i = 0; i < mapWidth * 4; i++) {
            for (int j = 0; j < mapHeight * 4; j++) {
                walkable[i][j] = gameData.walkable(i, j);
            }
        }

        mapSplitTilesMiniTileMask = new short[5000];
        mapSplitTilesRegion1 = new short[5000];
        mapSplitTilesRegion2 = new short[5000];
        for (int i = 0; i < 5000; i++) {
            mapSplitTilesMiniTileMask[i] = gameData.mapSplitTilesMiniTileMask(i);
            mapSplitTilesRegion1[i] = gameData.mapSplitTilesRegion1(i);
            mapSplitTilesRegion2[i] = gameData.mapSplitTilesRegion2(i);
        }

        mapPixelWidth = mapWidth * TilePosition.SIZE_IN_PIXELS;
        mapPixelHeight = mapHeight * TilePosition.SIZE_IN_PIXELS;
    }

    void unitShow(final int id) {
        if (id > units.length) {
            //rescale unit array if needed
            final Unit[] largerUnitsArray = new Unit[2 * units.length];
            System.arraycopy(units, 0, largerUnitsArray, 0, units.length);
            units = largerUnitsArray;
        }
        if (units[id] == null) {
            units[id] = new Unit(gameData.getUnit(id), this);
        }
        visibleUnits.add(id);
    }

    void unitHide(final int id) {
        visibleUnits.remove(id);
    }

    void updateUnitPositions(final int frame) {
        getAllUnits().forEach(u -> u.updatePosition(frame));
    }

    void addUnitCommand(final int type, final int unit, final int target, final int x, final int y, final int extra) {
        gameData.addUnitCommand(new Client.UnitCommand(type, unit, target, x, y, extra));
    }

    void addCommand(final int type, final int value1, final int value2) {
        gameData.addCommand(new Client.Command(type, value1, value2));
    }

    void addShape(final int type, final int coordType, final int x1, final int y1, final int x2, final int y2, final int extra1, final int extra2, final int color, final boolean isSolid) {
        gameData.addShape(new Client.Shape(type, coordType, x1, y1, x2, y2, extra1, extra2, color, isSolid));
    }

    public Set<Force> getForces() {
        return forceSet;
    }

    public Set<Player> getPlayers() {
        return playerSet;
    }

    public Set<Unit> getAllUnits() {
        if (getFrameCount() == 0) {
            final HashSet<Unit> us = new HashSet<>();
            for (final Unit u : units) {
                if (u != null) {
                    us.add(u);
                }
            }
            return us;
        }
        return visibleUnits.stream()
                .map(i -> units[i])
                .collect(Collectors.toSet());
    }

    public Set<Unit> getMinerals() {
        return getAllUnits().stream()
                .filter(u -> u.getType().isMineralField())
                .collect(Collectors.toSet());
    }

    public Set<Unit> getGeysers() {
        return getAllUnits().stream()
                .filter(u -> u.getType() == Resource_Vespene_Geyser)
                .collect(Collectors.toSet());
    }

    public Set<Unit> getNeutralUnits() {
        return getAllUnits().stream()
                .filter(u -> u.getPlayer().equals(neutral()))
                .collect(Collectors.toSet());
    }

    public Set<Unit> getStaticMinerals() {
        return new HashSet<>(staticMinerals);
    }

    public Set<Unit> getStaticGeysers() {
        return new HashSet<>(staticGeysers);
    }

    public Set<Unit> getStaticNeutralUnits() {
        return new HashSet<>(staticNeutralUnits);
    }

    public Set<Bullet> getBullets() {
        final Set<Bullet> bs = new HashSet<>();
        for (final Bullet bullet : bullets) {
            if (bullet.exists()) {
                bs.add(bullet);
            }
        }
        return bs;
    }

    public Set<Position> getNukeDots() {
        return IntStream.range(0, gameData.nukeDotCount())
                .mapToObj(id -> new Position(gameData.getNukeDotX(id), gameData.getNukeDotY((id))))
                .collect(Collectors.toSet());
    }

    public Force getForce(final int forceID) {
        return forces[forceID];
    }

    public Player getPlayer(final int playerID) {
        return players[playerID];
    }

    public Unit getUnit(final int unitID) {
        if (unitID < 0) {
            return null;
        }
        return units[unitID];
    }

    public Region getRegion(final int regionID) {
        return regions[regionID];
    }

    public GameType getGameType() {
        return GameType.gameTypes[gameData.gameType()];
    }

    public int getLatency() {
        return gameData.latency();
    }

    public int getFrameCount() {
        return gameData.frameCount();
    }

    public int getReplayFrameCount() {
        return gameData.replayFrameCount();
    }

    public int getFPS() {
        return gameData.fps();
    }

    public double getAverageFPS() {
        return gameData.averageFPS();
    }

    public Position getMousePosition() {
        return new Position(gameData.mouseX(), gameData.mouseY());
    }

    public boolean getMouseState(final MouseButton button) {
        return gameData.mouseState(button.value);
    }

    public boolean getKeyState(final Key key) {
        return gameData.keyState(key.value);
    }

    public Position getScreenPosition() {
        return new Position(gameData.screenX(), gameData.screenY());
    }

    public void setScreenPosition(final Position p) {
        setScreenPosition(p.x, p.y);
    }

    public void setScreenPosition(final int x, final int y) {
        addCommand(SetScreenPosition.value, x, y);
    }

    public void pingMinimap(final int x, final int y) {
        addCommand(PingMinimap.value, x, y);
    }

    public void pingMinimap(final Position p) {
        pingMinimap(p.x, p.y);
    }

    public boolean isFlagEnabled(final Flag flag) {
        return gameData.getFlag(flag.value);
    }

    public void enableFlag(final Flag flag) {
        gameData.setFlag(flag.value, true);
    }

    public Set<Unit> getUnitsOnTile(final int tileX, final int tileY) {
        return getAllUnits().stream().filter(u -> {
            final TilePosition tp = u.getTilePosition();
            return tp.x == tileX && tp.y == tileY;
        }).collect(Collectors.toSet());
    }

    public Set<Unit> getUnitsOnTile(final TilePosition tile) {
        return getUnitsOnTile(tile.x, tile.y);
    }

    public Set<Unit> getUnitsInRectangle(final int left, final int top, final int right, final int bottom, final UnitFilter filter) {
        return getAllUnits().stream().filter(u -> {
            final Position p = u.getPosition();
            return left <= p.x && top <= p.y && p.x < right && p.y < bottom && filter.operation(u);
        }).collect(Collectors.toSet());
    }

    public Set<Unit> getUnitsInRectangle(final Position leftTop, final Position rightBottom, final UnitFilter filter) {
        return getUnitsInRectangle(leftTop.x, leftTop.y, rightBottom.x, rightBottom.y, filter);
    }

    public Set<Unit> getUnitsInRadius(final int x, final int y, final int radius, final UnitFilter filter) {
        return getUnitsInRadius(new Position(x, y), radius, filter);
    }

    public Set<Unit> getUnitsInRadius(final Position center, final int radius, final UnitFilter filter) {
        return getAllUnits().stream().filter(u -> center.getApproxDistance(u.getPosition()) <= radius && filter.operation(u)).collect(Collectors.toSet());
    }

    public Unit getClosestUnitInRectangle(final Position center, final int left, final int top, final int right, final int bottom, final UnitFilter filter) {
        return getUnitsInRectangle(left, top, right, bottom, filter).stream()
                .min(Comparator.comparingInt(u -> u.getDistance(center))).orElse(null);
    }

    public Unit getClosestUnitInRadius(final Position center, final int radius, final UnitFilter filter) {
        return getUnitsInRadius(center, radius, filter).stream()
                .min(Comparator.comparingInt(u -> u.getDistance(center))).orElse(null);
    }

    public int mapWidth() {
        return mapWidth;
    }

    public int mapHeight() {
        return mapHeight;
    }

    public int mapPixelWidth() {
        return mapPixelWidth;
    }

    public int mapPixelHeight() {
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
        return buildable[position.x][position.y] && (includeBuildings ? !gameData.occupied(position.x, position.y) : true);
    }

    public boolean isVisible(final int tileX, final int tileY) {
        return isVisible(new TilePosition(tileX, tileY));
    }

    public boolean isVisible(final TilePosition position) {
        if (!position.isValid(this)) {
            return false;
        }
        return gameData.visible(position.x, position.y);
    }

    public boolean isExplored(final int tileX, final int tileY) {
        return isExplored(new TilePosition(tileX, tileY));
    }

    public boolean isExplored(final TilePosition position) {
        if (!position.isValid(this)) {
            return false;
        }
        return gameData.explored(position.x, position.y);
    }

    public boolean hasCreep(final int tileX, final int tileY) {
        return hasCreep(new TilePosition(tileX, tileY));
    }

    public boolean hasCreep(final TilePosition position) {
        if (!position.isValid(this)) {
            return false;
        }
        return gameData.hasCreep(position.x, position.y);
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
        return hasPower(position.x, position.y, unitType, self().getUnits().stream().filter(u -> u.getType() == Protoss_Pylon).collect(Collectors.toSet()));
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
        final TilePosition lt = (builder != null && type.isAddon()) ?
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
            final Set<Unit> unitsInRect = getUnitsInRectangle(lt.toPosition(), rb.toPosition(),
                    (u -> !u.isFlying() && !u.isLoaded() && builder != null || type == Zerg_Nydus_Canal
                            && u.getLeft() <= targPos.x + type.dimensionRight()
                            && u.getTop() <= targPos.y + type.dimensionDown()
                            && u.getRight() <= targPos.x + type.dimensionLeft()
                            && u.getBottom() <= targPos.y + type.dimensionUp()));

            for (Unit u : unitsInRect) {
                // Addons can be placed over units that can move, pushing them out of the way
                if (!(type.isAddon() && u.getType().canMove()))
                    return false;
            }

            // Creep Check
            // Note: Zerg structures that don't require creep can still be placed on creep
            boolean needsCreep = type.requiresCreep();
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
            if (type.requiresPsi() && hasPower(lt, type)) {
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
        Player pSelf = self();
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
            } else if (builderType != requiredType) {
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
        Race typeRace = type.getRace();
        final int supplyRequired = type.supplyRequired() * (type.isTwoUnitsInOneEgg() ? 2 : 1);
        if (supplyRequired > 0 && pSelf.supplyTotal(typeRace) < pSelf.supplyUsed(typeRace) + supplyRequired - (requiredType.getRace() == typeRace ? requiredType.supplyRequired() : 0)) {
            return false;
        }

        UnitType addon = UnitType.None;
        Map<UnitType, Integer> reqUnits = type.requiredUnits();
        for (final UnitType ut : type.requiredUnits().keySet()) {
            if (ut.isAddon())
                addon = ut;

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

        if (!self.hasUnitTypeRequirement(type.requiredUnit())) {
            return false;
        }

        return true;
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
        int nextLvl = self.getUpgradeLevel(type) + 1;

        if (!self.hasUnitTypeRequirement(type.whatUpgrades())) {
            return false;
        }

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
        return new ArrayList<>(startLocations);
    }

    public void printf(final String cstr_format) {
        addCommand(Printf.value, gameData.addString(cstr_format), 0);
    }

    public void sendText(final String cstr_format) {
        addCommand(SendText.value, gameData.addString(cstr_format), 0);
    }

    public void sendTextEx(final boolean toAllies, final String cstr_format) {
        addCommand(SendText.value, gameData.addString(cstr_format), toAllies ? 1 : 0);
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
        addCommand(PauseGame.value, 0, 0);
    }

    public void resumeGame() {
        addCommand(ResumeGame.value, 0, 0);
    }

    public void leaveGame() {
        addCommand(LeaveGame.value, 0, 0);
    }

    public void restartGame() {
        addCommand(RestartGame.value, 0, 0);
    }

    public void setLocalSpeed(final int speed) {
        addCommand(SetLocalSpeed.value, speed, 0);
    }

    public boolean issueCommand(final Collection<Unit> units, final UnitCommand command) {
        return !units.stream()
                .map(u -> u.issueCommand(command))
                .collect(Collectors.toList())
                .contains(false);
    }

    public Set<Unit> getSelectedUnits() {
        return IntStream.range(0, gameData.selectedUnitCount())
                .mapToObj(i -> units[gameData.selectedUnit(i)])
                .collect(Collectors.toSet());
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

    public Set<Player> allies() {
        final Player self = self();
        return getPlayers().stream()
                .filter(self::isAlly)
                .collect(Collectors.toSet());
    }

    //TODO FIX in 4.3.0
    public Set<Player> enemies() {
        final Player self = self();
        return getPlayers().stream()
                .filter(p -> !(p.isNeutral() || self.isAlly(p)))
                .collect(Collectors.toSet());
    }

    //TODO FIX in 4.3.0
    public Set<Player> observers() {
        return getPlayers().stream()
                .filter(Player::isObserver)
                .collect(Collectors.toSet());
    }

    public void drawText(final Coordinate ctype, final int x, final int y, final String cstr_format) {
        final int stringId = gameData.addString(cstr_format);
        addShape(Shape.Text.value, ctype.value, x, y, 0, 0, stringId, textSize.value, 0, false);
    }

    public void drawTextMap(final int x, final int y, final String cstr_format) {
        drawText(Coordinate.Map, x, y, cstr_format);
    }

    public void drawTextMap(final Position p, final String cstr_format) {
        drawTextMap(p.x, p.y, cstr_format);
    }

    public void drawTextMouse(final int x, final int y, final String cstr_format) {
        drawText(Coordinate.Mouse, x, y, cstr_format);
    }

    public void drawTextMouse(final Position p, final String cstr_format) {
        drawTextMouse(p.x, p.y, cstr_format);

    }

    public void drawTextScreen(final int x, final int y, final String cstr_format) {
        drawText(Coordinate.Screen, x, y, cstr_format);
    }

    public void drawTextScreen(final Position p, final String cstr_format) {
        drawTextScreen(p.x, p.y, cstr_format);
    }

    public void drawBox(final Coordinate ctype, final int left, final int top, final int right, final int bottom, final Color color) {
        drawBox(ctype, left, top, right, bottom, color, false);
    }

    public void drawBox(final Coordinate ctype, final int left, final int top, final int right, final int bottom, final Color color, final boolean isSolid) {
        addShape(Shape.Box.value, ctype.value, left, top, right, bottom, 0, 0, color.id, isSolid);
    }

    public void drawBoxMap(int left, int top, int right, int bottom, Color color) {
        drawBox(Coordinate.Map, left, top, right, bottom, color);
    }

    public void drawBoxMap(int left, int top, int right, int bottom, Color color, boolean isSolid) {
        drawBox(Coordinate.Map, left, top, right, bottom, color, isSolid);
    }

    public void drawBoxMap(Position leftTop, Position rightBottom, Color color) {
        drawBox(Coordinate.Map, leftTop.x, leftTop.y, rightBottom.x, rightBottom.y, color);
    }

    public void drawBoxMap(Position leftTop, Position rightBottom, Color color, boolean isSolid) {
        drawBox(Coordinate.Map, leftTop.x, leftTop.y, rightBottom.x, rightBottom.y, color, isSolid);
    }

    public void drawBoxMouse(int left, int top, int right, int bottom, Color color) {
        drawBox(Coordinate.Mouse, left, top, right, bottom, color);
    }

    public void drawBoxMouse(int left, int top, int right, int bottom, Color color, boolean isSolid) {
        drawBox(Coordinate.Mouse, left, top, right, bottom, color, isSolid);
    }

    public void drawBoxMouse(Position leftTop, Position rightBottom, Color color) {
        drawBox(Coordinate.Mouse, leftTop.x, leftTop.y, rightBottom.x, rightBottom.y, color);
    }

    public void drawBoxMouse(Position leftTop, Position rightBottom, Color color, boolean isSolid) {
        drawBox(Coordinate.Mouse, leftTop.x, leftTop.y, rightBottom.x, rightBottom.y, color, isSolid);
    }

    public void drawBoxScreen(int left, int top, int right, int bottom, Color color) {
        drawBox(Coordinate.Screen, left, top, right, bottom, color);
    }

    public void drawBoxScreen(int left, int top, int right, int bottom, Color color, boolean isSolid) {
        drawBox(Coordinate.Screen, left, top, right, bottom, color, isSolid);
    }

    public void drawBoxScreen(Position leftTop, Position rightBottom, Color color) {
        drawBox(Coordinate.Screen, leftTop.x, leftTop.y, rightBottom.x, rightBottom.y, color);
    }

    public void drawBoxScreen(Position leftTop, Position rightBottom, Color color, boolean isSolid) {
        drawBox(Coordinate.Screen, leftTop.x, leftTop.y, rightBottom.x, rightBottom.y, color, isSolid);
    }

    public void drawTriangle(Coordinate ctype, int ax, int ay, int bx, int by, int cx, int cy, Color color) {
        drawTriangle(ctype, ax, ay, bx, by, cx, cy, color, false);
    }

    public void drawTriangle(Coordinate ctype, int ax, int ay, int bx, int by, int cx, int cy, Color color, boolean isSolid) {
        addShape(Shape.Triangle.value, ctype.value, ax, ay, bx, by, cx, cy, color.id, isSolid);
    }

    public void drawTriangleMap(int ax, int ay, int bx, int by, int cx, int cy, Color color) {
        drawTriangle(Coordinate.Map, ax, ay, bx, by, cx, cy, color);
    }

    public void drawTriangleMap(int ax, int ay, int bx, int by, int cx, int cy, Color color, boolean isSolid) {
        drawTriangle(Coordinate.Map, ax, ay, bx, by, cx, cy, color, isSolid);
    }

    public void drawTriangleMap(Position a, Position b, Position c, Color color) {
        drawTriangle(Coordinate.Map, a.x, a.y, b.x, b.y, c.x, c.y, color);
    }

    public void drawTriangleMap(Position a, Position b, Position c, Color color, boolean isSolid) {
        drawTriangle(Coordinate.Map, a.x, a.y, b.x, b.y, c.x, c.y, color, isSolid);
    }

    public void drawTriangleMouse(int ax, int ay, int bx, int by, int cx, int cy, Color color) {
        drawTriangle(Coordinate.Mouse, ax, ay, bx, by, cx, cy, color);
    }

    public void drawTriangleMouse(int ax, int ay, int bx, int by, int cx, int cy, Color color, boolean isSolid) {
        drawTriangle(Coordinate.Mouse, ax, ay, bx, by, cx, cy, color, isSolid);
    }

    public void drawTriangleMouse(Position a, Position b, Position c, Color color) {
        drawTriangle(Coordinate.Mouse, a.x, a.y, b.x, b.y, c.x, c.y, color);
    }

    public void drawTriangleMouse(Position a, Position b, Position c, Color color, boolean isSolid) {
        drawTriangle(Coordinate.Mouse, a.x, a.y, b.x, b.y, c.x, c.y, color, isSolid);

    }

    public void drawTriangleScreen(int ax, int ay, int bx, int by, int cx, int cy, Color color) {
        drawTriangle(Coordinate.Screen, ax, ay, bx, by, cx, cy, color);
    }

    public void drawTriangleScreen(int ax, int ay, int bx, int by, int cx, int cy, Color color, boolean isSolid) {
        drawTriangle(Coordinate.Screen, ax, ay, bx, by, cx, cy, color, isSolid);
    }

    public void drawTriangleScreen(Position a, Position b, Position c, Color color) {
        drawTriangle(Coordinate.Screen, a.x, a.y, b.x, b.y, c.x, c.y, color);
    }

    public void drawTriangleScreen(Position a, Position b, Position c, Color color, boolean isSolid) {
        drawTriangle(Coordinate.Screen, a.x, a.y, b.x, b.y, c.x, c.y, color, isSolid);
    }

    public void drawCircle(Coordinate ctype, int x, int y, int radius, Color color) {
        drawCircle(ctype, x, y, radius, color, false);
    }

    public void drawCircle(Coordinate ctype, int x, int y, int radius, Color color, boolean isSolid) {
        addShape(Shape.Circle.value, ctype.value, x, y, 0, 0, radius, 0, color.id, isSolid);
    }

    public void drawCircleMap(int x, int y, int radius, Color color) {
        drawCircle(Coordinate.Map, x, y, radius, color);
    }

    public void drawCircleMap(int x, int y, int radius, Color color, boolean isSolid) {
        drawCircle(Coordinate.Map, x, y, radius, color, isSolid);
    }

    public void drawCircleMap(Position p, int radius, Color color) {
        drawCircle(Coordinate.Map, p.x, p.y, radius, color);
    }

    public void drawCircleMap(Position p, int radius, Color color, boolean isSolid) {
        drawCircle(Coordinate.Map, p.x, p.y, radius, color, isSolid);
    }

    public void drawCircleMouse(int x, int y, int radius, Color color) {
        drawCircle(Coordinate.Mouse, x, y, radius, color);
    }

    public void drawCircleMouse(int x, int y, int radius, Color color, boolean isSolid) {
        drawCircle(Coordinate.Mouse, x, y, radius, color, isSolid);
    }

    public void drawCircleMouse(Position p, int radius, Color color) {
        drawCircle(Coordinate.Mouse, p.x, p.y, radius, color);
    }

    public void drawCircleMouse(Position p, int radius, Color color, boolean isSolid) {
        drawCircle(Coordinate.Mouse, p.x, p.y, radius, color, isSolid);
    }

    public void drawCircleScreen(int x, int y, int radius, Color color) {
        drawCircle(Coordinate.Screen, x, y, radius, color);
    }

    public void drawCircleScreen(int x, int y, int radius, Color color, boolean isSolid) {
        drawCircle(Coordinate.Screen, x, y, radius, color, isSolid);
    }

    public void drawCircleScreen(Position p, int radius, Color color) {
        drawCircle(Coordinate.Screen, p.x, p.y, radius, color);
    }

    public void drawCircleScreen(Position p, int radius, Color color, boolean isSolid) {
        drawCircle(Coordinate.Screen, p.x, p.y, radius, color, isSolid);
    }

    public void drawEllipse(Coordinate ctype, int x, int y, int xrad, int yrad, Color color) {
        drawEllipse(ctype, x, y, xrad, yrad, color, false);
    }

    public void drawEllipse(Coordinate ctype, int x, int y, int xrad, int yrad, Color color, boolean isSolid) {
        addShape(Shape.Ellipse.value, ctype.value, x, y, 0, 0, xrad, yrad, color.id, isSolid);
    }

    public void drawEllipseMap(int x, int y, int xrad, int yrad, Color color) {
        drawEllipse(Coordinate.Map, x, y, xrad, yrad, color);
    }

    public void drawEllipseMap(int x, int y, int xrad, int yrad, Color color, boolean isSolid) {
        drawEllipse(Coordinate.Map, x, y, xrad, yrad, color, isSolid);
    }

    public void drawEllipseMap(Position p, int xrad, int yrad, Color color) {
        drawEllipse(Coordinate.Map, p.x, p.y, xrad, yrad, color);
    }

    public void drawEllipseMap(Position p, int xrad, int yrad, Color color, boolean isSolid) {
        drawEllipse(Coordinate.Map, p.x, p.y, xrad, yrad, color, isSolid);
    }

    public void drawEllipseMouse(int x, int y, int xrad, int yrad, Color color) {
        drawEllipse(Coordinate.Mouse, x, y, xrad, yrad, color);
    }

    public void drawEllipseMouse(int x, int y, int xrad, int yrad, Color color, boolean isSolid) {
        drawEllipse(Coordinate.Mouse, x, y, xrad, yrad, color, isSolid);
    }

    public void drawEllipseMouse(Position p, int xrad, int yrad, Color color) {
        drawEllipse(Coordinate.Mouse, p.x, p.y, xrad, yrad, color);
    }

    public void drawEllipseMouse(Position p, int xrad, int yrad, Color color, boolean isSolid) {
        drawEllipse(Coordinate.Mouse, p.x, p.y, xrad, yrad, color, isSolid);
    }

    public void drawEllipseScreen(int x, int y, int xrad, int yrad, Color color) {
        drawEllipse(Coordinate.Screen, x, y, xrad, yrad, color);
    }

    public void drawEllipseScreen(int x, int y, int xrad, int yrad, Color color, boolean isSolid) {
        drawEllipse(Coordinate.Mouse, x, y, xrad, yrad, color, isSolid);
    }

    public void drawEllipseScreen(Position p, int xrad, int yrad, Color color) {
        drawEllipse(Coordinate.Mouse, p.x, p.y, xrad, yrad, color);
    }

    public void drawEllipseScreen(Position p, int xrad, int yrad, Color color, boolean isSolid) {
        drawEllipse(Coordinate.Mouse, p.x, p.y, xrad, yrad, color, isSolid);
    }

    public void drawDot(Coordinate ctype, int x, int y, Color color) {
        addShape(Shape.Dot.value, ctype.value, x, y, 0, 0, 0, 0, color.id, false);
    }

    public void drawDotMap(int x, int y, Color color) {
        drawDot(Coordinate.Map, x, y, color);
    }

    public void drawDotMap(Position p, Color color) {
        drawDot(Coordinate.Map, p.x, p.y, color);
    }

    public void drawDotMouse(int x, int y, Color color) {
        drawDot(Coordinate.Mouse, x, y, color);
    }

    public void drawDotMouse(Position p, Color color) {
        drawDot(Coordinate.Mouse, p.x, p.y, color);
    }

    public void drawDotScreen(int x, int y, Color color) {
        drawDot(Coordinate.Screen, x, y, color);
    }

    public void drawDotScreen(Position p, Color color) {
        drawDot(Coordinate.Screen, p.x, p.y, color);
    }

    public void drawLine(Coordinate ctype, int x1, int y1, int x2, int y2, Color color) {
        addShape(Shape.Line.value, ctype.value, x1, y1, x2, y2, 0, 0, color.id, false);
    }

    public void drawLineMap(int x1, int y1, int x2, int y2, Color color) {
        drawLine(Coordinate.Map, x1, y1, x2, y2, color);
    }

    public void drawLineMap(Position a, Position b, Color color) {
        drawLine(Coordinate.Map, a.x, a.y, b.x, b.y, color);
    }

    public void drawLineMouse(int x1, int y1, int x2, int y2, Color color) {
        drawLine(Coordinate.Mouse, x1, y1, x2, y2, color);

    }

    public void drawLineMouse(Position a, Position b, Color color) {
        drawLine(Coordinate.Mouse, a.x, a.y, b.x, b.y, color);

    }

    public void drawLineScreen(int x1, int y1, int x2, int y2, Color color) {
        drawLine(Coordinate.Screen, x1, y1, x2, y2, color);
    }

    public void drawLineScreen(Position a, Position b, Color color) {
        drawLine(Coordinate.Screen, a.x, a.y, b.x, b.y, color);
    }

    public int getLatencyFrames() {
        return gameData.latencyFrames();
    }

    public int getLatencyTime() {
        return gameData.latencyTime();
    }

    public int getRemainingLatencyFrames() {
        return gameData.remainingLatencyFrames();
    }

    public int getRemainingLatencyTime() {
        return gameData.remainingLatencyTime();
    }

    public int getRevision() {
        return revision;
    }

    public boolean isDebug() {
        return debug;
    }

    public boolean isLatComEnabled() {
        return gameData.hasLatCom();
    }

    public void setLatCom(final boolean isEnabled) {
        addCommand(SetLatCom.value, isEnabled ? 1 : 0, 0);
    }

    public int getInstanceNumber() {
        return gameData.getInstanceID();
    }

    public int getAPM(final boolean includeSelects) {
        return includeSelects ? gameData.getBotAPM_selects() : gameData.getBotAPM_noselects();
    }

    public void setFrameSkip(int frameSkip) {
        addCommand(SetFrameSkip.value, frameSkip, 0);
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
            if (rgnA != null && rgnB != null && rgnA.getRegionGroupID() == rgnB.getRegionGroupID()) {
                return true;
            }
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
        return gameData.elapsedTime();
    }

    public void setCommandOptimizationLevel(final int level) {
        addCommand(SetCommandOptimizerLevel.value, level, 0);
    }

    public int countdownTimer() {
        return gameData.countdownTimer();
    }

    public Set<Region> getAllRegions() {
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
            final int minitileShift = ((position.x & 0x1F) / 8) + ((position.y & 0x1F) / 8) * 4;
            final int index = idx & 0x1FFF;

            if (index >= 5000) {
                return null;
            }

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
        if (wpn == WeaponType.None || wpn == WeaponType.Unknown)
            return 0;

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

    public int getDamageFrom(final UnitType fromType, UnitType toType) {
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
