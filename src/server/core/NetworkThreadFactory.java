package server.core;

import java.util.concurrent.ThreadFactory;

import server.core.net.AsynchronousReactor;

/**
 * A thread factory that will be used to prepare threads used for the
 * {@link AsynchronousReactor}.
 * 
 * @author lare96
 */
public class NetworkThreadFactory implements ThreadFactory {

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setPriority(Thread.NORM_PRIORITY);
        thread.setName(AsynchronousReactor.class.getName());
        return thread;
    }
}
