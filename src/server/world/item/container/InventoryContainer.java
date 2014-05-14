package server.world.item.container;

import java.util.Collection;

import server.world.entity.player.Player;
import server.world.item.Item;
import server.world.item.ItemContainer;
import server.world.item.ItemContainer.ContainerPolicy;

/**
 * Uses an {@link ItemContainer} to manage items in a player's inventory.
 * 
 * @author lare96
 * @author Vix
 */
public class InventoryContainer {

    /** The player's inventory being managed. */
    private Player player;

    /**
     * The item container that holds and manages all of the items in this
     * inventory.
     */
    private ItemContainer container = new ItemContainer(ContainerPolicy.NORMAL_POLICY, 28);

    /**
     * Create a new {@link InventoryContainer}.
     * 
     * @param player
     *        the player's inventory being managed.
     */
    public InventoryContainer(Player player) {
        this.player = player;
    }

    /**
     * Sends the items on this inventory to an interface.
     * 
     * @param writeInterface
     *        the interface to write this inventory on.
     */
    public void refresh(int writeInterface) {
        checkForZero();
        Item[] inv = container.toArray();
        player.getPacketBuilder().sendUpdateItems(writeInterface, inv);
    }

    /**
     * Adds an item into the inventory's container.
     * 
     * @param item
     *        the item to add to this container.
     */
    public void addItem(Item item) {

        /** Check if we are adding a valid item. */
        if (item == null) {
            return;
        }

        /** Check if we have enough space for this item. */
        if (!container.contains(item.getId()) && !item.getDefinition().isStackable()) {
            if (container.freeSlot() == -1) {
                player.getPacketBuilder().sendMessage("You don't have enough space in your inventory!");
                return;
            }
        }

        /**
         * Set the amount to the amount of free slots you have if there isn't
         * enough space.
         */
        if (item.getAmount() > container.freeSlots() && !item.getDefinition().isStackable()) {
            item.setAmount(container.freeSlots());
        }

        /** Add the item to the container. */
        container.add(item);

        /** Refresh the container. */
        refresh(3214);
    }

    /**
     * Adds a set of items into the inventory.
     * 
     * @param item
     *        the set of items to add.
     */
    public void addItemSet(Item[] item) {
        for (Item addItem : item) {
            if (item == null) {
                continue;
            }

            addItem(addItem);
        }
    }

    /**
     * Adds a collection of items into the inventory.
     * 
     * @param item
     *        the set of items to add.
     */
    public void addItemCollection(Collection<Item> collection) {
        for (Item addItem : collection) {
            if (collection == null) {
                continue;
            }

            addItem(addItem);
        }
    }

    /**
     * Overrides the specified slot with a new item.
     * 
     * @param item
     *        the item to override the slot with.
     * @param slot
     *        the slot to override.
     */
    public void overrideItemSlot(Item item, int slot) {

        /** Block if the item is malformed (null). */
        if (item == null) {
            return;
        }

        /** Add the item to the slot. */
        container.set(slot, item);

        /** Refresh the container. */
        refresh(3214);
    }

    /**
     * Deletes an item from this inventory's container.
     * 
     * @param item
     *        the item to delete from this container.
     */
    public void deleteItem(Item item) {

        /** Block if we are removing an malformed item. */
        if (item == null || item.getId() == -1 || item.getAmount() < 1) {
            return;
        }

        /** Block if this container doesn't contain this item. */
        if (!container.contains(item.getId())) {
            return;
        }

        /** Remove the item. */
        if (item.getDefinition().isStackable()) {
            container.remove(item);
        } else {
            container.remove(item);
        }

        /** Refresh this container. */
        refresh(3214);
    }

    /**
     * Deletes a set of items from the inventory.
     * 
     * @param item
     *        the set of items to delete.
     */
    public void deleteItemSet(Item[] item) {
        for (Item deleteItem : item) {
            if (item == null) {
                continue;
            }

            deleteItem(deleteItem);
        }
    }

    /**
     * Replaces an existing item in the inventory with a new one.
     * 
     * @param oldItem
     *        the item to replace.
     * @param newItem
     *        the item to add.
     */
    public void replaceItem(Item oldItem, Item newItem) {

        /** Replace the item. */
        if (getContainer().getCount(oldItem.getId()) >= oldItem.getAmount()) {
            deleteItem(oldItem);
            addItem(newItem);
        }
    }

    /**
     * Deletes an item from the specified slot in this inventory.
     * 
     * @param item
     *        the item to remove.
     * @param slot
     *        the slot to remove this item from.
     */
    public void deleteItemSlot(Item item, int slot) {

        /** Block if this item is malformed. */
        if (item == null || item.getId() == -1 || item.getAmount() < 1) {
            return;
        }

        /** Block if this slot is invalid. */
        if (slot == -1) {
            return;
        }

        /**
         * Block if no item exists on this slot or this container does not
         * contain the item.
         */
        if (container.getItem(slot) == null || !container.contains(item.getId())) {
            return;
        }

        /** Remove the item. */
        container.remove(item, slot);

        /** Refresh the inventory. */
        refresh(3214);
    }

    /**
     * Exchanges the slots of two items in the inventory container.
     * 
     * @param initialSlot
     *        the current slot of the item.
     * @param exchangeSlot
     *        the destination slot for this item.
     */
    public void exchangeItemSlot(int initialSlot, int exchangeSlot) {
        container.swap(initialSlot, exchangeSlot);
        refresh(3214);
    }

    /**
     * Checks if the bank has any items with a value of 0 and removes them if
     * so.
     */
    public void checkForZero() {

        /** Loop through the items in this container. */
        for (int i = 0; i < container.toArray().length; i++) {

            /** Ignore empty slots. */
            if (container.toArray()[i] == null) {
                continue;
            }

            /** Free up slots with items that have an amount below 1. */
            if (container.toArray()[i].getAmount() < 1) {
                container.toArray()[i] = null;
            }
        }
    }

    /**
     * Gets the backing {@link ItemContainer}.
     * 
     * @return the container.
     */
    public ItemContainer getContainer() {
        return container;
    }
}