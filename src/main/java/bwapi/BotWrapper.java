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

/**
 * Manages invocation of bot event handlers
 */
class BotWrapper {
    private final ClientData liveClientData = new ClientData();
    private final BWClientConfiguration configuration;
    private final BWEventListener eventListener;
    private final FrameBuffer frameBuffer;
    private Game game;
    private Thread botThread;
    private boolean gameOver;

    BotWrapper(BWClientConfiguration configuration, BWEventListener eventListener) {
        this.configuration = configuration;
        this.eventListener = eventListener;
        frameBuffer = configuration.async ? new FrameBuffer(configuration.asyncFrameBufferSize) : null;
    }

    /**
     * Resets the BotWrapper for a new game.
     */
    void startNewGame(ByteBuffer dataSource) {
        frameBuffer.initialize(dataSource);
        game = new Game(liveClientData);
        liveClientData.setBuffer(dataSource);
        botThread = null;
        gameOver = false;
    }

    /**
     * @return The Game object used by the bot
     * In asynchronous mode this Game object may point at a copy of a previous frame.
     */
    Game getGame() {
        return game;
    }

    /**
     * Handles the arrival of a new frame from BWAPI
     */
    void onFrame() {
        if (configuration.async) {
            long startNanos = System.nanoTime();
            long endNanos = startNanos + configuration.asyncFrameDurationNanos;
            if (botThread == null) {
                System.out.println("Creating bot thread");
                botThread = createBotThread();
                botThread.setName("JBWAPI Bot");
                botThread.start();
            }
            frameBuffer.enqueueFrame();
            frameBuffer.lockSize.lock();
            try {
                while (frameBuffer.empty()) {
                    if (configuration.asyncWaitOnFrameZero && liveClientData.gameData().getFrameCount() == 0) {
                        frameBuffer.conditionSize.await();
                    } else {
                        long remainingNanos = endNanos - System.nanoTime();
                        if (remainingNanos <= 0) break;
                        frameBuffer.conditionSize.awaitNanos(remainingNanos);
                    }
                }
            } catch(InterruptedException ignored) {
            } finally {
                frameBuffer.lockSize.unlock();
            }
        } else {
            handleEvents();
        }
    }

    /**
     * Allows an asynchronous bot time to finish operation
     */
    void endGame() {
        if (botThread != null) {
            try {
                botThread.join();
            } catch (InterruptedException ignored) {}
        }
    }

    private Thread createBotThread() {
        return new Thread(() -> {
            while ( ! gameOver) {
                frameBuffer.lockSize.lock();
                try { while (frameBuffer.empty()) frameBuffer.conditionSize.awaitUninterruptibly(); } finally { frameBuffer.lockSize.unlock(); }

                game.clientData().setBuffer(frameBuffer.peek());
                System.out.println("Bot thread: Handling events.");
                handleEvents();
                System.out.println("Bot thread: Handled events.");
                frameBuffer.dequeue();
            }
            System.out.println("Bot thread: Ending because game is over.");
        });
    }

    private void handleEvents() {
        ClientData.GameData gameData = game.clientData().gameData();
        for (int i = 0; i < gameData.getEventCount(); i++) {
            ClientData.Event event = gameData.getEvents(i);
            EventHandler.operation(eventListener, game, event);
            if (event.getType() == EventType.MatchEnd) {
                gameOver = true;
            }
        }
    }
}
