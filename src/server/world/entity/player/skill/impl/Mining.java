package server.world.entity.player.skill.impl;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import server.core.worker.TaskFactory;
import server.core.worker.WorkRate;
import server.core.worker.Worker;
import server.util.Misc;
import server.world.World;
import server.world.entity.Animation;
import server.world.entity.player.Player;
import server.world.entity.player.skill.SkillEvent;
import server.world.entity.player.skill.SkillManager.SkillConstant;
import server.world.item.Item;
import server.world.map.Position;
import server.world.object.WorldObject;
import server.world.object.WorldObject.Rotation;

/**
 * Mining class that handles the mining skill. This supports mining almost all
 * rocks with empty rocks and respawning. This also supports multiple people
 * mining on the same rock.
 * 
 * @author lare96
 */
public class Mining extends SkillEvent {

    // XXX: Pretty good mining compared to most people's. Even supports two
    // people mining on the same rock and one person mining faster than the
    // other. Needs random events maybe? This definitely needs a better mining
    // speed formula (like most of the other skills I've written lol).

    /** The {@link Mining} singleton instance. */
    private static Mining singleton;

    /** The positions of all the empty rocks. */
    private static Set<Position> rockSet = new HashSet<Position>();

    /**
     * All of the rocks that can be mined.
     * 
     * @author lare96
     */
    public enum Rock {
        CLAY(new OreObject[] { new OreObject(2108, 450), new OreObject(2109, 451) }, 1, 434, 1, 1, 5),
        RUNE_ESSENCE(new OreObject[] { new OreObject(2491, -1) }, 1, 1436, 0, 1, 5),
        TIN(new OreObject[] { new OreObject(2094, 450), new OreObject(2095, 451) }, 1, 438, 5, 1, 17),
        COPPER(new OreObject[] { new OreObject(2090, 450), new OreObject(2091, 451) }, 1, 436, 5, 1, 17),
        IRON(new OreObject[] { new OreObject(2092, 450), new OreObject(2093, 451) }, 15, 440, 10, 1, 35),
        SILVER(new OreObject[] { new OreObject(2100, 450), new OreObject(2101, 451) }, 20, 443, 120, 3, 40),
        COAL(new OreObject[] { new OreObject(2096, 450), new OreObject(2097, 451) }, 30, 453, 60, 4, 50),
        GOLD(new OreObject[] { new OreObject(2098, 450), new OreObject(2099, 451) }, 40, 444, 120, 5, 65),
        MITHRIL(new OreObject[] { new OreObject(2102, 450), new OreObject(2103, 451) }, 55, 447, 180, 6, 80),
        ADAMANTITE(new OreObject[] { new OreObject(2104, 450), new OreObject(2105, 451) }, 70, 449, 420, 7, 95),
        RUNITE(new OreObject[] { new OreObject(2106, 450), new OreObject(2107, 451) }, 85, 451, 840, 10, 125);

        /** All of the rocks and their empty rock replacements. */
        private OreObject[] ore;

        /** The level needed to mine this type of rock. */
        private int level;

        /** The item id of the rock. */
        private int itemId;

        /** The time it takes for this rock to respawn. */
        private int respawnTime;

        /** The rate at which this rock can be mined. */
        private int mineRate;

        /** The experience gained from mining this rock. */
        private int experience;

        /** A map of rocks mapped to another map of their ore objects. */
        private static Map<Rock, HashMap<Integer, OreObject>> rockMap = new HashMap<Rock, HashMap<Integer, OreObject>>();

        /** A map of object id's mapped to their respective rock instances. */
        private static Map<Integer, Rock> objectMap = new HashMap<Integer, Rock>();

        /** Load the data into the map. */
        static {
            for (Rock rock : Rock.values()) {
                rockMap.put(rock, new HashMap<Integer, OreObject>());

                for (OreObject ore : rock.getOre()) {
                    rockMap.get(rock).put(ore.getRock(), ore);
                    objectMap.put(ore.getRock(), rock);
                }
            }
        }

