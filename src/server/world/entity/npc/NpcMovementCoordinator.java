package server.world.entity.npc;

/**
 * Periodically coordinates movement for an in-game npc.
 * 
 * @author lare96
 */
public class NpcMovementCoordinator {

    /** The npc we are coordinating movement for. */
    private Npc npc;

    /** The coordinator for coordinating movement. */
    private Coordinator coordinator;

    public NpcMovementCoordinator(Npc npc) {
        this.npc = npc;
        this.coordinator = new Coordinator();
    }

    public void setCoordinator(Coordinator coordinator) {
        this.coordinator = coordinator;
    }

    public void setCoordinate(boolean coordinate) {
        coordinator.coordinate = coordinate;
    }

    public void setRadius(int radius) {
        coordinator.radius = radius;
    }

    /**
     * A container that holds if the npc should coordinate and at what radius.
     * 
     * @author lare96
     */
    public static class Coordinator {

        private boolean coordinate;

        private int radius;

        /**
         * @return the coordinate
         */
        public boolean isCoordinate() {
            return coordinate;
        }

        /**
         * @return the radius
         */
        public int getRadius() {
            return radius;
        }
    }
}