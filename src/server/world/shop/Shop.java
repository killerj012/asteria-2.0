package server.world.shop;

import java.util.HashMap;
import java.util.Map;

import server.core.net.packet.PacketBuffer;
import server.core.worker.TaskFactory;
import server.core.worker.Worker;
import server.util.Misc;
import server.world.World;
import server.world.entity.player.Player;
import server.world.item.Item;
import server.world.item.ItemContainer;
import server.world.item.ItemContainer.ContainerPolicy;

/**
 * A shop from which a {@link Player} can buy items at.
 * 
 * @author lare96
 */
public class Shop {

    /** A primitive array of registered shops. */
    private static Shop[] shops = new Shop[10];

    /** The index of this shop. */
    private int index;

    /** The name of this shop. */
    private String name;

    /** An {@link ItemContainer} that holds the items within this shop. */
    private ItemContainer container = new ItemContainer(
            ContainerPolicy.STACKABLE_POLICY, 48);

    /** A map of the original shop items and their amounts. */
    private Map<Integer, Integer> shopMap = new HashMap<Integer, Integer>(
            container.capacity());

    /** If the shop will replenish its stock once it runs out. */
    private boolean restockItems;

    /** If a {@link Player} is able to sell items to this shop. */
    private boolean sellItems;

    /** The currency this shop is using. */
    private Currency currency;

    /** A {@link Worker} that will restock this shop. */
    private Worker processor;

    /**
     * Create a new {@link Shop}.
     * 
     * @param index
     *            the index of this shop.
     * @param name
     *            the name of this shop.
     * @param items
     *            the items in this shop.
     * @param restockItems
     *            if the shop will replenish its stock once it runs out.
     * @param sellItems
     *            if a {@link Player} is able to sell items to this shop.
     * @param currency
     *            the currency this shop is using.
     */
    public Shop(int index, String name, Item[] items, boolean restockItems,
            boolean sellItems, Currency currency) {
        this.index = index;
        this.name = name;
        this.container.setItems(items);
        this.restockItems = restockItems;
        this.sellItems = sellItems;
        this.currency = currency;

        for (Item item : items) {
            if (item == null) {
                continue;
            }

            shopMap.put(item.getId(), item.getAmount());
        }
    }

    /**
     * Open this shop for the specified {@link Player}.
     * 
     * @param player
     *            the player to open the shop for.
     */
    public void openShop(Player player) {
        player.getPacketBuilder().sendUpdateItems(3823,
                player.getInventory().getContainer().toArray());
        updateShopItems(player);
        player.setOpenShopId(index);
        player.getPacketBuilder().sendInventoryInterface(3824, 3822);
        player.getPacketBuilder().sendString(name, 3901);
    }

    /**
     * Updates the images of the {@link Item}s in this shop.
     * 
     * @param player
     *            the player to update the images of the items for.
     */
    protected void updateShopItems(Player player) {
        PacketBuffer.WriteBuffer out = PacketBuffer.newWriteBuffer(2048);
        out.writeVariableShortPacketHeader(53);
        out.writeShort(3900);
        out.writeShort(getShopItemAmount());

        for (Item item : container.toArray()) {
            if (item == null) {
                continue;
            }

            if (item.getId() > 0) {
                if (item.getAmount() > 254) {
                    out.writeByte(255);
                    out.writeInt(item.getAmount(),
                            PacketBuffer.ByteOrder.INVERSE_MIDDLE);
                } else {
                    out.writeByte(item.getAmount());
                }

                out.writeShort(item.getId() + 1, PacketBuffer.ValueType.A,
                        PacketBuffer.ByteOrder.LITTLE);
            }
        }

        out.finishVariableShortPacketHeader();
        player.getSession().encode(out);
    }

