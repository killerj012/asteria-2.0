package server.world.entity.combat.task;

import server.core.worker.Worker;
import server.util.Misc;
import server.world.World;
import server.world.entity.Gfx;
import server.world.entity.Hit;
import server.world.entity.combat.CombatBuilder;
import server.world.entity.combat.CombatFactory;
import server.world.entity.combat.CombatHit;
import server.world.entity.combat.CombatType;
import server.world.entity.combat.prayer.CombatPrayer;
import server.world.entity.npc.Npc;
import server.world.entity.player.Player;
import server.world.entity.player.minigame.MinigameFactory;
import server.world.entity.player.skill.SkillManager;
import server.world.entity.player.skill.SkillManager.SkillConstant;
import server.world.map.Location;

/**
 * A worker that handles every combat turn during a combat session.
 * 
 * @author lare96
 */
public class CombatHookTask extends Worker {

    private CombatBuilder builder;

    public CombatHookTask(CombatBuilder builder) {
        super(1, false);
        this.builder = builder;
    }

    @Override
    public void fire() {
        if (builder.getCurrentTarget().isPlayer()) {
            Player player = (Player) builder.getCurrentTarget();

            if (player.getTeleportStage() > 0) {
                builder.reset();
                builder.getEntity().faceEntity(65535);
                builder.getEntity().getFollowWorker().cancel();
                builder.getEntity().setFollowing(false);
                builder.getEntity().setFollowingEntity(null);
                this.cancel();
                return;
            }
        }

        if (builder.getEntity().isNpc()) {
            Npc npc = (Npc) builder.getEntity();

            if (npc.getCombatBuilder().getCurrentTarget().getCombatBuilder().isCooldownEffect() && !npc.getPosition().withinDistance(npc.getOriginalPosition(), 5) && npc.getDefinition().isRetreats() || !builder.getCurrentTarget().getCombatBuilder().isBeingAttacked() && !npc.getPosition().withinDistance(npc.getOriginalPosition(), 5) && npc.getDefinition().isRetreats()) {
                System.out.println("lol");
                npc.getCombatBuilder().reset();
                npc.faceEntity(65535);
                npc.getFollowWorker().cancel();
                npc.setFollowing(false);
                npc.setFollowingEntity(null);
                npc.getMovementQueue().walk(npc.getOriginalPosition());
                this.cancel();
                return;
            }
        }

        if (builder.isCooldownEffect()) {
            builder.decrementCooldown();

            if (builder.getCooldown() == 0) {
                builder.reset();
                this.cancel();
                return;
            }

            if (!builder.getEntity().isAutoRetaliate()) {
                return;
            }
        }

        if (builder.getEntity().isPlayer()) {
            Player player = (Player) builder.getEntity();

            if (builder.getCurrentTarget().isPlayer()) {
                Player target = (Player) builder.getCurrentTarget();

                if (!Location.inWilderness(target)) {
                    player.getPacketBuilder().sendMessage("Your target is not in the wilderness!");
                    builder.reset();
                    builder.getEntity().faceEntity(65535);
                    builder.getEntity().getFollowWorker().cancel();
                    builder.getEntity().setFollowing(false);
                    builder.getEntity().setFollowingEntity(null);
                    this.cancel();
                    return;
                }
            }

            if (player.getCombatBuilder().getLastAttacker() != null) {
                if (!Location.inMultiCombat(player) && player.getCombatBuilder().getCurrentTarget() != player.getCombatBuilder().getLastAttacker()) {
                    player.getPacketBuilder().sendMessage("You are already under attack!");
                    builder.reset();
                    builder.getEntity().faceEntity(65535);
                    builder.getEntity().getFollowWorker().cancel();
                    builder.getEntity().setFollowing(false);
                    builder.getEntity().setFollowingEntity(null);
                    this.cancel();
                    return;
                }
            }
        }

        if (builder.getCurrentTarget().isUnregistered() || builder.getEntity().isUnregistered()) {
            this.cancel();
            return;
        }

        if (builder.getEntity().isHasDied()) {
            this.cancel();
            return;
        }

        if (builder.getEntity().isPlayer()) {
            Player player = (Player) builder.getEntity();
            CombatFactory.determinePlayerStrategy(player);
        }

        builder.decrementAttackTimer();

        if (builder.getAttackTimer() == 0) {
            if (builder.getCurrentTarget().isHasDied() || builder.getCurrentTarget().isUnregistered() || builder.getEntity().isUnregistered()) {
                builder.reset();
                this.cancel();
                return;
            }

            if (!builder.getEntity().getPosition().withinDistance(builder.getCurrentTarget().getPosition(), builder.getCurrentStrategy().getDistance(builder.getEntity()))) {
                return;
            }

            if (!builder.getCurrentStrategy().prepareAttack(builder.getEntity())) {
                return;
            }

            int totalDamage = 0;
            CombatHit combatHit = builder.getCurrentStrategy().attack(builder.getEntity(), builder.getCurrentTarget());

            if (combatHit != null && combatHit.getHits() != null) {

                if (builder.getEntity().isPlayer()) {
                    Player player = (Player) builder.getEntity();

                    if (Misc.getRandom().nextInt(1) == 0) {
                        if (CombatFactory.isWearingFullGuthans(player)) {
                            builder.getCurrentTarget().gfx(new Gfx(398));
                            player.heal(combatHit.getHits()[0].getDamage());
                        }
                    }
                }

                if (builder.getCurrentTarget().isPlayer() && builder.getEntity().isNpc()) {
                    Player player = (Player) builder.getCurrentTarget();

                    if (combatHit.getHitType() == CombatType.MELEE && CombatPrayer.isPrayerActivated(player, CombatPrayer.PROTECT_FROM_MELEE)) {
                        for (int i = 0; i < combatHit.getHits().length; i++) {
                            combatHit.getHits()[i] = new Hit(0);
                        }
                    } else if (combatHit.getHitType() == CombatType.MAGIC && CombatPrayer.isPrayerActivated(player, CombatPrayer.PROTECT_FROM_MAGIC)) {
                        for (int i = 0; i < combatHit.getHits().length; i++) {
                            combatHit.getHits()[i] = new Hit(0);
                        }
                    } else if (combatHit.getHitType() == CombatType.RANGE && CombatPrayer.isPrayerActivated(player, CombatPrayer.PROTECT_FROM_MISSILES)) {
                        for (int i = 0; i < combatHit.getHits().length; i++) {
                            combatHit.getHits()[i] = new Hit(0);
                        }
                    }
                } else if (builder.getCurrentTarget().isPlayer() && builder.getEntity().isPlayer()) {
                    Player player = (Player) builder.getEntity();
                    Player target = (Player) builder.getCurrentTarget();

                    if (!CombatFactory.isWearingFullVeracs(player)) {
                        if (combatHit.getHitType() == CombatType.MELEE && CombatPrayer.isPrayerActivated(target, CombatPrayer.PROTECT_FROM_MELEE)) {
                            if (Misc.getRandom().nextInt(4) == 0) {
                                for (int i = 0; i < combatHit.getHits().length; i++) {
                                    combatHit.getHits()[i] = new Hit(0);
                                }
                            }
                        } else if (combatHit.getHitType() == CombatType.MAGIC && CombatPrayer.isPrayerActivated(target, CombatPrayer.PROTECT_FROM_MAGIC)) {
                            if (Misc.getRandom().nextInt(4) == 0) {
                                for (int i = 0; i < combatHit.getHits().length; i++) {
                                    combatHit.getHits()[i] = new Hit(0);
                                }
                            }
                        } else if (combatHit.getHitType() == CombatType.RANGE && CombatPrayer.isPrayerActivated(target, CombatPrayer.PROTECT_FROM_MISSILES)) {
                            if (Misc.getRandom().nextInt(4) == 0) {
                                for (int i = 0; i < combatHit.getHits().length; i++) {
                                    combatHit.getHits()[i] = new Hit(0);
                                }
                            }
                        }
                    }
                }

                if (combatHit.getHits().length == 1) {
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
            }

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
            builder.setAttackTimer(builder.getCurrentStrategy().attackTimer(builder.getEntity()));
            builder.getCurrentTarget().getLastCombat().reset();
            builder.getCurrentTarget().getCombatBuilder().resetCooldown();
            builder.getEntity().facePosition(builder.getCurrentTarget().getPosition());

            if (builder.getCurrentTarget().isAutoRetaliate() && !builder.getCurrentTarget().getCombatBuilder().isAttacking()) {
                builder.getCurrentTarget().getCombatBuilder().attack(builder.getEntity());
            }
        }
    }
}
