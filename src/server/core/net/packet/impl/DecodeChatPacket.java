package server.core.net.packet.impl;

import server.core.net.packet.PacketBuffer;
import server.core.net.packet.PacketDecoder;
import server.core.net.packet.PacketOpcodeHeader;
import server.world.entity.UpdateFlags.Flag;
import server.world.entity.player.Player;

/**
 * Sent when the player speaks.
 * 
 * @author lare96
 */
@PacketOpcodeHeader( { 4 })
public class DecodeChatPacket extends PacketDecoder {

    @Override
    public void decode(Player player, PacketBuffer.ReadBuffer in) {
        int effects = in.readByte(false, PacketBuffer.ValueType.S);
        int color = in.readByte(false, PacketBuffer.ValueType.S);
        int chatLength = (player.getSession().getPacketLength() - 2);
        byte[] text = in.readBytesReverse(chatLength, PacketBuffer.ValueType.A);

        player.setChatEffects(effects);
        player.setChatColor(color);
        player.setChatText(text);
        player.getFlags().flag(Flag.CHAT);
    }
}
