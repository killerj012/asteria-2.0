package server.core;

/**
 * An exception thrown when a {@link Service} is rejected by a
 * {@link PriorityServicePool}.
 * 
 * @author lare96
 */
public class ServiceDeniedException extends RuntimeException {

    /**
     * Create a new {@link ServiceDeniedException}.
     * 
     * @param service
     *        the service throwing this exception.
     * @param reason
     *        the reason this service was rejected.
     */
    public ServiceDeniedException(Service service, String reason) {
        super("Service [" + service.name() + "] rejected! - " + reason);
    }

    /** The generated serial version UID. */
    private static final long serialVersionUID = 3292401103671200953L;
}
