package server.world.entity.combat;

import java.util.Arrays;
import java.util.List;

import server.util.Misc;
import server.world.entity.Entity;
import server.world.entity.combat.prayer.CombatPrayer;
import server.world.entity.combat.strategy.DefaultMagicCombatStrategy;
import server.world.entity.combat.strategy.DefaultMeleeCombatStrategy;
import server.world.entity.combat.strategy.DefaultRangedCombatStrategy;
import server.world.entity.npc.Npc;
import server.world.entity.player.Player;

/**
 * A class containing static factory fields and methods used for combat.
 * 
 * @author lare96
 */
public class CombatFactory {

    /** A list of weapons used for ranging. */
    public static final List<Integer> RANGE_WEAPONS = Arrays.asList();

    private CombatFactory() {
    }

    // TODO: poisoning and retreating

    public static double calculateMaxMeleeHit(Entity entity) {
        if (entity.isPlayer()) {
            Player player = (Player) entity;
            double strengthLevel = player.getSkills()[Misc.STRENGTH].getLevel();

            if (CombatPrayer.isPrayerActivated(player, CombatPrayer.BURST_OF_STRENGTH)) {
                strengthLevel *= 1.05;
            } else if (CombatPrayer.isPrayerActivated(player, CombatPrayer.SUPERHUMAN_STRENGTH)) {
                strengthLevel *= 1.1;
            } else if (CombatPrayer.isPrayerActivated(player, CombatPrayer.ULTIMATE_STRENGTH)) {
                strengthLevel *= 1.15;
            }

            int fightStyle = player.getFightType().getBonusType();
            int styleBonus = 0;

            if (fightStyle == Misc.ATTACK_CRUSH || fightStyle == Misc.ATTACK_SLASH)
                styleBonus = 3;
            else if (fightStyle == Misc.ATTACK_STAB)
                styleBonus = 1;

            int effectiveStrengthDamage = (int) (strengthLevel + styleBonus);
            double baseDamage = 5 + (effectiveStrengthDamage + 8) * (player.getPlayerBonus()[10] + 64) / 64;
            int maxHit = (int) Math.floor(baseDamage);

            return (int) Math.floor(maxHit / 10);
        } else if (entity.isNpc()) {
            Npc npc = (Npc) entity;
            return npc.getDefinition().getMaxHit();
        }
        return 0;
    }

    // private static double calculateMeleeAttack(Entity entity) {
    // double attackBonus =
    // attacker.getBonus(attackStyle.getBonus().toInteger());
    // double baseAttack =
    // attacker.getBaseAttackLevel(attackStyle.getAttackType());
    // if (attackStyle.getAttackType() == AttackType.MELEE &&
    // attacker.isPlayer()) {
    // Player player = (Player) attacker;
    // if (CombatPrayer.isPrayerActivated(player,
    // CombatPrayer.CLARITY_OF_THOUGHT)) {
    // baseAttack *= 1.05;
    // } else if (CombatPrayer.isPrayerActivated(player,
    // CombatPrayer.IMPROVED_REFLEXES)) {
    // baseAttack *= 1.1;
    // } else if (CombatPrayer.isPrayerActivated(player,
    // CombatPrayer.INCREDIBLE_REFLEXES)) {
    // baseAttack *= 1.15;
    // }
    // }
    // return Math.floor(baseAttack + attackBonus) + 8;
    // }

    public static double getChance(double attack, double defence) {
        double A = Math.floor(attack);
        double D = Math.floor(defence);
        double chance = A < D ? (A - 1.0) / (2.0 * D) : 1.0 - (D + 1.0) / (2.0 * A);
        chance = chance > 0.9999 ? 0.9999 : chance < 0.0001 ? 0.0001 : chance;
        return chance;
    }

    /**
     * Calculates the combat level difference for wilderness player vs. player
     * combat.
     * 
     * @param combatLevel
     *        the combat level of the first person.
     * @param otherCombatLevel
     *        the combat level of the other person.
     * @return the combat level difference.
     */
    public static int calculateCombatDifference(int combatLevel, int otherCombatLevel) {
        if (combatLevel > otherCombatLevel) {
            return (combatLevel - otherCombatLevel);
        } else if (otherCombatLevel > combatLevel) {
            return (otherCombatLevel - combatLevel);
        } else {
            return 0;
        }
    }

    public static CombatStrategy newDefaultMeleeStrategy() {
        return new DefaultMeleeCombatStrategy();
    }

    public static CombatStrategy newDefaultMagicStrategy() {
        return new DefaultMagicCombatStrategy();
    }

    public static CombatStrategy newDefaultRangedStrategy() {
        return new DefaultRangedCombatStrategy();
    }
}
