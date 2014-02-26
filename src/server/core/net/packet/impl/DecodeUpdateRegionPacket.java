package server.core.net.packet.impl;

import server.core.net.buffer.PacketBuffer.ReadBuffer;
import server.core.net.packet.PacketDecoder;
import server.world.entity.player.Player;
import server.world.item.ground.GroundItem;
import server.world.music.MapMusic;
import server.world.object.WorldObject;

/**
 * Sent when the player loads a new map region
 * 
 * @author lare96
 */
public class DecodeUpdateRegionPacket extends PacketDecoder {

    @Override
    public void decode(Player player, ReadBuffer in) {
        WorldObject.getRegisterable().loadNewRegion(player);
        GroundItem.getRegisterable().loadNewRegion(player);
        player.displayInterfaces();
        MapMusic.loadMusic(player);

        if (!player.isFirstPacket()) {
            player.setFirstPacket(true);
        }
    }

    @Override
    public int[] opcode() {
        return new int[] { 121 };
    }
}
