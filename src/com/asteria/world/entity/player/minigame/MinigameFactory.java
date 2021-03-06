package com.asteria.world.entity.player.minigame;

import java.util.HashMap;
import java.util.Map;

import com.asteria.world.entity.player.Player;

/**
 * Holds static utility methods that manage minigames.
 * 
 * @author lare96
 */
public final class MinigameFactory {

    // TODO: Rewrite this, add more functionality.

    /** A map containing all of the minigames. */
    private static Map<String, Minigame> minigames = new HashMap<>();

    /** Loads the instances of all minigames into a map.. */
    public static void loadMinigames() {
        // minigames.put("Pest Control", new PestControl());
    }

    /**
     * Gets the instance of the minigame the player is currently in.
     * 
     * @param player
     *            the player to get the instance for.
     * @return the instance of the minigame.
     */
    public static Minigame getMinigame(Player player) {
        for (Minigame minigame : minigames.values()) {
            if (minigame.inMinigame(player)) {
                return minigame;
            }
        }
        return null;
    }

    /**
     * Determines if the player is even in a minigame.
     * 
     * @param player
     *            the player to check for.
     * @return true if the player is in a minigame.
     */
    public static boolean inMinigame(Player player) {
        return getMinigame(player) != null;
    }

    /**
     * Gets the map containing all of the minigames.
     * 
     * @return the map containing all of the minigames.
     */
    public static Map<String, Minigame> getMinigames() {
        return minigames;
    }

    private MinigameFactory() {}
}
