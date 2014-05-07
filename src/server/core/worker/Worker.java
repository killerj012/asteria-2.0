package server.core.worker;

/**
 * A flexible dynamic worker created to carry out general game logic on the game
 * thread.
 * 
 * @author lare96
 */
public abstract class Worker {

    /** The delay for this worker (in ticks). */
    private int delay;

    /** The amount of ticks this worker has accumulated. */
    private int currentDelay;

    /** If this worker should be ran initially before being scheduled. */
    private boolean initialRun;

    /** The key bound to this worker. */
    private Object key;

    /** The rate this worker is firing logic at. */
    private WorkRate workRate;

    /** If this worker is currently running. */
    private boolean running;

    /**
     * Create a new {@link Worker} with a default <code>workRate</code> of
     * ticks.
     * 
     * @param delay
     *        the delay of this worker (in ticks).
     * @param initialRun
     *        if this worker should be ran initially before being scheduled.
     */
    public Worker(int delay, boolean initialRun) {
        this.delay = delay;
        this.initialRun = initialRun;
        this.workRate = WorkRate.DEFAULT;
        this.running = true;
    }

    /**
     * Create a new {@link Worker} with the specified work rate.
     * 
     * @param delay
     *        the delay of this worker (in ticks).
     * @param initialRun
     *        if this worker should be ran initially before being scheduled.
     * @param workRate
     *        the rate this worker is firing logic at.
     */
    public Worker(int delay, boolean initialRun, WorkRate workRate) {
        this.delay = delay * workRate.getTickRate();
        this.initialRun = initialRun;
        this.workRate = workRate;
        this.running = true;
    }

    /** The logic fired when the worker is ran. */
    public abstract void fire();

    /**
     * Method called when this task is stopped. Implementation is optional.
     */
    public void fireOnCancel() {

        /**
         * Workers can override this method to fire more logic once it has been
         * canceled.
         */
    }

    /**
     * Cancels this worker which will unregister it and stop its logic from
     * firing in the future.
     */
    public void cancel() {
        if (running) {
            this.running = false;
            fireOnCancel();
        }
    }

    /**
     * Attaches any key to this worker that can be retrieved with
     * <code>getKey()</code>. Workers <b>do not</b> have to have a key but
     * can be chained with this method for easily attaching keys on
     * registration.
     * 
     * @param key
     *        the key to attach.
     * @return this worker for chaining.
     */
    public Worker attach(Object key) {
        this.key = key;
        return this;
    }

    /**
     * Calculates the approximate time left until this worker executes (in
     * ticks).
     * 
     * @return the approximate time left until this worker executes (in ticks).
     */
    public int calculateExecutionTime() {
        return (delay - currentDelay);
    }

    /**
     * Resets the current delay.
     */
    public void resetCurrentDelay() {
        this.currentDelay = 0;
    }

    /**
     * Increments the current delay.
     */
    public void incrementCurrentDelay() {
        this.currentDelay++;
    }

    /**
     * Dynamically sets the run value to false. This should not be used in a
     * normal scenario. Do not invoke this if you do not know what you're doing!
     * 
     * @return this worker for chaining.
     */
    public Worker terminateRun() {
        this.running = false;
        return this;
    }

    /**
     * Gets the fixed delay.
     * 
     * @return the fixed delay (in ticks).
     */
    public int getDelay() {
        return delay;
    }

    /**
     * Sets a new delay for this worker. This can be used to make dynamic
     * runtime changes to the delay. <br>
     * <br>
     * Please note that the <code>workRate</code> still applies!
     * 
     * @param delay
     *        the new delay to set for this task.
     */
    public void setDelay(int delay) {
        this.delay = delay * workRate.getTickRate();
    }

    /**
     * Gets if this worker should be fired before being added to the queue of
     * workers awaiting registration.
     * 
     * @return true if it should be fired first.
     */
    public boolean isInitialRun() {
        return initialRun;
    }

    /**
     * Gets the key attached to this worker.
     * 
     * @return the key attached to this worker.
     */
    public Object getKey() {
        return key;
    }

    /**
     * Gets if this worker is running or not.
     * 
     * @return true if this worker is running.
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Gets the current delay.
     * 
     * @return the current delay (in ticks).
     */
    public int getCurrentDelay() {
        return currentDelay;
    }
}