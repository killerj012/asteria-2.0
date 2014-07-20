package com.asteria.engine.net.packet.impl;

import com.asteria.engine.net.ProtocolBuffer;
import com.asteria.engine.net.ProtocolBuffer.ByteOrder;
import com.asteria.engine.net.ProtocolBuffer.ValueType;
import com.asteria.engine.net.packet.PacketDecoder;
import com.asteria.engine.net.packet.PacketOpcodeHeader;
import com.asteria.world.World;
import com.asteria.world.entity.combat.magic.CombatSpell;
import com.asteria.world.entity.combat.magic.CombatSpells;
import com.asteria.world.entity.npc.Npc;
import com.asteria.world.entity.npc.NpcDefinition;
import com.asteria.world.entity.npc.dialogue.DialogueSender;
import com.asteria.world.entity.player.Player;
import com.asteria.world.entity.player.minigame.Minigame;
import com.asteria.world.entity.player.minigame.MinigameFactory;
import com.asteria.world.map.Location;
import com.asteria.world.map.Position;
import com.asteria.world.shop.Shop;

/**
 * Sent when the player attacks an npc using melee/range, attacks a npc using
 * magic, first clicks an npc or second clicks an npc.
 * 
 * @author lare96
 */
@PacketOpcodeHeader({ 72, 131, 155, 17 })
public class DecodeNpcActionPacket extends PacketDecoder {

    /** The various packet opcodes. */
    public static final int ATTACK_NPC = 72, MAGE_NPC = 131, FIRST_CLICK = 155,
            SECOND_CLICK = 17;

    /** The index of this npc. */
    private int index;

    /** The instance of the npc being interacted with. */
    private Npc interact;

    @Override
    public void decode(final Player player, ProtocolBuffer buf) {

        switch (player.getSession().getPacketOpcode()) {
        case ATTACK_NPC:
            index = buf.readShort(false, ValueType.A);

            if (index < 0) {
                return;
            }

            interact = World.getNpcs().get(index);

            if (interact == null) {
                return;
            }

            if (!NpcDefinition.getDefinitions()[interact.getNpcId()]
                    .isAttackable()) {
                return;
            }

            for (Minigame minigame : MinigameFactory.getMinigames().values()) {
                if (minigame.inMinigame(player)) {
                    if (!minigame.canHit(player, interact)) {
                        return;
                    }
                }
            }

            if (!Location.inMultiCombat(player)
                    && player.getCombatBuilder().isBeingAttacked()
                    && player.getCombatBuilder().getLastAttacker() != interact) {
                player.getPacketBuilder().sendMessage(
                        "You are already under attack!");
                return;
            }

            player.getCombatBuilder().attack(interact);
            break;
        case MAGE_NPC:
            index = buf.readShort(true, ValueType.A, ByteOrder.LITTLE);
            int spellId = buf.readShort(true, ValueType.A);

            if (index < 0 || spellId < 0) {
                return;
            }

            interact = World.getNpcs().get(index);
            CombatSpell spell = CombatSpells.getSpell(spellId).getSpell();

            if (interact == null) {
                return;
            }

            for (Minigame minigame : MinigameFactory.getMinigames().values()) {
                if (minigame.inMinigame(player)) {
                    if (!minigame.canHit(player, interact)) {
                        return;
                    }
                }
            }

            if (!NpcDefinition.getDefinitions()[interact.getNpcId()]
                    .isAttackable()) {
                return;
            }

            if (!Location.inMultiCombat(player)
                    && player.getCombatBuilder().isBeingAttacked()
                    && player.getCombatBuilder().getLastAttacker() != interact) {
                player.getPacketBuilder().sendMessage(
                        "You are already under attack!");
                return;
            }

            player.setAutocastSpell(null);
            player.setAutocast(false);
            player.getPacketBuilder().sendConfig(108, 0);
            player.setCastSpell(spell);
            player.getCombatBuilder().attack(interact);
            break;
        case FIRST_CLICK:
            index = buf.readShort(true, ByteOrder.LITTLE);

            if (index < 0) {
                return;
            }

            interact = World.getNpcs().get(index);

            if (interact == null) {
                return;
            }

            player.getMovementQueueListener().append(new Runnable() {
                @Override
                public void run() {
                    if (player.getPosition().withinDistance(
                            new Position(interact.getPosition().getX(),
                                    interact.getPosition().getY(), interact
                                            .getPosition().getZ()), 1)) {
                        player.facePosition(interact.getPosition());
                        interact.facePosition(player.getPosition());

                        switch (interact.getNpcId()) {
                        case 460:
                            DialogueSender.sendHomeWizardDialogue(player);
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
            index = buf.readShort(false, ValueType.A, ByteOrder.LITTLE);

            if (index < 0) {
                return;
            }

            interact = World.getNpcs().get(index);

            if (interact == null) {
                return;
            }

            player.getMovementQueueListener().append(new Runnable() {
                @Override
                public void run() {
                    if (player.getPosition().withinDistance(
                            new Position(interact.getPosition().getX(),
                                    interact.getPosition().getY(), interact
                                            .getPosition().getZ()), 1)) {
                        player.facePosition(interact.getPosition());
                        interact.facePosition(player.getPosition());

                        switch (interact.getNpcId()) {

                        }
                    }
                }
            });
            break;
        }
    }
}
