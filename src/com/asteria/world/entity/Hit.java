package com.asteria.world.entity;

/**
 * A class that represents a hit inflicted on an entity.
 * 
 * @author lare96
 */
public class Hit {

    /** The amount of damage inflicted in this hit. */
    private int damage;

    /** The hit type of this hit. */
    private HitType type;

    /**
     * The different types of hits that can be dealt.
     * 
     * @author lare96
     */
    public enum HitType {
        BLOCKED,
        NORMAL,
        POISON,
        DIESEASE
    }

    /**
     * Create a new {@link Hit}.
     * 
     * @param damage
     *            the amount of damage in this hit.
     * @param type
     *            the type of hit this is.
     */
    public Hit(int damage, HitType type) {
        this.damage = damage;
        this.type = type;
        this.modify();
    }

    /**
     * Create a new {@link Hit} with a default {@link HitType} of
     * <code>NORMAL</code>.
     * 
     * @param damage
     *            the amount of damage in this hit.
     */
    public Hit(int damage) {
        this(damage, HitType.NORMAL);
    }

    @Override
    public Hit clone() {
        return new Hit(damage, type);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Hit)) {
            return false;
        }

        Hit hit = (Hit) o;
        return (hit.damage == damage && hit.type == type);
    }

    /**
     * Modifies the <code>damage</code> and <code>type</code> fields so the hits
     * will look appropriate when displayed (things like not hitting a 15 with a
     * blue hitmark or 0 with a red hitmark).
     */
    private void modify() {
        if (this.damage == 0 && this.type == HitType.NORMAL) {
            this.type = HitType.BLOCKED;
        } else if (this.damage > 0 && this.type == HitType.BLOCKED) {
            this.damage = 0;
        } else if (this.damage < 0) {
            this.damage = 0;
        }
    }

    /**
     * Gets the amount of damage in this hit.
     * 
     * @return the amount of damage in this hit.
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Sets the amount of damage in this hit.
     * 
     * @param damage
     *            the amount of damage in this hit.
     */
    public void setDamage(int damage) {
        this.damage = damage;
        this.modify();
    }

    /**
     * Gets the type of hit.
     * 
     * @return the type of hit.
     */
    public HitType getType() {
        return type;
    }

    /**
     * Sets the type of hit.
     * 
     * @param type
     *            the type of hit.
     */
    public void setType(HitType type) {
        this.type = type;
        this.modify();
    }
}
