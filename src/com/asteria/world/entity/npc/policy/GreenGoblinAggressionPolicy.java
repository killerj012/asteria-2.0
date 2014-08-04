package com.asteria.world.entity.npc.policy;

import com.asteria.util.Utility;
import com.asteria.world.entity.Entity;
import com.asteria.world.entity.Entity.EntityType;
import com.asteria.world.entity.npc.Npc;
import com.asteria.world.entity.npc.NpcAggressionPolicy;

/**
 * The {@link NpcAggressionPolicy} implementation given to all green goblins.
 * 
 * @author lare96
 */
public class GreenGoblinAggressionPolicy implements NpcAggressionPolicy {

    /** Messages that will be shouted by green goblins. */
    public static final String[] GREEN_GOBLIN = { "Green!", "Stupid reddie!",
            "Green not red!", "Red armour stupid!", "Green armour best!" };

    @Override
    public boolean attackIf(Npc attacker, Entity victim) {

        // Goblin will only attack red goblins.
        if (victim.type() == EntityType.PLAYER) {
            return false;
        }
        return ((Npc) victim).getNpcId() == 299;
    }

    @Override
    public void onAttack(Npc attacker, Entity victim) {

        // The goblin will shout a random message.
        if (Utility.RANDOM.nextBoolean()) {
            attacker.forceChat(Utility.randomElement(GREEN_GOBLIN));
        }
    }

    @Override
    public int[] identifiers() {
        return new int[] { 298 };
    }
}