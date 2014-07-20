package com.asteria.world.entity.npc.dialogue;

/**
 * A dynamic stage that when together with other dialogue actions forms a
 * {@link Dialogue}.
 * 
 * @author lare96
 */
public interface DialogueAction {

    /**
     * The actual action the will be fired at the specified dialogue stage.
     * 
     * @param dialogue
     *            the dialogue this action is being fired for.
     */
    public void fire(Dialogue dialogue);
}
