package server.core.net.packet.impl;

import server.core.net.packet.PacketDecoder;
import server.core.net.packet.PacketOpcodeHeader;
import server.core.net.packet.PacketBuffer.ReadBuffer;
import server.world.entity.player.Player;

/**
 * Sent when the player updates the chat options.
 * 
 * @author lare96
 */
@PacketOpcodeHeader( { 95 })
public class DecodeChatOptionsPacket extends PacketDecoder {

    @Override
    public void decode(Player player, ReadBuffer in) {

    }
}
