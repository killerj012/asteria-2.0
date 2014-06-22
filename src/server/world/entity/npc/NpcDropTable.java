package server.world.entity.npc;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import server.util.Misc;
import server.world.entity.player.Player;
import server.world.item.Item;

/**
 * The table that will be used to hold every drop possible for an {@link Npc}
 * along with various functions to calculate which items will be dropped.
 * 
 * @author lare96
 */
public class NpcDropTable {

    /**
     * Will be used to hold all of the drops for each npc. We use a map for easy
     * and fast retrieval of the npc's table (npc id -----> drop table).
     */
    private static Map<Integer, NpcDropTable> drops = new HashMap<Integer, NpcDropTable>();

    /**
     * Will be used to generate a random number called a <code>roll</code> that
     * will be compared against the number assigned to the drop called a
     * <code>bet</code> that will determine if the item can be dropped or not.
     */
    private static Random roll = new Random();

    /** The npc who uses this drop table. */
    private int npc;

    /** The dynamic drop table assigned to this npc. */
    private NpcDrop[] dynamic;

    /** The rare table assigned to this npc. */
    private NpcDrop[] rare;

    /**
     * Create a new {@link NpcDropTable}.
     * 
     * @param npc
     *        the npc who uses this drop table.
     * @param dynamic
     *        the dynamic drop table assigned to this npc.
     * @param rare
     *        the rare table assigned to this npc.
     */
    public NpcDropTable(int npc, NpcDrop[] dynamic, NpcDrop[] rare) {
        this.npc = npc;
        this.dynamic = dynamic;
        this.rare = rare;
    }

    /**
     * Calculates a set of drops will the intention that they will be dropped on
     * death, although they can be used however one would like. Please note that
     * this is not a static implementation, meaning that calling this method
     * will return a different set of items each time.
     * 
     * @param player
     *        the player these items are being calculated for.
     * @return the calculated items that will be dropped.
     */
    public Item[] calculateDrops(Player player) {

        /** The placeholder that will be used to track our slot position. */
        int slot = 0;

        /** The array of items that will be dropped. */
        Item[] item = new Item[getDropLength()];

        /** Gamble all items in the dynamic table. */
        if (dynamic != null && dynamic.length > 0) {
            for (NpcDrop drop : dynamic) {
                if (drop == null) {
                    continue;
                }

                /** Compare the roll against the bet. */
                if (roll.nextDouble() <= drop.getBet()) {

                    /** Roll was successful! Add the item to the drops. */
                    item[slot++] = drop.toItem();
                }
            }
        }

        /** Gamble one item in the rare table. */
        if (rare != null && rare.length > 0) {

            /** The bet modification value. */
            BetModification betMod = getBetModification(player);

            /** Select one random item from out of the rare table. */
            NpcDrop rareDrop = Misc.randomElement(rare);

            /**
             * Compare the roll against the bet, including the bet
             * modifications.
             */
            if (roll.nextDouble() <= (rareDrop.getBet() + betMod.getMod())) {

                /** Roll was successful! Add the item to the drops. */
                item[slot++] = rareDrop.toItem();

                /** Drop successful! Apply any bet modification effects here. */
                betMod.itemPassed(player, rareDrop);
            } else {

                /** Drop unsuccessful! Apply any bet modification effects here. */
                betMod.itemFailed(player, rareDrop);
            }

        }

        return item;
    }

    /**
     * Gets the max possible size of the array that will hold the drops. Any
     * <code>null</code> elements will be filtered out when the items are
     * dropped.
     * 
     * @return the size of the array that will hold the drops.
     */
    private int getDropLength() {

        /** The common table length plus one, for one rare item. */
        return dynamic.length + 1;
    }

