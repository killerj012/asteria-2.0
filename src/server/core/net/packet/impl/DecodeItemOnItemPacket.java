package server.core.net.packet.impl;

import server.core.net.packet.PacketBuffer;
import server.core.net.packet.PacketDecoder;
import server.core.net.packet.PacketOpcodeHeader;
import server.core.net.packet.PacketBuffer.ReadBuffer;
import server.world.entity.player.Player;
import server.world.item.Item;

/**
 * Sent when the player uses an item on another item.
 * 
 * @author lare96
 */
@PacketOpcodeHeader({ 53 })
public class DecodeItemOnItemPacket extends PacketDecoder {

    @Override
    public void decode(Player player, ReadBuffer in) {
        int itemSecondClickSlot = in.readShort();
        int itemFirstClickSlot = in.readShort(PacketBuffer.ValueType.A);
        in.readShort();
        in.readShort();

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
