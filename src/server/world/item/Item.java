package server.world.item;

import java.util.Comparator;

/**
 * An in-game item that can be placed on miscellaneous interfaces using a
 * packet.
 * 
 * @author lare96
 */
public class Item implements Comparator<Item> {

    /** The item instance used to set the comparator. */
    public static final Item COMPARATOR = new Item(0, 0);

    /** The id of the item. */
    private int id;

    /** The amount of the item. */
    private int amount;

    /**
     * Create a new {@link Item} with the specified amount.
     * 
     * @param id
     *        the id of the item.
     * @param amount
     *        the amount of the item.
     */
    public Item(int id, int amount) {
        this.setId(id);
        this.setAmount(amount);
    }

    /**
     * Create a new {@link Item} with the specified amount as 1.
     * 
     * @param id
     *        the id of the item.
     */
    public Item(int id) {
        this.setId(id);
        this.setAmount(1);
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
        String name = getDefinition() == null ? "null" : getDefinition().getItemName();
        return "ITEM[item= " + id + ", amount= " + amount + ", name= " + name + "]";
    }

    /**
     * Gets the id of the item.
     * 
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the id of the item.
     * 
     * @param id
     *        the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Gets the amount of the item.
     * 
     * @return the amount
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Sets the amount of the item.
     * 
     * @param amount
     *        the amount to set
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
     * Increment the amount by the specified amount.
     */
    public void incrementAmountBy(int amount) {
        if ((this.amount + amount) > Integer.MAX_VALUE) {
            this.amount = Integer.MAX_VALUE;
        } else {
            this.amount += amount;
        }
    }

    /**
     * Decrement the amount by the specified amount.
     */
    public void decrementAmountBy(int amount) {
        if ((this.amount - amount) < 1) {
            this.amount = 0;
        } else {
            this.amount -= amount;
        }
    }

    /**
     * Gets this item definition.
     * 
     * @param id
     *        the item definition to get.
     * @return the definition.
     */
    public ItemDefinition getDefinition() {
        return ItemDefinition.getDefinitions()[id];
    }

    @Override
    public int compare(Item o1, Item o2) {
        if (o1 == null || o2 == null) {
            return -1;
        }

        if (o1.getDefinition().getGeneralStorePrice() > o2.getDefinition().getGeneralStorePrice()) {
            return 1;
        } else if (o1.getDefinition().getGeneralStorePrice() < o2.getDefinition().getGeneralStorePrice()) {
            return -1;
        }
        return 0;
    }
}