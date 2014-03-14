package server.world.entity;

import server.core.worker.TaskFactory;
import server.core.worker.listener.EventListener;

/**
 * Uses an {@link EventListener} to run actions when an entity finishes their
 * walking queue.
 * 
 * @author lare96
 */
public class MovementQueueListener {

    /** The entity that this listener is for. */
    private Entity entity;

    /**
     * The listener that will run the action once the walking queue has
     * finished.
     */
    private EventListener listener;

    /**
     * Create a new {@link MovementQueueListener}.
     * 
     * @param entity
     *        the entity that this listener is for.
     */
    public MovementQueueListener(Entity entity) {
        this.entity = entity;
    }

    /**
     * Creates a new {@link EventListener} that will execute the
     * {@link Runnable} action once the walking queue is finished. If a new
     * action is submitted while the current one hasn't finished, the existing
     * {@link EventListener} is stopped and a new one is created to carry out
     * the new action.
     * 
     * @param action
     *        the action to run once the walking queue is finished.
     */
    public void submit(final Runnable action) {

        /** Stop any existing actions. */
        if (listener != null) {
            if (listener.isRunning()) {
                listener.cancel();
            }
        }

        /** And begin listening for a new action. */
        listener = new EventListener() {

            @Override
            public boolean listenForEvent() {
                return entity.getMovementQueue().isMovementDone() || entity.isUnregistered() ? false : true;

            }

            @Override
            public void run() {

                /** Attempt to run the action. */
                try {
                    if (entity.isUnregistered()) {
                        return;
                    }

                    action.run();

                    /** Handle any errors we may come across. */
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        /** Schedule the listener. */
        TaskFactory.getFactory().submit(listener);
    }
}
