package server.core.worker;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Contains utility methods to manage stored pending and active workers.
 * 
 * @author lare96
 */
public final class TaskFactory {

    /** The singleton instance. */
    private static TaskFactory singleton = new TaskFactory();

    /** A queue of pending {@link Worker}s waiting to be registered. */
    private static Queue<Worker> pendingWorkers = new LinkedList<Worker>();

    /** A list of already registered {@link Worker}s being processed. */
    // XXX: We use a linked list instead of an arraylist because we only need
    // assertion and iterator removal which linkedlist has O(1) time
    // complexities for, unlike arraylist which has to resize and has a time
    // complexity of O(n) for iterator removal. Lengthy amounts of workers
    // should process a lot faster as a result.
    private static LinkedList<Worker> workers = new LinkedList<Worker>();

    /** So this class cannot be instantiated. */
    private TaskFactory() {
    }

    /**
     * Adds new pending workers, fires registered workers awaiting execution,
     * and removes workers that have been canceled. This also ticks workers that
     * don't need to be fired or removed yet.
     */
    public void tick() {

        /** Add pending workers to the active list. */
        Worker worker;

        while ((worker = pendingWorkers.poll()) != null) {

            /** Add workers only if they are still running! */
            if (worker.isRunning()) {
                workers.add(worker);
            }
        }

        /** Iterate and process all of the active services. */
        for (Iterator<Worker> it = workers.iterator(); it.hasNext();) {
            worker = it.next();

            if (worker == null) {
                continue;
            }

            if (!worker.isRunning()) {
                it.remove();
                continue;
            }

            /** Process each worker individually. */
            worker.process(it);
        }
    }

    /**
     * Submit a new {@link Worker} to be added to the
     * <code>pendingWorkers</code> queue.
     * 
     * @param worker
     *            the new worker to submit to the queue.
     */
    public void submit(Worker worker) {
        if (worker.isInitialRun()) {
            worker.fire();
        }

        pendingWorkers.add(worker);
    }

    /**
     * Cancels all of the currently registered {@link Worker}s.
     */
    public void cancelAllWorkers() {
        for (Worker c : workers) {
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
     *            the key to stop all workers with.
     */
    public void cancelWorkers(Object key) {
        for (Worker c : workers) {
            if (c == null || c.getKey() == null) {
                continue;
            }

            if (c.getKey().equals(key)) {
                c.cancel();
            }
        }
    }

    /**
     * Retrieves a list of {@link Worker}s with this key attachment.
     * 
     * @param key
     *            the workers with this key that will be added to the list.
     * @return a list of workers with this key attachment.
     */
    public LinkedList<Worker> retrieveWorkers(Object key) {
        LinkedList<Worker> tasks = new LinkedList<Worker>();

        for (Worker c : workers) {
            if (c == null || c.getKey() == null) {
                continue;
            }

            if (c.getKey().equals(key)) {
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
        return Collections.unmodifiableList(workers);
    }

    /**
     * Gets an unmodifiable queue of all of the {@link Worker}s awaiting
     * registration.
     * 
     * @return an unmodifiable queue of all of the workers awaiting
     *         registration.
     */
    public Collection<Worker> retrievePendingWorkers() {
        return Collections.unmodifiableCollection(pendingWorkers);
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
