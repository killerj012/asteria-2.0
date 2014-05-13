package server.world.entity.combat.magic;

import server.util.Misc;
import server.world.World;
import server.world.entity.Entity;
import server.world.entity.Hit;
import server.world.entity.npc.Npc;
import server.world.entity.player.Player;
import server.world.item.Item;

/**
 * A {@link CombatSpell} implementation that is primarily used for spells that
 * are a part of the ancients spellbok.
 * 
 * @author lare96
 */
public abstract class CombatAncientSpell extends CombatSpell {

    @Override
    public void endCast(Entity cast, Entity castOn, boolean spellAccurate) {
        if (spellAccurate) {
            if (this.spellRadius() == 0) {
                spellEffect(cast, castOn);
                return;
            }

            if (castOn.isNpc()) {
                for (Npc npc : World.getNpcs()) {
                    if (npc == null) {
                        continue;
                    }

                    if (npc.getPosition().withinDistance(castOn.getPosition(), spellRadius()) && npc != cast && npc.getDefinition().isAttackable()) {
                        npc.gfx(cast.getCurrentlyCasting().endGfx());
                        int damage = Misc.getRandom().nextInt(this.maximumStrength());
                        npc.dealDamage(new Hit(Misc.getRandom().nextInt(this.maximumStrength())));
                        npc.getCombatBuilder().addDamage(cast, damage);
                        spellEffect(cast, npc);
                    }
                }
            } else if (castOn.isPlayer()) {
                for (Player player : World.getPlayers()) {
                    if (player == null) {
                        continue;
                    }

                    if (player.getPosition().withinDistance(castOn.getPosition(), spellRadius()) && player != cast) {
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

    public abstract void spellEffect(Entity cast, Entity castOn);

    public abstract int spellRadius();

}
