package server.core.net.packet.impl;

import server.core.net.packet.PacketBuffer;
import server.core.net.packet.PacketDecoder;
import server.core.net.packet.PacketOpcodeHeader;
import server.world.entity.player.Player;
import server.world.entity.player.skill.SkillEvent;
import server.world.item.Item;
import server.world.item.ItemDefinition;
import server.world.shop.Shop;

/**
 * Sent when the player tries to bank, trade, buy, sell, equip, remove, insert,
 * or swap items.
 * 
 * @author lare96
 */
@PacketOpcodeHeader({ 145, 41, 117, 43, 129, 214 })
public class DecodeItemInterfacePacket extends PacketDecoder {

    @Override
    public void decode(Player player, PacketBuffer.ReadBuffer in) {

        int interfaceId, slot, itemId;

        switch (player.getSession().getPacketOpcode()) {
        case 145:
            interfaceId = in.readShort(PacketBuffer.ValueType.A);
            slot = in.readShort(PacketBuffer.ValueType.A);
            itemId = in.readShort(PacketBuffer.ValueType.A);
            SkillEvent.fireSkillEvents(player);

            switch (interfaceId) {

            case 1688:
                player.getEquipment().removeItem(slot);
                break;
            case 5064:
                player.getBank().addItem(slot, new Item(itemId, 1));
                break;
            case 5382:
                player.getBank().deleteItem(slot, new Item(itemId, 1));
                break;
            case 3900:
                Shop.getShop(player.getOpenShopId()).sendItemBuyingPrice(
                        player, new Item(itemId));
                break;
            case 3823:
                Shop.getShop(player.getOpenShopId()).sendItemSellingPrice(
                        player, new Item(itemId));
                break;
            case 3322:
                player.getTradeSession().offer(new Item(itemId, 1), slot);
                break;
            case 3415:
                player.getTradeSession().unoffer(new Item(itemId, 1));
                break;
            }
            break;

        case 117:
            interfaceId = in.readShort(true, PacketBuffer.ValueType.A,
                    PacketBuffer.ByteOrder.LITTLE);
            itemId = in.readShort(true, PacketBuffer.ValueType.A,
                    PacketBuffer.ByteOrder.LITTLE);
            slot = in.readShort(true, PacketBuffer.ByteOrder.LITTLE);

            switch (interfaceId) {

            case 5064:
                player.getBank().addItem(slot, new Item(itemId, 5));
                break;
            case 5382:
                player.getBank().deleteItem(slot, new Item(itemId, 5));
                break;
            case 3900:
                Shop.getShop(player.getOpenShopId()).purchaseItem(player,
                        new Item(itemId, 1));
                break;
            case 3823:
                Shop.getShop(player.getOpenShopId()).sellItem(player,
                        new Item(itemId, 1), slot);
                break;
            case 3322:
                player.getTradeSession().offer(new Item(itemId, 5), slot);
                break;
            case 3415:
                player.getTradeSession().unoffer(new Item(itemId, 5));
                break;
            }
            break;

        case 43:
            interfaceId = in.readShort(PacketBuffer.ByteOrder.LITTLE);
            itemId = in.readShort(PacketBuffer.ValueType.A);
            slot = in.readShort(PacketBuffer.ValueType.A);

            switch (interfaceId) {

            case 5064:
                player.getBank().addItem(slot, new Item(itemId, 10));
                break;

            case 5382:
                player.getBank().deleteItem(slot, new Item(itemId, 10));
                break;
            case 3900:
                Shop.getShop(player.getOpenShopId()).purchaseItem(player,
                        new Item(itemId, 5));
                break;
            case 3823:
                Shop.getShop(player.getOpenShopId()).sellItem(player,
                        new Item(itemId, 5), slot);
                break;
            case 3322:
                player.getTradeSession().offer(new Item(itemId, 10), slot);
                break;
            case 3415:
                player.getTradeSession().unoffer(new Item(itemId, 10));
                break;

            }
            break;

        case 129:
            slot = in.readShort(PacketBuffer.ValueType.A);
            interfaceId = in.readShort();
            itemId = in.readShort(PacketBuffer.ValueType.A);

            switch (interfaceId) {

            case 5064:
                player.getBank().addItem(
                        slot,
                        new Item(itemId, player.getInventory().getContainer()
                                .getCount(itemId)));
                break;

            case 5382:
                int withdrawAmount = 0;
                if (player.isWithdrawAsNote()) {
                    withdrawAmount = player.getBank().getContainer()
                            .getCount(itemId);
                } else {
                    Item itemWithdrew = new Item(itemId, 1);
                    withdrawAmount = ItemDefinition.getDefinitions()[itemWithdrew
                            .getId()].isStackable() ? player.getBank()
                            .getContainer().getCount(itemId) : 28;
                }

                player.getBank().deleteItem(slot,
                        new Item(itemId, withdrawAmount));
                break;
            case 3900:
                Shop.getShop(player.getOpenShopId()).purchaseItem(player,
                        new Item(itemId, 10));
                break;

            case 3823:
                Shop.getShop(player.getOpenShopId()).sellItem(player,
                        new Item(itemId, 10), slot);
                break;
            case 3322:
                player.getTradeSession().offer(
                        new Item(itemId, player.getInventory().getContainer()
                                .getCount(itemId)), slot);
                break;
            case 3415:
                player.getTradeSession().unoffer(
                        new Item(itemId, player.getTradeSession().getOffering()
                                .getCount(itemId)));
                break;
            }

            break;

        case 41:
            @SuppressWarnings("unused")
            int wear = in.readShort(false);
            slot = in.readShort(false, PacketBuffer.ValueType.A);
            interfaceId = in.readShort(false, PacketBuffer.ValueType.A);

            player.getCombatBuilder().resetAttackTimer();
            player.getEquipment().equipItem(slot);
            SkillEvent.fireSkillEvents(player);
            break;

        case 214:
            interfaceId = in.readShort(PacketBuffer.ValueType.A,
                    PacketBuffer.ByteOrder.LITTLE);
            in.readByte(PacketBuffer.ValueType.C);
            int fromSlot = in.readShort(PacketBuffer.ValueType.A,
                    PacketBuffer.ByteOrder.LITTLE);
            int toSlot = in.readShort(PacketBuffer.ByteOrder.LITTLE);

            switch (interfaceId) {
            case 3214:
                player.getInventory().exchangeItemSlot(fromSlot, toSlot);
                player.getInventory().refresh();
                break;
            case 5382:
                if (player.isInsertItem()) {
                    player.getBank().getContainer().swap(fromSlot, toSlot);
                } else {
                    player.getBank().getContainer().insert(fromSlot, toSlot);
                }
                Item[] bankItems = player.getBank().getContainer().toArray();
                player.getPacketBuilder().sendUpdateItems(5382, bankItems);
                break;
            }
            break;
        }
    }
}
