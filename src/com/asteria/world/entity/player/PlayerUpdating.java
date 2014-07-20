package com.asteria.world.entity.player;

import java.util.Iterator;

import com.asteria.engine.net.ProtocolBuffer;
import com.asteria.engine.net.ProtocolBuffer.ByteOrder;
import com.asteria.engine.net.ProtocolBuffer.ValueType;
import com.asteria.engine.net.Session;
import com.asteria.engine.task.TaskFactory;
import com.asteria.util.Utility;
import com.asteria.world.World;
import com.asteria.world.entity.UpdateFlags.Flag;
import com.asteria.world.entity.player.skill.Skills;
import com.asteria.world.map.Position;

/**
 * Provides static utility methods for updating {@link Player}s.
 * 
 * @author blakeman8192
 * @author lare96
 */
public final class PlayerUpdating {

    /**
     * Updates the argued {@link Player}.
     * 
     * @param player
     *            the player to update.
     */
    public static void update(Player player) throws Exception {

        ProtocolBuffer out = new ProtocolBuffer(16384);
        ProtocolBuffer block = new ProtocolBuffer(8192);

        // Initialize the update packet.
        out.buildVarShort(81, player.getSession());
        out.startBitAccess();

        // Update this player.
        PlayerUpdating.updateLocalPlayerMovement(player, out);

        if (player.getFlags().isUpdateRequired()) {
            PlayerUpdating.updateState(player, player, block, false, true);
        }

        // Update other local players.
        out.writeBits(8, player.getLocalPlayers().size());
        for (Iterator<Player> i = player.getLocalPlayers().iterator(); i
                .hasNext();) {
            Player other = i.next();
            if (other.getPosition().isViewableFrom(player.getPosition()) && other
                    .getSession().getStage() == Session.Stage.LOGGED_IN && !other
                    .isNeedsPlacement()) {
                PlayerUpdating.updateOtherPlayerMovement(other, out);
                if (other.getFlags().isUpdateRequired()) {
                    PlayerUpdating.updateState(other, player, block, false,
                            false);
                }
            } else {
                out.writeBit(true);
                out.writeBits(2, 3);
                i.remove();
            }
        }

        int added = 0;

        // Update the local player list.
        for (int i = 0; i < World.getPlayers().getCapacity(); i++) {
            if (added == 15 || player.getLocalPlayers().size() >= 255) {

                // Player limit has been reached.
                break;
            }
            Player other = World.getPlayers().get(i);
            if (other == null || other == player || other.getSession()
                    .getStage() != Session.Stage.LOGGED_IN) {
                continue;
            }
            if (!player.getLocalPlayers().contains(other) && other
                    .getPosition().isViewableFrom(player.getPosition())) {
                added++;
                player.getLocalPlayers().add(other);
                PlayerUpdating.addPlayer(out, player, other);
                PlayerUpdating.updateState(other, player, block, true, false);

            }
        }

        // Append the attributes block to the main packet.
        if (block.getBuffer().position() > 0) {
            out.writeBits(11, 2047);
            out.finishBitAccess();
            out.writeBytes(block.getBuffer());
        } else {
            out.finishBitAccess();
        }

        // Finish the packet and send it.
        out.endVarShort();
        out.sendPacket();
    }

    /**
     * Appends the state of a player's chat to a buffer.
     * 
     * @param player
     *            the player.
     * @param out
     *            the buffer.
     */
    public static void appendChat(Player player, ProtocolBuffer out) {
        out.writeShort(
                ((player.getChatColor() & 0xff) << 8) + (player
                        .getChatEffects() & 0xff),
                ProtocolBuffer.ByteOrder.LITTLE);
        out.writeByte(player.getRights().getProtocolValue());
        out.writeByte(player.getChatText().length, ProtocolBuffer.ValueType.C);
        out.writeBytesReverse(player.getChatText());
    }

