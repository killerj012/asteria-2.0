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
                oneLineMobDialogue(player, Expression.DISORIENTED_LEFT, "Hey kid want some drugs and booze?", 956);
                this.next(player);
                break;
            case 1:
                oneLinePlayerDialogue(player, Expression.ANNOYED, "Why would I want that?");
                this.next(player);
                break;
            case 2:
                oneLinePlayerDialogue(player, Expression.LAUGHING, "Hahahaha pussy!");
                this.stop(player);
                break;
        }
    }

    @Override
    public int dialogueId() {
        return 1;
    }
}
