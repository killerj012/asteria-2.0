package server.world.map;

import server.util.Misc;
import server.world.entity.Entity;

/**
 * A south-west and north-east {@link Position} that form a square on the map.
 * 
 * @author lare96
 */
public class Location {

    /** A location constant holding the values that make up the wilderness area. */
    private static final Location WILDERNESS = new Location(new Position(2941, 3518), new Position(3392, 3966));

    /** The south-west coordinates. */
    private Position southWest;

    /** The north-east coordinates. */
    private Position northEast;

    /**
     * Create a new {@link Location}.
     * 
     * @param southWest
     *        the south-west coordinates.
     * @param northEast
     *        the north-east coordinates.
     */
    public Location(Position southWest, Position northEast) {
        this.southWest = southWest;
        this.northEast = northEast;
    }

    /**
     * Create a new {@link Location}.
     * 
     * @param source
     *        the center of the location.
     * @param radius
     *        how big the location will be.
     */
    public Location(Position source, int radius) {
        this.southWest = new Position(source.getX() - radius, source.getY() - radius);
        this.northEast = new Position(source.getX() + radius, source.getY() + radius);
    }

    /**
     * Checks if a {@link Position} is within this location.
     * 
     * @param position
     *        the position to check.
     * @return true if the position is within this location.
     */
    public boolean inLocation(Position position) {
        int x = position.getX();
        int y = position.getY();

        return x > southWest.getX() && x < northEast.getX() && y > southWest.getY() && y < northEast.getY() ? true : false;
    }

    /**
     * Generates a random {@link Position} within this location.
     * 
     * @return the new position generated.
     */
    public Position randomPosition() {
        int x = Math.min(southWest.getX(), northEast.getX());
        int x2 = Math.max(southWest.getX(), northEast.getX());

        int y = Math.min(southWest.getY(), northEast.getY());
        int y2 = Math.max(southWest.getY(), northEast.getY());

        int randomX = Misc.getRandom().nextInt(x2 - x + 1) + x;
        int randomY = Misc.getRandom().nextInt(y2 - y + 1) + y;

        return new Position(randomX, randomY, 0);
    }

    @Override
    public String toString() {
        return "Location[sw(" + southWest.getX() + ", " + southWest.getY() + "):ne(" + northEast.getX() + ", " + northEast.getY() + ")]";
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Location) {
            Location l = (Location) other;
            return southWest.getX() == l.getSouthWest().getX() && southWest.getY() == l.getSouthWest().getY() && northEast.getX() == l.getNorthEast().getX() && northEast.getY() == l.getNorthEast().getY();
        }
        return false;
    }

    /**
     * Checks if this {@link Entity} is in the wilderness.
     * 
     * @param entity
     *        the entity to check for.
     * @return true if the entity is in the wilderness.
     */
    public static boolean inWilderness(Entity entity) {
        return WILDERNESS.inLocation(new Position(entity.getPosition().getX(), entity.getPosition().getY()));
    }

    /**
     * Checks if this {@link Entity} is in a multicombat zone.
     * 
     * @param entity
     *        the entity to check for.
     * @return true if the entity is in multicombat zone.
     */
    public static boolean inMultiCombat(Entity entity) {
        return (entity.getPosition().getX() >= 3136 && entity.getPosition().getX() <= 3327 && entity.getPosition().getY() >= 3519 && entity.getPosition().getY() <= 3607) || (entity.getPosition().getX() >= 3190 && entity.getPosition().getX() <= 3327 && entity.getPosition().getY() >= 3648 && entity.getPosition().getY() <= 3839) || (entity.getPosition().getX() >= 3200 && entity.getPosition().getX() <= 3390 && entity.getPosition().getY() >= 3840 && entity.getPosition().getY() <= 3967) || (entity.getPosition().getX() >= 2992 && entity.getPosition().getX() <= 3007 && entity.getPosition().getY() >= 3912 && entity.getPosition().getY() <= 3967) || (entity.getPosition().getX() >= 2946 && entity.getPosition().getX() <= 2959 && entity.getPosition().getY() >= 3816 && entity.getPosition().getY() <= 3831) || (entity.getPosition().getX() >= 3008 && entity.getPosition().getX() <= 3199 && entity.getPosition().getY() >= 3856 && entity.getPosition().getY() <= 3903) || (entity.getPosition().getX() >= 3008 && entity.getPosition().getX() <= 3071 && entity.getPosition().getY() >= 3600 && entity.getPosition().getY() <= 3711) || (entity.getPosition().getX() >= 3072 && entity.getPosition().getX() <= 3327 && entity.getPosition().getY() >= 3608 && entity.getPosition().getY() <= 3647) || (entity.getPosition().getX() >= 2624 && entity.getPosition().getX() <= 2690 && entity.getPosition().getY() >= 2550 && entity.getPosition().getY() <= 2619) || (entity.getPosition().getX() >= 2371 && entity.getPosition().getX() <= 2422 && entity.getPosition().getY() >= 5062 && entity.getPosition().getY() <= 5117) || (entity.getPosition().getX() >= 2896 && entity.getPosition().getX() <= 2927 && entity.getPosition().getY() >= 3595 && entity.getPosition().getY() <= 3630) || (entity.getPosition().getX() >= 2892 && entity.getPosition().getX() <= 2932 && entity.getPosition().getY() >= 4435 && entity.getPosition().getY() <= 4464) || (entity.getPosition().getX() >= 2256 && entity.getPosition().getX() <= 2287 && entity.getPosition().getY() >= 4680 && entity.getPosition().getY() <= 4711);
    }

    /**
     * Gets the south-west coordinates.
     * 
     * @return the south-west coordinates.
     */
    public Position getSouthWest() {
        return southWest;
    }

    /**
     * Gets the north-east coordinates.
     * 
     * @return the north-east coordinates.
     */
    public Position getNorthEast() {
        return northEast;
    }
}
