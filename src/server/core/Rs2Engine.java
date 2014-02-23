package server.core;

import java.nio.channels.SelectionKey;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import server.Main;
import server.core.net.AsynchronousReactor;
import server.core.net.Session;
import server.core.net.buffer.PacketBuffer;
import server.core.net.packet.PacketEncoder;
import server.util.Misc.Stopwatch;
import server.world.World;
import server.world.entity.npc.Npc;
import server.world.entity.npc.NpcUpdate;
import server.world.entity.player.Player;
import server.world.entity.player.PlayerUpdate;

/**
 * The 'heart' of the the server that fires tickable game logic and gives access
 * to vital components of the engine.
 * 
 * @author lare96
 */
public class Rs2Engine implements Runnable {

    /** A {@link World} to manage in-game entities. */
    private static World world;

    /**
     * An {@link AsynchronousReactor} to dispatch events to {@link SelectionKey}s
     * as needed.
     */
    private static AsynchronousReactor reactor;

    /**
     * A {@link PacketEncoder} that encodes the header of raw
     * {@link PacketBuffer}s and sends them to the appropriate {@link Session}.
     */
    private static PacketEncoder encoder;

    /**
     * An {@link ExecutorService} dedicated to the {@link AsynchronousReactor}.
     */
    private static ExecutorService networkExecutor;

    /**
     * An {@link ExecutorService} that will concurrently perform updating on
     * players.
     */
    private static ExecutorService updatePool;

    /**
     * A {@link ScheduledExecutorService} that ticks game logic and updating
     * every 600ms.
     */
    private static ScheduledExecutorService gameExecutor;

    /**
     * A {@link Thread} that fires general game logic and updates players every
     * tick.
     */
    private static Thread gameEngine;

    /**
     * Starts the {@link Rs2Engine} which creates and configures the core
     * components (including the engine) of this server.
     * 
     * @throws Exception
     *         if any errors occur during startup.
     */
    public static void start() throws Exception {

        /** Create and start the timer. */
        Stopwatch startup = new Stopwatch().reset();

        /** Create engine components. */
        world = new World();
        reactor = new AsynchronousReactor();
        encoder = new PacketEncoder();
        updatePool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        networkExecutor = Executors.newSingleThreadExecutor();
        gameExecutor = Executors.newSingleThreadScheduledExecutor();

        /** Create the actual engine. */
        gameEngine = new Thread(new Rs2Engine());

        /** Configure engine components. */
        world.configure();
        reactor.configure();
        encoder.configure();

        /** Configure the actual engine. */
        gameEngine.setPriority(Thread.MAX_PRIORITY);
        gameEngine.setName(Rs2Engine.class.getName());

        /** Start the network and engine. */
        networkExecutor.execute(reactor);
        gameExecutor.scheduleAtFixedRate(gameEngine, 0, 600, TimeUnit.MILLISECONDS);

        /** ... and we are online :) */
        Main.getLogger().info(Main.SERVER_NAME + " took " + startup.elapsed() + "ms to load!");
        Main.getLogger().info(Main.SERVER_NAME + " is online on " + reactor.getAddress());
    }

    /**
     * Performs an orderly shutdown of the core components (including the
     * engine) of this server.
     * 
     * @throws Exception
     *         if any exceptions occur during termination of the engine
     *         components.
     */
    public static void shutdown() throws Exception {
        encoder.terminate();
        world.terminate();
        reactor.terminate();
        networkExecutor.shutdownNow();
        updatePool.shutdownNow();
        gameExecutor.shutdownNow();
        System.exit(0);
    }

    @Override
    public void run() {

        /** Fire logic from workers. */
        world.fireWorkers();

        /** Dispatch network events. */
        reactor.pollQueuedEvents();

        try {

            /** Perform any logic processing for players. */
            for (Player player : world.getPlayers()) {
                if (player == null) {
                    continue;
                }

                try {
                    player.pulse();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Main.getLogger().warning(player + " error while firing game logic!");
                    player.getSession().disconnect();
                }
            }

            /** Perform any logic processing for NPCs. */
            for (Npc npc : world.getNpcs()) {
                if (npc == null) {
                    continue;
                }

                try {
                    npc.pulse();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Main.getLogger().warning(npc + " error while firing game logic!");
                    world.unregister(npc);
                }
            }

            /**
             * Now we perform concurrent updating for all players. We use a
             * countdown latch to ensure updating is completed by the
             * <code>updatePool</code> before the <code>gameEngine</code>
             * continues.
             */
            final CountDownLatch updateLatch = new CountDownLatch(world.playerAmount());

            for (final Player player : world.getPlayers()) {
                if (player == null) {
                    continue;
                }

                /**
                 * Here we use our thread pool to perform updating.
                 */
                updatePool.execute(new Runnable() {
                    @Override
                    public void run() {

                        /**
                         * Put a concurrent lock on the player we are currently
                         * updating - so only one thread in the pool can access
                         * this player at a time.
                         */
                        synchronized (player) {

                            /** Now we actually update the player. */
                            try {
                                PlayerUpdate.update(player);
                                NpcUpdate.update(player);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                Main.getLogger().warning(player + " error while updating concurrently!");
                                player.getSession().disconnect();

                                /**
                                 * And here we decrement the latch by one - one
                                 * less player we need to update. Once this
                                 * reaches zero the flow of code below will
                                 * continue :)
                                 */
                            } finally {
                                updateLatch.countDown();
                            }
                        }
                    }
                });
            }

            /**
             * The flow of code will literally stop here until our thread pool
             * is done updating players - when the countdown for the
             * <code>updateLatch</code> reaches 0 (no more players to
             * update!).
             */
            updateLatch.await();

            /** Reset all players. */
            for (Player player : world.getPlayers()) {
                if (player == null) {
                    continue;
                }

                try {
                    player.reset();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Main.getLogger().warning(player + " error while resetting for the next game tick!");
                    player.getSession().disconnect();
                }
            }

            /** Reset all NPCs. */
            for (Npc npc : world.getNpcs()) {
                if (npc == null) {
                    continue;
                }

                try {
                    npc.reset();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    Main.getLogger().warning(npc + " error while resetting for the next game tick!");
                    world.unregister(npc);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Gets the instance of the {@link World}.
     * 
     * @return the instance of the world.
     */
    public static World getWorld() {
        return world;
    }

    /**
     * Gets the instance of the {@link AsynchronousReactor}.
     * 
     * @return the instance of the reactor.
     */
    public static AsynchronousReactor getReactor() {
        return reactor;
    }

    /**
     * Gets the instance of the {@link PacketEncoder}.
     * 
     * @return the instance of the encoder.
     */
    public static PacketEncoder getEncoder() {
        return encoder;
    }
}
