package server.core.net;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

import server.core.Service;

/**
 * A service carried out by the <code>networkPool</code> that will accept
 * incoming connections.
 * 
 * @author lare96
 */
public class SessionService implements Service {

    /** Used to keep track of how many connections we've accepted. */
    private AtomicInteger eventCount = new AtomicInteger();

    /** The maximum amount of connections that will be accepted in this event. */
    private static final int MAXIMUM_ACCEPT_EVENT = 5;

    @Override
    public void run() {
        SocketChannel socket;

        try {

            /** Accept the connection. */
            while ((socket = EventSelector.getServerSocketChannel().accept()) != null || eventCount.get() <= MAXIMUM_ACCEPT_EVENT) {

                /** Check if the connection is valid. */
                if (socket == null) {
                    eventCount.incrementAndGet();
                    continue;
                }

                /** Block if we fail the security check. */
                if (!HostGateway.enter(socket.socket().getInetAddress().getHostAddress())) {
                    socket.close();
                    continue;
                }

                /** Otherwise create a new session. */
                socket.configureBlocking(false);
                SelectionKey newKey = socket.register(EventSelector.getSelector(), SelectionKey.OP_READ);
                newKey.attach(new Session(newKey));
                eventCount.incrementAndGet();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String name() {
        return SessionService.class.getSimpleName();
    }
}