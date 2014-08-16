package com.asteria.world.entity.combat;

import com.asteria.engine.task.Task;
import com.asteria.util.Utility;
import com.asteria.world.entity.Animation;
import com.asteria.world.entity.Entity;
import com.asteria.world.entity.Entity.EntityType;
import com.asteria.world.entity.Graphic;
import com.asteria.world.entity.combat.CombatContainer.CombatHit;
import com.asteria.world.entity.combat.CombatFactory.CombatType;
import com.asteria.world.entity.npc.Npc;
import com.asteria.world.entity.player.Player;
import com.asteria.world.item.Item;
import com.asteria.world.item.ground.GroundItem;
import com.asteria.world.item.ground.GroundItemManager;

/**
 * A {@link Task} implementation that deals a series of hits to an entity after
 * a delay.
 * 
 * @author lare96
 */
public class CombatHitTask extends Task {

    /** The attacker instance. */
    private Entity attacker;

    /** The victim instance. */
    private Entity victim;

    /** The attacker's combat builder attached to this task. */
    private CombatBuilder builder;

    /** The attacker's combat container that will be used. */
    private CombatContainer container;

    /** The total damage dealt during this hit. */
    private int damage;

    /**
     * Create a new {@link CombatHit}.
     * 
     * @param builder
     *            the combat builder attached to this task.
     * @param container
     *            the combat hit that will be used.
     * @param delay
     *            the delay in ticks before the hit will be dealt.
     * @param initialRun
     *            if the task should be ran right away.
     */
    public CombatHitTask(CombatBuilder builder, CombatContainer container,
            int delay, boolean initialRun) {
        super(delay, initialRun);
        this.builder = builder;
        this.container = container;
        this.attacker = builder.getEntity();
        this.victim = builder.getVictim();
    }

    @Override
    public void execute() {

        // Stop the task if the target isn't registered or has died.
        if (attacker.isDead() || attacker.isUnregistered()) {
            this.cancel();
            return;
        }

        // Do any hit modifications to the container here first.
        container = container.containerModify();

        // Now we send the hitsplats if needed! We can't send the hitsplats
        // there are none to send, or if we're using magic and it splashed.
        if (container.getHits().length != 0 && container.getCombatType() != CombatType.MAGIC || container
                .isAccurate()) {
            victim.getCombatBuilder().addDamage(attacker,
                    (damage = container.dealDamage()));
        }

        // Give experience based on the hits
        CombatFactory.giveExperience(builder, container, damage);

        // Now here's what we do if the container is or isn't accurate.
        if (!container.isAccurate()) {

            // If the container isn't accurate and we're using magic, send the
            // splash graphic.
            if (container.getCombatType() == CombatType.MAGIC) {
                victim.graphic(new Graphic(85));
                attacker.getCurrentlyCasting().finishCast(attacker, victim,
                        false, 0);
                attacker.setCurrentlyCasting(null);
            }

        } else if (container.isAccurate()) {

            // Handle the armor effects if the container is accurate.
            CombatFactory.handleArmorEffects(builder, container, damage);

            // Handle any prayer effects.
            CombatFactory.handlePrayerEffects(builder, container, damage);

            // Poison the victim if needed.
            attacker.poisonVictim(victim, container.getCombatType());

            // Finish the magic spell with the correct end graphic.
            if (container.getCombatType() == CombatType.MAGIC) {
                victim.graphic(attacker.getCurrentlyCasting().endGraphic());
                attacker.getCurrentlyCasting().finishCast(attacker, victim,
                        true, damage);
                attacker.setCurrentlyCasting(null);
            }

            // 50% chance of dropping arrows from a ranged attack.
            if (container.getCombatType() == CombatType.RANGED && attacker
                    .type() == EntityType.PLAYER && Utility
                    .exclusiveRandom(2) == 0) {
                Player player = (Player) attacker;
                if (player.getFireAmmo() > 0) {
                    GroundItemManager
                            .registerAndStack(new GroundItem(new Item(player
                                    .getFireAmmo()), victim.getPosition(),
                                    player));
                    player.setFireAmmo(0);
                }
            }
        }

        // Send the defensive animations.
        if (victim.type() == EntityType.PLAYER) {
            victim.animation(new Animation(404));
        } else if (victim.type() == EntityType.NPC) {
            victim.animation(new Animation(((Npc) victim).getDefinition()
                    .getDefenceAnimation()));
        }

        // Fire the container's dynamic hit method.
        container.onHit(damage, container.isAccurate());

        // Reset combat for the victim.
        victim.getLastCombat().reset();
        victim.getCombatBuilder().setLastAttacker(attacker);

        // And finally auto-retaliate if needed.
        if (victim.isAutoRetaliate() && !victim.getCombatBuilder()
                .isAttacking()) {
            victim.getCombatBuilder().attack(attacker);
        }

        // Then we cancel the task.
        this.cancel();

        // Stop combat if we're using magic and not autocasting.
        if (container.getCombatType() == CombatType.MAGIC) {
            if (attacker.type() == EntityType.PLAYER) {
                Player player = (Player) attacker;

                if (!player.isAutocast()) {
                    player.getCombatBuilder().reset();
                    player.setCastSpell(null);
                }
            }
        }
    }
}
