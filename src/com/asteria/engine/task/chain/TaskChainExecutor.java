package com.asteria.engine.task.chain;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import com.asteria.engine.task.Task;
import com.asteria.engine.task.TaskFactory;

/**
 * A {@link TaskChainExecutor} holds a queue of {@link TaskChain}s that are ran
 * in <i>FIFO</i> order. When a task in the chain is fired the next task in the
 * chain waits for its specified delay then fires, and so on like a chain. This
 * continues until the end of the chain is reached and then the executor will
 * automatically shut down. <br>
 * <br>
 * 
 * Chain executors can prove to be extremely useful when doing things that
 * require a lengthy amount of precisely executed tasks in a specific order.
 * This is because it only uses <b>one</b> task to run however many tasks it
 * needs to run, which means significantly less stress on the
 * {@link TaskFactory}. <br>
 * <br>
 * 
 * An example of usage is provided below: <br>
 * <br>
 * <br>
 * 
 * 
 * If you had this factory executor and following tasks appended:
 * 
 * <pre>
 * FactoryExecutor executor = new FactoryExecutor(&quot;our-factory-executor&quot;, Time.SECONDS);
 * 
 * executor.append(new ChainTask() {
 *     ... // lets just say the delay was 3 and it printed &quot;Hello world!&quot;.
 * });
 * 
 * And
 * 
 * executor.append(new ChainTask() {
 *      ... // lets just say the delay was 5 and it printed &quot;Goodbye world!&quot;.
 * });
 * </pre>
 * 
 * If you ran the executor using <code>executor.run()</code> it would result in:
 * 
 * <pre>
 * ... delay for three seconds
 * 
 * print &quot;Hello world!&quot;
 * 
 * ... delay for five seconds
 * 
 * print &quot;Goodbye world!&quot;
 * </pre>
 * 
 * And the executor would shut down allowing for more tasks to be appended to
 * the internal queue and the chance to be ran again.
 * 
 * @author lare96
 */
public class TaskChainExecutor {

    /** Queue of internal tasks in this chain executor. */
    private Queue<TaskChain> internalTasks = new LinkedList<>();

    /** The name of this chain executor. */
    private String name = "chain-executor";

    /** If this chain executor is running. */
    private boolean runningExecutor;

    /** If the internal queue should be emptied on shutdown. */
    private boolean shouldEmpty;

    /** The amount of delays passed. */
    private int delayPassed;

    /**
     * Create a new {@link TaskChainExecutor}.
     * 
     * @param name
     *            the name desired for this chain executor.
     */
    public TaskChainExecutor(String name) {
        this.name = name;
    }

    /**
     * Create a new {@link TaskChainExecutor}.
     */
    public TaskChainExecutor() {

    }

    /**
     * Runs this chain executor by using a single task to fire each member of
     * the entire chain. Once the chain executor is ran, no new tasks can be
     * appended to the internal queue unless the chain executor is canceled or
     * shutdown.
     */
    public void run() {

        // Makes sure we aren't running an empty executor.
        if (internalTasks.isEmpty()) {
            throw new IllegalStateException("[" + name
                    + "]: There are no tasks in the chain!");
        }

        // Flag this executor as running.
        runningExecutor = true;

        // Adds all internal tasks to the operation queue
        final Queue<TaskChain> operationTasks = new LinkedList<>();
        operationTasks.addAll(internalTasks);

        // The main task that will fire all of the tasks in the chain.
        TaskFactory.submit(new Task(1, false) {
            @Override
            public void fire() {

                // Shutdown if this executor has been canceled.
                if (!runningExecutor) {
                    this.cancel();
                    return;
                }

                // The chain has finished so shutdown the executor.
                if (operationTasks.isEmpty()) {
                    this.cancel();
                    return;
                }

                // Retrieves the next task in this chain without removing it. If
                // the task is ready to be executed it will do so and remove the
                // task from the chain.
                TaskChain nextTask = operationTasks.peek();

                delayPassed++;

                if (delayPassed == nextTask.delay() || nextTask.delay() == 0) {
                    nextTask.fire();
                    operationTasks.remove();
                    delayPassed = 0;
                }
            }

            @Override
            public void fireOnCancel() {
                shutdown();
            }
        });
    }

    /**
     * Cancels and shuts down this chain executor while it's running. The
     * <code>shutdown()</code> method is called shortly after this is invoked.
     */
    public void halt() {

        // Verify that this isn't already shutdown.
        if (!runningExecutor) {
            throw new IllegalStateException(
                    "["
                            + name
                            + "]: You cannot halt an executor which has already been shutdown!");
        }

        // Flag this executor as cancelled.
        runningExecutor = false;
    }

    /**
     * Completely shuts this executor down. This allows it to accept more tasks
     * and may clear the internal queue depending on the conditions set.
     */
    private void shutdown() {

        // Flag this executor as cancelled.
        runningExecutor = false;

        // Empty internal queue if needed.
        if (shouldEmpty) {
            internalTasks.clear();
        }
    }

    /**
     * Append a new task to the executor's chain.
     * 
     * @param task
     *            the task to append to the chain.
     */
    public void append(TaskChain task) {

        // Make sure this executor isn't running.
        if (!runningExecutor) {
            throw new IllegalStateException("[" + name
                    + "]: Cannot add task to a running executor!");
        }

        // Make sure the task has a positive delay.
        if (task.delay() < 0) {
            throw new IllegalArgumentException(
                    "["
                            + name
                            + "]: Cannot add task with delay value of below 0 to a executor!");
        }

        // Append the new task to the chain.
        internalTasks.add(task);
    }

    /**
     * Append new tasks to the executor's chain.
     * 
     * @param tasks
     *            the tasks to append to the chain.
     */
    public void appendAll(Collection<TaskChain> tasks) {
        for (TaskChain t : tasks) {
            append(t);
        }
    }

    /**
     * Append new tasks to the executor's chain.
     * 
     * @param tasks
     *            the tasks to append to the chain.
     */
    public void appendAll(TaskChain[] tasks) {
        appendAll(Arrays.asList(tasks));
    }

    /**
     * Gets the name of this executor.
     * 
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets a new name for this executor.
     * 
     * @param name
     *            the new name for this executor.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets if this executor should be emptied when it is canceled and/or
     * shutdown.
     * 
     * @return true if it should be emptied.
     */
    public boolean isShouldEmpty() {
        return shouldEmpty;
    }

    /**
     * Determine whether this executor should be emptied when it is shutdown.
     * 
     * @param shouldEmpty
     *            if this executor should be emptied when it is canceled and/or
     *            shutdown.
     */
    public void setShouldEmpty(boolean shouldEmpty) {
        this.shouldEmpty = shouldEmpty;
    }

    /**
     * Gets if this executor is running or not.
     * 
     * @return true if this executor is running.
     */
    public boolean isRunningExecutor() {
        return runningExecutor;
    }
}
