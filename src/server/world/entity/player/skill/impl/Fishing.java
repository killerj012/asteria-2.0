package server.world.entity.player.skill.impl;

import java.util.Iterator;

import javax.tools.Tool;

import server.core.Rs2Engine;
import server.core.worker.Worker;
import server.util.Misc;
import server.util.Misc.Chance;
import server.world.entity.Animation;
import server.world.entity.player.Player;
import server.world.entity.player.skill.SkillEvent;
import server.world.entity.player.skill.SkillManager.SkillConstant;
import server.world.item.Item;
import server.world.item.ItemDefinition;

/**
 * Handles the fishing skill. Has support for a variety of fish with all of the
 * correct tools.
 * 
 * @author lare96
 */
public class Fishing extends SkillEvent {

    // XXX: This is pretty good fishing. All this needs is a better fish
    // calculation method, fishing certain fish in certain areas, and catching
    // multiple fish with big nets to be near perfect.

    /**
     * The {@link Fishing} singleton instance.
     */
    private static Fishing singleton;

    /**
     * All of the tools that can be used to catch fish.
     * 
     * @author lare96
     */
    public enum FishingTool {
        NET(303, 1, -1, 3, 621, Fish.SHRIMP, Fish.ANCHOVY),
        BIG_NET(305, 16, -1, 3, 620, Fish.MACKEREL, Fish.OYSTER, Fish.COD, Fish.BASS, Fish.CASKET),
        FISHING_ROD(307, 5, 313, 1, 622, Fish.SARDINE, Fish.HERRING, Fish.PIKE, Fish.SLIMY_EEL, Fish.CAVE_EEL),
        OILY_FISHING_ROD(1585, 53, 313, 1, 622, Fish.LAVA_EEL),
        FLY_FISHING_ROD(309, 20, 314, 1, 622, Fish.TROUT, Fish.SALMON),
        HARPOON(311, 35, -1, 4, 618, Fish.TUNA, Fish.SWORDFISH, Fish.SHARK),
        LOBSTER_POT(301, 40, -1, 4, 619, Fish.LOBSTER);

        /** The item id of the tool. */
        private int id;

        /** The level you need to be to use this tool. */
        private int level;

        /** The id of an item needed to use this tool. */
        private int needed;

        /** The speed of this tool. */
        private int speed;

        /** The animation performed when using this tool. */
        private int animation;

        /** All of the fish you can catch with this tool. */
        private Fish[] fish;

        /**
         * Creates a new {@link Tool}.
         * 
         * @param id
         *        the item id of the tool.
         * @param level
         *        the level you need to be to use this tool.
         * @param needed
         *        the id of an item needed to use this tool.
         * @param speed
         *        the speed of this tool.
         * @param animation
         *        the animation performed when using this tool.
         * @param fish
         *        the fish you can catch with this tool.
         */
        FishingTool(int id, int level, int needed, int speed, int animation, Fish... fish) {
            this.id = id;
            this.level = level;
            this.needed = needed;
            this.speed = speed;
            this.animation = animation;
            this.fish = fish;
        }

        /**
         * Gets the item id of this tool.
         * 
         * @return the item id.
         */
        public int getId() {
            return id;
        }

        /**
         * Gets the level you need to be to use this tool.
         * 
         * @return the level needed.
         */
        public int getLevel() {
            return level;
        }

        /**
         * Gets the id of an item needed to use this tool.
         * 
         * @return the item needed.
         */
        public int getNeeded() {
            return needed;
        }

        /**
         * Gets the speed of this tool.
         * 
         * @return the speed.
         */
        public int getSpeed() {
            return speed;
        }

        /**
         * Gets the animation performed when using this tool.
         * 
         * @return the animation.
         */
        public int getAnimation() {
            return animation;
        }

        /**
         * Gets the fish you can catch with this tool.
         * 
         * @return the fish available.
         */
        public Fish[] getFish() {
            return fish;
        }
    }

    /**
     * All of the fish that can be caught while fishing.
     * 
     * @author lare96
     */
    public enum Fish {
        SHRIMP(317, 1, Chance.VERY_COMMON, 10),
        SARDINE(327, 5, Chance.VERY_COMMON, 20),
        HERRING(345, 10, Chance.VERY_COMMON, 30),
        ANCHOVY(321, 15, Chance.SOMETIMES, 40),
        MACKEREL(353, 16, Chance.VERY_COMMON, 20),
        CASKET(405, 16, Chance.ALMOST_IMPOSSIBLE, 100),
        OYSTER(407, 16, Chance.EXTREMELY_RARE, 80),
        TROUT(335, 20, Chance.VERY_COMMON, 50),
        COD(341, 23, Chance.VERY_COMMON, 45),
        PIKE(349, 25, Chance.VERY_COMMON, 60),
        SLIMY_EEL(3379, 28, Chance.EXTREMELY_RARE, 65),
        SALMON(331, 30, Chance.VERY_COMMON, 70),
        TUNA(359, 35, Chance.VERY_COMMON, 80),
        CAVE_EEL(5001, 38, Chance.SOMETIMES, 80),
        LOBSTER(377, 40, Chance.VERY_COMMON, 90),
        BASS(363, 46, Chance.SOMETIMES, 100),
        SWORDFISH(371, 50, Chance.COMMON, 100),
        LAVA_EEL(2148, 53, Chance.VERY_COMMON, 60),
        SHARK(383, 76, Chance.COMMON, 110);

