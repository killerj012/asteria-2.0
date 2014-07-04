package server.world.entity.player.skill;

import server.util.Misc;
import server.world.entity.Gfx;
import server.world.entity.UpdateFlags.Flag;
import server.world.entity.player.Player;

/**
 * Holds static utility methods for managing skills.
 * 
 * @author lare96
 */
public final class SkillManager {

    /**
     * How much the current exp rate will be multiplied by. The higher this
     * number, the more exp given.
     */
    public static final int EXP_RATE_MULTIPLIER = 1;

    /**
     * Holds constant data for all of the skills that can be trained.
     * 
     * @author lare96
     */
    public enum SkillConstant {
        ATTACK(6248, 6249, 6247, 4004, 4005, 4044, 4045),
        DEFENCE(6254, 6255, 6253, 4008, 4009, 4056, 4057),
        STRENGTH(6207, 6208, 6206, 4006, 4007, 4050, 4051),
        HITPOINTS(6217, 6218, 6216, 4016, 4017, 4080, 4081),
        RANGED(5453, 6114, 4443, 4010, 4011, 4062, 4063),
        PRAYER(6243, 6244, 6242, 4012, 4013, 4068, 4069),
        MAGIC(6212, 6213, 6211, 4014, 4015, 4074, 4075),
        COOKING(6227, 6228, 6226, 4034, 4035, 4134, 4135),
        WOODCUTTING(4273, 4274, 4272, 4038, 4039, 4146, 4147),
        FLETCHING(6232, 6233, 6231, 4026, 4027, 4110, 4111),
        FISHING(6259, 6260, 6258, 4032, 4033, 4128, 4129),
        FIREMAKING(4283, 4284, 4282, 4036, 4037, 4140, 4141),
        CRAFTING(6264, 6265, 6263, 4024, 4025, 4104, 4105),
        SMITHING(6222, 6223, 6221, 4030, 4031, 4122, 4123),
        MINING(4417, 4438, 4416, 4028, 4029, 4116, 4117),
        HERBLORE(6238, 6239, 6237, 4020, 4021, 4092, 4093),
        AGILITY(4278, 4279, 4277, 4018, 4019, 4086, 4087),
        THIEVING(4263, 4264, 4261, 4022, 4023, 4098, 4099),
        SLAYER(12123, 12124, 12122, 12166, 12167, 12171, 12172),
        FARMING(4889, 4890, 4887, 13926, 13927, 13921, 13922),
        RUNECRAFTING(4268, 4269, 4267, 4152, 4153, 4157, 4158);

        /** The lines that level up text will be printed on. */
        private int firstLine, secondLine;

        /** The chatbox interface displayed on level up. */
        private int chatbox;

        /** The lines that will be used to refresh the skill. */
        private int[] refresh;

        /**
         * Create a new {@link SkillConstant}.
         * 
         * @param firstLine
         *            the id of the first line sent when leveling up.
         * @param secondLine
         *            the id of the second line sent when leveling up.
         * @param chatbox
         *            the chatbox interface that will be sent.
         * @param refresh
         *            the lines that will be used to refresh the skill.
         */
        private SkillConstant(int firstLine, int secondLine, int chatbox,
                int... refresh) {
            this.firstLine = firstLine;
            this.secondLine = secondLine;
            this.chatbox = chatbox;
            this.refresh = refresh;
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
         * @return the chatbox interface.
         */
        public int getChatbox() {
            return chatbox;
        }

        /**
         * Gets the lines that will be used to refresh the skill.
         * 
         * @return the lines that will be used to refresh the skill.
         */
        public int[] getRefresh() {
            return refresh;
        }

        /**
         * Gets a skill constant by its id.
         * 
         * @param id
         *            the id of the skill constant we are trying to get.
         * @return the skill constant with this id.
         */
        public static SkillConstant getSkill(int id) {
            return values()[id];
        }
    }

    /**
     * Calculate the total level.
     * 
     * @param player
     *            the player to calculate the total level of.
     * @return the total level.
     */
    public static int totalLevel(Player player) {

        /** Calculate the new total level. */
        int totalLevel = 0;

        for (SkillConstant s : SkillConstant.values()) {
            totalLevel += player.getSkills()[s.ordinal()]
                    .getLevelForExperience();
        }

        return totalLevel;
    }

    /**
     * Adds the specified amount of experience to a certain skill for a player.
     * 
     * @param player
     *            the player being granted the experience.
     * @param amount
     *            the amount of experience being given.
     * @param skill
     *            the skill this experience is being given to.
     */
    public static void addExperience(Player player, int amount, int skill) {
        if (amount + player.getSkills()[skill].getExperience() < 0
                || player.getSkills()[skill].getExperience() > 2000000000) {
            return;
        }

        int oldLevel = player.getSkills()[skill].getLevelForExperience();
        int experience = player.getSkills()[skill].getExperience();
        amount *= EXP_RATE_MULTIPLIER;

        player.getSkills()[skill].setExperience(experience + amount);

        if (!(oldLevel >= 99)) {
            int newLevel = player.getSkills()[skill]
                    .calculateLevelForExperience();

            if (oldLevel < newLevel) {
                if (skill != 3) {
                    player.getSkills()[skill].setLevel(newLevel);
                } else {
                    int old = player.getSkills()[skill].getLevel();

                    player.getSkills()[skill].setLevel(old + 1);
                }
                levelUp(player, SkillConstant.getSkill(skill));
                player.gfx(new Gfx(199));
                player.getFlags().flag(Flag.APPEARANCE);
            }

            player.getPacketBuilder().sendSkill(skill,
                    player.getSkills()[skill].getLevel(),
                    player.getSkills()[skill].getExperience());
        }
        SkillManager.refresh(player, skill);
    }

