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
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Circular buffer of game states.
 */
public class FrameBuffer {

    private ByteBuffer data;
    private int size;
    private int stepGame = 0;
    private int stepBot = 0;

    // Synchronization locks
    private Object stepCount;
    private ArrayList<Lock> frameLocks;

    FrameBuffer(int size) {
        this.size = size;
        data = ByteBuffer.allocateDirect(size * ClientData.GameData.SIZE);
        while (frameLocks.size() < size) {
            frameLocks.add(new ReentrantLock());
        }
    }

    /**
     * @return The number of frames currently buffered ahead of the bot's current frame
     */
    int framesBuffered() {
        synchronized (stepCount) {
            return stepGame - stepBot;
        }
    }

    /**
     * @return Whether the frame buffer is empty and has no frames available for the bot to consume.
     */
    boolean empty() {
        synchronized (stepCount) {
            return framesBuffered() <= 0;
        }
    }

    /**
     * @return Whether the frame buffer is full and can not buffer any additional frames.
     * When the frame buffer is full, JBWAPI must wait for the bot to complete a frame before returning control to StarCraft.
     */
    boolean full() {
        synchronized (stepCount) {
            return framesBuffered() >= size - 1;
        }
    }

    /**
     * Copy data from shared memory into the head of the frame buffer.
     */
    void enqueueFrame(ByteBuffer source) {
        while(full()) try {
            wait();
        } catch (InterruptedException ignored) {}
        synchronized (stepCount) {
            data.put(source.array(), indexGame() * ClientData.GameData.SIZE, ClientData.GameData.SIZE);
            ++stepGame;
        }
        notifyAll();
    }

    /**
     * Points the bot to the next frame in the buffer.
     */
    void dequeueFrame(ClientData clientData) {
        while(empty()) try {
            wait();
        } catch (InterruptedException ignored) {}
        synchronized (stepCount) {
            clientData.setBuffer(data);
            clientData.setBufferOffset(indexBot() * ClientData.GameData.SIZE);
            ++stepBot;
        }
        notifyAll();
    }

    private int indexGame() {
        return stepGame % size;
    }

    private int indexBot() {
        return stepBot % size;
    }
}