        /** The item id of the fish. */
        private int id;

        /** The level needed to be able to catch the fish. */
        private int level;

        /** The chance of catching this fish (when grouped with other fishes). */
        private Chance chance;

        /** The experience gained from catching this fish. */
        private int experience;

        /**
         * Creates a new {@link Fish}.
         * 
         * @param id
         *        the item id of the fish.
         * @param level
         *        the level needed to be able to catch the fish.
         * @param chance
         *        the chance of catching this fish (when grouped with other
         *        fishes).
         * @param experience
         *        the experience gained from catching this fish.
         */
        Fish(int id, int level, Chance chance, int experience) {
            this.id = id;
            this.level = level;
            this.chance = chance;
            this.experience = experience;
        }

        /**
         * Gets the item id of the fish.
         * 
         * @return the item id.
         */
        public int getId() {
            return id;
        }

        /**
         * Gets the level needed to be able to catch the fish.
         * 
         * @return the level.
         */
        public int getLevel() {
            return level;
        }

        /**
         * Gets the chance of catching this fish (when grouped with other
         * fishes).
         * 
         * @return the chance.
         */
        public Chance getChance() {
            return chance;
        }

        /**
         * Gets the experience gained from catching this fish.
         * 
         * @return the experience.
         */
        public int getExperience() {
            return experience;
        }
    }

