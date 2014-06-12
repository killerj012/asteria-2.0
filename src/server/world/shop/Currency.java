package server.world.shop;

import server.world.entity.player.Player;

/**
 * All of the different types of tangible currencies that can be used in shops.
 * 
 * @author lare96
 */
public enum Currency {

    /** The default currency. */
    COINS(new TangibleCurrency() {
        @Override
        public int itemId() {
            return 995;
        }
    }),

    /** Usually used in TzHaar shops. */
    TOKKUL(new TangibleCurrency() {
        @Override
        public int itemId() {
            return 6529;
        }
    }),

    /** An example of how points can be done. */
    POINTS(new GenericCurrency() {
        @Override
        public void giveCurrency(Player player, int amount) {
            player.setExamplePoints(player.getExamplePoints() - amount);
        }

        @Override
        public void recieveCurrency(Player player, int amount) {
            player.setExamplePoints(player.getExamplePoints() + amount);
        }

        @Override
        public int getCurrencyAmount(Player player) {
            return player.getExamplePoints();
        }

        @Override
        public boolean inventoryFull(Player player) {
            return true;
        }
    });

    /** Holds the functions for this particular currency. */
    private GenericCurrency currency;

    /**
     * Create a new {@link Currency}.
     * 
     * @param currency
     *        holds the functions for this particular currency.
     */
    private Currency(GenericCurrency currency) {
        this.currency = currency;
    }

    /**
     * Gets the instance that holds the functions for this particular currency.
     * 
     * @return the instance that holds the functions for this particular
     *         currency.
     */
    public GenericCurrency getCurrency() {
        return currency;
    }
}
