package com.asteria.world.entity.player.content;

import com.asteria.engine.task.Task;
import com.asteria.world.World;
import com.asteria.world.entity.combat.prayer.CombatPrayer;
import com.asteria.world.entity.combat.special.CombatSpecial;
import com.asteria.world.entity.player.Player;
import com.asteria.world.entity.player.PlayerRights;
import com.asteria.world.entity.player.skill.Skills;

/**
 * A {@link Task} implementation that restores boosted and weakened stats for
 * all players online.
 * 
 * @author lare96
 */
public class RestoreStatTask extends Task {

    /**
     * Create a new {@link RestoreStatTask}.
     * 
     * @param player
     *            the player that will be attached to this task.
     */
    public RestoreStatTask() {
        super(30, false);
    }

    @Override
    public void execute() {
        for (Player player : World.getPlayers()) {
            if (player == null) {
                continue;
            }

            // Loop through all of the player's skills.
            for (int i = 0; i < player.getSkills().length; i++) {

                // Cache the value so we aren't doing calculations repeatedly.
                int realLevel = player.getSkills()[i].getLevelForExperience();

                // Check if the hitpoints level needs regeneration.
                if (i == Skills.HITPOINTS) {
                    if (player.getSkills()[i].getLevel() < realLevel && player
                            .isAcceptAid()) {
                        player.getSkills()[i].increaseLevel(1);

                        if (CombatPrayer.isActivated(player,
                                CombatPrayer.RAPID_HEAL)) {
                            if (player.getSkills()[i].getLevel() < realLevel) {
                                player.getSkills()[i].increaseLevel(1);
                            }
                        }
                        Skills.refresh(player, Skills.HITPOINTS);
                    }
                    continue;
                }

                // Check all other stats except prayer.
                if (player.getSkills()[i].getLevel() < realLevel && i != Skills.PRAYER) {
                    player.getSkills()[i].increaseLevel(1);

                    if (CombatPrayer.isActivated(player,
                            CombatPrayer.RAPID_RESTORE)) {
                        if (player.getSkills()[i].getLevel() < realLevel) {
                            player.getSkills()[i].increaseLevel(1);
                        }
                    }
                    Skills.refresh(player, i);

                    // Check all of the boosted stats.
                } else if (player.getSkills()[i].getLevel() > realLevel && i != Skills.PRAYER) {
                    player.getSkills()[i].decreaseLevel(1);
                    Skills.refresh(player, i);
                }
            }

            // Check the special meter and increase it if needed.
            if (player.getSpecialPercentage() < 100) {
                if (player.getRights().equalTo(PlayerRights.DEVELOPER)) {
                    CombatSpecial.restore(player, 100);
                    return;
                }

                CombatSpecial.restore(player, 5);
            }
        }
    }
}
