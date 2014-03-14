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
 * Handles the woodcutting skill. This has support for multiple players cutting
 * on the same tree, being able to get more than one log from every tree except
 * normal, tree stumps, and respawning.
 * 
 * @author lare96
 */
public class Woodcutting extends SkillEvent {

    // XXX: Near perfect woodcutting, just needs random nests and a better speed
    // formula :)

    /** The {@link Woodcutting} singleton instance. */
    private static Woodcutting singleton;

    /**
     * A {@link HashSet} of the positions of {@link TreeStump}s currently
     * placed throughout the game.
     */
    private static Set<Position> treeSet = new HashSet<Position>();

    /**
     * All of the possible types of trees that can be cut.
     * 
     * @author lare96
     */
    public enum Tree {
        NORMAL(1, 1511, 10, 25, 1, 2, new TreeStump[] { new TreeStump(1276, 1342), new TreeStump(1277, 1341), new TreeStump(1278, 1342), new TreeStump(1279, 1341), new TreeStump(1280, 1341), new TreeStump(1282, 1347), new TreeStump(1283, 1347), new TreeStump(1284, 1350), new TreeStump(1285, 1341), new TreeStump(1286, 1352), new TreeStump(1287, 1341), new TreeStump(1288, 1341), new TreeStump(1289, 1352), new TreeStump(1290, 1341), new TreeStump(1291, 1352), new TreeStump(1301, 1341), new TreeStump(1303, 1341), new TreeStump(1304, 1341), new TreeStump(1305, 1341), new TreeStump(1318, 1355), new TreeStump(1319, 1355), new TreeStump(1315, 1342), new TreeStump(1316, 1355), new TreeStump(1330, 1355), new TreeStump(1331, 1355), new TreeStump(1332, 1355), new TreeStump(1333, 1341), new TreeStump(1383, 1341), new TreeStump(1384, 1352), new TreeStump(2409, 1341), new TreeStump(2447, 1341), new TreeStump(2448, 1341), new TreeStump(3033, 1341), new TreeStump(3034, 1341), new TreeStump(3035, 1341), new TreeStump(3036, 1341), new TreeStump(3879, 1341), new TreeStump(3881, 1341), new TreeStump(3883, 1341), new TreeStump(3893, 1341), new TreeStump(3885, 1341), new TreeStump(3886, 1341), new TreeStump(3887, 1341), new TreeStump(3888, 1341), new TreeStump(3892, 1341), new TreeStump(3889, 1341), new TreeStump(3890, 1341), new TreeStump(3891, 1341), new TreeStump(3928, 1341), new TreeStump(3967, 1341), new TreeStump(3968, 1341), new TreeStump(4048, 1341), new TreeStump(4049, 1341), new TreeStump(4050, 1341), new TreeStump(4051, 1341), new TreeStump(4052, 1341), new TreeStump(4053, 1341), new TreeStump(4054, 1341), new TreeStump(4060, 1341), new TreeStump(5004, 1341), new TreeStump(5005, 1341), new TreeStump(5045, 1341), new TreeStump(5902, 1341), new TreeStump(5903, 1341), new TreeStump(5904, 1341), new TreeStump(8973, 1341), new TreeStump(8974, 1341) }),
        OAK(15, 1521, 15, 37, 7, 3, new TreeStump[] { new TreeStump(1281, 1356), new TreeStump(3037, 1341), new TreeStump(8462, 1341), new TreeStump(8463, 1341), new TreeStump(8464, 1341), new TreeStump(8465, 1341), new TreeStump(8466, 1341), new TreeStump(8467, 1341), new TreeStump(10083, 1341), new TreeStump(13413, 1341), new TreeStump(13420, 1341) }),
        WILLOW(30, 1519, 25, 67, 20, 3, new TreeStump[] { new TreeStump(1308, 7399), new TreeStump(5551, 5554), new TreeStump(5552, 5554), new TreeStump(5553, 5554), new TreeStump(8481, 1341), new TreeStump(8482, 1341), new TreeStump(8483, 1341), new TreeStump(8484, 1341), new TreeStump(8485, 1341), new TreeStump(8486, 1341), new TreeStump(8487, 1341), new TreeStump(8488, 1341), new TreeStump(8496, 1341), new TreeStump(8497, 1341), new TreeStump(8498, 1341), new TreeStump(8499, 1341), new TreeStump(8500, 1341), new TreeStump(8501, 1341) }),
        MAPLE(45, 1517, 45, 100, 25, 4, new TreeStump[] { new TreeStump(1307, 1342), new TreeStump(4674, 1342), new TreeStump(8435, 1341), new TreeStump(8436, 1341), new TreeStump(8437, 1341), new TreeStump(8438, 1341), new TreeStump(8439, 1341), new TreeStump(8440, 1341), new TreeStump(8441, 1341), new TreeStump(8442, 1341), new TreeStump(8443, 1341), new TreeStump(8444, 1341), new TreeStump(8454, 1341), new TreeStump(8455, 1341), new TreeStump(8456, 1341), new TreeStump(8457, 1341), new TreeStump(8458, 1341), new TreeStump(8459, 1341), new TreeStump(8460, 1341), new TreeStump(8461, 1341) }),
        YEW(60, 1515, 80, 175, 40, 6, new TreeStump[] { new TreeStump(1309, 7402), new TreeStump(8503, 1341), new TreeStump(8504, 1341), new TreeStump(8505, 1341), new TreeStump(8506, 1341), new TreeStump(8507, 1341), new TreeStump(8508, 1341), new TreeStump(8509, 1341), new TreeStump(8510, 1341), new TreeStump(8511, 1341), new TreeStump(8512, 1341), new TreeStump(8513, 1341) }),
        MAGIC(75, 1513, 120, 250, 50, 8, new TreeStump[] { new TreeStump(1306, 1341), new TreeStump(8396, 1341), new TreeStump(8397, 1341), new TreeStump(8398, 1341), new TreeStump(8399, 1341), new TreeStump(8400, 1341), new TreeStump(8401, 1341), new TreeStump(8402, 1341), new TreeStump(8403, 1341), new TreeStump(8404, 1341), new TreeStump(8405, 1341), new TreeStump(8406, 1341), new TreeStump(8407, 1341), new TreeStump(8408, 1341), new TreeStump(840, 1341) });

