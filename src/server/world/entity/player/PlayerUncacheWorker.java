package server.world.entity.player;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.logging.Logger;

import server.core.worker.WorkRate;
import server.core.worker.Worker;
import server.world.World;

/**
 * A worker that uncaches players from the <code>cachedPlayers</code> map as
 * needed.
 * 
 * @author lare96
 */
public class PlayerUncacheWorker extends Worker {

    /** The logger for printing debugging info. */
    private static Logger logger = Logger.getLogger(PlayerUncacheWorker.class.getSimpleName());

    /** How long the player will be cached for in minutes. */
    private static final int CACHE_TIME_MINUTES = 10;

    /**
     * Create a new {@link PlayerUncacheWorker}.
     */
    public PlayerUncacheWorker() {
        super(2, false, WorkRate.EXACT_MINUTE);
    }

    @Override
    public void fire() {
        for (Iterator<Entry<String, Player>> iterator = World.getCachedPlayers().entrySet().iterator(); iterator.hasNext();) {
            Player cachedPlayer = iterator.next().getValue();

            if (cachedPlayer.getCacheTicks() == CACHE_TIME_MINUTES) {
                iterator.remove();
                logger.info(cachedPlayer + " removed from the cache! [CACHE SIZE: " + World.getCachedPlayers().size() + "]");
                continue;
            }

            cachedPlayer.incrementCacheTicks();
        }
    }
}