    /**
     * Starts the fishing process.
     * 
     * @param player
     *        the player who is fishing.
     * @param tool
     *        the tool this player is fishing with.
     */
    public void startFish(final Player player, final FishingTool tool) {

        /** Block and reset if we are already fishing. */
        if (player.getSkillEvent()[eventFireIndex()]) {
            player.getPacketBuilder().resetAnimation();
            player.animation(new Animation(tool.getAnimation()));
            player.getPacketBuilder().sendMessage("You begin to fish...");
            return;
        }

        /** Block if we don't have the correct tools. */
        if (!player.getInventory().getContainer().contains(tool.getId())) {
            player.getPacketBuilder().sendMessage("You don't have the correct tools to fish here!");
            return;
        }

        /** Block if we don't have the items needed for the tools. */
        if (tool.getNeeded() > 0) {
            if (!player.getInventory().getContainer().contains(tool.getNeeded())) {
                player.getPacketBuilder().sendMessage("You do not have any " + ItemDefinition.getDefinitions()[tool.getNeeded()].getItemName() + ".");
                return;
            }
        }

        /** Block if we don't have enough inventory space. */
        if (player.getInventory().getContainer().freeSlots() < 1) {
            player.getPacketBuilder().sendMessage("You do not have any space left in your inventory.");
            return;
        }

        /** Block if we aren't a high enough level to use this tool. */
        if (!player.getSkills()[Misc.FISHING].reqLevel(tool.getLevel())) {
            player.getPacketBuilder().sendMessage("You are not a high enough level to fish with this. You must have a fishing level of " + tool.getLevel() + ".");
            return;
        }

        /** Prepare the player for fishing. */
        player.getPacketBuilder().sendMessage("You begin to fish...");
        player.getSkillEvent()[eventFireIndex()] = true;
        player.animation(new Animation(tool.getAnimation()));
        player.getMovementQueue().reset();

        /** Start the fishing process. */
        Rs2Engine.getWorld().submit(new Worker(tool.getSpeed() + addFishingTime(player), false) {
            @Override
            public void fire() {

                /** Stop if we do not have correct tool. */
                if (!player.getInventory().getContainer().contains(tool.getId())) {
                    fireResetEvent(player);
                    this.cancel();
                    return;
                }

                /** Stop if we don't have items needed for the tool. */
                if (tool.getNeeded() > 0) {
                    if (!player.getInventory().getContainer().contains(tool.getNeeded())) {
                        player.getPacketBuilder().sendMessage("You do not have anymore " + ItemDefinition.getDefinitions()[tool.getId()].getItemName() + ".");
                        fireResetEvent(player);
                        this.cancel();
                        return;
                    }
                }

                /** Block if we don't have enough inventory space. */
                if (player.getInventory().getContainer().freeSlots() < 1) {
                    player.getPacketBuilder().sendMessage("You do not have any space left in your inventory.");
                    fireResetEvent(player);
                    this.cancel();
                    return;
                }

                /** Block if we aren't a high enough level. */
                if (!player.getSkills()[Misc.FISHING].reqLevel(tool.getLevel())) {
                    player.getPacketBuilder().sendMessage("You are not a high enough level to fish with this. You must have a fishing level of " + tool.getLevel() + ".");
                    fireResetEvent(player);
                    this.cancel();
                    return;
                }

                /** Random stop for that 'old school' rs feel :) */
                if (Misc.getRandom().nextInt(15) == 0) {
                    fireResetEvent(player);
                    this.cancel();
                    return;
                }

                /** Stop if the skill is inactive. */
                if (!player.getSkillEvent()[eventFireIndex()]) {
                    fireResetEvent(player);
                    this.cancel();
                    return;
                }

                if (player.getSkillEvent()[eventFireIndex()]) {

                    /** Get a random fish for us. */
                    Fish caught = determineFish(player, tool);

                    /** Catch the fish. */
                    player.getFish().clear();
                    player.getPacketBuilder().sendMessage("You catch a " + caught.name().toLowerCase().replace("_", " ") + ".");
                    player.getInventory().addItem(new Item(caught.getId()));
                    exp(player, caught.getExperience());

                    /** Delete the item needed for the tools. */
                    if (tool.getNeeded() > 0) {
                        player.getInventory().deleteItem(new Item(tool.getNeeded()));
                    }

                    /** Another check for fishing tools. */
                    if (!player.getInventory().getContainer().contains(tool.getId())) {
                        player.getPacketBuilder().sendMessage("You don't have the correct tools to fish here!");
                        fireResetEvent(player);
                        this.cancel();
                        return;
                    }

                    /** Another check for items needed for tools. */
                    if (tool.getNeeded() > 0) {
                        if (!player.getInventory().getContainer().contains(tool.getNeeded())) {
                            player.getPacketBuilder().sendMessage("You do not have anymore " + ItemDefinition.getDefinitions()[tool.getId()].getItemName() + "(s) left.");
                            fireResetEvent(player);
                            this.cancel();
                            return;
                        }
                    }

                    /** Another check for inventory space. */
                    if (player.getInventory().getContainer().freeSlots() < 1) {
                        player.getPacketBuilder().sendMessage("You do not have any space left in your inventory.");
                        fireResetEvent(player);
                        this.cancel();
                        return;
                    }
                }
            }
        }.attach(player));

        /**
         * Because the fishing animation is based on a strict cycle of 4 ticks
         * we use a separate task for the animation.
         */
        Rs2Engine.getWorld().submit(new Worker(4, true) {
            @Override
            public void fire() {

                /** Stop if this skill is inactive. */
                if (!player.getSkillEvent()[eventFireIndex()]) {
                    fireResetEvent(player);
                    this.cancel();
                    return;
                }

                /** Do the animation. */
                player.animation(new Animation(tool.getAnimation()));
            }
        }.attach(player));
    }

    /**
     * Gets a random fish to be caught for the player based on fishing level and
     * rarity.
     * 
     * @param player
     *        the player that needs a fish.
     * @param tool
     *        the tool this player is fishing with.
     */
    private Fish determineFish(Player player, FishingTool tool) {

        /** Determine which fish are able to be caught. */
        for (Fish fish : tool.getFish()) {
            if (fish.getLevel() <= player.getSkills()[Misc.FISHING].getLevel()) {
                player.getFish().add(fish);
            }
        }

        /** Filter the fish based on rarity. */
        for (Iterator<Fish> iterator = player.getFish().iterator(); iterator.hasNext();) {
            Fish fish = iterator.next();

            if (player.getFish().size() == 1) {
                /** Return this fish if it's the only one left in the list. */
                return fish;
            }

            if (!fish.getChance().success(Misc.getRandom().nextInt(100))) {
                iterator.remove();
            }
        }

        /** Return a random fish from the list. */
        return Misc.randomElement(player.getFish());
    }

    /**
     * Adds on to the time that it takes to catch a fish.
     * 
     * @param player
     *        the player trying to catch a fish.
     * @return the time based on the fishing level of the player.
     */
    private int addFishingTime(Player player) {
        return (10 - (int) Math.floor(player.getSkills()[Misc.FISHING].getLevel() / 10));
    }

    /**
     * Gets the {@link Fishing} singleton instance.
     * 
     * @return the singleton instance.
     */
    public static Fishing getSingleton() {
        if (singleton == null) {
            singleton = new Fishing();
        }

        return singleton;
    }

    @Override
    public void fireResetEvent(Player player) {
        player.getPacketBuilder().resetAnimation();
        player.getFish().clear();
    }

    @Override
    public int eventFireIndex() {
        return SkillEvent.FISHING;
    }

    @Override
    public SkillConstant skillConstant() {
        return SkillConstant.FISHING;
    }
}
