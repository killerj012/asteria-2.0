package server.world.entity.combat;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import server.core.Rs2Engine;
import server.world.entity.Entity;
import server.world.entity.combat.melee.MeleeHitWorker;

public class CombatSession {

    /**
     * All of the entities in combat with this entity.
     */
    private Set<Entity> participants = new HashSet<Entity>();

    /**
     * A map of all the entities who have inflicted damage
     */
    private Map<Entity, Integer> damageMap = new HashMap<Entity, Integer>();

    private Entity thisEntity;

    private Entity currentlyAttacking;

    private Entity lastHitBy;

    public CombatSession(Entity entity) {
        this.thisEntity = entity;
    }

    public void attackEntity(Entity entity) {
        if (!Combat.check(thisEntity, entity)) {
            resetCombat(entity);
            return;
        }

        Combat.combatTypePrepare(thisEntity);

        currentlyAttacking = entity;

        // switch (thisEntity.getType()) {
        // case MELEE:

        if (!isAttacking()) {
            Rs2Engine.getWorld().submit(new MeleeHitWorker(thisEntity));
        } else {
            Rs2Engine.getWorld().submit(new MeleeHitWorker(4, thisEntity));
        }
        // break;
        // case RANGE:
        // if (participants.isEmpty()) {
        //
        // } else {
        //
        // }
        // break;
        // case MAGIC:
        // if (participants.isEmpty()) {
        //
        // } else {
        //
        // }
        // break;
        // }
    }

    public void addToDamage(Entity entity, int amountDealt) {
        if (damageMap.containsKey(entity)) {
            int damageAlreadyDealt = damageMap.get(entity);

            damageMap.put(entity, (amountDealt + damageAlreadyDealt));
            return;
        }

        damageMap.put(entity, amountDealt);
    }

    public void resetCombat(Entity entity) {
        participants.remove(entity);
        damageMap.remove(entity);
        currentlyAttacking = null;
    }

    public void resetCombatAll() {
        participants.clear();
        damageMap.clear();
        currentlyAttacking = null;
    }

    public boolean isAttacking() {
        return currentlyAttacking != null;
    }

    public boolean isBeingAttacked() {
        return !participants.isEmpty();
    }

    /**
     * @return the currentlyAttacking
     */
    public Entity getCurrentlyAttacking() {
        return currentlyAttacking;
    }

    /**
     * @param currentlyAttacking
     *        the currentlyAttacking to set
     */
    public void setCurrentlyAttacking(Entity currentlyAttacking) {
        this.currentlyAttacking = currentlyAttacking;
    }

    /**
     * @return the participants
     */
    public Set<Entity> getParticipants() {
        return participants;
    }

    /**
     * @return the damageMap
     */
    public Map<Entity, Integer> getDamageMap() {
        return damageMap;
    }

    /**
     * @return the lastHitBy
     */
    public Entity getLastHitBy() {
        return lastHitBy;
    }

    /**
     * @param lastHitBy
     *        the lastHitBy to set
     */
    public void setLastHitBy(Entity lastHitBy) {
        this.lastHitBy = lastHitBy;
    }
}
