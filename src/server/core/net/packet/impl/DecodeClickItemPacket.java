package server.core.net.packet.impl;

import server.core.net.packet.PacketBuffer.ByteOrder;
import server.core.net.packet.PacketBuffer.ReadBuffer;
import server.core.net.packet.PacketBuffer.ValueType;
import server.core.net.packet.PacketDecoder;
import server.core.net.packet.PacketOpcodeHeader;
import server.world.entity.content.consume.FoodConsumable;
import server.world.entity.content.consume.PotionConsumable;
import server.world.entity.player.Player;
import server.world.entity.player.skill.SkillEvent;
import server.world.item.Item;
import server.world.item.container.InventoryContainer;

/**
 * Sent when the player uses the first click item option.
 * 
 * @author lare96
 */
@PacketOpcodeHeader({ 122 })
public class DecodeClickItemPacket extends PacketDecoder {

    @Override
    public void decode(Player player, ReadBuffer in) {
	int interfaceId = in.readShort(true, ValueType.A, ByteOrder.LITTLE);
	int slot = in.readShort(false, ValueType.A);
	int id = in.readShort(false, ByteOrder.LITTLE);

	/*
	 * TODO: Packet validation
	 * - Be sure the interface id is valid
	 * - Be sure the slot is valid (this should be done within the item container itself)
	 * - Be sure the item id exists and is not < 0 || > definitions.size()
	 * - Check the id in the specified slot to the item sent
	 * - Check to be sure the slot is empty
	 */

	SkillEvent.fireSkillEvents(player);
	player.getCombatBuilder().resetAttackTimer();

	if (interfaceId == InventoryContainer.DEFAULT_INVENTORY_CONTAINER_ID) {
	    if (player.getInventory().getContainer().isSlotFree(slot)) {
		return;
	    }

	    Item item = player.getInventory().getContainer().getItem(slot);

	    if (item.getId() != id) {
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