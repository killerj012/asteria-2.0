package com.asteria.world.entity.combat.magic;

import com.asteria.world.entity.Entity;
import com.asteria.world.entity.Spell;

/**
 * A {@link Spell} implementation primarily used for spells that have no effects
 * at all when they hit the player.
 * 
 * @author lare96
 */
public abstract class CombatNormalSpell extends CombatSpell {

    @Override
    public void finishCast(Entity cast, Entity castOn, boolean accurate,
            int damage) {}
}