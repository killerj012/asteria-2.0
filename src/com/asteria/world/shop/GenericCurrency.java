package com.asteria.world.shop;

import com.asteria.world.entity.player.Player;

/**
 * An interface that represents any generic {@link Currency} that can be used
 * with shops.
 * 
 * @author lare96
 */
public interface GenericCurrency {

    /**
     * Fired when {@link Currency} is being taken away from the {@link Player}.
     * 
     * @param player
     *            the player the currency is being taken from.
     * @param amount
     *            the amount being taken.
     */
    public void give(Player player, int amount);

    /**
     * Fired when {@link Currency} is being given to the {@link Player}.
     * 
     * @param player
     *            the player the currency is being given to.
     * @param amount
     *            the amount being given.
     */
    public void recieve(Player player, int amount);

    /**
     * Gets the amount of this {@link Currency} the {@link Player} has.
     * 
     * @param player
     *            the player the currency is being calculated for.
     * @return the amount of this currency the player has.
     */
    public int getAmount(Player player);

    /**
     * Fired when {@link Currency} is being given to the {@link Player} on a
     * full inventory.
     * 
     * @param player
     *            the player the currency is being given to.
     * @return <code>true</code> if the currency can be added to the full
     *         inventory, <code>false</code> if it cannot.
     */
    public boolean inventoryFull(Player player);
}
