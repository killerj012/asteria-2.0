package com.asteria.world.entity.player.skill;

import com.asteria.util.Utility;
import com.asteria.world.entity.Graphic;
import com.asteria.world.entity.UpdateFlags.Flag;
import com.asteria.world.entity.player.Player;

/**
 * A class that holds various utility methods for managing a player's
 * {@link Skill}s.
 * 
 * @author lare96
 */
public final class Skills {

    /** The indexes of the skills in the player's skill array. */
    public static final int ATTACK = 0, DEFENCE = 1, STRENGTH = 2,
            HITPOINTS = 3, RANGED = 4, PRAYER = 5, MAGIC = 6, COOKING = 7,
            WOODCUTTING = 8, FLETCHING = 9, FISHING = 10, FIREMAKING = 11,
            CRAFTING = 12, SMITHING = 13, MINING = 14, HERBLORE = 15,
            AGILITY = 16, THIEVING = 17, SLAYER = 18, FARMING = 19,
            RUNECRAFTING = 20;

    /**
     * The experience multiplier. All experience will be multiplied by this
     * number before being added to the player's skill.
     */
    public static final int EXP_RATE_MULTIPLIER = 1;

    /**
     * Holds constant data for {@link Skill}s.
     * 
     * @author lare96
     */
    public enum SkillData {
        ATTACK(6248, 6249, 6247),
        DEFENCE(6254, 6255, 6253),
        STRENGTH(6207, 6208, 6206),
        HITPOINTS(6217, 6218, 6216),
        RANGED(5453, 6114, 4443),
        PRAYER(6243, 6244, 6242, SkillEvent.PRAYER),
        MAGIC(6212, 6213, 6211),
        COOKING(6227, 6228, 6226, SkillEvent.COOKING),
        WOODCUTTING(4273, 4274, 4272, SkillEvent.WOODCUTTING),
        FLETCHING(6232, 6233, 6231, SkillEvent.FLETCHING),
        FISHING(6259, 6260, 6258, SkillEvent.FISHING),
        FIREMAKING(4283, 4284, 4282, SkillEvent.FIREMAKING),
        CRAFTING(6264, 6265, 6263, SkillEvent.CRAFTING),
        SMITHING(6222, 6223, 6221, SkillEvent.SMITHING),
        MINING(4417, 4438, 4416, SkillEvent.MINING),
        HERBLORE(6238, 6239, 6237, SkillEvent.HERBLORE),
        AGILITY(4278, 4279, 4277, SkillEvent.AGILITY),
        THIEVING(4263, 4264, 4261, SkillEvent.THIEVING),
        SLAYER(12123, 12124, 12122, SkillEvent.SLAYER),
        FARMING(4889, 4890, 4887, SkillEvent.FARMING),
        RUNECRAFTING(4268, 4269, 4267, SkillEvent.RUNECRAFTING);

        /** The lines that level up text will be printed on. */
        private int firstLine, secondLine;

        /** The chatbox interface displayed on level up. */
        private int chatbox;

        /** The index in the skill event array. */
        private int index;

        /**
         * Create a new {@link SkillData}.
         * 
         * @param firstLine
         *            the first line that level up text will be printed on.
         * @param secondLine
         *            the second line that level up text will be printed on.
         * @param chatbox
         *            the chatbox interface displayed on level up.
         * @param index
         *            the index in the skill event array.
         */
        private SkillData(int firstLine, int secondLine, int chatbox, int index) {
            this.firstLine = firstLine;
            this.secondLine = secondLine;
            this.chatbox = chatbox;
            this.index = index;
        }

        /**
         * Create a new {@link SkillData} with the default skill event index.
         * 
         * @param firstLine
         *            the first line that level up text will be printed on.
         * @param secondLine
         *            the second line that level up text will be printed on.
         * @param chatbox
         *            the chatbox interface displayed on level up.
         */
        private SkillData(int firstLine, int secondLine, int chatbox) {
            this(firstLine, secondLine, chatbox, -1);
        }

        /**
         * Gets the first line that level up text will be printed on.
         * 
         * @return the first line that level up text will be printed on.
         */
        public int getFirstLine() {
            return firstLine;
        }

        /**
         * Gets the second line that level up text will be printed on.
         * 
         * @return the second line that level up text will be printed on.
         */
        public int getSecondLine() {
            return secondLine;
        }

        /**
         * Gets the chatbox interface displayed on level up.
         * 
         * @return the chatbox interface displayed on level up.
         */
        public int getChatbox() {
            return chatbox;
        }

        /**
         * Gets the index in the skill event array.
         * 
         * @return the index in the skill event array.
         */
        public int getIndex() {
            return index;
        }

        /**
         * Gets a skill data constant instance by its position in the enum.
         * 
         * @param position
         *            the position of the constant to grab.
         * @return the constant on the argued position.
         */
        public static SkillData getSkill(int position) {
            return values()[position];
        }
    }

