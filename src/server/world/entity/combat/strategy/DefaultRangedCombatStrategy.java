package server.world.entity.combat.strategy;

import server.world.entity.Entity;
import server.world.entity.combat.CombatHit;
import server.world.entity.combat.CombatStrategy;

public class DefaultRangedCombatStrategy implements CombatStrategy {

    @Override
    public boolean prepareAttack(Entity entity) {
        return false;
    }

    @Override
    public CombatHit attack(Entity entity, Entity victim) {
        return null;
        // TODO Auto-generated method stub

    }

    @Override
    public int attackTimer(Entity entity) {
        // XXX: Darts, thrownaxes, knives, javelins, crossbows, shortbows,
        // longbows, crystal bows
        return 3;
    }

    @Override
    public int getDistance(Entity entity) {
        // XXX: Darts, thrownaxes, knives, javelins, crossbows, shortbows,
        // longbows, crystal bows
        return 6;
    }
}
