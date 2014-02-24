package server.core.net.packet.impl;

import server.core.net.buffer.PacketBuffer.ByteOrder;
import server.core.net.buffer.PacketBuffer.ReadBuffer;
import server.core.net.buffer.PacketBuffer.ValueType;
import server.core.net.packet.PacketDecoder;
import server.world.entity.player.Player;
import server.world.entity.player.content.ConsumeFood;
import server.world.entity.player.content.ConsumeFood.Food;
import server.world.entity.player.skill.SkillEvent;
import server.world.entity.player.skill.impl.Prayer;
import server.world.entity.player.skill.impl.Prayer.PrayerItem;

/**
 * Sent when the player uses the first click item option.
 * 
 * @author lare96
 */
public class DecodeClickItemPacket extends PacketDecoder {

    @Override
    public void decode(Player player, ReadBuffer in) {
        in.readShort(true, ValueType.A, ByteOrder.LITTLE);
        int slot = in.readShort(false, ValueType.A);
        int id = in.readShort(false, ByteOrder.LITTLE);
        SkillEvent.fireSkillEvents(player);

        if (id != player.getInventory().getContainer().getIdBySlot(slot)) {
            return;
        }

        ConsumeFood.consume(player, Food.forId(id), slot);
        Prayer.getSingleton().buryItem(player, PrayerItem.getPrayerItem(id), slot);

        switch (id) {
        /** ... */
        }
    }

    @Override
    public int[] opcode() {
        return new int[] { 122 };
    }
}
