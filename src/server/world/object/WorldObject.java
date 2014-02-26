package server.world.object;

import server.world.Registerable;
import server.world.map.Position;

/**
 * A registerable object that can be placed anywhere in the rs2 world.
 * 
 * @author lare96
 */
public class WorldObject implements Registerable {

    /** The registerable container. */
    private static RegisterableWorldObject registerable;

    /**
     * The id of the object.
     */
    private int id;

    /**
     * The position of the object
     */
    private Position position;

    /**
     * The face of the object.
     */
    private Rotation face;

    /**
     * The type of object.
     */
    private int type;

    /**
     * All possible directions the object can be facing.
     * 
     * @author lare96
     */
    public enum Rotation {
        WEST(0), NORTH(1), EAST(2), SOUTH(3);

        /**
         * The id of the direction.
         */
        private int faceId;

        /**
         * Create a new object face.
         * 
         * @param faceId
         *        the id of the direction.
         */
        Rotation(int faceId) {
            this.setFaceId(faceId);
        }

        /**
         * @return the faceId.
         */
        public int getFaceId() {
            return faceId;
        }

        /**
         * @param faceId
         *        the faceId to set.
         */
        public void setFaceId(int faceId) {
            this.faceId = faceId;
        }
    }

    /**
     * Construct a new world object.
     * 
     * @param id
     *        the id of this object.
     * @param position
     *        the position of this object.
     * @param face
     *        the face of this object.
     * @param type
     *        the type of object.
     */
    public WorldObject(int id, Position position, Rotation face, int type) {
        this.setId(id);
        this.setPosition(position);
        this.setFace(face);
        this.setType(type);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof WorldObject)) {
            return false;
        }

        WorldObject o = (WorldObject) obj;

        return o.getId() == this.getId() && o.getPosition().equals(this.getPosition()) && o.getFace() == this.getFace() && o.getType() == this.getType();
    }

    /**
     * @return the id.
     */
    public int getId() {
        return id;
    }

    /**
     * @param id
     *        the id to set.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the position.
     */
    public Position getPosition() {
        return position;
    }

    /**
     * @param position
     *        the position to set.
     */
    public void setPosition(Position position) {
        this.position = position;
    }

    /**
     * @return the face.
     */
    public Rotation getFace() {
        return face;
    }

    /**
     * @param face
     *        the face to set.
     */
    public void setFace(Rotation face) {
        this.face = face;
    }

    /**
     * @return the type.
     */
    public int getType() {
        return type;
    }

    /**
     * @param type
     *        the type to set.
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Gets the registerable container.
     * 
     * @return the registerable container.
     */
    public static RegisterableWorldObject getRegisterable() {
        if (registerable == null) {
            registerable = new RegisterableWorldObject();
        }

        return registerable;
    }
}
