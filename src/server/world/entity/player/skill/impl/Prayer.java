package server.world.entity.player.skill.impl;

import java.util.HashMap;
import java.util.Map;

import server.world.entity.Animation;
import server.world.entity.Gfx;
import server.world.entity.player.Player;
import server.world.entity.player.skill.SkillEvent;
import server.world.entity.player.skill.SkillManager.SkillConstant;
import server.world.item.Item;

/**
 * Handles the training portion of the prayer skill.
 * 
 * @author lare96
 */
public class Prayer extends SkillEvent {

    /** The {@link Prayer} singleton instance. */
    private static Prayer singleton;

    /** The delay between burying bones. */
    private static final int BURY_DELAY = 1200;

    /** The delay between using bones on an altar. */
    private static final int ALTAR_DELAY = 3500;

    /** The animation played when burying an item. */
    private static final Animation BURY = new Animation(827);

    /** The animation played when using an item on the altar. */
    private static final Animation ALTAR = new Animation(896);

    /**
     * All of the possible items that can be buried or used on an altar for
     * prayer experience.
     * 
     * @author lare96
     */
    public enum PrayerItem {
        BONES(526, 4),
        BAT_BONES(530, 5),
        MONKEY_BONES(3179, 5),
        WOLF_BONES(2859, 4),
        BIG_BONES(532, 15),
        BABYDRAGON_BONES(534, 30),
        DRAGON_BONES(536, 72);

        /** The id of the item we are using. */
        private int itemId;

        /** The experience gained from using this item. */
        private int experience;

        /**
         * A map that will allow us to retrieve a {@link PrayerItem} constant by
         * its item id.
         */
        private static Map<Integer, PrayerItem> prayerItem = new HashMap<Integer, PrayerItem>();

        /**
         * Create a new {@link PrayerItem}.
         * 
         * @param itemId
         *        the id of the item we are using.
         * @param experience
         *        the experience gained from using this item.
         */
        PrayerItem(int itemId, int experience) {
            this.itemId = itemId;
            this.experience = experience;
        }

        /** Fill our map with the correct data. */
        static {
            for (PrayerItem item : PrayerItem.values()) {
                prayerItem.put(item.getItemId(), item);
            }
        }

        /**
         * Gets the id of the item we are using.
         * 
         * @return the id of the item.
         */
        public int getItemId() {
            return itemId;
        }

        /**
         * Gets the experience gained from using this item.
         * 
         * @return the experience.
         */
        public int getExperience() {
            return experience;
        }

        /**
         * Gets a {@link PrayerItem} constant by its item id.
         * 
         * @param itemId
         *        the itemId to get the constant of.
         * @return the constant.
         */
        public static PrayerItem getPrayerItem(int itemId) {
            return prayerItem.get(itemId);
        }
    }

    /**
     * Buries a prayer item for the specified player.
     * 
     * @param player
     *        the player burying the item.
     * @param item
     *        the item being buried.
     * @param slot
     *        the inventory slot the item is in.
     */
    public void buryItem(Player player, PrayerItem item, int slot) {

        /** Block if this is an invalid item. */
        if (item == null) {
            return;
        }

        /** Check if the delay interval has elapsed. */
        if (player.getBuryTimer().elapsed() > BURY_DELAY) {

            /** Check if we have the item in our inventory. */
            if (player.getInventory().getContainer().contains(item.getItemId())) {

                /** Bury the item and grant experience. */
                player.getMovementQueue().reset();
                player.animation(BURY);
                player.getPacketBuilder().sendMessage("You bury the " + item.name().toLowerCase().replaceAll("_", " ") + ".");
                exp(player, item.getExperience());
                player.getInventory().deleteItemSlot(new Item(item.getItemId()), slot);

                /** Reset the delay interval. */
                player.getBuryTimer().reset();
            }
        }
    }

    /**
     * Uses a prayer item on an altar for the specified player.
     * 
     * @param player
     *        the player using the item on the altar.
     * @param item
     *        the item being used.
     * @param slot
     *        the inventory slot the item is in.
     */
    public void altarItem(Player player, PrayerItem item, int slot) {

        /** Block if this is an invalid item. */
        if (item == null) {
            return;
        }

        /** Check if the delay interval has elapsed. */
        if (player.getAltarTimer().elapsed() > ALTAR_DELAY) {

            /** Check if we have the item in our inventory. */
            if (player.getInventory().getContainer().contains(item.getItemId())) {

                /** Use the item on the altar and grant experience. */
                player.getMovementQueue().reset();
                player.animation(ALTAR);
                player.gfx(new Gfx(247));
                player.getPacketBuilder().sendMessage("You use the " + item.name().toLowerCase().replaceAll("_", " ") + " on the altar.");
                exp(player, (item.getExperience() * 2));
                player.getInventory().deleteItemSlot(new Item(item.getItemId()), slot);

                /** Reset the delay interval. */
                player.getAltarTimer().reset();
            }
        }
    }

    /**
     * Gets the {@link Prayer} singleton instance.
     * 
     * @return the singleton instance.
     */
    public static Prayer getSingleton() {
        if (singleton == null) {
            singleton = new Prayer();
        }

        return singleton;
    }

    @Override
    public void fireResetEvent(Player player) {

    }

    @Override
    public int eventFireIndex() {
        return SkillEvent.PRAYER;
    }

    @Override
    public SkillConstant skillConstant() {
        return SkillConstant.PRAYER;
    }
}
