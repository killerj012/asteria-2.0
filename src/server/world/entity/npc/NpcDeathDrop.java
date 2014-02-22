package server.world.entity.npc;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import server.util.Misc;
import server.util.Misc.Chance;
import server.world.item.Item;

/**
 * A collection of drops placed by a mob when they die and conditions to manage
 * those drops.
 * 
 * @author lare96
 */
public class NpcDeathDrop {

    /**
     * An array of drop tables for each mob.
     */
    private static NpcDeathDrop[] dropDefinitions = new NpcDeathDrop[6102];

    /**
     * The id of the mob who will drop these items from the table.
     */
    private int mobId;

    /**
     * All of the possible items that can be dropped when the mob dies.
     */
    private DeathDrop[] drops;

    /**
     * Create a new {@link NpcDeathDrop}.
     * 
     * @param mobId
     *        the id of the mob who will drop these items from the table.
     * @param drops
     *        the items that can be dropped when the mob dies.
     */
    public NpcDeathDrop(int mobId, DeathDrop[] drops) {
        this.mobId = mobId;
        this.drops = drops;
    }

    /**
     * Gets the if of the mob who will drop these items from the table.
     * 
     * @return the mob id.
     */
    public int getMobId() {
        return mobId;
    }

    /**
     * Gets all of the possible items that can be dropped when the mob dies.
     * 
     * @return the drops.
     */
    public DeathDrop[] getDrops() {
        return drops;
    }

    /**
     * Calculates which {@link Item}'s should be dropped when this mob dies.
     * 
     * @param dropTable
     *        the table to calculate from.
     * @return the item that is dropped on death.
     */
    public static DeathDrop[] calculateDeathDrop(NpcDeathDrop dropTable) {
        List<DeathDrop> choose = Arrays.asList(dropTable.getDrops());

        for (Iterator<DeathDrop> iterator = choose.iterator(); iterator.hasNext();) {
            DeathDrop drop = iterator.next();

            if (!((Misc.getRandom().nextInt(100) + 1) <= drop.getItemChance().getPercentage())) {
                iterator.remove();
            }
        }

        return (DeathDrop[]) choose.toArray();
    }

    /**
     * Gets the array of drop tables for each mob.
     * 
     * @return the drop definitions.
     */
    public static NpcDeathDrop[] getDropDefinitions() {
        return dropDefinitions;
    }

    /**
     * A single drop placed by a mob when they die.
     * 
     * @author lare96
     */
    public static class DeathDrop {

        /**
         * The item that will be dropped.
         */
        private Item item;

        /**
         * The chance of this item being dropped.
         */
        private Chance itemChance;

        /**
         * Create a new {@link DeathDrop}.
         */
        public DeathDrop(Item item, Chance itemChance) {
            this.item = item;
            this.itemChance = itemChance;
        }

        /**
         * Gets the item that will be dropped.
         * 
         * @return the item.
         */
        public Item getItem() {
            return item;
        }

        /**
         * Gets the chance of this item being dropped.
         * 
         * @return the item chance.
         */
        public Chance getItemChance() {
            return itemChance;
        }
    }
}
