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
import server.core.net.packet.PacketBuffer;
import server.core.net.packet.PacketDecoder;
import server.core.net.packet.PacketOpcodeHeader;
import server.core.worker.TaskFactory;
import server.core.worker.WorkRate;
import server.core.worker.Worker;
import server.util.Misc;
import server.world.World;
import server.world.entity.Animation;
import server.world.entity.Gfx;
import server.world.entity.combat.CombatFactory;
import server.world.entity.combat.task.CombatPoisonTask.PoisonType;
import server.world.entity.npc.Npc;
import server.world.entity.npc.dialogue.Dialogue;
import server.world.entity.npc.dialogue.OptionDialogueAction;
import server.world.entity.player.Player;
import server.world.entity.player.PlayerRights;
import server.world.entity.player.bot.Bot;
import server.world.entity.player.bot.BotTask;
import server.world.entity.player.skill.SkillManager;
import server.world.item.Item;
import server.world.item.ItemDefinition;
import server.world.map.Location;
import server.world.map.Palette;
import server.world.map.Palette.PaletteTile;
import server.world.map.Position;
import server.world.object.WorldObject;
import server.world.object.WorldObject.Rotation;

/**
 * A custom packet that is sent when the player types a '::' command.
 * 
 * @author lare96
 */
@PacketOpcodeHeader({ 103 })
public class DecodeCommandPacket extends PacketDecoder {

    @Override
    public void decode(final Player player, PacketBuffer.ReadBuffer in) {
        String command = in.readString();
        final String[] cmd = command.toLowerCase().split(" ");

        if (player.getRights().greaterThan(PlayerRights.ADMINISTRATOR)) {
            if (cmd[0].equals("test")) {
                player.sendDialogue(new Dialogue(player,
                        new OptionDialogueAction(1, "Edgeville", "Karamja",
                                "Draynor Village", "Al Kharid", "Nowhere")));
            }
            if (cmd[0].equals("poison")) {
                CombatFactory.poisonEntity(player, PoisonType.SUPER);
            }
            if (cmd[0].equals("config")) {
                int parent = Integer.parseInt(cmd[1]);
                int child = Integer.parseInt(cmd[2]);

                player.getPacketBuilder().sendConfig(parent, child);
            } else if (cmd[0].equals("region")) {
                Palette p = new Palette();
                for (int x = 0; x < 13; x++) {
                    for (int y = 0; y < 13; y++) {
                        p.setTile(x, y, 0, new PaletteTile(2971, 3401, 0));
                    }
                }

                player.getPacketBuilder().sendCustomMapRegion(p);
            } else if (cmd[0].equals("bot")) {
                int amount = Integer.parseInt(cmd[1]);

                for (int i = 0; i < amount; i++) {

                    // logs bot in
                    final Bot bot = new Bot("bot" + i, "pass", player
                            .getPosition().clone()).loginBot();

                    // assigns the bot the walking around task
                    bot.assignTask(BotTask.WALK_AROUND);
                }

            } else if (cmd[0].equals("master")) {
                for (int i = 0; i < player.getSkills().length; i++) {
                    SkillManager
                            .addExperienceNoMultiplier(player,
                                    (2147000000 - player.getSkills()[i]
                                            .getExperience()), i);
                }
            } else if (cmd[0].equals("war")) {
                Location l = new Location(player.getPosition().clone(), 10);

                for (int i = 0; i < 20; i++) {
                    Position p = l.randomPosition();
                    Npc npc = new Npc(1, p.clone());
                    Npc attackNpc = new Npc(1, p.clone().move(Misc.random(2),
                            Misc.random(2)));
                    World.getNpcs().add(npc);
                    World.getNpcs().add(attackNpc);
                    npc.getCombatBuilder().attack(attackNpc);
                }
            } else if (cmd[0].equals("tele")) {
                final int x = Integer.parseInt(cmd[1]);
                final int y = Integer.parseInt(cmd[2]);
                player.move(new Position(x, y, 0));
            } else if (cmd[0].equals("picture")) {
                Rs2Engine.getSequentialPool().execute(new Runnable() {
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

                final File file = new File("./data/coordinates/"
                        + player.getPosition() + ".png");
                TaskFactory.getFactory().submit(
                        new Worker(2, false, WorkRate.APPROXIMATE_SECOND) {
                            @Override
                            public void fire() {
                                Rs2Engine.getSequentialPool().execute(
                                        new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Transferable t = Toolkit
                                                    .getDefaultToolkit()
                                                    .getSystemClipboard()
                                                    .getContents(null);
                                            RenderedImage image = (RenderedImage) t
                                                    .getTransferData(DataFlavor.imageFlavor);
                                            ImageIO.write(image, "png", file);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                                this.cancel();
                            }
                        }.attach(player));

                player.getPacketBuilder().sendMessage(
                        "Created picture [" + file.getPath() + "]");
            } else if (cmd[0].equals("npc")) {
                int npc = Integer.parseInt(cmd[1]);

                final Npc mob = new Npc(npc, player.getPosition());
                World.getNpcs().add(mob);
            } else if (cmd[0].equals("dummy")) {
                final Npc mob = new Npc(1, player.getPosition());
                mob.setCurrentHealth(100000);
                mob.setAutoRetaliate(false);
                World.getNpcs().add(mob);
            } else if (cmd[0].equals("music")) {
                final int id = Integer.parseInt(cmd[1]);
                player.getPacketBuilder().sendMusic(id);
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
                    player.getPacketBuilder().sendMessage(
                            "Item [" + item + "] found on " + count
                                    + " occurances.");
                }

                if (addedToBank) {
                    player.getPacketBuilder()
                            .sendMessage(
                                    bankCount
                                            + " items were banked due to lack of inventory space!");
                }
            } else if (cmd[0].equals("i")) {
                int x = Integer.parseInt(cmd[1]);

                player.getPacketBuilder().sendInterface(x);
            } else if (cmd[0].equals("s")) {
                int x = Integer.parseInt(cmd[1]);
                int delay = Integer.parseInt(cmd[2]);
                player.getPacketBuilder().sendSound(x, 0, delay);
            } else if (cmd[0].equals("mypos")) {
                player.getPacketBuilder().sendMessage(
                        "You are at: " + player.getPosition());
            } else if (cmd[0].equals("pickup")) {
                player.getInventory().addItem(
                        new Item(Integer.parseInt(cmd[1]), Integer
                                .parseInt(cmd[2])));
            } else if (cmd[0].equals("empty")) {
                player.getInventory().getContainer().clear();
                player.getInventory().refresh();
            } else if (cmd[0].equals("emptybank")) {
                player.getBank().getContainer().clear();
            } else if (cmd[0].equals("bank")) {
                player.getBank().open();
            } else if (cmd[0].equals("emote")) {
                int emote = Integer.parseInt(cmd[1]);
                player.animation(new Animation(emote));
            } else if (cmd[0].equals("players")) {
                int size = World.getPlayers().getSize();
                player.getPacketBuilder().sendMessage(
                        size == 1 ? "There is currently 1 player online!"
                                : "There are currently " + size
                                        + " players online!");
            } else if (cmd[0].equals("gfx")) {
                int gfx = Integer.parseInt(cmd[1]);
                player.gfx(new Gfx(gfx));
            } else if (cmd[0].equals("object")) {
                int id = Integer.parseInt(cmd[1]);
                World.getObjects().register(
                        new WorldObject(id, player.getPosition(),
                                Rotation.SOUTH, 10));
            }
        }
    }
}
