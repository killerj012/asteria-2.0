package com.asteria.world.entity.player.content;

import static com.asteria.world.entity.player.skill.Skills.ATTACK;
import static com.asteria.world.entity.player.skill.Skills.DEFENCE;
import static com.asteria.world.entity.player.skill.Skills.HITPOINTS;
import static com.asteria.world.entity.player.skill.Skills.MAGIC;
import static com.asteria.world.entity.player.skill.Skills.PRAYER;
import static com.asteria.world.entity.player.skill.Skills.RANGED;
import static com.asteria.world.entity.player.skill.Skills.STRENGTH;

import java.util.EnumSet;
import java.util.Set;

import com.asteria.engine.task.Task;
import com.asteria.engine.task.TaskManager;
import com.asteria.world.entity.Animation;
import com.asteria.world.entity.player.Player;
import com.asteria.world.entity.player.skill.Skill;
import com.asteria.world.entity.player.skill.Skills;
import com.asteria.world.item.Item;

/**
 * An enumeration managing consumable potion types.
 * 
 * @author Ryley Kimmel <ryley.kimmel@live.com>
 * @author lare96
 */
public enum PotionConsumable {

    RANGE_POTIONS(2444, 169, 171, 173) {
        @Override
        public void potionEffect(Player player) {
            doBasicEffect(player, RANGED, BoostType.NORMAL);

        }
    },

    ENERGY_POTIONS(3008, 3010, 3012, 3014) {
        @Override
        public void potionEffect(Player player) {
            doEnergyPotion(player);

        }
    },

    SUPER_ENERGY_POTIONS(3016, 3018, 3020, 3022) {
        @Override
        public void potionEffect(Player player) {
            doSuperEnergyPotion(player);

        }
    },

    MAGIC_POTIONS(3040, 3042, 3044, 3046) {
        @Override
        public void potionEffect(Player player) {
            doBasicEffect(player, MAGIC, BoostType.NORMAL);

        }
    },

    DEFENCE_POTIONS(2432, 133, 135, 137) {
        @Override
        public void potionEffect(Player player) {
            doBasicEffect(player, DEFENCE, BoostType.NORMAL);

        }
    },

    STRENGTH_POTIONS(113, 115, 117, 119) {
        @Override
        public void potionEffect(Player player) {
            doBasicEffect(player, STRENGTH, BoostType.NORMAL);

        }
    },

    ATTACK_POTIONS(2428, 121, 123, 125) {
        @Override
        public void potionEffect(Player player) {
            doBasicEffect(player, ATTACK, BoostType.NORMAL);

        }
    },

    SUPER_DEFENCE_POTIONS(2442, 163, 165, 167) {
        @Override
        public void potionEffect(Player player) {
            doBasicEffect(player, DEFENCE, BoostType.SUPER);

        }
    },

    SUPER_ATTACK_POTIONS(2436, 145, 147, 149) {
        @Override
        public void potionEffect(Player player) {
            doBasicEffect(player, ATTACK, BoostType.SUPER);

        }
    },

    SUPER_STRENGTH_POTIONS(2440, 157, 159, 161) {
        @Override
        public void potionEffect(Player player) {
            doBasicEffect(player, STRENGTH, BoostType.SUPER);

        }
    },

    SUPER_RESTORE_POTIONS(3024, 3026, 3028, 3030) {
        @Override
        public void potionEffect(Player player) {
            doPrayerPotion(player, true);

        }
    },

    PRAYER_POTIONS(2434, 139, 141, 143) {
        @Override
        public void potionEffect(Player player) {
            doPrayerPotion(player, false);

        }
    },

    ANTI_FIRE_POTIONS(2452, 2454, 2456, 2458) {
        @Override
        public void potionEffect(Player player) {
            doAntiFire(player);

        }
    },

    ANTIPOISON_POTIONS(2446, 175, 177, 179) {
        @Override
        public void potionEffect(Player player) {
            doAntiPoison(player, false);

        }
    },

    SUPER_ANTIPOISON_POTIONS(2448, 181, 183, 185) {
        @Override
        public void potionEffect(Player player) {
            doAntiPoison(player, true);

        }
    };

