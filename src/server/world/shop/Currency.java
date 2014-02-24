package server.world.shop;

/**
 * All of the different types of tangible currencies that can be used in shops.
 * 
 * @author lare96
 */
public enum Currency {

    /** The default currency - coins. */
    COINS(995),

    /** Usually used in TzHaar shops. */
    TOKKUL(6529);

    /** The item id of this currency. */
    private int itemId;

    /**
     * Construct a new {@link Currency}.
     * 
     * @param itemId
     *        the item id of this currency.
     */
    Currency(int itemId) {
        this.itemId = itemId;
    }

    /**
     * Gets the item id of this currency.
     * 
     * @return the item id.
     */
    public int getItemId() {
        return itemId;
    }
}
