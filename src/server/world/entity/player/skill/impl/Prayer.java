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

    // TODO: revamp and do further testing.

    /**
     * The singleton instance.
     */
    private static Prayer singleton;

    /**
     * The delay between burying bones.
     */
    private static final int BURY_DELAY = 1200;

    /**
     * The delay between using bones on an altar.
     */
    private static final int ALTAR_DELAY = 3500;

    /**
     * The animation sent when burying a bone.
     */
    private static final Animation BURY_BONE = new Animation(827);

    /**
     * The animation sent when using a bone on an altar.
     */
    private static final Animation BONE_ON_ALTAR = new Animation(896);

    /**
     * All possible bones that can be buried or used on an altar.
     * 
     * @author lare96
     */
    public enum Bone {
        BONES(526, 4),
        BAT_BONES(530, 5),
        MONKEY_BONES(3179, 5),
        WOLF_BONES(2859, 4),
        BIG_BONES(532, 15),
        BABYDRAGON_BONES(534, 30),
        DRAGON_BONES(536, 72),
        JOGRE_BONES(3125, 15),
        ZOGRE_BONES(4812, 22),
        FAYRG_BONES(4830, 84),
        RAURG_BONES(4832, 96),
        OURG_BONES(4834, 140),
        BURNT_BONES(528, 4),
        SHAIKAHAN_BONES(3123, 25);

        /**
         * The id of the bone.
         */
        private int boneId;

        /**
         * The experience you get for burying this bone.
         */
        private int experience;

        /**
         * Construct new data for a bone.
         * 
         * @param boneId
         *        the bone id.
         * @param experience
         *        the experience from burying this bone or using it on an altar.
         */
        Bone(int boneId, int experience) {
            this.setBoneId(boneId);
            this.setExperience(experience);
        }

        /**
         * The map that allows us to retrieve a constant by its id.
         */
        private static Map<Integer, Bone> bone = new HashMap<Integer, Bone>();

        /**
         * Begins loading the data for this enum.
         */
        static {
            for (Bone b : Bone.values()) {
                bone.put(b.getBoneId(), b);
            }
        }

        /**
         * Gets the instance by its id.
         * 
         * @param id
         *        the id to get the instance of.
         * @return the instance.
         */
        public static Bone forId(int id) {
            return bone.get(id);
        }

        /**
         * @return the boneId.
         */
        public int getBoneId() {
            return boneId;
        }

        /**
         * @param boneId
         *        the boneId to set.
         */
        public void setBoneId(int boneId) {
            this.boneId = boneId;
        }

        /**
         * @return the experience.
         */
        public int getExperience() {
            return experience;
        }

        /**
         * @param experience
         *        the experience to set.
         */
        public void setExperience(int experience) {
            this.experience = experience;
        }
    }

    /**
     * A method that determines what happens when a player buries a bone.
     * 
     * @param player
     *        the player burying the bone.
     * @param bone
     *        the bone being buried.
     * @param slot
     *        the inventory slot the bone is in.
     */
    public void bury(Player player, Bone bone, int slot) {
        if (bone == null) {
            return;
        }

        if (player.getBuryTimer().elapsed() > BURY_DELAY) {
            /** Check if we have the bone in our inventory. */
            if (player.getInventory().getContainer().contains(bone.getBoneId())) {

                /** Bury the bone. */
                player.getMovementQueue().reset();
                player.getSkillEvent()[eventFireIndex()] = true;
                player.animation(BURY_BONE);
                player.getPacketBuilder().sendMessage("You bury the " + bone.name().toLowerCase().replaceAll("_", " ") + ".");
                exp(player, bone.getExperience());
                player.getInventory().deleteItemSlot(new Item(bone.getBoneId()), slot);

                /** Reset skill. */
                player.getBuryTimer().reset();
                fireResetEvent(player);
            }
        }
    }

    /**
     * A method that determines what happens when a player uses a bone on an
     * altar.
     * 
     * @param player
     *        the player using the bone on the altar.
     * @param bone
     *        the bone being used.
     * @param slot
     *        the inventory slot the bone is in.
     */
    public void altar(Player player, Bone bone, int slot) {
        if (bone == null) {
            return;
        }

        if (player.getAltarTimer().elapsed() > ALTAR_DELAY) {

            /** Check if we have the bone in our inventory. */
            if (player.getInventory().getContainer().contains(bone.getBoneId())) {

                /** Use the bone on the altar. */
                player.getMovementQueue().reset();
                player.getSkillEvent()[eventFireIndex()] = true;
                player.animation(BONE_ON_ALTAR);
                player.gfx(new Gfx(247));
                player.getPacketBuilder().sendMessage("You use the " + bone.name().toLowerCase().replaceAll("_", " ") + " on the altar.");
                exp(player, (bone.getExperience() * 2));
                player.getInventory().deleteItemSlot(new Item(bone.getBoneId()), slot);

                /** Reset skill. */
                player.getAltarTimer().reset();
                fireResetEvent(player);
            }
        }
    }

    /**
     * @return the singleton.
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
