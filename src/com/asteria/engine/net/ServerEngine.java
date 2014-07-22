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

import com.asteria.engine.GameEngine;
import com.asteria.engine.net.Session.Stage;
import com.asteria.engine.net.packet.PacketDecoder;
import com.asteria.util.Utility;
import com.asteria.world.World;

/**
 * A reactor that runs on the main game thread. The reactor's job is to select
 * and handle various network events for all clients.
 * 
 * @author lare96
 * @author blakeman8192
 */
public final class ServerEngine {

    /** A logger for printing information. */
    private static final Logger logger = Logger.getLogger(ServerEngine.class
            .getSimpleName());

    /** The selector that selects keys ready to receive network events. */
    private static Selector selector;

    /** The server socket channel that will accept incoming connections. */
    private static ServerSocketChannel server;

    /** This class cannot be instantiated. */
    private ServerEngine() {}

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
            try {
                selector.close();
                server.close();
                selector = null;
                server = null;
                init();
                selector.selectNow();
            } catch (Exception e) {

                // Unable to restart, so print the exception and shutdown the
                // server.
                logger.log(Level.SEVERE,
                        "Unable to restart the reactor, shutting down!", e);
                World.shutdown();
                return;
            }
        }

        // Handle all of the selected events.
        for (final Iterator<SelectionKey> iterator = getSelector()
                .selectedKeys().iterator(); iterator.hasNext();) {
            SelectionKey key = iterator.next();

            if (!key.isValid()) {

                // Remove the key if its invalid.
                iterator.remove();

            } else if (key.isAcceptable()) {

                // Accept the key asynchronously if needed.
                try {
                    GameEngine.getSequentialPool().execute(new Runnable() {

                        /** A thread safe integer for holding the times looped. */
                        private final AtomicInteger loopsMade = new AtomicInteger();

                        @Override
                        public void run() {
                            SocketChannel socket;

                            try {

                                // Accept the connection.
                                while ((socket = ServerEngine.getServer()
                                        .accept()) != null || loopsMade.get() <= 5) {

                                    // Increment the loop count.
                                    loopsMade.incrementAndGet();

                                    // Check if the connection is valid.
                                    if (socket == null) {
                                        continue;
                                    }

                                    // Block if we fail the security check.
                                    if (!HostGateway.enter(socket.socket()
                                            .getInetAddress().getHostAddress())) {
                                        socket.close();
                                        continue;
                                    }

                                    // Otherwise create a new session.
                                    socket.configureBlocking(false);
                                    SelectionKey newKey = socket.register(
                                            ServerEngine.getSelector(),
                                            SelectionKey.OP_READ);
                                    newKey.attach(new Session(newKey));
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } finally {
                    iterator.remove();
                }

            } else if (key.isReadable()) {

                // Decode packets for the key, and handle login if needed.
                Session session = (Session) key.attachment();

                if (session == null) {
                    continue;
                }

                try {
                    if (session.getSocketChannel().read(session.getInData()) == -1) {
                        session.disconnect();
                        continue;
                    }

                    session.getInData().flip();

                    while (session.getInData().hasRemaining()) {

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

                                session.setPacketLength(session.getInData()
                                        .get() & 0xff);
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
                                            .getPacketOpcode()].decode(session
                                            .getPlayer(), new ProtocolBuffer(
                                            session.getInData()));
                                    session.getTimeout().reset();
                                } else {
                                    // logger.info(session.getPlayer()
                                    // + " unhandled packet "
                                    // + session.getPacketOpcode());
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            } finally {
                                int read = session.getInData().position() - positionBefore;

                                for (int i = read; i < session
                                        .getPacketLength(); i++) {
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
                } finally {
                    iterator.remove();
                }

            } else if (key.isWritable()) {

                // Send any previously queued data if needed.
                Session session = (Session) key.attachment();

                try {
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
                } finally {
                    iterator.remove();
                }
            }
        }
    }

    /**
     * Gets the selector instance.
     * 
     * @return the selector.
     */
    public static Selector getSelector() {
        return ServerEngine.selector;
    }

    /**
     * Gets the server socket channel instance.
     * 
     * @return the server socket channel.
     */
    public static ServerSocketChannel getServer() {
        return ServerEngine.server;
    }
}
