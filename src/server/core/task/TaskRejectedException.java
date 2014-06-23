package server.core.task;

import server.core.GenericTaskPool;


/**
 * An exception thrown when a {@link Task} is rejected by a
 * {@link GenericTaskPool}.
 * 
 * @author lare96
 */
public class TaskRejectedException extends RuntimeException {

    /**
     * Create a new {@link TaskRejectedException}.
     * 
     * @param t
     *        the task that was rejected.
     * @param reason
     *        the reason this service was rejected.
     */
    public TaskRejectedException(Task t, String reason) {
        super(t + " REJECTED: " + reason);
    }

    /** The generated serial version UID. */
    private static final long serialVersionUID = 3292401103671200953L;
}
