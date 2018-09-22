package bwapi;

import JavaBWAPIBackend.Client.GameData;
import bwapi.point.Position;
import bwapi.point.TilePosition;
import bwapi.point.WalkPosition;
import bwapi.types.GameType;
import bwapi.types.TechType;
import bwapi.types.UnitType;
import bwapi.types.UpgradeType;
import bwapi.values.*;

import java.util.*;

public class Game {
    final GameData gameData;

    ///CONSTANT
    final Map<Integer, Player> players = new HashMap<>();
    final Map<Integer, Region> regions = new HashMap<>();
    final Map<Integer, Force> forces = new HashMap<>();

    //CHANGING
    final Map<Integer, Unit> units = new HashMap<>();

    final Map<Integer, Bullet> bullets = new HashMap<>();

    public Game(GameData gameData) {
        this.gameData = gameData;
        init();
    }

    private void init() {
        for (int id=0; id < gameData.getForceCount(); id++) {
            forces.put(id, new Force(gameData.getForce(id), this));
        }
        for (int id=0; id < gameData.getPlayerCount(); id++) {
            players.put(id, new Player(gameData.getPlayer(id), this));
        }
        for (int id=0; id < gameData.regionCount(); id++) {
            regions.put(id, new Region(gameData.getRegion(id), this));
        }
    }

    public Collection<Force> getForces() {
        return forces.values();
    }

    public Collection<Player> getPlayers() {
        return players.values();
    }


    public Collection<Unit> getAllUnits() {
        return units.values();
    }

    /*
    public List<Unit> getMinerals();

    public List<Unit> getGeysers();

    public List<Unit> getNeutralUnits();

    public List<Unit> getStaticMinerals();

    public List<Unit> getStaticGeysers();

    public List<Unit> getStaticNeutralUnits();

    */
    public Collection<Bullet> getBullets() {
        //TODO cache this in onFrame
        final List<Bullet> bullets = new ArrayList<>();
        for (int i=0; i < gameData.bulletCount(); i++) {
            bullets.add(new Bullet(gameData.bullet(i), this));
        }
        return bullets;
    }

    /*
    public List<Position> getNukeDots();
    */

    public Force getForce(final int forceID) {
        return forces.get(forceID);
    }

    public Player getPlayer(final int playerID) {
        return players.get(playerID);
    }

    public Unit getUnit(final int unitID) {
        return units.get(unitID);
    }
    /*

    public Unit indexToUnit(int unitIndex);

    */
    public Region getRegion(final int regionID) {
        return regions.get(regionID);
    }

