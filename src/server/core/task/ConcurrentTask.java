package server.core.task;

import server.core.GenericTaskPool;

/**
 * A {@link Task} implementation that will be executed concurrently along with
 * other tasks. The amount of threads that will be actively executing tasks is
 * defined by <code>Runtime.getRuntime().availableProcessors()</code>.
 * 
 * @author lare96
 */
public abstract class ConcurrentTask implements Task {

    /** An executor that handles short lived concurrent game related tasks. */
    private static GenericTaskPool executor = new GenericTaskPool("ConcurrentThread", Runtime.getRuntime().availableProcessors(), Thread.MAX_PRIORITY);

    @Override
    public void context() {

        /** Execute the logic concurrently amongst multiple threads. */
        executor.execute(this);
    }

    @Override
    public String toString() {
        return "TASK[context= concurrent]";
    }
}
