package server.core.task;

import java.util.concurrent.Future;

/**
 * A {@link TaskFuture} implementation that will be executed concurrently along
 * with other tasks. The amount of threads that will be actively executing tasks
 * is defined by <code>Runtime.getRuntime().availableProcessors()</code>.
 * 
 * @author lare96
 * @param <T>
 *        the type of result returned by this task.
 */
public abstract class ConcurrentFutureTask<T> implements TaskFuture<T> {

    @Override
    public Future<T> context() {

        /** Execute the logic concurrently amongst multiple threads. */
        return TaskExecutors.getConcurrentExecutor().getTaskPool().submit(this);
    }

    @Override
    public String toString() {
        return "FUTURE TASK[context= concurrent]";
    }
}