    /**
     * Any modifications to the <code>bet</code> should be put here. The number
     * returned will increase the chance of a rare item being dropped.
     * Alternatively, you can return a negative {@link Double} to decrease the
     * chance of a rare item being dropped.
     * 
     * @param player
     *        the player these items are being dropped for.
     * @return the modification to the <code>bet</code>.
     */
    private static BetModification getBetModification(Player player) {

        /** Check if we have a ring of wealth equipped. */
        if (player.getEquipment().getContainer().getItemId(Misc.EQUIPMENT_SLOT_RING) == 2572) {

            /** Chance to drop a rare item is increased by 0.025, which is 2.5%. */
            return new BetModification(0.025) {
                @Override
                public void itemPassed(Player player, NpcDrop item) {

                    /** Item dropped, do ring of wealth stuff. */
                    if (roll.nextBoolean()) {
                        player.getEquipment().removeItem(Misc.EQUIPMENT_SLOT_RING);
                        player.getPacketBuilder().sendMessage("Your ring of wealth takes effect and crumbles into dust!");
                    } else {
                        player.getPacketBuilder().sendMessage("Your ring of wealth takes effect and keeps itself intact!");
                    }
                }

                @Override
                public void itemFailed(Player player, NpcDrop item) {

                    /** Item did not drop, nothing happens to our ring. */
                }
            };
        }
        return BetModification.DEFAULT_BET_MOD;
    }

    /**
     * Gets all of the drops for each npc.
     * 
     * @return all of the drops for each npc.
     */
    public static Map<Integer, NpcDropTable> getAllDrops() {
        return drops;
    }

    /**
     * Gets the npc who uses this drop table.
     * 
     * @return the npc who uses this drop table.
     */
    public int getNpc() {
        return npc;
    }

    /**
     * A container class used to hold the bet modification.
     * 
     * @author lare96
     */
    public static abstract class BetModification {

        /**
         * The default bet modification. We use this so we do not have to check
         * for <code>null</code> values.
         */
        public static final BetModification DEFAULT_BET_MOD = new BetModification(0.0) {
            @Override
            public void itemPassed(Player player, NpcDrop item) {

                /** Nothing happens, no bet modification. */
            }

            @Override
            public void itemFailed(Player player, NpcDrop item) {

                /** Nothing happens, no bet modification. */
            }
        };

        /** The fixed bet modification. */
        private double mod;

        /**
         * Create a new {@link BetModification}.
         * 
         * @param mod
         *        the fixed bet modification.
         */
        public BetModification(double mod) {
            this.mod = mod;
        }

        /**
         * What happens when the item drops.
         * 
         * @param player
         *        the player who is receiving the drops.
         * @param item
         *        the item being dropped.
         */
        public abstract void itemPassed(Player player, NpcDrop item);

        /**
         * What happens when the item was selected, but not successful enough to
         * be dropped.
         * 
         * @param player
         *        the player who is receiving the drops.
         * @param item
         *        the item being dropped.
         */
        public abstract void itemFailed(Player player, NpcDrop item);

        /**
         * Gets the fixed bet modification.
         * 
         * @return the fixed bet modification.
         */
        public double getMod() {
            return mod;
        }
    }

    /**
     * A single item that will be gambled when the assigned npc dies in order to
     * determine if it will be dropped or not.
     * 
     * @author lare96
     */
    public static class NpcDrop {

        /** The id of item that will be dropped. */
        private int id;

        /** The maximum amount of item that will be dropped. */
        private int amount;

        /**
         * The chance that this item will be dropped. The lowest chance being
         * 0.0 (0%, impossible) and the highest being 1.0 (100%, always).
         */
        private double bet;

        /**
         * Create a new {@link DeathDrop}.
         * 
         * @param id
         *        the id of item that will be dropped.
         * @param amount
         *        the maximum amount of item that will be dropped.
         * @param bet
         *        the chance that this item will be dropped.
         */
        public NpcDrop(int id, int amount, double bet) {
            this.id = id;
            this.amount = amount;
            this.bet = bet;
        }

        /**
         * Turns the id and amount into an {@link Item} instance.
         * 
         * @return the item instance representing the id and amount of this
         *         drop.
         */
        public Item toItem() {
            return new Item(id, Misc.randomNoZero(amount));
        }

        /**
         * Gets the chance that this item will be dropped.
         * 
         * @return the chance that this item will be dropped.
         */
        public double getBet() {
            return bet;
        }
    }
}
