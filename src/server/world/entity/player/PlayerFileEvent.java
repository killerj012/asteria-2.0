package server.world.entity.player;

/**
 * Used for reading and writing events to player files.
 * 
 * @author lare96
 */
public abstract class PlayerFileEvent {

    /** The player taking part in this event. */
    private Player player;

    /**
     * Create a new {@link PlayerFileEvent}.
     * 
     * @param player
     *            the player taking part in this event.
     */
    public PlayerFileEvent(Player player) {
        this.player = player;
    }

    /**
     * The event that will be performed on the file.
     */
    public abstract void run();

    /**
     * Gets player taking part in this event.
     * 
     * @return the player.
     */
    public Player getPlayer() {
        return player;
    }
}
