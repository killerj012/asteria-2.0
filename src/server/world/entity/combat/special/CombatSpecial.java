package server.world.entity.combat.special;

import server.util.Misc;
import server.world.entity.Animation;
import server.world.entity.Entity;
import server.world.entity.Gfx;
import server.world.entity.Hit;
import server.world.entity.Projectile;
import server.world.entity.combat.CombatFactory;
import server.world.entity.combat.CombatHit;
import server.world.entity.combat.CombatType;
import server.world.entity.player.Player;
import server.world.entity.player.skill.SkillManager;
import server.world.entity.player.skill.SkillManager.SkillConstant;

/**
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
        public CombatHit calculateHit(Player player, Entity target) {
            player.animation(new Animation(1062));
            player.gfx(new Gfx(252));
            CombatSpecial.drainAndDeplete(player, CombatSpecial.DRAGON_DAGGER.getSpecialAmount());

            if (CombatFactory.hitAccuracy(player, target, this.combatType(), 2)) {
                return new CombatHit(new Hit[] { CombatFactory.getMeleeHit(player), CombatFactory.getMeleeHit(player) }, this.combatType());
            }
            return null;
        }

        @Override
        public void onHit(Player player, Entity target) {
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
        public CombatHit calculateHit(Player player, Entity target) {
            player.gfx(new Gfx(337));
            player.animation(new Animation(1667));
            CombatSpecial.drainAndDeplete(player, CombatSpecial.GRANITE_MAUL.getSpecialAmount());

            if (CombatFactory.hitAccuracy(player, target, this.combatType(), 2)) {
                return new CombatHit(new Hit[] { CombatFactory.getMeleeHit(player), CombatFactory.getMeleeHit(player) }, this.combatType());
            }
            return null;
        }

        @Override
        public void onHit(Player player, Entity target) {
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
        public CombatHit calculateHit(Player player, Entity target) {
            player.animation(new Animation(1658));
            target.gfx(new Gfx(341));
            CombatSpecial.drainAndDeplete(player, CombatSpecial.ABYSSAL_WHIP.getSpecialAmount());

            return new CombatHit(new Hit[] { CombatFactory.getMeleeHit(player) }, this.combatType());
        }

        @Override
        public void onHit(Player player, Entity target) {
        }
    }),
    DRAGON_LONGSWORD(25, 1.25, 1.25, new CombatSpecialStrategy() {
        @Override
        public CombatType combatType() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void onActivation(Player player, Entity target) {
            // TODO Auto-generated method stub
        }

        @Override
        public CombatHit calculateHit(Player player, Entity target) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void onHit(Player player, Entity target) {
            // TODO Auto-generated method stub
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
        public CombatHit calculateHit(Player player, Entity target) {
            new Projectile(player, target, 249, 44, 3, 43, 31, 0).sendProjectile();
            new Projectile(player, target, 249, 44, 3, 43, 31, 0).sendProjectile();
            CombatSpecial.drainAndDeplete(player, CombatSpecial.MAGIC_SHORTBOW.getSpecialAmount());

            if (CombatFactory.hitAccuracy(player, target, this.combatType(), 2)) {
                return new CombatHit(new Hit[] { CombatFactory.getRangeHit(player, player.getRangedAmmo()), CombatFactory.getRangeHit(player, player.getRangedAmmo()) }, this.combatType());
            }
            return null;
        }

        @Override
        public void onHit(Player player, Entity target) {
        }
    }),
    MAGIC_LONGBOW(35, 1, 5, new CombatSpecialStrategy() {
        @Override
        public CombatType combatType() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void onActivation(Player player, Entity target) {
            // TODO Auto-generated method stub
        }

        @Override
        public CombatHit calculateHit(Player player, Entity target) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void onHit(Player player, Entity target) {
            // TODO Auto-generated method stub
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
        public CombatHit calculateHit(Player player, Entity target) {
            return null;
        }

        @Override
        public void onHit(Player player, Entity target) {
        }
    }),
    DRAGON_SPEAR(25, 1, 1, new CombatSpecialStrategy() {
        @Override
        public CombatType combatType() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void onActivation(Player player, Entity target) {
            // TODO Auto-generated method stub
        }

        @Override
        public CombatHit calculateHit(Player player, Entity target) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void onHit(Player player, Entity target) {
            // TODO Auto-generated method stub
        }
    }),
    DRAGON_MACE(25, 1.45, 0.9, new CombatSpecialStrategy() {
        @Override
        public CombatType combatType() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void onActivation(Player player, Entity target) {
            // TODO Auto-generated method stub
        }

        @Override
        public CombatHit calculateHit(Player player, Entity target) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void onHit(Player player, Entity target) {
            // TODO Auto-generated method stub
        }
    }),
    DRAGON_SCIMITAR(55, 1, 1, new CombatSpecialStrategy() {
        @Override
        public CombatType combatType() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void onActivation(Player player, Entity target) {
            // TODO Auto-generated method stub
        }

        @Override
        public CombatHit calculateHit(Player player, Entity target) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void onHit(Player player, Entity target) {
            // TODO Auto-generated method stub
        }
    }),
    DRAGON_2H_SWORD(60, 1, 1, new CombatSpecialStrategy() {
        @Override
        public CombatType combatType() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void onActivation(Player player, Entity target) {
            // TODO Auto-generated method stub
        }

        @Override
        public CombatHit calculateHit(Player player, Entity target) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void onHit(Player player, Entity target) {
            // TODO Auto-generated method stub
        }
    }),
    DRAGON_HALBERD(30, 1.1, 1, new CombatSpecialStrategy() {
        @Override
        public CombatType combatType() {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void onActivation(Player player, Entity target) {
            // TODO Auto-generated method stub
        }

        @Override
        public CombatHit calculateHit(Player player, Entity target) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public void onHit(Player player, Entity target) {
            // TODO Auto-generated method stub
        }
    });

    private int specialAmount;

    private double strengthBonus;
    private double accuracyBonus;
    private CombatSpecialStrategy specialStrategy;

    private CombatSpecial(int specialAmount, double strengthBonus, double accuracyBonus, CombatSpecialStrategy specialStrategy) {
        this.specialAmount = specialAmount;
        this.strengthBonus = strengthBonus;
        this.specialStrategy = specialStrategy;
    }

    public static void drainAndDeplete(Player player, int drainAmount) {
        player.decrementSpecialPercentage(drainAmount);
        CombatSpecial.updateSpecialAmount(player);
        player.getPacketBuilder().sendConfig(301, 0);
        player.setSpecialActivated(false);
    }

    public static void boostAndRestore(Player player, int boostAmount) {
        player.incrementSpecialPercentage(boostAmount);
        CombatSpecial.updateSpecialAmount(player);
    }

    /**
     * Updates the special bar with the amount of special you have left.
     * 
     * @param barId
     *        the special bar to perform this update on.
     */
    public static void updateSpecialAmount(Player player) {
        int specialCheck = 10;
        int specialBar = player.getWeapon().getSpecialMeter();
        byte specialAmount = (byte) (player.getSpecialPercentage() / 10);
        for (int i = 0; i < 10; i++) {
            player.getPacketBuilder().updateSpecialBar(specialAmount >= specialCheck ? 500 : 0, --specialBar);
            specialCheck--;
        }
    }

    /**
     * @return the specialAmount
     */
    public int getSpecialAmount() {
        return specialAmount;
    }

    /**
     * @return the strengthBonus
     */
    public double getStrengthBonus() {
        return strengthBonus;
    }

    /**
     * @return the accuracyBonus
     */
    public double getAccuracyBonus() {
        // TODO Auto-generated method stub
        return accuracyBonus;
    }

    /**
     * @return the specialStrategy
     */
    public CombatSpecialStrategy getSpecialStrategy() {
        return specialStrategy;
    }

}
