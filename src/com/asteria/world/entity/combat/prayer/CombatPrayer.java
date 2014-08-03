package com.asteria.world.entity.combat.prayer;

import com.asteria.engine.task.TaskManager;
import com.asteria.util.Utility;
import com.asteria.world.entity.UpdateFlags.Flag;
import com.asteria.world.entity.combat.CombatFactory.CombatType;
import com.asteria.world.entity.player.Player;
import com.asteria.world.entity.player.skill.Skills;

/**
 * All of the prayers that can be activated and deactivated. This currently only
 * has support for prayers present in the <b>317 protocol</b>.
 * 
 * @author lare96
 */
public enum CombatPrayer {

    THICK_SKIN(20, -1, 1, 83) {
        @Override
        protected void onActivation(Player player) {
            CombatPrayer.ROCK_SKIN.deactivate(player, false);
            CombatPrayer.STEEL_SKIN.deactivate(player, false);
            player.getPrayerActive()[CombatPrayer.THICK_SKIN.ordinal()] = true;
            player.getPacketBuilder().sendConfig(
                    CombatPrayer.THICK_SKIN.getPrayerGlow(), 1);
        }

        @Override
        protected void onDeactivation(Player player) {
            player.getPrayerActive()[CombatPrayer.THICK_SKIN.ordinal()] = false;
            player.getPacketBuilder().sendConfig(
                    CombatPrayer.THICK_SKIN.getPrayerGlow(), 0);
        }
    },

    BURST_OF_STRENGTH(20, -1, 4, 84) {
        @Override
        protected void onActivation(Player player) {
            CombatPrayer.SUPERHUMAN_STRENGTH.deactivate(player, false);
            CombatPrayer.ULTIMATE_STRENGTH.deactivate(player, false);
            player.getPrayerActive()[CombatPrayer.BURST_OF_STRENGTH.ordinal()] = true;
            player.getPacketBuilder().sendConfig(
                    CombatPrayer.BURST_OF_STRENGTH.getPrayerGlow(), 1);
        }

        @Override
        protected void onDeactivation(Player player) {
            player.getPrayerActive()[CombatPrayer.BURST_OF_STRENGTH.ordinal()] = false;
            player.getPacketBuilder().sendConfig(
                    CombatPrayer.BURST_OF_STRENGTH.getPrayerGlow(), 0);
        }
    },

    CLARITY_OF_THOUGHT(20, -1, 7, 85) {
        @Override
        protected void onActivation(Player player) {
            CombatPrayer.IMPROVED_REFLEXES.deactivate(player, false);
            CombatPrayer.INCREDIBLE_REFLEXES.deactivate(player, false);
            player.getPrayerActive()[CombatPrayer.CLARITY_OF_THOUGHT.ordinal()] = true;
            player.getPacketBuilder().sendConfig(
                    CombatPrayer.CLARITY_OF_THOUGHT.getPrayerGlow(), 1);
        }

        @Override
        protected void onDeactivation(Player player) {
            player.getPrayerActive()[CombatPrayer.CLARITY_OF_THOUGHT.ordinal()] = false;
            player.getPacketBuilder().sendConfig(
                    CombatPrayer.CLARITY_OF_THOUGHT.getPrayerGlow(), 0);
        }
    },

    ROCK_SKIN(10, -1, 10, 86) {
        @Override
        protected void onActivation(Player player) {
            CombatPrayer.STEEL_SKIN.deactivate(player, false);
            CombatPrayer.THICK_SKIN.deactivate(player, false);
            player.getPrayerActive()[CombatPrayer.ROCK_SKIN.ordinal()] = true;
            player.getPacketBuilder().sendConfig(
                    CombatPrayer.ROCK_SKIN.getPrayerGlow(), 1);
        }

        @Override
        protected void onDeactivation(Player player) {
            player.getPrayerActive()[CombatPrayer.ROCK_SKIN.ordinal()] = false;
            player.getPacketBuilder().sendConfig(
                    CombatPrayer.ROCK_SKIN.getPrayerGlow(), 0);
        }
    },

