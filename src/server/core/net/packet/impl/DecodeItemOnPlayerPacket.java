package server.core.net.packet.impl;

import server.core.net.packet.PacketDecoder;
import server.core.net.packet.PacketOpcodeHeader;
import server.core.net.packet.PacketBuffer.ByteOrder;
import server.core.net.packet.PacketBuffer.ReadBuffer;
import server.core.net.packet.PacketBuffer.ValueType;
import server.world.World;
import server.world.entity.player.Player;

/**
 * Sent when a player uses an item on another player.
 * 
 * @author lare96
 */
@PacketOpcodeHeader( { 14 })
public class DecodeItemOnPlayerPacket extends PacketDecoder {

    @Override
    @SuppressWarnings("unused")
    public void decode(final Player player, ReadBuffer in) {
        int something = in.readShort(ValueType.A, ByteOrder.BIG);
        int playerId = in.readShort();
        final int itemUsed = in.readShort();
        int something4 = in.readShort(false, ValueType.A, ByteOrder.LITTLE);
        final Player usedOn = World.getPlayers().get(playerId);

        if (usedOn == null || !player.getInventory().getContainer().contains(itemUsed)) {
            return;
        }

        player.getMovementQueueListener().submit(new Runnable() {
            @Override
            public void run() {
                if (player.getPosition().withinDistance(usedOn.getPosition(), 1)) {
                    switch (itemUsed) {

                    }
                }
            }
        });
    }
}
