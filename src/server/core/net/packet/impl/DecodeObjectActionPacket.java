package server.core.net.packet.impl;

import server.core.net.buffer.PacketBuffer.ByteOrder;
import server.core.net.buffer.PacketBuffer.ReadBuffer;
import server.core.net.buffer.PacketBuffer.ValueType;
import server.core.net.packet.PacketDecoder;
import server.core.net.packet.PacketOpcodeHeader;
import server.util.Misc;
import server.world.entity.Animation;
import server.world.entity.player.Player;
import server.world.entity.player.content.Spellbook;
import server.world.entity.player.skill.SkillManager;
import server.world.entity.player.skill.SkillManager.SkillConstant;
import server.world.map.Position;

/**
 * Sent when the player first/second/third clicks an object.
 * 
 * @author lare96
 */
@PacketOpcodeHeader( { 132, 252, 70 })
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

                            switch (objectId) {

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

                            }
                        }
                    }
                });
                break;
        }
    }
}
