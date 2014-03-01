package server.world.object;

import java.util.ArrayList;
import java.util.List;

import server.core.Rs2Engine;
import server.core.worker.Worker;
import server.util.Misc;
import server.world.entity.Animation;
import server.world.entity.Gfx;
import server.world.entity.player.Player;
import server.world.map.Location;
import server.world.map.Position;

/**
 * A set of obelisks located in different parts of the wilderness.
 * 
 * @author lare96
 */
public class WildernessObeliskSet {

    /** Index id for a wilderness obelisk. */
    public static final int LEVEL_13 = 0, LEVEL_19 = 1, LEVEL_27 = 2,
            LEVEL_35 = 3, LEVEL_44 = 4, LEVEL_50 = 5;

    /** The wilderness obelisks. */
    private static WildernessObelisk[] obeliskSet = { new WildernessObelisk(LEVEL_13, 14829, new Position(3156, 3620), new Location(new Position(3154, 3618), new Position(3158, 3622)), new Position[] { new Position(3154, 3618), new Position(3158, 3618), new Position(3154, 3622), new Position(3158, 3622) }), new WildernessObelisk(LEVEL_19, 14830, new Position(3227, 3667), new Location(new Position(3225, 3665), new Position(3229, 3669)), new Position[] { new Position(3225, 3665), new Position(3229, 3665), new Position(3225, 3669), new Position(3229, 3669) }), new WildernessObelisk(LEVEL_27, 14827, new Position(3035, 3732), new Location(new Position(3033, 3730), new Position(3037, 3733)), new Position[] { new Position(3033, 3730), new Position(3037, 3730), new Position(3033, 3734), new Position(3037, 3734) }), new WildernessObelisk(LEVEL_35, 14828, new Position(3106, 3794), new Location(new Position(3104, 3792), new Position(3108, 3796)), new Position[] { new Position(3104, 3792), new Position(3108, 3792), new Position(3104, 3796), new Position(3108, 3796) }), new WildernessObelisk(LEVEL_44, 14826, new Position(2980, 3866), new Location(new Position(2978, 3864), new Position(2982, 3868)), new Position[] { new Position(2978, 3864), new Position(2982, 3864), new Position(2978, 3868), new Position(2982, 3868) }), new WildernessObelisk(LEVEL_50, 14831, new Position(3307, 3916), new Location(new Position(3306, 3914), new Position(3310, 3918)), new Position[] { new Position(3305, 3914), new Position(3309, 3914), new Position(3305, 3918), new Position(3309, 3918) }) };

    /**
     * Activate a wilderness obelisk.
     * 
     * @param player
     *        the player activating this obelisk.
     * @param id
     *        the id of the wilderness obelisk to activate.
     */
    public static void activateObelisk(final Player player, int id) {

        /** The obelisk we are activating. */
        final WildernessObelisk obelisk = obeliskSet[id];

        /** Block if this obelisk is invalid. */
        if (obelisk == null) {
            throw new IllegalArgumentException("Invalid obelisk id: " + id);
        }

        /** Activate the obelisk. */
        obelisk.setActivated(true);
        player.getPacketBuilder().sendMessage("You activate the ancient obelisk...");

        for (int i = 0; i < 4; i++) {
            player.getPacketBuilder().replaceObject(obelisk.getObeliskPosition()[i], 14825);
        }

        /** Choose a random obelisk other than this one. */
        final WildernessObelisk chosen = getRandomObelisk(obelisk.getIndex());

        /** A little delay. */
        Rs2Engine.getWorld().submit(new Worker(7, false) {
            @Override
            public void fire() {

                /** Start teleporting the players. */
                for (Player all : Rs2Engine.getWorld().getPlayers()) {
                    if (all == null) {
                        continue;
                    }

                    if (all.getPosition().inLocationInclusive(obelisk.getTeleportAllOn())) {
                        all.getMovementQueue().setLockMovement(true);
                        all.gfx(new Gfx(342));
                        all.animation(new Animation(1816));
                    }
                }

                this.cancel();
            }
        });

        /** Six second delay. */
        Rs2Engine.getWorld().submit(new Worker(10, false) {
            @Override
            public void fire() {

                /** Actually move the players. */
                for (Player all : Rs2Engine.getWorld().getPlayers()) {
                    if (all == null) {
                        continue;
                    }

                    if (all.getPosition().inLocationInclusive(obelisk.getTeleportAllOn())) {
                        all.move(chosen.getTeleportFrom());
                        all.getPacketBuilder().sendMessage("You have been teleported by ancient magic!");
                        all.getMovementQueue().setLockMovement(false);
                    }
                }

                /** Reset the obelisk. */
                obelisk.setActivated(false);

                for (int i = 0; i < 4; i++) {
                    player.getPacketBuilder().replaceObject(obelisk.getObeliskPosition()[i], obelisk.getObeliskId());
                }

                this.cancel();
            }
        });
    }

