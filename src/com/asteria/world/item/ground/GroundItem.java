package com.asteria.world.item.ground;

import java.util.Iterator;

import com.asteria.world.World;
import com.asteria.world.entity.player.Player;
import com.asteria.world.item.Item;
import com.asteria.world.map.Position;

/**
 * An {@link Item} that can be placed anywhere in the {@link World}.
 * 
 * @author lare96
 */
public class GroundItem {

    /** The actual {@link Item} on the ground. */
    private Item item;

    /** The {@link Position} of the item. */
    private Position position;

    /** The {@link Player} who can see this item. */
    private Player player;

    /** The current {@link ItemState} of this item. */
    private ItemState state;

    /** The amount of ticks this item holds. */
    protected int ticks;

    /**
     * All of the possible states this item can be in.
     * 
     * @author lare96
     */
    public enum ItemState {

        /** This item is only visible to the player registered with it. */
        SEEN_BY_OWNER,

        /** This item is visible to everyone. */
        SEEN_BY_EVERYONE,

        /** This item is in the database but is not displayed to players. */
        HIDDEN
    }

    /**
     * Create a new {@link GroundItem}.
     * 
     * @param item
     *            the actual item on the ground.
     * @param position
     *            the position of the item.
     * @param player
     *            the player who can see this item.
     */
    public GroundItem(Item item, Position position, Player player) {
        this.item = item.clone();
        this.position = position.clone();
        this.player = player;
        this.state = ItemState.SEEN_BY_OWNER;
    }

    /** An event fired when this ground item is unregistered. */
    protected final void fireOnUnregister() {

        // Removes the ground item image based on the state of the item.
        switch (state) {
        case SEEN_BY_EVERYONE:
            for (Player player : World.getPlayers()) {
                if (player == null) {
                    continue;
                }

                if (player.getPosition().withinDistance(getPosition(), 60)) {
                    player.getPacketBuilder().sendRemoveGroundItem(this);
                }
            }
            break;
        case SEEN_BY_OWNER:
            World.getPlayerByHash(player.getUsernameHash()).getPacketBuilder()
                    .sendRemoveGroundItem(this);
            break;
        }
    }

    /** An event fired upon registration of this item. */
    protected void fireOnRegister() {

        // Send the ground item image.
        player.getPacketBuilder().sendGroundItem(this);
    }

    /**
     * An event fired by the {@link GroundItemManager} every minute.
     * 
     * @param it
     *            the iterator being used to iterate through the ground items.
     */
    protected void fireOnProcess(Iterator<GroundItem> it) {

        // After 1 minute show the item for everyone, and after two minutes
        // remove the item.
        switch (state) {
        case SEEN_BY_OWNER:
            for (Player p : World.getPlayers()) {
                if (p == null || p.equals(player)) {
                    continue;
                }

                if (p.getPosition().withinDistance(getPosition(), 60)) {
                    p.getPacketBuilder().sendGroundItem(
                            new GroundItem(item, position, null));
                }
            }
            player = null;
            state = ItemState.SEEN_BY_EVERYONE;
            break;
        case SEEN_BY_EVERYONE:
            fireOnUnregister();
            it.remove();
            break;
        }
    }

    /**
     * An event fired when this item is picked up.
     * 
     * @param player
     *            the player who is trying pick this item up.
     */
    public void fireOnPickup(Player player) {
        GroundItemManager.unregister(this);
        player.getInventory().addItem(item);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof GroundItem) {
            GroundItem w = (GroundItem) obj;
            if (w.item.equals(item) && w.position.equals(position) && w.state == state && w.ticks == ticks) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "GROUND ITEM[item= " + item + ", position= " + position + ", player= " + player + ", state= " + state + ", ticks= " + ticks + "]";
    }

    /**
     * Gets the actual item.
     * 
     * @return the actual item.
     */
    public final Item getItem() {
        return item;
    }

    /**
     * Gets the position of the item.
     * 
     * @return the position of the item.
     */
    public final Position getPosition() {
        return position;
    }

    /**
     * Gets the player who can see this item.
     * 
     * @return the player who can see this item.
     */
    public final Player getPlayer() {
        return player;
    }

    /**
     * Gets the state of this item.
     * 
     * @return the state of this item.
     */
    public final ItemState getState() {
        return state;
    }

    /**
     * A static implementation of a {@link GroundItem} that has no owner and is
     * visible to everyone from the moment of conception.
     * 
     * @author lare96
     */
    public static class StaticGroundItem extends GroundItem {

        /** The policy of this item. */
        private ItemPolicy policy;

        /** If this item is awaiting re-registration. */
        private boolean needsRespawn;

        /**
         * All of the policies this ground item can take on.
         * 
         * @author lare96
         */
        public enum ItemPolicy {

            /** This item will be removed like a normal ground item. */
            REMOVE,

            /** This item will respawn once picked up. */
            RESPAWN
        }

        /**
         * Create a new {@link StaticGroundItem}.
         * 
         * @param item
         *            the actual item on the ground.
         * @param position
         *            the position of the item.
         * @param policy
         *            the policy of this item.
         */
        public StaticGroundItem(Item item, Position position, ItemPolicy policy) {
            super(item, position, null);
            super.state = ItemState.SEEN_BY_EVERYONE;
            this.policy = policy;
        }

        /**
         * Create a new {@link StaticGroundItem} with the default policy.
         * 
         * @param item
         *            the actual item on the ground.
         * @param position
         *            the position of the item.
         */
        public StaticGroundItem(Item item, Position position) {
            this(item, position, ItemPolicy.REMOVE);
        }

        @Override
        protected void fireOnRegister() {

            // Send the item image for everyone.
            for (Player p : World.getPlayers()) {
                if (p == null) {
                    continue;
                }

                if (p.getPosition().withinDistance(getPosition(), 60)) {
                    p.getPacketBuilder().sendGroundItem(this);
                }
            }
        }

        @Override
        protected void fireOnProcess(Iterator<GroundItem> it) {

            // Process the item based on its policy: either remove this item
            // from or add it back to the ground item database.
            switch (policy) {
            case REMOVE:
                fireOnUnregister();
                it.remove();
                break;
            case RESPAWN:
                if (needsRespawn) {
                    fireOnRegister();
                    needsRespawn = false;
                    super.state = ItemState.SEEN_BY_EVERYONE;
                }
                break;
            }
        }

        @Override
        public void fireOnPickup(Player player) {

            // Fire pickup events based on the policy.
            switch (policy) {
            case REMOVE:
                GroundItemManager.unregister(this);
                break;
            case RESPAWN:
                fireOnUnregister();
                needsRespawn = true;
                super.state = ItemState.HIDDEN;
                break;
            }

            // Add the item to the player's inventory regardless.
            player.getInventory().addItem(super.item);
        }
    }
}
