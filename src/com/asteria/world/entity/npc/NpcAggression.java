package com.asteria.world.entity.npc;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.asteria.world.entity.Entity;
import com.asteria.world.entity.Entity.EntityType;
import com.asteria.world.entity.npc.policy.DefaultAggressionPolicy;
import com.asteria.world.entity.npc.policy.GreenGoblinAggressionPolicy;
import com.asteria.world.entity.npc.policy.RedGoblinAggressionPolicy;
import com.asteria.world.entity.player.Player;
import com.asteria.world.map.Location;
import com.asteria.world.map.Position;

/**
 * Handles the behavior of aggressive {@link Npc}s around players within the
 * <code>NPC_TARGET_DISTANCE</code> radius.
 * 
 * @author lare96
 */
public final class NpcAggression {

    /** How far aggressive npcs will look for targets. */
    public static final int NPC_TARGET_DISTANCE = 6;

    /**
     * Time that has to be spent in a region before npcs stop acting aggressive
     * toward a specific player.
     */
    public static final int NPC_TOLERANCE_SECONDS = 600;

    /**
     * A map that holds all of the aggressive npcs and their original positions.
     * We use this so only the aggressive npcs are accounted for.
     */
    private static Map<Npc, Position> aggressive = new HashMap<>();

    /** A map that holds the policies of all aggressive npcs. */
    private static Map<Integer, NpcAggressionPolicy> policies = new HashMap<>();

    /**
     * The aggressive npc will be prompted to attack the entity if they are
     * within <code>NPC_TARGET_DISTANCE</code> squares and the entity is not in
     * a multicombat area while not in combat. If the entity is in a multicombat
     * area, they will be attacked regardless if they are in combat or not. If
     * the entity is a player and has been in the area for
     * <code>NPC_TOLERANCE_SECONDS</code> seconds then aggressive npcs will stop
     * attacking the player to prevent AFK training. Once the player enters a
     * new region, the timer will reset and aggressive npcs will begin attacking
     * again.
     * 
     * @param entity
     *            the entity that will be attempted to be targeted by the
     *            aggressive npcs.
     */
    public static void target(Entity entity) {

        // Loop through all of the aggressive npcs.
        for (Entry<Npc, Position> entry : aggressive.entrySet()) {
            Position position = entry.getValue();
            Npc npc = entry.getKey();

            // Check if the entity is within distance.
            if (position.withinDistance(entity.getPosition(),
                    NPC_TARGET_DISTANCE)) {

                // Check if the aggressive npc is attacking or being attacked.
                if (npc.getCombatBuilder().isAttacking() || npc
                        .getCombatBuilder().isBeingAttacked() || npc
                        .equals(entity)) {
                    continue;
                }

                // Check if the entity is attacking or being attacked.
                if (!Location.inMultiCombat(entity) && entity
                        .getCombatBuilder().isAttacking() || entity
                        .getCombatBuilder().isBeingAttacked()) {
                    npc.getMovementCoordinator().setCoordinate(
                            npc.isOriginalRandomWalk());
                    continue;
                }

                // Check if the entity is a player and if they have been in the
                // region too long, and if they are the right level.
                if (entity.type() == EntityType.PLAYER) {
                    Player player = (Player) entity;

                    if (player.getTolerance().elapsed() > (NPC_TOLERANCE_SECONDS * 1000)) {
                        npc.getMovementCoordinator().setCoordinate(
                                npc.isOriginalRandomWalk());
                        continue;
                    }
                    if (!(player.getCombatLevel() <= (npc.getDefinition()
                            .getCombatLevel() * 2)) && !Location
                            .inWilderness(player)) {
                        npc.getMovementCoordinator().setCoordinate(
                                npc.isOriginalRandomWalk());
                        continue;
                    }
                }

                // Get the policy, if no policy is found then we use the default
                // one.
                NpcAggressionPolicy policy;

                if ((policy = policies.get(npc.getNpcId())) == null) {
                    policy = new DefaultAggressionPolicy();
                }

                // Check if we can attack based on the policy.
                if (policy.attackIf(npc, entity)) {

                    // We passed all of the checks, the npc can attack the
                    // player. We also apply any policy effects.
                    npc.getCombatBuilder().attack(entity);
                    policy.onAttack(npc, entity);
                }
            }
        }
    }

    /**
     * Loads all {@link NpcAggressionPolicy}s into the map of policies on
     * startup.
     */
    public static void loadPolicies() {

        // Add all custom aggression policies here.
        addPolicy(new GreenGoblinAggressionPolicy());
        addPolicy(new RedGoblinAggressionPolicy());
    }

    /**
     * Adds an {@link NpcAggressionPolicy} to the map of <code>policies</code>.
     * 
     * @param policy
     *            the policy to add to the map.
     */
    private static void addPolicy(NpcAggressionPolicy policy) {

        // Check and validate the identifiers.
        if (policy.identifiers() == null) {
            throw new IllegalArgumentException(
                    "Cannot add a policy with no indentifers!");
        }

        // Add the policy for all of the identifiers.
        for (int id : policy.identifiers()) {
            policies.put(id, policy);
        }
    }

    /**
     * Gets the map of aggressive npcs and their original positions.
     * 
     * @return the map of aggressive npcs and their original positions.
     */
    public static Map<Npc, Position> getAggressive() {
        return aggressive;
    }
}
