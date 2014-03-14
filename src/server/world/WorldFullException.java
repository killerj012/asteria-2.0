package server.world;

import server.world.entity.Entity;

/**
 * An exception thrown when the {@link World} is full of a certain entity.
 * 
 * @author lare96
 */
public class WorldFullException extends RuntimeException {

    /**
     * Create a new {@link WorldFullException}.
     * 
     * @param entity
     *        the entity trying to be added to the world.
     */
    public WorldFullException(Entity entity) {
        super(entity + " Cannot be added because the world is full!");
    }

    /** The generated serial version UID. */
    private static final long serialVersionUID = 1697799349317687228L;
}
