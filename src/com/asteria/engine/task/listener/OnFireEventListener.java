package com.asteria.engine.task.listener;

import com.asteria.engine.task.Task;

/**
 * A listener that will repeatedly fire logic until some sort of event occurs.
 * 
 * @author lare96
 */
public abstract class OnFireEventListener extends Task {

    /**
     * Create a new {@link OnFireEventListener}.
     * 
     * @param rate
     *            the rate in which the logic will be fired.
     */
    public OnFireEventListener(int rate) {
        super(rate, true);
    }

    /**
     * Create a new {@link OnFireEventListener}.
     */
    public OnFireEventListener() {
        this(1);
    }

    /**
     * Will repeatedly fire the logic until this boolean is flagged.
     * 
     * @return true if this listener should stop firing the logic, false if this
     *         listener should keep firing the logic.
     */
    public abstract boolean listenFor();

    /** The logic that will be fired until <code>listenFor()</code> is flagged. */
    public abstract void run();

    @Override
    public void execute() {

        // Check if the condition has been flagged.
        if (listenFor()) {

            // It has, so we cancel this listener.
            this.cancel();
            return;
        }

        // Otherwise we run the logic.
        this.run();
    }
}
