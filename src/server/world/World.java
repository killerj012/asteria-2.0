package server.world;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Phaser;
import java.util.logging.Logger;

import server.core.Rs2Engine;
import server.core.task.Task;
import server.core.task.impl.PlayerParallelUpdateTask;
import server.util.Misc;
import server.util.Misc.Stopwatch;
import server.world.entity.EntityContainer;
import server.world.entity.npc.Npc;
import server.world.entity.player.Player;
import server.world.entity.player.content.AssignSkillRequirement;
import server.world.entity.player.content.AssignWeaponAnimation;
import server.world.entity.player.content.AssignWeaponInterface;
import server.world.entity.player.file.WritePlayerFileEvent;
import server.world.entity.player.minigame.MinigameFactory;
import server.world.item.ground.RegisterableGroundItem;
import server.world.object.RegisterableWorldObject;

/**
 * Manages in-game entities in the game world.
 * 
 * @author lare96
 * @author blakeman8192
 */
public final class World {

    /** A logger for printing information. */
    private static Logger logger = Logger.getLogger(World.class.getSimpleName());

    /** All registered players. */
    private static EntityContainer<Player> players = new EntityContainer<Player>(2000);

    /** All registered NPCs. */
    private static EntityContainer<Npc> npcs = new EntityContainer<Npc>(4000);

    /** A list of players that are currently being saved. */
    private static CopyOnWriteArrayList<String> cachedPlayers = new CopyOnWriteArrayList<String>();

    /** A stopwatch to track the total time this server has been online. */
    private static Stopwatch totalOnlineTime = new Stopwatch().reset();

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
            Misc.codeFiles();
            Misc.codeHosts();
            Misc.codeEquipment();
            Misc.loadWorldObjects();
            Misc.loadItemDefinitions();
            Misc.loadNpcDefinitions();
            Misc.loadShops();
            Misc.loadWorldItems();
            Misc.loadWorldNpcs();
            AssignWeaponAnimation.class.newInstance();
            AssignWeaponInterface.class.newInstance();
            AssignSkillRequirement.class.newInstance();
            Misc.loadNpcDrops();
            MinigameFactory.fireDynamicTasks();
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
                    logger.warning(player + " error while firing game logic!");
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
                    logger.warning(npc + " error while firing game logic!");
                    npcs.remove(npc);
                }
            }

            /** Perform updating for entities in parallel. */
            final Phaser phaser = new Phaser(1);

            phaser.bulkRegister(players.getSize());

            for (Player player : players) {
                if (player == null) {
                    continue;
                }

                Rs2Engine.getUpdatePool().execute(new PlayerParallelUpdateTask(player, phaser));
            }

            phaser.arriveAndAwaitAdvance();

            /**
             * Reset all of the entities after updating and prepare for a new
             * cycle.
             */
            for (Player player : players) {
                if (player == null) {
                    continue;
                }

                try {
                    player.reset();
                    player.setCachedUpdateBlock(null);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    logger.warning(player + " error while resetting for the next game tick!");
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
                    logger.warning(npc + " error while resetting for the next game tick!");
                    World.getNpcs().remove(npc);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Gets the instance of a player by their online name.
     * 
     * @param username
     *        the username to get the player instance of.
     * @return the instance of the player with the specified username.
     */
    public static Player getPlayer(String username) {
        for (Player player : players) {
            if (player == null) {
                continue;
            }

            if (player.getUsername().equals(username)) {
                return player;
            }
        }
        return null;
    }

    /**
     * Sends a message to all online players.
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

        /** Cache the player until the saving is done. */
        cachedPlayers.add(player.getUsername());

        /** Save the actual file whenever the thread is available to. */
        Rs2Engine.pushTask(new Task() {
            @Override
            public void run() {
                synchronized (player) {
                    WritePlayerFileEvent save = new WritePlayerFileEvent(player);
                    save.run();
                    cachedPlayers.remove(player.getUsername());
                    logger.info(player + " game successfully saved by the task engine!");
                }
            }

            @Override
            public String name() {
                return "Saving file for " + player;
            }
        });
    }

    /**
     * Gets the total time this server has been running.
     * 
     * @return the time that this server has online for.
     */
    public static long getTimeOnline() {
        return totalOnlineTime.elapsed();
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
     * Gets the map of cached players.
     * 
     * @return the cached players.
     */
    public static CopyOnWriteArrayList<String> getCachedPlayers() {
        return cachedPlayers;
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
