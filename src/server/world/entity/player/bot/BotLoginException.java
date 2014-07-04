package server.world.entity.player.bot;

/**
 * An exception thrown when the bot cannot login for any reason.
 * 
 * @author lare96
 */
public class BotLoginException extends RuntimeException {

    /**
     * Create a new {@link BotLoginException}.
     * 
     * @param bot
     *            the bot that threw the exception.
     * @param reason
     *            the reason the exception was thrown.
     */
    public BotLoginException(Bot bot, String reason) {
        super(bot + " failed to login! reason[" + reason + "]");
    }

    /** The generated serial version UID. */
    private static final long serialVersionUID = -7096618267536144938L;
}
