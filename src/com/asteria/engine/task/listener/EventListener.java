package com.asteria.engine.task.listener;

import com.asteria.engine.task.Task;

/**
 * A listener that fires logic after some sort of event occurs.
 * 
 * @author lare96
 */
public abstract class EventListener extends Task {

    /** If the listener should be shut down once the logic has fired. */
    private boolean shutdown;

    /**
     * Create a new {@link EventListener}.
     * 
     * @param shutdown
     *            if the listener should be shut down once the logic has fired.
     * @param rate
     *            the rate in which the logic will be fired.
     */
    public EventListener(boolean shutdown, int rate) {
        super(rate, true);
        this.shutdown = shutdown;
    }

    /**
     * Create a new {@link EventListener}.
     * 
     * @param shutdown
     *            if the listener should be shut down once the logic has fired.
     */
    public EventListener(boolean shutdown) {
        this(shutdown, 1);
    }

    /**
     * Create a new {@link EventListener}.
     * 
     * @param rate
     *            the rate in which the logic will be fired.
     */
    public EventListener(int rate) {
        this(true, rate);
    }

    /**
     * Create a new {@link EventListener} with the default settings.
     */
    public EventListener() {
        this(true, 1);
    }

    /**
     * The logic will not be fired until this is unflagged.
     * 
     * @return true if this listener should keep listening, false if the event
     *         has occurred and the listener should fire the logic.
     */
    public abstract boolean listenFor();

    /** The logic that will be fired once <code>listenFor</code> is unflagged. */
    public abstract void run();

    @Override
    public void fire() {

        // Don't proceed unless unflagged.
        if (listenFor()) {
            return;
        }

        // We're unflagged, fire the logic.
        try {
            this.run();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            // Shutdown if needed, otherwise keep the checking and
            // firing.
            if (shutdown) {
                this.cancel();
            }
        }
    }
}
