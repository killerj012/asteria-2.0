package com.asteria.world.entity.combat.special;

import java.util.Set;

import com.asteria.engine.task.Task;
import com.asteria.engine.task.TaskFactory;
import com.asteria.util.Utility;
import com.asteria.world.entity.Animation;
import com.asteria.world.entity.Entity;
import com.asteria.world.entity.Entity.EntityType;
import com.asteria.world.entity.Graphic;
import com.asteria.world.entity.Hit;
import com.asteria.world.entity.Projectile;
import com.asteria.world.entity.combat.CombatContainer;
import com.asteria.world.entity.combat.CombatFactory;
import com.asteria.world.entity.combat.CombatFactory.CombatType;
import com.asteria.world.entity.player.Player;
import com.asteria.world.entity.player.skill.Skills;
import com.asteria.world.map.Location;

/**
 * Holds constants that hold data for all of the special attacks that can be
 * used.
 * 
 * @author lare96
 */
public enum CombatSpecial {

    DRAGON_DAGGER(25, 1.15, 1.25, CombatType.MELEE) {
        @Override
        public void onActivation(Player player, Entity target) {}

        @Override
        public CombatContainer container(Player player, Entity target) {
            player.animation(new Animation(1062));
            player.highGraphic(new Graphic(252));

            return new CombatContainer(player, target, 2, CombatType.MELEE,
                    true);
        }
    },
    GRANITE_MAUL(50, 1.5, 1, CombatType.MELEE) {
        @Override
        public void onActivation(Player player, Entity target) {}

        @Override
        public CombatContainer container(Player player, Entity target) {
            player.animation(new Animation(1667));
            player.highGraphic(new Graphic(337));

            return new CombatContainer(player, target, 1, CombatType.MELEE,
                    true);
        }
    },
    ABYSSAL_WHIP(50, 1, 1, CombatType.MELEE) {
        @Override
        public void onActivation(Player player, Entity target) {}

        @Override
        public CombatContainer container(Player player, Entity target) {
            player.animation(new Animation(1658));
            target.highGraphic(new Graphic(341));

            return new CombatContainer(player, target, 1, CombatType.MELEE,
                    false);
        }
    },
    DRAGON_LONGSWORD(25, 1.25, 1.25, CombatType.MELEE) {
        @Override
        public void onActivation(Player player, Entity target) {}

        @Override
        public CombatContainer container(Player player, Entity target) {
            player.animation(new Animation(1058));
            player.highGraphic(new Graphic(248));

            return new CombatContainer(player, target, 1, CombatType.MELEE,
                    true);
        }
    },
    MAGIC_SHORTBOW(50, 1, 1.1, CombatType.RANGED) {
        @Override
        public void onActivation(Player player, Entity target) {}

        @Override
        public CombatContainer container(final Player player,
                final Entity target) {
            player.animation(new Animation(426));
            player.highGraphic(new Graphic(250));
            new Projectile(player, target, 249, 44, 3, 43, 31, 0)
                    .sendProjectile();

            TaskFactory.submit(new Task(1, false) {
                @Override
                public void fire() {
                    player.animation(new Animation(426));
                    player.highGraphic(new Graphic(250));
                    new Projectile(player, target, 249, 44, 3, 43, 31, 0)
                            .sendProjectile();
                    this.cancel();
                }
            });

            return new CombatContainer(player, target, 2, CombatType.RANGED,
                    true);
        }
    },
    MAGIC_LONGBOW(35, 1, 5, CombatType.RANGED) {
        @Override
        public void onActivation(Player player, Entity target) {}

        @Override
        public CombatContainer container(Player player, Entity target) {
            player.animation(new Animation(426));
            player.highGraphic(new Graphic(250));
            new Projectile(player, target, 249, 44, 3, 43, 31, 0)
                    .sendProjectile();

            return new CombatContainer(player, target, 1, CombatType.RANGED,
                    true);
        }
    },
    DRAGON_BATTLEAXE(100, 1, 1, CombatType.MELEE) {
        @Override
        public void onActivation(Player player, Entity target) {
            player.graphic(new Graphic(246));
            player.animation(new Animation(1056));
            player.forceChat("Raarrrrrgggggghhhhhhh!");
            player.getSkills()[Skills.STRENGTH]
                    .increaseLevel((int) (player.getSkills()[Skills.STRENGTH]
                            .getLevelForExperience() * 0.2));
            player.getSkills()[Skills.ATTACK].decreaseLevel((int) (player
                    .getSkills()[Skills.ATTACK]
                            .getLevelForExperience() * 0.1));
            player.getSkills()[Skills.DEFENCE]
                    .decreaseLevel((int) (player.getSkills()[Skills.DEFENCE]
                            .getLevelForExperience() * 0.1));
            player.getSkills()[Skills.RANGED].decreaseLevel((int) (player
                    .getSkills()[Skills.RANGED]
                            .getLevelForExperience() * 0.1));
            player.getSkills()[Skills.MAGIC].decreaseLevel((int) (player
                    .getSkills()[Skills.MAGIC].getLevelForExperience() * 0.1));
            Skills.refresh(player, Skills.STRENGTH);
            Skills.refresh(player, Skills.ATTACK);
            Skills.refresh(player, Skills.DEFENCE);
            Skills.refresh(player, Skills.RANGED);
            Skills.refresh(player, Skills.MAGIC);
            player.getCombatBuilder().resetAttackTimer();
            CombatSpecial.drain(player, DRAGON_BATTLEAXE.drainAmount);
        }

        @Override
        public CombatContainer container(Player player, Entity target) {
            return null;
        }
    },
    DRAGON_SPEAR(25, 1, 1, CombatType.MELEE) {
        @Override
        public void onActivation(Player player, Entity target) {}

        @Override
        public CombatContainer container(final Player player,
                final Entity target) {
            player.animation(new Animation(1064));
            player.graphic(new Graphic(253));

            return new CombatContainer(player, target, 1, CombatType.MELEE,
                    true) {
                @Override
                public void onHit(int damage, boolean accurate) {
                    if (target.type() == EntityType.PLAYER) {
                        target.getMovementQueue()
                                .walk(Utility.DIRECTION_DELTA_X[player
                                        .getLastDirection()],
                                        Utility.DIRECTION_DELTA_Y[player
                                                .getLastDirection()]);
                    }
                    target.graphic(new Graphic(80));

                    target.getMovementQueueListener().append(new Runnable() {
                        @Override
                        public void run() {
                            target.getMovementQueue().freeze(6000);
                        }
                    });
                }
            };
        }
    },
    DRAGON_MACE(25, 1.45, 0.9, CombatType.MELEE) {
        @Override
        public void onActivation(Player player, Entity target) {}

        @Override
        public CombatContainer container(Player player, Entity target) {
            player.animation(new Animation(1060));
            player.highGraphic(new Graphic(251));

            return new CombatContainer(player, target, 1, CombatType.MELEE,
                    true);
        }
    },
    DRAGON_SCIMITAR(55, 1, 1, CombatType.MELEE) {
        @Override
        public void onActivation(Player player, Entity target) {}

        @Override
        public CombatContainer container(Player player, Entity target) {
            player.animation(new Animation(1872));
            player.highGraphic(new Graphic(347));

            return new CombatContainer(player, target, 1, CombatType.MELEE,
                    true);
        }
    },
    DRAGON_2H_SWORD(60, 1, 1, CombatType.MELEE) {
        @Override
        public void onActivation(Player player, Entity target) {}

        @SuppressWarnings("null")
        @Override
        public CombatContainer container(final Player player,
                final Entity target) {
            player.animation(new Animation(3157));
            player.graphic(new Graphic(559));

            return new CombatContainer(player, target, 1, CombatType.MELEE,
                    false) {
                @Override
                public void onHit(int damage, boolean accurate) {
                    if (Location.inMultiCombat(player)) {
                        Set<? extends Entity> localEntities = null;
                        if (target.type() == EntityType.PLAYER) {
                            localEntities = player.getLocalPlayers();
                        } else if (target.type() == EntityType.NPC) {
                            localEntities = player.getLocalNpcs();
                        }

                        for (Entity e : localEntities) {
                            if (e == null) {
                                continue;
                            }

                            if (e.getPosition().withinDistance(
                                    target.getPosition(), 1) && !e
                                    .equals(target) && !e.equals(player) && e
                                    .getCurrentHealth() > 0 && !e.isDead()) {
                                Hit hit = CombatFactory.getHit(player, target,
                                        CombatType.MELEE);
                                e.dealDamage(hit);
                                e.getCombatBuilder().addDamage(player,
                                        hit.getDamage());
                            }
                        }
                    }
                }
            };
        }
    },
    DRAGON_HALBERD(30, 1.1, 1, CombatType.MELEE) {
        @Override
        public void onActivation(Player player, Entity target) {}

        @Override
        public CombatContainer container(Player player, Entity target) {
            player.animation(new Animation(1203));
            player.highGraphic(new Graphic(282));

            return new CombatContainer(player, target, 2, CombatType.MELEE,
                    true);
        }
    };

