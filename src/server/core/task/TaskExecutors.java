package server.core.task;

import server.core.GenericTaskPool;

/**
 * A static factory class that contains the executors that will be used by each
 * task implementation.
 * 
 * @author lare96
 */
public final class TaskExecutors {

    /** An executor that handles short lived concurrent game related tasks. */
    private static final GenericTaskPool concurrent = new GenericTaskPool("ConcurrentThread", Runtime.getRuntime().availableProcessors(), Thread.MAX_PRIORITY);

    /** An executor that handles short lived sequential game related tasks. */
    private static final GenericTaskPool sequential = new GenericTaskPool("SequentialThread", 1, Thread.NORM_PRIORITY);

    /** To prevent instantiation. */
    private TaskExecutors() {
    }

    /**
     * Gets the executor that handles concurrent tasks.
     * 
     * @return the executor that handles concurrent tasks.
     */
    protected static final GenericTaskPool getConcurrentExecutor() {
        return concurrent;
    }

    /**
     * Gets the executor that handles sequential tasks.
     * 
     * @return the executor that handles sequential tasks.
     */
    protected static final GenericTaskPool getSequentialExecutor() {
        return sequential;
    }
}
