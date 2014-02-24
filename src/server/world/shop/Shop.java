package server.world.shop;

import server.core.Rs2Engine;
import server.core.net.buffer.PacketBuffer;
import server.core.worker.Worker;
import server.util.Misc;
import server.world.entity.player.Player;
import server.world.item.Item;
import server.world.item.ItemContainer;
import server.world.item.ItemContainer.ContainerPolicy;

/**
 * A collection of functions that represent a shop. Every shop contains its own
 * restocking worker that allows every shop to individually restock itself,
 * which takes away the need for a massive loop every 600ms (sound familiar?
 * check out the <code>WorldItem</code> class). This also means that if no
 * items are being restocked, no workers are even running! Which in my opinion,
 * is a ton better than looping through a massive array every 600ms, when half
 * the time restocking isn't even needed.
 * 
 * @author lare96
 */
public class Shop {

    /**
     * An array of active shops.
     */
    private static Shop[] shops = new Shop[10];

    /**
     * The shop id.
     */
    private int id;

    /**
     * The shop name.
     */
    private String name;

    /**
     * The modifiable shop container.
     */
    private ItemContainer container = new ItemContainer(ContainerPolicy.STACKABLE_POLICY, 48);

    /**
     * The original shop items.
     */
    private Item[] originalShopItems;

    /**
     * Flag that determines if the shop will replenish its stock or not.
     */
    private boolean stock;

    /**
     * The currency this shop is running on.
     */
    private Currency currency;

    /**
     * The worker that will restock this shop.
     */
    private Worker processor;

    /**
     * Create a new shop.
     * 
     * @param id
     *        the id of the shop.
     * @param name
     *        the name of the shop.
     * @param items
     *        the items in this shop.
     * @param restock
     *        if this shop will restock itself.
     * @param currency
     *        the currency this shop runs on.
     */
    public Shop(int id, String name, Item[] items, boolean restock, Currency currency) {
        this.setId(id);
        this.setName(name);
        this.getShopContainer().setItems(items);
        this.setOriginalShopItems(items);
        this.setReplenishStock(restock);
        this.setCurrency(currency);
        this.processor = new ShopWorker(this);
    }

    /**
     * Open and configure this shop.
     * 
     * @param player
     *        the player to open the shop for.
     */
    public void openShop(Player player) {

        /**
         * Open the shop, display the shop items and send an interface to the
         * inventory that allows the player to right click buy and sell.
         */
        player.getPacketBuilder().sendUpdateItems(3823, player.getInventory().getContainer().toArray());
        updateShopItems(player);
        player.setOpenShopId(this.getId());
        player.getPacketBuilder().sendInventoryInterface(3824, 3822);
        player.getPacketBuilder().sendString(this.getName(), 3901);
    }

    /**
     * Updates the items in this shop.
     * 
     * @param player
     *        the player to update the items for.
     */
    protected void updateShopItems(Player player) {

        /**
         * A custom implementation of the <code>sendItemUpdates</code> method
         * used for shops.
         */
        PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(2048);
        out.writeVariableShortPacketHeader(53);
        out.writeShort(3900);
        out.writeShort(this.getShopItemAmount());

        for (Item item : this.getShopContainer().toArray()) {
            if (item == null) {
                continue;
            }

            if (item.getId() > 0) {
                if (item.getAmount() > 254) {
                    out.writeByte(255);
                    out.writeInt(item.getAmount(), PacketBuffer.ByteOrder.INVERSE_MIDDLE);
                } else {
                    out.writeByte(item.getAmount());
                }

                out.writeShort(item.getId() + 1, PacketBuffer.ValueType.A, PacketBuffer.ByteOrder.LITTLE);
            }
        }

        out.finishVariableShortPacketHeader();
        Rs2Engine.getEncoder().encode(out, player.getSession());
    }

