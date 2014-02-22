package server.core.net;

import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import server.Main;
import server.core.Rs2Engine;
import server.core.net.event.QueuedAsynchronousEvent;
import server.core.net.event.impl.AsynchronousAcceptEvent;
import server.core.net.event.impl.AsynchronousBufferEvent;
import server.core.net.event.impl.AsynchronousDecodeEvent;

/**
 * A {@link Runnable} later passed onto a {@link Thread} that fires vital
 * networking logic needed for the server to function correctly.
 * 
 * @author lare96
 * @author blakeman8192
 */
public final class AsynchronousReactor implements Runnable {

    /** A thread-safe map of keys and the corresponding sessions. */
    private static ConcurrentHashMap<SelectionKey, Session> sessionMap;

    /**
     * A thread-safe queue of network events waiting to be fired on the game
     * thread.
     */
    private static ConcurrentLinkedQueue<QueuedAsynchronousEvent> eventQueue;

    /**
     * A selector on this networking thread that selects keys ready to receive
     * network events.
     */
    private static Selector selector;

    /** The address the server will listen for incoming connections on. */
    private static InetSocketAddress address;

    /** A {@link ServerSocketChannel} that will accept incoming connections. */
    private static ServerSocketChannel serverSocketChannel;

    /**
     * Configures the {@link AsynchronousReactor}.
     * 
     * @throws Exception
     *         if any errors occurs during configuration.
     */
    public void configure() throws Exception {

        /** Configure the session map. */
        sessionMap = new ConcurrentHashMap<SelectionKey, Session>();

        /** Configure the event queue. */
        eventQueue = new ConcurrentLinkedQueue<QueuedAsynchronousEvent>();

        /** Configure the socket address. */
        address = new InetSocketAddress("127.0.0.1", Main.PORT);

        /** Initialize the networking objects. */
        selector = Selector.open();
        serverSocketChannel = ServerSocketChannel.open();

        /** ... and configure them! */
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.socket().bind(address);
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    /**
     * Safely terminates the {@link AsynchronousReactor}.
     * 
     * @throws Exception
     *         if any errors occur during termination.
     */
    public void terminate() throws Exception {
        sessionMap.clear();
        eventQueue.clear();
        selector.close();
        serverSocketChannel.close();
    }

    /**
     * Fires all queued events from this reactor in the order that they were
     * recieved.
     */
    public void pollQueuedEvents() {
        QueuedAsynchronousEvent event;

        /** Poll all of the queued events. */
        while ((event = eventQueue.poll()) != null) {

            /** Check if the key is still valid before dispatching. */
            if (!event.getKey().isValid()) {
                continue;
            }

            /** If so dispatch the event! */
            try {
                event.getEvent().dispatch(event.getKey());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {

        /** Set the name of this thread. */
        Thread.currentThread().setName(AsynchronousReactor.class.getName());

        /**
         * Set the priority - this thread isn't high priority because all it
         * does is send out events.
         */
        Thread.currentThread().setPriority(Thread.NORM_PRIORITY);

        /**
         * This thread will stay alive as long as the selector and server socket
         * channel are open.
         */
        while (selector.isOpen() && serverSocketChannel.isOpen()) {
            try {
                /** Selects the keys ready for network events. */
                selector.selectNow();

                /** Iterates over the selected keys. */
                for (Iterator<SelectionKey> iterator = getSelector().selectedKeys().iterator(); iterator.hasNext();) {

                    /** Retrieves the next key in the set of keys. */
                    SelectionKey key = iterator.next();

                    /** Checks if this key is valid. */
                    if (!key.isValid()) {

                        /** Dispose of the key... we don't need it anymore. */
                        iterator.remove();
                        continue;

                        /** Checks if this key is acceptable. */
                    } else if (key.isAcceptable()) {

                        /**
                         * If so fire a {@link AsynchronousAcceptEvent} right on
                         * the networking thread.
                         */
                        new AsynchronousAcceptEvent().dispatch(key);

                        /** Checks if this key is readable. */
                    } else if (key.isReadable()) {

                        /**
                         * If so queue a {@link AsynchronousDecodeEvent} for
                         * execution on the main thread.
                         */
                        eventQueue.add(new QueuedAsynchronousEvent(new AsynchronousDecodeEvent(), key));

                        /** Checks if this key is writable. */
                    } else if (key.isWritable()) {

                        /**
                         * If so queue a {@link AsynchronousBufferEvent} for
                         * execution on the main thread.
                         */
                        eventQueue.add(new QueuedAsynchronousEvent(new AsynchronousBufferEvent(), key));
                    }

                    /** Dispose of the key... we don't need it anymore. */
                    iterator.remove();
                }

                /** Encode and send out all raw outgoing packets. */
                Rs2Engine.getEncoder().encodePackets();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Gets the selector instance.
     * 
     * @return the selector.
     */
    public Selector getSelector() {
        return AsynchronousReactor.selector;
    }

    /**
     * Gets the host address instance.
     * 
     * @return the address.
     */
    public InetSocketAddress getAddress() {
        return AsynchronousReactor.address;
    }

    /**
     * Gets the server socket channel instance.
     * 
     * @return the server socket channel.
     */
    public ServerSocketChannel getServerSocketChannel() {
        return AsynchronousReactor.serverSocketChannel;
    }

    /**
     * Gets the session map instance.
     * 
     * @return the session map.
     */
    public ConcurrentHashMap<SelectionKey, Session> getSessionMap() {
        return sessionMap;
    }
}
