package com.asteria.world;

import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadPoolExecutor;

import com.asteria.engine.GameEngine;
import com.asteria.engine.ThreadPoolFactory;
import com.asteria.engine.ThreadPoolFactory.BlockingThreadPool;
import com.asteria.engine.net.Session.Stage;
import com.asteria.world.entity.EntityContainer;
import com.asteria.world.entity.npc.Npc;
import com.asteria.world.entity.npc.NpcUpdating;
import com.asteria.world.entity.player.Player;
import com.asteria.world.entity.player.PlayerFileTask.WritePlayerFileTask;
import com.asteria.world.entity.player.PlayerUpdating;

/**
 * Updates all in-game entities, and also contains utility methods to manage
 * various aspects of the world.
 * 
 * @author lare96
 */
public final class World {

    /** All of the registered players. */
    private static final EntityContainer<Player> players = new EntityContainer<>(
            1000);

    /** All of the registered NPCs. */
    private static final EntityContainer<Npc> npcs = new EntityContainer<>(1500);

    /** A concurrent pool that executes code in parallel. */
    private static ThreadPoolExecutor updateExecutor = ThreadPoolFactory
        .createThreadPool("Concurrent-Thread",
        Runtime.getRuntime().availableProcessors(), Thread.MAX_PRIORITY, 5);

    /** Performs processing on all registered entities. */
    public static void tick() {
        try {

            // Perform any general logic processing for players.
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

            // Perform any general logic processing for npcs.
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

            // Perform updating for players in parallel.
            final Phaser phaser = new Phaser(1);
            phaser.bulkRegister(players.getSize());

            for (final Player player : players) {
                if (player == null) {
                    continue;
                }

                GameEngine.getConcurrentPool().execute(new Runnable() {
                    @Override
                    public void run() {

                        // Put a concurrent lock on the player we are currently
                        // updating, so only one thread in the pool can access
                        // this player at a time.
                        synchronized (player) {

                            // Now we actually update the player.
                            try {
                                PlayerUpdating.update(player);
                                NpcUpdating.update(player);

                                // Handle any errors with the player.
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                player.getSession().disconnect();

                                // Arrive at the phaser regardless if there was
                                // an error or not.
                            } finally {
                                phaser.arrive();
                            }
                        }
                    }
                });
            }

            // Wait here until updating is complete.
            phaser.arriveAndAwaitAdvance();

            // Reset all players and prepare them for the next cycle.
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

            // Reset all npcs and prepare them for the next cycle.
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
     * Returns an instance of a {@link Player} object for the specified username
     * hash.
     * 
     * @param username
     *            The username hash.
     * @return The <code>Player</code> object representing the player or
     *         {@code null} if no such player exists.
     */
    public static Player getPlayerByHash(long username) {
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
     * Returns an instance of a {@link Player} object for the specified
     * username.
     * 
     * @param username
     *            The username.
     * @return The <code>Player</code> object representing the player or
     *         {@code null} if no such player exists.
     */
    public static Player getPlayerByName(String username) {
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
     * Sends a message to all online {@link Player}s.
     * 
     * @param message
     *            the message to send that will be sent to everyone online.
     */
    public void sendMessage(String message) {
        for (Player p : players) {
            if (p == null) {
                continue;
            }

            p.getPacketBuilder().sendMessage(message);
        }
    }

    /** Saves the game for all players that are currently online. */
    public static void savePlayers() {
        for (Player player : players) {
            if (player == null) {
                continue;
            }

            savePlayer(player);
        }
    }

    /** Performs a series of operations that shut the entire server down. */
    public static void shutdown() {
        try {

            // First save all players, we block the calling thread until all
            // players are saved.
            BlockingThreadPool pool = new BlockingThreadPool();

            for (Player player : players) {
                if (player == null) {
                    continue;
                }

                pool.append(new WritePlayerFileTask(player));
            }
            pool.fireAndAwait();

            // Terminate any thread pools.
            GameEngine.getConcurrentPool().shutdown();
            GameEngine.getServiceExecutor().shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            // Exit regardless if there was an error or not during shutdown.
            System.exit(1);
        }
    }

    /**
     * Saves the game for a single player.
     * 
     * @param player
     *            the player to save the game for.
     */
    public static void savePlayer(Player player) {

        // Don't save if we aren't logged in.
        if (player.getSession().getStage() != Stage.LOGGED_IN) {
            return;
        }

        // Push the save task to the sequential pool.
        GameEngine.getServiceExecutor().execute(new WritePlayerFileTask(player));
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

    private World() {}
}