    /**
     * Purchase an item from this shop.
     * 
     * @param player
     *        the player purchasing the item.
     * @param item
     *        the item being purchased.
     */
    public void buyItem(Player player, Item item) {

        /** Check if the item you are buying as 0 stock. */
        if (item.getAmount() == 0) {
            player.getPacketBuilder().sendMessage("There is none of this item left in stock!");
            return;
        }

        /**
         * Check if the shop even contains the item you're trying to buy
         * (protection from packet injection).
         */
        if (!this.getShopContainer().contains(item.getId())) {
            return;
        }

        /**
         * Check if the player has the required amount of the currency needed to
         * buy this item.
         */
        if (this.getCurrency() == Currency.COINS) {
            if (!(player.getInventory().getContainer().getCount(this.getCurrency().getItemId()) >= (item.getDefinition().getGeneralStorePrice() * item.getAmount()))) {
                player.getPacketBuilder().sendMessage("You do not have enough coins to buy this item.");
                return;
            }
        } else {
            if (!(player.getInventory().getContainer().getCount(this.getCurrency().getItemId()) >= (item.getDefinition().getSpecialStorePrice() * item.getAmount()))) {
                player.getPacketBuilder().sendMessage("You do not have enough " + this.getCurrency().name().toLowerCase().replaceAll("_", " ") + " to buy this item.");
                return;
            }
        }

        /**
         * If you are buying more than the shop has in stock, set the amount you
         * are buying to how much is in stock.
         */
        if (item.getAmount() > this.getShopContainer().getCount(item.getId())) {
            item.setAmount(this.getShopContainer().getCount(item.getId()));
        }

        /**
         * Set the amount you are buying to your current amount of free slots if
         * you do not have enough room for the item you are trying to buy.
         */
        if (!player.getInventory().getContainer().hasRoomFor(item)) {
            item.setAmount(player.getInventory().getContainer().freeSlots());

            if (item.getAmount() == 0) {
                player.getPacketBuilder().sendMessage("You do not have enough space in your inventory to buy this item!");
                return;
            }
        }

        /** Buy the item. */
        if (player.getInventory().getContainer().freeSlots() >= item.getAmount() && !item.getDefinition().isStackable() || player.getInventory().getContainer().freeSlots() >= 1 && item.getDefinition().isStackable()) {
            this.getShopContainer().getById(item.getId()).decrementAmountBy(item.getAmount());

            if (this.getCurrency() == Currency.COINS) {
                player.getInventory().deleteItem(new Item(this.getCurrency().getItemId(), item.getAmount() * item.getDefinition().getGeneralStorePrice()));
            } else {
                player.getInventory().deleteItem(new Item(this.getCurrency().getItemId(), item.getAmount() * item.getDefinition().getSpecialStorePrice()));
            }

            player.getInventory().addItem(item);
        } else {
            player.getPacketBuilder().sendMessage("You don't have enough space in your inventory.");
            return;
        }

        /** Update the players inventory. */
        player.getPacketBuilder().sendUpdateItems(3823, player.getInventory().getContainer().toArray());

        /** Update the shop for anyone who has it open. */
        for (Player p : Rs2Engine.getWorld().getPlayers()) {
            if (p == null) {
                continue;
            }

            if (p.getOpenShopId() == this.getId()) {
                this.updateShopItems(p);
            }
        }

        /** Check if this shop needs to be restocked, if so restock it. */
        restockShop();
    }

    /**
     * Sell an item to this shop.
     * 
     * @param player
     *        the player selling the item.
     * @param item
     *        the item being sold.
     * @param fromSlot
     *        the slot being sold from.
     */
    public void sellItem(Player player, Item item, int fromSlot) {

        /** Check if this is a valid item. */
        if (item.getId() < 1 || item.getAmount() < 1 || item == null) {
            return;
        }

        /** Checks if this item is allowed to be sold. */
        for (int i : Misc.NO_SHOP_ITEMS) {
            if (i == item.getId()) {
                player.getPacketBuilder().sendMessage("You can't sell " + item.getDefinition().getItemName() + " to this store.");
                return;
            }
        }

        /**
         * Checks if you have the item you want to sell in your inventory, to
         * protect against packet injection.
         */
        if (!player.getInventory().getContainer().contains(item.getId())) {
            return;
        }

        /**
         * Block if this shop isn't a general store and you are trying to sell
         * an item that the shop doesn't even have in stock.
         */
        if (!this.getShopContainer().contains(item.getId()) && !this.getName().equalsIgnoreCase("General Store")) {
            player.getPacketBuilder().sendMessage("You can't sell " + item.getDefinition().getItemName() + " to this store.");
            return;
        }

        /** Checks if this shop has room for the item you are trying to sell. */
        if (!this.getShopContainer().hasRoomFor(item)) {
            player.getPacketBuilder().sendMessage("There is no room for the item you are trying to sell in this store!");
            return;
        }

        /**
         * Checks if you have enough space in your inventory to receive the
         * currency.
         */
        if (player.getInventory().getContainer().freeSlots() == 0 && !player.getInventory().getContainer().contains(this.getCurrency().getItemId())) {
            player.getPacketBuilder().sendMessage("You do not have enough space in your inventory to sell this item!");
            return;
        }

        /**
         * If you try and sell more then you have, it sets the amount to what
         * you have.
         */
        if (item.getAmount() > player.getInventory().getContainer().getCount(item.getId()) && !item.getDefinition().isStackable()) {
            item.setAmount(player.getInventory().getContainer().getCount(item.getId()));
        } else if (item.getAmount() > player.getInventory().getContainer().getItem(fromSlot).getAmount() && item.getDefinition().isStackable()) {
            item.setAmount(player.getInventory().getContainer().getItem(fromSlot).getAmount());
        }

        /** Sell the item. */
        player.getInventory().deleteItemSlot(item, fromSlot);
        player.getInventory().addItem(new Item(this.getCurrency().getItemId(), item.getAmount() * getSellingPrice(item)));
        this.getShopContainer().getById(item.getId()).incrementAmountBy(item.getAmount());

        /** Update your inventory. */
        player.getPacketBuilder().sendUpdateItems(3823, player.getInventory().getContainer().toArray());

        /** Update the shop for anyone who has it open. */
        for (Player p : Rs2Engine.getWorld().getPlayers()) {
            if (p == null) {
                continue;
            }

            if (p.getOpenShopId() == this.getId()) {
                this.updateShopItems(p);
            }
        }
    }

