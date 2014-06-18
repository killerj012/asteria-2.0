package server.core.task;

import server.core.GenericTaskPool;


/**
 * A generic task that can be carried out by a {@link GenericTaskPool}.
 * 
 * @author lare96
 */
public interface Task extends Runnable {

    /**
     * The name of the task being carried out primarily used for debugging
     * purposes. If the task does not have a name a {@link Runnable} should be
     * used instead.
     * 
     * @return the name of the task being carried out.
     */
    public String name();
}
