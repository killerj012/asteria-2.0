package com.asteria.engine.net.packet.impl;

import com.asteria.engine.net.HostGateway;
import com.asteria.engine.net.ProtocolBuffer;
import com.asteria.engine.net.packet.PacketDecoder;
import com.asteria.engine.net.packet.PacketOpcodeHeader;
import com.asteria.world.World;
import com.asteria.world.entity.Animation;
import com.asteria.world.entity.Graphic;
import com.asteria.world.entity.npc.Npc;
import com.asteria.world.entity.player.Player;
import com.asteria.world.entity.player.PlayerRights;
import com.asteria.world.entity.player.skill.Skills;
import com.asteria.world.item.Item;
import com.asteria.world.item.ItemDefinition;
import com.asteria.world.map.Position;
import com.asteria.world.object.WorldObject;
import com.asteria.world.object.WorldObject.Rotation;
import com.asteria.world.object.WorldObjectManager;

/**
 * A custom packet that is sent when the player types the '::' keyword.
 * 
 * @author lare96
 */
@PacketOpcodeHeader({ 103 })
public class DecodeCommandPacket extends PacketDecoder {

    @Override
    public void decode(final Player player, ProtocolBuffer buf) {
        String command = buf.readString().toLowerCase();
        final String[] cmd = command.split(" ");

        // All commands are currently for 'developers' only, which is the
        // highest rank. For all of the other ranks look at the 'PlayerRights'
        // class.
        if (player.getRights().greaterThan(PlayerRights.ADMINISTRATOR)) {
            switch (cmd[0]) {
            case "teleto":
                Player teleTo = World.getPlayerByName(cmd[1].replaceAll("_",
                        " "));

                if (teleTo != null)
                    player.move(teleTo.getPosition());
                break;
            case "teletome":
                Player teleToMe = World.getPlayerByName(cmd[1].replaceAll("_",
                        " "));

                if (teleToMe != null)
                    teleToMe.move(player.getPosition());
                break;
            case "ipban":
                Player ipban = World.getPlayerByName(cmd[1]
                        .replaceAll("_", " "));

                if (ipban != null && ipban.getRights().lessThan(
                        PlayerRights.ADMINISTRATOR) && !ipban.equals(player)) {
                    player.getPacketBuilder().sendMessage(
                            "Successfully IP banned " + player);
                    HostGateway.addBannedHost(ipban.getSession().getHost());
                    ipban.logout();
                }
                break;
            case "ban":
                Player ban = World.getPlayerByName(cmd[1].replaceAll("_", " "));

                if (ban != null && ban.getRights().lessThan(
                        PlayerRights.MODERATOR) && !ban.equals(player)) {
                    player.getPacketBuilder().sendMessage(
                            "Successfully banned " + player);
                    ban.setBanned(true);
                    ban.logout();
                }
                break;
            case "master":
                for (int i = 0; i < player.getSkills().length; i++) {
                    Skills.experience(player, (Integer.MAX_VALUE - player
                            .getSkills()[i].getExperience()), i);
                }
                break;
            case "tele":
                int x = Integer.parseInt(cmd[1]);
                int y = Integer.parseInt(cmd[2]);
                player.move(new Position(x, y, 0));
                break;
            case "npc":
                World.getNpcs()
                        .add(new Npc(Integer.parseInt(cmd[1]), player
                                .getPosition()));
                break;
            case "dummy":
                Npc mob = new Npc(Integer.parseInt(cmd[1]),
                        player.getPosition());
                mob.setCurrentHealth(100000);
                mob.setAutoRetaliate(false);
                World.getNpcs().add(mob);
                break;
            case "music":
                int id = Integer.parseInt(cmd[1]);
                player.getPacketBuilder().sendMusic(id);
                break;
            case "item":
                String item = cmd[1].replaceAll("_", " ");
                int amount = Integer.parseInt(cmd[2]);
                player.getPacketBuilder().sendMessage("Searching...");

                int count = 0;
                int bankCount = 0;
                boolean addedToBank = false;
                for (ItemDefinition i : ItemDefinition.getDefinitions()) {
                    if (i == null || i.isNoted()) {
                        continue;
                    }

                    if (i.getItemName().toLowerCase().contains(item)) {
                        if (player.getInventory().getContainer()
                                .hasRoomFor(new Item(i.getItemId(), amount))) {
                            player.getInventory().addItem(
                                    new Item(i.getItemId(), amount));
                        } else {
                            player.getBank().addItem(
                                    new Item(i.getItemId(), amount));
                            addedToBank = true;
                            bankCount++;
                        }
                        count++;
                    }
                }

                if (count == 0) {
                    player.getPacketBuilder().sendMessage(
                            "Item [" + item + "] not found!");
                } else {
                    player.getPacketBuilder()
                            .sendMessage(
                                    "Item [" + item + "] found on " + count + " occurances.");
                }

                if (addedToBank) {
                    player.getPacketBuilder()
                            .sendMessage(
                                    bankCount + " items were banked due to lack of inventory space!");
                }
                break;
            case "interface":
                player.getPacketBuilder().sendInterface(
                        Integer.parseInt(cmd[1]));
                break;
            case "sound":
                player.getPacketBuilder().sendSound(Integer.parseInt(cmd[1]),
                        0, Integer.parseInt(cmd[2]));
                break;
            case "mypos":
                player.getPacketBuilder().sendMessage(
                        "You are at: " + player.getPosition());
                break;
            case "pickup":
                player.getInventory().addItem(
                        new Item(Integer.parseInt(cmd[1]), Integer
                                .parseInt(cmd[2])));
                break;
            case "empty":
                player.getInventory().getContainer().clear();
                player.getInventory().refresh();
                break;
            case "emptybank":
                player.getBank().getContainer().clear();
                player.getBank().refresh();
                break;
            case "bank":
                player.getBank().open();
                break;
            case "emote":
                player.animation(new Animation(Integer.parseInt(cmd[1])));
                break;
            case "players":
                int size = World.getPlayers().getSize();
                player.getPacketBuilder()
                        .sendMessage(
                                size == 1 ? "There is currently 1 player online!"
                                        : "There are currently " + size + " players online!");
                break;
            case "gfx":
                player.graphic(new Graphic(Integer.parseInt(cmd[1])));
                break;
            case "object":
                WorldObjectManager.register(new WorldObject(Integer
                        .parseInt(cmd[1]), player.getPosition(),
                        Rotation.SOUTH, 10));
                break;
            case "config":
                player.getPacketBuilder().sendConfig(Integer.parseInt(cmd[1]),
                        Integer.parseInt(cmd[2]));
                break;
            default:
                player.getPacketBuilder().sendMessage(
                        "Command [::" + cmd[0] + "] does not exist!");
                break;
            }
        }
    }
}
