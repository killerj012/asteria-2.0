package server.world.item.ground;

import server.core.worker.TaskFactory;
import server.core.worker.Worker;
import server.world.World;
import server.world.entity.player.Player;
import server.world.item.Item;
import server.world.map.Position;

/**
 * A static implementation of a {@link GroundItem} that has no owner and is
 * visible to everyone from the moment of conception.
 * 
 * @author lare96
 */
public class StaticGroundItem extends GroundItem {

    /**
     * If this item should be removed like a normal {@link GroundItem} (by the
     * assigned {@link Worker} after a certain amount of time has elapsed).
     */
    private boolean removeOnProcess;

    /** If this item should respawn once picked up. */
    private boolean respawnOnPickup;

    /** If this item is awaiting re-registration. */
    private boolean needsRespawn;

    /**
     * Create a new {@link StaticGroundItem}.
     * 
     * @param item
     *        the actual item.
     * @param position
     *        the position of the item.
     * @param removeOnProcess
     *        if this item should be removed like a normal world item.
     * @param respawnOnPickup
     *        if this item should respawn once picked up.
     */
    public StaticGroundItem(Item item, Position position, boolean removeOnProcess, boolean respawnOnPickup) {
        super(item, position, null);

        /**
         * Having these two conditions flagged at the same time is illogical and
         * will cause issues so we throw an exception if an attempt is made to
         * flag both.
         */
        if (removeOnProcess && respawnOnPickup) {
            throw new IllegalStateException("Static ground items cannot be configured to be removed and respawned at the same time!");
        }

        /** Set the appropriate values. */
        this.removeOnProcess = removeOnProcess;
        this.respawnOnPickup = respawnOnPickup;
        setState(null);
    }

    @Override
    protected void fireOnRegister() {

        /** Send the item image for everyone. */
        for (Player p : World.getPlayers()) {
            if (p == null) {
                continue;
            }

            p.getPacketBuilder().sendGroundItem(this);
        }

        /** Start the <code>processor</code> if needed. */
        if (removeOnProcess) {
            TaskFactory.getFactory().submit(getProcessor());
        }
    }

    @Override
    protected void fireOnUnregister() {

        /** Cancel the processor. */
        getProcessor().cancel();

        /** Remove the item image for everyone. */
        for (Player p : World.getPlayers()) {
            if (p == null) {
                continue;
            }

            p.getPacketBuilder().removeGroundItem(this);
        }
    }

    @Override
    protected void fireOnProcess() {

        /** If this item was set to be removed after a delay do so now. */
        if (removeOnProcess) {
            World.getGroundItems().unregister(this);
            return;
        }

        /**
         * If this item needs respawning do that now.
         */
        if (isItemPicked() && respawnOnPickup && needsRespawn) {
            for (Player p : World.getPlayers()) {
                if (p == null) {
                    continue;
                }

                p.getPacketBuilder().sendGroundItem(this);
            }

            World.getGroundItems().getItemList().add(this);
            needsRespawn = false;
            setItemPicked(false);
            getProcessor().cancel();
        }
    }

    @Override
    protected void fireOnPickup(Player player) {
        if (!isItemPicked()) {
            setItemPicked(true);

            /** Remove the item image for everyone. */
            for (Player p : World.getPlayers()) {
                if (p == null) {
                    continue;
                }

                p.getPacketBuilder().removeGroundItem(this);
            }

            /** Remove the item from the database. */
            World.getGroundItems().getItemList().remove(this);

            /** Add the item in the player's inventory. */
            player.getInventory().addItem(getItem());

            /**
             * Cancel the worker - we don't need it anymore because the item was
             * picked up.
             */
            if (removeOnProcess) {
                getProcessor().cancel();
            }

            /** Submit a new worker to respawn the item if needed. */
            if (respawnOnPickup) {
                setProcessor(new GroundItemWorker(this));
                TaskFactory.getFactory().submit(getProcessor());
                needsRespawn = true;
            }
        }
    }
}
