package com.asteria.world.entity.combat;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.asteria.engine.net.Session.Stage;
import com.asteria.engine.task.TaskManager;
import com.asteria.engine.task.listener.EventListener;
import com.asteria.util.Stopwatch;
import com.asteria.world.entity.Entity;
import com.asteria.world.entity.Entity.EntityType;
import com.asteria.world.entity.npc.Npc;
import com.asteria.world.entity.player.Player;

/**
 * Holds methods for running the entire combat process.
 * 
 * @author lare96
 */
public class CombatBuilder {

    /** The entity controlling this builder. */
    private Entity entity;

    /** The entity this controller is currently attacking. */
    private Entity victim;

    /** The entity that last attacked you. */
    private Entity lastAttacker;

    /** The task used to handle the combat process. */
    private CombatHookTask combatTask;

    /** A map of all players who have inflicted damage on this controller. */
    private Map<Player, CombatDamageCache> damageMap = new HashMap<>();

    /** The combat strategy this entity is using to attack. */
    private CombatStrategy strategy;

    /** The time that must be waited in order to attack. */
    protected int attackTimer;

    /** The time that must be waited before the entity can be attacked. */
    protected int cooldown;

    /**
     * Create a new {@link CombatBuilder}.
     * 
     * @param entity
     *            the entity controlling this builder.
     */
    public CombatBuilder(Entity entity) {
        this.entity = entity;
    }

    /**
     * Prompts the controller to begin attacking the argued entity. Wilderness
     * and multicombat checks are still applied, although later on in the
     * process. If this controller is already attack
     * 
     * @param target
     *            the entity that this controller will attempt to attack.
     */
    public void attack(Entity target) {

        // Make sure we aren't attacking ourself.
        if (entity.equals(target)) {
            return;
        }

        // Start following the victim right away.
        entity.getMovementQueue().follow(target);

        // If the combat task is running, change targets.
        if (combatTask != null && combatTask.isRunning()) {
            victim = target;
            cooldown = 0;
            return;
        }

        // Start the event listener implementation that will allow the
        // controller to attack the victim once we're close enough.
        TaskManager.submit(new CombatDistanceListener(this, target));
    }

    /**
     * Resets this combat builder by discarding various values associated with
     * the combat process.
     */
    public void reset() {

        // Reset and discard all the builder's fields.
        if (combatTask != null) {
            combatTask.cancel();
        }

        victim = null;
        combatTask = null;
        attackTimer = 0;
        strategy = null;
        cooldown = 0;
        entity.faceEntity(null);
        entity.setFollowing(false);
    }

    /**
     * Resets the attack timer to the value based on the strategy being used.
     * This method is used primarily for when the player eats food or equips
     * something and has to wait the designated time again before attacking. If
     * the player does not have a strategy this method does nothing.
     */
    public void resetAttackTimer() {

        // We have no strategy so do nothing.
        if (strategy == null) {
            return;
        }

        // Start the cooldown.
        cooldown = 10;

        // Reset the attack timer.
        attackTimer = strategy.attackDelay(entity);
    }

    /**
     * Performs a search on the <code>damageMap</code> to find which
     * {@link Player} dealt the most damage on this controller.
     * 
     * @param clearMap
     *            <code>true</code> if the map should be discarded once the
     *            killer is found, <code>false</code> if no data in the map
     *            should be modified.
     * @return the player who killed this entity, or <code>null</code> if an npc
     *         or something else killed this entity.
     */
    public Player getKiller(boolean clearMap) {

        // Return null if no players killed this entity.
        if (damageMap.size() == 0) {
            return null;
        }

        // The damage and killer placeholders.
        int damage = 0;
        Player killer = null;

        for (Entry<Player, CombatDamageCache> entry : damageMap.entrySet()) {

            // Check if this entry is valid.
            if (entry == null) {
                continue;
            }

            // Check if the cached time is valid.
            long timeout = entry.getValue().getStopwatch().elapsed();

            if (timeout > CombatFactory.DAMAGE_CACHE_TIMEOUT) {
                continue;
            }

            // Check if the key for this entry is dead, too far, or has logged
            // out.
            Player player = entry.getKey();
            if (player.isDead() || !player.getPosition().withinDistance(
                    entity.getPosition(), 25) || player.getSession().getStage() != Stage.LOGGED_IN) {
                continue;
            }

            // If their damage is above the placeholder value, they become the
            // new 'placeholder'.
            if (entry.getValue().getDamage() > damage) {
                damage = entry.getValue().getDamage();
                killer = entry.getKey();
            }
        }

        // Clear the damage map if needed.
        if (clearMap)
            damageMap.clear();

        // Return the killer placeholder.
        return killer;
    }

    /**
     * Adds damage to the damage map, as long as the argued amount of damage is
     * above 0 and the argued entity is a player.
     * 
     * @param entity
     *            the entity to add damage for.
     * @param amount
     *            the amount of damage to add for the argued entity.
     */
    public void addDamage(Entity entity, int amount) {

        // No damage below 0, and no npcs can be added to the map.
        if (amount < 1 || entity.type() == EntityType.NPC) {
            return;
        }

        // Add on to the damage for existing players.
        Player player = (Player) entity;
        if (damageMap.containsKey(player)) {
            damageMap.get(player).incrementDamage(amount);
            return;
        }

        // Or add a completely new entry.
        damageMap.put(player, new CombatDamageCache(amount));
    }

