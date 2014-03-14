package server.world.entity.player.container;

import server.world.entity.player.Player;
import server.world.item.Item;
import server.world.item.ItemContainer;
import server.world.item.ItemContainer.ContainerPolicy;

/**
 * Uses an {@link ItemContainer} to manage items stored in a player's bank.
 * 
 * @author lare96
 * @author Vix
 */
public class BankContainer {

    // TODO: when the player's bank is full, when you click on the item it goes
    // in but inventory doesn't update

    /** The player's bank being managed. */
    private Player player;

    /** The item container that holds and manages all of the items in this bank. */
    private ItemContainer container = new ItemContainer(ContainerPolicy.STACKABLE_POLICY, 250);

    /**
     * Create a new {@link BankContainer}.
     * 
     * @param player
     *        the player's bank being managed.
     */
    public BankContainer(Player player) {
        this.player = player;
    }

    /**
     * Opens and refreshes the banking interface for the player.
     */
    public void open() {
        checkForZero();
        player.getPacketBuilder().sendInventoryInterface(5292, 5063);
        Item[] bankItems = container.toArray();
        Item[] inventoryItems = player.getInventory().getContainer().toArray();
        player.getPacketBuilder().sendUpdateItems(5382, bankItems);
        player.getPacketBuilder().sendUpdateItems(5064, inventoryItems);
    }

    /**
     * Adds an item into the backing {@link ItemContainer} from the player's
     * inventory. This is used for when a player is manually depositing an item.
     * 
     * @param slot
     *        the slot from your inventory.
     * @param item
     *        the item to deposit into your bank.
     */
    public void addItem(int slot, Item item) {

        /** The item you are depositing. */
        Item inventoryItem = player.getInventory().getContainer().getItem(slot);

        /** The amount of this item you have in your inventory. */
        int count = player.getInventory().getContainer().getCount(item.getId());

        /** Gets a free banking slot. */
        int freeBankingSlots = container.freeSlot();

        /** Packet validation check. */
        if (inventoryItem == null || inventoryItem.getId() != item.getId()) {
            return;
        }

        /**
         * If you try and deposit more then you have, it sets the amount to what
         * you have.
         */
        if (item.getAmount() > count) {
            item.setAmount(count);
        }

        /** Block if the bank is full and we don't have this item inside. */
        if (freeBankingSlots == -1 && !container.contains(item.getId())) {
            player.getPacketBuilder().sendMessage("You don't have enough space to deposit this item!");
            return;

            /**
             * Add the item if the bank is full and we already have this item
             * inside.
             */
        } else if (freeBankingSlots == -1 && container.contains(item.getId())) {
            player.getInventory().deleteItemSlot(item, slot);
            container.getItem(container.getSlotById(item.getId())).incrementAmountBy(item.getAmount());
            return;
        }

        /** Changes a noted item into a regular item. */
        int depositItem = item.getDefinition().isNoted() ? item.getDefinition().getUnNotedId() : item.getId();

        /** Either add or stack the item. */
        if (!container.contains(item.getId())) {
            container.add(new Item(depositItem, item.getAmount()));
        } else if (container.contains(item.getId())) {
            container.getItem(container.getSlotById(depositItem)).incrementAmountBy(item.getAmount());
        }

        /** Remove the item from the player's inventory. */
        player.getInventory().deleteItemSlot(item, slot);

        /** Refresh the bank and inventory. */
        checkForZero();
        player.getInventory().refresh(5064);
        Item[] bankItems = container.toArray();
        player.getPacketBuilder().sendUpdateItems(5382, bankItems);
    }

