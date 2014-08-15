package com.asteria.world.object;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.asteria.util.JsonLoader;
import com.asteria.world.World;
import com.asteria.world.entity.player.Player;
import com.asteria.world.map.Position;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Manages every single {@link WorldObject} registered to the
 * <code>objectSet</code> database.
 * 
 * @author lare96
 */
public class WorldObjectManager implements Iterable<WorldObject> {

    /** A set to keep track of all of the objects in the game. */
    private static final Set<WorldObject> objectSet = new HashSet<>();

    /**
     * Registers a new {@link WorldObject} to the database.
     * 
     * @param object
     *            the new object to register.
     */
    public static void register(WorldObject object) {

        // Check if an object is already on this position and if so it removes
        // the object from the database before spawning the new one over it.
        for (Iterator<WorldObject> iter = objectSet.iterator(); iter.hasNext();) {
            WorldObject next = iter.next();

            if (next.getPosition().equals(object.getPosition())) {
                iter.remove();
            }
        }

        // Register object for future players.
        objectSet.add(object);

        // Add object for existing players in the region.
        for (Player player : World.getPlayers()) {
            if (player == null) {
                continue;
            }

            if (player.getPosition().withinDistance(object.getPosition(), 60)) {
                player.getPacketBuilder().sendObject(object);
            }
        }
    }

    /**
     * Unregisters an existing {@link WorldObject} from the database.
     * 
     * @param object
     *            the existing object to unregister.
     */
    public static void unregister(WorldObject registerable) {

        // Remove the object from the database.
        if (objectSet.remove(registerable)) {

            // Remove object for all existing players.
            for (Player player : World.getPlayers()) {
                if (player == null) {
                    continue;
                }

                player.getPacketBuilder().sendRemoveObject(registerable);
            }
        }
    }

    /**
     * Gets the object on the argued {@link Position}.
     * 
     * @param position
     *            the position to get the object on.
     * @return the object on the position.
     */
    public static WorldObject getObjectOnPosition(Position position) {
        for (WorldObject object : objectSet) {
            if (position.equals(object.getPosition())) {
                return object;
            }
        }
        return null;
    }

    /**
     * Loads the images of {@link WorldObject}s for the argued player when they
     * enter a new region.
     * 
     * @param player
     *            the player loading the new region.
     */
    public static void load(Player player) {

        // Update existing objects for player in region.
        for (WorldObject object : objectSet) {
            if (object == null) {
                continue;
            }

            player.getPacketBuilder().sendRemoveObject(object);

            if (object.getPosition().withinDistance(player.getPosition(), 60)) {
                player.getPacketBuilder().sendObject(object);
            }
        }
    }

    @Override
    public Iterator<WorldObject> iterator() {
        return objectSet.iterator();
    }

    /**
     * Prepares the dynamic json loader for loading world objects.
     * 
     * @return the dynamic json loader.
     * @throws Exception
     *             if any errors occur while preparing for load.
     */
    public static JsonLoader parseObjects() throws Exception {
        return new JsonLoader() {
            @Override
            public void load(JsonObject reader, Gson builder) {
                int id = reader.get("id").getAsInt();
                Position position = builder.fromJson(reader.get("position"),
                        Position.class);
                WorldObject.Rotation face = WorldObject.Rotation.valueOf(reader
                        .get("rotation").getAsString());

                if (face == null) {
                    throw new IllegalStateException(
                            "Invalid object rotation! for [id: " + id + " - " + position + "");
                }

                int type = reader.get("type").getAsInt();

                objectSet.add(new WorldObject(id, position, face, type));
            }

            @Override
            public String filePath() {
                return "./data/json/objects/world_objects.json";
            }
        };
    }
}
