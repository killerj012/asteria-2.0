package server.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import server.core.worker.Worker;
import server.util.Misc;
import server.util.Misc.Stopwatch;
import server.world.entity.Entity;
import server.world.entity.npc.Npc;
import server.world.entity.player.Player;
import server.world.entity.player.content.AssignWeaponAnimation;
import server.world.entity.player.content.AssignWeaponInterface;
import server.world.entity.player.file.WritePlayerFileEvent;

/**
 * Manages in-game entities and fires workers.
 * 
 * @author lare96
 * @author blakeman8192
 */
public final class World {

    /** A queue of {@link Worker}s waiting to be registered. */
    private static Queue<Worker> workQueue;

    /**
     * A list of already registered {@link Worker}s waiting to have their logic
     * fired.
     */
    private static List<Worker> registeredWorkers;

    /** All registered players. */
    private static Player[] players;

    /** All registered NPCs. */
    private static Npc[] npcs;

    /** Total time this server has been online. */
    private static Stopwatch totalOnlineTime;

    /**
     * Submits queued workers for execution and fires logic from previously
     * workers when needed.
     */
    public void fireWorkers() {
        Worker worker;

        /** Register the queued workers! */
        while ((worker = workQueue.poll()) != null) {
            registeredWorkers.add(worker);
        }

        /** Fire any workers that need firing. */
        for (Iterator<Worker> iterator = registeredWorkers.iterator(); iterator.hasNext();) {

            /** Retrieve the next worker. */
            worker = iterator.next();

            /** Block if this worker is malformed. */
            if (worker == null) {
                continue;
            }

            /** Unregister the worker if it is no longer running. */
            if (!worker.isRunning()) {
                iterator.remove();
                continue;
            }

            /** Increment the delay for this worker. */
            worker.incrementCurrentDelay();

            /** Check if this worker is ready to fire! */
            if (worker.getDelay() == worker.getCurrentDelay() && worker.isRunning()) {

                /**
                 * Fire the logic within the worker! ... and handle any errors
                 * that might occur during execution.
                 */
                try {
                    worker.fire();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                /** Reset the delay for the worker. */
                worker.resetCurrentDelay();
            }
        }
    }

    /**
     * Submit a new {@link Worker} for registration.
     * 
     * @param worker
     *        the new worker to submit.
     */
    public void submit(Worker worker) {
        if (worker.isInitialRun()) {
            worker.fire();
        }

        workQueue.add(worker);
    }

    /**
     * Submit an array of new {@link Worker}s.
     * 
     * @param workers
     *        the workers to submit.
     */
    public void submitAll(Worker... workers) {
        if (workers.length < 1) {
            throw new IllegalArgumentException("No workers specified!");
        }

        for (Worker worker : workers) {
            if (worker == null) {
                continue;
            }

            submit(worker);
        }
    }

    /**
     * Cancels all of the currently registered workers.
     */
    public void cancelAllWorkers() {
        for (Worker c : registeredWorkers) {
            if (c == null) {
                continue;
            }

            c.cancel();
        }
    }

    /**
     * Stops all {@link Worker}s with this key attachment.
     * 
     * @param key
     *        the key to stop all workers with.
     */
    public void cancelWorkers(Object key) {
        for (Worker c : registeredWorkers) {
            if (c == null || c.getKey() == null) {
                continue;
            }

            if (c.getKey() == key) {
                c.cancel();
            }
        }
    }

    /**
     * Retrieves a list of {@link Worker}s with this key attachment.
     * 
     * @param key
     *        the workers with this key that will be added to the list.
     * @return a list of workers with this key attachment.
     */
    public List<Worker> retrieveWorkers(Object key) {
        List<Worker> tasks = new ArrayList<Worker>();

        for (Worker c : registeredWorkers) {
            if (c == null || c.getKey() == null) {
                continue;
            }

            if (c.getKey() == key) {
                tasks.add(c);
            }
        }

        return tasks;
    }

    /**
     * Gets an unmodifiable list of all of the registered {@link Worker}s.
     * 
     * @return an unmodifiable list of all of the registered workers.
     */
    public List<Worker> retrieveRegisteredWorkers() {
        return Collections.unmodifiableList(registeredWorkers);
    }

    /**
     * Gets an unmodifiable queue of all of the {@link Worker}s awaiting
     * registration.
     * 
     * @return an unmodifiable queue of all of the workers awaiting
     *         registration.
     */
    public Queue<Worker> retrieveAwaitingWorkers() {
        return (Queue<Worker>) Collections.unmodifiableCollection(workQueue);
    }

    /**
     * Configures the world by loading miscellaneous things.
     */
    public void configure() {

        /** Create world objects. */
        npcs = new Npc[500];
        players = new Player[1000];
        workQueue = new LinkedList<Worker>();
        registeredWorkers = new ArrayList<Worker>();
        totalOnlineTime = new Stopwatch().reset();

        /** Begin loading miscellaneous things. */
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
            // Misc.loadNpcDrops();
            Misc.loadMusic();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Safely terminates the {@link World}.
     * 
     * @throws Exception
     *         if any errors occur during the termination of the world.
     */
    public void terminate() throws Exception {
        for (Player player : getPlayers()) {
            if (player == null) {
                continue;
            }

            player.getTradeSession().resetTrade(false);
            player.logout();
        }

        cancelAllWorkers();
        workQueue.clear();
        registeredWorkers.clear();
    }

    /**
     * Registers an entity for processing.
     * 
     * @param entity
     *        the entity to register.
     */
    public void register(Entity entity) {
        entity.register();
    }

    /**
     * Unregisters an entity from processing.
     * 
     * @param entity
     *        the entity to unregister.
     */
    public void unregister(Entity entity) {
        entity.unregister();
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
     * Saves the game for all players that are currently online.
     */
    public void savePlayers() {
        for (Player player : getPlayers()) {
            if (player == null) {
                continue;
            }

            WritePlayerFileEvent write = new WritePlayerFileEvent(player);
            write.run();
        }
    }

    /**
     * Saves the game for a single player.
     */
    public void savePlayer(Player player) {
        WritePlayerFileEvent write = new WritePlayerFileEvent(player);
        write.run();
    }

    /**
     * Gets an instance of a player by their name.
     * 
     * @param player
     *        the name of the player you are trying to get the instance of.
     * @return the instance of the player, null if no player with that name was
     *         found.
     */
    public Player getPlayer(String player) {
        for (Player p : players) {
            if (p == null) {
                continue;
            }

            if (p.getUsername().equals(player)) {
                return p;
            }
        }
        return null;
    }

    /**
     * Gets the amount of players that are online.
     * 
     * @return the amount of online players.
     */
    public int playerAmount() {
        int amount = 0;
        for (int i = 1; i < players.length; i++) {
            if (players[i] != null) {
                amount++;
            }
        }
        return amount;
    }

    /**
     * Gets the amount of NPCs that are online.
     * 
     * @return the amount of online NPCs.
     */
    public int npcAmount() {
        int amount = 0;
        for (int i = 1; i < npcs.length; i++) {
            if (npcs[i] != null) {
                amount++;
            }
        }
        return amount;
    }

    /**
     * Gets all registered players.
     * 
     * @return the players.
     */
    public Player[] getPlayers() {
        return players;
    }

    /**
     * Gets all registered NPCs.
     * 
     * @return the npcs.
     */
    public Npc[] getNpcs() {
        return npcs;
    }

    /**
     * Gets the total time this server has been running.
     * 
     * @return the time that this server has online for.
     */
    public long getTimeOnline() {
        return totalOnlineTime.elapsed();
    }
}
