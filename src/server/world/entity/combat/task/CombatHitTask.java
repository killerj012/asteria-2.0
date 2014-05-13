package server.world.entity.combat.task;

import server.core.worker.Worker;
import server.util.Misc;
import server.world.World;
import server.world.entity.Animation;
import server.world.entity.Entity;
import server.world.entity.Gfx;
import server.world.entity.Hit;
import server.world.entity.combat.CombatFactory;
import server.world.entity.combat.CombatType;
import server.world.entity.combat.prayer.CombatPrayer;
import server.world.entity.combat.task.CombatPoisonTask.CombatPoison;
import server.world.entity.npc.Npc;
import server.world.entity.player.Player;
import server.world.entity.player.minigame.MinigameFactory;
import server.world.entity.player.skill.SkillManager;
import server.world.entity.player.skill.SkillManager.SkillConstant;
import server.world.item.Item;
import server.world.item.ground.GroundItem;
import server.world.map.Location;

/**
 * A {@link Worker} implementation that deals a series of hits to an entity
 * after a delay.
 * 
 * @author lare96
 */
public class CombatHitTask extends Worker {

    /** The entity that will be dealing these hits. */
    private Entity attacker;

    /** The entity that will be dealt these hits. */
    private Entity target;

    /** The hits that will be dealt to the entity. */
    private Hit[] hits;

    /** The type of combat being used. */
    private CombatType combatType;

    /** The total damage dealt to the entity. */
    private int totalDamage;

    /**
     * Create a new {@link CombatHitTask}.
     * 
     * @param attacker
     *        the entity that will be dealing these hits.
     * @param target
     *        the entity that will be dealt these hits.
     * @param hits
     *        the hits that will be dealt to the entity.
     * @param combatType
     *        the type of combat being used.
     * @param totalDamage
     *        the total damage dealt to the entity.
     * @param delay
     *        the delay in ticks before the hit will be dealt.
     * @param initialRun
     *        if the task should be ran right away.
     */
    public CombatHitTask(Entity attacker, Entity target, Hit[] hits, CombatType combatType, int totalDamage, int delay, boolean initialRun) {
        super(delay, initialRun);
        this.attacker = attacker;
        this.target = target;
        this.hits = hits;
        this.combatType = combatType;
        this.totalDamage = totalDamage;
    }

