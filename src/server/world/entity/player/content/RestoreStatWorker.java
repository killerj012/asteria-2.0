package server.world.entity.player.content;

import server.core.worker.WorkRate;
import server.core.worker.Worker;
import server.util.Misc;
import server.world.World;
import server.world.entity.combat.prayer.CombatPrayer;
import server.world.entity.combat.special.CombatSpecial;
import server.world.entity.player.Player;
import server.world.entity.player.PlayerRights;
import server.world.entity.player.skill.SkillManager;

/**
 * A {@link Worker} implementation that restores boosted and weakened stats for
 * all players online.
 * 
 * @author lare96
 */
public class RestoreStatWorker extends Worker {

    /**
     * Create a new {@link RestoreStatWorker}.
     * 
     * @param player
     *        the player that will be attached to this worker.
     */
    public RestoreStatWorker() {
        super(15, false, WorkRate.APPROXIMATE_SECOND);
    }

    @Override
    public void fire() {
        for (Player player : World.getPlayers()) {
            if (player == null) {
                continue;
            }

            /** Loop through all of the player's skills. */
            for (int i = 0; i < player.getSkills().length; i++) {

                /** Cache the value so we aren't doing calculations repeatedly. */
                int realLevel = player.getSkills()[i].getLevelForExperience();

                /** Check if the hp level needs regeneration. */
                if (i == Misc.HITPOINTS) {
                    if (player.getSkills()[i].getLevel() < realLevel
                            && player.isAcceptAid()) {
                        player.getSkills()[i].increaseLevel(1);

                        if (CombatPrayer.isPrayerActivated(player, CombatPrayer.RAPID_HEAL)) {
                            if (player.getSkills()[i].getLevel() < realLevel) {
                                player.getSkills()[i].increaseLevel(1);
                            }
                        }
                        SkillManager.refresh(player, Misc.HITPOINTS);
                    }
                    continue;
                }

                /** Check all other stats except prayer. */
                if (player.getSkills()[i].getLevel() < realLevel
                        && i != Misc.PRAYER) {
                    player.getSkills()[i].increaseLevel(1);

                    if (CombatPrayer.isPrayerActivated(player, CombatPrayer.RAPID_RESTORE)) {
                        if (player.getSkills()[i].getLevel() < realLevel) {
                            player.getSkills()[i].increaseLevel(1);
                        }
                    }
                    SkillManager.refresh(player, i);

                    /** Check all boosted stats. */
                } else if (player.getSkills()[i].getLevel() > realLevel
                        && i != Misc.PRAYER) {
                    player.getSkills()[i].decreaseLevel(1);
                    SkillManager.refresh(player, i);
                }
            }

            /** Check the special meter and increase it if needed. */
            if (player.getSpecialPercentage() < 100) {
                if (player.getRights().greaterThan(PlayerRights.MODERATOR)) {
                    CombatSpecial.boostAndRestore(player, 100);
                    return;
                }

                CombatSpecial.boostAndRestore(player, 5);
            }
        }
    }
}
