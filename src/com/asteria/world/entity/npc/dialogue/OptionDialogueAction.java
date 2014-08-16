package com.asteria.world.entity.npc.dialogue;

/**
 * A {@link DialogueAction} implementation that presents a set of options for
 * the player.
 * 
 * @author lare96
 */
public class OptionDialogueAction implements DialogueAction {

    /** The id of the options being presented. */
    private int optionId;

    /** The options that will be presented. */
    private String[] options;

    /**
     * Create a new {@link OpdationDialogueAction}.
     * 
     * @param optionId
     *            the id of the options being presented.
     * @param options
     *            the options that will be presented.
     */
    public OptionDialogueAction(int optionId, String... options) {
        this.optionId = optionId;
        this.options = options;
    }

    @Override
    public void accept(Dialogue dialogue) {
        Dialogue.sendOptionDialogue(dialogue.getPlayer(), options);
        dialogue.getPlayer().setOption(optionId);
    }
}
