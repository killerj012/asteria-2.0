package server.world.entity.player.content;

import java.util.HashSet;
import java.util.Set;

import server.core.Rs2Engine;
import server.core.worker.Worker;
import server.util.Misc.Interval;
import server.world.entity.Animation;
import server.world.entity.player.Player;
import server.world.item.Item;
import server.world.map.Position;
import server.world.object.WorldObject;
import server.world.object.WorldObject.Rotation;

public class DwarfMultiCannon {

    /** The damage interval for cannons (inclusive). */
    private static final Interval DAMAGE_INTERVAL = new Interval().inclusiveInterval(0, 25);

    /** The radius of this cannon's attack. */
    private static final int RADIUS = 7;

    /** How far you can build from another cannon. */
    private static final int BUILD_RADIUS = 15;

    /**
     * A {@link HashSet} of the positions of cannons set up by every single
     * {@link Player} in the game.
     */
    private static Set<Position> positionSet = new HashSet<Position>();

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

    /**
     * The cannon building process.
     * 
     * @param player
     *        the player building this cannon.
     */
    public static void makeCannon(final Player player) {

        /** Block if we already have a cannon. */
        if (player.getCannonCredentials().hasCannon()) {
            player.getPacketBuilder().sendMessage("You can only own one built cannon at a time!");
            return;
        }

        /** Block if we have started setting up another cannon. */
        if (player.getCannonCredentials().getSetupStage() != CannonSetup.NOTHING) {
            player.getPacketBuilder().sendMessage("You have already started setting up a cannon!");
            return;
        }

        /** Block if we're too close to another cannon. */
        for (Position position : positionSet) {
            if (player.getPosition().withinDistance(position, BUILD_RADIUS)) {
                player.getPacketBuilder().sendMessage("You must build your cannon at least " + BUILD_RADIUS + " squares away from other cannons!");
                return;
            }
        }

        /** Block if we don't have the required items. */
        if (!player.getInventory().getContainer().contains(new int[] { 6, 8, 10, 12 })) {
            player.getPacketBuilder().sendMessage("You need a base, stand, barrels, and a furnace in order to build a cannon!");
            return;
        }

        /** Lock movement while we're building. */
        player.getMovementQueue().reset();
        player.getMovementQueue().setLockMovement(true);

        /** Now we setup the cannon. */
        Rs2Engine.getWorld().submit(new Worker(3, true) {
            @SuppressWarnings("fallthrough")
            @Override
            public void fire() {
                switch (player.getCannonCredentials().getSetupStage()) {
                    case NOTHING:
                        player.getPacketBuilder().sendMessage("You put down the base...");
                        player.getCannonCredentials().setSetupStage(CannonSetup.BASE);
                    case BASE:
                        player.animation(new Animation(827));
                        player.getInventory().deleteItem(new Item(6));
                        WorldObject.getRegisterable().register(new WorldObject(7, player.getPosition().clone(), Rotation.SOUTH, 10));
                        player.getCannonCredentials().setSetupStage(CannonSetup.STAND);
                        break;
                    case STAND:
                        player.animation(new Animation(827));
                        player.getInventory().deleteItem(new Item(8));
                        WorldObject.getRegisterable().register(new WorldObject(8, player.getPosition().clone(), Rotation.SOUTH, 10));
                        player.getCannonCredentials().setSetupStage(CannonSetup.BARRELS);
                        player.getPacketBuilder().sendMessage("You attach the stand to the base...");
                        break;
                    case BARRELS:
                        player.animation(new Animation(827));
                        player.getInventory().deleteItem(new Item(10));
                        WorldObject.getRegisterable().register(new WorldObject(9, player.getPosition().clone(), Rotation.SOUTH, 10));
                        player.getCannonCredentials().setSetupStage(CannonSetup.FURNACE);
                        player.getPacketBuilder().sendMessage("You attach the barrels to the stand...");
                        break;
                    case FURNACE:
                        player.animation(new Animation(827));
                        player.getInventory().deleteItem(new Item(12));
                        WorldObject.getRegisterable().register(new WorldObject(6, player.getPosition().clone(), Rotation.SOUTH, 10));
                        player.getCannonCredentials().setSetupStage(CannonSetup.CANNON);
                    case CANNON:
                        positionSet.add(player.getPosition().clone());
                        player.getCannonCredentials().setCannon(new Cannon(player));
                        player.getMovementQueue().setLockMovement(false);
                        player.getPacketBuilder().sendMessage("You attach the furnace to the barrels and finish putting your cannon together!");
                        this.cancel();
                        break;
                }
            }

            @Override
            public void fireOnCancel() {
                player.getInventory().deleteItemSet(new Item[] { new Item(6), new Item(8), new Item(10), new Item(12) });
                player.getInventory().addItemSet(new Item[] { new Item(6), new Item(8), new Item(10), new Item(12) });

                WorldObject.getRegisterable().removeOnPosition(player.getPosition().clone());
            }
        }.attach(player));
    }

