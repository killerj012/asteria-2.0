package server.world.item.ground;

import server.core.Rs2Engine;
import server.world.entity.player.Player;
import server.world.item.Item;
import server.world.map.Position;

/**
 * A static {@link GroundItem} that is visible to everyone from the moment of
 * conception and can be placed anywhere in the rs2 world.
 * 
 * @author lare96
 */
public class StaticGroundItem extends GroundItem {

    // TODO: items that respawn on pickup respawn really fast?
    // TODO: test delay remove and respawn on pickup together

    /**
     * If this item should be removed like a normal {@link GroundItem} (by the
     * processor after a certain amount of time has elapsed).
     */
    private boolean delayRemove;

    /**
     * If this item should respawn once picked up.
     */
    private boolean respawnOnPickup;

    /**
     * If this item needs to be respawned.
     */
    private boolean needsRespawn;

    /**
     * Create a new {@link StaticGroundItem}.
     * 
     * @param item
     *        the actual item.
     * @param position
     *        the position of the item.
     * @param delayRemove
     *        if this item should be removed like a normal world item.
     * @param respawnOnPickup
     *        if this item should respawn once picked up.
     */
    public StaticGroundItem(Item item, Position position, boolean delayRemove, boolean respawnOnPickup) {
        super(item, position, null);
        this.delayRemove = delayRemove;
        this.respawnOnPickup = respawnOnPickup;
        setState(null);
    }

    @Override
    protected void fireOnRegister() {

        /** Send the item image for everyone. */
        for (Player p : Rs2Engine.getWorld().getPlayers()) {
            if (p == null) {
                continue;
            }

            p.getPacketBuilder().sendGroundItem(this);
        }

        /** Start the <code>processor</code> if needed. */
        if (delayRemove) {
            Rs2Engine.getWorld().submit(getProcessor());
        }
    }

    @Override
    protected void fireOnUnregister() {

        /** Cancel the processor. */
        getProcessor().cancel();

        /** Remove the item image for everyone. */
        for (Player p : Rs2Engine.getWorld().getPlayers()) {
            if (p == null) {
                continue;
            }

            p.getPacketBuilder().removeGroundItem(this);
        }
    }

    @Override
    protected void fireOnProcess() {

        /** If this item was set to be removed after a delay do so now. */
        if (delayRemove && !needsRespawn) {
            getRegisterable().unregister(this);
            return;
        }

        /**
         * If this item needs respawning do that now.
         */
        if (isItemPicked() && respawnOnPickup && needsRespawn) {
            for (Player p : Rs2Engine.getWorld().getPlayers()) {
                if (p == null) {
                    continue;
                }

                p.getPacketBuilder().sendGroundItem(this);
            }

            getRegisterable().getItemList().add(this);
            needsRespawn = false;
            setItemPicked(false);
        }
    }

    @Override
    protected void fireOnPickup(Player player) {
        if (!isItemPicked()) {
            setItemPicked(true);

            /** Remove the item image for everyone. */
            for (Player p : Rs2Engine.getWorld().getPlayers()) {
                if (p == null) {
                    continue;
                }

                p.getPacketBuilder().removeGroundItem(this);
            }

            /** Remove the item from the database. */
            getRegisterable().getItemList().remove(this);

            /** Add the item in the player's inventory. */
            player.getInventory().addItem(getItem());

            /**
             * Cancel the <code>processor</code> and create a new one if
             * needed.
             */
            if (delayRemove) {
                getProcessor().cancel();
                setProcessor(new GroundItemWorker(this));
            }

            /**
             * Schedule a new <code>processor</code> to respawn the item if
             * needed.
             */
            if (respawnOnPickup) {
                Rs2Engine.getWorld().submit(getProcessor());
                needsRespawn = true;
            }
        }
    }
}
