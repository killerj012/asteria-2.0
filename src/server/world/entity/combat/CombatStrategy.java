package server.world.entity.combat;

import server.world.entity.Entity;

/**
 * A combat
 * 
 * @author lare96
 */
public interface CombatStrategy {

    public boolean prepareAttack(Entity entity);

    public CombatHit attack(Entity entity, Entity victim);

    public int attackTimer(Entity entity);

    public int getDistance(Entity entity);
}
