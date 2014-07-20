package com.asteria.engine.net.packet.impl;

import com.asteria.engine.net.ProtocolBuffer;
import com.asteria.engine.net.packet.PacketDecoder;
import com.asteria.engine.net.packet.PacketOpcodeHeader;
import com.asteria.world.entity.player.Player;
import com.asteria.world.entity.player.skill.SkillEvent;
import com.asteria.world.map.Position;

/**
 * Sent whenever the makes a yellow-x click, red-x click, or clicks the minimap.
 * 
 * @author lare96
 */
@PacketOpcodeHeader({ 248, 164, 98 })
public class DecodeMovementPacket extends PacketDecoder {

    // TODO: Does walking need to be validated?
    @Override
    public void decode(Player player, ProtocolBuffer buf) {
        int length = player.getSession().getPacketLength();
        player.faceEntity(null);

        // minimap click
        if (player.getSession().getPacketOpcode() == 248) {
            SkillEvent.fireSkillEvents(player);
            player.setFollowing(false);
            player.getCombatBuilder().resetAttackTimer();
            length -= 14;
        }

        // yellow x click
        if (player.getSession().getPacketOpcode() == 164) {
            SkillEvent.fireSkillEvents(player);
            player.setFollowing(false);
            player.getCombatBuilder().resetAttackTimer();
            // player.getRS2Packet().sendMessage("164");

            // red x click
        } else if (player.getSession().getPacketOpcode() == 98) {
            // player.getRS2Packet().sendMessage("98");
        }

        if (player.isFrozen()) {
            player.getPacketBuilder().sendMessage("You are unable to move.");
            return;
        }

        if (player.inDialogue()) {
            player.stopDialogue();
        }

        if (player.getTradeSession().inTrade()) {
            player.getTradeSession().reset(false);
        }

        player.getPacketBuilder().sendCloseWindows();
        player.setOpenShopId(-1);

        int steps = (length - 5) / 2;
        int[][] path = new int[steps][2];
        int firstStepX = buf.readShort(ProtocolBuffer.ValueType.A,
                ProtocolBuffer.ByteOrder.LITTLE);

        for (int i = 0; i < steps; i++) {
            path[i][0] = buf.readByte();
            path[i][1] = buf.readByte();
        }
        int firstStepY = buf.readShort(ProtocolBuffer.ByteOrder.LITTLE);
        player.getMovementQueue().reset();
        player.getMovementQueue().setRunPath(
                buf.readByte(ProtocolBuffer.ValueType.C) == 1);
        player.getMovementQueue().addToPath(
                new Position(firstStepX, firstStepY));

        for (int i = 0; i < steps; i++) {
            path[i][0] += firstStepX;
            path[i][1] += firstStepY;
            player.getMovementQueue().addToPath(
                    new Position(path[i][0], path[i][1]));
        }
        player.getMovementQueue().finish();
        player.getPacketBuilder().sendMessage(
                "DEBUG[walking= " + player.getPosition().getRegion() + "]");
    }
}
