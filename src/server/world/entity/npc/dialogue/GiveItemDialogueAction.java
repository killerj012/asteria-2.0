package server.world.entity.npc.dialogue;

import server.world.item.Item;

/**
 * A {@link DialogueAction} implementation that gives a player an item.
 * 
 * @author lare96
 */
public class GiveItemDialogueAction implements DialogueAction {

    /** The item to give the player. */
    private Item item;

    /** The text to show when the item is given. */
    private String text;

    /**
     * Create a new {@link GiveItemDialogueAction}.
     * 
     * @param item
     *        the item to give the player.
     * @param text
     *        the text to show when the item is given.
     */
    public GiveItemDialogueAction(Item item, String text) {
        this.item = item;
        this.text = text;
    }

    @Override
    public void fire(Dialogue dialogue) {
        dialogue.getPlayer().getPacketBuilder().sendString(text, 308);
        dialogue.getPlayer().getPacketBuilder().sendItemOnInterface(307, 200, item.getId());
        dialogue.getPlayer().getPacketBuilder().sendChatInterface(306);
        dialogue.getPlayer().getInventory().addItem(item);
    }
}