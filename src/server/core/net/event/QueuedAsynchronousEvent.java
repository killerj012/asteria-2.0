package server.core.net.event;

import java.nio.channels.SelectionKey;

/**
 * An {@link AsynchronousEvent} that has been queued by the reactor and is
 * awaiting execution on the main game thread.
 * 
 * @author lare96
 */
public class QueuedAsynchronousEvent {

    /** The event to fire on the game thread. */
    private AsynchronousEvent event;

    /** The key to fire this event for. */
    private SelectionKey key;

    /**
     * Create a new {@link QueuedAsynchronousEvent}.
     * 
     * @param event
     *        the event to fire on the game thread.
     * @param key
     *        the key to fire this event for.
     */
    public QueuedAsynchronousEvent(AsynchronousEvent event, SelectionKey key) {
        this.event = event;
        this.key = key;
    }

    /**
     * Gets the event that will be fired.
     * 
     * @return the event that will be fired.
     */
    public AsynchronousEvent getEvent() {
        return event;
    }

    /**
     * Gets the key that the event will be fired for.
     * 
     * @return the key that the event will be fired for.
     */
    public SelectionKey getKey() {
        return key;
    }
}
