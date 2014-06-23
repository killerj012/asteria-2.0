package server.core.task;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

/**
 * A multi-purpose task that can be used to return a blocking result upon
 * completion.
 * 
 * @author lare96
 * @param <T>
 *        the type of result returned by this task.
 */
public interface TaskFuture<T> extends Callable<T> {

    /**
     * This method defines how the task will be ran. The task can be ran
     * sequentially, concurrently, or however else the user wants it to.
     * 
     * @return the result of the task executed in this context.
     */
    public abstract Future<T> context();

    @Override
    public abstract String toString();
}
