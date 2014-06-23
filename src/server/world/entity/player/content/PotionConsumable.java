package server.world.entity.player.content;

import static server.util.Misc.ATTACK;
import static server.util.Misc.DEFENCE;
import static server.util.Misc.HITPOINTS;
import static server.util.Misc.MAGIC;
import static server.util.Misc.PRAYER;
import static server.util.Misc.RANGED;
import static server.util.Misc.STRENGTH;

import java.util.EnumSet;
import java.util.Set;

import server.core.worker.TaskFactory;
import server.core.worker.Worker;
import server.util.Misc.GenericAction;
import server.world.entity.Animation;
import server.world.entity.player.Player;
import server.world.entity.player.skill.Skill;
import server.world.entity.player.skill.SkillManager;
import server.world.item.Item;

public enum PotionConsumable implements GenericAction<Player> {

	RANGE_POTIONS(2444, 169, 171, 173) {
		@Override
		public void fireAction(Player player) {
			doBasicEffect(player, RANGED, BoostType.NORMAL);

		}
	},

	ENERGY_POTIONS(3008, 3010, 3012, 3014) {
		@Override
		public void fireAction(Player player) {
			doEnergyPotion(player);

		}
	},

	SUPER_ENERGY_POTIONS(3016, 3018, 3020, 3022) {
		@Override
		public void fireAction(Player player) {
			doSuperEnergyPotion(player);

		}
	},

	MAGIC_POTIONS(3040, 3042, 3044, 3046) {
		@Override
		public void fireAction(Player player) {
			doBasicEffect(player, MAGIC, BoostType.NORMAL);

		}
	},

	DEFENCE_POTIONS(2432, 133, 135, 137) {
		@Override
		public void fireAction(Player player) {
			doBasicEffect(player, DEFENCE, BoostType.NORMAL);

		}
	},

	STRENGTH_POTIONS(113, 115, 117, 119) {
		@Override
		public void fireAction(Player player) {
			doBasicEffect(player, STRENGTH, BoostType.NORMAL);

		}
	},

	ATTACK_POTIONS(2428, 121, 123, 125) {
		@Override
		public void fireAction(Player player) {
			doBasicEffect(player, ATTACK, BoostType.NORMAL);

		}
	},

	SUPER_DEFENCE_POTIONS(2442, 163, 165, 167) {
		@Override
		public void fireAction(Player player) {
			doBasicEffect(player, DEFENCE, BoostType.SUPER);

		}
	},

	SUPER_ATTACK_POTIONS(2436, 145, 147, 149) {
		@Override
		public void fireAction(Player player) {
			doBasicEffect(player, ATTACK, BoostType.SUPER);

		}
	},

	SUPER_STRENGTH_POTIONS(2440, 157, 159, 161) {
		@Override
		public void fireAction(Player player) {
			doBasicEffect(player, STRENGTH, BoostType.SUPER);

		}
	},

	SUPER_RESTORE_POTIONS(3024, 3026, 3028, 3030) {
		@Override
		public void fireAction(Player player) {
			doPrayerPotion(player, true);

		}
	},

	PRAYER_POTIONS(2434, 139, 141, 143) {
		@Override
		public void fireAction(Player player) {
			doPrayerPotion(player, false);

		}
	},

	ANTI_FIRE_POTIONS(2452, 2454, 2456, 2458) {
		@Override
		public void fireAction(Player player) {
			antiFire(player);

		}
	};

	private final int[] ids;

	private static final Set<PotionConsumable> ALL_POTIONS = EnumSet.allOf(PotionConsumable.class);

	private PotionConsumable(int... ids) {
		this.ids = ids;
	}

	private static PotionConsumable forId(int id) {
		for (PotionConsumable potion : ALL_POTIONS) {
			for (int potionId : potion.getIds()) {
				if (id == potionId) {
					return potion;
				}
			}
		}
		return null;
	}

