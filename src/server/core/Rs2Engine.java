package server.core;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import server.core.net.EventSelector;
import server.core.worker.TaskFactory;
import server.world.World;

/**
 * The 'heart' of the the server that fires game logic at 600ms intervals and
 * gives access to the important core components of this server.
 * 
 * @author lare96
 */
public final class Rs2Engine implements Runnable {

    /** A logger for printing information. */
    private static Logger logger;

    /**
     * The amount of time in minutes for this server to become 'idle'. When the
     * server becomes idle threads are terminated accordingly in order to
     * preserve resources. The server can only become idle after the
     * </code>updatePool</code> or the <code>taskEngine</code> stop receiving
     * tasks and have waited for the specified timeout value (default 3
     * minutes).
     */
    public static final int THREAD_IDLE_TIMEOUT = 3;

    /**
     * Determines if the server should start up in an idle state rather than
     * prestarting all pools by default. This value should be true if you aren't
     * expecting many tasks as soon as you start your server up. Otherwise this
     * value should be false for more popular servers that receive players the
     * moment they start their servers.
     */
    public static final boolean INITIALLY_IDLE = false;

    /**
     * An average priority {@link GenericTaskPool} that handles short lived
     * asynchronous game related tasks.
     */
    private static GenericTaskPool taskEngine;

    /**
     * A high priority {@link GenericTaskPool} that updates players in parallel.
     * We separate this from the task pool because we do not want to delay the
     * updating of players with less important tasks such as logging in and
     * logging out.
     */
    private static GenericTaskPool updatePool;

    /**
     * An extremely high priority {@link ScheduledExecutorService} that ticks
     * game logic at 600 millisecond intervals.
     */
    private static ScheduledExecutorService gameExecutor;

    /** So this class cannot be instantiated. */
    private Rs2Engine() {
    }

    /**
     * Initialize the core components of the {@link Rs2Engine}.
     * 
     * @throws Exception
     *         if any errors occur during the initialization.
     */
    public static void init() throws Exception {

        /** Create the logger. */
        logger = Logger.getLogger(Rs2Engine.class.getSimpleName());

        /** Create the pools. */
        taskEngine = new GenericTaskPool("TaskThread", 1, Thread.MIN_PRIORITY);
        updatePool = new GenericTaskPool("UpdateThread", Runtime.getRuntime().availableProcessors(), Thread.MAX_PRIORITY);

        /** Create the game executor. */
        gameExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadProvider(Rs2Engine.class.getName(), Thread.MAX_PRIORITY, false, false));

        /** Start the world! */
        gameExecutor.scheduleAtFixedRate(new Rs2Engine(), 0, 600, TimeUnit.MILLISECONDS);

        /** Start miscellaneous tasks. */
        // ...
    }

    /**
     * Pushes a generic task to be carried out asynchronously by the
     * {@link#taskEngine}.
     * 
     * @param r
     *        the task to be pushed to the {@link#taskEngine}.
     */
    public static void pushTask(Runnable r) {
        taskEngine.execute(r);
    }

    @Override
    public void run() {
        try {
            // XXX: Please do not add multiple task systems... Asteria already
            // comes with one! trying to keep as little overhead as possible.

            TaskFactory.getFactory().tick();
            EventSelector.tick();
            World.tick();
        } catch (Exception e) {
            logger.warning("Error while ticking the " + Rs2Engine.class.getSimpleName() + "!");
            e.printStackTrace();
        }
    }

    /**
     * Gets the pool that performs updating on players.
     * 
     * @return the update pool.
     */
    public static GenericTaskPool getUpdatePool() {
        return updatePool;
    }
}
