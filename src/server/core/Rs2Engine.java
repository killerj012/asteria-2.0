package server.core;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import server.core.net.EventSelector;
import server.core.worker.TaskFactory;
import server.world.World;

/**
 * The 'heart' of the the server that fires tickable game logic at 600ms
 * intervals and gives access to important core components of this application.
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
     * <code>networkPool</code>, </code>updatePool</code>, or the <code>diskPool</code>
     * stop receiving tasks and have waited for the specified timeout value
     * (default 3). Also note that specific parts of the server can become idle;
     * for instance, if there are players online being updated but no one has
     * logged in for over (default) 3 minutes the network becomes idle.
     */
    public static final int THREAD_IDLE_TIMEOUT = 3;

    /**
     * Determines if the server should start up in an idle state rather than
     * prestarting all priority pools by default. This value should be true if
     * you aren't expecting many incoming connections when you start your server
     * up. Otherwise this value should be false for more popular servers that
     * get connections the moment they start they server.
     */
    public static final boolean INITIALLY_IDLE = true;

    /** A low priority {@link PriorityServicePool} that takes care of disk IO. */
    private static PriorityServicePool diskPool;

    /**
     * An above-average priority {@link PriorityServicePool} that handles
     * incoming connections.
     */
    private static PriorityServicePool networkPool;

    /**
     * A high priority {@link PriorityServicePool} that updates players in
     * parallel.
     */
    private static PriorityServicePool updatePool;

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

        /** Create the priority pools. */
        diskPool = new PriorityServicePool("DiskThread", 1, Thread.MIN_PRIORITY);
        networkPool = new PriorityServicePool("NetworkThread", 1, Thread.NORM_PRIORITY + 1);
        updatePool = new PriorityServicePool("UpdateThread", Runtime.getRuntime().availableProcessors(), Thread.MAX_PRIORITY);

        /** Create the game executor. */
        gameExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadProvider(Rs2Engine.class.getName(), Thread.MAX_PRIORITY, false, false));

        /** Start the world! */
        gameExecutor.scheduleAtFixedRate(new Rs2Engine(), 0, 600, TimeUnit.MILLISECONDS);

        /** Start miscellaneous tasks. */
        // ...
    }

    @Override
    public void run() {
        try {
            TaskFactory.getFactory().tick();
            EventSelector.tick();
            World.tick();
        } catch (Exception e) {
            logger.warning("Error while ticking the " + Rs2Engine.class.getSimpleName() + "!");
            e.printStackTrace();
        }
    }

    /**
     * Gets the pool that handles disk IO.
     * 
     * @return the disk pool.
     */
    public static PriorityServicePool getDiskPool() {
        return diskPool;
    }

    /**
     * Gets the pool that accepts incoming connections.
     * 
     * @return the network pool.
     */
    public static PriorityServicePool getNetworkPool() {
        return networkPool;
    }

    /**
     * Gets the pool that performs updating on players.
     * 
     * @return the update pool.
     */
    public static PriorityServicePool getUpdatePool() {
        return updatePool;
    }
}
