package com.asteria.world.entity.npc;

import com.asteria.world.entity.Entity;

/**
 * An interface that acts as a blueprint for all aggressive {@link Npc}s.
 * 
 * @author lare96
 */
public interface NpcAggressionPolicy {

    /**
     * The aggressive {@link Npc} will only attack the victim when this is
     * flagged.
     * 
     * @param attacker
     *            the aggressive npc attacking the victim.
     * @param victim
     *            the victim being attacked by the aggressive npc.
     * @return <code>true</code> if the npc can attack, <code>false</code>
     *         otherwise.
     */
    public boolean attackIf(Npc attacker, Entity victim);

    /**
     * A method invoked if the {@link Npc} was successful in attacking the
     * victim.
     * 
     * @param attacker
     *            the aggressive npc attacking the victim.
     * @param victim
     *            the victim being attacked by the aggressive npc.
     */
    public void onAttack(Npc attacker, Entity victim);

    /**
     * The id's of the {@link Npc}s that will take on this aggression policy.
     * 
     * @return the identifiers for this aggression policy.
     */
    public int[] identifiers();
}