    /**
     * Appends the state of a player's appearance to a buffer.
     * 
     * @param player
     *            the player.
     * @param out
     *            the buffer.
     */
    public static void appendAppearance(Player player, ProtocolBuffer out) {
        ProtocolBuffer block = new ProtocolBuffer(128);

        block.writeByte(player.getGender());
        block.writeByte(player.getHeadIcon());
        block.writeByte(player.getSkullIcon());

        if (player.getNpcAppearanceId() == -1) {

            if (player.getEquipment().getContainer()
                    .getItemId(Utility.EQUIPMENT_SLOT_HEAD) > 1) {
                block.writeShort(0x200 + player.getEquipment().getContainer()
                        .getItemId(Utility.EQUIPMENT_SLOT_HEAD));
            } else {
                block.writeByte(0);
            }

            if (player.getEquipment().getContainer()
                    .getItemId(Utility.EQUIPMENT_SLOT_CAPE) > 1) {
                block.writeShort(0x200 + player.getEquipment().getContainer()
                        .getItemId(Utility.EQUIPMENT_SLOT_CAPE));
            } else {
                block.writeByte(0);
            }

            if (player.getEquipment().getContainer()
                    .getItemId(Utility.EQUIPMENT_SLOT_AMULET) > 1) {
                block.writeShort(0x200 + player.getEquipment().getContainer()
                        .getItemId(Utility.EQUIPMENT_SLOT_AMULET));
            } else {
                block.writeByte(0);
            }

            if (player.getEquipment().getContainer()
                    .getItemId(Utility.EQUIPMENT_SLOT_WEAPON) > 1) {
                block.writeShort(0x200 + player.getEquipment().getContainer()
                        .getItemId(Utility.EQUIPMENT_SLOT_WEAPON));
            } else {
                block.writeByte(0);
            }

            if (player.getEquipment().getContainer()
                    .getItemId(Utility.EQUIPMENT_SLOT_CHEST) > 1) {
                block.writeShort(0x200 + player.getEquipment().getContainer()
                        .getItemId(Utility.EQUIPMENT_SLOT_CHEST));
            } else {
                block.writeShort(0x100 + player.getAppearance()[Utility.APPEARANCE_SLOT_CHEST]);
            }

            if (player.getEquipment().getContainer()
                    .getItemId(Utility.EQUIPMENT_SLOT_SHIELD) > 1) {
                block.writeShort(0x200 + player.getEquipment().getContainer()
                        .getItemId(Utility.EQUIPMENT_SLOT_SHIELD));
            } else {
                block.writeByte(0);
            }

            if (player.getEquipment().getContainer()
                    .getItemId(Utility.EQUIPMENT_SLOT_CHEST) > 1) {

                if (!player.getEquipment().getContainer()
                        .getItem(Utility.EQUIPMENT_SLOT_CHEST).getDefinition()
                        .isPlatebody()) {
                    block.writeShort(0x100 + player.getAppearance()[Utility.APPEARANCE_SLOT_ARMS]);
                } else {
                    block.writeByte(0);
                }
            } else {
                block.writeShort(0x100 + player.getAppearance()[Utility.APPEARANCE_SLOT_ARMS]);
            }

            if (player.getEquipment().getContainer()
                    .getItemId(Utility.EQUIPMENT_SLOT_LEGS) > 1) {
                block.writeShort(0x200 + player.getEquipment().getContainer()
                        .getItemId(Utility.EQUIPMENT_SLOT_LEGS));
            } else {
                block.writeShort(0x100 + player.getAppearance()[Utility.APPEARANCE_SLOT_LEGS]);
            }

            if (player.getEquipment().getContainer()
                    .getItemId(Utility.EQUIPMENT_SLOT_HEAD) > 1 && player
                    .getEquipment().getContainer()
                    .getItem(Utility.EQUIPMENT_SLOT_HEAD).getDefinition()
                    .isFullHelm()) {
                block.writeByte(0);
            } else {
                block.writeShort(0x100 + player.getAppearance()[Utility.APPEARANCE_SLOT_HEAD]);
            }

            if (player.getEquipment().getContainer()
                    .getItemId(Utility.EQUIPMENT_SLOT_HANDS) > 1) {
                block.writeShort(0x200 + player.getEquipment().getContainer()
                        .getItemId(Utility.EQUIPMENT_SLOT_HANDS));
            } else {
                block.writeShort(0x100 + player.getAppearance()[Utility.APPEARANCE_SLOT_HANDS]);
            }

            if (player.getEquipment().getContainer()
                    .getItemId(Utility.EQUIPMENT_SLOT_FEET) > 1) {
                block.writeShort(0x200 + player.getEquipment().getContainer()
                        .getItemId(Utility.EQUIPMENT_SLOT_FEET));
            } else {
                block.writeShort(0x100 + player.getAppearance()[Utility.APPEARANCE_SLOT_FEET]);
            }

            if (player.getGender() == Utility.GENDER_MALE) {
                if (player.getEquipment().getContainer()
                        .getItemId(Utility.EQUIPMENT_SLOT_HEAD) > 1 && !player
                        .getEquipment().getContainer()
                        .getItem(Utility.EQUIPMENT_SLOT_HEAD).getDefinition()
                        .isFullHelm() || player.getEquipment().getContainer()
                        .isSlotFree(Utility.EQUIPMENT_SLOT_HEAD)) {
                    block.writeShort(0x100 + player.getAppearance()[Utility.APPEARANCE_SLOT_BEARD]);
                } else {
                    block.writeByte(0);
                }
            }

        } else {
            block.writeShort(-1);
            block.writeShort(player.getNpcAppearanceId());
        }

        block.writeByte(player.getColors()[0]);
        block.writeByte(player.getColors()[1]);
        block.writeByte(player.getColors()[2]);
        block.writeByte(player.getColors()[3]);
        block.writeByte(player.getColors()[4]);

        block.writeShort(player.getUpdateAnimation().getStandingAnimation() == -1 ? 0x328
                : player.getUpdateAnimation().getStandingAnimation());
        block.writeShort(0x337);
        block.writeShort(player.getUpdateAnimation().getWalkingAnimation() == -1 ? 0x333
                : player.getUpdateAnimation().getWalkingAnimation());
        block.writeShort(0x334);
        block.writeShort(0x335);
        block.writeShort(0x336);
        block.writeShort(player.getUpdateAnimation().getRunningAnimation() == -1 ? 0x338
                : player.getUpdateAnimation().getRunningAnimation());

        block.writeLong(player.getUsernameHash());
        block.writeByte(player.getCombatLevel());
        block.writeShort(0);

        out.writeByte(block.getBuffer().position(), ProtocolBuffer.ValueType.C);
        out.writeBytes(block.getBuffer());
    }

