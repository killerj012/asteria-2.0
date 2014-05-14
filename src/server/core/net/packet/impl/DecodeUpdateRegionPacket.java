package server.core.net.packet.impl;

import server.core.net.packet.PacketDecoder;
import server.core.net.packet.PacketOpcodeHeader;
import server.core.net.packet.PacketBuffer.ReadBuffer;
import server.world.World;
import server.world.entity.player.Player;

/**
 * Sent when the player loads a new map region
 * 
 * @author lare96
 */
@PacketOpcodeHeader( { 121 })
public class DecodeUpdateRegionPacket extends PacketDecoder {

    @Override
    public void decode(Player player, ReadBuffer in) {
        World.getObjects().loadNewRegion(player);
        World.getGroundItems().loadNewRegion(player);
        player.displayInterfaces();
        player.getPacketBuilder().sendMessage(player.getPosition().getRegionId() + " - region");
    }
}
