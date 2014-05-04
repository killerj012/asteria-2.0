package server.world.entity.player.skill;

import java.util.HashMap;
import java.util.Map;

import server.util.Misc;
import server.world.entity.Gfx;
import server.world.entity.player.Player;

/**
 * Holds static utility methods for managing skills.
 * 
 * @author lare96
 */
public class SkillManager {

    /**
     * How much the current exp rate will be multiplied by. The current
     * experience rates are identical to those in runescape. So in other words:
     * <br>
     * <br>
     * <code>EXP_RATE_MULTIPLIER*RS EXPERIENCE</code>
     */
    public static final int EXP_RATE_MULTIPLIER = 1;

    /**
     * All of the skills that can be trained.
     * 
     * @author lare96
     */
    public enum SkillConstant {
        ATTACK(6248, 6249, 6247, true, 4004, 4005, 4044, 4045),
        DEFENCE(6254, 6255, 6253, false, 4008, 4009, 4056, 4057),
        STRENGTH(6207, 6208, 6206, false, 4006, 4007, 4050, 4051),
        HITPOINTS(6217, 6218, 6216, false, 4016, 4017, 4080, 4081),
        RANGED(5453, 6114, 4443, false, 4010, 4011, 4062, 4063),
        PRAYER(6243, 6244, 6242, false, 4012, 4013, 4068, 4069),
        MAGIC(6212, 6213, 6211, false, 4014, 4015, 4074, 4075),
        COOKING(6227, 6228, 6226, false, 4034, 4035, 4134, 4135),
        WOODCUTTING(4273, 4274, 4272, false, 4038, 4039, 4146, 4147),
        FLETCHING(6232, 6233, 6231, false, 4026, 4027, 4110, 4111),
        FISHING(6259, 6260, 6258, false, 4032, 4033, 4128, 4129),
        FIREMAKING(4283, 4284, 4282, false, 4036, 4037, 4140, 4141),
        CRAFTING(6264, 6265, 6263, false, 4024, 4025, 4104, 4105),
        SMITHING(6222, 6223, 6221, false, 4030, 4031, 4122, 4123),
        MINING(4417, 4438, 4416, false, 4028, 4029, 4116, 4117),
        HERBLORE(6238, 6239, 6237, false, 4020, 4021, 4092, 4093),
        AGILITY(4278, 4279, 4277, true, 4018, 4019, 4086, 4087),
        THIEVING(4263, 4264, 4261, false, 4022, 4023, 4098, 4099),
        SLAYER(12123, 12124, 12122, false, 12166, 12167, 12171, 12172),
        FARMING(4889, 4890, 4887, false, 13926, 13927, 13921, 13922),
        RUNECRAFTING(4268, 4269, 4267, false, 4152, 4153, 4157, 4158);

        /**
         * The line that will be used to print text to an interface upon
         * leveling up.
         */
        private int firstLine, secondLine;

        /**
         * The interface that will be displayed in the chatbox when this skill
         * levels up.
         */
        private int sendChatbox;

        /**
         * If 'a' or 'an' should be used (true for 'an', false for 'a') for
         * grammar.
         */
        private boolean grammar;

        /** The line that will be used to refresh the skill. */
        private int refreshOne, refreshTwo, refreshThree, refreshFour;

        /** A map that holds the skill id mapped to the skill instance. */
        private static Map<Integer, SkillConstant> map = new HashMap<Integer, SkillConstant>();

        /** Fill the map with data. */
        static {
            for (SkillConstant s : SkillConstant.values()) {
                map.put(s.ordinal(), s);
            }
        }

