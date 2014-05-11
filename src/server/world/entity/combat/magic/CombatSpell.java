package server.world.entity.combat.magic;

import server.core.worker.TaskFactory;
import server.core.worker.Worker;
import server.world.entity.Animation;
import server.world.entity.Entity;
import server.world.entity.Gfx;
import server.world.entity.Projectile;
import server.world.entity.Spell;

/**
 * A {@link Spell} implemenation primarily used for combat and fast action
 * spells.
 * 
 * @author lare96
 */
public abstract class CombatSpell extends Spell {

    @Override
    public void castSpell(final Entity cast, final Entity castOn) {

        /** First play the animation. */
        cast.animation(castAnimation());

        /** Then send the first graphic. */
        if (startGfx() != null) {
            cast.gfx(startGfx());
        }

        /** * Send the projectile after a delay. */
        if (castProjectile(cast, castOn) != null) {
            TaskFactory.getFactory().submit(new Worker(2, false) {
                @Override
                public void fire() {

                    /** Cancel the task if the target has died or unregistered. */
                    if (castOn.isHasDied() || castOn.isUnregistered()) {
                        this.cancel();
                        return;
                    }

                    /** Send out the projectile. */
                    castProjectile(cast, castOn).sendProjectile();

                    /** And stop the task. */
                    this.cancel();
                }
            });
        }
    }

    /**
     * Determines the maximum strength of this spell.
     * 
     * @return the maximum strength of this spell.
     */
    public abstract int maximumStrength();

    /**
     * The animation played when the spell is cast.
     * 
     * @return the animation played when the spell is cast.
     */
    public abstract Animation castAnimation();

    /**
     * The starting graphic for this spell.
     * 
     * @return the starting graphic for this spell.
     */
    public abstract Gfx startGfx();

    /**
     * The mid-cast projectile for this spell.
     * 
     * @param cast
     *        the person casting the spell.
     * @param castOn
     *        the target of the spell.
     * 
     * @return the projectile for this spell.
     */
    public abstract Projectile castProjectile(Entity cast, Entity castOn);

    /**
     * The ending graphic for this spell.
     * 
     * @return the ending graphic for this spell.
     */
    public abstract Gfx endGfx();

    /**
     * Invoked when the spell hits the target.
     * 
     * @param cast
     *        the person casting the spell.
     * @param castOn
     *        the target of the spell.
     * @param spellAccurate
     *        if the spell actually hit the player.
     */
    public abstract void endCast(Entity cast, Entity castOn, boolean spellAccurate);
}
