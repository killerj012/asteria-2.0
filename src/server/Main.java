package server;

import java.nio.channels.ServerSocketChannel;
import java.util.logging.Logger;

import server.core.Rs2Engine;
import server.core.net.EventSelector;
import server.util.Misc.Stopwatch;
import server.world.World;

/**
 * The 'origin' class which contains the main method of this server.
 * 
 * @author lare96
 */
public final class Main {

    /** The logger for printing information. */
    private static Logger logger = Logger.getLogger(Main.class.getSimpleName());

    /** The name of this server. */
    public static final String NAME = "Asteria 2.0";

    /** The port for the {@link ServerSocketChannel} to be bound to. */
    public static final int PORT = 43594;

    /** So this class cannot be instantiated. */
    private Main() {
    }

    /**
     * The first method invoked when the server is ran.
     * 
     * @param args
     *            the array of runtime arguments.
     */
    public static void main(String[] args) {
        // XXX: ALL UTILITIES SHOULD BE LOADED IN "WORLD.INIT". DO NOT START ANY
        // ASYNCHRONOUS EVENT SYSTEMS LIKE 'EVENT MANAGER' AT ALL. THAT IS NOT
        // THREAD SAFE AND YOU WILL MESS UP THE SERVER. ASTERIA ALREADY COMES
        // WITH A TASK SYSTEM (server -> core -> worker -> Worker.class).

        /** Initialize the core parts of the server. */
        try {
            Stopwatch timer = new Stopwatch();

            World.init();
            logger.info("The world is now running...");

            EventSelector.init();
            logger.info("The reactor is now running...");

            Rs2Engine.init();
            logger.info("The engine is now running...");
            logger.info(NAME
                    + " is now ".concat(
                            Rs2Engine.START_THREADS ? "ACTIVE " : "IDLE ")
                            .concat("[took " + timer.elapsed() + " ms]"));
        } catch (Exception e) {

            /** An error occurred, print it and abort startup. */
            e.printStackTrace();
            throw new IllegalStateException("An error occured while starting "
                    + Main.NAME + "!");
        }
    }
}
