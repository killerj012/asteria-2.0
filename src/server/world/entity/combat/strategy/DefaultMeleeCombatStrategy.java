package server.world.entity.combat.strategy;

import server.util.Misc;
import server.world.entity.Animation;
import server.world.entity.Entity;
import server.world.entity.Hit;
import server.world.entity.combat.CombatFactory;
import server.world.entity.combat.CombatHit;
import server.world.entity.combat.CombatStrategy;
import server.world.entity.combat.CombatType;
import server.world.entity.npc.Npc;
import server.world.entity.player.Player;
import server.world.entity.player.content.AssignWeaponInterface.FightType;
import server.world.entity.player.content.AssignWeaponInterface.WeaponInterface;
import server.world.item.Item;

/**
 * The default combat strategy assigned to an entity during a melee based combat
 * session.
 * 
 * @author lare96
 */
public class DefaultMeleeCombatStrategy implements CombatStrategy {

    @Override
    public boolean prepareAttack(Entity entity) {

        /** We don't need to check anything before attacking with melee. */
        return true;
    }

    @Override
    public CombatHit attack(Entity entity, Entity victim) {

        /** Determine the animation that will be used. */
        if (entity.isNpc()) {
            Npc npc = (Npc) entity;
            npc.animation(new Animation(npc.getDefinition().getAttackAnimation()));
        } else if (entity.isPlayer()) {
            Player player = (Player) entity;

            if (!player.isSpecialActivated()) {
                Item item = player.getEquipment().getContainer().getItem(Misc.EQUIPMENT_SLOT_WEAPON);

                if (item != null) {
                    if (item.getDefinition().getItemName().startsWith("Dharoks")) {
                        if (player.getFightType() == FightType.BATTLEAXE_SMASH) {
                            player.animation(new Animation(2067));
                        } else {
                            player.animation(new Animation(2066));
                        }
                    } else if (item.getDefinition().getItemName().equals("Granite maul")) {
                        player.animation(new Animation(1665));
                    } else if (item.getDefinition().getItemName().equals("Tzhaar-ket-om")) {
                        player.animation(new Animation(2661));
                    } else if (item.getDefinition().getItemName().endsWith("wand")) {
                        player.animation(new Animation(FightType.UNARMED_KICK.getAnimation()));
                    } else if (item.getDefinition().getItemName().startsWith("Torags")) {
                        player.animation(new Animation(2068));
                    } else if (item.getDefinition().getItemName().startsWith("Veracs")) {
                        player.animation(new Animation(2062));
                    } else {
                        player.animation(new Animation(player.getFightType().getAnimation()));
                    }
                } else {
                    player.animation(new Animation(player.getFightType().getAnimation()));
                }
            } else {
                return player.getCombatSpecial().getSpecialStrategy().calculateHit(player, victim);
            }
        }

        /** Determine the damage that will be dealt this turn (if any). */
        if (CombatFactory.hitAccuracy(entity, victim, CombatType.MELEE, 1)) {
            return new CombatHit(new Hit[] { CombatFactory.getMeleeHit(entity) }, CombatType.MELEE);
        }

        return null;
    }

    @Override
    public int attackTimer(Entity entity) {

        /** The attack speed implementation is used here. */
        return entity.getAttackSpeed();
    }

    @Override
    public int getDistance(Entity entity) {

        /** The default distance for all npcs is 1. */
        if (entity.isNpc()) {
            return 1;
        }

        /**
         * The default distance for all players is 1, or 2 if they are using a
         * halberd.
         */
        int distance = 1;
        Player player = (Player) entity;

        if (player.getWeapon() == WeaponInterface.HALBERD) {
            distance++;
        }
        return distance;
    }
}
