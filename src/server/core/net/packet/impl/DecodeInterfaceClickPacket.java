package server.core.net.packet.impl;

import server.core.net.packet.PacketDecoder;
import server.core.net.packet.PacketOpcodeHeader;
import server.core.net.packet.PacketBuffer.ReadBuffer;
import server.world.entity.player.Player;

/**
 * Sent when the player clicks certain options on an interface.
 * 
 * @author lare96
 */
@PacketOpcodeHeader({ 130 })
public class DecodeInterfaceClickPacket extends PacketDecoder {

    @Override
    public void decode(Player player, ReadBuffer in) {
        if (player.getTradeSession().inTrade()) {
            player.getTradeSession().resetTrade(true);
        }

        player.getPacketBuilder().closeWindows();
    }
}
