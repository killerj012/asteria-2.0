package server.world.entity.combat;

import server.world.entity.Entity;

/**
 * A dynamic table used to determine how an entity will act during a combat
 * session.
 * 
 * @author lare96
 */
public interface CombatStrategy {

    /**
     * Fired right before the actual combat turn. Used for miscellaneous checks
     * and calculations.
     * 
     * @param entity
     *            the attacking entity in this combat session.
     * @return true if the attack can be successfully made.
     */
    public boolean prepareAttack(Entity entity);

    /**
     * Fired when the actual combat turn is taking place.
     * 
     * @param entity
     *            the attacking entity in this combat session.
     * @param victim
     *            the defending entity in this combat session.
     * @return the hit that will be dealt to the defender during this turn.
     */
    public CombatHitContainer attack(Entity entity, Entity victim);

    /**
     * How long the attacker must wait in intervals to attack.
     * 
     * @param entity
     *            the attacking entity in this combat session.
     * @return the amount of time in ticks that the attacker must wait to
     *         attack.
     */
    public int attackTimer(Entity entity);

    /**
     * How close the attacker must be to attack.
     * 
     * @param entity
     *            the attacking entity in this combat session.
     * @return the distance in single tiles that the attacker must be to attack.
     */
    public int getDistance(Entity entity);
}
