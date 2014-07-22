package com.asteria.engine;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.asteria.engine.net.ServerEngine;
import com.asteria.engine.task.TaskFactory;
import com.asteria.world.World;
import com.asteria.world.entity.player.content.RestoreStatTask;
import com.asteria.world.item.ground.GroundItemManager;

/**
 * A {@link Thread} that fires all game logic and gives access to multiple
 * thread pools that carry out work.
 * 
 * @author lare96
 */
public final class GameEngine implements Runnable {

    /** An executor that is dedicated to running game logic. */
    private static ScheduledExecutorService gameExecutor;

    /** A thread pool that handles short lived concurrent game related tasks. */
    private static ThreadPoolExecutor concurrent;

    /** A thread pool that handles short lived sequential game related tasks. */
    private static ThreadPoolExecutor sequential;

    /**
     * Start the core components of the engine.
     * 
     * @throws Exception
     *             if any errors occur during the initialization.
     */
    public static void init() throws Exception {

        // Check if we have already started the engine.
        if (gameExecutor != null) {
            throw new IllegalStateException(
                    "The engine has already been started!");
        }

        // Create all of the executors.
        gameExecutor = Executors
                .newSingleThreadScheduledExecutor(new ThreadProvider(
                        "Engine-Thread", Thread.NORM_PRIORITY, false));
        concurrent = ThreadPoolFactory.createThreadPool("Concurrent-Thread",
                Runtime.getRuntime().availableProcessors(),
                Thread.MAX_PRIORITY, 5);
        sequential = ThreadPoolFactory.createThreadPool("Sequential-Thread", 1,
                Thread.MIN_PRIORITY, 5);

        // Start ticking the game at 600ms intervals.
        gameExecutor.scheduleAtFixedRate(new GameEngine(), 0, 600,
                TimeUnit.MILLISECONDS);

        // Start miscellaneous tasks.
        TaskFactory.submit(new RestoreStatTask());
        TaskFactory.submit(new GroundItemManager());
    }

    @Override
    public void run() {
        try {

            // Handle all cycle-based tasks.
            TaskFactory.tick();

            // Handle all networking events.
            ServerEngine.tick();

            // Handle processing for entities.
            World.tick();
        } catch (Exception e) {

            // Exceptions should never be thrown this far up, but if somehow
            // they are then we print the error and save all online players.
            e.printStackTrace();
            World.savePlayers();
        }
    }

    /**
     * Gets the thread pool that handles concurrent tasks.
     * 
     * @return the thread pool that handles concurrent tasks.
     */
    public static ThreadPoolExecutor getConcurrentPool() {
        return concurrent;
    }

    /**
     * Gets the thread pool that handles sequential tasks.
     * 
     * @return the thread pool that handles sequential tasks.
     */
    public static ThreadPoolExecutor getSequentialPool() {
        return sequential;
    }

    private GameEngine() {}
}
