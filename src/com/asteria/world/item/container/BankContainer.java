package com.asteria.world.item.container;

import com.asteria.world.entity.player.Player;
import com.asteria.world.item.Item;
import com.asteria.world.item.ItemContainer;
import com.asteria.world.item.ItemContainer.ContainerPolicy;

/**
 * Uses an {@link ItemContainer} to manage items stored in a player's bank.
 * 
 * @author lare96
 * @author Vix
 */
public class BankContainer {

    /** The player's bank being managed. */
    private Player player;

    /** The item container that holds all of the items in this bank. */
    private ItemContainer container = new ItemContainer(
            ContainerPolicy.STACKABLE_POLICY, 250);

    /**
     * Create a new {@link BankContainer}.
     * 
     * @param player
     *            the player's bank being managed.
     */
    public BankContainer(Player player) {
        this.player = player;
    }

    /**
     * Opens and refreshes the banking interface for the player.
     */
    public void open() {
        player.setWithdrawAsNote(false);
        player.getPacketBuilder().sendConfig(115, 0);
        player.getPacketBuilder().sendInventoryInterface(5292, 5063);
        refresh();
        player.getPacketBuilder().sendUpdateItems(
                InventoryContainer.BANK_INVENTORY_CONTAINER_ID,
                player.getInventory().getContainer().toArray());
    }

    /**
     * Adds an item into the backing {@link ItemContainer} from the player's
     * inventory. This is used for when a player is manually depositing an item.
     * 
     * @param slot
     *            the slot from your inventory.
     * @param item
     *            the item to deposit into your bank.
     */
    public void addItem(int slot, Item item) {
        Item inventoryItem = player.getInventory().getContainer().getItem(slot);
        int count = player.getInventory().getContainer().getCount(item.getId());
        int freeBankingSlots = container.getFreeSlot();

        if (inventoryItem == null || inventoryItem.getId() != item.getId()) {
            return;
        }

        if (item.getAmount() > count) {
            item.setAmount(count);
        }

        if (freeBankingSlots == -1 && !container.contains(item.getId())) {
            player.getPacketBuilder().sendMessage(
                    "You don't have enough space to deposit this item!");
            return;
        } else if (freeBankingSlots == -1 && container.contains(item.getId())) {
            player.getInventory().deleteItemSlot(item, slot);
            container.getItem(container.getSlotById(item.getId()))
                    .incrementAmountBy(item.getAmount());
            checkForZero();
            player.getInventory().refresh(
                    InventoryContainer.BANK_INVENTORY_CONTAINER_ID);
            Item[] bankItems = container.toArray();
            player.getPacketBuilder().sendUpdateItems(5382, bankItems);
            return;
        }

        int depositItem = item.getDefinition().isNoted() ? item.getDefinition()
                .getUnNotedId() : item.getId();

        if (!container.contains(item.getId())) {
            container.add(new Item(depositItem, item.getAmount()));
        } else if (container.contains(item.getId())) {
            container.getItem(container.getSlotById(depositItem))
                    .incrementAmountBy(item.getAmount());
        }

        player.getInventory().deleteItemSlot(item, slot);

        checkForZero();
        player.getInventory().refresh(
                InventoryContainer.BANK_INVENTORY_CONTAINER_ID);
        Item[] bankItems = container.toArray();
        player.getPacketBuilder().sendUpdateItems(5382, bankItems);
    }

