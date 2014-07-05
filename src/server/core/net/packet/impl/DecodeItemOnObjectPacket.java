package server.core.net.packet.impl;

import server.core.net.packet.PacketBuffer.ByteOrder;
import server.core.net.packet.PacketBuffer.ReadBuffer;
import server.core.net.packet.PacketBuffer.ValueType;
import server.core.net.packet.PacketDecoder;
import server.core.net.packet.PacketOpcodeHeader;
import server.util.Misc;
import server.world.entity.player.Player;
import server.world.item.Item;
import server.world.item.container.InventoryContainer;
import server.world.map.Position;

/**
 * Sent when the player uses an item on an object.
 * 
 * @author lare96
 */
@PacketOpcodeHeader({ 192 })
public class DecodeItemOnObjectPacket extends PacketDecoder {

    // TODO: When cache reading is done, check position of objects to
    // see if you're actually near them or not.

    @Override
    public void decode(final Player player, ReadBuffer in) {
        final int container = in.readShort(false);
        final int objectId = in.readShort(true, ByteOrder.LITTLE);
        final int objectY = in.readShort(true, ValueType.A, ByteOrder.LITTLE);
        final int slot = in.readShort(true, ByteOrder.LITTLE);;
        final int objectX = in.readShort(true, ValueType.A, ByteOrder.LITTLE);
        final int itemId = in.readShort(false);
        final int size = 1;
        
        final Item item = player.getInventory().getContainer().getItem(slot);

        if (item == null || container != InventoryContainer.DEFAULT_INVENTORY_CONTAINER_ID) {
            return;
        }


        player.facePosition(new Position(objectX, objectY));
        player.getMovementQueueListener().submit(new Runnable() {
            @Override
            public void run() {
                if (Misc.canClickObject(player.getPosition(), new Position(
                        objectX, objectY), size)) {
                    switch (objectId) {

                    }

                    switch (itemId) {

                    }
                }
            }
        });
    }
}
