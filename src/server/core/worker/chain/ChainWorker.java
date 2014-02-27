package server.core.worker.chain;

import server.core.worker.Logic;

/**
 * A basic worker contained within a {@link ChainExecutor}.
 * 
 * @author lare96
 * @see ChainExecutor#append(ChainWorker)
 * @see ChainExecutor#appendAll(ChainWorker[])
 * @see ChainExecutor#appendAll(java.util.Collection)
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
