package server.world.entity;

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
     * The different types of hits that can be inflicted.
     * 
     * @author lare96
     */
    public enum HitType {
        BLOCKED(0), NORMAL(1), POISON(2), DIESEASE(3);

        /** The id of this hit type. */
        private int id;

        /**
         * Create a new {@link HitType}.
         * 
         * @param id
         *        the id of this hit type.
         */
        private HitType(int id) {
            this.id = id;
        }

        /**
         * Gets the id of this hit type.
         * 
         * @return the id.
         */
        public int getId() {
            return id;
        }
    }

    /**
     * Create a new {@link Hit} with a {@link HitType} of <code>NORMAL</code>.
     * 
     * @param damage
     *        the amount of damage in this hit.
     */
    public Hit(int damage) {
        this(damage, HitType.NORMAL);
    }

    /**
     * Create a new {@link Hit}.
     * 
     * @param damage
     *        the amount of damage in this hit.
     * @param type
     *        the type of hit this is.
     */
    public Hit(int damage, HitType type) {
        this.damage = damage;
        this.type = type;

        if (this.damage == 0 && this.type == HitType.NORMAL) {
            this.type = HitType.BLOCKED;
        } else if (this.damage > 0 && this.type == HitType.BLOCKED) {
            this.damage = 0;
        } else if (this.damage < 0) {
            this.damage = 0;
        }
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
     * Gets the amount of damage in this hit.
     * 
     * @return the damage.
     */
    public int getDamage() {
        return damage;
    }

    /**
     * Gets the type of hit.
     * 
     * @return the type.
     */
    public HitType getType() {
        return type;
    }
}
