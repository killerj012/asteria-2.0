package com.asteria.world.entity.player.skill;

import java.util.HashSet;
import java.util.Set;

import com.asteria.world.entity.player.Player;
import com.asteria.world.entity.player.skill.Skills.SkillData;

/**
 * A parent class that fires various events for skills when needed.
 * 
 * @author lare96
 */
public abstract class SkillEvent {

    // TODO: Support for skilling actions.

    /** The indexes for all of the skills. */
    protected static final int PRAYER = 0, COOKING = 1, WOODCUTTING = 2,
            FLETCHING = 3, FISHING = 4, FIREMAKING = 5, CRAFTING = 6,
            SMITHING = 7, MINING = 8, HERBLORE = 9, AGILITY = 10,
            THIEVING = 11, SLAYER = 12, FARMING = 13, RUNECRAFTING = 14;

    /** A set of every unique {@link SkillEvent} that has fireable events. */
    private static Set<SkillEvent> skillEvents = new HashSet<>();

    /**
     * The event that will be fired when this skill needs to be stopped.
     * 
     * @param player
     *            the player this skill needs to be stopped for.
     */
    public abstract void stopSkill(Player player);

    /**
     * The {@link SkillData} for this skill.
     * 
     * @return the skill constant for this skill.
     */
    public abstract SkillData skill();

    /**
     * Starts this skill by flagging the skill event for it.
     * 
     * @param player
     *            the player to start the skill for.
     */
    public void startSkill(Player player) {
        player.getSkillEvent()[skill().getIndex()] = true;
    }

    /**
     * Loads the instances of all skills on startup.
     */
    public static void loadSkills() {
        // skillEvents.add(new Fishing());
    }

    /**
     * Fires the reset event methods for all of the coded {@link SkillEvent}s.
     * 
     * @param player
     *            the player to fire the policy for.
     */
    public static void fireSkillEvents(Player player) {

        // Iterate through the registered skills and fire events.
        for (SkillEvent skill : skillEvents) {
            if (skill.skill().getIndex() == -1)
                continue;

            if (player.getSkillEvent()[skill.skill().getIndex()]) {
                skill.stopSkill(player);
                player.getSkillEvent()[skill.skill().getIndex()] = false;
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