        /**
         * Create a new {@link Rock}.
         * 
         * @param ore
         *        all of the rocks and their empty rock replacements.
         * @param level
         *        the level needed to mine this type of rock.
         * @param itemId
         *        the item id of the rock.
         * @param respawnTime
         *        the time it takes for this rock to respawn.
         * @param mineRate
         *        the rate at which this rock can be mined.
         * @param experience
         *        the experience gained from mining this rock.
         */
        private Rock(OreObject[] ore, int level, int itemId, int respawnTime, int mineRate, int experience) {
            this.ore = ore;
            this.level = level;
            this.itemId = itemId;
            this.respawnTime = respawnTime;
            this.mineRate = mineRate;
            this.experience = experience;
        }

        /**
         * Gets all of the rocks and their empty rock replacements.
         * 
         * @return the ore.
         */
        public OreObject[] getOre() {
            return ore;
        }

        /**
         * Gets the level needed to mine this type of rock.
         * 
         * @return the level.
         */
        public int getLevel() {
            return level;
        }

        /**
         * Gets the item id of the rock.
         * 
         * @return the item id.
         */
        public int getItemId() {
            return itemId;
        }

        /**
         * Gets the time it takes for this rock to respawn.
         * 
         * @return the respawn time.
         */
        public int getRespawnTime() {
            return respawnTime;
        }

        /**
         * Gets the rate at which this rock can be mined.
         * 
         * @return the speed.
         */
        public int getMineRate() {
            return mineRate;
        }

        /**
         * Gets the experience gained from mining this rock.
         * 
         * @return the experience.
         */
        public int getExperience() {
            return experience;
        }

        /**
         * Gets the correct {@link OreObject} for the specified {@link Rock} and
         * object id.
         * 
         * @param rock
         *        the rock to get the ore object for.
         * @param objectId
         *        the object id to get the ore object for.
         * @return the correct object ore.
         */
        public static OreObject getOre(Rock rock, int objectId) {
            return rockMap.get(rock).get(objectId);
        }

        /**
         * Gets the correct rock instance for the specified object id.
         * 
         * @param objectId
         *        the specified object id.
         * @return the correct rock instance.
         */
        public static Rock getRock(int objectId) {
            return objectMap.get(objectId);
        }
    }

    /**
     * All of the different types of pickaxes that can be used to mine
     * {@link Rock}s.
     * 
     * @author lare96
     */
    public enum Pickaxe {
        BRONZE(1265, 1, 625, 3),
        IRON(1267, 1, 626, 3),
        STEEL(1269, 5, 627, 3),
        MITHRIL(1273, 20, 629, 2),
        ADAMANT(1271, 30, 628, 1),
        RUNE(1275, 40, 624, 0);

        /** The item id of this pickaxe. */
        private int itemId;

        /** The level needed to use this pickaxe. */
        private int level;

        /** The animation when mining with this pickaxe. */
        private int animation;

        /** The mining rate of this pickaxe. */
        private int mineRate;

        /**
         * Create a new {@link Pickaxe}.
         * 
         * @param itemId
         *        the item id of this pickaxe.
         * @param level
         *        the level needed to use this pickaxe.
         * @param animation
         *        the animation when mining with this pickaxe.
         * @param mineRate
         *        the mining rate of this pickaxe.
         */
        private Pickaxe(int itemId, int level, int animation, int mineRate) {
            this.itemId = itemId;
            this.level = level;
            this.animation = animation;
            this.mineRate = mineRate;
        }

        /**
         * Gets the item id of this pickaxe.
         * 
         * @return the item id.
         */
        public int getItemId() {
            return itemId;
        }

        /**
         * Gets the level needed to use this pickaxe.
         * 
         * @return the level.
         */
        public int getLevel() {
            return level;
        }

        /**
         * Gets the animation when mining with this pickaxe.
         * 
         * @return the animation.
         */
        public int getAnimation() {
            return animation;
        }

        /**
         * Gets the mining rate of this pickaxe.
         * 
         * @return the mine rate.
         */
        public int getMineRate() {
            return mineRate;
        }
    }

