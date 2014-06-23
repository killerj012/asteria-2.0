package server.world.entity.player.content;

import server.core.worker.WorkRate;
import server.core.worker.Worker;
import server.world.World;
import server.world.entity.player.Player;

/**
 * A {@link Worker} implementation that restores run energy for the player when
 * needed.
 * 
 * @author lare96
 */
public class RestoreEnergyWorker extends Worker {

    // TODO: Restoration formula for different agility levels?

    /**
     * Create a new {@link RestoreEnergyWorker}.
     * 
     * @param player
     *        the player we are restoring run energy for.
     */
    public RestoreEnergyWorker() {
        super(5, false, WorkRate.APPROXIMATE_SECOND);
    }

    @Override
    public void fire() {
        for (Player player : World.getPlayers()) {
            if (player == null) {
                continue;
            }

            /** Block if we have full run energy. */
            if (player.getRunEnergy() == 100) {
                return;
            }

            /** Restore energy whenever the player isn't running. */
            if (player.getMovementQueue().isMovementDone() || !player.getMovementQueue().isRunPath()) {
                player.incrementRunEnergy();
            }
        }
    }
}
