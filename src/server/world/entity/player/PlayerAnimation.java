package server.world.entity.player;

/**
 * All of the standard animations for player updating.
 * 
 * @author lare96
 */
public class PlayerAnimation {

    /**
     * The standing animation.
     */
    private static int standEmote = 0x328;

    /**
     * The turning animation.
     */
    private static int standTurnEmote = 0x337;

    /**
     * The walking animation.
     */
    private static int walkEmote = 0x333;

    /**
     * The turning 180 degrees animation.
     */
    private static int turn180Emote = 0x334;

    /**
     * The turning 90 degrees clockwise animation.
     */
    private static int turn90CWEmote = 0x335;

    /**
     * The turning 90 degrees counter-clockwise animation.
     */
    private static int turn90CCWEmote = 0x336;

    /**
     * The running animation.
     */
    private static int runEmote = 0x338;

    /**
     * Gets the standing animation.
     * 
     * @return the stand emote.
     */
    public static int getStandEmote() {
        return standEmote;
    }

    /**
     * Gets the turning animation.
     * 
     * @return the stand turn emote.
     */
    public static int getStandTurnEmote() {
        return standTurnEmote;
    }

    /**
     * Gets the walking animation.
     * 
     * @return the walk emote.
     */
    public static int getWalkEmote() {
        return walkEmote;
    }

    /**
     * Gets the 180 degree turn animation.
     * 
     * @return the turn 180 degrees emote.
     */
    public static int getTurn180Emote() {
        return turn180Emote;
    }

    /**
     * Gets the 90 degree turn clockwise animation.
     * 
     * @return the turn 90 degrees clockwise Emote.
     */
    public static int getTurn90CWEmote() {
        return turn90CWEmote;
    }

    /**
     * Gets the 90 degree turn counter-clockwise animation.
     * 
     * @return the turn 90 degrees counter-clockwise emote.
     */
    public static int getTurn90CCWEmote() {
        return turn90CCWEmote;
    }

    /**
     * Gets the running animation.
     * 
     * @return the run emote.
     */
    public static int getRunEmote() {
        return runEmote;
    }
}
