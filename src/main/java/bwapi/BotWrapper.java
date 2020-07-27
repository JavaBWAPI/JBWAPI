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
    private final BWClientConfiguration configuration;
    private final BWEventListener eventListener;
    private final Game game;
    private FrameBuffer frameBuffer = null;
    private Thread botThread = null;
    private volatile boolean idle = false;

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

    void startBot() {
        if (configuration.async) {
            botThread.start();
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
                        while(frameBuffer.empty()) try {
                            idle = true;
                            Thread.sleep(0, 100);
                        } catch (InterruptedException ignored) {}
                        idle = false;
                        System.out.println("Bot thread: Dequeuing frame. There are " + frameBuffer.framesBuffered() + " frames buffered.");
                        game.clientData().setBuffer(frameBuffer.dequeueFrame());
                        System.out.println("Bot thread: Handling events.");
                        handleEvents();
                        System.out.println("Bot thread: Handled events.");
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
