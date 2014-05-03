package server.core.net.packet.impl;

import server.core.net.buffer.PacketBuffer.ByteOrder;
import server.core.net.buffer.PacketBuffer.ReadBuffer;
import server.core.net.packet.PacketDecoder;
import server.core.net.packet.PacketOpcodeHeader;
import server.util.Misc;
import server.world.World;
import server.world.entity.combat.CombatFactory;
import server.world.entity.player.Player;
import server.world.entity.player.minigame.Minigame;
import server.world.entity.player.minigame.MinigameFactory;

/**
 * Sent when a player attacks another player.
 * 
 * @author lare96
 */
@PacketOpcodeHeader( { 73 })
public class DecodeAttackPlayerPacket extends PacketDecoder {

    @Override
    public void decode(Player player, ReadBuffer in) {
        int index = in.readShort(true, ByteOrder.LITTLE);
        Player attacked = World.getPlayers().get(index);

        if (attacked == null) {
            return;
        }

        Minigame minigame = MinigameFactory.getMinigame(player);

        if (minigame == null) {

            // /** Wilderness location check. */
            // if (!Location.inWilderness(player) ||
            // !Location.inWilderness(attacked)) {
            // player.getPacketBuilder().sendMessage("Both you and " +
            // attacked.getUsername() + " need to be in the wilderness to
            // fight!");
            // return;
            // }
            //
            // /** The combat level difference check. */
            // int combatDifference =
            // CombatFactory.calculateCombatDifference(player.getCombatLevel(),
            // attacked.getCombatLevel());
            //
            // if (combatDifference > player.getWildernessLevel() ||
            // combatDifference > attacked.getWildernessLevel()) {
            // player.getPacketBuilder().sendMessage("Your combat level
            // difference is too great to attack that player here.");
            // player.getMovementQueue().reset();
            // return;
            // }
        } else {
            if (!minigame.canHit(player, attacked)) {
                return;
            }
        }

        /** Start combat! */
        if (CombatFactory.RANGE_WEAPONS.contains(player.getEquipment().getContainer().getItemId(Misc.EQUIPMENT_SLOT_WEAPON))) {
            player.getCombatBuilder().attack(attacked, CombatFactory.newDefaultRangedStrategy());
        } else if (player.isAutocastMagic()) {
            player.getCombatBuilder().attack(attacked, CombatFactory.newDefaultMagicStrategy());
        } else {
            player.getCombatBuilder().attack(attacked, CombatFactory.newDefaultMeleeStrategy());
        }
    }
}
