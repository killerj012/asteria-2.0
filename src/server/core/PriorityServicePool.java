package server.core;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * A {@link Executor} implementation that is meant to carry out prioritized
 * services throughout the application.
 * 
 * @author lare96
 */
public final class PriorityServicePool implements Executor {

    /** The {@link ThreadPoolExecutor} that will hold our service threads. */
    private ThreadPoolExecutor servicePool;

    /**
     * Create a new {@link PriorityServicePool}.
     * 
     * @param poolName
     *        the name of the threads in this pool.
     * @param poolSize
     *        the maximum amount of threads to have in this pool.
     * @param poolPriority
     *        the priority of this pool.
     */
    public PriorityServicePool(String poolName, int poolSize, int poolPriority) {
        servicePool = (ThreadPoolExecutor) Executors.newFixedThreadPool(poolSize);
        servicePool.setThreadFactory(new ThreadProvider(poolName, poolPriority, true, false));
        servicePool.setRejectedExecutionHandler(new RejectedPriorityService());
        servicePool.setKeepAliveTime(Rs2Engine.THREAD_IDLE_TIMEOUT, TimeUnit.MINUTES);
        servicePool.allowCoreThreadTimeOut(true);

        if (!Rs2Engine.INITIALLY_IDLE) {
            servicePool.prestartAllCoreThreads();
        }
    }

    @Override
    public void execute(Runnable command) {
        servicePool.execute(command);
    }

    /**
     * Determines if this service pool is still running.
     * 
     * @return true if the pool is still running.
     */
    public boolean isRunning() {
        return !servicePool.isShutdown();
    }

    /**
     * A {@link RejectedExecutionHandler} implementation that will handle
     * services that have been rejected by the {@link PriorityServicePool}.
     * 
     * @author lare96
     */
    private static class RejectedPriorityService implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

            // XXX: Maybe redirect the task elsewhere?
            if (!(r instanceof Service)) {
                throw new RuntimeException("[REJECTED TASK]: Unknown task!");
            }

            throw new RuntimeException("[REJECTED TASK]: " + ((Service) r).name());
        }
    }
}
