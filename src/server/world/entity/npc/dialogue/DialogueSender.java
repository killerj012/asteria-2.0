package server.world.entity.npc.dialogue;

import server.world.entity.player.Player;
import server.world.item.Item;

/**
 * A static factory class that holds methods for sending dialogues.
 * 
 * @author lare96
 */
public final class DialogueSender {

    /** The dialogue action for the wizard at the home area. */
    public static void sendHomeWizardDialogue(Player player) {
        player.sendDialogue(new Dialogue(player,
                new NpcDialogueAction(460, "Hello "
                        + player.getUsername()
                        + "! This is the new dialogue system!", "It should be much easier to use."),
                new PlayerDialogueAction("Cool, I'm loving it already!"),
                new NpcDialogueAction(460, "Would you like some money?"),
                new PlayerDialogueAction("I would love some money!"),
                new GiveItemDialogueAction(new Item(995, 1000), "The wizard hands you 1000 coins."),
                new PlayerDialogueAction("Thank you sir this truly means so", "much to me."),
                new NpcDialogueAction(460, "Anytime, farewell young warrior!")));
    }
}
