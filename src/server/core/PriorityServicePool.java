package server.core;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * An {@link Executor} implementation that is used to carry out prioritized
 * services throughout the application. When these pools are not in use for a
 * certain period of time they will automatically go 'idle' therefore consuming
 * less resources.
 * 
 * @author lare96
 */
public final class PriorityServicePool implements Executor {

    /** The backing executor that will hold our pool of service threads. */
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
     * Handles services that have been rejected by the underlying priority pool.
     * 
     * @author lare96
     */
    private static class RejectedPriorityService implements RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            if (r instanceof Service) {
                String reason = "unknown!";

                if (executor.getQueue().remainingCapacity() == 0) {
                    reason = "no more space in the work queue!";
                } else if (executor.isShutdown()) {
                    reason = "the pool is not running!";
                }

                throw new ServiceDeniedException((Service) r, reason);
            }
        }
    }
}
