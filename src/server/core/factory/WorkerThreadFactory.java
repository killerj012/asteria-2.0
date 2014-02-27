package server.core.factory;

import java.util.concurrent.ThreadFactory;

/**
 * A thread factory that will be used to prepare <i>worker</i> threads that
 * carry out parallel tasks on the main game thread.
 * 
 * @author lare96
 */
public class WorkerThreadFactory implements ThreadFactory {

    /** The amount of worker threads prepared by this factory. */
    private int threadCount = 1;

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.setName("WorkerThread-" + threadCount);
        thread.setDaemon(true);
        threadCount++;
        return thread;
    }
}
