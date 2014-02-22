package server.core.net.packet.impl;

import server.core.Rs2Engine;
import server.core.net.buffer.PacketBuffer.ByteOrder;
import server.core.net.buffer.PacketBuffer.ReadBuffer;
import server.core.net.packet.PacketDecoder;
import server.world.entity.combat.Combat;
import server.world.entity.player.Player;
import server.world.map.Location;

/**
 * Sent when a player attacks another player.
 * 
 * @author lare96
 */
public class DecodeAttackPlayerPacket extends PacketDecoder {

    @Override
    public void decode(Player player, ReadBuffer in) {
        int index = in.readShort(true, ByteOrder.LITTLE);
        Player attacked = Rs2Engine.getWorld().getPlayers()[index];

        if (attacked == null) {
            return;
        }

        /** Wilderness location check. */
        if (!Location.inWilderness(player) || !Location.inWilderness(attacked)) {
            player.getPacketBuilder().sendMessage("Both you and " + attacked.getUsername() + " need to be in the wilderness to fight!");
            return;
        }

        /** The combat level difference check. */
        int combatDifference = Combat.calculateCombatDifference(player.getCombatLevel(), attacked.getCombatLevel());

        if (combatDifference > player.getWildernessLevel() || combatDifference > attacked.getWildernessLevel()) {
            player.getPacketBuilder().sendMessage("Your combat level difference is too great to attack that player here.");
            player.getMovementQueue().reset();
            return;
        }

        /** Start combat. */
        Combat.fight(player, attacked);
    }

    @Override
    public int[] opcode() {
        return new int[] { 73 };
    }
}
