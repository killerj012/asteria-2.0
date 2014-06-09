package server.world.entity.player;

import java.util.concurrent.Phaser;
import java.util.logging.Logger;

import server.core.Service;
import server.world.entity.npc.NpcUpdate;

/**
 * A concurrent task that performs updating on a single {@link Player}.
 * 
 * @author lare96
 */
public class PlayerParallelUpdateService implements Service {

    /** A {@link Logger} for printing debugging info. */
    private static Logger logger = Logger.getLogger(PlayerParallelUpdateService.class.getSimpleName());

    /** The {@link Player} to perform updating on. */
    private Player player;

    /**
     * The {@link Phaser} being used to allow the main game thread to wait for
     * updating to complete.
     */
    private Phaser phaser;

    /**
     * Create a new {@link PlayerParallelUpdateService}.
     * 
     * @param player
     *        the player to perform updating on.
     * @param phaser
     *        the phaser being used to keep the main game thread in sync with
     *        updating.
     */
    public PlayerParallelUpdateService(Player player, Phaser phaser) {
        this.player = player;
        this.phaser = phaser;
    }

    @Override
    public void run() {

        /**
         * Put a concurrent lock on the player we are currently updating - so
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
                logger.warning(player + " error while updating concurrently!");
                player.getSession().disconnect();

                /**
                 * Arrive at the phaser regardless if there was an error or not.
                 */
            } finally {
                phaser.arrive();
            }
        }
    }

    @Override
    public String name() {
        return PlayerParallelUpdateService.class.getSimpleName();
    }
}
