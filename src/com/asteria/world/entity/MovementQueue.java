package com.asteria.world.entity;

import java.util.Deque;
import java.util.LinkedList;

import com.asteria.engine.task.Task;
import com.asteria.engine.task.TaskFactory;
import com.asteria.util.Utility;
import com.asteria.world.entity.Entity.EntityType;
import com.asteria.world.entity.player.Player;
import com.asteria.world.map.Position;

/**
 * Handles the movement of an {@link Entity}.
 * 
 * @author blakeman8192
 * @author lare96
 */
public class MovementQueue {

    /** The entity trying to move. */
    private final Entity entity;

    /** Queue of waypoints for the entity. */
    private Deque<Point> waypoints = new LinkedList<Point>();

    /** The task for following other entities. */
    private Task followTask;

    /** If your run is toggled. */
    private boolean runToggled = false;

    /** If the current path is a run path. */
    private boolean runPath = false;

    /** If this entity's movement is locked. */
    private boolean lockMovement;

    /**
     * Creates a new {@link MovementQueue}.
     * 
     * @param entity
     *            the entity to create the new movement queue for.
     */
    public MovementQueue(Entity entity) {
        this.entity = entity;
    }

    /**
     * Handle movement processing for this entity.
     */
    public void execute() {

        // No processing needed if movement is locked.
        if (lockMovement || entity.isFrozen()) {
            return;
        }

        // Handle the movement.
        Point walkPoint = null;
        Point runPoint = null;

        walkPoint = waypoints.poll();

        if (runToggled) {
            runPoint = waypoints.poll();
        }

        // Decide if this is a run path or not.
        runPath = runPoint != null;

        // Handle run energy restoration.
        if (!runPath && entity.type() == EntityType.PLAYER) {
            ((Player) entity).restoreRunEnergy();
        }

        // Walk if this is a walk point.
        if (walkPoint != null && walkPoint.getDirection() != -1) {
            int x = Utility.DIRECTION_DELTA_X[walkPoint.getDirection()];
            int y = Utility.DIRECTION_DELTA_Y[walkPoint.getDirection()];

            if (entity.isFollowing() && entity.getFollowEntity() != null) {
                if (entity.getPosition().clone().move(x, y)
                        .equals(entity.getFollowEntity().getPosition())) {
                    return;
                }
            }

            entity.getPosition().move(x, y);
            entity.setPrimaryDirection(walkPoint.getDirection());
            entity.setLastDirection(walkPoint.getDirection());

            if (entity.type() == EntityType.PLAYER) {
                ((Player) entity).displayInterfaces();
            }
        }

        // Run if this is a run point.
        if (runPoint != null && runPoint.getDirection() != -1) {
            int x = Utility.DIRECTION_DELTA_X[runPoint.getDirection()];
            int y = Utility.DIRECTION_DELTA_Y[runPoint.getDirection()];

            if (entity.isFollowing() && entity.getFollowEntity() != null) {
                if (entity.getPosition().clone().move(x, y)
                        .equals(entity.getFollowEntity().getPosition())) {
                    return;
                }
            }

            if (entity.type() == EntityType.PLAYER) {
                Player player = (Player) entity;
                if (player.getRunEnergy() > 0) {
                    player.decrementRunEnergy();
                    player.displayInterfaces();
                } else {
                    runToggled = false;
                    player.getPacketBuilder().sendConfig(173, 0);
                }
            }

            entity.getPosition().move(x, y);
            entity.setSecondaryDirection(runPoint.getDirection());
            entity.setLastDirection(runPoint.getDirection());
        }

        // Check for region changes.
        if (entity.type() == EntityType.PLAYER) {
            int deltaX = entity.getPosition().getX()
                    - entity.getCurrentRegion().getRegionX() * 8;
            int deltaY = entity.getPosition().getY()
                    - entity.getCurrentRegion().getRegionY() * 8;

            if (deltaX < 16 || deltaX >= 88 || deltaY < 16 || deltaY > 88) {
                ((Player) entity).getPacketBuilder().sendMapRegion();
            }
        }
    }

