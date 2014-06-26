package server.core.net.packet.impl;

import server.core.net.packet.PacketBuffer.ByteOrder;
import server.core.net.packet.PacketBuffer.ReadBuffer;
import server.core.net.packet.PacketBuffer.ValueType;
import server.core.net.packet.PacketDecoder;
import server.core.net.packet.PacketOpcodeHeader;
import server.world.World;
import server.world.entity.combat.CombatFactory;
import server.world.entity.combat.magic.CombatMagicSpells;
import server.world.entity.combat.magic.CombatSpell;
import server.world.entity.player.Player;
import server.world.entity.player.minigame.Minigame;
import server.world.entity.player.minigame.MinigameFactory;
import server.world.map.Location;

/**
 * Sent when a player attacks another player using magic.
 * 
 * @author lare96
 */
@PacketOpcodeHeader({ 249 })
public class DecodeAttackPlayerMagicPacket extends PacketDecoder {

    @Override
    public void decode(Player player, ReadBuffer in) {
        int index = in.readShort(true, ValueType.A);
        int spellId = in.readShort(true, ByteOrder.LITTLE);
        Player attacked = World.getPlayers().get(index);
        CombatSpell spell = CombatMagicSpells.getSpell(spellId).getSpell();

        if (attacked == null) {
            return;
        }

        Minigame minigame = MinigameFactory.getMinigame(player);

        if (minigame == null) {

            /** Wilderness location check. */
            if (!Location.inWilderness(player)
                    || !Location.inWilderness(attacked)) {
                player.getPacketBuilder().sendMessage("Both you and "
                        + attacked.getUsername()
                        + " need to be in the wilderness to fight!");
                return;
            }

            /** Multicombat location check. */
            if (!Location.inMultiCombat(player)
                    && player.getCombatBuilder().isBeingAttacked()
                    && player.getCombatBuilder().getLastAttacker() != attacked) {
                player.getPacketBuilder().sendMessage("You are already under attack!");
                return;
            }

            /** The combat level difference check. */
            int combatDifference = CombatFactory.calculateCombatDifference(player.getCombatLevel(), attacked.getCombatLevel());

            if (combatDifference > player.getWildernessLevel()
                    || combatDifference > attacked.getWildernessLevel()) {
                player.getPacketBuilder().sendMessage("Your combat level difference is too great to attack that player here.");
                player.getMovementQueue().reset();
                return;
            }

            /** Skull the player if needed. */
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
        /** Start combat! */
        player.setAutocastSpell(null);
        player.setAutocast(false);
        player.getPacketBuilder().sendConfig(108, 0);
        player.setCastSpell(spell);
        player.getCombatBuilder().attack(attacked);
    }
}
