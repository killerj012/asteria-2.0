package server.core.net.packet.impl;

import server.core.net.packet.PacketBuffer.ReadBuffer;
import server.core.net.packet.PacketDecoder;
import server.core.net.packet.PacketOpcodeHeader;
import server.world.entity.player.Player;

/**
 * Sent when the player clicks on the "Click this to continue" link to forward a
 * dialogue.
 * 
 * @author lare96
 */
@PacketOpcodeHeader({ 40 })
public class DecodeForwardDialoguePacket extends PacketDecoder {

    @Override
    public void decode(Player player, ReadBuffer in) {
        player.advanceDialogue();
    }
}
