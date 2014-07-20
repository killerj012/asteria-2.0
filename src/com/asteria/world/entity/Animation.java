package com.asteria.world.entity;

/**
 * An animation that can be performed by an entity.
 * 
 * @author lare96
 */
public class Animation {

    /** The id of the animation. */
    private int id;

    /** The delay before the animation executes in ticks. */
    private int delay;

    /**
     * Create a new {@link Animation}.
     * 
     * @param id
     *            the id of the animation.
     * @param delay
     *            the delay of the animation.
     */
    public Animation(int id, int delay) {
        this.id = id;
        this.delay = delay;
    }

    /**
     * Create a new {@link Animation}.
     * 
     * @param id
     *            the id of the animation.
     */
    public Animation(int id) {
        this(id, 0);
    }

    /**
     * Create a new {@link Animation}.
     */
    public Animation() {

    }

    @Override
    public Animation clone() {
        return new Animation(id, delay);
    }

    /**
     * Gets the id of the animation.
     * 
     * @return the id of the animation.
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id of the animation.
     * 
     * @param id
     *            the id of the animation.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the delay of the animation.
     * 
     * @return the delay of the animation.
     */
    public int getDelay() {
        return delay;
    }

    /**
     * Sets the delay of the animation.
     * 
     * @param delay
     *            the delay of the animation.
     */
    public void setDelay(int delay) {
        this.delay = delay;
    }
}
