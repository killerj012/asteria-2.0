package com.asteria.world.object;

import com.asteria.world.World;
import com.asteria.world.map.Position;

/**
 * An object that can be constructed and placed anywhere in the {@link World}.
 * 
 * @author lare96
 */
public final class WorldObject {

    /** The id of this object. */
    private final int id;

    /** The position of this object */
    private final Position position;

    /** The direction this object is facing. */
    private final Rotation rotation;

    /** The type of object that this is. */
    private final int type;

    /**
     * All of the directions an object can face.
     * 
     * @author lare96
     */
    public enum Rotation {
        WEST,
        NORTH,
        EAST,
        SOUTH
    }

    /**
     * Create a new {@link WorldObject}.
     * 
     * @param id
     *            the id of this object.
     * @param position
     *            the position of this object.
     * @param rotation
     *            the direction this object is facing.
     * @param type
     *            the type of object that this is.
     */
    public WorldObject(int id, Position position, Rotation rotation, int type) {
        this.id = id;
        this.position = position;
        this.rotation = rotation;
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof WorldObject)) {
            return false;
        }

        WorldObject object = (WorldObject) obj;

        return object.id == id && object.position.equals(position) && object.rotation == rotation && object.type == type;
    }

    /**
     * Gets the id of this object.
     * 
     * @return the id of this object.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the position of this object.
     * 
     * @return the position of this object.
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Gets the direction this object is facing.
     * 
     * @return the direction this object is facing.
     */
    public Rotation getRotation() {
        return rotation;
    }

    /**
     * Gets the type of object that this is.
     * 
     * @return the type of object that this is.
     */
    public int getType() {
        return type;
    }
}
