package server.world.entity.combat;

import server.util.Misc.GenericAction;
import server.world.entity.player.Player;

public enum CombatPrayer {

    THICK_SKIN(6, -1, 1, 83, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            Prayer.getSingleton().stopCombatPrayer(player, ROCK_SKIN);
            Prayer.getSingleton().stopCombatPrayer(player, STEEL_SKIN);
        }
    }, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {

        }
    }),

    BURST_OF_STRENGTH(6, -1, 4, 84, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            Prayer.getSingleton().stopCombatPrayer(player, SUPERHUMAN_STRENGTH);
            Prayer.getSingleton().stopCombatPrayer(player, ULTIMATE_STRENGTH);
        }
    }, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            // TODO Auto-generated method stub

        }
    }),

    CLARITY_OF_THOUGHT(6, -1, 7, 85, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            Prayer.getSingleton().stopCombatPrayer(player, IMPROVED_REFLEXES);
            Prayer.getSingleton().stopCombatPrayer(player, INCREDIBLE_REFLEXES);
        }
    }, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            // TODO Auto-generated method stub

        }
    }),

    ROCK_SKIN(6, -1, 10, 86, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            Prayer.getSingleton().stopCombatPrayer(player, STEEL_SKIN);
            Prayer.getSingleton().stopCombatPrayer(player, THICK_SKIN);
        }
    }, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            // TODO Auto-generated method stub

        }
    }),

    SUPERHUMAN_STRENGTH(6, -1, 13, 87, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            Prayer.getSingleton().stopCombatPrayer(player, BURST_OF_STRENGTH);
            Prayer.getSingleton().stopCombatPrayer(player, ULTIMATE_STRENGTH);
        }
    }, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            // TODO Auto-generated method stub

        }
    }),

    IMPROVED_REFLEXES(6, -1, 16, 88, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            Prayer.getSingleton().stopCombatPrayer(player, CLARITY_OF_THOUGHT);
            Prayer.getSingleton().stopCombatPrayer(player, INCREDIBLE_REFLEXES);
        }
    }, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            // TODO Auto-generated method stub

        }
    }),

    RAPID_RESTORE(5, -1, 19, 89, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {

        }
    }, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            // TODO Auto-generated method stub

        }
    }),

    RAPID_HEAL(5, -1, 22, 90, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {

        }
    }, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            // TODO Auto-generated method stub

        }
    }),

    PROTECT_ITEM(5, -1, 25, 91, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {

        }
    }, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            // TODO Auto-generated method stub

        }
    }),

    STEEL_SKIN(4, -1, 28, 92, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            Prayer.getSingleton().stopCombatPrayer(player, THICK_SKIN);
            Prayer.getSingleton().stopCombatPrayer(player, ROCK_SKIN);
        }
    }, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            // TODO Auto-generated method stub

        }
    }),

    ULTIMATE_STRENGTH(4, -1, 31, 93, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            Prayer.getSingleton().stopCombatPrayer(player, BURST_OF_STRENGTH);
            Prayer.getSingleton().stopCombatPrayer(player, SUPERHUMAN_STRENGTH);
        }
    }, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            // TODO Auto-generated method stub

        }
    }),

    INCREDIBLE_REFLEXES(4, -1, 34, 94, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            Prayer.getSingleton().stopCombatPrayer(player, IMPROVED_REFLEXES);
            Prayer.getSingleton().stopCombatPrayer(player, CLARITY_OF_THOUGHT);
        }
    }, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            // TODO Auto-generated method stub

        }
    }),

    PROTECT_FROM_MAGIC(3, 2, 37, 95, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            Prayer.getSingleton().stopCombatPrayer(player, PROTECT_FROM_MISSILES);
            Prayer.getSingleton().stopCombatPrayer(player, PROTECT_FROM_MELEE);
            Prayer.getSingleton().stopCombatPrayer(player, RETRIBUTION);
            Prayer.getSingleton().stopCombatPrayer(player, REDEMPTION);
            Prayer.getSingleton().stopCombatPrayer(player, SMITE);
        }
    }, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            // TODO Auto-generated method stub

        }
    }),

    PROTECT_FROM_MISSILES(3, 1, 40, 96, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            Prayer.getSingleton().stopCombatPrayer(player, PROTECT_FROM_MAGIC);
            Prayer.getSingleton().stopCombatPrayer(player, PROTECT_FROM_MELEE);
            Prayer.getSingleton().stopCombatPrayer(player, RETRIBUTION);
            Prayer.getSingleton().stopCombatPrayer(player, REDEMPTION);
            Prayer.getSingleton().stopCombatPrayer(player, SMITE);
        }
    }, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            // TODO Auto-generated method stub

        }
    }),

    PROTECT_FROM_MELEE(3, 0, 43, 97, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            Prayer.getSingleton().stopCombatPrayer(player, PROTECT_FROM_MISSILES);
            Prayer.getSingleton().stopCombatPrayer(player, PROTECT_FROM_MAGIC);
            Prayer.getSingleton().stopCombatPrayer(player, RETRIBUTION);
            Prayer.getSingleton().stopCombatPrayer(player, REDEMPTION);
            Prayer.getSingleton().stopCombatPrayer(player, SMITE);
        }
    }, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            // TODO Auto-generated method stub

        }
    }),

    RETRIBUTION(2, 3, 46, 98, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            Prayer.getSingleton().stopCombatPrayer(player, PROTECT_FROM_MISSILES);
            Prayer.getSingleton().stopCombatPrayer(player, PROTECT_FROM_MAGIC);
            Prayer.getSingleton().stopCombatPrayer(player, PROTECT_FROM_MELEE);
            Prayer.getSingleton().stopCombatPrayer(player, REDEMPTION);
            Prayer.getSingleton().stopCombatPrayer(player, SMITE);
        }
    }, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            // TODO Auto-generated method stub

        }
    }),

    REDEMPTION(2, 5, 49, 99, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            Prayer.getSingleton().stopCombatPrayer(player, PROTECT_FROM_MISSILES);
            Prayer.getSingleton().stopCombatPrayer(player, PROTECT_FROM_MAGIC);
            Prayer.getSingleton().stopCombatPrayer(player, PROTECT_FROM_MELEE);
            Prayer.getSingleton().stopCombatPrayer(player, RETRIBUTION);
            Prayer.getSingleton().stopCombatPrayer(player, SMITE);
        }
    }, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            // TODO Auto-generated method stub

        }
    }),

    SMITE(2, 4, 52, 100, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            Prayer.getSingleton().stopCombatPrayer(player, PROTECT_FROM_MISSILES);
            Prayer.getSingleton().stopCombatPrayer(player, PROTECT_FROM_MAGIC);
            Prayer.getSingleton().stopCombatPrayer(player, PROTECT_FROM_MELEE);
            Prayer.getSingleton().stopCombatPrayer(player, RETRIBUTION);
            Prayer.getSingleton().stopCombatPrayer(player, REDEMPTION);
        }
    }, new GenericAction<Player>() {
        @Override
        public void fireAction(Player player) {
            // TODO Auto-generated method stub

        }
    });

    /**
     * The rate this prayer drains your prayer level at (drain by 1 every
     * <number> ticks).
     */
    private int drainRate;

    /**
     * The headicon of this prayer.
     */
    private int headIcon;

    /**
     * The level required to use this prayer.
     */
    private int levelRequired;

    /**
     * The prayer glow config.
     */
    private int prayerGlow;

    /**
     * The effect this prayer has on activation.
     */
    private GenericAction<Player> activate;

    /**
     * The effect this prayer has on deactivation.
     */
    private GenericAction<Player> deactivate;

    /**
     * Create a new combat prayer.
     * 
     * @param drainRate
     *        the drain rate in seconds.
     * @param headIcon
     *        the headicon id.
     * @param levelRequired
     *        the level required to active this prayer.
     * @param prayerGlow
     *        the glow id.
     * @param activate
     *        the action on activation.
     * @param deactivate
     *        the action on deactivation.
     */
    CombatPrayer(int drainRate, int headIcon, int levelRequired, int prayerGlow, GenericAction<Player> activate, GenericAction<Player> deactivate) {
        this.setDrainRate(drainRate);
        this.setHeadIcon(headIcon);
        this.setLevelRequired(levelRequired);
        this.setPrayerGlow(prayerGlow);
        this.setActivate(deactivate);
        this.setDeactivate(deactivate);
    }

    /**
     * @return the drainRate.
     */
    public int getDrainRate() {
        return drainRate;
    }

    /**
     * @param drainRate
     *        the drainRate to set.
     */
    public void setDrainRate(int drainRate) {
        this.drainRate = drainRate;
    }

    /**
     * @return the headIcon.
     */
    public int getHeadIcon() {
        return headIcon;
    }

    /**
     * @param headIcon
     *        the headIcon to set.
     */
    public void setHeadIcon(int headIcon) {
        this.headIcon = headIcon;
    }

    /**
     * @return the levelRequired.
     */
    public int getLevelRequired() {
        return levelRequired;
    }

    /**
     * @param levelRequired
     *        the levelRequired to set.
     */
    public void setLevelRequired(int levelRequired) {
        this.levelRequired = levelRequired;
    }

    /**
     * @return the prayerGlow.
     */
    public int getPrayerGlow() {
        return prayerGlow;
    }

    /**
     * @param prayerGlow
     *        the prayerGlow to set.
     */
    public void setPrayerGlow(int prayerGlow) {
        this.prayerGlow = prayerGlow;
    }

    /**
     * @return the activate.
     */
    public GenericAction<Player> getActivate() {
        return activate;
    }

    /**
     * @param activate
     *        the activate to set.
     */
    public void setActivate(GenericAction<Player> activate) {
        this.activate = activate;
    }

    /**
     * @return the deactivate.
     */
    public GenericAction<Player> getDeactivate() {
        return deactivate;
    }

    /**
     * @param deactivate
     *        the deactivate to set.
     */
    public void setDeactivate(GenericAction<Player> deactivate) {
        this.deactivate = deactivate;
    }
}
