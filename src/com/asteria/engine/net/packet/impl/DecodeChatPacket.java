package com.asteria.engine.net.packet.impl;

import com.asteria.engine.net.ProtocolBuffer;
import com.asteria.engine.net.packet.PacketDecoder;
import com.asteria.engine.net.packet.PacketOpcodeHeader;
import com.asteria.world.entity.UpdateFlags.Flag;
import com.asteria.world.entity.player.Player;

/**
 * Sent when the player speaks.
 * 
 * @author lare96
 */
@PacketOpcodeHeader({ 4 })
public class DecodeChatPacket extends PacketDecoder {

    @Override
    public void decode(Player player, ProtocolBuffer buf) {
        int effects = buf.readByte(false, ProtocolBuffer.ValueType.S);
        int color = buf.readByte(false, ProtocolBuffer.ValueType.S);
        int chatLength = (player.getSession().getPacketLength() - 2);
        byte[] text = buf.readBytesReverse(chatLength,
                ProtocolBuffer.ValueType.A);

        if (effects < 0 || color < 0 || chatLength < 0 || text == null) {
            return;
        }

        player.setChatEffects(effects);
        player.setChatColor(color);
        player.setChatText(text);
        player.getFlags().flag(Flag.CHAT);
    }
}
