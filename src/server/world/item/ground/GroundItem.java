package server.world.item.ground;

import server.core.worker.TaskFactory;
import server.core.worker.WorkRate;
import server.core.worker.Worker;
import server.world.World;
import server.world.entity.player.Player;
import server.world.item.Item;
import server.world.map.Position;

/**
 * A ground item that can be placed anywhere in the world.
 * 
 * @author lare96
 */
public class GroundItem {

    // XXX: afaik I've fixed all of the issues with this, its still a work in
    // progress though so I'm wondering if there were any bugs I missed. Design
    // is a ton better than it was in my previous release, and written in less
    // code as well :)

    /** The actual {@link Item} on the ground. */
    private Item item;

    /** The {@link Position} of the item. */
    private Position position;

    /** The {@link Player} who owns this item. */
    private Player player;

    /** The current state of this item. */
    private ItemState state;

    /** The {@link Worker} assigned to fire events for this item. */
    private Worker processor;

    /** Flag that determines whether this item has been picked up or not. */
    private boolean itemPicked;

    /**
     * All of the possible states this item can be in.
     * 
     * @author lare96
     */
    public enum ItemState {

        /** This item is only visible to the {@link Player} registered with it. */
        SEEN_BY_OWNER,

        /** This item is visible to everyone. */
        SEEN_BY_EVERYONE,

        /** This item is visible to no one and is awaiting unregistration. */
        SEEN_BY_NO_ONE
    }

    /**
     * Create a new {@link GroundItem}.
     * 
     * @param item
     *        the actual item.
     * @param position
     *        the position of the item.
     * @param player
     *        the player who owns this item.
     */
    public GroundItem(Item item, Position position, Player player) {
        this.item = item;
        this.position = position;
        this.player = player;
        this.state = ItemState.SEEN_BY_OWNER;
        this.processor = new GroundItemWorker(this);
    }

    /**
     * An asynchronous event fired upon registration of this item.
     */
    protected void fireOnRegister() {

        /** Send the ground item image. */
        player.getPacketBuilder().sendGroundItem(this);

        /** Start processing for this item. */
        TaskFactory.getFactory().submit(processor);
    }

    /**
     * An asynchronous event fired upon the unregistration of this item.
     */
    @SuppressWarnings("incomplete-switch")
    protected void fireOnUnregister() {

        /** Cancels the processing for this item. */
        processor.cancel();

        /** Removes the ground item image depending on the state of the item. */
        switch (state) {
            case SEEN_BY_NO_ONE:
                for (Player p : World.getPlayers()) {
                    if (p == null) {
                        continue;
                    }

                    p.getPacketBuilder().removeGroundItem(this);
                }
                break;
            case SEEN_BY_OWNER:
                World.getPlayer(player.getUsername()).getPacketBuilder().removeGroundItem(this);
                break;
        }
    }

    /**
     * An asynchronous event fired by the assigned {@link Worker} at 1-minute
     * intervals.
     */
    @SuppressWarnings("fallthrough")
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

                for (Player p : World.getPlayers()) {
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

                World.getGroundItems().unregister(this);
                break;
        }
    }

    /**
     * An asynchronous event fired when a {@link Player} tries to pick this item
     * up.
     * 
     * @param player
     *        the player trying to pick this item up.
     */
    protected void fireOnPickup(Player player) {
        if (!itemPicked) {
            itemPicked = true;
            World.getGroundItems().unregister(this);
            player.getInventory().addItem(item);
        }
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
     * A {@link Worker} implementation that fires processing events for its
     * designated {@link GroundItem} at 1-minute intervals. This takes away the
     * need for iterating through a database of items every tick.
     * 
     * @author lare96
     */
    protected static class GroundItemWorker extends Worker {

        /**
         * The {@link GroundItem} this worker has been assigned to fire events
         * for.
         */
        private GroundItem item;

        /**
         * Create a new {@link GroundItemWorker}.
         * 
         * @param item
         *        the item this worker has been assigned to fire events for.
         */
        public GroundItemWorker(GroundItem item) {
            super(1, false, WorkRate.EXACT_MINUTE);
            this.item = item;
        }

        @Override
        public void fire() {

            /** Fire the processing event for this item. */
            item.fireOnProcess();
        }
    }
}
