package com.asteria.engine;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
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

    /**
     * Creates a new {@link ThreadPoolExecutor} ready to carry out work. All
     * pools are pre-started by default and will terminate after not receiving
     * work for the argued timeout value.
     * 
     * @param poolName
     *            the name of this thread pool.
     * @param poolSize
     *            the size of this thread pool.
     * @param poolPriority
     *            the priority of this thread pool.
     * @param timeout
     *            how long in minutes it takes for threads in this pool to
     *            timeout.
     * @return the newly constructed thread pool.
     */
    public static ThreadPoolExecutor createThreadPool(String poolName,
            int poolSize, int poolPriority, long timeout) {
        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors
                .newFixedThreadPool(poolSize);
        threadPool.setThreadFactory(new ThreadProvider(poolName, poolPriority,
                true));
        threadPool
                .setRejectedExecutionHandler(new ThreadPoolRejectedExecutionHook());
        threadPool.setKeepAliveTime(timeout, TimeUnit.MINUTES);
        threadPool.allowCoreThreadTimeOut(true);
        threadPool.prestartAllCoreThreads();
        return threadPool;
    }

    /**
     * Tasks that have been rejected by thread pools created using the
     * <code>createThreadPool</code> method are redirected to this execution
     * hook.
     * 
     * @author lare96
     */
    private static class ThreadPoolRejectedExecutionHook implements
            RejectedExecutionHandler {

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor pool) {
            throw new ThreadPoolRejectedExecutionException(
                    r,
                    pool,
                    pool.getQueue().remainingCapacity() == 0 ? "No more space in the work queue!"
                            : pool.isShutdown() ? "The pool is not running!"
                                    : "reason unknown!");
        }
    }

    /**
     * An exception thrown when a task is rejected by a thread pool for whatever
     * reason.
     * 
     * @author lare96
     */
    private static class ThreadPoolRejectedExecutionException extends
            RuntimeException {

        /**
         * Create a new {@link ThreadPoolRejectedExecutionException}.
         * 
         * @param r
         *            the task that was rejected.
         * @param reason
         *            the reason this task was rejected.
         */
        public ThreadPoolRejectedExecutionException(Runnable r,
                ThreadPoolExecutor pool, String reason) {
            super("REJECTED EXECUTION[runnable= " + r + ", factory= " + pool
                    .getThreadFactory() + ", reason= " + reason + "]");
        }

        /** The generated serial version UID. */
        private static final long serialVersionUID = 3292401103671200953L;
    }

    /**
     * A thread pool that will concurrently execute a set of {@link Runnable}s
     * in the order they were appended.
     * 
     * @author lare96
     */
    public static final class BlockingThreadPool {

        /** The backing executor that will execute pending tasks in parallel. */
        private final ExecutorService executor = Executors
                .newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        /** The phaser that blocks the calling thread until the tasks finish. */
        private final Phaser phaser = new Phaser(1);

        /**
         * A queue that will hold all of the pending tasks. Only the calling
         * thread will be accessing this queue so it does not need to be a
         * thread safe implementation.
         */
        private final Queue<Runnable> pendingTasks = new LinkedList<>();

        /**
         * Appends the argued {@link Runnable} to the queue of pending tasks.
         * When this pool is ran using <code>fireAndAwait()</code> all of the
         * pending tasks will be submitted in <i>FIFO</> order.
         * 
         * @param r
         *            the task to add to the queue of pending tasks.
         */
        public void append(final Runnable r) {

            // Register a new party for the phaser.
            phaser.register();

            // Wrap the argued task in this new task, so we can arrive at
            // the phaser and handle any errors.
            pendingTasks.add(new Runnable() {
                @Override
                public void run() {
                    try {
                        r.run();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        phaser.arrive();
                    }
                }
            });
        }

        /**
         * Submit all of the pending tasks to the backing executor and wait for
         * them to complete. Once all of the tasks have completed the backing
         * executor will shutdown.
         */
        public void fireAndAwait() {

            // Submit all pending tasks to the executor.
            Runnable r;
            while ((r = pendingTasks.poll()) != null) {
                executor.execute(r);
            }

            // Wait for the submitted tasks to complete.
            phaser.arriveAndAwaitAdvance();

            // Then shutdown the executor.
            executor.shutdownNow();
        }
    }

    private ThreadPoolFactory() {}
}