        /**
         * Create a new {@link SkillConstant}.
         * 
         * @param firstLine
         *        the id of the first line sent when leveling up.
         * @param secondLine
         *        the id of the second line sent when leveling up.
         * @param sendChatbox
         *        the chatbox interface.
         * @param grammar
         *        if 'a' or 'an' should be used (true for 'an', false for 'a').
         * @param refreshOne
         *        the id of the first refresh line.
         * @param refreshTwo
         *        the id of the second refresh line.
         * @param refreshThree
         *        the id of the third refresh line.
         * @param refreshFour
         *        the id of the fourth refresh line.
         */
        SkillConstant(int firstLine, int secondLine, int sendChatbox, boolean grammar, int refreshOne, int refreshTwo, int refreshThree, int refreshFour) {
            this.firstLine = firstLine;
            this.secondLine = secondLine;
            this.sendChatbox = sendChatbox;
            this.grammar = grammar;
            this.refreshOne = refreshOne;
            this.refreshTwo = refreshTwo;
            this.refreshThree = refreshThree;
            this.refreshFour = refreshFour;
        }

        /**
         * Gets the id of the first line sent when leveling up.
         * 
         * @return the first line.
         */
        public int getFirstLine() {
            return firstLine;
        }

        /**
         * Gets the id of the second line sent when leveling up.
         * 
         * @return the second line.
         */
        public int getSecondLine() {
            return secondLine;
        }

        /**
         * Gets the chatbox interface.
         * 
         * @return the send chatbox.
         */
        public int getSendChatbox() {
            return sendChatbox;
        }

        /**
         * Gets if 'a' or 'an' should be used (true for 'an', false for 'a').
         * 
         * @return the grammar.
         */
        public boolean isGrammar() {
            return grammar;
        }

        /**
         * Gets the id of the first refresh line.
         * 
         * @return the refresh one.
         */
        public int getRefreshOne() {
            return refreshOne;
        }

        /**
         * Gets the id of the second refresh line.
         * 
         * @return the refresh two.
         */
        public int getRefreshTwo() {
            return refreshTwo;
        }

        /**
         * Gets the id of the third refresh line.
         * 
         * @return the refresh three.
         */
        public int getRefreshThree() {
            return refreshThree;
        }

        /**
         * Gets the id of the fourth refresh line.
         * 
         * @return the refresh four.
         */
        public int getRefreshFour() {
            return refreshFour;
        }

        /**
         * Gets a skill constant by its id.
         * 
         * @param id
         *        the id of the skill constant we are trying to get.
         * @return the skill constant with this id.
         */
        public static SkillConstant getSkill(int id) {
            return map.get(id);
        }
    }

    /**
     * Calculate the total level.
     * 
     * @param player
     *        the player to calculate the total level of.
     * @return the total level.
     */
    public static int totalLevel(Player player) {

        /** Calculate the new total level. */
        int totalLevel = 0;

        for (SkillConstant s : SkillConstant.values()) {
            totalLevel += player.getSkills()[s.ordinal()].getLevelForExperience();
        }

        return totalLevel;
    }

    /**
     * Adds the specified amount of experience to a certain skill for a player.
     * 
     * @param player
     *        the player being granted the experience.
     * @param amount
     *        the amount of experience being given.
     * @param skill
     *        the skill this experience is being given to.
     */
    public static void addExperience(Player player, int amount, int skill) {
        if (amount + player.getSkills()[skill].getExperience() < 0 || player.getSkills()[skill].getExperience() > 2000000000) {
            return;
        }

        int oldLevel = player.getSkills()[skill].getLevelForExperience();
        int experience = player.getSkills()[skill].getExperience();

        player.getSkills()[skill].setExperience(experience + amount);

        if (oldLevel < player.getSkills()[skill].getLevelForExperience()) {
            if (skill != 3) {
                player.getSkills()[skill].setLevel(player.getSkills()[skill].getLevelForExperience());
            } else {
                int old = player.getSkills()[skill].getLevel();

                player.getSkills()[skill].setLevel(old + 1);
            }
            levelUp(player, SkillConstant.getSkill(skill));
            player.gfx(new Gfx(199));
        }

        player.getPacketBuilder().sendSkill(skill, player.getSkills()[skill].getLevel(), player.getSkills()[skill].getExperience());
        SkillManager.refresh(player, SkillConstant.getSkill(skill));
    }

