package server.world.entity.combat.task;

import java.util.HashMap;
import java.util.Map;

import server.core.worker.WorkRate;
import server.core.worker.Worker;
import server.util.Misc;
import server.world.entity.Entity;
import server.world.entity.Hit;
import server.world.entity.Hit.HitType;
import server.world.item.Item;

/**
 * A {@link Worker} implementation that handles the poisoning process.
 * 
 * @author lare96
 */
public class CombatPoisonTask extends Worker {

	/** The entity being inflicted with poison. */
	private Entity entity;

	/**
	 * Create a new {@link CombatPoisonTask}.
	 * 
	 * @param entity
	 *            the entity being inflicted with poison.
	 */
	public CombatPoisonTask(Entity entity) {
		super(15, false, WorkRate.APPROXIMATE_SECOND);
		this.entity = entity;
	}

	/**
	 * Holds all of the different strengths of poisons.
	 * 
	 * @author lare96
	 */
	public enum PoisonType {
		MILD, EXTRA, SUPER;

		/**
		 * Gets the damage that will be inflicted by this level of poison.
		 * Please note that this is not a static implementation, meaning a
		 * different number will be returned each time.
		 * 
		 * @return the damage inflicted by this level of poison.
		 */
		public int getDamage() {
			if (Misc.RANDOM.nextBoolean()) {
				return Misc.randomNoZero((ordinal() + 5));
			}
			return Misc.randomNoZero((ordinal() + 6));
		}
	}

	@Override
	public void fire() {

		/** Stop the task if needed. */
		if (entity.isUnregistered()) {
			this.cancel();
			return;
		}

		if (entity.getPoisonHits() == 0) {
			entity.setPoisonHits(0);
			entity.setPoisonStrength(PoisonType.MILD);
			this.cancel();
			return;
		}

		/** Calculate the poison hit for this turn. */
		int calculateHit = entity.getPoisonStrength().getDamage();

		/**
		 * If the damage is above your current health then don't deal any
		 * damage.
		 */
		if (calculateHit >= entity.getCurrentHealth()) {
			return;
		}

		/** Otherwise deal damage as normal. */
		entity.dealDamage(new Hit(calculateHit, HitType.POISON));
		entity.decrementPoisonHits();
	}

	/**
	 * The class that manages all of the combat poison data.
	 * 
	 * @author lare96
	 * @author Advocatus
	 */
	public static class CombatPoisonData {

		/** The map of all of the different weapons that poison. */
		// XXX: Increase the capacity of the hashmap as more elements are added
		// please!
		private static Map<Integer, PoisonType> types = new HashMap<Integer, PoisonType>(
				150);

