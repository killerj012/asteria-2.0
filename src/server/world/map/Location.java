package server.world.map;

import server.util.Misc;
import server.world.entity.Entity;

/**
 * South-west and north-east coordinates that form a rectangle or square region.
 * 
 * @author lare96
 */
public class Location {

    /**
     * A location constant holding the values that make up the wilderness area.
     */
    private static final Location WILDERNESS = new Location(new Position(2941, 3518), new Position(3392, 3966));

    /**
     * The south-west coordinates.
     */
    private Position southWest;

    /**
     * The north-east coordinates.
     */
    private Position northEast;

    /**
     * Creates a new location.
     * 
     * @param southWest
     *            the south-west coordinates.
     * @param northEast
     *            the north-east coordinates.
     */
    public Location(Position southWest, Position northEast) {
        this.setSouthWest(southWest);
        this.setNorthEast(northEast);
    }

    /**
     * Checks if a position is within this location.
     * 
     * @param position
     *            the position to check.
     * @return true if the position is within this location.
     */
    public boolean inLocation(Position position) {
        int x = position.getX();
        int y = position.getY();

        return x > southWest.getX() && x < northEast.getX() && y > southWest.getY() && y < northEast.getY() ? true : false;
    }

    /**
     * Generates a random position within this location.
     * 
     * @return the position generated.
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
     * Checks if this entity is in the wilderness.
     * 
     * @param player
     *            the player to check for.
     * @return true if the player is in the wilderness.
     */
    public static boolean inWilderness(Entity entity) {
        return WILDERNESS.inLocation(new Position(entity.getPosition().getX(), entity.getPosition().getY()));
    }

    /**
     * Checks if this entity is in a multicombat zone.
     * 
     * @param player
     *            the player to check for.
     * @return true if the player is in multicombat zone.
     */
    public static boolean inMultiCombat(Entity entity) {
        return false;
    }

    /**
     * @return the southWest.
     */
    public Position getSouthWest() {
        return southWest;
    }

    /**
     * @param southWest
     *            the southWest to set.
     */
    public void setSouthWest(Position southWest) {
        this.southWest = southWest;
    }

    /**
     * @return the northEast.
     */
    public Position getNorthEast() {
        return northEast;
    }

    /**
     * @param northEast
     *            the northEast to set.
     */
    public void setNorthEast(Position northEast) {
        this.northEast = northEast;
    }
}
