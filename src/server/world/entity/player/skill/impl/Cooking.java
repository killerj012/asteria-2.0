package server.world.entity.player.skill.impl;

import java.util.HashMap;
import java.util.Map;

import server.core.Rs2Engine;
import server.core.worker.Worker;
import server.util.Misc;
import server.world.entity.Animation;
import server.world.entity.player.Player;
import server.world.entity.player.skill.SkillEvent;
import server.world.entity.player.skill.SkillManager.SkillConstant;
import server.world.item.Item;
import server.world.item.ItemDefinition;

/**
 * Handles the cooking skill. This has support for burning formulas with a
 * higher chance of burning on a fire and the ability to stop burning certain
 * foods at certain levels.
 * 
 * @author lare96
 */
public class Cooking extends SkillEvent {

    // XXX: Perfect cooking, just needs more content like pies, pizzas,
    // churning, etc. All fish work though. Oh, and I need to make it so it
    // stops cooking when fire turns into ashes :)

    /**
     * The {@link Cooking} singleton instance.
     */
    private static Cooking singleton;

    /**
     * All of the food we are able to cook over a stove or fire.
     * 
     * @author lare96
     */
    public enum CookFish {
        SHRIMP(317, 1, 315, 34, 323, 30),
        SARDINE(327, 5, 325, 38, 369, 40),
        HERRING(345, 10, 347, 37, 357, 50),
        ANCHOVIES(321, 15, 319, 34, 323, 30),
        MACKEREL(353, 16, 355, 45, 357, 60),
        TROUT(335, 20, 333, 50, 343, 70),
        COD(341, 23, 339, 39, 343, 75),
        PIKE(349, 25, 351, 52, 343, 80),
        SLIMY_EEL(3379, 28, 3381, 56, 3383, 95),
        SALMON(331, 30, 329, 58, 343, 90),
        TUNA(359, 35, 361, 63, 367, 100),
        CAVE_EEL(5001, 38, 5003, 72, 5002, 115),
        LOBSTER(377, 40, 379, 74, 381, 120),
        BASS(363, 46, 365, 80, 367, 130),
        SWORDFISH(371, 50, 373, 86, 375, 140),
        LAVA_EEL(2148, 53, 2149, 89, 3383, 140),
        SHARK(383, 76, 385, 94, 387, 210);

        /** The id of the raw fish. */
        private int rawFishId;

        /** The level needed to cook the raw fish. */
        private int levelNeeded;

        /** The id of the cooked fish. */
        private int cookedFishId;

        /** The level you stop burning this fish at. */
        private int stopBurnLevel;

        /** The id of the burnt fish. */
        private int burntFishId;

        /** The experience you get when you properly cook this fish. */
        private int experience;

        /**
         * A map that holds the id's of the raw fish with the corresponding
         * {@link CookFish} instances.
         */
        private static Map<Integer, CookFish> fishMap = new HashMap<Integer, CookFish>();

        /** Fill the map with the correct data. */
        static {

            /** Iterate through this enum and fill the map. */
            for (CookFish c : CookFish.values()) {
                fishMap.put(c.getRawFishId(), c);
            }
        }

        /**
         * Creates a new {@link CookFish}.
         * 
         * @param rawFishId
         *        the id of the raw fish.
         * @param levelNeeded
         *        the level needed to cook the raw fish.
         * @param cookedFishId
         *        the id of the cooked fish.
         * @param stopBurnLevel
         *        the level you stop burning this fish at.
         * @param burntFishId
         *        the id of the burnt fish.
         * @param experience
         *        the experience you get when you properly cook this fish.
         */
        CookFish(int rawFishId, int levelNeeded, int cookedFishId, int stopBurnLevel, int burntFishId, int experience) {
            this.rawFishId = rawFishId;
            this.levelNeeded = levelNeeded;
            this.cookedFishId = cookedFishId;
            this.stopBurnLevel = stopBurnLevel;
            this.burntFishId = burntFishId;
            this.experience = experience;
        }

