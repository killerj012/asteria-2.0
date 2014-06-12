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
 * @author Ryley Kimmel <ryley.kimmel@ive.com>
 */
public enum CombatPrayer {

    THICK_SKIN(20, -1, 1, 83) {
        @Override
        public void deactivateConflictingPrayers(Player player) {
            CombatPrayer.ROCK_SKIN.deactivatePrayer(player, false);
            CombatPrayer.STEEL_SKIN.deactivatePrayer(player, false);
        }
    },

    BURST_OF_STRENGTH(20, -1, 4, 84) {
        @Override
        public void deactivateConflictingPrayers(Player player) {
            CombatPrayer.SUPERHUMAN_STRENGTH.deactivatePrayer(player, false);
            CombatPrayer.ULTIMATE_STRENGTH.deactivatePrayer(player, false);
        }
    },

    CLARITY_OF_THOUGHT(20, -1, 7, 85) {
        @Override
        public void deactivateConflictingPrayers(Player player) {
            CombatPrayer.IMPROVED_REFLEXES.deactivatePrayer(player, false);
            CombatPrayer.INCREDIBLE_REFLEXES.deactivatePrayer(player, false);
        }
    },

    ROCK_SKIN(10, -1, 10, 86) {
        @Override
        public void deactivateConflictingPrayers(Player player) {
            CombatPrayer.STEEL_SKIN.deactivatePrayer(player, false);
            CombatPrayer.THICK_SKIN.deactivatePrayer(player, false);
        }
    },

    SUPERHUMAN_STRENGTH(10, -1, 13, 87) {
        @Override
        public void deactivateConflictingPrayers(Player player) {
            CombatPrayer.BURST_OF_STRENGTH.deactivatePrayer(player, false);
            CombatPrayer.ULTIMATE_STRENGTH.deactivatePrayer(player, false);
        }
    },

    IMPROVED_REFLEXES(10, -1, 16, 88) {
        @Override
        public void deactivateConflictingPrayers(Player player) {
            CombatPrayer.CLARITY_OF_THOUGHT.deactivatePrayer(player, false);
            CombatPrayer.INCREDIBLE_REFLEXES.deactivatePrayer(player, false);
        }
    },

    RAPID_RESTORE(29, -1, 19, 89),

    RAPID_HEAL(29, -1, 22, 90),

    PROTECT_ITEM(29, -1, 25, 91),

    STEEL_SKIN(5, -1, 28, 92) {
        @Override
        public void deactivateConflictingPrayers(Player player) {
            CombatPrayer.THICK_SKIN.deactivatePrayer(player, false);
            CombatPrayer.ROCK_SKIN.deactivatePrayer(player, false);
        }
    },

    ULTIMATE_STRENGTH(5, -1, 31, 93) {
        @Override
        public void deactivateConflictingPrayers(Player player) {
            CombatPrayer.BURST_OF_STRENGTH.deactivatePrayer(player, false);
            CombatPrayer.SUPERHUMAN_STRENGTH.deactivatePrayer(player, false);
        }
    },

    INCREDIBLE_REFLEXES(5, -1, 34, 94) {
        @Override
        public void deactivateConflictingPrayers(Player player) {
            CombatPrayer.IMPROVED_REFLEXES.deactivatePrayer(player, false);
            CombatPrayer.CLARITY_OF_THOUGHT.deactivatePrayer(player, false);
        }
    },

    PROTECT_FROM_MAGIC(5, 2, 37, 95) {
        @Override
        public void deactivateConflictingPrayers(Player player) {
            CombatPrayer.PROTECT_FROM_MISSILES.deactivatePrayer(player, false);
            CombatPrayer.PROTECT_FROM_MELEE.deactivatePrayer(player, false);
            CombatPrayer.REDEMPTION.deactivatePrayer(player, false);
            CombatPrayer.RETRIBUTION.deactivatePrayer(player, false);
            CombatPrayer.SMITE.deactivatePrayer(player, false);
        }
    },

    PROTECT_FROM_MISSILES(5, 1, 40, 96) {
        @Override
        public void deactivateConflictingPrayers(Player player) {
            CombatPrayer.REDEMPTION.deactivatePrayer(player, false);
            CombatPrayer.PROTECT_FROM_MAGIC.deactivatePrayer(player, false);
            CombatPrayer.PROTECT_FROM_MELEE.deactivatePrayer(player, false);
            CombatPrayer.RETRIBUTION.deactivatePrayer(player, false);
            CombatPrayer.SMITE.deactivatePrayer(player, false);
        }
    },

