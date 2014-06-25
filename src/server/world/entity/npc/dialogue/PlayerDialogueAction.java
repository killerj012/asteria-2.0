package server.world.entity.npc.dialogue;

import server.world.entity.npc.dialogue.Dialogue.Expression;

/**
 * A {@link DialogueAction} implementation that sends player dialogue to the
 * player.
 * 
 * @author lare96
 */
public class PlayerDialogueAction implements DialogueAction {

    /** The expression of the dialogue. */
    private Expression expression;

    /** The lines that will be sent. */
    private String[] lines;

    /**
     * Create a new {@link PlayerDialogueAction}.
     * 
     * @param expression
     *        the expression of the dialogue.
     * @param lines
     *        the lines that will be sent.
     */
    public PlayerDialogueAction(Expression expression, String... lines) {
        this.expression = expression;
        this.lines = lines;
    }

    /**
     * Create a new {@link PlayerDialogueAction} with the default expression.
     * 
     * @param lines
     *        the lines that will be sent.
     */
    public PlayerDialogueAction(String... lines) {
        this(Expression.CALM, lines);
    }

    @Override
    public void fire(Dialogue dialogue) {
        Dialogue.sendPlayerDialogue(dialogue.getPlayer(), expression, lines);
    }
}
