package com.asteria.engine;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

/**
 * A {@link ThreadFactory} used for the creation of all threads throughout the
 * entire server. This factory can be used in conjunction with {@link Executor}s
 * or even for just raw {@link Thread}s.
 * 
 * @author lare96
 */
public class ThreadProvider implements ThreadFactory {

    /** The name of threads prepared by this factory. */
    private final String name;

    /** The priority of threads prepared by this factory. */
    private final int priority;

    /** If threads prepared by this factory are daemon. */
    private final boolean daemon;

    /**
     * Create a new {@link ThreadProvider}.
     * 
     * @param name
     *            the name of threads prepared by this factory.
     * @param priority
     *            the priority of threads prepared by this factory.
     * @param daemon
     *            if threads prepared by this factory are daemon.
     */
    public ThreadProvider(String name, int priority, boolean daemon) {
        this.name = Objects.requireNonNull(name);
        this.priority = priority;
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setName(name);
        thread.setPriority(priority);
        thread.setDaemon(daemon);
        return thread;
    }

    @Override
    public String toString() {
        return "THREAD FACTORY[name= " + name + ", priority= " + priority + ", daemon= " + daemon + "]";
    }
}