    PROTECT_FROM_MELEE(5, 0, 43, 97) {
        @Override
        public void deactivateConflictingPrayers(Player player) {
            CombatPrayer.PROTECT_FROM_MISSILES.deactivatePrayer(player, false);
            CombatPrayer.PROTECT_FROM_MAGIC.deactivatePrayer(player, false);
            CombatPrayer.REDEMPTION.deactivatePrayer(player, false);
            CombatPrayer.RETRIBUTION.deactivatePrayer(player, false);
            CombatPrayer.SMITE.deactivatePrayer(player, false);
        }
    },

    RETRIBUTION(17, 3, 46, 98) {
        @Override
        public void deactivateConflictingPrayers(Player player) {
            CombatPrayer.PROTECT_FROM_MISSILES.deactivatePrayer(player, false);
            CombatPrayer.PROTECT_FROM_MAGIC.deactivatePrayer(player, false);
            CombatPrayer.PROTECT_FROM_MELEE.deactivatePrayer(player, false);
            CombatPrayer.REDEMPTION.deactivatePrayer(player, false);
            CombatPrayer.SMITE.deactivatePrayer(player, false);
        }
    },

    REDEMPTION(6, 5, 49, 99) {
        @Override
        public void deactivateConflictingPrayers(Player player) {
            CombatPrayer.PROTECT_FROM_MISSILES.deactivatePrayer(player, false);
            CombatPrayer.PROTECT_FROM_MAGIC.deactivatePrayer(player, false);
            CombatPrayer.PROTECT_FROM_MELEE.deactivatePrayer(player, false);
            CombatPrayer.RETRIBUTION.deactivatePrayer(player, false);
            CombatPrayer.SMITE.deactivatePrayer(player, false);
        }
    },

    SMITE(7, 4, 52, 100) {
        @Override
        public void deactivateConflictingPrayers(Player player) {
            CombatPrayer.PROTECT_FROM_MISSILES.deactivatePrayer(player, false);
            CombatPrayer.PROTECT_FROM_MAGIC.deactivatePrayer(player, false);
            CombatPrayer.PROTECT_FROM_MELEE.deactivatePrayer(player, false);
            CombatPrayer.RETRIBUTION.deactivatePrayer(player, false);
            CombatPrayer.REDEMPTION.deactivatePrayer(player, false);
        }
    };

    /** Drain the prayer by 1 every <number> ticks. */
    private int drainRate;

    /** The head icon of this prayer. */
    private int headIcon;

    /** The level required to use this prayer. */
    private int levelRequired;

    /** The prayer glow config. */
    private int prayerGlow;
    
    /**
     * Deactivates conflicting prayers.
     * 
     * @param player The player to deactivate the conflicting prayers for.
     * 
     * <p>
     * If protect form melee is on and you turn on protect from mage, protect
     * from melee will now be turned off.
     * </p>
     */
    protected void deactivateConflictingPrayers(Player player) {
	/* Method intended to be overridden. */
    }

    /** The effect this prayer has on activation. */
    private final GenericAction<Player> activate = (Player player) -> {
	/* Deactivate conflicting prayers. */
	deactivateConflictingPrayers(player);
	/* Activate this prayer. */
        player.getPrayerActive()[ordinal()] = true;
        /* Send the prayer glow. */
        player.getPacketBuilder().sendConfig(prayerGlow, 1);
         
        /* If the prayer has a head icon. */
        if (headIcon != -1) {
            /* Set it. */
            player.setHeadIcon(headIcon);
            /* Update this players appearance. */
            player.getFlags().flag(Flag.APPEARANCE);
        }
        return true;
    };

    /** The effect this prayer has on deactivation. */
    private final GenericAction<Player> deactivate = (Player player) -> {
	/* Deactivate the prayer. */
        player.getPrayerActive()[ordinal()] = false;
        /* Remove the prayer glow. */
        player.getPacketBuilder().sendConfig(prayerGlow, 0);
        
        /* If the prayer has a head icon */
	if (headIcon != -1) {
	    /* Remove it. */
	    player.setHeadIcon(-1);
	    /* Update this players appearance. */
	    player.getFlags().flag(Flag.APPEARANCE);
	}
	return true;
    };

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
    private CombatPrayer(int drainRate, int headIcon, int levelRequired, int prayerGlow) {
        this.drainRate = drainRate;
        this.headIcon = headIcon;
        this.levelRequired = levelRequired;
        this.prayerGlow = prayerGlow;
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
