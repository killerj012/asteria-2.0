package com.asteria.engine.task;

/**
 * A collection of constants that hold conversion rates from their defined time
 * unit to ticks.
 * 
 * @author lare96
 */
public enum TaskTime {

    SECONDS(2),
    MINUTES(100),
    HOURS(6000),
    DAYS(144000);

    /** The conversion rate for this time unit. */
    private int conversion;

    /**
     * Create a new {@link TaskTime}.
     * 
     * @param conversion
     *            the conversion rate for this time unit.
     */
    private TaskTime(int conversion) {
        this.conversion = conversion;
    }

    /**
     * Converts the argued time into ticks.
     * 
     * @param ticks
     *            the argued time to convert into ticks.
     * @return the amount of ticks represented by this time.
     */
    public int getTicks(int time) {
        return conversion * time;
    }
}
