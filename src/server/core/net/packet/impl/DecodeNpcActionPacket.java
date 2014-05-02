package server.core.net.packet.impl;

import server.core.net.buffer.PacketBuffer.ByteOrder;
import server.core.net.buffer.PacketBuffer.ReadBuffer;
import server.core.net.buffer.PacketBuffer.ValueType;
import server.core.net.packet.PacketDecoder;
import server.core.net.packet.PacketOpcodeHeader;
import server.util.Misc;
import server.world.World;
import server.world.entity.combat.CombatFactory;
import server.world.entity.npc.Npc;
import server.world.entity.npc.NpcDefinition;
import server.world.entity.player.Player;
import server.world.entity.player.minigame.Minigame;
import server.world.entity.player.minigame.MinigameFactory;
import server.world.map.Position;
import server.world.shop.Shop;

/**
 * Sent when the player attacks an npc using melee/range, attacks a npc using
 * magic, first clicks an npc or second clicks an npc.
 * 
 * @author lare96
 */
@PacketOpcodeHeader( { 72, 131, 155, 17 })
public class DecodeNpcActionPacket extends PacketDecoder {

    /** The various packet opcodes. */
    public static final int ATTACK_NPC = 72, MAGE_NPC = 131, FIRST_CLICK = 155,
            SECOND_CLICK = 17;

    @Override
    public void decode(final Player player, ReadBuffer in) {

        switch (player.getSession().getPacketOpcode()) {
            case ATTACK_NPC:
                int index = in.readShort(false, ValueType.A);
                final Npc attackMelee = World.getNpcs().get(index);

                if (attackMelee == null) {
                    return;
                }

                /** Check if this mob is attackable. */
                if (!NpcDefinition.getNpcDefinition()[attackMelee.getNpcId()].isAttackable()) {
                    return;
                }

                for (Minigame minigame : MinigameFactory.getMinigames().values()) {
                    if (minigame.inMinigame(player)) {
                        if (!minigame.canHit(player, attackMelee)) {
                            return;
                        }
                    }
                }

                if (CombatFactory.RANGE_WEAPONS.contains(player.getEquipment().getContainer().getItemId(Misc.EQUIPMENT_SLOT_WEAPON))) {
                    player.getCombatBuilder().attack(attackMelee, CombatFactory.newDefaultRangedStrategy());
                } else if (player.isAutocastMagic()) {
                    player.getCombatBuilder().attack(attackMelee, CombatFactory.newDefaultMagicStrategy());
                } else {
                    player.getCombatBuilder().attack(attackMelee, CombatFactory.newDefaultMeleeStrategy());
                }
                break;
            case MAGE_NPC:
                index = in.readShort(true, ValueType.A, ByteOrder.LITTLE);
                final Npc attackMagic = World.getNpcs().get(index);

                if (attackMagic == null) {
                    return;
                }

                for (Minigame minigame : MinigameFactory.getMinigames().values()) {
                    if (minigame.inMinigame(player)) {
                        if (!minigame.canHit(player, attackMagic)) {
                            return;
                        }
                    }
                }

                /** Check if this npc is attackable. */
                if (!NpcDefinition.getNpcDefinition()[attackMagic.getNpcId()].isAttackable()) {
                    return;
                }

                player.getCombatBuilder().attack(attackMagic, CombatFactory.newDefaultMagicStrategy());
                break;
            case FIRST_CLICK:
                index = in.readShort(true, ByteOrder.LITTLE);
                final Npc firstClickMob = World.getNpcs().get(index);

                if (firstClickMob == null) {
                    return;
                }

                player.getMovementQueueListener().submit(new Runnable() {
                    @Override
                    public void run() {
                        if (player.getPosition().withinDistance(new Position(firstClickMob.getPosition().getX(), firstClickMob.getPosition().getY(), firstClickMob.getPosition().getZ()), 1)) {
                            player.facePosition(firstClickMob.getPosition());

                            switch (firstClickMob.getNpcId()) {
                                case 460:
                                    player.dialogue(2);
                                    break;
                                case 520:
                                    Shop.getShop(0).openShop(player);
                                    break;
                            }
                        }
                    }
                });
                break;

            case SECOND_CLICK:
                index = in.readShort(false, ValueType.A, ByteOrder.LITTLE);
                final Npc secondClickNpc = World.getNpcs().get(index);

                if (secondClickNpc == null) {
                    return;
                }

                player.getMovementQueueListener().submit(new Runnable() {
                    @Override
                    public void run() {
                        if (player.getPosition().withinDistance(new Position(secondClickNpc.getPosition().getX(), secondClickNpc.getPosition().getY(), secondClickNpc.getPosition().getZ()), 1)) {
                            player.facePosition(secondClickNpc.getPosition());

                            switch (secondClickNpc.getNpcId()) {

                            }
                        }
                    }
                });
                break;
        }
    }
}