    /*
    public GameType getGameType() ;

    public int getLatency();

    public int getFrameCount();

    public int getReplayFrameCount();

    public int getFPS();

    public double getAverageFPS();

     public Position getMousePosition();

     public boolean getMouseState(MouseButton button);

     public boolean getKeyState(Key key);

     public Position getScreenPosition();

     public void setScreenPosition(int x, int y);

     public void setScreenPosition(Position p);

     public void pingMinimap(int x, int y);

     public void pingMinimap(Position p);

     public boolean isFlagEnabled(int flag);

     public void enableFlag(int flag);

     public List<Unit> getUnitsOnTile(int tileX, int tileY);

     public List<Unit> getUnitsOnTile(TilePosition tile);

     public List<Unit> getUnitsInRectangle(int left, int top, int right, int bottom);

     public List<Unit> getUnitsInRectangle(Position topLeft, Position bottomRight);

     public List<Unit> getUnitsInRadius(int x, int y, int radius);

     public List<Unit> getUnitsInRadius(Position center, int radius);

     public int mapWidth();

     public int mapHeight();

     public String mapFileName();

     public String mapPathName();

     public String mapName();

     public String mapHash();

     public boolean isWalkable(int walkX, int walkY);

     public boolean isWalkable(WalkPosition position);

     public int getGroundHeight(int tileX, int tileY);

     public int getGroundHeight(TilePosition position);

     public boolean isBuildable(int tileX, int tileY);

     public boolean isBuildable(int tileX, int tileY, boolean includeBuildings);

     public boolean isBuildable(TilePosition position);

     public boolean isBuildable(TilePosition position, boolean includeBuildings);

     public boolean isVisible(int tileX, int tileY);

     public boolean isVisible(TilePosition position);

     public boolean isExplored(int tileX, int tileY);

     public boolean isExplored(TilePosition position);

     public boolean hasCreep(int tileX, int tileY);

     public boolean hasCreep(TilePosition position);

     public boolean hasPowerPrecise(int x, int y);

     public boolean hasPowerPrecise(int x, int y, UnitType unitType);

     public boolean hasPowerPrecise(Position position);

     public boolean hasPowerPrecise(Position position, UnitType unitType);

     public boolean hasPower(int tileX, int tileY);

     public boolean hasPower(int tileX, int tileY, UnitType unitType);

     public boolean hasPower(TilePosition position);

     public boolean hasPower(TilePosition position, UnitType unitType);

     public boolean hasPower(int tileX, int tileY, int tileWidth, int tileHeight);

     public boolean hasPower(int tileX, int tileY, int tileWidth, int tileHeight, UnitType unitType);

     public boolean hasPower(TilePosition position, int tileWidth, int tileHeight);

     public boolean hasPower(TilePosition position, int tileWidth, int tileHeight, UnitType unitType);

     public boolean canBuildHere(TilePosition position, UnitType type, Unit builder);

     public boolean canBuildHere(TilePosition position, UnitType type);

     public boolean canBuildHere(TilePosition position, UnitType type, Unit builder, boolean checkExplored);

     public boolean canMake(UnitType type);

     public boolean canMake(UnitType type, Unit builder);

     public boolean canResearch(TechType type, Unit unit);

     public boolean canResearch(TechType type);

     public boolean canResearch(TechType type, Unit unit, boolean checkCanIssueCommandType);

     public boolean canUpgrade(UpgradeType type, Unit unit);

     public boolean canUpgrade(UpgradeType type);

     public boolean canUpgrade(UpgradeType type, Unit unit, boolean checkCanIssueCommandType);

     public List<TilePosition> getStartLocations();

     public void printf(String cstr_format);

     public void sendText(String cstr_format);

     public void sendTextEx(boolean toAllies, String cstr_format);

     public boolean isInGame();

     public boolean isMultiplayer();

     public boolean isBattleNet();

     public boolean isPaused();

     public boolean isReplay();

     public void pauseGame();

     public void resumeGame();

     public void leaveGame();

     public void restartGame();

     public void setLocalSpeed(int speed);

     public boolean issueCommand(List<Unit> units, UnitCommand command);

     public List<Unit> getSelectedUnits();
    */

    public Player self() {
        return players.get(gameData.self());
    }


    public Player enemy() {
        return players.get(gameData.enemy());
    }

    public Player neutral() {
        return players.get(gameData.neutral());
    }

