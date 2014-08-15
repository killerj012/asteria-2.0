package com.asteria.world.item;

import com.asteria.world.World;

/**
 * A {@link World} item that can be interacted with by various {@link Entity}s.
 * 
 * @author lare96
 */
public class Item {

    /** The id of the item. */
    private int id;

    /** The amount of the item. */
    private int amount;

    /**
     * Create a new {@link Item}.
     * 
     * @param id
     *            the id of the item.
     * @param amount
     *            the amount of the item.
     */
    public Item(int id, int amount) {
        this.id = id;
        this.amount = amount;
    }

    /**
     * Create a new {@link Item} with the amount as 1.
     * 
     * @param id
     *            the id of the item.
     */
    public Item(int id) {
        this(id, 1);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Item) {
            Item item = (Item) obj;

            if (id == item.id && amount == item.amount) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        String defintion = getDefinition() == null ? "null" : getDefinition()
                .getItemName();
        return "ITEM[item= " + id + ", amount= " + amount + ", name= " + defintion + "]";
    }

    @Override
    public Item clone() {
        return new Item(id, amount);
    }

    /**
     * Gets the id of the item.
     * 
     * @return the id of the item.
     */
    public int getId() {
        return id;

    }

    /**
     * Sets the id of the item.
     * 
     * @param id
     *            the id of the item.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the amount of the item.
     * 
     * @return the amount of the item.
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Sets the amount of the item.
     * 
     * @param amount
     *            the amount of the item.
     */
    public void setAmount(int amount) {
        this.amount = amount;
    }

    /**
     * Increment the amount by 1.
     */
    public void incrementAmount() {
        if ((amount + 1) > Integer.MAX_VALUE) {
            return;
        }
        amount++;
    }

    /**
     * Decrement the amount by 1.
     */
    public void decrementAmount() {
        if ((amount - 1) < 0) {
            return;
        }
        amount--;
    }

    /**
     * Increment the amount by the argued amount.
     * 
     * @param amount
     *            the amount to increment by.
     */
    public void incrementAmountBy(int amount) {
        if ((this.amount + amount) > Integer.MAX_VALUE) {
            this.amount = Integer.MAX_VALUE;
        } else {
            this.amount += amount;
        }
    }

    /**
     * Decrement the amount by the argued amount.
     * 
     * @param amount
     *            the amount to decrement by.
     */
    public void decrementAmountBy(int amount) {
        if ((this.amount - amount) < 1) {
            this.amount = 0;
        } else {
            this.amount -= amount;
        }
    }

    /**
     * Gets the item definition for this item.
     * 
     * @return the item definition for this item.
     */
    public ItemDefinition getDefinition() {
        return ItemDefinition.getDefinitions()[id];
    }
}