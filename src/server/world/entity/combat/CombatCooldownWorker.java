package server.world.entity.combat;

import server.core.worker.Worker;
import server.world.entity.Entity;

/**
 * A task that performs a cooldown operation for a combat session.
 * 
 * @author lare96
 */
public class CombatCooldownWorker extends Worker {

    /** The entity we are performing the cooldown task on. */
    private Entity entity;

    /** The cooldown countdown. */
    private int cooldown = 7;

    /**
     * Create a new {@link CombatCooldownWorker}.
     * 
     * @param entity
     *        the entity we are performing the cooldown task on.
     */
    public CombatCooldownWorker(Entity entity) {
        super(1, false);
        this.entity = entity;
    }

    @Override
    public void fire() {

        /** If this entity resumes combat while we are cooling down then cancel. */
        if (entity.isUnregistered() || entity.getCombatSession().isAttacking() || entity.getCombatSession().isBeingAttacked()) {
            this.cancel();
            return;
        }

        /** Decrement the countdown. */
        cooldown--;

        /**
         * If we have finished cooling down and now we can completely reset
         * combat for this entity.
         */
        if (cooldown == 0) {
            entity.getCombatSession().resetCombatAll();
            this.cancel();
            return;
        }
    }
}
