package server.world.entity.combat;

import server.core.worker.Worker;
import server.util.Misc;
import server.world.World;
import server.world.entity.Gfx;
import server.world.entity.Hit;
import server.world.entity.combat.prayer.CombatPrayer;
import server.world.entity.npc.Npc;
import server.world.entity.player.Player;
import server.world.entity.player.minigame.MinigameFactory;
import server.world.entity.player.skill.SkillManager;
import server.world.entity.player.skill.SkillManager.SkillConstant;
import server.world.map.Location;

public class CombatWorker extends Worker { // XXX: cooldown system

    private CombatBuilder builder;
    private CombatStrategy strategy;

    public CombatWorker(CombatBuilder builder, CombatStrategy strategy) {
        super(1, false);
        this.builder = builder;
        this.strategy = strategy;
    }

    @Override
    public void fire() {
        if (builder.getEntity().isHasDied() || builder.getCurrentTarget().isUnregistered() || builder.getEntity().isUnregistered()) {
            if (builder.getEntity().isPlayer()) {
                System.out.println("stopped0");
            }
            this.cancel();
            builder.reset();
            return;
        }

        if (builder.getEntity().isPlayer()) {
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

        if (builder.getAttackTimer() == 0) {
            if (builder.getEntity().isHasDied() || builder.getCurrentTarget().isHasDied() || builder.getCurrentTarget().isUnregistered() || builder.getEntity().isUnregistered()) {
                this.cancel();
                builder.reset();
                return;
            }

            if (!builder.getEntity().getPosition().withinDistance(builder.getCurrentTarget().getPosition(), strategy.getDistance(builder.getEntity()))) {
                if (builder.getEntity().isPlayer()) {
                    System.out.println("stopped2");
                }
                return;
            }

            if (!strategy.prepareAttack(builder.getEntity())) {
                return;
            }

            int totalDamage = 0;
            CombatHit combatHit = strategy.attack(builder.getEntity(), builder.getCurrentTarget());

            if (combatHit.getHits() == null || combatHit.getHits().length == 0) {
                builder.getCurrentTarget().dealDamage(new Hit(0));
            } else if (combatHit.getHits().length == 1) {
                builder.getCurrentTarget().dealDamage(combatHit.getHits()[0]);
                totalDamage += combatHit.getHits()[0].getDamage();
            } else if (combatHit.getHits().length == 2) {
                builder.getCurrentTarget().dealDoubleDamage(combatHit.getHits()[0], combatHit.getHits()[1]);
                totalDamage += combatHit.getHits()[0].getDamage();
                totalDamage += combatHit.getHits()[1].getDamage();
            } else if (combatHit.getHits().length == 3) {
                builder.getCurrentTarget().dealTripleDamage(combatHit.getHits()[0], combatHit.getHits()[1], combatHit.getHits()[2]);
                totalDamage += combatHit.getHits()[0].getDamage();
                totalDamage += combatHit.getHits()[1].getDamage();
                totalDamage += combatHit.getHits()[2].getDamage();
            } else if (combatHit.getHits().length == 4) {
                builder.getCurrentTarget().dealQuadrupleDamage(combatHit.getHits()[0], combatHit.getHits()[1], combatHit.getHits()[2], combatHit.getHits()[3]);
                totalDamage += combatHit.getHits()[0].getDamage();
                totalDamage += combatHit.getHits()[1].getDamage();
                totalDamage += combatHit.getHits()[2].getDamage();
                totalDamage += combatHit.getHits()[3].getDamage();
            }

            builder.getCurrentTarget().getCombatBuilder().addDamage(builder.getEntity(), totalDamage);

            if (builder.getEntity().isPlayer() && builder.getCurrentTarget().isPlayer()) {
                Player player = (Player) builder.getEntity();
                Player target = (Player) builder.getCurrentTarget();

                if (CombatPrayer.isPrayerActivated(target, CombatPrayer.RETRIBUTION)) {
                    if (target.getSkills()[Misc.HITPOINTS].getLevel() < 1) {
                        if (Location.inWilderness(player) || MinigameFactory.inMinigame(player)) {

                            player.gfx(new Gfx(437));

                            if (Location.inMultiCombat(target)) {
                                for (Player plr : World.getPlayers()) {
                                    if (plr == null) {
                                        continue;
                                    }

                                    if (!plr.getUsername().equals(target.getUsername()) && plr.getPosition().withinDistance(target.getPosition().clone(), 5)) {
                                        plr.dealDamage(new Hit(Misc.getRandom().nextInt(15)));
                                    }
                                }
                            } else {
                                player.dealDamage(new Hit(Misc.getRandom().nextInt(9)));
                            }
                        }
                    }
                }
                if (CombatPrayer.isPrayerActivated(player, CombatPrayer.SMITE)) {
                    target.getSkills()[Misc.PRAYER].decreaseLevel(totalDamage / 4);
                }
            } else if (builder.getCurrentTarget().isPlayer()) {
                Player player = (Player) builder.getCurrentTarget();

                if (CombatPrayer.isPrayerActivated(player, CombatPrayer.REDEMPTION)) {
                    if (player.getSkills()[Misc.HITPOINTS].getLevel() <= (player.getSkills()[Misc.HITPOINTS].getLevelForExperience() / 10)) {
                        player.getSkills()[Misc.HITPOINTS].increaseLevel(Misc.getRandom().nextInt((player.getSkills()[Misc.HITPOINTS].getLevelForExperience() - player.getSkills()[Misc.HITPOINTS].getLevel())));
                        player.gfx(new Gfx(436));
                        player.getSkills()[Misc.PRAYER].setLevel(0);
                        player.getPacketBuilder().sendMessage("You've run out of prayer points!");
                        CombatPrayer.deactivateAllPrayer(player);
                        SkillManager.refresh(player, SkillConstant.PRAYER);
                        SkillManager.refresh(player, SkillConstant.HITPOINTS);
                    }
                }
            }

            builder.getCurrentTarget().getCombatBuilder().setLastAttacker(builder.getEntity());
            builder.setAttackTimer(strategy.attackTimer(builder.getEntity()));
            builder.getEntity().getLastCombat().reset();

            if (builder.getCurrentTarget().isAutoRetaliate() && !builder.getCurrentTarget().getCombatBuilder().isAttacking()) {
                // builder.getCurrentTarget().facePosition(builder.getEntity().getPosition().clone());
                builder.getCurrentTarget().getMovementQueue().follow(builder.getEntity());

                if (builder.getCurrentTarget().isNpc()) {
                    builder.getCurrentTarget().getCombatBuilder().attack(builder.getEntity(), ((Npc) builder.getCurrentTarget()).getCombatStrategy());
                } else if (builder.getCurrentTarget().isPlayer()) {
                    Player player = (Player) builder.getCurrentTarget();

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
