package server.world.entity.npc;

import java.util.HashMap;
import java.util.Map;

import server.world.entity.player.Player;

/**
 * A click-based conversation in the chatbox between a player and npc.
 * 
 * @author lare96
 */
public abstract class NpcDialogue {

    /** A map of the all of the dialogues. */
    private static Map<Integer, NpcDialogue> dialogueMap = new HashMap<Integer, NpcDialogue>();

    /**
     * All the possible expressions that can be used by an entity during a
     * conversation.
     * 
     * @author lare96
     */
    public enum Expression {
        HAPPY(588),
        CALM(589),
        CALM_CONTINUED(590),
        DEFAULT(591),
        EVIL(592),
        EVIL_CONTINUED(593),
        DELIGHTED_EVIL(594),
        ANNOYED(595),
        DISTRESSED(596),
        DISTRESSED_CONTINUED(597),
        DISORIENTED_LEFT(600),
        DISORIENTED_RIGHT(601),
        UNINTERESTED(602),
        SLEEPY(603),
        PLAIN_EVIL(604),
        LAUGHING(605),
        LAUGHING_2(608),
        LONGER_LAUGHING(606),
        LONGER_LAUGHING_2(607),
        EVIL_LAUGH_SHORT(609),
        SLIGHTLY_SAD(610),
        SAD(599),
        VERY_SAD(611),
        OTHER(612),
        NEAR_TEARS(598),
        NEAR_TEARS_2(613),
        ANGRY_1(614),
        ANGRY_2(615),
        ANGRY_3(616),
        ANGRY_4(617);

        /** The id of the expression. */
        private int expressionId;

        /**
         * Create a new {@link Expression}.
         * 
         * @param expressionId
         *        the id of the expression to create.
         */
        private Expression(int expressionId) {
            this.expressionId = expressionId;
        }

        /**
         * Gets the id of the expression.
         * 
         * @return the id of the expression.
         */
        public int getExpressionId() {
            return expressionId;
        }
    }

    /**
     * The entire conversation between a player and npc.
     * 
     * @param player
     *        the player taking part in this conversation.
     */
    public abstract void dialogue(Player player);

    /**
     * The dialogue id that will be mapped to the instance of itself.
     * 
     * @return the id of this dialogue.
     */
    public abstract int dialogueId();

    /**
     * Displays one line of npc dialogue to the player.
     * 
     * @param player
     *        the player this dialogue is being displayed for.
     * @param expression
     *        the expression this mob will make.
     * @param text
     *        the text that will be displayed.
     * @param mob
     *        the mob speaking.
     */
    public static void oneLineMobDialogue(Player player, Expression expression, String text, int mob) {
        player.getPacketBuilder().interfaceAnimation(4883, expression.getExpressionId());
        player.getPacketBuilder().sendString(NpcDefinition.getNpcDefinition()[mob].getName(), 4884);
        player.getPacketBuilder().sendString(text, 4885);
        player.getPacketBuilder().sendMobHeadModel(mob, 4883);
        player.getPacketBuilder().sendChatInterface(4882);
    }

    /**
     * Displays two lines of npc dialogue to the player.
     * 
     * @param player
     *        the player this dialogue is being displayed for.
     * @param expression
     *        the expression this mob will make.
     * @param text
     *        the text that will be displayed.
     * @param text2
     *        the text that will be displayed.
     * @param mob
     *        the mob speaking.
     */
    public static void twoLineMobDialogue(Player player, Expression expression, String text, String text2, int mob) {
        player.getPacketBuilder().interfaceAnimation(4888, expression.getExpressionId());
        player.getPacketBuilder().sendString(NpcDefinition.getNpcDefinition()[mob].getName(), 4889);
        player.getPacketBuilder().sendString(text, 4890);
        player.getPacketBuilder().sendString(text2, 4891);
        player.getPacketBuilder().sendMobHeadModel(mob, 4901);
        player.getPacketBuilder().sendChatInterface(4887);
    }

