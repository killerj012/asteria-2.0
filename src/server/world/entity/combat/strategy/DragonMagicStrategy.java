package server.world.entity.combat.strategy;

import server.core.worker.TaskFactory;
import server.core.worker.Worker;
import server.util.Misc;
import server.world.entity.Animation;
import server.world.entity.Entity;
import server.world.entity.Hit;
import server.world.entity.combat.CombatHitContainer;
import server.world.entity.combat.CombatStrategy;
import server.world.entity.combat.CombatType;
import server.world.entity.combat.magic.CombatMagicSpells;
import server.world.entity.player.Player;

public final class DragonMagicStrategy implements CombatStrategy {

    private static final int[] PROTECTIVE_SHIELDS = { 11283, 11285, 1540 };

    @Override
    public boolean prepareAttack(Entity entity) {
	return entity.isNpc();
    }

    @Override
    public CombatHitContainer attack(Entity entity, Entity victim) {
	entity.animation(new Animation(81));

	TaskFactory.getFactory().submit(new Worker(1, false) {
	    @Override
	    public void fire() {
		if (entity.isUnregistered() || victim.isUnregistered()) {
		    cancel();
		    return;
		}

		CombatMagicSpells.FIRE_BLAST.getSpell().castProjectile(entity, victim).sendProjectile();
		cancel();
	    }
	});

	/* Let's start with a maximum of 65 possible damage. */
	int damage = Misc.random(65);

	boolean flag = false;
	if (entity.isPlayer()) {
	    Player player = (Player) entity;

	    /*
	     * Since the only protective armor is a shield, we can check if the
	     * slot is used, otherwise assume it does not exist.
	     */
	    if (player.getEquipment().getContainer().isSlotUsed(Misc.EQUIPMENT_SLOT_SHIELD)) {
		for (int id : PROTECTIVE_SHIELDS) {
		    /* If the equip container contains one of the shields... */
		    if (player.getEquipment().getContainer().contains(id)) {
			/* Flag it. */
			flag = true;
			break;
		    }
		}
	    }

	    /* If the player contains a shield and */
	    
	}

	entity.setCurrentlyCasting(CombatMagicSpells.FIRE_BLAST.getSpell());
	return new CombatHitContainer(new Hit[] { new Hit(damage) }, CombatType.MAGIC, false);
    }

    @Override
    public int attackTimer(Entity entity) {
	return 6;
    }

    @Override
    public int getDistance(Entity entity) {
	return 8;
    }

}