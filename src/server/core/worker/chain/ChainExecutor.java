package server.core.worker.chain;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import server.core.worker.TaskFactory;
import server.core.worker.WorkRate;
import server.core.worker.Worker;

/**
 * A {@link ChainExecutor} holds a queue of {@link ChainWorker}s that can be ran
 * in first in first out order. When logic from a worker is executed the next
 * worker waits for its specified delay and then executes its logic, and so on
 * (like a chain) until the end is reached and the executor automatically shuts
 * down (and can be re-ran after). <br>
 * <br>
 * 
 * Factory executors can prove to be extremely useful when doing things that
 * require a lengthy amount of precisely executed workers in a specific order.
 * This is because it only uses <b>one</b> worker to run however many workers it
 * needs to run (rather than having the same amount of workers as there are
 * tasks, which can be a lot!). <br>
 * <br>
 * 
 * An example of usage is provided below: <br>
 * <br>
 * <br>
 * 
 * 
 * If you had this factory executor and following workers appended:
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
 * And the executor would shut down allowing for more workers to be appended to
 * the internal queue and the chance to be ran again.
 * 
 * @author lare96
 */
public class ChainExecutor {

    /** Queue of internal workers in this factory executor. */
    private Queue<ChainWorker> internalWorkers = new LinkedList<ChainWorker>();

    /** A temporary queue of workers that will be use for polling operations. */
    private Queue<ChainWorker> operationWorkers = new LinkedList<ChainWorker>();

    /** The name of this factory executor. */
    private String name = "factory-executor";

    /** If this factory executor is running. */
    private boolean runningExecutor;

    /** If the internal queue should be emptied on shutdown. */
    private boolean shouldEmpty;

    /** The amount of delays passed. */
    private int delayPassed;

    /** The rate to fire tasks at. */
    private WorkRate fireRate = WorkRate.DEFAULT;

    /**
     * Create a new {@link ChainExecutor}.
     * 
     * @param name
     *        the name desired for this factory executor.
     * @param fireRate
     *        the rate to fire tasks at.
     */
    public ChainExecutor(String name, WorkRate fireRate) {
        this.name = name;
        this.fireRate = fireRate;
    }

    /**
     * Create a new {@link ChainExecutor}.
     * 
     * @param name
     *        the name desired for this factory executor.
     */
    public ChainExecutor(String name) {
        this.name = name;
    }

    /**
     * Create a new {@link ChainExecutor}.
     * 
     * @param fireRate
     *        the rate to fire tasks at.
     */
    public ChainExecutor(WorkRate fireRate) {
        this.fireRate = fireRate;
    }

    /**
     * Create a new {@link ChainExecutor}.
     */
    public ChainExecutor() {

    }

    /**
     * Runs this factory executor by using a single delayed task to schedule and
     * execute the entire chain. Once the factory executor is ran, no new tasks
     * can be appended to the internal queue unless the factory executor is
     * canceled or shutdown.
     */
    public void run() {

        /** Makes sure we aren't running an empty executor. */
        if (internalWorkers.isEmpty()) {
            throw new IllegalStateException("[" + this.getName() + "]: Empty task executors cannot be ran!");
        }

        /** Sets the flag that determines if this factory executor is running. */
        runningExecutor = true;

        /** Sets the temporary workers to the internal queue. */
        operationWorkers.addAll(internalWorkers);

        /** Schedules all of the temporary workers in chronological order. */
        TaskFactory.getFactory().submit(new Worker(1, false, fireRate) {
            @Override
            public void fire() {

                /** Shutdown if this executor has been canceled. */
                if (!isRunningExecutor()) {
                    this.cancel();
                    return;
                }

                /** Retrieves the next worker in this chain without removing it. */
                ChainWorker e = operationWorkers.peek();

                /**
                 * If a worker exists check if it is ready for execution. If the
                 * worker is ready to be executed do so and remove the worker
                 * from the chain.
                 */
                if (e != null) {
                    delayPassed++;

                    if (delayPassed == e.delay()) {
                        e.fire();
                        operationWorkers.remove();
                        delayPassed = 0;
                    }

                    /**
                     * If a worker does not exist the chain has finished and
                     * therefore will shutdown.
                     */
                } else {
                    this.cancel();
                    return;
                }
            }

            @Override
            public void fireOnCancel() {
                shutdown();
            }
        });
    }

