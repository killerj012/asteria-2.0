package server.world.entity.combat.prayer;

import server.core.worker.TaskFactory;
import server.util.Misc;
import server.util.Misc.GenericAction;
import server.world.entity.UpdateFlags.Flag;
import server.world.entity.player.Player;

/**
 * Holds data and miscellaneous functions for combat prayers.
 * 
 * @author lare96
 */
public enum CombatPrayer {

    THICK_SKIN(6, -1, 1, 83, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            CombatPrayer.ROCK_SKIN.deactivatePrayer(player, false);
            CombatPrayer.STEEL_SKIN.deactivatePrayer(player, false);
            player.getPrayerActive()[CombatPrayer.THICK_SKIN.ordinal()] = true;
            player.getPacketBuilder().sendConfig(CombatPrayer.THICK_SKIN.getPrayerGlow(), 1);
        }
    }, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            player.getPrayerActive()[CombatPrayer.THICK_SKIN.ordinal()] = false;
            player.getPacketBuilder().sendConfig(CombatPrayer.THICK_SKIN.getPrayerGlow(), 0);
        }
    }),

    BURST_OF_STRENGTH(6, -1, 4, 84, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            CombatPrayer.SUPERHUMAN_STRENGTH.deactivatePrayer(player, false);
            CombatPrayer.ULTIMATE_STRENGTH.deactivatePrayer(player, false);
            player.getPrayerActive()[CombatPrayer.BURST_OF_STRENGTH.ordinal()] = true;
            player.getPacketBuilder().sendConfig(CombatPrayer.BURST_OF_STRENGTH.getPrayerGlow(), 1);
        }
    }, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            player.getPrayerActive()[CombatPrayer.BURST_OF_STRENGTH.ordinal()] = false;
            player.getPacketBuilder().sendConfig(CombatPrayer.BURST_OF_STRENGTH.getPrayerGlow(), 0);
        }
    }),

    CLARITY_OF_THOUGHT(6, -1, 7, 85, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            CombatPrayer.IMPROVED_REFLEXES.deactivatePrayer(player, false);
            CombatPrayer.INCREDIBLE_REFLEXES.deactivatePrayer(player, false);
            player.getPrayerActive()[CombatPrayer.CLARITY_OF_THOUGHT.ordinal()] = true;
            player.getPacketBuilder().sendConfig(CombatPrayer.CLARITY_OF_THOUGHT.getPrayerGlow(), 1);
        }
    }, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            player.getPrayerActive()[CombatPrayer.CLARITY_OF_THOUGHT.ordinal()] = false;
            player.getPacketBuilder().sendConfig(CombatPrayer.CLARITY_OF_THOUGHT.getPrayerGlow(), 0);
        }
    }),

    ROCK_SKIN(6, -1, 10, 86, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            CombatPrayer.STEEL_SKIN.deactivatePrayer(player, false);
            CombatPrayer.THICK_SKIN.deactivatePrayer(player, false);
            player.getPrayerActive()[CombatPrayer.ROCK_SKIN.ordinal()] = true;
            player.getPacketBuilder().sendConfig(CombatPrayer.ROCK_SKIN.getPrayerGlow(), 1);
        }
    }, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            player.getPrayerActive()[CombatPrayer.ROCK_SKIN.ordinal()] = false;
            player.getPacketBuilder().sendConfig(CombatPrayer.ROCK_SKIN.getPrayerGlow(), 0);
        }
    }),

    SUPERHUMAN_STRENGTH(6, -1, 13, 87, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            CombatPrayer.BURST_OF_STRENGTH.deactivatePrayer(player, false);
            CombatPrayer.ULTIMATE_STRENGTH.deactivatePrayer(player, false);
            player.getPrayerActive()[CombatPrayer.SUPERHUMAN_STRENGTH.ordinal()] = true;
            player.getPacketBuilder().sendConfig(CombatPrayer.SUPERHUMAN_STRENGTH.getPrayerGlow(), 1);
        }
    }, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            player.getPrayerActive()[CombatPrayer.SUPERHUMAN_STRENGTH.ordinal()] = false;
            player.getPacketBuilder().sendConfig(CombatPrayer.SUPERHUMAN_STRENGTH.getPrayerGlow(), 0);
        }
    }),

    IMPROVED_REFLEXES(6, -1, 16, 88, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            CombatPrayer.CLARITY_OF_THOUGHT.deactivatePrayer(player, false);
            CombatPrayer.INCREDIBLE_REFLEXES.deactivatePrayer(player, false);
            player.getPrayerActive()[CombatPrayer.IMPROVED_REFLEXES.ordinal()] = true;
            player.getPacketBuilder().sendConfig(CombatPrayer.IMPROVED_REFLEXES.getPrayerGlow(), 1);
        }
    }, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            player.getPrayerActive()[CombatPrayer.IMPROVED_REFLEXES.ordinal()] = false;
            player.getPacketBuilder().sendConfig(CombatPrayer.IMPROVED_REFLEXES.getPrayerGlow(), 0);
        }
    }),

    RAPID_RESTORE(5, -1, 19, 89, new GenericAction<Player>() {
        @Override
        // TODO: get working
        public void fireAction(Player player) {
            player.getPrayerActive()[CombatPrayer.RAPID_RESTORE.ordinal()] = true;
            player.getPacketBuilder().sendConfig(CombatPrayer.RAPID_RESTORE.getPrayerGlow(), 1);
        }
    }, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            player.getPrayerActive()[CombatPrayer.RAPID_RESTORE.ordinal()] = false;
            player.getPacketBuilder().sendConfig(CombatPrayer.RAPID_RESTORE.getPrayerGlow(), 0);
        }
    }),

    RAPID_HEAL(5, -1, 22, 90, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            player.getPrayerActive()[CombatPrayer.RAPID_HEAL.ordinal()] = true;
            player.getPacketBuilder().sendConfig(CombatPrayer.RAPID_HEAL.getPrayerGlow(), 1);
        }
    }, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            player.getPrayerActive()[CombatPrayer.RAPID_HEAL.ordinal()] = false;
            player.getPacketBuilder().sendConfig(CombatPrayer.RAPID_HEAL.getPrayerGlow(), 0);
        }
    }),

    PROTECT_ITEM(5, -1, 25, 91, new GenericAction<Player>() {
        @Override
        // TODO: get working
        public void fireAction(Player player) {
            player.getPrayerActive()[CombatPrayer.PROTECT_ITEM.ordinal()] = true;
            player.getPacketBuilder().sendConfig(CombatPrayer.PROTECT_ITEM.getPrayerGlow(), 1);
        }
    }, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            player.getPrayerActive()[CombatPrayer.PROTECT_ITEM.ordinal()] = false;
            player.getPacketBuilder().sendConfig(CombatPrayer.PROTECT_ITEM.getPrayerGlow(), 0);
        }
    }),

    STEEL_SKIN(4, -1, 28, 92, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            CombatPrayer.THICK_SKIN.deactivatePrayer(player, false);
            CombatPrayer.ROCK_SKIN.deactivatePrayer(player, false);
            player.getPrayerActive()[CombatPrayer.STEEL_SKIN.ordinal()] = true;
            player.getPacketBuilder().sendConfig(CombatPrayer.STEEL_SKIN.getPrayerGlow(), 1);
        }
    }, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            player.getPrayerActive()[CombatPrayer.STEEL_SKIN.ordinal()] = false;
            player.getPacketBuilder().sendConfig(CombatPrayer.STEEL_SKIN.getPrayerGlow(), 0);
        }
    }),

    ULTIMATE_STRENGTH(4, -1, 31, 93, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            CombatPrayer.BURST_OF_STRENGTH.deactivatePrayer(player, false);
            CombatPrayer.SUPERHUMAN_STRENGTH.deactivatePrayer(player, false);
            player.getPrayerActive()[CombatPrayer.ULTIMATE_STRENGTH.ordinal()] = true;
            player.getPacketBuilder().sendConfig(CombatPrayer.ULTIMATE_STRENGTH.getPrayerGlow(), 1);
        }
    }, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            player.getPrayerActive()[CombatPrayer.ULTIMATE_STRENGTH.ordinal()] = false;
            player.getPacketBuilder().sendConfig(CombatPrayer.ULTIMATE_STRENGTH.getPrayerGlow(), 0);
        }
    }),

    INCREDIBLE_REFLEXES(4, -1, 34, 94, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            CombatPrayer.IMPROVED_REFLEXES.deactivatePrayer(player, false);
            CombatPrayer.CLARITY_OF_THOUGHT.deactivatePrayer(player, false);
            player.getPrayerActive()[CombatPrayer.INCREDIBLE_REFLEXES.ordinal()] = true;
            player.getPacketBuilder().sendConfig(CombatPrayer.INCREDIBLE_REFLEXES.getPrayerGlow(), 1);
        }
    }, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            player.getPrayerActive()[CombatPrayer.INCREDIBLE_REFLEXES.ordinal()] = false;
            player.getPacketBuilder().sendConfig(CombatPrayer.INCREDIBLE_REFLEXES.getPrayerGlow(), 0);
        }
    }),

    PROTECT_FROM_MAGIC(3, 2, 37, 95, new GenericAction<Player>() {
        @Override
        // TODO: get working
        public void fireAction(Player player) {
            CombatPrayer.PROTECT_FROM_MISSILES.deactivatePrayer(player, false);
            CombatPrayer.PROTECT_FROM_MELEE.deactivatePrayer(player, false);
            CombatPrayer.REDEMPTION.deactivatePrayer(player, false);
            CombatPrayer.RETRIBUTION.deactivatePrayer(player, false);
            CombatPrayer.SMITE.deactivatePrayer(player, false);
            player.getPrayerActive()[CombatPrayer.PROTECT_FROM_MAGIC.ordinal()] = true;
            player.setHeadIcon(CombatPrayer.PROTECT_FROM_MAGIC.getHeadIcon());
            player.getPacketBuilder().sendConfig(CombatPrayer.PROTECT_FROM_MAGIC.getPrayerGlow(), 1);
            player.getFlags().flag(Flag.APPEARANCE);
        }
    }, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            player.getPrayerActive()[CombatPrayer.PROTECT_FROM_MAGIC.ordinal()] = false;
            player.setHeadIcon(-1);
            player.getPacketBuilder().sendConfig(CombatPrayer.PROTECT_FROM_MAGIC.getPrayerGlow(), 0);
            player.getFlags().flag(Flag.APPEARANCE);
        }
    }),

    PROTECT_FROM_MISSILES(3, 1, 40, 96, new GenericAction<Player>() {
        @Override
        // TODO: get working
        public void fireAction(Player player) {
            CombatPrayer.REDEMPTION.deactivatePrayer(player, false);
            CombatPrayer.PROTECT_FROM_MAGIC.deactivatePrayer(player, false);
            CombatPrayer.PROTECT_FROM_MELEE.deactivatePrayer(player, false);
            CombatPrayer.RETRIBUTION.deactivatePrayer(player, false);
            CombatPrayer.SMITE.deactivatePrayer(player, false);
            player.getPrayerActive()[CombatPrayer.PROTECT_FROM_MISSILES.ordinal()] = true;
            player.setHeadIcon(CombatPrayer.PROTECT_FROM_MISSILES.getHeadIcon());
            player.getPacketBuilder().sendConfig(CombatPrayer.PROTECT_FROM_MISSILES.getPrayerGlow(), 1);
            player.getFlags().flag(Flag.APPEARANCE);
        }
    }, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            player.getPrayerActive()[CombatPrayer.PROTECT_FROM_MISSILES.ordinal()] = false;
            player.setHeadIcon(-1);
            player.getPacketBuilder().sendConfig(CombatPrayer.PROTECT_FROM_MISSILES.getPrayerGlow(), 0);
            player.getFlags().flag(Flag.APPEARANCE);
        }
    }),

    PROTECT_FROM_MELEE(3, 0, 43, 97, new GenericAction<Player>() {
        @Override
        // TODO: get working
        public void fireAction(Player player) {
            CombatPrayer.PROTECT_FROM_MISSILES.deactivatePrayer(player, false);
            CombatPrayer.PROTECT_FROM_MAGIC.deactivatePrayer(player, false);
            CombatPrayer.REDEMPTION.deactivatePrayer(player, false);
            CombatPrayer.RETRIBUTION.deactivatePrayer(player, false);
            CombatPrayer.SMITE.deactivatePrayer(player, false);
            player.getPrayerActive()[CombatPrayer.PROTECT_FROM_MELEE.ordinal()] = true;
            player.setHeadIcon(CombatPrayer.PROTECT_FROM_MELEE.getHeadIcon());
            player.getPacketBuilder().sendConfig(CombatPrayer.PROTECT_FROM_MELEE.getPrayerGlow(), 1);
            player.getFlags().flag(Flag.APPEARANCE);
        }
    }, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            player.getPrayerActive()[CombatPrayer.PROTECT_FROM_MELEE.ordinal()] = false;
            player.setHeadIcon(-1);
            player.getPacketBuilder().sendConfig(CombatPrayer.PROTECT_FROM_MELEE.getPrayerGlow(), 0);
            player.getFlags().flag(Flag.APPEARANCE);
        }
    }),

    RETRIBUTION(2, 3, 46, 98, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            CombatPrayer.PROTECT_FROM_MISSILES.deactivatePrayer(player, false);
            CombatPrayer.PROTECT_FROM_MAGIC.deactivatePrayer(player, false);
            CombatPrayer.PROTECT_FROM_MELEE.deactivatePrayer(player, false);
            CombatPrayer.REDEMPTION.deactivatePrayer(player, false);
            CombatPrayer.SMITE.deactivatePrayer(player, false);
            player.getPrayerActive()[CombatPrayer.RETRIBUTION.ordinal()] = true;
            player.setHeadIcon(CombatPrayer.RETRIBUTION.getHeadIcon());
            player.getPacketBuilder().sendConfig(CombatPrayer.RETRIBUTION.getPrayerGlow(), 1);
            player.getFlags().flag(Flag.APPEARANCE);
        }
    }, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            player.getPrayerActive()[CombatPrayer.RETRIBUTION.ordinal()] = false;
            player.setHeadIcon(-1);
            player.getPacketBuilder().sendConfig(CombatPrayer.RETRIBUTION.getPrayerGlow(), 0);
            player.getFlags().flag(Flag.APPEARANCE);
        }
    }),

    REDEMPTION(2, 5, 49, 99, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            CombatPrayer.PROTECT_FROM_MISSILES.deactivatePrayer(player, false);
            CombatPrayer.PROTECT_FROM_MAGIC.deactivatePrayer(player, false);
            CombatPrayer.PROTECT_FROM_MELEE.deactivatePrayer(player, false);
            CombatPrayer.RETRIBUTION.deactivatePrayer(player, false);
            CombatPrayer.SMITE.deactivatePrayer(player, false);
            player.getPrayerActive()[CombatPrayer.REDEMPTION.ordinal()] = true;
            player.setHeadIcon(CombatPrayer.REDEMPTION.getHeadIcon());
            player.getPacketBuilder().sendConfig(CombatPrayer.REDEMPTION.getPrayerGlow(), 1);
            player.getFlags().flag(Flag.APPEARANCE);
        }
    }, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            player.getPrayerActive()[CombatPrayer.REDEMPTION.ordinal()] = false;
            player.setHeadIcon(-1);
            player.getPacketBuilder().sendConfig(CombatPrayer.REDEMPTION.getPrayerGlow(), 0);
            player.getFlags().flag(Flag.APPEARANCE);
        }
    }),

    SMITE(2, 4, 52, 100, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            CombatPrayer.PROTECT_FROM_MISSILES.deactivatePrayer(player, false);
            CombatPrayer.PROTECT_FROM_MAGIC.deactivatePrayer(player, false);
            CombatPrayer.PROTECT_FROM_MELEE.deactivatePrayer(player, false);
            CombatPrayer.RETRIBUTION.deactivatePrayer(player, false);
            CombatPrayer.REDEMPTION.deactivatePrayer(player, false);
            player.getPrayerActive()[CombatPrayer.SMITE.ordinal()] = true;
            player.setHeadIcon(CombatPrayer.SMITE.getHeadIcon());
            player.getPacketBuilder().sendConfig(CombatPrayer.SMITE.getPrayerGlow(), 1);
            player.getFlags().flag(Flag.APPEARANCE);
        }
    }, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            player.getPrayerActive()[CombatPrayer.SMITE.ordinal()] = false;
            player.setHeadIcon(-1);
            player.getPacketBuilder().sendConfig(CombatPrayer.SMITE.getPrayerGlow(), 0);
            player.getFlags().flag(Flag.APPEARANCE);
        }
    });

    /**
     * The rate this prayer drains your prayer level at (drain by <number> every
     * 2 ticks).
     */
    private int drainRate;

    /** The head icon of this prayer. */
    private int headIcon;

    /** The level required to use this prayer. */
    private int levelRequired;

    /** The prayer glow config. */
    private int prayerGlow;

    /** The effect this prayer has on activation. */
    private GenericAction<Player> activate;

    /** The effect this prayer has on deactivation. */
    private GenericAction<Player> deactivate;

    /**
     * Create a new combat prayer.
     * 
     * @param drainRate
     *        the drain rate in seconds.
     * @param headIcon
     *        the head icon id.
     * @param levelRequired
     *        the level required to active this prayer.
     * @param prayerGlow
     *        the glow id.
     * @param activate
     *        the action on activation.
     * @param deactivate
     *        the action on deactivation.
     */
    private CombatPrayer(int drainRate, int headIcon, int levelRequired, int prayerGlow, GenericAction<Player> activate, GenericAction<Player> deactivate) {
        this.drainRate = drainRate;
        this.headIcon = headIcon;
        this.levelRequired = levelRequired;
        this.prayerGlow = prayerGlow;
        this.activate = activate;
        this.deactivate = deactivate;
    }

    /**
     * Activates this prayer if the player has the required level to do so.
     * 
     * @param player
     *        the player to activate this prayer for.'
     * @param deactivateIfNeeded
     *        if the prayer should be deactivated if it is active.
     */
    public void activatePrayer(Player player, boolean deactivateIfNeeded) {

        /** Check if this prayer is active. */
        if (CombatPrayer.isPrayerActivated(player, this)) {
            if (deactivateIfNeeded) {
                deactivatePrayer(player, false);
            }
            return;
        }

        /** Check the required level. */
        if (player.getSkills()[Misc.PRAYER].getLevelForExperience() < levelRequired) {
            player.getPacketBuilder().sendChatboxString("You need a @blu@Prayer level of " + levelRequired + " @bla@to use @blu@" + Misc.formatInputString(name().toLowerCase().replaceAll("_", " ")) + "@bla@.");
            player.getPacketBuilder().sendConfig(prayerGlow, 0);
            return;
        }

        /** Check your prayer points. */
        if (player.getSkills()[Misc.PRAYER].getLevel() < 1) {
            player.getPacketBuilder().sendMessage("You've run out of prayer points!");
            player.getPacketBuilder().sendConfig(prayerGlow, 0);
            return;
        }

        /** Start a worker if needed. */
        if (!player.getPrayerDrain().isRunning()) {
            player.setPrayerDrain(new CombatPrayerWorker(player));
            TaskFactory.getFactory().submit(player.getPrayerDrain());
        }

        /** Activate the effects of this prayer. */
        activate.fireAction(player);
    }

    /**
     * Deactivates this prayer if the player has this prayer activated.
     * 
     * @param player
     *        the player to deactivate this prayer for.
     * @param activateIfNeeded
     *        if the prayer should be activated if it is deactivated.
     */
    public void deactivatePrayer(Player player, boolean activateIfNeeded) {

        /** Check if this prayer is deactivated. */
        if (!CombatPrayer.isPrayerActivated(player, this)) {
            if (activateIfNeeded) {
                activatePrayer(player, false);
            }
            return;
        }

        /** Deactivate this prayer. */
        deactivate.fireAction(player);
    }

    /**
     * Deactivates all activated prayers for the specified player.
     * 
     * @param player
     *        the player to deactivate all prayers for.
     */
    public static void deactivateAllPrayer(Player player) {
        for (CombatPrayer combatPrayer : CombatPrayer.values()) {
            if (CombatPrayer.isPrayerActivated(player, combatPrayer)) {
                combatPrayer.deactivatePrayer(player, false);
            }
        }
    }

    /**
     * Resets all of the prayer glows to their default states.
     * 
     * @param player
     *        the player to reset the prayer glows for.
     */
    public static void resetPrayerGlows(Player player) {
        for (CombatPrayer combatPrayer : CombatPrayer.values()) {
            player.getPacketBuilder().sendConfig(combatPrayer.getPrayerGlow(), 0);
        }
    }

    /**
     * Gets a {@link CombatPrayer} constant by its id.
     * 
     * @param prayerId
     *        the id of the constant.
     * @return the constant corresponding to the id.
     */
    public static CombatPrayer getPrayer(int prayerId) {
        return CombatPrayer.values()[prayerId];
    }

    /**
     * Determines if a prayer is activated or not.
     * 
     * @param player
     *        the player to determine for.
     * @param prayer
     *        the prayer to check.
     * @return true if the prayer is activated.
     */
    public static boolean isPrayerActivated(Player player, CombatPrayer prayer) {
        return player.getPrayerActive()[prayer.ordinal()];
    }

    /**
     * Gets the rate this prayer drains your prayer level at (drain by <number>
     * every 2 ticks).
     * 
     * @return the rate this prayer drains your prayer level at.
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

    /**
     * Gets the effect this prayer has on activation.
     * 
     * @return the effect this prayer has on activation.
     */
    public GenericAction<Player> getActivate() {
        return activate;
    }

    /**
     * Gets the effect this prayer has on deactivation.
     * 
     * @return the effect this prayer has on deactivation.
     */
    public GenericAction<Player> getDeactivate() {
        return deactivate;
    }
}
