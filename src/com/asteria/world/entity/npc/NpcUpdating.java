package com.asteria.world.entity.npc;

import java.util.Iterator;

import com.asteria.engine.net.ProtocolBuffer;
import com.asteria.engine.net.ProtocolBuffer.ByteOrder;
import com.asteria.engine.net.ProtocolBuffer.ValueType;
import com.asteria.engine.task.TaskFactory;
import com.asteria.util.Utility;
import com.asteria.world.World;
import com.asteria.world.entity.UpdateFlags.Flag;
import com.asteria.world.entity.player.Player;
import com.asteria.world.map.Position;

/**
 * Provides static utility methods for updating {@link Npc}s.
 * 
 * @author blakeman8192
 * @author lare96
 */
public class NpcUpdating {

    /**
     * Updates all {@link Npc}s for the argued {@link Player}.
     * 
     * @param player
     *            the player to update npcs for.
     */
    public static void update(Player player) throws Exception {
        ProtocolBuffer out = new ProtocolBuffer(2048);
        ProtocolBuffer block = new ProtocolBuffer(1024);

        // Initialize the update packet.
        out.buildVarShort(65, player.getSession());
        out.startBitAccess();

        // Update the NPCs in the local list.
        out.writeBits(8, player.getLocalNpcs().size());
        for (Iterator<Npc> i = player.getLocalNpcs().iterator(); i.hasNext();) {
            Npc npc = i.next();
            if (npc.getPosition().isViewableFrom(player.getPosition())) {
                NpcUpdating.updateNpcMovement(out, npc);
                if (npc.getFlags().isUpdateRequired()) {
                    NpcUpdating.updateState(block, npc);
                }
            } else {

                // Remove the NPC from the local list.
                out.writeBit(true);
                out.writeBits(2, 3);
                i.remove();
            }
        }

        // Update the local NPC list itself.
        int added = 0;
        for (Npc npc : World.getNpcs()) {
            if (npc == null || added == 15 || player.getLocalNpcs().size() >= 255 || player
                    .getLocalNpcs().contains(npc)) {
                continue;
            }

            if (npc.getPosition().isViewableFrom(player.getPosition())) {
                npc.getFlags().flag(Flag.APPEARANCE);
                player.getLocalNpcs().add(npc);
                addNpc(out, player, npc);

                if (npc.getFlags().isUpdateRequired()) {
                    NpcUpdating.updateState(block, npc);
                }
                added++;
            }
        }

        // Append the update block to the packet if need be.
        if (block.getBuffer().position() > 0) {
            out.writeBits(14, 16383);
            out.finishBitAccess();
            out.writeBytes(block.getBuffer());
        } else {
            out.finishBitAccess();
        }

        // Ship the packet out to the client.
        out.endVarShort();
        out.sendPacket();
    }

    /**
     * Adds the NPC to the client side local list.
     * 
     * @param out
     *            The buffer to write to.
     * @param player
     *            The player.
     * @param npc
     *            The NPC being added.
     */
    private static void addNpc(ProtocolBuffer out, Player player, Npc npc) {
        out.writeBits(14, npc.getSlot());
        Position delta = Utility.delta(player.getPosition(), npc.getPosition());
        out.writeBits(5, delta.getY());
        out.writeBits(5, delta.getX());
        out.writeBit(npc.getFlags().isUpdateRequired());
        out.writeBits(12, npc.getNpcId());
        out.writeBit(true);
    }

    /**
     * Updates the movement of a NPC for this cycle.
     * 
     * @param out
     *            The buffer to write to.
     * @param npc
     *            The NPC to update.
     */
    private static void updateNpcMovement(ProtocolBuffer out, Npc npc) {
        if (npc.getPrimaryDirection() == -1) {
            if (npc.getFlags().isUpdateRequired()) {
                out.writeBit(true);
                out.writeBits(2, 0);
            } else {
                out.writeBit(false);
            }
        } else {
            out.writeBit(true);
            out.writeBits(2, 1);
            out.writeBits(3, npc.getPrimaryDirection());

            if (npc.getFlags().isUpdateRequired()) {
                out.writeBit(true);
            } else {
                out.writeBit(false);
            }
        }
    }

