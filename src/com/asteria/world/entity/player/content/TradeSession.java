package com.asteria.world.entity.player.content;

import com.asteria.world.entity.player.Player;
import com.asteria.world.entity.player.PlayerRights;
import com.asteria.world.item.Item;
import com.asteria.world.item.ItemContainer;
import com.asteria.world.item.ItemContainer.ContainerPolicy;

/**
 * Manages a full trade session with another player.
 * 
 * @author lare96
 */
public class TradeSession {

    /** Items that are not allowed to be traded. */
    public static final int[] ITEM_UNTRADEABLE = {};

    /** The controller of this trade session. */
    private Player player;

    /** The items being offered to trade. */
    private ItemContainer offering = new ItemContainer(
            ContainerPolicy.NORMAL_POLICY, 28);

    /** The other player in this trade session. */
    private Player partner;

    /** The stage this trade session is currently in. */
    private TradeStage stage;

    /**
     * Create a new {@link TradeSession}.
     * 
     * @param player
     *            the controller of this trade session.
     */
    public TradeSession(Player player) {
        this.player = player;
    }

    /**
     * The different stages of a trade session.
     * 
     * @author lare96
     */
    public enum TradeStage {
        OFFER,
        FIRST_ACCEPT,
        FINAL_ACCEPT
    }

    /**
     * Sends a trade request to the argued {@link Player}. If they have already
     * sent us a trade request then we move on to the <code>OFFER</code> stage.
     * 
     * @param sending
     *            the player being sent this request.
     */
    public void request(Player sending) {

        // Can't send a request to someone already in a trade, or ourselves.
        if (inTrade() || player.equals(sending)) {
            return;
        }

        // If we are replying to a request start the trade.
        if (player.equals(sending.getTradeSession().partner)) {
            partner = sending;
            sending.getTradeSession().partner = player;
            stage = TradeStage.OFFER;
            sending.getTradeSession().stage = TradeStage.OFFER;
            openTradeOffer();
            sending.getTradeSession().openTradeOffer();
            return;
        }

        // If not then send a normal request.
        partner = sending;
        player.getPacketBuilder().sendMessage("Sending trade request...");
        sending.getPacketBuilder().sendMessage(
                player.getCapitalizedUsername() + ":tradereq:");
    }

    /**
     * Opens the initial trading screen so items that each player wish to trade
     * can be presented.
     */
    public void openTradeOffer() {

        // Open the initial trade interface and set up the features.
        player.getPacketBuilder().sendUpdateItems(3322,
                player.getInventory().getContainer().toArray());

        player.getPacketBuilder()
                .sendString(
                        "Trading with: " + getDisplayName(partner) + " who has @gre@" + partner
                                .getInventory().getContainer()
                                .getRemainingSlots() + " free slots", 3417);
        player.getPacketBuilder().sendString("", 3431);
        player.getPacketBuilder().sendString(
                "Are you sure you want to make this trade?", 3535);
        player.getPacketBuilder().sendInventoryInterface(3323, 3321);
    }

    /**
     * Opens the confirm trading screen so each player can be sure if they are
     * getting the right items.
     */
    public void openTradeConfirm() {

        // Open the confirm trade interface and set up the features.
        player.getPacketBuilder().sendUpdateItems(3214,
                player.getInventory().getContainer().toArray());
        player.getPacketBuilder().sendString(confirmText(offering.toArray()),
                3557);
        player.getPacketBuilder()
                .sendString(
                        confirmText(partner.getTradeSession().offering
                                .toArray()),
                        3558);
        player.getPacketBuilder().sendInventoryInterface(3443, 3213);
    }

    /**
     * Puts an item from the player's inventory on the trade screen.
     * 
     * @param item
     *            the item to put on the trade screen.
     * @param slot
     *            the inventory slot this item is coming from.
     */
    public void offer(Item item, int slot) {

        // Validate the item being sent.
        if (item == null || !player.getInventory().getContainer()
                .contains(item.getId())) {
            return;
        }

        // Determine if this item is tradeable.
        for (int id : TradeSession.ITEM_UNTRADEABLE) {
            if (id == item.getId()) {
                player.getPacketBuilder().sendMessage(
                        "You cannot trade this item.");
                return;
            }
        }

        // Set this amount to the proper amount if needed.
        if (item.getAmount() > player.getInventory().getContainer()
                .getCount(item.getId()) && !item.getDefinition().isStackable()) {
            item.setAmount(player.getInventory().getContainer()
                    .getCount(item.getId()));
        } else if (item.getAmount() > player.getInventory().getContainer()
                .getItem(slot).getAmount() && item.getDefinition()
                .isStackable()) {
            item.setAmount(player.getInventory().getContainer().getItem(slot)
                    .getAmount());
        }

        // Delete the item and update the trade screen.
        player.getInventory().deleteItemSlot(item, slot);
        offering.add(item);

        partner.getPacketBuilder()
                .sendString(
                        "Trading with: " + getDisplayName(player) + " who has @gre@" + player
                                .getInventory().getContainer()
                                .getRemainingSlots() + " free slots", 3417);
        player.getPacketBuilder().sendUpdateItems(3322,
                player.getInventory().getContainer().toArray());
        int length = offering.size();
        player.getPacketBuilder().sendUpdateItems(3415, offering.toArray(),
                length);
        partner.getPacketBuilder().sendUpdateItems(3416, offering.toArray(),
                length);
        stage = TradeStage.OFFER;
        partner.getTradeSession().stage = TradeStage.OFFER;
        player.getPacketBuilder().sendString("", 3431);
        partner.getPacketBuilder().sendString("", 3431);
    }