    /** The amount of special energy this attack will drain. */
    private int drainAmount;

    /** The strength bonus when performing this special attack. */
    private double strengthBonus;

    /** The accuracy bonus when performing this special attack. */
    private double accuracyBonus;

    /** The combat type used when performing this special attack. */
    private CombatType combatType;

    /**
     * Create a new {@link CombatSpecial}.
     * 
     * @param drainAmount
     *            the amount of special energy this attack will drain.
     * @param strengthBonus
     *            the strength bonus when performing this special attack.
     * @param accuracyBonus
     *            the accuracy bonus when performing this special attack.
     * @param combatType
     *            the combat type used when performing this special attack.
     */
    private CombatSpecial(int drainAmount, double strengthBonus,
            double accuracyBonus, CombatType combatType) {
        this.drainAmount = drainAmount;
        this.strengthBonus = strengthBonus;
        this.accuracyBonus = accuracyBonus;
        this.combatType = combatType;
    }

    /**
     * Fired when the argued {@link Player} activates the special attack bar.
     * 
     * @param player
     *            the player activating the special attack bar.
     * @param target
     *            the target when activating the special attack bar, will be
     *            <code>null</code> if the player is not in combat while
     *            activating the special bar.
     */
    public abstract void onActivation(Player player, Entity target);

