package com.asteria.engine.task;

import java.util.Iterator;
import java.util.Objects;

/**
 * A dynamic cycle based task that carries out general game logic.
 * 
 * @author lare96
 */
public abstract class Task {

    /** The default key for every task. */
    public static final Object DEFAULT_KEY = new Object();

    /** The fixed delay for this task. */
    private int delay;

    /** The amount of time that this task has been waiting for. */
    private int waitingDelay;

    /** If this task should be ran straight away before being submitted. */
    private boolean initialRun;

    /** The key bound to this task. */
    private Object key;

    /**
     * If this task is currently running. When this field is unflagged the task
     * is removed from the list of active tasks and can never be added to the
     * list of pending tasks.
     */
    private boolean running;

    /**
     * Create a new {@link Task}.
     * 
     * @param delay
     *            the fixed delay for this task.
     * @param initialRun
     *            if this task should be ran straight away before being
     *            submitted.
     */
    public Task(int delay, boolean initialRun) {

        // Check if the delay is valid first.
        if (delay <= 0) {
            throw new IllegalArgumentException(
                    "The delay must be above 0! Delay was: " + delay);
        }

        // Delay is valid, so set the fields.
        this.delay = delay;
        this.initialRun = initialRun;
        this.running = true;
        this.bind(DEFAULT_KEY);
    }

    /** The logic ran when the task is fired. */
    public abstract void fire();

    /**
     * Tasks can override this method to run more logic once it has been
     * canceled.
     */
    public void fireOnCancel() {

    }

    /**
     * Called every single tick for all active task, and is used to fire tasks
     * and remove cancelled tasks. This method should never be called anywhere
     * else other than the <code>tick()</code> method in the {@link TaskFactory}
     * class or tasks will become out of sync!
     * 
     * @param it
     *            the iterator instance so we can remove cancelled tasks.
     */
    protected final void process(Iterator<Task> it) {

        // Increment the waiting delay.
        waitingDelay++;

        // Check if this task is ready to execute.
        if (waitingDelay == delay) {

            try {

                // Run the logic within the task.
                fire();
            } catch (Exception e) {

                // Print any errors we may come across.
                e.printStackTrace();
            }

            // Reset the waiting delay.
            waitingDelay = 0;

            // Remove the task if needed.
            if (!running) {
                it.remove();
            }
        }
    }

    /**
     * Cancels this task which will unregister it and stop its logic from firing
     * in the future.
     */
    public final void cancel() {
        if (running) {
            running = false;
            fireOnCancel();
        }
    }

    /**
     * Binds any key to this task that can be retrieved with
     * <code>getKey()</code>. Task instances can be chained with this method for
     * easily binding keys on registration or creation. This is a very useful
     * feature because similar or related tasks can be bound with the same key,
     * and then be retrieved and/or cancelled later on. All player related tasks
     * should be bound with the player's instance so all tasks are automatically
     * stopped on logout. Please note that keys with a value of
     * <code>null</code> are not permitted, the default value for all keys is
     * defined as <code>DEFAULT_KEY</code>.
     * 
     * @param key
     *            the key to attach.
     * @return this task for chaining.
     */
    public final Task bind(Object key) {
        this.key = Objects.requireNonNull(key,
                "Keys with a value of null not permitted!");
        return this;
    }

    /**
     * Sets a new fixed delay for this task. This can be used to make dynamic
     * runtime changes to the delay.
     * 
     * @param delay
     *            the new delay to set for this task.
     */
    public final void setDelay(int delay) {
        this.delay = delay;
    }

    /**
     * Gets if this task should be fired before being added to the queue of
     * tasks awaiting registration.
     * 
     * @return true if it should be fired first.
     */
    public final boolean isInitialRun() {
        return initialRun;
    }

    /**
     * Gets the key bound to this task.
     * 
     * @return the key bound to this task.
     */
    public final Object getKey() {
        return key;
    }

    /**
     * Gets if this task is running or not.
     * 
     * @return true if this task is running.
     */
    public final boolean isRunning() {
        return running;
    }
}