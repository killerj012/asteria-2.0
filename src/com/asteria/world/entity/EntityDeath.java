package com.asteria.world.entity;

import com.asteria.engine.task.Task;

/**
 * A class that provides implementation for handling the death of an
 * {@link Entity}.
 * 
 * @author lare96
 * @param <T>
 *            the type of entity we are handling death for.
 */
public abstract class EntityDeath<T extends Entity> extends Task {

    /** The entity we are handling death for. */
    private T entity;

    /** The amount of ticks that have elapsed. */
    private int ticks;

    /**
     * Create a new {@link EntityDeath}.
     * 
     * @param entity
     *            the entity we are handling death for.
     */
    public EntityDeath(T entity) {
        super(1, true);
        this.entity = entity;
    }

    /**
     * Fired when the entity has just died.
     * 
     * @param entity
     *            the entity we are handling death for.
     */
    public abstract void preDeath(T entity);

    /**
     * Fired when the killer needs to be found and the items need to be dropped.
     * 
     * @param entity
     *            the entity we are handling death for.
     */
    public abstract void death(T entity);

    /**
     * Fired when the entity needs to be reset or respawned.
     * 
     * @param entity
     *            the entity we are handling death for.
     */
    public abstract void postDeath(T entity);

    @Override
    public void execute() {
        switch (ticks) {
        
        // Npc has just died so flag them, reset poison, and reset movement.
        case 0:
            entity.setDead(true);
            entity.setPoisonDamage(0);
            entity.getMovementQueue().reset();
            break;

        // Fire pre-death event.
        case 1:
            preDeath(entity);
            break;

        // Fire death event.
        case 5:
            death(entity);
            break;

        // Fire post-death event, unflag them, and cancel the task.
        case 6:
            postDeath(entity);
            entity.setDead(false);
            this.cancel();
            break;
        }

        // Increment the amount of ticks.
        ticks++;
    }
}
