package server.core.net.packet.impl;

import server.core.net.packet.PacketDecoder;
import server.core.net.packet.PacketOpcodeHeader;
import server.core.net.packet.PacketBuffer.ReadBuffer;
import server.world.entity.player.Player;

/**
 * An empty decoder executed when unused packets are sent.
 * 
 * @author lare96
 */
@PacketOpcodeHeader( { 0 })
public class DecodeDefaultPacket extends PacketDecoder {

    @Override
    public void decode(Player player, ReadBuffer in) {

    }
}
