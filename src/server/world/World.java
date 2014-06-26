package server.world;

import java.util.concurrent.Phaser;
import java.util.logging.Logger;

import server.core.Rs2Engine;
import server.core.net.Session.Stage;
import server.core.task.SequentialTask;
import server.core.task.impl.PlayerParallelUpdateTask;
import server.util.Misc;
import server.world.entity.EntityContainer;
import server.world.entity.npc.Npc;
import server.world.entity.player.Player;
import server.world.entity.player.content.AssignSkillRequirement;
import server.world.entity.player.content.AssignWeaponAnimation;
import server.world.entity.player.content.AssignWeaponInterface;
import server.world.entity.player.file.WritePlayerFileEvent;
import server.world.item.ground.RegisterableGroundItem;
import server.world.object.RegisterableWorldObject;

/**
 * Manages entities in the game world.
 * 
 * @author lare96
 */
public final class World {

    /** A logger for printing information. */
    private static Logger logger = Logger.getLogger(World.class.getSimpleName());

    /** All registered players. */
    private static final EntityContainer<Player> players = new EntityContainer<Player>(2147);

    /** All registered NPCs. */
    private static final EntityContainer<Npc> npcs = new EntityContainer<Npc>(4000);

    /** The registerable container for ground item management. */
    private static RegisterableGroundItem registerableGroundItems;

    /** The registerable container for objects. */
    private static RegisterableWorldObject registerableObjects;

    /**
     * Initialize the utilities of the {@link World}.
     * 
     * @throws Exception
     *         if any errors occur during initialization.
     */
    public static void init() {
        try {
            Misc.loadNpcDrops();
            Misc.loadItemDefinitions();
            // NpcDropTable.getAllDrops().get(1615).calculateDropsDebug();
            Misc.codeFiles();
            Misc.codeHosts();
            Misc.codeEquipment();
            Misc.loadWorldObjects();
            Misc.loadNpcDefinitions();
            Misc.loadShops();
            Misc.loadWorldItems();
            Misc.loadWorldNpcs();
            AssignWeaponAnimation.class.newInstance();
            AssignWeaponInterface.class.newInstance();
            AssignSkillRequirement.class.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Ticks logic for the actual game - general logic sequentially and updating
     * logic in parallel.
     */
    public static void tick() {
        try {

            /** Perform any general logic processing for entities. */
            for (Player player : players) {
                if (player == null) {
                    continue;
                }

                try {
                    player.pulse();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    player.getSession().disconnect();
                }
            }

            for (Npc npc : npcs) {
                if (npc == null) {
                    continue;
                }

                try {
                    npc.pulse();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    npcs.remove(npc);
                }
            }

            /** Perform updating for players in parallel. */
            final Phaser phaser = new Phaser(1);

            phaser.bulkRegister(players.getSize());

            for (Player player : players) {
                if (player == null) {
                    continue;
                }

                Rs2Engine.pushTask(new PlayerParallelUpdateTask(player, phaser));
            }

            phaser.arriveAndAwaitAdvance();

            /** Reset all entities and prepare for next cycle. */
            for (Player player : players) {
                if (player == null) {
                    continue;
                }

                try {
                    player.reset();
                    player.setCachedUpdateBlock(null);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    player.getSession().disconnect();
                }
            }

            for (Npc npc : npcs) {
                if (npc == null) {
                    continue;
                }

                try {
                    npc.reset();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    npcs.remove(npc);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Returns an instance of a {@link Player} object for the specified
     * username. hash.
     * 
     * @param username
     *        The username hash.
     * @return The <code>Player</code> object representing the player or
     *         {@code null} if no such player exists.
     */
    public static Player getPlayer(long username) {
        for (Player player : players) {
            if (player == null) {
                continue;
            }

            if (player.getUsernameHash() == username) {
                return player;
            }
        }
        return null;
    }

    /**
     * Sends a message to all online {@link Player}s.
     * 
     * @param message
     *        the message to send that will be sent to everyone online.
     */
    public void sendMessage(String message) {
        for (Player p : players) {
            if (p == null) {
                continue;
            }

            p.getPacketBuilder().sendMessage(message);
        }
    }

    /**
     * Saves the game for all players that are currently registered.
     */
    public static void savePlayers() {
        for (Player player : players) {
            if (player == null) {
                continue;
            }

            savePlayer(player);
        }
    }

    /**
     * Saves the game for a single player.
     * 
     * @param player
     *        the player to save the game for.
     */
    public static void savePlayer(final Player player) {

        /** Don't save if we aren't logged in. */
        if (player.getSession().getStage() != Stage.LOGGED_IN) {
            return;
        }

        /** Add the pending logout until the saving is done. */
        Rs2Engine.pushTask(new SequentialTask() {
            @Override
            public void run() {
                synchronized (player) {
                    WritePlayerFileEvent save = new WritePlayerFileEvent(player);
                    save.run();
                    logger.info(player
                            + " game successfully saved!");
                }
            }
        });
    }

    /**
     * Gets the container of players.
     * 
     * @return the container of players.
     */
    public static EntityContainer<Player> getPlayers() {
        return players;
    }

    /**
     * Gets the container of npcs.
     * 
     * @return the container of npcs.
     */
    public static EntityContainer<Npc> getNpcs() {
        return npcs;
    }

    /**
     * Gets the registerable container for ground items.
     * 
     * @return the registerable container for ground items.
     */
    public static RegisterableGroundItem getGroundItems() {
        if (registerableGroundItems == null) {
            registerableGroundItems = new RegisterableGroundItem();
        }

        return registerableGroundItems;
    }

    /**
     * Gets the registerable container for objects.
     * 
     * @return the registerable container for objects.
     */
    public static RegisterableWorldObject getObjects() {
        if (registerableObjects == null) {
            registerableObjects = new RegisterableWorldObject();
        }

        return registerableObjects;
    }
}