    /** All of the id's for the different potion doses. */
    private final int[] ids;

    /** The enum set of potions. */
    private static final Set<PotionConsumable> ALL_POTIONS = EnumSet
            .allOf(PotionConsumable.class);

    /**
     * Create a new {@link PotionConsumable}.
     * 
     * @param ids
     *            the id's for the different potion doses.
     */
    private PotionConsumable(int... ids) {
        this.ids = ids;
    }

    public abstract void potionEffect(Player player);

    /**
     * Get a {@link PotionConsumable} by its potion id.
     * 
     * @param id
     *            the id of the potion.
     * @return the potion consumable instance.
     */
    private static PotionConsumable forId(int id) {
        for (PotionConsumable potion : ALL_POTIONS) {
            for (int potionId : potion.getIds()) {
                if (id == potionId) {
                    return potion;
                }
            }
        }
        return null;
    }

    /**
     * Gets the potion replacement dose.
     * 
     * @param item
     *            the higher potion dose.
     * @return the lower potion dose.
     */
    private static Item getReplacementItem(Item item) {
        PotionConsumable potion = forId(item.getId());
        int length = potion.getIds().length;
        for (int index = 0; index < length; index++) {
            if (potion.getIds()[index] == item.getId() && index + 1 < length) {
                return new Item(potion.getIds()[index + 1]);
            }
        }
        return VIAL;
    }

    /**
     * Gets the id's for the different potion doses.
     * 
     * @return the id's for the different potion doses.
     */
    public int[] getIds() {
        return ids;
    }

    /** The default potion dose. */
    public final static Item VIAL = new Item(229);

    /**
     * Does the prayer potion action.
     * 
     * @param player
     *            the player to do this action for.
     * @param isRestorePotion
     *            true if this potion is a restore potion.
     */
    private static void doPrayerPotion(Player player, boolean isRestorePotion) {
        Skill skill = player.getSkills()[PRAYER];
        int realLevel = skill.getLevelForExperience();

        skill.increaseLevel((int) (realLevel * .33), realLevel);

        if (isRestorePotion) {
            skill.increaseLevel(1, realLevel);
            getRestoreStats(player);
        }
        Skills.refresh(player, PRAYER);
    }

    /**
     * Does the energy potion action.
     * 
     * @param player
     *            the player to do this action for.
     */
    private static void doEnergyPotion(Player player) {
        player.incrementRunEnergy(10);
    }

    /**
     * Does the super energy potion action.
     * 
     * @param player
     *            the player to do this action for.
     */
    private static void doSuperEnergyPotion(Player player) {
        player.incrementRunEnergy(10);
    }

    /**
     * Does the anti-poison potion action.
     * 
     * @param player
     *            the player to do this action for.
     * @param boolean superPotion if the boolean is a super potion.
     */
    private static void doAntiPoison(final Player player, boolean superPotion) {
        if (player.isPoisoned()) {
            player.setPoisonDamage(0);
            player.getPacketBuilder().sendMessage(
                    "You have been cured of your poison!");
        }

        if (superPotion) {

            if (player.getPoisonImmunity() <= 0) {
                player.getPacketBuilder().sendMessage(
                        "You have been granted immunity against poison.");
                player.incrementPoisonImmunity(500);

                TaskManager.submit(new Task(50, false) {
                    @Override
                    public void fire() {
                        player.decrementPoisonImmunity(50);
                        if (player.getPoisonImmunity() == 50) {
                            player.getPacketBuilder()
                                    .sendMessage(
                                            "Your resistance to poison is about to wear off!");
                        } else if (player.getPoisonImmunity() <= 0) {
                            cancel();
                        }
                    }

                    @Override
                    public void fireOnCancel() {
                        player.getPacketBuilder().sendMessage(
                                "Your resistance to poison has worn off!");
                        player.setPoisonImmunity(0);
                    }

                }.bind(player));
            } else if (player.getPoisonImmunity() > 0) {
                player.getPacketBuilder().sendMessage(
                        "Your immunity against poison has been restored!");
                player.incrementPoisonImmunity(250);
            }
        }
    }

