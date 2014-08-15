package com.asteria.engine.net.packet.impl;

import com.asteria.engine.net.ProtocolBuffer;
import com.asteria.engine.net.ProtocolBuffer.ValueType;
import com.asteria.engine.net.packet.PacketDecoder;
import com.asteria.engine.net.packet.PacketOpcodeHeader;
import com.asteria.world.entity.player.Player;
import com.asteria.world.entity.player.skill.SkillEvent;
import com.asteria.world.item.Item;
import com.asteria.world.item.ItemDefinition;
import com.asteria.world.item.ground.GroundItem;
import com.asteria.world.item.ground.GroundItemManager;
import com.asteria.world.map.Position;

/**
 * Sent when the player drops an item.
 * 
 * @author lare96
 */
@PacketOpcodeHeader({ 87 })
public class DecodeDropItemPacket extends PacketDecoder {

    // TODO: Proper validation.

    @Override
    public void decode(Player player, ProtocolBuffer buf) {
        int item = buf.readShort(false, ValueType.A);
        buf.readByte(false);
        buf.readByte(false);
        int slot = buf.readShort(false, ValueType.A);

        if (slot < 0 || item < 0) {
            return;
        }

        SkillEvent.fireSkillEvents(player);

        if (player.getInventory().contains(item)) {
            int amount = ItemDefinition.getDefinitions()[item].isStackable() ? amount = player
                .getInventory().totalAmount(item)
                    : 1;

            player.getInventory().remove(new Item(item, amount), slot);
            final Position itemLocation = new Position(player.getPosition()
                    .getX(), player.getPosition().getY(), player.getPosition()
                    .getZ());
            GroundItemManager.register(new GroundItem(new Item(item, amount),
                    itemLocation, player));
        }
    }
}
