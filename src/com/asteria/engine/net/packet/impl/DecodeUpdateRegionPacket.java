package com.asteria.engine.net.packet.impl;

import com.asteria.Main;
import com.asteria.engine.net.ProtocolBuffer;
import com.asteria.engine.net.packet.PacketDecoder;
import com.asteria.engine.net.packet.PacketOpcodeHeader;
import com.asteria.world.entity.player.Player;
import com.asteria.world.item.ground.GroundItemManager;
import com.asteria.world.object.WorldObjectManager;

/**
 * Sent when the player loads a new map region.
 * 
 * @author lare96
 */
@PacketOpcodeHeader({ 121 })
public class DecodeUpdateRegionPacket extends PacketDecoder {

    @Override
    public void decode(Player player, ProtocolBuffer buf) {

        // To prevent abuse of this packet, imagine someone attempting to inject
        // this 500 or so times.
        if (player.isUpdateRegion()) {
            WorldObjectManager.load(player);
            GroundItemManager.load(player);
            player.displayInterfaces();
            player.getTolerance().reset();
            player.setUpdateRegion(false);

            if (Main.DEBUG)
                player.getPacketBuilder().sendMessage(
                    "DEBUG[region= " + player.getPosition().getRegion() + "]");
        }
    }
}
