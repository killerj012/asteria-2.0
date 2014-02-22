package server.world;

import server.world.entity.player.Player;

/**
 * A skeleton for managing registerable things that are a part of the world.
 * 
 * @author lare96
 * @param <T>
 *        the registerable element we are trying to manage.
 */
public interface RegisterableWorldContainer<T extends RegisterableWorld> {

    /**
     * Register something in the world.
     * 
     * @param registerable
     *        whatever you are registering.
     */
    public void register(T registerable);

    /**
     * Unregister something in the world.
     * 
     * @param registerable
     *        whatever you are unregistering.
     */
    public void unregister(T registerable);

    /**
     * Update whatever you currently have registered when a new region loads for
     * a player.
     * 
     * @param player
     *        the player who's region is being updated.
     */
    public void loadNewRegion(Player player);
}