    /**
     * Adds the specified amount of experience to a certain skill for a player
     * without taking the multiplier into effect.
     * 
     * @param player
     *            the player being granted the experience.
     * @param amount
     *            the amount of experience being given.
     * @param skill
     *            the skill this experience is being given to.
     */
    public static void addExperienceNoMultiplier(Player player, int amount,
            int skill) {
        if (amount + player.getSkills()[skill].getExperience() < 0
                || player.getSkills()[skill].getExperience() > 2000000000) {
            return;
        }

        int oldLevel = player.getSkills()[skill].getLevelForExperience();
        int experience = player.getSkills()[skill].getExperience();

        player.getSkills()[skill].setExperience(experience + amount);

        if (!(oldLevel >= 99)) {
            int newLevel = player.getSkills()[skill]
                    .calculateLevelForExperience();

            if (oldLevel < newLevel) {
                if (skill != 3) {
                    player.getSkills()[skill].setLevel(newLevel);
                } else {
                    int old = player.getSkills()[skill].getLevel();

                    player.getSkills()[skill].setLevel(old + 1);
                }
                levelUp(player, SkillConstant.getSkill(skill));
                player.gfx(new Gfx(199));
                player.getFlags().flag(Flag.APPEARANCE);
            }

            player.getPacketBuilder().sendSkill(skill,
                    player.getSkills()[skill].getLevel(),
                    player.getSkills()[skill].getExperience());
        }
        SkillManager.refresh(player, skill);
    }

    /**
     * Sent when a the player reaches a new skill level.
     * 
     * @param player
     *            the player leveling up.
     * @param skill
     *            the skill advancing a level.
     */
    private static void levelUp(Player player, SkillConstant skill) {

        /** Calculate the new total level. */
        int totalLevel = 0;

        for (SkillConstant s : SkillConstant.values()) {
            totalLevel += player.getSkills()[s.ordinal()]
                    .getLevelForExperience();
        }

        /** Send the player an indication that they have leveled up. */
        player.getPacketBuilder().sendString("Total Lvl: " + totalLevel, 3984);
        player.getPacketBuilder().sendString(
                "@dre@Congratulations, you've just advanced "
                        + Misc.appendIndefiniteArticle(skill.name()
                                .toLowerCase().replaceAll("_", " "))
                        + " level!", skill.getFirstLine());
        player.getPacketBuilder()
                .sendString(
                        "Your "
                                + skill.name().toLowerCase()
                                        .replaceAll("_", " ")
                                + " level is now "
                                + player.getSkills()[skill.ordinal()].getLevelForExperience()
                                + ".", skill.getSecondLine());
        player.getPacketBuilder().sendMessage(
                "Congratulations, you've just advanced "
                        + Misc.appendIndefiniteArticle(skill.name()
                                .toLowerCase().replaceAll("_", " "))
                        + " level!");
        player.getPacketBuilder().sendChatInterface(skill.getChatbox());
    }

    /**
     * Refreshes a players skill.
     * 
     * @param player
     *            the player refreshing the skill.
     * @param skill
     *            the skill being refreshed.
     */
    public static void refresh(Player player, int skill) {

        if (player.getSkills()[skill] == null) {
            player.getSkills()[skill] = new Skill();

            if (skill == 3) {
                player.getSkills()[skill].setLevel(10);
                player.getSkills()[skill].setExperience(1300);
            }
        }

        int l = player.getSkills()[skill].getLevelForExperience();
        SkillConstant constant = SkillConstant.values()[skill];

        player.getPacketBuilder().sendString(
                "" + player.getSkills()[skill].getLevel() + "",
                constant.getRefresh()[0]);
        player.getPacketBuilder().sendString("" + l + "",
                constant.getRefresh()[1]);
        player.getPacketBuilder().sendString(
                "" + player.getSkills()[skill].getExperience() + "",
                constant.getRefresh()[2]);
        player.getPacketBuilder()
                .sendString(
                        ""
                                + player.getSkills()[skill].getExperienceForNextLevel()
                                + "", constant.getRefresh()[3]);

        if (constant == SkillConstant.PRAYER) {
            player.getPacketBuilder().sendString(
                    "Prayer: " + player.getSkills()[Misc.PRAYER].getLevel()
                            + "/" + l + "", 687);
        }
    }

    /**
     * Refreshes all the skills.
     * 
     * @param player
     *            the player to refresh all skills for.
     */
    public static void refreshAll(Player player) {
        /** New local variable. */
        int totalLevel = 0;

        /** Refresh total level and stats. */
        for (SkillConstant s : SkillConstant.values()) {
            refresh(player, s.ordinal());
            totalLevel += player.getSkills()[s.ordinal()]
                    .getLevelForExperience();
        }

        /** Send new total level. */
        player.getPacketBuilder().sendString("Total Lvl: " + totalLevel, 3984);
    }

    /**
     * Refreshes skills on login.
     * 
     * @param player
     *            the player to refresh.
     */
    public static void login(Player player) {
        for (int i = 0; i < player.getSkills().length; i++) {
            player.getSkills()[i] = new Skill();

            if (i == 3) {
                player.getSkills()[i].setLevel(10);
                player.getSkills()[i].setRealLevel(10);
                player.getSkills()[i].setExperience(1300);
            }
        }
    }
}
