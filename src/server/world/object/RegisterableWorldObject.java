package server.world.object;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import server.world.World;
import server.world.entity.player.Player;
import server.world.map.Position;

/**
 * Manages every single {@link WorldObject} registered to the
 * <code>objectSet</code> database.
 * 
 * @author lare96
 */
public class RegisterableWorldObject {

    /**
     * A {@link HashSet} to keep track of all of the {@link WorldObject}s in the
     * game.
     */
    private static Set<WorldObject> objectSet = new HashSet<WorldObject>();

    /**
     * Registers a new object to the database.
     * 
     * @param registerable
     *            the new object to register to the database.
     */
    public void register(WorldObject registerable) {

        /**
         * Check if an object is already on this position and if so it removes
         * the object from the database before spawning the new one over it.
         */
        for (Iterator<WorldObject> iter = objectSet.iterator(); iter.hasNext();) {
            WorldObject object = iter.next();

            if (object.getPosition().equals(registerable.getPosition())) {
                iter.remove();
            }
        }

        /** Register object for future players. */
        objectSet.add(registerable);

        /** Add object for existing players in the region. */
        for (Player player : World.getPlayers()) {
            if (player == null) {
                continue;
            }

            if (player.getPosition().withinDistance(registerable.getPosition(),
                    60)) {
                player.getPacketBuilder().sendObject(registerable);
            }
        }
    }

    /**
     * Removes an existing object from the database.
     * 
     * @param registerable
     *            the existing object from the database.
     */
    public void unregister(WorldObject registerable) {

        /** Can't remove an object that isn't there. */
        if (!objectSet.contains(registerable)) {
            return;
        }

        /** Unregister object for future players. */
        for (Iterator<WorldObject> iter = objectSet.iterator(); iter.hasNext();) {
            WorldObject object = iter.next();

            if (object.equals(registerable)) {
                iter.remove();
            }
        }

        /** Remove object for all existing players. */
        for (Player player : World.getPlayers()) {
            if (player == null) {
                continue;
            }

            player.getPacketBuilder().removeObject(registerable);
        }
    }

    /**
     * Gets the object on the speicified position.
     * 
     * @param position
     *            the position to get the object on.
     * @return the object on the position.
     */
    public WorldObject getObjectOnPosition(Position position) {
        for (WorldObject object : objectSet) {
            if (position.equals(object.getPosition())) {
                return object;
            }
        }
        return null;
    }

    /**
     * Fired when the player loads a new region.
     * 
     * @param player
     *            the player loading a new region.
     */
    public void loadNewRegion(Player player) {

        /** Update existing objects for player in region. */
        for (WorldObject object : objectSet) {
            if (object == null) {
                continue;
            }

            player.getPacketBuilder().removeObject(object);

            if (object.getPosition().withinDistance(player.getPosition(), 60)) {
                player.getPacketBuilder().sendObject(object);
            }
        }
    }

    /**
     * Gets the set of {@link WorldObject}s.
     * 
     * @return the set of objects.
     */
    public Set<WorldObject> getObjectSet() {
        return objectSet;
    }
}