    SUPERHUMAN_STRENGTH(10, -1, 13, 87) {
        @Override
        protected void onActivation(Player player) {
            CombatPrayer.BURST_OF_STRENGTH.deactivate(player, false);
            CombatPrayer.ULTIMATE_STRENGTH.deactivate(player, false);
            player.getPrayerActive()[CombatPrayer.SUPERHUMAN_STRENGTH.ordinal()] = true;
            player.getPacketBuilder().sendConfig(
                    CombatPrayer.SUPERHUMAN_STRENGTH.getPrayerGlow(), 1);
        }

        @Override
        protected void onDeactivation(Player player) {
            player.getPrayerActive()[CombatPrayer.SUPERHUMAN_STRENGTH.ordinal()] = false;
            player.getPacketBuilder().sendConfig(
                    CombatPrayer.SUPERHUMAN_STRENGTH.getPrayerGlow(), 0);
        }
    },

    IMPROVED_REFLEXES(10, -1, 16, 88) {
        @Override
        protected void onActivation(Player player) {
            CombatPrayer.CLARITY_OF_THOUGHT.deactivate(player, false);
            CombatPrayer.INCREDIBLE_REFLEXES.deactivate(player, false);
            player.getPrayerActive()[CombatPrayer.IMPROVED_REFLEXES.ordinal()] = true;
            player.getPacketBuilder().sendConfig(
                    CombatPrayer.IMPROVED_REFLEXES.getPrayerGlow(), 1);
        }

        @Override
        protected void onDeactivation(Player player) {
            player.getPrayerActive()[CombatPrayer.IMPROVED_REFLEXES.ordinal()] = false;
            player.getPacketBuilder().sendConfig(
                    CombatPrayer.IMPROVED_REFLEXES.getPrayerGlow(), 0);
        }
    },

    RAPID_RESTORE(29, -1, 19, 89) {
        @Override
        protected void onActivation(Player player) {
            player.getPrayerActive()[CombatPrayer.RAPID_RESTORE.ordinal()] = true;
            player.getPacketBuilder().sendConfig(
                    CombatPrayer.RAPID_RESTORE.getPrayerGlow(), 1);
        }

        @Override
        protected void onDeactivation(Player player) {
            player.getPrayerActive()[CombatPrayer.RAPID_RESTORE.ordinal()] = false;
            player.getPacketBuilder().sendConfig(
                    CombatPrayer.RAPID_RESTORE.getPrayerGlow(), 0);
        }
    },

    RAPID_HEAL(29, -1, 22, 90) {
        @Override
        protected void onActivation(Player player) {
            player.getPrayerActive()[CombatPrayer.RAPID_HEAL.ordinal()] = true;
            player.getPacketBuilder().sendConfig(
                    CombatPrayer.RAPID_HEAL.getPrayerGlow(), 1);
        }

        @Override
        protected void onDeactivation(Player player) {
            player.getPrayerActive()[CombatPrayer.RAPID_HEAL.ordinal()] = false;
            player.getPacketBuilder().sendConfig(
                    CombatPrayer.RAPID_HEAL.getPrayerGlow(), 0);
        }
    },

    PROTECT_ITEM(29, -1, 25, 91) {
        @Override
        protected void onActivation(Player player) {
            player.getPrayerActive()[CombatPrayer.PROTECT_ITEM.ordinal()] = true;
            player.getPacketBuilder().sendConfig(
                    CombatPrayer.PROTECT_ITEM.getPrayerGlow(), 1);
        }

        @Override
        protected void onDeactivation(Player player) {
            player.getPrayerActive()[CombatPrayer.PROTECT_ITEM.ordinal()] = false;
            player.getPacketBuilder().sendConfig(
                    CombatPrayer.PROTECT_ITEM.getPrayerGlow(), 0);
        }
    },

    STEEL_SKIN(5, -1, 28, 92) {
        @Override
        protected void onActivation(Player player) {
            CombatPrayer.THICK_SKIN.deactivate(player, false);
            CombatPrayer.ROCK_SKIN.deactivate(player, false);
            player.getPrayerActive()[CombatPrayer.STEEL_SKIN.ordinal()] = true;
            player.getPacketBuilder().sendConfig(
                    CombatPrayer.STEEL_SKIN.getPrayerGlow(), 1);
        }

        @Override
        protected void onDeactivation(Player player) {
            player.getPrayerActive()[CombatPrayer.STEEL_SKIN.ordinal()] = false;
            player.getPacketBuilder().sendConfig(
                    CombatPrayer.STEEL_SKIN.getPrayerGlow(), 0);
        }
    },

