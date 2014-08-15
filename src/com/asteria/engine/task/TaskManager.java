package com.asteria.engine.task;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Utility methods to manage stored pending and active tasks.
 * 
 * @author lare96
 */
public final class TaskManager {

    /** A queue of pending {@link Task}s waiting to be registered. */
    private static Queue<Task> pendingTasks = new LinkedList<>();

    /**
     * A list of already active {@link Task}s being processed. We use a linked
     * list instead of an arraylist because we only need assertion and iterator
     * removal which linkedlist has O(1) time complexities for, unlike an
     * arraylist which has to resize periodically and has a time complexity of
     * O(n) for iterator removal.
     */
    private static List<Task> activeTasks = new LinkedList<>();

    /**
     * Adds pending tasks to the list of active tasks, fires tasks that are
     * ready to be ran, and removes tasks that have been canceled.
     */
    public static void tick() {

        // Add all of the pending tasks to the active list only if they are
        // running.
        Task t;

        while ((t = pendingTasks.poll()) != null) {
            if (t.isRunning()) {
                activeTasks.add(t);
            }
        }

        // Iterate through every single task and perform processing on it.
        Iterator<Task> it = activeTasks.iterator();

        while (it.hasNext()) {
            t = it.next();

            if (!t.isRunning()) {
                it.remove();
                continue;
            }
            t.process(it);
        }
    }

    /**
     * Submit a new {@link Task} to be added to the <code>pendingTasks</code>
     * queue.
     * 
     * @param task
     *            the new task to submit to the queue.
     */
    public static void submit(Task task) {

        // Check if this task is running first.
        if (!task.isRunning()) {
            throw new IllegalStateException(
                "Cannot submit a task that is not running!");
        }

        // Fire the task before adding it, if needed.
        if (task.isInitialRun()) {
            task.fire();
        }

        // Add the task to the queue.
        pendingTasks.add(task);
    }

    /**
     * Cancels all of the currently registered {@link Task}s.
     */
    public static void cancelAllTasks() {
        pendingTasks.stream().forEach(t -> t.cancel());
        activeTasks.stream().forEach(t -> t.cancel());
    }

    /**
     * Stops all {@link Task}s with this bound key.
     * 
     * @param key
     *            the key to stop all tasks with.
     */
    public static void cancelTasks(Object key) {
        activeTasks.stream().filter(t -> t.getKey().equals(key)).forEach(
            t -> t.cancel());
    }

    /**
     * Retrieves a list of {@link Task}s with this bound key.
     * 
     * @param key
     *            the key that tasks will be retrieved with.
     * @return a list of tasks with this bound key.
     */
    public static LinkedList<Task> retrieveTasks(Object key) {
        LinkedList<Task> tasks = new LinkedList<>();
        activeTasks.stream().filter(t -> t.getKey().equals(key)).forEach(
            t -> tasks.add(t));
        return tasks;
    }

    /**
     * Gets an unmodifiable list of all of the active {@link Task}s.
     * 
     * @return an unmodifiable list of all of the active tasks.
     */
    public static List<Task> retrieveActiveTasks() {
        return Collections.unmodifiableList(activeTasks);
    }

    /**
     * Gets an unmodifiable queue of all of the {@link Task}s awaiting
     * registration.
     * 
     * @return an unmodifiable queue of all of the tasks awaiting registration.
     */
    public static Collection<Task> retrievePendingtasks() {
        return Collections.unmodifiableCollection(pendingTasks);
    }

    private TaskManager() {}
}
