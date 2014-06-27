package server.world.entity.combat;

import server.world.entity.Entity;
import server.world.entity.Hit;

/**
 * A set of hits that will be dealt during a single combat turn.
 * 
 * @author lare96
 */
public abstract class CombatHitContainer {

    /** A static {@link CombatHitContianer} implementation that deals no damage. */
    public static final CombatHitContainer NO_DAMAGE = new CombatHitContainer(null, null, true) {
        @Override
        public void onHit(Entity attacker, Entity victim, int totalDamage, boolean accurate) {
        }
    };

    /** The hits that will be dealt during this combat turn. */
    private CombatHit[] hits;

    /** The combat style that is being used during this combat turn. */
    private CombatType hitType;

    /** If accuracy should be taken into account. */
    private boolean checkAccuracy;

    /**
     * Create a new {@link CombatHitContainer}.
     * 
     * @param hits
     *        the hits that will be dealt during this combat turn.
     * @param hitType
     *        the combat style that is being used during this combat turn.
     * @param checkAccuracy
     *        if accuracy should be taken into account.
     */
    public CombatHitContainer(Hit[] hitSet, CombatType hitType, boolean checkAccuracy) {
        if (hitSet != null) {
            if (hitSet.length > 4) {
                throw new IllegalArgumentException("Illegal number of hits! The maximum number of hits per turn is 4.");
            } else if (hitSet.length > 1
                    && hitType == CombatType.MAGIC) {
                throw new IllegalArgumentException("Illegal number of hits! The maximum number of hits per turn with magic is 1.");
            } else if (hitSet.length < 1) {
                throw new IllegalArgumentException("Illegal number of hits! The minimum number of hits per turn is 1.");
            }

            this.hits = new CombatHit[hitSet.length];

            for (int i = 0; i < hitSet.length; i++) {
                hits[i] = new CombatHit(hitSet[i], true);
            }
        }

        this.hitType = hitType;
        this.checkAccuracy = checkAccuracy;
    }

    /**
     * Fired when the entity gets hit.
     * 
     * @param attacker
     *        the person dealing the attack.
     * @param victim
     *        the person being dealt the attack.
     * @param totalDamage
     *        the damage inflicted with this attack, always 0 if the attack
     *        isn't accurate.
     * @param accurate
     *        if the attack actually hit the player or not.
     */
    public abstract void onHit(Entity attacker, Entity victim, int totalDamage, boolean accurate);

    /**
     * Gets the hits that will be dealt during this combat turn.
     * 
     * @return the hits that will be dealt during this combat turn.
     */
    public CombatHit[] getHits() {
        return hits;
    }

    /**
     * Gets the combat style that is being used during this combat turn.
     * 
     * @return the combat style that is being used during this combat turn.
     */
    public CombatType getHitType() {
        return hitType;
    }

    /**
     * Gets if accuracy should be taken into account.
     * 
     * @return true if accuracy should be taken into account.
     */
    public boolean isCheckAccuracy() {
        return checkAccuracy;
    }

    /**
     * A single hit that will be dealt during a combat turn.
     * 
     * @author lare96
     */
    public static class CombatHit {

        /** The hit that will be dealt. */
        private Hit hit;

        /** If the hit was successful or not. */
        private boolean successful;

        /**
         * Create a new {@link CombatHit}.
         * 
         * @param hit
         *        the hit that will be dealt.
         * @param successful
         *        if the hit was successful or not.
         */
        public CombatHit(Hit hit, boolean successful) {
            this.hit = hit;
            this.successful = successful;
        }

        /**
         * Gets the hit that will be dealt.
         * 
         * @return the hit that will be dealt.
         */
        public Hit getHit() {
            return hit;
        }

        /**
         * Sets the hit that will be dealt.
         * 
         * @param hit
         *        the hit that will be dealt.
         */
        public void setHit(Hit hit) {
            this.hit = hit;
        }

        /**
         * Gets if the hit was successful or not.
         * 
         * @return true if the hit was successful or not.
         */
        public boolean isSuccessful() {
            return successful;
        }

        /**
         * Sets if the hit was successful or not.
         * 
         * @param successful
         *        true if the hit was successful or not.
         */
        public void setSuccessful(boolean successful) {
            if (!successful) {
                this.hit = new Hit(0);
            }

            this.successful = successful;
        }
    }
}
