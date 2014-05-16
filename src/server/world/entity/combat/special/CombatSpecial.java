package server.world.entity.combat.special;

import server.core.worker.WorkRate;
import server.util.Misc;
import server.world.World;
import server.world.entity.Animation;
import server.world.entity.Entity;
import server.world.entity.Gfx;
import server.world.entity.Hit;
import server.world.entity.Projectile;
import server.world.entity.combat.CombatFactory;
import server.world.entity.combat.CombatHitContainer;
import server.world.entity.combat.CombatType;
import server.world.entity.npc.Npc;
import server.world.entity.player.Player;
import server.world.entity.player.skill.SkillManager;
import server.world.entity.player.skill.SkillManager.SkillConstant;
import server.world.map.Location;

/**
 * Holds data for all of the special attacks that can be used in game.
 * 
 * @author lare96
 */
public enum CombatSpecial {

    DRAGON_DAGGER(25, 1.15, 1.25, new CombatSpecialStrategy() {
        @Override
        public CombatType combatType() {
            return CombatType.MELEE;
        }

        @Override
        public void onActivation(Player player, Entity target) {
        }

        @Override
        public CombatHitContainer calculateHit(Player player, Entity target) {
            player.animation(new Animation(1062));
            player.gfx(new Gfx(252, 6553600));
            CombatSpecial.drainAndDeplete(player, CombatSpecial.DRAGON_DAGGER.getSpecialAmount());

            return new CombatHitContainer(new Hit[] { CombatFactory.getMeleeHit(player), CombatFactory.getMeleeHit(player) }, this.combatType(), true);
        }
    }),
    GRANITE_MAUL(50, 1.5, 1, new CombatSpecialStrategy() {
        @Override
        public CombatType combatType() {
            return CombatType.MELEE;
        }

        @Override
        public void onActivation(Player player, Entity target) {
        }

        @Override
        public CombatHitContainer calculateHit(Player player, Entity target) {
            player.animation(new Animation(1667));
            player.gfx(new Gfx(337, 6553600));
            CombatSpecial.drainAndDeplete(player, CombatSpecial.GRANITE_MAUL.getSpecialAmount());

            return new CombatHitContainer(new Hit[] { CombatFactory.getMeleeHit(player), CombatFactory.getMeleeHit(player) }, this.combatType(), true);
        }
    }),
    ABYSSAL_WHIP(50, 1, 1, new CombatSpecialStrategy() {
        @Override
        public CombatType combatType() {
            return CombatType.MELEE;
        }

        @Override
        public void onActivation(Player player, Entity target) {
        }

        @Override
        public CombatHitContainer calculateHit(Player player, Entity target) {
            player.animation(new Animation(1658));
            target.gfx(new Gfx(341, 6553600));
            CombatSpecial.drainAndDeplete(player, CombatSpecial.ABYSSAL_WHIP.getSpecialAmount());

            return new CombatHitContainer(new Hit[] { CombatFactory.getMeleeHit(player) }, this.combatType(), false);
        }

    }),
    DRAGON_LONGSWORD(25, 1.25, 1.25, new CombatSpecialStrategy() {
        @Override
        public CombatType combatType() {
            return CombatType.MELEE;
        }

        @Override
        public void onActivation(Player player, Entity target) {
        }

        @Override
        public CombatHitContainer calculateHit(Player player, Entity target) {
            player.animation(new Animation(1058));
            player.gfx(new Gfx(248, 6553600));
            CombatSpecial.drainAndDeplete(player, CombatSpecial.DRAGON_LONGSWORD.getSpecialAmount());

            return new CombatHitContainer(new Hit[] { CombatFactory.getMeleeHit(player) }, this.combatType(), true);
        }

    }),
    MAGIC_SHORTBOW(50, 1, 1.1, new CombatSpecialStrategy() {
        @Override
        public CombatType combatType() {
            return CombatType.RANGE;
        }

        @Override
        public void onActivation(Player player, Entity target) {
        }

        @Override
        public CombatHitContainer calculateHit(Player player, Entity target) {
            player.gfx(new Gfx(250, 6553600));
            CombatSpecial.drainAndDeplete(player, CombatSpecial.MAGIC_SHORTBOW.getSpecialAmount());
            new Projectile(player, target, 249, 44, 3, 43, 31, 0).sendProjectile();

            return new CombatHitContainer(new Hit[] { CombatFactory.getRangeHit(player, player.getRangedAmmo()), CombatFactory.getRangeHit(player, player.getRangedAmmo()) }, this.combatType(), true);
        }

    }),
    MAGIC_LONGBOW(35, 1, 5, new CombatSpecialStrategy() {
        @Override
        public CombatType combatType() {
            return CombatType.RANGE;
        }

        @Override
        public void onActivation(Player player, Entity target) {
        }

        @Override
        public CombatHitContainer calculateHit(Player player, Entity target) {
            player.animation(new Animation(426));
            player.gfx(new Gfx(250, 6553600));
            new Projectile(player, target, 249, 44, 3, 43, 31, 0).sendProjectile();
            CombatSpecial.drainAndDeplete(player, CombatSpecial.MAGIC_LONGBOW.getSpecialAmount());

            return new CombatHitContainer(new Hit[] { CombatFactory.getMeleeHit(player) }, this.combatType(), true);
        }
    }),
    DRAGON_BATTLEAXE(100, 1, 1, new CombatSpecialStrategy() {
        @Override
        public CombatType combatType() {
            return null;
        }

        @Override
        public void onActivation(Player player, Entity target) {
            player.gfx(new Gfx(246));
            player.animation(new Animation(1056));
            player.forceChat("Raarrrrrgggggghhhhhhh!");
            player.getSkills()[Misc.STRENGTH].increaseLevel((int) (player.getSkills()[Misc.STRENGTH].getLevel() * 0.2));
            player.getSkills()[Misc.ATTACK].decreaseLevel((int) (player.getSkills()[Misc.ATTACK].getLevel() * 0.1));
            player.getSkills()[Misc.DEFENCE].decreaseLevel((int) (player.getSkills()[Misc.DEFENCE].getLevel() * 0.1));
            player.getSkills()[Misc.RANGED].decreaseLevel((int) (player.getSkills()[Misc.RANGED].getLevel() * 0.1));
            player.getSkills()[Misc.MAGIC].decreaseLevel((int) (player.getSkills()[Misc.MAGIC].getLevel() * 0.1));
            SkillManager.refresh(player, SkillConstant.STRENGTH);
            SkillManager.refresh(player, SkillConstant.ATTACK);
            SkillManager.refresh(player, SkillConstant.DEFENCE);
            SkillManager.refresh(player, SkillConstant.RANGED);
            SkillManager.refresh(player, SkillConstant.MAGIC);
            CombatSpecial.drainAndDeplete(player, CombatSpecial.DRAGON_BATTLEAXE.getSpecialAmount());
        }

        @Override
        public CombatHitContainer calculateHit(Player player, Entity target) {
            return null;
        }

    }),
    DRAGON_SPEAR(25, 1, 1, new CombatSpecialStrategy() {
        @Override
        public CombatType combatType() {
            return CombatType.MELEE;
        }

        @Override
        public void onActivation(Player player, Entity target) {
        }

        @Override
        public CombatHitContainer calculateHit(Player player, final Entity target) {
            player.animation(new Animation(1064));
            player.gfx(new Gfx(253));
            CombatSpecial.drainAndDeplete(player, CombatSpecial.DRAGON_SPEAR.getSpecialAmount());
            target.getMovementQueue().walk(Misc.DIRECTION_DELTA_X[player.getLastDirection()], Misc.DIRECTION_DELTA_Y[player.getLastDirection()]);
            target.gfx(new Gfx(80));

            target.getMovementQueueListener().submit(new Runnable() {
                @Override
                public void run() {
                    target.getMovementQueue().lockMovementFor(10, WorkRate.DEFAULT);
                }
            });

            return new CombatHitContainer(new Hit[] { CombatFactory.getMeleeHit(player) }, this.combatType(), true);
        }
    }),
    DRAGON_MACE(25, 1.45, 0.9, new CombatSpecialStrategy() {
        @Override
        public CombatType combatType() {
            return CombatType.MELEE;
        }

        @Override
        public void onActivation(Player player, Entity target) {

        }

        @Override
        public CombatHitContainer calculateHit(Player player, Entity target) {
            player.animation(new Animation(1060));
            player.gfx(new Gfx(251, 6553600));
            CombatSpecial.drainAndDeplete(player, CombatSpecial.DRAGON_MACE.getSpecialAmount());
            return new CombatHitContainer(new Hit[] { CombatFactory.getMeleeHit(player) }, this.combatType(), true);
        }
    }),
    DRAGON_SCIMITAR(55, 1, 1, new CombatSpecialStrategy() {
        @Override
        public CombatType combatType() {
            return CombatType.MELEE;
        }

        @Override
        public void onActivation(Player player, Entity target) {
        }

        @Override
        public CombatHitContainer calculateHit(Player player, Entity target) {
            player.animation(new Animation(1872));
            player.gfx(new Gfx(347));
            CombatSpecial.drainAndDeplete(player, CombatSpecial.DRAGON_SCIMITAR.getSpecialAmount());
            return new CombatHitContainer(new Hit[] { CombatFactory.getMeleeHit(player) }, this.combatType(), true);
        }
    }),
    DRAGON_2H_SWORD(60, 1, 1, new CombatSpecialStrategy() {
        @Override
        public CombatType combatType() {
            return CombatType.MELEE;
        }

        @Override
        public void onActivation(Player player, Entity target) {

        }

        @Override
        public CombatHitContainer calculateHit(Player player, Entity target) {
            player.animation(new Animation(3157));
            player.gfx(new Gfx(559));
            CombatSpecial.drainAndDeplete(player, CombatSpecial.DRAGON_2H_SWORD.getSpecialAmount());

            if (Location.inMultiCombat(player)) {
                if (target.isPlayer()) {
                    for (Player players : World.getPlayers()) {
                        if (players == null) {
                            continue;
                        }

                        if (players.getPosition().withinDistance(target.getPosition(), 1) && players.getSlot() != target.getSlot() && players.getSlot() != player.getSlot()) {
                            int damage = CombatFactory.getMeleeHit(player).getDamage();
                            players.dealDamage(new Hit(damage));
                            players.getCombatBuilder().addDamage(player, damage);
                        }
                    }
                } else if (target.isNpc()) {
                    for (Npc npc : World.getNpcs()) {
                        if (npc == null) {
                            continue;
                        }

                        if (npc.getPosition().withinDistance(target.getPosition(), 1) && npc.getSlot() != target.getSlot() && npc.getDefinition().isAttackable()) {
                            int damage = CombatFactory.getMeleeHit(player).getDamage();
                            npc.dealDamage(new Hit(damage));
                            npc.getCombatBuilder().addDamage(player, damage);
                        }
                    }
                }
            }
            return new CombatHitContainer(new Hit[] { CombatFactory.getMeleeHit(player) }, this.combatType(), false);
        }
    }),
    DRAGON_HALBERD(30, 1.1, 1, new CombatSpecialStrategy() {
        @Override
        public CombatType combatType() {
            return CombatType.MELEE;
        }

        @Override
        public void onActivation(Player player, Entity target) {

        }

        @Override
        public CombatHitContainer calculateHit(Player player, Entity target) {
            player.animation(new Animation(1203));
            player.gfx(new Gfx(282));
            CombatSpecial.drainAndDeplete(player, CombatSpecial.DRAGON_HALBERD.getSpecialAmount());
            return new CombatHitContainer(new Hit[] { CombatFactory.getMeleeHit(player), CombatFactory.getMeleeHit(player) }, this.combatType(), true);
        }
    });