    /**
     * Displays three lines of npc dialogue to the player.
     * 
     * @param player
     *        the player this dialogue is being displayed for.
     * @param expression
     *        the expression this mob will make.
     * @param text
     *        the text that will be displayed.
     * @param text2
     *        the text that will be displayed.
     * @param text3
     *        the text that will be displayed.
     * @param mob
     *        the mob speaking.
     */
    public static void threeLineMobDialogue(Player player, Expression expression, String text, String text2, String text3, int mob) {
        player.getPacketBuilder().interfaceAnimation(4894, expression.getExpressionId());
        player.getPacketBuilder().sendString(NpcDefinition.getNpcDefinition()[mob].getName(), 4895);
        player.getPacketBuilder().sendString(text, 4896);
        player.getPacketBuilder().sendString(text2, 4897);
        player.getPacketBuilder().sendString(text3, 4898);
        player.getPacketBuilder().sendMobHeadModel(mob, 4894);
        player.getPacketBuilder().sendChatInterface(4893);
    }

    /**
     * Displays four lines of npc dialogue to the player.
     * 
     * @param player
     *        the player this dialogue is being displayed for.
     * @param expression
     *        the expression this mob will make.
     * @param text
     *        the text that will be displayed.
     * @param text2
     *        the text that will be displayed.
     * @param text3
     *        the text that will be displayed.
     * @param text4
     *        the text that will be displayed.
     * @param mob
     *        the mob speaking.
     */
    public static void fourLineMobDialogue(Player player, Expression expression, String text1, String text2, String text3, String text4, int mob) {
        player.getPacketBuilder().interfaceAnimation(4901, expression.getExpressionId());
        player.getPacketBuilder().sendString(NpcDefinition.getNpcDefinition()[mob].getName(), 4902);
        player.getPacketBuilder().sendString(text1, 4903);
        player.getPacketBuilder().sendString(text2, 4904);
        player.getPacketBuilder().sendString(text3, 4905);
        player.getPacketBuilder().sendString(text4, 4906);
        player.getPacketBuilder().sendString("Click here to continue", 4907);
        player.getPacketBuilder().sendMobHeadModel(mob, 4901);
        player.getPacketBuilder().sendChatInterface(4900);
    }

    /**
     * Displays one line of player dialogue to the player.
     * 
     * @param player
     *        the player this dialogue is being displayed for.
     * @param expression
     *        the expression this player will make.
     * @param text
     *        the text that will be displayed.
     */
    public static void oneLinePlayerDialogue(Player player, Expression expression, String text) {
        player.getPacketBuilder().interfaceAnimation(969, expression.getExpressionId());
        player.getPacketBuilder().sendString(player.getUsername(), 970);
        player.getPacketBuilder().sendString(text, 971);
        player.getPacketBuilder().sendString("Click here to continue", 972);
        player.getPacketBuilder().sendPlayerHeadModel(969);
        player.getPacketBuilder().sendChatInterface(968);
    }

    /**
     * Displays two lines of player dialogue to the player.
     * 
     * @param player
     *        the player this dialogue is being displayed for.
     * @param expression
     *        the expression this player will make.
     * @param text
     *        the text that will be displayed.
     * @param text2
     *        the text that will be displayed.
     */
    public static void twoLinePlayerDialogue(Player player, Expression expression, String text1, String text2) {
        player.getPacketBuilder().interfaceAnimation(974, expression.getExpressionId());
        player.getPacketBuilder().sendString(player.getUsername(), 975);
        player.getPacketBuilder().sendString(text1, 976);
        player.getPacketBuilder().sendString(text2, 977);
        player.getPacketBuilder().sendString("Click here to continue", 978);
        player.getPacketBuilder().sendPlayerHeadModel(974);
        player.getPacketBuilder().sendChatInterface(973);
    }

    /**
     * Displays three lines of player dialogue to the player.
     * 
     * @param player
     *        the player this dialogue is being displayed for.
     * @param expression
     *        the expression this player will make.
     * @param text
     *        the text that will be displayed.
     * @param text2
     *        the text that will be displayed.
     * @param text3
     *        the text that will be displayed.
     */
    public static void threeLinePlayerDialogue(Player player, Expression expression, String text1, String text2, String text3) {
        player.getPacketBuilder().interfaceAnimation(980, expression.getExpressionId());
        player.getPacketBuilder().sendString(player.getUsername(), 981);
        player.getPacketBuilder().sendString(text1, 982);
        player.getPacketBuilder().sendString(text2, 983);
        player.getPacketBuilder().sendString(text3, 984);
        player.getPacketBuilder().sendString("Click here to continue", 985);
        player.getPacketBuilder().sendPlayerHeadModel(980);
        player.getPacketBuilder().sendChatInterface(979);
    }

