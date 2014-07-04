package server.core;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import server.core.task.TaskRejectedHook;

/**
 * A thread pool executor that is used to carry out generic asynchronous tasks
 * throughout the server. When these pools are not in use for a certain period
 * of time they will automatically go 'idle' therefore consuming less resources.
 * 
 * @author lare96
 */
public final class GenericTaskPool {

    /** The backing executor that will hold our pool of worker threads. */
    private ThreadPoolExecutor taskPool;

    /**
     * Create a new {@link GenericTaskPool}.
     * 
     * @param poolName
     *            the name of the threads in this pool.
     * @param poolSize
     *            the maximum amount of threads to have in this pool.
     * @param poolPriority
     *            the priority of the threads in this pool.
     */
    @SuppressWarnings("unused")
    public GenericTaskPool(String poolName, int poolSize, int poolPriority) {
        if (Rs2Engine.THREAD_IDLE_TIMEOUT < 1) {
            throw new IllegalStateException(
                    "Idle thread timeout value must be greater than 0!");
        }

        taskPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(poolSize);
        taskPool.setThreadFactory(new ThreadProvider(poolName, poolPriority,
                true, false));
        taskPool.setRejectedExecutionHandler(new TaskRejectedHook());
        taskPool.setKeepAliveTime(Rs2Engine.THREAD_IDLE_TIMEOUT,
                TimeUnit.MINUTES);
        taskPool.allowCoreThreadTimeOut(true);

        if (!Rs2Engine.INITIALLY_IDLE) {
            taskPool.prestartAllCoreThreads();
        }
    }

    /**
     * Gets the backing executor that holds our threads.
     * 
     * @return the backing executor that holds our threads.
     */
    public ThreadPoolExecutor getTaskPool() {
        return taskPool;
    }

    /**
     * Determines if this task pool is still running.
     * 
     * @return true if the pool is still running.
     */
    public boolean isRunning() {
        return !taskPool.isShutdown();
    }
}