    /**
     * Adds the argued amount of experience to a certain {@link Skill} for an
     * {@link Player}. All experience added using this method is multiplied
     * using the <code>EXP_RATE_MULTIPLIER</code>.
     * 
     * @param player
     *            the player being granted the experience.
     * @param amount
     *            the amount of experience being given.
     * @param skill
     *            the skill this experience is being given to.
     */
    public static void experience(Player player, int amount, int skill) {

        // Get the level and experience before adding the experience.
        int oldLevel = player.getSkills()[skill].getLevelForExperience();
        int experience = player.getSkills()[skill].getExperience();

        // Multiply the argued experience and add it.
        amount *= Skills.EXP_RATE_MULTIPLIER;

        player.getSkills()[skill].setExperience(experience + amount);

        // Check if we are able to level up and do so if needed.
        if (!(oldLevel >= 99)) {
            int newLevel = player.getSkills()[skill]
                    .calculateLevelForExperience();

            if (oldLevel < newLevel) {
                if (skill != 3) {
                    player.getSkills()[skill].setLevel(newLevel, true);
                } else {
                    int old = player.getSkills()[skill].getLevel();

                    player.getSkills()[skill].setLevel(old + 1, true);
                }
                levelUp(player, SkillData.getSkill(skill));
                player.graphic(new Graphic(199));
                player.getFlags().flag(Flag.APPEARANCE);
            }
        }

        // Refresh the skill once we're done.
        Skills.refresh(player, skill);
    }

    /**
     * Sent when a the player reaches a new skill level.
     * 
     * @param player
     *            the player leveling up.
     * @param skill
     *            the skill being advanced a level.
     */
    private static void levelUp(Player player, SkillData skill) {

        // Send the player an indication that they have leveled up.
        player.getPacketBuilder()
                .sendString(
                        "@dre@Congratulations, you've just advanced " + Utility
                                .appendIndefiniteArticle(skill.name()
                                        .toLowerCase().replaceAll("_", " ")) + " level!",
                        skill.getFirstLine());
        player.getPacketBuilder()
                .sendString(
                        "Your " + skill.name().toLowerCase()
                                .replaceAll("_", " ") + " level is now " + player
                                .getSkills()[skill.ordinal()]
                                .getLevelForExperience() + ".",
                        skill.getSecondLine());
        player.getPacketBuilder().sendMessage(
                "Congratulations, you've just advanced " + Utility
                        .appendIndefiniteArticle(skill.name().toLowerCase()
                                .replaceAll("_", " ")) + " level!");
        player.getPacketBuilder().sendChatInterface(skill.getChatbox());
    }

    /**
     * Refreshes the argued {@link Skill} for the argued {@link Player}.
     * 
     * @param player
     *            the player refreshing the skill.
     * @param skill
     *            the skill being refreshed.
     */
    public static void refresh(Player player, int skill) {

        // Get the instance of the skill.
        Skill s = player.getSkills()[skill];

        // If the skill doesn't exist, we create it.
        if (s == null) {
            s = new Skill();

            if (skill == Skills.HITPOINTS) {
                s.setLevel(10, true);
                s.setExperience(1300);
            }

            player.getSkills()[skill] = s;
        }

        // Send the skill data to the client.
        player.getPacketBuilder().sendSkill(skill, s.getLevel(),
                s.getExperience());
    }

    /**
     * Refreshes all of the argued {@link Player}'s skills.
     * 
     * @param player
     *            the player to refresh all skills for.
     */
    public static void refreshAll(Player player) {

        // Refresh all of the skills.
        for (SkillData s : SkillData.values()) {
            refresh(player, s.ordinal());
        }
    }

    /**
     * Creates a completely new array of {@link Skill}s for the argued
     * {@link Player}.
     * 
     * @param player
     *            the player to create a new array of skills for.
     */
    public static void create(Player player) {

        // Loop through the array of skills.
        for (int i = 0; i < player.getSkills().length; i++) {

            // Create a new skill instance.
            player.getSkills()[i] = new Skill();

            // If the skill is hitpoints, set the level to 10.
            if (i == Skills.HITPOINTS) {
                player.getSkills()[i].setLevel(10, true);
                player.getSkills()[i].setRealLevel(10);
                player.getSkills()[i].setExperience(1300);
            }
        }
    }

    /**
     * Restores a certain skill back to its original level.
     * 
     * @param player
     *            the player to restore the skill for.
     * @param skill
     *            the skill to restore.
     */
    public static void restore(Player player, int skill) {

        // Restore it back to its original level.
        player.getSkills()[skill].setLevel(
                player.getSkills()[skill].getLevelForExperience(), true);

        // Refresh the skill after.
        refresh(player, skill);
    }

    /**
     * Restores all skills back to their original levels.
     * 
     * @param player
     *            the player to restore the skill for.
     */
    public static void restoreAll(Player player) {

        // Restore all of the skills.
        for (SkillData s : SkillData.values()) {
            restore(player, s.ordinal());
        }
    }

    private Skills() {}
}
