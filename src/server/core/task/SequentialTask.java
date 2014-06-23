package server.core.task;

/**
 * A {@link Task} implementation that will be executed sequentially along with
 * other tasks. Only one thread will be actively executing tasks at a time.
 * 
 * @author lare96
 */
public abstract class SequentialTask extends Task {

    @Override
    public void context() {

        /** Execute the logic serially amongst one thread. */
        TaskExecutors.getSequentialExecutor().getTaskPool().execute(this);
    }

    @Override
    public String toString() {
        return "TASK[context= sequential]";
    }
}
