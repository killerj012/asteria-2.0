package server.world.entity.player.skill;

import java.util.HashSet;
import java.util.Set;

import server.world.entity.player.Player;
import server.world.entity.player.skill.SkillManager.SkillConstant;

/**
 * A parent class that fires events for skills.
 * 
 * @author lare96
 */
public abstract class SkillEvent {

    /**
     * The indexes for all of the various non-combat skills in the event reset
     * array.
     */
    protected static final int PRAYER = 0, COOKING = 1, WOODCUTTING = 2,
            FLETCHING = 3, FISHING = 4, FIREMAKING = 5, CRAFTING = 6,
            SMITHING = 7, MINING = 8, HERBLORE = 9, AGILITY = 10,
            THIEVING = 11, SLAYER = 12, FARMING = 13, RUNECRAFTING = 14;

    /** A set of every unique {@link SkillEvent} that has fireable events. */
    private static Set<SkillEvent> skillEvents = new HashSet<SkillEvent>();

    /**
     * Fired when the <code>fireSkillEvent(Player)</code> method is invoked.
     * 
     * @param player
     *        the player that this event will be fired for.
     */
    public abstract void fireResetEvent(Player player);

    /**
     * Gets the value of the index in the event reset array.
     * 
     * @return the value of the index in the event reset array.
     */
    public abstract int eventFireIndex();

    /**
     * A {@link SkillConstant} instance of the skill.
     * 
     * @return the instance of the skill.
     */
    public abstract SkillConstant skillConstant();

    /**
     * Adds experience for this skill.
     * 
     * @param player
     *        the player to add experience for.
     * @param amount
     *        the amount to add.
     */
    public void exp(Player player, int amount) {
        SkillManager.addExperience(player, amount, skillConstant());
    }

    /**
     * Fires the <code>fireResetEvent(Player)</code> method for all of the
     * coded {@link SkillEvent}s.
     * 
     * @param player
     *        the player to fire the policy for.
     */
    public static void fireSkillEvents(Player player) {

        /** Iterate through the registered skills and fire events. */
        for (SkillEvent skill : skillEvents) {
            if (player.getSkillEvent()[skill.eventFireIndex()]) {
                skill.fireResetEvent(player);
                player.getSkillEvent()[skill.eventFireIndex()] = false;
            }
        }
    }

    /**
     * Gets the {@link HashSet} of coded {@link SkillEvent}s.
     * 
     * @return the set of coded skills.
     */
    public static Set<SkillEvent> getSkillEvents() {
        return skillEvents;
    }
}
