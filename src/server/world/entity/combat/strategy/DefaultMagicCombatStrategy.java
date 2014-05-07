package server.world.entity.combat.strategy;

import server.world.entity.Entity;
import server.world.entity.combat.CombatHit;
import server.world.entity.combat.CombatStrategy;

public class DefaultMagicCombatStrategy implements CombatStrategy {

    @Override
    public boolean prepareAttack(Entity entity) {
        // check for magic level
        // check for runes
        // check if you have spell selected (if autocasting)
        return false;
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