    /*
    public List<Player> allies();

    public List<Player> enemies();

    public List<Player> observers();

    public void setTextSize();

    public void setTextSize(TextSize size);

    public void drawText(Coordinate ctype, int x, int y, String cstr_format);

    public void drawTextMap(int x, int y, String cstr_format);

    public void drawTextMap(Position p, String cstr_format);

    public void drawTextMouse(int x, int y, String cstr_format);

    public void drawTextMouse(Position p, String cstr_format);

    public void drawTextScreen(int x, int y, String cstr_format);

    public void drawTextScreen(Position p, String cstr_format);

    public void drawBox(Coordinate ctype, int left, int top, int right, int bottom, Color color);

    public void drawBox(Coordinate ctype, int left, int top, int right, int bottom, Color color, boolean isSolid);

    public void drawBoxMap(int left, int top, int right, int bottom, Color color);

    public void drawBoxMap(int left, int top, int right, int bottom, Color color, boolean isSolid);

    public void drawBoxMap(Position leftTop, Position rightBottom, Color color);

    public void drawBoxMap(Position leftTop, Position rightBottom, Color color, boolean isSolid);

    public void drawBoxMouse(int left, int top, int right, int bottom, Color color);

    public void drawBoxMouse(int left, int top, int right, int bottom, Color color, boolean isSolid);

    public void drawBoxMouse(Position leftTop, Position rightBottom, Color color);

    public void drawBoxMouse(Position leftTop, Position rightBottom, Color color, boolean isSolid);

    public void drawBoxScreen(int left, int top, int right, int bottom, Color color);

    public void drawBoxScreen(int left, int top, int right, int bottom, Color color, boolean isSolid);

    public void drawBoxScreen(Position leftTop, Position rightBottom, Color color);

    public void drawBoxScreen(Position leftTop, Position rightBottom, Color color, boolean isSolid);

    public void drawTriangle(Coordinate ctype, int ax, int ay, int bx, int by, int cx, int cy, Color color);

    public void drawTriangle(Coordinate ctype, int ax, int ay, int bx, int by, int cx, int cy, Color color, boolean isSolid);

    public void drawTriangleMap(int ax, int ay, int bx, int by, int cx, int cy, Color color);

    public void drawTriangleMap(int ax, int ay, int bx, int by, int cx, int cy, Color color, boolean isSolid);

    public void drawTriangleMap(Position a, Position b, Position c, Color color);

    public void drawTriangleMap(Position a, Position b, Position c, Color color, boolean isSolid);

    public void drawTriangleMouse(int ax, int ay, int bx, int by, int cx, int cy, Color color);

    public void drawTriangleMouse(int ax, int ay, int bx, int by, int cx, int cy, Color color, boolean isSolid);

    public void drawTriangleMouse(Position a, Position b, Position c, Color color);

    public void drawTriangleMouse(Position a, Position b, Position c, Color color, boolean isSolid);

    public void drawTriangleScreen(int ax, int ay, int bx, int by, int cx, int cy, Color color);

    public void drawTriangleScreen(int ax, int ay, int bx, int by, int cx, int cy, Color color, boolean isSolid);

    public void drawTriangleScreen(Position a, Position b, Position c, Color color);

    public void drawTriangleScreen(Position a, Position b, Position c, Color color, boolean isSolid);

    public void drawCircle(Coordinate ctype, int x, int y, int radius, Color color);

    public void drawCircle(Coordinate ctype, int x, int y, int radius, Color color, boolean isSolid);

    public void drawCircleMap(int x, int y, int radius, Color color);

    public void drawCircleMap(int x, int y, int radius, Color color, boolean isSolid);

    public void drawCircleMap(Position p, int radius, Color color);

    public void drawCircleMap(Position p, int radius, Color color, boolean isSolid);

    public void drawCircleMouse(int x, int y, int radius, Color color);

    public void drawCircleMouse(int x, int y, int radius, Color color, boolean isSolid);

    public void drawCircleMouse(Position p, int radius, Color color);

    public void drawCircleMouse(Position p, int radius, Color color, boolean isSolid);

    public void drawCircleScreen(int x, int y, int radius, Color color);

    public void drawCircleScreen(int x, int y, int radius, Color color, boolean isSolid);

    public void drawCircleScreen(Position p, int radius, Color color);

    public void drawCircleScreen(Position p, int radius, Color color, boolean isSolid);

    public void drawEllipse(Coordinate ctype, int x, int y, int xrad, int yrad, Color color);

    public void drawEllipse(Coordinate ctype, int x, int y, int xrad, int yrad, Color color, boolean isSolid);

    public void drawEllipseMap(int x, int y, int xrad, int yrad, Color color);

    public void drawEllipseMap(int x, int y, int xrad, int yrad, Color color, boolean isSolid);

    public void drawEllipseMap(Position p, int xrad, int yrad, Color color);

    public void drawEllipseMap(Position p, int xrad, int yrad, Color color, boolean isSolid);

    public void drawEllipseMouse(int x, int y, int xrad, int yrad, Color color);

    public void drawEllipseMouse(int x, int y, int xrad, int yrad, Color color, boolean isSolid);

    public void drawEllipseMouse(Position p, int xrad, int yrad, Color color);

    public void drawEllipseMouse(Position p, int xrad, int yrad, Color color, boolean isSolid);

    public void drawEllipseScreen(int x, int y, int xrad, int yrad, Color color);

    public void drawEllipseScreen(int x, int y, int xrad, int yrad, Color color, boolean isSolid);

    public void drawEllipseScreen(Position p, int xrad, int yrad, Color color);

    public void drawEllipseScreen(Position p, int xrad, int yrad, Color color, boolean isSolid);

    public void drawDot(Coordinate ctype, int x, int y, Color color);

    public void drawDotMap(int x, int y, Color color);

    public void drawDotMap(Position p, Color color);

    public void drawDotMouse(int x, int y, Color color);

    public void drawDotMouse(Position p, Color color);

    public void drawDotScreen(int x, int y, Color color);

    public void drawDotScreen(Position p, Color color);

    public void drawLine(Coordinate ctype, int x1, int y1, int x2, int y2, Color color);

    public void drawLineMap(int x1, int y1, int x2, int y2, Color color);

    public void drawLineMap(Position a, Position b, Color color);

    public void drawLineMouse(int x1, int y1, int x2, int y2, Color color);

    public void drawLineMouse(Position a, Position b, Color color);

    public void drawLineScreen(int x1, int y1, int x2, int y2, Color color);

    public void drawLineScreen(Position a, Position b, Color color);

    public int getLatencyFrames();

    public int getLatencyTime();

    public int getRemainingLatencyFrames();

    public int getRemainingLatencyTime();

    public int getRevision();

    public boolean isDebug();

    public boolean isLatComEnabled();

    public void setLatCom(boolean isEnabled);

    public boolean isGUIEnabled();

    public void setGUI(boolean enabled);

    public int getInstanceNumber();

    public int getAPM(boolean includeSelects);

    public boolean setMap(String cstr_mapFileName);

    public void setFrameSkip(int frameSkip);

    public boolean hasPath(Position source, Position destination);

    public boolean setAlliance(Player player, boolean allied);

    public boolean setAlliance(Player player);

    public boolean setAlliance(Player player, boolean allied, boolean alliedVictory);

    public boolean setVision(Player player);

    public boolean setVision(Player player, boolean enabled);

    public int elapsedTime();

    public void setCommandOptimizationLevel(int level);

    public int countdownTimer();

    */
    public Collection<Region> getAllRegions() {
        return regions.values();
    }

    /*
    public Region getRegionAt(int x, int y);

    public Region getRegionAt(Position position);

    public int getLastEventTime();

    public boolean setRevealAll();

    public boolean setRevealAll(boolean reveal);

    public TilePosition getBuildLocation(UnitType type, TilePosition desiredPosition, int maxRange);

    public TilePosition getBuildLocation(UnitType type, TilePosition desiredPosition);

    public TilePosition getBuildLocation(UnitType type, TilePosition desiredPosition, int maxRange, boolean creep);

    public int getDamageFrom(UnitType fromType, UnitType toType, Player fromPlayer);

    public int getDamageFrom(UnitType fromType, UnitType toType);

    public int getDamageFrom(UnitType fromType, UnitType toType, Player fromPlayer, Player toPlayer);

    public int getDamageTo(UnitType toType, UnitType fromType, Player toPlayer);

    public int getDamageTo(UnitType toType, UnitType fromType);

    public int getDamageTo(UnitType toType, UnitType fromType, Player toPlayer, Player fromPlayer);
    */
}