    /**
     * Adds a player to the local player list of another player.
     * 
     * @param out
     *            the packet to write to.
     * @param player
     *            the host player.
     * @param other
     *            the player being added.
     */
    public static void addPlayer(ProtocolBuffer out, Player player, Player other) {
        out.writeBits(11, other.getSlot()); // Server slot.
        out.writeBit(true); // Yes, an update is required.
        out.writeBit(true); // Discard walking queue(?)

        // Write the relative position.
        Position delta = Utility.delta(player.getPosition(),
                other.getPosition());
        out.writeBits(5, delta.getY());
        out.writeBits(5, delta.getX());
    }

    /**
     * Updates movement for this local player. The difference between this
     * method and the other player method is that this will make use of sector
     * 2,3 to place the player in a specific position while sector 2,3 is not
     * present in updating of other players (it simply flags local list removal
     * instead).
     * 
     * @param player
     *            the player to update movement for.
     * @param out
     *            the packet to write to.
     */
    public static void updateLocalPlayerMovement(Player player,
            ProtocolBuffer out) {
        boolean updateRequired = player.getFlags().isUpdateRequired();
        if (player.isNeedsPlacement()) { // Do they need placement?

            out.writeBit(true); // Yes, there is an update.
            int posX = player.getPosition()
                    .getLocalX(player.getCurrentRegion());
            int posY = player.getPosition()
                    .getLocalY(player.getCurrentRegion());

            appendPlacement(out, posX, posY, player.getPosition().getZ(),
                    player.isResetMovementQueue(), updateRequired);

            // player.setNeedsPlacement(false);
        } else { // No placement update, check for movement.
            int pDir = player.getPrimaryDirection();
            int sDir = player.getSecondaryDirection();
            if (pDir != -1) { // If they moved.
                out.writeBit(true); // Yes, there is an update.
                if (sDir != -1) { // If they ran.
                    appendRun(out, pDir, sDir, updateRequired);
                } else { // Movement but no running - they walked.
                    appendWalk(out, pDir, updateRequired);
                }
            } else { // No movement.
                if (updateRequired) { // Does the state need to be updated?
                    out.writeBit(true); // Yes, there is an update.
                    appendStand(out);
                } else { // No update whatsoever.
                    out.writeBit(false);
                }
            }
        }
    }

