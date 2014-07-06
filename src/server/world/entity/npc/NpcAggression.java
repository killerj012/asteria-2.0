package server.world.entity.npc;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import server.world.entity.player.Player;
import server.world.map.Location;
import server.world.map.Position;

/**
 * Handles the behavior of aggressive npcs around players within a certain
 * radius.
 * 
 * @author lare96
 */
public class NpcAggression {

    /** How far aggressive npcs will look for players. */
    private static final int NPC_TARGET_DISTANCE = 5;

    /** Time the player has to spend before npcs stop acting aggressive. */
    private static final int NPC_TOLERANCE_SECONDS = 600;

    /**
     * A map that holds all of the aggressive npcs and their original positions.
     * We use this so we do not have to unnecessarily loop through players and
     * so only the aggressive npcs are accounted for.
     */
    private static Map<Npc, Position> aggressive = new HashMap<Npc, Position>();

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
        for (Entry<Npc, Position> entry : NpcAggression.getAggressive().entrySet()) {
            Position position = entry.getValue();
            Npc npc = entry.getKey();

            if (position.withinDistance(player.getPosition(),
                    NPC_TARGET_DISTANCE)) {

                if(npc.getCombatBuilder().isAttacking()
                        || npc.getCombatBuilder().isBeingAttacked()){
                    continue;
                }

                if (!Location.inMultiCombat(player)
                        && player.getCombatBuilder().isAttacking()
                        || player.getCombatBuilder().isBeingAttacked()) {
                    npc.getMovementCoordinator().setCoordinate(
                            npc.isOriginalRandomWalk());
                    continue;
                }

                if (player.getTolerance().elapsed() > (NPC_TOLERANCE_SECONDS * 1000)) {
                    npc.getMovementCoordinator().setCoordinate(
                            npc.isOriginalRandomWalk());
                    continue;
                }

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
        return aggressive;
    }
}
