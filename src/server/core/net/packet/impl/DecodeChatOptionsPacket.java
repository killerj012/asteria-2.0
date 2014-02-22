package server.core.net.packet.impl;

import server.core.net.buffer.PacketBuffer.ReadBuffer;
import server.core.net.packet.PacketDecoder;
import server.world.entity.player.Player;

/**
 * Sent when the player updates the chat options.
 * 
 * @author lare96
 */
public class DecodeChatOptionsPacket extends PacketDecoder {

    @Override
    public void decode(Player player, ReadBuffer in) {

    }

    @Override
    public int[] opcode() {
        return new int[] { 95 };
    }
}