    /**
     * Updates the movement of a player for another player (does not make use of
     * sector 2,3).
     * 
     * @param player
     *            the player to update movement for.
     * @param out
     *            the packet to write to.
     */
    public static void updateOtherPlayerMovement(Player player,
            ProtocolBuffer out) {
        boolean updateRequired = player.getFlags().isUpdateRequired();
        int pDir = player.getPrimaryDirection();
        int sDir = player.getSecondaryDirection();
        if (pDir != -1) { // If they moved.
            out.writeBit(true); // Yes, there is an update.
            if (sDir != -1) { // If they ran.
                appendRun(out, pDir, sDir, updateRequired);
            } else { // Movement but no running - they walked.
                appendWalk(out, pDir, updateRequired);
            }
        } else { // No movement.
            if (updateRequired) { // Does the state need to be updated?
                out.writeBit(true); // Yes, there is an update.
                appendStand(out);
            } else { // No update whatsoever.
                out.writeBit(false);
            }
        }
    }

    /**
     * Updates the state of a player.
     * 
     * @param player
     *            the player being constructed.
     * @param thisPlayer
     *            the player being constructed for.
     * @param block
     *            the update block.
     */
    public static void updateState(Player player, Player thisPlayer,
            ProtocolBuffer block, boolean forceAppearance, boolean noChat)
            throws Exception {

        // Block if no update is required.
        if (!player.getFlags().isUpdateRequired() && !forceAppearance) {
            return;
        }

        // Send the cached update block if we are able to.
        if (player.getCachedUpdateBlock() != null && player != thisPlayer && !forceAppearance && !noChat) {
            block.getBuffer().put(player.getCachedUpdateBlock().array());
            return;
        }

        // Create the buffer we are going to cache.
        ProtocolBuffer cachedBuffer = new ProtocolBuffer(300);

        // First we build the update mask.
        int mask = 0x0;

        if (player.getFlags().get(Flag.GRAPHICS)) {
            mask |= 0x100;
        }
        if (player.getFlags().get(Flag.ANIMATION)) {
            mask |= 8;
        }
        if (player.getFlags().get(Flag.FORCED_CHAT)) {
            mask |= 4;
        }
        if (player.getFlags().get(Flag.CHAT) && !noChat) {
            mask |= 0x80;
        }
        if (player.getFlags().get(Flag.APPEARANCE) || forceAppearance) {
            mask |= 0x10;
        }
        if (player.getFlags().get(Flag.FACE_ENTITY)) {
            mask |= 1;
        }
        if (player.getFlags().get(Flag.FACE_COORDINATE)) {
            mask |= 2;
        }
        if (player.getFlags().get(Flag.HIT)) {
            mask |= 0x20;
        }
        if (player.getFlags().get(Flag.HIT_2)) {
            mask |= 0x200;
        }

        // Then we write the built mask.
        if (mask >= 0x100) {
            mask |= 0x40;
            cachedBuffer.writeShort(mask, ProtocolBuffer.ByteOrder.LITTLE);
        } else {
            cachedBuffer.writeByte(mask);
        }

        // Then we add the attribute data to the block.
        if (player.getFlags().get(Flag.GRAPHICS)) {
            appendGfx(player, cachedBuffer);
        }
        if (player.getFlags().get(Flag.ANIMATION)) {
            appendAnimation(player, cachedBuffer);
        }
        if (player.getFlags().get(Flag.FORCED_CHAT)) {
            appendForcedChat(player, cachedBuffer);
        }
        if (player.getFlags().get(Flag.CHAT) && !noChat) {
            appendChat(player, cachedBuffer);
        }
        if (player.getFlags().get(Flag.FACE_ENTITY)) {
            appendFaceEntity(player, cachedBuffer);
        }
        if (player.getFlags().get(Flag.APPEARANCE) || forceAppearance) {
            appendAppearance(player, cachedBuffer);
        }
        if (player.getFlags().get(Flag.FACE_COORDINATE)) {
            appendFaceCoordinate(player, cachedBuffer);
        }
        if (player.getFlags().get(Flag.HIT)) {
            appendPrimaryHit(player, cachedBuffer);
        }
        if (player.getFlags().get(Flag.HIT_2)) {
            appendSecondaryHit(player, cachedBuffer);
        }

        // Cache the block if possible.
        if (player != thisPlayer && !forceAppearance && !noChat) {
            player.setCachedUpdateBlock(cachedBuffer.getBuffer());

        }

        // Add the cached block to the update block.
        block.writeBytes(cachedBuffer.getBuffer());
    }

