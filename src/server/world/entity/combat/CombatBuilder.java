package server.world.entity.combat;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import server.core.worker.TaskFactory;
import server.core.worker.listener.EventListener;
import server.world.entity.Entity;
import server.world.entity.combat.task.CombatHookTask;
import server.world.entity.npc.Npc;
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
    private CombatHookTask combatWorker;

    /** A map of all entities who have inflicted damage. */
    private Map<Entity, Integer> damageMap = new HashMap<Entity, Integer>();

    /** The current combat strategy this npc is using. */
    private CombatStrategy currentStrategy;

    /** If the cooldown should start or not. */
    private boolean cooldownEffect;

    /** The amount of cooldown ticks left. */
    private int cooldown = 5;

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
     */
    public void attack(final Entity victim) {

        /** Start following the victim. */
        entity.getMovementQueue().follow(victim);

        /** Check if we are already attacking this target. */
        if (currentTarget != null) {
            if (currentTarget == victim) {
                cooldownEffect = false;
                cooldown = 5;
                return;
            }
        }

        /** A dummy instance of this combat builder. */
        final CombatBuilder builder = this;

        /** Determine the combat strategy for npcs. */
        if (builder.getEntity().isNpc()) {
            Npc npc = (Npc) builder.getEntity();
            CombatFactory.determineNpcStrategy(npc);
        }

        /**
         * A listener that will be used to determine when the entity is close
         * enough to attack.
         */
        TaskFactory.getFactory().submit(new EventListener() {

            /** Will be used to determine how many loops have been made. */
            private int loopCount;

            @Override
            public boolean listenForEvent() {

                /**
                 * Redetermines the combat strategy while walking to the victim
                 * if you're a player.
                 */
                if (builder.getEntity().isPlayer()) {
                    Player player = (Player) builder.getEntity();
                    CombatFactory.determinePlayerStrategy(player);
                }

                /**
                 * Stop listening if we take too long or the victim becomes way
                 * out of range.
                 */
                if (loopCount > 15 || !entity.getPosition().isViewableFrom(victim.getPosition())) {
                    builder.reset();
                    this.cancel();
                    entity.faceEntity(65535);
                    entity.getFollowWorker().cancel();
                    entity.setFollowing(false);
                    entity.setFollowingEntity(null);
                    return true;
                }

                /** Set the attack timer. */
                if (loopCount == 0) {
                    cooldownEffect = false;
                    cooldown = 5;
                    attackTimer = currentStrategy.attackTimer(entity);
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

                /** If we are starting a fresh combat session attack right away. */
                if (entity.getLastFight().elapsed() > 10000) {
                    attackTimer = 0;
                }

                /** Start combat once we are in the correct distance. */
                return !entity.getPosition().withinDistance(victim.getPosition(), currentStrategy.getDistance(entity));
            }

            @Override
            public void run() {

                /** Stop movement before attacking. */
                entity.getMovementQueue().reset();

                /** Change targets if needed. */
                if (currentTarget != null && combatWorker.isRunning()) {
                    currentTarget = victim;
                    return;
                }

                /** Set the new target. */
                currentTarget = victim;

                /** Start the combat worker if needed. */
                if (combatWorker == null || !combatWorker.isRunning()) {
                    combatWorker = new CombatHookTask(builder);
                    TaskFactory.getFactory().submit(combatWorker);
                }
            }
        });
    }

    /**
     * Decrements the cooldown.
     */
    public void decrementCooldown() {
        cooldown--;
    }

    /**
     * Resets the cooldown;
     */
    public void resetCooldown() {
        cooldown = 5;
    }

    /**
     * Gets the cooldown.
     * 
     * @return the cooldown.
     */
    public int getCooldown() {
        return cooldown;
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
        currentStrategy = null;
        cooldownEffect = false;
        cooldown = 5;
    }

    /**
     * Resets the attack timer to the value based on the strategy being used.
     */
    public void resetAttackTimer() {
        if (currentStrategy == null) {
            return;
        }

        cooldownEffect = true;
        attackTimer = currentStrategy.attackTimer(entity);
    }

    /**
     * Resets the damage map.
     */
    public void resetDamage() {
        damageMap.clear();
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

        /** No damage below 0 is accounted for. */
        if (amountDealt < 1) {
            return;
        }

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
        return entity.getLastCombat().elapsed() < 5000;
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

    /**
     * Gets the current combat strategy this npc is using.
     * 
     * @return the current combat strategy this npc is using.
     */
    public CombatStrategy getCurrentStrategy() {
        return currentStrategy;
    }

    /**
     * Sets the current combat strategy this npc is using.
     * 
     * @param currentStrategy
     *        the current combat strategy this npc is using.
     */
    public void setCurrentStrategy(CombatStrategy currentStrategy) {
        this.currentStrategy = currentStrategy;
    }

    /**
     * Gets if the cooldown should start or not.
     * 
     * @return true if the cooldown should start.
     */
    public boolean isCooldownEffect() {
        return cooldownEffect;
    }
}
