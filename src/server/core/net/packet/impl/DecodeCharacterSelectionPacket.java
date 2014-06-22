package server.core.net.packet.impl;

import server.core.net.packet.PacketBuffer.ReadBuffer;
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
@PacketOpcodeHeader({ 101 })
public class DecodeCharacterSelectionPacket extends PacketDecoder {

    @Override
    public void decode(Player player, ReadBuffer in) {
        int[] charMod = new int[13];

        for (int i = 0; i < charMod.length; i++) {
            charMod[i] = in.readByte();

            if (charMod[i] < 1) {
                charMod[i] = 0;
            }
        }

        player.setGender(charMod[0]);
        player.getAppearance()[Misc.APPEARANCE_SLOT_HEAD] = charMod[1];
        player.getAppearance()[Misc.APPEARANCE_SLOT_BEARD] = charMod[2];
        player.getAppearance()[Misc.APPEARANCE_SLOT_CHEST] = charMod[3];
        player.getAppearance()[Misc.APPEARANCE_SLOT_ARMS] = charMod[4];
        player.getAppearance()[Misc.APPEARANCE_SLOT_HANDS] = charMod[5];
        player.getAppearance()[Misc.APPEARANCE_SLOT_LEGS] = charMod[6];
        player.getAppearance()[Misc.APPEARANCE_SLOT_FEET] = charMod[7];
        player.getColors()[0] = charMod[8];
        player.getColors()[1] = charMod[9];
        player.getColors()[2] = charMod[10];
        player.getColors()[3] = charMod[11];
        player.getColors()[4] = charMod[12];
        player.getFlags().flag(Flag.APPEARANCE);
        player.getPacketBuilder().closeWindows();
    }
}
