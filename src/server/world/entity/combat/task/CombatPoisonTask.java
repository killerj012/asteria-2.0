package server.world.entity.combat.task;

import server.core.worker.WorkRate;
import server.core.worker.Worker;
import server.util.Misc.Interval;
import server.world.entity.Entity;
import server.world.entity.Hit;
import server.world.entity.Hit.HitType;

/**
 * A {@link Worker} implementation that handles the poisoning process.
 * 
 * @author lare96
 */
public class CombatPoisonTask extends Worker {

    /** The entity being inflicted with poison. */
    private Entity entity;

    /**
     * Create a new {@link CombatPoisonTask}.
     * 
     * @param entity
     *        the entity being inflicted with poison.
     */
    public CombatPoisonTask(Entity entity) {
        super(10, false, WorkRate.APPROXIMATE_SECOND);
        this.entity = entity;
    }

    /**
     * Holds all of the different strengths of poisons.
     * 
     * @author lare96
     */
    public enum CombatPoison {
        MILD(new Interval().inclusiveInterval(1, 5), 50),
        STRONG(new Interval().inclusiveInterval(5, 10), 100),
        SEVERE(new Interval().inclusiveInterval(10, 15), 150);

        /** The damage range (inclusive). */
        private Interval damageRange;

        /** The amount of hits dealt. */
        private int hitAmount;

        /**
         * Create a new {@link CombatPoison}.
         * 
         * @param damageRange
         *        the damage range (inclusive).
         * @param hitAmount
         *        the amount of hits dealt.
         */
        private CombatPoison(Interval damageRange, int hitAmount) {
            this.damageRange = damageRange;
            this.hitAmount = hitAmount;
        }

        /**
         * Gets the damage range (inclusive).
         * 
         * @return the damage range (inclusive).
         */
        public Interval getDamageRange() {
            return damageRange;
        }

        /**
         * Gets the amount of hits dealt.
         * 
         * @return the amount of hits dealt.
         */
        public int getHitAmount() {
            return hitAmount;
        }
    }

    @Override
    public void fire() {

        /** Stop the task if needed. */
        if (entity.isUnregistered() || entity.getPoisonHits() == 0) {
            entity.setPoisonHits(0);
            entity.setPoisonStrength(CombatPoison.MILD);
            this.cancel();
            return;
        }

        /** Calculate the poison hit for this turn. */
        int calculateHit = entity.getPoisonStrength().getDamageRange().calculate();

        /**
         * If the damage is above your current health then don't deal any
         * damage.
         */
        if (calculateHit >= entity.getCurrentHealth()) {
            return;
        }

        /** Otherwise deal damage as normal. */
        entity.dealDamage(new Hit(calculateHit, HitType.POISON));
        entity.decrementPoisonHits();
    }
}
