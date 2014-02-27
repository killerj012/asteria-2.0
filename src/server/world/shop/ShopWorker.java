package server.world.shop;

import server.core.Rs2Engine;
import server.core.worker.Worker;
import server.world.entity.player.Player;
import server.world.item.Item;

/**
 * A worker that will restock a {@link Shop} once it has run out of at least one
 * item to sell.
 * 
 * @author lare96
 */
public class ShopWorker extends Worker {

    /** The shop we are restocking. */
    private Shop shop;

    /**
     * Create a new {@link ShopWorker}.
     * 
     * @param shop
     *        the shop we are restocking.
     */
    public ShopWorker(Shop shop) {
        super(9, false);
        this.shop = shop;
    }

    @Override
    public void fire() {

        /**
         * If the stop is fully restocked or isn't a restockable shop then we
         * cancel this worker.
         */
        if (shop.isFullyRestocked() || !shop.isRestockItems()) {
            this.cancel();
            return;
        }

        /** Iterate through the shops items. */
        for (Item item : shop.getShopContainer().toArray()) {

            /** Here we skip malformed items. */
            if (item == null) {
                continue;
            }

            /** If this item is not at its original amount... */
            if (shop.getShopMap().containsKey(item.getId())) {
                if (item.getAmount() < shop.getShopMap().get(item.getId())) {

                    /** Increment the item's amount by 1. */
                    item.incrementAmount();

                    /** And update it for every player viewing that shop! */
                    for (Player player : Rs2Engine.getWorld().getPlayers()) {
                        if (player == null) {
                            continue;
                        }

                        if (player.getOpenShopId() == shop.getIndex()) {
                            shop.updateShopItems(player);
                        }
                    }
                }
            }
        }
    }
}
