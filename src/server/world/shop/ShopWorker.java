package server.world.shop;

import server.core.Rs2Engine;
import server.core.worker.Worker;
import server.world.entity.player.Player;
import server.world.item.Item;

public class ShopWorker extends Worker {

    private Shop shop;

    public ShopWorker(Shop shop) {
        super(9, false);
        this.shop = shop;
    }

    @Override
    public void fire() {
        if (shop.atOriginalAmounts() || !shop.isReplenishStock()) {
            this.cancel();
            return;
        }

        for (Item item : shop.getShopContainer().toArray()) {
            if (item == null) {
                continue;
            }
            if (item.getAmount() < shop.getOriginalAmount(item.getId())) {
                item.incrementAmount();

                for (Player player : Rs2Engine.getWorld().getPlayers()) {
                    if (player == null) {
                        continue;
                    }

                    if (player.getOpenShopId() == shop.getId()) {
                        shop.updateShopItems(player);
                    }
                }
            }
        }
    }
}
