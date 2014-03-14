package server.core.worker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Manages tasks that have been submitted to the <code>workQueue</code>.
 * 
 * @author lare96
 */
public final class TaskFactory {

    /** The singleton instance. */
    private static TaskFactory singleton = new TaskFactory();

    /** A queue of {@link Worker}s waiting to be registered. */
    private static Queue<Worker> workQueue = new LinkedList<Worker>();

    /**
     * A list of already registered {@link Worker}s waiting to have their logic
     * fired.
     */
    private static List<Worker> registeredWorkers = new ArrayList<Worker>();

    /**
     * Adds new workers from the <code>workQueue</code>, fires workers
     * awaiting execution, and removes workers that have been canceled. This
     * also ticks workers that don't need to be fired or removed yet.
     */
    public void tick() {
        Worker worker;

        /** Register the queued workers! */
        while ((worker = workQueue.poll()) != null) {
            registeredWorkers.add(worker);
        }

        /** Fire any workers that need firing. */
        for (Iterator<Worker> iterator = registeredWorkers.iterator(); iterator.hasNext();) {

            /** Retrieve the next worker. */
            worker = iterator.next();

            /** Block if this worker is malformed. */
            if (worker == null) {
                continue;
            }

            /** Unregister the worker if it is no longer running. */
            if (!worker.isRunning()) {
                iterator.remove();
                continue;
            }

            /** Increment the delay for this worker. */
            worker.incrementCurrentDelay();

            /** Check if this worker is ready to fire! */
            if (worker.getDelay() == worker.getCurrentDelay() && worker.isRunning()) {

                /**
                 * Fire the logic within the worker! ... and handle any errors
                 * that might occur during execution.
                 */
                try {
                    worker.fire();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                /** Reset the delay for the worker. */
                worker.resetCurrentDelay();
            }
        }
    }

    /**
     * Submit a new {@link Worker} for registration.
     * 
     * @param worker
     *        the new worker to submit.
     */
    public void submit(Worker worker) {
        if (worker.isInitialRun()) {
            worker.fire();
        }

        workQueue.add(worker);
    }

    /**
     * Cancels all of the currently registered {@link Worker}s.
     */
    public void cancelAllWorkers() {
        for (Worker c : registeredWorkers) {
            if (c == null) {
                continue;
            }

            c.cancel();
        }
    }

    /**
     * Stops all {@link Worker}s with this key attachment.
     * 
     * @param key
     *        the key to stop all workers with.
     */
    public void cancelWorkers(Object key) {
        for (Worker c : registeredWorkers) {
            if (c == null || c.getKey() == null) {
                continue;
            }

            if (c.getKey() == key) {
                c.cancel();
            }
        }
    }

    /**
     * Retrieves a list of {@link Worker}s with this key attachment.
     * 
     * @param key
     *        the workers with this key that will be added to the list.
     * @return a list of workers with this key attachment.
     */
    public List<Worker> retrieveWorkers(Object key) {
        List<Worker> tasks = new ArrayList<Worker>();

        for (Worker c : registeredWorkers) {
            if (c == null || c.getKey() == null) {
                continue;
            }

            if (c.getKey() == key) {
                tasks.add(c);
            }
        }

        return tasks;
    }

    /**
     * Gets an unmodifiable list of all of the registered {@link Worker}s.
     * 
     * @return an unmodifiable list of all of the registered workers.
     */
    public List<Worker> retrieveRegisteredWorkers() {
        return Collections.unmodifiableList(registeredWorkers);
    }

    /**
     * Gets an unmodifiable queue of all of the {@link Worker}s awaiting
     * registration.
     * 
     * @return an unmodifiable queue of all of the workers awaiting
     *         registration.
     */
    public Queue<Worker> retrieveAwaitingWorkers() {
        return (Queue<Worker>) Collections.unmodifiableCollection(workQueue);
    }

    /**
     * Gets the singleton instance.
     * 
     * @return the singleton instance.
     */
    public static TaskFactory getFactory() {
        return singleton;
    }
}