        /** The level needed to cut this type of tree. */
        private int level;

        /** The item id of the log obtained after cutting this type of tree. */
        private int logId;

        /** The time it takes for this type of tree to respawn in seconds. */
        private int respawnTime;

        /** The experience received when you get a log from this type of tree. */
        private int experience;

        /** The maximum amount of logs in a single tree of this type. */
        private int logsInTree;

        /** The speed in getting logs from this type of tree. */
        private int speed;

        /**
         * The tree object id's and the stump object id's that replace them once
         * cut.
         */
        private TreeStump[] trees;

        /**
         * A map with the object id of every tree mapped to an instance of the
         * tree.
         */
        private static Map<Integer, Tree> treeMap = new HashMap<Integer, Tree>();

        /**
         * A map with the object id of every tree mapped to an instance of the
         * stump object.
         */
        private static Map<Integer, TreeStump> stumpMap = new HashMap<Integer, TreeStump>();

        /**
         * Loads the <code>treeMap</code> and <code>stumpMap</code> with the
         * appropriate data.
         */
        static {
            for (Tree tree : Tree.values()) {
                for (TreeStump stump : tree.getTrees()) {
                    treeMap.put(stump.getTreeId(), tree);
                    stumpMap.put(stump.getTreeId(), stump);
                }
            }
        }

        /**
         * Create a new {@link Tree}.
         * 
         * @param level
         *        the level needed to cut this type of tree.
         * @param logId
         *        the item id of the log obtained after cutting this type of
         *        tree.
         * @param respawnTime
         *        the time it takes for this type of tree to respawn in seconds.
         * @param experience
         *        the experience received when you get a log from this type of
         *        tree.
         * @param logsInTree
         *        the maximum amount of logs in a single tree of this type.
         * @param speed
         *        the speed in getting logs from this type of tree.
         * @param trees
         *        the tree object id's and the stump object id's that replace
         *        them once cut.
         */
        Tree(int level, int logId, int respawnTime, int experience, int logsInTree, int speed, TreeStump[] trees) {
            this.level = level;
            this.logId = logId;
            this.respawnTime = respawnTime;
            this.experience = experience;
            this.logsInTree = logsInTree;
            this.speed = speed;
            this.trees = trees;
        }

