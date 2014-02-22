package server.core.worker;

/**
 * All of the rates in which a worker can fire logic in.
 * 
 * @author lare96
 */
public enum WorkRate {

    /** The work rate for every worker by default (every full tick - 600ms). */
    DEFAULT(1),

    /**
     * The work rate to execute logic every approximate second (approximately
     * every 1.2 seconds).
     */
    APPROXIMATE_SECOND(2),

    /** The work rate to execute logic every exact minute (every full minute). */
    EXACT_MINUTE(100),

    /** The work rate to execute logic every exact minute (every full hour). */
    EXACT_HOUR(6000),

    /** The work rate to execute logic every exact minute (every full day). */
    EXACT_DAY(144000);

    /** The delay of this work rate in ticks. */
    private int tickRate;

    /**
     * Create a new {@link WorkRate}.
     * 
     * @param tickRate
     *        the delay of this work rate in ticks.
     */
    private WorkRate(int tickRate) {
        this.tickRate = tickRate;
    }

    /**
     * Gets the delay of this work rate in ticks.
     * 
     * @return the tick rate.
     */
    public int getTickRate() {
        return tickRate;
    }
}
