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
import java.util.ArrayList;

/**
 * Circular buffer of game states.
 */
class FrameBuffer {

    private ByteBuffer dataSource;
    private int size;
    private int stepGame = 0;
    private int stepBot = 0;
    private ClientData clientData = new ClientData();
    private ArrayList<ByteBuffer> dataBuffer = new ArrayList<>();

    // Synchronization locks
    private Object stepCount = new Object();

    FrameBuffer(int size, ByteBuffer source) {
        this.size = size;
        this.dataSource = source;
        while(dataBuffer.size() < size) {
            dataBuffer.add(ByteBuffer.allocateDirect(ClientData.GameData.SIZE));
        }
    }

    /**
     * @return The number of frames currently buffered ahead of the bot's current frame
     */
    synchronized int framesBuffered() {
        return stepGame - stepBot;
    }

    /**
     * @return Whether the frame buffer is empty and has no frames available for the bot to consume.
     */
    synchronized boolean empty() {
        return framesBuffered() <= 0;
    }

    /**
     * @return Whether the frame buffer is full and can not buffer any additional frames.
     * When the frame buffer is full, JBWAPI must wait for the bot to complete a frame before returning control to StarCraft.
     */
    synchronized boolean full() {
        return framesBuffered() >= size - 1;
    }

    synchronized private int indexGame() {
        return stepGame % size;
    }

    synchronized private int indexBot() {
        return stepBot % size;
    }

    /**
     * Copy dataBuffer from shared memory into the head of the frame buffer.
     */
    void enqueueFrame() {
        while(full()) try { Thread.sleep(0, 100); } catch (InterruptedException ignored) {}
        int indexGame = indexGame();
        System.out.println("FrameBuffer: Moving game to " + indexGame);
        dataBuffer.get(indexGame).put(dataSource);
        ++stepGame;
    }

    /**
     * Points the bot to the next frame in the buffer.
     */
    ByteBuffer dequeueFrame() {
        while(empty()) try { Thread.sleep(0, 100); } catch (InterruptedException ignored) {}
        System.out.println("FrameBuffer: Moving bot to " + indexBot());
        ByteBuffer output = dataBuffer.get(indexBot());
        ++stepBot;
        return output;
    }
}
