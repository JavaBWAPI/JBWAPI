package bwapi;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.win32.W32APIOptions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.StandardCharsets;

class Client {
    public static final int READ_WRITE = 0x1 | 0x2 | 0x4;
    private static final int GAME_SIZE = 4 // ServerProcID
            + 4 // IsConnected
            + 4 // LastKeepAliveTime
            ;

    private static final int maxNumGames = 8;
    private static final int gameTableSize = GAME_SIZE * maxNumGames;
    private ByteBuffer sharedMemory;
    private LittleEndianPipe pipe;
    private final GameData data = new GameData();

    public Client() throws Exception {
        final ByteBuffer gameList = Kernel32.INSTANCE.MapViewOfFile(MappingKernel.INSTANCE.OpenFileMapping(READ_WRITE, false, "Local\\bwapi_shared_memory_game_list"), READ_WRITE, 0, 0, gameTableSize).getByteBuffer(0, GAME_SIZE * 8);
        gameList.order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < 8; ++i) {
            final int procID = gameList.getInt(GAME_SIZE * i);
            final boolean connected = gameList.get(GAME_SIZE * i + 4) != 0;

            if (procID != 0 && !connected) {
                try {
                    this.connect(procID);
                    return;
                } catch (Exception ignored) {
                }
            }
        }
        throw new Exception("All servers busy!");
    }

    public GameData data() {
        return data;
    }

    private void connect(final int procID) throws Exception {
        pipe = new LittleEndianPipe("\\\\.\\pipe\\bwapi_pipe_" + procID, "rw");
        sharedMemory = Kernel32.INSTANCE.MapViewOfFile(MappingKernel.INSTANCE.OpenFileMapping(READ_WRITE, false, "Local\\bwapi_shared_memory_" + procID), READ_WRITE, 0, 0, GameData.SIZE).getByteBuffer(0, GameData.SIZE);
        sharedMemory.order(ByteOrder.LITTLE_ENDIAN);
        int code = 1;
        while (code != 2) {
            code = pipe.readInt();
        }
        System.out.println("Connected to BWAPI@" + procID + " with version " + data.getClientVersion() + ": " + data.getRevision());
    }

    public void update(final EventHandler handler) throws Exception {
        int code = 1;
        pipe.writeInt(code);
        while (code != 2) {
            code = pipe.readInt();
        }
        for (int i = 0; i < data.eventCount(); ++i) {
            handler.operation(data.event(i));
        }
    }

    interface MappingKernel extends Kernel32 {
        MappingKernel INSTANCE = Native.loadLibrary(MappingKernel.class, W32APIOptions.DEFAULT_OPTIONS);

        HANDLE OpenFileMapping(int desiredAccess, boolean inherit, String name);
    }

    public interface EventHandler {
        void operation(GameData.Event event);
    }

    public static class UnitCommand {
        private static final int SIZE = 24;
        final int type;
        final int unit;
        final int target;
        final int x;
        final int y;
        final int extra;

        public UnitCommand(final int type, final int unit, final int target, final int x, final int y, final int extra) {
            this.type = type;
            this.unit = unit;
            this.target = target;
            this.x = x;
            this.y = y;
            this.extra = extra;
        }
    }

    public static class Command {
        private static final int SIZE = 12;
        final int type;
        final int value1;
        final int value2;

        public Command(final int type, final int value1, final int value2) {
            this.type = type;
            this.value1 = value1;
            this.value2 = value2;
        }
    }

    public static class Shape {
        private static final int SIZE = 40;
        final int type;
        final int coordType;
        final int x1;
        final int y1;
        final int x2;
        final int y2;
        final int extra1;
        final int extra2;
        final int color;
        final byte isSolid;

        public Shape(final int type, final int coordType, final int x1, final int y1, final int x2, final int y2, final int extra1, final int extra2, final int color, final boolean isSolid) {
            this.type = type;
            this.coordType = coordType;
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.extra1 = extra1;
            this.extra2 = extra2;
            this.color = color;
            this.isSolid = (byte) (isSolid ? 1 : 0);
        }
    }

    class LittleEndianPipe {
        RandomAccessFile pipe;

        LittleEndianPipe(final String name, final String mode) throws FileNotFoundException {
            pipe = new RandomAccessFile(name, mode);
        }

        final int readByte() throws IOException {
            return pipe.readByte();
        }

        final void writeByte(final int b) throws IOException {
            pipe.writeByte(b);
        }

        final int readInt() throws IOException {
            final int b1 = readByte();
            final int b2 = readByte();
            final int b3 = readByte();
            final int b4 = readByte();
            return (b4 << 24) | (b3 << 16) | (b2 << 8) | b1;
        }

        final void writeInt(final int i) throws IOException {
            writeByte(i >> 24);
            writeByte((i >> 16) & 0xff);
            writeByte((i >> 8) & 0xff);
            writeByte(i & 0xff);
        }
    }

    public final class GameData {
        private static final int MetaOffset = 0;
        private static final int ForceOffset = 0x18;
        private static final int PlayerOffset = 0xbc;
        private static final int UnitOffset = 0x11010;
        private static final int BulletOffset = 0x346fa8;
        private static final int NukeOffset = 0x348ee8;
        private static final int GameOffset = 0x34952c;
        private static final int RegionOffset = 0x500e0c;
        private static final int StartOffset = 0xa18970;
        private static final int GameStatusOffset = 0xa189b4;
        private static final int SelectionOffset = 0xa189bc;
        private static final int PlayersOffset = 0xa189f0;
        private static final int EventOffset = 0xa189fc;
        private static final int StringOffset = 0xa35ec0;
        private static final int ShapeOffset = 0x1dfc6c8;
        private static final int CommandOffset = 0x1ebfbcc;
        private static final int UnitCommandOffset = 0x1efa550;
        private static final int SIZE = 0x1f7ccd8;
        private final CharsetEncoder enc = Charset.forName("ISO-8859-1").newEncoder();

        private String parseString(final int offset, final int maxLen) {
            final byte[] buf = new byte[maxLen];
            sharedMemory.position(offset);
            sharedMemory.get(buf, 0, maxLen);
            sharedMemory.position(0);
            int len = 0;
            while (len < buf.length && buf[len] != 0) {
                ++len;
            }
            return new String(buf, 0, len, StandardCharsets.ISO_8859_1);
        }

        private double parseDouble(final int offset) {
            final byte[] buf = new byte[8];
            sharedMemory.position(offset);
            sharedMemory.get(buf, 0, 8);
            sharedMemory.position(0);
            final ByteBuffer bb = ByteBuffer.wrap(buf);
            bb.order(ByteOrder.LITTLE_ENDIAN);
            return bb.getDouble();
        }

        public int getClientVersion() {
            return sharedMemory.getInt(MetaOffset);
        }

        public int getRevision() {
            return sharedMemory.getInt(MetaOffset + 4);
        }

        public boolean isDebug() {
            return sharedMemory.get(MetaOffset + 8) != 0;
        }

        public int getInstanceID() {
            return sharedMemory.getInt(MetaOffset + 12);
        }

        public int getBotAPM_noselects() {
            return sharedMemory.getInt(MetaOffset + 16);
        }

        public int getBotAPM_selects() {
            return sharedMemory.getInt(MetaOffset + 20);
        }

        public int getForceCount() {
            return sharedMemory.getInt(ForceOffset);
        }

        public ForceData getForce(final int id) {
            return new ForceData(id);
        }

        public int getPlayerCount() {
            return sharedMemory.getInt(PlayerOffset);
        }

        public PlayerData getPlayer(final int id) {
            return new PlayerData(id);
        }

        public int getInitialUnitCount() {
            return sharedMemory.getInt(UnitOffset);
        }

        public UnitData getUnit(final int id) {
            return new UnitData(id);
        }

        public int bulletCount() {
            return 100;
        }

        public BulletData getBullet(final int index) {
            return new BulletData(index);
        }

        public int nukeDotCount() {
            return sharedMemory.getInt(NukeOffset);
        }

        public int getNukeDotX(final int id) {
            return sharedMemory.getInt(NukeOffset + id * 8 + 4);
        }

        public int getNukeDotY(final int id) {
            return sharedMemory.getInt(NukeOffset + id * 8 + 8);
        }

        public int gameType() {
            return sharedMemory.getInt(GameOffset);
        }

        public int latency() {
            return sharedMemory.getInt(GameOffset + 4);
        }

        public int latencyFrames() {
            return sharedMemory.getInt(GameOffset + 8);
        }

        public int latencyTime() {
            return sharedMemory.getInt(GameOffset + 12);
        }

        public int remainingLatencyFrames() {
            return sharedMemory.getInt(GameOffset + 16);
        }

        public int remainingLatencyTime() {
            return sharedMemory.getInt(GameOffset + 20);
        }

        public boolean hasLatCom() {
            return sharedMemory.get(GameOffset + 24) != 0;
        }

        public boolean hasGUI() {
            return sharedMemory.get(GameOffset + 25) != 0;
        }

        public int replayFrameCount() {
            return sharedMemory.getInt(GameOffset + 28);
        }

        public int randomSeed() {
            return sharedMemory.getInt(GameOffset + 32);
        }

        public int frameCount() {
            return sharedMemory.getInt(GameOffset + 36);
        }

        public int elapsedTime() {
            return sharedMemory.getInt(GameOffset + 40);
        }

        public int countdownTimer() {
            return sharedMemory.getInt(GameOffset + 44);
        }

        public int fps() {
            return sharedMemory.getInt(GameOffset + 48);
        }

        public double averageFPS() {
            return parseDouble(GameOffset + 52);
        }

        public int mouseX() {
            return sharedMemory.getInt(GameOffset + 60);
        }

        public int mouseY() {
            return sharedMemory.getInt(GameOffset + 64);
        }

        public boolean mouseState(final int button) {
            return sharedMemory.get(GameOffset + 68 + button) != 0;
        }

        public boolean keyState(final int key) {
            return sharedMemory.get(GameOffset + 71 + key) != 0;
        }

        public int screenX() {
            return sharedMemory.getInt(GameOffset + 328);
        }

        public int screenY() {
            return sharedMemory.getInt(GameOffset + 332);
        }

        public boolean getFlag(final int f) {
            return sharedMemory.get(GameOffset + 336 + f) != 0;
        }

        public void setFlag(final int f, final boolean value) {
            sharedMemory.put(GameOffset + 336 + f, (byte) (value ? 1 : 0));
        }

        public int mapWidth() {
            return sharedMemory.getInt(GameOffset + 340);
        }

        public int mapHeight() {
            return sharedMemory.getInt(GameOffset + 344);
        }

        public String mapFileName() {
            return parseString(GameOffset + 348, 261);
        }

        public String mapPathName() {
            return parseString(GameOffset + 609, 261);
        }

        public String mapName() {
            return parseString(GameOffset + 870, 33);
        }

        public String mapHash() {
            return parseString(GameOffset + 903, 41);
        }

        public int groundHeight(final int x, final int y) {
            return sharedMemory.getInt(GameOffset + 944 + x * 1024 + y * 4);
        }

        public boolean walkable(final int x, final int y) {
            return sharedMemory.get(GameOffset + 263088 + x * 1024 + y) != 0;
        }

        public boolean buildable(final int x, final int y) {
            return sharedMemory.get(GameOffset + 1311664 + x * 256 + y) != 0;
        }

        public boolean visible(final int x, final int y) {
            return sharedMemory.get(GameOffset + 1377200 + x * 256 + y) != 0;
        }

        public boolean explored(final int x, final int y) {
            return sharedMemory.get(GameOffset + 1442736 + x * 256 + y) != 0;
        }

        public boolean hasCreep(final int x, final int y) {
            return sharedMemory.get(GameOffset + 1508272 + x * 256 + y) != 0;
        }

        public boolean occupied(final int x, final int y) {
            return sharedMemory.get(GameOffset + 1573808 + x * 256 + y) != 0;
        }

        public short mapTileRegionID(final int x, final int y) {
            return sharedMemory.getShort(GameOffset + 1639344 + x * 512 + y * 2);
        }

        public short mapSplitTilesMiniTileMask(final int idx) {
            return sharedMemory.getShort(GameOffset + 1770416 + idx * 2);
        }

        public short mapSplitTilesRegion1(final int idx) {
            return sharedMemory.getShort(GameOffset + 1780416 + idx * 2);
        }

        public short mapSplitTilesRegion2(final int idx) {
            return sharedMemory.getShort(GameOffset + 1790416 + idx * 2);
        }

        public int regionCount() {
            return sharedMemory.getInt(RegionOffset);
        }

        public RegionData getRegion(final int index) {
            return new RegionData(index);
        }

        public int startLocationCount() {
            return sharedMemory.get(StartOffset);
        }

        public int startLocationX(final int location) {
            return sharedMemory.get(StartOffset + location * 8 + 4);
        }

        public int startLocationY(final int location) {
            return sharedMemory.get(StartOffset + location * 8 + 8);
        }

        public boolean isInGame() {
            return sharedMemory.get(GameStatusOffset) != 0;
        }

        public boolean isMultiplayer() {
            return sharedMemory.get(GameStatusOffset + 1) != 0;
        }

        public boolean isBattleNet() {
            return sharedMemory.get(GameStatusOffset + 2) != 0;
        }

        public boolean isPaused() {
            return sharedMemory.get(GameStatusOffset + 3) != 0;
        }

        public boolean isReplay() {
            return sharedMemory.get(GameStatusOffset + 4) != 0;
        }

        public int selectedUnitCount() {
            return sharedMemory.getInt(SelectionOffset);
        }

        public int selectedUnit(final int idx) {
            return sharedMemory.getInt(SelectionOffset + 4 + 4 * idx);
        }

        public int self() {
            return sharedMemory.getInt(PlayersOffset);
        }

        public int enemy() {
            return sharedMemory.getInt(PlayersOffset + 4);
        }

        public int neutral() {
            return sharedMemory.getInt(PlayersOffset + 8);
        }

        public int eventCount() {
            return sharedMemory.getInt(EventOffset);
        }

        private Event event(final int idx) {
            return new Event(idx);
        }

        public int eventStringCount() {
            return sharedMemory.getInt(StringOffset);
        }

        public String eventString(final int s) {
            return parseString(StringOffset + 4 + 256 * s, 256);
        }

        public int addString(final String s) {
            final int len = s.length() + 1;
            if (len >= 1024) {
                throw new StringIndexOutOfBoundsException();
            }
            final int at = sharedMemory.getInt(StringOffset + 256004);
            final byte b[] = new byte[len];
            enc.encode(CharBuffer.wrap(s), ByteBuffer.wrap(b), true);
            sharedMemory.position(StringOffset + 256008 + at * 1024);
            sharedMemory.put(b, 0, len);
            sharedMemory.position(0);
            sharedMemory.putInt(StringOffset + 256004, at + 1);
            return at;
        }

        public void addShape(final Shape shape) {
            final int at = sharedMemory.getInt(ShapeOffset);
            sharedMemory.putInt(ShapeOffset + at * Shape.SIZE + 4, shape.type);
            sharedMemory.putInt(ShapeOffset + at * Shape.SIZE + 8, shape.coordType);
            sharedMemory.putInt(ShapeOffset + at * Shape.SIZE + 12, shape.x1);
            sharedMemory.putInt(ShapeOffset + at * Shape.SIZE + 16, shape.y1);
            sharedMemory.putInt(ShapeOffset + at * Shape.SIZE + 20, shape.x2);
            sharedMemory.putInt(ShapeOffset + at * Shape.SIZE + 24, shape.y2);
            sharedMemory.putInt(ShapeOffset + at * Shape.SIZE + 28, shape.extra1);
            sharedMemory.putInt(ShapeOffset + at * Shape.SIZE + 32, shape.extra2);
            sharedMemory.putInt(ShapeOffset + at * Shape.SIZE + 36, shape.color);
            sharedMemory.put(ShapeOffset + at * Shape.SIZE + 40, shape.isSolid);
            sharedMemory.putInt(ShapeOffset, at + 1);
        }

        public void addCommand(final Command command) {
            final int at = sharedMemory.getInt(CommandOffset);
            sharedMemory.putInt(CommandOffset + at * Command.SIZE + 4, command.type);
            sharedMemory.putInt(CommandOffset + at * Command.SIZE + 8, command.value1);
            sharedMemory.putInt(CommandOffset + at * Command.SIZE + 12, command.value2);
            sharedMemory.putInt(CommandOffset, at + 1);
        }

        public void addUnitCommand(final UnitCommand command) {
            final int at = sharedMemory.getInt(UnitCommandOffset);
            sharedMemory.putInt(UnitCommandOffset + at * UnitCommand.SIZE + 4, command.type);
            sharedMemory.putInt(UnitCommandOffset + at * UnitCommand.SIZE + 8, command.unit);
            sharedMemory.putInt(UnitCommandOffset + at * UnitCommand.SIZE + 12, command.target);
            sharedMemory.putInt(UnitCommandOffset + at * UnitCommand.SIZE + 16, command.x);
            sharedMemory.putInt(UnitCommandOffset + at * UnitCommand.SIZE + 20, command.y);
            sharedMemory.putInt(UnitCommandOffset + at * UnitCommand.SIZE + 24, command.extra);
            sharedMemory.putInt(UnitCommandOffset, at + 1);
        }

        public class ForceData {
            static final int SIZE = 32;
            private final int id;

            public ForceData(final int id) {
                this.id = id;
            }

            public int id() {
                return id;
            }

            public String name() {
                return parseString(ForceOffset + 4 + ForceData.SIZE * id, 32);
            }
        }

        public class PlayerData {
            static final int SIZE = 5788;
            private final int id;

            public PlayerData(final int id) {
                this.id = id;
            }

            public int id() {
                return id;
            }

            public String name() {
                return parseString(PlayerOffset + 4 + PlayerData.SIZE * id, 25);
            }

            public int race() {
                return sharedMemory.getInt(PlayerOffset + 4 + PlayerData.SIZE * id + 28);
            }

            public int type() {
                return sharedMemory.getInt(PlayerOffset + 4 + PlayerData.SIZE * id + 32);
            }

            public int force() {
                return sharedMemory.getInt(PlayerOffset + 4 + PlayerData.SIZE * id + 36);
            }

            public boolean isAlly(final int otherPlayer) {
                return sharedMemory.get(PlayerOffset + 4 + PlayerData.SIZE * id + 40 + otherPlayer) != 0;
            }

            public boolean isEnemy(final int otherPlayer) {
                return sharedMemory.get(PlayerOffset + 4 + PlayerData.SIZE * id + 52 + otherPlayer) != 0;
            }

            public boolean isNeutral() {
                return sharedMemory.get(PlayerOffset + 4 + PlayerData.SIZE * id + 64) != 0;
            }

            public int startLocationX() {
                return sharedMemory.getInt(PlayerOffset + 4 + PlayerData.SIZE * id + 68);
            }

            public int startLocationY() {
                return sharedMemory.getInt(PlayerOffset + 4 + PlayerData.SIZE * id + 72);
            }

            public boolean isVictorious() {
                return sharedMemory.get(PlayerOffset + 4 + PlayerData.SIZE * id + 76) != 0;
            }

            public boolean isDefeated() {
                return sharedMemory.get(PlayerOffset + 4 + PlayerData.SIZE * id + 77) != 0;
            }

            public boolean leftGame() {
                return sharedMemory.get(PlayerOffset + 4 + PlayerData.SIZE * id + 78) != 0;
            }

            public boolean isParticipating() {
                return sharedMemory.get(PlayerOffset + 4 + PlayerData.SIZE * id + 79) != 0;
            }

            public int minerals() {
                return sharedMemory.getInt(PlayerOffset + 4 + PlayerData.SIZE * id + 80);
            }

            public int gas() {
                return sharedMemory.getInt(PlayerOffset + 4 + PlayerData.SIZE * id + 84);
            }

            public int gatheredMinerals() {
                return sharedMemory.getInt(PlayerOffset + 4 + PlayerData.SIZE * id + 88);
            }

            public int gatheredGas() {
                return sharedMemory.getInt(PlayerOffset + 4 + PlayerData.SIZE * id + 92);
            }

            public int repairedMinerals() {
                return sharedMemory.getInt(PlayerOffset + 4 + PlayerData.SIZE * id + 96);
            }

            public int repairedGas() {
                return sharedMemory.getInt(PlayerOffset + 4 + PlayerData.SIZE * id + 100);
            }

            public int refundedMinerals() {
                return sharedMemory.getInt(PlayerOffset + 4 + PlayerData.SIZE * id + 104);
            }

            public int refundedGas() {
                return sharedMemory.getInt(PlayerOffset + 4 + PlayerData.SIZE * id + 108);
            }

            public int supplyTotal(final int race) {
                return sharedMemory.getInt(PlayerOffset + 4 + PlayerData.SIZE * id + 112 + race * 4);
            }

            public int supplyUsed(final int race) {
                return sharedMemory.getInt(PlayerOffset + 4 + PlayerData.SIZE * id + 124 + race * 4);
            }

            public int allUnitCount(final int unitType) {
                return sharedMemory.getInt(PlayerOffset + 4 + PlayerData.SIZE * id + 136 + unitType * 4);
            }

            public int visibleUnitCount(final int unitType) {
                return sharedMemory.getInt(PlayerOffset + 4 + PlayerData.SIZE * id + 1072 + unitType * 4);
            }

            public int completedUnitCount(final int unitType) {
                return sharedMemory.getInt(PlayerOffset + 4 + PlayerData.SIZE * id + 2008 + unitType * 4);
            }

            public int deadUnitCount(final int unitType) {
                return sharedMemory.getInt(PlayerOffset + 4 + PlayerData.SIZE * id + 2944 + unitType * 4);
            }

            public int killedUnitCount(final int unitType) {
                return sharedMemory.getInt(PlayerOffset + 4 + PlayerData.SIZE * id + 3880 + unitType * 4);
            }

            public int upgradeLevel(final int upgradeType) {
                return sharedMemory.getInt(PlayerOffset + 4 + PlayerData.SIZE * id + 4816 + upgradeType * 4);
            }

            public boolean hasResearched(final int techType) {
                return sharedMemory.get(PlayerOffset + 4 + PlayerData.SIZE * id + 5068 + techType) != 0;
            }

            public boolean isResearching(final int techType) {
                return sharedMemory.get(PlayerOffset + 4 + PlayerData.SIZE * id + 5115 + techType) != 0;
            }

            public boolean isUpgrading(final int upgradeType) {
                return sharedMemory.get(PlayerOffset + 4 + PlayerData.SIZE * id + 5162 + upgradeType) != 0;
            }

            public int color() {
                return sharedMemory.getInt(PlayerOffset + 4 + PlayerData.SIZE * id + 5228);
            }

            public int totalUnitScore() {
                return sharedMemory.getInt(PlayerOffset + 4 + PlayerData.SIZE * id + 5232);
            }

            public int totalKillScore() {
                return sharedMemory.getInt(PlayerOffset + 4 + PlayerData.SIZE * id + 5227);
            }

            public int totalBuildingScore() {
                return sharedMemory.getInt(PlayerOffset + 4 + PlayerData.SIZE * id + 5240);
            }

            public int totalRazingScore() {
                return sharedMemory.getInt(PlayerOffset + 4 + PlayerData.SIZE * id + 5244);
            }

            public int customScore() {
                return sharedMemory.getInt(PlayerOffset + 4 + PlayerData.SIZE * id + 5248);
            }

            public int maxUpgradeLevel(final int upgradeType) {
                return sharedMemory.getInt(PlayerOffset + 4 + PlayerData.SIZE * id + 5252 + upgradeType * 4);
            }

            public boolean isResearchAvailable(final int techType) {
                return sharedMemory.get(PlayerOffset + 4 + PlayerData.SIZE * id + 5504 + techType) != 0;
            }

            public boolean isUnitAvailable(final int unitType) {
                return sharedMemory.get(PlayerOffset + 4 + PlayerData.SIZE * id + 5551 + unitType) != 0;
            }
        }

        public class UnitData {
            static final int SIZE = 336;
            private final int id;

            UnitData(final int id) {
                this.id = id;
            }

            public int clearanceLevel() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id);
            }

            public int id() {
                return id;
            }

            public int player() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 8);
            }

            public int type() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 12);
            }

            public int positionX() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 16);
            }

            public int positionY() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 20);
            }

            public double angle() {
                return parseDouble(UnitOffset + 8 + UnitData.SIZE * id + 24);
            }

            public double velocityX() {
                return parseDouble(UnitOffset + 8 + UnitData.SIZE * id + 32);
            }

            public double velocityY() {
                return parseDouble(UnitOffset + 8 + UnitData.SIZE * id + 40);
            }

            public int hitPoints() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 48);
            }

            public int lastHitPoints() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 52);
            }

            public int shields() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 56);
            }

            public int energy() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 60);
            }

            public int resources() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 64);
            }

            public int resourceGroup() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 68);
            }

            public int killCount() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 72);
            }

            public int acidSporeCount() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 76);
            }

            public int scarabCount() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 80);
            }

            public int interceptorCount() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 84);
            }

            public int spiderMineCount() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 88);
            }

            public int groundWeaponCooldown() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 92);
            }

            public int airWeaponCooldown() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 96);
            }

            public int spellCooldown() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 100);
            }

            public int defenseMatrixPoints() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 104);
            }

            public int defenseMatrixTimer() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 108);
            }

            public int ensnareTimer() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 112);
            }

            public int irradiateTimer() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 116);
            }

            public int lockdownTimer() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 120);
            }

            public int maelstromTimer() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 124);
            }

            public int orderTimer() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 128);
            }

            public int plagueTimer() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 132);
            }

            public int removeTimer() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 136);
            }

            public int stasisTimer() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 140);
            }

            public int stimTimer() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 144);
            }

            public int buildType() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 148);
            }

            public int trainingQueueCount() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 152);
            }

            public int trainingQueue(final int pos) {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 156 + pos * 4);
            }

            public int tech() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 176);
            }

            public int upgrade() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 180);
            }

            public int remainingBuildTime() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 184);
            }

            public int remainingTrainTime() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 188);
            }

            public int remainingResearchTime() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 192);
            }

            public int remainingUpgradeTime() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 196);
            }

            public int buildUnit() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 200);
            }

            public int target() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 204);
            }

            public int targetPositionX() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 208);
            }

            public int targetPositionY() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 212);
            }

            public int order() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 216);
            }

            public int orderTarget() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 220);
            }

            public int orderTargetPositionX() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 224);
            }

            public int orderTargetPositionY() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 228);
            }

            public int secondaryOrder() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 232);
            }

            public int rallyPositionX() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 236);
            }

            public int rallyPositionY() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 240);
            }

            public int rallyUnit() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 244);
            }

            public int addon() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 248);
            }

            public int nydusExit() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 252);
            }

            public int powerUp() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 256);
            }

            public int transport() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 260);
            }

            public int carrier() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 264);
            }

            public int hatchery() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 268);
            }

            public boolean exists() {
                return sharedMemory.get(UnitOffset + 8 + UnitData.SIZE * id + 272) != 0;
            }

            public boolean hasNuke() {
                return sharedMemory.get(UnitOffset + 8 + UnitData.SIZE * id + 273) != 0;
            }

            public boolean isAccelerating() {
                return sharedMemory.get(UnitOffset + 8 + UnitData.SIZE * id + 274) != 0;
            }

            public boolean isAttacking() {
                return sharedMemory.get(UnitOffset + 8 + UnitData.SIZE * id + 275) != 0;
            }

            public boolean isAttackFrame() {
                return sharedMemory.get(UnitOffset + 8 + UnitData.SIZE * id + 276) != 0;
            }

            public boolean isBeingGathered() {
                return sharedMemory.get(UnitOffset + 8 + UnitData.SIZE * id + 277) != 0;
            }

            public boolean isBlind() {
                return sharedMemory.get(UnitOffset + 8 + UnitData.SIZE * id + 278) != 0;
            }

            public boolean isBraking() {
                return sharedMemory.get(UnitOffset + 8 + UnitData.SIZE * id + 279) != 0;
            }

            public boolean isBurrowed() {
                return sharedMemory.get(UnitOffset + 8 + UnitData.SIZE * id + 280) != 0;
            }

            public int carryResourceType() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 284);
            }

            public boolean isCloaked() {
                return sharedMemory.get(UnitOffset + 8 + UnitData.SIZE * id + 288) != 0;
            }

            public boolean isCompleted() {
                return sharedMemory.get(UnitOffset + 8 + UnitData.SIZE * id + 289) != 0;
            }

            public boolean isConstructing() {
                return sharedMemory.get(UnitOffset + 8 + UnitData.SIZE * id + 290) != 0;
            }

            public boolean isDetected() {
                return sharedMemory.get(UnitOffset + 8 + UnitData.SIZE * id + 291) != 0;
            }

            public boolean isGathering() {
                return sharedMemory.get(UnitOffset + 8 + UnitData.SIZE * id + 292) != 0;
            }

            public boolean isHallucination() {
                return sharedMemory.get(UnitOffset + 8 + UnitData.SIZE * id + 293) != 0;
            }

            public boolean isIdle() {
                return sharedMemory.get(UnitOffset + 8 + UnitData.SIZE * id + 294) != 0;
            }

            public boolean isInterruptible() {
                return sharedMemory.get(UnitOffset + 8 + UnitData.SIZE * id + 295) != 0;
            }

            public boolean isInvincible() {
                return sharedMemory.get(UnitOffset + 8 + UnitData.SIZE * id + 296) != 0;
            }

            public boolean isLifted() {
                return sharedMemory.get(UnitOffset + 8 + UnitData.SIZE * id + 297) != 0;
            }

            public boolean isMorphing() {
                return sharedMemory.get(UnitOffset + 8 + UnitData.SIZE * id + 298) != 0;
            }

            public boolean isMoving() {
                return sharedMemory.get(UnitOffset + 8 + UnitData.SIZE * id + 299) != 0;
            }

            public boolean isParasited() {
                return sharedMemory.get(UnitOffset + 8 + UnitData.SIZE * id + 300) != 0;
            }

            public boolean isSelected() {
                return sharedMemory.get(UnitOffset + 8 + UnitData.SIZE * id + 301) != 0;
            }

            public boolean isStartingAttack() {
                return sharedMemory.get(UnitOffset + 8 + UnitData.SIZE * id + 302) != 0;
            }

            public boolean isStuck() {
                return sharedMemory.get(UnitOffset + 8 + UnitData.SIZE * id + 303) != 0;
            }

            public boolean isTraining() {
                return sharedMemory.get(UnitOffset + 8 + UnitData.SIZE * id + 304) != 0;
            }

            public boolean isUnderStorm() {
                return sharedMemory.get(UnitOffset + 8 + UnitData.SIZE * id + 305) != 0;
            }

            public boolean isUnderDarkSwarm() {
                return sharedMemory.get(UnitOffset + 8 + UnitData.SIZE * id + 306) != 0;
            }

            public boolean isUnderDWeb() {
                return sharedMemory.get(UnitOffset + 8 + UnitData.SIZE * id + 307) != 0;
            }

            public boolean isPowered() {
                return sharedMemory.get(UnitOffset + 8 + UnitData.SIZE * id + 308) != 0;
            }

            public boolean isVisible(final int player) {
                return sharedMemory.get(UnitOffset + 8 + UnitData.SIZE * id + 309 + player) != 0;
            }

            public int buttonset() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 320);
            }

            public int lastAttackerPlayer() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 324);
            }

            public boolean recentlyAttacked() {
                return sharedMemory.get(UnitOffset + 8 + UnitData.SIZE * id + 328) != 0;
            }

            public int replayID() {
                return sharedMemory.getInt(UnitOffset + 8 + UnitData.SIZE * id + 332);
            }
        }

        public class BulletData {
            static final int SIZE = 80;
            private final int index;

            BulletData(final int index) {
                this.index = index;
            }

            public int id() {
                return sharedMemory.getInt(BulletOffset + index * BulletData.SIZE);
            }

            public int player() {
                return sharedMemory.getInt(BulletOffset + index * BulletData.SIZE + 4);
            }

            public int type() {
                return sharedMemory.getInt(BulletOffset + index * BulletData.SIZE + 8);
            }

            public int source() {
                return sharedMemory.getInt(BulletOffset + index * BulletData.SIZE + 12);
            }

            public int positionX() {
                return sharedMemory.getInt(BulletOffset + index * BulletData.SIZE + 16);
            }

            public int positionY() {
                return sharedMemory.getInt(BulletOffset + index * BulletData.SIZE + 20);
            }

            public double angle() {
                return parseDouble(BulletOffset + index * BulletData.SIZE + 24);
            }

            public double velocityX() {
                return parseDouble(BulletOffset + index * BulletData.SIZE + 32);
            }

            public double velocityY() {
                return parseDouble(BulletOffset + index * BulletData.SIZE + 40);
            }

            public int target() {
                return sharedMemory.getInt(BulletOffset + index * BulletData.SIZE + 48);
            }

            public int targetPositionX() {
                return sharedMemory.getInt(BulletOffset + index * BulletData.SIZE + 52);
            }

            public int targetPositionY() {
                return sharedMemory.getInt(BulletOffset + index * BulletData.SIZE + 56);
            }

            public int removeTimer() {
                return sharedMemory.getInt(BulletOffset + index * BulletData.SIZE + 60);
            }

            public boolean exists() {
                return sharedMemory.get(BulletOffset + index * BulletData.SIZE + 64) != 0;
            }

            public boolean isVisible(final int player) {
                return sharedMemory.get(BulletOffset + index * BulletData.SIZE + 65 + player) != 0;
            }
        }

        public class RegionData {
            private static final int SIZE = 1068;
            int index;

            RegionData(final int index) {
                this.index = index;
            }

            public int id() {
                return sharedMemory.getInt(RegionOffset + 4 + index * RegionData.SIZE);
            }

            public int islandID() {
                return sharedMemory.getInt(RegionOffset + 4 + index * RegionData.SIZE + 4);
            }

            public int centerX() {
                return sharedMemory.getInt(RegionOffset + 4 + index * RegionData.SIZE + 8);
            }

            public int centerY() {
                return sharedMemory.getInt(RegionOffset + 4 + index * RegionData.SIZE + 12);
            }

            public int priority() {
                return sharedMemory.getInt(RegionOffset + 4 + index * RegionData.SIZE + 16);
            }

            public int leftMost() {
                return sharedMemory.getInt(RegionOffset + 4 + index * RegionData.SIZE + 20);
            }

            public int rightMost() {
                return sharedMemory.getInt(RegionOffset + 4 + index * RegionData.SIZE + 24);
            }

            public int topMost() {
                return sharedMemory.getInt(RegionOffset + 4 + index * RegionData.SIZE + 28);
            }

            public int bottomMost() {
                return sharedMemory.getInt(RegionOffset + 4 + index * RegionData.SIZE + 32);
            }

            public int neighborCount() {
                return sharedMemory.getInt(RegionOffset + 4 + index * RegionData.SIZE + 36);
            }

            public int neighbor(final int n) {
                return sharedMemory.getInt(RegionOffset + 4 + index * RegionData.SIZE + 40 + n * 4);
            }

            public boolean isAccessible() {
                return sharedMemory.get(RegionOffset + 4 + index * RegionData.SIZE + 1064) != 0;
            }

            public boolean isHigherGround() {
                return sharedMemory.get(RegionOffset + 4 + index * RegionData.SIZE + 1065) != 0;
            }
        }

        public class Event {
            private static final int SIZE = 12;
            final int idx;

            private Event(final int idx) {
                this.idx = idx;
            }

            public int type() {
                return sharedMemory.getInt(EventOffset + 4 + idx * Event.SIZE);
            }

            public int v1() {
                return sharedMemory.getInt(EventOffset + 8 + idx * Event.SIZE);
            }

            public int v2() {
                return sharedMemory.getInt(EventOffset + 12 + idx * Event.SIZE);
            }
        }
    }
}