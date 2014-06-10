package server.core.task;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Handles tasks that have been rejected by the underlying task pool.
 * 
 * @author lare96
 */
public class TaskDeniedHook implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        String reason = "reason unknown!";
        String name = "unknown";

        if (r instanceof Task) {
            name = ((Task) r).name();

            if (executor.getQueue().remainingCapacity() == 0) {
                reason = "no more space in the work queue!";
            } else if (executor.isShutdown()) {
                reason = "the pool is not running!";
            }
        }

        // XXX: Redirect tasks to the update pool?
        throw new TaskDeniedException(name, reason);
    }
}