    public static void retrieveCannon(Player player) {

    }

    public static void fireCannon(Player player) {
        // /**
        // * Checks if the cannon we are shooting is our cannon
        // */
        // if (cannon.getPosition().getX() == p.getX() &&
        // cannon.getPosition().getY() == p.getY() &&
        // cannon.getPosition().getZ() == p.getZ()) {
        //
        // /**
        // * Do not shoot if we are already firing
        // */
        // if (shooting) {
        // player.sendMessage("Your cannon is already firing!");
        // return;
        // }
        //
        // /**
        // * Load cannonballs and begin rotating the cannon and firing at mobs
        // * based on the direction the cannon is facing.
        // */
        // if (balls < 1) {
        // int amountOfCannonBalls = player.getItems().itemAmount(2) > 30 ? 30 :
        // player.getItems().itemAmount(2);
        //
        // if (amountOfCannonBalls < 1) {
        // player.sendMessage("You need ammo to shoot this cannon!");
        // return;
        // }
        //
        // balls = amountOfCannonBalls;
        // player.getItems().deleteItem(2, amountOfCannonBalls);
        // shooting = true;
        //
        // Server.getTaskScheduler().schedule(new
        // Task((player.getRegion().inMulti() ? 2 : 4), false) {
        // @Override
        // protected void execute() {
        // if (player.disconnected || !shooting) {
        // this.stop();
        // return;
        // }
        //
        // if (balls < 1) {
        // player.sendMessage("Your cannon has run out of ammo!");
        // shooting = false;
        // this.stop();
        // return;
        // } else {
        // if (rotation == null) {
        // rotation = Rotation.NORTH;
        // rotate(cannon);
        // fireAtMobs();
        // return;
        // }
        //
        // switch (rotation) {
        // case NORTH: // north
        // rotation = Rotation.NORTH_EAST;
        // break;
        // case NORTH_EAST: // north-east
        // rotation = Rotation.EAST;
        // break;
        // case EAST: // east
        // rotation = Rotation.SOUTH_EAST;
        // break;
        // case SOUTH_EAST: // south-east
        // rotation = Rotation.SOUTH;
        // break;
        // case SOUTH: // south
        // rotation = Rotation.SOUTH_WEST;
        // break;
        // case SOUTH_WEST: // south-west
        // rotation = Rotation.WEST;
        // break;
        // case WEST: // west
        // rotation = Rotation.NORTH_WEST;
        // break;
        // case NORTH_WEST: // north-west
        // rotation = null;
        // break;
        // }
        //
        // rotate(cannon);
        // fireAtMobs();
        // }
        // }
        // });
        // }
        // } else {
        // player.sendMessage("This is not your cannon to fire!");
        // }
    }

    public static class CannonCredentials {

        private Player player;
        private CannonSetup setupStage = CannonSetup.NOTHING;
        private Cannon cannon;

        public CannonCredentials(Player player) {
            this.player = player;
        }

        public boolean hasCannon() {
            return cannon != null;
        }

        /**
         * @return the setupStage
         */
        public CannonSetup getSetupStage() {
            return setupStage;
        }

        /**
         * @param setupStage
         *        the setupStage to set
         */
        public void setSetupStage(CannonSetup setupStage) {
            this.setupStage = setupStage;
        }

        /**
         * @return the cannon
         */
        public Cannon getCannon() {
            return cannon;
        }

        /**
         * @param cannon
         *        the cannon to set
         */
        public void setCannon(Cannon cannon) {
            this.cannon = cannon;
        }
    }

    public static class Cannon extends WorldObject {

        private Player player;

        public Cannon(Player player) {
            super(6, player.getPosition().clone(), Rotation.SOUTH, 10);
            this.player = player;
        }

        private boolean currentlyFiring;
        private int ammunition;

        /**
         * @return the currentlyFiring
         */
        public boolean isCurrentlyFiring() {
            return currentlyFiring;
        }

        /**
         * @param currentlyFiring
         *        the currentlyFiring to set
         */
        public void setCurrentlyFiring(boolean currentlyFiring) {
            this.currentlyFiring = currentlyFiring;
        }

        /**
         * @return the ammunition
         */
        public int getAmmunition() {
            return ammunition;
        }

        /**
         * @param ammunition
         *        the ammunition to set
         */
        public void setAmmunition(int ammunition) {
            this.ammunition = ammunition;
        }

        /**
         * @return the player
         */
        public Player getPlayer() {
            return player;
        }

        /**
         * @param player
         *        the player to set
         */
        public void setPlayer(Player player) {
            this.player = player;
        }
    }
}
