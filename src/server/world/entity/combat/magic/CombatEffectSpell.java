package server.world.entity.combat.magic;

import server.world.entity.Entity;
import server.world.entity.Spell;
import server.world.entity.player.Player;
import server.world.item.Item;

/**
 * A {@link Spell} implemenation primarily used for spells that have effects
 * when they hit the player.
 * 
 * @author lare96
 */
public abstract class CombatEffectSpell extends CombatSpell {

    @Override
    public int maximumStrength() {

        /** Effect spells don't show a hitsplat. */
        return -1;
    }

    @Override
    public Item[] equipmentRequired(Player player) {

        /** Effect spells never require equipment. */
        return null;
    }

    @Override
    public void endCast(Entity cast, Entity castOn, boolean spellAccurate, int damageInflicted) {
        if (spellAccurate) {
            spellEffect(cast, castOn);
        }
    }

    /**
     * The effect that will take place once the spell hits the target.
     * 
     * @param cast
     *        the caster of the spell.
     * @param castOn
     *        the target being hit by the spell.
     */
    public abstract void spellEffect(Entity cast, Entity castOn);
}