    /**
     * Purchases an {@link Item} from this shop for the specified {@link Player}
     * .
     * 
     * @param player
     *            the player purchasing the item.
     * @param item
     *            the item being purchased.
     */
    public void purchaseItem(Player player, Item item) {

        /** Check if the item you are buying is at 0 stock. */
        if (item.getAmount() == 0) {
            player.getPacketBuilder().sendMessage(
                    "There is none of this item left in stock!");
            return;
        }

        /**
         * Check if the shop even contains the item you're trying to buy.
         */
        if (!container.contains(item.getId())) {
            return;
        }

        /**
         * Check if the player has the required amount of the currency needed to
         * buy this item.
         */
        if (currency == Currency.COINS) {
            if (!(currency.getCurrency().getCurrencyAmount(player) >= (item
                    .getDefinition().getGeneralStorePrice() * item.getAmount()))) {
                player.getPacketBuilder().sendMessage(
                        "You do not have enough coins to buy this item.");
                return;
            }
        } else {
            if (!(currency.getCurrency().getCurrencyAmount(player) >= (item
                    .getDefinition().getSpecialStorePrice() * item.getAmount()))) {
                player.getPacketBuilder().sendMessage(
                        "You do not have enough "
                                + currency.name().toLowerCase()
                                        .replaceAll("_", " ")
                                + " to buy this item.");
                return;
            }
        }

        /**
         * If you are buying more than the shop has in stock, set the amount you
         * are buying to how much is in stock.
         */
        if (item.getAmount() > container.getCount(item.getId())) {
            item.setAmount(container.getCount(item.getId()));
        }

        /**
         * Set the amount you are buying to your current amount of free slots if
         * you do not have enough room for the item you are trying to buy.
         */
        if (!player.getInventory().getContainer().hasRoomFor(item)) {
            item.setAmount(player.getInventory().getContainer().freeSlots());

            if (item.getAmount() == 0) {
                player.getPacketBuilder()
                        .sendMessage(
                                "You do not have enough space in your inventory to buy this item!");
                return;
            }
        }

        /** Here we actually buy the item. */
        if (player.getInventory().getContainer().freeSlots() >= item
                .getAmount()
                && !item.getDefinition().isStackable()
                || player.getInventory().getContainer().freeSlots() >= 1
                && item.getDefinition().isStackable()) {

            if (shopMap.containsKey(item.getId())) {
                container.getById(item.getId()).decrementAmountBy(
                        item.getAmount());
            } else if (!shopMap.containsKey(item.getId())) {
                container.remove(item);
            }

            if (currency == Currency.COINS) {
                currency.getCurrency().giveCurrency(
                        player,
                        item.getAmount()
                                * item.getDefinition().getGeneralStorePrice());
            } else {
                currency.getCurrency().giveCurrency(
                        player,
                        item.getAmount()
                                * item.getDefinition().getSpecialStorePrice());
            }

            player.getInventory().addItem(item);
        } else {
            player.getPacketBuilder().sendMessage(
                    "You don't have enough space in your inventory.");
            return;
        }

        /** Update the players inventory. */
        player.getPacketBuilder().sendUpdateItems(3823,
                player.getInventory().getContainer().toArray());

        /** Update the shop for anyone who has it open. */
        for (Player p : World.getPlayers()) {
            if (p == null) {
                continue;
            }

            if (p.getOpenShopId() == index) {
                updateShopItems(p);
            }
        }

        /** Check if this shop needs to be restocked and do so if needed. */
        fireRestock();
    }

