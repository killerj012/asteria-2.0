package server.world.entity.npc.dialogue;

import server.world.entity.npc.NpcDialogue;
import server.world.entity.player.Player;

/**
 * A conversation between the player and a nice wizard.
 * 
 * @author lare96
 */
public class NiceWizardDialogue extends NpcDialogue {

    @Override
    public void dialogue(Player player) {
        switch (player.getConversationStage()) {
            case 0:
                oneLineMobDialogue(player, Expression.HAPPY, "Hi " + player.getUsername() + "! Nice day today isn't it?", 460);
                this.stop(player);
                break;
        }
    }

    @Override
    public int dialogueId() {
        return 2;
    }
}
