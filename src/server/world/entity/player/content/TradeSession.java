package server.world.entity.player.content;

import server.core.Rs2Engine;
import server.core.net.buffer.PacketBuffer;
import server.util.Misc;
import server.world.entity.player.Player;
import server.world.item.Item;
import server.world.item.ItemContainer;
import server.world.item.ItemContainer.ContainerPolicy;

/**
 * Manages a full trade session with another player.
 * 
 * @author lare96
 */
public class TradeSession {

    /**
     * The controller of this trade session.
     */
    private Player player;

    /**
     * A container of the items being offered by the controller of this trade
     * session.
     */
    private ItemContainer offering = new ItemContainer(ContainerPolicy.NORMAL_POLICY, 28);

    /**
     * The other player in this trade session.
     */
    private Player partner;

    /**
     * The stage this trade session is currently in.
     */
    private TradeStage stage;

    /**
     * If the player has accepted the trade offer.
     */
    private boolean acceptInitialOffer, acceptConfirmOffer;

    /**
     * Create a new {@link TradeSession}.
     * 
     * @param player
     *        the controller of this trade session.
     */
    public TradeSession(Player player) {
        this.player = player;
    }

    /**
     * The different stages in a trade session.
     * 
     * @author lare96
     */
    public enum TradeStage {
        REQUEST, OFFER, CONFIRM_OFFER
    }

    /**
     * The first stage of a trade session is the request. In this stage a
     * request is sent to the player to trade.
     * 
     * @param sending
     *        the player being sent this request.
     */
    public void request(Player sending) {

        /** Can't send a request to someone already in a trade. */
        if (inTrade()) {
            return;
        }

        /** If we are replying to a request start the trade. */
        if (sending.getTradeSession().getPartner() != null) {
            if (sending.getTradeSession().getPartner().getUsername().equals(player.getUsername())) {
                setPartner(sending);
                sending.getTradeSession().setPartner(player);

                setStage(TradeStage.OFFER);
                sending.getTradeSession().setStage(TradeStage.OFFER);

                firstOffer();
                sending.getTradeSession().firstOffer();
                return;
            }
        }

        /** If not then send a normal request. */
        setPartner(sending);
        setStage(TradeStage.REQUEST);
        player.getPacketBuilder().sendMessage("Sending trade request...");
        sending.getPacketBuilder().sendMessage(player.getUsername() + ":tradereq:");
    }

    /**
     * The second stage of a trade session is the first offer confirmation. In
     * this stage the items you wish to trade can be presented to the other
     * player through the trade screen.
     */
    public void firstOffer() {

        /** Set up the trade screen. */
        player.getPacketBuilder().sendUpdateItems(3322, player.getInventory().getContainer().toArray());

        String out = partner.getUsername();

        if (partner.getStaffRights() == 1) {
            out = "@cr1@" + out;
        } else if (partner.getStaffRights() == 2 || partner.getStaffRights() == 3) {
            out = "@cr2@" + out;
        }

        player.getPacketBuilder().sendString("Trading with: " + partner.getUsername() + " who has @gre@" + partner.getInventory().getContainer().freeSlots() + " free slots", 3417);
        player.getPacketBuilder().sendString("", 3431);
        player.getPacketBuilder().sendString("Are you sure you want to make this trade?", 3535);
        player.getPacketBuilder().sendInventoryInterface(3323, 3321);
    }

