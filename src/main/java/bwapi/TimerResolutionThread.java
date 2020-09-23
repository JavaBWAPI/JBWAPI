package bwapi;

/**
 * See https://hazelcast.com/blog/locksupport-parknanos-under-the-hood-and-the-curious-case-of-parking-part-ii-windows/
 *
 * "Is this it? Just a single flag and my JVM will use high-resolution timers?
 * Well, it turns out it’s not that simple.
 * This flag has been reported to be broken since 2006!
 * However, the very same bug report suggests a very interesting workaround:"
 *
 * “Do not use ForceTimeHighResolution but instead,
 * at the start of the application create and start
 * a daemon Thread that simply sleeps for a very long time
 * (that isn’t a multiple of 10ms)
 * as this will set the timer period to be 1ms for the duration of that sleep
 * – which in this case is the lifetime of the VM"
 *
 * "What the R$#@#@ have I just read?
 * Let me rephrase it:
 *  “If you want a high-resolution timer then just start a new thread and let it sleep forever”.
 *  That’s simply hilarious!
 */
class TimerResolutionThread {
    TimerResolutionThread() {
        Thread t = new Thread(() -> {
            try {
                Thread.sleep(Long.MAX_VALUE);
            } catch (InterruptedException e) { // a delicious interrupt, omm, omm
            }
        });
        t.setDaemon(true);
        t.start();
    }
}
