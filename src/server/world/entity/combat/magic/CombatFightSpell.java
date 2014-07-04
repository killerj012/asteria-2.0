package server.world.entity.combat.magic;

import server.world.entity.Entity;
import server.world.entity.Spell;

/**
 * A {@link Spell} implemenation primarily used for spells that have no effects
 * at all when they hit the player.
 * 
 * @author lare96
 */
public abstract class CombatFightSpell extends CombatSpell {

    @Override
    public void endCast(Entity cast, Entity castOn, boolean spellAccurate,
            int damageInflicted) {

        /** Normal combat spells have no effects. */
    }
}