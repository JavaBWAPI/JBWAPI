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
    private final ByteBuffer sharedMemory;
    private final ClientData clientData;
    private final EventHandler eventHandler;
    private final FrameBuffer frameBuffer;
    private final Thread botThread;

    BotWrapper(
            BWClientConfiguration configuration,
            ByteBuffer sharedMemory,
            ClientData clientData,
            EventHandler eventHandler) {
        this.configuration = configuration;
        this.sharedMemory = sharedMemory;
        this.clientData = clientData;
        this.eventHandler = eventHandler;

        if (configuration.async) {
            frameBuffer = new FrameBuffer(configuration.asyncFrameBufferSize);
            botThread = new Thread(() -> {
                while(true) {
                    while(frameBuffer.empty()) try {
                        frameBuffer.wait();
                    } catch (InterruptedException ignored) {}

                    frameBuffer.dequeueFrame(clientData);
                    handleEvents();
                    if (!clientData.new GameData(0).isInGame()) {
                        return;
                    }
                }
            });
        } else {
            frameBuffer = null;
            botThread = null;
        }
    }

    void step() {
        if (configuration.async) {
            frameBuffer.enqueueFrame(sharedMemory);
            frameBuffer.notifyAll();
        } else {
            handleEvents();
        }
    }

    private void handleEvents() {
        for (int i = 0; i < clientData.new GameData(0).getEventCount(); i++) {
            eventHandler.operation(clientData.new GameData(0).getEvents(i));
        }
    }


}
