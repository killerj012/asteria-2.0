package com.asteria.engine;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.asteria.engine.net.ServerEngine;
import com.asteria.engine.task.TaskManager;
import com.asteria.world.World;

/**
 * A sequential task ran by the {@link #gameExecutor} that executes game related
 * code such as cycled tasks, network events, and the updating of entities every
 * <tt>600</tt>ms.
 * 
 * @author lare96
 */
public final class GameEngine implements Runnable {

    /** A sequential executor that acts as the main game thread. */
    private static final ScheduledExecutorService gameExecutor = Executors
        .newSingleThreadScheduledExecutor(new ThreadProvider("Game-Thread",
            Thread.NORM_PRIORITY, false));

    /**
     * A thread pool that executes code in a sequential fashion. This thread
     * pool should be used to carry out any short lived tasks that don't have to
     * be done on the game thread.
     */
    private static final ThreadPoolExecutor serviceExecutor = ThreadPoolFactory
        .createThreadPool("Service-Thread", 1, Thread.MIN_PRIORITY, 5);

    /**
     * Schedule the task that will execute game code at 600ms intervals. This
     * method should only be called <b>once</b> when the server is launched.
     */
    public static void init() {
        gameExecutor.scheduleAtFixedRate(new GameEngine(), 0, 600,
            TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
        try {

            // Handle all cycle-based tasks.
            TaskManager.tick();

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
     * Gets the thread pool that executes code in a sequential fashion. This
     * thread pool should be used to carry out any short lived tasks that don't
     * have to be done on the game thread.
     * 
     * @return the thread pool that executes code in a sequential fashion.
     */
    public static ThreadPoolExecutor getServiceExecutor() {
        return serviceExecutor;
    }
}
