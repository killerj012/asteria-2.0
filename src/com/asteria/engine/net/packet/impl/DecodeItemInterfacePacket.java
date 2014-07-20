package com.asteria.engine.net.packet.impl;

import com.asteria.engine.net.ProtocolBuffer;
import com.asteria.engine.net.packet.PacketDecoder;
import com.asteria.engine.net.packet.PacketOpcodeHeader;
import com.asteria.world.entity.player.Player;
import com.asteria.world.entity.player.skill.SkillEvent;
import com.asteria.world.item.Item;
import com.asteria.world.item.ItemDefinition;
import com.asteria.world.shop.Shop;

/**
 * Sent when the player tries to bank, trade, buy, sell, equip, remove, insert,
 * or swap items.
 * 
 * @author lare96
 */
@PacketOpcodeHeader({ 145, 41, 117, 43, 129, 214 })
public class DecodeItemInterfacePacket extends PacketDecoder {

    @Override
    public void decode(Player player, ProtocolBuffer buf) {

        int interfaceId, slot, itemId;

        switch (player.getSession().getPacketOpcode()) {
        case 145:
            interfaceId = buf.readShort(ProtocolBuffer.ValueType.A);
            slot = buf.readShort(ProtocolBuffer.ValueType.A);
            itemId = buf.readShort(ProtocolBuffer.ValueType.A);
            SkillEvent.fireSkillEvents(player);

            if (interfaceId < 0 || slot < 0 || itemId < 0) {
                return;
            }

            switch (interfaceId) {

            case 1688:
                player.getCombatBuilder().resetAttackTimer();
                SkillEvent.fireSkillEvents(player);
                player.getEquipment().removeItem(slot);
                break;
            case 5064:
                player.getBank().addItem(slot, new Item(itemId, 1));
                break;
            case 5382:
                player.getBank().deleteItem(slot, new Item(itemId, 1));
                break;
            case 3900:
                Shop.getShop(player.getOpenShopId()).getPurchasePrice(
                        player, new Item(itemId));
                break;
            case 3823:
                Shop.getShop(player.getOpenShopId()).getSellingPrice(player,
                        new Item(itemId));
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
            interfaceId = buf.readShort(true, ProtocolBuffer.ValueType.A,
                    ProtocolBuffer.ByteOrder.LITTLE);
            itemId = buf.readShort(true, ProtocolBuffer.ValueType.A,
                    ProtocolBuffer.ByteOrder.LITTLE);
            slot = buf.readShort(true, ProtocolBuffer.ByteOrder.LITTLE);
            if (interfaceId < 0 || slot < 0 || itemId < 0) {
                return;
            }
            switch (interfaceId) {

            case 5064:
                player.getBank().addItem(slot, new Item(itemId, 5));
                break;
            case 5382:
                player.getBank().deleteItem(slot, new Item(itemId, 5));
                break;
            case 3900:
                Shop.getShop(player.getOpenShopId()).purchase(player,
                        new Item(itemId, 1));
                break;
            case 3823:
                Shop.getShop(player.getOpenShopId()).sell(player,
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
            interfaceId = buf.readShort(ProtocolBuffer.ByteOrder.LITTLE);
            itemId = buf.readShort(ProtocolBuffer.ValueType.A);
            slot = buf.readShort(ProtocolBuffer.ValueType.A);
            if (interfaceId < 0 || slot < 0 || itemId < 0) {
                return;
            }
            switch (interfaceId) {

            case 5064:
                player.getBank().addItem(slot, new Item(itemId, 10));
                break;

            case 5382:
                player.getBank().deleteItem(slot, new Item(itemId, 10));
                break;
            case 3900:
                Shop.getShop(player.getOpenShopId()).purchase(player,
                        new Item(itemId, 5));
                break;
            case 3823:
                Shop.getShop(player.getOpenShopId()).sell(player,
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
            slot = buf.readShort(ProtocolBuffer.ValueType.A);
            interfaceId = buf.readShort();
            itemId = buf.readShort(ProtocolBuffer.ValueType.A);
            if (interfaceId < 0 || slot < 0 || itemId < 0) {
                return;
            }
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
                Shop.getShop(player.getOpenShopId()).purchase(player,
                        new Item(itemId, 10));
                break;

            case 3823:
                Shop.getShop(player.getOpenShopId()).sell(player,
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
            itemId = buf.readShort(false);
            slot = buf.readShort(false, ProtocolBuffer.ValueType.A);
            interfaceId = buf.readShort(false, ProtocolBuffer.ValueType.A);
            if (interfaceId < 0 || slot < 0 || itemId < 0) {
                return;
            }

            switch (itemId) {

            }
            player.getCombatBuilder().resetAttackTimer();
            player.getEquipment().equipItem(slot);
            SkillEvent.fireSkillEvents(player);
            break;

        case 214:
            interfaceId = buf.readShort(ProtocolBuffer.ValueType.A,
                    ProtocolBuffer.ByteOrder.LITTLE);
            buf.readByte(ProtocolBuffer.ValueType.C);
            int fromSlot = buf.readShort(ProtocolBuffer.ValueType.A,
                    ProtocolBuffer.ByteOrder.LITTLE);
            int toSlot = buf.readShort(ProtocolBuffer.ByteOrder.LITTLE);
            if (interfaceId < 0 || fromSlot < 0 || toSlot < 0) {
                return;
            }
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
