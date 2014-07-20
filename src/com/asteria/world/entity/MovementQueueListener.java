package com.asteria.world.entity;

import java.util.Objects;

import com.asteria.engine.task.TaskFactory;
import com.asteria.engine.task.listener.EventListener;

/**
 * Uses an {@link EventListener} to append tasks to the end of {@link Entity}s
 * walking queues.
 * 
 * @author lare96
 */
public class MovementQueueListener {

    /** The entity that the action will be appended for. */
    private Entity entity;

    /** The listener that determine's when to run the action. */
    private EventListener listener;

    /**
     * Create a new {@link MovementQueueListener}.
     * 
     * @param entity
     *            the entity that the action will be appended for.
     */
    public MovementQueueListener(Entity entity) {
        this.entity = entity;
    }

    /**
     * Creates a new {@link EventListener} that will listen for the walking
     * queue to finish. Once the walking queue is finished the listener will run
     * the logic within argued task. <br>
     * <br>
     * <b>Please note that appended tasks are not guaranteed to be ran!</b> If a
     * new task is being appended while the listener is already waiting to run
     * another task, the existing listener is stopped, the old task discarded,
     * and a new listener is started to run the new task.
     * 
     * @param task
     *            the task that will be ran once the walking queue is finished.
     */
    public void append(Runnable task) {

        // Discard the existing listener before running a new one.
        discard();

        // Make sure we are not following.
        entity.setFollowing(false);

        // Build the new listener.
        listener = new MovementQueueListenerTask(entity,
                Objects.requireNonNull(task));

        // Then submit it to the task factory!
        TaskFactory.submit(listener);
    }

    /**
     * Stops the listener and discards the task bound to the listener. If the
     * listener is not running then invoking this method does nothing.
     */
    public void discard() {

        // Check if the listener is even built.
        if (listener != null) {

            // Check if the listener is running.
            if (listener.isRunning()) {

                // Passed the checks, so cancel the listener.
                listener.cancel();
            }
        }
    }

    /**
     * The {@link EventListener} implementation that will listen for the walking
     * queue to finish.
     * 
     * @author lare96
     */
    private static class MovementQueueListenerTask extends EventListener {

        /** The entity's walking queue we are listening for. */
        private Entity entity;

        /** The task that will be ran. */
        private Runnable task;

        /**
         * Create a new {@link MovementQueueListenerTask}.
         * 
         * @param entity
         *            the entity's walking queue we are listening for.
         * @param task
         *            the task that will be ran.
         */
        public MovementQueueListenerTask(Entity entity, Runnable task) {
            super(true);
            this.entity = entity;
            this.task = task;
        }

        @Override
        public boolean listenFor() {
            return !(entity.getMovementQueue().isMovementDone() || entity
                    .isUnregistered());
        }

        @Override
        public void run() {

            // Make sure the entity is still online before running the task.
            if (entity.isUnregistered()) {
                return;
            }

            // Entity is online, so run the task.
            try {
                task.run();

                // Handle any errors we come across when running the task.
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