    /**
     * The third stage of a trade session is the final offer confirmation. In
     * this stage the items in the trade from both users are presented in a
     * textual form. This stage isn't even really a necessity, but exists merely
     * for security purposes.
     */
    public void confirmTrade() {

        /** Present your offer in a textual form. */
        player.getPacketBuilder().sendUpdateItems(3214, player.getInventory().getContainer().toArray());

        String tradeItems = "Absolutely nothing!";
        String tradeAmount = "";

        int count = 0;

        for (Item item : offering.toArray()) {
            if (item == null || item.getId() < 1 || item.getAmount() < 1) {
                continue;
            }

            if (item.getAmount() >= 1000 && item.getAmount() < 1000000) {
                tradeAmount = "@cya@" + (item.getAmount() / 1000) + "K @whi@(" + item.getAmount() + ")";
            } else if (item.getAmount() >= 1000000) {
                tradeAmount = "@gre@" + (item.getAmount() / 1000000) + " million @whi@(" + item.getAmount() + ")";
            } else {
                tradeAmount = "" + item.getAmount();
            }

            if (count == 0) {
                tradeItems = item.getDefinition().getItemName();
            } else {
                tradeItems = tradeItems + "\\n" + item.getDefinition().getItemName();
            }

            if (item.getDefinition().isStackable()) {
                tradeItems = tradeItems + " x " + tradeAmount;
            }

            count++;
        }

        /** Present your partners offer in a textual form. */
        player.getPacketBuilder().sendString(tradeItems, 3557);

        tradeItems = "Absolutely nothing!";
        tradeAmount = "";

        count = 0;

        for (Item item : partner.getTradeSession().getOffering().toArray()) {
            if (item == null || item.getId() < 1 || item.getAmount() < 1) {
                continue;
            }

            if (item.getAmount() >= 1000 && item.getAmount() < 1000000) {
                tradeAmount = "@cya@" + (item.getAmount() / 1000) + "K @whi@(" + item.getAmount() + ")";
            } else if (item.getAmount() >= 1000000) {
                tradeAmount = "@gre@" + (item.getAmount() / 1000000) + " million @whi@(" + item.getAmount() + ")";
            } else {
                tradeAmount = "" + item.getAmount();
            }

            if (count == 0) {
                tradeItems = item.getDefinition().getItemName();
            } else {
                tradeItems = tradeItems + "\\n" + item.getDefinition().getItemName();
            }

            if (item.getDefinition().isStackable()) {
                tradeItems = tradeItems + " x " + tradeAmount;
            }

            count++;
        }

        player.getPacketBuilder().sendString(tradeItems, 3558);
        player.getPacketBuilder().sendInventoryInterface(3443, 3213);
    }

    /**
     * The fourth and last stage of a trade is the item distribution and reset.
     * In this stage if both players have come to an agreement on what items
     * they want traded the appropriate items are handed out and everything is
     * reset.
     */
    public void finishTrade() {

        /** Distribute items. */
        for (Item item : offering.toArray()) {
            if (item == null) {
                continue;
            }

            partner.getInventory().addItem(item);
        }

        for (Item item : partner.getTradeSession().getOffering().toArray()) {
            if (item == null) {
                continue;
            }

            player.getInventory().addItem(item);
        }

        /** Close trade screen. */
        player.getPacketBuilder().closeWindows();
        partner.getPacketBuilder().closeWindows();

        /** Clear the offer containers. */
        partner.getTradeSession().getOffering().clear();
        offering.clear();

        /**
         * Update the trade screen with the empty offer containers (so next time
         * you open the trade interface you don't see items from the last
         * trade).
         */
        this.updateThisTrade();
        partner.getTradeSession().updateThisTrade();
        this.updateOtherTrade();
        partner.getTradeSession().updateOtherTrade();

        /** Reset the values in this trade session. */
        this.setAcceptInitialOffer(false);
        this.setAcceptConfirmOffer(false);
        partner.getTradeSession().setAcceptInitialOffer(false);
        partner.getTradeSession().setAcceptConfirmOffer(false);

        partner.getTradeSession().setStage(null);
        partner.getTradeSession().setPartner(null);
        setStage(null);
        setPartner(null);
    }

    /**
     * Gets if you are in a trade or not.
     * 
     * @return true if you're in a trade.
     */
    public boolean inTrade() {
        return player.getTradeSession().getStage() == TradeStage.OFFER || player.getTradeSession().getStage() == TradeStage.CONFIRM_OFFER ? true : false;
    }

    /**
     * Puts an item on the trade screen.
     * 
     * @param item
     *        the item to put on the trade screen.
     * @param slot
     *        the inventory slot it's coming from.
     */
    public void offer(Item item, int slot) {
        if (item.getId() < 1 || item.getAmount() < 1 || item == null) {
            return;
        }

        if (!player.getInventory().getContainer().contains(item.getId())) {
            return;
        }

        for (int i : Misc.ITEM_UNTRADEABLE) {
            if (i == item.getId()) {
                player.getPacketBuilder().sendMessage("You cannot trade this item.");
                return;
            }
        }

        if (item.getAmount() > player.getInventory().getContainer().getCount(item.getId()) && !item.getDefinition().isStackable()) {
            item.setAmount(player.getInventory().getContainer().getCount(item.getId()));
        } else if (item.getAmount() > player.getInventory().getContainer().getItem(slot).getAmount() && item.getDefinition().isStackable()) {
            item.setAmount(player.getInventory().getContainer().getItem(slot).getAmount());
        }

        player.getInventory().deleteItemSlot(item, slot);
        offering.add(item);

        partner.getPacketBuilder().sendString("Trading with: " + player.getUsername() + " who has @gre@" + player.getInventory().getContainer().freeSlots() + " free slots", 3417);
        player.getPacketBuilder().sendUpdateItems(3322, player.getInventory().getContainer().toArray());
        this.updateThisTrade();
        partner.getTradeSession().updateOtherTrade();
        player.getPacketBuilder().sendString("", 3431);
        partner.getPacketBuilder().sendString("", 3431);
    }

