package server.core.worker.listener;

import server.core.worker.Worker;

/**
 * A listener that fires logic until some sort of event occurs.
 * 
 * @author lare96
 */
public abstract class OnFireEventListener extends Worker {

    /**
     * Create a new {@link OnFireEventListener}.
     * 
     * @param rateOfFire
     *        the rate in which the event will be checked and the logic will be
     *        fired in ticks.
     */
    public OnFireEventListener(int rateOfFire) {
        super(rateOfFire, false);
    }

    /**
     * Create a new {@link OnFireEventListener}.
     */
    public OnFireEventListener() {
        super(1, false);
    }

    /**
     * Will keep firing the logic until this boolean is flagged.
     * 
     * @return true if this listener should stop firing the logic, false if this
     *         listener should keep firing the logic.
     */
    public abstract boolean fireLogicUntil();

    /**
     * The actual logic that will be fired until <code>fireLogicUntil</code>
     * is flagged.
     */
    public abstract void run();

    @Override
    public void fire() {

        /** Check if the condition has been flagged. */
        if (fireLogicUntil()) {

            /** If so stop firing logic and shutdown. */
            this.cancel();
            return;
        }

        /** Fire the logic otherwise. */
        run();
    }
}
