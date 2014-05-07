package server.world.entity;

import server.world.map.Position;

public class FlyingProjectile {

    private Position position;
    private Position offset;
    private int angle;
    private int speed;
    private int gfxMoving;
    private int startHeight;
    private int endHeight;
    private int lockon;
    private int time;

    public FlyingProjectile(Position position, Position offset, int angle, int speed, int gfxMoving, int startHeight, int endHeight, int lockon, int time) {
        this.position = position;
        this.offset = offset;
        this.angle = angle;
        this.speed = speed;
        this.gfxMoving = gfxMoving;
        this.startHeight = startHeight;
        this.endHeight = endHeight;
        this.lockon = lockon;
        this.time = time;
    }

    /**
     * @return the position
     */
    public Position getPosition() {
        return position;
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
     * @return the gfxMoving
     */
    public int getGfxMoving() {
        return gfxMoving;
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
     * @return the time
     */
    public int getTime() {
        return time;
    }
}
