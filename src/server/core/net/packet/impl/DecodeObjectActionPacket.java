package server.core.net.packet.impl;

import server.core.net.buffer.PacketBuffer.ByteOrder;
import server.core.net.buffer.PacketBuffer.ReadBuffer;
import server.core.net.buffer.PacketBuffer.ValueType;
import server.core.net.packet.PacketDecoder;
import server.util.Misc;
import server.world.entity.Animation;
import server.world.entity.player.Player;
import server.world.entity.player.content.Spellbook;
import server.world.entity.player.skill.SkillManager;
import server.world.entity.player.skill.SkillManager.SkillConstant;
import server.world.entity.player.skill.impl.Mining;
import server.world.entity.player.skill.impl.Runecrafting;
import server.world.entity.player.skill.impl.Smithing;
import server.world.entity.player.skill.impl.Thieving;
import server.world.entity.player.skill.impl.Woodcutting;
import server.world.entity.player.skill.impl.Mining.Ore;
import server.world.entity.player.skill.impl.Mining.OreObject;
import server.world.entity.player.skill.impl.Mining.Pickaxe;
import server.world.entity.player.skill.impl.Runecrafting.Altar;
import server.world.entity.player.skill.impl.Thieving.TheftObject;
import server.world.entity.player.skill.impl.Woodcutting.Axe;
import server.world.entity.player.skill.impl.Woodcutting.Tree;
import server.world.map.Position;
import server.world.object.WildernessObeliskSet;

/**
 * Sent when the player first/second/third clicks an object.
 * 
 * @author lare96
 */
public class DecodeObjectActionPacket extends PacketDecoder {

    /** The various packet opcodes. */
    private static final int FIRST_CLICK = 132, SECOND_CLICK = 252,
            THIRD_CLICK = 70;