        /**
         * Gets the level needed to cut this type of tree.
         * 
         * @return the level.
         */
        public int getLevel() {
            return level;
        }

        /**
         * Gets the item id of the log obtained after cutting this type of tree.
         * 
         * @return the log id.
         */
        public int getLogId() {
            return logId;
        }

        /**
         * Gets the time it takes for this type of tree to respawn in seconds.
         * 
         * @return the respawn time.
         */
        public int getRespawnTime() {
            return respawnTime;
        }

        /**
         * Gets the experience received when you get a log from this type of
         * tree.
         * 
         * @return the experience.
         */
        public int getExperience() {
            return experience;
        }

        /**
         * Gets the maximum amount of logs in a single tree of this type.
         * 
         * @return the logs in tree.
         */
        public int getLogsInTree() {
            return logsInTree;
        }

        /**
         * Gets the speed in getting logs from this type of tree.
         * 
         * @return the speed.
         */
        public int getSpeed() {
            return speed;
        }

        /**
         * Gets the tree object id's and the stump object id's that replace them
         * once cut.
         * 
         * @return the trees.
         */
        public TreeStump[] getTrees() {
            return trees;
        }

        /**
         * Gets the stump for the specified tree.
         * 
         * @param objectId
         *        the specified tree to get the stump for.
         * @return the
         */
        public TreeStump getStumpObject(int objectId) {
            return stumpMap.get(objectId);
        }

        /**
         * Gets a {@link Tree} constant by its object id.
         * 
         * @param objectId
         *        the id of the constant to get.
         * @return the constant.
         */
        public static Tree getTree(int objectId) {
            return treeMap.get(objectId);
        }

        /**
         * Determines if this enum contains a {@link Tree} constant with this
         * object id.
         * 
         * @param id
         *        the object id to check this enum for.
         * @return if this enum contains a constant with this object id.
         */
        public static boolean containsTree(int id) {
            return !(getTree(id) == null);
        }
    }

    /**
     * All of the possible types axes that can be used to cut a tree.
     * 
     * @author lare96
     */
    public enum Axe {
        BRONZE(1351, 1, 879, 7),
        IRON(1349, 1, 877, 7),
        STEEL(1353, 6, 875, 6),
        BLACK(1361, 6, 873, 5),
        MITHRIL(1355, 21, 871, 4),
        ADAMANT(1357, 31, 869, 3),
        RUNE(1359, 41, 867, 2),
        DRAGON(6739, 61, 2846, 0);

        /** The id of the axe. */
        private int id;

        /** The level of the axe. */
        private int level;

        /** The animation that will be used when cutting with this axe. */
        private int animation;

        /** The speed of this axe. */
        private int speed;

        /**
         * Create a new {@link Axe}.
         * 
         * @param id
         *        the id of the axe.
         * @param level
         *        the level of the axe.
         * @param animation
         *        the animation that will be used when cutting with this axe.
         * @param speed
         *        the speed of this axe.
         */
        Axe(int id, int level, int animation, int speed) {
            this.id = id;
            this.level = level;
            this.animation = animation;
            this.speed = speed;
        }

        /**
         * Gets the id of the axe.
         * 
         * @return the axe id.
         */
        public int getId() {
            return id;
        }

        /**
         * Gets the level of the axe.
         * 
         * @return the level.
         */
        public int getLevel() {
            return level;
        }

        /**
         * Gets the animation that will be used when cutting with this axe.
         * 
         * @return the animation.
         */
        public int getAnimation() {
            return animation;
        }

        /**
         * Gets the speed of this axe.
         * 
         * @return the speed.
         */
        public int getSpeed() {
            return speed;
        }
    }