	private static Item getReplacementItem(Item item) {
		PotionConsumable potion = forId(item.getId());
		int length = potion.getIds().length;
		for (int index = 0; index < length; index++) {
			if (potion.getIds()[index] == item.getId() && index + 1 < length) {
				return new Item(potion.getIds()[index + 1]);
			}
		}
		return VIAL;
	}

	public int[] getIds() {
		return ids;
	}

	public final static Item VIAL = new Item(229);

	private static void doPrayerPotion(Player player, boolean isRestorePotion) {
		Skill skill = player.getSkills()[PRAYER];
		int realLevel = skill.getLevelForExperience();

		skill.increaseLevel((int) (realLevel * .33), realLevel);

		if (isRestorePotion) {
			skill.increaseLevel(1, realLevel);
			getRestoreStats(player);
		}
	}

	private static void doEnergyPotion(Player player) {
		player.incrementRunEnergy(10);
	}

	private static void doSuperEnergyPotion(Player player) {
		player.incrementRunEnergy(10);
	}

	private static void getRestoreStats(Player player) {
		for (int index = 0; index <= 6; index++) {
			if ((index == PRAYER) || (index == HITPOINTS)) {
				continue;
			}
			Skill skill = player.getSkills()[index];
			int realLevel = skill.getLevelForExperience();

			skill.increaseLevel((int) (realLevel * .33), realLevel);
		}
	}

	private static void antiFire(final Player player) {
		player.incrementDragonFireImmunity(360);

		player.getPacketBuilder().sendMessage("Your immunity against dragon fire has been increased.");

		TaskFactory.getFactory().submit(new Worker(player.getDragonFireImmunity(), false) {

			@Override
			public void fire() {
				player.deincrementDragonFireImmunity();
				if (player.getDragonFireImmunity() == 30) {
					player.getPacketBuilder().sendMessage("Your resistance to dragon fire is about to wear off!");
				} else if (player.getDragonFireImmunity() <= 0) {
					cancel();
				}
			}

			@Override
			public void fireOnCancel() {
				player.getPacketBuilder().sendMessage("Your resistance to dragon fire has worn off!");
				player.setDragonFireImmunity(0);
			}

		}.attach(player));
	}

	private static void doBasicEffect(Player player, int skillId, BoostType type) {
		Skill skill = player.getSkills()[skillId];
		int realLevel = skill.getLevelForExperience();
		int boostLevel = Math.round(realLevel * type.getBoostAmount());
		int cap = realLevel + boostLevel;
		if (type == BoostType.NORMAL) {
			boostLevel += 1;
		}

		if ((skill.getLevel() + boostLevel) > (realLevel + boostLevel + 1)) {
			boostLevel = (realLevel + boostLevel) - skill.getLevel();
		}

		skill.increaseLevel(boostLevel, cap);
	}

	public static boolean consume(Player player, Item item, int slot) {
		PotionConsumable potion = forId(item.getId());
		if (potion == null) {
			return false;
		}
		if (player.isHasDied()) {
			return false;
		}
		if (player.getPotionTimer().elapsed() < 1500) { // TODO: Specialized
			// delays?
			return false;
		}
		// TODO: Check duel rule for no potions

		player.animation(new Animation(829));
		player.getPotionTimer().reset();
		player.getEatingTimer().reset();
		player.getCombatBuilder().reset();
		player.getCombatBuilder().resetAttackTimer();
		player.getInventory().deleteItemSlot(item, slot);
		player.getInventory().addItem(getReplacementItem(item));
		potion.fireAction(player);

		SkillManager.refreshAll(player);
		return true;
	}

	private enum BoostType {
		NORMAL(.13F), SUPER(.20F);

		private final float boostAmount;

		private BoostType(float boostAmount) {
			this.boostAmount = boostAmount;
		}

		protected final float getBoostAmount() {
			return boostAmount;
		}
	}
}
