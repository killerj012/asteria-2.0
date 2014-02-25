package server.world.entity.player.content;

import java.util.HashSet;
import java.util.Set;

import server.util.Misc.Interval;
import server.world.entity.player.Player;
import server.world.map.Position;
import server.world.object.WorldObject;

public class DwarfMultiCannon {

    /** The damage interval for cannons. */
    private static final Interval DAMAGE_INTERVAL = new Interval().inclusiveInterval(0, 25);

    /** The radius of this cannon's attack. */
    private static final int RADIUS = 7;

    /** How far you can build from another cannon. */
    private static final int BUILD_RADIUS = 15;

    /**
     * A {@link HashSet} of cannons set up by every single {@link Player} in the
     * game.
     */
    private static final Set<Cannon> WORLD_CANNONS = new HashSet<Cannon>();

    /**
     * All of the stages in the cannon setup.
     * 
     * @author lare96
     */
    public enum CannonSetup {

        /** We have nothing set up yet. */
        NOTHING,

        /** We have a cannon base set up. */
        BASE,

        /** We have a cannon stand set up. */
        STAND,

        /** We have the cannon barrels set up. */
        BARRELS,

        /** We have the cannon furnace set up. */
        FURNACE,

        /** The cannon is completely set up. */
        CANNON
    }

    /**
     * All of the directions that the cannon can fire in.
     * 
     * @author lare96
     */
    public enum FireDirection {

        /** Turns <code>NORTH_EAST</code> next. */
        NORTH,

        /** Turns <code>EAST</code> next. */
        NORTH_EAST,

        /** Turns <code>SOUTH_EAST</code> next. */
        EAST,

        /** Turns <code>SOUTH</code> next. */
        SOUTH_EAST,

        /** Turns <code>SOUTH_WEST</code> next. */
        SOUTH,

        /** Turns <code>WEST</code> next. */
        SOUTH_WEST,

        /** Turns <code>NORTH_WEST</code> next. */
        WEST,

        /** Turns <code>NORTH</code> next. */
        NORTH_WEST
    }

    public static void makeCannon(Player player) {

    }

    public static void retrieveCannon(Player player) {

    }

    public static void fireCannon(Player player) {

    }

    public static class Cannon extends WorldObject {

        public Cannon(int id, Position position, Rotation face, int type) {
            super(id, position, face, type);
            // TODO Auto-generated constructor stub
        }

        private Player player;
        private boolean currentlyFiring;
        private int ammunition;
    }
}
