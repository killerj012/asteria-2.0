package server.world.item.ground;

import java.util.ArrayList;
import java.util.List;

import server.world.entity.player.Player;
import server.world.item.Item;
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
     *        the item's pickup event to fire.
     * @param player
     *        the player that fired the pickup event.
     */
    public void firePickupEvent(GroundItem item, Player player) {

        /** Fire the pickup event. */
        item.fireOnPickup(player);
    }

    /**
     * Searches the database for an item and returns it if found.
     * 
     * @param item
     *        the item to search for.
     */
    public GroundItem searchDatabase(GroundItem item) {
        if (itemList.contains(item)) {
            return item;
        }
        return null;
    }

    /**
     * Determines if an item exists on this position or not.
     * 
     * @param position
     *        the position to check has items on it.
     * @return true if there are any items on this position.
     */
    public boolean searchDatabasePosition(Position position) {

        /**
         * Iterate through all of the global items and check if any of them are
         * on the position.
         */
        for (GroundItem databaseItem : itemList) {
            if (databaseItem == null) {
                continue;
            }

            if (databaseItem.getPosition().equals(position)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Removes any items the aren't on the same height level as this player.
     * 
     * @param player
     *        the player to remove the items for.
     */
    public void searchDatabaseHeightChange(Player player) {

        /**
         * Iterate through the ground items and remove the ones that aren't
         * supposed to be on this level height level.
         */
        for (final GroundItem databaseItem : itemList) {
            if (databaseItem == null) {
                continue;
            }

            if (player.getPosition().getZ() != databaseItem.getPosition().getZ()) {
                player.getPacketBuilder().removeGroundItem(databaseItem);
            }
        }
    }

    /**
     * Remove all registered items for the player.
     * 
     * @param player
     *        the player to remove registered items for.
     */
    public void removeAllItems(Player player) {
        for (final GroundItem databaseItem : itemList) {
            if (databaseItem == null) {
                continue;
            }

            player.getPacketBuilder().removeGroundItem(databaseItem);
        }
    }

    public void register(GroundItem registerable) {

        /** Fire the item's registration event. */
        registerable.fireOnRegister();

        /** Add the item in the database. */
        itemList.add(registerable);
    }

    public void unregister(GroundItem registerable) {

        /** Fire the item's unregistration event. */
        registerable.fireOnUnregister();

        /** Remove the item from the database. */
        itemList.remove(registerable);
    }

    public void loadNewRegion(Player player) {

        /** First remove all items. */
        removeAllItems(player);

        /**
         * Iterate through all of the registered ground items and update the
         * region with items in the same region as the player.
         */
        for (final GroundItem databaseItem : itemList) {
            if (databaseItem == null) {
                continue;
            }

            if (databaseItem.getState() == ItemState.SEEN_BY_NO_ONE || databaseItem.getState() == null) {
                player.getPacketBuilder().sendGroundItem(new GroundItem(new Item(databaseItem.getItem().getId(), databaseItem.getItem().getAmount()), new Position(databaseItem.getPosition().getX(), databaseItem.getPosition().getY(), databaseItem.getPosition().getZ()), player));
                continue;
            }

            if (databaseItem.getPlayer() != null) {
                if (databaseItem.getPlayer().getUsername().equals(player.getUsername())) {
                    player.getPacketBuilder().sendGroundItem(new GroundItem(new Item(databaseItem.getItem().getId(), databaseItem.getItem().getAmount()), new Position(databaseItem.getPosition().getX(), databaseItem.getPosition().getY(), databaseItem.getPosition().getZ()), player));
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
