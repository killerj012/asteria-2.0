package com.asteria.engine.net;

import java.io.File;
import java.io.FileWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import com.asteria.engine.GameEngine;

/**
 * A static gateway type class that is used to limit the maximum amount of
 * connections per host.
 * 
 * @author blakeman8192
 * @author lare96
 */
public class HostGateway {

    /** A {@link Logger} for printing debugging info. */
    private static Logger logger = Logger.getLogger(HostGateway.class
            .getSimpleName());

    /** The maximum amount of connections from any host that isn't localhost. */
    public static final int MAX_CONNECTIONS_PER_HOST = 1;

    /** Used to keep track of hosts and their amount of connections. */
    private static ConcurrentHashMap<String, Integer> hostMap = new ConcurrentHashMap<>();

    /** Used to keep track of banned hosts. */
    private static Set<String> bannedHosts = Collections
            .synchronizedSet(new HashSet<String>());

    /** This class cannot be instantiated. */
    private HostGateway() {}

    /**
     * Checks the host into the gateway.
     * 
     * @param host
     *            the host that needs to be checked.
     * @return true if the host can connect, false if they have reached or
     *         surpassed the maximum amount of connections.
     */
    public static boolean enter(String host) {

        // If the host is coming from the hosting computer we don't need to
        // check it.
        if (host.equals("127.0.0.1") || host.equals("localhost")) {
            logger.info("Session request from " + host + "<unlimited> accepted.");
            return true;
        }

        // Makes sure this host is not connecting too fast.
        if (!HostThrottler.throttleHost(host)) {
            return false;
        }

        // Retrieve the amount of connections this host has.
        Integer amount = hostMap.putIfAbsent(host, 1);

        // If the host was not in the map, they're clear to go.
        if (amount == null) {
            logger.info("Session request from " + host + "<1> accepted.");
            return true;
        }

        // If they've reached or surpassed the connection limit, reject the
        // host.
        if (amount >= MAX_CONNECTIONS_PER_HOST) {
            logger.warning("Session request from " + host + "<" + amount + "> over connection limit, rejected.");
            return false;
        }

        // Otherwise, replace the key with the next value if it was present.
        hostMap.putIfAbsent(host, amount + 1);
        logger.info("Session request from " + host + "<" + hostMap.get(host) + "> accepted.");
        return true;
    }

    /**
     * Unchecks the host from the gateway.
     * 
     * @param host
     *            the host that needs to be unchecked.
     */
    public static void exit(String host) {

        // If we're connecting locally, no need to uncheck.
        if (host.equals("127.0.0.1") || host.equals("localhost")) {
            return;
        }

        // Get the amount of connections stored for the host.
        Integer amount = hostMap.get(host);

        if (amount < 1) {

            // Remove the host from the map if it's below 1 connection.
            hostMap.remove(host);
            HostThrottler.getTimeMap().remove(host);
            return;
        } else if (amount > 1) {

            // Otherwise decrement the amount of connections stored.
            hostMap.putIfAbsent(host, amount - 1);
        }
    }

    /**
     * Adds a new host to the text file of banned hosts.
     * 
     * @param host
     *            the host to add to the text file.
     */
    public static void addBannedHost(final String host) {
        GameEngine.getSequentialPool().execute(new Runnable() {
            @Override
            public void run() {
                try (FileWriter writer = new FileWriter(new File(
                        "./data/ip_ban_list.txt"), true)) {

                    // First add the host to the active list.
                    bannedHosts.add(host);

                    // Then add it to the file.
                    writer.write(host);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Scans the list of banned hosts from an external text file using a
     * {@link Scanner}.
     * 
     * @throws Exception
     *             if any errors occur during the scanning of hosts.
     */
    public static void loadBannedHosts() throws Exception {

        try (Scanner scanner = new Scanner(new File("./data/ip_ban_list.txt"))) {
            while (scanner.hasNextLine()) {
                bannedHosts.add(scanner.nextLine());
            }
        }
    }

    /**
     * Gets the map of connections.
     * 
     * @return the map of connections.
     */
    public static ConcurrentHashMap<String, Integer> getHostMap() {
        return hostMap;
    }

    /**
     * Gets the set of banned hosts.
     * 
     * @return the set of banned hosts.
     */
    public static Set<String> getBannedHosts() {
        return bannedHosts;
    }
}
