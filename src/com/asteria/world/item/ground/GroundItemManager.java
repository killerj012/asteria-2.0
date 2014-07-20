package com.asteria.world.item.ground;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.asteria.engine.task.Task;
import com.asteria.util.JsonLoader;
import com.asteria.world.World;
import com.asteria.world.entity.player.Player;
import com.asteria.world.item.Item;
import com.asteria.world.item.ground.GroundItem.ItemState;
import com.asteria.world.item.ground.GroundItem.StaticGroundItem.ItemPolicy;
import com.asteria.world.map.Position;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Manages every single {@link GroundItem} registered to the
 * <code>itemList</code> database.
 * 
 * @author lare96
 */
public final class GroundItemManager extends Task implements
        Iterable<GroundItem> {

    /** How often processing events will be fired. */
    public static final int FIRE_PROCESSING_EVENTS = 100;

    /** A database that holds every single registered {@link GroundItem}. */
    private static final List<GroundItem> itemList = new LinkedList<GroundItem>();

    /** Create a new {@link GroundItemManager}. */
    public GroundItemManager() {

        // Every 10 ticks so not as stressful on the server, items may be off by
        // a few ticks but it won't be noticeable at all so its fine.
        super(10, true);
    }

    @Override
    public void fire() {

        // Iterate through all of the ground items.
        for (Iterator<GroundItem> it = itemList.listIterator(); it.hasNext();) {
            GroundItem item = it.next();

            // Skip all of the null values.
            if (item == null) {
                continue;
            }

            // Increment the ticks and fire the processing event.
            item.ticks += 10;

            // Fire the processing event for this item if enough time has
            // passed.
            if (item.ticks >= FIRE_PROCESSING_EVENTS) {
                item.fireOnProcess(it);
                item.ticks = 0;
            }
        }
    }

    @Override
    public Iterator<GroundItem> iterator() {
        return itemList.iterator();
    }

    /**
     * Registers the argued {@link GroundItem} to the {@link World}.
     * 
     * @param item
     *            the ground item to register.
     */
    public static void register(GroundItem item) {

        // Fire the item's registration event.
        item.fireOnRegister();

        // Add the item to the database.
        itemList.add(item);
    }

    /**
     * Registers the argued {@link GroundItem} to the {@link World} and stacks
     * it on top of any existing ones with the same ID.
     * 
     * @param item
     *            the ground item to register and stack.
     */
    public static void registerAndStack(GroundItem item) {

        // The item count holder.
        int count = 0;

        // Iterate through the items to increment the count.
        for (Iterator<GroundItem> iterator = itemList.iterator(); iterator
                .hasNext();) {
            GroundItem next = iterator.next();

            if (next == null) {
                continue;
            }

            if (next.getItem().getId() == item.getItem().getId() && next
                    .getPosition().equals(item.getPosition()) && next
                    .getPlayer().equals(item.getPlayer())) {
                count += next.getItem().getAmount();
                next.fireOnUnregister();
                iterator.remove();
            }
        }

        // Then register the item.
        item.getItem().incrementAmountBy(count);
        item.fireOnRegister();
        itemList.add(item);
    }

    /**
     * Unregisters the argued {@link GroundItem} to the {@link World}.
     * 
     * @param item
     *            the ground item to unregister.
     */
    public static void unregister(GroundItem item) {

        // Fire the item's unregistration event.
        item.fireOnUnregister();

        // Remove the item from the database.
        itemList.remove(item);
    }

    /**
     * Loads the images of {@link GroundItem}s for the argued player when they
     * enter a new region.
     * 
     * @param player
     *            the player loading the new region.
     */
    public static void load(Player player) {

        // Iterate through the ground items.
        for (GroundItem item : itemList) {
            if (item == null || item.getState() == ItemState.HIDDEN) {
                continue;
            }

            // Remove the image of the ground item.
            player.getPacketBuilder().sendRemoveGroundItem(item);

            // Check if we're even in the right distance.
            if (item.getPosition().withinDistance(player.getPosition(), 60)) {

                // Send the image to the player if the item is seen by everyone.
                if (item.getPlayer() == null && item.getState() == ItemState.SEEN_BY_EVERYONE) {
                    player.getPacketBuilder().sendGroundItem(item);
                    continue;
                }

                // Send the image to player if the ground item belongs to the
                // player.
                if (item.getPlayer().equals(player) && item.getState() == ItemState.SEEN_BY_OWNER) {
                    player.getPacketBuilder().sendGroundItem(item);
                    continue;
                }
            }
        }
    }

    /**
     * Gets the first occurrence of the argued item on the argued position.
     * 
     * @param id
     *            the id of the item.
     * @param position
     *            the position of the item.
     * @return the instance of the item, or <code>null</code> if the item does
     *         not exist.
     */
    public static GroundItem getItem(int id, Position position) {
        for (GroundItem item : itemList) {
            if (item == null) {
                continue;

            }
            if (item.getItem().getId() == id && item.getPosition().equals(
                    position)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Prepares the dynamic json loader for loading world items.
     * 
     * @return the dynamic json loader.
     * @throws Exception
     *             if any errors occur while preparing for load.
     */
    public static JsonLoader parseItems() throws Exception {
        return new JsonLoader() {
            @Override
            public void load(JsonObject reader, Gson builder) {
                GroundItem.StaticGroundItem item = new GroundItem.StaticGroundItem(
                        new Item(reader.get("id").getAsInt(), reader.get(
                                "amount").getAsInt()), builder.fromJson(
                                reader.get("position"), Position.class),
                        ItemPolicy.RESPAWN);
                register(item);
            }

            @Override
            public String filePath() {
                return "./data/json/items/world_items.json";
            }
        };
    }
}
