package com.asteria.world.entity.combat.prayer;

import com.asteria.engine.task.Task;
import com.asteria.util.Utility;
import com.asteria.world.entity.player.Player;
import com.asteria.world.entity.player.skill.Skills;

/**
 * A {@link Task} implementation that handles prayer draining. An array of
 * countdown integers determine when the {@link Player}'s prayer should be
 * drained.
 * 
 * @author lare96
 */
public class CombatPrayerTask extends Task {

    /** The array of countdown integers. */
    private int[] countdown = new int[18];

    /**
     * Flag that determines if the task should be cancelled. Determined by if
     * any prayers are active.
     */
    private boolean cancelTask = true;

    /** The player attached to this task. */
    private Player player;

    /**
     * Create a new {@link CombatPrayerTask}.
     * 
     * @param player
     *            the player attached to this task.
     */
    public CombatPrayerTask(Player player) {
        super(1, false);
        super.bind(player);
        this.player = player;
    }

    @Override
    public void execute() {

        // Decrement the countdown and drain prayer points if needed.
        for (int i = 0; i < player.getPrayerActive().length; i++) {
            if (player.getPrayerActive()[i]) {
                countdown[i]++;
                cancelTask = false;

                if (countdown[i] >= calculateDrainRate(CombatPrayer.get(i))) {
                    player.getSkills()[Skills.PRAYER].decreaseLevel(1);
                    Skills.refresh(player, Skills.PRAYER);
                    countdown[i] = 0;
                }
            }
        }

        // If there are no prayers active then stop the task.
        if (cancelTask) {
            this.cancel();
            return;
        }

        // Reset the flag for the next cycle.
        cancelTask = true;

        // Check the prayer level.
        if (player.getSkills()[Skills.PRAYER].getLevel() < 1) {
            player.getPacketBuilder().sendMessage(
                    "You've run out of prayer points!");
            CombatPrayer.deactivateAll(player);
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
        return (player.getBonus()[Utility.BONUS_PRAYER] / 2)
                + prayer.getDrainRate();
    }
}
