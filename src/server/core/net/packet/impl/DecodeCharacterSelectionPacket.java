package server.core.net.packet.impl;

import server.core.net.buffer.PacketBuffer.ReadBuffer;
import server.core.net.packet.PacketDecoder;
import server.core.net.packet.PacketOpcodeHeader;
import server.util.Misc;
import server.world.entity.UpdateFlags.Flag;
import server.world.entity.player.Player;

/**
 * Sent when the player selects their appearance.
 * 
 * @author lare96
 */
@PacketOpcodeHeader( { 101 })
public class DecodeCharacterSelectionPacket extends PacketDecoder {

    @Override
    public void decode(Player player, ReadBuffer in) {
        player.setGender(in.readByte());
        player.getAppearance()[Misc.APPEARANCE_SLOT_HEAD] = in.readByte();
        player.getAppearance()[Misc.APPEARANCE_SLOT_BEARD] = in.readByte();
        player.getAppearance()[Misc.APPEARANCE_SLOT_CHEST] = in.readByte();
        player.getAppearance()[Misc.APPEARANCE_SLOT_ARMS] = in.readByte();
        player.getAppearance()[Misc.APPEARANCE_SLOT_HANDS] = in.readByte();
        player.getAppearance()[Misc.APPEARANCE_SLOT_LEGS] = in.readByte();
        player.getAppearance()[Misc.APPEARANCE_SLOT_FEET] = in.readByte();
        player.getColors()[0] = in.readByte();
        player.getColors()[1] = in.readByte();
        player.getColors()[2] = in.readByte();
        player.getColors()[3] = in.readByte();

        int color = in.readByte();

        if (color < 1) {
            color = 0;
        }
        player.getColors()[4] = color;
        player.getFlags().flag(Flag.APPEARANCE);
        player.getPacketBuilder().closeWindows();
    }
}