    /**
     * Mine the specified {@link Rock} with the specified {@link Pickaxe} for
     * the specified {@link Player}.
     * 
     * @param player
     *        the player mining this rock.
     * @param rock
     *        the rock being mined by the player.
     * @param pickaxe
     *        the pickaxe you are using to mine the rock.
     * @param position
     *        the position that this rock is on.
     * @param objectId
     *        the object id of this rock.
     */
    public void startRockMine(final Player player, final Rock rock, final Pickaxe pickaxe, final Position position, final int objectId) {

        /** Block if we are already mining. */
        if (player.getSkillEvent()[eventFireIndex()]) {
            player.getPacketBuilder().resetAnimation();
            player.animation(new Animation(pickaxe.getAnimation()));
            player.getPacketBuilder().sendMessage("You mine the rocks...");
            return;
        }

        /** Block if we have no space in our inventory. */
        if (player.getInventory().getContainer().freeSlots() < 1) {
            player.getPacketBuilder().sendMessage("You do not have any space left in your inventory.");
            return;
        }

        /** Block if we aren't a high enough level to mine this ore. */
        if (!player.getSkills()[Misc.MINING].reqLevel(rock.getLevel())) {
            player.getPacketBuilder().sendMessage("You need a mining level of " + rock.getLevel() + " to mine " + rock.name().toLowerCase().replaceAll("_", " ") + ".");
            return;
        }

        /** Prepare for mining and start the mining task. */
        player.getPacketBuilder().sendMessage("You mine the rocks...");
        player.getSkillEvent()[eventFireIndex()] = true;
        player.animation(new Animation(pickaxe.getAnimation()));
        player.getMovementQueue().reset();

        TaskFactory.getFactory().submit(new Worker((Misc.getRandom().nextInt(getMiningTime(player, rock, pickaxe)) + 1), false) {
            @Override
            public void fire() {

                /**
                 * Cancel if there is an empty rock where you are mining
                 * (someone mined it faster than you).
                 */
                if (isEmptyRock(position) && rock != Rock.RUNE_ESSENCE) {
                    fireResetEvent(player);
                    this.cancel();
                    return;
                }

                /**
                 * Cancel if a random integer between 0-15 is equal to 0 (used
                 * for making the player randomly stop mining).
                 */
                if (Misc.getRandom().nextInt(15) == 0 && rock != Rock.RUNE_ESSENCE) {
                    fireResetEvent(player);
                    this.cancel();
                    return;
                }

                /** Cancel if the skill has been stopped. */
                if (!player.getSkillEvent()[eventFireIndex()]) {
                    fireResetEvent(player);
                    this.cancel();
                    return;
                }

                /** Actually mine the ore. */
                player.getPacketBuilder().sendMessage("You recieve the ore that crumbles off of the rocks.");
                player.getInventory().addItem(new Item(rock.getItemId()));
                exp(player, rock.getExperience());

                /** Respawn the rock. */
                if (rock != Rock.RUNE_ESSENCE) {
                    respawnRock(rock, position, objectId);
                }

                fireResetEvent(player);
                this.cancel();
            }
        }.attach(player));

        /**
         * Because the mining animation is based on a strict time of 4 seconds,
         * we use a separate task for the animation.
         */
        TaskFactory.getFactory().submit(new Worker(4, true, WorkRate.APPROXIMATE_SECOND) {
            @Override
            public void fire() {

                /** Cancel if the skill has been stopped. */
                if (!player.getSkillEvent()[eventFireIndex()]) {
                    fireResetEvent(player);
                    this.cancel();
                    return;
                }

                /** Perform the animation */
                player.animation(new Animation(pickaxe.getAnimation()));
            }
        }.attach(player));
    }

    /**
     * Replaces the mined rock with an empty version of the rock and submits a
     * new {@link Worker} to take care of respawning the proper rock.
     * 
     * @param rock
     *        the type of rock that is being respawned.
     * @param position
     *        the position this rock is on.
     * @param objectId
     *        the object id of the rock that was just mined.
     */
    private void respawnRock(Rock rock, final Position position, int objectId) {
        final OreObject respawn = Rock.getOre(rock, objectId);

        /** Register an empty rock. */
        World.getObjects().register(new WorldObject(respawn.getEmpty(), position, Rotation.SOUTH, 10));
        rockSet.add(position);

        /** Schedule a task to respawn the proper ore in place of the empty rock. */
        TaskFactory.getFactory().submit(new Worker(rock.getRespawnTime(), false, WorkRate.APPROXIMATE_SECOND) {
            @Override
            public void fire() {
                World.getObjects().register(new WorldObject(respawn.getRock(), position, Rotation.SOUTH, 10));
                rockSet.remove(position);
                this.cancel();
            }
        });
    }

