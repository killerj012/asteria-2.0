package com.asteria.world.entity.npc;

import com.asteria.util.JsonLoader;
import com.asteria.world.World;
import com.asteria.world.entity.Entity;
import com.asteria.world.entity.Hit;
import com.asteria.world.entity.combat.CombatFactory;
import com.asteria.world.entity.combat.CombatFactory.CombatType;
import com.asteria.world.entity.combat.CombatStrategy;
import com.asteria.world.entity.combat.effect.CombatPoisonEffect.PoisonType;
import com.asteria.world.entity.npc.NpcMovementCoordinator.Coordinator;
import com.asteria.world.entity.npc.aggression.NpcAggression;
import com.asteria.world.map.Position;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * A non-player-character that is able to interact with other entities and world
 * models, only when forced to by the server.
 * 
 * @author blakeman8192
 * @author lare96
 */
public class Npc extends Entity {

    /** The id of the npc based on its npc definition. */
    private int npcId;

    /** The npc's max health. */
    private int maxHealth;

    /** The npc's current health. */
    private int currentHealth;

    /** If this npc respawns or not. */
    private boolean respawn;

    /** Determines if this npc's stats have been weakened. */
    private boolean[] attackWeakened = new boolean[3],
            strengthWeakened = new boolean[3];

    /** The movement coordinator for this npc. */
    private NpcMovementCoordinator movementCoordinator = new NpcMovementCoordinator(
            this);

    /** The npc's position from the moment of conception. */
    private final Position originalPosition;

    /** If this npc was originally walking randomly. */
    private boolean originalRandomWalk;

    /**
     * Create a new {@link Npc}.
     * 
     * @param npcId
     *            the id of the npc based on its npc definition.
     * @param position
     *            the position of the npc.
     */
    public Npc(int npcId, Position position) {
        this.npcId = npcId;
        this.originalPosition = position.clone();
        this.maxHealth = getDefinition().getHitpoints();
        this.currentHealth = maxHealth;
        this.getPosition().setAs(originalPosition);
        this.setAutoRetaliate(true);

        if (this.getDefinition().isAggressive()) {
            NpcAggression.getAggressive().put(this, originalPosition);
        }
    }

    @Override
    public Hit decrementHealth(Hit hit) {
        if (hit.getDamage() > currentHealth) {
            hit.setDamage(currentHealth);
        }
        currentHealth -= hit.getDamage();
        return hit;
    }

    @Override
    public void pulse() throws Exception {
        movementCoordinator.coordinate();
        getMovementQueue().execute();
    }

    @Override
    public void move(Position position) {
        Npc newNpc = this.clone();
        newNpc.getPosition().setAs(position);
        this.getPosition().setAs(new Position(1, 1));
        World.getNpcs().remove(this);
        World.getNpcs().add(newNpc);
    }

    @Override
    public int getAttackSpeed() {
        return this.getDefinition().getAttackSpeed();
    }

    @Override
    public int getCurrentHealth() {
        return this.getCurrentHP();
    }

    @Override
    public String toString() {
        return "NPC[slot= " + getSlot() + ", name=" + getDefinition().getName() + "]";
    }

