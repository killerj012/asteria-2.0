package server.core.net.event.impl;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import server.core.Rs2Engine;
import server.core.net.Session;
import server.core.net.event.AsynchronousEvent;

/**
 * An implementation of an {@link AsynchronousEvent} that writes data to the
 * {@link SocketChannel}.
 * 
 * @author lare96
 * @author blakeman8192
 */
public final class AsynchronousBufferEvent implements AsynchronousEvent {

    @Override
    public void dispatch(SelectionKey key) {
        Session session = Rs2Engine.getReactor().getSessionMap().get(key);

        try {
            /** Prepare the buffer. */
            session.getOutData().flip();

            /** Write the data. */
            session.getSocketChannel().write(session.getOutData());

            /** Check if all the data was sent. */
            if (!session.getOutData().hasRemaining()) {

                /** And clear the buffer. */
                session.getOutData().clear();
            } else {
                /** Not all data was sent - compact it! */
                session.getOutData().compact();
            }
        } catch (IOException e) {
            e.printStackTrace();
            session.setPacketDisconnect(true);
            session.disconnect();
        }
    }
}
