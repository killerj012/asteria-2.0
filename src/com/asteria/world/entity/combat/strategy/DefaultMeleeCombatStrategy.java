package com.asteria.world.entity.combat.strategy;

import com.asteria.util.Utility;
import com.asteria.world.entity.Animation;
import com.asteria.world.entity.Entity;
import com.asteria.world.entity.Entity.EntityType;
import com.asteria.world.entity.combat.CombatContainer;
import com.asteria.world.entity.combat.CombatFactory.CombatType;
import com.asteria.world.entity.combat.CombatStrategy;
import com.asteria.world.entity.npc.Npc;
import com.asteria.world.entity.player.Player;
import com.asteria.world.entity.player.content.AssignWeaponInterface.FightType;
import com.asteria.world.entity.player.content.AssignWeaponInterface.WeaponInterface;
import com.asteria.world.item.Item;

/**
 * The default combat strategy assigned to an {@link Entity} during a melee
 * based combat session. This is the combat strategy used by all {@link Npc}s by
 * default.
 * 
 * @author lare96
 */
public class DefaultMeleeCombatStrategy implements CombatStrategy {

    @Override
    public boolean canAttack(Entity entity, Entity victim) {

        // We don't need to check anything before attacking with melee.
        return true;
    }

    @Override
    public CombatContainer attack(Entity entity, Entity victim) {

        // Start the animation for this attack.
        startAnimation(entity);

        // Create the combat container for this hook.
        return new CombatContainer(entity, victim, 1, CombatType.MELEE, true);
    }

    @Override
    public int attackDelay(Entity entity) {

        // The attack speed for the weapon being used.
        return entity.getAttackSpeed();
    }

    @Override
    public int attackDistance(Entity entity) {

        // The default distance for all npcs using melee is 1.
        if (entity.type() == EntityType.NPC) {
            return 1;
        }

        // The default distance for all players is 1, or 2 if they are using a
        // halberd.
        Player player = (Player) entity;
        if (player.getWeapon() == WeaponInterface.HALBERD) {
            return 2;
        }
        return 1;
    }

    /**
     * Starts the animation for the argued entity in the current combat hook.
     * 
     * @param entity
     *            the entity to start the animation for.
     */
    private void startAnimation(Entity entity) {
        if (entity.type() == EntityType.NPC) {
            Npc npc = (Npc) entity;
            npc.animation(new Animation(npc.getDefinition()
                    .getAttackAnimation()));
        } else if (entity.type() == EntityType.PLAYER) {
            Player player = (Player) entity;
            Item item = player.getEquipment()
                    .get(Utility.EQUIPMENT_SLOT_WEAPON);

            if (!player.isSpecialActivated() && item != null) {
                if (item.getDefinition().getItemName()
                        .startsWith("Dragon dagger")) {
                    player.animation(new Animation(402));
                } else if (item.getDefinition().getItemName()
                        .startsWith("Dharoks")) {
                    if (player.getFightType() == FightType.BATTLEAXE_SMASH) {
                        player.animation(new Animation(2067));
                    } else {
                        player.animation(new Animation(2066));
                    }
                } else if (item.getDefinition().getItemName()
                        .equals("Granite maul")) {
                    player.animation(new Animation(1665));
                } else if (item.getDefinition().getItemName()
                        .equals("Tzhaar-ket-om")) {
                    player.animation(new Animation(2661));
                } else if (item.getDefinition().getItemName().endsWith("wand")) {
                    player.animation(new Animation(FightType.UNARMED_KICK
                            .getAnimation()));
                } else if (item.getDefinition().getItemName()
                        .startsWith("Torags")) {
                    player.animation(new Animation(2068));
                } else if (item.getDefinition().getItemName()
                        .startsWith("Veracs")) {
                    player.animation(new Animation(2062));
                } else {
                    player.animation(new Animation(player.getFightType()
                            .getAnimation()));
                }
            } else {
                player.animation(new Animation(player.getFightType()
                        .getAnimation()));
            }
        }
    }
}
