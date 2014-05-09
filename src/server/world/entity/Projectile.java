package server.world.entity;

import server.world.World;
import server.world.entity.player.Player;
import server.world.map.Position;

public class Projectile {

    private Position start;
    private Position offset;
    private int angle;
    private int speed;
    private int projectileId;
    private int startHeight;
    private int endHeight;
    private int lockon;
    private int delay;
    private int curve;

    public Projectile(Position start, Position end, int lockon, int projectileId, int delay, int speed, int startHeight, int endHeight, int curve) {
        this.start = start;
        this.offset = new Position((end.getX() - start.getX()), (end.getY() - start.getY()));
        this.lockon = lockon;
        this.projectileId = projectileId;
        this.delay = speed;
        this.speed = delay;
        this.startHeight = startHeight;
        this.endHeight = endHeight;
        this.curve = curve;
    }

    public Projectile(Entity source, Entity victim, int projectileId, int delay, int speed, int startHeight, int endHeight, int curve) {
        this(source.getPosition(), victim.getPosition(), (victim.isPlayer() ? -victim.getSlot() - 1 : victim.getSlot() + 1), projectileId, delay, speed, startHeight, endHeight, curve);
    }

    public void sendProjectile() {
        for (Player player : World.getPlayers()) {
            if (player == null) {
                continue;
            }

            if (start.isViewableFrom(player.getPosition())) {
                player.getPacketBuilder().sendProjectile(start, offset, angle, speed, projectileId, startHeight, endHeight, lockon, delay);
            }
        }
    }

    /**
     * @return the start
     */
    public Position getStart() {
        return start;
    }

    /**
     * @return the offset
     */
    public Position getOffset() {
        return offset;
    }

    /**
     * @return the angle
     */
    public int getAngle() {
        return angle;
    }

    /**
     * @return the speed
     */
    public int getSpeed() {
        return speed;
    }

    /**
     * @return the projectileId
     */
    public int getProjectileId() {
        return projectileId;
    }

    /**
     * @return the startHeight
     */
    public int getStartHeight() {
        return startHeight;
    }

    /**
     * @return the endHeight
     */
    public int getEndHeight() {
        return endHeight;
    }

    /**
     * @return the lockon
     */
    public int getLockon() {
        return lockon;
    }

    /**
     * @return the delay
     */
    public int getDelay() {
        return delay;
    }

    /**
     * @return the curve
     */
    public int getCurve() {
        return curve;
    }
}
