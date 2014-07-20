package com.asteria.world.entity.combat.strategy;

import com.asteria.util.Utility;
import com.asteria.world.entity.Entity;
import com.asteria.world.entity.Entity.EntityType;
import com.asteria.world.entity.combat.CombatContainer;
import com.asteria.world.entity.combat.CombatFactory.CombatType;
import com.asteria.world.entity.combat.CombatStrategy;
import com.asteria.world.entity.combat.magic.CombatSpells;
import com.asteria.world.entity.npc.Npc;
import com.asteria.world.entity.player.Player;

/**
 * The default combat strategy assigned to an {@link Entity} during a magic
 * based combat session.
 * 
 * @author lare96
 */
public class DefaultMagicCombatStrategy implements CombatStrategy {

    @Override
    public boolean canAttack(Entity entity, Entity victim) {

        // Npcs don't need to be checked.
        if (entity.type() == EntityType.NPC) {
            return true;
        }

        // Create the player instance.
        Player player = (Player) entity;

        // We can't attack without a spell.
        if (player.getCastSpell() == null) {
            throw new IllegalStateException(entity + " no spell to cast!");
        }

        // Check the cast using the spell implementation.
        return player.getCastSpell().canCast(player);
    }

    @Override
    public CombatContainer attack(Entity entity, Entity victim) {

        // Prepare the spell to be cast.
        if (entity.type() == EntityType.PLAYER) {
            Player player = (Player) entity;
            player.prepareSpell(player.getCastSpell(), victim);
        } else if (entity.type() == EntityType.NPC) {
            Npc npc = (Npc) entity;

            switch (npc.getNpcId()) {
            case 13:
            case 172:
            case 174:
                npc.prepareSpell(Utility.randomElement(new CombatSpells[] { CombatSpells.WEAKEN,
                CombatSpells.FIRE_STRIKE,CombatSpells.EARTH_STRIKE,
                CombatSpells.WATER_STRIKE }).getSpell(),
                        victim);
                break;
            }

            if (npc.getCurrentlyCasting() == null)
                npc.prepareSpell(CombatSpells.WIND_STRIKE.getSpell(), victim);
        }

        // Disabling spells are here because there is no hit.
        if (entity.getCurrentlyCasting().maximumHit() == -1) {
            return new CombatContainer(entity, victim, CombatType.MAGIC, true);
        }

        // Otherwise we create the container as normal.
        return new CombatContainer(entity, victim, 1, CombatType.MAGIC, true);
    }

    @Override
    public int attackDelay(Entity entity) {

        // The magic attack delay for all spells.
        return 10;
    }

    @Override
    public int attackDistance(Entity entity) {

        // The magic attack distance for all spells.
        return 8;
    }
}
