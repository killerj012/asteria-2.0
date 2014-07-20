package com.asteria.world.entity.npc;

import com.asteria.util.Utility;
import com.asteria.world.map.Position;

/**
 * Will make all {@link Npc}s set to coordinate, pseudo-randomly move within a
 * specified radius of their original position.
 * 
 * @author lare96
 */
public class NpcMovementCoordinator {

    /** The npc we are coordinating movement for. */
    private Npc npc;

    /** The coordinate state this npc is in. */
    private CoordinateState coordinateState;

    /** The coordinator for coordinating movement. */
    private Coordinator coordinator;

    /**
     * All of the possible coordinate states this npc can be in. This state
     * based enum is used to make the npc walk back to its original position so
     * it doesn't wander off.
     * 
     * @author lare96
     */
    public enum CoordinateState {
        HOME,
        AWAY
    }

    /**
     * Create a new {@link NpcMovementCoordinator}.
     * 
     * @param npc
     *            the npc we are coordinating movement for.
     */
    public NpcMovementCoordinator(Npc npc) {
        this.npc = npc;
        this.coordinator = new Coordinator();
        this.coordinateState = CoordinateState.HOME;
    }

    /**
     * Coordinate movement for this npc. When the npc is in the
     * <code>AWAY</code> state it will be moved back to its original position
     * and when the npc is in the <code>HOME</code> state it will be moved away
     * from its original position.
     */
    public void coordinate() {

        // Block if this coordinator isn't set to coordinate or if the npc is in
        // combat.
        if (!coordinator.isCoordinate() || npc.getCombatBuilder().isAttacking() || npc
                .getCombatBuilder().isBeingAttacked()) {
            return;
        }

        // Periodic coordinate effect.
        if (Utility.exclusiveRandom(13) == 5) {
            switch (coordinateState) {

            // Coordinate the npc away from its original position.
            case HOME:
                if (npc.getMovementQueue().isMovementDone()) {
                    npc.getMovementQueue().walk(
                            generateLocalPosition(coordinator.getRadius()));
                    coordinateState = CoordinateState.AWAY;
                }
                break;

            // Coordinate the npc back to its original position.
            case AWAY:
                if (npc.getMovementQueue().isMovementDone()) {
                    npc.getMovementQueue().walk(npc.getOriginalPosition());
                    coordinateState = CoordinateState.HOME;
                }
                break;
            }
        }
    }

    /**
     * Generates a local {@link Position} to this {@link Npc} within the given
     * radius.
     * 
     * @param radius
     *            the radius to generate the local position within.
     * @return the generated local position.
     */
    private Position generateLocalPosition(int radius) {
        switch (Utility.inclusiveRandom(3)) {

        // Northwest, north, and west directions.
        case 0:
            return new Position(
                    npc.getPosition().getX() + Utility.exclusiveRandom(radius),
                    npc.getPosition().getY() + Utility.exclusiveRandom(radius),
                    npc.getPosition().getZ());

            // Southeast, south, and east directions.
        case 1:
            return new Position(
                    npc.getPosition().getX() - Utility.exclusiveRandom(radius),
                    npc.getPosition().getY() - Utility.exclusiveRandom(radius),
                    npc.getPosition().getZ());

            // Southwest, south, and west directions.
        case 2:
            return new Position(
                    npc.getPosition().getX() + Utility.exclusiveRandom(radius),
                    npc.getPosition().getY() - Utility.exclusiveRandom(radius),
                    npc.getPosition().getZ());

            // Northeast, north, and east directions.
        case 3:
            return new Position(
                    npc.getPosition().getX() - Utility.exclusiveRandom(radius),
                    npc.getPosition().getY() + Utility.exclusiveRandom(radius),
                    npc.getPosition().getZ());

            // Invalid number, no directions.
        default:
            throw new IllegalStateException(
                    "Invalid number range! Must be 0, 1, 2, or 3.");
        }
    }

    /**
     * Set a new coordinator for this npc.
     * 
     * @param coordinator
     *            the new coordinator to set.
     */
    public void setCoordinator(Coordinator coordinator) {
        this.coordinator = coordinator;
    }

    /**
     * Set if this coordinator should coordinate.
     * 
     * @param coordinate
     *            true if this coordinator should coordinate.
     */
    public void setCoordinate(boolean coordinate) {
        coordinator.coordinate = coordinate;
    }

    /**
     * Set the radius of the coordinator.
     * 
     * @param radius
     *            the radius of the coordinator.
     */
    public void setRadius(int radius) {
        coordinator.radius = radius;
    }

    /**
     * A container that holds if the npc should coordinate and at what radius.
     * 
     * @author lare96
     */
    public static class Coordinator {

        /** If this coordinator should coordinate. */
        private boolean coordinate;

        /** The radius of the coordinator. */
        private int radius;

        /**
         * Gets if this coordinator should coordinate.
         * 
         * @return true if the coordinator is coordinating.
         */
        public boolean isCoordinate() {
            return coordinate;
        }

        /**
         * Gets the radius of the coordinator.
         * 
         * @return the radius the npc will be coordinated in.
         */
        public int getRadius() {
            return radius;
        }
    }
}