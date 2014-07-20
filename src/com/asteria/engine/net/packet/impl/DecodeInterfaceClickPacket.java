package com.asteria.engine.net.packet.impl;

import com.asteria.engine.net.ProtocolBuffer;
import com.asteria.engine.net.packet.PacketDecoder;
import com.asteria.engine.net.packet.PacketOpcodeHeader;
import com.asteria.world.entity.player.Player;

/**
 * Sent when the player clicks certain options on an interface.
 * 
 * @author lare96
 */
@PacketOpcodeHeader({ 130 })
public class DecodeInterfaceClickPacket extends PacketDecoder {

    @Override
    public void decode(Player player, ProtocolBuffer buf) {
        if (player.getTradeSession().inTrade()) {
            player.getTradeSession().reset(true);
        }

        player.getPacketBuilder().sendCloseWindows();
    }
}