    /**
     * Sends the player a message of what ore is concealed within the rock.
     * 
     * @param player
     *        the player to send the message to.
     * @param rock
     *        the rock being prospected.
     */
    public void prospect(Player player, Rock rock) {
        player.getPacketBuilder().sendMessage("This rock contains " + rock.name().toLowerCase().replaceAll("_", " ") + " ore.");
    }

    /**
     * Checks if there's an empty rock where you're mining.
     * 
     * @param position
     *        the position to check for an .
     * @return true if there is an empty rock.
     */
    private boolean isEmptyRock(Position position) {
        return !rockSet.contains(position);
    }

    /**
     * Gets the amount of time it takes to mine a rock.
     * 
     * @param player
     *        the player mining the rock.
     * @param rock
     *        the rock being mined.
     * @param pickaxe
     *        the pickaxe being used to mine.
     * @return the amount of time that it will take to mine this rock.
     */
    private int getMiningTime(Player player, Rock rock, Pickaxe pickaxe) {
        if (player.getSkills()[Misc.MINING].getLevel() <= 45) {
            return (rock.getMineRate() + pickaxe.getMineRate()) * 3;
        } else if (player.getSkills()[Misc.MINING].getLevel() > 45 && player.getSkills()[Misc.MINING].getLevel() <= 85) {
            return (rock.getMineRate() + pickaxe.getMineRate()) * 2;
        } else if (player.getSkills()[Misc.MINING].getLevel() > 85) {
            return (rock.getMineRate() + pickaxe.getMineRate());
        }

        return (rock.getMineRate() + pickaxe.getMineRate()) * 3;
    }

    /**
     * Determines if you have a pickaxe which you have the required level to
     * use. If you do it returns an instance of the pickaxe.
     * 
     * @param player
     *        the player to determine this for.
     * @return the instance of the pickaxe.
     */
    public Pickaxe getPickaxe(Player player) {
        for (Pickaxe pickaxe : Pickaxe.values()) {
            if (player.getInventory().getContainer().contains(pickaxe.getItemId()) || player.getEquipment().getContainer().contains(pickaxe.getItemId())) {
                if (player.getSkills()[Misc.MINING].getLevel() >= pickaxe.getLevel()) {
                    return pickaxe;
                }
                player.getPacketBuilder().sendMessage("You are not a high enough level to use the " + pickaxe.name().toLowerCase().replaceAll("_", " ") + " pickaxe.");
                return null;
            }
        }
        player.getPacketBuilder().sendMessage("You need a pickaxe in order to mine rocks!");
        return null;
    }

    /**
     * Gets the {@link Mining} singleton instance.
     * 
     * @return the singleton instance.
     */
    public static Mining getSingleton() {
        if (singleton == null) {
            singleton = new Mining();
        }

        return singleton;
    }

    @Override
    public void fireResetEvent(Player player) {
        player.getPacketBuilder().resetAnimation();
    }

    @Override
    public int eventFireIndex() {
        return SkillEvent.MINING;
    }

    @Override
    public SkillConstant skillConstant() {
        return SkillConstant.MINING;
    }

    /**
     * A rock and its corresponding empty rock replacement.
     * 
     * @author lare96
     */
    public static class OreObject {

        /** The actual rock that can be mined. */
        private int rock;

        /** The empty replacement for the rock when its ore has been mined. */
        private int empty;

        /**
         * Create a new {@link OreObject}.
         * 
         * @param rock
         *        the actual rock that can be mined.
         * @param empty
         *        the empty replacement for the rock when its ore has been
         *        mined.
         */
        public OreObject(int rock, int empty) {
            this.rock = rock;
            this.empty = empty;
        }

        /**
         * Gets the actual rock that can be mined.
         * 
         * @return the actual rock.
         */
        public int getRock() {
            return rock;
        }

        /**
         * Gets the empty replacement for the rock when its ore has been mined.
         * 
         * @return the empty replacement.
         */
        public int getEmpty() {
            return empty;
        }
    }
}