    @Override
    public void decode(final Player player, ReadBuffer in) {
        switch (player.getSession().getPacketOpcode()) {
            case FIRST_CLICK:
                final int objectX = in.readShort(true, ValueType.A, ByteOrder.LITTLE);
                final int objectId = in.readShort(false);
                final int objectY = in.readShort(false, ValueType.A);
                final int objSize = 1;

                player.facePosition(new Position(objectX, objectY));
                player.getMovementQueueListener().submit(new Runnable() {
                    @Override
                    public void run() {
                        if (Misc.canClickObject(player.getPosition(), new Position(objectX, objectY, player.getPosition().getZ()), objSize)) {

                            if (Tree.containsTree(objectId)) {
                                Tree tree = Tree.getTree(objectId);
                                Axe axe = Woodcutting.getSingleton().getAxe(player);

                                if (axe != null) {
                                    Woodcutting.getSingleton().cut(player, tree, axe, new Position(objectX, objectY, player.getPosition().getZ()), objectId);
                                }
                            }

                            switch (objectId) {
                                case 14829:
                                    WildernessObeliskSet.activate(player, 0);
                                    break;
                                case 14830:
                                    WildernessObeliskSet.activate(player, 1);
                                    break;
                                case 14827:
                                    WildernessObeliskSet.activate(player, 2);
                                    break;
                                case 14828:
                                    WildernessObeliskSet.activate(player, 3);
                                    break;
                                case 14826:
                                    WildernessObeliskSet.activate(player, 4);
                                    break;
                                case 14831:
                                    WildernessObeliskSet.activate(player, 5);
                                    break;
                                case 450:
                                case 451:
                                case 452:
                                case 453:
                                    player.getPacketBuilder().sendMessage("There is no ore concealed within this rock!");
                                    break;
                                case 3193:
                                case 2213:
                                    player.getBank().open();
                                    break;
                                case 409:
                                    if (player.getSkills()[Misc.PRAYER].getLevel() < player.getSkills()[Misc.PRAYER].getLevelForExperience()) {
                                        player.animation(new Animation(645));
                                        player.getSkills()[Misc.PRAYER].setLevel(player.getSkills()[Misc.PRAYER].getLevelForExperience());
                                        player.getPacketBuilder().sendMessage("You recharge your prayer points.");
                                        SkillManager.refresh(player, SkillConstant.PRAYER);
                                    } else {
                                        player.getPacketBuilder().sendMessage("You already have full prayer points.");
                                    }
                                    break;
                                case 6552:
                                    if (player.getSpellbook() == Spellbook.ANCIENT) {
                                        Spellbook.convert(player, Spellbook.NORMAL);
                                    } else if (player.getSpellbook() == Spellbook.NORMAL) {
                                        Spellbook.convert(player, Spellbook.ANCIENT);
                                    }
                                    break;
                                case 2108:
                                case 2109:
                                case 2090:
                                case 2091:
                                case 2094:
                                case 2095:
                                case 2092:
                                case 2093:
                                case 2100:
                                case 2101:
                                case 2096:
                                case 2097:
                                case 2098:
                                case 2099:
                                case 2102:
                                case 2103:
                                case 2104:
                                case 2105:
                                case 2106:
                                case 2107:
                                case 2491:
                                    for (Ore o : Ore.values()) {
                                        if (o == null) {
                                            continue;
                                        }

                                        for (OreObject ore : o.getObjectOre()) {
                                            if (ore == null) {
                                                continue;
                                            }

                                            if (objectId == ore.getOre()) {
                                                Pickaxe pick = Mining.getSingleton().getPickaxe(player);

                                                if (pick != null) {
                                                    Mining.getSingleton().mine(player, o, pick, new Position(objectX, objectY, player.getPosition().getZ()), objectId);
                                                }
                                            }
                                        }
                                    }
                                    break;
                                case 2478:
                                case 2479:
                                case 2480:
                                case 2481:
                                case 2482:
                                case 2483:
                                case 2484:
                                case 2487:
                                case 2486:
                                case 2485:
                                case 2488:
                                case 7141:
                                case 7138:
                                    for (Altar a : Altar.values()) {
                                        if (a == null) {
                                            continue;
                                        }

                                        if (a.getAltarId() == objectId) {
                                            Runecrafting.getSingleton().craftRunes(player, a.getRune());
                                        }
                                    }
                                    break;
                                case 2781:
                                case 2785:
                                case 2966:
                                case 6189:
                                case 3044:
                                case 3294:
                                case 4304:
                                    Smithing.getSingleton().smeltInterface(player);
                                    break;
                            }
                        }
                    }
                });
                break;

            case SECOND_CLICK:
                final int objId = in.readShort(false, ValueType.A, ByteOrder.LITTLE);
                final int objY = in.readShort(true, ByteOrder.LITTLE);
                final int objX = in.readShort(false, ValueType.A);
                final int size = 1;

                player.facePosition(new Position(objX, objY));

                player.getMovementQueueListener().submit(new Runnable() {
                    @Override
                    public void run() {
                        if (Misc.canClickObject(player.getPosition(), new Position(objX, objY, player.getPosition().getZ()), size)) {
                            switch (objId) {
                                case 635:
                                    player.getPacketBuilder().sendMessage("lol");
                                    Thieving.getSingleton().stealFromObject(player, TheftObject.TEA_STALL);
                                    break;
                                case 2108:
                                case 2109:
                                case 2090:
                                case 2091:
                                case 2094:
                                case 2095:
                                case 2092:
                                case 2093:
                                case 2100:
                                case 2101:
                                case 2096:
                                case 2097:
                                case 2098:
                                case 2099:
                                case 2102:
                                case 2103:
                                case 2104:
                                case 2105:
                                case 2106:
                                case 2107:
                                case 2491:
                                    for (Ore o : Ore.values()) {
                                        if (o == null) {
                                            continue;
                                        }

                                        for (OreObject ore : o.getObjectOre()) {
                                            if (ore == null) {
                                                continue;
                                            }

                                            if (objId == ore.getOre()) {
                                                Mining.getSingleton().prospect(player, o);
                                            }
                                        }
                                    }
                                    break;
                            }
                        }
                    }
                });
                break;

            case THIRD_CLICK:
                final int x = in.readShort(true, ByteOrder.LITTLE);
                final int y = in.readShort(false);
                final int id = in.readShort(false, ValueType.A, ByteOrder.LITTLE);
                final int objectSize = 1;

                player.facePosition(new Position(x, y));

                player.getMovementQueueListener().submit(new Runnable() {
                    @Override
                    public void run() {
                        if (Misc.canClickObject(player.getPosition(), new Position(x, y, player.getPosition().getZ()), objectSize)) {
                            switch (id) {

                                // FIXME: Find the ids for all of the thieving
                                // stalls, and add them here.
                                case 635:
                                    Thieving.getSingleton().stealFromObject(player, TheftObject.TEA_STALL);
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
        return new int[] { 132, 252, 70 };
    }
}
