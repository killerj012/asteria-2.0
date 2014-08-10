package com.asteria.world.entity.npc.policy;

import com.asteria.world.entity.Entity;
import com.asteria.world.entity.Entity.EntityType;
import com.asteria.world.entity.npc.Npc;
import com.asteria.world.entity.npc.NpcAggressionPolicy;

/**
 * The default {@link NpcAggressionPolicy} implementation given to all
 * aggressive {@link Npc}s that don't have a custom policy.
 * 
 * @author lare96
 */
public class DefaultAggressionPolicy implements NpcAggressionPolicy {

    @Override
    public boolean attackIf(Npc attacker, Entity victim) {

        // If the victim isn't an NPC, we can attack.
        return victim.type() != EntityType.NPC;
    }

    @Override
    public void onAttack(Npc attacker, Entity victim) {

        // Nothing else happens when they attack.
    }

    @Override
    public int[] identifiers() {

        // No identifiers needed, npcs without a custom policy will
        // automatically take this one on.
        return new int[] {};
    }
}
