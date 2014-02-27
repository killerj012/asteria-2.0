package server.core.factory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A thread factory that will be used to prepare <i>worker</i> threads that
 * carry out parallel tasks on the main game thread.
 * 
 * @author lare96
 */
public class WorkerThreadFactory implements ThreadFactory {

    /** The amount of worker threads prepared by this factory. */
    private AtomicInteger threadCount = new AtomicInteger();

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.setName("WorkerThread-" + threadCount.incrementAndGet());
        thread.setDaemon(true);
        return thread;
    }
}
