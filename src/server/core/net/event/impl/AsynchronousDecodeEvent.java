package server.core.net.event.impl;

import java.nio.channels.SelectionKey;

import server.Main;
import server.core.Rs2Engine;
import server.core.net.Session;
import server.core.net.Session.Stage;
import server.core.net.buffer.PacketBuffer;
import server.core.net.event.AsynchronousEvent;
import server.core.net.packet.PacketDecoder;
import server.util.Misc;

/**
 * An implementation of an {@link AsynchronousEvent} that decodes and handles
 * incoming packets.
 * 
 * @author lare96
 * @author blakeman8192
 */
public final class AsynchronousDecodeEvent implements AsynchronousEvent {

    @Override
    public void dispatch(SelectionKey key) {

        /** Get the session from the session map. */
        Session session = Rs2Engine.getReactor().getSessionMap().get(key);

        try {

            /** Read the incoming data. */
            if (session.getSocketChannel().read(session.getInData()) == -1) {
                session.disconnect();
                return;
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
                    session.setPacketOpcode(session.getPacketOpcode() - session.getDecryptor().getKey() & 0xff);
                }

                /** Decode the packet length. */
                if (session.getPacketLength() == -1) {
                    session.setPacketLength(Misc.packetLengths[session.getPacketOpcode()]);

                    if (session.getPacketLength() == -1) {
                        if (!session.getInData().hasRemaining()) {
                            session.getInData().flip();
                            session.getInData().compact();
                            break;
                        }

                        session.setPacketLength(session.getInData().get() & 0xff);
                    }
                }

                /** Decode the packet payload. */
                if (session.getInData().remaining() >= session.getPacketLength()) {

                    /** Reset the timeout counter. */
                    session.getTimeoutStopwatch().reset();

                    /** Gets the buffer's position before this packet is read. */
                    int positionBefore = session.getInData().position();

                    /**
                     * Creates a new buffer for reading packets backed by the
                     * set data.
                     */
                    PacketBuffer.ReadBuffer in = PacketBuffer.newInBuffer(session.getInData());

                    /**
                     * Decode and handle the packet with the previously created
                     * buffer.
                     */
                    try {
                        if (PacketDecoder.getPackets()[session.getPacketOpcode()] != null) {
                            PacketDecoder.getPackets()[session.getPacketOpcode()].decode(session.getPlayer(), in);
                        } else {
                            Main.getLogger().info(session.getPlayer() + " unhandled packet " + session.getPacketOpcode());
                        }

                        /** Take care of any errors that may have occurred. */
                    } catch (Exception ex) {
                        ex.printStackTrace();

                        /**
                         * Make sure we have finished reading all of this
                         * packet.
                         */
                    } finally {
                        int read = session.getInData().position() - positionBefore;

                        for (int i = read; i < session.getPacketLength(); i++) {
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
            session.setPacketDisconnect(true);
            session.disconnect();
        }
    }
}
