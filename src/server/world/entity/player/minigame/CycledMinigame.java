package server.world.entity.player.minigame;

import server.core.worker.Worker;

/**
 * A procedural {@link Minigame} that depends on a {@link Worker} with a fixed
 * delay to fire {@link Logic} in a 'cycled' manner in order to function.
 * 
 * @author lare96
 */
public abstract class CycledMinigame extends Minigame {

    /**
     * A {@link Worker} that will be submitted on startup that will keep this
     * minigame running.
     * 
     * @return the worker that will fire {@link Logic} for this minigame to keep
     *         it functioning.
     */
    public abstract Worker cycleWorker();
}
