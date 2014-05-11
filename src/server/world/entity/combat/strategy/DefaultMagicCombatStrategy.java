package server.world.entity.combat.strategy;

import server.util.Misc;
import server.world.entity.Entity;
import server.world.entity.Hit;
import server.world.entity.combat.CombatFactory;
import server.world.entity.combat.CombatHit;
import server.world.entity.combat.CombatStrategy;
import server.world.entity.combat.CombatType;
import server.world.entity.player.Player;

/**
 * The default combat strategy assigned to an entity during a magic based combat
 * session. NPCs with magic attacks should not be assigned this combat strategy
 * but instead have an individualized combat strategy dedicated to them.
 * 
 * @author lare96
 */
public class DefaultMagicCombatStrategy implements CombatStrategy {

    @Override
    public boolean prepareAttack(Entity entity) {
        if (entity.isPlayer()) {
            Player player = (Player) entity;

            /** Prepare the cast effectively. */
            return player.getCastSpell().prepareCast(player, null);
        }
        return true;
    }

    @Override
    public CombatHit attack(Entity entity, Entity victim) {
        if (entity.isPlayer()) {
            Player player = (Player) entity;

            /** Cast the spell. */
            player.setCurrentlyCasting(player.getCastSpell());
            player.getCurrentlyCasting().castSpell(entity, victim);

            /** Disabling spells block here because there is no hit. */
            if (player.getCurrentlyCasting().maximumStrength() == -1) {
                CombatFactory.hitAccuracy(player, victim, CombatType.MAGIC, 1);
                return null;
            }

            /** Actual combat spells here. */
            if (CombatFactory.hitAccuracy(player, victim, CombatType.MAGIC, 1)) {
                return new CombatHit(new Hit[] { new Hit(Misc.getRandom().nextInt(player.getCurrentlyCasting().maximumStrength())) }, CombatType.MAGIC);
            }
        }
        return null;
    }

    @Override
    public int attackTimer(Entity entity) {

        /** The deafult attack time. */
        return 7;
    }

    @Override
    public int getDistance(Entity entity) {

        /** The default distance. */
        return 8;
    }
}
