package server.world.entity;

/**
 * A graphic that can be performed by an entity.
 * 
 * @author lare96
 */
public class Gfx {

    /** The id of the graphic. */
    private int id;

    /** The delay before the graphic executes in ticks. */
    private int delay;

    /**
     * Create a new {@link Gfx}.
     * 
     * @param id
     *            the id of the graphic.
     * @param delay
     *            the delay for the graphic.
     */
    public Gfx(int id, int delay) {
        this.id = id;
        this.delay = delay;
    }

    /**
     * Create a new {@link Gfx}.
     * 
     * @param id
     *            the id of the graphic.
     */
    public Gfx(int id) {
        this(id, 0);
    }

    /**
     * Create a new {@link Gfx}.
     */
    public Gfx() {

    }

    /**
     * Gets the id of the graphic.
     * 
     * @return the id.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the delay for this graphic.
     * 
     * @return the delay.
     */
    public int getDelay() {
        return delay;
    }

    /**
     * Sets the values for this graphic as values form another graphic.
     * 
     * @param other
     *            the other graphic to set the values from.
     * @return this graphic.
     */
    public Gfx setAs(Gfx other) {
        this.id = other.id;
        this.delay = other.delay;
        return this;
    }
}
