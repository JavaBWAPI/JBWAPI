/*
MIT License

Copyright (c) 2018 Hannes Bredberg
Modified work Copyright (c) 2018 Jasper

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package bwapi;

import java.nio.ByteBuffer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Manages invocation of bot event handlers
 */
class BotWrapper {
    private final BWClientConfiguration configuration;
    private final BWEventListener eventListener;
    private final Game game;
    private FrameBuffer frameBuffer = null;
    private Thread botThread = null;
    private boolean idle = false;

    Lock idleLock = new ReentrantLock();
    Condition idleCondition = idleLock.newCondition();

    BotWrapper(BWClientConfiguration configuration, BWEventListener eventListener, ByteBuffer dataSource) {
        this.configuration = configuration;
        this.eventListener = eventListener;

        ClientData currentClientData = new ClientData();
        currentClientData.setBuffer(dataSource);
        game = new Game(currentClientData);

        if (configuration.async) {
            frameBuffer = new FrameBuffer(configuration.asyncFrameBufferSize, dataSource);
        }
    }

    Game getGame() {
        return game;
    }

    /**
     * True if the bot has handled all enqueued frames and is waiting for a new frame from StarCraft.
     */
    boolean botIdle() {
        return idle || ! configuration.async;
    }

    void step() {
        if (configuration.async) {
            frameBuffer.enqueueFrame();
            if (botThread == null) {
                botThread = new Thread(() -> {
                    //noinspection InfiniteLoopStatement
                    while(true) {
                        // Await non-empty frame buffer
                        frameBuffer.lockCapacity.lock();
                        try {
                            while(frameBuffer.empty()) {
                                // Signal idleness
                                idleLock.lock();
                                try {
                                    idle = true;
                                    idleCondition.signalAll();
                                } finally {
                                    idleLock.unlock();
                                }
                                frameBuffer.conditionEmpty.awaitUninterruptibly();
                            }
                        } finally {
                            frameBuffer.lockCapacity.unlock();
                        }

                        // Signal non-idleness
                        idleLock.lock();
                        try {
                            idle = false;
                            idleCondition.signalAll();
                         } finally {
                            idleLock.unlock();
                        }

                        game.clientData().setBuffer(frameBuffer.dequeueFrame());
                        System.out.println("Bot thread: Handling events while " + frameBuffer.framesBuffered() + " frames behind.");
                        handleEvents();
                        System.out.println("Bot thread: Handled events.");

                        if ( ! game.clientData().gameData().isInGame()) {
                            System.out.println("Bot thread: Ending because game is over.");
                            return;
                        }
                    }});
                botThread.setName("JBWAPI Bot");
                botThread.start();
            }
        } else {
            handleEvents();
        }
    }

    private void handleEvents() {
        ClientData.GameData gameData = game.clientData().gameData();
        for (int i = 0; i < gameData.getEventCount(); i++) {
            EventHandler.operation(eventListener, game, gameData.getEvents(i));
        }
    }
}
