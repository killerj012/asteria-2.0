package com.asteria.engine;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * A class that contains functions to manage the creation and configuration of
 * all {@link ThreadPoolExecutor}s used to execute code throughout the server.
 * 
 * @author lare96
 */
public final class ThreadPoolFactory {

    /**
     * Creates a new {@link ThreadPoolExecutor} with the argued settings. All
     * pools created through this method have their core threads started and
     * will terminate after being idle for the argued timeout value.
     * 
     * @param name
     *            the name of the threads in this thread pool.
     * @param size
     *            the maximum amount of threads that will be allocated in this
     *            thread pool.
     * @param priority
     *            the priority of threads in this thread pool.
     * @param timeout
     *            how long in minutes it takes for an idle thread in this thread
     *            pool to be deallocated.
     * @return the new thread pool with the argued settings.
     */
    public static ThreadPoolExecutor createThreadPool(String name, int size,
        int priority, long timeout) {
        ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors
            .newFixedThreadPool(size);
        threadPool.setThreadFactory(new ThreadProvider(name, priority, true));
        threadPool
            .setRejectedExecutionHandler(new IndicationCallerRunsPolicy());
        threadPool.setKeepAliveTime(timeout, TimeUnit.MINUTES);
        threadPool.allowCoreThreadTimeOut(true);
        threadPool.prestartAllCoreThreads();
        return threadPool;
    }

    /**
     * A handler for rejected tasks that runs the rejected task directly in the
     * calling thread of the <code>execute</code> method, unless the executor
     * has been shut down, in which case the task is discarded. The difference
     * between this handler and {@link CallerRunsPolicy} is that this handler
     * will print off an indication of what happened.
     * 
     * @author lare96
     */
    private static class IndicationCallerRunsPolicy extends CallerRunsPolicy {

        /** The logger for printing information. */
        private static Logger logger = Logger
            .getLogger(IndicationCallerRunsPolicy.class.getSimpleName());

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            super.rejectedExecution(r, e);
            logger
                .warning(e.isShutdown() ? "Task discared by thread pool: " + e
                    .toString()
                    : "Task executed on calling thread by thread pool: " + e
                        .toString());
        }
    }

    /**
     * A thread pool that will execute a queue of tasks and block until all of
     * them have completed. The tasks are first appended to the internal queue
     * using the {@link #append} method and then ran using the
     * {@link #fireAndAwait} method. Once the pool is ran it will be shutdown
     * and can no longer be used. This class is <b>NOT</b> intended for use
     * across multiple threads.
     * 
     * @author lare96
     */
    public static final class BlockingThreadPool {

        /** The backing thread pool that will execute the pending tasks. */
        private final ThreadPoolExecutor executor;

        /** The phaser that blocks the calling thread until the tasks finish. */
        private final Phaser phaser;

        /** A queue that will hold all of the pending tasks. */
        private final Queue<Runnable> pendingTasks = new LinkedList<>();

        /**
         * Create a new {@link BlockingThreadPool} with the argued size.
         * 
         * @param size
         *            the maximum amount of threads that will be allocated in
         *            this thread pool.
         */
        public BlockingThreadPool(int size) {
            this.executor = ThreadPoolFactory.createThreadPool(
                "Blocking-Thread", size, Thread.NORM_PRIORITY, Long.MAX_VALUE);
            this.executor.allowCoreThreadTimeOut(false);
            this.phaser = new Phaser(1);
        }

        /**
         * Create a new {@link BlockingThreadPool} with the size equal to how
         * many processors are available to the JVM.
         */
        public BlockingThreadPool() {
            this(Runtime.getRuntime().availableProcessors());
        }

        /**
         * Appends the argued task to the queue of pending tasks. When this pool
         * is ran using <code>fireAndAwait()</code> all of the pending tasks
         * will be executed in <i>FIFO</> order.
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
         * Submit all of the pending tasks to the backing thread pool and wait
         * for them to complete. Once all of the tasks have completed the
         * backing thread pool will shutdown.
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
