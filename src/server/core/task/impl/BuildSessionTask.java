package server.core.task.impl;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicInteger;

import server.core.net.EventSelector;
import server.core.net.HostGateway;
import server.core.net.Session;
import server.core.task.SequentialTask;

/**
 * An asynchronous task that will accept incoming connections.
 * 
 * @author lare96
 */
public class BuildSessionTask extends SequentialTask {

    /** Used to keep track of how many connections we've accepted. */
    private AtomicInteger eventCount = new AtomicInteger();

    /** The maximum amount of connections that will be accepted in this task. */
    private static final int MAXIMUM_ACCEPT_EVENT = 5;

    @Override
    public void run() {
        SocketChannel socket;

        try {

            /** Accept the connection. */
            while ((socket = EventSelector.getServer().accept()) != null || eventCount.get() <= MAXIMUM_ACCEPT_EVENT) {

                /** Check if the connection is valid. */
                if (socket == null) {
                    eventCount.incrementAndGet();
                    continue;
                }

                /** Block if we fail the security check. */
                if (!HostGateway.enter(socket.socket().getInetAddress().getHostAddress())) {
                    socket.close();
                    eventCount.incrementAndGet();
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
}