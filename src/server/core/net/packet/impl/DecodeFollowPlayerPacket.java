package server.core.net.packet.impl;

import server.core.Rs2Engine;
import server.core.net.buffer.PacketBuffer.ByteOrder;
import server.core.net.buffer.PacketBuffer.ReadBuffer;
import server.core.net.packet.PacketDecoder;
import server.world.entity.player.Player;
import server.world.entity.player.skill.SkillEvent;

/**
 * Sent when the player tries to follow another player.
 * 
 * @author lare96
 */
public class DecodeFollowPlayerPacket extends PacketDecoder {

    @Override
    public void decode(Player player, ReadBuffer in) {
        int followId = in.readShort(false, ByteOrder.LITTLE);
        Player follow = Rs2Engine.getWorld().getPlayers()[followId];

        if (follow == null) {
            return;
        }

        SkillEvent.resetSkillEvent(player);
        player.follow(follow);
    }

    @Override
    public int[] opcode() {
        return new int[] { 39 };
    }
}
