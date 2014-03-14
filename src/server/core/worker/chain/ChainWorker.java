package server.core.worker.chain;

/**
 * A basic worker contained within a chain for a {@link ChainExecutor}.
 * 
 * @author lare96
 * @see ChainExecutor#append(ChainWorker)
 * @see ChainExecutor#appendAll(ChainWorker[])
 * @see ChainExecutor#appendAll(java.util.Collection)
 */
public interface ChainWorker {

    /** The logic fired when this worker is ran. */
    public void fire();

    /**
     * The delay for this worker that will be activated after the previous
     * worker in the chain is fired.
     * 
     * @return the delay for this task.
     */
    public int delay();
}
