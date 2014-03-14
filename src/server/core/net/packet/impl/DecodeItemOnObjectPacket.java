package server.core.net.packet.impl;

import server.core.net.buffer.PacketBuffer.ByteOrder;
import server.core.net.buffer.PacketBuffer.ReadBuffer;
import server.core.net.buffer.PacketBuffer.ValueType;
import server.core.net.packet.PacketDecoder;
import server.core.net.packet.PacketOpcodeHeader;
import server.util.Misc;
import server.world.entity.player.Player;
import server.world.entity.player.skill.impl.Cooking;
import server.world.entity.player.skill.impl.Prayer;
import server.world.entity.player.skill.impl.Smithing;
import server.world.entity.player.skill.impl.Cooking.CookFish;
import server.world.entity.player.skill.impl.Prayer.PrayerItem;
import server.world.entity.player.skill.impl.Smithing.Smelt;
import server.world.entity.player.skill.impl.Smithing.Smith;
import server.world.map.Position;

/**
 * Sent when the player uses an item on an object.
 * 
 * @author lare96
 */
@PacketOpcodeHeader( { 192 })
public class DecodeItemOnObjectPacket extends PacketDecoder {

    @Override
    public void decode(final Player player, ReadBuffer in) {
        in.readShort(false);
        final int objectId = in.readShort(true, ByteOrder.LITTLE);
        final int objectY = in.readShort(true, ValueType.A, ByteOrder.LITTLE);
        in.readShort(false);
        final int objectX = in.readShort(true, ValueType.A, ByteOrder.LITTLE);
        final int itemId = in.readShort(false);
        final int slot = player.getInventory().getContainer().getSlotById(itemId);
        final int size = 1;

        if (!player.getInventory().getContainer().contains(itemId)) {
            return;
        }

        player.facePosition(new Position(objectX, objectY));
        player.getMovementQueueListener().submit(new Runnable() {
            @Override
            public void run() {
                if (Misc.canClickObject(player.getPosition(), new Position(objectX, objectY), size)) {
                    switch (objectId) {
                        case 409:
                            PrayerItem b = PrayerItem.getPrayerItem(itemId);
                            Prayer.getSingleton().altarItem(player, b, slot);
                            break;
                        case 2732:
                            CookFish fish = CookFish.getFish(itemId);

                            if (fish != null) {
                                player.setUsingStove(false);
                                player.setCook(fish);
                                Cooking.getSingleton().openCookingSelection(player, fish.getRawFishId());
                            }
                            break;
                        case 114:
                        case 2728:
                            fish = CookFish.getFish(itemId);

                            if (fish != null) {
                                player.setUsingStove(true);
                                player.setCook(fish);
                                Cooking.getSingleton().openCookingSelection(player, fish.getRawFishId());
                            }
                            break;
                        case 2781:
                        case 2785:
                        case 2966:
                        case 6189:
                        case 3044:
                        case 3294:
                        case 4304:
                            if (Smelt.containSmeltItem(itemId)) {
                                Smithing.getSingleton().smeltInterface(player);
                            }
                            break;
                        case 2783:
                            if (!player.getInventory().getContainer().contains(2347)) {
                                player.getPacketBuilder().sendMessage("You'll need a hammer if you want to make armor and weapons!");
                                player.getPacketBuilder().closeWindows();
                                return;
                            }

                            switch (itemId) {
                                case 2349:
                                    Smithing.getSingleton().smithInterface(player, Smith.BRONZE);
                                    break;
                                case 2351:
                                    Smithing.getSingleton().smithInterface(player, Smith.IRON);
                                    break;
                                case 2353:
                                    Smithing.getSingleton().smithInterface(player, Smith.STEEL);
                                    break;
                                case 2359:
                                    Smithing.getSingleton().smithInterface(player, Smith.MITHRIL);
                                    break;
                                case 2361:
                                    Smithing.getSingleton().smithInterface(player, Smith.ADAMANT);
                                    break;
                                case 2363:
                                    Smithing.getSingleton().smithInterface(player, Smith.RUNE);
                                    break;
                            }
                            break;

                    }
                }
            }
        });
    }
}
