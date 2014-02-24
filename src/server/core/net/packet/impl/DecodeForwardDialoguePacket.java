package server.core.net.packet.impl;

import server.core.net.buffer.PacketBuffer.ReadBuffer;
import server.core.net.packet.PacketDecoder;
import server.world.entity.npc.NpcDialogue;
import server.world.entity.player.Player;

/**
 * Sent when the player clicks on the "Click this to continue" link to forward a
 * dialogue.
 * 
 * @author lare96
 */
public class DecodeForwardDialoguePacket extends PacketDecoder {

    @Override
    public void decode(Player player, ReadBuffer in) {
        if (player.getNpcDialogue() != 0) {
            NpcDialogue.getDialogueMap().get(player.getNpcDialogue()).dialogue(player);
        } else {
            player.getPacketBuilder().closeWindows();
        }
    }

    @Override
    public int[] opcode() {
        return new int[] { 40 };
    }
}
