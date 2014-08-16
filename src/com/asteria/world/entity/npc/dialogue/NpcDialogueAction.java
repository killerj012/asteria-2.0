package com.asteria.world.entity.npc.dialogue;

import com.asteria.world.entity.npc.dialogue.Dialogue.Expression;

/**
 * A {@link DialogueAction} implementation that sends npc dialogue to the
 * player.
 * 
 * @author lare96
 */
public class NpcDialogueAction implements DialogueAction {

    /** The id of the npc sending this dialogue. */
    private int npcId;

    /** The expression that this npc is making. */
    private Expression expression;

    /** The lines that will be sent. */
    private String[] lines;

    /**
     * Create a new {@link NpcDialogueAction}.
     * 
     * @param npcId
     *            the id of the npc sending this dialogue.
     * @param expression
     *            the expression that this npc is making.
     * @param lines
     *            the lines that will be sent.
     */
    public NpcDialogueAction(int npcId, Expression expression, String... lines) {
        this.npcId = npcId;
        this.expression = expression;
        this.lines = lines;
    }

    /**
     * Create a new {@link NpcDialogueAction} with the default expression.
     * 
     * @param npcId
     *            the id of the npc sending this dialogue.
     * @param lines
     *            the lines that will be sent.
     */
    public NpcDialogueAction(int npcId, String... lines) {
        this(npcId, Expression.CALM, lines);
    }

    @Override
    public void accept(Dialogue dialogue) {
        Dialogue.sendNpcDialogue(dialogue.getPlayer(), expression, npcId, lines);
    }
}
