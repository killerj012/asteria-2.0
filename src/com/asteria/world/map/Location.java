package com.asteria.world.map;

import com.asteria.util.Utility;
import com.asteria.world.World;
import com.asteria.world.entity.Entity;

/**
 * A collection of coordinates that form a square on the {@link World}.
 * 
 * @author lare96
 */
public class Location {

    /** The wilderness location. */
    public static final Location WILDERNESS = new Location(2941, 3518, 3392,
            3966);

    /** The south-west <code>x</code> coordinate. */
    private int southWestX;

    /** The south-west <code>y</code> coordinate. */
    private int southWestY;

    /** The north-east <code>x</code> coordinate. */
    private int northEastX;

    /** The north-east <code>y</code> coordinate. */
    private int northEastY;

    /**
     * Create a new {@link Location}.
     * 
     * @param southWestX
     *            the south-west <code>x</code> coordinate.
     * @param southWestY
     *            the south-west <code>y</code> coordinate.
     * @param northEastX
     *            the north-east <code>x</code> coordinate.
     * @param northEastY
     *            the north-east <code>y</code> coordinate.
     */
    public Location(int southWestX, int southWestY, int northEastX,
            int northEastY) {
        this.southWestX = southWestX;
        this.southWestY = southWestY;
        this.northEastX = northEastX;
        this.northEastY = northEastY;
    }

    /**
     * Create a new {@link Location}.
     * 
     * @param source
     *            the center of the location.
     * @param radius
     *            how big the location will be from the center.
     */
    public Location(Position source, int radius) {
        this(source.getX() - radius, source.getY() - radius,
                source.getX() + radius, source.getY() + radius);
    }

    /**
     * Checks if a {@link Position} is within this location.
     * 
     * @param position
     *            the position to check.
     * @return <code>true</code> if the position is within this location,
     *         <code>false</code> otherwise.
     */
    public boolean inLocation(Position position) {
        int x = position.getX();
        int y = position.getY();
        return x > southWestX && x < northEastX && y > southWestY && y < northEastY;
    }

    /**
     * Generates a random {@link Position} within this location.
     * 
     * @return the position generated from within this location.
     */
    public Position getRandomPosition() {
        int x = Math.min(southWestX, northEastX);
        int x2 = Math.max(southWestX, northEastX);
        int y = Math.min(southWestY, northEastY);
        int y2 = Math.max(southWestY, northEastY);
        int randomX = Utility.exclusiveRandom(x2 - x + 1) + x;
        int randomY = Utility.exclusiveRandom(y2 - y + 1) + y;
        return new Position(randomX, randomY, 0);
    }

