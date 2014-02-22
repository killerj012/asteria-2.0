package server.world.entity.player.content;

import server.world.entity.player.Player;

public class CatFighter {

    /** The amount of tokens needed to advance a level. */
    private static final int NEED_TOKEN_AMOUNT_FOR_LEVEL = 5;

    /** The maximum level for a skill. */
    private static final int MAXIMUM_LEVEL = 750;

    /** The constant skill slots. */
    private static int ATTACK_LEVEL = 0, STRENGTH_LEVEL = 1, DEFENCE_LEVEL = 2,
            INTELLECT_LEVEL = 3;

    /** The owner of this cat. */
    private Player player;

    /** The name of this cat. */
    private String name;

    /** The skill levels this cat has. */
    private int[] level = new int[4];

    /** The amount of fights this cat has won. */
    private int fightsWon;

    /** The amount of fights this cat has lost. */
    private int fightsLost;

    // TODO: tokens let you advance your cat's levle
    // name ur cat
    // 'duel' style.

    public CatFighter(String name, Player player) {
        this.name = name;
        this.player = player;
    }

    /**
     * Increases a level by the specified amount.
     * 
     * @param amount
     *        the amount to increase this level by.
     * @param id
     *        the id of the level to increase.
     */
    public void incrementLevel(int amount, int id) {

        /** Block if we've reached the maximum level. */
        if (level[id] == MAXIMUM_LEVEL) {
            player.getPacketBuilder().sendMessage(name + " has reached the maximum level for this skill!");
            return;
        }

        /** Otherwise increment the level. */
        level[id] += amount;
    }

    /**
     * Gets the combat level for this cat.
     * 
     * @return the combat level.
     */
    public int getCombatLevel() {

        /** Start the combat level at 1. */
        int combatLevel = 1;

        /** Get the total level. */
        for (int i : level) {
            combatLevel += i;
        }

        /** Divide by eight and return. */
        return (combatLevel / 8);
    }

    /**
     * Gets the the win/lose ratio for this cat.
     * 
     * @return the win/lose ratio.
     */
    public double getWinLoseRatio() {
        return (fightsWon / fightsLost);
    }
}
