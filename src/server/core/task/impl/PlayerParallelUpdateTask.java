package server.core.task.impl;

import java.util.concurrent.Phaser;

import server.core.task.ConcurrentTask;
import server.world.entity.npc.NpcUpdate;
import server.world.entity.player.Player;
import server.world.entity.player.PlayerUpdate;

/**
 * A concurrent task that performs updating on a single {@link Player}.
 * 
 * @author lare96
 */
public class PlayerParallelUpdateTask extends ConcurrentTask {

    /** The {@link Player} to perform updating on. */
    private Player player;

    /**
     * The {@link Phaser} being used to allow the main game thread to wait for
     * updating to complete.
     */
    private Phaser phaser;

    /**
     * Create a new {@link PlayerParallelUpdateTask}.
     * 
     * @param player
     *            the player to perform updating on.
     * @param phaser
     *            the phaser being used to keep the main game thread in sync
     *            with updating.
     */
    public PlayerParallelUpdateTask(Player player, Phaser phaser) {
        this.player = player;
        this.phaser = phaser;
    }

    @Override
    public void run() {

        /**
         * Put a concurrent lock on the player we are currently updating, so
         * only one thread in the pool can access this player at a time.
         */
        synchronized (player) {

            /** Now we actually update the player. */
            try {
                PlayerUpdate.update(player);
                NpcUpdate.update(player);

                /** Handle any errors with the player. */
            } catch (Exception ex) {
                ex.printStackTrace();
                player.getSession().disconnect();

                /** Arrive at the phaser regardless if there was an error. */
            } finally {
                phaser.arrive();
            }
        }
    }
}
