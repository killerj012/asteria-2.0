package server.world.entity.player.content;

import server.core.worker.Worker;
import server.util.Misc;
import server.world.entity.player.Player;

/**
 * A {@link DynamicTask} implementation that restores run energy for the player
 * when needed.
 * 
 * @author lare96
 */
public class DynamicEnergyTask extends Worker {

    /**
     * The player we are restoring run energy for.
     */
    private Player player;

    /**
     * Create a new {@link DynamicEnergyTask}.
     * 
     * @param player
     *        the player we are restoring run energy for.
     */
    public DynamicEnergyTask(Player player) {
        super(restorationRate(player), false);
        super.attach(player);
        this.player = player;
    }

    @Override
    public void fire() {

        /** Block if we have full run energy. */
        if (player.getRunEnergy() == 100) {
            return;
        }

        /** Modify restoration rate based on agility level. */
        this.setDelay(restorationRate(player));

        /** Restore energy whenever the player isn't running. */
        if (player.getMovementQueue().isMovementDone() || !player.getMovementQueue().isRunPath()) {
            player.incrementRunEnergy();
        }
    }

    /**
     * Calculate the rate of restoration based on your agility level.
     * 
     * @param player
     *        the player we are restoring run energy for.
     * @return the rate of restoration in ticks.
     */
    private static int restorationRate(Player player) {
        int level = player.getSkills()[Misc.AGILITY].getLevel();

        if (level > 0 && level <= 25) {
            return 7;
        } else if (level > 25 && level <= 50) {
            return 5;
        } else if (level > 50 && level <= 75) {
            return 3;
        } else if (level > 75) {
            return 2;
        }
        return 7;
    }
}
