package com.asteria.engine.net.packet.impl;

import com.asteria.engine.net.ProtocolBuffer;
import com.asteria.engine.net.ProtocolBuffer.ByteOrder;
import com.asteria.engine.net.packet.PacketDecoder;
import com.asteria.engine.net.packet.PacketOpcodeHeader;
import com.asteria.world.World;
import com.asteria.world.entity.player.Player;
import com.asteria.world.entity.player.skill.SkillEvent;

/**
 * Sent when the player tries to follow another player.
 * 
 * @author lare96
 */
@PacketOpcodeHeader({ 39 })
public class DecodeFollowPlayerPacket extends PacketDecoder {

    @Override
    public void decode(Player player, ProtocolBuffer buf) {
        int followId = buf.readShort(false, ByteOrder.LITTLE);

        if (followId < 0) {
            return;
        }
        Player follow = World.getPlayers().get(followId);

        if (follow == null
                || !follow.getPosition().isViewableFrom(player.getPosition())
                || follow.equals(player)) {
            return;
        }

        SkillEvent.fireSkillEvents(player);
        player.getMovementQueue().follow(follow);
    }
}
