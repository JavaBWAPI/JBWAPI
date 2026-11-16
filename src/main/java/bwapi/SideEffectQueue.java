package bwapi;

import java.util.ArrayList;

/**
 * Queue of intended bot interactions with the game, to be flushed as JBWAPI returns control to StarCraft after a frame.
 */
class SideEffectQueue {

    private ArrayList<SideEffect> queue = new ArrayList<>();
    private ArrayList<SideEffect> flushQueue = new ArrayList<>();

    /**
     * Includes a side effect to be sent back to BWAPI in the future.
     *
     * @param sideEffect
     * A side effect to be applied to the game state the next time the queue is flushed.
     */
    synchronized void enqueue(SideEffect sideEffect) {
        queue.add(sideEffect);
    }

    /**
     * Applies all enqueued side effects to the current BWAPI frame.
     *
     * @param liveGameData
     * The live game frame's data, using the BWAPI shared memory.
     */
    void flushTo(ClientData.GameData liveGameData) {
        final ArrayList<SideEffect> toFlush;
        synchronized (this) {
            if (queue.isEmpty()) {
                return;
            }
            toFlush = queue;
            queue = flushQueue;
            flushQueue = toFlush;
        }
        for (int i = 0, size = toFlush.size(); i < size; i++) {
            toFlush.get(i).apply(liveGameData);
        }
        toFlush.clear();
    }
}
