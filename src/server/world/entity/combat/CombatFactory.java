package server.world.entity.combat;

import java.util.Arrays;
import java.util.List;

import server.util.Misc;
import server.world.entity.Entity;
import server.world.entity.Hit;
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

    // XXX: formulas... should start on prayer too
    public static Hit calculateHit(Entity entity, CombatType type) {
        if (entity instanceof Player) {
            Player player = (Player) entity;
            switch (type) {
                case MELEE:
                    return new Hit(Misc.getRandom().nextInt(10));
                case RANGE:
                    return new Hit(Misc.getRandom().nextInt(5));
                case MAGIC:
                    return new Hit(Misc.getRandom().nextInt(5));
            }
        } else if (entity instanceof Npc) {
            switch (type) {
                case MELEE:
                    return new Hit(Misc.getRandom().nextInt(2));
                case RANGE:
                    return new Hit(Misc.getRandom().nextInt(5));
                case MAGIC:
                    return new Hit(Misc.getRandom().nextInt(5));
            }
        }
        return new Hit(0);
    }

    private static void calculateMaxHit() {

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