    /**
     * Sends the value of the item to the player when selling.
     * 
     * @param player
     *        the player to send the value for.
     * @param item
     *        the item to send the value of.
     */
    public void sendItemSellingPrice(Player player, Item item) {

        /** Checks if this item is able to be sold. */
        for (int i : Misc.NO_SHOP_ITEMS) {
            if (i == item.getId()) {
                player.getPacketBuilder().sendMessage("You can't sell " + item.getDefinition().getItemName() + " to this store.");
                return;
            }
        }

        /**
         * Block if this shop isn't a general store and you are trying to sell
         * an item that the shop doesn't even have in stock.
         */
        if (!this.getShopContainer().contains(item.getId()) && !this.getName().equalsIgnoreCase("General Store")) {
            player.getPacketBuilder().sendMessage("You can't sell " + item.getDefinition().getItemName() + " to this store.");
            return;
        }

        /** Send the value. */
        if (this.getCurrency() == Currency.COINS) {
            player.getPacketBuilder().sendMessage(item.getDefinition().getItemName() + ": shop will buy for " + this.getSellingPrice(item) + " " + this.getCurrency().name().toLowerCase().replaceAll("_", " ") + "" + Misc.formatPrice(this.getSellingPrice(item)) + ".");
        } else {
            player.getPacketBuilder().sendMessage(item.getDefinition().getItemName() + ": shop will buy for " + this.getSellingPrice(item) + " " + this.getCurrency().name().toLowerCase().replaceAll("_", " ") + "" + Misc.formatPrice(this.getSellingPrice(item)) + ".");
        }
    }

    /**
     * Sends the value of the item to the player when buying.
     * 
     * @param player
     *        the player to send the value for.
     * @param item
     *        the item to send the value of.
     */
    public void sendItemBuyingPrice(Player player, Item item) {

        /** Send the value of the item based on the currency. */
        if (this.getCurrency() == Currency.COINS) {
            player.getPacketBuilder().sendMessage(item.getDefinition().getItemName() + ": shop will sell for " + item.getDefinition().getGeneralStorePrice() + " " + this.getCurrency().name().toLowerCase().replaceAll("_", " ") + "" + Misc.formatPrice(item.getDefinition().getGeneralStorePrice()) + ".");
        } else {
            player.getPacketBuilder().sendMessage(item.getDefinition().getItemName() + ": shop will sell for " + item.getDefinition().getSpecialStorePrice() + " " + this.getCurrency().name().toLowerCase().replaceAll("_", " ") + "" + Misc.formatPrice(item.getDefinition().getSpecialStorePrice()) + ".");
        }
    }

    /**
     * Gets the selling price of a certain item (the item has to be worth less
     * when selling or people would just buy then sell for profit).
     * 
     * @param item
     *        the item to get the selling price for.
     * @return the price.
     */
    private int getSellingPrice(Item item) {
        return (int) (this.getCurrency() == Currency.COINS ? Math.floor((item.getDefinition().getGeneralStorePrice() / 2)) : Math.floor((item.getDefinition().getSpecialStorePrice() / 2)));
    }

    /**
     * Instantiate and schedule a new worker that will restock this shop.
     */
    private void restockShop() {
        if (!this.needsRestock() || !this.isReplenishStock()) {
            return;
        }

        if (processor == null || !processor.isRunning()) {
            processor = new ShopWorker(this);
        }

        Rs2Engine.getWorld().submit(processor);
    }