        /**
         * Gets the id of the uncooked fish.
         * 
         * @return the raw fish id.
         */
        public int getRawFishId() {
            return rawFishId;
        }

        /**
         * Gets the level needed to cook this fish.
         * 
         * @return the level needed.
         */
        public int getLevelNeeded() {
            return levelNeeded;
        }

        /**
         * Gets the id of the cooked fish.
         * 
         * @return the cooked fish id.
         */
        public int getCookedFishId() {
            return cookedFishId;
        }

        /**
         * Gets the level that the player will stop burning this fish at.
         * 
         * @return the stop burn level.
         */
        public int getStopBurnLevel() {
            return stopBurnLevel;
        }

        /**
         * Gets the id of the burnt fish.
         * 
         * @return the burnt fish id.
         */
        public int getBurntFishId() {
            return burntFishId;
        }

        /**
         * Gets the experience gained when successfully cooking this fish.
         * 
         * @return the experience.
         */
        public int getExperience() {
            return experience;
        }

        /**
         * Gets an {@link CookFish} instance by the id of its uncooked fish.
         * 
         * @param id
         *        the id to get the instance of.
         * @return the {@link CookFish} instance.
         */
        public static CookFish getFish(int id) {
            return fishMap.get(id);
        }
    }

    /**
     * Opens the cooking interface in the chatbox for the player.
     * 
     * @param player
     *        the player to open the interface for.
     * @param displayItemId
     *        the item to display on the cooking interface.
     */
    public void openCookingSelection(Player player, int displayItemId) {
        player.getPacketBuilder().sendChatInterface(1743);
        player.getPacketBuilder().sendItemOnInterface(13716, 190, displayItemId);
        player.getPacketBuilder().sendString("\\n\\n\\n\\n\\n" + ItemDefinition.getDefinitions()[displayItemId].getItemName() + "", 13717);
    }

    /**
     * Cooks the specified {@link CookFish} at the designated amount.
     * 
     * @param player
     *        the player cooking the fish.
     * @param fish
     *        the type of fish that the player is cooking.
     * @param amount
     *        the amount of fish that the player is cooking.
     */
    public void cookFish(final Player player, final CookFish fish, final int amount) {

        /** Block if this fish type is invalid. */
        if (fish == null) {
            return;
        }

        /** Block if we are already cooking. */
        if (player.getSkillEvent()[eventFireIndex()]) {
            player.getPacketBuilder().sendMessage("You cannot cook two things at once!");
            player.getPacketBuilder().closeWindows();
            return;
        }

        /** Block if we don't have the raw fish in our inventory. */
        if (!player.getInventory().getContainer().contains(fish.getRawFishId())) {
            player.getPacketBuilder().sendMessage("You do not have any " + ItemDefinition.getDefinitions()[fish.getRawFishId()].getItemName() + " to cook!");
            player.getPacketBuilder().closeWindows();
            return;
        }

        /** Block if we aren't at the correct level to cook this fish. */
        if (!player.getSkills()[Misc.COOKING].reqLevel(fish.getLevelNeeded())) {
            player.getPacketBuilder().sendMessage("You need a Cooking level of " + fish.getLevelNeeded() + " to cook this " + ItemDefinition.getDefinitions()[fish.getRawFishId()].getItemName() + ".");
            player.getPacketBuilder().closeWindows();
            return;
        }

        /** Prepare for cooking. */
        player.getSkillEvent()[eventFireIndex()] = true;
        player.getPacketBuilder().closeWindows();
        player.getMovementQueue().reset();
        player.setCookAmount(0);

        /** And start cooking :) */
        Rs2Engine.getWorld().submit(new Worker(4, true) {
            @Override
            public void fire() {

                /** Stop if we have reached the amount we are cooking. */
                if (player.getCookAmount() == amount) {
                    fireResetEvent(player);
                    this.cancel();
                    return;
                }

                /** Stop if we have ran out of raw food to cook. */
                if (!player.getInventory().getContainer().contains(fish.getRawFishId())) {
                    player.getPacketBuilder().sendMessage("You've ran out of " + ItemDefinition.getDefinitions()[fish.getRawFishId()].getItemName() + ".");
                    fireResetEvent(player);
                    this.cancel();
                    return;
                }

                /** Stop if for some reason we are no longer cooking. */
                if (!player.getSkillEvent()[eventFireIndex()]) {
                    fireResetEvent(player);
                    this.cancel();
                    return;
                }

                /** Close any windows currently open. */
                player.getPacketBuilder().closeWindows();

                /** Perform animation based on if we are using a stove or not. */
                player.animation(!player.isUsingStove() ? new Animation(897) : new Animation(896));

                /** Cook food with burn formula. */
                if (player.getSkills()[Misc.COOKING].reqLevel(fish.getLevelNeeded())) {
                    if (!calculateBurnChance(player, fish)) {
                        exp(player, fish.getExperience());
                        player.getInventory().replaceItem(new Item(fish.getRawFishId(), 1), new Item(fish.getCookedFishId(), 1));
                    } else {
                        player.getInventory().replaceItem(new Item(fish.getRawFishId(), 1), new Item(fish.getBurntFishId(), 1));
                    }
                } else {
                    player.getPacketBuilder().sendMessage("You need a cooking level of " + fish.getLevelNeeded() + " to cook this " + ItemDefinition.getDefinitions()[fish.getRawFishId()].getItemName() + ".");
                    fireResetEvent(player);
                    this.cancel();
                    return;
                }

                player.addCookAmount();
            }
        }.attach(player));
    }

