package com.asteria.engine.net.packet;

import java.io.File;

import com.asteria.engine.net.ProtocolBuffer;
import com.asteria.world.entity.player.Player;

/**
 * Reads and handles incoming packets sent from the client.
 * 
 * @author lare96
 */
public abstract class PacketDecoder {

    /**
     * An array of packet decoders that are able to read packets with the same
     * opcode value as the slot they are in.
     */
    private static PacketDecoder[] packets = new PacketDecoder[256];

    /**
     * Read and handle the packet for the specified player.
     * 
     * @param player
     *            the player to handle the packet for.
     * @param buf
     *            the buffer for reading the packet data.
     */
    public abstract void decode(Player player, ProtocolBuffer buf);

    /**
     * Loads new decoders that will be able to read packets under a certain
     * opcode.
     * 
     * @throws Exception
     *             if an error occurs while loading the decoders.
     */
    public static void loadDecoders() throws Exception {

        // List all the files in the specified directory and loop through them.
        File[] files = new File("./src/com/asteria/engine/net/packet/impl/")
                .listFiles();

        for (File file : files) {
            Class<?> c = Class.forName("com.asteria.engine.net.packet.impl."
                    + file.getName().replaceAll(".java", ""));

            // Check if this class is a decoder.
            if (!(c.getSuperclass() == PacketDecoder.class)) {
                throw new IllegalStateException(
                        "Illegal packet decoder! Not an instance of PacketDecoder: "
                                + file.getName());
            }

            // Create the decoder instance.
            PacketDecoder packet = (PacketDecoder) c.newInstance();

            // Throw an exception if no header is found for the decoder.
            if (packet.getClass().getAnnotation(PacketOpcodeHeader.class) == null) {
                throw new PacketHeaderException(packet);
            }

            // Get all of the data from the header.
            int packetOpcodes[] = packet.getClass()
                    .getAnnotation(PacketOpcodeHeader.class).value();

            // Add the decoder for all of its opcodes.
            for (int opcode : packetOpcodes) {
                packets[opcode] = packet;
            }
        }
    }

    /**
     * Gets the array of packet decoders.
     * 
     * @return the array of decoders.
     */
    public static PacketDecoder[] getPackets() {
        return packets;
    }
}
