package server.world.item.ground;

import server.core.Rs2Engine;
import server.core.worker.Worker;
import server.world.Registerable;
import server.world.entity.player.Player;
import server.world.item.Item;
import server.world.map.Position;

/**
 * A registerable ground item that can be placed anywhere in the rs2 world. Each
 * item is assigned a {@link Worker} that asynchronously fires processing events
 * for it in 1-minute intervals.
 * 
 * @author lare96
 */
@SuppressWarnings( { "fallthrough", "incomplete-switch" })
public class GroundItem implements Registerable {

    // TODO: test items spawning twice
    // TODO: stopwatch for items!

    /** The registerable container. */
    private static RegisterableGroundItem registerable;

    /** The actual item. */
    private Item item;

    /** The position of the item. */
    private Position position;

    /** The controller of this item. */
    private Player player;

    /** The current state of this item. */
    private ItemState state;

    /** The {@link Worker} assigned to process this item. */
    private Worker processor;

    /** Flag that determines whether this item has been picked up or not. */
    private boolean itemPicked;

    /**
     * All of the possible states this item can be in.
     * 
     * @author lare96
     */
    public enum ItemState {
        SEEN_BY_OWNER, SEEN_BY_EVERYONE, SEEN_BY_NO_ONE
    }

    /**
     * Create a new {@link GroundItem}.
     * 
     * @param item
     *        the actual item.
     * @param position
     *        the position of the item.
     * @param player
     *        the controller of this item.
     */
    public GroundItem(Item item, Position position, Player player) {
        this.item = item;
        this.position = position;
        this.player = player;
        this.state = ItemState.SEEN_BY_OWNER;
        this.processor = new GroundItemWorker(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GroundItem) {
            GroundItem w = (GroundItem) obj;
            if (w.getItem().equals(item) && w.getPosition().equals(position)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method fired on the registration of this item.
     */
    protected void fireOnRegister() {

        /** Send the ground item image. */
        player.getPacketBuilder().sendGroundItem(this);

        /** Start processing for this item. */
        Rs2Engine.getWorld().submit(processor);
    }

    /**
     * Method fired on the unregistration of this item.
     */
    protected void fireOnUnregister() {

        /** Cancels the processing for this item. */
        processor.cancel();

        /** Removes the ground item image. */
        switch (state) {
            case SEEN_BY_NO_ONE:
                for (Player p : Rs2Engine.getWorld().getPlayers()) {
                    if (p == null) {
                        continue;
                    }

                    p.getPacketBuilder().removeGroundItem(this);
                }
                break;
            case SEEN_BY_OWNER:
                Rs2Engine.getWorld().getPlayer(player.getUsername()).getPacketBuilder().removeGroundItem(this);
                break;
        }
    }

    /**
     * Method fired by the <code>processor</code> at 1-minute intervals.
     */
    protected void fireOnProcess() {
        switch (state) {

            /** Change the state. */
            case SEEN_BY_OWNER:
                state = ItemState.SEEN_BY_EVERYONE;

                /** Show the item for everyone. */
            case SEEN_BY_EVERYONE:
                if (itemPicked) {
                    break;
                }

                for (Player p : Rs2Engine.getWorld().getPlayers()) {
                    if (p == null || p.getUsername().equals(player.getUsername())) {
                        continue;
                    }

                    p.getPacketBuilder().sendGroundItem(new GroundItem(item, position, player));
                }

                player = null;
                state = ItemState.SEEN_BY_NO_ONE;
                break;

            /** Show the item for no one. */
            case SEEN_BY_NO_ONE:
                if (itemPicked) {
                    break;
                }

                registerable.unregister(this);
                break;
        }
    }

    /**
     * Method fired when a player tries to pick this item up.
     * 
     * @param player
     *        the player trying to pick this item up.
     */
    protected void fireOnPickup(Player player) {
        if (!itemPicked) {
            itemPicked = true;
            registerable.unregister(this);
            player.getInventory().addItem(item);
        }
    }

    /**
     * Gets the actual item.
     * 
     * @return the actual item instance.
     */
    public Item getItem() {
        return item;
    }

    /**
     * Gets the position of the item.
     * 
     * @return the position.
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Gets the player controlling this item.
     * 
     * @return the player controlling this item.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Get the current state of this item.
     * 
     * @return the state of this item.
     */
    public ItemState getState() {
        return state;
    }

    /**
     * Sets the current state of this item.
     * 
     * @param state
     *        the new state to set.
     */
    protected void setState(ItemState state) {
        this.state = state;
    }

    /**
     * Gets the {@link Worker} performing processing on this item.
     * 
     * @return the processor.
     */
    public Worker getProcessor() {
        return processor;
    }

    /**
     * Sets the {@link Worker} performing processing on this item.
     * 
     * @param processor
     *        the new processor to set.
     */
    protected void setProcessor(Worker processor) {
        this.processor = processor;
    }

    /**
     * Gets if this item was picked up.
     * 
     * @return true if the item was picked up.
     */
    public boolean isItemPicked() {
        return itemPicked;
    }

    /**
     * Sets if this item was picked up.
     * 
     * @param itemPicked
     *        if this item was picked up.
     */
    protected void setItemPicked(boolean itemPicked) {
        this.itemPicked = itemPicked;
    }

    /**
     * Gets the registerable container.
     * 
     * @return the registerable container.
     */
    public static RegisterableGroundItem getRegisterable() {
        if (registerable == null) {
            registerable = new RegisterableGroundItem();
        }

        return registerable;
    }
}
