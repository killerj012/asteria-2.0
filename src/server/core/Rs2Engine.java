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
import server.core.task.PooledNpcResetTask;
import server.core.task.PooledPlayerResetTask;
import server.core.task.PooledPlayerUpdateTask;
import server.util.Misc.Stopwatch;
import server.world.World;
import server.world.entity.npc.Npc;
import server.world.entity.player.Player;

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
     * An {@link ExecutorService} that takes care of engine tasks in parallel.
     */
    private static ExecutorService engineTask;

    /**
     * A {@link ScheduledExecutorService} that ticks game logic and updating
     * every 600ms.
     */
    private static ScheduledExecutorService gameExecutor;

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
        engineTask = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), new GameThreadFactory());
        networkExecutor = Executors.newSingleThreadExecutor(new NetworkThreadFactory());
        gameExecutor = Executors.newSingleThreadScheduledExecutor(new GameThreadFactory());

        /** Configure engine components. */
        world.configure();
        reactor.configure();
        encoder.configure();

        /** Start the network and engine. */
        networkExecutor.execute(reactor);
        gameExecutor.scheduleAtFixedRate(new Rs2Engine(), 0, 600, TimeUnit.MILLISECONDS);

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
        engineTask.shutdownNow();
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
             * A countdown latch to block this thread while our thread pool
             * takes care of updating.
             */
            CountDownLatch updateLatch = new CountDownLatch(world.playerAmount());

            for (final Player player : world.getPlayers()) {
                if (player == null) {
                    continue;
                }

                /** Here we use our thread pool to perform updating in parallel. */
                engineTask.execute(new PooledPlayerUpdateTask(player, updateLatch));
            }

            /** Block until the update task is complete. */
            updateLatch.await();

            /**
             * Create a new countdown latch to block this thread while our
             * thread pool resets players.
             */
            updateLatch = new CountDownLatch(world.playerAmount());

            for (Player player : world.getPlayers()) {
                if (player == null) {
                    continue;
                }

                /** Here we use our thread pool to perform resetting in parallel. */
                engineTask.execute(new PooledPlayerResetTask(player, updateLatch));
            }

            /** Block until the reset task is complete. */
            updateLatch.await();

            /**
             * Create a new countdown latch to block this thread while our
             * thread pool resets npcs.
             */
            updateLatch = new CountDownLatch(world.npcAmount());

            for (Npc npc : world.getNpcs()) {
                if (npc == null) {
                    continue;
                }

                /** Here we use our thread pool to perform resetting in parallel. */
                engineTask.execute(new PooledNpcResetTask(npc, updateLatch));
            }

            /** Block until the reset task is complete. */
            updateLatch.await();
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
