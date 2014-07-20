package com.asteria.world.item.container;

import java.util.Collection;

import com.asteria.world.entity.player.Player;
import com.asteria.world.item.Item;
import com.asteria.world.item.ItemContainer;
import com.asteria.world.item.ItemContainer.ContainerPolicy;

/**
 * Uses an {@link ItemContainer} to manage items in a player's inventory.
 * 
 * @author lare96
 * @author Vix
 */
public class InventoryContainer {

    /** The id of the inventory container */
    public static final int DEFAULT_INVENTORY_CONTAINER_ID = 3214;

    /** The id of the inventory when you have the bank open. */
    public static final int BANK_INVENTORY_CONTAINER_ID = 5064;

    /** The player's inventory being managed. */
    private Player player;

    /** The item container that holds all of the items in this inventory. */
    private ItemContainer container = new ItemContainer(
            ContainerPolicy.NORMAL_POLICY, 28);

    /**
     * Create a new {@link InventoryContainer}.
     * 
     * @param player
     *            the player's inventory being managed.
     */
    public InventoryContainer(Player player) {
        this.player = player;
    }

    /**
     * Sends the items on this inventory to the specified interface.
     * 
     * @param interfaceId
     *            The interface id.
     */
    public void refresh(int interfaceId) {
        checkForZero();
        player.getPacketBuilder().sendUpdateItems(interfaceId,
                container.toArray());
    }

    /**
     * Sends the items on this inventory to the default container.
     */
    public void refresh() {
        refresh(DEFAULT_INVENTORY_CONTAINER_ID);
    }

    /**
     * Adds an item into the inventory's container.
     * 
     * @param item
     *            the item to add to this container.
     */
    public void addItem(Item item) {
        if (item == null) {
            return;
        }

        if (!container.contains(item.getId()) && !item.getDefinition()
                .isStackable()) {
            if (container.getFreeSlot() == -1) {
                player.getPacketBuilder().sendMessage(
                        "You don't have enough space in your inventory!");
                return;
            }
        }

        if (item.getAmount() > container.getRemainingSlots() && !item
                .getDefinition().isStackable()) {
            item.setAmount(container.getRemainingSlots());
        }

        container.add(item);
        refresh();
    }

    /**
     * Adds a set of items into the inventory.
     * 
     * @param item
     *            the set of items to add.
     */
    public void addItemSet(Item[] item) {
        for (Item addItem : item) {
            if (addItem == null) {
                continue;
            }

            addItem(addItem);
        }
    }

    /**
     * Adds a collection of items into the inventory.
     * 
     * @param item
     *            the set of items to add.
     */
    public void addItemCollection(Collection<Item> collection) {
        for (Item addItem : collection) {
            if (addItem == null) {
                continue;
            }

            addItem(addItem);
        }
    }

    /**
     * Overrides the specified slot with a new item.
     * 
     * @param item
     *            the item to override the slot with.
     * @param slot
     *            the slot to override.
     */
    public void overrideItemSlot(Item item, int slot) {
        if (item == null) {
            return;
        }

        container.set(slot, item);
        refresh();
    }

    /**
     * Deletes an item from this inventory's container.
     * 
     * @param item
     *            the item to delete from this container.
     */
    public void deleteItem(Item item) {
        if (item == null || item.getId() == -1 || item.getAmount() < 1) {
            return;
        }

        if (!container.contains(item.getId())) {
            return;
        }

        if (item.getDefinition().isStackable()) {
            container.remove(item);
        } else {
            container.remove(item);
        }

        refresh();
    }

    /**
     * Deletes a set of items from the inventory.
     * 
     * @param item
     *            the set of items to delete.
     */
    public void deleteItemSet(Item[] item) {
        for (Item deleteItem : item) {
            if (deleteItem == null) {
                continue;
            }

            deleteItem(deleteItem);
        }
    }

    /**
     * Replaces an existing item in the inventory with a new one.
     * 
     * @param oldItem
     *            the item to replace.
     * @param newItem
     *            the item to add.
     */
    public void replaceItem(Item oldItem, Item newItem) {
        if (getContainer().getCount(oldItem.getId()) >= oldItem.getAmount()) {
            deleteItem(oldItem);
            addItem(newItem);
        }
    }

    /**
     * Deletes an item from the specified slot in this inventory.
     * 
     * @param item
     *            the item to remove.
     * @param slot
     *            the slot to remove this item from.
     */
    public void deleteItemSlot(Item item, int slot) {
        if (item == null || item.getId() == -1 || item.getAmount() < 1) {
            return;
        }

        if (slot == -1) {
            return;
        }

        if (container.getItem(slot) == null || !container
                .contains(item.getId())) {
            return;
        }

        container.remove(item, slot);
        refresh();
    }

    /**
     * Exchanges the slots of two items in the inventory container.
     * 
     * @param initialSlot
     *            the current slot of the item.
     * @param exchangeSlot
     *            the destination slot for this item.
     */
    public void exchangeItemSlot(int initialSlot, int exchangeSlot) {
        container.swap(initialSlot, exchangeSlot);
        refresh();
    }

    /**
     * Checks if the bank has any items with a value of 0 and removes them if
     * so.
     */
    public void checkForZero() {
        for (int i = 0; i < container.toArray().length; i++) {
            if (container.toArray()[i] == null) {
                continue;
            }
            if (container.toArray()[i].getAmount() < 1) {
                container.toArray()[i] = null;
            }
        }
    }

    /**
     * Gets the backing item container.
     * 
     * @return the backing item container.
     */
    public ItemContainer getContainer() {
        return container;
    }
}