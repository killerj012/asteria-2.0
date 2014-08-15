package com.asteria.world.shop;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.asteria.engine.task.Task;
import com.asteria.engine.task.TaskManager;
import com.asteria.util.JsonLoader;
import com.asteria.world.World;
import com.asteria.world.entity.player.Player;
import com.asteria.world.item.Item;
import com.asteria.world.item.ItemContainer;
import com.asteria.world.item.ItemContainer.Policy;
import com.asteria.world.item.ItemDefinition;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * A shop from which any {@link Player} may buy items.
 * 
 * @author lare96
 */
public class Shop {

    // TODO: Set of players currently viewing the shop to avoid looping through
    // everyone online.
    // TODO: More functionality for general stores and special stores.

    /** Items that are not allowed to be in a shop. */
    public static final int[] NO_SHOP_ITEMS = { 995 };

    /** A primitive array of registered shops. */
    // Increase this array size if you need more than 25 shops.
    private static Shop[] shops = new Shop[25];

    /** An {@link ItemContainer} that holds the items within this shop. */
    private ItemContainer container = new ItemContainer(Policy.STACK_ALWAYS, 48);

    /** The index of this shop. */
    private int index;

    /** The name of this shop. */
    private String name;

    /** A map of the original shop items and their amounts. */
    private Map<Integer, Integer> shopMap;

    /** If the shop will replenish its stock once it runs out. */
    private boolean restockItems;

    /** If a {@link Player} is able to sell items to this shop. */
    private boolean sellItems;

    /** The currency this shop is using. */
    private Currency currency;

