package server.core;

import java.util.concurrent.ThreadFactory;

/**
 * A thread factory that will be used to prepare threads used on the
 * {@link Rs2Engine}.
 * 
 * @author lare96
 */
public class GameThreadFactory implements ThreadFactory {

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.setName(Rs2Engine.class.getName());
        return thread;
    }
}
