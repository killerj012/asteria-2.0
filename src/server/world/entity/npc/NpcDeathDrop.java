package server.world.entity.npc;

import server.util.Misc;
import server.util.Misc.Chance;
import server.world.item.Item;

/**
 * A table of items that can be dropped by a npc when they die.
 * 
 * @author lare96
 */
public class NpcDeathDrop {

    /** An array of drop tables for each npc. */
    private static NpcDeathDrop[] dropDefinitions = new NpcDeathDrop[6102];

    /** The id of the npc who will drop these items from the table. */
    private int npcId;

    /** All of the possible items that can be dropped when the npc dies. */
    private DeathDrop[] drops;

    /**
     * Create a new {@link NpcDeathDrop}.
     * 
     * @param npcId
     *        the id of the npc who will drop these items from the table.
     * @param drops
     *        the items that can be dropped when the npc dies.
     */
    public NpcDeathDrop(int npcId, DeathDrop[] drops) {
        this.npcId = npcId;
        this.drops = drops;
    }

    /**
     * Gets the if of the npc who will drop these items from the table.
     * 
     * @return the npc id.
     */
    public int getNpcId() {
        return npcId;
    }

    /**
     * Gets all of the possible items that can be dropped when the npc dies.
     * 
     * @return the drops.
     */
    public DeathDrop[] getDrops() {
        return drops;
    }

    /**
     * Calculates which {@link Item}s should be dropped when this npc dies.
     * 
     * @param npc
     *        the npc's table to retrieve and calculate drops from.
     * @return the item(s) that will be dropped on death.
     */
    public static DeathDrop[] calculateDeathDrop(Npc npc) {

        /** Get the npcs death drop table from the database. */
        NpcDeathDrop dropTable = dropDefinitions[npc.getNpcId()];

        /** If there is no table present for the npc just drop bones. */
        if (dropTable == null) {
            return new DeathDrop[] { new DeathDrop(new Item(526), Chance.ALWAYS) };
        }

        /** Will determine if a rare item was calculated to be dropped. */
        boolean foundRare = false;

        /** Will hold a copy of our drop table that we can modify. */
        DeathDrop[] drops = dropTable.getDrops().clone();

        /** Loop through the drop table. */
        for (int slot = 0; slot < drops.length; slot++) {

            /**
             * If the drop has a chance of <code>ALWAYS</code> do nothing
             * because this item is always dropped.
             */
            if (drops[slot].getChance() == Chance.ALWAYS) {
                continue;
            }

            /** Do calculations based on the drop chance. */
            if ((Misc.getRandom().nextInt(100) + 1) <= drops[slot].getChance().getPercentage()) {

                /**
                 * If the drop chance calculation was successful (the item was
                 * picked) but we already have a rare then remove this drop. We
                 * can only have one rare!
                 */
                if (drops[slot].getChance() == Chance.ALMOST_IMPOSSIBLE || drops[slot].getChance() == Chance.EXTREMELY_RARE && foundRare) {
                    drops[slot] = null;

                    /**
                     * If the drop chance calculation was successful (the item
                     * was picked) but we have no rares then keep the drop and
                     * set a flag that tells us that we've found a rare.
                     */
                } else if (drops[slot].getChance() == Chance.ALMOST_IMPOSSIBLE || drops[slot].getChance() == Chance.EXTREMELY_RARE && !foundRare) {
                    foundRare = true;
                }

                /**
                 * If the drop chance calculation was unsuccessful (the item was
                 * not picked) then simply remove it.
                 */
            } else {
                drops[slot] = null;
            }
        }

        /** Any elements left in the list will be dropped! */
        return drops;
    }

    /**
     * Gets the array of drop tables for each npc.
     * 
     * @return the drop definitions.
     */
    public static NpcDeathDrop[] getDropDefinitions() {
        return dropDefinitions;
    }

    /**
     * A single drop placed by a npc when they die.
     * 
     * @author lare96
     */
    public static class DeathDrop {

        /** The item that will be dropped. */
        private Item item;

        /** The chance of this item being dropped. */
        private Chance chance;

        /**
         * Create a new {@link DeathDrop}.
         * 
         * @param item
         *        the item that will be dropped.
         * @param chance
         *        the chance of this item being dropped.
         */
        public DeathDrop(Item item, Chance chance) {
            this.item = item;
            this.chance = chance;
        }

        @Override
        public Object clone() {
            return new DeathDrop(item, chance);
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
        public Chance getChance() {
            return chance;
        }
    }
}
