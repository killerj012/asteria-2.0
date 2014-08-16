package com.asteria.engine.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.asteria.Main;
import com.asteria.engine.GameEngine;
import com.asteria.engine.net.Session.Stage;
import com.asteria.engine.net.packet.PacketDecoder;
import com.asteria.util.Utility;
import com.asteria.world.World;
import com.asteria.world.entity.player.Player;

/**
 * A reactor that runs on the main game thread. The reactor's job is to select
 * and handle various network events for all clients.
 * 
 * @author lare96
 * @author blakeman8192
 */
public final class ServerEngine {

    // TODO: Throttle packet

    /** A logger for printing information. */
    private static Logger logger = Logger.getLogger(ServerEngine.class
        .getSimpleName());

    /** The selector that selects keys ready to receive network events. */
    private static Selector selector;

    /** The server socket channel that will accept incoming connections. */
    private static ServerSocketChannel server;

    /**
     * Starts the core components of the reactor.
     * 
     * @throws Exception
     *             if any errors occur during the initialization.
     */
    public static void init() throws Exception {

        // Check if we have already started the reactor.
        if (server != null && selector != null) {
            throw new IllegalStateException(
                "The reactor has already been started!");
        }

        // Create the networking objects.
        selector = Selector.open();
        server = ServerSocketChannel.open();

        // ... and configure them!
        server.configureBlocking(false);
        server.socket().bind(new InetSocketAddress("127.0.0.1", 43594));
        server.register(selector, SelectionKey.OP_ACCEPT);
    }

    /**
     * Restarts the reactor by discarding the networking objects and then
     * reinitializing them again.
     */
    private static void restart() {
        try {

            // Log everyone off first.
            for (Player player : World.getPlayers()) {
                if (player == null) {
                    continue;
                }
                player.logout();
            }

            // Discard the networking objects.
            selector.close();
            server.close();
            selector = null;
            server = null;

            // And reinitialize them.
            init();

        } catch (Exception e) {

            // Unable to restart, so print the exception and shutdown the
            // server.
            logger.log(Level.SEVERE,
                "Unable to restart the reactor, shutting down!", e);
            World.shutdown();
        }
    }

    /**
     * Submits a task to the {@link GameEngine} that will asynchronously accept
     * all incoming connections for this cycle. If after <tt>5</tt> attempts no
     * connections have been made, the task returns.
     */
    private static void acceptClients() {

        // Accept the key asynchronously if needed.
        GameEngine.getServiceExecutor().execute(new Runnable() {

            /** A thread safe integer for holding the times looped. */
            private final AtomicInteger loopsMade = new AtomicInteger();

            @Override
            public void run() {
                SocketChannel socket;

                try {

                    // Accept the connection.
                    while ((socket = server.accept()) != null || loopsMade
                        .get() <= 5) {

                        // Increment the loop count.
                        loopsMade.incrementAndGet();

                        // Check if the connection is valid.
                        if (socket == null) {
                            continue;
                        }

                        // Block if we fail the security check.
                        if (!HostGateway.enter(socket.socket().getInetAddress()
                            .getHostAddress())) {
                            socket.close();
                            continue;
                        }

                        // Otherwise create a new session.
                        socket.configureBlocking(false);
                        SelectionKey newKey = socket.register(selector,
                            SelectionKey.OP_READ);
                        newKey.attach(new Session(newKey));
                    }
                } catch (IOException e) {
                    logger.log(Level.WARNING,
                        "Error while accepting incoming clients!", e);
                }
            }
        });
    }