    /**
     * Update the forced chat block.
     * 
     * @param player
     *            the player to update for.
     * @param out
     *            the packet to write to.
     */
    private static void appendForcedChat(Player player, ProtocolBuffer out) {
        out.writeString(player.getForcedText());
    }

    /**
     * Update the face entity block.
     * 
     * @param player
     *            the player to update for.
     * @param out
     *            the packet to write to.
     */
    private static void appendFaceEntity(Player player, ProtocolBuffer out) {
        out.writeShort(player.getFaceIndex(), ByteOrder.LITTLE);
    }

    /**
     * Update the face coordinate block.
     * 
     * @param player
     *            the player to update for.
     * @param out
     *            the packet to write to.
     */
    private static void appendFaceCoordinate(Player player, ProtocolBuffer out) {
        out.writeShort(player.getFaceCoordinates().getX(), ValueType.A,
                ByteOrder.LITTLE);
        out.writeShort(player.getFaceCoordinates().getY(), ByteOrder.LITTLE);
    }

    /**
     * Update the animation block.
     * 
     * @param player
     *            the player to update for.
     * @param out
     *            the packet to write to.
     */
    private static void appendAnimation(Player player, ProtocolBuffer out) {
        out.writeShort(player.getAnimation().getId(), ByteOrder.LITTLE);
        out.writeByte(player.getAnimation().getDelay(), ValueType.C);
    }

    /**
     * Update the primary hitmark block.
     * 
     * @param player
     *            the player to update for.
     * @param out
     *            the packet to write to.
     */
    private static void appendPrimaryHit(Player player, ProtocolBuffer out)
            throws Exception {
        out.writeByte(player.getPrimaryHit().getDamage());
        out.writeByte(player.getPrimaryHit().getType().ordinal(), ValueType.A);

        if (!player.isDead()) {
            if (player.getSkills()[Skills.HITPOINTS].getLevel() <= 0) {
                player.getSkills()[Skills.HITPOINTS].setLevel(0, true);
                player.setDead(true);
                TaskFactory.submit(new PlayerDeath(player));
            }
        }

        out.writeByte(player.getSkills()[Skills.HITPOINTS].getLevel(),
                ValueType.C);
        out.writeByte(player.getSkills()[Skills.HITPOINTS]
                .getLevelForExperience());
    }

