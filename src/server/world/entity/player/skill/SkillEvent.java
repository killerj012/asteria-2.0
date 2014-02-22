package server.world.entity.player.skill;

import java.util.HashSet;
import java.util.Set;

import server.world.entity.player.Player;
import server.world.entity.player.skill.SkillManager.SkillConstant;

/**
 * Handles the main functions of non-combat skills.
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

    /**
     * A set of every unique {@link SkillEvent} that was registered from the
     * <code>server.world.entity.player.skill.impl</code> package.
     */
    private static Set<SkillEvent> skillEvents = new HashSet<SkillEvent>();

    /**
     * Determines what will happen when this {@link SkillEvent} is reset.
     * 
     * @param player
     *        the player that will be affected by this event.
     */
    public abstract void fireResetEvent(Player player);

    /**
     * Gets the value of the index in the event reset array.
     * 
     * @return the value of the index in the event reset array.
     */
    public abstract int eventFireIndex();

    /**
     * An instance of the skill itself.
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
        SkillManager.addExperience(player, amount, skillConstant().ordinal());
    }

    /**
     * Fires the reset policy of a {@link SkillEvent} that needs to be reset.
     * 
     * @param player
     *        the player to fire the policy for.
     */
    public static void resetSkillEvent(Player player) {
        for (SkillEvent skill : skillEvents) {
            if (skill == null) {
                continue;
            }

            if (player.getSkillEvent()[skill.eventFireIndex()]) {
                skill.fireResetEvent(player);
                player.getSkillEvent()[skill.eventFireIndex()] = false;
            }
        }
    }

    /**
     * Gets the set of skill events.
     * 
     * @return the skills.
     */
    public static Set<SkillEvent> getSkillEvents() {
        return skillEvents;
    }
}
