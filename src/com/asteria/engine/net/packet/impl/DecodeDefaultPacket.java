package com.asteria.engine.net.packet.impl;

import com.asteria.engine.net.ProtocolBuffer;
import com.asteria.engine.net.packet.PacketDecoder;
import com.asteria.engine.net.packet.PacketOpcodeHeader;
import com.asteria.world.entity.player.Player;

/**
 * An empty decoder executed when unused packets are sent.
 * 
 * @author lare96
 */
@PacketOpcodeHeader({ 0 })
public class DecodeDefaultPacket extends PacketDecoder {

    @Override
    public void decode(Player player, ProtocolBuffer buf) {

    }
}
