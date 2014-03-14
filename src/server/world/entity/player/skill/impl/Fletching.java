package server.world.entity.player.skill.impl;

import server.core.worker.TaskFactory;
import server.core.worker.Worker;
import server.world.entity.Animation;
import server.world.entity.player.Player;
import server.world.entity.player.skill.SkillEvent;
import server.world.entity.player.skill.SkillManager.SkillConstant;
import server.world.entity.player.skill.impl.Fletching.FletchLog.FletchBow;
import server.world.item.Item;

/**
 * Handles the fletching skill. Has support for making incomplete bows and
 * stringing them later, as well as making arrows.
 * 
 * @author lare96
 */
public class Fletching extends SkillEvent {

    /** The {@link Fletching} singleton instance. */
    private static Fletching singleton;

    /**
     * All of the types of logs that can be used in fletching.
     * 
     * @author lare96
     */
    public enum FletchLog {
        NORMAL_LOG(1151, FletchBow.ARROW_SHAFT, FletchBow.NORMAL_SHORTBOW, FletchBow.NORMAL_LONGBOW),
        OAK_LOG(1521, FletchBow.OAK_SHORTBOW, FletchBow.OAK_LONGBOW),
        WILLOW_LOG(1519, FletchBow.WILLOW_SHORTBOW, FletchBow.WILLOW_LONGBOW),
        MAPLE_LOG(1517, FletchBow.MAPLE_SHORTBOW, FletchBow.MAPLE_LONGBOW),
        YEW_LOG(1515, FletchBow.YEW_SHORTBOW, FletchBow.YEW_LONGBOW),
        MAGIC_LOG(1513, FletchBow.MAGIC_SHORTBOW, FletchBow.MAGIC_LONGBOW);

        /** The item id of this log. */
        private int logId;

        /** All of the items that can be made with this log. */
        private FletchBow[] fletch;

        /**
         * Create a new {@link FletchLog}.
         * 
         * @param logId
         *        the item id of this log.
         * @param fletch
         *        the items that can be made with this log.
         */
        FletchLog(int logId, FletchBow... fletch) {
            this.logId = logId;
            this.fletch = fletch;
        }

        /**
         * Gets the item id of this log
         * 
         * @return the log id.
         */
        public int getLogId() {
            return logId;
        }

        /**
         * Gets the items that can be made with this log.
         * 
         * @return the fletching items.
         */
        public FletchBow[] getFletch() {
            return fletch;
        }

        /**
         * All of the items that can be made using a knife and log.
         * 
         * @author lare96
         */
        public enum FletchBow {
            ARROW_SHAFT(new Item(52, 15), null, 1, 5),
            NORMAL_SHORTBOW(new Item(50, 1), new Item(841, 1), 5, 5),
            NORMAL_LONGBOW(new Item(48, 1), new Item(839, 1), 10, 10),
            OAK_SHORTBOW(new Item(54, 1), new Item(843, 1), 20, 16),
            OAK_LONGBOW(new Item(56, 1), new Item(845, 1), 25, 25),
            WILLOW_SHORTBOW(new Item(60, 1), new Item(849, 1), 35, 33),
            WILLOW_LONGBOW(new Item(58, 1), new Item(847, 1), 40, 41),
            MAPLE_SHORTBOW(new Item(64, 1), new Item(853, 1), 50, 50),
            MAPLE_LONGBOW(new Item(62, 1), new Item(851, 1), 55, 58),
            YEW_SHORTBOW(new Item(68, 1), new Item(857, 1), 65, 67),
            YEW_LONGBOW(new Item(66, 1), new Item(855, 1), 70, 75),
            MAGIC_SHORTBOW(new Item(72, 1), new Item(861, 1), 80, 83),
            MAGIC_LONGBOW(new Item(70, 1), new Item(859, 1), 85, 91);

            /** The incomplete item. */
            private Item incompleteBow;

            /** The complete item. */
            private Item completeBow;

            /** The level needed to make this item. */
            private int levelNeeded;

            /** The experience gained when making this item. */
            private int experience;

            /**
             * Create a new {@link FletchBow}.
             * 
             * @param incompleteBow
             *        the incomplete item.
             * @param completeBow
             *        the complete item.
             * @param levelNeeded
             *        the level needed to make this item.
             * @param experience
             *        the experience gained when making this item.
             */
            FletchBow(Item incompleteBow, Item completeBow, int levelNeeded, int experience) {
                this.incompleteBow = incompleteBow;
                this.completeBow = completeBow;
                this.levelNeeded = levelNeeded;
                this.experience = experience;
            }

            /**
             * Gets the incomplete item.
             * 
             * @return the incomplete bow.
             */
            public Item getIncompleteBow() {
                return incompleteBow;
            }

            /**
             * Gets the complete item.
             * 
             * @return the complete bow.
             */
            public Item getCompleteBow() {
                return completeBow;
            }