    /**
     * Gets the instance of a random obelisk.
     * 
     * @param obelisk
     *        the obelisk that will be excluded.
     * @return the random obelisk instance.
     */
    private static WildernessObelisk getRandomObelisk(int id) {
        List<WildernessObelisk> tempList = new ArrayList<WildernessObelisk>();

        for (WildernessObelisk w : obeliskSet) {
            if (w.getIndex() == id) {
                continue;
            }

            tempList.add(w);
        }

        return Misc.randomElement(tempList);
    }

    /**
     * A single wilderness obelisk.
     * 
     * @author lare96
     */
    public static class WildernessObelisk {

        /** The index of this wilderness obelisk. */
        private int index;

        /** The obelisk object id. */
        private int obeliskId;

        /**
         * The position that the players will be teleported on when the obelisk
         * is activated.
         */
        private Position teleportFrom;

        /** Any players in this location will be teleported. */
        private Location teleportAllOn;

        /** The position of each obelisk. */
        private Position[] obeliskPosition;

        /** If this obelisk has been activated. */
        private boolean activated;

        /**
         * Create a new {@link WildernessObelisk}.
         * 
         * @param index
         *        the index of this obelisk.
         * @param obeliskId
         *        the object id of this obelisk.
         * @param teleportFrom
         *        the position that the players will be teleported on when the
         *        obelisk is activated.
         * @param teleportAllOn
         *        if this obelisk has been activated.
         */
        public WildernessObelisk(int index, int obeliskId, Position teleportFrom, Location teleportAllOn, Position[] obeliskPosition) {
            if (obeliskPosition.length != 4) {
                throw new IllegalArgumentException("Invalid obelisk position length:" + obeliskPosition.length);
            }

            this.index = index;
            this.obeliskId = obeliskId;
            this.teleportFrom = teleportFrom;
            this.teleportAllOn = teleportAllOn;
            this.obeliskPosition = obeliskPosition;
        }

        /**
         * Gets the index of this obelisk.
         * 
         * @return the index.
         */
        public int getIndex() {
            return index;
        }

        /**
         * Gets the obelisk object id.
         * 
         * @return the obelisk id.
         */
        public int getObeliskId() {
            return obeliskId;
        }

        /**
         * Gets the position that the players will be teleported on when the
         * obelisk is activated.
         * 
         * @return the teleport from.
         */
        public Position getTeleportFrom() {
            return teleportFrom;
        }

        /**
         * If this obelisk has been activated.
         * 
         * @return the teleport all on.
         */
        public Location getTeleportAllOn() {
            return teleportAllOn;
        }

        /**
         * Gets the position of each obelisk.
         * 
         * @return the obelisk position.
         */
        public Position[] getObeliskPosition() {
            return obeliskPosition;
        }

        /**
         * Gets if this obelisk has been activated.
         * 
         * @return true if the obelisk is activated.
         */
        public boolean isActivated() {
            return activated;
        }

        /**
         * Sets if this obelisk has been activated.
         * 
         * @param activated
         *        if this obelisk is activated.
         */
        public void setActivated(boolean activated) {
            this.activated = activated;
        }
    }
}