    /** The amount of special energy this attack requires. */
    private int specialAmount;

    /** The strength bonus when performing this special attack. */
    private double strengthBonus;

    /** The accuracy bonus when performing this special attack. */
    private double accuracyBonus;

    /** The strategy used when performing this special attack. */
    private CombatSpecialStrategy specialStrategy;

    /**
     * Create a new {@link CombatSpecial}.
     * 
     * @param specialAmount
     *        the amount of special energy this attack requires.
     * @param strengthBonus
     *        the strength bonus when performing this special attack.
     * @param accuracyBonus
     *        the accuracy bonus when performing this special attack.
     * @param specialStrategy
     *        the strategy used when performing this special attack.
     */
    private CombatSpecial(int specialAmount, double strengthBonus, double accuracyBonus, CombatSpecialStrategy specialStrategy) {
        this.specialAmount = specialAmount;
        this.strengthBonus = strengthBonus;
        this.specialStrategy = specialStrategy;
    }

    /**
     * Drains the player's special bar.
     * 
     * @param player
     *        the player who's special bar will be drained.
     * @param drainAmount
     *        the amount of energy to drain.
     */
    public static void drainAndDeplete(Player player, int drainAmount) {
        player.decrementSpecialPercentage(drainAmount);
        CombatSpecial.updateSpecialAmount(player);
        player.getPacketBuilder().sendConfig(301, 0);
        player.setSpecialActivated(false);
    }

