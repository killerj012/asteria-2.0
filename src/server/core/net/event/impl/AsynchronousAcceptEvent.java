package server.core.net.event.impl;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import server.core.Rs2Engine;
import server.core.net.Session;
import server.core.net.event.AsynchronousEvent;
import server.core.net.security.HostGateway;

/**
 * An implementation of an {@link AsynchronousEvent} that creates a session for
 * any newly connected clients.
 * 
 * @author lare96
 */
public final class AsynchronousAcceptEvent implements AsynchronousEvent {

    /**
     * The maximum amount of connections that will be accepted in this current
     * event.
     */
    public static final int MAXIMUM_ACCEPT_EVENT = 5;

    /** If the last connection was a valid one. */
    private boolean lastConnectionValid = true;

    @Override
    public void dispatch(SelectionKey key) {
        SocketChannel socket;

        /**
         * Here I used blakeman8192's original idea which consisted of a for
         * loop so we can accept multiple clients per event for lower latency.
         * But I extended on this idea by making it so that it stops looping
         * when there are no more connections to be accepted. This should speed
         * up login (probably not even by a noticeable amount) because we're
         * doing less looping, while still keeping the security feature of
         * limiting how many clients can be accepted.
         */
        for (int i = 0; i < MAXIMUM_ACCEPT_EVENT && lastConnectionValid; i++) {
            try {
                socket = Rs2Engine.getReactor().getServerSocketChannel().accept();

                /** Checks if this was a valid connection. */
                if (socket == null) {

                    /**
                     * Stop looping if it wasn't because we are done accepting
                     * connections for this event!
                     */
                    lastConnectionValid = false;
                    continue;
                }

                /** Make sure we can allow this session to be created. */
                if (!HostGateway.enter(socket.socket().getInetAddress().getHostAddress())) {
                    socket.close();
                    continue;
                }

                /** Set up the new session. */
                socket.configureBlocking(false);
                SelectionKey newKey = socket.register(key.selector(), SelectionKey.OP_READ);
                Rs2Engine.getReactor().getSessionMap().put(newKey, new Session(newKey));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
