package server.world.entity.combat.task;

import server.core.worker.Worker;
import server.world.entity.Entity;
import server.world.entity.Gfx;
import server.world.entity.Hit;
import server.world.entity.combat.CombatType;
import server.world.entity.player.Player;

/**
 * A {@link Worker} implementation that deals a series of inaccurate hits to an
 * entity after a delay.
 * 
 * @author lare96
 */
public class CombatAccuracyHitTask extends Worker {

    /** The attacker trying to hit the target. */
    private Entity attacker;

    /** The target that the hit missed. */
    private Entity target;

    /** The combat type being used. */
    private CombatType combatType;

    /** The amount of hits missed. */
    private int hitCount;

    /**
     * Create a new {@link CombatAccuracyHitTask}.
     * 
     * @param attacker
     *        the attacker trying to hit the target.
     * @param target
     *        the target that the hit missed.
     * @param combatType
     *        the combat type being used.
     * @param hitCount
     *        the amount of hits missed.
     * @param delay
     *        the delay in ticks before the hit will be dealt.
     * @param initialRun
     *        if the task should be ran right away.
     */
    public CombatAccuracyHitTask(Entity attacker, Entity target, CombatType combatType, int hitCount, int delay, boolean initialRun) {
        super(delay, initialRun);
        this.attacker = attacker;
        this.target = target;
        this.combatType = combatType;
        this.hitCount = hitCount;
    }

    @Override
    public void fire() {

        /** Stop the task if the target isn't registered or has died. */
        if (target.isHasDied() || target.isUnregistered()) {
            this.cancel();
            return;
        }

        /** Send the inaccurate hits based on the combat type. */
        switch (combatType) {
            case MELEE:
            case RANGE:
                if (hitCount == 0 || hitCount == 1) {
                    target.dealDamage(new Hit(0));
                } else if (hitCount == 2) {
                    target.dealDoubleDamage(new Hit(0), new Hit(0));
                } else if (hitCount == 3) {
                    target.dealTripleDamage(new Hit(0), new Hit(0), new Hit(0));
                } else if (hitCount == 4) {
                    target.dealQuadrupleDamage(new Hit(0), new Hit(0), new Hit(0), new Hit(0));
                }
                break;
            case MAGIC:
                target.gfx(new Gfx(85));
                attacker.getCurrentlyCasting().endCast(attacker, target, false);
                attacker.setCurrentlyCasting(null);

                if (attacker.isPlayer()) {
                    Player player = (Player) attacker;

                    if (!player.isAutocast()) {
                        player.setCastSpell(null);
                    }
                }
                break;
        }

        /** And stop the task afterward. */
        this.cancel();
    }
}
