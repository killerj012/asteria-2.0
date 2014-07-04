package server.world.shop;

import server.world.entity.player.Player;

/**
 * Represents any type of currency that can be used to buy items from shops.
 * 
 * @author lare96
 */
public interface GenericCurrency {

    /**
     * Fired when currency is being taken away from the player.
     * 
     * @param player
     *            the player the currency is being taken from.
     * @param amount
     *            the amount being taken.
     */
    public void giveCurrency(Player player, int amount);

    /**
     * Fired when currency is being given to the player.
     * 
     * @param player
     *            the player the currency is being given to.
     * @param amount
     *            the amount being given.
     */
    public void recieveCurrency(Player player, int amount);

    /**
     * Gets the amount of this currency the player has.
     * 
     * @param player
     *            the player the currency is being calculated for.
     * @return the amount of this currency the player has.
     */
    public int getCurrencyAmount(Player player);

    /**
     * Fired when currency is being given to the player on a full inventory.
     * 
     * @param player
     *            the player the currency is being given to.
     * @return true if the currency can be added to the full inventory.
     */
    public boolean inventoryFull(Player player);
}
