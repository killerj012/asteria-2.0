package server.world.entity.combat;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import server.core.worker.TaskFactory;
import server.core.worker.listener.EventListener;
import server.util.Misc;
import server.world.entity.Entity;
import server.world.entity.player.Player;

/**
 * Holds data and methods that run the entire combat process for the specified
 * entity.
 * 
 * @author lare96
 */
public class CombatBuilder {

    /** The entity controlling this builder. */
    private Entity entity;

    /** The entity this controller is currently attacking. */
    private Entity currentTarget;

    /** The entity that last attacked you. */
    private Entity lastAttacker;

    /** The time in ticks that must be waited in order to attack. */
    private int attackTimer;

    /** The worker used to handle the combat process. */
    private CombatWorker combatWorker;

    /** A map of all entities who have inflicted damage. */
    private Map<Entity, Integer> damageMap = new HashMap<Entity, Integer>();

    /**
     * Create a new {@link CombatBuilder}.
     * 
     * @param entity
     *        the entity controlling this builder.
     */
    public CombatBuilder(Entity entity) {
        this.entity = entity;
    }

    /**
     * Prompts the controller to begin attacking another entity.
     * 
     * @param victim
     *        the entity that will be attacked.
     * @param finalStrategy
     *        the strategy that will be used to attack this entity.
     */
    public void attack(final Entity victim, final CombatStrategy finalStrategy) {

        /** Start following the victim. */
        entity.getMovementQueue().follow(victim);

        /** Check if we are already attacking this target. */
        if (currentTarget != null) {
            if (currentTarget == victim) {
                return;
            }
        }

        /** A dummy instance of this combat builder. */
        final CombatBuilder builder = this;

        /**
         * A listener that will be used to determine when the entity is close
         * enough to attack.
         */
        TaskFactory.getFactory().submit(new EventListener() {

            /** The combat strategy being used to attack. */
            private CombatStrategy strategy = finalStrategy;

            /** Will be used to determine how many loops have been made. */
            private int loopCount;

            @Override
            public boolean listenForEvent() {

                /** Redetermines the combat strategy if you're a player. */
                if (builder.getEntity().isPlayer()) {
                    Player player = (Player) builder.getEntity();

                    if (CombatFactory.RANGE_WEAPONS.contains(player.getEquipment().getContainer().getItemId(Misc.EQUIPMENT_SLOT_WEAPON))) {
                        strategy = CombatFactory.newDefaultRangedStrategy();
                    } else if (player.isAutocastMagic()) {
                        strategy = CombatFactory.newDefaultMagicStrategy();
                    } else {
                        strategy = CombatFactory.newDefaultMeleeStrategy();
                    }
                }

                /**
                 * Stop listening if we take too long or the victim becomes way
                 * out of range.
                 */
                if (loopCount > 15 || !entity.getPosition().isViewableFrom(victim.getPosition())) {
                    this.cancel();
                    entity.faceEntity(65535);
                    entity.getFollowWorker().cancel();
                    entity.setFollowing(false);
                    entity.setFollowingEntity(null);
                    return true;
                }

                /** Set the attack timer. */
                if (loopCount == 0) {
                    attackTimer = strategy.attackTimer(entity);
                }

                /**
                 * Countdown the attack timer while we are walking to the
                 * victim.
                 */
                if (loopCount > 0) {
                    builder.decrementAttackTimer();
                }

                /** Increment the amount of loops made. */
                loopCount++;

                /** Start combat once we are in the correct distance. */
                return entity.getPosition().withinDistance(victim.getPosition(), strategy.getDistance(entity));
            }

            @Override
            public void run() {

                /** Change targets if needed. */
                if (currentTarget != null && combatWorker.isRunning()) {
                    currentTarget = victim;
                    return;
                }

                /** Prepare to attack using this strategy. */
                if (!strategy.prepareAttack(entity)) {
                    return;
                }

                /** Set the new target. */
                currentTarget = victim;

                /** Start the combat worker if needed. */
                if (combatWorker == null || !combatWorker.isRunning()) {
                    combatWorker = new CombatWorker(builder, strategy);
                    TaskFactory.getFactory().submit(combatWorker);
                    entity.getLastCombat().reset();
                }
            }
        });
    }