    /**
     * Adds an item into the backing {@link ItemContainer} from the player's
     * inventory. This is used for when the server needs to deposit an item into
     * the player's bank.
     * 
     * @param item
     *            the item to deposit into your bank.
     */
    public void addItem(Item item) {
        int freeBankingSlots = container.getFreeSlot();

        if (freeBankingSlots == -1 && !container.contains(item.getId())) {
            player.getPacketBuilder().sendMessage(
                    "You don't have enough space to deposit this item!");
            return;

        } else if (freeBankingSlots == -1 && container.contains(item.getId())) {
            container.getItem(container.getSlotById(item.getId()))
                    .incrementAmountBy(item.getAmount());
            checkForZero();
            player.getInventory().refresh(
                    InventoryContainer.BANK_INVENTORY_CONTAINER_ID);
            Item[] bankItems = container.toArray();
            player.getPacketBuilder().sendUpdateItems(5382, bankItems);
            return;
        }

        int depositItem = item.getDefinition().isNoted() ? item.getDefinition()
                .getUnNotedId() : item.getId();

        if (!container.contains(item.getId())) {
            container.add(new Item(depositItem, item.getAmount()));
        } else if (container.contains(item.getId())) {
            container.getItem(container.getSlotById(depositItem))
                    .incrementAmountBy(item.getAmount());
        }
    }

    /**
     * Removes an item from the backing {@link ItemContainer} into the player's
     * inventory. This is used for when a player is manually depositing an item.
     * 
     * @param slot
     *            the slot from your bank.
     * @param item
     *            the item to withdraw from your bank.
     */
    public void deleteItem(int slot, Item item) {

        boolean withdrawItemNoted = item.getDefinition().isNoteable();
        int withdrawAmount = container.getCount(item.getId());

        if (item.getAmount() < 1 || item.getId() < 0 || !container
                .contains(item.getId())) {
            return;
        }

        if (item.getAmount() > withdrawAmount) {
            item.setAmount(withdrawAmount);
        }

        if (item.getAmount() > player.getInventory().getContainer()
                .getRemainingSlots() && !item.getDefinition().isStackable() && !player
                .isWithdrawAsNote()) {
            item.setAmount(player.getInventory().getContainer()
                    .getRemainingSlots());
        }

        if (!item.getDefinition().isStackable() && !item.getDefinition()
                .isNoted() && !player.isWithdrawAsNote()) {
            if (player.getInventory().getContainer().getRemainingSlots() < item
                    .getAmount()) {
                player.getPacketBuilder().sendMessage(
                        "You do not have enough space in your inventory!");
                return;
            }
        } else {
            if (player.getInventory().getContainer().getRemainingSlots() < 1 && !player
                    .getInventory()
                    .getContainer()
                    .contains(
                            !player.isWithdrawAsNote() ? item.getId() : item
                                    .getId() + 1)) {
                player.getPacketBuilder().sendMessage(
                        "You do not have enough space in your inventory!");
                return;
            }
        }

        if (player.isWithdrawAsNote() && !withdrawItemNoted) {
            player.getPacketBuilder().sendMessage(
                    "This item can't be withdrawn as a note.");
            player.setWithdrawAsNote(false);
            player.getPacketBuilder().sendConfig(115, 0);
        }

        if (!player.isWithdrawAsNote()) {
            player.getInventory().addItem(
                    new Item(item.getId(), item.getAmount()));
        } else if (player.isWithdrawAsNote()) {
            player.getInventory().addItem(
                    new Item(item.getId() + 1, item.getAmount()));
        }

        container.remove(new Item(item.getId(), item.getAmount()), slot);

        checkForZero();
        container.compact();
        Item[] bankItems = container.toArray();
        player.getInventory().refresh(
                InventoryContainer.BANK_INVENTORY_CONTAINER_ID);
        player.getPacketBuilder().sendUpdateItems(5382, bankItems);
    }

    /**
     * Removes an item from the backing {@link ItemContainer} into the player's
     * inventory. This is used for when the server needs to deposit an item into
     * the player's bank.
     * 
     * @param item
     *            the item to withdraw from your bank.
     */
    public void deleteItem(Item item) {

        int withdrawAmount = container.getCount(item.getId());

        if (!container.contains(item.getId())) {
            return;
        }

        if (item.getAmount() > withdrawAmount) {
            item.setAmount(withdrawAmount);
        }

        container.remove(new Item(item.getId(), item.getAmount()));
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
     * Refreshes the banking interface.
     */
    public void refresh() {
        checkForZero();
        player.getPacketBuilder().sendUpdateItems(5382, container.toArray());
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