    /**
     * Update the secondary hitmark block.
     * 
     * @param player
     *            the player to update for.
     * @param out
     *            the packet to write to.
     */
    private static void appendSecondaryHit(Player player, ProtocolBuffer out)
            throws Exception {
        out.writeByte(player.getSecondaryHit().getDamage());
        out.writeByte(player.getSecondaryHit().getType().ordinal(), ValueType.S);

        if (!player.isDead()) {
            if (player.getSkills()[Skills.HITPOINTS].getLevel() <= 0) {
                player.getSkills()[Skills.HITPOINTS].setLevel(0, true);
                player.setDead(true);
                TaskFactory.submit(new PlayerDeath(player));
            }
        }

        out.writeByte(player.getSkills()[Skills.HITPOINTS].getLevel());
        out.writeByte(
                player.getSkills()[Skills.HITPOINTS].getLevelForExperience(),
                ValueType.C);
    }

    /**
     * Update the graphics block.
     * 
     * @param player
     *            the player to update for.
     * @param out
     *            the packet to write to.
     */
    private static void appendGfx(Player player, ProtocolBuffer out) {
        out.writeShort(player.getGfx().getId(), ByteOrder.LITTLE);
        out.writeInt(player.getGfx().getHeight());
    }

    /**
     * Appends the stand version of the movement section of the update packet
     * (sector 2,0). Appending this (instead of just a zero bit) automatically
     * assumes that there is a required attribute update afterwards.
     * 
     * @param out
     *            the buffer to append to.
     */
    public static void appendStand(ProtocolBuffer out) {
        out.writeBits(2, 0); // 0 - no movement.
    }

    /**
     * Appends the walk version of the movement section of the update packet
     * (sector 2,1).
     * 
     * @param out
     *            the buffer to append to
     * @param direction
     *            the walking direction
     * @param attributesUpdate
     *            whether or not a player attributes update is required
     */
    public static void appendWalk(ProtocolBuffer out, int direction,
            boolean attributesUpdate) {
        out.writeBits(2, 1); // 1 - walking.

        /** Append the actual sector. */
        out.writeBits(3, direction);
        out.writeBit(attributesUpdate);
    }

    /**
     * Appends the walk version of the movement section of the update packet
     * (sector 2,2).
     * 
     * @param out
     *            the buffer to append to.
     * @param direction
     *            the walking direction.
     * @param direction2
     *            the running direction.
     * @param attributesUpdate
     *            whether or not a player attributes update is required.
     */
    public static void appendRun(ProtocolBuffer out, int direction,
            int direction2, boolean attributesUpdate) {
        out.writeBits(2, 2); // 2 - running.

        // Append the actual sector.
        out.writeBits(3, direction);
        out.writeBits(3, direction2);
        out.writeBit(attributesUpdate);
    }

    /**
     * Appends the player placement version of the movement section of the
     * update packet (sector 2,3). Note that by others this was previously
     * called the "teleport update".
     * 
     * @param out
     *            the buffer to append to.
     * @param localX
     *            the local X coordinate.
     * @param localY
     *            the local Y coordinate.
     * @param z
     *            the Z coordinate.
     * @param discardMovementQueue
     *            whether or not the client should discard the movement queue.
     * @param attributesUpdate
     *            whether or not a plater attributes update is required.
     */
    public static void appendPlacement(ProtocolBuffer out, int localX,
            int localY, int z, boolean discardMovementQueue,
            boolean attributesUpdate) {
        out.writeBits(2, 3); // 3 - placement.

        // Append the actual sector.
        out.writeBits(2, z);
        out.writeBit(discardMovementQueue);
        out.writeBit(attributesUpdate);
        out.writeBits(7, localY);
        out.writeBits(7, localX);
    }
}
