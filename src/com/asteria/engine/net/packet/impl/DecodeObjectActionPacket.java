package com.asteria.engine.net.packet.impl;

import com.asteria.engine.net.ProtocolBuffer;
import com.asteria.engine.net.ProtocolBuffer.ByteOrder;
import com.asteria.engine.net.ProtocolBuffer.ValueType;
import com.asteria.engine.net.packet.PacketDecoder;
import com.asteria.engine.net.packet.PacketOpcodeHeader;
import com.asteria.world.entity.Animation;
import com.asteria.world.entity.player.Player;
import com.asteria.world.entity.player.content.Spellbook;
import com.asteria.world.entity.player.skill.Skills;
import com.asteria.world.map.Position;

/**
 * Sent when the player first/second/third clicks an object.
 * 
 * @author lare96
 */
@PacketOpcodeHeader({ 132, 252, 70 })
public class DecodeObjectActionPacket extends PacketDecoder {

    // TODO: When cache reading is done, check position of objects to
    // see if you're actually near them or not.

    /** The various packet opcodes. */
    public static final int FIRST_CLICK = 132, SECOND_CLICK = 252,
            THIRD_CLICK = 70;

    /** The various fields for the packets. */
    private int objectX, objectY, objectId, objectSize;

    @Override
    public void decode(final Player player, ProtocolBuffer buf) {
        switch (player.getSession().getPacketOpcode()) {
        case FIRST_CLICK:
            objectX = buf.readShort(true, ValueType.A, ByteOrder.LITTLE);
            objectId = buf.readShort(false);
            objectY = buf.readShort(false, ValueType.A);
            objectSize = 1;

            player.facePosition(new Position(objectX, objectY));
            player.getMovementQueueListener().append(new Runnable() {
                @Override
                public void run() {
                    if (player.getPosition().withinDistance(
                            new Position(objectX, objectY, player.getPosition()
                                    .getZ()), objectSize)) {

                        switch (objectId) {

                        case 3193:
                        case 2213:
                            player.getBank().open();
                            break;
                        case 409:
                            int level = player.getSkills()[Skills.PRAYER]
                                    .getLevelForExperience();

                            if (player.getSkills()[Skills.PRAYER].getLevel() < level) {
                                player.animation(new Animation(645));
                                player.getSkills()[Skills.PRAYER]
.setLevel(
                                        level, true);
                                player.getPacketBuilder().sendMessage(
                                        "You recharge your prayer points.");
                                Skills.refresh(player, Skills.PRAYER);
                            } else {
                                player.getPacketBuilder().sendMessage(
                                        "You already have full prayer points.");
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
            objectId = buf.readShort(false, ValueType.A, ByteOrder.LITTLE);
            objectY = buf.readShort(true, ByteOrder.LITTLE);
            objectX = buf.readShort(false, ValueType.A);
            objectSize = 1;

            player.facePosition(new Position(objectX, objectY));

            player.getMovementQueueListener().append(new Runnable() {
                @Override
                public void run() {
                    if (player.getPosition().withinDistance(
                            new Position(objectX, objectY, player.getPosition()
                                    .getZ()), objectSize)) {
                        switch (objectId) {

                        }
                    }
                }
            });
            break;

        case THIRD_CLICK:
            objectX = buf.readShort(true, ByteOrder.LITTLE);
            objectY = buf.readShort(false);
            objectId = buf.readShort(false, ValueType.A, ByteOrder.LITTLE);
            objectSize = 1;

            player.facePosition(new Position(objectX, objectY));

            player.getMovementQueueListener().append(new Runnable() {
                @Override
                public void run() {
                    if (player.getPosition().withinDistance(
                            new Position(objectX, objectY, player.getPosition()
                                    .getZ()), objectSize)) {
                        switch (objectId) {

                        }
                    }
                }
            });
            break;
        }
    }
}
