package com.asteria.world.entity.npc;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.asteria.world.entity.player.Player;
import com.asteria.world.map.Location;
import com.asteria.world.map.Position;

/**
 * Handles the behavior of aggressive {@link Npc}s around players within the
 * <code>NPC_TARGET_DISTANCE</code> radius.
 * 
 * @author lare96
 */
public class NpcAggression {

    /** How far aggressive npcs will look for players. */
    public static final int NPC_TARGET_DISTANCE = 5;

    /**
     * Time that has to be spent in a region before npcs stop acting aggressive
     * toward the specific player.
     */
    public static final int NPC_TOLERANCE_SECONDS = 600;

    /**
     * A map that holds all of the aggressive npcs and their original positions.
     * We use this so only the aggressive npcs are accounted for.
     */
    private static Map<Npc, Position> aggressiveMap = new HashMap<>();

    /**
     * The aggressive npc will be prompted to attack the player if they are
     * within <code>NPC_TARGET_DISTANCE</code> squares and the player is not in
     * a multicombat area while not in combat. If the player is in a multicombat
     * area, they will be attacked regardless if they are in combat or not. If
     * the player has been in the area for <code>NPC_TOLERANCE_SECONDS</code>
     * seconds then aggressive npcs will stop attacking the player to prevent
     * AFK training. Once the player enters a new region, the timer will reset
     * and aggressive npcs will begin attacking again.
     * 
     * @param player
     *            the player that will be attempted to be targeted by the
     *            aggressive npcs.
     */
    public static void targetPlayer(Player player) {

        // Loop through all of the aggressive npcs.
        for (Entry<Npc, Position> entry : NpcAggression.getAggressive()
                .entrySet()) {
            Position position = entry.getValue();
            Npc npc = entry.getKey();

            // Check if the player is within distance.
            if (position.withinDistance(player.getPosition(),
                    NPC_TARGET_DISTANCE)) {

                // Check if the aggressive npc is attacking or being attacked.
                if (npc.getCombatBuilder().isAttacking() || npc
                        .getCombatBuilder().isBeingAttacked()) {
                    continue;
                }

                // Check if the player is attacking or being attacked.
                if (!Location.inMultiCombat(player) && player
                        .getCombatBuilder().isAttacking() || player
                        .getCombatBuilder().isBeingAttacked()) {
                    npc.getMovementCoordinator().setCoordinate(
                            npc.isOriginalRandomWalk());
                    continue;
                }

                // Check if the player has been in the region too long.
                if (player.getTolerance().elapsed() > (NPC_TOLERANCE_SECONDS * 1000)) {
                    npc.getMovementCoordinator().setCoordinate(
                            npc.isOriginalRandomWalk());
                    continue;
                }

                // We passed all of the checks, the npc can attack the player.
                npc.getCombatBuilder().attack(player);
            }
        }
    }

    /**
     * Gets the map of aggressive npcs and their original positions.
     * 
     * @return the map of aggressive npcs and their original positions.
     */
    public static Map<Npc, Position> getAggressive() {
        return aggressiveMap;
    }
}
