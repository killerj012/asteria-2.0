package server.world.entity.combat.magic;

import server.util.Misc;
import server.world.World;
import server.world.entity.Entity;
import server.world.entity.Hit;
import server.world.entity.npc.Npc;
import server.world.entity.player.Player;
import server.world.item.Item;
import server.world.map.Location;

/**
 * A {@link CombatSpell} implementation that is primarily used for spells that
 * are a part of the ancients spellbok.
 * 
 * @author lare96
 */
public abstract class CombatAncientSpell extends CombatSpell {

    @Override
    public void endCast(Entity cast, Entity castOn, boolean spellAccurate) {

        /** Multitarget support with the proper radius. */
        if (spellAccurate) {
            spellEffect(cast, castOn);

            if (this.spellRadius() == 0 || !Location.inMultiCombat(castOn)) {
                return;
            }

            if (castOn.isNpc()) {
                for (Npc npc : World.getNpcs()) {
                    if (npc == null) {
                        continue;
                    }

                    if (npc.getPosition().withinDistance(castOn.getPosition(), spellRadius()) && npc != cast && npc.getSlot() != castOn.getSlot() && npc.getDefinition().isAttackable()) {
                        npc.gfx(cast.getCurrentlyCasting().endGfx());
                        int damage = Misc.getRandom().nextInt(this.maximumStrength());
                        npc.dealDamage(new Hit(damage));
                        npc.getCombatBuilder().addDamage(cast, damage);
                        spellEffect(cast, npc);
                    }
                }
            } else if (castOn.isPlayer()) {
                for (Player player : World.getPlayers()) {
                    if (player == null) {
                        continue;
                    }

                    if (player.getPosition().withinDistance(castOn.getPosition(), spellRadius()) && player != cast && player.getSlot() != castOn.getSlot()) {
                        player.gfx(cast.getCurrentlyCasting().endGfx());
                        int damage = Misc.getRandom().nextInt(this.maximumStrength());
                        player.dealDamage(new Hit(damage));
                        player.getCombatBuilder().addDamage(cast, damage);
                        spellEffect(cast, player);
                    }
                }
            }
        }
    }

    @Override
    public Item[] equipmentRequired(Player player) {

        /** Ancient spells never require any equipment. */
        return null;
    }

    /**
     * The effect this spell has on the target(s).
     * 
     * @param cast
     *        the entity casting this spell.
     * @param castOn
     *        the person being hit by this spell.
     */
    public abstract void spellEffect(Entity cast, Entity castOn);

    /**
     * The radius of this spell.
     * 
     * @return how far from the target this spell can hit.
     */
    public abstract int spellRadius();
}