    /**
     * Decrements the attack timer to a minimum of 0.
     */
    public void decrementAttackTimer() {
        if (attackTimer > 0) {
            attackTimer--;
        }
    }

    /**
     * Resets the attack timer to 0.
     */
    public void clearAttackTimer() {
        attackTimer = 0;
    }

    /**
     * Sets the attack timer to any positive value including 0.
     * 
     * @param value
     *        the value to set the attack timer to.
     */
    public void setAttackTimer(int value) {
        if (value < 0) {
            value = 0;
        }

        attackTimer = value;
    }

    /**
     * Gets the time in ticks that must be waited in order to attack.
     * 
     * @return the time in ticks that must be waited in order to attack.
     */
    public int getAttackTimer() {
        return attackTimer;
    }

    /**
     * Resets this combat builder.
     */
    public void reset() {
        if (combatWorker != null) {
            combatWorker.cancel();
        }

        currentTarget = null;
        combatWorker = null;
        attackTimer = 0;
    }

    /**
     * Resets the attack timer to the value based on the strategy being used.
     */
    public void resetAttackTimer() {
        if (combatWorker == null || !combatWorker.isRunning()) {
            return;
        }

        attackTimer = combatWorker.getStrategy().attackTimer(entity);
    }

    /**
     * Gets the entity who killed this player.
     * 
     * @return the entity who killed this player.
     */
    public Entity getKiller() {

        /** We weren't killed by any entities. */
        if (damageMap.size() == 0) {
            return null;
        }

        /** The value we are searching for - the highest value in the damage map. */
        int searchValue = Collections.max(damageMap.values()).intValue();

        /** Search for the value and return the key for that value (the killer). */
        for (Entry<Entity, Integer> nextEntry : damageMap.entrySet()) {
            if (nextEntry.getValue().intValue() == searchValue) {
                if (nextEntry.getKey().isHasDied() || nextEntry.getKey().isUnregistered()) {
                    continue;
                }

                return nextEntry.getKey();
            }
        }

        /** If no killers have been found return the last attacker. */
        return lastAttacker;
    }

    /**
     * Adds damage to the damage map.
     * 
     * @param entity
     *        the entity to add damage for.
     * @param amountDealt
     *        the amount of damage dealt.
     */
    public void addDamage(Entity entity, int amountDealt) {

        /** Add on to the damage for existing entities. */
        if (damageMap.containsKey(entity)) {
            int damageAlreadyDealt = damageMap.get(entity);

            damageMap.put(entity, (amountDealt + damageAlreadyDealt));
            return;
        }

        /** Or add a completely new entry. */
        damageMap.put(entity, amountDealt);
    }

    /**
     * Determines if this entity is attacking another entity.
     * 
     * @return true if this entity is attacking another entity.
     */
    public boolean isAttacking() {
        return currentTarget != null;
    }

    /**
     * Determines if this entity is being attacked by another entity.
     * 
     * @return true if this entity is being attacked by another entity.
     */
    public boolean isBeingAttacked() {
        return combatWorker != null && combatWorker.isRunning();
    }

    /**
     * Gets the entity controlling this builder.
     * 
     * @return the entity controlling this builder.
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * Sets the entity controlling this builder.
     * 
     * @param entity
     *        the entity controlling this builder.
     */
    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    /**
     * Gets the entity this controller is currently attacking.
     * 
     * @return the entity this controller is currently attacking.
     */
    public Entity getCurrentTarget() {
        return currentTarget;
    }

    /**
     * Sets the entity this controller is currently attacking.
     * 
     * @param currentTarget
     *        the entity this controller is currently attacking.
     */
    public void setCurrentTarget(Entity currentTarget) {
        this.currentTarget = currentTarget;
    }

    /**
     * Gets the entity that last attacked you.
     * 
     * @return the entity that last attacked you.
     */
    public Entity getLastAttacker() {
        return lastAttacker;
    }

    /**
     * Sets the entity that last attacked you.
     * 
     * @param lastAttacker
     *        the entity that last attacked you.
     */
    public void setLastAttacker(Entity lastAttacker) {
        this.lastAttacker = lastAttacker;
    }
}
