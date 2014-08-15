package com.asteria.engine.net.packet.impl;

import com.asteria.engine.net.ProtocolBuffer;
import com.asteria.engine.net.ProtocolBuffer.ByteOrder;
import com.asteria.engine.net.ProtocolBuffer.ValueType;
import com.asteria.engine.net.packet.PacketDecoder;
import com.asteria.engine.net.packet.PacketOpcodeHeader;
import com.asteria.world.entity.player.Player;
import com.asteria.world.entity.player.content.FoodConsumable;
import com.asteria.world.entity.player.content.PotionConsumable;
import com.asteria.world.entity.player.skill.SkillEvent;
import com.asteria.world.item.Item;
import com.asteria.world.item.ItemDefinition;

/**
 * Sent when the player uses the first click item option.
 * 
 * @author lare96
 */
@PacketOpcodeHeader({ 122 })
public class DecodeClickItemPacket extends PacketDecoder {

    @Override
    public void decode(Player player, ProtocolBuffer buf) {
        int container = buf.readShort(true, ValueType.A, ByteOrder.LITTLE);
        int slot = buf.readShort(false, ValueType.A);
        int id = buf.readShort(false, ByteOrder.LITTLE);

        if (slot < 0 || container < 0 || id < 0
                || id > ItemDefinition.getDefinitions().length) {
            return;
        }

        SkillEvent.fireSkillEvents(player);
        player.getCombatBuilder().resetAttackTimer();

        if (container == 3214) {
            Item item = player.getInventory().get(slot);

            if (item == null || item.getId() != id) {
                return;
            }

            if (FoodConsumable.consume(player, item, slot)) {
                return;
            }

            if (PotionConsumable.consume(player, item, slot)) {
                return;
            }
        }
    }

}