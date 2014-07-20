package com.asteria.world.entity.combat;

import com.asteria.util.GenericAction;
import com.asteria.world.entity.Entity;
import com.asteria.world.entity.Entity.EntityType;
import com.asteria.world.entity.Hit;
import com.asteria.world.entity.combat.CombatFactory.CombatType;
import com.asteria.world.entity.player.Player;

/**
 * A container that holds all of the data needed for a single combat hook.
 * 
 * @author lare96
 */
public class CombatContainer {

    /** The attacker in this combat hook. */
    private Entity attacker;

    /** The victim in this combat hook. */
    private Entity victim;

    /** The hits that will be dealt during this combat hook. */
    private CombatHit[] hits;

    /** The skills that will be given experience. */
    private int[] experience;

    /** The combat type that is being used during this combat hook. */
    private CombatType combatType;

    /** If accuracy should be taken into account. */
    private boolean checkAccuracy;

    /** If at least one hit in this container is accurate. */
    private boolean accurate;

    /**
     * Create a new {@link CombatContainer}.
     * 
     * @param attacker
     *            the attacker in this combat hook.
     * @param victim
     *            the victim in this combat hook.
     * @param hitAmount
     *            the amount of hits to deal this combat hook.
     * @param hitType
     *            the combat type that is being used during this combat hook
     * @param checkAccuracy
     *            if accuracy should be taken into account.
     */
    public CombatContainer(Entity attacker, Entity victim, int hitAmount,
            CombatType hitType, boolean checkAccuracy) {
        this.attacker = attacker;
        this.victim = victim;
        this.combatType = hitType;
        this.checkAccuracy = checkAccuracy;
        this.hits = prepareHits(hitAmount);
        this.experience = getSkills();
    }

    /**
     * Create a new {@link CombatContainer} that will deal no hit this turn.
     * Used for things like spells that have special effects but don't deal
     * damage.
     * 
     * @param checkAccuracy
     *            if accuracy should be taken into account.
     */
    public CombatContainer(Entity attacker, Entity victim, CombatType hitType,
            boolean checkAccuracy) {
        this(attacker, victim, 0, hitType, checkAccuracy);
    }

    /**
     * Prepares the hits that will be dealt this combat hook.
     * 
     * @param hitAmount
     *            the amount of hits to deal, maximum 4 and minimum 0.
     * @return the hits that will be dealt this combat hook.
     */
    private final CombatHit[] prepareHits(int hitAmount) {

        // Check the hit amounts.
        if (hitAmount > 4) {
            throw new IllegalArgumentException(
                    "Illegal number of hits! The maximum number of hits per turn is 4.");
        } else if (hitAmount < 0) {
            throw new IllegalArgumentException(
                    "Illegal number of hits! The minimum number of hits per turn is 0.");
        }

        // No hit for this turn, but we still need to calculate accuracy.
        if (hitAmount == 0) {
            accurate = checkAccuracy ? CombatFactory.rollAccuracy(attacker,
                    victim, combatType) : true;
            return new CombatHit[] {};
        }

        // Create the new array of hits, and populate it. Here we do the maximum
        // hit and accuracy calculations.
        CombatHit[] array = new CombatHit[hitAmount];

        for (int i = 0; i < array.length; i++) {
            boolean accuracy = checkAccuracy ? CombatFactory.rollAccuracy(
                    attacker, victim, combatType) : true;
            array[i] = new CombatHit(CombatFactory.getHit(attacker, victim,
                    combatType),
                    accuracy);
            if (array[i].isAccurate()) {
                accurate = true;
            }
        }
        return array;
    }

    /**
     * Performs an action on every single hit in this container.
     * 
     * @param action
     *            the action to perform on every single hit.
     */
    protected final void allHits(GenericAction<CombatHit> action) {
        for (CombatHit hit : hits) {
            if (hit == null)
                continue;
            action.run(hit);
        }
    }

    /**
     * Deals all damage in this container to the victim.
     * 
     * @return the total amount of damage dealt.
     */
    public final int dealDamage() {
        int damage = 0;

        // Reduce all inaccurate hits to 0 and increment the damage counter.
        for (CombatHit hit : hits) {
            if (hit == null)
                continue;
            if (!hit.accurate) {
                hit.hit = new Hit(0);
            }
            damage += hit.hit.getDamage();
        }

        // Deal the damage to the victim.
        if (hits.length == 1) {
            victim.dealDamage(hits[0].getHit());
        } else if (hits.length == 2) {
            victim.dealDoubleDamage(hits[0].getHit(), hits[1].getHit());
        } else if (hits.length == 3) {
            victim.dealTripleDamage(hits[0].getHit(), hits[1].getHit(),
                    hits[2].getHit());
        } else if (hits.length == 4) {
            victim.dealQuadrupleDamage(hits[0].getHit(), hits[1].getHit(),
                    hits[2].getHit(), hits[3].getHit());
        }

        // Return the damage counter.
        return damage;
    }