            /**
             * Gets the level needed to make this item.
             * 
             * @return the level needed.
             */
            public int getLevelNeeded() {
                return levelNeeded;
            }

            /**
             * Gets the experience gained when making this item.
             * 
             * @return the experience gained.
             */
            public int getExperience() {
                return experience;
            }
        }
    }

    /**
     * All of the arrows that can be made from arrow shafts.
     * 
     * @author lare96
     */
    public enum FletchArrow {
        BRONZE_ARROW(39, 1, 1),
        IRON_ARROW(40, 15, 2),
        STEEL_ARROW(41, 30, 5),
        MITHRIL_ARROW(42, 45, 7),
        ADAMANT_ARROW(43, 60, 10),
        RUNE_ARROW(44, 75, 12);

        /** The id of the arrowtip. */
        private int arrowId;

        /** The level needed to fletch this arrow. */
        private int levelNeeded;

        /** The experience gained when fletching this arrow. */
        private int experience;

        /**
         * Create a new {@link FletchArrow}.
         * 
         * @param arrowId
         *        the id of the arrowtip.
         * @param levelNeeded
         *        the level needed to fletch this arrow.
         * @param experience
         *        the experience gained when fletching this arrow.
         */
        FletchArrow(int arrowId, int levelNeeded, int experience) {
            this.arrowId = arrowId;
            this.levelNeeded = levelNeeded;
            this.experience = experience;
        }

        /**
         * Gets the id of the arrowtip.
         * 
         * @return the arrow id.
         */
        public int getArrowId() {
            return arrowId;
        }

        /**
         * Gets the level needed to fletch this arrow.
         * 
         * @return the level needed.
         */
        public int getLevelNeeded() {
            return levelNeeded;
        }

        /**
         * Gets the experience gained when fletching this arrow.
         * 
         * @return the experience.
         */
        public int getExperience() {
            return experience;
        }
    }

    // use logs with knife
    public void fletchIncompleteBow(Player player, int amount) {
        // knife id = 946
        // fletching emote = 6782
    }

    // use string with incomplete bow
    public void stringBow(Player player, FletchLog log, FletchBow bow, int amount) {
        // knife id = 946
        // fletching emote = 6782
    }

    /**
     * Starts the process of the player fletching headless arrows.
     * 
     * @param player
     *        the player fletching headless arrows.
     * @param amount
     *        the amount of headless arrows being fletched.
     */
    public void fletchHeadlessArrow(final Player player) {

        /** Check if we have the required materials. */
        if (!player.getInventory().getContainer().contains(52)) {
            player.getPacketBuilder().sendMessage("You cannot fletch headless arrows without arrow shafts!");
            return;
        } else if (!player.getInventory().getContainer().contains(314)) {
            player.getPacketBuilder().sendMessage("You cannot fletch headless arrows without feathers!");
            return;
        }

        TaskFactory.getFactory().submit(new Worker(3, true) {
            private int amount = 15;

            @Override
            public void fire() {

                /** Check if we have the required materials. */
                if (!player.getInventory().getContainer().contains(52)) {
                    player.getPacketBuilder().sendMessage("You cannot fletch headless arrows without arrow shafts!");
                    this.cancel();
                    return;
                } else if (!player.getInventory().getContainer().contains(314)) {
                    player.getPacketBuilder().sendMessage("You cannot fletch headless arrows without feathers!");
                    this.cancel();
                    return;
                }

                /** Set the amount to what we have if needed. */
                if (player.getInventory().getContainer().getCount(52) < amount || (player.getInventory().getContainer().getCount(314) < amount)) {
                    amount = Math.min(player.getInventory().getContainer().getCount(52), player.getInventory().getContainer().getCount(314));
                }

                /** Make the headless arrows and give the experience. */
                player.getInventory().deleteItem(new Item(52, amount));
                player.getInventory().deleteItem(new Item(314, amount));
                player.getInventory().addItem(new Item(53, amount));
                player.animation(new Animation(6782));
                exp(player, 1 * amount);
            }
        }.attach(player));
    }

    // use headless arrow with arrow tips
    public void fletchArrow(Player player, FletchArrow arrow, int amount) {
        // fletching emote = 6782
    }

    /**
     * Gets the {@link Fletching} singleton instance.
     * 
     * @return the singleton instance.
     */
    public static Fletching getSingleton() {
        if (singleton == null) {
            singleton = new Fletching();
        }

        return singleton;
    }

    @Override
    public void fireResetEvent(Player player) {
        player.getPacketBuilder().resetAnimation();
    }

    @Override
    public int eventFireIndex() {
        return SkillEvent.FLETCHING;
    }

    @Override
    public SkillConstant skillConstant() {
        return SkillConstant.FLETCHING;
    }
}
