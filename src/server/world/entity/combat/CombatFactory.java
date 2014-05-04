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
    public static final List<Integer> RANGE_WEAPONS = Arrays.asList(2577, 589, 776, 775, 800, 801, 802, 803, 804, 805, 806, 807, 808, 809, 810, 811, 825, 826, 827, 828, 829, 830, 837, 839, 841, 843, 845, 847, 849, 851, 853, 855, 857, 859, 861, 863, 864, 865, 866, 867, 868, 869, 1095, 1097, 1099, 1135, 1133, 1131, 1129, 1169, 1167, 2581, 2576, 2995, 2487, 2489, 2491, 2493, 1495, 2497, 2499, 2501, 2503, 2505, 2507, 2509, 3105, 3107, 3749, 4732, 4734, 4736, 4738, 5553, 5554, 5555, 5556, 5557, 5558, 4212, 4214, 4215, 4216, 4217, 4218, 4219, 4220, 4221, 4222, 4223, 4224, 4225, 4226, 4227, 4228, 4229, 4230, 4231, 4232, 4233, 4234, 2631, 2633, 2635, 2637, 2639, 2641, 2643, 2645, 2647, 2649, 6733);

    /**
     * So this class cannot be instantiated.
     */
    private CombatFactory() {
    }

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
