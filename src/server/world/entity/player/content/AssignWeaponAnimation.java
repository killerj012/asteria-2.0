package server.world.entity.player.content;

import server.util.Misc;
import server.world.entity.player.Player;
import server.world.item.Item;
import server.world.item.ItemDefinition;

/**
 * Changes the appearance animation for the player whenever a new weapon is
 * equipped or an existing item is unequipped.
 * 
 * @author lare96
 */
public class AssignWeaponAnimation {

    /** A map of items and the appearance animations. */
    private static WeaponAnimationIndex[] weaponAnimation = new WeaponAnimationIndex[7956];

    /** Loads all of the items and appearance animations. */
    static {

        /** Iterate through the item database. */
        for (ItemDefinition def : ItemDefinition.getDefinitions()) {

            /** Filter items. */
            if (def == null || def.isNoted()
                    || def.getEquipmentSlot() != Misc.EQUIPMENT_SLOT_WEAPON) {
                continue;
            }

            /** Load the map with the correct data. */
            if (def.getItemName().endsWith("2h sword")) {
                weaponAnimation[def.getItemId()] = new WeaponAnimationIndex(
                        2561, 2064, 2563);
            } else if (def.getItemName().equalsIgnoreCase("granite maul")) {
                weaponAnimation[def.getItemId()] = new WeaponAnimationIndex(
                        1662, 1663, 1664);
            } else if (def.getItemName().equalsIgnoreCase("boxing gloves")) {
                weaponAnimation[def.getItemId()] = new WeaponAnimationIndex(
                        3677, 3680, -1);
            } else if (def.getItemName().endsWith("whip")) {
                weaponAnimation[def.getItemId()] = new WeaponAnimationIndex(
                        1832, 1660, 1661);
            } else if (def.getItemName().equalsIgnoreCase("fixed device")) {
                weaponAnimation[def.getItemId()] = new WeaponAnimationIndex(
                        2316, 2317, 2322);
            } else if (def.getItemName().endsWith("halberd")
                    || def.getItemName().contains("guthan")) {
                weaponAnimation[def.getItemId()] = new WeaponAnimationIndex(
                        809, 1146, 1210);
            } else if (def.getItemName().startsWith("Dharoks")) {
                weaponAnimation[def.getItemId()] = new WeaponAnimationIndex(
                        2065, 1663, 1664);
            } else if (def.getItemName().startsWith("Ahrims")) {
                weaponAnimation[def.getItemId()] = new WeaponAnimationIndex(
                        809, 1146, 1210);
            } else if (def.getItemName().startsWith("Veracs")) {
                weaponAnimation[def.getItemId()] = new WeaponAnimationIndex(
                        1832, 1830, 1831);
            } else if (def.getItemName().startsWith("Karils")
                    || def.getItemName().equals("Crossbow")) {
                weaponAnimation[def.getItemId()] = new WeaponAnimationIndex(
                        2074, 2076, 2077);
            } else if (def.getItemName().endsWith("shortbow")
                    || def.getItemName().endsWith("longbow")) {
                weaponAnimation[def.getItemId()] = new WeaponAnimationIndex(
                        808, 819, 824);
            } else if (def.getItemName().equalsIgnoreCase("dragon longsword")) {
                weaponAnimation[def.getItemId()] = new WeaponAnimationIndex(
                        809, -1, -1);
            }
        }
    }

    /**
     * Assigns the correct animation for the player based on the item.
     * 
     * @param player
     *            the player to assign the animation for.
     * @param item
     *            the item to base the animation on.
     */
    public static void assignAnimation(Player player, Item item) {

        /** Check if the item is valid. */
        if (item == null) {
            return;
        }

        /** Reset the animations. */
        player.getUpdateAnimation().reset();

        /** Retrieve the animation container for the weapon from the map. */
        WeaponAnimationIndex animation = weaponAnimation[item.getId()];

        /** If this weapon is not in the map keep the default animation. */
        if (animation == null) {
            return;
        }

        /** Otherwise update the animation. */
        player.getUpdateAnimation().setAs(animation);
    }

    /**
     * Container that holds animations for a certain weapon.
     * 
     * @author lare96
     */
    public static class WeaponAnimationIndex {

        /** The standing animation for this player. */
        private int standingAnimation = -1;

        /** The walking animation for this player. */
        private int walkingAnimation = -1;

        /** The running animation for this player. */
        private int runningAnimation = -1;

        /**
         * Create a new {@link WeaponAnimationIndex}.
         * 
         * @param standingAnimation
         *            the standing animation for this player.
         * @param walkingAnimation
         *            the walking animation for this player.
         * @param runningAnimation
         *            the running animation for this player.
         */
        public WeaponAnimationIndex(int standingAnimation,
                int walkingAnimation, int runningAnimation) {
            this.standingAnimation = standingAnimation;
            this.walkingAnimation = walkingAnimation;
            this.runningAnimation = runningAnimation;
        }

        /**
         * Create a new {@link WeaponAnimationIndex}.
         */
        public WeaponAnimationIndex() {

        }

        /**
         * Sets this container's values with another container's values.
         * 
         * @param index
         *            the other container to set this container to.
         * @return this container after the values have been set.
         */
        public WeaponAnimationIndex setAs(WeaponAnimationIndex index) {
            this.standingAnimation = index.standingAnimation;
            this.walkingAnimation = index.walkingAnimation;
            this.runningAnimation = index.runningAnimation;
            return this;
        }

        /**
         * Resets the animation indexes.
         */
        public void reset() {
            standingAnimation = -1;
            walkingAnimation = -1;
            runningAnimation = -1;
        }

        /**
         * Gets the standing animation for this player.
         * 
         * @return the standing animation.
         */
        public int getStandingAnimation() {
            return standingAnimation;
        }

        /**
         * Gets the walking animation for this player.
         * 
         * @return the walking animation.
         */
        public int getWalkingAnimation() {
            return walkingAnimation;
        }

        /**
         * Gets the running animation for this player.
         * 
         * @return the running animation.
         */
        public int getRunningAnimation() {
            return runningAnimation;
        }

        /**
         * Sets the standing animation for this player.
         * 
         * @param standingAnimation
         *            the new standing animation to set.
         */
        public void setStandingAnimation(int standingAnimation) {
            this.standingAnimation = standingAnimation;
        }

        /**
         * Sets the walking animation for this player.
         * 
         * @param walkingAnimation
         *            the new walking animation to set.
         */
        public void setWalkingAnimation(int walkingAnimation) {
            this.walkingAnimation = walkingAnimation;
        }

        /**
         * Sets the running animation for this player.
         * 
         * @param runningAnimation
         *            the new running animation to set.
         */
        public void setRunningAnimation(int runningAnimation) {
            this.runningAnimation = runningAnimation;
        }
    }
}