    /**
     * Allow the entity to walk to a certain position point relevant to its
     * current position.
     * 
     * @param addX
     *            the amount of spaces to walk to the x.
     * @param addY
     *            the amount of spaces to walk to the y.
     */
    public void walk(int addX, int addY) {
        reset();
        addToPath(new Position(entity.getPosition().getX() + addX, entity
                .getPosition().getY() + addY));
        finish();
    }

    /**
     * Allow the entity to walk to a certain position point not relevant to its
     * current position.
     * 
     * @param position
     *            the position the entity is moving too.
     */
    public void walk(Position position) {
        reset();
        addToPath(position);
        finish();
    }

    /**
     * Resets the walking queue.
     */
    public void reset() {
        runPath = false;
        waypoints.clear();

        // Set the base point as this position.
        Position p = entity.getPosition();
        waypoints.add(new Point(p.getX(), p.getY(), -1));
    }

    /**
     * Finishes the current path.
     */
    public void finish() {
        waypoints.removeFirst();
    }

    /**
     * Returns if the walking queue is finished or not.
     */
    public boolean isMovementDone() {
        return waypoints.size() == 0;
    }

    /**
     * Adds a position to the path.
     * 
     * @param position
     *            the position.
     */
    public void addToPath(Position position) {
        if (waypoints.size() == 0) {
            reset();
        }
        Point last = waypoints.peekLast();
        int deltaX = position.getX() - last.getX();
        int deltaY = position.getY() - last.getY();
        int max = Math.max(Math.abs(deltaX), Math.abs(deltaY));
        for (int i = 0; i < max; i++) {
            if (deltaX < 0) {
                deltaX++;
            } else if (deltaX > 0) {
                deltaX--;
            }
            if (deltaY < 0) {
                deltaY++;
            } else if (deltaY > 0) {
                deltaY--;
            }
            addStep(position.getX() - deltaX, position.getY() - deltaY);
        }
    }

    /**
     * Adds a step.
     * 
     * @param x
     *            the X coordinate
     * @param y
     *            the Y coordinate
     */
    private void addStep(int x, int y) {
        if (waypoints.size() >= 100) {
            return;
        }
        Point last = waypoints.peekLast();
        int deltaX = x - last.getX();
        int deltaY = y - last.getY();
        int direction = Utility.direction(deltaX, deltaY);
        if (direction > -1) {
            waypoints.add(new Point(x, y, direction));
        }
    }

    /**
     * Freezes this entity's movement for the desired time. Please note that
     * this is different from <code>lockMovement</code> in the sense that a
     * message will be sent whenever the player tries to move.
     * 
     * @param delay
     *            the desired time to freeze this player for.
     * @param time
     *            the desired time unit.
     */
    public void freeze(long delay) {
        entity.setFreezeDelay(delay);
        entity.getFreezeTimer().reset();
        reset();
    }