    /**
     * Puts an item from the player's side of the trade screen back into their
     * inventory.
     * 
     * @param item
     *            the item to put back into the inventory.
     */
    public void unoffer(Item item) {

        // Check if we actually have this item.
        if (!offering.contains(item.getId())) {
            return;
        }

        // Set this amount to the proper amount if needed.
        if (item.getAmount() > offering.getCount(item.getId())) {
            item.setAmount(offering.getCount(item.getId()));
        }

        // Delete the item and update the trade screen.
        offering.remove(item);
        player.getInventory().addItem(item);

        partner.getPacketBuilder()
                .sendString(
                        "Trading with: " + getDisplayName(player) + " who has @gre@" + player
                                .getInventory().getContainer()
                                .getRemainingSlots() + " free slots", 3417);
        player.getPacketBuilder().sendUpdateItems(3322,
                player.getInventory().getContainer().toArray());
        int length = offering.size();
        player.getPacketBuilder().sendUpdateItems(3415, offering.toArray(),
                length);
        partner.getPacketBuilder().sendUpdateItems(3416, offering.toArray(),
                length);
        stage = TradeStage.OFFER;
        partner.getTradeSession().stage = TradeStage.OFFER;
        player.getPacketBuilder().sendString("", 3431);
        partner.getPacketBuilder().sendString("", 3431);
    }

    /** Gives each player their respective items and resets the trade. */
    public void distributeItems() {

        // Give the items to each of the players
        partner.getInventory().addItemSet(offering.toArray());
        player.getInventory().addItemSet(
                partner.getTradeSession().offering.toArray());

        // Reset the trade.
        reset();
    }

    /**
     * Resets the trade for both this player and their partner.
     * 
     * @param declined
     *            <code>true</code> if the trade is being reset because the
     *            player declined, <code>false</code> if the trade is being
     *            forced to reset.
     */
    public void reset(boolean declined) {

        // Don't need to reset if we aren't in a trade.
        if (!inTrade()) {
            return;
        }

        // Give the items to each of the players
        player.getInventory().addItemSet(offering.toArray());
        partner.getInventory().addItemSet(
                partner.getTradeSession().offering.toArray());

        // Send the partner a message if applicable.
        if (declined) {
            partner.getPacketBuilder().sendMessage(
                    "The other player has declined the trade!");
            player.getPacketBuilder().sendMessage(
                    "You have declined the trade.");
        }

        // Reset the trade.
        reset();
    }

    /** Resets the components of this trade session. */
    private void reset() {

        // Close trade the screen.
        player.getPacketBuilder().sendCloseWindows();
        partner.getPacketBuilder().sendCloseWindows();

        // Clear the offer containers.
        partner.getTradeSession().offering.clear();
        offering.clear();

        // Update the trade screens with empty containers.
        player.getPacketBuilder().sendUpdateItems(3415, null, 0);
        partner.getPacketBuilder().sendUpdateItems(3416, null, 0);

        // Reset the values in this trade session.
        partner.getTradeSession().stage = null;
        partner.getTradeSession().partner = null;
        stage = null;
        partner = null;
    }

    /**
     * Gets the trade display name for the argued {@link Player}.
     * 
     * @param player
     *            the player to get the trade display name for.
     * @return the trade display name.
     */
    private String getDisplayName(Player player) {
        return player.getCapitalizedUsername().concat(
                player.getCapitalizedUsername()
                        .concat(player.getRights().equalTo(
                                PlayerRights.MODERATOR) ? "@cr1@" : player
                                .getRights()
                                .greaterThan(PlayerRights.MODERATOR) ? "@cr2@"
                                : ""));
    }

    /**
     * Generates the confirm text that will be displayed based on the argued
     * array of items.
     * 
     * @param items
     *            the array to generate confirm text for.
     * @return the confirm text for the array of items.
     */
    private String confirmText(Item[] items) {
        String tradeItems = "Absolutely nothing!";
        String tradeAmount = "";

        int count = 0;

        for (Item item : items) {
            if (item == null) {
                continue;
            }

            if (item.getAmount() >= 1000 && item.getAmount() < 1000000) {
                tradeAmount = "@cya@" + (item.getAmount() / 1000) + "K @whi@(" + item
                        .getAmount() + ")";
            } else if (item.getAmount() >= 1000000) {
                tradeAmount = "@gre@" + (item.getAmount() / 1000000) + " million @whi@(" + item
                        .getAmount() + ")";
            } else {
                tradeAmount = "" + item.getAmount();
            }

            if (count == 0) {
                tradeItems = item.getDefinition().getItemName();
            } else {
                tradeItems = tradeItems + "\\n" + item.getDefinition()
                        .getItemName();
            }

            if (item.getDefinition().isStackable()) {
                tradeItems = tradeItems + " x " + tradeAmount;
            }

            count++;
        }
        return tradeItems;
    }

    /**
     * Determines if this player is in a trade or not.
     * 
     * @return <code>true</code> if this player is in a trade,
     *         <code>false</code> otherwise.
     */
    public boolean inTrade() {
        return player.getTradeSession().stage == TradeStage.OFFER || player
                .getTradeSession().stage == TradeStage.FIRST_ACCEPT || player
                .getTradeSession().stage == TradeStage.FINAL_ACCEPT;
    }

    /**
     * Gets the container with the item you are offering in this trade session.
     * 
     * @return the offering.
     */
    public ItemContainer getOffering() {
        return offering;
    }

    /**
     * Gets your partner in the trade session.
     * 
     * @return the partner.
     */
    public Player getPartner() {
        return partner;
    }

    /**
     * Gets the current trade stage.
     * 
     * @return the stage.
     */
    public TradeStage getStage() {
        return stage;
    }

    /**
     * Sets the current trade stage.
     * 
     * @param stage
     *            the current trade stage.
     */
    public void setStage(TradeStage stage) {
        this.stage = stage;
    }
}
