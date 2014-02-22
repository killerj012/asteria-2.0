package server.world.item.ground;

import server.core.worker.WorkRate;
import server.core.worker.Worker;

/**
 * A {@link Worker} that fires processing events for its designated item.
 * 
 * @author lare96
 */
public class WorldItemWorker extends Worker {

    /** The item this task has been assigned to process. */
    private WorldItem item;

    /**
     * Create a new {@link WorldItemWorker}.
     * 
     * @param item
     *        the item this task has been assigned to process.
     */
    public WorldItemWorker(WorldItem item) {
        super(1, false, WorkRate.EXACT_MINUTE);
        this.item = item;
    }

    @Override
    public void fire() {
        item.fireOnProcess();
    }
}
