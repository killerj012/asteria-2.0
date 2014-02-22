package server.core.net.packet.impl;

import server.core.net.buffer.PacketBuffer.ReadBuffer;
import server.core.net.packet.PacketDecoder;
import server.world.entity.player.Player;

/**
 * Sent when the player clicks anywhere on the game screen.
 * 
 * @author lare96
 */
public class DecodeClickScreenPacket extends PacketDecoder {

    @Override
    public void decode(Player player, ReadBuffer in) {

    }

    @Override
    public int[] opcode() {
        return new int[] { 241 };
    }
}