    @Override
    public String toString() {
        return "LOCATION[south west= (" + southWestX + ", " + southWestY + "), north east= (" + northEastX + ", " + northEastY + ")]";
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Location) {
            Location l = (Location) other;
            return southWestX == l.southWestX && southWestY == l.southWestY && northEastX == l.northEastX && northEastY == l.northEastY;
        }
        return false;
    }

    /**
     * Checks if this {@link Entity} is in the wilderness.
     * 
     * @param entity
     *            the entity to check for.
     * @return true if the entity is in the wilderness.
     */
    public static boolean inWilderness(Entity entity) {
        return WILDERNESS.inLocation(entity.getPosition());
    }

    /**
     * Checks if this {@link Entity} is in a multicombat zone.
     * 
     * @param entity
     *            the entity to check for.
     * @return true if the entity is in multicombat zone.
     */
    public static boolean inMultiCombat(Entity entity) {
        return (entity.getPosition().getX() >= 3136 && entity.getPosition()
                .getX() <= 3327 && entity.getPosition().getY() >= 3519 && entity
                .getPosition().getY() <= 3607) || (entity.getPosition().getX() >= 3190 && entity
                .getPosition().getX() <= 3327 && entity.getPosition().getY() >= 3648 && entity
                .getPosition().getY() <= 3839) || (entity.getPosition().getX() >= 3200 && entity
                .getPosition().getX() <= 3390 && entity.getPosition().getY() >= 3840 && entity
                .getPosition().getY() <= 3967) || (entity.getPosition().getX() >= 2992 && entity
                .getPosition().getX() <= 3007 && entity.getPosition().getY() >= 3912 && entity
                .getPosition().getY() <= 3967) || (entity.getPosition().getX() >= 2946 && entity
                .getPosition().getX() <= 2959 && entity.getPosition().getY() >= 3816 && entity
                .getPosition().getY() <= 3831) || (entity.getPosition().getX() >= 3008 && entity
                .getPosition().getX() <= 3199 && entity.getPosition().getY() >= 3856 && entity
                .getPosition().getY() <= 3903) || (entity.getPosition().getX() >= 3008 && entity
                .getPosition().getX() <= 3071 && entity.getPosition().getY() >= 3600 && entity
                .getPosition().getY() <= 3711) || (entity.getPosition().getX() >= 3072 && entity
                .getPosition().getX() <= 3327 && entity.getPosition().getY() >= 3608 && entity
                .getPosition().getY() <= 3647) || (entity.getPosition().getX() >= 2624 && entity
                .getPosition().getX() <= 2690 && entity.getPosition().getY() >= 2550 && entity
                .getPosition().getY() <= 2619) || (entity.getPosition().getX() >= 2371 && entity
                .getPosition().getX() <= 2422 && entity.getPosition().getY() >= 5062 && entity
                .getPosition().getY() <= 5117) || (entity.getPosition().getX() >= 2896 && entity
                .getPosition().getX() <= 2927 && entity.getPosition().getY() >= 3595 && entity
                .getPosition().getY() <= 3630) || (entity.getPosition().getX() >= 2892 && entity
                .getPosition().getX() <= 2932 && entity.getPosition().getY() >= 4435 && entity
                .getPosition().getY() <= 4464) || (entity.getPosition().getX() >= 2256 && entity
                .getPosition().getX() <= 2287 && entity.getPosition().getY() >= 4680 && entity
                .getPosition().getY() <= 4711);
    }

    /**
     * Gets the south-west <code>x</code> coordinate.
     * 
     * @return the south-west <code>x</code> coordinate.
     */
    public int getSouthWestX() {
        return southWestX;
    }

    /**
     * Sets the south-west <code>x</code> coordinate.
     * 
     * @param southWestX
     *            the south-west <code>x</code> coordinate.
     */
    public void setSouthWestX(int southWestX) {
        this.southWestX = southWestX;
    }

    /**
     * Gets the south-west <code>y</code> coordinate.
     * 
     * @return the south-west <code>y</code> coordinate.
     */
    public int getSouthWestY() {
        return southWestY;
    }

    /**
     * Sets the south-west <code>y</code> coordinate.
     * 
     * @param southWestY
     *            the south-west <code>y</code> coordinate.
     */
    public void setSouthWestY(int southWestY) {
        this.southWestY = southWestY;
    }

    /**
     * Gets the north-east <code>x</code> coordinate.
     * 
     * @return the north-east <code>x</code> coordinate.
     */
    public int getNorthEastX() {
        return northEastX;
    }

    /**
     * Sets the north-east <code>x</code> coordinate.
     * 
     * @param northEastX
     *            the north-east <code>x</code> coordinate.
     */
    public void setNorthEastX(int northEastX) {
        this.northEastX = northEastX;
    }

    /**
     * Gets the north-east <code>y</code> coordinate.
     * 
     * @return the north-east <code>y</code> coordinate.
     */
    public int getNorthEastY() {
        return northEastY;
    }

    /**
     * Sets the north-east <code>y</code> coordinate.
     * 
     * @param northEastY
     *            the north-east <code>y</code> coordinate.
     */
    public void setNorthEastY(int northEastY) {
        this.northEastY = northEastY;
    }
}
