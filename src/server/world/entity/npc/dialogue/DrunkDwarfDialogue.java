package server.world.entity.npc.dialogue;

import server.world.entity.npc.NpcDialogue;
import server.world.entity.player.Player;

/**
 * The dialogue that a typical man will hold with a player on the server.
 * 
 * @author lare96
 */
public class DrunkDwarfDialogue extends NpcDialogue {

    @Override
    public void dialogue(Player player) {
        switch (player.getConversationStage()) {
            case 0:
                NpcDialogue.oneLineMobDialogue(player, Expression.DISORIENTED_LEFT, "Err... hello there uh? arghh?", 956);
                this.next(player);
                break;
            case 1:
                NpcDialogue.oneLineMobDialogue(player, Expression.DISORIENTED_RIGHT, "Urmph... Err... WAIT. HAHAHA.", 956);
                this.next(player);
                break;
            case 2:
                NpcDialogue.oneLinePlayerDialogue(player, Expression.ANNOYED, "Alright.");
                this.stop(player);
                break;
        }
    }

    @Override
    public int dialogueId() {
        return 1;
    }
}
