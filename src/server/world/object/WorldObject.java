package server.world.object;

import server.world.map.Position;

/**
 * An object that can be placed anywhere in the world.
 * 
 * @author lare96
 */
public class WorldObject {

    /** The id of the object. */
    private int id;

    /** The position of the object */
    private Position position;

    /** The direction this object is facing.. */
    private Rotation rotation;

    /** The type of object. */
    private int type;

    /**
     * All of the directions an object can face.
     * 
     * @author lare96
     */
    public enum Rotation {

        /** The west direction. */
        WEST(0),

        /** The north direction. */
        NORTH(1),

        /** The east direction. */
        EAST(2),

        /** The south direction. */
        SOUTH(3);

        /** The id of the direction. */
        private int faceId;

        /**
         * Create a new {@link Rotation}.
         * 
         * @param faceId
         *        the id of the direction.
         */
        Rotation(int faceId) {
            this.faceId = faceId;
        }

        /**
         * Gets the id of the direction.
         * 
         * @return the face id.
         */
        public int getFaceId() {
            return faceId;
        }
    }

    /**
     * Create a new {@link WorldObject}.
     * 
     * @param id
     *        the id of the object.
     * @param position
     *        the position of the object.
     * @param rotation
     *        the direction this object is facing.
     * @param type
     *        the type of object that this is.
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
     * Gets the id of the object.
     * 
     * @return the id.
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the position of the object.
     * 
     * @return the position.
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Gets the direction this object is facing.
     * 
     * @return the face.
     */
    public Rotation getRotation() {
        return rotation;
    }

    /**
     * Gets the type of object that this is.
     * 
     * @return the type.
     */
    public int getType() {
        return type;
    }
}
