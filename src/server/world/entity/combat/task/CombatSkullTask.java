package server.world.entity.combat.task;

import server.core.worker.Worker;
import server.world.entity.UpdateFlags.Flag;
import server.world.entity.player.Player;

/**
 * A {@link Worker} implementation that will unskull the player after the
 * counter reaches 0.
 * 
 * @author lare96
 */
public class CombatSkullTask extends Worker {

    /** The player attached to this worker. */
    private Player player;

    /**
     * Create a new {@link CombatSkullTask}.
     * 
     * @param player
     *            the player attached to this worker.
     */
    public CombatSkullTask(Player player) {
        super(1, false);
        super.attach(player);
        this.player = player;
    }

    @Override
    public void fire() {

        /** When the timer reaches 0 unskull the player. */
        if (player.getSkullTimer() == 0) {
            player.getPacketBuilder().sendMessage(
                    "You have been successfully unskulled.");
            player.setSkullIcon(-1);
            player.getFlags().flag(Flag.APPEARANCE);
            this.cancel();
            return;
        }

        /** Otherwise decrement the timer. */
        player.decrementSkullTimer();
    }
}
