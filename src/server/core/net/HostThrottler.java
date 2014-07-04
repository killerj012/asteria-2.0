package server.core.net;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import server.util.Misc.Stopwatch;

/**
 * Controls the speed at which a host can connect by limiting the maximum amount
 * of connections they can make in a certain time interval.
 * 
 * @author lare96
 */
public class HostThrottler {

    /** A {@link Logger} for printing debugging info. */
    private static Logger logger = Logger.getLogger(HostThrottler.class
            .getSimpleName());

    /** A map of hosts and their respective timers. */
    private static ConcurrentHashMap<String, Stopwatch> timeMap = new ConcurrentHashMap<String, Stopwatch>();

    /**
     * The maximum amount of connections allowed per
     * <code>THROTTLE_TIME_INTERVAL</code> for a single host.
     */
    private static final int AMOUNT_OF_CONNECTIONS_PER_INTERVAL = 1;

    /**
     * The amount of time the host must wait to connect simultaneously (in
     * milliseconds).
     */
    private static final int THROTTLE_TIME_INTERVAL = 1000;

    /** So this class cannot be instantiated. */
    private HostThrottler() {

    }

    /**
     * Makes sure the host can only connect a certain amount of times in a
     * certain time interval
     * 
     * @param host
     *            the host being throttled.
     * @return true if the host is allowed to pass.
     */
    public static boolean throttleHost(String host) {

        /**
         * If the host has connected once already we need to check if they are
         * allowed to connect again.
         */
        if (timeMap.containsKey(host)) {

            /** Get the time since the last connection. */
            long time = timeMap.get(host).elapsed();

            /** Get how many existing connections this host has. */
            Integer connection = HostGateway.getHostMap().get(host) == null ? 0
                    : HostGateway.getHostMap().get(host);

            /**
             * If the time since the last connection is less than
             * <code>THROTTLE_TIME_INTERVAL</code> and the amount of connections
             * is equal to or above the
             * <code>AMOUNT_OF_CONNECTIONS_PER_SECOND</code> then the host is
             * connecting too fast.
             */
            if (time < THROTTLE_TIME_INTERVAL
                    && connection.intValue() >= AMOUNT_OF_CONNECTIONS_PER_INTERVAL) {
                logger.warning("Session request from " + host
                        + " denied: connecting too fast!");
                return false;
            }

            /**
             * If the host has waited one second before connecting again the
             * timer is reset and the host is allowed to pass.
             */
            timeMap.get(host).reset();
            return true;
        }

        /**
         * If the host is connecting for the first time (has no other clients
         * logged in) then the host is added to the the map with its own timer.
         */
        timeMap.putIfAbsent(host, new Stopwatch().reset());
        return true;
    }

    /**
     * Gets the map of hosts and their respective timers.
     * 
     * @return the map of hosts and their respective timers.
     */
    public static ConcurrentHashMap<String, Stopwatch> getTimeMap() {
        return timeMap;
    }
}
