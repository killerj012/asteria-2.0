package server.core.task;

import server.core.GenericTaskPool;

/**
 * A multi-purpose task that can be carried out by a {@link GenericTaskPool}.
 * 
 * @author lare96
 */
public abstract class Task implements Runnable {

    /**
     * This method defines how the task will be ran. The task can be ran
     * sequentially, concurrently, or however else the user wants it to.
     */
    public abstract void context();

    @Override
    public abstract String toString();
}
