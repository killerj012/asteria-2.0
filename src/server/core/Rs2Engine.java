package server.core;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import server.core.net.EventSelector;
import server.core.task.Task;
import server.core.task.TaskFuture;
import server.core.worker.TaskFactory;
import server.world.World;

/**
 * The 'heart' of the the server that fires game logic at 600ms intervals and
 * gives access to the important core components of this server.
 * 
 * @author lare96
 */
public final class Rs2Engine implements Runnable {

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
    public static final boolean INITIALLY_IDLE = true;

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

        /** Check if we have already started the engine. */
        if (gameExecutor != null) {
            throw new IllegalStateException("The engine has already been started!");
        }

        /** Create the game executor. */
        gameExecutor = Executors.newSingleThreadScheduledExecutor(new ThreadProvider(Rs2Engine.class.getName(), Thread.MAX_PRIORITY, false, false));

        /** Start ticking the game executor */
        gameExecutor.scheduleAtFixedRate(new Rs2Engine(), 0, 600, TimeUnit.MILLISECONDS);
    }

    /**
     * Pushes a generic short-lived task to be carried out be the engine. The
     * task may be executed sequentially, concurrently, or on the calling thread
     * based on the {@link Task}s <code>context()</code> implementation.
     * 
     * @param t
     *        the task to be pushed to the engine.
     */
    public static void pushTask(Task t) {
        t.context();
    }

    /**
     * Pushes a generic short-lived <b>blocking result</b> task to be carried
     * out be the engine. The task may be executed sequentially, concurrently,
     * or on the calling thread based on the {@link FutureTask}s
     * <code>context()</code> implementation.
     * 
     * @param t
     *        the task to be pushed to the engine.
     */
    public static <T> Future<T> pushTask(TaskFuture<T> t) {
        return t.context();
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

            /** Nothing we can do, print error and continue processing. */
            e.printStackTrace();
        }
    }
}
