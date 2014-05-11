package server.world.entity.player.content;

import server.core.worker.WorkRate;
import server.core.worker.Worker;
import server.util.Misc;
import server.world.entity.combat.prayer.CombatPrayer;
import server.world.entity.player.Player;
import server.world.entity.player.skill.SkillManager;
import server.world.entity.player.skill.SkillManager.SkillConstant;

/**
 * A {@link Worker} implementation that restores boosted and weakened stats.
 * 
 * @author lare96
 */
public class RestoreStatWorker extends Worker {

    /** The player that will be attached to this worker. */
    private Player player;

    /**
     * Create a new {@link RestoreStatWorker}.
     * 
     * @param player
     *        the player that will be attached to this worker.
     */
    public RestoreStatWorker(Player player) {
        super(15, false, WorkRate.APPROXIMATE_SECOND);
        super.attach(player);
        this.player = player;
    }

    @Override
    public void fire() {

        for (int i = 0; i < player.getSkills().length; i++) {

            /**
             * Check all weakened stats taking into account the rapid restore
             * prayer.
             */
            if (player.getSkills()[i].getLevel() < player.getSkills()[i].getLevelForExperience() && i != Misc.HITPOINTS && i != Misc.PRAYER) {
                player.getSkills()[i].increaseLevel(1);

                if (CombatPrayer.isPrayerActivated(player, CombatPrayer.RAPID_RESTORE)) {
                    if (player.getSkills()[i].getLevel() < player.getSkills()[i].getLevelForExperience()) {
                        player.getSkills()[i].increaseLevel(1);
                    }
                }
                SkillManager.refresh(player, SkillConstant.getSkill(i));

                /** Check all boosted stats. */
            } else if (player.getSkills()[i].getLevel() > player.getSkills()[i].getLevelForExperience() && i != Misc.HITPOINTS && i != Misc.PRAYER) {
                player.getSkills()[i].decreaseLevel(1);
                SkillManager.refresh(player, SkillConstant.getSkill(i));
            }
        }

        /**
         * Check the hp level and increased if needed taking into account the
         * rapid heal prayer.
         */
        if (player.getSkills()[Misc.HITPOINTS].getLevel() < player.getSkills()[Misc.HITPOINTS].getLevelForExperience() && player.isAcceptAid()) {
            player.getSkills()[Misc.HITPOINTS].increaseLevel(1);

            if (CombatPrayer.isPrayerActivated(player, CombatPrayer.RAPID_HEAL)) {
                if (player.getSkills()[Misc.HITPOINTS].getLevel() < player.getSkills()[Misc.HITPOINTS].getLevelForExperience()) {
                    player.getSkills()[Misc.HITPOINTS].increaseLevel(1);
                    SkillManager.refresh(player, SkillConstant.HITPOINTS);
                }
            }
        }
    }
}
