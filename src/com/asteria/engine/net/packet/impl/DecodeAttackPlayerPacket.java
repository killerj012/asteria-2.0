package com.asteria.engine.net.packet.impl;

import com.asteria.engine.net.ProtocolBuffer;
import com.asteria.engine.net.ProtocolBuffer.ByteOrder;
import com.asteria.engine.net.packet.PacketDecoder;
import com.asteria.engine.net.packet.PacketOpcodeHeader;
import com.asteria.world.World;
import com.asteria.world.entity.combat.CombatFactory;
import com.asteria.world.entity.player.Player;
import com.asteria.world.entity.player.minigame.Minigame;
import com.asteria.world.entity.player.minigame.MinigameFactory;
import com.asteria.world.map.Location;

/**
 * Sent when a player attacks another player.
 * 
 * @author lare96
 */
@PacketOpcodeHeader({ 73 })
public class DecodeAttackPlayerPacket extends PacketDecoder {

    @Override
    public void decode(Player player, ProtocolBuffer buf) {
        int index = buf.readShort(true, ByteOrder.LITTLE);

        if (index < 0) {
            return;
        }

        Player attacked = World.getPlayers().get(index);
        if (attacked == null || attacked.equals(player)) {
            return;
        }
        Minigame minigame = MinigameFactory.getMinigame(player);

        if (minigame == null) {

            // Wilderness location check.
            if (!Location.inWilderness(player)
                    || !Location.inWilderness(attacked)) {
                player.getPacketBuilder().sendMessage(
                                "Both you and " + attacked
                                        .getCapitalizedUsername()
                                + " need to be in the wilderness to fight!");
                return;
            }

            // Multicombat location check.
            if (!Location.inMultiCombat(player)
                    && player.getCombatBuilder().isBeingAttacked()
                    && player.getCombatBuilder().getLastAttacker() != attacked) {
                player.getPacketBuilder().sendMessage(
                        "You are already under attack!");
                return;
            }

            // The combat level difference check.
            int combatDifference = CombatFactory.combatLevelDifference(
                    player.getCombatLevel(), attacked.getCombatLevel());

            if (combatDifference > player.getWildernessLevel()
                    || combatDifference > attacked.getWildernessLevel()) {
                player.getPacketBuilder()
                        .sendMessage(
                                "Your combat level difference is too great to attack that player here.");
                player.getMovementQueue().reset();
                return;
            }

            // Skull the player if needed.
            if (!player.getCombatBuilder().isBeingAttacked()
                    || player.getCombatBuilder().isBeingAttacked()
                    && player.getCombatBuilder().getLastAttacker() != attacked
                    && Location.inMultiCombat(player)) {
                CombatFactory.skullPlayer(player);
            }
        } else {
            if (!minigame.canHit(player, attacked)) {
                return;
            }
        }

        // Start combat!
        player.getCombatBuilder().attack(attacked);
    }
}