		/** Load all of the poison data. */
		static {
			types.put(817, PoisonType.MILD);
			types.put(816, PoisonType.MILD);
			types.put(818, PoisonType.MILD);
			types.put(831, PoisonType.MILD);
			types.put(812, PoisonType.MILD);
			types.put(813, PoisonType.MILD);
			types.put(814, PoisonType.MILD);
			types.put(815, PoisonType.MILD);
			types.put(883, PoisonType.MILD);
			types.put(885, PoisonType.MILD);
			types.put(887, PoisonType.MILD);
			types.put(889, PoisonType.MILD);
			types.put(891, PoisonType.MILD);
			types.put(893, PoisonType.MILD);
			types.put(870, PoisonType.MILD);
			types.put(871, PoisonType.MILD);
			types.put(872, PoisonType.MILD);
			types.put(873, PoisonType.MILD);
			types.put(874, PoisonType.MILD);
			types.put(875, PoisonType.MILD);
			types.put(876, PoisonType.MILD);
			types.put(834, PoisonType.MILD);
			types.put(835, PoisonType.MILD);
			types.put(832, PoisonType.MILD);
			types.put(833, PoisonType.MILD);
			types.put(836, PoisonType.MILD);
			types.put(1221, PoisonType.MILD);
			types.put(1223, PoisonType.MILD);
			types.put(1219, PoisonType.MILD);
			types.put(1229, PoisonType.MILD);
			types.put(1231, PoisonType.MILD);
			types.put(1225, PoisonType.MILD);
			types.put(1227, PoisonType.MILD);
			types.put(1233, PoisonType.MILD);
			types.put(1253, PoisonType.MILD);
			types.put(1251, PoisonType.MILD);
			types.put(1263, PoisonType.MILD);
			types.put(1261, PoisonType.MILD);
			types.put(1259, PoisonType.MILD);
			types.put(1257, PoisonType.MILD);
			types.put(3094, PoisonType.MILD);

			types.put(5621, PoisonType.EXTRA);
			types.put(5620, PoisonType.EXTRA);
			types.put(5617, PoisonType.EXTRA);
			types.put(5616, PoisonType.EXTRA);
			types.put(5619, PoisonType.EXTRA);
			types.put(5618, PoisonType.EXTRA);
			types.put(5629, PoisonType.EXTRA);
			types.put(5628, PoisonType.EXTRA);
			types.put(5631, PoisonType.EXTRA);
			types.put(5630, PoisonType.EXTRA);
			types.put(5645, PoisonType.EXTRA);
			types.put(5644, PoisonType.EXTRA);
			types.put(5647, PoisonType.EXTRA);
			types.put(5646, PoisonType.EXTRA);
			types.put(5643, PoisonType.EXTRA);
			types.put(5642, PoisonType.EXTRA);
			types.put(5633, PoisonType.EXTRA);
			types.put(5632, PoisonType.EXTRA);
			types.put(5634, PoisonType.EXTRA);
			types.put(5660, PoisonType.EXTRA);
			types.put(5656, PoisonType.EXTRA);
			types.put(5657, PoisonType.EXTRA);
			types.put(5658, PoisonType.EXTRA);
			types.put(5659, PoisonType.EXTRA);
			types.put(5654, PoisonType.EXTRA);
			types.put(5655, PoisonType.EXTRA);
			types.put(5680, PoisonType.EXTRA);

			types.put(5623, PoisonType.SUPER);
			types.put(5622, PoisonType.SUPER);
			types.put(5625, PoisonType.SUPER);
			types.put(5624, PoisonType.SUPER);
			types.put(5627, PoisonType.SUPER);
			types.put(5626, PoisonType.SUPER);
			types.put(5698, PoisonType.SUPER);
			types.put(5730, PoisonType.SUPER);
			types.put(5641, PoisonType.SUPER);
			types.put(5640, PoisonType.SUPER);
			types.put(5637, PoisonType.SUPER);
			types.put(5636, PoisonType.SUPER);
			types.put(5639, PoisonType.SUPER);
			types.put(5638, PoisonType.SUPER);
			types.put(5635, PoisonType.SUPER);
			types.put(5661, PoisonType.SUPER);
			types.put(5662, PoisonType.SUPER);
			types.put(5663, PoisonType.SUPER);
			types.put(5652, PoisonType.SUPER);
			types.put(5653, PoisonType.SUPER);
			types.put(5648, PoisonType.SUPER);
			types.put(5649, PoisonType.SUPER);
			types.put(5650, PoisonType.SUPER);
			types.put(5651, PoisonType.SUPER);
			types.put(5667, PoisonType.SUPER);
			types.put(5666, PoisonType.SUPER);
			types.put(5665, PoisonType.SUPER);
			types.put(5664, PoisonType.SUPER);
		}

		/**
		 * Gets the poison type of the specified item. Returns <code>null</code>
		 * if the item is not able to poison the victim.
		 * 
		 * @param item
		 *            the item to get the poison type of.
		 * 
		 * @return the poison type of the specified item.
		 */
		public static PoisonType getPoisonType(Item item) {
			return types.get(item.getId());
		}
	}
}
