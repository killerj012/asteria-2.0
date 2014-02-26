package server.core.net.packet.impl;

import server.core.Rs2Engine;
import server.core.net.buffer.PacketBuffer.ByteOrder;
import server.core.net.buffer.PacketBuffer.ReadBuffer;
import server.core.net.buffer.PacketBuffer.ValueType;
import server.core.net.packet.PacketDecoder;
import server.world.entity.combat.Combat;
import server.world.entity.npc.Npc;
import server.world.entity.npc.NpcDefinition;
import server.world.entity.player.Player;
import server.world.entity.player.skill.impl.Fishing;
import server.world.entity.player.skill.impl.Thieving;
import server.world.entity.player.skill.impl.Fishing.FishingTool;
import server.world.entity.player.skill.impl.Thieving.TheftNpc;
import server.world.map.Position;
import server.world.shop.Shop;

/**
 * Sent when the player attacks an npc using melee/range, attacks a npc using
 * magic, first clicks an npc or second clicks an npc.
 * 
 * @author lare96
 */
public class DecodeNpcActionPacket extends PacketDecoder {

    /** The various packet opcodes. */
    public static final int ATTACK_NPC = 72, MAGE_NPC = 131, FIRST_CLICK = 155,
            SECOND_CLICK = 17;

    @Override
    public void decode(final Player player, ReadBuffer in) {

        switch (player.getSession().getPacketOpcode()) {
            case ATTACK_NPC:
                int index = in.readShort(false, ValueType.A);
                final Npc attackMelee = Rs2Engine.getWorld().getNpcs()[index];

                if (attackMelee == null) {
                    return;
                }

                /** Check if this mob is attackable. */
                if (!NpcDefinition.getNpcDefinition()[attackMelee.getNpcId()].isAttackable()) {
                    return;
                }

                Combat.fight(player, attackMelee);
                break;
            case MAGE_NPC:
                index = in.readShort(true, ValueType.A, ByteOrder.LITTLE);
                final Npc attackMagic = Rs2Engine.getWorld().getNpcs()[index];

                if (attackMagic == null) {
                    return;
                }

                /** Check if this npc is attackable. */
                if (!NpcDefinition.getNpcDefinition()[attackMagic.getNpcId()].isAttackable()) {
                    return;
                }

                Combat.fight(player, attackMagic);
                break;
            case FIRST_CLICK:
                index = in.readShort(true, ByteOrder.LITTLE);
                final Npc firstClickMob = Rs2Engine.getWorld().getNpcs()[index];

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
                                    player.setRunecraftingNpc(firstClickMob);
                                    player.dialogue(2);
                                    break;
                                case 956:
                                    player.dialogue(1);
                                    break;
                                case 249:
                                    player.dialogue(3);
                                    break;
                                case 605:
                                    player.dialogue(4);
                                    break;
                                case 319:
                                    if (player.getInventory().getContainer().contains(FishingTool.NET.getId())) {
                                        Fishing.getSingleton().startFish(player, FishingTool.NET);
                                    } else {
                                        Fishing.getSingleton().startFish(player, FishingTool.BIG_NET);
                                    }
                                    break;
                                case 324:
                                    Fishing.getSingleton().startFish(player, FishingTool.LOBSTER_POT);
                                    break;
                                case 328:
                                    Fishing.getSingleton().startFish(player, FishingTool.FLY_FISHING_ROD);
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
                final Npc secondClickNpc = Rs2Engine.getWorld().getNpcs()[index];

                if (secondClickNpc == null) {
                    return;
                }

                player.getMovementQueueListener().submit(new Runnable() {
                    @Override
                    public void run() {
                        if (player.getPosition().withinDistance(new Position(secondClickNpc.getPosition().getX(), secondClickNpc.getPosition().getY(), secondClickNpc.getPosition().getZ()), 1)) {
                            player.facePosition(secondClickNpc.getPosition());

                            switch (secondClickNpc.getNpcId()) {
                                case 1:
                                case 2:
                                case 3:
                                case 4:
                                case 5:
                                case 6:
                                    Thieving.getSingleton().stealNpc(player, TheftNpc.MAN_AND_WOMAN, secondClickNpc);
                                    break;
                                case 7:
                                    Thieving.getSingleton().stealNpc(player, TheftNpc.FARMER, secondClickNpc);
                                    break;
                                case 1714:
                                    Thieving.getSingleton().stealNpc(player, TheftNpc.MALE_HAM, secondClickNpc);
                                    break;
                                case 1715:
                                    Thieving.getSingleton().stealNpc(player, TheftNpc.FEMALE_HAM, secondClickNpc);
                                    break;
                                case 15:
                                    Thieving.getSingleton().stealNpc(player, TheftNpc.WARRIOR_WOMAN, secondClickNpc);
                                    break;
                                case 187:
                                    Thieving.getSingleton().stealNpc(player, TheftNpc.ROGUE, secondClickNpc);
                                    break;
                                case 2234:
                                    Thieving.getSingleton().stealNpc(player, TheftNpc.MASTER_FARMER, secondClickNpc);
                                    break;
                                case 9:
                                case 32:
                                case 2699:
                                case 2700:
                                case 2701:
                                case 2702:
                                case 2703:
                                case 3228:
                                case 3229:
                                case 3230:
                                case 3231:
                                case 3232:
                                case 3233:
                                case 3241:
                                    Thieving.getSingleton().stealNpc(player, TheftNpc.GUARD, secondClickNpc);
                                    break;
                                case 1305:
                                case 1306:
                                case 1307:
                                case 1308:
                                case 1309:
                                case 1310:
                                case 1311:
                                case 1312:
                                case 1313:
                                    Thieving.getSingleton().stealNpc(player, TheftNpc.RELLEKKA_CITIZEN, secondClickNpc);
                                    break;
                                case 23:
                                case 26:
                                    Thieving.getSingleton().stealNpc(player, TheftNpc.KNIGHT_OF_ARDOUGNE, secondClickNpc);
                                    break;
                                case 34:
                                    Thieving.getSingleton().stealNpc(player, TheftNpc.WATCHMAN, secondClickNpc);
                                    break;
                                case 1904:
                                case 1905:
                                    Thieving.getSingleton().stealNpc(player, TheftNpc.MENAPHITE_THUG, secondClickNpc);
                                    break;
                                case 20:
                                case 365:
                                case 2256:
                                    Thieving.getSingleton().stealNpc(player, TheftNpc.PALADIN, secondClickNpc);
                                    break;
                                case 66:
                                case 67:
                                case 68:
                                    Thieving.getSingleton().stealNpc(player, TheftNpc.GNOME, secondClickNpc);
                                    break;
                                case 21:
                                    Thieving.getSingleton().stealNpc(player, TheftNpc.HERO, secondClickNpc);
                                    break;
                                case 1183:
                                case 1184:
                                    Thieving.getSingleton().stealNpc(player, TheftNpc.ELF, secondClickNpc);
                                    break;
                                case 319:
                                    if (player.getInventory().getContainer().contains(FishingTool.FISHING_ROD.getId())) {
                                        Fishing.getSingleton().startFish(player, FishingTool.FISHING_ROD);
                                    } else {
                                        Fishing.getSingleton().startFish(player, FishingTool.OILY_FISHING_ROD);
                                    }
                                    break;
                                case 324:
                                    Fishing.getSingleton().startFish(player, FishingTool.HARPOON);
                                    break;
                                case 328:
                                    if (player.getInventory().getContainer().contains(FishingTool.FISHING_ROD.getId())) {
                                        Fishing.getSingleton().startFish(player, FishingTool.FISHING_ROD);
                                    } else {
                                        Fishing.getSingleton().startFish(player, FishingTool.OILY_FISHING_ROD);
                                    }
                                    break;
                            }
                        }
                    }
                });
                break;
        }
    }

    @Override
    public int[] opcode() {
        return new int[] { 72, 131, 155, 17 };
    }
}
