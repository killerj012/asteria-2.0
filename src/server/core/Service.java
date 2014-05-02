package server.core;

/**
 * A task that can be carried out by a {@link PriorityServicePool}.
 * 
 * @author lare96
 */
public interface Service extends Runnable {

    /**
     * The name of the service being carried out primarily used for debugging
     * purposes. If the service does not have a name a {@link Runnable} should
     * be used instead.
     * 
     * @return the name of the service being carried out.
     */
    public String name();
}