    /**
     * Sent when a the player reaches a new skill level.
     * 
     * @param player
     *        the player leveling up.
     * @param skill
     *        the skill advancing a level.
     */
    public static void levelUp(Player player, SkillConstant skill) {
        /** Calculate the new total level. */
        int totalLevel = 0;

        for (SkillConstant s : SkillConstant.values()) {
            totalLevel += player.getSkills()[s.ordinal()].getLevelForExperience();
        }

        /** Send the player an indication that they have leveled up. */
        player.getPacketBuilder().sendString("Total Lvl: " + totalLevel, 3984);
        player.getPacketBuilder().sendString(skill.isGrammar() ? "Congratulations, you've just advanced an " + skill.name().toLowerCase().replaceAll("_", " ") + " level!" : "Congratulations, you've just advanced a " + skill.name().toLowerCase().replaceAll("_", " ") + " level!", skill.getFirstLine());
        player.getPacketBuilder().sendString("Your " + skill.name().toLowerCase().replaceAll("_", " ") + " level is now " + player.getSkills()[skill.ordinal()].getLevel() + ".", skill.getSecondLine());
        player.getPacketBuilder().sendMessage(skill.isGrammar() ? "Congratulations, you've just advanced an " + skill.name().toLowerCase().replaceAll("_", " ") + " level!" : "Congratulations, you've just advanced a " + skill.name().toLowerCase().replaceAll("_", " ") + " level!");
        player.getPacketBuilder().sendChatInterface(skill.getSendChatbox());
    }

    /**
     * Refreshes a players skill.
     * 
     * @param player
     *        the player refreshing the skill.
     * @param skill
     *        the skill being refreshed.
     */
    public static void refresh(Player player, SkillConstant skill) {

        if (player.getSkills()[skill.ordinal()] == null) {
            player.getSkills()[skill.ordinal()] = new Skill();
            if (skill.ordinal() == 3) {
                player.getSkills()[skill.ordinal()].setLevel(10);
                player.getSkills()[skill.ordinal()].setExperience(1300);
            }
        }

        player.getPacketBuilder().sendString("" + player.getSkills()[skill.ordinal()].getLevel() + "", skill.getRefreshOne());
        player.getPacketBuilder().sendString("" + player.getSkills()[skill.ordinal()].getLevelForExperience() + "", skill.getRefreshTwo());
        player.getPacketBuilder().sendString("" + player.getSkills()[skill.ordinal()].getExperience() + "", skill.getRefreshThree());
        player.getPacketBuilder().sendString("" + player.getSkills()[skill.ordinal()].getExperienceForNextLevel() + "", skill.getRefreshFour());

        if (skill == SkillConstant.PRAYER) {
            player.getPacketBuilder().sendString("Prayer: " + player.getSkills()[Misc.PRAYER].getLevel() + "/" + player.getSkills()[Misc.PRAYER].getLevelForExperience() + "", 687);
        }
    }

    /**
     * Refreshes all the skills.
     * 
     * @param player
     *        the player to refresh all skills for.
     */
    public static void refreshAll(Player player) {
        /** New local variable. */
        int totalLevel = 0;

        /** Refresh total level and stats. */
        for (SkillConstant s : SkillConstant.values()) {
            refresh(player, s);
            totalLevel += player.getSkills()[s.ordinal()].getLevelForExperience();
        }

        /** Send new total level. */
        player.getPacketBuilder().sendString("Total Lvl: " + totalLevel, 3984);
    }

    /**
     * Refreshes skills on login.
     * 
     * @param player
     *        the player to refresh.
     */
    public static void login(Player player) {
        for (int i = 0; i < player.getSkills().length; i++) {
            player.getSkills()[i] = new Skill();

            if (i == 3) {
                player.getSkills()[i].setLevel(10);
                player.getSkills()[i].setExperience(1300);
            }
        }
    }
}
