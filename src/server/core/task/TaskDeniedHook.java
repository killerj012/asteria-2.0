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

        if (r instanceof Task) {
            if (executor.getQueue().remainingCapacity() == 0) {
                reason = "No more space in the work queue!";
            } else if (executor.isShutdown()) {
                reason = "The pool is not running!";
            }

            throw new TaskDeniedException((Task) r, reason);
        }
    }
}