package server.world.entity.combat;

import server.world.entity.Hit;

/**
 * A hit dealt during a combat turn.
 * 
 * @author lare96
 */
public class CombatHit {

    /** The hits within this combat hit. */
    private Hit[] hits;

    /** The combat style this hit is. */
    private CombatType hitType;

    /**
     * Create a new {@link CombatHit}.
     * 
     * @param hits
     *        the hits within this combat hit.
     * @param hitType
     *        the combat style this hit is.
     */
    public CombatHit(Hit[] hits, CombatType hitType) {
        if (hits.length > 4) {
            throw new IllegalArgumentException("Too many hits! The maximum number of hits per turn is 4.");
        }

        this.hits = hits;
        this.hitType = hitType;
    }

    /**
     * Gets the hits within this combat hit.
     * 
     * @return the hits within this combat hit.
     */
    public Hit[] getHits() {
        return hits;
    }

    /**
     * Gets the combat style this hit is.
     * 
     * @return the combat style this hit is.
     */
    public CombatType getHitType() {
        return hitType;
    }
}
