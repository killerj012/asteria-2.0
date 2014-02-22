package server.core.worker.listener;

import server.core.worker.Worker;

/**
 * A listener that fires logic after some sort of event occurs.
 * 
 * @author lare96
 */
public abstract class EventListener extends Worker {

    /**
     * Determines if the listener should be shut down once the logic has fired.
     */
    private boolean shutdownOnFire = true;

    /**
     * Create a new {@link EventListener}.
     * 
     * @param shutdownOnFire
     *        if the listener should be shut down once the logic has fired.
     * @param rateOfFire
     *        the rate in which the event will be checked and the logic will be
     *        fired in ticks.
     */
    public EventListener(boolean shutdownOnFire, int rateOfFire) {
        super(rateOfFire, false);
        this.shutdownOnFire = shutdownOnFire;
    }

    /**
     * Create a new {@link EventListener}.
     * 
     * @param shutdownOnFire
     *        if the listener should be shut down once the logic has fired.
     */
    public EventListener(boolean shutdownOnFire) {
        super(1, false);
        this.shutdownOnFire = shutdownOnFire;
    }

    /**
     * Create a new {@link EventListener}.
     * 
     * @param rateOfFire
     *        the rate in which the event will be checked and the logic will be
     *        fired in ticks.
     */
    public EventListener(int rateOfFire) {
        super(rateOfFire, false);
    }

    /**
     * Create a new {@link EventListener} with the default settings.
     */
    public EventListener() {
        super(1, false);
    }

    /**
     * Will block from firing the logic until this condition is unflagged.
     * 
     * @return true if this listener should keep listening, false if the event
     *         has occurred and the listener should fire the logic.
     */
    public abstract boolean listenForEvent();

    /**
     * The actual logic that will be fired once <code>listenForEvent</code> is
     * unflagged.
     */
    public abstract void run();

    @Override
    public void fire() {

        /** Block if the event has not yet occurred. */
        if (listenForEvent()) {
            return;
        }

        /** Fire the logic once the event has occurred. */
        run();

        /**
         * Shutdown the listener once the task has been ran if it is set to do
         * so, if not it will keep listening and will execute the logic every
         * 600ms (as long as the condition is unflagged).
         */
        if (shutdownOnFire) {
            this.cancel();
        }
    }
}
