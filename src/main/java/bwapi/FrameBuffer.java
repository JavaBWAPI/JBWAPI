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
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Circular buffer of game states.
 */
class FrameBuffer {

    private ByteBuffer dataSource;
    private int size;
    private int stepGame = 0;
    private int stepBot = 0;
    private ArrayList<ByteBuffer> dataBuffer = new ArrayList<>();

    // Synchronization locks
    private final Lock lockWrite = new ReentrantLock();
    private final Lock lockRead = lockWrite;
    final Lock lockSize = new ReentrantLock();
    final Condition conditionSize = lockSize.newCondition();

    FrameBuffer(int size) {
        this.size = size;
        while(dataBuffer.size() < size) {
            dataBuffer.add(ByteBuffer.allocateDirect(ClientData.GameData.SIZE));
        }
    }

    /**
     * Resets for a new game
     */
    void initialize(ByteBuffer dataSource) {
        this.dataSource = dataSource;
        stepGame = 0;
        stepBot = 0;
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
    boolean empty() {
        lockSize.lock();
        try {
            return framesBuffered() <= 0;
        }  finally {
            lockSize.unlock();
        }
    }

    /**
     * @return Whether the frame buffer is full and can not buffer any additional frames.
     * When the frame buffer is full, JBWAPI must wait for the bot to complete a frame before returning control to StarCraft.
     */
    boolean full() {
        lockSize.lock();
        try {
            return framesBuffered() >= size - 1;
        } finally {
            lockSize.unlock();
        }
    }

    private int indexGame() {
        return stepGame % size;
    }

    private int indexBot() {
        return stepBot % size;
    }

    /**
     * Copy dataBuffer from shared memory into the head of the frame buffer.
     */
    void enqueueFrame() {
        lockWrite.lock();
        try {
            lockSize.lock();
            try { while (full()) conditionSize.awaitUninterruptibly(); } finally { lockSize.unlock(); };
            ByteBuffer dataTarget = dataBuffer.get(indexGame());
            dataSource.rewind();
            dataTarget.rewind();
            dataTarget.put(dataSource);

            lockSize.lock();
            try {
                ++stepGame;
                //System.out.println("FrameBuffer: Enqueued buffer " + indexGame() + " on game step #" + stepGame);
                if (framesBuffered() > 0) {
                    //System.out.println("FrameBuffer: There are now " + framesBuffered() + " frames buffered.");
                }
                conditionSize.signalAll();
            } finally { lockSize.unlock(); }
        } finally { lockWrite.unlock(); }
    }

    /**
     * Peeks the frontmost value in the buffer.
     */
    ByteBuffer peek() {
        lockSize.lock();
        try {
            while(empty()) conditionSize.awaitUninterruptibly();
            //System.out.println("FrameBuffer: Sharing buffer " + indexBot() + " on bot step #" + stepBot);
            return dataBuffer.get(indexBot());
        } finally { lockSize.unlock(); }

    }

    /**
     * Removes the frontmost frame in the buffer.
     */
    void dequeue() {
        lockSize.lock();
        try {
            while(empty()) conditionSize.awaitUninterruptibly();
            //System.out.println("FrameBuffer: Dequeuing buffer " + indexBot() + " on bot step #" + stepBot);
            ++stepBot;
            conditionSize.signalAll();
        } finally { lockSize.unlock(); }
    }
}
