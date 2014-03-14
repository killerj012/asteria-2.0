package server.world.entity.combat.melee;

import server.core.worker.Worker;
import server.world.entity.Animation;
import server.world.entity.Entity;
import server.world.entity.combat.Combat;
import server.world.entity.combat.Hit;
import server.world.entity.combat.Combat.CombatType;

public class MeleeHitWorker extends Worker {

    private Entity attacker;

    private Entity victim;

    public MeleeHitWorker(int delay, Entity attacker) {
        super(delay, false);
        this.attacker = attacker;
        this.victim = attacker.getCombatSession().getCurrentlyAttacking();
    }

    public MeleeHitWorker(Entity attacker) {
        super(1, true);
        this.attacker = attacker;
        this.victim = attacker.getCombatSession().getCurrentlyAttacking();
    }

    @Override
    public void fire() {
        if (!Combat.check(attacker, victim) || !attacker.getPosition().withinDistance(victim.getPosition(), Combat.getDistanceRequired(attacker, CombatType.MELEE))) {
            attacker.getCombatSession().resetCombat(victim);
            this.cancel();
        }

        Hit hit = Combat.calculateHit(attacker, attacker.getType());

        // TODO: combat animation
        attacker.facePosition(victim.getPosition());
        attacker.animation(new Animation(423));

        victim.facePosition(attacker.getPosition());
        victim.animation(new Animation(404));
        victim.dealDamage(hit);
        victim.getCombatSession().getParticipants().add(attacker);
        victim.getCombatSession().addToDamage(attacker, hit.getDamage());
        victim.getCombatSession().setLastHitBy(attacker);
        Combat.fight(victim, attacker);
        this.cancel();
    }
}