    ULTIMATE_STRENGTH(5, -1, 31, 93) {
        @Override
        protected void onActivation(Player player) {
            CombatPrayer.BURST_OF_STRENGTH.deactivate(player, false);
            CombatPrayer.SUPERHUMAN_STRENGTH.deactivate(player, false);
            player.getPrayerActive()[CombatPrayer.ULTIMATE_STRENGTH.ordinal()] = true;
            player.getPacketBuilder().sendConfig(
                    CombatPrayer.ULTIMATE_STRENGTH.getPrayerGlow(), 1);
        }

        @Override
        protected void onDeactivation(Player player) {
            player.getPrayerActive()[CombatPrayer.ULTIMATE_STRENGTH.ordinal()] = false;
            player.getPacketBuilder().sendConfig(
                    CombatPrayer.ULTIMATE_STRENGTH.getPrayerGlow(), 0);
        }
    },

    INCREDIBLE_REFLEXES(5, -1, 34, 94) {
        @Override
        protected void onActivation(Player player) {
            CombatPrayer.IMPROVED_REFLEXES.deactivate(player, false);
            CombatPrayer.CLARITY_OF_THOUGHT.deactivate(player, false);
            player.getPrayerActive()[CombatPrayer.INCREDIBLE_REFLEXES.ordinal()] = true;
            player.getPacketBuilder().sendConfig(
                    CombatPrayer.INCREDIBLE_REFLEXES.getPrayerGlow(), 1);
        }

        @Override
        protected void onDeactivation(Player player) {
            player.getPrayerActive()[CombatPrayer.INCREDIBLE_REFLEXES.ordinal()] = false;
            player.getPacketBuilder().sendConfig(
                    CombatPrayer.INCREDIBLE_REFLEXES.getPrayerGlow(), 0);
        }
    },

    PROTECT_FROM_MAGIC(5, 2, 37, 95) {
        @Override
        protected void onActivation(Player player) {
            CombatPrayer.PROTECT_FROM_MISSILES.deactivate(player, false);
            CombatPrayer.PROTECT_FROM_MELEE.deactivate(player, false);
            CombatPrayer.REDEMPTION.deactivate(player, false);
            CombatPrayer.RETRIBUTION.deactivate(player, false);
            CombatPrayer.SMITE.deactivate(player, false);
            player.getPrayerActive()[CombatPrayer.PROTECT_FROM_MAGIC.ordinal()] = true;
            player.setHeadIcon(CombatPrayer.PROTECT_FROM_MAGIC.getHeadIcon());
            player.getPacketBuilder().sendConfig(
                    CombatPrayer.PROTECT_FROM_MAGIC.getPrayerGlow(), 1);
            player.getFlags().flag(Flag.APPEARANCE);
        }

        @Override
        protected void onDeactivation(Player player) {
            player.getPrayerActive()[CombatPrayer.PROTECT_FROM_MAGIC.ordinal()] = false;
            player.setHeadIcon(-1);
            player.getPacketBuilder().sendConfig(
                    CombatPrayer.PROTECT_FROM_MAGIC.getPrayerGlow(), 0);
            player.getFlags().flag(Flag.APPEARANCE);
        }
    },

    PROTECT_FROM_MISSILES(5, 1, 40, 96) {
        @Override
        protected void onActivation(Player player) {
            CombatPrayer.REDEMPTION.deactivate(player, false);
            CombatPrayer.PROTECT_FROM_MAGIC.deactivate(player, false);
            CombatPrayer.PROTECT_FROM_MELEE.deactivate(player, false);
            CombatPrayer.RETRIBUTION.deactivate(player, false);
            CombatPrayer.SMITE.deactivate(player, false);
            player.getPrayerActive()[CombatPrayer.PROTECT_FROM_MISSILES
                    .ordinal()] = true;
            player.setHeadIcon(CombatPrayer.PROTECT_FROM_MISSILES.getHeadIcon());
            player.getPacketBuilder().sendConfig(
                    CombatPrayer.PROTECT_FROM_MISSILES.getPrayerGlow(), 1);
            player.getFlags().flag(Flag.APPEARANCE);
        }

        @Override
        protected void onDeactivation(Player player) {
            player.getPrayerActive()[CombatPrayer.PROTECT_FROM_MISSILES
                    .ordinal()] = false;
            player.setHeadIcon(-1);
            player.getPacketBuilder().sendConfig(
                    CombatPrayer.PROTECT_FROM_MISSILES.getPrayerGlow(), 0);
            player.getFlags().flag(Flag.APPEARANCE);
        }
    },