    /**
     * Used to calculate whether we will burn the food or not. Thanks to whoever
     * posted the formula on rune-server.
     * 
     * @param player
     *        the player cooking the fish.
     * @param fish
     *        the type of fish that the player is cooking.
     * @return true if you burned the food.
     */
    private boolean calculateBurnChance(Player player, CookFish cook) {

        /**
         * Flag false if we at or above the level where you stop burning this
         * type of fish.
         */
        if (player.getSkills()[Misc.COOKING].getLevel() >= cook.getStopBurnLevel()) {
            player.getPacketBuilder().sendMessage("You successfully cook the " + ItemDefinition.getDefinitions()[cook.getRawFishId()].getItemName() + ".");
            return false;
        }

        /**
         * Here we do some simple calculations. The 55.0 can be lowered for a
         * less chance of burning (and vice versa).
         */
        double burn_chance = (55.0 - (player.isUsingStove() ? 4.0 : 0.0));
        double cook_level = player.getSkills()[Misc.COOKING].getLevel();
        double lev_needed = cook.getLevelNeeded();
        double burn_stop = cook.getStopBurnLevel();
        double multi_a = (burn_stop - lev_needed);
        double burn_dec = (burn_chance / multi_a);
        double multi_b = (cook_level - lev_needed);
        burn_chance -= (multi_b * burn_dec);
        double randNum = Misc.getRandom().nextDouble() * 100.0;

        /** Here we successfully cook the food. */
        if (burn_chance <= randNum) {
            player.getPacketBuilder().sendMessage("You successfully cook the " + ItemDefinition.getDefinitions()[cook.getRawFishId()].getItemName() + ".");
            return false;
        }

        /** Otherwise we burn! :( */
        player.getPacketBuilder().sendMessage("Oops! You accidently burn the " + ItemDefinition.getDefinitions()[cook.getRawFishId()].getItemName() + ".");
        return true;
    }

    /**
     * Gets the {@link Cooking} singleton instance.
     * 
     * @return the singleton instance.
     */
    public static Cooking getSingleton() {
        if (singleton == null) {
            singleton = new Cooking();
        }

        return singleton;
    }

    @Override
    public void fireResetEvent(Player player) {
        player.getPacketBuilder().resetAnimation();
        player.setCook(null);
        player.setCookAmount(0);
    }

    @Override
    public int eventFireIndex() {
        return SkillEvent.COOKING;
    }

    @Override
    public SkillConstant skillConstant() {
        return SkillConstant.COOKING;
    }
}