    /**
     * Decodes and handles all incoming packets sent from the client for this
     * cycle.
     * 
     * @param key
     *            the selection key that holds the session instance.
     */
    private static void decodePackets(SelectionKey key) {

        // Grab the session attachment from the key.
        Session session = (Session) key.attachment();

        if (session == null) {
            return;
        }

        try {

            // Decode packets for the key, and handle login if needed.
            if (session.getSocketChannel().read(session.getInData()) == -1) {
                session.disconnect();
                return;
            }

            session.getInData().flip();

            while (session.getInData().hasRemaining()) {

                // There's data to be read, reset the timeout.
                session.getTimeout().reset();

                // Handle login here if needed.
                if (session.getStage() != Stage.LOGGED_IN) {
                    session.handleLogin();
                    break;
                }

                // Decode the packet opcode and packet length.
                if (session.getPacketOpcode() == -1) {
                    session.setPacketOpcode(session.getInData().get() & 0xff);
                    session.setPacketOpcode(session.getPacketOpcode() - session
                        .getDecryptor().getKey() & 0xff);
                }

                if (session.getPacketLength() == -1) {
                    session.setPacketLength(Utility.PACKET_LENGTHS[session
                        .getPacketOpcode()]);

                    if (session.getPacketLength() == -1) {
                        if (!session.getInData().hasRemaining()) {
                            session.getInData().flip();
                            session.getInData().compact();
                            break;
                        }

                        session
                            .setPacketLength(session.getInData().get() & 0xff);
                    }
                }

                // Handle the decoded packet and make sure all of the
                // data is read
                if (session.getInData().remaining() >= session
                    .getPacketLength()) {
                    int positionBefore = session.getInData().position();

                    try {
                        if (PacketDecoder.getPackets()[session
                            .getPacketOpcode()] != null) {
                            PacketDecoder.getPackets()[session
                                .getPacketOpcode()].decode(session.getPlayer(),
                                new ProtocolBuffer(session.getInData()));
                        } else {
                            if (Main.DEBUG)
                                logger
                                    .info(session.getPlayer() + " unhandled packet " + session
                                        .getPacketOpcode());
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        int read = session.getInData().position() - positionBefore;

                        for (int i = read; i < session.getPacketLength(); i++) {
                            session.getInData().get();
                        }
                    }
                    session.setPacketOpcode(-1);
                    session.setPacketLength(-1);
                } else {
                    session.getInData().flip();
                    session.getInData().compact();
                    break;
                }
            }

            // Clear the buffer for the next cycle.
            session.getInData().clear();
        } catch (Exception e) {
            e.printStackTrace();
            session.disconnect();
        }
    }

    /**
     * Sends any previously queued data in the session's <code>outData</code>
     * buffer to its socket channel.
     * 
     * @param key
     *            the selection key that holds the session instance.
     */
    private static void sendQueuedData(SelectionKey key) {

        // Grab the session attachment from the key.
        Session session = (Session) key.attachment();

        if (session == null) {
            return;
        }

        try {

            // Send any previously queued data if needed.
            session.getOutData().flip();
            session.getSocketChannel().write(session.getOutData());

            if (!session.getOutData().hasRemaining()) {
                session.getOutData().clear();
            } else {
                session.getOutData().compact();
            }
        } catch (IOException e) {
            e.printStackTrace();
            session.disconnect();
        }
    }

    /**
     * Determines which clients are ready for networking events and handles
     * those events straight away for them. Accept events are pushed to the
     * engine and read/write events are handled right on the game thread as soon
     * as they are received.
     */
    public static void tick() {

        // Selects the keys ready for network events.
        try {
            selector.selectNow();
        } catch (IOException io) {
            logger.log(Level.WARNING,
                "Error during event selection, restarting the reactor!", io);

            // Something happened while selecting, attempt to restart!
            restart();
            return;
        }

        // Handle all of the selected events.
        for (final Iterator<SelectionKey> iterator = selector.selectedKeys()
            .iterator(); iterator.hasNext();) {
            SelectionKey key = iterator.next();

            if (!key.isValid()) {
                iterator.remove();
            } else if (key.isAcceptable()) {
                try {
                    acceptClients();
                } finally {
                    iterator.remove();
                }
            } else if (key.isReadable()) {
                try {
                    decodePackets(key);
                } finally {
                    iterator.remove();
                }
            } else if (key.isWritable()) {
                try {
                    sendQueuedData(key);
                } finally {
                    iterator.remove();
                }
            }
        }
    }

    private ServerEngine() {}
}
