package com.asteria.engine.net.packet.impl;

import com.asteria.engine.net.ProtocolBuffer;
import com.asteria.engine.net.packet.PacketDecoder;
import com.asteria.engine.net.packet.PacketOpcodeHeader;
import com.asteria.world.entity.player.Player;

/**
 * Sent when the player clicks on the "Click this to continue" link to forward a
 * dialogue.
 * 
 * @author lare96
 */
@PacketOpcodeHeader({ 40 })
public class DecodeForwardDialoguePacket extends PacketDecoder {

    @Override
    public void decode(Player player, ProtocolBuffer buf) {
        player.advanceDialogue();
    }
}
