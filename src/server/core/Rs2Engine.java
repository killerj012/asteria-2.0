package server.core;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import server.core.net.EventSelector;
import server.core.worker.TaskFactory;
import server.world.World;
import server.world.entity.player.content.RestoreEnergyWorker;
import server.world.entity.player.content.RestoreStatWorker;

/**
 * The 'heart' of the the server that fires game logic at 600ms intervals and
 * gives access to the thread pools that carry out miscellaneous work.
 * 
 * @author lare96
 */
public final class Rs2Engine implements Runnable {

    /**
     * The amount of time in minutes for this server to become 'idle'. When the
     * server becomes idle threads are terminated accordingly in order to
     * preserve resources. The server can only become idle after thread pools
     * stop receiving tasks and have waited for the specified timeout value.
     */
    public static final int THREAD_IDLE_TIMEOUT = 1;

    /**
     * Determines if the server should start up in an idle state rather than
     * pre-starting all thread pools by default. This value should be
     * <code>false</code> if you aren't expecting much activity as soon as you
     * start your server up, otherwise this value should be <code>true</code>
     * for more popular servers that receive players the moment they start their
     * servers.
     */
    public static final boolean START_THREADS = false;

    /** An executor that is dedicated to ticking game logic. */
    private static ScheduledExecutorService gameExecutor;

    /** A thread pool that handles short lived concurrent game related tasks. */
    private static ThreadPoolExecutor concurrent;

    /** A thread pool that handles short lived sequential game related tasks. */
    private static ThreadPoolExecutor sequential;

    /** This class cannot be instantiated. */
    private Rs2Engine() {
    }

    /**
     * Initialize the core components of the {@link Rs2Engine}.
     * 
     * @throws Exception
     *             if any errors occur during the initialization.
     */
    public static void init() throws Exception {

        /** Check if we have already started the engine. */
        if (gameExecutor != null) {
            throw new IllegalStateException(
                    "The engine has already been started!");
        }

        /** Create the executors and thread pools. */
        gameExecutor = Executors
                .newSingleThreadScheduledExecutor(new ThreadProvider(
                        Rs2Engine.class.getName(), Thread.NORM_PRIORITY, false,
                        false));
        concurrent = ThreadPoolFactory
                .createThreadPool("ConcurrentThread", Runtime
                .getRuntime().availableProcessors(), Thread.MAX_PRIORITY);
        sequential = ThreadPoolFactory.createThreadPool("SequentialThread", 1,
                Thread.MIN_PRIORITY);

        /** Start ticking the game executor */
        gameExecutor.scheduleAtFixedRate(new Rs2Engine(), 0, 600,
                TimeUnit.MILLISECONDS);

        /** Start miscellaneous tasks. */
        // TODO: Run these tasks only when needed!
        TaskFactory.getFactory().submit(new RestoreStatWorker());
        TaskFactory.getFactory().submit(new RestoreEnergyWorker());
    }




    @Override
    public void run() {
        try {
            // XXX: DO NOT ADD 'TASK SCHEDULER', 'EVENT MANAGER' OR ANY OTHER
            // TASK OR CYCLE SYSTEM. YOU WILL MESS UP THE SERVER AND MAKE IT RUN
            // SLOWER. IF YOU'RE LOOKING FOR A TASK SYSTEM, USE THE 'WORKER'
            // CLASS LOCATED IN 'server.core.worker'. THANK YOU.

            TaskFactory.getFactory().tick();
            EventSelector.tick();
            World.tick();
        } catch (Exception e) {

            /** Nothing we can do, print error and continue processing. */
            e.printStackTrace();
        }
    }

    /**
     * Gets the thread pool that handles concurrent tasks.
     * 
     * @return the thread pool that handles concurrent tasks.
     */
    public static final ThreadPoolExecutor getConcurrentPool() {
        return concurrent;
    }

    /**
     * Gets the thread pool that handles sequential tasks.
     * 
     * @return the thread pool that handles sequential tasks.
     */
    public static final ThreadPoolExecutor getSequentialPool() {
        return sequential;
    }
}
