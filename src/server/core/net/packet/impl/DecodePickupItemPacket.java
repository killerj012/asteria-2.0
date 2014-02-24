package server.core.net.packet.impl;

import server.core.net.buffer.PacketBuffer.ByteOrder;
import server.core.net.buffer.PacketBuffer.ReadBuffer;
import server.core.net.packet.PacketDecoder;
import server.world.entity.player.Player;
import server.world.entity.player.skill.SkillEvent;
import server.world.item.Item;
import server.world.item.ground.GroundItem;
import server.world.map.Position;

/**
 * Sent when the player attempts to pickup an item.
 * 
 * @author lare96
 */
public class DecodePickupItemPacket extends PacketDecoder {

    @Override
    public void decode(final Player player, ReadBuffer in) {
        final int itemY = in.readShort(ByteOrder.LITTLE);
        final int itemId = in.readShort(false);
        final int itemX = in.readShort(ByteOrder.LITTLE);

        SkillEvent.fireSkillEvents(player);

        player.getMovementQueueListener().submit(new Runnable() {
            @Override
            public void run() {
                if (player.getPosition().equals(new Position(itemX, itemY, player.getPosition().getZ()))) {
                    GroundItem worldItem = GroundItem.getRegisterable().searchDatabase(new GroundItem(new Item(itemId, 1), new Position(itemX, itemY, player.getPosition().getZ()), player));

                    if (worldItem == null) {
                        player.getPacketBuilder().sendMessage("The item you are trying to pickup is not in the item database.");
                        return;
                    }

                    if (!player.getInventory().getContainer().hasRoomFor(new Item(itemId, worldItem.getItem().getAmount()))) {
                        player.getPacketBuilder().sendMessage("You don't have enough free inventory space to pickup this item.");
                        return;
                    }

                    GroundItem.getRegisterable().pickupDatabaseItem(worldItem, player);
                }
            }
        });
    }

    @Override
    public int[] opcode() {
        return new int[] { 236 };
    }
}
