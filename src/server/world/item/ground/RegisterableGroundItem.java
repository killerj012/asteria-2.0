package server.world.item.ground;

import java.util.ArrayList;
import java.util.List;

import server.world.RegisterableContainer;
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
public class RegisterableGroundItem implements RegisterableContainer<GroundItem> {

    /**
     * The database that holds every single registered {@link GroundItem}.
     */
    private static List<GroundItem> itemList = new ArrayList<GroundItem>();

    /**
     * Checks if the specified item exists.
     * 
     * @param item
     *        the item to check exists.
     * @return the instance of the item in the database.
     */
    public GroundItem searchDatabase(GroundItem item) {

        /**
         * Iterate through all of the global items and check if any of them
         * match the parameter.
         */
        for (GroundItem w : itemList) {
            if (w == null) {
                continue;
            }

            if (w.getItem().getId() == item.getItem().getId() && w.getPosition().getX() == item.getPosition().getX() && w.getPosition().getY() == item.getPosition().getY() && w.getPosition().getZ() == item.getPosition().getZ()) {
                return w;
            }
        }
        return null;
    }

    /**
     * Fires the pickup event for an {@link GroundItem}.
     * 
     * @param item
     *        the item's pickup event to fire.
     * @param player
     *        the player that fired the pickup event.
     */
    public void pickupDatabaseItem(GroundItem item, Player player) {

        /** Fire the pickup event. */
        item.fireOnPickup(player);
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
        for (GroundItem w : itemList) {
            if (w == null) {
                continue;
            }

            if (w.getPosition().equals(position)) {
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
        for (final GroundItem w : itemList) {
            if (w == null) {
                continue;
            }

            if (player.getPosition().getZ() != w.getPosition().getZ()) {
                player.getPacketBuilder().removeGroundItem(w);
            }
        }
    }

    @Override
    public void register(GroundItem registerable) {

        /** Fire the item's registration event. */
        registerable.fireOnRegister();

        /** Add the item in the database. */
        itemList.add(registerable);
    }

    @Override
    public void unregister(GroundItem registerable) {

        /** Fire the item's unregistration event. */
        registerable.fireOnUnregister();

        /** Remove the item from the database. */
        itemList.remove(registerable);
    }

    @Override
    public void loadNewRegion(Player player) {

        /**
         * Iterate through all of the registered ground items and update the
         * region with items in the same region as the player.
         */
        for (final GroundItem w : itemList) {
            if (w == null) {
                continue;
            }

            if (w.getState() == ItemState.SEEN_BY_NO_ONE || w.getState() == null && player.getPosition().withinDistance(w.getPosition(), 60)) {
                player.getPacketBuilder().sendGroundItem(new GroundItem(new Item(w.getItem().getId(), w.getItem().getAmount()), new Position(w.getPosition().getX(), w.getPosition().getY(), w.getPosition().getZ()), player));
                continue;
            }

            if (w.getPlayer() != null) {
                if (w.getPlayer().getUsername().equals(player.getUsername()) && player.getPosition().withinDistance(w.getPosition(), 60)) {
                    player.getPacketBuilder().sendGroundItem(new GroundItem(new Item(w.getItem().getId(), w.getItem().getAmount()), new Position(w.getPosition().getX(), w.getPosition().getY(), w.getPosition().getZ()), player));
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
