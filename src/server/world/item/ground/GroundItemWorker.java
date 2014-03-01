package server.world.item.ground;

import server.core.worker.WorkRate;
import server.core.worker.Worker;

/**
 * A {@link Worker} implementation that fires processing events for its
 * designated {@link GroundItem} at 1-minute intervals. This takes away the need
 * for iterating through a database of items every tick.
 * 
 * @author lare96
 */
public class GroundItemWorker extends Worker {

    /** The {@link GroundItem} this worker has been assigned to fire events for. */
    private GroundItem item;

    /**
     * Create a new {@link GroundItemWorker}.
     * 
     * @param item
     *        the item this worker has been assigned to fire events for.
     */
    public GroundItemWorker(GroundItem item) {
        super(1, false, WorkRate.EXACT_MINUTE);
        this.item = item;
    }

    @Override
    public void fire() {

        /** Fire the processing event for this item. */
        item.fireOnProcess();
    }
}
