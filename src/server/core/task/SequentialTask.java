package server.core.task;

import server.core.GenericTaskPool;


/**
 * A {@link Task} implementation that will be executed sequentially along with
 * other tasks. Only one thread will be actively executing tasks at a time.
 * 
 * @author lare96
 */
public abstract class SequentialTask implements Task {

    /** An executor that handles short lived sequential game related tasks. */
    private static GenericTaskPool executor = new GenericTaskPool("SequentialThread", 1, Thread.NORM_PRIORITY);

    @Override
    public void context() {

        /** Execute the logic serially amongst one thread. */
        executor.execute(this);
    }

    @Override
    public String toString() {
        return "TASK[context= sequential]";
    }
}
