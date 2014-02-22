package server.core.worker.factory;

import server.core.worker.Logic;

/**
 * A basic worker contained within a {@link FactoryExecutor}.
 * 
 * @author lare96
 * @see FactoryExecutor#append(ChainWorker)
 * @see FactoryExecutor#appendAll(ChainWorker[])
 * @see FactoryExecutor#appendAll(java.util.Collection)
 */
public interface ChainWorker extends Logic {

    /**
     * The delay for this worker that will be activated after the previous
     * worker in the chain is fired.
     * 
     * @return the delay for this task.
     */
    public int delay();
}
