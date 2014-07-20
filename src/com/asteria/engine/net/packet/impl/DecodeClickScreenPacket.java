package com.asteria.engine.net.packet.impl;

import com.asteria.engine.net.ProtocolBuffer;
import com.asteria.engine.net.packet.PacketDecoder;
import com.asteria.engine.net.packet.PacketOpcodeHeader;
import com.asteria.world.entity.player.Player;

/**
 * Sent when the player clicks anywhere on the game screen.
 * 
 * @author lare96
 */
@PacketOpcodeHeader({ 241 })
public class DecodeClickScreenPacket extends PacketDecoder {

    @Override
    public void decode(Player player, ProtocolBuffer buf) {

    }
}
