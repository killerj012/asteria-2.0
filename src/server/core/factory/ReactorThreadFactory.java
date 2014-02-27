package server.core.factory;

import java.util.concurrent.ThreadFactory;

import server.core.net.AsynchronousReactor;

/**
 * A thread factory that will be used to prepare the reactor thread.
 * 
 * @author lare96
 */
public class ReactorThreadFactory implements ThreadFactory {

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setPriority(Thread.NORM_PRIORITY);
        thread.setName(AsynchronousReactor.class.getSimpleName());
        return thread;
    }
}
