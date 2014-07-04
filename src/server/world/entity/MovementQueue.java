package server.world.entity;

import java.util.Deque;
import java.util.LinkedList;

import server.core.worker.TaskFactory;
import server.core.worker.WorkRate;
import server.core.worker.Worker;
import server.util.Misc;
import server.world.entity.UpdateFlags.Flag;
import server.world.entity.npc.Npc;
import server.world.entity.player.Player;
import server.world.map.Position;

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

        if (lockMovement) {
            return;
        }

        Point walkPoint = null;
        Point runPoint = null;

        /** Handle the movement. */
        walkPoint = waypoints.poll();
        if (isRunToggled()) {
            runPoint = waypoints.poll();
        }

        /** Decide if this is a run path or not. */
        if (runPoint != null) {
            this.setRunPath(true);
        } else {
            this.setRunPath(false);
        }

        /** Walk if this is a walk point. */
        if (walkPoint != null && walkPoint.getDirection() != -1) {
            int x = Misc.DIRECTION_DELTA_X[walkPoint.getDirection()];
            int y = Misc.DIRECTION_DELTA_Y[walkPoint.getDirection()];

            if (entity.isFollowing() && entity.getFollowingEntity() != null) {
                if (entity.getPosition().clone().move(x, y)
                        .equals(entity.getFollowingEntity().getPosition())) {
                    return;
                }
            }

            entity.getPosition().move(x, y);
            entity.setPrimaryDirection(walkPoint.getDirection());
            entity.setLastDirection(walkPoint.getDirection());

            if (entity instanceof Player) {
                Player player = (Player) entity;
                player.displayInterfaces();
            }
        }

        /** Run if this is a run point. */
        if (runPoint != null && runPoint.getDirection() != -1) {
            int x = Misc.DIRECTION_DELTA_X[runPoint.getDirection()];
            int y = Misc.DIRECTION_DELTA_Y[runPoint.getDirection()];

            if (entity.isFollowing() && entity.getFollowingEntity() != null) {
                if (entity.getPosition().clone().move(x, y)
                        .equals(entity.getFollowingEntity().getPosition())) {
                    return;
                }
            }

            if (entity instanceof Player) {
                Player player = (Player) entity;
                if (player.getRunEnergy() > 0) {
                    player.decrementRunEnergy();
                    player.displayInterfaces();
                } else {
                    setRunToggled(false);
                    player.getPacketBuilder().sendConfig(173, 0);
                }
            }

            entity.getPosition().move(x, y);
            entity.setSecondaryDirection(runPoint.getDirection());
            entity.setLastDirection(runPoint.getDirection());
        }

        /** Check for region changes. */
        int deltaX = entity.getPosition().getX()
                - entity.getCurrentRegion().getRegionX() * 8;
        int deltaY = entity.getPosition().getY()
                - entity.getCurrentRegion().getRegionY() * 8;
        if (deltaX < 16 || deltaX >= 88 || deltaY < 16 || deltaY > 88) {
            if (!(entity instanceof Npc)) {
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
        this.reset();
        this.addToPath(new Position(entity.getPosition().getX() + addX, entity
                .getPosition().getY() + addY));
        this.finish();

        if (entity instanceof Npc) {
            ((Npc) entity).getFlags().flag(Flag.APPEARANCE);
        }
    }

    /**
     * Allow the entity to walk to a certain position point not relevant to its
     * current position.
     * 
     * @param position
     *            the position the entity is moving too.
     */
    public void walk(Position position) {
        this.reset();
        this.addToPath(position);
        this.finish();

        if (entity instanceof Npc) {
            ((Npc) entity).getFlags().flag(Flag.APPEARANCE);
        }
    }

    /**
     * Resets the walking queue.
     */
    public void reset() {
        setRunPath(false);
        waypoints.clear();

        /** Set the base point as this position. */
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
        int direction = Misc.direction(deltaX, deltaY);
        if (direction > -1) {
            waypoints.add(new Point(x, y, direction));
        }
    }

    /**
     * Locks this entity's movement for the desired time.
     * 
     * @param delay
     *            the desired time.
     * @param time
     *            the desired time unit.
     */
    public void lockMovementFor(int delay, WorkRate workRate) {
        if (this.isLockMovement()) {
            return;
        }

        this.reset();
        this.setLockMovement(true);

        TaskFactory.getFactory().submit(new Worker(delay, false, workRate) {
            @Override
            public void fire() {
                if (entity.isUnregistered()) {
                    this.cancel();
                    return;
                }

                setLockMovement(false);
                this.cancel();
            }
        });
    }

    /**
     * Prompts this entity to follow another entity.
     * 
     * @param leader
     *            the entity to follow.
     */
    public void follow(final Entity leader) {
        if (entity.isFollowing() && entity.getFollowingEntity() != leader) {
            entity.faceEntity(65535);
            entity.getFollowWorker().cancel();
            entity.setFollowing(false);
            entity.setFollowingEntity(null);
        }

        if (!entity.getFollowWorker().isRunning()) {

            entity.setFollowing(true);
            entity.setFollowingEntity(leader);

            entity.setFollowWorker(new Worker(1, true) {
                @Override
                public void fire() {
                    if (!entity.isFollowing()
                            || !entity.getPosition().withinDistance(
                                    leader.getPosition(), 20)
                            || entity.isHasDied() || leader.isHasDied()) {
                        entity.faceEntity(65535);
                        entity.setFollowing(false);
                        entity.setFollowingEntity(null);
                        this.cancel();
                        return;
                    }

                    if (leader.type() == EntityType.PLAYER) {
                        entity.faceEntity(leader.getSlot() + 32768);
                    } else if (leader.type() == EntityType.NPC) {
                        entity.faceEntity(leader.getSlot());
                    }

                    if (entity.getMovementQueue().isLockMovement()) {
                        return;
                    }

                    if (entity.getPosition().equals(
                            leader.getPosition().clone())) {
                        entity.getMovementQueue().reset();

                        int x = entity.getPosition().getX();
                        int y = entity.getPosition().getY();
                        int z = entity.getPosition().getZ();

                        switch (Misc.random(3)) {
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

                    if (entity.getCombatBuilder().isAttacking()
                            && entity.getPosition().withinDistance(
                                    entity.getCombatBuilder()
                                            .getCurrentTarget().getPosition(),
                                    entity.getCombatBuilder()
                                            .getCurrentStrategy()
                                            .getDistance(entity))) {
                        entity.getMovementQueue().reset();
                        return;
                    }

                    if (entity.type() == EntityType.PLAYER) {
                        Player player = (Player) entity;

                        if (player.getCombatBuilder().isAttacking()) {
                            // ...
                        }

                        if (player.getPosition().withinDistance(
                                leader.getPosition(), 1)) {
                            return;
                        }

                        int x = leader.getPosition().getX();
                        int y = leader.getPosition().getY();
                        // ClippedPathFinder.getPathFinder().findRoute(player,
                        // x, y, true, 0, 0);
                        player.getMovementQueue()
                                .walk(new Position(x, y, player.getPosition()
                                        .getZ()));

                    } else if (entity.type() == EntityType.NPC) {
                        Npc npc = (Npc) entity;

                        if (npc.getCombatBuilder().isAttacking()) {
                            // ...
                        }

                        if (npc.getPosition().withinDistance(
                                leader.getPosition(), 1)) {
                            return;
                        }

                        int x = leader.getPosition().getX();
                        int y = leader.getPosition().getY();
                        // ClippedPathFinder.getDumbPathFinder().findRoute(player,
                        // x, y, true, 0, 0);
                        npc.getMovementQueue().walk(
                                new Position(x, y, npc.getPosition().getZ()));
                    }
                }
            });

            TaskFactory.getFactory().submit(entity.getFollowWorker());
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
