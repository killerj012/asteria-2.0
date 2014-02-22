package server.core.net.packet.impl;

import server.core.net.buffer.PacketBuffer.ReadBuffer;
import server.core.net.packet.PacketDecoder;
import server.world.entity.player.Player;

/**
 * An empty packet executed when unused packets are sent.
 * 
 * @author lare96
 */
@SuppressWarnings("unused")
public class DecodeDefaultPacket extends PacketDecoder {

    @Override
    public void decode(Player player, ReadBuffer in) {
        /** ... */
    }

    @Override
    public int[] opcode() {
        return new int[] { 0 };
    }
}
