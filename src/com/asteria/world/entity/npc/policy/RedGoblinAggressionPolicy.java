package com.asteria.world.entity.npc.policy;

import com.asteria.util.Utility;
import com.asteria.world.entity.Entity;
import com.asteria.world.entity.Entity.EntityType;
import com.asteria.world.entity.npc.Npc;
import com.asteria.world.entity.npc.NpcAggressionPolicy;

/**
 * The {@link NpcAggressionPolicy} implementation given to all red goblins.
 * 
 * @author lare96
 */
public class RedGoblinAggressionPolicy implements NpcAggressionPolicy {

    /** Messages that will be shouted by red goblins. */
    public static final String[] RED_GOBLIN = { "Red!", "Stupid greenie!",
            "Red not green!", "Green armour stupid!", "Red armour best!" };

    @Override
    public boolean attackIf(Npc attacker, Entity victim) {

        // Goblin will only attack green goblins.
        if (victim.type() == EntityType.PLAYER) {
            return false;
        }
        return ((Npc) victim).getNpcId() == 298;
    }

    @Override
    public void onAttack(Npc attacker, Entity victim) {

        // The goblin will shout a random message.
        if (Utility.RANDOM.nextBoolean()) {
            attacker.forceChat(Utility.randomElement(RED_GOBLIN));
        }
    }

    @Override
    public int[] identifiers() {
        return new int[] { 299 };
    }
}