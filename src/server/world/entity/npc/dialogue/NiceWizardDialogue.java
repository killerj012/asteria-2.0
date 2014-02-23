package server.world.entity.npc.dialogue;

import server.world.entity.npc.NpcDialogue;
import server.world.entity.player.Player;

/**
 * A conversation between the player and a wizard that offers teleportation to
 * runecrafting areas.
 * 
 * @author lare96
 */
public class NiceWizardDialogue extends NpcDialogue {

    @Override
    public void dialogue(Player player) {
        switch (player.getConversationStage()) {
            case 0:
                oneLineMobDialogue(player, Expression.HAPPY, "Hi " + player.getUsername() + "! What can I help you with?", 460);
                this.next(player);
                break;
            case 1:
                twoOptions(player, "I would like to mine essence.", "I would like to craft runes.");
                player.setOption(1);
                this.stop(player);
                break;
        }
    }

    @Override
    public int dialogueId() {
        return 2;
    }
}
