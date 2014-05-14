package server.world.entity.player.content;

import java.util.HashMap;
import java.util.Map;

import server.util.Misc;
import server.world.entity.Animation;
import server.world.entity.player.Player;
import server.world.item.Item;

/**
 * Manages the consumption of various food types.
 * 
 * @author lare96
 */
public class ConsumeFood {

    /** The delay interval for food consumption. */
    private static final int EATING_DELAY = 1800;

    /** The animation played when the player is consuming food. */
    private static final Animation CONSUME = new Animation(829);

    /**
     * All of the many different types of foods that can be consumed for
     * hitpoint regeneration.
     * 
     * @author lare96
     */
    public enum Food {
        // XXX: GenericAction<?> can be used here to make it so certain
        // foods have special effects!

        ANCHOVIES(319, 3, true),
        SWORDFISH(373, 14, true),
        COD(339, 7, true),
        BREAD(2309, 2, true),
        PIKE(351, 8, true),
        SHRIMPS(315, 3, true),
        LOBSTER(379, 12, true),
        CHOCOLATE_CAKE(1901, 5, true),
        MACKEREL(355, 6, true),
        BASS(365, 13, true),
        SHARK(385, 20, true),
        TROUT(333, 7, true),
        CAKE(1891, 4, true),
        MANTA_RAY(391, 22, true),
        TWO_THIRDS_OF_CAKE(1893, 4, true),
        SLICE_OF_CAKE(1895, 4, false),
        TUNA(361, 10, true),
        SALMON(329, 9, true);

        /** The item id of the consumable food. */
        private int itemId;

        /** The amount of hitpoints the food will heal. */
        private int heal;

        /** If 'the' should come before the name of the food (for grammar). */
        private boolean grammarCheck;

        /**
         * A map that allows us to get a {@link Food} constant by its id.
         */
        private static Map<Integer, Food> food = new HashMap<Integer, Food>();

        /** Fill the map with data. */
        static {
            for (Food f : Food.values()) {
                food.put(f.getItemId(), f);
            }
        }

        /**
         * Create a new {@link Food}.
         * 
         * @param itemId
         *        the item id of the food.
         * @param heal
         *        the amount it heals.
         * @param grammarCheck
         *        if 'the' should come before the name of the food.
         */
        Food(int itemId, int heal, boolean grammarCheck) {
            this.itemId = itemId;
            this.heal = heal;
            this.grammarCheck = grammarCheck;
        }

        /**
         * Gets the item id of the consumable food.
         * 
         * @return the item id.
         */
        public int getItemId() {
            return itemId;
        }

        /**
         * Gets the amount of hitpoints the food will heal.
         * 
         * @return the heal amount.
         */
        public int getHeal() {
            return heal;
        }

        /**
         * Gets if 'the' should come before the name of the food (for grammar).
         * 
         * @return true if it should.
         */
        public boolean isGrammarCheck() {
            return grammarCheck;
        }

        /**
         * Gets an instance of food by its id.
         * 
         * @param id
         *        the id to get the instance of.
         * @return the instance.
         */
        public static Food forId(int id) {
            return food.get(id);
        }
    }

    /**
     * Allows the player to consume a certain type of food.
     * 
     * @param player
     *        the player consuming the food.
     * @param food
     *        the food type to consume.
     * @param slot
     *        the slot the food is in.
     */
    public static void consume(final Player player, final Food food, final int slot) {

        /** Checks if this food type is valid. */
        if (food == null) {
            return;
        }

        /** Check if your hp is currently 0. */
        if (player.getSkills()[Misc.HITPOINTS].getLevel() < 1) {
            player.getPacketBuilder().sendMessage("Too late, you're dead!");
            return;
        }

        /** Consume the food. */
        if (player.getEatingTimer().elapsed() > EATING_DELAY) {
            if (player.getInventory().getContainer().contains(food.getItemId())) {
                if (player.getSkills()[Misc.HITPOINTS].getLevelForExperience() > player.getSkills()[Misc.HITPOINTS].getLevel()) {
                    player.animation(CONSUME);
                    player.getInventory().deleteItemSlot(new Item(food.getItemId()), slot);
                    player.getPacketBuilder().sendMessage(food.isGrammarCheck() ? "You eat the " + food.name().toLowerCase().replaceAll("_", " ") + " and it restores some health." : "You eat a " + food.name().toLowerCase().replaceAll("_", " ") + " and it restores some health.");
                } else {
                    player.getPacketBuilder().sendMessage(food.isGrammarCheck() ? "You eat the " + food.name().toLowerCase().replaceAll("_", " ") + "." : "You eat a " + food.name().toLowerCase().replaceAll("_", " ") + ".");
                    player.animation(CONSUME);
                    player.getInventory().deleteItemSlot(new Item(food.getItemId()), slot);
                }

                switch (food.getItemId()) {
                    case 1891:
                        player.getInventory().overrideItemSlot(new Item(1893), slot);
                        break;
                    case 1893:
                        player.getInventory().overrideItemSlot(new Item(1895), slot);
                        break;
                }

                player.heal(food.getHeal());
                player.getEatingTimer().reset();
            }
        }
    }
}