    /**
     * Takes an item off of the trade screen.
     * 
     * @param item
     *        the item to take off of the trade screen.
     */
    public void unoffer(Item item) {
        if (!offering.contains(item.getId())) {
            return;
        }

        if (item.getAmount() > offering.getCount(item.getId())) {
            item.setAmount(offering.getCount(item.getId()));
        }

        offering.remove(item);
        player.getInventory().addItem(item);

        partner.getPacketBuilder().sendString("Trading with: " + player.getUsername() + " who has @gre@" + player.getInventory().getContainer().freeSlots() + " free slots", 3417);
        player.getPacketBuilder().sendUpdateItems(3322, player.getInventory().getContainer().toArray());
        this.updateThisTrade();
        partner.getTradeSession().updateOtherTrade();
        player.getPacketBuilder().sendString("", 3431);
        partner.getPacketBuilder().sendString("", 3431);
    }

    /**
     * Resets the trade for the player and their partner.
     * 
     * @param declined
     *        if the trade was manually reset (by declining).
     */
    public void resetTrade(boolean declined) {
        if (!inTrade()) {
            return;
        }

        for (Item item : offering.toArray()) {
            if (item == null) {
                continue;
            }

            player.getInventory().addItem(item);
        }

        for (Item item : partner.getTradeSession().getOffering().toArray()) {
            if (item == null) {
                continue;
            }

            partner.getInventory().addItem(item);
        }

        player.getPacketBuilder().closeWindows();
        partner.getPacketBuilder().closeWindows();

        partner.getTradeSession().getOffering().clear();
        offering.clear();

        this.updateThisTrade();
        partner.getTradeSession().updateThisTrade();
        this.updateOtherTrade();
        partner.getTradeSession().updateOtherTrade();

        if (declined) {
            partner.getPacketBuilder().sendMessage("The other player has declined the trade!");
        }

        this.setAcceptInitialOffer(false);
        this.setAcceptConfirmOffer(false);
        partner.getTradeSession().setAcceptInitialOffer(false);
        partner.getTradeSession().setAcceptConfirmOffer(false);

        partner.getTradeSession().setStage(null);
        partner.getTradeSession().setPartner(null);
        setStage(null);
        setPartner(null);
    }

    /**
     * Updates the trading interface with the items from this player.
     */
    public void updateThisTrade() {
        PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(2048);
        out.writeVariableShortPacketHeader(53);
        out.writeShort(3415);
        out.writeShort(this.getThisTradeAmount());

        for (Item item : this.getOffering().toArray()) {
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
     * Updates the trading interface with the items from the trading partner.
     */
    public void updateOtherTrade() {
        PacketBuffer.WriteBuffer out = PacketBuffer.newOutBuffer(2048);
        out.writeVariableShortPacketHeader(53);
        out.writeShort(3416);
        out.writeShort(this.getOtherTradeAmount());

        for (Item item : partner.getTradeSession().getOffering().toArray()) {
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
     * @return the amount of different items in this player's container
     *         excluding null values.
     */
    private int getThisTradeAmount() {
        int total = 0;

        for (Item i : this.getOffering().toArray()) {
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
     * @return the amount of different items in this player's partner's
     *         container excluding null values.
     */
    private int getOtherTradeAmount() {
        int total = 0;

        for (Item i : partner.getTradeSession().getOffering().toArray()) {
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
     * Sets your partner in this trade session.
     * 
     * @param partner
     *        the new partner to set.
     */
    public void setPartner(Player partner) {
        this.partner = partner;
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
     * Sets a new stage for this trade session.
     * 
     * @param stage
     *        the new stage to set.
     */
    public void setStage(TradeStage stage) {
        this.stage = stage;
    }

    /**
     * Gets if this controller has accepted the initial offer.
     * 
     * @return true if they have.
     */
    public boolean isAcceptInitialOffer() {
        return acceptInitialOffer;
    }

    /**
     * Sets if this controller has accepted the initial offer.
     * 
     * @param acceptInitialOffer
     *        if they have accepted or not.
     */
    public void setAcceptInitialOffer(boolean acceptInitialOffer) {
        this.acceptInitialOffer = acceptInitialOffer;
    }

    /**
     * Gets if this controller has accepted the confirm offer.
     * 
     * @return true if they have.
     */
    public boolean isAcceptConfirmOffer() {
        return acceptConfirmOffer;
    }

    /**
     * Sets if this controller has accepted the confirm offer.
     * 
     * @param acceptConfirmOffer
     *        if they have accepted or not.
     */
    public void setAcceptConfirmOffer(boolean acceptConfirmOffer) {
        this.acceptConfirmOffer = acceptConfirmOffer;
    }
}
