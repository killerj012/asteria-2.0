package com.asteria.world.map;

import com.asteria.util.Utility;

/**
 * A position point on the map.
 * 
 * @author blakeman8192
 * @author lare96
 */
public class Position {

    /** The x coordinate. */
    private int x;

    /** The y coordinate. */
    private int y;

    /** The z coordinate. */
    private int z;

    /**
     * Create a new {@link Position}.
     * 
     * @param x
     *            the x coordinate.
     * @param y
     *            the y coordinate.
     * @param the
     *            z coordinate.
     */
    public Position(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Create a new {@link Position} with a <code>z</code> coordinate of 0.
     * 
     * @param x
     *            the x coordinate.
     * @param y
     *            the y coordinate.
     */
    public Position(int x, int y) {
        this(x, y, 0);
    }

    /** Creates a new position with default values for the coordinates. */
    public Position() {}

    /**
     * Checks if this position is in the argued {@link Location}.
     * 
     * @param other
     *            the location to check for.
     * @param inclusive
     *            if it should check inclusively.
     * @return <code>true</code> if this position was in the specified location,
     *         <code>false</code> otherwise.
     */
    public boolean inLocation(Location other, boolean inclusive) {
        return !inclusive ? this.x > other.getSouthWestX() && this.x < other
                .getNorthEastX() && this.y > other.getSouthWestY() && this.y < other
                .getNorthEastY()
                : this.x >= other.getSouthWestX() && this.x <= other
                        .getNorthEastX() && this.y >= other.getSouthWestY() && this.y <= other
                        .getNorthEastY();
    }

    /**
     * Moves this position by the argued amounts.
     * 
     * @param amountX
     *            the amount to move the <code>x</code> coordinate.
     * @param amountY
     *            the amount to move the <code>y</code> coordinate.
     * @param amountZ
     *            the amount to move the <code>z</code> coordinate.
     * @return this position with the new coordinates.
     */
    public Position move(int amountX, int amountY, int amountZ) {
        this.x += amountX;
        this.y += amountY;
        this.z += amountZ;
        return this;
    }

    /**
     * Moves this position by the argued amounts.
     * 
     * @param amountX
     *            the amount to move the <code>x</code> coordinate.
     * @param amountY
     *            the amount to move the <code>y</code> coordinate.
     * @return this position with the new coordinates.
     */
    public Position move(int amountX, int amountY) {
        return move(amountX, amountY, 0);
    }

    /**
     * Moves this position <code>N</code>, <code>NW</code>, <code>NE</code>,
     * <code>S</code>, <code>SW</code>, <code>SE</code>, <code>W</code>, or
     * <code>E</code> between 0 and the argued amount inclusive <b>at
     * random</b>.
     * 
     * @param amount
     *            the amount to move this position by at random.
     * @return this position with the new coordinates.
     */
    public Position move(int amount) {
        int x = Utility.inclusiveRandom(amount);
        int y = Utility.inclusiveRandom(amount);

        switch (Utility.inclusiveRandom(3)) {
        case 0:
            return move(x, y);
        case 1:
            return move(-x, -y);
        case 2:
            return move(-x, y);
        case 3:
            return move(x, -y);
        default:
            throw new IllegalStateException();
        }
    }

    @Override
    public String toString() {
        return "POSITION[x= " + x + ", y= " + y + ", z= " + z + "]";
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Position) {
            Position p = (Position) other;
            return x == p.x && y == p.y && z == p.z;
        }
        return false;
    }

    @Override
    public Position clone() {
        return new Position(x, y, z);
    }

    /**
     * Sets this position as the other position. <b>Please use this method
     * instead of player.setPosition(other)</b> because of reference conflicts
     * (if the other position gets modified, so will the players).
     * Alternatively, if you know what you're doing you can use
     * <code>clone()</code> to accomplish to same thing.
     * 
     * @param other
     *            the other position
     */
    public void setAs(Position other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    /**
     * Sets the <code>x</code> coordinate.
     * 
     * @param x
     *            the <code>x</code> coordinate.
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Gets the <code>x</code> coordinate.
     * 
     * @return the <code>x</code> coordinate.
     */
    public int getX() {
        return x;
    }

    /**
     * Sets the <code>y</code> coordinate.
     * 
     * @param y
     *            the <code>y</code> coordinate.
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Gets the <code>y</code> coordinate.
     * 
     * @return the <code>y</code> coordinate.
     */
    public int getY() {
        return y;
    }

    /**
     * Sets the <code>z</code> coordinate.
     * 
     * @param z
     *            the <code>z</code> coordinate.
     */
    public void setZ(int z) {
        this.z = z;
    }

    /**
     * Gets the <code>z</code> coordinate.
     * 
     * @return the <code>z</code> coordinate.
     */
    public int getZ() {
        return z;
    }

    /**
     * Gets the X coordinate of the region containing this Position.
     * 
     * @return the region X coordinate.
     */
    public int getRegionX() {
        return (x >> 3) - 6;
    }

    /**
     * Gets the Y coordinate of the region containing this Position.
     * 
     * @return the region Y coordinate.
     */
    public int getRegionY() {
        return (y >> 3) - 6;
    }

    /**
     * Gets the local X coordinate relative to the base Position.
     * 
     * @param base
     *            the base Position.
     * @return the local X coordinate.
     */
    public int getLocalX(Position base) {
        return x - 8 * base.getRegionX();
    }

    /**
     * Gets the local Y coordinate relative to the base Position.
     * 
     * @param base
     *            the base Position.
     * @return the local Y coordinate.
     */
    public int getLocalY(Position base) {
        return y - 8 * base.getRegionY();
    }

    /**
     * Gets the local X coordinate relative to this Position.
     * 
     * @return the local X coordinate.
     */
    public int getLocalX() {
        return getLocalX(this);
    }

    /**
     * Gets the local Y coordinate relative to this Position.
     * 
     * @return the local Y coordinate.
     */
    public int getLocalY() {
        return getLocalY(this);
    }

    /**
     * Gets the X map region chunk relative to this position.
     * 
     * @return the X region chunk.
     */
    public int getChunkX() {
        return (x >> 6);
    }

    /**
     * Gets the Y map region chunk relative to this position.
     * 
     * @return the Y region chunk.
     */
    public int getChunkY() {
        return (y >> 6);
    }

    /**
     * Gets the region relative to this position.
     * 
     * @return the region relative to this position.
     */
    public int getRegion() {
        return ((getChunkX() << 8) + getChunkY());
    }

    /**
     * Checks if this position is viewable from the other position.
     * 
     * @param other
     *            the other position.
     * @return true if it is viewable, false otherwise.
     */
    public boolean isViewableFrom(Position other) {
        if (this.getZ() != other.getZ())
            return false;

        Position p = Utility.delta(this, other);
        return p.x <= 14 && p.x >= -15 && p.y <= 14 && p.y >= -15;
    }

    /**
     * Checks if this position is within distance of another position.
     * 
     * @param position
     *            the position to check the distance for.
     * @param distance
     *            the distance to check.
     * @return true if this position is within the distance of another position.
     */
    public boolean withinDistance(Position position, int distance) {
        if (this.getZ() != position.getZ())
            return false;

        return Math.abs(position.getX() - this.getX()) <= distance && Math
                .abs(position.getY() - this.getY()) <= distance;
    }
}
