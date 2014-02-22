package server.core.net.event;

import java.nio.channels.SelectionKey;

import server.core.net.AsynchronousReactor;

/**
 * A networking event that is asynchronously dispatched to awaiting keys by the
 * {@link AsynchronousReactor}.
 * 
 * @author lare96
 */
public interface AsynchronousEvent {

    /**
     * The logic that will be fired once the event has been dispatched.
     * 
     * @param key
     *        the key to dispatch this event to.
     */
    public void dispatch(SelectionKey key);
}