    /**
     * Gets all of the skills that will be trained.
     * 
     * @return an array of skills that this attack will train.
     */
    private final int[] getSkills() {
        if (attacker.type() == EntityType.NPC) {
            return new int[] {};
        }
        return ((Player) attacker).getFightType().getTrainType();
    }

    /**
     * Used to do any last second modifications to the container before hits are
     * dealt to the player. An example of usage is using this to weaken
     * dragonfire depending on if the victim has dragonfire immunity or not.
     * 
     * @return the modified combat container.
     */
    public CombatContainer containerModify() {
        return this;
    }

    /**
     * A dynamic method invoked when the victim is hit with an attack. An
     * example of usage is using this to do some sort of special effect when the
     * victim is hit with a spell. <b>Do not reset combat builder in this
     * method!</b>
     * 
     * @param totalDamage
     *            the damage inflicted with this attack, always 0 if the attack
     *            isn't accurate.
     * @param accurate
     *            if the attack is accurate.
     */
    public void onHit(int damage, boolean accurate) {}

    /**
     * Gets the hits that will be dealt during this combat hook.
     * 
     * @return the hits that will be dealt during this combat hook.
     */
    public final CombatHit[] getHits() {
        return hits;
    }

    /**
     * Gets the skills that will be given experience.
     * 
     * @return the skills that will be given experience.
     */
    public final int[] getExperience() {
        return experience;
    }

    /**
     * Sets the amount of hits that will be dealt during this combat hook.
     * 
     * @param hitAmount
     *            the amount of hits that will be dealt during this combat hook.
     */
    public final void setHitAmount(int hitAmount) {
        this.hits = prepareHits(hitAmount);
    }

    /**
     * Gets the combat type that is being used during this combat hook.
     * 
     * @return the combat type that is being used during this combat hook.
     */
    public final CombatType getCombatType() {
        return combatType;
    }

    /**
     * Sets the combat type that is being used during this combat hook.
     * 
     * @param combatType
     *            the combat type that is being used during this combat hook.
     */
    public final void setCombatType(CombatType combatType) {
        this.combatType = combatType;
    }

    /**
     * Gets if accuracy should be taken into account.
     * 
     * @return true if accuracy should be taken into account.
     */
    public final boolean isCheckAccuracy() {
        return checkAccuracy;
    }

    /**
     * Sets if accuracy should be taken into account.
     * 
     * @param checkAccuracy
     *            true if accuracy should be taken into account.
     */
    public final void setCheckAccuracy(boolean checkAccuracy) {
        this.checkAccuracy = checkAccuracy;
    }

    /**
     * Gets if at least one hit in this container is accurate.
     * 
     * @return true if at least one hit in this container is accurate.
     */
    public final boolean isAccurate() {
        return accurate;
    }

    /**
     * A single hit that is dealt during a combat hook.
     * 
     * @author lare96
     */
    public static class CombatHit {

        /** The actual hit that will be dealt. */
        private Hit hit;

        /** The accuracy of the hit to be dealt. */
        private boolean accurate;

        /**
         * Create a new {@link CombatHit}.
         * 
         * @param hit
         *            the actual hit that will be dealt.
         * @param accurate
         *            the accuracy of the hit to be dealt.
         */
        public CombatHit(Hit hit, boolean accurate) {
            this.hit = hit;
            this.accurate = accurate;
        }

        /**
         * Gets the actual hit that will be dealt.
         * 
         * @return the actual hit that will be dealt.
         */
        public Hit getHit() {
            return hit;
        }

        /**
         * Sets the actual hit that will be dealt.
         * 
         * @param hit
         *            the actual hit that will be dealt.
         */
        public void setHit(Hit hit) {
            this.hit = hit;
        }

        /**
         * Gets the accuracy of the hit to be dealt.
         * 
         * @return the accuracy of the hit to be dealt.
         */
        public boolean isAccurate() {
            return accurate;
        }

        /**
         * Sets the accuracy of the hit to be dealt.
         * 
         * @param accurate
         *            the accuracy of the hit to be dealt.
         */
        public void setAccurate(boolean accurate) {
            this.accurate = accurate;
        }
    }
}
