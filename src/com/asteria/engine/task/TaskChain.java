package com.asteria.engine.task;

/**
 * A basic task contained within a chain for a {@link TaskChainExecutor}.
 * 
 * @author lare96
 * @see TaskChainExecutor#append(ChainTask)
 * @see TaskChainExecutor#appendAll(ChainTask[])
 * @see TaskChainExecutor#appendAll(java.util.Collection)
 */
public interface TaskChain {

    /** The logic ran when this task is fired. */
    public void fire();

    /**
     * The delay for this task that will come into effect once the previous task
     * in the chain is fired.
     * 
     * @return the delay for this task.
     */
    public int delay();
}
