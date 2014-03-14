package server.core.net.packet.impl;

import server.core.net.buffer.PacketBuffer.ByteOrder;
import server.core.net.buffer.PacketBuffer.ReadBuffer;
import server.core.net.packet.PacketDecoder;
import server.core.net.packet.PacketOpcodeHeader;
import server.world.World;
import server.world.entity.player.Player;
import server.world.entity.player.skill.SkillEvent;

/**
 * Sent when the player tries to follow another player.
 * 
 * @author lare96
 */
@PacketOpcodeHeader( { 39 })
public class DecodeFollowPlayerPacket extends PacketDecoder {

    @Override
    public void decode(Player player, ReadBuffer in) {
        int followId = in.readShort(false, ByteOrder.LITTLE);
        Player follow = World.getPlayers().get(followId);

        if (follow == null) {
            return;
        }

        SkillEvent.fireSkillEvents(player);
        player.follow(follow);
    }
}
