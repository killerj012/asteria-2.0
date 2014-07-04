package server.world.entity.combat.task;

import server.core.worker.Worker;
import server.util.Misc;
import server.world.World;
import server.world.entity.Animation;
import server.world.entity.Entity;
import server.world.entity.EntityType;
import server.world.entity.Gfx;
import server.world.entity.Hit;
import server.world.entity.combat.CombatFactory;
import server.world.entity.combat.CombatHitContainer;
import server.world.entity.combat.CombatType;
import server.world.entity.combat.prayer.CombatPrayer;
import server.world.entity.combat.task.CombatPoisonTask.CombatPoisonData;
import server.world.entity.combat.task.CombatPoisonTask.PoisonType;
import server.world.entity.npc.Npc;
import server.world.entity.player.Player;
import server.world.entity.player.content.AssignWeaponInterface.WeaponInterface;
import server.world.entity.player.minigame.MinigameFactory;
import server.world.entity.player.skill.SkillManager;
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

	/** The combat hit that will be used. */
	private CombatHitContainer combatHit;

	/** The weapon used to attack the victim. */
	private WeaponInterface attackWeapon;

	/** Determines if at least one hit was accurate. */
	private boolean oneHitAccurate;

	/** The total damage dealt this turn. */
	private int totalDamage;

	/**
	 * Create a new {@link CombatHitTask}.
	 * 
	 * @param attacker
	 *            the entity that will be dealing these hits.
	 * @param target
	 *            the entity that will be dealt these hits.
	 * @param combatHit
	 *            the combat hit that will be used.
	 * @param oneHitAccurate
	 *            if at least one hit was accurate.
	 * @param delay
	 *            the delay in ticks before the hit will be dealt.
	 * @param initialRun
	 *            if the task should be ran right away.
	 */
	public CombatHitTask(Entity attacker, Entity target,
			CombatHitContainer combatHit, boolean oneHitAccurate, int delay,
			boolean initialRun) {
		super(delay, initialRun);
		this.attacker = attacker;
		this.target = target;
		this.combatHit = combatHit;
		this.attackWeapon = attacker.type() == EntityType.PLAYER ? ((Player) attacker)
				.getWeapon() : null;
		this.oneHitAccurate = oneHitAccurate;
	}

	@Override
	public void fire() {

		/** Stop the task if the target isn't registered or has died. */
		if (target.isHasDied() || target.isUnregistered()) {
			this.cancel();
			return;
		}

		/** A complete miss! None of the hits were accurate. */
		if (!oneHitAccurate) {
			if (combatHit.getHitType() == CombatType.MAGIC) {
				target.gfx(new Gfx(85));
				attacker.getCurrentlyCasting().endCast(attacker, target, false,
						0);

				if (attacker.type() == EntityType.PLAYER) {
					Player player = (Player) attacker;
					SkillManager
							.addExperience(player, attacker
									.getCurrentlyCasting().baseExperience(),
									Misc.MAGIC);

					if (!player.isAutocast()) {
						player.setCastSpell(null);
					}
				}
				attacker.setCurrentlyCasting(null);
			}
			combatHit.onHit(attacker, target, 0, false);
		}

		/** Send the hitsplats if needed. */
		if (combatHit.getHits() != null) {
			if (combatHit.getHitType() != CombatType.MAGIC || oneHitAccurate) {
				if (combatHit.getHits().length == 1) {
					target.dealDamage(combatHit.getHits()[0].getHit());
					totalDamage += combatHit.getHits()[0].getHit().getDamage();
				} else if (combatHit.getHits().length == 2) {
					target.dealDoubleDamage(combatHit.getHits()[0].getHit(),
							combatHit.getHits()[1].getHit());
					totalDamage += combatHit.getHits()[0].getHit().getDamage();
					totalDamage += combatHit.getHits()[1].getHit().getDamage();
				} else if (combatHit.getHits().length == 3) {
					target.dealTripleDamage(combatHit.getHits()[0].getHit(),
							combatHit.getHits()[1].getHit(),
							combatHit.getHits()[2].getHit());
					totalDamage += combatHit.getHits()[0].getHit().getDamage();
					totalDamage += combatHit.getHits()[1].getHit().getDamage();
					totalDamage += combatHit.getHits()[2].getHit().getDamage();
				} else if (combatHit.getHits().length == 4) {
					target.dealQuadrupleDamage(combatHit.getHits()[0].getHit(),
							combatHit.getHits()[1].getHit(),
							combatHit.getHits()[2].getHit(),
							combatHit.getHits()[3].getHit());
					totalDamage += combatHit.getHits()[0].getHit().getDamage();
					totalDamage += combatHit.getHits()[1].getHit().getDamage();
					totalDamage += combatHit.getHits()[2].getHit().getDamage();
					totalDamage += combatHit.getHits()[3].getHit().getDamage();
				}

				/** Give range/melee/magic exp. */
				if (attacker.type() == EntityType.PLAYER) {
					// XXX: These exp rates could use some work.

					Player player = (Player) attacker;
					int defaultExp = ((totalDamage * 10) / 3);

					switch (player.getFightType().getTrainType()) {
					case ATTACK:
						SkillManager.addExperience(player, defaultExp,
								Misc.ATTACK);
						break;
					case STRENGTH:
						SkillManager.addExperience(player, defaultExp,
								Misc.STRENGTH);
						break;
					case DEFENCE:
						SkillManager.addExperience(player, defaultExp,
								Misc.DEFENCE);
						break;
					case RANGED:
						SkillManager.addExperience(player, defaultExp,
								Misc.RANGED);
						break;
					case ATTACK_STRENGTH_DEFENCE:
						SkillManager.addExperience(player,
								((totalDamage * 10) / 5), Misc.ATTACK);
						SkillManager.addExperience(player,
								((totalDamage * 10) / 5), Misc.STRENGTH);
						SkillManager.addExperience(player,
								((totalDamage * 10) / 5), Misc.DEFENCE);
						break;
					case RANGED_DEFENCE:
						SkillManager.addExperience(player,
								((totalDamage * 10) / 4), Misc.RANGED);
						SkillManager.addExperience(player,
								((totalDamage * 10) / 5), Misc.DEFENCE);
						break;
					}
				}
			}

			/** Add the total damage to the target's damage map. */
			target.getCombatBuilder().addDamage(attacker, totalDamage);

			if (oneHitAccurate) {

				/** Various armor and weapon effects. */
				combatHit.onHit(attacker, target, totalDamage, true);

				if (Misc.random(4) == 0) {
					if (combatHit.getHitType() == CombatType.MELEE) {
						if (attacker.type() == EntityType.PLAYER
								&& target.type() == EntityType.PLAYER) {
							Player player = (Player) attacker;
							Player victim = (Player) target;

							if (CombatFactory.isWearingFullTorags(player)) {
								victim.decrementRunEnergy(Misc.random(19) + 1);
								victim.gfx(new Gfx(399));
							} else if (CombatFactory
									.isWearingFullAhrims(player)) {
								victim.getSkills()[Misc.STRENGTH]
										.decreaseLevel(Misc.random(4) + 1);
								SkillManager.refresh(victim, Misc.STRENGTH);
								victim.gfx(new Gfx(400));
							} else if (CombatFactory
									.isWearingFullGuthans(player)) {
								target.gfx(new Gfx(398));
								player.getSkills()[Misc.HITPOINTS]
										.increaseLevel(totalDamage, 99);
								SkillManager.refresh(player, Misc.HITPOINTS);
							}
						} else if (attacker.type() == EntityType.PLAYER) {
							Player player = (Player) attacker;

							if (CombatFactory.isWearingFullGuthans(player)) {
								target.gfx(new Gfx(398));
								player.getSkills()[Misc.HITPOINTS]
										.increaseLevel(totalDamage, 99);
								SkillManager.refresh(player, Misc.HITPOINTS);
							}
						}
					} else if (combatHit.getHitType() == CombatType.RANGE) {
						if (attacker.type() == EntityType.PLAYER
								&& target.type() == EntityType.PLAYER) {
							Player player = (Player) attacker;
							Player victim = (Player) target;

							if (CombatFactory.isWearingFullKarils(player)) {
								victim.gfx(new Gfx(401));
								victim.getSkills()[Misc.AGILITY]
										.decreaseLevel(Misc.random(4) + 1);
								SkillManager.refresh(victim, Misc.AGILITY);
							}
						}
					} else if (combatHit.getHitType() == CombatType.MAGIC) {
						if (attacker.type() == EntityType.PLAYER
								&& target.type() == EntityType.PLAYER) {
							Player player = (Player) attacker;
							Player victim = (Player) target;

							if (CombatFactory.isWearingFullAhrims(player)) {
								victim.getSkills()[Misc.STRENGTH]
										.decreaseLevel(Misc.random(4) + 1);
								SkillManager.refresh(victim, Misc.STRENGTH);
								victim.gfx(new Gfx(400));
							}
						}
					}
				}

				/** Various entity effects take place here. */
				if (attacker.type() == EntityType.NPC) {
					Npc npc = (Npc) attacker;

					if (npc.getDefinition().isPoisonous()) {
						CombatFactory.poisonEntity(target, PoisonType.EXTRA);
					}
				} else if (attacker.type() == EntityType.PLAYER
						&& attackWeapon != null) {
					Player player = (Player) attacker;

					if (combatHit.getHitType() == CombatType.MELEE
							|| attackWeapon == WeaponInterface.DART
							|| attackWeapon == WeaponInterface.KNIFE
							|| attackWeapon == WeaponInterface.THROWNAXE
							|| attackWeapon == WeaponInterface.JAVELIN) {
						Item weapon = player.getEquipment().getContainer()
								.getItem(Misc.EQUIPMENT_SLOT_WEAPON);

						if (weapon != null) {
							CombatFactory.poisonEntity(target,
									CombatPoisonData.getPoisonType(weapon));
						}
					} else if (combatHit.getHitType() == CombatType.RANGE) {

						Item ammo = player.getEquipment().getContainer()
								.getItem(Misc.EQUIPMENT_SLOT_ARROWS);

						if (ammo != null) {
							CombatFactory.poisonEntity(target,
									CombatPoisonData.getPoisonType(ammo));
						}
					}
				}

				/**
				 * If both the attacker and target are players then check for
				 * retribution and do smite prayer effects.
				 */
				if (attacker.type() == EntityType.PLAYER
						&& target.type() == EntityType.PLAYER) {
					Player player = (Player) attacker;
					Player victim = (Player) target;

					/** Retribution prayer check and function here. */
					if (CombatPrayer.isPrayerActivated(victim,
							CombatPrayer.RETRIBUTION)) {
						if (victim.getSkills()[Misc.HITPOINTS].getLevel() < 1) {
							if (Location.inWilderness(player)
									|| MinigameFactory.inMinigame(player)) {

								victim.gfx(new Gfx(437));

								if (Location.inMultiCombat(target)) {
									for (Player plr : victim.getPlayers()) {
										if (plr == null) {
											continue;
										}

										if (!plr.getUsername().equals(
												victim.getUsername())
												&& plr.getPosition()
														.withinDistance(
																target.getPosition()
																		.clone(),
																5)) {
											plr.dealDamage(new Hit(Misc
													.random(15)));
										}
									}
								} else {
									player.dealDamage(new Hit(Misc.random(9)));
								}
							}
						}
					}

					/** Smite prayer check and function here. */
					if (CombatPrayer.isPrayerActivated(player,
							CombatPrayer.SMITE)) {
						victim.getSkills()[Misc.PRAYER]
								.decreaseLevel(totalDamage / 4);
						SkillManager.refresh(victim, Misc.PRAYER);
					}
				}
			}
		}

		/**
		 * If the target is a player then check for the redemption prayer
		 * effect.
		 */
		if (target.type() == EntityType.PLAYER && combatHit.getHits() != null) {
			Player player = (Player) target;

			/** Redemption prayer check here. */
			if (CombatPrayer.isPrayerActivated(player, CombatPrayer.REDEMPTION)) {

				int level = player.getSkills()[Misc.HITPOINTS]
						.getLevelForExperience();

				if (player.getSkills()[Misc.HITPOINTS].getLevel() <= (level / 10)) {
					player.getSkills()[Misc.HITPOINTS].increaseLevel(Misc
							.randomNoZero(20));
					player.gfx(new Gfx(436));
					player.getSkills()[Misc.PRAYER].setLevel(0);
					player.getPacketBuilder().sendMessage(
							"You've run out of prayer points!");
					CombatPrayer.deactivateAllPrayer(player);
					SkillManager.refresh(player, Misc.PRAYER);
					SkillManager.refresh(player, Misc.HITPOINTS);
				}
			}
		}

		/** Various checks for different combat types. */
		if (combatHit.getHitType() == CombatType.MAGIC) {
			if (oneHitAccurate) {
				target.gfx(attacker.getCurrentlyCasting().endGfx());
				attacker.getCurrentlyCasting().endCast(attacker, target, true,
						totalDamage);

				if (attacker.type() == EntityType.PLAYER) {
					Player player = (Player) attacker;

					if (combatHit.getHits() == null) {
						SkillManager.addExperience(player, attacker
								.getCurrentlyCasting().baseExperience(),
								Misc.MAGIC);
					} else {
						SkillManager.addExperience(player,
								((totalDamage * 10) / 3)
										+ player.getCurrentlyCasting()
												.baseExperience(), Misc.MAGIC);
					}

					if (!player.isAutocast()) {
						player.setCastSpell(null);
					}
				}

				attacker.setCurrentlyCasting(null);
			}
		} else if (combatHit.getHitType() == CombatType.MELEE) {
			if (target.type() == EntityType.PLAYER) {
				Player player = (Player) target;
				player.animation(new Animation(404));
			}
		} else if (combatHit.getHitType() == CombatType.RANGE) {
			if (target.type() == EntityType.PLAYER) {
				Player player = (Player) target;
				player.animation(new Animation(404));
			}

			if (attacker.type() == EntityType.PLAYER) {
				if (Misc.random(3) != 0) {

					Player player = (Player) attacker;

					if (player.getFireAmmo() != 0) {
						World.getGroundItems().registerAndStack(
								new GroundItem(
										new Item(player.getFireAmmo(), 1),
										target.getPosition(), player));
						player.setFireAmmo(0);
					}
				}
			}
		}

		/** Auto-retaliate the attacker if needed. */
		if (target.isAutoRetaliate()
				&& !target.getCombatBuilder().isAttacking()) {
			target.getCombatBuilder().attack(attacker);
		}

		/** And last but not least cancel the task. */
		this.cancel();
	}
}
