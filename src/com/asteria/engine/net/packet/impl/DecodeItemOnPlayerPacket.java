package com.asteria.engine.net.packet.impl;

import com.asteria.engine.net.ProtocolBuffer;
import com.asteria.engine.net.ProtocolBuffer.ByteOrder;
import com.asteria.engine.net.ProtocolBuffer.ValueType;
import com.asteria.engine.net.packet.PacketDecoder;
import com.asteria.engine.net.packet.PacketOpcodeHeader;
import com.asteria.world.World;
import com.asteria.world.entity.player.Player;

/**
 * Sent when a player uses an item on another player.
 * 
 * @author lare96
 */
@PacketOpcodeHeader({ 14 })
public class DecodeItemOnPlayerPacket extends PacketDecoder {

    // TODO: Proper validation.

    @Override
    public void decode(final Player player, ProtocolBuffer buf) {
        int something = buf.readShort(ValueType.A, ByteOrder.BIG);
        int playerId = buf.readShort();
        final int itemUsed = buf.readShort();
        int something4 = buf.readShort(false, ValueType.A, ByteOrder.LITTLE);

        if (something < 0 || playerId < 0 || itemUsed < 0 || something4 < 0) {
            return;
        }

        final Player usedOn = World.getPlayers().get(playerId);

        if (usedOn == null
 || !player.getInventory().contains(itemUsed)) {
            return;
        }
        player.getMovementQueueListener().append(new Runnable() {
            @Override
            public void run() {
                if (player.getPosition()
                        .withinDistance(usedOn.getPosition(), 1)) {
                    switch (itemUsed) {

                    }
                }
            }
        });
    }
}
