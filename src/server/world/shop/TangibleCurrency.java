package server.world.shop;

import server.world.entity.player.Player;
import server.world.item.Item;

/**
 * Currency that is tangible and that can be held in a player's inventory.
 * 
 * @author lare96
 */
public abstract class TangibleCurrency implements GenericCurrency {

    @Override
    public void giveCurrency(Player player, int amount) {
        player.getInventory().deleteItem(new Item(itemId(), amount));
    }

    @Override
    public void recieveCurrency(Player player, int amount) {
        player.getInventory().addItem(new Item(itemId(), amount));
    }

    @Override
    public int getCurrencyAmount(Player player) {
        return player.getInventory().getContainer().getCount(itemId());
    }

    @Override
    public boolean inventoryFull(Player player) {
        return player.getInventory().getContainer().contains(itemId());
    }

    /**
     * Gets the item id of the currency.
     * 
     * @return the item id of the currency.
     */
    public abstract int itemId();
}