    /**
     * Start cutting a {@link Tree} for the specified {@link Player}.
     * 
     * @param player
     *        the player cutting this tree.
     * @param tree
     *        the type of tree this player is cutting.
     * @param axe
     *        the type of axe being used.
     * @param position
     *        the position of this tree.
     * @param objectId
     *        the object id of this tree.
     */
    public void chopTree(final Player player, final Tree tree, final Axe axe, final Position position, final int objectId) {

        /** Block if we are already woodcutting. */
        if (player.getSkillEvent()[eventFireIndex()]) {
            player.getPacketBuilder().resetAnimation();
            player.animation(new Animation(axe.getAnimation()));
            player.getPacketBuilder().sendMessage("You swing your axe at the tree...");
            return;
        }

        /** Block if we have no space in our inventory. */
        if (player.getInventory().getContainer().freeSlots() < 1) {
            player.getPacketBuilder().sendMessage("You do not have any space left in your inventory.");
            return;
        }

        /** Block if we aren't a high enough level to cut this tree. */
        if (!player.getSkills()[Misc.WOODCUTTING].reqLevel(tree.getLevel())) {
            player.getPacketBuilder().sendMessage("You need a woodcutting level of " + tree.getLevel() + " to cut " + tree.name().toLowerCase().replaceAll("_", " ") + " trees.");
            return;
        }

        /** Prepare the player for woodcutting. */
        player.getPacketBuilder().sendMessage("You swing your axe at the tree...");
        player.getSkillEvent()[eventFireIndex()] = true;
        player.animation(new Animation(axe.getAnimation()));
        player.setWoodcuttingLogAmount(getLogsInTree(tree));
        player.getMovementQueue().reset();

        /** Submit the woodcutting worker. */
        TaskFactory.getFactory().submit(new Worker(Misc.getRandom().nextInt(getWoodcuttingTime(player, tree, axe)) + 1, false, WorkRate.APPROXIMATE_SECOND) {
            @Override
            public void fire() {

                /**
                 * Block and cancel this worker if there is a stump where you
                 * are cutting (someone cut the tree faster than you).
                 */
                if (isTreeStump(position)) {
                    fireResetEvent(player);
                    this.cancel();
                    return;
                }

                /**
                 * Block and cancel this worker if a random integer between 0-15
                 * is equal to 0 (used for making the player randomly stop
                 * mining).
                 */
                if (Misc.getRandom().nextInt(15) == 0) {
                    fireResetEvent(player);
                    this.cancel();
                    return;
                }

                /**
                 * Block and cancel this worker if the skill has been stopped.
                 */
                if (!player.getSkillEvent()[eventFireIndex()]) {
                    fireResetEvent(player);
                    this.cancel();
                    return;
                }

                /** Here we get some logs. */
                player.getPacketBuilder().sendMessage("You recieve some " + tree.name().toLowerCase().replaceAll("_", " ") + " logs.");
                player.getInventory().addItem(new Item(tree.getLogId()));
                exp(player, tree.getExperience());
                player.decrementWoodcuttingLogAmount();

                /**
                 * If the tree is out of logs we stop this worker, replace the
                 * tree with a stump and work on getting a new one in its place.
                 */
                if (player.getWoodcuttingLogAmount() == 0) {
                    respawnTree(tree, position, objectId);
                    fireResetEvent(player);
                    this.cancel();
                    return;
                }

                /** Another check for inventory space. */
                if (player.getInventory().getContainer().freeSlots() < 1) {
                    player.getPacketBuilder().sendMessage("You do not have any space left in your inventory.");
                    fireResetEvent(player);
                    this.cancel();
                    return;
                }
            }
        }.attach(player));

        /**
         * Because the woodcutting animation is based on a strict time we use a
         * separate worker for the animation.
         */
        TaskFactory.getFactory().submit(new Worker(5, true) {
            @Override
            public void fire() {

                /**
                 * Block and cancel this worker if the skill has been stopped.
                 */
                if (!player.getSkillEvent()[eventFireIndex()]) {
                    this.cancel();
                    return;
                }

                /** Perform the animation. */
                player.animation(new Animation(axe.getAnimation()));
            }
        }.attach(player));
    }

    /**
     * Replaces the cut {@link Tree} with the stump and submits a {@link Worker}
     * to respawn the {@link Tree} that was cut.
     * 
     * @param tree
     *        the type of tree to respawn.
     * @param position
     *        the position of the stump.
     * @param objectId
     *        the object id to replace the stump with.
     */
    private void respawnTree(Tree tree, final Position position, int objectId) {

        /** The stump we are going to place. */
        final TreeStump treeStump = tree.getStumpObject(objectId);

        /** Place the stump object. */
        World.getObjects().register(new WorldObject(treeStump.getStumpId(), position, Rotation.SOUTH, 10));
        treeSet.add(position);

        /** Submit a worker to respawn the tree in place of the stump. */
        TaskFactory.getFactory().submit(new Worker(tree.getRespawnTime(), false, WorkRate.APPROXIMATE_SECOND) {
            @Override
            public void fire() {
                World.getObjects().register(new WorldObject(treeStump.getTreeId(), position, Rotation.SOUTH, 10));
                treeSet.remove(position);
                this.cancel();
            }
        });
    }

