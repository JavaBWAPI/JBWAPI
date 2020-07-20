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
public class BotWrapper {
    private BWClientConfiguration configuration;
    private ByteBuffer sharedMemory;
    private ClientData.GameData gameData;
    private EventHandler eventHandler;
    private FrameBuffer frameBuffer;

    public BotWrapper(
            BWClientConfiguration configuration,
            ByteBuffer sharedMemory,
            ClientData.GameData gameData,
            EventHandler eventHandler) {
        this.configuration = configuration;
        this.sharedMemory = sharedMemory;
        this.gameData = gameData;
        this.eventHandler = eventHandler;

        if (configuration.async) {
            frameBuffer = new FrameBuffer(configuration.asyncFrameBufferSize);
        }
    }

    public void step() {
        if (configuration.async) {
            // TODO: Synchronize
            frameBuffer.enqueueFrame();
        } else {
            for (int i = 0; i < gameData.getEventCount(); i++) {
                eventHandler.operation(gameData.getEvents(i));
            }
        }
    }
}