    /**
     * Cancels and shuts down this factory executor during runtime. It is
     * recommended that you avoid prematurely canceling factory executors and
     * wait for them to complete and shutdown. The <code>shutdown()</code>
     * method is called shortly after this is invoked.
     */
    public void halt() {

        /**
         * Make sure this executor isn't already canceled or shutdown before
         * canceling.
         */
        if (!this.isRunningExecutor()) {
            throw new IllegalStateException("[" + this.getName() + "]: You cannot cancel an executor which has already been shutdown!");
        }

        /** Cancels this executor. */
        runningExecutor = false;
    }

    /**
     * Completely shuts this executor down. This allows it to accept more
     * workers and may clear the internal queue depending on the conditions set.
     */
    private void shutdown() {

        /** Cancels this factory executor. */
        runningExecutor = false;

        /**
         * Empties the internal queue depending on if the condition was set.
         */
        if (isShouldEmpty()) {
            internalWorkers.clear();
        }

        /** Clears the temporary workers. */
        operationWorkers.clear();
    }

    /**
     * Append a new worker to the executor's chain.
     * 
     * @param worker
     *        the worker to append to the chain.
     */
    public void append(ChainWorker worker) {

        /** Make sure this executor isn't running. */
        if (isRunningExecutor()) {
            throw new IllegalStateException("[" + this.getName() + "]: Cannot add worker to a running executor!");
        }

        /** Make sure the worker being appended isn't malformed. */
        if (worker == null) {
            throw new IllegalStateException("[" + this.getName() + "]: Cannot add malformed worker with a value of null to a executor!");
        }

        /** Make sure the worker being appended has a positive delay. */
        if (worker.delay() < 1) {
            throw new IllegalStateException("[" + this.getName() + "]: Cannot add worker with a delay value of below 1 to a executor!");
        }

        /** Append the new worker to the chain. */
        internalWorkers.add(worker);
    }

    /**
     * Append new workers to the executor's chain.
     * 
     * @param workers
     *        the workers to append to the chain.
     */
    public void appendAll(Collection<? extends ChainWorker> workers) {

        /** Make sure this executor isn't running. */
        if (this.isRunningExecutor()) {
            throw new IllegalStateException("[" + this.getName() + "]: Cannot add workers to a running executor!");
        }

        /** Make sure the workers being appended aren't malformed. */
        if (workers == null || workers.contains(null)) {
            throw new IllegalStateException("[" + this.getName() + "]: Cannot add malformed workers with a value of null to a executor!");
        }

        /** Make sure the workers being appended have a positive delay. */
        for (ChainWorker e : workers) {
            if (e.delay() < 1) {
                throw new IllegalStateException("[" + this.getName() + "]: Cannot add workers with delay values of below 1 to a executor!");
            }
        }

        /** Append the new workers to the chain. */
        internalWorkers.addAll(workers);
    }

    /**
     * Append new workers to the executor's chain.
     * 
     * @param workers
     *        the workers to append to the chain.
     */
    public void appendAll(ChainWorker[] workers) {

        /** Make sure this executor isn't running. */
        if (this.isRunningExecutor()) {
            throw new IllegalStateException("[" + this.getName() + "]: Cannot add workers to a running executor!");
        }

        for (ChainWorker e : workers) {

            /** Make sure the workers being appended aren't malformed. */
            if (e == null) {
                throw new IllegalStateException("[" + this.getName() + "]: Cannot add malformed workers with a value of null to a executor!");
            }

            /** Make sure the workers being appended have a positive delay. */
            if (e.delay() < 1) {
                throw new IllegalStateException("[" + this.getName() + "]: Cannot add workers with delay values of below 1 to a executor!");
            }

            /** Append the new workers to the chain. */
            internalWorkers.add(e);
        }
    }

    /**
     * Gets the name of this {@link ChainExecutor}.
     * 
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets a new name for this {@link ChainExecutor}.
     * 
     * @param name
     *        the new name for this {@link ChainExecutor}.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets if this {@link ChainExecutor} should be emptied when it is canceled
     * and/or shutdown.
     * 
     * @return true if it should be emptied.
     */
    public boolean isShouldEmpty() {
        return shouldEmpty;
    }

    /**
     * Determine whether this {@link ChainExecutor} should be emptied when it is
     * shutdown.
     * 
     * @param shouldEmpty
     *        if this {@link ChainExecutor} should be emptied when it is
     *        canceled and/or shutdown.
     */
    public void setShouldEmpty(boolean shouldEmpty) {
        this.shouldEmpty = shouldEmpty;
    }

    /**
     * Gets if this {@link ChainExecutor} is running or not.
     * 
     * @return true if this {@link ChainExecutor} is running.
     */
    public boolean isRunningExecutor() {
        return runningExecutor;
    }
}
