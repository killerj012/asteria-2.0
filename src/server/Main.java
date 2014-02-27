package server;

import java.nio.channels.ServerSocketChannel;

import server.core.Rs2Engine;

/**
 * The 'origin' class which contains the main method of this application and
 * miscellaneous constants for control over the server.
 * 
 * @author lare96
 */
public final class Main {

    /** The name of this server. */
    public static final String SERVER_NAME = "Asteria 2.0";

    /** The port for the {@link ServerSocketChannel} to be bound to. */
    public static final int PORT = 43594;

    /**
     * If RSA should be decoded in the login block (set this to false if you
     * don't have RSA enabled in your client and you don't know how to get RSA
     * working).
     */
    public static final boolean DECODE_RSA = true;

    /**
     * The first method invoked when the server is ran.
     * 
     * @param args
     *        the array of runtime arguments.
     */
    public static void main(String[] args) {

        /** Prepare and start the server. */
        try {
            Rs2Engine.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
