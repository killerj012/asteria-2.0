package server.world.entity.combat.strategy;

import server.util.Misc;
import server.world.entity.Entity;
import server.world.entity.Hit;
import server.world.entity.combat.CombatHitContainer;
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

            if (player.getCastSpell() == null) {
                return false;
            }

            /** Prepare the cast effectively. */
            return player.getCastSpell().prepareCast(player, null);
        }
        return true;
    }

    @Override
    public CombatHitContainer attack(Entity entity, Entity victim) {
        if (entity.isPlayer()) {
            Player player = (Player) entity;

            /** Cast the spell. */
            player.setCurrentlyCasting(player.getCastSpell());
            player.getCurrentlyCasting().castSpell(entity, victim);

            /** Disabling spells block here because there is no hit. */
            if (player.getCurrentlyCasting().maximumStrength() == -1) {
                return new CombatHitContainer(null, CombatType.MAGIC, true);
            }

            return new CombatHitContainer(new Hit[] { new Hit(Misc.random(player.getCurrentlyCasting().maximumStrength())) }, CombatType.MAGIC, true);
        }
        return null;
    }

    @Override
    public int attackTimer(Entity entity) {

        /** The deafult attack time. */
        return 10;
    }

    @Override
    public int getDistance(Entity entity) {

        /** The default distance. */
        return 8;
    }
}
