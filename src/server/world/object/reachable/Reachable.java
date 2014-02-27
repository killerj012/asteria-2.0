package server.world.object.reachable;

import java.util.HashMap;
import java.util.Map;

import server.world.entity.player.Player;
import server.world.map.Position;
import server.world.object.WorldObject;

/**
 * A {@link WorldObject}
 * 
 * @author lare96
 */
public abstract class Reachable {

    private static Map<Position, Reachable> reachableMap = new HashMap<Position, Reachable>();

    /** The starting point of this reachable {@link WorldObject}. */
    private Position startingPoint;

    /** The ending point of this reachable {@link WorldObject}. */
    private Position destinationPoint;

    /**
     * How the player will be transported.
     * 
     * @param player
     *        the player to transport.
     */
    public abstract void transport(Player player);
}
