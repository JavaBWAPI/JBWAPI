package bwapi;

import java.nio.ByteBuffer;

/**
 * https://github.com/bwapi/bwapi/blob/456ad612abc84da4103162ba0bf8ec4f053a4b1d/bwapi/include/BWAPI/Client/GameTable.h
 */
class GameInstance {
    final int serverProcessID;
    final boolean isConnected;
    final int lastKeepAliveTime;

    static final int SIZE = 4 // ServerProcID
            + 4 // IsConnected
            + 4 // LastKeepAliveTime
            ;

    public GameInstance(int serverProcessID, boolean isConnected, int lastKeepAliveTime) {
        this.serverProcessID = serverProcessID;
        this.isConnected = isConnected;
        this.lastKeepAliveTime = lastKeepAliveTime;
    }

}

class GameTable {
    static final int MAX_GAME_INSTANCES = 8;
    static final int SIZE = MAX_GAME_INSTANCES * GameInstance.SIZE;

    GameInstance[] gameInstances;

    GameTable(final ByteBuffer gameTableFileHandle) {
        gameInstances = new GameInstance[MAX_GAME_INSTANCES];

        for (int i = 0; i < MAX_GAME_INSTANCES; i++) {
            int serverProcessID = gameTableFileHandle.getInt(GameInstance.SIZE * i);
            boolean isConnected = gameTableFileHandle.get(GameInstance.SIZE * i + 4) != 0;
            int lastKeepAliveTime = gameTableFileHandle.getInt(GameInstance.SIZE * i + 4 + 1);
            gameInstances[i] = new GameInstance(serverProcessID, isConnected, lastKeepAliveTime);
        }
    }
}
