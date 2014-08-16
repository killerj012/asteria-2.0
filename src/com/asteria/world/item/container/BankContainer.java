package com.asteria.world.item.container;

import java.util.Collection;

import com.asteria.world.entity.player.Player;
import com.asteria.world.item.Item;
import com.asteria.world.item.ItemContainer;

/**
 * An {@link ItemContainer} implementation that will manage items stored in a
 * player's bank.
 * 
 * @author lare96
 */
public class BankContainer extends ItemContainer {

    /** The player's bank being managed. */
    private Player player;

    /**
     * Create a new {@link BankContainer}.
     * 
     * @param player
     *            the player's bank being managed.
     */
    public BankContainer(Player player) {
        super(Policy.STACK_ALWAYS, 250);
        this.player = player;
    }

    /** Opens and refreshes the banking interfaces. */
    public void open() {
        player.setWithdrawAsNote(false);
        player.getPacketBuilder().sendConfig(115, 0);
        player.getPacketBuilder().sendInventoryInterface(5292, 5063);
        refresh();
        player.getPacketBuilder().sendUpdateItems(5064,
            player.getInventory().toArray());
    }

    /** Refreshes the contents of this container to the banking interface. */
    public void refresh() {
        refresh(5382, player);
    }

    /**
     * Deposits an item to the container that currently exists in the player's
     * inventory. This is used for when a player is manually depositing an item
     * using the banking interface.
     * 
     * @param inventorySlot
     *            the slot from the player's inventory.
     * @param amount
     *            the amount of the item being deposited.
     * @return <code>true</code> if the container was modified as a result of
     *         the call, <code>false</code> otherwise.
     */
    public boolean depositFromInventory(int inventorySlot, int amount) {
        Item item = new Item(player.getInventory().get(inventorySlot).getId(),
            amount);
        int count = player.getInventory().totalAmount(item.getId());

        if (item.getAmount() > count) {
            item.setAmount(count);
        }

        if (deposit(item)) {
            player.getInventory().remove(item, inventorySlot);
            refresh();
            player.getPacketBuilder().sendUpdateItems(5064,
                player.getInventory().toArray());
            return true;
        }
        return false;
    }

    /**
     * Deposits an item to the container. This is used for when the server needs
     * to deposit an item into the player's bank.
     * 
     * @param item
     *            the item to deposit into the player's bank.
     * @return <code>true</code> if the container was modified as a result of
     *         the call, <code>false</code> otherwise.
     */
    public boolean deposit(Item item) {
        int slot = getFreeSlot();
        boolean contains = contains(item.getId());

        if (slot == -1 && !contains) {
            player.getPacketBuilder().sendMessage(
                "You don't have enough space to deposit this item!");
            return false;
        }

        item.setId(item.getDefinition().isNoted() ? item.getDefinition()
            .getUnNotedId() : item.getId());

        if (!contains) {
            super.add(item, slot);
        } else {
            get(getSlot(item.getId())).incrementAmountBy(item.getAmount());
        }
        return true;
    }

    /**
     * Withdraws an item from the container that currently exists in the
     * player's bank.
     * 
     * @param bankSlot
     *            the slot from the player's bank.
     * @param amount
     *            the amount of the item being withdrawn.
     * @param addItem
     *            if the item should be added back into the player's inventory
     *            after being withdrawn.
     * @return <code>true</code> if the container was modified as a result of
     *         the call, <code>false</code> otherwise.
     */
    public boolean withdraw(int bankSlot, int amount, boolean addItem) {

        Item item = new Item(get(bankSlot).getId(), amount);
        boolean withdrawItemNoted = item.getDefinition().isNoteable();
        int withdrawAmount = totalAmount(item.getId());

        if (isSlotFree(bankSlot)) {
            return false;
        }

        if (item.getAmount() > withdrawAmount) {
            item.setAmount(withdrawAmount);
        }

        if (item.getAmount() > player.getInventory().getRemainingSlots() && !item
            .getDefinition().isStackable() && !player.isWithdrawAsNote()) {
            item.setAmount(player.getInventory().getRemainingSlots());
        }

        if (!item.getDefinition().isStackable() && !item.getDefinition()
            .isNoted() && !player.isWithdrawAsNote()) {
            if (player.getInventory().getRemainingSlots() < item.getAmount()) {
                player.getPacketBuilder().sendMessage(
                    "You do not have enough space in your inventory!");
                return false;
            }
        } else {
            if (player.getInventory().getRemainingSlots() < 1 && !player
                .getInventory().contains(
                    !player.isWithdrawAsNote() ? item.getId()
                        : item.getId() + 1)) {
                player.getPacketBuilder().sendMessage(
                    "You do not have enough space in your inventory!");
                return false;
            }
        }

        if (player.isWithdrawAsNote() && !withdrawItemNoted) {
            player.getPacketBuilder().sendMessage(
                "This item can't be withdrawn as a note.");
            player.setWithdrawAsNote(false);
            player.getPacketBuilder().sendConfig(115, 0);
        }

        if (player.isWithdrawAsNote()) {
            item.setId(item.getId() + 1);
        }

        if (addItem)
            player.getInventory().add(item);

        super.remove(item, bankSlot);
        shift();
        refresh();
        player.getPacketBuilder().sendUpdateItems(5064,
            player.getInventory().toArray());
        return true;
    }

    /**
     * Withdraws an item from the container that currently exists in the
     * player's bank.
     * 
     * @param item
     *            the item to withdraw.
     * @param addItem
     *            if the item should be added back into the player's inventory
     *            after being withdrawn.
     * @return <code>true</code> if the container was modified as a result of
     *         the call, <code>false</code> otherwise.
     */
    public boolean withdraw(Item item, boolean addItem) {
        return withdraw(getSlot(item.getId()), item.getAmount(), addItem);
    }

    /**
     * This method is not supported by this container implementation. It will
     * always throw an {@link UnsupportedOperationException}.
     */
    @Override
    public final boolean add(Item item, int slot) {
        throw new UnsupportedOperationException(
            "This method is not supported by this container implementation.");
    }

    /**
     * This method is not supported by this container implementation. It will
     * always throw an {@link UnsupportedOperationException}.
     */
    @Override
    public final boolean add(Item item) {
        throw new UnsupportedOperationException(
            "This method is not supported by this container implementation.");
    }

    /**
     * This method is not supported by this container implementation. It will
     * always throw an {@link UnsupportedOperationException}.
     */
    @Override
    public final boolean addAll(Collection<? extends Item> c) {
        throw new UnsupportedOperationException(
            "This method is not supported by this container implementation.");
    }

    /**
     * This method is not supported by this container implementation. It will
     * always throw an {@link UnsupportedOperationException}.
     */
    @Override
    public final boolean remove(Item item, int slot) {
        throw new UnsupportedOperationException(
            "This method is not supported by this container implementation.");
    }

    /**
     * This method is not supported by this container implementation. It will
     * always throw an {@link UnsupportedOperationException}.
     */
    @Override
    public final boolean remove(Item item) {
        throw new UnsupportedOperationException(
            "This method is not supported by this container implementation.");
    }

    /**
     * This method is not supported by this container implementation. It will
     * always throw an {@link UnsupportedOperationException}.
     */
    @Override
    public final boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException(
            "This method is not supported by this container implementation.");
    }
}