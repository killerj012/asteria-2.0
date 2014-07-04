package server.core.task;

import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

/**
 * A {@link FutureTask} implementation that will be executed sequentially along
 * with other tasks. Only one thread will be actively executing tasks at a time.
 * 
 * @author lare96
 * @param <T>
 *            the type of result returned by this task.
 */
public abstract class SequentialFutureTask<T> implements TaskFuture<T> {

    @Override
    public Future<T> context() {
        return TaskExecutors.getSequentialExecutor().getTaskPool().submit(this);
    }

    @Override
    public String toString() {
        return "FUTURE TASK[context= sequential]";
    }
}
