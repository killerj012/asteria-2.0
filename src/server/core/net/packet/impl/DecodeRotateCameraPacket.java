package server.core.net.packet.impl;

import server.core.net.buffer.PacketBuffer.ReadBuffer;
import server.core.net.packet.PacketDecoder;
import server.core.net.packet.PacketOpcodeHeader;
import server.world.entity.player.Player;

/**
 * Sent when the player uses the arrow keys to rotate the camera.
 * 
 * @author lare96
 */
@PacketOpcodeHeader( { 86 })
public class DecodeRotateCameraPacket extends PacketDecoder {

    @Override
    public void decode(Player player, ReadBuffer in) {

    }
}
