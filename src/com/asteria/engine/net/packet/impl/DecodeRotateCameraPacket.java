package com.asteria.engine.net.packet.impl;

import com.asteria.engine.net.ProtocolBuffer;
import com.asteria.engine.net.packet.PacketDecoder;
import com.asteria.engine.net.packet.PacketOpcodeHeader;
import com.asteria.world.entity.player.Player;

/**
 * Sent when the player uses the arrow keys to rotate the camera.
 * 
 * @author lare96
 */
@PacketOpcodeHeader({ 86 })
public class DecodeRotateCameraPacket extends PacketDecoder {

    @Override
    public void decode(Player player, ProtocolBuffer buf) {

    }
}
