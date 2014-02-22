package server.world.entity.player;

import java.io.File;

/**
 * Used for reading and writing events to player files.
 * 
 * @author lare96
 */
public abstract class PlayerFileEvent {

    /**
     * The player taking part in this event.
     */
    private Player player;

    /**
     * Create a new {@link PlayerFileEvent}.
     * 
     * @param player
     *        the player taking part in this event.
     */
    public PlayerFileEvent(Player player) {
        this.player = player;
    }

    /**
     * The event that will be performed on the file.
     */
    public abstract void run();

    /**
     * Gets the file that the event will be performed on.
     * 
     * @return the file that the event will be performed on.
     */
    public abstract File file();

    /**
     * Gets player taking part in this event.
     * 
     * @return the player.
     */
    public Player getPlayer() {
        return player;
    }
}
