package com.asteria.engine.net.packet.impl;

import com.asteria.engine.net.ProtocolBuffer;
import com.asteria.engine.net.packet.PacketDecoder;
import com.asteria.engine.net.packet.PacketOpcodeHeader;
import com.asteria.world.entity.player.Player;
import com.asteria.world.item.Item;

/**
 * Sent when the player uses an item on another item.
 * 
 * @author lare96
 */
@PacketOpcodeHeader({ 53 })
public class DecodeItemOnItemPacket extends PacketDecoder {

    // TODO: Proper validation.

    @Override
    public void decode(Player player, ProtocolBuffer buf) {
        int itemSecondClickSlot = buf.readShort();
        int itemFirstClickSlot = buf.readShort(ProtocolBuffer.ValueType.A);
        buf.readShort();
        buf.readShort();

        if (itemSecondClickSlot < 0 || itemFirstClickSlot < 0) {
            return;
        }

        Item itemUsed = player.getInventory().getContainer()
                .getItem(itemFirstClickSlot);
        Item itemOn = player.getInventory().getContainer()
                .getItem(itemSecondClickSlot);

        if (itemUsed == null || itemOn == null) {
            return;
        }

        switch (itemOn.getId()) {

        }

        switch (itemUsed.getId()) {

        }
    }
}
