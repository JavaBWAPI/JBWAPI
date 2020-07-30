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
    private final Lock writeLock = new ReentrantLock();
    private final Lock readLock = writeLock;
    final Lock lockCapacity = new ReentrantLock();
    final Condition conditionFull = lockCapacity.newCondition();
    final Condition conditionEmpty = lockCapacity.newCondition();

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
        lockCapacity.lock();
        try {
            return framesBuffered() <= 0;
        }  finally {
            lockCapacity.unlock();
        }
    }

    /**
     * @return Whether the frame buffer is full and can not buffer any additional frames.
     * When the frame buffer is full, JBWAPI must wait for the bot to complete a frame before returning control to StarCraft.
     */
    boolean full() {
        lockCapacity.lock();
        try {
            return framesBuffered() >= size - 1;
        } finally {
            lockCapacity.unlock();
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
        // In practice we don't particularly expect multiple threads to write to this, but support it just to be safe.
        writeLock.lock();
        try {
            // Wait for the buffer to have space to enqueue
            lockCapacity.lock();
            try {
                while (full()) {
                    conditionFull.awaitUninterruptibly();
                }
             } finally {
                lockCapacity.unlock();
            }

            System.out.println("FrameBuffer: Enqueuing buffer " + indexGame() + " on game step #" + stepGame + " with " + framesBuffered() + " frames buffered.");
            ByteBuffer dataTarget = dataBuffer.get(indexGame());
            dataSource.rewind();
            dataTarget.rewind();
            dataTarget.put(dataSource);
            ++stepGame;

            // Notify anyone waiting for something to dequeue
            lockCapacity.lock();
            try {
                conditionEmpty.signalAll();
            } finally {
                lockCapacity.unlock();
            }
        } finally {
            writeLock.unlock();
        }
    }

    /**
     * Points the bot to the next frame in the buffer.
     */
    ByteBuffer dequeueFrame() {
        // In practice we don't particularly expect multiple threads to read from this, but support it just to be safe.
        readLock.lock();
        try {
            // Wait for the buffer to have something to dequeue
            lockCapacity.lock();
            try {
                while (empty()) {
                    conditionEmpty.awaitUninterruptibly();
                }
            } finally {
                lockCapacity.unlock();
            }

            System.out.println("FrameBuffer: Dequeuing buffer " + indexBot() + " on bot step #" + stepBot);
            ByteBuffer output = dataBuffer.get(indexBot());
            ++stepBot;

            // Notify anyone waiting for capacity to enqueue
            lockCapacity.lock();
            try {
                conditionFull.signalAll();
                return output;
            } finally {
                lockCapacity.unlock();
            }
        } finally {
          readLock.unlock();
        }
    }
}
