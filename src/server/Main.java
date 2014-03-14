package server;

import java.nio.channels.ServerSocketChannel;
import java.util.logging.Logger;

import server.core.Rs2Engine;
import server.core.net.EventSelector;
import server.util.Misc.Stopwatch;
import server.world.World;

/**
 * The 'origin' class which contains the main method of this application and
 * miscellaneous constants for control over the core of the server.
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
     *        the array of runtime arguments.
     */
    public static void main(String[] args) {

        /** Initialize the core parts of the server. */
        try {
            Stopwatch timer = new Stopwatch();

            World.init();
            logger.info("The world is now running...");

            EventSelector.init();
            logger.info("The reactor is now running...");

            Rs2Engine.init();
            logger.info("The engine is now running...");
            logger.info(Rs2Engine.INITIALLY_IDLE ? NAME + " is now IDLE! [took " + timer.elapsed() + " ms]" : NAME + " is now ACTIVE! [took " + timer.elapsed() + " ms]");
        } catch (Exception e) {
            logger.warning("Error during initialization of " + NAME + "! Exiting...");
            e.printStackTrace();
            System.exit(1);
        }
    }
}