    /**
     * Sell an {@link Item} to this shop for the specified {@link Player}.
     * 
     * @param player
     *            the player selling the item to this shop.
     * @param item
     *            the item being sold to this shop.
     * @param fromSlot
     *            the inventory slot this item is being sold from.
     */
    public void sellItem(Player player, Item item, int fromSlot) {

        /** Check if this is a valid item. */
        if (item == null || item.getId() < 1 || item.getAmount() < 1) {
            return;
        }

        /** Check if we can sell to this shop. */
        if (!sellItems) {
            player.getPacketBuilder().sendMessage(
                    "The shop owner doesn't want any of your items.");
            return;
        }

        /** Checks if this item is allowed to be sold. */
        for (int i : Misc.NO_SHOP_ITEMS) {
            if (i == item.getId()) {
                player.getPacketBuilder().sendMessage(
                        "You can't sell " + item.getDefinition().getItemName()
                                + " to this store.");
                return;
            }
        }

        /**
         * Checks if you have the item you want to sell in your inventory.
         */
        if (!player.getInventory().getContainer().contains(item.getId())) {
            return;
        }

        /**
         * Block if this shop isn't a general store and you are trying to sell
         * an item that the shop doesn't even have in stock.
         */
        if (!container.contains(item.getId())
                && !name.equalsIgnoreCase("General Store")) {
            player.getPacketBuilder().sendMessage(
                    "You can't sell " + item.getDefinition().getItemName()
                            + " to this store.");
            return;
        }

        /** Checks if this shop has room for the item you are trying to sell. */
        if (!container.hasRoomFor(item)) {
            player.getPacketBuilder()
                    .sendMessage(
                            "There is no room for the item you are trying to sell in this store!");
            return;
        }

        /**
         * Checks if you have enough space in your inventory to receive the
         * currency.
         */
        if (player.getInventory().getContainer().freeSlots() == 0
                && !currency.getCurrency().inventoryFull(player)) {
            player.getPacketBuilder()
                    .sendMessage(
                            "You do not have enough space in your inventory to sell this item!");
            return;
        }

        /**
         * If you try and sell more then you have it sets the amount to what you
         * have.
         */
        if (item.getAmount() > player.getInventory().getContainer()
                .getCount(item.getId())
                && !item.getDefinition().isStackable()) {
            item.setAmount(player.getInventory().getContainer()
                    .getCount(item.getId()));
        } else if (item.getAmount() > player.getInventory().getContainer()
                .getItem(fromSlot).getAmount()
                && item.getDefinition().isStackable()) {
            item.setAmount(player.getInventory().getContainer()
                    .getItem(fromSlot).getAmount());
        }

        /** Actually sell the item. */
        player.getInventory().deleteItemSlot(item, fromSlot);
        currency.getCurrency().recieveCurrency(player,
                item.getAmount() * calculateSellingPrice(item));

        /**
         * Add on to the item if its in the shop already or add it to a whole
         * new slot if its not.
         */
        if (container.contains(item.getId())) {
            container.getById(item.getId()).incrementAmountBy(item.getAmount());
        } else if (!container.contains(item.getId())) {
            container.add(item);
        }

        /** Update your inventory. */
        player.getPacketBuilder().sendUpdateItems(3823,
                player.getInventory().getContainer().toArray());

        /** Update the shop for anyone who has it open. */
        for (Player p : World.getPlayers()) {
            if (p == null) {
                continue;
            }

            if (p.getOpenShopId() == index) {
                updateShopItems(p);
            }
        }
    }

    /**
     * Sends the shop value of an {@link Item} to the specified {@link Player}
     * when selling.
     * 
     * @param player
     *            the player to send the value for.
     * @param item
     *            the item to send the value of.
     */
    public void sendItemSellingPrice(Player player, Item item) {

        /** Checks if this item is able to be sold. */
        for (int i : Misc.NO_SHOP_ITEMS) {
            if (i == item.getId()) {
                player.getPacketBuilder().sendMessage(
                        "You can't sell " + item.getDefinition().getItemName()
                                + " here.");
                return;
            }
        }

        /**
         * Block if this shop isn't a general store and you are trying to sell
         * an item that the shop doesn't even have in stock.
         */
        if (!container.contains(item.getId())
                && !name.equalsIgnoreCase("General Store")) {
            player.getPacketBuilder().sendMessage(
                    "You can't sell " + item.getDefinition().getItemName()
                            + " to this store.");
            return;
        }

        /** Send the actual value here. */
        player.getPacketBuilder().sendMessage(
                item.getDefinition().getItemName() + ": shop will buy for "
                        + calculateSellingPrice(item) + " "
                        + currency.name().toLowerCase().replaceAll("_", " ")
                        + "" + Misc.formatPrice(calculateSellingPrice(item))
                        + ".");
    }

    /**
     * Sends the shop value of an {@link Item} to the specified {@link Player}
     * when buying.
     * 
     * @param player
     *            the player to send the value for.
     * @param item
     *            the item to send the value of.
     */
    public void sendItemBuyingPrice(Player player, Item item) {

        /** Send the value of the item based on the currency. */
        if (currency == Currency.COINS) {
            player.getPacketBuilder().sendMessage(
                    item.getDefinition().getItemName()
                            + ": shop will sell for "
                            + item.getDefinition().getGeneralStorePrice()
                            + " "
                            + currency.name().toLowerCase()
                                    .replaceAll("_", " ")
                            + ""
                            + Misc.formatPrice(item.getDefinition()
                                    .getGeneralStorePrice()) + ".");
        } else {
            player.getPacketBuilder().sendMessage(
                    item.getDefinition().getItemName()
                            + ": shop will sell for "
                            + item.getDefinition().getSpecialStorePrice()
                            + " "
                            + currency.name().toLowerCase()
                                    .replaceAll("_", " ")
                            + ""
                            + Misc.formatPrice(item.getDefinition()
                                    .getSpecialStorePrice()) + ".");
        }
    }