    PROTECT_FROM_MELEE(5, 0, 43, 97) {
        @Override
        protected void onActivation(Player player) {
            CombatPrayer.PROTECT_FROM_MISSILES.deactivate(player, false);
            CombatPrayer.PROTECT_FROM_MAGIC.deactivate(player, false);
            CombatPrayer.REDEMPTION.deactivate(player, false);
            CombatPrayer.RETRIBUTION.deactivate(player, false);
            CombatPrayer.SMITE.deactivate(player, false);
            player.getPrayerActive()[CombatPrayer.PROTECT_FROM_MELEE.ordinal()] = true;
            player.setHeadIcon(CombatPrayer.PROTECT_FROM_MELEE.getHeadIcon());
            player.getPacketBuilder().sendConfig(
                    CombatPrayer.PROTECT_FROM_MELEE.getPrayerGlow(), 1);
            player.getFlags().flag(Flag.APPEARANCE);
        }

        @Override
        protected void onDeactivation(Player player) {
            player.getPrayerActive()[CombatPrayer.PROTECT_FROM_MELEE.ordinal()] = false;
            player.setHeadIcon(-1);
            player.getPacketBuilder().sendConfig(
                    CombatPrayer.PROTECT_FROM_MELEE.getPrayerGlow(), 0);
            player.getFlags().flag(Flag.APPEARANCE);
        }
    },

    RETRIBUTION(17, 3, 46, 98) {
        @Override
        protected void onActivation(Player player) {
            CombatPrayer.PROTECT_FROM_MISSILES.deactivate(player, false);
            CombatPrayer.PROTECT_FROM_MAGIC.deactivate(player, false);
            CombatPrayer.PROTECT_FROM_MELEE.deactivate(player, false);
            CombatPrayer.REDEMPTION.deactivate(player, false);
            CombatPrayer.SMITE.deactivate(player, false);
            player.getPrayerActive()[CombatPrayer.RETRIBUTION.ordinal()] = true;
            player.setHeadIcon(CombatPrayer.RETRIBUTION.getHeadIcon());
            player.getPacketBuilder().sendConfig(
                    CombatPrayer.RETRIBUTION.getPrayerGlow(), 1);
            player.getFlags().flag(Flag.APPEARANCE);
        }

        @Override
        protected void onDeactivation(Player player) {
            player.getPrayerActive()[CombatPrayer.RETRIBUTION.ordinal()] = false;
            player.setHeadIcon(-1);
            player.getPacketBuilder().sendConfig(
                    CombatPrayer.RETRIBUTION.getPrayerGlow(), 0);
            player.getFlags().flag(Flag.APPEARANCE);
        }
    },

    REDEMPTION(6, 5, 49, 99) {
        @Override
        protected void onActivation(Player player) {
            CombatPrayer.PROTECT_FROM_MISSILES.deactivate(player, false);
            CombatPrayer.PROTECT_FROM_MAGIC.deactivate(player, false);
            CombatPrayer.PROTECT_FROM_MELEE.deactivate(player, false);
            CombatPrayer.RETRIBUTION.deactivate(player, false);
            CombatPrayer.SMITE.deactivate(player, false);
            player.getPrayerActive()[CombatPrayer.REDEMPTION.ordinal()] = true;
            player.setHeadIcon(CombatPrayer.REDEMPTION.getHeadIcon());
            player.getPacketBuilder().sendConfig(
                    CombatPrayer.REDEMPTION.getPrayerGlow(), 1);
            player.getFlags().flag(Flag.APPEARANCE);
        }

        @Override
        protected void onDeactivation(Player player) {
            player.getPrayerActive()[CombatPrayer.REDEMPTION.ordinal()] = false;
            player.setHeadIcon(-1);
            player.getPacketBuilder().sendConfig(
                    CombatPrayer.REDEMPTION.getPrayerGlow(), 0);
            player.getFlags().flag(Flag.APPEARANCE);
        }
    },

