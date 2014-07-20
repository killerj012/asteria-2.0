package com.asteria.engine.net.packet.impl;

import com.asteria.engine.net.ProtocolBuffer;
import com.asteria.engine.net.ProtocolBuffer.ByteOrder;
import com.asteria.engine.net.ProtocolBuffer.ValueType;
import com.asteria.engine.net.packet.PacketDecoder;
import com.asteria.engine.net.packet.PacketOpcodeHeader;
import com.asteria.world.entity.player.Player;
import com.asteria.world.item.Item;
import com.asteria.world.item.container.InventoryContainer;
import com.asteria.world.map.Position;

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
    public void decode(final Player player, ProtocolBuffer buf) {
        final int container = buf.readShort(false);
        final int objectId = buf.readShort(true, ByteOrder.LITTLE);
        final int objectY = buf.readShort(true, ValueType.A, ByteOrder.LITTLE);
        final int slot = buf.readShort(true, ByteOrder.LITTLE);
        final int objectX = buf.readShort(true, ValueType.A, ByteOrder.LITTLE);
        final int itemId = buf.readShort(false);
        final int objectSize = 1;

        if (container < 0 || objectId < 0 || objectY < 0 || slot < 0
                || objectX < 0 || itemId < 0 || objectSize < 0) {
            return;
        }

        final Item item = player.getInventory().getContainer().getItem(slot);

        if (item == null
                || container != InventoryContainer.DEFAULT_INVENTORY_CONTAINER_ID) {
            return;
        }

        player.facePosition(new Position(objectX, objectY));
        player.getMovementQueueListener().append(new Runnable() {
            @Override
            public void run() {
                if (player.getPosition().withinDistance(
                        new Position(objectX, objectY, player.getPosition()
                                .getZ()), objectSize)) {
                    switch (objectId) {

                    }

                    switch (itemId) {

                    }
                }
            }
        });
    }
}
