package server.core.task;

import server.core.GenericTaskPool;

/**
 * An exception thrown when a {@link Task} is rejected by a
 * {@link GenericTaskPool}.
 * 
 * @author lare96
 */
public class TaskDeniedException extends RuntimeException {

    /**
     * Create a new {@link TaskDeniedException}.
     * 
     * @param t
     *        the task that was rejected.
     * @param reason
     *        the reason this service was rejected.
     */
    public TaskDeniedException(Task t, String reason) {
        super("Task[" + t + "] rejected: " + reason);
    }

    /** The generated serial version UID. */
    private static final long serialVersionUID = 3292401103671200953L;
}
