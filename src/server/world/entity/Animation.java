package server.world.entity;

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
     *        the id of the animation.
     * @param delay
     *        the delay of the animation.
     */
    public Animation(int id, int delay) {
        this.id = id;
        this.setDelay(delay);
    }

    /**
     * Create a new animation with a delay of 0.
     * 
     * @param id
     *        the id of the animation.
     */
    public Animation(int id) {
        this.id = id;
    }

    /**
     * Create a new animation with the default values.
     */
    public Animation() {

        /**
         * Nothing in here so the default values remain unchanged.
         */
    }

    /**
     * Gets the id of this animation.
     * 
     * @return the id.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the delay of this animation.
     * 
     * @return the delay.
     */
    public int getDelay() {
        return delay;
    }

    /**
     * Sets the delay of this animation.
     * 
     * @param delay
     *        the new delay to set.
     */
    private void setDelay(int delay) {
        if (delay < 0) {
            throw new IllegalArgumentException("Cannot have a delay below 0!");
        }

        this.delay = delay;
    }

    /**
     * Sets the values for this animation as the values from another animation.
     * 
     * @param other
     *        the other animation.
     * @return this animation.
     */
    public Animation setAs(Animation other) {
        this.id = other.id;
        this.delay = other.delay;
        return this;
    }
}
