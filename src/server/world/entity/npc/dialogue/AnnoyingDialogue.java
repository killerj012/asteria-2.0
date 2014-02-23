package server.world.entity.npc.dialogue;

import server.world.entity.npc.NpcDialogue;
import server.world.entity.player.Player;

/**
 * A conversation where an npc offends the player.
 * 
 * @author lare96
 */
public class AnnoyingDialogue extends NpcDialogue {

    @Override
    public void dialogue(Player player) {
        switch (player.getConversationStage()) {
            case 0:
                oneLineMobDialogue(player, Expression.DISORIENTED_LEFT, "Err... hello there uh? arghh?", 956);
                this.next(player);
                break;
            case 1:
                oneLineMobDialogue(player, Expression.DISORIENTED_RIGHT, "Urmph... WAIT. HAHAHA YOU'RE UGLY.", 956);
                this.next(player);
                break;
            case 2:
                oneLinePlayerDialogue(player, Expression.ANNOYED, "Oh really? So is your mother.");
                this.stop(player);
                break;
        }
    }

    @Override
    public int dialogueId() {
        return 1;
    }
}
