package server.core.worker;

/**
 * An abstract container for any game logic that can be fired on the main game
 * thread.
 * 
 * @author lare96
 */
public interface Logic {

    /**
     * The logic that will be fired.
     */
    public void fire();
}
