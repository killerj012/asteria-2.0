package server.core.net.packet.impl;

import server.core.net.packet.PacketBuffer.ReadBuffer;
import server.core.net.packet.PacketBuffer.ValueType;
import server.core.net.packet.PacketDecoder;
import server.core.net.packet.PacketOpcodeHeader;
import server.world.World;
import server.world.entity.player.Player;
import server.world.entity.player.skill.SkillEvent;
import server.world.item.Item;
import server.world.item.ItemDefinition;
import server.world.item.ground.GroundItem;
import server.world.map.Position;

/**
 * Sent when the player drops an item.
 * 
 * @author lare96
 */
@PacketOpcodeHeader({ 87 })
public class DecodeDropItemPacket extends PacketDecoder {

    @Override
    public void decode(Player player, ReadBuffer in) {
        int item = in.readShort(false, ValueType.A);
        in.readByte(false);
        in.readByte(false);
        int slot = in.readShort(false, ValueType.A);

        SkillEvent.fireSkillEvents(player);

        if (player.getInventory().getContainer().contains(item)) {
            int amount = ItemDefinition.getDefinitions()[item].isStackable() ? amount = player
                    .getInventory().getContainer().getCount(item)
                    : 1;

            player.getInventory().deleteItemSlot(new Item(item, amount), slot);
            final Position itemLocation = new Position(player.getPosition()
                    .getX(), player.getPosition().getY(), player.getPosition()
                    .getZ());
            World.getGroundItems()
                    .register(
                            new GroundItem(new Item(item, amount),
                                    itemLocation, player));
        }
    }
}
