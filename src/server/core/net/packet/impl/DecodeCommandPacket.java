package server.core.net.packet.impl;

import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.awt.image.RenderedImage;
import java.io.File;

import javax.imageio.ImageIO;

import server.core.Rs2Engine;
import server.core.net.buffer.PacketBuffer;
import server.core.net.packet.PacketDecoder;
import server.core.net.packet.PacketOpcodeHeader;
import server.core.worker.TaskFactory;
import server.core.worker.WorkRate;
import server.core.worker.Worker;
import server.util.Misc;
import server.world.World;
import server.world.entity.Animation;
import server.world.entity.Gfx;
import server.world.entity.Hit;
import server.world.entity.npc.Npc;
import server.world.entity.player.Player;
import server.world.entity.player.content.TeleportSpell;
import server.world.entity.player.skill.SkillManager;
import server.world.item.Item;
import server.world.item.ItemDefinition;
import server.world.map.Palette;
import server.world.map.Position;
import server.world.map.Palette.PaletteTile;
import server.world.object.WorldObject;
import server.world.object.WorldObject.Rotation;

/**
 * A custom packet that is sent when the player types a '::' command.
 * 
 * @author lare96
 */
@PacketOpcodeHeader( { 103 })
public class DecodeCommandPacket extends PacketDecoder {