    @Override
    public void fire() {

        /** Stop the task if the target isn't registered or has died. */
        if (target.isHasDied() || target.isUnregistered()) {
            this.cancel();
            return;
        }

        /** Deal the damage as normal otherwise. */
        if (hits != null) {
            if (hits.length == 1) {
                target.dealDamage(hits[0]);
            } else if (hits.length == 2) {
                target.dealDoubleDamage(hits[0], hits[1]);
            } else if (hits.length == 3) {
                target.dealTripleDamage(hits[0], hits[1], hits[2]);
            } else if (hits.length == 4) {
                target.dealQuadrupleDamage(hits[0], hits[1], hits[2], hits[3]);
            }

            /** Add the total damage to the target's damage map. */
            target.getCombatBuilder().addDamage(attacker, totalDamage);
        }

        /** Various checks for different combat types. */
        if (combatType == CombatType.MAGIC) {
            target.gfx(attacker.getCurrentlyCasting().endGfx());
            attacker.getCurrentlyCasting().endCast(attacker, target, true);
            attacker.setCurrentlyCasting(null);

            if (attacker.isPlayer()) {
                Player player = (Player) attacker;

                if (!player.isAutocast()) {
                    player.setCastSpell(null);
                }
            }
        } else if (combatType == CombatType.MELEE) {
            if (target.isPlayer()) {
                Player player = (Player) attacker;
                player.animation(new Animation(404));
            }
        } else if (combatType == CombatType.RANGE) {
            if (target.isPlayer()) {
                Player player = (Player) attacker;
                player.animation(new Animation(404));
            }

            if (attacker.isPlayer()) {
                if (Misc.getRandom().nextInt(3) != 0) {

                    Player player = (Player) attacker;

                    if (player.getFireAmmo() != 0) {
                        World.getGroundItems().registerAndStack(new GroundItem(new Item(player.getFireAmmo(), 1), target.getPosition(), player));
                        player.setFireAmmo(0);
                    }
                }
            }
        }

        /** Various entity effects take place here. */
        if (attacker.isNpc()) {
            Npc npc = (Npc) attacker;

            if (npc.getDefinition().isPoisonous()) {
                CombatFactory.poisonEntity(target, CombatPoison.STRONG);
            }
        } else if (attacker.isPlayer()) {
            Player player = (Player) attacker;

            if (player.isSpecialActivated()) {
                player.getCombatSpecial().getSpecialStrategy().onHit(player, target);
            }

            if (combatType == CombatType.MELEE || combatType == CombatType.RANGE) {
                Item weapon = player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_WEAPON);

                if (weapon != null) {
                    if (weapon.getDefinition().getItemName().endsWith("(p)")) {
                        CombatFactory.poisonEntity(target, CombatPoison.MILD);
                    } else if (weapon.getDefinition().getItemName().endsWith("(p+)")) {
                        CombatFactory.poisonEntity(target, CombatPoison.STRONG);
                    } else if (weapon.getDefinition().getItemName().endsWith("(p++)")) {
                        CombatFactory.poisonEntity(target, CombatPoison.SEVERE);
                    }
                }
            }

            if (combatType == CombatType.RANGE) {
                Item weapon = player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_ARROWS);

                if (weapon != null) {
                    if (weapon.getDefinition().getItemName().endsWith("(p)")) {
                        CombatFactory.poisonEntity(target, CombatPoison.MILD);
                    } else if (weapon.getDefinition().getItemName().endsWith("(p+)")) {
                        CombatFactory.poisonEntity(target, CombatPoison.STRONG);
                    } else if (weapon.getDefinition().getItemName().endsWith("(p++)")) {
                        CombatFactory.poisonEntity(target, CombatPoison.SEVERE);
                    }
                }
            }
        }

        /** Various armor and weapon effects. */
        if (Misc.getRandom().nextInt(4) == 0) {
            if (combatType == CombatType.MELEE) {
                if (attacker.isPlayer() && target.isPlayer()) {
                    Player player = (Player) attacker;
                    Player victim = (Player) target;

                    if (CombatFactory.isWearingFullTorags(player)) {
                        victim.decrementRunEnergy(Misc.getRandom().nextInt(19) + 1);
                        victim.gfx(new Gfx(399));
                    } else if (CombatFactory.isWearingFullAhrims(player)) {
                        victim.getSkills()[Misc.STRENGTH].decreaseLevel(Misc.getRandom().nextInt(4) + 1);
                        SkillManager.refresh(victim, SkillConstant.STRENGTH);
                        victim.gfx(new Gfx(400));
                    } else if (CombatFactory.isWearingFullGuthans(player)) {
                        target.gfx(new Gfx(398));
                        player.getSkills()[Misc.HITPOINTS].increaseLevel(totalDamage, 99);
                        SkillManager.refresh(player, SkillConstant.HITPOINTS);
                    }
                } else if (attacker.isPlayer()) {
                    Player player = (Player) attacker;

                    if (CombatFactory.isWearingFullGuthans(player)) {
                        target.gfx(new Gfx(398));
                        player.getSkills()[Misc.HITPOINTS].increaseLevel(totalDamage, 99);
                        SkillManager.refresh(player, SkillConstant.HITPOINTS);
                    }
                }
            } else if (combatType == CombatType.RANGE) {
                if (attacker.isPlayer() && target.isPlayer()) {
                    Player player = (Player) attacker;
                    Player victim = (Player) target;

                    if (CombatFactory.isWearingFullKarils(player)) {
                        victim.gfx(new Gfx(401));
                        victim.getSkills()[Misc.AGILITY].decreaseLevel(Misc.getRandom().nextInt(4) + 1);
                        SkillManager.refresh(victim, SkillConstant.AGILITY);
                    }
                }
            } else if (combatType == CombatType.MAGIC) {
                if (attacker.isPlayer() && target.isPlayer()) {
                    Player player = (Player) attacker;
                    Player victim = (Player) target;

                    if (CombatFactory.isWearingFullAhrims(player)) {
                        victim.getSkills()[Misc.STRENGTH].decreaseLevel(Misc.getRandom().nextInt(4) + 1);
                        SkillManager.refresh(victim, SkillConstant.STRENGTH);
                        victim.gfx(new Gfx(400));
                    }
                }
            }
        }

        /**
         * If both the attacker and target are players then check for
         * retribution and do smite prayer effects.
         */
        if (attacker.isPlayer() && target.isPlayer()) {
            Player player = (Player) attacker;
            Player victim = (Player) target;

            /** Retribution prayer check and function here. */
            if (CombatPrayer.isPrayerActivated(victim, CombatPrayer.RETRIBUTION)) {
                if (victim.getSkills()[Misc.HITPOINTS].getLevel() < 1) {
                    if (Location.inWilderness(player) || MinigameFactory.inMinigame(player)) {

                        player.gfx(new Gfx(437));

                        if (Location.inMultiCombat(target)) {
                            for (Player plr : World.getPlayers()) {
                                if (plr == null) {
                                    continue;
                                }

                                if (!plr.getUsername().equals(victim.getUsername()) && plr.getPosition().withinDistance(target.getPosition().clone(), 5)) {
                                    plr.dealDamage(new Hit(Misc.getRandom().nextInt(15)));
                                }
                            }
                        } else {
                            player.dealDamage(new Hit(Misc.getRandom().nextInt(9)));
                        }
                    }
                }
            }

            /** Smite prayer check and function here. */
            if (CombatPrayer.isPrayerActivated(player, CombatPrayer.SMITE)) {
                victim.getSkills()[Misc.PRAYER].decreaseLevel(totalDamage / 4);
                SkillManager.refresh(victim, SkillConstant.PRAYER);
            }

        }

        /**
         * If the target is a player then check for the redemption prayer
         * effect.
         */
        if (target.isPlayer()) {
            Player player = (Player) target;

            /** Redemption prayer check here. */
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

        /** Auto-retaliate the attacker if needed. */
        if (target.isAutoRetaliate() && !target.getCombatBuilder().isAttacking()) {
            target.getCombatBuilder().attack(attacker);
        }

        /** And last but not least cancel the task. */
        this.cancel();
    }
}
