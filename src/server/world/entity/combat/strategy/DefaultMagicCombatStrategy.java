package server.world.entity.combat.strategy;

import server.world.entity.Entity;
import server.world.entity.combat.CombatHit;
import server.world.entity.combat.CombatStrategy;

public class DefaultMagicCombatStrategy implements CombatStrategy {

    @Override
    public boolean prepareAttack(Entity entity) {
        return false;
        // TODO Auto-generated method stub

    }

    @Override
    public CombatHit attack(Entity entity, Entity victim) {
        return null;
    }

    @Override
    public int attackTimer(Entity entity) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int getDistance(Entity entity) {
        return 8;
    }
}