    /**
     * Checks if this shops needs to restock its items.
     * 
     * @return if the shop needs to be restocked.
     */
    private boolean needsRestock() {

        /**
         * Loop through the current shop items and check if the stock of any
         * original shop items are 0. If so, return true.
         */
        for (Item item : this.getShopContainer().toArray()) {
            if (item == null) {
                continue;
            }

            if (item.getAmount() < 1 && isOriginalItem(item.getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if an item id is apart of the original shop items.
     * 
     * @param id
     *        the id to check for.
     * @return if the item is apart of the original shop items.
     */
    private boolean isOriginalItem(int id) {

        /**
         * Loop through the original shop items. If the id specified matches
         * with any of the original shop items, return true.
         */
        for (Item item : this.getOriginalShopItems()) {
            if (item == null) {
                continue;
            }

            if (item.getId() == id) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the current shop items are at the original amount.
     * 
     * @return if the current shop items are at the original amount.
     */
    protected boolean atOriginalAmounts() {
        int amountNeeded = this.getOriginalShopItemAmount();
        int amountGotten = 0;

        for (Item item : this.getShopContainer().toArray()) {
            if (item == null) {
                continue;
            }

            if (item.getAmount() == getOriginalAmount(item.getId())) {
                amountGotten++;
            }
        }
        return amountNeeded == amountGotten ? true : false;
    }

    /**
     * Gets the original amount of a shop item.
     * 
     * @param id
     *        the item to get the original amount of.
     * @return the original amount.
     */
    protected int getOriginalAmount(int id) {
        for (Item item : this.getOriginalShopItems()) {
            if (item == null) {
                continue;
            }

            if (item.getId() == id) {
                return item.getAmount();
            }
        }
        return -1;
    }

    /**
     * @return the amount of different items in this shop excluding null values.
     */
    private int getShopItemAmount() {
        int total = 0;

        for (Item i : this.getShopContainer().toArray()) {
            if (i == null) {
                continue;
            }

            if (i.getId() > 0) {
                total++;
            }
        }

        return total;
    }

    /**
     * @return the amount of different items in this shop excluding null values
     *         and non-primitive shop items.
     */
    private int getOriginalShopItemAmount() {
        int total = 0;

        for (Item item : this.getShopContainer().toArray()) {
            if (item == null) {
                continue;
            }

            if (item.getId() > 0 && isOriginalItem(item.getId())) {
                total++;
            }
        }

        return total;
    }

    /**
     * Gets an instance of a shop by it's id.
     * 
     * @param id
     *        the id of the shop to get.
     * @return the static instance of the shop.
     */
    public static Shop getShop(int id) {
        return shops[id];
    }

    /**
     * Gets an instance of a shop by it's name.
     * 
     * @param id
     *        the name of the shop to get.
     * @return the static instance of the shop.
     */
    public static Shop getShop(String name) {
        for (Shop s : shops) {
            if (s == null) {
                continue;
            }

            if (s.getName().equals(name)) {
                return s;
            }
        }
        return null;
    }

    /**
     * @return the id.
     */
    public int getId() {
        return id;
    }

    /**
     * @param id
     *        the id to set.
     */
    private void setId(int id) {
        this.id = id;
    }

    /**
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *        the name to set.
     */
    private void setName(String name) {
        this.name = name;
    }

    /**
     * @return the container.
     */
    public ItemContainer getShopContainer() {
        return container;
    }

    /**
     * @return the replenishStock.
     */
    protected boolean isReplenishStock() {
        return stock;
    }

    /**
     * @param replenishStock
     *        the replenishStock to set.
     */
    public void setReplenishStock(boolean stock) {
        this.stock = stock;
    }

    /**
     * @return the currency.
     */
    public Currency getCurrency() {
        return currency;
    }

    /**
     * @param currency
     *        the currency to set.
     */
    private void setCurrency(Currency currency) {
        this.currency = currency;
    }

    /**
     * @return the restockWorker.
     */
    private Worker getTask() {
        return processor;
    }

    /**
     * @return the originalShopItems.
     */
    private Item[] getOriginalShopItems() {
        return originalShopItems;
    }

    /**
     * @param originalShopItems
     *        the originalShopItems to set.
     */
    private void setOriginalShopItems(Item[] originalShopItems) {
        this.originalShopItems = originalShopItems;
    }

    /**
     * @return the shops
     */
    public static Shop[] getShops() {
        return shops;
    }
}