    /**
     * Fired when the argued {@link Player} is about to attack the argued
     * target.
     * 
     * @param player
     *            the player about to attack the target.
     * @param target
     *            the entity being attacked by the player.
     * @return the combat container for this combat hook.
     */
    public abstract CombatContainer container(Player player, Entity target);

    /**
     * Drains the special bar for the argued {@link Player}.
     * 
     * @param player
     *            the player who's special bar will be drained.
     * @param amount
     *            the amount of energy to drain from the special bar.
     */
    public static void drain(Player player, int amount) {
        player.decrementSpecialPercentage(amount);
        CombatSpecial.updateSpecialAmount(player);
        player.getPacketBuilder().sendConfig(301, 0);
        player.setSpecialActivated(false);
    }

    /**
     * Restores the special bar for the argued {@link Player}.
     * 
     * @param player
     *            the player who's special bar will be restored.
     * @param amount
     *            the amount of energy to restore to the special bar.
     */
    public static void restore(Player player, int amount) {
        player.incrementSpecialPercentage(amount);
        CombatSpecial.updateSpecialAmount(player);
    }

    /**
     * Updates the special bar with the amount of special energy the argued
     * {@link Player} has.
     * 
     * @param player
     *            the player who's special bar will be updated.
     */
    public static void updateSpecialAmount(Player player) {
        if (player.getWeapon().getSpecialBar() == -1 || player.getWeapon()
                .getSpecialMeter() == -1) {
            return;
        }

        int specialCheck = 10;
        int specialBar = player.getWeapon().getSpecialMeter();
        int specialAmount = player.getSpecialPercentage() / 10;

        for (int i = 0; i < 10; i++) {
            player.getPacketBuilder().updateSpecialBar(
                    specialAmount >= specialCheck ? 500 : 0, --specialBar);
            specialCheck--;
        }
    }

    /**
     * Gets the amount of special energy this attack will drain.
     * 
     * @return the amount of special energy this attack will drain.
     */
    public int getDrainAmount() {
        return drainAmount;
    }

    /**
     * Gets the strength bonus when performing this special attack.
     * 
     * @return the strength bonus when performing this special attack.
     */
    public double getStrengthBonus() {
        return strengthBonus;
    }

    /**
     * Gets the accuracy bonus when performing this special attack.
     * 
     * @return the accuracy bonus when performing this special attack.
     */
    public double getAccuracyBonus() {
        return accuracyBonus;
    }

    /**
     * Gets the combat type used when performing this special attack.
     * 
     * @return the combat type used when performing this special attack.
     */
    public CombatType getCombatType() {
        return combatType;
    }
}
