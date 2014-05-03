package server.world.entity.combat.prayer;

import server.core.worker.Worker;
import server.util.Misc;
import server.world.entity.player.Player;
import server.world.entity.player.skill.SkillManager;
import server.world.entity.player.skill.SkillManager.SkillConstant;

/**
 * A {@link Worker} implementation that handles prayer draining.
 * 
 * @author lare96
 */
public class CombatPrayerWorker extends Worker {

    /** The amount of prayer that will be drained this cycle. */
    private int drainRate;

    /** The player attached to this worker. */
    private Player player;

    /**
     * Create a new {@link CombatPrayerWorker}.
     * 
     * @param player
     *        the player attached to this worker.
     */
    public CombatPrayerWorker(Player player) {
        super(20, false);
        super.attach(player);
        this.player = player;
    }

    @Override
    public void fire() {

        /** Determine the drain rate for this cycle. */
        for (int i = 0; i < player.getPrayerActive().length; i++) {
            if (player.getPrayerActive()[i]) {
                drainRate += CombatPrayer.getPrayer(i).getDrainRate();
            }
        }

        /** If there are no prayers active then stop the task. */
        if (drainRate == 0) {
            this.cancel();
            return;
        }

        /** If there are prayers active then drain the specified amount. */
        player.getSkills()[Misc.PRAYER].decreaseLevel(drainRate);
        SkillManager.refresh(player, SkillConstant.PRAYER);

        /** Reset the drain rate for the next cycle. */
        drainRate = 0;

        /** Check the prayer level. */
        if (player.getSkills()[Misc.PRAYER].getLevel() < 1) {
            player.getPacketBuilder().sendMessage("You've run out of prayer points!");
            CombatPrayer.deactivateAllPrayer(player);
            this.cancel();
            return;
        }
    }
}