    /**
     * Gets the selling price of an {@link Item}.
     * 
     * @param item
     *            the item to get the selling price for.
     * @return the selling price of this item.
     */
    private int calculateSellingPrice(Item item) {
        return (int) (currency == Currency.COINS ? Math.floor((item
                .getDefinition().getGeneralStorePrice() / 2)) : Math
                .floor((item.getDefinition().getSpecialStorePrice() / 2)));
    }

    /**
     * Assigns a new {@link Worker} to restock this shop if needed.
     */
    private void fireRestock() {

        /**
         * If this shop does not need restocking or is not set to restock then
         * just block.
         */
        if (!needRestockFire() || !restockItems) {
            return;
        }

        /** If this worker isn't running creating a new one. */
        if (processor == null || !processor.isRunning()) {
            processor = new ShopWorker(this);
        } else if (processor.isRunning()) {
            return;
        }

        /** And submit it to the world. */
        TaskFactory.getFactory().submit(processor);
    }

    /**
     * Determines if this shops needs to restock its {@link Item}s.
     * 
     * @return true if 1 or more items is out of stock.
     */
    private boolean needRestockFire() {

        /** Iterate through the shop items. */
        for (Item item : container.toArray()) {

            /** Skip all malformed items. */
            if (item == null) {
                continue;
            }

            /**
             * Flag true if the item is apart of the shop (not sold by a player)
             * and its out of stock.
             */
            if (item.getAmount() < 1 && shopMap.containsKey(item.getId())) {
                return true;
            }
        }

        /** Otherwise flag false. */
        return false;
    }

    /**
     * Determines if the current shop is fully restocked.
     * 
     * @return true if the current shop is fully restocked.
     */
    protected boolean isFullyRestocked() {
        for (Item item : container.toArray()) {
            if (item == null) {
                continue;
            }

            if (shopMap.containsKey(item.getId())) {
                if (item.getAmount() < shopMap.get(item.getId())) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Gets the amount of different items in this shop excluding null values.
     * 
     * @return the amount of different items in this shop.
     */
    private int getShopItemAmount() {
        int total = 0;

        /** Iterate through the shop items. */
        for (Item item : container.toArray()) {

            /** Skip any malformed items. */
            if (item == null) {
                continue;
            }

            /** Increment the total. */
            if (item.getId() > 0) {
                total++;
            }
        }

        return total;
    }

    /**
     * Gets an instance of a shop by it's id.
     * 
     * @param id
     *            the id of the shop to get.
     * @return the static instance of the shop.
     */
    public static Shop getShop(int id) {
        return shops[id];
    }

    /**
     * Gets the map of original shop items.
     * 
     * @return the map of original shop items.
     */
    protected Map<Integer, Integer> getShopMap() {
        return shopMap;
    }

    /**
     * Gets the index of this shop.
     * 
     * @return the index of this shop.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Gets the container of shop items.
     * 
     * @return the container of items.
     */
    public ItemContainer getShopContainer() {
        return container;
    }

    /**
     * Gets if this shop should replenish its stock or not.
     * 
     * @return true if the shop should restock items.
     */
    protected boolean isRestockItems() {
        return restockItems;
    }

    /**
     * Gets the array of registered shops.
     * 
     * @return the registered shops.
     */
    public static Shop[] getShops() {
        return shops;
    }

    /**
     * A worker that will restock a {@link Shop} once it has run out of at least
     * one item to sell.
     * 
     * @author lare96
     */
    private static class ShopWorker extends Worker {

        /** The shop we are restocking. */
        private Shop shop;

        /**
         * Create a new {@link ShopWorker}.
         * 
         * @param shop
         *            the shop we are restocking.
         */
        public ShopWorker(Shop shop) {
            super(9, false);
            this.shop = shop;
        }

        @Override
        public void fire() {

            /**
             * If the stop is fully restocked or isn't a restockable shop then
             * we cancel this worker.
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
                        for (Player player : World.getPlayers()) {
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
}
