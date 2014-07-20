package com.asteria.world.entity.npc.dialogue;

import com.asteria.world.entity.npc.NpcDefinition;
import com.asteria.world.entity.player.Player;

/**
 * A set of {@link DialogueActions}s that make up a conversation between a
 * player and npc.
 * 
 * @author lare96
 */
public class Dialogue {

    /** The player this dialogue is being displayed for. */
    private Player player;

    /** The {@link DialogueAction}s that make up this dialogue. */
    private DialogueAction[] dialogues;

    /**
     * Create a new {@link Dialogue}.
     * 
     * @param player
     *            the player this dialogue is being displayed for.
     * @param dialogues
     *            the dialogue actions that make up this dialogue.
     */
    public Dialogue(Player player, DialogueAction... dialogues) {
        this.player = player;
        this.dialogues = dialogues;
    }

    /**
     * Gets the player this dialogue is being displayed for.
     * 
     * @return the player this dialogue is being displayed for.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Gets the {@link DialogueAction}s that make up this dialogue.
     * 
     * @return the dialogue actions that make up this dialogue.
     */
    public DialogueAction[] getDialogues() {
        return dialogues;
    }

    /**
     * Displays various lines of npc dialogue.
     * 
     * @param player
     *            the player this dialogue is being displayed for.
     * @param expression
     *            the expression this mob will make.
     * @param mob
     *            the mob that is speaking.
     * @param text
     *            the text that will be displayed.
     */
    public static void sendNpcDialogue(Player player, Expression expression,
            int mob, String... text) {
        switch (text.length) {
        case 1:
            player.getPacketBuilder().interfaceAnimation(4883,
                    expression.getExpressionId());
            player.getPacketBuilder().sendString(
                    NpcDefinition.getDefinitions()[mob].getName(), 4884);
            player.getPacketBuilder().sendString(text[0], 4885);
            player.getPacketBuilder().sendMobHeadModel(mob, 4883);
            player.getPacketBuilder().sendChatInterface(4882);
            break;
        case 2:
            player.getPacketBuilder().interfaceAnimation(4888,
                    expression.getExpressionId());
            player.getPacketBuilder().sendString(
                    NpcDefinition.getDefinitions()[mob].getName(), 4889);
            player.getPacketBuilder().sendString(text[0], 4890);
            player.getPacketBuilder().sendString(text[1], 4891);
            player.getPacketBuilder().sendMobHeadModel(mob, 4888);
            player.getPacketBuilder().sendChatInterface(4887);
            break;
        case 3:
            player.getPacketBuilder().interfaceAnimation(4894,
                    expression.getExpressionId());
            player.getPacketBuilder().sendString(
                    NpcDefinition.getDefinitions()[mob].getName(), 4895);
            player.getPacketBuilder().sendString(text[0], 4896);
            player.getPacketBuilder().sendString(text[1], 4897);
            player.getPacketBuilder().sendString(text[2], 4898);
            player.getPacketBuilder().sendMobHeadModel(mob, 4894);
            player.getPacketBuilder().sendChatInterface(4893);
            break;
        case 4:
            player.getPacketBuilder().interfaceAnimation(4901,
                    expression.getExpressionId());
            player.getPacketBuilder().sendString(
                    NpcDefinition.getDefinitions()[mob].getName(), 4902);
            player.getPacketBuilder().sendString(text[0], 4903);
            player.getPacketBuilder().sendString(text[1], 4904);
            player.getPacketBuilder().sendString(text[2], 4905);
            player.getPacketBuilder().sendString(text[3], 4906);
            player.getPacketBuilder().sendMobHeadModel(mob, 4901);
            player.getPacketBuilder().sendChatInterface(4900);
            break;
        default:
            throw new IllegalArgumentException("Illegal npc dialogue length: "
                    + text.length);
        }
    }

