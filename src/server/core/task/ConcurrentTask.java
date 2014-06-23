package server.core.task;

/**
 * A {@link Task} implementation that will be executed concurrently along with
 * other tasks. The amount of threads that will be actively executing tasks is
 * defined by <code>Runtime.getRuntime().availableProcessors()</code>.
 * 
 * @author lare96
 */
public abstract class ConcurrentTask extends Task {

    @Override
    public void context() {

        /** Execute the logic concurrently amongst multiple threads. */
        TaskExecutors.getConcurrentExecutor().getTaskPool().execute(this);
    }

    @Override
    public String toString() {
        return "TASK[context= concurrent]";
    }
}
