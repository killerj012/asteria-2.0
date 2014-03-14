package server.core;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A dynamic {@link ThreadFactory} used for the creation of all threads used
 * throughout the engine.
 * 
 * @author lare96
 */
public class ThreadProvider implements ThreadFactory {

    /**
     * A thread safe integer that will be used to track the amount of threads
     * created by this provider.
     */
    private volatile AtomicInteger threadCount;

    /** The name of the prepared thread in the group. */
    private volatile String threadName;

    /** The priority of the prepared thread. */
    private volatile int threadPriority;

    /** If this thread is a daemon thread. */
    private volatile boolean daemonThread;

    /** If this provider should keep track of the amount of threads it created. */
    private volatile boolean keepThreadCount;

    /**
     * Create a new {@link ThreadProvider}.
     * 
     * @param threadName
     *        the name of the prepared thread.
     * @param threadPriority
     *        the priority of the prepared thread.
     * @param daemonThread
     *        if this thread is a daemon thread.
     * @param keepThreadCount
     *        if this provider should keep track of the amount of threads it
     *        created.
     */
    public ThreadProvider(String threadName, int threadPriority, boolean daemonThread, boolean keepThreadCount) {
        this.threadCount = keepThreadCount ? new AtomicInteger() : null;
        this.threadName = threadName;
        this.threadPriority = threadPriority;
        this.daemonThread = daemonThread;
        this.keepThreadCount = keepThreadCount;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName(keepThreadCount ? threadName + "-" + threadCount.incrementAndGet() : threadName);
        thread.setPriority(threadPriority);
        thread.setDaemon(daemonThread);
        return thread;
    }
}
