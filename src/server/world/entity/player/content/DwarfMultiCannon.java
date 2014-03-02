package server.world.entity.player.content;

import java.util.HashSet;
import java.util.Set;

import server.core.Rs2Engine;
import server.core.worker.Worker;
import server.util.Misc.Interval;
import server.world.entity.Animation;
import server.world.entity.combat.Combat;
import server.world.entity.combat.Hit;
import server.world.entity.npc.Npc;
import server.world.entity.player.Player;
import server.world.item.Item;
import server.world.item.ground.GroundItem;
import server.world.map.Location;
import server.world.map.Position;
import server.world.object.WorldObject;
import server.world.object.WorldObject.Rotation;

/**
 * A collection of classes and methods that handle everything that has to do
 * with the dwarf multicannon.
 * 
 * @author lare96
 */
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
        NORTH(FireDirection.NORTH_EAST, 516, new TargetSequence() {
            @Override
            public boolean canTarget(Position cannonPosition, Position targetPosition) {
                return (targetPosition.getY() > cannonPosition.getY() && targetPosition.getX() >= cannonPosition.getX() - 1 && targetPosition.getX() <= cannonPosition.getX() + 1);
            }
        }),

        /** Turns <code>EAST</code> next. */
        NORTH_EAST(FireDirection.EAST, 517, new TargetSequence() {
            @Override
            public boolean canTarget(Position cannonPosition, Position targetPosition) {
                return (targetPosition.getX() >= cannonPosition.getX() + 1 && targetPosition.getY() >= cannonPosition.getY() + 1);
            }
        }),

        /** Turns <code>SOUTH_EAST</code> next. */
        EAST(FireDirection.SOUTH_EAST, 518, new TargetSequence() {
            @Override
            public boolean canTarget(Position cannonPosition, Position targetPosition) {
                return (targetPosition.getX() > cannonPosition.getX() && targetPosition.getY() >= cannonPosition.getY() - 1 && targetPosition.getY() <= cannonPosition.getY() + 1);
            }
        }),

        /** Turns <code>SOUTH</code> next. */
        SOUTH_EAST(FireDirection.SOUTH, 519, new TargetSequence() {
            @Override
            public boolean canTarget(Position cannonPosition, Position targetPosition) {
                return (targetPosition.getY() <= cannonPosition.getY() - 1 && targetPosition.getX() >= cannonPosition.getX() + 1);
            }
        }),

        /** Turns <code>SOUTH_WEST</code> next. */
        SOUTH(FireDirection.SOUTH_WEST, 520, new TargetSequence() {
            @Override
            public boolean canTarget(Position cannonPosition, Position targetPosition) {
                return (targetPosition.getY() < cannonPosition.getY() && targetPosition.getX() >= cannonPosition.getX() - 1 && targetPosition.getX() <= cannonPosition.getX() + 1);
            }
        }),

        /** Turns <code>WEST</code> next. */
        SOUTH_WEST(FireDirection.WEST, 521, new TargetSequence() {
            @Override
            public boolean canTarget(Position cannonPosition, Position targetPosition) {
                return (targetPosition.getX() <= cannonPosition.getX() - 1 && targetPosition.getY() <= cannonPosition.getY() - 1);
            }
        }),

        /** Turns <code>NORTH_WEST</code> next. */
        WEST(FireDirection.NORTH_WEST, 514, new TargetSequence() {
            @Override
            public boolean canTarget(Position cannonPosition, Position targetPosition) {
                return (targetPosition.getX() < cannonPosition.getX() && targetPosition.getY() >= cannonPosition.getY() - 1 && targetPosition.getY() <= cannonPosition.getY() + 1);
            }
        }),

        /** Turns <code>NORTH</code> next. */
        NORTH_WEST(FireDirection.NORTH, 515, new TargetSequence() {
            @Override
            public boolean canTarget(Position cannonPosition, Position targetPosition) {
                return (targetPosition.getX() <= cannonPosition.getX() - 1 && targetPosition.getY() >= cannonPosition.getY() + 1);
            }
        });

        /** The next direction in the rotation sequence. */
        private FireDirection nextDirection;

        /** The object animation for this direction. */
        private int objectAnimation;

        /** The target sequence for this direction. */
        private TargetSequence sequence;

        /**
         * Create a new {@link FireDirection}.
         * 
         * @param nextDirection
         *        the next direction in the rotation sequence.
         * @param objectAnimation
         *        the object animation for this direction.
         * @param sequence
         *        the target sequence for this direction.
         */
        private FireDirection(FireDirection nextDirection, int objectAnimation, TargetSequence sequence) {
            this.nextDirection = nextDirection;
            this.objectAnimation = objectAnimation;
            this.sequence = sequence;
        }

        /**
         * Gets the next direction in the rotation sequence.
         * 
         * @return the next direction.
         */
        public FireDirection getNextDirection() {
            return nextDirection;
        }

        /**
         * Gets the object animation for this direction.
         * 
         * @return the object animation.
         */
        public int getObjectAnimation() {
            return objectAnimation;
        }

        /**
         * Gets the target sequence for this direction.
         * 
         * @return the sequence.
         */
        public TargetSequence getSequence() {
            return sequence;
        }
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
                        player.getCannonCredentials().setCannon(new Cannon(player));
                        player.getCannonCredentials().setSetupStage(CannonSetup.CANNON);
                    case CANNON:
                        WorldObject.getRegisterable().register(player.getCannonCredentials().getCannon());
                        positionSet.add(player.getPosition().clone());
                        player.getMovementQueue().setLockMovement(false);
                        player.getPacketBuilder().sendMessage("You attach the furnace to the barrels and finish putting your cannon together!");
                        this.cancel();
                        break;
                }
            }
        }.attach(player));
    }

    /**
     * The cannon retrieval process.
     * 
     * @param player
     *        the player retrieving this cannon.
     * @param cannonPosition
     *        the position of the cannon being retrieved.
     */
    public static void retrieveCannon(Player player, Position cannonPosition) {

        /**
         * Check if we own this cannon by comparing the position of your cannon
         * and the cannon trying to be picked up.
         */
        if (!player.getCannonCredentials().hasCannon() || !player.getCannonCredentials().getCannon().equals(cannonPosition)) {
            player.getPacketBuilder().sendMessage("This is not your cannon to pick up!");
            return;
        }

        /** Remove the cannon from the world. */
        player.animation(new Animation(827));
        positionSet.remove(cannonPosition);
        WorldObject.getRegisterable().removeOnPosition(cannonPosition);
        player.getCannonCredentials().getCannon().setCurrentlyFiring(false);
        player.getCannonCredentials().setSetupStage(CannonSetup.NOTHING);

        /** Return the items to your inventory, bank or the ground. */
        if (player.getInventory().getContainer().freeSlots() > 4) {
            player.getInventory().addItem(new Item(6));
            player.getInventory().addItem(new Item(8));
            player.getInventory().addItem(new Item(10));
            player.getInventory().addItem(new Item(12));
            player.getPacketBuilder().sendMessage("The cannon parts were returned to your inventory.");

            if (player.getCannonCredentials().getCannon().getAmmunition() > 0) {
                player.getInventory().addItem(new Item(2, player.getCannonCredentials().getCannon().getAmmunition()));
                player.getCannonCredentials().getCannon().setAmmunition(0);
                player.getPacketBuilder().sendMessage("The leftover ammunition was returned to your inventory.");
            }
        } else if (player.getInventory().getContainer().freeSlots() < 5) {

            if (player.getBank().getContainer().freeSlots() < 5) {
                GroundItem.getRegisterable().register(new GroundItem(new Item(6), player.getPosition().clone(), player));
                GroundItem.getRegisterable().register(new GroundItem(new Item(8), player.getPosition().clone(), player));
                GroundItem.getRegisterable().register(new GroundItem(new Item(10), player.getPosition().clone(), player));
                GroundItem.getRegisterable().register(new GroundItem(new Item(12), player.getPosition().clone(), player));
                player.getPacketBuilder().sendMessage("The cannon parts were placed on the ground.");

                if (player.getCannonCredentials().getCannon().getAmmunition() > 0) {
                    GroundItem.getRegisterable().register(new GroundItem(new Item(2, player.getCannonCredentials().getCannon().getAmmunition()), player.getPosition().clone(), player));
                    player.getCannonCredentials().getCannon().setAmmunition(0);
                    player.getPacketBuilder().sendMessage("The leftover ammunition was placed on the ground.");
                }
                return;
            }

            player.getBank().addItem(new Item(6));
            player.getBank().addItem(new Item(8));
            player.getBank().addItem(new Item(10));
            player.getBank().addItem(new Item(12));
            player.getPacketBuilder().sendMessage("The cannon parts were returned to your bank.");

            if (player.getCannonCredentials().getCannon().getAmmunition() > 0) {
                player.getBank().addItem(new Item(2, player.getCannonCredentials().getCannon().getAmmunition()));
                player.getCannonCredentials().getCannon().setAmmunition(0);
                player.getPacketBuilder().sendMessage("The leftover ammunition was returned to your bank.");
            }
        }
    }

    /**
     * The cannon firing process.
     * 
     * @param player
     *        the player who fired this cannon.
     */
    public static void fireCannon(final Player player, Position cannonPosition) {

        /**
         * Check if we own this cannon by comparing the position of your cannon
         * and the cannon trying to be fired.
         */
        if (!player.getCannonCredentials().hasCannon() || !player.getCannonCredentials().getCannon().equals(cannonPosition)) {
            player.getPacketBuilder().sendMessage("This is not your cannon to fire!");
            return;
        }

        /** Check if we're already shooting. */
        if (player.getCannonCredentials().getCannon().isCurrentlyFiring()) {
            player.getPacketBuilder().sendMessage("Your cannon is already firing!");
            return;
        }

        /** Load the ammunition in the cannon (if any). */
        int count = player.getInventory().getContainer().getCount(2);

        if (count >= 30) {
            player.getCannonCredentials().getCannon().setAmmunition(30);
            player.getInventory().deleteItem(new Item(2, 30));
            player.getPacketBuilder().sendMessage("You load 30 of your " + count + " cannon balls and begin firing.");
        } else if (count > 0 && count < 30) {
            player.getCannonCredentials().getCannon().setAmmunition(count);
            player.getInventory().deleteItem(new Item(2, count));
            player.getPacketBuilder().sendMessage("You load " + count + " of your cannon balls and begin firing.");
        } else if (count == 0) {
            player.getPacketBuilder().sendMessage("You need cannon balls in order to fire this cannon!");
            return;
        }

        /** Flag the cannon as firing. */
        player.getCannonCredentials().getCannon().setCurrentlyFiring(true);

        /** Cache an instance of the cannon. */
        final Cannon cannon = player.getCannonCredentials().getCannon();

        /** Start firing the cannon. */
        Rs2Engine.getWorld().submit(new Worker((Location.inMultiCombat(player) ? 2 : 4), false) {
            @Override
            public void fire() {
                if (player.isUnregistered() || !cannon.isCurrentlyFiring()) {
                    this.cancel();
                    return;
                }

                if (cannon.getAmmunition() == 0) {
                    player.getPacketBuilder().sendMessage("Your cannon has run out of ammo.");
                    this.cancel();
                    return;
                }

                /** Do the cannon rotation animation. */
                player.getPacketBuilder().sendGlobalObjectAnimation(cannon.getPosition(), cannon.getDirection().getObjectAnimation(), 10, -1);

                /** Target based on the direction. */
                for (Npc npc : Rs2Engine.getWorld().getNpcs()) {
                    if (npc == null || npc.isHasDied() || !npc.getPosition().withinDistance(cannon.getPosition(), RADIUS)) {
                        continue;
                    }

                    if (cannon.getDirection().getSequence().canTarget(cannon.getPosition(), npc.getPosition())) {
                        if (!Location.inMultiCombat(npc) && npc.getCombatSession().isBeingAttacked() || npc.getCombatSession().isAttacking()) {
                            continue;
                        }

                        // TODO: fire cannonball projectile...
                        Rs2Engine.getWorld().submit(new FireWorker(npc, player));
                        cannon.decrementAmmunition();
                    }
                }

                /** Rotate the cannon. */
                cannon.setDirection(cannon.getDirection().getNextDirection());
            }
        });
    }

    /**
     * A {@link Worker} implementation that will deal damage to NPCs targeted by
     * the cannon after one second. This is to ensure that the damage is dealt
     * only when the actual cannonball projectile reaches them.
     * 
     * @author lare96
     */
    public static class FireWorker extends Worker {

        /**
         * Create a new {@link FireWorker}.
         * 
         * @param npc
         *        the npc being hit.
         * @param player
         *        the player who owns the cannon.
         */
        public FireWorker(Npc npc, Player player) {
            super(2, false);
            this.hit = new Hit(DAMAGE_INTERVAL.calculate());
            this.npc = npc;
            this.player = player;
        }

        /** The actual damage that will be inflicted. */
        private Hit hit;

        /** The npc being hit. */
        private Npc npc;

        /** The player who owns the cannon. */
        private Player player;

        @Override
        public void fire() {
            npc.primaryHit(hit);
            Combat.fight(npc, player);
            npc.faceEntity(player.getSlot());
            this.cancel();
        }
    }

    /**
     * An abstract container that provides open implementation for different
     * targeting methods.
     * 
     * @author lare96
     */
    public static interface TargetSequence {

        /**
         * Determines if the position can be targeted based on the cannon's
         * position and the position trying to be targeted.
         * 
         * @param cannonPosition
         *        the cannon's position.
         * @param targetPosition
         *        the position trying to be targeted.
         * @return true if the position can be targeted.
         */
        public boolean canTarget(Position cannonPosition, Position targetPosition);
    }

    /**
     * A container that holds important credentials for a player.
     * 
     * @author lare96
     */
    public static class CannonCredentials {

        private Player player;
        private CannonSetup setupStage = CannonSetup.NOTHING;
        private Cannon cannon;

        public CannonCredentials(Player player) {
            this.player = player;
        }

        public boolean hasCannon() {
            return setupStage != CannonSetup.NOTHING;
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

    /**
     * A {@link WorldObject} implementation that holds important data for a
     * dwarf multicannon.
     * 
     * @author lare96
     */
    public static class Cannon extends WorldObject {

        private Player player;

        public Cannon(Player player) {
            super(6, player.getPosition().clone(), Rotation.SOUTH, 10);
            this.player = player;
        }

        private FireDirection direction = FireDirection.NORTH;
        private boolean currentlyFiring;
        private int ammunition;

        public void decrementAmmunition() {
            ammunition--;
        }

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
         * @return the direction
         */
        public FireDirection getDirection() {
            return direction;
        }

        /**
         * @param direction
         *        the direction to set
         */
        public void setDirection(FireDirection direction) {
            this.direction = direction;
        }
    }
}