    @Override
    public EntityType type() {
        return EntityType.NPC;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Npc)) {
            return false;
        }

        Npc n = (Npc) o;
        return n.getSlot() == getSlot();
    }

    @Override
    public CombatStrategy determineStrategy() {

        // TODO: Put this in a map, will get big and ugly after awhile if I
        // don't.
        switch (npcId) {
        case 13:
        case 172:
        case 174:
            return CombatFactory.newDefaultMagicStrategy();
        case 688:
            return CombatFactory.newDefaultRangedStrategy();
        }
        return CombatFactory.newDefaultMeleeStrategy();
    }

    @Override
    public int getBaseAttack(CombatType type) {
        return getDefinition().getAttackBonus();
    }

    @Override
    public int getBaseDefence(CombatType type) {
        if (type == CombatType.MAGIC)
            return getDefinition().getDefenceMage();
        else if (type == CombatType.RANGED)
            return getDefinition().getDefenceRange();
        return getDefinition().getDefenceMelee();
    }

    @Override
    public void poisonVictim(Entity victim, CombatType type) {
        if (getDefinition().isPoisonous()) {
            CombatFactory.poisonEntity(victim,type == CombatType.RANGED
                    || type == CombatType.MAGIC ? PoisonType.MILD
                                    : PoisonType.EXTRA);
        }
    }

    @Override
    public void heal(int damage) {
        if ((currentHealth + damage) > maxHealth) {
            currentHealth = maxHealth;
            return;
        }

        currentHealth += damage;
    }

    @Override
    public Npc clone() {

        // Not 100% accurate.
        Npc npc = new Npc(npcId, originalPosition);
        npc.currentHealth = currentHealth;
        npc.respawn = respawn;
        npc.attackWeakened = attackWeakened;
        npc.strengthWeakened = strengthWeakened;
        npc.movementCoordinator = movementCoordinator;
        npc.originalRandomWalk = originalRandomWalk;
        return npc;
    }

    /**
     * Prepares the dynamic json loader for loading world npcs.
     * 
     * @return the dynamic json loader.
     * @throws Exception
     *             if any errors occur while preparing for load.
     */
    public static JsonLoader parseNpcs() throws Exception {
        return new JsonLoader() {
            @Override
            public void load(JsonObject reader, Gson builder) {
                int id = reader.get("npc-id").getAsInt();
                Position position = builder.fromJson(reader.get("position")
                        .getAsJsonObject(), Position.class);
                Coordinator coordinator = builder.fromJson(
                        reader.get("walking-policy").getAsJsonObject(),
                        Coordinator.class);


                if (coordinator.isCoordinate() && coordinator.getRadius() == 0) {
                    throw new IllegalStateException(
                            "Radius must be higher than 0 when coordinator is active!");
                } else if (!coordinator.isCoordinate() && coordinator
                        .getRadius() > 0) {
                    throw new IllegalStateException(
                            "Radius must be 0 when coordinator is inactive!");
                }

                Npc npc = new Npc(id, position);
                npc.originalRandomWalk = coordinator.isCoordinate();
                npc.movementCoordinator.setCoordinator(coordinator);
                npc.respawn = true;
                World.getNpcs().add(npc);
            }

            @Override
            public String filePath() {
                return "./data/json/npcs/world_npcs.json";
            }
        };
    }

    /**
     * Gets the respawn time in ticks.
     * 
     * @return the respawn time in ticks.
     */
    public int getRespawnTime() {

        // -1 because the task is scheduled 1 tick late.
        return ((getDefinition().getRespawnTime() - 1) <= 0 ? 1
                : (getDefinition()
                .getRespawnTime() - 1));
    }

    /**
     * Gets the npc id.
     * 
     * @return the npc id.
     */
    public int getNpcId() {
        return npcId;
    }

    /**
     * Gets the max health of this npc.
     * 
     * @return the max health.
     */
    public int getMaxHealth() {
        return maxHealth;
    }

    /**
     * Gets this npc's current health.
     * 
     * @return the current health.
     */
    public int getCurrentHP() {
        return currentHealth;
    }

    /**
     * Sets this npc's current health.
     * 
     * @param currentHealth
     *            the new health value to set.
     */
    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = currentHealth;
    }

    /**
     * Gets the original position of this npc (from the moment of conception).
     * 
     * @return the original position.
     */
    public Position getOriginalPosition() {
        return originalPosition;
    }

    /**
     * Gets a npc definition.
     * 
     * @param id
     *            the npc definition to get.
     * @return the definition.
     */
    public NpcDefinition getDefinition() {
        return NpcDefinition.getDefinitions()[npcId];
    }

    /**
     * Gets if this npc was originally walking.
     * 
     * @return the original random walk.
     */
    public boolean isOriginalRandomWalk() {
        return originalRandomWalk;
    }

    /**
     * Sets if this npc should respawn on death.
     * 
     * @param respawn
     *            the respawn to set.
     */
    public void setRespawn(boolean respawn) {
        this.respawn = respawn;
    }

    /**
     * Gets if this npc will respawn on death.
     * 
     * @return the respawn.
     */
    public boolean isRespawn() {
        return respawn;
    }

    /**
     * Get the movement coordinator.
     * 
     * @return the movement coordinator.
     */
    public NpcMovementCoordinator getMovementCoordinator() {
        return movementCoordinator;
    }

    /**
     * @return the statsWeakened
     */
    public boolean[] getDefenceWeakened() {
        return attackWeakened;
    }

    /**
     * @return the statsBadlyWeakened
     */
    public boolean[] getStrengthWeakened() {
        return strengthWeakened;
    }
}