    @Override
    public void decode(final Player player, PacketBuffer.ReadBuffer in) {
        String command = in.readString();
        final String[] cmd = command.toLowerCase().split(" ");

        if (cmd[0].equals("config")) {
            int parent = Integer.parseInt(cmd[1]);
            int child = Integer.parseInt(cmd[2]);

            player.getPacketBuilder().sendConfig(parent, child);
        } else if (cmd[0].equals("die")) {
            player.dealDamage(new Hit(100));
        } else if (cmd[0].equals("barrows")) {
            for (ItemDefinition i : ItemDefinition.getDefinitions()) {
                if (i == null || i.getItemName().endsWith("0") || i.getItemName().endsWith("25") || i.getItemName().endsWith("50") || i.getItemName().endsWith("75") || i.getItemName().endsWith("100") || i.isNoted()) {
                    continue;
                }

                if (i.getItemName().startsWith("Dharoks") || i.getItemName().startsWith("Torags") || i.getItemName().startsWith("Ahrims") || i.getItemName().startsWith("Karils") || i.getItemName().startsWith("Veracs") || i.getItemName().startsWith("Guthans")) {
                    if (player.getInventory().getContainer().hasRoomFor(new Item(i.getItemId(), 1))) {
                        player.getInventory().addItem(new Item(i.getItemId(), 1));
                    } else {
                        player.getBank().addItem(new Item(i.getItemId(), 1));
                    }
                }
            }
        } else if (cmd[0].equals("master")) {
            for (int i = 0; i < player.getSkills().length; i++) {
                SkillManager.addExperience(player, (2147000000 - player.getSkills()[i].getExperience()), i);
            }
        } else if (cmd[0].equals("reload")) {
            try {
                Misc.codeFiles();
                Misc.codeHosts();
                Misc.codeEquipment();
            } catch (Exception e) {
                e.printStackTrace();
            }

            player.getPacketBuilder().sendMessage("Sucessfully reloaded data!");
        } else if (cmd[0].equals("tele")) {
            final int x = Integer.parseInt(cmd[1]);
            final int y = Integer.parseInt(cmd[2]);

            player.teleport(new TeleportSpell() {
                @Override
                public Position teleportTo() {
                    return new Position(x, y);
                }

                @Override
                public Teleport type() {
                    return player.getSpellbook().getTeleport();
                }

                @Override
                public int baseExperience() {
                    return 500;
                }

                @Override
                public Item[] itemsRequired() {
                    return null;
                }

                @Override
                public int levelRequired() {
                    return 1;
                }
            });
        } else if (cmd[0].equals("move")) {
            final int x = Integer.parseInt(cmd[1]);
            final int y = Integer.parseInt(cmd[2]);

            player.move(x, y);
        } else if (cmd[0].equals("picture")) {
            // XXX: take a picture of the screen, saved in the
            // ./data/coordinates/folder :)

            int time = Integer.parseInt(cmd[1]);

            Rs2Engine.getDiskPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Robot robot = new Robot();

                        robot.keyPress(KeyEvent.VK_ALT);
                        robot.keyPress(KeyEvent.VK_PRINTSCREEN);
                        robot.keyRelease(KeyEvent.VK_PRINTSCREEN);
                        robot.keyRelease(KeyEvent.VK_ALT);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            final File file = new File("./data/coordinates/" + player.getPosition() + ".png");
            TaskFactory.getFactory().submit(new Worker(time, false, WorkRate.APPROXIMATE_SECOND) {
                @Override
                public void fire() {
                    Rs2Engine.getDiskPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
                                RenderedImage image = (RenderedImage) t.getTransferData(DataFlavor.imageFlavor);
                                ImageIO.write(image, "png", file);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    this.cancel();
                }
            }.attach(player));

            player.getPacketBuilder().sendMessage("Created picture [" + file.getPath() + "]");
        } else if (cmd[0].equals("npc")) {
            int npc = Integer.parseInt(cmd[1]);

            final Npc mob = new Npc(npc, player.getPosition());
            World.getNpcs().add(mob);
        } else if (cmd[0].equals("music")) {
            final int id = Integer.parseInt(cmd[1]);
            player.getPacketBuilder().sendMusic(id);
        } else if (cmd[0].equals("region")) {
            // RegionBuilder map = new RegionBuilder();
            //
            // for (int z = 0; z < 4; z++) {
            // for (int x = 0; x < 13; x++) {
            // for (int y = 0; y < 13; y++) {
            // map.setTile(x, y, z, new
            // RegionTileBuilder(player.getPosition().getX(),
            // player.getPosition().getY()));
            // }
            // }
            // }

            Palette p = new Palette();
            for (int z = 0; z < 4; z++) {
                for (int x = 0; x < 13; x++) {
                    for (int y = 0; y < 13; y++) {
                        p.setTile(x, y, z, new PaletteTile(3222, 3222, 0));
                    }
                }
            }

            player.getPacketBuilder().sendCustomMapRegion(p);
        } else if (cmd[0].equals("item")) {
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

                if (i.getItemName().contains(item) || i.getItemName().equalsIgnoreCase(item) || i.getItemName().startsWith(item) || i.getItemName().endsWith(item)) {
                    if (player.getInventory().getContainer().hasRoomFor(new Item(i.getItemId(), amount))) {
                        player.getInventory().addItem(new Item(i.getItemId(), amount));
                    } else {
                        player.getBank().addItem(new Item(i.getItemId(), amount));
                        addedToBank = true;
                        bankCount++;
                    }
                    count++;
                }
            }

            if (count == 0) {
                player.getPacketBuilder().sendMessage("Item [" + item + "] not found!");
            } else {
                player.getPacketBuilder().sendMessage("Item [" + item + "] found on " + count + " occurances.");
            }

            if (addedToBank) {
                player.getPacketBuilder().sendMessage(bankCount + " items were banked due to lack of inventory space!");
            }
        } else if (cmd[0].equals("i")) {
            int x = Integer.parseInt(cmd[1]);

            player.getPacketBuilder().sendInterface(x);
        } else if (cmd[0].equals("s")) {
            int x = Integer.parseInt(cmd[1]);
            int delay = Integer.parseInt(cmd[2]);
            player.getPacketBuilder().sendSound(x, 0, delay);
        } else if (cmd[0].equals("mypos")) {
            player.getPacketBuilder().sendMessage("You are at: " + player.getPosition());
        } else if (cmd[0].equals("pickup")) {
            player.getInventory().addItem(new Item(Integer.parseInt(cmd[1]), Integer.parseInt(cmd[2])));
        } else if (cmd[0].equals("empty")) {
            player.getInventory().getContainer().clear();
            player.getInventory().refresh(3214);
        } else if (cmd[0].equals("bank")) {
            player.getBank().open();
        } else if (cmd[0].equals("emote")) {
            int emote = Integer.parseInt(cmd[1]);

            player.animation(new Animation(emote));
        } else if (cmd[0].equals("emote2")) {
            TaskFactory.getFactory().submit(new Worker(2, false, WorkRate.APPROXIMATE_SECOND) {
                int start = Integer.parseInt(cmd[1]);
                int end = Integer.parseInt(cmd[2]);

                @Override
                public void fire() {
                    if (start == end) {
                        this.cancel();
                        return;
                    }

                    player.animation(new Animation(start));
                    player.getPacketBuilder().sendMessage("Playing emote: " + start);
                    start++;
                }
            }.attach(player));
        } else if (cmd[0].equals("players")) {
            int size = World.getPlayers().getSize();
            player.getPacketBuilder().sendMessage(size == 1 ? "There is currently 1 player online!" : "There are currently " + size + " players online!");
        } else if (cmd[0].equals("gfx")) {
            int gfx = Integer.parseInt(cmd[1]);

            player.gfx(new Gfx(gfx));
        } else if (cmd[0].equals("object")) {
            int id = Integer.parseInt(cmd[1]);
            World.getObjects().register(new WorldObject(id, player.getPosition(), Rotation.SOUTH, 10));
        }
    }
}
