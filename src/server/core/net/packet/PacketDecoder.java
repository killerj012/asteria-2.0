package server.core.net.packet;

import server.core.net.buffer.PacketBuffer;
import server.world.entity.player.Player;

/**
 * Reads data from incoming packets and proceeds to fire logic for a player
 * based on that data.
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
     * Reads data from an incoming packets and fires logic based on that data.
     * 
     * @param player
     *        the player to fire the logic for.
     * @param in
     *        the buffer for reading the data within the packet.
     */
    public abstract void decode(Player player, PacketBuffer.ReadBuffer in);

    /**
     * Add a new decoder that will be able to read packets under a certain
     * opcode.
     * 
     * @param packet
     *        the decoder to add to the array.
     */
    public static void addDecoder(PacketDecoder packet) {
        if (packet.getClass().getAnnotation(PacketOpcodeHeader.class) == null) {
            throw new PacketHeaderException(packet);
        }

        int packetOpcodes[] = packet.getClass().getAnnotation(PacketOpcodeHeader.class).value();

        /** Add the decoder for all of the opcodes. */
        for (int opcode : packetOpcodes) {
            packets[opcode] = packet;
        }
    }

    /**
     * Instantiates the array of decoders effectively clearing any previously
     * added decoders.
     */
    public static void clear() {
        packets = new PacketDecoder[256];
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