    SMITE(7, 4, 52, 100) {
        @Override
        protected void onActivation(Player player) {
            CombatPrayer.PROTECT_FROM_MISSILES.deactivate(player, false);
            CombatPrayer.PROTECT_FROM_MAGIC.deactivate(player, false);
            CombatPrayer.PROTECT_FROM_MELEE.deactivate(player, false);
            CombatPrayer.RETRIBUTION.deactivate(player, false);
            CombatPrayer.REDEMPTION.deactivate(player, false);
            player.getPrayerActive()[CombatPrayer.SMITE.ordinal()] = true;
            player.setHeadIcon(CombatPrayer.SMITE.getHeadIcon());
            player.getPacketBuilder().sendConfig(
                    CombatPrayer.SMITE.getPrayerGlow(), 1);
            player.getFlags().flag(Flag.APPEARANCE);
        }

        @Override
        protected void onDeactivation(Player player) {
            player.getPrayerActive()[CombatPrayer.SMITE.ordinal()] = false;
            player.setHeadIcon(-1);
            player.getPacketBuilder().sendConfig(
                    CombatPrayer.SMITE.getPrayerGlow(), 0);
            player.getFlags().flag(Flag.APPEARANCE);
        }
    };

    /** The amount of ticks it takes for one prayer point to be drained. */
    private int drainRate;

    /** The head icon of this prayer. */
    private int headIcon;

    /** The level required to use this prayer. */
    private int levelRequired;

    /** The prayer glow config. */
    private int prayerGlow;

    /**
     * Create a new {@link CombatPrayer}.
     * 
     * @param drainRate
     *            the amount of ticks it takes for one prayer point to be
     *            drained.
     * @param headIcon
     *            the head icon id.
     * @param levelRequired
     *            the level required to active this prayer.
     * @param prayerGlow
     *            the glow id.
     * @param activate
     *            the action on activation.
     * @param deactivate
     *            the action on deactivation.
     */
    private CombatPrayer(int drainRate, int headIcon, int levelRequired,
            int prayerGlow) {
        this.drainRate = drainRate;
        this.headIcon = headIcon;
        this.levelRequired = levelRequired;
        this.prayerGlow = prayerGlow;
    }

    /**
     * Fired when this prayer is activated through the
     * <code>activatePrayer(Player p, boolean b)</code> method.
     * 
     * @param player
     *            the player this prayer is being turned on for.
     */
    protected abstract void onActivation(Player player);

    /**
     * Fired when this prayer is deactivated through the
     * <code>deactivatePrayer(Player p, boolean b)</code> method.
     * 
     * @param player
     *            the player this prayer is being turned on for.
     */
    protected abstract void onDeactivation(Player player);

    /**
     * Activates this prayer if the {@link Player} has the required level to do
     * so. If the prayer has already been activated and
     * <code>deactivateIfNeeded</code> is flagged then the argued prayer will be
     * deactivated.
     * 
     * @param player
     *            the player to activate this prayer for.
     * @param deactivateIfNeeded
     *            if the prayer should be deactivated if it is activated.
     */
    public void activate(Player player, boolean deactivateIfNeeded) {

        // Check if this prayer is activated.
        if (CombatPrayer.isActivated(player, this)) {

            // It is, check if we need to deactivate and do so if needed.
            if (deactivateIfNeeded) {
                deactivate(player, false);
            }
            return;
        }

        // Check the required prayer level.
        if (player.getSkills()[Skills.PRAYER].getLevelForExperience() < levelRequired) {
            player.getPacketBuilder().sendEmptyChatbox(
                    "You need a @blu@Prayer level of "
                            + levelRequired
                            + " @bla@to use @blu@"
                            + Utility.capitalize(name().toLowerCase()
                                    .replaceAll("_", " ")) + "@bla@.");
            player.getPacketBuilder().sendConfig(prayerGlow, 0);
            return;
        }

        // Check your prayer point level.
        if (player.getSkills()[Skills.PRAYER].getLevel() < 1) {
            player.getPacketBuilder().sendMessage(
                    "You've run out of prayer points!");
            player.getPacketBuilder().sendConfig(prayerGlow, 0);
            return;
        }

        // Start a task if needed to drain prayer.
        if (player.getPrayerDrain() == null
                || !player.getPrayerDrain().isRunning()) {
            player.setPrayerDrain(new CombatPrayerTask(player));
            TaskManager.submit(player.getPrayerDrain());
        }

        // Activate the effects of this prayer.
        onActivation(player);
    }

