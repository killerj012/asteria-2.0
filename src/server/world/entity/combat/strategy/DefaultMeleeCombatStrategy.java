package server.world.entity.combat.strategy;

import server.world.entity.Animation;
import server.world.entity.Entity;
import server.world.entity.Hit;
import server.world.entity.combat.CombatHit;
import server.world.entity.combat.CombatStrategy;
import server.world.entity.combat.CombatType;
import server.world.entity.npc.Npc;
import server.world.entity.player.Player;
import server.world.entity.player.content.AssignWeaponInterface.WeaponInterface;

public class DefaultMeleeCombatStrategy implements CombatStrategy {

    @Override
    public boolean prepareAttack(Entity entity) {
        return true;

    }

    @Override
    public CombatHit attack(Entity entity, Entity victim) {
        if (entity instanceof Npc) {
            Npc npc = (Npc) entity;
            npc.facePosition(victim.getPosition().clone());
            npc.animation(new Animation(npc.getDefinition().getAttackAnimation()));
        } else if (entity instanceof Player) {
            Player player = (Player) entity;
            player.facePosition(victim.getPosition().clone());
            player.animation(new Animation(player.getFightType().getAnimation()));
        }
        return new CombatHit(new Hit[] { new Hit(1) }, CombatType.MELEE);
    }

    @Override
    public int attackTimer(Entity entity) {
        return entity.getAttackSpeed();
    }

    @Override
    public int getDistance(Entity entity) {
        if (entity instanceof Npc) {
            return ((Npc) entity).getDefinition().getNpcSize();
        }

        int distance = 1;
        Player player = (Player) entity;

        if (player.getWeapon() == WeaponInterface.HALBERD) {
            distance++;
        }
        return distance;
    }
}
