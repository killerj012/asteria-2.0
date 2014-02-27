package server.core.task;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import server.world.entity.npc.NpcUpdate;
import server.world.entity.player.Player;
import server.world.entity.player.PlayerUpdate;

/**
 * A concurrent task that performs updating on a single {@link Player}.
 * 
 * @author lare96
 */
public class ParallelPlayerUpdateTask implements Runnable {

    /** A {@link Logger} for printing debugging info. */
    private static Logger logger = Logger.getLogger(ParallelPlayerUpdateTask.class.getSimpleName());

    /** The {@link Player} to perform updating on. */
    private Player player;

    /**
     * The {@link CountDownLatch} being used to keep the main game thread in
     * sync with updating.
     */
    private CountDownLatch updateLatch;

    /**
     * Create a new {@link ParallelPlayerUpdateTask}.
     * 
     * @param player
     *        the {@link Player} to perform updating on.
     * @param updateLatch
     *        the {@link CountDownLatch} being used to keep the main game thread
     *        in sync with updating.
     */
    public ParallelPlayerUpdateTask(Player player, CountDownLatch updateLatch) {
        this.player = player;
        this.updateLatch = updateLatch;
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

                /** Count down the latch regardless if there was an error or not. */
            } finally {
                updateLatch.countDown();
            }
        }
    }
}
