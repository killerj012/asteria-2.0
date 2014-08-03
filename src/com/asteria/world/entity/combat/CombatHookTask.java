package com.asteria.world.entity.combat;

import com.asteria.engine.task.Task;
import com.asteria.engine.task.TaskManager;
import com.asteria.world.entity.Entity.EntityType;
import com.asteria.world.entity.combat.CombatFactory.CombatType;
import com.asteria.world.entity.combat.special.CombatSpecial;
import com.asteria.world.entity.player.Player;

/**
 * A {@link Task} implementation that handles every combat 'hook' or 'turn'
 * during a combat session.
 * 
 * @author lare96
 */
public class CombatHookTask extends Task {

    /** The builder assigned to this task. */
    private CombatBuilder builder;

    /**
     * Create a new {@link CombatHookTask}.
     * 
     * @param builder
     *            the builder assigned to this task.
     */
    public CombatHookTask(CombatBuilder builder) {
        super(1, false);
        this.builder = builder;
    }

    @Override
    public void fire() {

        // Check if the builder is in cooldown mode before proceeding. If the
        // builder is in cooldown mode we decrement a timer that will reset the
        // combat session when it hits zero.
        if (builder.isCooldown()) {
            builder.cooldown--;
            builder.attackTimer--;

            if (builder.cooldown == 0) {
                builder.reset();
            }
            return;
        }

        // Any checks before the attack timer are done here.
        if (!CombatFactory.checkHook(builder)) {
            return;
        }

        // If the entity is an npc we redetermine the combat strategy before
        // attacking.
        if (builder.getEntity().type() == EntityType.PLAYER) {
            builder.determineStrategy();
        }

        // Decrement the attack timer.
        builder.attackTimer--;

        // The attack timer is below 1, we can attack.
        if (builder.attackTimer < 1) {

            // Check if the attacker is close enough to attack.
            if (!CombatFactory.checkAttackDistance(builder)) {
                return;
            }

            // Check if the attack can be made on this hook
            if (!builder.getStrategy().canAttack(builder.getEntity(),
                    builder.getVictim())) {
                return;
            }

            // Do all combat calculations here, we create the combat containers
            // using the attacking entity's combat strategy.
            CombatContainer container = builder.getStrategy().attack(
                    builder.getEntity(), builder.getVictim());

            if (builder.getEntity().type() == EntityType.PLAYER) {
                Player player = (Player) builder.getEntity();
                player.getPacketBuilder().sendCloseWindows();
                if (player.isSpecialActivated()) {
                    container = player.getCombatSpecial().container(player,
                            builder.getVictim());
                    CombatSpecial.drain(player, player.getCombatSpecial()
                            .getDrainAmount());
                }
            }

            // If there is no hit type the combat turn is ignored.
            if (container.getCombatType() != null) {

                // If we have hit splats to deal, we filter them through combat
                // prayer effects now. If not then we still send the hit tasks
                // next to handle any effects.
                if (container.getHits().length != 0) {
                    CombatFactory.applyPrayer(container, builder);
                }

                // Schedule a task based on the combat type. Melee hits are done
                // right away, ranged hits are done after 2 ticks, and magic
                // after 3.
                if (container.getCombatType() == CombatType.MELEE) {
                    TaskManager.submit(new CombatHitTask(builder, container, 1,
                            true));
                } else if (container.getCombatType() == CombatType.RANGED) {
                    TaskManager.submit(new CombatHitTask(builder, container, 2,
                            false));
                } else if (container.getCombatType() == CombatType.MAGIC) {
                    TaskManager.submit(new CombatHitTask(builder, container, 3,
                            false));
                }
            }

            // Reset the attacking entity.
            builder.attackTimer = builder.getStrategy().attackDelay(
                    builder.getEntity());
            builder.cooldown = 0;
            builder.getEntity().faceEntity(builder.getVictim());
        }
    }
}
