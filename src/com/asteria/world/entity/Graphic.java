package com.asteria.world.entity;

/**
 * A graphic that can be performed by an entity.
 * 
 * @author lare96
 */
public class Graphic {

    /** The id of the graphic. */
    private int id;

    /** The height of this graphic. */
    private int height;

    /**
     * Create a new {@link Graphic}.
     * 
     * @param id
     *            the id of the graphic.
     * @param height
     *            the height of this graphic
     */
    public Graphic(int id, int height) {
        this.id = id;
        this.height = height;
    }

    /**
     * Create a new {@link Graphic}.
     * 
     * @param id
     *            the id of the graphic.
     */
    public Graphic(int id) {
        this(id, 0);
    }

    /**
     * Create a new {@link Graphic}.
     */
    public Graphic() {

    }

    @Override
    public Graphic clone() {
        return new Graphic(id, height);
    }

    /**
     * Gets the id of the graphic.
     * 
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id of the graphic.
     * 
     * @param id
     *            the id of the graphic.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the height of this graphic
     * 
     * @return the height of this graphic
     */
    public int getHeight() {
        return height;
    }

    /**
     * Sets the height of this graphic
     * 
     * @param height
     *            the height of this graphic
     */
    public void setHeight(int height) {
        this.height = height;
    }

}