    /**
     * Displays four lines of player dialogue to the player.
     * 
     * @param player
     *        the player this dialogue is being displayed for.
     * @param expression
     *        the expression this player will make.
     * @param text
     *        the text that will be displayed.
     * @param text2
     *        the text that will be displayed.
     * @param text3
     *        the text that will be displayed.
     * @param text4
     *        the text that will be displayed.
     */
    public static void fourLinePlayerDialogue(Player player, Expression expression, String text1, String text2, String text3, String text4) {
        player.getPacketBuilder().interfaceAnimation(987, expression.getExpressionId());
        player.getPacketBuilder().sendString(player.getUsername(), 988);
        player.getPacketBuilder().sendString(text1, 989);
        player.getPacketBuilder().sendString(text2, 990);
        player.getPacketBuilder().sendString(text3, 991);
        player.getPacketBuilder().sendString(text4, 992);
        player.getPacketBuilder().sendString("Click here to continue", 993);
        player.getPacketBuilder().sendPlayerHeadModel(987);
        player.getPacketBuilder().sendChatInterface(986);
    }

    /**
     * Displays two options to the player.
     * 
     * @param player
     *        the player to display the options for.
     * @param option
     *        the first option to display.
     * @param option2
     *        the second option to display.
     */
    public static void twoOptions(Player player, String option, String option2) {
        player.getPacketBuilder().sendString("Select an Option", 14444);
        player.getPacketBuilder().sendString(option, 14445);
        player.getPacketBuilder().sendString(option2, 14446);
        player.getPacketBuilder().sendChatInterface(14443);
    }

    /**
     * Displays three options to the player.
     * 
     * @param player
     *        the player to display the options for.
     * @param option
     *        the first option to display.
     * @param option2
     *        the second option to display.
     * @param option3
     *        the third option to display.
     */
    public static void threeOptions(Player player, String option, String option2, String option3) {
        player.getPacketBuilder().sendString("Select an Option", 2470);
        player.getPacketBuilder().sendString(option, 2471);
        player.getPacketBuilder().sendString(option2, 2472);
        player.getPacketBuilder().sendString(option3, 2473);
        player.getPacketBuilder().sendChatInterface(2469);
    }

    /**
     * Displays four options to the player.
     * 
     * @param player
     *        the player to display the options for.
     * @param option
     *        the first option to display.
     * @param option2
     *        the second option to display.
     * @param option3
     *        the third option to display.
     * @param option4
     *        the fourth option to display.
     */
    public static void fourOptions(Player player, String option, String option2, String option3, String option4) {
        player.getPacketBuilder().sendString("Select an Option", 8208);
        player.getPacketBuilder().sendString(option, 8209);
        player.getPacketBuilder().sendString(option2, 8210);
        player.getPacketBuilder().sendString(option3, 8211);
        player.getPacketBuilder().sendString(option4, 8212);
        player.getPacketBuilder().sendChatInterface(8207);
    }

    /**
     * Displays five options to the player.
     * 
     * @param player
     *        the player to display the options for.
     * @param option
     *        the first option to display.
     * @param option2
     *        the second option to display.
     * @param option3
     *        the third option to display.
     * @param option4
     *        the fourth option to display.
     * @param option5
     *        the five option to display.
     */
    public static void fiveOptions(Player player, String option, String option2, String option3, String option4, String option5) {
        player.getPacketBuilder().sendString("Select an Option", 8220);
        player.getPacketBuilder().sendString(option, 8221);
        player.getPacketBuilder().sendString(option2, 8222);
        player.getPacketBuilder().sendString(option3, 8223);
        player.getPacketBuilder().sendString(option4, 8224);
        player.getPacketBuilder().sendString(option5, 8225);
        player.getPacketBuilder().sendChatInterface(8219);
    }

    /**
     * Advances this conversation by one stage.
     * 
     * @param player
     *        the player to forward the conversation for.
     */
    public void next(Player player) {
        int nextStage = (player.getConversationStage() + 1);

        player.setConversationStage(nextStage);
    }

    /**
     * Stops and resets this conversation for the player.
     * 
     * @param player
     *        the player to stop and reset the conversation for.
     */
    public void stop(Player player) {
        player.setConversationStage(0);
        player.setNpcDialogue(0);
    }

    /**
     * Gets the map of the dialogues.
     * 
     * @return the map of dialogues.
     */
    public static Map<Integer, NpcDialogue> getDialogueMap() {
        return dialogueMap;
    }
}