    /** A {@link Task} that will re-stock this shop. */
    private Task processor;

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
        this.restockItems = restockItems;
        this.sellItems = sellItems;
        this.currency = currency;
        this.container.setItems(items);
        this.shopMap = new HashMap<>(container.capacity());
        Arrays.stream(items).filter(Objects::nonNull)
                .forEach(item -> shopMap.put(item.getId(), item.getAmount()));
    }

    /**
     * Opens this shop for the specified {@link Player}.
     * 
     * @param player
     *            the player to open the shop for.
     */
    public void openShop(Player player) {
        player.getPacketBuilder().sendUpdateItems(3823,
                player.getInventory().toArray());
        player.getPacketBuilder().sendUpdateItems(3900, container.toArray(),
                container.size());
        player.setOpenShopId(index);
        player.getPacketBuilder().sendInventoryInterface(3824, 3822);
        player.getPacketBuilder().sendString(name, 3901);
    }

    /**
     * Purchases an {@link Item} from this shop for the argued {@link Player}.
     * 
     * @param player
     *            the player purchasing the item.
     * @param item
     *            the item being purchased.
     */
    public void purchase(Player player, Item item) {

        // Check if the item you are buying is at 0 stock.
        if (item.getAmount() == 0) {
            player.getPacketBuilder().sendMessage(
                    "There is none of this item left in stock!");
            return;
        }

        // Check if the shop even contains the item you're trying to buy.
        if (!container.contains(item.getId())) {
            return;
        }

        // Check if the player has the required amount of the currency needed to
        // buy this item.
        if (currency == Currency.COINS) {
            if (!(currency.getCurrency().getAmount(player) >= (item
                    .getDefinition().getGeneralStorePrice() * item.getAmount()))) {
                player.getPacketBuilder().sendMessage(
                        "You do not have enough coins to buy this item.");
                return;
            }
        } else {
            if (!(currency.getCurrency().getAmount(player) >= (item
                    .getDefinition().getSpecialStorePrice() * item.getAmount()))) {
                player.getPacketBuilder()
                        .sendMessage(
                                "You do not have enough " + currency.name()
                                        .toLowerCase().replaceAll("_", " ") + " to buy this item.");
                return;
            }
        }

        // If you are buying more than the shop has in stock, set the amount you
        // are buying to how much is in stock.
        if (item.getAmount() > container.totalAmount(item.getId())) {
            item.setAmount(container.totalAmount(item.getId()));
        }

        // Set the amount you are buying to your current amount of free slots if
        // you do not have enough room for the item you are trying to buy.
        if (!player.getInventory().spaceFor(item)) {
            item.setAmount(player.getInventory().getRemainingSlots());

            if (item.getAmount() == 0) {
                player.getPacketBuilder()
                        .sendMessage(
                                "You do not have enough space in your inventory to buy this item!");
                return;
            }
        }

        // Here we actually buy the item.
        if (player.getInventory().getRemainingSlots() >= item.getAmount() && !item
                .getDefinition().isStackable() || player.getInventory()
                .getRemainingSlots() >= 1 && item.getDefinition().isStackable() || player
                .getInventory().contains(item.getId()) && item.getDefinition()
                .isStackable()) {

            if (shopMap.containsKey(item.getId())) {
                container.getItem(item.getId()).decrementAmountBy(
                        item.getAmount());
            } else if (!shopMap.containsKey(item.getId())) {
                container.remove(item);
            }

            if (currency == Currency.COINS) {
                currency.getCurrency().give(
                        player,
                        item.getAmount() * item.getDefinition()
                                .getGeneralStorePrice());
            } else {
                currency.getCurrency().give(
                        player,
                        item.getAmount() * item.getDefinition()
                                .getSpecialStorePrice());
            }

            player.getInventory().add(item);
        } else {
            player.getPacketBuilder().sendMessage(
                    "You don't have enough space in your inventory.");
            return;
        }

        // Update the players inventory.
        player.getPacketBuilder().sendUpdateItems(3823,
                player.getInventory().toArray());

        // Update the shop for anyone who has it open.
        int size = container.size();
        for (Player p : World.getPlayers()) {
            if (p == null) {
                continue;
            }

            if (p.getOpenShopId() == index) {
                p.getPacketBuilder().sendUpdateItems(3900, container.toArray(),
                        size);
            }
        }

        // Check if this shop needs to be restocked and do so if needed.
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
    public void sell(Player player, Item item, int fromSlot) {

        // Check if this is a valid item.
        if (item == null || item.getId() < 1 || item.getAmount() < 1) {
            return;
        }

        // Check if we can sell to this shop.
        if (!sellItems) {
            player.getPacketBuilder().sendMessage(
                    "The shop owner doesn't want any of your items.");
            return;
        }

        // Checks if this item is allowed to be sold.
        for (int i : Shop.NO_SHOP_ITEMS) {
            if (i == item.getId()) {
                player.getPacketBuilder()
                        .sendMessage(
                                "You can't sell " + item.getDefinition()
                                        .getItemName() + " to this store.");
                return;
            }
        }

        // Checks if you have the item you want to sell in your inventory.
        if (!player.getInventory().contains(item.getId())) {
            return;
        }

        // Block if this shop isn't a general store and you are trying to sell
        // an item that the shop doesn't even have in stock.
        if (!container.contains(item.getId()) && !name
                .equalsIgnoreCase("General Store")) {
            player.getPacketBuilder()
                    .sendMessage(
                            "You can't sell " + item.getDefinition()
                                    .getItemName() + " to this store.");
            return;
        }

        // Checks if this shop has room for the item you are trying to sell.
        if (!container.spaceFor(item)) {
            player.getPacketBuilder()
                    .sendMessage(
                            "There is no room for the item you are trying to sell in this store!");
            return;
        }

        // Checks if you have enough space in your inventory to receive the
        // currency.
        if (player.getInventory().getRemainingSlots() == 0 && !currency
                .getCurrency().inventoryFull(player)) {
            player.getPacketBuilder()
                    .sendMessage(
                            "You do not have enough space in your inventory to sell this item!");
            return;
        }

        // Sets the amount to what you have if you try and buy more.
        if (item.getAmount() > player.getInventory().totalAmount(item.getId()) && !item
                .getDefinition().isStackable()) {
            item.setAmount(player.getInventory().totalAmount(item.getId()));
        } else if (item.getAmount() > player.getInventory().get(fromSlot)
                .getAmount() && item.getDefinition().isStackable()) {
            item.setAmount(player.getInventory().get(fromSlot).getAmount());
        }

        // Actually sell the item.
        player.getInventory().remove(item, fromSlot);
        currency.getCurrency().recieve(player,
                item.getAmount() * calculateSellingPrice(item));

        // Add on to the item if its in the shop already or add it to a whole
        // new slot if its not.
        if (container.contains(item.getId())) {
            container.getItem(item.getId()).incrementAmountBy(item.getAmount());
        } else if (!container.contains(item.getId())) {
            container.add(item);
        }

        // Update your inventory.
        player.getPacketBuilder().sendUpdateItems(3823,
                player.getInventory().toArray());

        // Update the shop for anyone who has it open.
        int size = container.size();
        for (Player p : World.getPlayers()) {
            if (p == null) {
                continue;
            }

            if (p.getOpenShopId() == index) {
                p.getPacketBuilder().sendUpdateItems(3900, container.toArray(),
                        size);
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
    public void getSellingPrice(Player player, Item item) {

        // Checks if this item is able to be sold.
        for (int i : Shop.NO_SHOP_ITEMS) {
            if (i == item.getId()) {
                player.getPacketBuilder()
                        .sendMessage(
                                "You can't sell " + item.getDefinition()
                                        .getItemName() + " here.");
                return;
            }
        }

        // Block if this shop isn't a general store and you are trying to sell
        // an item that the shop doesn't even have in stock.
        if (!container.contains(item.getId()) && !name
                .equalsIgnoreCase("General Store")) {
            player.getPacketBuilder()
                    .sendMessage(
                            "You can't sell " + item.getDefinition()
                                    .getItemName() + " to this store.");
            return;
        }

        // Send the actual value here.
        player.getPacketBuilder()
                .sendMessage(
                        item.getDefinition().getItemName() + ": shop will buy for " + calculateSellingPrice(item) + " " + currency
                                .name().toLowerCase().replaceAll("_", " ") + "" + Shop
                                .formatPrice(calculateSellingPrice(item)) + ".");
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
    public void getPurchasePrice(Player player, Item item) {

        // Send the value of the item based on the currency.
        if (currency == Currency.COINS) {
            player.getPacketBuilder()
                    .sendMessage(
                            item.getDefinition().getItemName() + ": shop will sell for " + Shop
                                    .formatPrice(item.getDefinition()
                                            .getGeneralStorePrice()) + " " + currency
                                    .name().toLowerCase().replaceAll("_", " ") + ".");
        } else {
            player.getPacketBuilder()
                    .sendMessage(
                            item.getDefinition().getItemName() + ": shop will sell for " + Shop
                                    .formatPrice(item.getDefinition()
                                            .getSpecialStorePrice()) + " " + currency
                                    .name().toLowerCase().replaceAll("_", " ") + ".");
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

    /** Submits a {@link Task} to re-stock this shop if needed. */
    private void fireRestock() {

        // If this shop does not need re-stocking then return.
        if (!needRestockFire() || !restockItems) {
            return;
        }

        // If this task isn't running creating a new one.
        if (processor == null || !processor.isRunning()) {
            processor = new ShopWorker(this);
        } else if (processor.isRunning()) {
            return;
        }

        // And submit it to the task factory.
        TaskManager.submit(processor);
    }

    /**
     * Determines if this shops needs to re-stock its {@link Item}s.
     * 
     * @return <code>true</code> if it needs to re-stock, <code>false</code>
     *         otherwise.
     */
    private boolean needRestockFire() {
        for (Item item : container) {
            if (item == null) {
                continue;
            }

            // Return if the item is a part of the original shop and out of
            // stock.
            if (item.getAmount() <= 0 && shopMap.containsKey(item.getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the current shop is fully restocked.
     * 
     * @return <code>true</code> if the current shop is fully restocked,
     *         <code>false</code> otherwise.
     */
    private boolean isFullyRestocked() {
        for (Item item : container) {
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
     * Formats the argued price for easier viewing.
     * 
     * @param price
     *            the price to format.
     * @return the newly formatted price.
     */
    public static String formatPrice(int price) {
        if (price >= 1000 && price < 1000000) {
            return " (" + (price / 1000) + "K)";
        } else if (price >= 1000000) {
            return " (" + (price / 1000000) + " million)";
        }

        return Integer.toString(price);
    }

    /**
     * Prepares the dynamic json loader for loading shops.
     * 
     * @return the dynamic json loader.
     * @throws Exception
     *             if any errors occur while preparing for load.
     */
    public static JsonLoader parseShops() throws Exception {
        return new JsonLoader() {
            @Override
            public void load(JsonObject reader, Gson builder) {
                Shop shop = new Shop(reader.get("id").getAsInt(), reader.get(
                        "name").getAsString(), builder.fromJson(
                        reader.get("items").getAsJsonArray(), Item[].class),
                        reader.get("restock").getAsBoolean(), reader.get(
                                "can-sell-items").getAsBoolean(),
                        Currency.valueOf(reader.get("currency").getAsString()));

                for (int id : NO_SHOP_ITEMS) {
                    if (shop.getShopContainer().contains(id)) {
                        throw new IllegalStateException(
                                "Item not allowed to be sold in shops: " + ItemDefinition
                                        .getDefinitions()[id].getItemName());
                    }
                }

                Shop.getShops()[shop.getIndex()] = shop;
            }

            @Override
            public String filePath() {
                return "./data/json/shops/world_shops.json";
            }
        };
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
     * A task that will re-stock a {@link Shop} once it has run out of at least
     * one item to sell.
     * 
     * @author lare96
     */
    private static class ShopWorker extends Task {

        /** The shop we are re-stocking. */
        private Shop shop;

        /**
         * Create a new {@link ShopWorker}.
         * 
         * @param shop
         *            the shop we are re-stocking.
         */
        public ShopWorker(Shop shop) {
            super(20, false);
            this.shop = shop;
        }

        @Override
        public void fire() {

            // If the stop is fully restocked or isn't a re-stockable shop then
            // we cancel this task.
            if (shop.isFullyRestocked() || !shop.isRestockItems()) {
                this.cancel();
                return;
            }

            // Iterate through the shops items.
            for (Item item : shop.getShopContainer()) {
                if (item == null) {
                    continue;
                }

                // If this item is not at its original amount...
                if (shop.getShopMap().containsKey(item.getId())) {
                    if (item.getAmount() < shop.getShopMap().get(item.getId())) {

                        // Increment the item's amount by 1.
                        item.incrementAmount();

                        // And update it for every player viewing that shop!
                        int size = shop.container.size();
                        for (Player p : World.getPlayers()) {
                            if (p == null) {
                                continue;
                            }

                            if (p.getOpenShopId() == shop.index) {
                                p.getPacketBuilder().sendUpdateItems(3900,
                                        shop.container.toArray(), size);
                            }
                        }
                    }
                }
            }
        }
    }
}
