package com.asteria.world.entity.combat.magic;

import java.util.Iterator;

import com.asteria.util.Utility;
import com.asteria.world.World;
import com.asteria.world.entity.Entity;
import com.asteria.world.entity.Entity.EntityType;
import com.asteria.world.entity.Hit;
import com.asteria.world.entity.player.Player;
import com.asteria.world.item.Item;
import com.asteria.world.map.Location;

/**
 * A {@link CombatSpell} implementation that is primarily used for spells that
 * are a part of the ancients spellbook.
 * 
 * @author lare96
 */
public abstract class CombatAncientSpell extends CombatSpell {

    @SuppressWarnings("null")
    @Override
    public void finishCast(Entity cast, Entity castOn, boolean accurate,
            int damage) {

        // The spell wasn't accurate, so do nothing.
        if (!accurate) {
            return;
        }

        // Do the spell effect here.
        spellEffect(cast, castOn, damage);

        // The spell doesn't support multiple targets or we aren't in a
        // multicombat zone, so do nothing.
        if (spellRadius() == 0 || !Location.inMultiCombat(castOn)) {
            return;
        }

        // We passed the checks, so now we do multiple target stuff.
        Iterator<? extends Entity> it = null;
        if (cast.type() == EntityType.PLAYER
                && castOn.type() == EntityType.PLAYER) {
            it = ((Player) cast).getLocalPlayers().iterator();
        } else if (cast.type() == EntityType.PLAYER
                && castOn.type() == EntityType.NPC) {
            it = ((Player) cast).getLocalNpcs().iterator();
        } else if (cast.type() == EntityType.NPC
                && castOn.type() == EntityType.NPC) {
            it = World.getNpcs().iterator();
        } else if (cast.type() == EntityType.NPC
                && castOn.type() == EntityType.PLAYER) {
            it = World.getPlayers().iterator();
        }

        for (Iterator<? extends Entity> $it = it; $it.hasNext();) {
            Entity next = $it.next();

            if (next == null) {
                continue;
            }

            if (next.getPosition().withinDistance(castOn.getPosition(),
                    spellRadius())
                    && !next.equals(cast)
                    && !next.equals(castOn)
                    && next.getCurrentHealth() > 0 && !next.isDead()) {
                next.graphic(cast.getCurrentlyCasting().endGraphic());
                int calc = Utility.inclusiveRandom(0, maximumHit());
                next.dealDamage(new Hit(calc));
                next.getCombatBuilder().addDamage(cast, calc);
                spellEffect(cast, next, calc);
            }
        }
    }

    @Override
    public Item[] equipmentRequired(Player player) {

        // Ancient spells never require any equipment, although the method can
        // still be overridden if by some chance a spell does.
        return null;
    }

    /**
     * The effect this spell has on the target.
     * 
     * @param cast
     *            the entity casting this spell.
     * @param castOn
     *            the person being hit by this spell.
     * @param damage
     *            the damage inflicted.
     */
    public abstract void spellEffect(Entity cast, Entity castOn, int damage);

    /**
     * The radius of this spell, only comes in effect when the victim is hit in
     * a multicombat area.
     * 
     * @return how far from the target this spell can hit when targeting
     *         multiple entities.
     */
    public abstract int spellRadius();
}
