package server.world.entity.combat.task;

import server.core.worker.Worker;
import server.world.entity.player.Player;

/**
 * A {@link Worker} implementation that will unteleblock the player after the
 * counter reaches 0.
 * 
 * @author lare96
 */
public class CombatTeleblockTask extends Worker {

    /** The player attached to this worker. */
    private Player player;

    /**
     * Create a new {@link CombatTeleblockTask}.
     * 
     * @param player
     *            the player attached to this worker.
     */
    public CombatTeleblockTask(Player player) {
        super(1, false);
        super.attach(player);
        this.player = player;
    }

    @Override
    public void fire() {

        /** When the timer reaches 0 unteleblock the player. */
        if (player.getTeleblockTimer() == 0) {
            player.getPacketBuilder().sendMessage(
                    "You feel the effects of the strange spell go away.");
            this.cancel();
            return;
        }

        /** Otherwise decrement the timer. */
        player.decrementTeleblockTimer();
    }
}