    /**
     * Displays various lines of player dialogue.
     * 
     * @param player
     *            the player this dialogue is being displayed for.
     * @param expression
     *            the expression this player will make.
     * @param text
     *            the text that will be displayed.
     */
    public static void sendPlayerDialogue(Player player, Expression expression,
            String... text) {
        switch (text.length) {
        case 1:
            player.getPacketBuilder().interfaceAnimation(969,
                    expression.getExpressionId());
            player.getPacketBuilder().sendString(player.getUsername(), 970);
            player.getPacketBuilder().sendString(text[0], 971);
            player.getPacketBuilder().sendPlayerHeadModel(969);
            player.getPacketBuilder().sendChatInterface(968);
            break;
        case 2:
            player.getPacketBuilder().interfaceAnimation(974,
                    expression.getExpressionId());
            player.getPacketBuilder().sendString(player.getUsername(), 975);
            player.getPacketBuilder().sendString(text[0], 976);
            player.getPacketBuilder().sendString(text[1], 977);
            player.getPacketBuilder().sendPlayerHeadModel(974);
            player.getPacketBuilder().sendChatInterface(973);
            break;
        case 3:
            player.getPacketBuilder().interfaceAnimation(980,
                    expression.getExpressionId());
            player.getPacketBuilder().sendString(player.getUsername(), 981);
            player.getPacketBuilder().sendString(text[0], 982);
            player.getPacketBuilder().sendString(text[1], 983);
            player.getPacketBuilder().sendString(text[2], 984);
            player.getPacketBuilder().sendPlayerHeadModel(980);
            player.getPacketBuilder().sendChatInterface(979);
            break;
        case 4:
            player.getPacketBuilder().interfaceAnimation(987,
                    expression.getExpressionId());
            player.getPacketBuilder().sendString(player.getUsername(), 988);
            player.getPacketBuilder().sendString(text[0], 989);
            player.getPacketBuilder().sendString(text[1], 990);
            player.getPacketBuilder().sendString(text[2], 991);
            player.getPacketBuilder().sendString(text[3], 992);
            player.getPacketBuilder().sendPlayerHeadModel(987);
            player.getPacketBuilder().sendChatInterface(986);
            break;
        default:
            throw new IllegalArgumentException(
                    "Illegal player dialogue length: " + text.length);
        }
    }

    /**
     * Displays various lines of option dialogue.
     * 
     * @param player
     *            the player these options are being displayed for.
     * @param text
     *            the options that will be displayed.
     */
    public static void sendOptionDialogue(Player player, String... text) {
        switch (text.length) {
        case 2:
            player.getPacketBuilder().sendString(text[0], 14445);
            player.getPacketBuilder().sendString(text[1], 14446);
            player.getPacketBuilder().sendChatInterface(14443);
            break;
        case 3:
            player.getPacketBuilder().sendString(text[0], 2471);
            player.getPacketBuilder().sendString(text[1], 2472);
            player.getPacketBuilder().sendString(text[2], 2473);
            player.getPacketBuilder().sendChatInterface(2469);
            break;
        case 4:
            player.getPacketBuilder().sendString(text[0], 8209);
            player.getPacketBuilder().sendString(text[1], 8210);
            player.getPacketBuilder().sendString(text[2], 8211);
            player.getPacketBuilder().sendString(text[3], 8212);
            player.getPacketBuilder().sendChatInterface(8207);
            break;
        case 5:
            player.getPacketBuilder().sendString(text[0], 8221);
            player.getPacketBuilder().sendString(text[1], 8222);
            player.getPacketBuilder().sendString(text[2], 8223);
            player.getPacketBuilder().sendString(text[3], 8224);
            player.getPacketBuilder().sendString(text[4], 8225);
            player.getPacketBuilder().sendChatInterface(8219);
            break;
        default:
            throw new IllegalArgumentException(
                    "Illegal dialogue option length: " + text.length);
        }
    }

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
         *            the id of the expression to create.
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
}
