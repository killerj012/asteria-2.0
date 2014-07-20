package com.asteria.world.entity.combat.effect;

import com.asteria.engine.task.Task;
import com.asteria.world.entity.UpdateFlags.Flag;
import com.asteria.world.entity.player.Player;

/**
 * A {@link Task} implementation that will remove the white skull from above
 * player's head.
 * 
 * @author lare96
 */
public class CombatSkullEffect extends Task {

    /** The player attached to this task. */
    private Player player;

    /**
     * Create a new {@link CombatSkullEffect}.
     * 
     * @param player
     *            the player attached to this task.
     */
    public CombatSkullEffect(Player player) {
        super(50, false);
        super.bind(player);
        this.player = player;
    }

    @Override
    public void fire() {

        // Timer is at or below 0 so we can remove the skull.
        if (player.getSkullTimer() <= 0) {
            player.setSkullIcon(-1);
            player.getFlags().flag(Flag.APPEARANCE);
            this.cancel();
            return;
        }

        // Otherwise we just decrement the timer.
        player.decrementSkullTimer();
    }
}