    /**
     * Deactivates this prayer if the {@link Player} has this prayer activated.
     * 
     * @param player
     *            the player to deactivate this prayer for.
     * @param activateIfNeeded
     *            if the prayer should be activated if it is deactivated.
     */
    public void deactivate(Player player, boolean activateIfNeeded) {

        // Check if this prayer is deactivated.
        if (!CombatPrayer.isActivated(player, this)) {

            // It is, check if we need to activate and do so if needed.
            if (activateIfNeeded) {
                activate(player, false);
            }
            return;
        }

        // Deactivate the effects of this prayer.
        onDeactivation(player);
    }

    /**
     * Deactivates all activated prayers for the specified {@link Player}.
     * 
     * @param player
     *            the player to deactivate all prayers for.
     */
    public static void deactivateAll(Player player) {
        for (CombatPrayer combatPrayer : CombatPrayer.values()) {
            if (CombatPrayer.isActivated(player, combatPrayer)) {
                combatPrayer.deactivate(player, false);
            }
        }
    }

    /**
     * Resets all of the prayer glows to their default states for the specified
     * {@link Player}.
     * 
     * @param player
     *            the player to reset the prayer glows for.
     */
    public static void resetAllGlows(Player player) {
        for (CombatPrayer combatPrayer : CombatPrayer.values()) {
            player.getPacketBuilder().sendConfig(combatPrayer.getPrayerGlow(),
                    0);
        }
    }

    /**
     * Gets a combat prayer constant by its position in the enumeration.
     * 
     * @param prayerId
     *            the position of the constant.
     * @return the constant corresponding to the position.
     */
    public static CombatPrayer get(int prayerId) {
        return CombatPrayer.values()[prayerId];
    }

    /**
     * Determines if the argued prayer is activated for the argued player.
     * 
     * @param player
     *            the player to check.
     * @param prayer
     *            the prayer to check.
     * @return <code>true</code> if the prayer is activated, <code>false</code>
     *         if it is not.
     */
    public static boolean isActivated(Player player, CombatPrayer prayer) {
        return player.getPrayerActive()[prayer.ordinal()];
    }

    /**
     * Gets the protecting prayer based on the argued combat type.
     * 
     * @param type
     *            the combat type.
     * @return the protecting prayer.
     */
    public static CombatPrayer getProtectingPrayer(CombatType type) {
        switch (type) {
        case MELEE:
            return CombatPrayer.PROTECT_FROM_MELEE;
        case MAGIC:
            return CombatPrayer.PROTECT_FROM_MAGIC;
        case RANGED:
            return CombatPrayer.PROTECT_FROM_MISSILES;
        default:
            throw new IllegalArgumentException("Invalid combat type: " + type);
        }
    }

    /**
     * Gets the amount of ticks it takes for one prayer point to be drained.
     * 
     * @return the amount of ticks it takes for one prayer point to be drained.
     */
    public int getDrainRate() {
        return drainRate;
    }

    /**
     * Gets the head icon of this prayer.
     * 
     * @return the head icon of this prayer.
     */
    public int getHeadIcon() {
        return headIcon;
    }

    /**
     * Gets the level required to use this prayer.
     * 
     * @return the level required to use this prayer.
     */
    public int getLevelRequired() {
        return levelRequired;
    }

    /**
     * Gets the prayer glow config.
     * 
     * @return the prayer glow config.
     */
    public int getPrayerGlow() {
        return prayerGlow;
    }
}
