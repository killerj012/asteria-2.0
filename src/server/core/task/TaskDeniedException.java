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
     * @param service
     *        the service throwing this exception.
     * @param reason
     *        the reason this service was rejected.
     */
    public TaskDeniedException(String taskName, String reason) {
        super("Task[" + taskName + "] rejected: " + reason);
    }

    /** The generated serial version UID. */
    private static final long serialVersionUID = 3292401103671200953L;
}