    /**
     * Determines if there is a {@link TreeStump} on the specified
     * {@link Position}.
     * 
     * @param position
     *        the position to check for a stump.
     * @return true if there is on stump on the position.
     */
    private boolean isTreeStump(Position position) {
        return treeSet.contains(position);
    }

    /**
     * Calculates the time it takes to receive a log from a {@link Tree}.
     * 
     * @param player
     *        the player trying to cut this tree.
     * @param tree
     *        the type of tree being cut.
     * @param axe
     *        the type of axe being used.
     */
    private int getWoodcuttingTime(Player player, Tree tree, Axe axe) {
        if (player.getSkills()[Misc.WOODCUTTING].getLevel() <= 45) {
            return (tree.getSpeed() + axe.getSpeed()) * 3;
        } else if (player.getSkills()[Misc.WOODCUTTING].getLevel() > 45 && player.getSkills()[Misc.WOODCUTTING].getLevel() <= 85) {
            return (tree.getSpeed() + axe.getSpeed()) * 2;
        } else if (player.getSkills()[Misc.WOODCUTTING].getLevel() > 85) {
            return (tree.getSpeed() + axe.getSpeed());
        }

        return (tree.getSpeed() + axe.getSpeed()) * 3;
    }

    /**
     * Gets the amount of logs in the tree.
     * 
     * @param tree
     *        the type of tree.
     * @return the amount of logs in the tree.
     */
    private int getLogsInTree(Tree tree) {
        int amount = Misc.getRandom().nextInt(tree.getLogsInTree());

        return amount == 0 ? 1 : amount;
    }

    /**
     * Determines if you have an axe and the required level to use it to cut a
     * tree.
     * 
     * @param player
     *        the player being checked.
     * @return the axe you are wielding (if any).
     */
    public Axe getAxe(Player player) {
        for (Axe axe : Axe.values()) {
            if (player.getInventory().getContainer().contains(axe.getId()) || player.getEquipment().getContainer().contains(axe.getId())) {
                if (player.getSkills()[Misc.WOODCUTTING].getLevel() >= axe.getLevel()) {
                    return axe;
                }

                player.getPacketBuilder().sendMessage("You are not a high enough level to use the " + axe.name().toLowerCase().replaceAll("_", " ") + " axe.");
                return null;
            }
        }

        player.getPacketBuilder().sendMessage("You need an axe in order to cut trees!");
        return null;
    }

    /**
     * Gets the {@link Woodcutting} singleton instance.
     * 
     * @return the singleton instance.
     */
    public static Woodcutting getSingleton() {
        if (singleton == null) {
            singleton = new Woodcutting();
        }

        return singleton;
    }

    @Override
    public void fireResetEvent(Player player) {
        player.getSkillEvent()[eventFireIndex()] = false;
        player.getPacketBuilder().resetAnimation();
        player.setWoodcuttingLogAmount(0);
    }

    @Override
    public int eventFireIndex() {
        return SkillEvent.WOODCUTTING;
    }

    @Override
    public SkillConstant skillConstant() {
        return SkillConstant.WOODCUTTING;
    }

    /**
     * A container for the object id of a tree and its corresponding stump.
     * 
     * @author lare96
     */
    public static class TreeStump {

        /** The object id of the tree that will replace the stump. */
        private int treeId;

        /** The object id of the stump that will replace the tree. */
        private int stumpId;

        /**
         * Create a new {@link TreeStump}.
         * 
         * @param treeId
         *        the object id of the tree that will replace the stump.
         * @param stumpId
         *        the object id of the stump that will replace the tree.
         */
        public TreeStump(int treeId, int stumpId) {
            this.treeId = treeId;
            this.stumpId = stumpId;
        }

        /**
         * Gets the object id of the tree that will replace the stump.
         * 
         * @return the tree id.
         */
        public int getTreeId() {
            return treeId;
        }

        /**
         * Gets the object id of the stump that will replace the tree.
         * 
         * @return the stump id.
         */
        public int getStumpId() {
            return stumpId;
        }
    }
}