    /**
     * Adds an item into the backing {@link ItemContainer} from the player's
     * inventory. This is used for when the server needs to deposit an item into
     * the player's bank.
     * 
     * @param item
     *        the item to deposit into your bank.
     */
    public void addItem(Item item) {

        /** Gets a free banking slot. */
        int freeBankingSlots = container.freeSlot();

        /** Block if the bank is full and we don't have this item inside. */
        if (freeBankingSlots == -1 && !container.contains(item.getId())) {
            player.getPacketBuilder().sendMessage("You don't have enough space to deposit this item!");
            return;

            /**
             * Add the item if the bank is full and we already have this item
             * inside.
             */
        } else if (freeBankingSlots == -1 && container.contains(item.getId())) {
            container.getItem(container.getSlotById(item.getId())).incrementAmountBy(item.getAmount());
            return;
        }

        /** Changes a noted item into a regular item. */
        int depositItem = item.getDefinition().isNoted() ? item.getDefinition().getUnNotedId() : item.getId();

        /** Either add or stack the item. */
        if (!container.contains(item.getId())) {
            container.add(new Item(depositItem, item.getAmount()));
        } else if (container.contains(item.getId())) {
            container.getItem(container.getSlotById(depositItem)).incrementAmountBy(item.getAmount());
        }
    }

    /**
     * Removes an item from the backing {@link ItemContainer} into the player's
     * inventory. This is used for when a player is manually depositing an item.
     * 
     * @param slot
     *        the slot from your bank.
     * @param item
     *        the item to withdraw from your bank.
     */
    public void deleteItem(int slot, Item item) {

        /** Gets if the requested item is noted. */
        boolean withdrawItemNoted = item.getDefinition().isNoteable();

        /** Gets how many of the requested item is already in the bank. */
        int withdrawAmount = container.getCount(item.getId());

        /** Packet validation check. */
        if (item.getAmount() < 1 || item.getId() < 0 || !container.contains(item.getId())) {
            return;
        }

        /**
         * If we are withdrawing more than we have, set the amount to what we
         * have.
         */
        if (item.getAmount() > withdrawAmount) {
            item.setAmount(withdrawAmount);
        }

        /**
         * If we are withdrawing more than the free slots we have.
         */
        if (!item.getDefinition().isStackable()) {
            if (item.getAmount() > player.getInventory().getContainer().freeSlots() && !item.getDefinition().isStackable()) {
                item.setAmount(player.getInventory().getContainer().freeSlots());
            }
        }

        /** Check the inventory space. */
        if (!item.getDefinition().isStackable()) {
            if (player.getInventory().getContainer().freeSlots() < item.getAmount()) {
                player.getPacketBuilder().sendMessage("You do not have enough space in your inventory!");
                return;
            }
        } else {
            if (player.getInventory().getContainer().freeSlots() < 1) {
                player.getPacketBuilder().sendMessage("You do not have enough space in your inventory!");
                return;
            }
        }

        /** Check if the item can be withdrawn as a note. */
        if (player.isWithdrawAsNote() && !withdrawItemNoted) {
            player.getPacketBuilder().sendMessage("This item can't be withdrawn as a note.");
            return;
        }

        /** Withdraw the item. */
        if (!player.isWithdrawAsNote()) {
            player.getInventory().addItem(new Item(item.getId(), item.getAmount()));
        } else if (player.isWithdrawAsNote()) {
            player.getInventory().addItem(new Item(item.getId() + 1, item.getAmount()));
        }

        /** Remove the item from the player's bank. */
        container.remove(new Item(item.getId(), item.getAmount()), slot);

        /** Refresh the bank. */
        checkForZero();
        container.compact();
        Item[] bankItems = container.toArray();
        player.getInventory().refresh(5064);
        player.getPacketBuilder().sendUpdateItems(5382, bankItems);
    }

    /**
     * Removes an item from the backing {@link ItemContainer} into the player's
     * inventory. This is used for when the server needs to deposit an item into
     * the player's bank.
     * 
     * @param item
     *        the item to withdraw from your bank.
     */
    public void deleteItem(Item item) {

        /** Gets how many of the requested item is already in the bank. */
        int withdrawAmount = container.getCount(item.getId());

        /** Block if we are trying to withdraw a non-existing item. */
        if (!container.contains(item.getId())) {
            return;
        }

        /**
         * If we are withdrawing more than we have, set the amount to what we
         * have.
         */
        if (item.getAmount() > withdrawAmount) {
            item.setAmount(withdrawAmount);
        }

        /** Remove the item. */
        container.remove(new Item(item.getId(), item.getAmount()));
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
     * Gets the item container that holds and manages all of the items in this
     * bank.
     * 
     * @return the backing item container.
     */
    public ItemContainer getContainer() {
        return container;
    }
}