package com.asteria.engine.net.packet.impl;

import com.asteria.engine.net.ProtocolBuffer;
import com.asteria.engine.net.packet.PacketDecoder;
import com.asteria.engine.net.packet.PacketOpcodeHeader;
import com.asteria.util.Utility;
import com.asteria.world.entity.UpdateFlags.Flag;
import com.asteria.world.entity.player.Player;

/**
 * Sent when the player selects their appearance.
 * 
 * @author lare96
 */
@PacketOpcodeHeader({ 101 })
public class DecodeCharacterSelectionPacket extends PacketDecoder {

    // TODO: Proper validation for this.

    @Override
    public void decode(Player player, ProtocolBuffer buf) {
        int[] charMod = new int[13];

        for (int i = 0; i < charMod.length; i++) {
            charMod[i] = buf.readByte();

            if (charMod[i] < 1) {
                charMod[i] = 0;
            }
        }

        player.setGender(charMod[0]);
        player.getAppearance()[Utility.APPEARANCE_SLOT_HEAD] = charMod[1];
        player.getAppearance()[Utility.APPEARANCE_SLOT_BEARD] = charMod[2];
        player.getAppearance()[Utility.APPEARANCE_SLOT_CHEST] = charMod[3];
        player.getAppearance()[Utility.APPEARANCE_SLOT_ARMS] = charMod[4];
        player.getAppearance()[Utility.APPEARANCE_SLOT_HANDS] = charMod[5];
        player.getAppearance()[Utility.APPEARANCE_SLOT_LEGS] = charMod[6];
        player.getAppearance()[Utility.APPEARANCE_SLOT_FEET] = charMod[7];
        player.getColors()[0] = charMod[8];
        player.getColors()[1] = charMod[9];
        player.getColors()[2] = charMod[10];
        player.getColors()[3] = charMod[11];
        player.getColors()[4] = charMod[12];
        player.getFlags().flag(Flag.APPEARANCE);
        player.getPacketBuilder().sendCloseWindows();
    }
}
