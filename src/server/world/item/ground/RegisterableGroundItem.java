package server.world.item.ground;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import server.world.entity.player.Player;
import server.world.item.ground.GroundItem.ItemState;
import server.world.map.Position;

/**
 * Manages every single {@link GroundItem} registered to the
 * <code>itemList</code> database.
 * 
 * @author lare96
 */
public class RegisterableGroundItem {

    /** A database that holds every single registered {@link GroundItem}. */
    private static List<GroundItem> itemList = new ArrayList<GroundItem>();

    /**
     * Fires the pickup event for a {@link GroundItem}.
     * 
     * @param item
     *            the item's pickup event to fire.
     * @param player
     *            the player that fired the pickup event.
     */
    public void firePickupEvent(GroundItem item, Player player) {

        /** Fire the pickup event. */
        item.fireOnPickup(player);
    }

    /**
     * Searches the database for an item and returns it if found.
     * 
     * @param itemId
     *            the id of the item to search for.
     * @param position
     *            the position of the item to search for.
     */
    public GroundItem searchDatabase(int itemId, Position position) {
        for (GroundItem item : itemList) {
            if (item == null) {
                continue;

            }

            if (item.getItem().getId() == itemId
                    && item.getPosition().equals(position)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Registers a new ground item to the game world.
     * 
     * @param registerable
     *            the ground item to register.
     */
    public void register(GroundItem registerable) {

        /** Fire the item's registration event. */
        registerable.fireOnRegister();

        /** Add the item in the database. */
        itemList.add(registerable);
    }

    /**
     * Registers a new ground item and stacks it on top of any existing ground
     * items with the same id.
     * 
     * @param registerable
     *            the ground item to register and stack if applicable.
     */
    public void registerAndStack(GroundItem registerable) {
        int itemCount = 0;

        for (Iterator<GroundItem> iterator = itemList.iterator(); iterator
                .hasNext();) {
            GroundItem item = iterator.next();

            if (item == null) {
                continue;
            }

            if (item.getItem().getId() == registerable.getItem().getId()
                    && item.getPosition().equals(registerable.getPosition())
                    && item.getPlayer().getUsername()
                            .equals(registerable.getPlayer().getUsername())) {
                itemCount += item.getItem().getAmount();
                item.fireOnUnregister();
                iterator.remove();
            }
        }

        registerable.getItem().incrementAmountBy(itemCount);

        /** Fire the item's registration event. */
        registerable.fireOnRegister();

        /** Add the item in the database. */
        itemList.add(registerable);
    }

    /**
     * Unregisters an existing ground item from the game world.
     * 
     * @param registerable
     *            the ground item to unregister.
     */
    public void unregister(GroundItem registerable) {

        /** Fire the item's unregistration event. */
        registerable.fireOnUnregister();

        /** Remove the item from the database. */
        itemList.remove(registerable);
    }

    /**
     * Fired when the player loads a new region.
     * 
     * @param player
     *            the player loading a new region.
     */
    public void loadNewRegion(Player player) {

        /**
         * Iterate through all of the registered ground items and update the
         * region with items in the same region as the player.
         */
        for (final GroundItem databaseItem : itemList) {
            if (databaseItem == null) {
                continue;
            }

            player.getPacketBuilder().removeGroundItem(databaseItem);

            if (databaseItem.getState() == ItemState.SEEN_BY_NO_ONE
                    || databaseItem.getState() == null
                    && databaseItem.getPosition().withinDistance(
                            player.getPosition(), 60)) {
                player.getPacketBuilder().sendGroundItem(databaseItem);
                continue;
            }

            if (databaseItem.getPlayer() != null) {
                if (databaseItem.getPlayer().getUsername()
                        .equals(player.getUsername())
                        && databaseItem.getPosition().withinDistance(
                                player.getPosition(), 60)) {
                    player.getPacketBuilder().sendGroundItem(databaseItem);
                    continue;
                }
            }
        }
    }

    /**
     * Gets the database of registered items.
     * 
     * @return the database of items.
     */
    protected List<GroundItem> getItemList() {
        return itemList;
    }
}
