package server.world.entity.combat;

import server.core.worker.TaskFactory;
import server.core.worker.listener.EventListener;
import server.util.Misc;
import server.world.entity.Entity;
import server.world.entity.player.Player;

/**
 * Handles the entire combat process for the specified entity.
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

    public CombatBuilder(Entity entity) {
        this.entity = entity;
    }

    public void attack(final Entity victim, final CombatStrategy finalStrategy) {
        // XXX: follow entity and listener shit here

        final CombatBuilder builder = this;

        TaskFactory.getFactory().submit(new EventListener() {
            private CombatStrategy strategy = finalStrategy;

            @Override
            public boolean listenForEvent() {
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

                return false;
            }

            @Override
            public void run() {
                if (currentTarget != null) {
                    currentTarget = victim;
                    return;
                }

                if (!strategy.prepareAttack(entity)) {
                    System.out.println("stopped2");
                    return;
                }

                attackTimer = strategy.attackTimer(entity);
                currentTarget = victim;

                if (combatWorker == null || !combatWorker.isRunning()) {
                    combatWorker = new CombatWorker(builder, strategy);
                    TaskFactory.getFactory().submit(combatWorker);
                }
            }
        });
    }

    public void incrementAttackTimer() {
        attackTimer++;
    }

    public void decrementAttackTimer() {
        attackTimer--;
    }

    public void clearAttackTimer() {
        attackTimer = 0;
    }

    public void setAttackTimer(int value) {
        attackTimer = value;
    }

    public int getAttackTimer() {
        return attackTimer;
    }

    public void reset() {
        currentTarget = null;
        combatWorker = null;
        attackTimer = 0;
    }

    public void resetAttackTimer() {
        if (combatWorker == null || !combatWorker.isRunning()) {
            return;
        }

        attackTimer = combatWorker.getStrategy().attackTimer(entity);
    }

    // /**
    // * Gets the entity who inflicted the most damage.
    // *
    // * @return the entity who inflicted the most damage.
    // */
    // public Entity getMostDamageInflicted() {
    //
    // /** If we weren't killed by a player or npc return null. */
    // if (damageMap.size() == 0) {
    // return null;
    // }
    //
    // /** The value we are searching for - the highest value in the damage map.
    // */
    // int searchValue = Collections.max(damageMap.values()).intValue();
    //
    // /** Search for the value and return the key for that value (the entity).
    // */
    // for (Entry<Entity, Integer> nextEntry : damageMap.entrySet()) {
    // if (nextEntry.getValue().intValue() == searchValue) {
    // return nextEntry.getKey();
    // }
    // }
    // return null;
    // }
    //
    // public void addDamage(Entity entity, int amountDealt) {
    // if (damageMap.containsKey(entity)) {
    // int damageAlreadyDealt = damageMap.get(entity);
    //
    // damageMap.put(entity, (amountDealt + damageAlreadyDealt));
    // return;
    // }
    //
    // damageMap.put(entity, amountDealt);
    // }

    /**
     * @return the entity
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * @param entity
     *        the entity to set
     */
    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    /**
     * @return the currentTarget
     */
    public Entity getCurrentTarget() {
        return currentTarget;
    }

    /**
     * @param currentTarget
     *        the currentTarget to set
     */
    public void setCurrentTarget(Entity currentTarget) {
        this.currentTarget = currentTarget;
    }

    public boolean isAttacking() {
        return currentTarget != null;
    }

    public boolean isBeingAttacked() {
        return combatWorker != null && combatWorker.isRunning();
    }

    /**
     * @return the lastAttacker
     */
    public Entity getLastAttacker() {
        return lastAttacker;
    }

    /**
     * @param lastAttacker
     *        the lastAttacker to set
     */
    public void setLastAttacker(Entity lastAttacker) {
        this.lastAttacker = lastAttacker;
    }
}
