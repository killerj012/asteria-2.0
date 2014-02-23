package server.world.object;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import server.core.Rs2Engine;
import server.world.RegisterableContainer;
import server.world.entity.player.Player;

/**
 * Manages all objects in the world.
 * 
 * @author lare96
 */
public class RegisterableWorldObject implements RegisterableContainer<WorldObject> {

    /**
     * The singleton instance.
     */
    private static RegisterableWorldObject singleton;

    /**
     * A set to keep track of the objects in the world. We use a set so there
     * cannot be duplicates of an object.
     */
    private static Set<WorldObject> objects = new HashSet<WorldObject>();

    /**
     * Unregisters an existing global object not in the database (spawned from
     * the client).
     * 
     * @param object
     *        the object to unregister.
     */
    public void unregisterNoDatabase(WorldObject object) {

        /** Remove object for all existing players. */
        for (Player player : Rs2Engine.getWorld().getPlayers()) {
            if (player == null) {
                continue;
            }

            player.getPacketBuilder().removeObject(object);
        }
    }

    /**
     * Removes any ground items for a player that aren't on the same height
     * level.
     * 
     * @param player
     *        the player to remove the items for.
     */
    public void removeAllHeight(Player player) {
        for (final WorldObject w : objects) {
            if (w == null) {
                continue;
            }

            if (player.getPosition().getZ() != w.getPosition().getZ()) {
                player.getPacketBuilder().removeObject(w);
            }
        }
    }

    @Override
    public void register(WorldObject registerable) {

        /**
         * Check if an object is already on this position, and if so it removes
         * the object from the database before spawning the new one over it.
         */
        for (Iterator<WorldObject> iter = objects.iterator(); iter.hasNext();) {
            WorldObject o = iter.next();

            if (o == null) {
                continue;
            }

            if (o.getPosition().equals(registerable.getPosition())) {
                iter.remove();
            }
        }

        /** Register object for future players. */
        objects.add(registerable);

        /** Add object for existing players (in the region) */
        for (Player player : Rs2Engine.getWorld().getPlayers()) {
            if (player == null) {
                continue;
            }

            if (player.getPosition().withinDistance(registerable.getPosition(), 60)) {
                player.getPacketBuilder().sendObject(registerable);
            }
        }
    }

    @Override
    public void unregister(WorldObject registerable) {

        /** Can't remove an object that isn't there. */
        if (!objects.contains(registerable)) {
            return;
        }

        /** Unregister object for future players. */
        for (Iterator<WorldObject> iter = objects.iterator(); iter.hasNext();) {
            WorldObject o = iter.next();

            if (o == null) {
                continue;
            }

            if (o.equals(registerable)) {
                iter.remove();
            }
        }

        /** Remove object for all existing players. */
        for (Player player : Rs2Engine.getWorld().getPlayers()) {
            if (player == null) {
                continue;
            }

            player.getPacketBuilder().removeObject(registerable);
        }
    }

    @Override
    public void loadNewRegion(Player player) {

        /** Update existing objects for player in region. */
        for (WorldObject object : objects) {
            if (object == null) {
                continue;
            }

            if (object.getPosition().withinDistance(player.getPosition(), 60)) {
                player.getPacketBuilder().sendObject(object);
            }
        }
    }

    /**
     * @return the objects.
     */
    public Set<WorldObject> getObjects() {
        return objects;
    }

    /**
     * @return the singleton instance.
     */
    public static RegisterableWorldObject getSingleton() {
        if (singleton == null) {
            singleton = new RegisterableWorldObject();
        }
        return singleton;
    }
}
