package server.core.factory;

import java.util.concurrent.ThreadFactory;

import server.core.Rs2Engine;

/**
 * A thread factory that will be used to prepare the main game thread.
 * 
 * @author lare96
 */
public class GameThreadFactory implements ThreadFactory {

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.setName(Rs2Engine.class.getSimpleName());
        return thread;
    }
}