    /**
     * Boosts the player's special bar.
     * 
     * @param player
     *        the player who's special bar will be boosted.
     * @param boostAmount
     *        the amount of energy to boost.
     */
    public static void boostAndRestore(Player player, int boostAmount) {
        player.incrementSpecialPercentage(boostAmount);
        CombatSpecial.updateSpecialAmount(player);
    }

    /**
     * Updates the special bar with the amount of special you have left.
     * 
     * @param player
     *        the player who's special bar will be updated.
     */
    public static void updateSpecialAmount(Player player) {
        if (player.getWeapon().getSpecialBar() == -1 || player.getWeapon().getSpecialMeter() == -1) {
            return;
        }

        int specialCheck = 10;
        int specialBar = player.getWeapon().getSpecialMeter();
        byte specialAmount = (byte) (player.getSpecialPercentage() / 10);
        for (int i = 0; i < 10; i++) {
            player.getPacketBuilder().updateSpecialBar(specialAmount >= specialCheck ? 500 : 0, --specialBar);
            specialCheck--;
        }
    }

    /**
     * Gets the amount of special energy this attack requires.
     * 
     * @return the amount of special energy this attack requires.
     */
    public int getSpecialAmount() {
        return specialAmount;
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
     * Gets the strategy used when performing this special attack.
     * 
     * @return the strategy used when performing this special attack.
     */
    public CombatSpecialStrategy getSpecialStrategy() {
        return specialStrategy;
    }
}
