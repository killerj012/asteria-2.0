package server.world.entity.combat;

import server.world.entity.Hit;

/**
 * A set of hits that will be dealt during a single combat turn.
 * 
 * @author lare96
 */
public class CombatHit {

    /** The hits that will be dealt during this combat turn. */
    private Hit[] hits;

    /** The combat style that is being used during this combat turn. */
    private CombatType hitType;

    /**
     * Create a new {@link CombatHit}.
     * 
     * @param hits
     *        the hits that will be dealt during this combat turn.
     * @param hitType
     *        the combat style that is being used during this combat turn.
     */
    public CombatHit(Hit[] hits, CombatType hitType) {
        if (hits.length > 4) {
            throw new IllegalArgumentException("Illegal number of hits! The maximum number of hits per turn is 4.");
        }

        this.hits = hits;
        this.hitType = hitType;
    }

    /**
     * Gets the hits that will be dealt during this combat turn.
     * 
     * @return the hits that will be dealt during this combat turn.
     */
    public Hit[] getHits() {
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
}