    /**
     * Does the restore potion action.
     * 
     * @param player
     *            the player to do this action for.
     */
    private static void getRestoreStats(Player player) {
        for (int index = 0; index <= 6; index++) {
            if ((index == PRAYER) || (index == HITPOINTS)) {
                continue;
            }
            Skill skill = player.getSkills()[index];
            int realLevel = skill.getLevelForExperience();

            skill.increaseLevel((int) (realLevel * .33), realLevel);
            Skills.refresh(player, index);
        }
    }

    /**
     * Does the ant-fire potion action.
     * 
     * @param player
     *            the player to do this action for..
     */
    private static void doAntiFire(final Player player) {
        // TODO: When king black dragon is done, check if immunity is
        // above 0. If not then deal fire damage.
        //
        // if(player.getDragonFireImmunity() > 0) {
        // player.getPacketBuilder().sendMessage("You are immune to dragonfire!");
        // return;
        // }

        if (player.getPoisonImmunity() <= 0) {
            player.getPacketBuilder().sendMessage(
                    "You have been granted immunity against dragon fire.");
            player.incrementDragonFireImmunity(360);

            TaskManager.submit(new Task(30, false) {
                @Override
                public void fire() {
                    player.decrementDragonFireImmunity(30);
                    if (player.getDragonFireImmunity() == 30) {
                        player.getPacketBuilder()
                                .sendMessage(
                                        "Your resistance to dragon fire is about to wear off!");
                    } else if (player.getDragonFireImmunity() <= 0) {
                        cancel();
                    }
                }

                @Override
                public void fireOnCancel() {
                    player.getPacketBuilder().sendMessage(
                            "Your resistance to dragon fire has worn off!");
                    player.setDragonFireImmunity(0);
                }

            }.bind(player));
        } else if (player.getPoisonImmunity() > 0) {
            player.getPacketBuilder().sendMessage(
                    "Your immunity against dragon fire has been increased.");
            player.incrementDragonFireImmunity(180);
        }
    }

    /**
     * Does the basic effect potion action.
     * 
     * @param player
     *            the player to do this action for.
     * @param skillId
     *            the skill to boost.
     * @param type
     *            the boost type.
     */
    private static void doBasicEffect(Player player, int skillId, BoostType type) {
        Skill skill = player.getSkills()[skillId];
        int realLevel = skill.getLevelForExperience();
        int boostLevel = Math.round(realLevel * type.getBoostAmount());
        int cap = realLevel + boostLevel;
        if (type == BoostType.NORMAL) {
            boostLevel += 1;
        }

        if ((skill.getLevel() + boostLevel) > (realLevel + boostLevel + 1)) {
            boostLevel = (realLevel + boostLevel) - skill.getLevel();
        }

        skill.increaseLevel(boostLevel, cap);
        Skills.refresh(player, skillId);
    }

    /**
     * Handles the consumption of the actual potion.
     * 
     * @param player
     *            the player consuming the potion.
     * @param item
     *            the potion being consumed.
     * @param slot
     *            the inventory slot the potion is in.
     * @return true if the potion was consumed.
     */
    public static boolean consume(Player player, Item item, int slot) {
        PotionConsumable potion = forId(item.getId());
        if (potion == null) {
            return false;
        }
        if (player.isDead()) {
            return false;
        }
        if (player.getPotionTimer().elapsed() < 1500) {
            return false;
        }
        // TODO: Check duel rule for no potions.

        player.animation(new Animation(829));
        player.getPotionTimer().reset();
        player.getEatingTimer().reset();
        player.getInventory().deleteItemSlot(item, slot);
        player.getInventory().addItem(getReplacementItem(item));
        potion.potionEffect(player);
        return true;
    }

    /**
     * The type of boost given when drinking a potion.
     * 
     * @author lare96
     */
    private enum BoostType {
        NORMAL(.13F),
        SUPER(.20F);

        /** The boost amount. */
        private final float boostAmount;

        /**
         * Create a new {@link BoostType}.
         * 
         * @param boostAmount
         *            the boost amount.
         */
        private BoostType(float boostAmount) {
            this.boostAmount = boostAmount;
        }

        /**
         * Gets the boost amount.
         * 
         * @return the boost amount.
         */
        protected final float getBoostAmount() {
            return boostAmount;
        }
    }
}
