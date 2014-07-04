package server.world.entity.combat.magic;

import server.util.Misc;
import server.world.World;
import server.world.entity.Entity;
import server.world.entity.EntityType;
import server.world.entity.Hit;
import server.world.entity.npc.Npc;
import server.world.entity.player.Player;
import server.world.item.Item;
import server.world.map.Location;

/**
 * A {@link CombatSpell} implementation that is primarily used for spells that
 * are a part of the ancients spellbook.
 * 
 * @author lare96
 */
public abstract class CombatAncientSpell extends CombatSpell {

    @Override
    public void endCast(Entity cast, Entity castOn, boolean spellAccurate,
            int damageInflicted) {

        /** Multitarget support with the proper radius. */
        if (spellAccurate) {
            spellEffect(cast, castOn, damageInflicted);

            if (this.spellRadius() == 0 || !Location.inMultiCombat(castOn)) {
                return;
            }

            if (castOn.type() == EntityType.NPC) {
                for (Npc npc : World.getNpcs()) {
                    if (npc == null) {
                        continue;
                    }

                    if (npc.getPosition().withinDistance(castOn.getPosition(),
                            spellRadius())
                            && npc != cast
                            && npc.getSlot() != castOn.getSlot()
                            && npc.getDefinition().isAttackable()) {
                        npc.gfx(cast.getCurrentlyCasting().endGfx());
                        int damage = Misc.random(this.maximumStrength());
                        npc.dealDamage(new Hit(damage));
                        npc.getCombatBuilder().addDamage(cast, damage);
                        spellEffect(cast, npc, damage);
                    }
                }
            } else if (castOn.type() == EntityType.PLAYER) {
                for (Player player : World.getPlayers()) {
                    if (player == null) {
                        continue;
                    }

                    if (player.getPosition().withinDistance(
                            castOn.getPosition(), spellRadius())
                            && player != cast
                            && player.getSlot() != castOn.getSlot()) {
                        player.gfx(cast.getCurrentlyCasting().endGfx());
                        int damage = Misc.random(this.maximumStrength());
                        player.dealDamage(new Hit(damage));
                        player.getCombatBuilder().addDamage(cast, damage);
                        spellEffect(cast, player, damage);
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
     *            the entity casting this spell.
     * @param castOn
     *            the person being hit by this spell.
     * @param damageInflicted
     *            the damage inflicted.
     */
    public abstract void spellEffect(Entity cast, Entity castOn,
            int damageInflicted);

    /**
     * The radius of this spell.
     * 
     * @return how far from the target this spell can hit.
     */
    public abstract int spellRadius();
}
