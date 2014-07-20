package com.asteria.engine.net.packet.impl;

import com.asteria.engine.net.ProtocolBuffer;
import com.asteria.engine.net.packet.PacketDecoder;
import com.asteria.engine.net.packet.PacketOpcodeHeader;
import com.asteria.world.entity.player.Player;

/**
 * Sent when the player updates the chat options.
 * 
 * @author lare96
 */
@PacketOpcodeHeader({ 95 })
public class DecodeChatOptionsPacket extends PacketDecoder {

    @Override
    public void decode(Player player, ProtocolBuffer buf) {

    }
}