    /**
     * Prompts this entity to follow another entity. Pathfinding has not been
     * added yet so entities will clip through objects in order to reach the
     * destination of the leader.
     * 
     * @param leader
     *            the entity that this entity is being prompted to follow.
     */
    public void follow(final Entity leader) {

        // If we are currently following, stop before following someone else.
        if (followTask != null) {
            if (entity.isFollowing() && !entity.getFollowEntity()
                    .equals(leader)) {
                entity.faceEntity(null);
                followTask.cancel();
                entity.setFollowing(false);
                entity.setFollowEntity(null);
            } else if (entity.isFollowing() && entity.getFollowEntity().equals(
                    leader)) {
                return;
            }
        }

        if (followTask == null || !followTask.isRunning()) {

            // Prepare this entity for following.
            entity.setFollowing(true);
            entity.setFollowEntity(leader);

            // Build the task that will be scheduled when following.
            followTask = new Task(1, true) {
                @Override
                public void fire() {

                    // Check if we can still follow the leader.
                    if (!entity.isFollowing()
                            || !entity.getPosition().withinDistance(
                                    leader.getPosition(), 20)
                            || entity.isDead() || leader.isDead()) {
                        entity.faceEntity(null);
                        entity.setFollowing(false);
                        entity.setFollowEntity(null);
                        this.cancel();
                        return;
                    }

                    // Face the leader.
                    entity.faceEntity(leader);

                    // Block if our movement is locked.
                    if (entity.getMovementQueue().isLockMovement()
                            || entity.isFrozen()) {
                        return;
                    }

                    // If we are on the same position as the leader then move
                    // away.
                    if (entity.getPosition().equals(
                            leader.getPosition().clone())) {
                        entity.getMovementQueue().reset();

                        int x = entity.getPosition().getX();
                        int y = entity.getPosition().getY();
                        int z = entity.getPosition().getZ();

                        switch (Utility.inclusiveRandom(3)) {
                        case 0:
                            if (/* entity.canMove(-1, 0) && */true) {
                                entity.getMovementQueue().walk(
                                        new Position(x - 1, y, z));
                            }
                            break;
                        case 1:
                            if (/* entity.canMove(-1, 0) && */true) {
                                entity.getMovementQueue().walk(
                                        new Position(x + 1, y, z));
                            }
                            break;
                        case 2:
                            if (/* entity.canMove(-1, 0) && */true) {
                                entity.getMovementQueue().walk(
                                        new Position(x, y - 1, z));
                            }
                            break;

                        case 3:
                            if (/* entity.canMove(-1, 0) && */true) {
                                entity.getMovementQueue().walk(
                                        new Position(x, y + 1, z));
                            }
                            break;
                        }
                        return;
                    }

                    // Check if we are within distance to attack for combat.
                    if (entity.getCombatBuilder().isAttacking()
                            && entity.getPosition().withinDistance(
                                    entity.getCombatBuilder().getVictim()
                                            .getPosition(),
                                    entity.getCombatBuilder().getStrategy()
                                            .attackDistance(entity))) {
                        entity.getMovementQueue().reset();
                        return;
                    }

                    // If we are within 1 square we don't need to move.
                    if (entity.getPosition().withinDistance(
                            leader.getPosition(), 1)) {
                        return;
                    }

                    // We are more than 1 square away, we can move toward the
                    // leader.
                    int x = leader.getPosition().getX();
                    int y = leader.getPosition().getY();
                    // ClippedPathFinder.getPathFinder().findRoute(entity,
                    // x, y, true, 0, 0);
                    entity.getMovementQueue().walk(
                            new Position(x, y, entity.getPosition().getZ()));
                }
            };

            // Then submit the actual task.
            TaskFactory.submit(followTask);
        }
    }

    /**
     * Toggles the running flag.
     * 
     * @param runToggled
     *            the flag.
     */
    public void setRunToggled(boolean runToggled) {
        this.runToggled = runToggled;
    }

    /**
     * Gets whether or not run is toggled.
     * 
     * @return run toggled.
     */
    public boolean isRunToggled() {
        return runToggled;
    }

    /**
     * Toggles running for the current path only.
     * 
     * @param runPath
     *            the flag.
     */
    public void setRunPath(boolean runPath) {
        this.runPath = runPath;
    }

    /**
     * Gets whether or not we're running for the current path.
     * 
     * @return running.
     */
    public boolean isRunPath() {
        return runPath;
    }

    /**
     * Gets whether or not this entity is 'frozen'.
     * 
     * @return true if this entity cannot move.
     */
    public boolean isLockMovement() {
        return lockMovement;
    }

    /**
     * Sets if this entity can move or not.
     * 
     * @param lockMovement
     *            true if this entity cannot move.
     */
    public void setLockMovement(boolean lockMovement) {
        this.lockMovement = lockMovement;
    }

    /**
     * An internal Position type class with support for direction.
     * 
     * @author blakeman8192
     */
    private class Point extends Position {

        /** The direction. */
        private int direction;

        /**
         * Creates a new Point.
         * 
         * @param x
         *            the X coordinate.
         * @param y
         *            the Y coordinate.
         * @param direction
         *            the direction to this point.
         */
        public Point(int x, int y, int direction) {
            super(x, y);
            setDirection(direction);
        }

        /**
         * Sets the direction.
         * 
         * @param direction
         *            the direction.
         */
        public void setDirection(int direction) {
            this.direction = direction;
        }

        /**
         * Gets the direction.
         * 
         * @return the direction.
         */
        public int getDirection() {
            return direction;
        }
    }
}
