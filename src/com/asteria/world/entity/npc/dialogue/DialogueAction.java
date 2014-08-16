package com.asteria.world.entity.npc.dialogue;

import java.util.function.Consumer;

/**
 * A dynamic stage that when together with other dialogue actions forms a
 * {@link Dialogue}.
 * 
 * @author lare96
 */
public interface DialogueAction extends Consumer<Dialogue> {

}
