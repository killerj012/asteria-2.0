package server.core.task;

import java.util.concurrent.CountDownLatch;

import server.Main;
import server.world.entity.player.Player;

/**
 * A concurrent task that resets a single {@link Player}.
 * 
 * @author lare96
 */
public class ParallelPlayerResetTask implements Runnable {

    /** The {@link Player} we need to reset. */
    private Player player;

    /**
     * The {@link CountDownLatch} being used to keep the main game thread in
     * sync with resetting.
     */
    private CountDownLatch updateLatch;

    /**
     * Create a new {@link ParallelPlayerResetTask}.
     * 
     * @param player
     *        the {@link Player} we need to reset.
     * @param updateLatch
     *        the {@link CountDownLatch} being used to keep the main game thread
     *        in sync with resetting.
     */
    public ParallelPlayerResetTask(Player player, CountDownLatch updateLatch) {
        this.player = player;
        this.updateLatch = updateLatch;
    }

    @Override
    public void run() {

        /**
         * Put a concurrent lock on the player we are currently resetting - so
         * only one thread in the pool can access this player at a time.
         */
        synchronized (player) {

            /** Now we actually reset the player. */
            try {
                player.reset();

                /** Handle any errors with the player. */
            } catch (Exception ex) {
                ex.printStackTrace();
                Main.getLogger().warning(player + " error while concurrently resetting for the next game tick!");
                player.getSession().disconnect();

                /** Count down the latch regardless if there was an error or not. */
            } finally {
                updateLatch.countDown();
            }
        }
    }
}
