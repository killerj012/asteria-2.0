package server.core.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.Iterator;
import java.util.logging.Logger;

import server.Main;
import server.core.Rs2Engine;
import server.core.net.Session.Stage;
import server.core.net.packet.PacketBuffer;
import server.core.net.packet.PacketDecoder;
import server.core.task.impl.BuildSessionTask;
import server.util.Misc;

/**
 * A reactor that uses a selector to determine when connected client sessions
 * are ready to connect, as well as send and receive network data.
 * 
 * @author lare96
 * @author blakeman8192
 */
public final class EventSelector {

    /** A logger for printing information. */
    private static Logger logger;

    /** A selector that selects keys ready to receive network events. */
    private static Selector selector;

    /** A server socket channel that will accept incoming connections. */
    private static ServerSocketChannel server;

    /** So this class cannot be instantiated. */
    private EventSelector() {

    }

    /**
     * Initialize the core components of the {@link EventSelector}.
     * 
     * @throws Exception
     *             if any errors occur during the initialization.
     */
    public static void init() throws Exception {

        /** Check if we have already started the selector. */
        if (server != null && selector != null) {
            throw new IllegalStateException(
                    "The event selctor has already been started!");
        }

        /** Create the logger. */
        logger = Logger.getLogger(EventSelector.class.getSimpleName());

        /** Initialize the networking objects. */
        selector = Selector.open();
        server = ServerSocketChannel.open();

        /** ... and configure them! */
        server.configureBlocking(false);
        server.socket().bind(new InetSocketAddress("127.0.0.1", Main.PORT));
        server.register(selector, SelectionKey.OP_ACCEPT);
    }

    /**
     * Determines which clients are ready for networking events and handles
     * those events straight away for them. Accept events are pushed to the
     * engine and read/write events are handled right on the game thread as soon
     * as they are recieved.
     */
    public static void tick() {

        /** Selects the keys ready for network events. */
        try {
            selector.selectNow();
        } catch (IOException io) {
            io.printStackTrace();

            /**
             * Selector is closed or something happened while selecting, attempt
             * to restart!
             */
            try {
                selector.close();
                server.close();
                selector = null;
                server = null;
                logger = null;
                init();
                selector.selectNow();
            } catch (Exception ex) {

                /** Unable to restart! So throw an exception. */
                ex.printStackTrace();
                throw new IllegalStateException(
                        "Unable to restart reactor after shutdown!");
            }
        }

        for (Iterator<SelectionKey> iterator = getSelector().selectedKeys()
                .iterator(); iterator.hasNext();) {
            SelectionKey key = iterator.next();

            /** Remove the key if its invalid. */
            if (!key.isValid()) {
                iterator.remove();

                /** Accept the key concurrently if needed. */
            } else if (key.isAcceptable()) {
                try {
                    Rs2Engine.pushTask(new BuildSessionTask());
                } finally {
                    iterator.remove();
                }
                /** Decode packets for the key if needed. */
            } else if (key.isReadable()) {
                Session session = (Session) key.attachment();

                /** Check if the session is valid. */
                if (session == null) {
                    continue;
                }

                try {

                    /** Read the incoming data. */
                    if (session.getSocketChannel().read(session.getInData()) == -1) {
                        session.disconnect();
                        continue;
                    }

                    /** Handle the received data. */
                    session.getInData().flip();

                    while (session.getInData().hasRemaining()) {

                        /** Handle login if we need to. */
                        if (session.getStage() != Stage.LOGGED_IN) {
                            session.handleLogin();
                            break;
                        }

                        /** Decode the packet opcode. */
                        if (session.getPacketOpcode() == -1) {
                            session.setPacketOpcode(session.getInData().get() & 0xff);
                            session.setPacketOpcode(session.getPacketOpcode()
                                    - session.getDecryptor().getKey() & 0xff);
                        }

                        /** Decode the packet length. */
                        if (session.getPacketLength() == -1) {
                            session.setPacketLength(Misc.packetLengths[session
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

                        /** Decode the packet payload. */
                        if (session.getInData().remaining() >= session
                                .getPacketLength()) {

                            /**
                             * Gets the buffer's position before this packet is
                             * read.
                             */
                            int positionBefore = session.getInData().position();

                            /**
                             * Creates a new buffer for reading packets backed
                             * by the set data.
                             */
                            PacketBuffer.ReadBuffer in = PacketBuffer
                                    .newReadBuffer(session.getInData());

                            /**
                             * Decode and handle the packet with the previously
                             * created buffer.
                             */
                            try {
                                if (PacketDecoder.getPackets()[session
                                        .getPacketOpcode()] != null) {
                                    PacketDecoder.getPackets()[session
                                            .getPacketOpcode()].decode(
                                            session.getPlayer(), in);
                                } else {
                                    logger.info(session.getPlayer()
                                            + " unhandled packet "
                                            + session.getPacketOpcode());
                                }

                                /**
                                 * Take care of any errors that may have
                                 * occurred.
                                 */
                            } catch (Exception ex) {
                                ex.printStackTrace();

                                /**
                                 * Make sure we have finished reading all of
                                 * this packet.
                                 */
                            } finally {
                                int read = session.getInData().position()
                                        - positionBefore;

                                for (int i = read; i < session
                                        .getPacketLength(); i++) {
                                    session.getInData().get();
                                }
                            }

                            /** Reset for the next packet. */
                            session.setPacketOpcode(-1);
                            session.setPacketLength(-1);
                        } else {
                            session.getInData().flip();
                            session.getInData().compact();
                            break;
                        }
                    }

                    /** Clear everything for the next read. */
                    session.getInData().clear();
                } catch (Exception e) {
                    e.printStackTrace();
                    session.disconnect();
                } finally {
                    iterator.remove();
                }

                /** Send any queued data if needed. */
            } else if (key.isWritable()) {
                Session session = (Session) key.attachment();

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
        return EventSelector.selector;
    }

    /**
     * Gets the server socket channel instance.
     * 
     * @return the server socket channel.
     */
    public static ServerSocketChannel getServer() {
        return EventSelector.server;
    }
}
