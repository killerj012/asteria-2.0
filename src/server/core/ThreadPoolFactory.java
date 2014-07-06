package server.core;

import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * A static factory class that manages the creation of all thread pools used to
 * carry out asynchronous work throughout the server.
 * 
 * @author lare96
 */
public final class ThreadPoolFactory {

    /** We cannot create instances of this class. */
    private ThreadPoolFactory() {
    }

    /**
     * Creates a new {@link ThreadPoolExecutor} ready to carry out work. Whether
     * or not the threads in this pool are pre-started or not depends on the
     * value of the <code>START_THREADS</code> boolean in the {@link Rs2Engine}
     * class.
     * 
     * @param poolName
     *            the name of this thread pool.
     * @param poolSize
     *            the size of this thread pool.
     * @param poolPriority
     *            the priority of this thread pool.
     * @return the newly constructed thread pool.
     */
    @SuppressWarnings("unused")
    public static ThreadPoolExecutor createThreadPool(String poolName,
            int poolSize, int poolPriority) {

        if (Rs2Engine.THREAD_IDLE_TIMEOUT < 1) {
            throw new IllegalStateException(
                    "Idle thread timeout value must be greater than 0!");
        }

        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors
                .newFixedThreadPool(poolSize);
        threadPool.setThreadFactory(new ThreadProvider(poolName, poolPriority,
                true, false));
        threadPool.setRejectedExecutionHandler(new ThreadPoolExecutionHook());
        threadPool.setKeepAliveTime(Rs2Engine.THREAD_IDLE_TIMEOUT,
                TimeUnit.MINUTES);
        threadPool.allowCoreThreadTimeOut(true);

        if (Rs2Engine.START_THREADS) {
            threadPool.prestartAllCoreThreads();
        }
        return threadPool;
    }

    /**
     * Handles various asynchronous operations that have been rejected by thread
     * pools.
     * 
     * @author lare96
     */
    private static class ThreadPoolExecutionHook implements
            RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor pool) {
            String reason = "reason unknown!";

            if (pool.getQueue().remainingCapacity() == 0) {
                reason = "No more space in the work queue!";
            } else if (pool.isShutdown()) {
                reason = "The pool is not running!";
            }

            throw new ThreadPoolExecutionException(r, pool, reason);
        }
    }

    /**
     * An exception thrown when an operation is rejected by a thread pool for
     * whatever reason.
     * 
     * @author lare96
     */
    private static class ThreadPoolExecutionException extends RuntimeException {

        /**
         * Create a new {@link ThreadPoolExecutionException}.
         * 
         * @param r
         *            the operation that was rejected.
         * @param reason
         *            the reason this service was rejected.
         */
        public ThreadPoolExecutionException(Runnable r, ThreadPoolExecutor pool, String reason) {
            super("THREADPOOL EXECUTION EXCEPTION[operation= " + r
                    + ", factory= " + pool.getThreadFactory() + ", reason= "
                    + reason + "]");
        }

        /** The generated serial version UID. */
        private static final long serialVersionUID = 3292401103671200953L;
    }
}
