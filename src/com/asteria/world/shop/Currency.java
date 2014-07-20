package com.asteria.world.shop;

import com.asteria.world.entity.player.Player;

/**
 * All of the currencies that can be used with shops.
 * 
 * @author lare96
 */
public enum Currency {

    COINS(new ItemCurrency() {
        @Override
        public int item() {
            return 995;
        }
    }),
    TOKKUL(new ItemCurrency() {
        @Override
        public int item() {
            return 6529;
        }
    }),
    POINTS(new PointCurrency() {
        @Override
        public void give(Player player, int amount) {
            player.setExamplePoints(player.getExamplePoints() - amount);
        }

        @Override
        public void recieve(Player player, int amount) {
            player.setExamplePoints(player.getExamplePoints() + amount);
        }

        @Override
        public int getAmount(Player player) {
            return player.getExamplePoints();
        }
    });

    /** The currency function. */
    private GenericCurrency currency;

    /**
     * Create a new {@link Currency}.
     * 
     * @param currency
     *            the currency function.
     */
    private Currency(GenericCurrency currency) {
        this.currency = currency;
    }

    /**
     * Gets the currency function.
     * 
     * @return the currency function.
     */
    public GenericCurrency getCurrency() {
        return currency;
    }
}
