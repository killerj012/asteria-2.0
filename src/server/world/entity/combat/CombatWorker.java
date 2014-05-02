package server.world.entity.combat;

import server.core.worker.Worker;
import server.util.Misc;
import server.world.entity.Hit;
import server.world.entity.npc.Npc;
import server.world.entity.player.Player;

public class CombatWorker extends Worker {

    private int cooldown = 15;
    private CombatBuilder builder;
    private CombatStrategy strategy;

    public CombatWorker(CombatBuilder builder, CombatStrategy strategy) {
        super(1, false);
        this.builder = builder;
        this.strategy = strategy;
    }

    @Override
    public void fire() {
        if (cooldown == 0 || builder.getEntity().isHasDied()) {
            this.cancel();
            builder.reset();
            return;
        }

        if (builder.getEntity() instanceof Player) {
            Player player = (Player) builder.getEntity();

            if (CombatFactory.RANGE_WEAPONS.contains(player.getEquipment().getContainer().getItemId(Misc.EQUIPMENT_SLOT_WEAPON))) {
                strategy = CombatFactory.newDefaultRangedStrategy();
            } else if (player.isAutocastMagic()) {
                strategy = CombatFactory.newDefaultMagicStrategy();
            } else {
                strategy = CombatFactory.newDefaultMeleeStrategy();
            }
        }

        builder.decrementAttackTimer();
        cooldown--;

        if (builder.getAttackTimer() == 0) {
            if (builder.getEntity().isHasDied() || builder.getCurrentTarget().isHasDied()) {
                this.cancel();
                builder.reset();
                return;
            }

            if (!builder.getEntity().getPosition().withinDistance(builder.getCurrentTarget().getPosition(), strategy.getDistance(builder.getEntity()))) {
                // XXX: follow and do shit
                return;
            }

            if (!strategy.prepareAttack(builder.getEntity())) {
                System.out.println("stopped3");
                return;
            }

            CombatHit combatHit = strategy.attack(builder.getEntity(), builder.getCurrentTarget());

            if (combatHit.getHits() == null || combatHit.getHits().length == 0) {
                builder.getCurrentTarget().dealDamage(new Hit(0));
            } else if (combatHit.getHits().length == 1) {
                builder.getCurrentTarget().dealDamage(combatHit.getHits()[0]);
            } else if (combatHit.getHits().length == 2) {
                builder.getCurrentTarget().dealDoubleDamage(combatHit.getHits()[0], combatHit.getHits()[1]);
            } else if (combatHit.getHits().length == 3) {
                builder.getCurrentTarget().dealTripleDamage(combatHit.getHits()[0], combatHit.getHits()[1], combatHit.getHits()[2]);
            } else if (combatHit.getHits().length == 4) {
                builder.getCurrentTarget().dealQuadrupleDamage(combatHit.getHits()[0], combatHit.getHits()[1], combatHit.getHits()[2], combatHit.getHits()[3]);
            }

            builder.getCurrentTarget().getCombatBuilder().setLastAttacker(builder.getEntity());
            builder.setAttackTimer(strategy.attackTimer(builder.getEntity()));
            cooldown = 10;

            if (builder.getCurrentTarget().isAutoRetaliate() && !builder.getCurrentTarget().getCombatBuilder().isAttacking()) {
                builder.getCurrentTarget().facePosition(builder.getEntity().getPosition().clone());

                if (builder.getCurrentTarget() instanceof Npc) {
                    builder.getCurrentTarget().getCombatBuilder().attack(builder.getEntity(), ((Npc) builder.getCurrentTarget()).getCombatStrategy());
                } else {
                    Player player = (Player) builder.getEntity();

                    if (CombatFactory.RANGE_WEAPONS.contains(player.getEquipment().getContainer().getItemId(Misc.EQUIPMENT_SLOT_WEAPON))) {
                        builder.getCurrentTarget().getCombatBuilder().attack(builder.getEntity(), CombatFactory.newDefaultRangedStrategy());
                    } else if (player.isAutocastMagic()) {
                        builder.getCurrentTarget().getCombatBuilder().attack(builder.getEntity(), CombatFactory.newDefaultMagicStrategy());
                    } else {
                        builder.getCurrentTarget().getCombatBuilder().attack(builder.getEntity(), CombatFactory.newDefaultMeleeStrategy());
                    }
                }
            }
        }
    }

    /**
     * @return the strategy
     */
    public CombatStrategy getStrategy() {
        return strategy;
    }
}
