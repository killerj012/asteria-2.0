package server.core.net.packet.impl;

import server.core.net.buffer.PacketBuffer.ByteOrder;
import server.core.net.buffer.PacketBuffer.ReadBuffer;
import server.core.net.buffer.PacketBuffer.ValueType;
import server.core.net.packet.PacketDecoder;
import server.core.net.packet.PacketOpcodeHeader;
import server.util.Misc;
import server.world.entity.player.Player;
import server.world.map.Position;

/**
 * Sent when the player uses an item on an object.
 * 
 * @author lare96
 */
@PacketOpcodeHeader( { 192 })
public class DecodeItemOnObjectPacket extends PacketDecoder {

    @Override
    @SuppressWarnings("unused")
    public void decode(final Player player, ReadBuffer in) {
        in.readShort(false);
        final int objectId = in.readShort(true, ByteOrder.LITTLE);
        final int objectY = in.readShort(true, ValueType.A, ByteOrder.LITTLE);
        in.readShort(false);
        final int objectX = in.readShort(true, ValueType.A, ByteOrder.LITTLE);
        final int itemId = in.readShort(false);
        final int slot = player.getInventory().getContainer().getSlotById(itemId);
        final int size = 1;

        if (!player.getInventory().getContainer().contains(itemId)) {
            return;
        }

        player.facePosition(new Position(objectX, objectY));
        player.getMovementQueueListener().submit(new Runnable() {
            @Override
            public void run() {
                if (Misc.canClickObject(player.getPosition(), new Position(objectX, objectY), size)) {
                    switch (objectId) {

                    }

                    switch (itemId) {

                    }
                }
            }
        });
    }
}
