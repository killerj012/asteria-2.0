package server.world;

/**
 * A container that contains a single runnable method with any Object as its
 * parameter.
 * 
 * @author lare96
 * @param <T>
 *        The Object to use as the parameter.
 */
public interface GenericAction<T> {

    /**
     * The action that will be executed.
     * 
     * @param object
     *        the Object that will be used as the parameter.
     */
    void fireAction(final T object);
}
