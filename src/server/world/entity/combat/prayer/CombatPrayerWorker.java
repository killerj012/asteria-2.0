package server.world.entity.combat.prayer;

import server.core.worker.Worker;
import server.util.Misc;
import server.world.entity.player.Player;
import server.world.entity.player.skill.SkillManager;

/**
 * A {@link Worker} implementation that handles prayer draining.
 * 
 * @author lare96
 */
public class CombatPrayerWorker extends Worker {

    /** Holds the drain rate for every prayer. */
    private int[] prayerTicks = new int[18];

    /** Flag that determines if any prayers are active. */
    private boolean cancelWorker = true;

    /** The player attached to this worker. */
    private Player player;

    /**
     * Create a new {@link CombatPrayerWorker}.
     * 
     * @param player
     *            the player attached to this worker.
     */
    public CombatPrayerWorker(Player player) {
        super(1, false);
        super.attach(player);
        this.player = player;
    }

    @Override
    public void fire() {

        /** Drain the prayer if needed. */
        for (int i = 0; i < player.getPrayerActive().length; i++) {
            if (player.getPrayerActive()[i]) {
                prayerTicks[i]++;
                cancelWorker = false;

                if (prayerTicks[i] >= calculateDrainRate(CombatPrayer
                        .getPrayer(i))) {
                    player.getSkills()[Misc.PRAYER].decreaseLevel(1);
                    SkillManager.refresh(player, Misc.PRAYER);
                    prayerTicks[i] = 0;
                }
            }
        }

        /** If there are no prayers active then stop the task. */
        if (cancelWorker) {
            this.cancel();
            return;
        }

        /** Reset the flag for the next cycle. */
        cancelWorker = true;

        /** Check the prayer level. */
        if (player.getSkills()[Misc.PRAYER].getLevel() < 1) {
            player.getPacketBuilder().sendMessage(
                    "You've run out of prayer points!");
            CombatPrayer.deactivateAllPrayer(player);
            this.cancel();
            return;
        }
    }

    /**
     * Calculates the amount of ticks needed to drain 1 level of prayer.
     * 
     * @param prayer
     *            the prayer being calculated.
     * @return the amount of ticks needed to drain 1 level of prayer.
     */
    private int calculateDrainRate(CombatPrayer prayer) {
        return (player.getPlayerBonus()[Misc.BONUS_PRAYER] / 3)
                + prayer.getDrainRate();
    }
}