    /**
     * Updates the state of the NPC to the given update block.
     * 
     * @param block
     *            The update block to append to.
     * @param npc
     *            The NPC to update.
     */
    private static void updateState(ProtocolBuffer block, Npc npc)
            throws Exception {
        int mask = 0x0;

        // NPC update masks.
        if (npc.getFlags().get(Flag.ANIMATION)) {
            mask |= 0x10;
        }
        if (npc.getFlags().get(Flag.HIT_2)) {
            mask |= 8;
        }
        if (npc.getFlags().get(Flag.GRAPHICS)) {
            mask |= 0x80;
        }
        if (npc.getFlags().get(Flag.FACE_ENTITY)) {
            mask |= 0x20;
        }
        if (npc.getFlags().get(Flag.FORCED_CHAT)) {
            mask |= 1;
        }
        if (npc.getFlags().get(Flag.HIT)) {
            mask |= 0x40;
        }
        if (npc.getFlags().get(Flag.FACE_COORDINATE)) {
            mask |= 4;
        }

        // Write the update masks.
        if (mask >= 0x100) {
            mask |= 0x40;
            block.writeShort(mask, ProtocolBuffer.ByteOrder.LITTLE);
        } else {
            block.writeByte(mask);
        }

        // Append the NPC update blocks.
        if (npc.getFlags().get(Flag.ANIMATION)) {
            appendAnimation(block, npc);
        }
        if (npc.getFlags().get(Flag.HIT_2)) {
            appendSecondaryHit(block, npc);
        }
        if (npc.getFlags().get(Flag.GRAPHICS)) {
            appendGfxUpdate(block, npc);
        }
        if (npc.getFlags().get(Flag.FACE_ENTITY)) {
            appendFaceEntity(block, npc);
        }
        if (npc.getFlags().get(Flag.FORCED_CHAT)) {
            appendForcedChat(block, npc);
        }
        if (npc.getFlags().get(Flag.HIT)) {
            appendPrimaryHit(block, npc);
        }
        if (npc.getFlags().get(Flag.FACE_COORDINATE)) {
            appendFaceCoordinate(block, npc);
        }
    }

    /**
     * Update the GFX block.
     * 
     * @param out
     *            the packet to write to.
     * @param npc
     *            the npc to append this update for.
     */
    private static void appendGfxUpdate(ProtocolBuffer out, Npc npc) {
        out.writeShort(npc.getGfx().getId());
        out.writeInt(npc.getGfx().getHeight());
    }

    /**
     * Update the secondary hit block.
     * 
     * @param out
     *            the packet to write to.
     * @param npc
     *            the npc to append this update for.
     */
    private static void appendSecondaryHit(ProtocolBuffer out, Npc npc)
            throws Exception {
        if (!npc.isDead()) {
            if (npc.getCurrentHP() <= 0) {
                npc.setCurrentHealth(0);
                TaskFactory.submit(new NpcDeath(npc));
            }
        }

        out.writeByte(npc.getSecondaryHit().getDamage(), ValueType.A);
        out.writeByte(npc.getSecondaryHit().getType().ordinal(), ValueType.C);
        out.writeByte(npc.getCurrentHP(), ValueType.A);
        out.writeByte(npc.getMaxHealth());
    }

    /**
     * Update the face entity block.
     * 
     * @param out
     *            the packet to write to.
     * @param npc
     *            the npc to append this update for.
     */
    private static void appendFaceEntity(ProtocolBuffer out, Npc npc) {
        out.writeShort(npc.getFaceIndex());
    }

    /**
     * Update the forced chat block.
     * 
     * @param out
     *            the packet to write to.
     * @param npc
     *            the npc to append this update for.
     */
    private static void appendForcedChat(ProtocolBuffer out, Npc npc) {
        out.writeString(npc.getForcedText());
    }

    /**
     * Update the primary hit block.
     * 
     * @param out
     *            the packet to write to.
     * @param npc
     *            the npc to append this update for.
     */
    private static void appendPrimaryHit(ProtocolBuffer out, Npc npc) {
        if (!npc.isDead()) {
            if (npc.getCurrentHP() <= 0) {
                npc.setCurrentHealth(0);
                TaskFactory.submit(new NpcDeath(npc));
            }
        }

        out.writeByte(npc.getPrimaryHit().getDamage(), ValueType.C);
        out.writeByte(npc.getPrimaryHit().getType().ordinal(), ValueType.S);
        out.writeByte(npc.getCurrentHP(), ValueType.S);
        out.writeByte(npc.getMaxHealth(), ValueType.C);
    }

    /**
     * Update the face coordinate block.
     * 
     * @param out
     *            the packet to write to.
     * @param npc
     *            the npc to append this update for.
     */
    private static void appendFaceCoordinate(ProtocolBuffer out, Npc npc) {
        out.writeShort(npc.getFaceCoordinates().getX(), ByteOrder.LITTLE);
        out.writeShort(npc.getFaceCoordinates().getY(), ByteOrder.LITTLE);
    }

    /**
     * Update the animation block.
     * 
     * @param out
     *            the packet to write to.
     * @param npc
     *            the npc to append this update for.
     */
    private static void appendAnimation(ProtocolBuffer out, Npc npc) {
        out.writeShort(npc.getAnimation().getId(), ByteOrder.LITTLE);
        out.writeByte(npc.getAnimation().getDelay());
    }
}