    /**
     * Determines if this entity is attacking another entity.
     * 
     * @return true if this entity is attacking another entity.
     */
    public boolean isAttacking() {
        return victim != null;
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
     * Gets the entity this controller is currently attacking.
     * 
     * @return the entity this controller is currently attacking.
     */
    public Entity getVictim() {
        return victim;
    }

    /**
     * Determines if the builder is in cooldown mode.
     * 
     * @return true if the builder is in cooldown mode.
     */
    public boolean isCooldown() {
        return cooldown > 0;
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
     *            the entity that last attacked you.
     */
    public void setLastAttacker(Entity lastAttacker) {
        this.lastAttacker = lastAttacker;
    }

    /**
     * Gets the current combat strategy this npc is using.
     * 
     * @return the current combat strategy this npc is using.
     */
    public CombatStrategy getStrategy() {
        return strategy;
    }

    /**
     * Gets the combat task that runs the combat process.
     * 
     * @return the combat task that runs the combat process.
     */
    public CombatHookTask getCombatTask() {
        return combatTask;
    }

    /**
     * Sets the combat task that runs the combat process.
     * 
     * @param combatTask
     *            the combat task that runs the combat process.
     */
    public void setCombatTask(CombatHookTask combatTask) {
        this.combatTask = combatTask;
    }

    /**
     * Calculates and sets the combat strategy.
     */
    public void determineStrategy() {
        this.strategy = entity.determineStrategy();
    }

    /**
     * A value held in the damage map for caching damage dealt against an
     * {@link Entity}.
     * 
     * @author lare96
     */
    private static class CombatDamageCache {

        /** The amount of cached damage. */
        private int damage;

        /** The stopwatch to time how long the damage is cached. */
        private final Stopwatch stopwatch;

        /**
         * Create a new {@link CombatDamageCache}.
         * 
         * @param damage
         *            the amount of cached damage.
         */
        public CombatDamageCache(int damage) {
            this.damage = damage;
            this.stopwatch = new Stopwatch().reset();
        }

        /**
         * Gets the amount of cached damage.
         * 
         * @return the amount of cached damage.
         */
        public int getDamage() {
            return damage;
        }

        /**
         * Increments the amount of cached damage.
         * 
         * @param damage
         *            the amount of cached damage to add.
         */
        public void incrementDamage(int damage) {
            this.damage += damage;
            this.stopwatch.reset();
        }

        /**
         * Gets the stopwatch to time how long the damage is cached.
         * 
         * @return the stopwatch to time how long the damage is cached.
         */
        public Stopwatch getStopwatch() {
            return stopwatch;
        }
    }

    /**
     * An {@link EventListener} implementation that is used to listen for the
     * player to become in proper range of the victim.
     * 
     * @author lare96
     */
    private static class CombatDistanceListener extends EventListener {

        /** The combat builder. */
        private CombatBuilder builder;

        /** The victim being hunted. */
        private Entity victim;

        /**
         * Create a new {@link CombatDistanceListener}.
         * 
         * @param builder
         *            the combat builder.
         * @param victim
         *            the victim being hunted.
         */
        public CombatDistanceListener(CombatBuilder builder, Entity victim) {
            super();
            this.builder = builder;
            this.victim = victim;
        }

        @Override
        public boolean listenFor() {

            // Redetermine the strategy while we're walking to the victim, just
            // in case the entity activates some sort of special effect or
            // changes equipment.
            builder.determineStrategy();

            // Stop if we reset the cooldown, or the victim becomes too out of
            // range.
            if (builder.isCooldown() || !builder.entity.getPosition()
                    .isViewableFrom(victim.getPosition())) {

                builder.reset();
                this.cancel();
                return true;
            }

            // Stop if this entity is an npc and needs to retreat.
            if (builder.entity.type() == EntityType.NPC) {
                Npc npc = (Npc) builder.entity;

                if (!npc.getPosition()
                        .isViewableFrom(npc.getOriginalPosition()) && npc
                        .getDefinition().isRetreats()) {
                    npc.getMovementQueue().walk(npc.getOriginalPosition());
                    builder.reset();
                    this.cancel();
                    return true;
                }
            }

            // Reset the attack timer so we can attack straight away.
            builder.attackTimer = 0;


            // Start combat if we are in the correct distance.
            return !builder.entity.getPosition().withinDistance(
                    victim.getPosition(),
                    builder.strategy.attackDistance(builder.getEntity()));
        }

        @Override
        public void run() {

            // Reset the movement queue.
            builder.getEntity().getMovementQueue().reset();

            // Set the new target.
            builder.victim = victim;

            // Start a new combat task if needed.
            if (builder.getCombatTask() == null || !builder.getCombatTask()
                    .isRunning()) {
                builder.setCombatTask(new CombatHookTask(builder));
                TaskManager.submit(builder.getCombatTask());
            }
        }
    }
}
